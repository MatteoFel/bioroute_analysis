
/**
@author      Matteo Felder
*/

package bioroute_analysis;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;

public class RunAnalysisBiorouteBikeNoObservedRoute {
	
	private static final Logger log = Logger.getLogger(RunAnalysisBiorouteBikeNoObservedRoute.class);
	
	/**
	 * analyses choice sets according to the AnalysisBioroutePedestrian file.
	 * @param args                 inputNetworkFile:  matsim input XML network file (String)
	 *                             inputBiorouteOutputFile:       input id|origin|destination|links alternatives comma separated table (String)
	 *                             outputPathSetFile: output path set file (String)
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 4) {
			log.error("       inputNetworkFile:  matsim input XML network file (String)");
			log.error("       inputBiorouteOutputFile:       input id|origin|destination|links alternatives comma seperated table (String)");
			log.error("       outputPathSetFile: output path set file (String)");
			log.error("----------------");
			log.error("2022, matsim.org");
			throw new RuntimeException("incorrect number of arguments");
		}
		
		Gbl.printSystemInfo();

		long startTimeMilliseconds = System.currentTimeMillis();
		log.warn("The start time is now: " + startTimeMilliseconds);
		
		
		String inputNetworkFile = args[0];
		String inputChosenFile = args[1];
		String inputBiorouteOutputFile = args[2];
		String outputPathSetFile = args[3];

		log.info("inputNetworkFile:  "+inputNetworkFile);
		log.info("inputBiorouteOutputFile:       "+inputBiorouteOutputFile);
		log.info("outputPathSetFile: "+outputPathSetFile);
		

		MutableScenario scenario = (MutableScenario) ScenarioUtils.createScenario(ConfigUtils.createConfig());
		Network network = scenario.getNetwork();
		new MatsimNetworkReader(scenario.getNetwork()).readFile(inputNetworkFile);
		
		Gbl.printMemoryUsage();

		Map<String, Quadruple<Node, Node, List<Path>, Map<Path,String>>> ods_alternatives = ParseAlternatives.parseODandBiorouteAlternatives(inputBiorouteOutputFile, network);
			
		Gbl.printMemoryUsage();

		AnalysisBiorouteBikeNoObservedRoute.analysis(outputPathSetFile, network, ods_alternatives);
		
		log.warn("The time now is: " + System.currentTimeMillis());
		long calcTime = System.currentTimeMillis()-startTimeMilliseconds;
		
		
		log.warn("It took us " + calcTime + " ms to compute everything.");
	}	
}


