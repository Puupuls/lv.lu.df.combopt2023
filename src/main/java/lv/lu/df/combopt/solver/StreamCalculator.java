package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import lv.lu.df.combopt.domain.*;

public class StreamCalculator implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                overspentTime(constraintFactory),
                missedPoints(constraintFactory),
                shortestPath(constraintFactory)
        };
    }

    public Constraint overspentTime(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(TaskLocation.class)
                .filter(v -> v.getName().equals("End"))
                .penalize(HardMediumSoftScore.ONE_HARD, v -> Math.max((int)Math.round(v.getDistanceSinceStart()/1000.0/5*60 - v.getNavigationSolution().getMaxDuration()), 0))
                .asConstraint("overspentTime");
    }

    public Constraint missedPoints(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(TaskLocation.class)
                .filter(v -> !v.getIsVisited())
                .penalize(HardMediumSoftScore.ONE_MEDIUM, v -> v.getValue() + 1)
                .asConstraint("missedPoints");
    }

    public Constraint shortestPath(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(TaskLocation.class)
                .filter(TaskLocation::getIsVisited)
                .penalize(HardMediumSoftScore.ONE_SOFT, v -> v.getDistanceSinceStart())
                .asConstraint("shortestPath");
    }

}
