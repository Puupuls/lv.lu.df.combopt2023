let map = L.map('map').setView([56.95, 24.05], 13);

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
    let visited_points = [];
    let unvisited_points = [];
    solution.pointList.forEach((point) => {
        if(point.isVisited) {
            visited_points.push(point);
        } else {
            unvisited_points.push(point);
        }
    });
    $("#solutionTitle").html(
        `
            <h3>Route ${solutionId}</h3>
            <p>
                <b>Created:</b> <br/> ${solution.created}
                <br/>
                <b>Last solution:</b> <br/> ${solution.lastSolutionTime}
                <br/>
                <b>Path distance:</b> <br/> ${solution.totalDistance/1000} km
                <br/>
                <b>Time taken:</b> <br/> ${formatTime(solution.totalTime)} / ${formatTime(solution.maxDuration)}
                <br/>
                <b>Points visited:</b> <br/> ${visited_points.length} / ${solution.pointList.length}
                <br/>
                <b>Point value collected:</b> <br/> ${visited_points.reduce((a, b) => a + b.value, 0)} / ${solution.pointList.reduce((a, b) => a + b.value, 0)}
                <br/>
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
        if(point.prev && point.isVisited){
            L.polyline(point.pathMap[point.prev], {color: "#5577ffcc", weight: 5}).addTo(map).bindPopup(
                `<b>${points[point.prev].name}-${point.name}</b><br/>Distance: ${point.distanceToPrev/1000}km <br/>`
            )
        }
    });
}

function getEntityPopoverContent(point, indictmentMap) {
    var popover_content = `<b>${point.name}</b> ${point.isVisited? "Visited": "Not-Visited"} <br/>
    Value: ${point.value}<br/>
    Time to complete: ${point.timeToComplete}<br/>
    Time since start: ${formatTime(point.timeSinceStart)}<br/>
    Distance from previous: ${point.distanceToPrev/1000}km<br/>
    <hr/>`;
    const indictment = indictmentMap[point.name];
    if (indictment != null) {
        popover_content += `Score: <b>${indictment.score}</b> (${indictment.matchCount})<hr>Indicaments:<br>`;
        indictment.constraintMatches.forEach((match) => {
            if (getHardScore(match.score) == 0) {
                popover_content += `<b>${match.constraintName}</b> : ${match.score}<br>`;
            } else {
                popover_content += `<b> ${match.constraintName}</b> : ${match.score}<br>`;
            }
        })
    }
    return popover_content;
}

function getScorePopoverContent(constraint_list) {
    var popover_content = ``;
    constraint_list.forEach((constraint) => {
        if (getHardScore(constraint.score) === 0) {
            popover_content += `${constraint.name} : {constraint.score}<br>`;
        } else {
            popover_content += `<b>${constraint.name}: ${constraint.score}</b><br>`;
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