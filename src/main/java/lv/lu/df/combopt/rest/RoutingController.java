package lv.lu.df.combopt.rest;

import ai.timefold.solver.core.api.score.analysis.ScoreAnalysis;
import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import ai.timefold.solver.core.api.score.constraint.Indictment;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.SolverManager;
import jakarta.annotation.PostConstruct;
import lv.lu.df.combopt.domain.Location;
import lv.lu.df.combopt.domain.NavigationSolution;
import lv.lu.df.combopt.domain.Router;
import lv.lu.df.combopt.solver.SimpleIndictmentObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/routes")
public class RoutingController {
    @Autowired
    private SolverManager<NavigationSolution, String> solverManager;
    @Autowired
    private SolutionManager<NavigationSolution, HardMediumSoftScore> solutionManager;

    private Map<String, NavigationSolution> solutionMap = new HashMap<>();

    private Router ghRouter = Router.getDefaultRouterInstance();

    @PostMapping("/solve")
    public void solve(@RequestBody NavigationSolution problem) {
        ghRouter.setDistanceTimeMap(problem.getPointList());
        solverManager.solveAndListen(
                problem.getSolutionId(),
                id -> problem,
                solution -> {
                    solution.setLastSolutionTime(LocalDateTime.now());
                    solutionMap.put(solution.getSolutionId(), solution);
                }
        );
    }

    @GetMapping("/solution")
    public NavigationSolution solution(@RequestParam String id) {
        return solutionMap.get(id);
    }
    @GetMapping("/list")
    public List<NavigationSolution> list() {
        return solutionMap.values().stream().toList();
    }

    @GetMapping("/score")
    public ScoreAnalysis<HardMediumSoftScore> score(@RequestParam String id) {
        return solutionManager.analyze(solutionMap.get(id));
    }

    @GetMapping("/indictments")
    public List<SimpleIndictmentObject> indictments(@RequestParam String id) {
        return solutionManager.explain(solutionMap.getOrDefault(id, null)).getIndictmentMap().entrySet().stream()
                .map(entry -> {
                    Indictment<HardMediumSoftScore> indictment = entry.getValue();
                    return
                            new SimpleIndictmentObject(entry.getKey(), // indicted Object
                                    indictment.getScore(),
                                    indictment.getConstraintMatchCount(),
                                    indictment.getConstraintMatchSet());
                }).collect(Collectors.toList());
    }

    @PostConstruct
    public void init() {
        NavigationSolution problem = NavigationSolution.generateData(30);

        List<Location> pl = new ArrayList<>();
        Collections.addAll(pl, problem.getPointList().toArray(Location[]::new));
        Collections.addAll(pl, problem.getStart());
        ghRouter.setDistanceTimeMap(pl);

        solverManager.solveAndListen(
                problem.getSolutionId(),
                id -> problem,
                solution -> {
                    solution.setLastSolutionTime(LocalDateTime.now());
                    solutionMap.put(solution.getSolutionId(), solution);
                }
        );
    }

}
