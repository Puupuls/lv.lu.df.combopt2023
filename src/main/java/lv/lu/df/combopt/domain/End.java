package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import ai.timefold.solver.core.api.domain.variable.PlanningVariableGraphType;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor
@JsonIdentityInfo(scope = End.class,
        property = "name",
        generator = ObjectIdGenerators.PropertyGenerator.class)
public class End extends TaskLocation{

    public Location getPrev() {
        List<String> childLocations = new ArrayList<>();
        for(Location tl : this.getNavigationSolution().getPointList()) {
            TaskLocation tl1 = (TaskLocation) tl;
            if (tl1.getPrev() != null) {
                childLocations.add(tl1.getPrev().getName());
            }
        }
        TaskLocation tail = null;
        for(Location tl : this.getNavigationSolution().getPointList()) {
            if(!childLocations.contains(tl.getName())) {
                tail = (TaskLocation) tl;
                break;
            }
        }
        return tail;
    }

    public End(Double lat, Double lon, Double alt) {
        super(lat, lon, alt);
    }
}
