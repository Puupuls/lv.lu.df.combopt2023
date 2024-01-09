package lv.lu.df.combopt.solver;

import ai.timefold.solver.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;
import lv.lu.df.combopt.domain.Location;

public class DistanceMeter implements NearbyDistanceMeter<Location, Location> {
    public double getNearbyDistance(Location origin, Location destination) {
        return origin.distanceTo(destination);
    }
}