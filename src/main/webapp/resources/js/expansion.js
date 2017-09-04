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
function runProgram(program, robot) {
    program(robot);
}

// ========================== game setup ==========================

if (typeof game == 'undefined') {
    game = {};
    game.demo = true;
    game.code = 123;
    game.playerName = 'user@gmail.com';
    initLayout = function(game, html, context, transformations, scripts, onLoad) {
        onLoad();
    }
}

var gameName = localStorage.getItem('gameName');
if (gameName == 'Expansion Training') {
    game.sprites = 'robot';
} else if (gameName == 'Expansion Contest') {
    game.sprites = 'robot';
    game.onlyLeaderBoard = true;
}
game.enableDonate = false;
game.enableJoystick = false;
game.enableAlways = true;
game.enablePlayerInfo = false;
game.enableLeadersTable = false;
game.enableChat = false;
game.enableInfo = false;
game.enableHotkeys = true;
game.enableAdvertisement = false;
game.showBody = false;
game.debug = false;

// ========================== leaderboard page ==========================

var initHelpLink = function() {
    var pageName = gameName.split(' ').join('-').toLowerCase();
    $('#help-link').attr('href', '/codenjoy-contest/resources/expansion/landing-' + pageName + '.html')
}
var initAdditionalLink = function() {
    if (game.onlyLeaderBoard) {
        $('#additional-link').attr('href', '/codenjoy-contest/resources/user/expansion-servers.zip')
        $('#additional-link').text('Get client')
    }
}

game.onBoardAllPageLoad = function() {
    loadArrowImages();
    initLayout(game.gameName, 'leaderboard.html', game.contextPath,
        null,
        ['js/game/loader/boardAllPageLoad.js'],
        function() {
            boardAllPageLoad();
            loadStuff();
        });
}

// ========================== user page ==========================

var controller;

if (game.onlyLeaderBoard) {
    game.onBoardPageLoad = game.onBoardAllPageLoad;
} else {
    game.onBoardPageLoad = function() {
        loadArrowImages();
        initLayout(game.gameName, 'board.html', game.contextPath,
            null,
            [],
            function() {
                if (this.hasOwnProperty('boardPageLoad')) {
                    boardPageLoad();
                    loadStuff();
                }
            });
    }
}

// ========================== board draw logic ==========================

var loadStuff = function() {
    initHelpLink();
    initAdditionalLink();
}

var arrows = {};
var directions = ['up', 'right_up', 'right', 'right_down', 'down', 'left_down', 'left', 'left_up'];
var loadArrowImages = function() {
    for (var force = 0; force < 4; force++) {
    	arrows[force] = {};
    	for (var i in directions) {
    		var direction = directions[i];
    		var url = game.contextPath + 'resources/sprite/' + game.gameName + '/' + game.sprites + '/force' + (force + 1) + '_' + direction + '.png';
    		var image = new Image();
    		image.onload = function() {
    			// do nothing
    		}
    		image.src = url;
    		arrows[force][direction] = image;
    	}
    }
}

game.drawBoard = function(drawer) {
    var canvas = drawer.canvas;
    var board = drawer.playerData.board;
    var heroesData = drawer.playerData.heroesData;
    var forces = board.forces;
    var size = canvas.boardSize;

    drawer.clear();
    drawer.drawBack();

    var BLUE = 0;
    var RED = 1;
    var GREEN = 2;
    var YELLOW = 3;

    var fonts = {};
    fonts.forces = {};
    fonts.forces.dx = 24;
    fonts.forces.dyForce = 30;
    fonts.forces.dyBase = 35;
    fonts.forces.font = "23px 'verdana'";
    fonts.forces.fillStyles = {};
    fonts.forces.fillStyles[GREEN] = "#115e34";
    fonts.forces.fillStyles[RED] = "#681111";
    fonts.forces.fillStyles[BLUE] = "#306177";
    fonts.forces.fillStyles[YELLOW] = "#7f6c1b";
    fonts.forces.shadowStyles = {};
    fonts.forces.shadowStyles[GREEN] = "#64d89b";
    fonts.forces.shadowStyles[RED] = "#d85e5b";
    fonts.forces.shadowStyles[BLUE] = "#6edff9";
    fonts.forces.shadowStyles[YELLOW] = "#f9ec91";
    fonts.forces.textAlign = "center";
    fonts.forces.shadowOffsetX = 0;
    fonts.forces.shadowOffsetY = 0;
    fonts.forces.shadowBlur = 0;

    var changeColor = function(color) {
        if (color == GREEN) return YELLOW;
        if (color == YELLOW) return GREEN;
        if (color == RED) return BLUE;
        return RED;
    }

    var parseColor = function(char) {
        if (char == 'P') return BLUE;
        if (char == 'Q') return RED;
        if (char == 'R') return GREEN;
        if (char == 'S') return YELLOW;
    }

    var isBase = function(char) {
        return (char == 'X') || (char == 'Y') || (char == 'Z') || (char == 'a');
    }

    var isForce = function(char) {
        return (char == 'P') || (char == 'Q') || (char == 'R') || (char == 'S');
    }

    var parseCount = function(code) {
        if (code == '-=#') return 0;
        return parseInt(code, 36);
    }

    var length = function(x, y) {
        return (size - 1 - y)*size + x;
    }

    var getColor = function(x, y) {
        var layer2 = board.layers[1];
        var l = length(x ,y);
        var color = parseColor(layer2.substring(l, l + 1));
        return color;
    }

    var COUNT_NUMBERS = 3;
    var getCount = function(x, y) {
        var l = length(x, y);
        var sub = forces.substring(l*COUNT_NUMBERS, (l + 1)*COUNT_NUMBERS);
        var count = parseCount(sub);
        return count;
    }

    var drawForces = function(x, y, afterBase){
        var count = getCount(x, y);
        if (count == 0) return;

        if (afterBase) {
            fonts.forces.dy = fonts.forces.dyBase;
        } else {
            fonts.forces.dy = fonts.forces.dyForce;
        }
        var color = getColor(x, y);
        if (!color) return;
        fonts.forces.fillStyle = fonts.forces.fillStyles[color];
        fonts.forces.shadowColor = fonts.forces.shadowStyles[color];
        canvas.drawText(count, {'x':x - 1, 'y':y}, fonts.forces);
    }

    try {
        drawer.drawLayers(function(layers, layerIndex, charIndex, x, y) {
            try {
                var afterForce = (layerIndex == 1 && isForce(layers[layerIndex][charIndex]));
                var afterBase = (layerIndex == 0 && isBase(layers[layerIndex][charIndex]));
                if (afterForce || afterBase) {
                    drawForces(x, y, afterForce);
                }
            } catch (err) {
                console.log(err);
            }
        });
    } catch (err) {
        console.log(err);
    }

    try {
        var h = canvas.getPlotSize()/2;
        var drawArrow = function(color, direction, x, y) {
            direction = direction.toLowerCase();
            switch (direction) {
                case 'right':      canvas.drawImage(arrows[color]['right'], x, y, h, 0); break;
                case 'up':         canvas.drawImage(arrows[color]['up'], x, y, 0, -h); break;
                case 'down':       canvas.drawImage(arrows[color]['down'], x, y, 0, h); break;
                case 'left':       canvas.drawImage(arrows[color]['left'], x, y, -h, 0); break;
                case 'right_up':   canvas.drawImage(arrows[color]['right_up'], x, y, h, -h); break;
                case 'right_down': canvas.drawImage(arrows[color]['right_down'], x, y, h, h); break;
                case 'left_up':    canvas.drawImage(arrows[color]['left_up'], x, y, -h, -h); break;
                case 'left_down':  canvas.drawImage(arrows[color]['left_down'], x, y, -h, h); break;
                default: break;
            }
        }
        for (var name in heroesData) {
            var additionalData = heroesData[name][name].additionalData;
            var lastAction = additionalData.lastAction;
            if (!lastAction) continue;
            var movements = lastAction.movements;
            var increase = lastAction.increase;

            for (var i in movements) {
                var movement = movements[i];
                var pt = movement.region;
                var direction = movement.direction;

                drawArrow(getColor(pt.x, pt.y), direction, pt.x, pt.y);
            }
        }
    } catch (err) {
        console.log(err);
    }

    fonts.userName = {};
    fonts.userName.dx = -15;
    fonts.userName.dy = -45;
    fonts.userName.font = "22px 'verdana'";
    fonts.userName.fillStyle = "#0FF";
    fonts.userName.textAlign = "left";
    fonts.userName.shadowColor = "#000";
    fonts.userName.shadowOffsetX = 0;
    fonts.userName.shadowOffsetY = 0;
    fonts.userName.shadowBlur = 5;
    drawer.drawPlayerNames(fonts.userName);

    drawer.drawFog();
}

// ========================== demo stuff ==========================

if (game.demo) {
    game.onBoardPageLoad();
}