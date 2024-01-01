package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import ai.timefold.solver.core.api.score.constraint.ConstraintMatch;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SimpleConstraintMatch {
    private String constraintName;
    private HardMediumSoftScore score;

    public SimpleConstraintMatch(ConstraintMatch<HardMediumSoftScore> constraintMatch) {
        this.constraintName = constraintMatch.getConstraintRef().constraintName();
        this.score = constraintMatch.getScore();
    }
}
