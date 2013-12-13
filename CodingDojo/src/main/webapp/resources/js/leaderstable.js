function initLeadersTable(contextPath){

    function leaderboardStyle() {
        var width = $("#leaderboard").width();
        var margin = 20;
        $("#glasses").width($(window).width() - width - margin).css({ marginLeft: margin, marginTop: margin });

        $("#leaderboard").width(width).css({ position: "absolute",
                        marginLeft: 0, marginTop: margin,
                        top: 0, left: $("#glasses").width()});
    }

    function getFirstValue(data) {
        return data[Object.keys(data)[0]];
    }

    function sortByScore(data) {
        var vals = new Array();

        for (i in data) {
            vals.push([i, data[i]])
        }
        vals = vals.sort(function(a, b) {
            return b[1] - a[1];
        });

        var result = new Object();

        for (i in vals) {
            result[vals[i][0]] = vals[i][1];
        }

        return result;
    }

    function drawLeaderTable(data) {
        if (data == null) {
            $("#table-logs-body").empty();
            return;
        }
        data = getFirstValue(data);
        data = data.scores;
        if (data == null) {
            $("#table-logs-body").empty();
            return;
        }
        data = $.parseJSON(data);

        data = sortByScore(data);

        var tbody = '';
        var count = 0;
        $.each(data, function (playerName, score) {
            if (playerName == 'chatLog') {
                return;
            }

            count++;
            tbody +=
             '<tr>' +
                 '<td>' + count + '</td>' +
                 '<td>' + playerName + '</td>' +
                 '<td class="center">' + score + '</td>' +
                 //  '<td class="center">' + playerData.maxLength + '</td>' +
                 // '<td class="center">' + playerData.level + '</td>' +
             '</tr>'
        });

        $("#table-logs-body").empty().append(tbody);
        $("#leaderboard").trigger($.Event('resize'));
    }

    $('body').bind("board-updated", function(event, data) {
        drawLeaderTable(data);
    });

    if (!!$("#glasses")) {
        $(window).resize(leaderboardStyle);
        leaderboardStyle();
    }
};