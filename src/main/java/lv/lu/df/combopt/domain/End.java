package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import ai.timefold.solver.core.api.domain.variable.PlanningVariableGraphType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@JsonIdentityInfo(scope = End.class,
        property = "name",
        generator = ObjectIdGenerators.PropertyGenerator.class)
public class End extends TaskLocation{
    private Integer timeToComplete = 0;
    private Integer value = 0;
    private Location location;

    public End(Double lat, Double lon, Double alt) {
        super(lat, lon, alt);
    }
}
