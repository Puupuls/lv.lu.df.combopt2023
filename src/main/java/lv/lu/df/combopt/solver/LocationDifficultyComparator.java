package lv.lu.df.combopt.solver;

import lv.lu.df.combopt.domain.Location;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

public class LocationDifficultyComparator implements Comparator<Location> {
    public int compare(Location a, Location b) {
        return new CompareToBuilder()
                .append(a.getDifficulty(), b.getDifficulty())
                .append(a.getName(), b.getName())
                .toComparison();
    }
}
