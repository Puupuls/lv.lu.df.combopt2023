package lv.lu.df.combopt.domain;

import ai.timefold.solver.jackson.impl.domain.solution.JacksonSolutionFileIO;

public class NavigationSolutionJsonIO extends JacksonSolutionFileIO<NavigationSolution> {
    public NavigationSolutionJsonIO() {
        super(NavigationSolution.class);
    }
}
