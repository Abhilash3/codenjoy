game.enableDonate = false;
game.enableJoystick = true;
game.enableAlways = true;
game.enablePlayerInfo = false;
game.enableLeadersTable = false;
game.enableChat = false;
game.enableHotkeys = true;
game.enableAdvertisement = false;
game.showBody = false;

// -----------------------------------------------------------------------------------

var el = function(char, type) {
    return {
        char : char,
        type : type
    }
}

var Element = {
    EMPTY : el('-', 'NONE'),
    FLOOR : el('.', 'NONE'),

    ANGLE_IN_LEFT : el('╔', 'WALL'),
    WALL_FRONT : el('═', 'WALL'),
    ANGLE_IN_RIGHT : el('┐', 'WALL'),
    WALL_RIGHT : el('│', 'WALL'),
    ANGLE_BACK_RIGHT : el('┘', 'WALL'),
    WALL_BACK : el('─', 'WALL'),
    ANGLE_BACK_LEFT : el('└', 'WALL'),
    WALL_LEFT : el('║', 'WALL'),
    WALL_BACK_ANGLE_LEFT : el('┌', 'WALL'),
    WALL_BACK_ANGLE_RIGHT : el('╗', 'WALL'),
    ANGLE_OUT_RIGHT : el('╝', 'WALL'),
    ANGLE_OUT_LEFT : el('╚', 'WALL'),
    SPACE : el(' ', 'WALL'),

    ROBO : el('☺', 'MY_ROBO'),
    ROBO_FALLING : el('o', 'HOLE'),
    ROBO_FLYING : el('*', 'MY_ROBO'),
    ROBO_LASER : el('☻', 'MY_ROBO'),

    ROBO_OTHER : el('X', 'OTHER_ROBO'),
    ROBO_OTHER_FALLING : ('x', 'HOLE'),
    ROBO_OTHER_FLYING : ('^', 'OTHER_ROBO'),
    ROBO_OTHER_LASER : ('&', 'OTHER_ROBO'),

    LASER_MACHINE_CHARGING_LEFT : el('˂', 'LASER_MACHINE'),
    LASER_MACHINE_CHARGING_RIGHT : el('˃', 'LASER_MACHINE'),
    LASER_MACHINE_CHARGING_UP : el('˄', 'LASER_MACHINE'),
    LASER_MACHINE_CHARGING_DOWN : el('˅', 'LASER_MACHINE'),

    LASER_MACHINE_READY_LEFT : el('◄', 'LASER_MACHINE_READY'),
    LASER_MACHINE_READY_RIGHT : el('►', 'LASER_MACHINE_READY'),
    LASER_MACHINE_READY_UP : el('▲', 'LASER_MACHINE_READY'),
    LASER_MACHINE_READY_DOWN : el('▼', 'LASER_MACHINE_READY'),

    LASER_LEFT : el('←', 'LASER_LEFT'),
    LASER_RIGHT : el('→', 'LASER_RIGHT'),
    LASER_UP : el('↑', 'LASER_UP'),
    LASER_DOWN : el('↓', 'LASER_DOWN'),

    START : el('S', 'START'),
    EXIT : el('E', 'EXIT'),
    GOLD : el('$', 'GOLD'),
    HOLE : el('O', 'HOLE'),
    BOX : el('B', 'BOX'),

    getElement : function(char) {
        for (name in this) {
            if (this[name].char == char) {
                return this[name];
            }
        }
        return null;
    }

};

var D = function(index, dx, dy, name){

    var changeX = function(x) {
        return x + dx;
    };

    var changeY = function(y) {
        return y + dy;
    };

    var inverted = function() {
        switch (this) {
            case Direction.UP : return Direction.DOWN;
            case Direction.DOWN : return Direction.UP;
            case Direction.LEFT : return Direction.RIGHT;
            case Direction.RIGHT : return Direction.LEFT;
            default : return Direction.STOP;
        }
    };

    var toString = function() {
        return name;
    };

    return {
        changeX : changeX,

        changeY : changeY,

        inverted : inverted,

        toString : toString,

        getIndex : function() {
            return index;
        }
    };
};

var Direction = {
    UP : D(2, 0, -1, 'up'),
    DOWN : D(3, 0, 1, 'down'),
    LEFT : D(0, -1, 0, 'left'),
    RIGHT : D(1, 1, 0, 'right'),
    ACT : D(4, 0, 0, 'act'),
    STOP : D(5, 0, 0, '')
};

Direction.values = function() {
   return [Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.ACT, Direction.STOP];
};

Direction.valueOf = function(index) {
    var directions = Direction.values();
    for (var i in directions) {
        var direction = directions[i];
        if (direction.getIndex() == index) {
             return direction;
        }
    }
    return Direction.STOP;
};

var Point = function (x, y) {
    return {
        equals : function (o) {
            return o.getX() == x && o.getY() == y;
        },

        toString : function() {
            return '[' + x + ',' + y + ']';
        },

        isBad : function(boardSize) {
            return x >= boardSize || y >= boardSize || x < 0 || y < 0;
        },

        getX : function() {
            return x;
        },

        getY : function() {
            return y;
        }
    }
};

var pt = function(x, y) {
    return new Point(x, y);
};

var LengthToXY = function(boardSize) {
    return {
        getXY : function(length) {
            if (length == -1) {
                return null;
            }
            return new Point(length % boardSize, Math.ceil(length / boardSize));
        },

        getLength : function(x, y) {
            return y*boardSize + x;
        }
    };
};

var LAYER1 = 0;
var LAYER2 = 1;

var Board = function(boardString){
    var board = eval(boardString);
    var layers = board.layers;

    var contains  = function(a, obj) {
        var i = a.length;
        while (i--) {
           if (a[i].equals(obj)) {
               return true;
           }
        }
        return false;
    };

    var removeDuplicates = function(all) {
        var result = [];
        for (var index in all) {
            var point = all[index];
            if (!contains(result, point)) {
                result.push(point);
            }
        }
        return result;
    };

    var boardSize = function() {
        return Math.sqrt(layers[LAYER1].length);
    };

    var size = boardSize();
    var xyl = new LengthToXY(size);

    var isAt = function(x, y, layer, element) {
       if (pt(x, y).isBad(size)) {
           return false;
       }
       return getAt(x, y, layer).char == element.char;
    };

    var getAt = function(x, y, layer) {
        return Element.getElement(layers[layer].charAt(xyl.getLength(x, y)));
    };

    var findAll = function(element, layer) {
        var result = [];
        for (var i = 0; i < size*size; i++) {
            var point = xyl.getXY(i);
            if (isAt(point.getX(), point.getY(), layer, element)) {
                result.push(point);
            }
        }
        return result;
    };

    var isAnyOfAt = function(x, y, layer, elements) {
        for (var index in elements) {
            var element = elements[index];
            if (isAt(x, y, layer, element)) {
                return true;
            }
        }
        return false;
    };

    var isNear = function(x, y, layer, element) {
        if (pt(x, y).isBad(size)) {
            return false;
        }
        return isAt(x + 1, y, layer, element) || isAt(x - 1, y, layer, element)
             || isAt(x, y + 1, layer, element) || isAt(x, y - 1, layer, element);
    };

    var isBarrierAt = function(x, y) {
        return contains(getBarriers(), pt(x, y));
    };

    var countNear = function(x, y, layer, element) {
        if (pt(x, y).isBad(size)) {
            return 0;
        }
        var count = 0;
        if (isAt(x - 1, y    , layer, element)) count ++;
        if (isAt(x + 1, y    , layer, element)) count ++;
        if (isAt(x    , y - 1, layer, element)) count ++;
        if (isAt(x    , y + 1, layer, element)) count ++;
        return count;
    };

    var getHero = function() {
        var result = [];
        result = result.concat(findAll(Element.ROBO, LAYER2));
        result = result.concat(findAll(Element.ROBO_FALLING, LAYER2));
        result = result.concat(findAll(Element.ROBO_FLYING, LAYER2));
        result = result.concat(findAll(Element.ROBO_LASER, LAYER2));
        return result[0];
    };

    var getOtherHeroes = function() {
        var result = [];
        result = result.concat(findAll(Element.ROBO_OTHER, LAYER2));
        return result;
    };

    var getLaserMachines = function() {
        var result = [];
        result = result.concat(findAll(Element.LASER_MACHINE_CHARGING_LEFT, LAYER1));
        result = result.concat(findAll(Element.LASER_MACHINE_CHARGING_RIGHT, LAYER1));
        result = result.concat(findAll(Element.LASER_MACHINE_CHARGING_UP, LAYER1));
        result = result.concat(findAll(Element.LASER_MACHINE_CHARGING_DOWN, LAYER1));
        result = result.concat(findAll(Element.LASER_MACHINE_READY_LEFT, LAYER1));
        result = result.concat(findAll(Element.LASER_MACHINE_READY_RIGHT, LAYER1));
        result = result.concat(findAll(Element.LASER_MACHINE_READY_UP, LAYER1));
        result = result.concat(findAll(Element.LASER_MACHINE_READY_DOWN, LAYER1));
        return result;
    };

    var getLaser = function() {
        var result = [];
        result = result.concat(findAll(Element.LASER_LEFT, LAYER2));
        result = result.concat(findAll(Element.LASER_RIGHT, LAYER2));
        result = result.concat(findAll(Element.LASER_UP, LAYER2));
        result = result.concat(findAll(Element.LASER_DOWN, LAYER2));
        return result;
    };

    var getWalls = function() {
        var result = [];
        result = result.concat(findAll(Element.ANGLE_IN_LEFT, LAYER1));
        result = result.concat(findAll(Element.WALL_FRONT, LAYER1));
        result = result.concat(findAll(Element.ANGLE_IN_RIGHT, LAYER1));
        result = result.concat(findAll(Element.WALL_RIGHT, LAYER1));
        result = result.concat(findAll(Element.ANGLE_BACK_RIGHT, LAYER1));
        result = result.concat(findAll(Element.WALL_BACK, LAYER1));
        result = result.concat(findAll(Element.ANGLE_BACK_LEFT, LAYER1));
        result = result.concat(findAll(Element.WALL_LEFT, LAYER1));
        result = result.concat(findAll(Element.WALL_BACK_ANGLE_LEFT, LAYER1));
        result = result.concat(findAll(Element.WALL_BACK_ANGLE_RIGHT, LAYER1));
        result = result.concat(findAll(Element.ANGLE_OUT_RIGHT, LAYER1));
        result = result.concat(findAll(Element.ANGLE_OUT_LEFT, LAYER1));
        result = result.concat(findAll(Element.SPACE, LAYER1));
        return result;
    };

    var getBoxes = function() {
        var result = [];
        result = result.concat(findAll(Element.BOX, LAYER1));
        return result;
    };

    var getGold = function() {
        var result = [];
        result = result.concat(findAll(Element.GOLD, LAYER1));
        return result;
    };

    var isMyHeroDead = function() {
        return layers[LAYER2].indexOf(Element.ROBO_LASER) != -1 ||
                layers[LAYER2].indexOf(Element.ROBO_FALLING) != -1;
    };

    var getBarriers = function() {
        var all = getWalls();
        all = all.concat(getLaserMachines());
        all = all.concat(getBoxes());
        return removeDuplicates(all);
    };

    var boardAsString = function(layer) {
        var result = "";
        for (var i = 0; i <= size - 1; i++) {
            result += layers[layer].substring(i * size, (i + 1) * size);
            result += "\n";
        }
        return result;
    };

    // thanks http://jsfiddle.net/queryj/g109jvxd/
    String.format = function() {
        // The string containing the format items (e.g. "{0}")
        // will and always has to be the first argument.
        var theString = arguments[0];

        // start with the second argument (i = 1)
        for (var i = 1; i < arguments.length; i++) {
            // "gm" = RegEx options for Global search (more than one instance)
            // and for Multiline search
            var regEx = new RegExp("\\{" + (i - 1) + "\\}", "gm");
            theString = theString.replace(regEx, arguments[i]);
        }
    }

    var toString = function() {
        return String.format(
            "Board layer 1:\n{0}\n" +
            "Board layer 2:\n{1}\n" +
            "Robo at: {2}\n" +
            "Other robos at: {3}\n" +
            "LaserMachine at: {4}" +
            "Laser at: {5}" +
                boardAsString(LAYER1),
                boardAsString(LAYER2),
                getHero(),
                printArray(getOtherHeroes()),
                printArray(getLaserMachines()),
                printArray(getLaser())
            );
    };

    return {
        size : size,
        getHero : getHero,
        getOtherHeroes : getOtherHeroes,
        getLaserMachines : getLaserMachines,
        getLaser : getLaser,
        getWalls : getWalls,
        getBoxes : getBoxes,
        getGold : getGold,
        isMyHeroDead : isMyHeroDead,
        isAt : isAt,
        getAt : getAt,
        toString : toString,
        layer1 : function() {
            boardAsString(LAYER1)
        },
        layer2 : function() {
            boardAsString(LAYER2)
        },
        getBarriers : getBarriers,
        findAll : findAll,
        isAnyOfAt : isAnyOfAt,
        isNear : isNear,
        isBarrierAt : isBarrierAt,
        countNear : countNear
    };
};

var random = function(n){
    return Math.floor(Math.random()*n);
};

game.onMainPageLoad = function() {
    window.location.replace('/register');
}

game.onRegistrationPageLoad = function() {
    initLayout('icancode', 'register.html', game.contextPath,
        ['js/ace/src/ace.js'],
        function() {
            $(document.body).show();
        });
}

// -----------------------------------------------------------------------------------

game.onBoardAllPageLoad = function() {
    initLayout('icancode', 'leaderboard.html', game.contextPath,
        [],
        function() {
            initLeadersTable(game.contextPath, game.playerName, game.code,
                    function() {
                    },
                    function(count, you, link, name, score, maxLength, level) {
                        var star = '';
                        if (count == 1) {
                            star = 'first';
                        } else if (count < 3) {
                            star = 'second';
                        }
                        return '<tr>' +
                                '<td><span class="' + star + ' star">' + count + '<span></td>' +
                                '<td>' + you + '<a href="' + link + '">' + name + '</a></td>' +
                                '<td class="center">' + score + '</td>' +
                            '</tr>';
                    });
            $('#table-logs').removeClass('table');
            $('#table-logs').removeClass('table-striped');
            $(document.body).show();
        });
}

// -----------------------------------------------------------------------------------

game.onBoardPageLoad = function() {
    initLayout('icancode', 'board.html', game.contextPath,
        ['js/ace/src/ace.js'],
        function() {
            var starting = true;

            var editor = ace.edit('ide-block');
            editor.setTheme('ace/theme/monokai');
            editor.session.setMode('ace/mode/javascript');
            editor.setOptions({
                fontSize: '14pt'
            });
            editor.on('focus', function() {
                game.enableJoystick = false;
            });
            editor.on('blur', function() {
                game.enableJoystick = true;
            });
            var typeCounter = 0;
            editor.on('change', function() {
                if (!starting && editor.getValue() == '') {
                    editor.setValue(controller.getDefaultEditorValue(), 1);
                }
                if (typeCounter++ % 10 == 0) {
                    saveSettings();
                }
            });

            var resetButton = $('#ide-reset');
            var releaseButton = $('#ide-release');
            var commitButton = $('#ide-commit');
            var console = $('#ide-console');
            console.empty();

            var print = function(message) {
                console.append('> ' + message + '<br>')
                console.animate({scrollTop: console.prop('scrollHeight')});
            }

            var error = function(message) {
                print('Error: ' + message);
            }

            var replace = function(string, from, to) {
                return string.split(from).join(to);
            }

            var sleep = function(onSuccess) {
                setTimeout(function(){
                    onSuccess();
                }, 1000);
            }

            var send = function(command) {
                if (socket == null) {
                    connect(function() {
                        socket.send(command);
                    });
                } else {
                    socket.send(command);
                }
            }

            var compileCommands = function(onSuccess) {
                var code = editor.getValue();
                print('Compiling program...');
                try {
                    eval(code);
                } catch (e) {
                    error(e.message);
                    print('Please try again.');
                    enableAll();
                    return;
                }

                sleep(function() {
                    print('Uploading program...');

                    sleep(function() {
                        print('Running program...');

                        try {
                            var simpleMode = true;
                            try {
                                simpleMode = !!SIMPLE_MODE;
                            } catch (e) {
                                simpleMode = false;
                            }

                            if (simpleMode) {
                                controller = justDoItMethod();
                            } else {
                                controller = scannerMethod();
                            }
                            controller.storeProgram(program);
                        } catch (e) {
                            error(e.message);
                            print('Please try again.');
                            enableAll();
                            return;
                        }

                        sleep(function() {
                            onSuccess();
                        });
                    });
                });
            }

            var encode = function(command) {
                command = replace(command, 'WAIT', '');
                command = replace(command, 'JUMP', 'ACT(1)');
                command = replace(command, 'RESET', 'ACT(0)');
                command = replace(command, 'LEVEL1', 'ACT(0,1)');
                command = replace(command, 'LEVEL2', 'ACT(0,2)');
                command = replace(command, 'LEVEL3', 'ACT(0,3)');
                command = replace(command, 'LEVEL4', 'ACT(0,4)');
                command = replace(command, 'LEVEL5', 'ACT(0,5)');
                command = replace(command, 'LEVEL6', 'ACT(0,6)');
                command = replace(command, 'MULTIPLAYER', 'ACT(0,1000)');
                return command;
            }

            var justDoItMethod = function() {
                var commands = [];
                var controlling = false;
                var command = null;
                var functionToRun = null;

                var finish = function() {
                    controlling = false;
                    enableAll();
                }

                var robot = {
                    goLeft : function() {
                        commands.push('LEFT');
                    },
                    goRight : function() {
                        commands.push('RIGHT');
                    },
                    goUp : function() {
                        commands.push('UP');
                    },
                    goDown : function() {
                        commands.push('DOWN');
                    },
                    jump : function() {
                       commands.push('JUMP');
                       commands.push('WAIT');
                    },
                    jumpLeft : function() {
                        commands.push('JUMP,LEFT');
                        commands.push('WAIT');
                    },
                    jumpRight : function() {
                        commands.push('JUMP,RIGHT');
                        commands.push('WAIT');
                    },
                    jumpUp : function() {
                        commands.push('JUMP,UP');
                        commands.push('WAIT');
                    },
                    jumpDown : function() {
                        commands.push('JUMP,DOWN');
                        commands.push('WAIT');
                    },
                    getScanner : function() {
                        return {
                            atLeft : function() { },
                            atRight : function() { },
                            atUp : function() { },
                            atDown : function() { },
                            atNear : function(dx, dy) { }
                        }
                    }
                };

                var processCommands = function() {
                    if (commands.length == 0 || commands.indexOf('STOP') == 0) {
                        finish();
                    }
                    if (controlling) {
                        if (commands.length > 0) {
                            command = commands.shift();
                            send(encode(command));
                        }
                    }
                }

                return {
                    isControlling : function() {
                        return controlling;
                    },
                    startControlling : function() {
                        if (commands.indexOf('STOP') != -1) {
                            // do nothing
                        } else if (!!functionToRun) {
                            try {
                                functionToRun(controller.getRobot());
                            } catch (e) {
                                error(e.message);
                                print('Please try again.');
                                enableAll();
                                return;
                            }
                        } else {
                            error('function program(robot) not implemented!');
                            print('Info: if you clean your code you will get info about commands')
                        }
                        controlling = true;
                    },
                    stopControlling : function() {
                        controlling = false;
                    },
                    getRobot : function() {
                        return robot;
                    },
                    processCommands : processCommands,
                    cleanCommands : function() {
                        commands = [];
                    },
                    resetCommand : function() {
                        commands = ['RESET'];
                    },
                    stopCommand : function() {
                        commands.push('STOP');
                    },
                    popLastCommand : function() {
                        var result = command;
                        command == null;
                        return command;
                    },
                    isSimpleMode : function() {
                        return true;
                    },
                    storeProgram : function(fn) {
                        functionToRun = fn;
                    },
                    getDefaultEditorValue : function() {
                        return 'SIMPLE_MODE = true;\n' +
                               '\n' +
                               'function program(robot) {\n' +
                               '    robot.goRight();\n' +
                               '    robot.goDown();\n' +
                               '    robot.goLeft();\n' +
                               '    robot.goUp();\n' +
                               '    robot.jumpRight();\n' +
                               '    robot.jumpDown();\n' +
                               '    robot.jumpLeft();\n' +
                               '    robot.jumpUp();\n' +
                               '    robot.jump();\n' +
                               '}';
                    }
                };
            }

            var scannerMethod = function() {
                var controlling = false;
                var commands = [];
                var command = null;
                board = null;
                var functionToRun = null;

                var finish = function() {
                    controlling = false;
                    commands = [];
                    command = null;
                    board = null;
                    functionToRun = null;
                    enableAll();
                }

                var currentCommand = function() {
                    if (commands.length != 0) {
                        return commands[0];
                    } else {
                        return null;
                    }
                }

                var hasCommand = function(cmd) {
                    return (commands.indexOf(cmd) != -1);
                }

                var robot = {
                    goLeft : function() {
                        commands = [];
                        commands.push('LEFT');
                    },
                    goRight : function() {
                        commands = [];
                        commands.push('RIGHT');
                    },
                    goUp : function() {
                        commands = [];
                        commands.push('UP');
                    },
                    goDown : function() {
                        commands = [];
                        commands.push('DOWN');
                    },
                    jump : function() {
                        commands = [];
                        commands.push('JUMP');
                    },
                    jumpLeft : function() {
                        commands = [];
                        commands.push('JUMP,LEFT');
                        commands.push('WAIT');
                    },
                    jumpRight : function() {
                        commands = [];
                        commands.push('JUMP,RIGHT');
                        commands.push('WAIT');
                    },
                    jumpUp : function() {
                        commands = [];
                        commands.push('JUMP,UP');
                        commands.push('WAIT');
                    },
                    jumpDown : function() {
                        commands = [];
                        commands.push('JUMP,DOWN');
                        commands.push('WAIT');
                    },
                    getScanner : function() {
                        var b = new Board(board);
                        var hero = b.getHero();

                        var atNear = function(dx, dy) {
                            var element1 = b.getAt(hero.getX() + dx, hero.getY() + dy, LAYER1);
                            var element2 = b.getAt(hero.getX() + dx, hero.getY() + dy, LAYER2);

                            var result = [];
                            result.push(element1.type);
                            if (element2.type != 'NONE') {
                                result.push(element2.type);
                            }
                            return result;
                        }

                        return {
                            atLeft : function() {
                                return atNear(-1, 0);
                            },
                            atRight : function() {
                                return atNear(+1, 0);
                            },
                            atUp : function() {
                                return atNear(0, +1);
                            },
                            atDown : function() {
                                return atNear(0, -1);
                            },
                            atNear : atNear
                        }
                    }
                };

                var processCommands = function(newBoard) {
                    board = newBoard;
                    if (!controlling || currentCommand() == 'STOP') {
                        finish();
                        return;
                    }
                    if (hasCommand('STOP')) {
                        commands = ['RESET', 'STOP'];
                    } else if (currentCommand() == 'RESET') {
                        // do nothing
                    } else if (!board) {
                        commands = ['WAIT'];
                    } else {
                        if (!!functionToRun) {
                            try {
                                functionToRun(robot);
                            } catch (e) {
                                error(e.message);
                                print('Please try again.');
                                enableAll();
                                return;
                            }
                        } else {
                            error('function program(robot) not implemented!');
                            print('Info: if you clean your code you will get info about commands')
                        }
                    }
                    if (commands.length == 0) {
                        finish();
                        return;
                    }
                    if (controlling) {
                        if (commands.length > 0) {
                            command = commands.shift();
                            send(encode(command));
                        }
                    }
                }

                return {
                    isControlling : function() {
                        return controlling;
                    },
                    startControlling : function() {
                        controlling = true;
                    },
                    stopControlling : function() {
                        controlling = false;
                    },
                    getRobot : function() {
                        return robot;
                    },
                    processCommands : processCommands,
                    cleanCommands : function() {
                        commands = [];
                    },
                    resetCommand : function() {
                        commands = ['RESET'];
                    },
                    stopCommand : function() {
                        commands.push('STOP');
                    },
                    popLastCommand : function() {
                        var result = command;
                        command == null;
                        return command;
                    },
                    isSimpleMode : function() {
                        return false;
                    },
                    storeProgram : function(fn) {
                        functionToRun = fn;
                    },
                    getDefaultEditorValue : function() {
                        return '// SIMPLE_MODE = true;\n' +
                               '\n' +
                               'function program(robot) {\n' +
                               '    var scanner = robot.getScanner();\n' +
                               '    if (scanner.atRight() != \'HOLE\'){\n' +
                               '        robot.goRight();\n' +
                               '    } else {\n' +
                               '        robot.jumpRight();\n' +
                               '    }\n' +
                               '    // scanner.atLeft();\n' +
                               '    // scanner.atDown();\n' +
                               '    // scanner.atUp();\n' +
                               '    // scanner.atNear(2,-1);\n' +
                               '}';
                    }
                };
            }
            var controller = justDoItMethod();

            var socket = null;
            var connect = function(onSuccess) {
                var hostIp = window.location.hostname;
                var port = window.location.port;
                var server = 'ws://' + hostIp + ':' + port + '/codenjoy-contest/ws';

                print('Connecting to Robo...');
                socket = new WebSocket(server + '?user=' + game.playerName);

                socket.onopen = function() {
                    print('...connected successfully!');
              		print('Hi ' + game.playerName + '! I am Robo! Please write your code.');
                    if (!!onSuccess) {
                        onSuccess();
                    }
                }

                socket.onclose = function(event) {
                    var controlling = controller.isControlling();
                    controller.stopControlling();
                    
                    var reason = ((!!event.reason)?(' reason: ' + event.reason):'');
                    print('Signal lost! Code: ' + event.code + reason);

                    socket = null;
                    sleep(function() {
                        connect(function() {
                            if (controlling) {
                                controller.startControlling();
                                controller.processCommands();
                            }
                        });
                    });
                }

                socket.onmessage = function(event) {
                    var data = event.data;
                    var command = controller.popLastCommand();
                    if (!!command && command != 'WAIT') {
                        print('Robo do ' + command);
                    }
                    controller.processCommands(data);
                }

                socket.onerror = function(error) {
                    error(error);
                    socket = null;
                }
            }

            var enable = function(button, enable) {
                button.prop('disabled', !enable);
            }

            var enableAll = function() {
                enable(resetButton, true);
                enable(releaseButton, true);
                enable(commitButton, true);
            }

            var disableAll = function() {
                enable(resetButton, false);
                enable(releaseButton, false);
                enable(commitButton, false);
            }

            resetButton.click(function() {
                disableAll();

                controller.resetCommand();
                controller.stopCommand();
                if (!controller.isControlling()) {
                    controller.startControlling();
                    controller.processCommands();
                }
            });

            releaseButton.click(function() {
                disableAll();

                controller.cleanCommands();
                compileCommands(function() {
                    controller.resetCommand();
                    controller.startControlling();
                    controller.processCommands();

                    enable(resetButton, true);
                });
            });

            commitButton.click(function() {
                disableAll();

                controller.cleanCommands();
                compileCommands(function() {
                    controller.startControlling();
                    controller.processCommands();

                    enable(resetButton, true);
                });
            });

            disableAll();
            $(document.body).show();

            var saveSettings = function() {
                var text = editor.getValue();
                if (!!text && text != '') {
                    localStorage.setItem('editor.code', editor.getValue());
                    var position =  editor.selection.getCursor();
                    localStorage.setItem('editor.cursor.position.column', position.column);
                    localStorage.setItem('editor.cursor.position.row', position.row);
                    editor.selection.getCursor()
                }
            }
            var loadSettings = function() {
                try {
                    var text = localStorage.getItem('editor.code');
                    if (!!text && text != '') {
                        editor.setValue(text);
                        var column = localStorage.getItem('editor.cursor.position.column');
                        var row = localStorage.getItem('editor.cursor.position.row');
                        editor.focus();
                        editor.selection.moveTo(row, column);
                    } else {
                        editor.setValue(controller.getDefaultEditorValue());
                    }
                } catch (e) {
                    // do nothing
                }
            }
            $(window).unload(saveSettings);

            loadSettings();

            if (!!game.code) {
                connect(function() {
                    enableAll();
                });
            }
            starting = false;
        });
}