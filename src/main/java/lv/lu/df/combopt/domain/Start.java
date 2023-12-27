package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.solution.PlanningEntityProperty;
import ai.timefold.solver.core.api.domain.variable.InverseRelationShadowVariable;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import ai.timefold.solver.core.api.domain.variable.PlanningVariableGraphType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@PlanningAnchor
@Getter @Setter @NoArgsConstructor
@JsonIdentityInfo(scope = Start.class,
        property = "name",
        generator = ObjectIdGenerators.PropertyGenerator.class)
public class Start extends Location{
    private Integer timeToComplete = 0;
    private Integer value = 0;
    private Location location;


    public Start(Double lat, Double lon, Double alt) {
        super(lat, lon, alt);
    }
}
