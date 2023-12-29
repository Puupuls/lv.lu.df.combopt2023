package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.InverseRelationShadowVariable;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@PlanningEntity
@Getter @Setter @NoArgsConstructor
@JsonIdentityInfo(scope = Player.class,
        property = "id",
        generator = ObjectIdGenerators.PropertyGenerator.class)
public class Player {
    private String id;

    @PlanningListVariable()
    private List<Point> points = new ArrayList<>();

    private Integer distanceCost = 1;
    private Integer altitudeCost = 5;
    private Double speed = 5d; // km/h

    private NavigationSolution problem;

    public Integer getMinutesToTravel(double distance) {
        return (int) Math.round(distance/1000 / speed * 60);
    }

    public double getTotalDistance() {
        double dist = 0d;
        if(this.points.isEmpty()) return dist;
        Point point = this.getFirstVisitedPoint();
        while(point != null) {
            if(point.getNextVisited() == null) break;
            dist += point.getLocation().distanceTo(point.getNextVisited().getLocation());
            point = point.getNextVisited();
        }
        return dist;
    }

    public Integer getTotalTimeMinutes(){
        Integer totalTaskDuration = this.points.stream().filter(Point::getIsVisited).mapToInt(Point::getTimeToComplete).sum();
        return this.getMinutesToTravel(this.getTotalDistance()) + totalTaskDuration;
    }

    public double getTotalAltitudeChange(){
        double altChange = 0d;
        if(this.points.isEmpty()) return altChange;
        Point point = this.getFirstVisitedPoint();
        while(point != null) {
            if(point.getNextVisited() == null) break;
            altChange += Math.abs(point.getLocation().getAlt() - point.getNextVisited().getLocation().getAlt());
            point = point.getNextVisited();
        }
        return altChange;
    }

    public Point getLastVisitedPoint() {
        if(this.points.isEmpty()) return null;
        Point point = this.points.get(this.points.size() - 1);
        while(point != null && !point.getIsVisited()) {
            point = point.getPrev();
        }
        return point;
    }

    public Point getFirstVisitedPoint() {
        if(this.points.isEmpty()) return null;
        Point point = this.points.get(0);
        while(point != null && !point.getIsVisited()) {
            point = point.getNext();
        }
        return point;
    }

    @Override
    public String toString() {
        return "Player({"+this.id+"})";
    }
}
