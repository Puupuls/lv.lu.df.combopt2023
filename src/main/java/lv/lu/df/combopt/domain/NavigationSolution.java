package lv.lu.df.combopt.domain;

import ai.timefold.solver.core.api.domain.solution.*;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
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
    private HardMediumSoftScore score;

    @PlanningEntityProperty
    private Player player;

    // Minutes
    private Integer maxDuration;

    @ProblemFactCollectionProperty
    @ValueRangeProvider
    @JsonIdentityReference(alwaysAsId = false)
    private List<Point> pointList = new ArrayList<>();


    @ProblemFactCollectionProperty
    private List<Location> locationList = new ArrayList<>();

    private Point start;
    private Point end;

    private static int problemId = 0;
    private static Integer getProblemId() { problemId++; return problemId;}

    public static NavigationSolution generateData(Integer pointCount) {
        Random random = new Random();
        NavigationSolution problem = new NavigationSolution();
        problem.setSolutionId(NavigationSolution.getProblemId().toString());
        problem.maxDuration = pointCount * 5; // 5 minūtes uz punktu

        problem.player = new Player();
        problem.player.setId("Player 1");
        problem.player.setProblem(problem);
        problem.player.setDistanceCost(1);
        problem.player.setAltitudeCost(10);
        problem.player.setSpeed(5d);


        problem.start = new Point();
        problem.start.setName("Start");
        problem.start.setTimeToComplete(0);
        problem.start.setLocation(
                new Location(
                        random.nextDouble() * (UPPER_LEFT_COORD_LAT - LOWER_RIGHT_COORD_LAT) + UPPER_LEFT_COORD_LAT,
                        random.nextDouble() * (UPPER_LEFT_COORD_LON - LOWER_RIGHT_COORD_LON) + UPPER_LEFT_COORD_LON,
                        random.nextDouble() * 25
                )
        );
        problem.getPointList().add(problem.start);
        problem.getLocationList().add(problem.start.getLocation());


        problem.end = new Point();
        problem.end.setName("End");
        problem.end.setTimeToComplete(0);
        problem.end.setLocation(
                new Location(
                        random.nextDouble() * (UPPER_LEFT_COORD_LAT - LOWER_RIGHT_COORD_LAT) + UPPER_LEFT_COORD_LAT,
                        random.nextDouble() * (UPPER_LEFT_COORD_LON - LOWER_RIGHT_COORD_LON) + UPPER_LEFT_COORD_LON,
                        random.nextDouble() * 25
                )
        );
        problem.getPointList().add(problem.end);
        problem.getLocationList().add(problem.end.getLocation());

        for (int i = 1; i <= pointCount; i++) {
            Point p = new Point();
            p.setName("Point " + i);
            p.setLocation(
                    new Location(
                            random.nextDouble() * (UPPER_LEFT_COORD_LAT - LOWER_RIGHT_COORD_LAT) + UPPER_LEFT_COORD_LAT,
                            random.nextDouble() * (UPPER_LEFT_COORD_LON - LOWER_RIGHT_COORD_LON) + UPPER_LEFT_COORD_LON,
                            random.nextDouble() * 25
                    )
            );
            p.setTimeToComplete(random.nextInt(10) + 1);
            p.setValue(random.nextInt(10) + 1);
            problem.getPointList().add(p);
            problem.getLocationList().add(p.getLocation());

        }

        return problem;
    }

    public void print(){
        LOGGER.info("Solution: " + this.solutionId);
        LOGGER.info("Score: " + this.score);
        LOGGER.info("Player: " + this.player.getId());
        LOGGER.info("Max duration: " + this.maxDuration);
        LOGGER.info("Spent time: " + this.player.getTotalTimeMinutes());
        LOGGER.info("Total distance: " + this.player.getTotalDistance());
        LOGGER.info("Total altitude change: " + this.player.getTotalAltitudeChange());

        LOGGER.info("Route: ");
        for(Point p : this.player.getPoints()){
            LOGGER.info("\t" + (p.getIsVisited()? "+" : "-") + " " + p.getName());
        }
    }
}
