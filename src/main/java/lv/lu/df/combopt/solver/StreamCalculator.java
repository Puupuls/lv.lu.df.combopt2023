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
                missedPoints(constraintFactory),
        };
    }

    public Constraint missedPoints(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(TaskLocation.class)
                .filter(v -> !v.getIsVisited())
                .penalize(HardMediumSoftScore.ONE_MEDIUM, v -> v.getValue() + 1)
                .asConstraint("overspentTime");
    }
}
