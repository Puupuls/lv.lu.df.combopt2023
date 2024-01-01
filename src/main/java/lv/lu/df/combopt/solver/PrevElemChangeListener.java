package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.api.domain.variable.VariableListener;
import ai.timefold.solver.core.api.score.director.ScoreDirector;
import lv.lu.df.combopt.domain.Location;
import lv.lu.df.combopt.domain.NavigationSolution;
import lv.lu.df.combopt.domain.TaskLocation;

public class PrevElemChangeListener implements VariableListener<NavigationSolution, Location> {

    @Override
    public void beforeVariableChanged(ScoreDirector<NavigationSolution> scoreDirector, Location location) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector<NavigationSolution> scoreDirector, Location location) {
        // If location is of type TaskLocation, then it has a prev element
        if (location instanceof TaskLocation) {
            scoreDirector.beforeVariableChanged(((TaskLocation) location).getPrev(), "next");
            ((TaskLocation) location).getPrev().setNext(location);
            scoreDirector.afterVariableChanged(((TaskLocation) location).getPrev(), "next");
        }else{
            scoreDirector.beforeVariableChanged(location.getNavigationSolution().getStart(), "next");
            location.getNavigationSolution().getStart().setNext(location);
            scoreDirector.afterVariableChanged(location.getNavigationSolution().getStart(), "next");
        }

    }

    @Override
    public void beforeEntityAdded(ScoreDirector<NavigationSolution> scoreDirector, Location location) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector<NavigationSolution> scoreDirector, Location location) {

    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<NavigationSolution> scoreDirector, Location location) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector<NavigationSolution> scoreDirector, Location location) {

    }
}
