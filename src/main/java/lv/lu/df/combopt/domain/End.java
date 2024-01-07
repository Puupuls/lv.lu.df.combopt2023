package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import ai.timefold.solver.core.api.domain.variable.PlanningVariableGraphType;
import ai.timefold.solver.core.api.domain.variable.ShadowVariable;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.lu.df.combopt.solver.PrevElemChangeListener;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor
@JsonIdentityInfo(scope = End.class,
        property = "name",
        generator = ObjectIdGenerators.PropertyGenerator.class)
public class End extends TaskLocation{

    @PlanningVariable(
            graphType = PlanningVariableGraphType.CHAINED
    )
    public Location getPrev() {
        List<List<String>> chains = new ArrayList<>();
        List<Location> locations = this.getNavigationSolution().getPointList();
        List<Location> starts = new ArrayList<Location>(locations);
        for (Location tl : locations) {
            if (tl instanceof TaskLocation && ((TaskLocation) tl).getPrev() != null) {
                starts.remove(((TaskLocation) tl).getPrev());
            }
        }
        for (Location start : starts) {
            List<String> chain = new ArrayList<>();
            Location current = start;
            while (current != null) {
                chain.add(current.getName());
                current = current.getNext(locations);
            }
            chains.add(chain);
        }

        // Get chain that ends in "Start"
        List<String> chain = null;
        for (List<String> c : chains) {
            if (c.get(c.size() - 1).equals("Start")) {
                chain = c;
                break;
            }
        }
        if (chain == null) {
            return null;
        }

        // Add this to end tof that chain by linking it to the last element
        for (Location loc : locations) {
            if (loc.getName().equals(chain.get(0))) {
                this.setPrev(loc);
            }
        }

        return this.getPrev();
    }

    @ShadowVariable(
            sourceVariableName = "prev",
            variableListenerClass = PrevElemChangeListener.class,
            sourceEntityClass = TaskLocation.class
    )
    @Override
    public Integer getDistanceSinceStart() {
        return this.getPrev().getDistanceSinceStart() + this.distanceTo(this.getPrev());
    }

    public End(Double lat, Double lon, Double alt) {
        super(lat, lon, alt);
    }
}
