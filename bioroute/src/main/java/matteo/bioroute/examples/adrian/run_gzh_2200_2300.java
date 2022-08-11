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
public class run_gzh_2200_2300 {

	public static void main(String[] args) {
		
		final String testdataPath = "C:\\Users\\mfelder\\Documents\\bioroute\\";
		final String biorouteConfig = testdataPath + "bioroute_path_and_config_files\\bike\\config_2200_2300.xml";
//		final String netvisConfig = testdataPath + "vis-config.xml";
//		final String resultFile = testdataPath + "frequencies_0_100.xml";
				
		BiorouteRunner.main(new String[] { biorouteConfig });
		
//		AnalysisRunner.main(new String[] { "FREQUENCIES", "-CONFIGFILE",
//				biorouteConfig, "-RESULTFILE", resultFile, "-totals", "true"});
		
//		AnalysisRunner.main(new String[] { "VISUAL", "-CONFIGFILE",
//				biorouteConfig, "-VISCONFIGFILE", netvisConfig, "-VISDATAFILE",
//				testdataPath + "vis-data.xml" });

		// set link with to 250 to see something meaningful
//		NetvisFromFileRunner.main(new String[] { netvisConfig });		

		
	}
	
}
