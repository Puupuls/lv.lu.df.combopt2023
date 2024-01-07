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
        List<Location> chain = new ArrayList<>();
        chain.add(location);

        Location l = location;
        while (l instanceof TaskLocation tl) {
            l = tl.getPrev();
            if(l != null) {
                chain.add(l);
            }
        }
        Collections.reverse(chain);

        boolean sawStart = false;
        boolean sawEnd = false;

//        System.out.println("==================================");
        for (Location loc : chain) {
            if(Objects.equals(loc.getName(), "Start")){
                sawStart = true;
            }
            if(Objects.equals(loc.getName(), "End")){
                sawEnd = true;
            }
            if (loc instanceof TaskLocation tl) {
                scoreDirector.beforeVariableChanged(tl, "isVisited");
                if(sawStart){
                    if(sawEnd && !Objects.equals(loc.getName(), "End")){
                        tl.setIsVisited(false);
                    } else {
                        tl.setIsVisited(true);
                    }
                } else {
                    tl.setIsVisited(false);
                }
                scoreDirector.afterVariableChanged(tl, "isVisited");
                scoreDirector.beforeVariableChanged(loc, "distanceSinceStart");
                if (tl.getPrev() != null) {
                    tl.setDistanceSinceStart(tl.getPrev().getDistanceSinceStart() + tl.distanceTo(tl.getPrev()));
                } else {
                    tl.setDistanceSinceStart(0);
                }
                scoreDirector.afterVariableChanged(loc, "distanceSinceStart");
            }
//            System.out.println(location);
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
