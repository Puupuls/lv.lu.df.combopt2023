function getHardScore(score) {
    return Number.parseInt(score.slice(0,score.indexOf("hard")))
}

function getMediumScore(score) {
    return Number.parseInt(score.slice(score.indexOf("hard/")+5,score.indexOf("medium")))
}

function getSoftScore(score) {
    return Number.parseInt(score.slice(score.indexOf("medium/")+7,score.indexOf("soft")))
}

function getClassFromScore(score) {
    let color = "bg-success"
    if(getMediumScore(score) < 0) { color = "bg-warning" }
    if(getHardScore(score) < 0) { color = "bg-danger" }
    return color;
}

let g_analysis, g_solution, g_indictments;
$(document).ready(function () {
    const urlParams = new URLSearchParams(window.location.search);
    const solutionId = urlParams.get('id');

    $.getJSON("/routes/score?id=" + solutionId, function(analysis) {
        g_analysis = analysis;
        var badge = "badge " + getClassFromScore(analysis.score);
        $("#score_text").text(analysis.score);
        $("#score_text").attr({"class":badge});
    });

    $.getJSON("/routes/solution?id=" + solutionId, function(solution) {
        g_solution = solution;
        let stats = $('#core_stats');
        stats.append($('<div class=""><b>Time limit:</b> ' + formatTime(solution.maxDuration) + '</div>'));
        stats.append($('<div class=""><b>Time spent:</b> ' + formatTime(solution.player.totalTime) + ' (Cost per second: ' + solution.player.timeCost + 'soft)</div>'));
        stats.append($('<div class=""><b>Speed:</b> ' + solution.player.speed + '</div>'));
        stats.append($('<div class=""><b>Total distance:</b> ' + Math.round(solution.player.totalDistance * 100) / 100 + 'm (Cost per meter: ' + solution.player.distanceCost + 'soft)</div>'));
        stats.append($('<div class=""><b>Total altitude change:</b> ' + Math.round(solution.player.totalAltitudeChange * 100) / 100 + ' (Cost per meter: ' + solution.player.altitudeCost + 'soft)</div>'));
        stats.append($('<div class=""><b>Total points visited:</b> ' + solution.player.visitedPointsCount + '</div>'));
        stats.append($('<div class=""><b>Total points not visited:</b> ' + (solution.pointList.length - solution.player.visitedPointsCount)+'</div>'));
        stats.append($('<div class=""><b>Collected point value:</b> ' + solution.player.collectedPointValue+'</div>'));
        stats.append($('<div class=""><b>Missed point value:</b> ' + (solution.player.totalPointValue - solution.player.collectedPointValue) + '</div>'));

        let j_indictments = $('#indictments');
        $.getJSON("/routes/indictments?id=" + solutionId, function(indictments) {
            g_indictments = indictments;
            let indictmentMap = {};
            indictments.forEach((indictment) => {
                indictmentMap[indictment.indictedObjectID] = indictment;
            })

            let playerIndictments = indictmentMap[solution.player.id];
            let p = $('<li class="' + getClassFromScore(playerIndictments.score) + '"><b>Player</b></li>')
            let ul = $('<ul></ul>')
            p.append(ul);
            j_indictments.append(p);
            if(playerIndictments != null) {
                playerIndictments.constraintMatches.forEach((i) => {
                    ul.append($('<li><b>' + i.constraintName + ':</b> ' + i.score + '</li>'));
                })
            }

        })
    });

});

function formatTime(timeInSeconds) {
    if (timeInSeconds != null) {
        const HH = Math.floor(timeInSeconds / 3600).toString().padStart(2, '0');
        const MM = Math.floor((timeInSeconds % 3600) / 60).toString().padStart(2, '0');
        const SS = Math.floor(timeInSeconds % 60).toString().padStart(2, '0');
        return HH + ":" + MM + ":" + SS;
    } else return "null";
}



