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
/**
 * Created by Mikhail_Udalyi on 08.08.2016.
 */

var boardPageLoad = function() {
    var libs = game.contextPath + 'resources/' + game.gameName + '/js';
    if (game.demo) {
        libs = 'js';
    }

    // ----------------------- disable backspace -------------------
    $(document).on('keydown', function(e) {
        if (e.which === 8 && !$(e.target).is('input, textarea')) {
            e.preventDefault();
        }
    });
    // ----------------------- init scrollbar -------------------
    $(".content").mCustomScrollbar({
        theme:'dark-2',
        axis: 'yx',
        mouseWheel : { enable : true }
    });

    // ----------------------- init tooltip -------------------
    $('[data-toggle="tooltip"]').tooltip();

    // ----------------------- init console -------------------
    var console = initConsole();
    console.printCongrats = function() {
        console.print('Congrats ' + game.playerName + '! You have passed the puzzle!!!');
    }

    console.printHello = function() {
        console.print('Hello ' + game.playerName + '! I am Robot! Please write your code and press Commit.');
    }

    // ----------------------- init slider -------------------
    var setupSlider = function() {
        $("#console-panel").click(function(){
            if ($("#console").hasClass("open")) {
                $("#console").removeClass("open").addClass("close");
                $("#block").removeClass("console-open").addClass("console-close");
                $("#console-panel-icon").removeClass("fa-angle-right").addClass("fa-angle-left");
            } else {
                $("#console").removeClass("close").addClass("open");
                $("#block").removeClass("console-close").addClass("console-open");
                $("#console-panel-icon").removeClass("fa-angle-left").addClass("fa-angle-right");
            }
        });

        $("#editor-panel").click(function(){
            if (!$("#main").hasClass("editor-fullscreen")) {
                $("#main").addClass("editor-fullscreen");
                $("#editor-panel-icon").removeClass("fa-angle-left").addClass("fa-angle-right");
            } else {
                $("#main").removeClass("editor-fullscreen");
                $("#editor-panel-icon").removeClass("fa-angle-right").addClass("fa-angle-left");
            }
        });
    }
    setupSlider();

    // ----------------------- win window --------------
    var showWinWindow = function() {
        $("#modal-level").removeClass("close");
    };

    var hideWinWindow = function() {
        $("#modal-level").addClass("close");
    };

    var setupWinWindow = function() {
        $("#close-level-modal").click(function(){
            hideWinWindow();
        });
        $("#next-level-modal").click(function(){
            hideWinWindow();
        });
        $("#previous-level-modal").click(function(){
            hideWinWindow();
            levelProgress.selectLevel(1);
        });
        $("body").keydown(function(event){
            if (event.which == 27){
                $("#close-level-modal").click();
            }
        });
    };
    setupWinWindow();

    // ----------------------- init help modal -------------------
    $("#close").click(function(){
        $("#modal").addClass("close");
    });
    $("body").keydown(function(event){
        if (event.which == 27){
            $("#close").click();
        }
    });

    // ----------------------- init buttons -------------------
    var onCommitClick = function() {
        buttons.disableAll();
        resetRobot();
        controller.commit();
    }
    var onResetClick = function() {
        buttons.disableAll();
        controller.reset();
    }
    var onHelpClick = function() {
        var level = levelProgress.getCurrentLevel();
        var help = levelInfo.getInfo(level).help;
        $('#ide-help-window').html(help);
        $("#modal").removeClass("close");
    };
    var buttons = initButtons(onCommitClick, onResetClick, onHelpClick);

    // ----------------------- init runner -------------------
//    var runner = initRunnerBefunge(console);

    var getCurrentLevelInfo = function(){
        return levelInfo.getInfo(levelProgress.getCurrentLevel());
    };
    var runner = initRunnerJs(game, libs, getCurrentLevelInfo);

    // ------------------------ init controller ----------------------
    var sleep = function(onSuccess) {
        setTimeout(function(){
            onSuccess();
        }, 1000);
    }

    var onSocketMessage = function(data) {
        controller.onMessage(data);
    }
    var onSocketClose = function() {
        controller.reconnect();
    }
    var socket = initSocket(game, buttons, console, onSocketMessage, onSocketClose);

    var controller = initController(socket, runner, console, buttons, function() {
        return robot;
    });

    var robot = null;
    var resetRobot = function() {
        robot = initRobot(console, controller);
    }
    resetRobot();

    // ----------------------- init level info -----------------------------
    var levelInfo = initLevelInfo();

    // ----------------------- init progressbar -------------------
    var oldLastPassed = 0;
    var onUpdate = function(level, multiple, lastPassed) {
        if (oldLastPassed < lastPassed) {
            oldLastPassed = lastPassed;
            showWinWindow();
        }
    }
    var onChangeLevel = function(level) {
        initAutocomplete(level, levelInfo);
    }
    var levelProgress = initLevelProgress(game, socket, onUpdate, onChangeLevel);

    // ----------------------- starting UI -------------------
    if (game.demo) {
        var data = '{"' + game.playerName + '":{"board":"{\\"levelProgress\\":{\\"total\\":18,\\"current\\":3,\\"lastPassed\\":2,\\"multiple\\":false},\\"layers\\":[\\"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOCDDDDEOOOOOOOOOOJaBB9FOOOOOOOOOOIHHHHGOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO\\",\\"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\\"]}","gameName":"icancode","score":150,"maxLength":0,"length":0,"level":1,"boardSize":16,"info":"","scores":"{\\"fasdfddd@gmail.com\\":150,\\"SDAsd@sas.as\\":2250}","coordinates":"{\\"fasdfddd@gmail.com\\":{\\"y\\":8,\\"x\\":9},\\"SDAsd@sas.as\\":{\\"y\\":8,\\"x\\":9}}"}}';
        $('body').trigger('board-updated', JSON.parse(data));
    }
    buttons.disableAll();
    $(document.body).show();

    if (!!game.code) {
        runner.loadSettings();

        socket.connect(function() {
            buttons.enableAll();
        });
    } else {
        buttons.enable(helpButton, false);

        var link = $('#register-link').attr('href');
        console.print('<a href="' + link + '">Please register</a>');

        runner.setStubValue();
    }
};