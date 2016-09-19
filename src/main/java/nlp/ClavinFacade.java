package nlp;

import com.bericotech.clavin.ClavinException;
import com.bericotech.clavin.GeoParser;
import com.bericotech.clavin.GeoParserFactory;
import com.bericotech.clavin.extractor.LocationOccurrence;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.gazetteer.query.LuceneGazetteer;
import com.bericotech.clavin.resolver.ClavinLocationResolver;
import com.bericotech.clavin.resolver.ResolvedLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

/**
 * Created by lorenzoluce on 07/09/16.
 */
public class ClavinFacade {

    private static ClavinFacade ourInstance = null;
    final static Logger log = Logger.getLogger(nlp.ClavinFacade.class);
    
    private static final String CITIES_INDEX = "./citiesIndex";
    private static final String ALL_LOCATIONS_INDEX = "./IndexDirectory";
    private static final int NO_HEURISTICS_MAX_HIT_DEPTH = 1;
    private static final int NO_HEURISTICS_MAX_CONTEXT_WINDOW = 1;
    private static final int HEURISTICS_MAX_HIT_DEPTH = 5;
    private static final int HEURISTICS_MAX_CONTEXT_WINDOW = 5;
    private LuceneGazetteer gazetteer;
    private GeoParser cityParser;
    private ClavinLocationResolver cityResolver;
    private GeoParser locationParser;
    private ClavinLocationResolver locationResolver;

    public static ClavinFacade getInstance() {
    	if (ourInstance == null)
    		ourInstance = new ClavinFacade();
    	return ourInstance;
    }

    private ClavinFacade() {
        try {
            cityParser = GeoParserFactory.getDefault(CITIES_INDEX);
            cityResolver = new ClavinLocationResolver(new LuceneGazetteer(new File(CITIES_INDEX)));
            locationParser = GeoParserFactory.getDefault(ALL_LOCATIONS_INDEX);
            gazetteer = new LuceneGazetteer(new File(ALL_LOCATIONS_INDEX));
            locationResolver = new ClavinLocationResolver(gazetteer);
        } catch (ClavinException e) {
            log.error("cannot load clavin index");
        	System.out.println("CANNOT LOAD CLAVIN INDEX");
        	log.error("the error is: " + e);
        	if (cityParser == null) log.error("è cityparser");
        	if (cityResolver == null) log.error("è cityResolver");
        	if (locationParser == null) log.error("è locationParser");
        	if (gazetteer == null) log.error("è gazetteer");
        	if (locationResolver == null) log.error("è locationResolver");
        }
    }

    public List<GeoName> resolveLocation(String string) throws ClavinException {
        try {
            List<ResolvedLocation> resolvedLocations = resolve(string, locationParser, locationResolver);
            if (!notValid(resolvedLocations))
                return resolvedLocations.stream().map(loc -> loc.getGeoname()).collect(Collectors.toList());
            resolvedLocations = resolve(string, cityParser, cityResolver);
            if (!notValid(resolvedLocations)) {
                return resolvedLocations.stream().map(loc -> {
                    try {
                        return gazetteer.getGeoName(loc.getGeoname().getGeonameID());
                    } catch (ClavinException e) {
                        //log.error("cannot load parent for "+loc.getGeoname().getGeonameID());
                        System.out.println("cannot load parent for "+loc.getGeoname().getGeonameID());
                        return loc.getGeoname();
                    }
                }).collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("cannot extract locations from "+string);
            System.out.println("cannot extract locations from "+string);
            log.error("THE ERROR IS: "+e);
        }
        return new ArrayList<>();
    }

    //public LazyAncestryGeoName[] getParents(ResolvedLocation loc) {
        //gazetteer.getGeoName(loc.getGeoname().getParent());
    //}

    private boolean notValid(List<ResolvedLocation> resolvedLocations) {
        return resolvedLocations.size()<1 ||
                resolvedLocations.get(0).getConfidence()<0.1 ||
                resolvedLocations.get(0).getGeoname().getPopulation()==0;
    }

    private List<ResolvedLocation> resolve(String string, GeoParser parser, ClavinLocationResolver resolver) throws Exception {
        List<LocationOccurrence> locs = Arrays.asList(string.split("\\p{Punct}+")).stream().map(s -> new LocationOccurrence(s, 0)).collect(Collectors.toList());
        List<ResolvedLocation> resolvedLocations = resolveNoHeuristics(locs, resolver);
        if (notValid(resolvedLocations))
            resolvedLocations = parser.parse(string);
        return resolvedLocations;
    }

    private List<ResolvedLocation> resolveNoHeuristics(final List<LocationOccurrence> locs, ClavinLocationResolver resolver) throws ClavinException {
        List<ResolvedLocation> resolvedLocations = resolver.resolveLocations(locs, NO_HEURISTICS_MAX_HIT_DEPTH, NO_HEURISTICS_MAX_CONTEXT_WINDOW, false);
        if (resolvedLocations.size()<1)
            resolvedLocations = resolver.resolveLocations(locs, NO_HEURISTICS_MAX_HIT_DEPTH, NO_HEURISTICS_MAX_CONTEXT_WINDOW, true);
        return resolvedLocations;
    }

    /** data una lista di LocationOccurrence, se è indeciso su un luogo (es Worchester CA e Worchester TX) restituisce
     * qll che è geograficamente più vicino agli altri luoghi */
    private List<ResolvedLocation> resolveWithHeuristics(final List<LocationOccurrence> locs, ClavinLocationResolver resolver) throws ClavinException {
        List<ResolvedLocation> resolvedLocations = resolver.resolveLocations(locs, HEURISTICS_MAX_HIT_DEPTH, HEURISTICS_MAX_CONTEXT_WINDOW, false);
        if (resolvedLocations.size()<1)
            resolvedLocations = resolver.resolveLocations(locs, HEURISTICS_MAX_HIT_DEPTH, HEURISTICS_MAX_CONTEXT_WINDOW, true);
        return resolvedLocations;
    }

}
