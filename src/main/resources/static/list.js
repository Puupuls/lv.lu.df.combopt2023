$(document).ready(function () {
    $.getJSON("/routes/list", function(solutions) {
        let listofroutes = $("#listofsolutions");
        $.each(solutions, function(idx, s) {
              listofroutes.append($(`
                    <li>
                        <a href="route.html?id=${s.solutionId}">
                            ${s.solutionId} ${s.score}
                        </a>
                    </li>`));
        });
    });
});