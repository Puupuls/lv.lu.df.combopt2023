package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.api.domain.variable.VariableListener;
import ai.timefold.solver.core.api.score.director.ScoreDirector;
import lv.lu.df.combopt.domain.Location;
import lv.lu.df.combopt.domain.NavigationSolution;
import lv.lu.df.combopt.domain.Start;
import lv.lu.df.combopt.domain.TaskLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PrevElemChangeListener implements VariableListener<NavigationSolution, TaskLocation> {

    @Override
    public void beforeVariableChanged(ScoreDirector<NavigationSolution> scoreDirector, TaskLocation location) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector<NavigationSolution> scoreDirector, TaskLocation location) {
        // Update:
        // distanceToPrev
        // distanceSinceStart
        // timeSinceStart
        // isVisited

        if(location.getPrev() == null) {
            scoreDirector.beforeVariableChanged(location, "distanceToPrev");
            location.setDistanceToPrev(0);
            scoreDirector.afterVariableChanged(location, "distanceToPrev");
            scoreDirector.beforeVariableChanged(location, "distanceSinceStart");
            location.setDistanceSinceStart(0);
            scoreDirector.afterVariableChanged(location, "distanceSinceStart");
            scoreDirector.beforeVariableChanged(location, "timeSinceStart");
            location.setTimeSinceStart(0);
            scoreDirector.afterVariableChanged(location, "timeSinceStart");
            scoreDirector.beforeVariableChanged(location, "isVisited");
            location.setIsVisited(false);
            scoreDirector.afterVariableChanged(location, "isVisited");
        }else{
            TaskLocation loc = location;
            while(loc != null){
                scoreDirector.beforeVariableChanged(loc, "distanceToPrev");
                loc.setDistanceToPrev(loc.distanceTo(loc.getPrev()));
                scoreDirector.afterVariableChanged(loc, "distanceToPrev");

                scoreDirector.beforeVariableChanged(loc, "distanceSinceStart");
                loc.setDistanceSinceStart(loc.getPrev().getDistanceSinceStart() + loc.distanceTo(loc.getPrev()));
                scoreDirector.afterVariableChanged(loc, "distanceSinceStart");

                scoreDirector.beforeVariableChanged(loc, "timeSinceStart");
                loc.setTimeSinceStart(loc.getPrev().getTimeSinceStart() + loc.timeTo(loc.getPrev()) + loc.getPrev().getTimeToComplete());
                scoreDirector.afterVariableChanged(loc, "timeSinceStart");

                Boolean isVisited = loc.getPrev().getIsVisited();
                isVisited = isVisited || Objects.equals(loc.getName(), "End");
                isVisited = isVisited && !Objects.equals(loc.getPrev().getName(), "End");
                isVisited = isVisited || loc.getPrev() instanceof Start;
                scoreDirector.beforeVariableChanged(loc, "isVisited");
                loc.setIsVisited(isVisited);
                scoreDirector.afterVariableChanged(loc, "isVisited");

                loc = loc.getNext();
            }
        }
    }

    @Override
    public void beforeEntityAdded(ScoreDirector<NavigationSolution> scoreDirector, TaskLocation location) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector<NavigationSolution> scoreDirector, TaskLocation location) {

    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<NavigationSolution> scoreDirector, TaskLocation location) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector<NavigationSolution> scoreDirector, TaskLocation location) {

    }
}
