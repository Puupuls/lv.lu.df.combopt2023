package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.domain.variable.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@PlanningEntity
@Getter @Setter @NoArgsConstructor
@JsonIdentityInfo(scope = TaskLocation.class,
        property = "name",
        generator = ObjectIdGenerators.PropertyGenerator.class)
public class TaskLocation extends Location{
    private Integer timeToComplete = 0;
    private Integer value = 0;

    @PlanningVariable(
            graphType = PlanningVariableGraphType.CHAINED
    )
    private Location prev=null;

    @PlanningVariable(
            valueRangeProviderRefs = {"booleanRange"}
    )
    private Boolean isVisited = false;

    public void setIsVisited(Boolean isVisited) {
        this.isVisited = isVisited;
        System.out.println("setIsVisited: " + isVisited);
    }
    public void setPrev(Location prev) {
        this.prev = prev;
        System.out.println("setPrev: " + prev);
    }

    @ValueRangeProvider(id = "booleanRange")
    public Boolean[] getBooleanRange() {
        return new Boolean[]{true, false};
    }

    public TaskLocation(Double lat, Double lon, Double alt) {
        super(lat, lon, alt);
    }
}
