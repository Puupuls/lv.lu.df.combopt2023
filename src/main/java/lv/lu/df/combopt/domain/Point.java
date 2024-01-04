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

import java.util.HashMap;
import java.util.Map;

@PlanningEntity
@Getter @Setter @NoArgsConstructor
@JsonIdentityInfo(scope = Point.class,
        property = "name",
        generator = ObjectIdGenerators.PropertyGenerator.class)
public class Point {
    private String name;
    private Integer timeToComplete = 0;
    private Integer value = 0;

    private Double lat;
    private Double lon;
    private Double alt;
    @JsonIgnore
    private Map<Point, Double> distanceMap = new HashMap<>();
    @JsonIgnore
    private Map<Point, Integer> timeMap = new HashMap<>();

    @NextElementShadowVariable(sourceVariableName = "points")
    @JsonIdentityReference(alwaysAsId = true)
    private Point next;

    @PreviousElementShadowVariable(sourceVariableName = "points")
    @JsonIdentityReference(alwaysAsId = true)
    private Point prev;

    @PlanningVariable(valueRangeProviderRefs = {"booleanRange"})
    private Boolean isVisited = false;

    @JsonIdentityReference(alwaysAsId = true)
    public Point getPreviousVisited() {
        Point point = this;
        if(point.getPrev() == null) return null;
        if(point.getPrev().getIsVisited()) return point.getPrev();
        return null;
    }
    @JsonIdentityReference(alwaysAsId = true)
    public Point getNextVisited() {
        Point point = this;
        if(point.getNext() == null) return null;
        if(point.getNext().getIsVisited()) return point.getNext();
        return null;
    }

    public double getDistanceToNextVisited() {
        Point next = this.getNextVisited();
        if (next == null) {
            return 0d;
        }
        return this.distanceTo(next);
    }

    public double distanceTo(Point p2) {
        Double distance = this.distanceMap.get(p2);
        if (distance == null) {
            distance = this.simpleDistanceTo(p2);
            this.distanceMap.put(p2, distance);
        }
        return distance;
    }

    private double simpleDistanceTo(Point p2) {
        return distanceBetweenCoords(
                this.getLat(),
                p2.getLat(),
                this.getLon(),
                p2.getLon(),
                this.getAlt(),
                p2.getAlt()
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
    private static double distanceBetweenCoords(double lat1, double lat2, double lon1, double lon2, double alt1, double alt2) {
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = alt1 - alt2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    @InverseRelationShadowVariable(sourceVariableName = "points")
    @JsonIdentityReference(alwaysAsId = true)
    private Player player;

    @Override
    public String toString() {
        return "Point{" +
                "name='" + name + '\'' +
                ", timeToComplete=" + timeToComplete +
                ", value=" + value +
                ", lat=" + lat +
                ", lon=" + lon +
                ", alt=" + alt +
                ", isVisited=" + isVisited +
                '}';
    }
}
