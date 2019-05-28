/*-
 * #%L
 * iCanCode - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
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
function initRunnerJs(game, libs, getLevelInfo) {
    
    if (game.debug) {
        game.debugger();
    }
    
    var starting = true;

    var container = $('#ide-content');
    container.empty();
    container.append('<pre id="ide-block" class="editor">function program(robot) {\n' +
                      '    var scanner = robot.getScanner();\n' +
                      '    if (scanner.atRight() != "HOLE"){\n' +
                      '        robot.goRight();\n' +
                      '    } else {\n' +
                      '        robot.jumpRight();\n' +
                      '    }\n' +
                      '}</pre>');

    var editor = initEditor(libs, 'ide-block', autocomplete);

    editor.on('focus', function() {
        game.savedEnableJoystick = game.enableJoystick;
        game.enableJoystick = false;
    });

    editor.on('blur', function() {
        game.enableJoystick = !!game.savedEnableJoystick;
        game.savedEnableJoystick = null;
    });

    var typeCounter = 0;
    var clean = null;
    editor.on('change', function() {
        if (!game.code) {
            return;
        }

        if (!starting && editor.getValue() == '') {
            clean = 0;
        }

        if (typeCounter++ % 10 == 0) {
            saveSettings();
        }
    });

    $('body').bind("tick", function() {
        if (!game.code) {
            return;
        }

        if (clean != null) {
            clean++;
            if (clean == 2) {
                clean = null;
                if (editor.getValue() == '') {
                    editor.setValue(getDefaultEditorValue(), 1);
                } else if (editor.getValue() == 'win') {
                    editor.setValue(getWinEditorValue(), 1);
                } else if (editor.getValue() == 'ref') {
                    editor.setValue(getRefactoringEditorValue(), 1);
                }
            }
        }
    });

    var getDefaultEditorValue = function() {
        return getLevelInfo().defaultCode;
    }

    var getWinEditorValue = function() {
        return getLevelInfo().winCode;
    }

    var getRefactoringEditorValue = function() {
        var code = getLevelInfo().refactoringCode;
        if (!!code) {
            return code;
        } else {
            return getWinEditorValue();
        }
    }

    // ----------------------- save ide code -------------------
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
                editor.setValue(getDefaultEditorValue());
            }
        } catch (e) {
            // do nothing
        }
        if (starting) {
            starting = false;
        }
    }
    $(window).on('unload', saveSettings);

    var functionToRun = null;

    return {
        loadSettings : loadSettings,
        getValue : function() {
            return editor.getValue();
        },
        setStubValue : function() {
            editor.setValue('function program(robot) {\n' +
                    '    // PLEASE REGISTER\n' +
                    '}');
        },
        compileProgram : function(robot) {
            var code = editor.getValue();
            eval(code);
            functionToRun = program;
        },
        cleanProgram : function() {
            functionToRun = null;
        },
        isProgramCompiled : function() {
            return functionToRun != null;
        },
        runProgram : function(robot) {
            runProgram(functionToRun, robot);
        }
    };
}