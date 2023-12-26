package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.score.stream.Joiners;
import lv.lu.df.combopt.domain.Player;
import lv.lu.df.combopt.domain.Point;

import static ai.timefold.solver.core.api.score.stream.Joiners.equal;

public class StreamCalculator implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                startAtStart(constraintFactory),
                endAtEnd(constraintFactory),
                overspentTime(constraintFactory),
                altitudeChange(constraintFactory),
        };
    }

    public Constraint startAtStart(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Player.class)
                .filter(player -> player.getPoints().isEmpty() || player.getPoints().get(0) != player.getProblem().getStart())
                .penalize(HardSoftScore.ONE_HARD, v -> 1)
                .asConstraint("startAtStart");
    }

    public Constraint endAtEnd(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Player.class)
                .filter(player -> player.getPoints().isEmpty() || player.getPoints().get(player.getPoints().size() - 1) != player.getProblem().getEnd())
                .penalize(HardSoftScore.ONE_HARD, v -> 1)
                .asConstraint("endAtEnd");
    }

    public Constraint overspentTime(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Player.class)
                .filter(player -> player.getTotalTimeMinutes() > player.getProblem().getMaxDuration())
                .penalize(HardSoftScore.ONE_HARD, v -> 1)
                .asConstraint("overspentTime");
    }

    public Constraint altitudeChange(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Player.class)
                .penalize(HardSoftScore.ONE_SOFT, v -> (int) v.getTotalAltitudeChange())
                .asConstraint("altitudeChange");
    }
}
