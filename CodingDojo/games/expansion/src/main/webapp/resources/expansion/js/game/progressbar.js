/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
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

function initProgressbar(container) {
    var progressBar = $('#' + container + ' li.training');
    progressBar.all = function(aClass) {
        for (var level = 0; level < progressBar.length; level++) {
            progressBar.set(level, aClass);
        }
    }
    progressBar.clean = function(level) {
        $(progressBar[level]).removeClass("level-done");
        $(progressBar[level]).removeClass("level-current");
        $(progressBar[level]).removeClass("level-not-active");
        $(progressBar[level]).removeClass("level-during")
    }
    progressBar.notActive = function(level) {
        progressBar.set(level, "level-not-active");
    }
    progressBar.active = function(level) {
        progressBar.set(level, "level-current");
    }
    progressBar.process = function(level) {
        progressBar.set(level, "level-during");
    }
    progressBar.done = function(level) {
        progressBar.set(level, "level-done");
    }
    progressBar.set = function(level, aClass) {
        progressBar.clean(level);
        $(progressBar[level]).addClass(aClass);
    }
    progressBar.each(function(index) {
        progressBar.notActive(index);
    });

    $(".trainings").mCustomScrollbar({
        scrollButtons:{ enable: true },
        theme:"dark-2",
        axis: "x"
    });

    var initScrolling = function() {
        var width = 0;
        var currentWidth = 0;
        $(".training").each(function() {
            width += $(this).outerWidth();
        });
        $(".training.level-done").each(function() {
            currentWidth += $(this).outerWidth();
        });
        currentWidth += $(".training.level-current").outerWidth();

        if (currentWidth > width) {
            $(".trainings").animate({left: "50%"}, 1000, function(){
            });
        }

        $(".trainings-button.left").click(function(){
            $(".trainings").animate({right: "-=200"}, 1000, function(){
            });
        });

        $(".trainings-button.right").click(function(){
            if ($(".trainings").attr("style")) {
                if (parseFloat($(".trainings").attr("style").substring(7)) >= 0) {
                    return;
                }
            }
            $(".trainings").animate({right: "+=200"}, 1000, function(){
            });
        });
    }
    initScrolling();

    return progressBar;
};
