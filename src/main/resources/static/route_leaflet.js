var map = L.map('map').setView([56.94, 24.12], 12);
var color_idx = 0;
const colors = ["#f44336","#e81e63","#9c27b0","#673ab7","#3f51b5","#2196f3","#03a9f4","#00bcd4","#009688",
    "#4caf50","#8bc34a","#cddc39","#ffeb3b","#ffc107","#ff9800","#ff5722"];
;
const defaultIcon = new L.Icon.Default();
const playerIcon = L.divIcon({
    html: '<i class="fas fa-person"></i>',
    className: 'noWhiteBg'
});
const pointIconRed = L.divIcon({
    html: '<i class="fas fa-map-marker-alt" style="color:red"></i>',
    className: 'noWhiteBg'
})
const pointIconGreen = L.divIcon({
    html: '<i class="fas fa-map-marker-alt" style="color:green"></i>',
    className: 'noWhiteBg'
})
const pointIconYellow = L.divIcon({
    html: '<i class="fas fa-map-marker-alt" style="color:orange"></i>',
    className: 'noWhiteBg'
})
const startIcon = L.divIcon({
    html: '<i class="fas fa-flag" style="color: green"></i>',
    className: 'noWhiteBg'
})
const endIcon = L.divIcon({
    html: '<i class="fas fa-flag-checkered"></i>',
    className: 'noWhiteBg'
})

$(document).ready(function () {
    const urlParams = new URLSearchParams(window.location.search);
    const solutionId = urlParams.get('id');

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    $.getJSON("/routes/score?id=" + solutionId, function(analysis) {
        g_analysis = analysis;
        let badge = "badge " + getClassFromScore(analysis.score);
        $("#score_text").text(analysis.score);
        $("#score_text").attr({"class": badge});
    });

    $.getJSON("/routes/solution?id=" + solutionId, function(solution) {
        $.getJSON("/routes/indictments?id=" + solutionId, function(indictments) {
            renderRoute(solution, indictments);
            $(function () {
                $('[data-toggle="popover"]').popover()
            })
        })
    });
});

function renderRoute(solution, indictments) {
    $("#solutionTitle").text("solutionId: " + solution.solutionId);

    let indictmentMap = {};
    indictments.forEach((indictment) => {
        indictmentMap[indictment.indictedObjectID] = indictment;
    })

    let previous_location = undefined;
    const color = getColor();
    let pointsMap = {};
    solution.pointList.forEach((point) => {
        pointsMap[point.name] = point;
    })
    solution.player.points.forEach((pname) => {
        let point = pointsMap[pname];
        const location = [point.lat, point.lon];
        const marker = L.marker(location).addTo(map).setIcon(getPointIcon(solution, point, indictmentMap[point.name]));
        marker.bindPopup(`<b>${point.name}</b><hr>${getEntityPopoverContent(point, indictmentMap)}`);
        if(point.isVisited) {
            if (previous_location)
                L.polyline([previous_location, location], {color: color}).addTo(map);
            previous_location = location;
        }
    });
}

function getEntityPopoverContent(entityId, indictmentMap) {
    var popover_content = "";
    const indictment = indictmentMap[entityId];
    if (indictment != null) {
        popover_content = popover_content + "Total score: <b>" + indictment.score + "</b> (" + indictment.matchCount + ")<br>";
        indictment.constraintMatches.forEach((match) => {
            if (getHardScore(match.score) == 0) {
                popover_content = popover_content + match.constraintName + " : " + match.score + "<br>";
            } else {
                popover_content = popover_content + "<b>" + match.constraintName + " : " + match.score + "</b><br>";
            }
        })
    }
    return popover_content;
}

function getPointIcon(sol, point, indictment) {
    let isStartPoint = point.name === sol.start;
    let isEndPoint = point.name === sol.end;
    let isVisited = point.isVisited;

    let hardScore = getHardScore(indictment.score);
    let mediumScore = getMediumScore(indictment.score);
    let softScore = getSoftScore(indictment.score);

    if (isStartPoint) {
        return startIcon;
    }else if (isEndPoint) {
        return endIcon;
    }else {
        if(hardScore !== 0) {
            return pointIconRed;
        }else if(mediumScore !== 0) {
            return pointIconYellow;
        }else{
            return pointIconGreen;
        }
    }
}

function getColor() {
    color_idx = (color_idx + 1) % colors.length;
    return colors[color_idx];
}

function getScorePopoverContent(constraint_list) {
    var popover_content = "";
    constraint_list.forEach((constraint) => {
        if (getHardScore(constraint.score) == 0) {
            popover_content = popover_content + constraint.name + " : " + constraint.score + "<br>";
        } else {
            popover_content = popover_content + "<b>" + constraint.name + " : " + constraint.score + "</b><br>";
        }
    })
    return popover_content;
}

function getHardScore(score) {
    return Number.parseInt(score.slice(0,score.indexOf("hard")))
}

function getMediumScore(score) {
    return Number.parseInt(score.slice(score.indexOf("hard/")+5,score.indexOf("medium")))
}

function getSoftScore(score) {
    return Number.parseInt(score.slice(score.indexOf("medium/")+7,score.indexOf("soft")))
}

function formatTime(timeInSeconds) {
    if (timeInSeconds != null) {
        const HH = Math.floor(timeInSeconds / 3600).toString().padStart(2, '0');
        const MM = Math.floor((timeInSeconds % 3600) / 60).toString().padStart(2, '0');
        const SS = Math.floor(timeInSeconds % 60).toString().padStart(2, '0');
        return HH + ":" + MM + ":" + SS;
    } else return "null";
}

function getClassFromScore(score) {
    let color = "bg-success"
    if(getMediumScore(score) < 0) { color = "bg-warning" }
    if(getHardScore(score) < 0) { color = "bg-danger" }
    return color;
}