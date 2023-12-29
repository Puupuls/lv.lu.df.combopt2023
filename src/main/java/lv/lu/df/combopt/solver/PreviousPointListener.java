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
        Point shadowPoint = point;
        while(shadowPoint != null) {
            if (shadowPoint.getPlayer() != null) {
                Boolean isAfterStart = false;

                if (shadowPoint == shadowPoint.getPlayer().getProblem().getStart()) {
                    isAfterStart = true;
                } else if (shadowPoint.getPrev() != null) {
                    if (shadowPoint.getPrev().getIsVisited()) {
                        isAfterStart = true;
                    }
                }

                Boolean isBeforeEnd = false;
                if (shadowPoint == shadowPoint.getPlayer().getProblem().getEnd()) {
                    isBeforeEnd = true;
                } else if (shadowPoint.getNext() != null) {
                    Point temp = shadowPoint.getNext();
                    while (temp != null) {
                        if (temp == shadowPoint.getPlayer().getProblem().getEnd()) {
                            isBeforeEnd = true;
                            break;
                        }
                        temp = temp.getNext();
                    }
                }

                scoreDirector.beforeVariableChanged(shadowPoint, "isVisited");
                shadowPoint.setIsVisited(isAfterStart && isBeforeEnd);
                scoreDirector.afterVariableChanged(shadowPoint, "isVisited");

            } else {
                scoreDirector.beforeVariableChanged(shadowPoint, "isVisited");
                shadowPoint.setIsVisited(false);
                scoreDirector.afterVariableChanged(shadowPoint, "isVisited");
            }
            shadowPoint = shadowPoint.getNext();
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