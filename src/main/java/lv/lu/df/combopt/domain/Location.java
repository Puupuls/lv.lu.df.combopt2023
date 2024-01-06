package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.variable.ShadowVariable;
import ai.timefold.solver.core.impl.domain.variable.nextprev.PreviousElementVariableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.lu.df.combopt.solver.PrevElemChangeListener;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Location {
    private Double lat;
    private Double lon;
    private Double alt;

    private Integer value = 0;
    private Integer timeToComplete = 0;

    private String name;
    private Boolean isVisited;
    private NavigationSolution navigationSolution;

    public Location(Double lat, Double lon, Double alt) {
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
    }

    @JsonIgnore
    private Map<Location, Double> distanceMap = new HashMap<>();
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

    public Double distanceTo(Location location) {
        Double distance = this.distanceMap.get(location);
        if (distance == null) {
            distance = this.simpleDistanceTo(location);
            this.distanceMap.put(location, distance);
        }
        return distance;
    }

    public Double simpleDistanceTo(Location location) {
        return distance(
                this.getLat(),
                location.getLat(),
                this.getLon(),
                location.getLon(),
                this.getAlt(),
                location.getAlt()
        );
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
    private static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public String toString() {
        return this.getName() + " (" + this.getIsVisited() + ")";
    }
}
