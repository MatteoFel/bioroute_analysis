package bioroute_analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;


public class AnalysisBioroutePedestrian {
	
	// variables
	
	private static final Logger log = Logger.getLogger(AnalysisBioroutePedestrian.class);
	
	// methods
	
	public static Double getPathLength(Path path) {
		Double length = path.links.stream().map(x -> x.getLength())
		  .reduce(Double::sum).orElse(0d);
		return length;
	}
	
	public static Double getPathSize(Path alternative, Path chosen_route, List<Path> alternatives) {
		
		Double length_alt = getPathLength(alternative);
		
		List<Double> PS_alt = new ArrayList<Double>();
		
		//the similarity factor counts the number of links a given route shares
		//with other routes in the choice set
		for (Link l : alternative.links) {
			int similarity_alt = 0;
			
			if (chosen_route.links.contains(l)) {
				similarity_alt ++;
			}
			for (Path path_alt : alternatives) {
				if (path_alt.links.contains(l)) {
					similarity_alt ++;
				}
			}
			//the path size is the sum of terms of the following form
			PS_alt.add(l.getLength()/similarity_alt);
		}
		
		Double path_size_alt = 1/length_alt * PS_alt.stream().mapToDouble(f -> ((Double) f).doubleValue()).sum();
		
		
		return path_size_alt;
		
	}
	
	/**
	 * analyses chosen route and all alternatives
	 * @param outputFileName
	 * @param network
	 * @param ods_chosenRoutes        dictionary: od id -> (o,d, chosen path)
	 * @param ods_alternatives        dictionary: od id -> (o,d, list of alternatives, dictionary: alternative -> frequency)
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	
	public static final void analysis(String outputFileName, Network network, Map<String, Triple<Node, Node, Path>> ods_chosenRoutes, Map<String, Quadruple<Node, Node, List<Path>, Map<Path, String>>> ods_alternatives) throws IOException, NumberFormatException {
		FileWriter fw = new FileWriter(outputFileName);
		BufferedWriter out = new BufferedWriter(fw);
		
		int max_number_of_alternatives = -1;
		for (Entry<String, Quadruple<Node, Node, List<Path>, Map<Path, String>>> entry : ods_alternatives.entrySet()) {
			int number_of_alternatives = entry.getValue().getThird().size();
			max_number_of_alternatives = Math.max(number_of_alternatives, max_number_of_alternatives);
		}
		
		out.write("OD_pair_id" + ",");
		
		for (int i = 1; i <= max_number_of_alternatives + 1; i++){
			out.write("availability_" + String.valueOf(i) +  ",");
			out.write("internal_id_" + String.valueOf(i) +  ",");
			out.write("number_of_links_" + String.valueOf(i) +  ",");
			out.write("length_" + String.valueOf(i) +  ",");
			out.write("overlap " + String.valueOf(i) + "," );
			out.write("path_size_" + String.valueOf(i) + "," );
			out.write("ln_path_size_" + String.valueOf(i) + "," );
			out.write("frequency_" + String.valueOf(i) + ",");
			out.write("normalised_weight_" + String.valueOf(i) + ",");
		}
		out.write("CHOICE" + "\n");
		
		
		for (Entry<String, Triple<Node, Node, Path>> entry : ods_chosenRoutes.entrySet()) {
			String id = entry.getKey();
			
			// write id
			out.write(id + ",");
			
			// get the observed route
			Triple<Node, Node, Path> od_cr = entry.getValue();
			Path chosen_route = od_cr.getThird();
			
			// get bioroute's alternatives
			List<Path> alternatives = ods_alternatives.get(id).getThird();
			
			// shortest path
			Path shortest_path = alternatives.get(0);
			
			
			Map<Path, String> frequencies = ods_alternatives.get(id).getFourth();
			
			frequencies.put(chosen_route, "1.0");
			
			// assign to each a new id, internal to the choice set
			Map<Integer, Path> internal_ids = new HashMap<>();
			
			internal_ids.put(0, chosen_route);
			
			int j = 1;
			for (Path alternative : alternatives) {
				internal_ids.put(j, alternative);
				j ++;
			}
			
			///// OVERLAP //////
			
			
			Map <Integer, Double> overlap = new HashMap<>();
			
			for (Map.Entry<Integer, Path> choice_set_entry : internal_ids.entrySet()) {
				int internal_id = choice_set_entry.getKey();
				Path path = choice_set_entry.getValue();
				
				// overlap 
				Double link_for_link_overlap_with_cr = 0.0;
				for (Link l : path.links) {
					if (chosen_route.links.contains(l)) {
						link_for_link_overlap_with_cr += l.getLength(); 
					}
				}
				Double overlap_percentage = link_for_link_overlap_with_cr / getPathLength(chosen_route);
				overlap.put(internal_id, overlap_percentage);
			}
			
			// remove alternatives that overlap with chosen route by more than 90%, add frequency of that route to
			// the frequency of the chosen route.
			
			List<Integer> internal_ids_to_remove = new ArrayList<Integer>();
			
			for (Map.Entry<Integer, Path> choice_set_entry : internal_ids.entrySet()) {
				int internal_id = choice_set_entry.getKey();
				if (internal_id == 0) {
					continue;
				}
				Double overlap_with_cr = overlap.get(internal_id);
				if (overlap_with_cr >= 0.9) {
					internal_ids_to_remove.add(internal_id);
					frequencies.put(chosen_route, Double.toString(Double.parseDouble(frequencies.get(chosen_route)) + Double.parseDouble(frequencies.get(choice_set_entry.getValue()))));
				}
			}
			
			for (Integer id_to_remove : internal_ids_to_remove) {
//				log.warn(id);
//				log.warn(frequencies.get(internal_ids.get(id_to_remove)));
//				log.warn(id_to_remove);
				internal_ids.remove(id_to_remove);
			}
			
			///////////////////////// START ANALYSIS ///////////////////////////////
			
			for (Map.Entry<Integer, Path> choice_set_entry : internal_ids.entrySet()) {
				int internal_id = choice_set_entry.getKey();
				Path path = choice_set_entry.getValue();
				
				out.write(1 + ",");
				
				out.write(internal_id + ",");
				
				// number of links
				
				int number_of_links = path.links.size();
				out.write(number_of_links + ",");
				
				// length
				
				Double length = path.links.stream().map(x -> x.getLength()).reduce(Double::sum).orElse(0d);
				
				out.write(length + ",");
				
				// overlap
				
				Double overlap_with_chosen_route = overlap.get(internal_id);
				
				out.write(overlap_with_chosen_route + ",");
				
				// path sizes 
				
				Double path_size = getPathSize(path, chosen_route, alternatives);
				
				out.write(path_size + ",");
				
				out.write(Math.log(path_size) + ",");
				
				// frequency
				
				out.write(frequencies.get(path) + ",");
				
				// normalised weight
				
				Double mu = - Math.log(2)/(0.2 * getPathLength(shortest_path));
				
				Double normalising_factor = (double) (number_of_links * (number_of_links - 1) * (number_of_links - 2) / 6);
				
				Double normalised_weight = Math.exp(- mu * length)/normalising_factor;
				
				out.write(normalised_weight + ",");
				
			}
			out.write("\n");
		}
		out.close();
		fw.close();
	}

}
