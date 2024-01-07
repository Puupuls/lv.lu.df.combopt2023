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
            chain.add(l);
        }
        Collections.reverse(chain);
//        System.out.println("==================================");
        for (Location loc : chain) {
            if (loc instanceof TaskLocation tl) {
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
