$(document).ready(function () {
    $.getJSON("/routes/list", function(routes) {
        var listofroutes = $("#listofroutes");
        $.each(routes, function(idx, value) {
              listofroutes.append($('<li><a href="route.html?id='+ value.solutionId + '">' +
                    value.name + ' ' +
               value.score +'</a><a href="route_leaflet.html?id='+ value.solutionId + '"> (map) </a></li>'));
        });
    });
});