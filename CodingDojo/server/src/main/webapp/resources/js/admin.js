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

pages = pages || {};

pages.admin = function() {
    var contextPath = game.contextPath = getSettings('contextPath');

    // ------------------------ registration settings -----------------------
    var settings = new AdminSettings(contextPath, 'general', 'registration');

    var loadRegSettings = function() {
        settings.load(function(data) {
            setRegSettings(data);
        });
    }

    var saveRegSettings = function() {
        settings.save(getRegSettings(),
            function() {
                loadRegSettings();
            }, function(errMsg) {
                console.log(errMsg);
            });
    }

    var getRegSettings = function() {
        return {
            showGamesOnRegistration : $('#show-games').prop('checked'),
            showNamesOnRegistration : $('#show-names').prop('checked'),
            showTechSkillsOnRegistration : $('#show-tech').prop('checked'),
            showUniversityOnRegistration : $('#show-university').prop('checked'),
            defaultGameOnRegistration : $('#default-game').find('option:selected').text()
        };
    }

    var setRegSettings = function(data) {
        if ($.isEmptyObject(data)) {
            data = {
                showGamesOnRegistration: true,
                showNamesOnRegistration: false,
                showCitiesOnRegistration: false,
                showTechSkillsOnRegistration: false,
                showUniversityOnRegistration: false,
                defaultGameOnRegistration: null
            };
        }
        if (!data.defaultGameOnRegistration) {
            data.defaultGameOnRegistration = $("#default-game option:first").val();
        }

        $('#show-games').prop('checked', data.showGamesOnRegistration);
        $('#show-names').prop('checked', data.showNamesOnRegistration);
        $('#show-tech').prop('checked', data.showTechSkillsOnRegistration);
        $('#show-university').prop('checked', data.showUniversityOnRegistration);
        $('#default-game').val(data.defaultGameOnRegistration);
    }

    $('#registration-save-button').click(function() {
        saveRegSettings();
    });

    // ------------------------ init ----------------------
    validatePlayerRegistration("#adminSettings");
    initHotkeys();
    loadRegSettings();
}
