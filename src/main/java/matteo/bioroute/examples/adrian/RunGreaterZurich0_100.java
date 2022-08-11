package matteo.bioroute.examples.adrian;

import floetteroed.bioroute.BiorouteRunner;
import floetteroed.bioroute.analysis.AnalysisRunner;
import floetteroed.utilities.visualization.NetvisFromFileRunner;

/**
 * 
 * @author Gunnar Flötteröd
 * adapted by Matteo Felder
 *
 */
public class RunGreaterZurich0_100 {

	public static void main(String[] args) {
		
		final String testdataPath = "/Users/matteofelder/Documents/IVT/bioroute/zurich_bike/";
		final String biorouteConfig = testdataPath + "config_files/config_0_20.xml";
//		final String netvisConfig = testdataPath + "vis-config.xml";
		final String resultFile = testdataPath + "frequencies_0_20.xml";
				
		BiorouteRunner.main(new String[] { biorouteConfig });
		
		AnalysisRunner.main(new String[] { "FREQUENCIES", "-CONFIGFILE",
				biorouteConfig, "-RESULTFILE", resultFile, "-totals", "true"});
		
//		AnalysisRunner.main(new String[] { "VISUAL", "-CONFIGFILE",
//				biorouteConfig, "-VISCONFIGFILE", netvisConfig, "-VISDATAFILE",
//				testdataPath + "vis-data.xml" });

		// set link with to 250 to see something meaningful
//		NetvisFromFileRunner.main(new String[] { netvisConfig });		

		
	}
	
}
