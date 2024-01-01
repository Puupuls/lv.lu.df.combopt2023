package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import lv.lu.df.combopt.domain.End;
import lv.lu.df.combopt.domain.NavigationSolution;

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
                .forEach(End.class)
                .penalize(HardMediumSoftScore.ONE_HARD, v -> v.getPrev() != v.getNavigationSolution().getStart()? 1 : 0)
                .asConstraint("endAtEnd");
    }
//
//    public Constraint overspentTime(ConstraintFactory constraintFactory) {
//        return constraintFactory
//                .forEach(Player.class)
//                .filter(player -> player.getTotalTimeMinutes() > player.getProblem().getMaxDuration())
//                .penalize(HardMediumSoftScore.ONE_HARD, v -> 1)
//                .asConstraint("overspentTime");
//    }
}
