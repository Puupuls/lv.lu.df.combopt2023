package lv.lu.df.combopt;


import ai.timefold.solver.core.api.score.ScoreExplanation;
import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import lv.lu.df.combopt.domain.NavigationSolution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {

        NavigationSolution problem = NavigationSolution.generateData(100);

        SolverFactory<NavigationSolution> solverFactory = SolverFactory.createFromXmlResource("SolverConfig.xml");

        Solver<NavigationSolution> solver = solverFactory.buildSolver();
        solver.addEventListener(event -> {
            if(event.isEveryProblemChangeProcessed()) {
                LOGGER.info("New best solution found at {}ms\n{}", event.getTimeMillisSpent(), event.getNewBestSolution());
            }
        });

        NavigationSolution solution = solver.solve(problem);
        LOGGER.info("Best solution found: {}", solution.getScore());
        SolutionManager<NavigationSolution, HardMediumSoftScore> solutionManager = SolutionManager.create(solverFactory);
        ScoreExplanation<NavigationSolution, HardMediumSoftScore> scoreExplanation = solutionManager.explain(solution);
        LOGGER.info(scoreExplanation.getSummary());
        solution.print();
    }
}