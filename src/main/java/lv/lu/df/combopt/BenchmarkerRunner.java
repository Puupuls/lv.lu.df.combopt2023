package lv.lu.df.combopt;

import ai.timefold.solver.benchmark.api.PlannerBenchmark;
import ai.timefold.solver.benchmark.api.PlannerBenchmarkFactory;
import lv.lu.df.combopt.domain.NavigationSolution;
import lv.lu.df.combopt.domain.NavigationSolutionJsonIO;

import java.io.File;

public class BenchmarkerRunner {
    public static void main(String[] args) {
        createDatasets();

        PlannerBenchmarkFactory benchmarkFactoryFromXML = PlannerBenchmarkFactory
                .createFromXmlResource("BenchmarkConfigTabus.xml");

        PlannerBenchmark benchmark = benchmarkFactoryFromXML.buildPlannerBenchmark();

        benchmark.benchmarkAndShowReportInBrowser();

    }

    public static void createDatasets() {
        System.out.println("Creating datasets");
        NavigationSolution problem = NavigationSolution.generateData(10);
        new NavigationSolutionJsonIO().write(problem, new File("data/ex10.json"));
        problem = NavigationSolution.generateData(25);
        new NavigationSolutionJsonIO().write(problem, new File("data/ex25.json"));
        problem = NavigationSolution.generateData(50);
        new NavigationSolutionJsonIO().write(problem, new File("data/ex50.json"));
        problem = NavigationSolution.generateData(75);
        new NavigationSolutionJsonIO().write(problem, new File("data/ex75.json"));
        problem = NavigationSolution.generateData(100);
        new NavigationSolutionJsonIO().write(problem, new File("data/ex100.json"));
        System.out.println("Datasets created");
    }
}
