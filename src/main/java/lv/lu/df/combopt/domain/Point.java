package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.lu.df.combopt.solver.PreviousPointListener;

@PlanningEntity
@Getter @Setter @NoArgsConstructor
@JsonIdentityInfo(scope = Point.class,
        property = "name",
        generator = ObjectIdGenerators.PropertyGenerator.class)
public class Point {
    private String name;
    private Integer timeToComplete = 0;
    private Integer value = 0;
    private Location location;

    @PlanningVariable
    private Boolean isVisited = false;

    @NextElementShadowVariable(sourceVariableName = "points")
    @JsonIdentityReference(alwaysAsId = true)
    private Point next;

    @PreviousElementShadowVariable(sourceVariableName = "points")
    @JsonIdentityReference(alwaysAsId = true)
    private Point prev;

    @ShadowVariable(variableListenerClass = PreviousPointListener.class, sourceVariableName = "prev")
    private Double arrivalTime = null;

    @InverseRelationShadowVariable(sourceVariableName = "points")
    @JsonIdentityReference(alwaysAsId = true)
    private Player player;

    @JsonIgnore
    public Double getDepartureTime() {
        return this.getArrivalTime() != null ?
                this.getArrivalTime() + this.getTimeToComplete() :
                null;
    }
}
