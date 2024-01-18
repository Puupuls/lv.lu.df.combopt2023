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
                distanceToPrev(constraintFactory),
                taskCompletionTime(constraintFactory)
        };
    }

    public Constraint overspentTime(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(TaskLocation.class)
                .filter(v -> v.getName().equals("End") && v.getIsVisited())
                .penalize(HardMediumSoftScore.ONE_HARD, v -> Math.max(v.getTimeSinceStart() + v.getTimeToComplete() - v.getNavigationSolution().getMaxDuration(), 0))
                .asConstraint("overspentTime");
    }
    public Constraint missedPoints(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(TaskLocation.class)
                .filter(v -> !v.getIsVisited())
                .penalize(HardMediumSoftScore.ONE_MEDIUM, Location::getValue)
                .asConstraint("missedPoints");
    }

    public Constraint distanceToPrev(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(TaskLocation.class)
                .filter(TaskLocation::getIsVisited)
                .penalize(HardMediumSoftScore.ONE_SOFT, TaskLocation::getDistanceToPrev)
                .asConstraint("distanceToPrev");
    }

    public Constraint taskCompletionTime(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(TaskLocation.class)
                .filter(TaskLocation::getIsVisited)
                .penalize(HardMediumSoftScore.ONE_SOFT, Location::getTimeToComplete)
                .asConstraint("taskCompletionTime");
    }

}
