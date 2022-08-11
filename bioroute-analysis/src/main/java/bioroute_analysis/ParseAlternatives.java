package bioroute_analysis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;

public class ParseAlternatives {
	
	// variables
	
	private static final Logger log = Logger.getLogger(ParseAlternatives.class);
	
	
	// methods
	/**
	 * reads observed route file
	 * @param inputFileName
	 * @param network
	 * @return dictionary assigning od_id to a tuple (origin, destination, chosen route)
	 * @throws IOException
	 */
	
	public static final Map<String, Triple<Node,Node,Path>> parseODandChosenRoutes(String inputFileName, Network network) throws IOException {
		Map<String, Triple<Node,Node,Path>> ods_chosenRoutes = new TreeMap<>();
		int lineCnt = 0;
		FileReader fr = new FileReader(inputFileName);
		BufferedReader in = new BufferedReader(fr);

		// Skip header
		String currLine = in.readLine(); lineCnt++;
		while ((currLine = in.readLine()) != null) {
			String[] entries = currLine.split("\t", -1);
			// IDSEGMENT Origin Destination LinkId1  LinkId2 ... LinkId
			// 0          1         2		 3	        4         last
			
			
			String id = entries[0].trim();
			Node origin = network.getNodes().get(Id.create(entries[1].trim(), Node.class));
			Node destination = network.getNodes().get(Id.create(entries[2].trim(), Node.class));
			if ((origin == null) || (destination == null)) 
			{log.warn("line " +lineCnt+ " id " + id + ": O and/or D not found in the network");}//{ throw new RuntimeException("line " +lineCnt+ " id " + id + ": O and/or D not found in the network"); }
			
			
			List<Node> nodes = new ArrayList<Node>();
			List<Link> links = new ArrayList<Link>();
			for (int i = 3; i < entries.length; i++) {
				if (entries[i].trim().length()==0) {
					break;
				}
				Link link = network.getLinks().get(Id.create(entries[i].trim(), Link.class));
				if (link == null)
				{log.warn("od pair " + id + " and i is " + i + " and link with id " + entries[i].trim() + " does not appear in the network.");}
				links.add(link);
				Node fromNode = link.getFromNode();
				nodes.add(fromNode);
			}
			Link lastLink = links.get(links.size() - 1);
			nodes.add(lastLink.getToNode());
			Path path = new Path(nodes, links, 0.0, 0.0);
			
			ods_chosenRoutes.put(id,new Triple<Node,Node,Path>(origin,destination,path));
			// progress report
			if (lineCnt % 100000 == 0) { log.debug("line "+lineCnt); }
			lineCnt++;
		}
		in.close();
		fr.close();
		log.debug("# lines read: " + lineCnt);
		log.debug("# OD pairs: " + ods_chosenRoutes.size());
		return ods_chosenRoutes;
	}
	/**
	 * reads Bioroute output file consisting of a set of alternatives for each od pair
	 * @param inputFileName
	 * @param network
	 * @return dictionary assigning to each od id a tupe (origin, destination, list of alternatives, dictionary: alternative -> frequency)
	 * @throws IOException
	 */
	
	public static final Map<String, Quadruple<Node, Node, List<Path>, Map<Path, String>>> parseODandBiorouteAlternatives(String inputFileName, Network network) throws IOException {
		Map<String, Quadruple<Node,Node,List<Path>, Map<Path, String>>> ods_alternatives = new TreeMap<>();
		int lineCnt = 0;
		FileReader fr = new FileReader(inputFileName);
		BufferedReader in = new BufferedReader(fr);
		
		// Skip header
		String currLine = in.readLine(); lineCnt++;
		while ((currLine = in.readLine()) != null) {
			String[] entries = currLine.split("\t", -1);
			// IDSEGMENT Origin Destination LinkId1  LinkId2 ... LinkId
			// 0          1         2		 3	        4         last
			
			
			String id = entries[0].trim();
			Node origin = network.getNodes().get(Id.create(entries[2].trim(), Node.class));
			Node destination = network.getNodes().get(Id.create(entries[3].trim(), Node.class));
			List <Path> paths = new ArrayList<Path>();
			Map <Path, String> frequencies = new HashMap<>();
			
			if ((origin == null) || (destination == null)) 
			{log.warn("line " +lineCnt+ " id " + id + ": O and/or D not found in the network");}//{ throw new RuntimeException("line " +lineCnt+ " id " + id + ": O and/or D not found in the network"); }
			
			for (int i = 4; i < entries.length; i++) {
				
				if (entries[i].trim().length()==0) {
					break;
				}
				List<Node> nodes = new ArrayList<Node>();
				List<Link> links = new ArrayList<Link>();
				List<String> list_of_links_id_freq = Arrays.asList(entries[i].split(" "));
				String[] list_of_links = list_of_links_id_freq.subList(0, list_of_links_id_freq.size() - 2).toArray(new String[0]);
				String path_id = list_of_links_id_freq.toArray(new String[0])[list_of_links_id_freq.size() - 2];
				String frequency = list_of_links_id_freq.toArray(new String[0])[list_of_links_id_freq.size() - 1];
						
				for (String link_id : list_of_links) {
					Link link = network.getLinks().get(Id.create(link_id.trim(), Link.class));
					if (link == null)
					{log.warn("od pair " + id + " and i is " + i + " and link with id " + link_id.trim() + " does not appear in the network.");}
					links.add(link);
					Node fromNode = link.getFromNode();
					nodes.add(fromNode);
				}
				Link lastLink = links.get(links.size() - 1);
				nodes.add(lastLink.getToNode());
				Path path = new Path(nodes, links, 0.0, 0.0);
				paths.add(path);
				frequencies.put(path, frequency);
				
			}
			ods_alternatives.put(id,new Quadruple<Node,Node,List<Path>, Map<Path, String>>(origin, destination, paths, frequencies));
			// progress report
			if (lineCnt % 100000 == 0) { log.debug("line "+lineCnt); }
			lineCnt++;
		}
		in.close();
		fr.close();
		log.debug("# lines read: " + lineCnt);
		log.debug("# OD pairs: " + ods_alternatives.size());
		return ods_alternatives;
	}
	

}

