package lv.lu.df.combopt;


import ai.timefold.solver.core.api.score.ScoreExplanation;
import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.Solver;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.EnvironmentMode;
import ai.timefold.solver.core.config.solver.SolverConfig;
import ai.timefold.solver.core.config.solver.termination.TerminationConfig;
import lv.lu.df.combopt.domain.*;
import lv.lu.df.combopt.solver.StreamCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) {

        NavigationSolution problem = NavigationSolution.generateData(10);

        SolverFactory<NavigationSolution> solverFactoryFromXML = SolverFactory.createFromXmlResource("SolverConfig.xml");

        SolverFactory<NavigationSolution> solverFactory = SolverFactory.create(
                new SolverConfig()
                        .withSolutionClass(NavigationSolution.class)
                        .withEntityClasses(TaskLocation.class)
                        .withConstraintProviderClass(StreamCalculator.class)
                        .withTerminationConfig(new TerminationConfig().withSecondsSpentLimit(30L))
                        .withEnvironmentMode(EnvironmentMode.FULL_ASSERT)
        );

        Solver<NavigationSolution> solver = solverFactoryFromXML.buildSolver();
        solver.addEventListener(event -> {
            LOGGER.info("New best solution found: {}", event.getNewBestScore());
        });
        NavigationSolution solution = solver.solve(problem);

        SolutionManager<NavigationSolution, HardMediumSoftScore> solutionManager = SolutionManager.create(solverFactory);
        ScoreExplanation<NavigationSolution, HardMediumSoftScore> scoreExplanation = solutionManager.explain(solution);
        LOGGER.info(scoreExplanation.getSummary());
        solution.print();
    }
}