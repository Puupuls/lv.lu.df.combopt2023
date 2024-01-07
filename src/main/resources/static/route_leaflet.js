let map = L.map('map').setView([56.97, 24.0309], 13);
let color_idx = 0;
const colors = ["#f44336","#e81e63","#9c27b0","#673ab7","#3f51b5","#2196f3","#03a9f4","#00bcd4","#009688",
    "#4caf50","#8bc34a","#cddc39","#ffeb3b","#ffc107","#ff9800","#ff5722"];

const locationGreenIcon = L.divIcon({
    html: '<i class="fas fa-map-marker-alt" style="color: #00bb00"></i>',
    className: 'dummy'
});
const locationRedIcon = L.divIcon({
    html: '<i class="fas fa-map-marker-alt" style="color: #ee0000"></i>',
    className: 'dummy'
});
const locationOrangeIcon = L.divIcon({
    html: '<i class="fas fa-map-marker-alt" style="color: #bb8000"></i>',
    className: 'dummy'
});
const finishIcon = L.divIcon({
    html: '<i class="fas fa-flag-checkered"></i>',
    className: 'dummy'
});
const startIcon = L.divIcon({
    html: '<i class="fas fa-flag" style="color: #007700"></i>',
    className: 'dummy'
});

$(document).ready(function () {
    const urlParams = new URLSearchParams(window.location.search);
    const solutionId = urlParams.get('id');

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    $.getJSON("/routes/score?id=" + solutionId, function(analysis) {
        $("#score_a").attr({
            "title":"Score Brakedown",
            "data-bs-content":getScorePopoverContent(analysis.constraints) + "",
            "data-bs-html":"true"
        });
        $("#score_text").text(analysis.score);
        $("#score_text").attr({
            "class": "badge " + getClassFromScore(analysis.score)
        });
        $(function () {
            $('[data-toggle="popover"]').popover()
        });
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

    var indictmentMap = {};
    indictments.forEach((indictment) => {
        indictmentMap[indictment.indictedObjectID] = indictment;
    })

    let points = {};
    points[solution.start.name] = solution.start;
    solution.pointList.forEach((point) => {
        points[point.name] = point;
    });

    Object.values(points).forEach((point) => {
        const location = [point.lat, point.lon];
        const marker = L.marker(location).addTo(map);
        if(point.name === solution.start.name) {
            marker.setIcon(startIcon);
        }else if(point.name === solution.end) {
            marker.setIcon(finishIcon);
        }else{
            if(getHardScore(indictmentMap[point.name].score) < 0) {
                marker.setIcon(locationRedIcon);
            } else if(getMediumScore(indictmentMap[point.name].score) < 0) {
                marker.setIcon(locationOrangeIcon);
            } else {
                marker.setIcon(locationGreenIcon);
            }
        }
        if(point.prev){
            const next_location = [points[point.prev].lat, points[point.prev].lon];
            L.polyline([location, next_location], {color: "#00000077"}).addTo(map);
            // Add dist to prev on line
            const dist = point.distanceToPrev;
            const dist_location = [(location[0] + next_location[0])/2, (location[1] + next_location[1])/2];
            L.marker(dist_location, {
                icon: L.divIcon({
                    html: dist + "m",
                    className: 'dummy'
                })
            }).addTo(map);
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

function getVisitIcon(v_type, indictment) {
    if (indictment==undefined || getHardScore(indictment.score) == 0) {
        return v_type == "STOCK" ? stockIcon : v_type == "PICKUP" ? pickupIcon : deliveryIcon;
    } else {
        return v_type == "STOCK" ? stockIcon_red : v_type == "PICKUP" ? pickupIcon_red : deliveryIcon_red;
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