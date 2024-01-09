package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.solution.*;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@PlanningSolution
@Getter @Setter @NoArgsConstructor
public class NavigationSolution {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavigationSolution.class);

    private static final Double UPPER_LEFT_COORD_LAT = 56.95;
    private static final Double UPPER_LEFT_COORD_LON = 24.10;
    private static final Double LOWER_RIGHT_COORD_LAT = 56.90;
    private static final Double LOWER_RIGHT_COORD_LON = 24.20;

    private String solutionId;

    private LocalDateTime created;
    private LocalDateTime lastSolutionTime;

    @PlanningScore
    private HardMediumSoftScore score;

    // Minutes
    private Integer maxDuration;

    @ValueRangeProvider
    @PlanningEntityCollectionProperty
    private List<Location> pointList = new ArrayList<>();

    @ProblemFactProperty
    private Start start;

    @ValueRangeProvider
    @JsonIgnore
    public List<Location> getStartList() {
        return Collections.singletonList(start);
    }

//    @ProblemFactProperty
    private TaskLocation end;

    private static int problemId = 0;
    private static Integer getProblemId() { problemId++; return problemId;}

    private Integer distanceCost = 1;
    private Integer altitudeCost = 5;
    private Double speed = 5d; // km/h

    public Integer getMinutesToTravel(double distance) {
        return (int) Math.round(distance/1000 / speed * 60);
    }
    public Integer getTotalTimeMinutes(){
        return this.getMinutesToTravel(this.getTotalDistance());
    }

    public Double getTotalDistance() {
        Double dist = 0d;
        Location p = this.end;
        while(p != null){
            if(p instanceof TaskLocation tl) {
                if (tl.getPrev() == null) break;
                dist += p.distanceTo(tl.getPrev());
                p = tl.getPrev();
            } else {
                break;
            }
        }
        return dist;
    }

    public static NavigationSolution generateData(int pointCount) {
        Random random = new Random(19026);
        NavigationSolution problem = new NavigationSolution();
        problem.setCreated(LocalDateTime.now());
        problem.setSolutionId(NavigationSolution.getProblemId().toString());
        problem.maxDuration = pointCount * 10; // 10 minūtes uz punktu

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
        problem.start.setNavigationSolution(problem);


        problem.end = new TaskLocation(
                random.nextDouble() * (UPPER_LEFT_COORD_LAT - LOWER_RIGHT_COORD_LAT) + UPPER_LEFT_COORD_LAT,
                random.nextDouble() * (UPPER_LEFT_COORD_LON - LOWER_RIGHT_COORD_LON) + UPPER_LEFT_COORD_LON,
                random.nextDouble() * 25
        );
        problem.end.setName("End");
        problem.end.setTimeToComplete(0);
        problem.end.setNavigationSolution(problem);
        problem.getPointList().add(problem.end);

        for (int i = 1; i <= pointCount; i++) {
            TaskLocation p = new TaskLocation(
                    random.nextDouble() * (UPPER_LEFT_COORD_LAT - LOWER_RIGHT_COORD_LAT) + UPPER_LEFT_COORD_LAT,
                    random.nextDouble() * (UPPER_LEFT_COORD_LON - LOWER_RIGHT_COORD_LON) + UPPER_LEFT_COORD_LON,
                    random.nextDouble() * 25
            );
            p.setName("Point " + i);
            p.setTimeToComplete(random.nextInt(10) + 1);
            p.setValue(random.nextInt(10));
            p.setNavigationSolution(problem);
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

        LOGGER.info("Route: ");
        Location p = this.end;
        while(p != null){
            LOGGER.info(p.toString());
            if(p instanceof TaskLocation tl) {
                p = tl.getPrev();
            } else {
                break;
            }
        }
    }
}
