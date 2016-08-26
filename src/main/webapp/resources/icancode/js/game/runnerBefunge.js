/*-
 * #%L
 * iCanCode - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 EPAM
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

/*
 * Based on work by http://www.elated.com is licensed under a Creative Commons Attribution 3.0 Unported License (http://creativecommons.org/licenses/by/3.0/)
 * From http://www.elated.com/res/File/articles/development/javascript/jquery/drag-and-drop-with-jquery-your-essential-guide/card-game.html
 **/
function initRunnerBefunge(console) {

    var container = $('#ide-content');
    container.empty();
    container.append('<div id="cardPile"></div>' +
                     '<div id="cardSlots"></div>');

    $('.autocomplete').hide();
    $('.bottom-panel').append('<button class="button reset" id="ide-clean">Clean</button>')
    $('#ide-clean').click(function() {
        moveAllCardsToCardPile();
    });

	var cursor = null;
	var value = null;
	var running = false;
	var robot = null;
	
	var finishCommand = function() {
		value = null;
		running = false;
		// console.print('// FINISH');
	}
	
	var commands = [
			{id:'start', type:9, title:'»', process: function(x, y) {
				cursor = pt(x, y);
				direction = Direction.RIGHT;
				value = null;
				running = true;
				// console.print('// BEGIN');
			}},
			{id:'finish', type:9, title:'•', process: finishCommand},

			{id:'cursor-right', type:1, title:'˃', process: function(x, y) {
				direction = Direction.RIGHT;
				// console.print('// change processor direction to RIGHT');
			}},
			{id:'cursor-left', type:1, title:'˂', process: function(x, y) {
				direction = Direction.LEFT;
				// console.print('// change processor direction to LEFT');
			}},
			{id:'cursor-up', type:1, title:'˄', process: function(x, y) {
				direction = Direction.UP;
				// console.print('// change processor direction to UP');
			}},
			{id:'cursor-down', type:1, title:'˅', process: function(x, y) {
				direction = Direction.DOWN;
				// console.print('// change processor direction to DOWN');
			}},
			
			{id:'robot-left', type:5, title:'←', process: function(x, y) {
				robot.go('LEFT');
				// console.print('robot.goLeft();');
			}},			
			{id:'robot-right', type:5, title:'→', process: function(x, y) {
				robot.go('RIGHT');
				// console.print('robot.goRight();');
			}},			
			{id:'robot-up', type:5, title:'↑', process: function(x, y) {
				robot.go('UP');
				// console.print('robot.goUp();');
			}},			
			{id:'robot-down', type:5, title:'↓', process: function(x, y) {
				robot.go('DOWN');
				// console.print('robot.goDown();');
			}},			
			{id:'robot-go', type:5, title:'Go', process: function(x, y) {
				robot.go(value);
				// console.print('robot.go("' + value + '");');
			}},

			{id:'robot-jump-left', type:8, title:'↤', process: function(x, y) {
                robot.jump('LEFT');
                // console.print('robot.jumpLeft();');
            }},
            {id:'robot-jump-right', type:8, title:'↦', process: function(x, y) {
                robot.jump('RIGHT');
                // console.print('robot.jumpRight();');
            }},
            {id:'robot-jump-up', type:8, title:'↥', process: function(x, y) {
                robot.jump('UP');
                // console.print('robot.jumpUp();');
            }},
            {id:'robot-jump-down', type:8, title:'↧', process: function(x, y) {
                robot.jump('DOWN');
                // console.print('robot.jumpDown();');
            }},
            {id:'robot-jump', type:8, title:'Jm', process: function(x, y) {
                robot.jump(value);
                // console.print('robot.jump("' + value + '");');
            }},
			
			{id:'if', type:7, title:'If', process: function(x, y) {
				var leftOperand = value;
				var point = direction.change(cursor);
				var rightValue = board.process(point.getX(), point.getY());
				var expression = (leftOperand == rightValue);
				if (expression) {
					// console.print('if ("' + leftOperand + '" == "' + rightValue + '") { !!! } else { ... }');
					direction = direction.contrClockwise();
				} else {
					// console.print('if ("' + leftOperand + '" == "' + rightValue + '") { ... } else { !!! }');
					direction = direction.clockwise();
				}
			}},			
			{id:'scanner-at', type:7, title:'Sc', process: function(x, y) {
				var oldValue = value;
				value = robot.getScanner().at(oldValue);
				// console.print('value = robot.getScanner().at("' + oldValue + '"); = ' + value)
			}},			
			{id:'robot-came-from', type:10, title:'Cf', process: function(x, y) {
				value = robot.cameFrom();
				// console.print('value = cameFrom() = ' + value);
			}},			
			{id:'robot-previous-direction', type:10, title:'Pd', process: function(x, y) {
				value = robot.previousDirection();
				// console.print('value = previousDirection() = ' + value);
			}},			
			
			{id:'value-left', type:4, title:'L', process: function(x, y) {
				value = 'LEFT';
				// console.print('value = "LEFT"');
			}},			
			{id:'value-right', type:4, title:'R', process: function(x, y) {
				value = 'RIGHT';
				// console.print('value = "RIGHT"');
			}},			
			{id:'value-up', type:4, title:'U', process: function(x, y) {
				value = 'UP';
				// console.print('value = "UP"');
			}},			
			{id:'value-down', type:4, title:'D', process: function(x, y) {
				value = 'DOWN';
				// console.print('value = "DOWN"');
			}},			
			
			{id:'value-null', type:6, title:'Nu', process: function(x, y) {
				value = null;
				// console.print('value = null');
			}},			
			
			{id:'value-wall', type:3, title:'W', process: function(x, y) {
				value = 'WALL';
				// console.print('value = "WALL"');
			}},			
			{id:'value-none', type:3, title:'N', process: function(x, y) {
				value = 'NONE';
				// console.print('value = "NONE"');
			}},			
			{id:'value-start', type:3, title:'S', process: function(x, y) {
				value = 'START';
				// console.print('value = "START"');
			}},			
			{id:'value-end', type:3, title:'E', process: function(x, y) {
				value = 'END';
				// console.print('value = "END"');
			}},			
			{id:'value-gold', type:3, title:'G', process: function(x, y) {
				value = 'GOLD';
				// console.print('value = "GOLD"');
			}},			
			{id:'value-box', type:3, title:'B', process: function(x, y) {
				value = 'BOX';
				// console.print('value = "BOX"');
			}},			
			{id:'value-hole', type:3, title:'H', process: function(x, y) {
				value = 'HOLE';
				// console.print('value = "HOLE"');
			}}			
		];

	var mapSlots = [];
	var size = 10;
	for (var y = 0; y < size; y++) {
		mapSlots[y] = [];
		for (var x = 0; x < size; x++) {
			var element = $('<div></div>')
				.data('data-point', pt(x, y))
				.appendTo('#cardSlots');
			mapSlots[y][x] = element;
		}
	}
	
	for (var index = 0; index < commands.length; index++) {
		for (var count = 0; count < 1; count++) {
			$('<div></div>')
				.data('data-type', commands[index])
				.appendTo('#cardPile');
			
		}
	}

	var mapCards = [];
	var createNewOnPile = function(element) {
		var type = $(element).data('data-type');
		var appended = $('<div class="type-' + type.type + '">' + type.title  + '</div>')
			.data('data-type', type)
			.appendTo(element);	
		mapCards.push(appended);
	}
	
	$('#cardPile>div').each(function(index, element) {
		createNewOnPile(element);	
	});
	
	var initBoard = function() {
		var processCard = function(card, x, y) {
			if (!!card) {
				card.data('data-type').process(x, y);
			} else if (card == null) {
				// console.print('// skip empty cell');
			} else {
				// do nothing - we are out of the board
			}
		}
	
		var getSlot = function(x, y) {
			return mapSlots[y][x];
		}
	
		var getCard = function(x, y) {
			if (pt(x, y).isBad(size)) {
				// console.print('// out of the board');
				finishCommand();
				return false;
			}
			var card = getSlot(x, y).data('parked');
			if (!card) {
				return null;
			}
			return card;
		}
		
		var find = function(id) {
			for (var index in mapCards) {
				var card = mapCards[index];
				if (card.data('data-type').id != id) {
					continue;
				}
				
				var slot = card.data('parkedTo');
				if (isOnCardPile(slot)) {
					continue;
				}
				
				return card;
			}
			console.print('Card with id "' + id + '" not found!');
		}
		
		var start = function() {
			var startCard = find('start');
			if (!startCard) {
			    console.print("Error: Create start point!");
			    return;
			}
			var point = startCard.data('parkedTo').data('data-point');
			animate(point.getX(), point.getY());
			processCard(startCard, point.getX(), point.getY());
		}
		
		var animateDiv = function(div, style, value) {
			var oldValue = div.css(style);
			var css = {};
			css[style] = value;
			div.animate(css, {duration : "fast", complete: function () {
				css[style] = oldValue;
				div.animate(css, "fast");
			}});
		}
		
		var animate = function(x, y) {
			var div = getCard(x, y);
			if (div == null) {
				div = getSlot(x, y);
            }
			if (div != false) {
			    animateDiv(div, "background-color", "#000");
			}
		}
		
		var goNext = function() {
			cursor = direction.change(cursor);	
			animate(cursor.getX(), cursor.getY());
			// console.print('// processor go ' + direction.name());
			process(cursor.getX(), cursor.getY());
		}
		
		var process = function(x, y) {
			var card = getCard(x, y);
			processCard(card, x, y);
			return value;
		}
		
		return {
			start : start,
			goNext : goNext, 
			process : process
		}
	}
	
	var park = function(card, slot) {
		var fromSlot = card.data('parkedTo');
		if (!!fromSlot) {
			fromSlot.data('parked', null);
		}
		var initialSlot = card.data('initial');
		if (!initialSlot) {
			card.data('initial', slot);
		}
		slot.data('parked', card);
		card.data('parkedTo', slot);
		card.position({of: slot, my: 'left top', at: 'left top'});
	}
		
	var isOnCardPile = function(slot) {
		return slot.parent().attr('id') == 'cardPile';
	}

	var doNotRevert = false;
	$('#cardPile>div>div').draggable({
		cursor: 'move',
		revert: function (event, ui) {
			if (typeof event == 'object') {
				var slot = event;
				var card = $(this);
				var parked = slot.data('parked');
				if (!parked) {
					return false;
				}
				
				if (parked[0] == card[0]) {
					return false;
				}
				
				if (doNotRevert) {
					doNotRevert	= false;
					return false;
				}
		
				return true;
			}
			return !event;
		}
	})
	
	var moveToInitial = function(card) {
		var slot = card.data('initial');
		park(card, slot);
	}

	var moveCartToCardPile = function(card) {
        var slot = card.data('parkedTo');
        if (isOnCardPile(slot)) {
            return;
        }
        moveToInitial(card);
	}

	var moveAllCardsToCardPile = function() {
        for (var index in mapCards) {
            var card = mapCards[index];
            moveCartToCardPile(card);
        }
	}

	$('#cardPile>div>div').each(function(index, element) {
		var card = $(this);
		var slot = card.parent();
		park(card, slot);
	}).dblclick(function(element) {
		var card = $(this);
		moveCartToCardPile(card);
	});

    $('#cardSlots>div, #cardPile>div').droppable({
		hoverClass: 'hovered',
		drop: function (event, ui) {
			var slot = $(this);
			if (slot.hasClass('ui-draggable')) {
				slot = slot.data('parkedTo');
			}
			var card = ui.draggable;

			var busy = !!slot.data('parked')
			if (busy) {
				if (isOnCardPile(slot)) {
					doNotRevert = true;
					moveToInitial(card);
				}
				return;
			}			
			
			if (isOnCardPile(slot)) {
				moveToInitial(card);
			} else {
				park(card, slot);
			}
		}
	});
	
	var board = null;

	return {
	    setStubValue : function() {
            // TODO implement me
	    },
	    loadSettings : function() {
            // TODO implement me
        },
        compileProgram : function(robot) {
            // do nothing
        },
        cleanProgram : function() {
            running = false;
            board = initBoard();
        },
        isProgramCompiled : function() {
            return true;
        },
        runProgram : function(r) {
            robot = r;
            board.start();
            var deadLoopCounter = 0;
            while (++deadLoopCounter < 100 && running) {
                board.goNext();
            }
        }
	}
	
}