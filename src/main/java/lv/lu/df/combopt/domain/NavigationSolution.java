package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.solution.*;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@PlanningSolution
@Getter @Setter @NoArgsConstructor
public class NavigationSolution {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavigationSolution.class);

    private static final Double UPPER_LEFT_COORD_LAT = 56.9947;
    private static final Double UPPER_LEFT_COORD_LON = 24.0309;
    private static final Double LOWER_RIGHT_COORD_LAT = 56.8884;
    private static final Double LOWER_RIGHT_COORD_LON = 24.2520;

    private String solutionId;

    @PlanningScore
    private HardSoftScore score;

    // Minutes
    private Integer maxDuration;

    @ProblemFactCollectionProperty
    @ValueRangeProvider
    @JsonIdentityReference(alwaysAsId = false)
    private List<Location> pointList = new ArrayList<>();

    @PlanningListVariable(valueRangeProviderRefs = {"pointList"})
    @JsonIdentityReference(alwaysAsId = false)
    private List<Location> visitedPoints = new ArrayList<>();

    private Start start;
    private Point end;

    private static int problemId = 0;
    private static Integer getProblemId() { problemId++; return problemId;}

    private Integer distanceCost = 1;
    private Integer altitudeCost = 5;
    private Double speed = 5d; // km/h

    private NavigationSolution problem;

    public Integer getMinutesToTravel(double distance) {
        return (int) Math.round(distance/1000 / speed * 60);
    }
    public Integer getTotalTimeMinutes(){
        return this.getMinutesToTravel(this.getTotalDistance());
    }

    public Double getTotalDistance() {
        Double dist = 0d;
        if(this.visitedPoints.isEmpty()) return dist;
        Location point = this.visitedPoints.get(0);
        for (int i = 1; i < this.visitedPoints.size(); i++) {
            dist += point.distanceTo(this.visitedPoints.get(i));
            point = this.visitedPoints.get(i);
        }
        return dist;
    }


    public double getTotalAltitudeChange(){
        double altChange = 0d;
        if(this.visitedPoints.isEmpty()) return altChange;
        Location point = this.visitedPoints.get(0);
        for (int i = 1; i < this.visitedPoints.size(); i++) {
            altChange += Math.abs(point.getAlt() - this.visitedPoints.get(i).getAlt());
            point = this.visitedPoints.get(i);
        }
        return altChange;
    }

    public static NavigationSolution generateData(int pointCount) {
        Random random = new Random();
        NavigationSolution problem = new NavigationSolution();
        problem.setSolutionId(NavigationSolution.getProblemId().toString());
        problem.maxDuration = pointCount * 10; // 10 minūtes uz punktu

        problem.setProblem(problem);
        problem.setDistanceCost(1);
        problem.setAltitudeCost(10);
        problem.setSpeed(5d);


        problem.start = new Start(
                random.nextDouble() * (UPPER_LEFT_COORD_LAT - LOWER_RIGHT_COORD_LAT) + UPPER_LEFT_COORD_LAT,
                random.nextDouble() * (UPPER_LEFT_COORD_LON - LOWER_RIGHT_COORD_LON) + UPPER_LEFT_COORD_LON,
                random.nextDouble() * 25
        );
        problem.start.setName("Start");
        problem.start.setTimeToComplete(0);
//        problem.getPointList().add(problem.start);


        problem.end = new Point(
                random.nextDouble() * (UPPER_LEFT_COORD_LAT - LOWER_RIGHT_COORD_LAT) + UPPER_LEFT_COORD_LAT,
                random.nextDouble() * (UPPER_LEFT_COORD_LON - LOWER_RIGHT_COORD_LON) + UPPER_LEFT_COORD_LON,
                random.nextDouble() * 25
        );
        problem.end.setName("End");
        problem.end.setTimeToComplete(0);
        problem.getPointList().add(problem.end);

        for (int i = 1; i <= pointCount; i++) {
            Point p = new Point(
                    random.nextDouble() * (UPPER_LEFT_COORD_LAT - LOWER_RIGHT_COORD_LAT) + UPPER_LEFT_COORD_LAT,
                    random.nextDouble() * (UPPER_LEFT_COORD_LON - LOWER_RIGHT_COORD_LON) + UPPER_LEFT_COORD_LON,
                    random.nextDouble() * 25
            );
            p.setName("Point " + i);
            p.setTimeToComplete(random.nextInt(10) + 1);
            p.setValue(random.nextInt(10) + 1);
            problem.getPointList().add(p);
        }

        return problem;
    }

    public void print(){
        LOGGER.info("Solution: " + this.solutionId);
        LOGGER.info("Score: " + this.score);
        LOGGER.info("Max duration: " + this.maxDuration);
        LOGGER.info("Spent time: " + this.getTotalTimeMinutes());
        LOGGER.info("Total distance: " + this.getTotalDistance());
        LOGGER.info("Total altitude change: " + this.getTotalAltitudeChange());

        LOGGER.info("Route: ");
        for(Location p : this.visitedPoints){
            LOGGER.info("\t" + p.getName());
        }
    }
}
