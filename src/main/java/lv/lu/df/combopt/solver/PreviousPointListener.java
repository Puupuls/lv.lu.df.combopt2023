package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.api.domain.variable.VariableListener;
import ai.timefold.solver.core.api.score.director.ScoreDirector;
import lv.lu.df.combopt.domain.NavigationSolution;
import lv.lu.df.combopt.domain.Point;

public class PreviousPointListener implements VariableListener<NavigationSolution, Point> {
    @Override
    public void beforeVariableChanged(ScoreDirector<NavigationSolution> scoreDirector, Point point) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector<NavigationSolution> scoreDirector, Point point) {
            Double arrival = point.getPreviousVisited() != null && point.getPreviousVisited().getDepartureTime() != null?
                    point.getPreviousVisited().getDepartureTime() + point.getPlayer().getMinutesToTravel(
                            point.getPreviousVisited().getLocation().distanceTo(point.getLocation())
                    ) : 0;

            Point shadowPoint = point;
            while (shadowPoint != null) {
                scoreDirector.beforeVariableChanged(shadowPoint, "arrivalTime");
                shadowPoint.setArrivalTime(arrival);
                scoreDirector.afterVariableChanged(shadowPoint, "arrivalTime");

                shadowPoint = shadowPoint.getNextVisited();

                if (shadowPoint != null) {
                    arrival = shadowPoint.getPreviousVisited().getDepartureTime() +
                            shadowPoint.getPlayer().getMinutesToTravel(
                                shadowPoint.getPreviousVisited().getLocation().distanceTo(shadowPoint.getLocation())
                            );
                }

            }
        }

    @Override
    public void beforeEntityAdded(ScoreDirector<NavigationSolution> scoreDirector, Point point) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector<NavigationSolution> scoreDirector, Point point) {

    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<NavigationSolution> scoreDirector, Point point) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector<NavigationSolution> scoreDirector, Point point) {

    }
}