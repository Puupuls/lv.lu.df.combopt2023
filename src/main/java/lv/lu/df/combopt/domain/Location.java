package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.variable.ShadowVariable;
import ai.timefold.solver.core.impl.domain.variable.nextprev.PreviousElementVariableListener;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.lu.df.combopt.solver.PrevElemChangeListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@JsonIdentityInfo(scope = Location.class,
        property = "name",
        generator = ObjectIdGenerators.PropertyGenerator.class)
public class Location {
    private Double lat;
    private Double lon;
    private Double alt;

    private Integer value = 0;
    private Integer timeToComplete = 0;
    private Integer distanceSinceStart = 0;

    private String name;

    @JsonIgnore
    private NavigationSolution navigationSolution;

    public Location(Double lat, Double lon, Double alt) {
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
    }

    public TaskLocation getNext(List<Location> locations) {
        for (Location l : locations) {
            if(l instanceof TaskLocation tl && tl.getPrev() == this) {
                return tl;
            }
        }
        return null;
    }

    @JsonIgnore
    private Map<Location, Integer> distanceMap = new HashMap<>();
    @JsonIgnore
    private Map<Location, Integer> timeMap = new HashMap<>();

    public Integer timeTo(Location location) {
        Integer time = this.timeMap.get(location);
        if (time == null) {
            time = (int) (this.distanceTo(location) / 1000 / this.navigationSolution.getSpeed() * 3600);
            this.timeMap.put(location, time);
        }
        return time;
    }

    public Integer distanceTo(Location location) {
        Integer distance = this.distanceMap.get(location);
        if (distance == null) {
            distance = this.simpleDistanceTo(location);
            this.distanceMap.put(location, distance);
        }
        return distance;
    }

    /**
     * https://stackoverflow.com/a/16794680
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public Integer simpleDistanceTo(Location location) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(location.lat - this.lat);
        double lonDistance = Math.toRadians(location.lon - this.lon);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(this.lat)) * Math.cos(Math.toRadians(location.lat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = this.alt - location.alt;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return (int) Math.round(Math.sqrt(distance));
    }

    public String toString() {
        return this.getName();
    }
}
