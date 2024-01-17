package lv.lu.df.combopt.solver;

import lv.lu.df.combopt.domain.Location;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

public class LocationStrengthComparator implements Comparator<Location> {
    public int compare(Location a, Location b) {
        return new CompareToBuilder()
                .append(a.getValue(), b.getValue())
                .append(a.getName(), b.getName())
                .toComparison();
    }
}
