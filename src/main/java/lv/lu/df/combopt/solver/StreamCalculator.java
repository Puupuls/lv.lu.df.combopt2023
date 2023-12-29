package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
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
                overspentTime(constraintFactory),
                notVisitedPoints(constraintFactory),
                altitudeChange(constraintFactory),
                distanceChange(constraintFactory)
        };
    }

    public Constraint startAtStart(ConstraintFactory constraintFactory) {
        // Tīri organizatorisks constraint, kas novieto sākuma punktu masīva pirmajā pozīcijā
        return constraintFactory
                .forEach(Player.class)
                .filter(player ->
                        player.getPoints().isEmpty() ||
                                player.getPoints().get(0) != player.getProblem().getStart() ||
                                !player.getPoints().get(0).getIsVisited()
                )
                .penalize(HardMediumSoftScore.ONE_HARD, player -> player.getPoints().contains(player.getProblem().getStart())? player.getPoints().indexOf(player.getProblem().getStart()) : 1)
                .asConstraint("startAtStart");
    }

    public Constraint overspentTime(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Player.class)
                .filter(player -> player.getTotalTimeMinutes() > player.getProblem().getMaxDuration())
                .penalize(HardMediumSoftScore.ONE_HARD, v -> v.getTotalTimeMinutes() - v.getProblem().getMaxDuration())
                .asConstraint("overspentTime");
    }

    public Constraint notVisitedPoints(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Point.class)
                .filter(point -> !point.getIsVisited())
                .penalize(HardMediumSoftScore.ONE_MEDIUM)
                .asConstraint("notVisitedPoints");
    }

    public Constraint altitudeChange(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Player.class)
                .penalize(HardMediumSoftScore.ONE_SOFT, p -> (int) p.getTotalAltitudeChange() * p.getAltitudeCost())
                .asConstraint("altitudeChange");
    }

    public Constraint distanceChange(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Player.class)
                .penalize(HardMediumSoftScore.ONE_SOFT, p -> (int) Math.round(p.getTotalDistance() * p.getDistanceCost()))
                .asConstraint("totalDistance");
    }
}
