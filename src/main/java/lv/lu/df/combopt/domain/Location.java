package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.InverseRelationShadowVariable;
import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lv.lu.df.combopt.solver.LocationDifficultyComparator;
import lv.lu.df.combopt.solver.LocationStrengthComparator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@JsonIdentityInfo(scope = Location.class,
        property = "name",
        generator = ObjectIdGenerators.PropertyGenerator.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@PlanningEntity(
        difficultyComparatorClass = LocationDifficultyComparator.class
)
public class Location {
    @InverseRelationShadowVariable(sourceVariableName = "prev")
    @JsonIdentityReference(alwaysAsId = true)
    TaskLocation next;

    private Double lat;
    private Double lon;
    private Double alt;

    private Integer value = 0;
    private Boolean isVisited = false;
    private Integer timeToComplete = 0;
    private Integer distanceSinceStart = 0;
    private Integer timeSinceStart = 0;

    private String name;

    @JsonIdentityReference(alwaysAsId = true)
    private NavigationSolution navigationSolution;

    public Location(Double lat, Double lon, Double alt) {
        Double[] ll = Router.getDefaultRouterInstance().getClosestPointOnGraph(new Double[]{lat, lon});
        this.lat = ll[0];
        this.lon = ll[1];
        this.alt = alt;
    }

    @JsonIgnore
    private Map<String, Integer> distanceMap = new HashMap<>();
    @JsonIgnore
    private Map<String, Integer> timeMap = new HashMap<>();
    private Map<String, List<List<Double>>> pathMap = new HashMap<>();

    public Integer timeTo(Location location) {
        Integer time = this.timeMap.get(location.name);
        if (time == null) {
            time = (int) (this.distanceTo(location) / 1000 / NavigationSolution.SPEED * 3600);
            this.timeMap.put(location.name, time);
        }
        return time;
    }

    public Integer distanceTo(Location location) {
        Integer distance = this.distanceMap.get(location.name);
        if (distance == null) {
//            distance = this.simpleDistanceTo(location);
            distance = Router.getDefaultRouterInstance().getDistance(this, location);
            this.distanceMap.put(location.name, distance);
        }
        return distance;
    }
    private Double difficulty = null;
    public Double getDifficulty(){
        if (this.difficulty != null)
            return this.difficulty;
        // Average distance to 10 closest points from distanceMap
        AtomicReference<Double> avgDistance = new AtomicReference<>(0d);
        HashMap<String, Integer> sortedDistanceMap = new HashMap<>(this.distanceMap);
        sortedDistanceMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(10)
                .forEachOrdered(x -> avgDistance.updateAndGet(v -> v + x.getValue()));
        avgDistance.updateAndGet(v -> v / 10);

        Double difficulty = avgDistance.get();

        // Average distance from all points
        AtomicReference<Double> avgDistanceFromAll = new AtomicReference<>(0d);
        this.distanceMap.entrySet().stream()
                .forEachOrdered(x -> avgDistanceFromAll.updateAndGet(v -> v + x.getValue()));
        avgDistanceFromAll.updateAndGet(v -> v / this.distanceMap.size());


        return difficulty;
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
        double distance = R * c * 1000; // convert mto meters

        double height = this.alt - location.alt;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return (int) Math.round(Math.sqrt(distance));
    }

    public String toString() {
        return this.getName();
    }
}
