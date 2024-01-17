package lv.lu.df.combopt.domain;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.util.shapes.GHPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Router {
    private static Router instance;
    public GraphHopper router;
    static public Router getDefaultRouterInstance() {
        if(instance == null)
            new Router("data/latvia.osm.pbf", ".ghtemp");
        return instance;
    }


    public Router(String osmFile, String ghLocation) {
        router = new GraphHopper();
        router.setOSMFile(osmFile);
        router.setGraphHopperLocation(ghLocation);
        router.setProfiles(new Profile("profile").setVehicle("foot").setWeighting("shortest"));
        router.getCHPreparationHandler().setCHProfiles(new CHProfile("profile"));
        router.setElevation(true);
        router.importOrLoad();
        instance = this;
    }

    public Double[] getClosestPointOnGraph(Double[] location){
        GHRequest req = new GHRequest(
                new GHPoint(location[0], location[1]),
                new GHPoint(location[0], location[1])
        ).
                setProfile("profile").
                setLocale(Locale.US);
        GHResponse rsp = router.route(req);
        if (rsp.hasErrors())
            throw new RuntimeException(rsp.getErrors().toString());
        ResponsePath path = rsp.getBest();
        return new Double[]{path.getWaypoints().getLat(0), path.getWaypoints().getLon(0)};
    }

    public Integer getDistance(Location from, Location to){
        GHRequest req = new GHRequest(
                new GHPoint(from.getLat(), from.getLon()),
                new GHPoint(to.getLat(), to.getLon())
        ).
                setProfile("profile").
                setLocale(Locale.US);
        GHResponse rsp = router.route(req);
        if (rsp.hasErrors())
            throw new RuntimeException(rsp.getErrors().toString());
        ResponsePath path = rsp.getBest();
        from.getDistanceMap().put(to.getName(), (int)Math.round(path.getDistance()));
        List<List<Double>> pth = new ArrayList<>();
        for (int i = 0; i < path.getPoints().size(); i++) {
            pth.add(List.of(path.getPoints().getLat(i), path.getPoints().getLon(i), path.getPoints().getEle(i)));
        }
        from.getPathMap().put(to.getName(), pth);
        from.getTimeMap().put(to.getName(), Math.toIntExact(path.getTime() / 1000));
        return (int)Math.round(path.getDistance());
    }

    public void setDistanceTimeMap(List<Location> locationList) {
        for (Location location: locationList) {
            for (Location toLocation: locationList) {
                GHRequest req = new GHRequest(location.getLat(), location.getLon(), toLocation.getLat(), toLocation.getLon()).
                                setProfile("profile").
                                setLocale(Locale.US);
                GHResponse rsp = router.route(req);
                if (rsp.hasErrors())
                    throw new RuntimeException(rsp.getErrors().toString());
                ResponsePath path = rsp.getBest();
                List<List<Double>> pth = new ArrayList<>();
                for (int i = 0; i < path.getPoints().size(); i++) {
                    pth.add(List.of(path.getPoints().getLat(i), path.getPoints().getLon(i), path.getPoints().getEle(i)));
                }
                location.getPathMap().put(toLocation.getName(), pth);

                // distance in meters and time in millis in the response path
                location.getDistanceMap().put(toLocation.getName(), (int)Math.round(path.getDistance()));
                location.getTimeMap().put(toLocation.getName(), Math.toIntExact(path.getTime() / 1000));
            }
        }
    }
}