package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.domain.variable.*;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.lu.df.combopt.solver.LocationDifficultyComparator;
import lv.lu.df.combopt.solver.LocationStrengthComparator;
import lv.lu.df.combopt.solver.PrevElemChangeListener;

@Getter @Setter @NoArgsConstructor
@JsonIdentityInfo(scope = TaskLocation.class,
        property = "name",
        generator = ObjectIdGenerators.PropertyGenerator.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@PlanningEntity(difficultyComparatorClass = LocationDifficultyComparator.class)
public class TaskLocation extends Location{

    @PlanningVariable(
            graphType = PlanningVariableGraphType.CHAINED,
            valueRangeProviderRefs = {"startList", "pointList"},
            strengthComparatorClass = LocationStrengthComparator.class
    )
    @JsonIdentityReference(alwaysAsId = true)
    private Location prev=null;

    @AnchorShadowVariable(sourceVariableName = "prev")
    @JsonIdentityReference(alwaysAsId = true)
    private Start anchor;

    @ShadowVariable(
            sourceVariableName = "prev",
            variableListenerClass = PrevElemChangeListener.class
    )
    private Integer distanceSinceStart = 0;

    @PiggybackShadowVariable(shadowVariableName = "distanceSinceStart")
    private Integer timeSinceStart = 0;

    @PiggybackShadowVariable(shadowVariableName = "distanceSinceStart")
    private Integer distanceToPrev = 0;

    @PiggybackShadowVariable(shadowVariableName = "distanceSinceStart")
    private Boolean isVisited = false;

    @ValueRangeProvider(id = "booleanRange")
    @JsonIgnore
    public Boolean[] getBooleanRange() {
        return new Boolean[]{true, false};
    }

    public TaskLocation(Double lat, Double lon, Double alt) {
        super(lat, lon, alt);
    }
}
