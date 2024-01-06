package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.api.domain.variable.VariableListener;
import ai.timefold.solver.core.api.score.director.ScoreDirector;
import lv.lu.df.combopt.domain.Location;
import lv.lu.df.combopt.domain.NavigationSolution;
import lv.lu.df.combopt.domain.TaskLocation;

import java.util.ArrayList;
import java.util.List;

public class PrevElemChangeListener implements VariableListener<NavigationSolution, Location> {

    @Override
    public void beforeVariableChanged(ScoreDirector<NavigationSolution> scoreDirector, Location location) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector<NavigationSolution> scoreDirector, Location location) {
        if(location == location.getNavigationSolution().getEnd()){
            return;
        }
        List<String> childLocations = new ArrayList<>();
        for(Location tl : location.getNavigationSolution().getPointList()) {
            TaskLocation tl1 = (TaskLocation) tl;
            if (tl1.getPrev() != null) {
                childLocations.add(tl1.getPrev().getName());
            }
        }
        TaskLocation tail = (TaskLocation) location;
        for(Location tl : location.getNavigationSolution().getPointList()) {
            if(!childLocations.contains(tl.getName())) {
                tail = (TaskLocation) tl;
                break;
            }
        }

        scoreDirector.beforeVariableChanged(location.getNavigationSolution().getEnd(), "prev");
        location.getNavigationSolution().getEnd().setPrev(tail);
        scoreDirector.afterVariableChanged(location.getNavigationSolution().getEnd(), "prev");
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
