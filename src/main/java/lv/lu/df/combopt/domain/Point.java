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

@PlanningEntity
@Getter @Setter @NoArgsConstructor
@JsonIdentityInfo(scope = Point.class,
        property = "name",
        generator = ObjectIdGenerators.PropertyGenerator.class)
public class Point extends Location{
    private Integer timeToComplete = 0;
    private Integer value = 0;

    @PlanningVariable(
            graphType = PlanningVariableGraphType.CHAINED
    )
    private Location prev;

    public Point(Double lat, Double lon, Double alt) {
        super(lat, lon, alt);
    }
}
