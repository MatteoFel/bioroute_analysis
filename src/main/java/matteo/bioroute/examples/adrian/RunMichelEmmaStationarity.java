package matteo.bioroute.examples.adrian;

import floetteroed.bioroute.BiorouteRunner;
import floetteroed.bioroute.analysis.AnalysisRunner;
import floetteroed.utilities.visualization.NetvisFromFileRunner;

/**
 * 
 * @author Gunnar Flötteröd
 *
 * an copy of Gunnar's example, adapted to experiment with other types of analysis
 * configurations.
 *
 */
public class RunMichelEmmaStationarity {

	public static void main(String[] args) {
		
		//final String testdataPath = "./testdata/MichelEmmaNetwork/";
		final String testdataPath = "/Users/matteofelder/Documents/IVT/bioroute/MichelEmmaNetwork/";
		final String biorouteConfig = testdataPath + "config.xml";
		final String netvisConfig = testdataPath + "vis-config.xml";
//		final String resultFile = testdataPath + "frequencies.xml";
		final String resultFile = testdataPath + "correlation.xml";

		BiorouteRunner.main(new String[] { biorouteConfig });
	
		AnalysisRunner.main(new String[] { "CORRELATION", "-CONFIGFILE",
				biorouteConfig, "-RESULTFILE", resultFile, "-MAXDISTANCE", "100000"});
		
//		AnalysisRunner.main(new String[] { "STATIONARITY", "-CONFIGFILE",
//				biorouteConfig, "-RESULTFILE", resultFile, "-totals", "true"});

//		AnalysisRunner.main(new String[] { "FREQUENCIES", "-CONFIGFILE",
//				biorouteConfig, "-RESULTFILE", resultFile, "-totals", "true"});
		
//		AnalysisRunner.main(new String[] { "VISUAL", "-CONFIGFILE",
//				biorouteConfig, "-VISCONFIGFILE", netvisConfig, "-VISDATAFILE",
//				testdataPath + "vis-data.xml" });

		// set link with to 250 to see something meaningful
//		NetvisFromFileRunner.main(new String[] { netvisConfig });		

		
	}
	
}
