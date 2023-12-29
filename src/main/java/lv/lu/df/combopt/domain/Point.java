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

    @NextElementShadowVariable(sourceVariableName = "points")
    @JsonIdentityReference(alwaysAsId = true)
    private Point next;

    @PreviousElementShadowVariable(sourceVariableName = "points")
    @JsonIdentityReference(alwaysAsId = true)
    private Point prev;

    @ShadowVariable(variableListenerClass = PreviousPointListener.class, sourceVariableName = "prev")
    private Boolean isVisited = false;

    public Point getPreviousVisited() {
        Point point = this;
        while (point.getPrev() != null && point.getPrev().getIsVisited()) {
            point = point.getPrev();
        }
        if (point == this) {
            return null;
        }
        return point;
    }
    public Point getNextVisited() {
        Point point = this;
        while (point.getNext() != null && point.getNext().getIsVisited()) {
            point = point.getNext();
        }
        if (point == this) {
            return null;
        }
        return point;
    }

    @InverseRelationShadowVariable(sourceVariableName = "points")
    @JsonIdentityReference(alwaysAsId = true)
    private Player player;

    @Override
    public String toString() {
        return "Point({"+this.name+"})";
    }
}
