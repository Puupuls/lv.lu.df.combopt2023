let map = L.map('map').setView([56.97, 24.0309], 13);

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

const urlParams = new URLSearchParams(window.location.search);
const solutionId = urlParams.get('id');
$(document).ready(function () {

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);
    getData()
});

function getData() {
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
}

function renderRoute(solution, indictments) {
    $("#solutionTitle").html(
        `
            <h3>Route ${solutionId}</h3>
            <p>
                <b>Created:</b> ${solution.created}
                <br/>
                <b>Last sol:</b> ${solution.lastSolutionTime}
            </p>
        `
    );
    map.eachLayer((layer) => {
        if (layer instanceof L.Marker || layer instanceof L.Polyline) {
            layer.remove();
        }
    });

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
        marker.bindPopup(getEntityPopoverContent(point, indictmentMap))
        if(point.prev && points[point.name].isVisited){
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

function getEntityPopoverContent(point, indictmentMap) {
    var popover_content = "";
    const indictment = indictmentMap[point.name];
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