package lv.lu.df.combopt.rest;

import ai.timefold.solver.core.api.score.analysis.ScoreAnalysis;
import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import ai.timefold.solver.core.api.score.constraint.Indictment;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.SolverManager;
import jakarta.annotation.PostConstruct;
import lv.lu.df.combopt.domain.NavigationSolution;
import lv.lu.df.combopt.domain.Router;
import lv.lu.df.combopt.solver.SimpleIndictmentObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        solverManager.solveAndListen(problem.getSolutionId(), id -> problem,
                solution -> solutionMap.put(solution.getSolutionId(), solution));
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
        NavigationSolution problem50 = NavigationSolution.generateData(50);
        ghRouter.setDistanceTimeMap(problem50.getPointList());
        //solutionIOJSON.write(problem50, new File("data/exampleRiga50.json"));
        solverManager.solveAndListen(problem50.getSolutionId(), id -> problem50, solution -> {solutionMap.put(solution.getSolutionId(), solution);});
    }

}
