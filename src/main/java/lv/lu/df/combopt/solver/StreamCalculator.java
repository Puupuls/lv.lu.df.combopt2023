package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import lv.lu.df.combopt.domain.End;
import lv.lu.df.combopt.domain.NavigationSolution;
import lv.lu.df.combopt.domain.Start;
import lv.lu.df.combopt.domain.TaskLocation;

import static ai.timefold.solver.core.api.score.stream.Joiners.equal;

public class StreamCalculator implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                endAtEnd(constraintFactory),
//                overspentTime(constraintFactory),
        };
    }

    public Constraint endAtEnd(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Start.class)
                .penalize(HardMediumSoftScore.ONE_HARD, v -> (
                        v.getNext() != null &&
                        v.getNext().getNext() != null &&
                        v.getNext().getNext() == v.getNavigationSolution().getEnd()
                )? 0 : 1)
                .asConstraint("endAtEnd");
    }

    public Constraint missedPoints(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(TaskLocation.class)
                .filter(l -> l.getPrev() == null)
                .penalize(HardMediumSoftScore.ONE_HARD, TaskLocation::getValue)
                .asConstraint("overspentTime");
    }
}
