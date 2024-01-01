package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import ai.timefold.solver.core.api.score.constraint.ConstraintMatch;
import lombok.Getter;
import lombok.Setter;
import lv.lu.df.combopt.domain.TaskLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Setter @Getter
public class SimpleIndictmentObject {
    private String indictedObjectID;
    private HardMediumSoftScore score;
    private int matchCount;
    private List<SimpleConstraintMatch> constraintMatches = new ArrayList<>();

    public SimpleIndictmentObject(Object indictedObject, HardMediumSoftScore score, int matchCount, Set<ConstraintMatch<HardMediumSoftScore>> constraintMatches) {
        this.indictedObjectID = ((TaskLocation) indictedObject).getName();
        this.score = score;
        this.matchCount = matchCount;
        this.constraintMatches = constraintMatches.stream().map(constraintMatch -> {
            return new SimpleConstraintMatch(constraintMatch);
        }).collect(Collectors.toList());
    }
}
