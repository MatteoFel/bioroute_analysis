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
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.scenario.MutableScenario;
import org.matsim.core.scenario.ScenarioUtils;

public class AnalysisBiorouteBikeNoObservedRoute {
	
	// variables
	
	private static final Logger log = Logger.getLogger(AnalysisBiorouteBikeNoObservedRoute.class);
	
	// methods
	
	public static Double getPathLength(Path path) {
		Double length = path.links.stream().map(x -> x.getLength())
		  .reduce(Double::sum).orElse(0d);
		return length;
	}
	
	public static Double getPathSize(Path alternative, List<Path> alternatives) {
		
		Double length_alt = getPathLength(alternative);
		
		List<Double> PS_alt = new ArrayList<Double>();
		
		//the similarity factor counts the number of links a given route shares
		//with other routes in the choice set
		for (Link l : alternative.links) {
			int similarity_alt = 0;
			
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
	 * @param ods_alternatives        dictionary: od id -> (o,d, list of alternatives, dictionary: alternative -> frequency)
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	
	public static final void analysis(String outputFileName, Network network, Map<String, Quadruple<Node, Node, List<Path>, Map<Path, String>>> ods_alternatives) throws IOException, NumberFormatException {
		FileWriter fw = new FileWriter(outputFileName);
		BufferedWriter out = new BufferedWriter(fw);
		
		int max_number_of_alternatives = -1;
		for (Entry<String, Quadruple<Node, Node, List<Path>, Map<Path, String>>> entry : ods_alternatives.entrySet()) {
			int number_of_alternatives = entry.getValue().getThird().size();
			max_number_of_alternatives = Math.max(number_of_alternatives, max_number_of_alternatives);
		}
		
		String[] osm_highway_tags = {"primary", "secondary", "tertiary", "footway", "pedestrian",
                "path", "track", "cycleway", "unclassified", "residential", 
                "living_street", "service", "motorway", "motorway_link",
                "primary_link", "secondary_link", "tertiary_link"};
		
		String[] number_of_lanes = {"0_lanes", "1_lanes", "2_lanes", "3_lanes", "4_lanes"}; 
		
		
		out.write("OD_pair_id" + ",");
		
		for (int i = 1; i <= max_number_of_alternatives; i++){
			out.write("availability_" + String.valueOf(i) +  ",");
			out.write("internal_id_" + String.valueOf(i) +  ",");
			out.write("number_of_links_" + String.valueOf(i) +  ",");
			out.write("length_" + String.valueOf(i) +  ",");
			out.write("path_size_" + String.valueOf(i) + "," );
			out.write("ln_path_size_" + String.valueOf(i) + "," );
			out.write("frequency_" + String.valueOf(i) + ",");
			out.write("normalised_weight_" + String.valueOf(i) + ",");
			for (String tag : osm_highway_tags) {
				out.write(tag + "_" + String.valueOf(i) + ",");
			}
			for (String lanes : number_of_lanes) {
				out.write(lanes + "_" + String.valueOf(i) + ",");
			}
			out.write("max_slope" + "_" + String.valueOf(i) + ",");
			out.write("avg_slope" + "_" + String.valueOf(i) + ",");
			out.write("avg_link_slope" + "_" + String.valueOf(i) + ",");
			out.write("total_elevation_gain" + "_"+ String.valueOf(i) + ",");
			out.write("uphill_distance" + "_" + String.valueOf(i) + ",");
			out.write("elevation_gain_total_length" + "_" + String.valueOf(i) + ",");
			out.write("elevation_gain_uphill_length" + "_" + String.valueOf(i) + ",");
			out.write("grade_1_2" + "_" + String.valueOf(i) + ",");
			out.write("grade_2_3" + "_" + String.valueOf(i) + ",");
			out.write("grade_3_4" + "_" + String.valueOf(i) + ",");
			out.write("grade_4_5" + "_" + String.valueOf(i) + ",");
			out.write("grade_5_6" + "_" + String.valueOf(i) + ",");
			out.write("grade_6_7" + "_" + String.valueOf(i) + ",");
			out.write("grade_7_8" + "_" + String.valueOf(i) + ",");
			out.write("grade_8_9" + "_" + String.valueOf(i) + ",");
			out.write("grade_9_10" + "_" + String.valueOf(i) + ",");
			out.write("grade_10_infty" + "_" + String.valueOf(i) + ",");
			out.write("max_maxspeed" + "_" + String.valueOf(i) + ",");
			out.write("distance_lower_30" + "_" + String.valueOf(i) + ",");
			out.write("distance_30" + "_" + String.valueOf(i) + ",");
			out.write("max_ldv_count" + "_" + String.valueOf(i) + ",");
			out.write("avg_ldv_count" + "_" + String.valueOf(i) + ",");
			out.write("ldv_count_0_2500" + "_" + String.valueOf(i) + ",");
			out.write("ldv_count_2500_10000" + "_" + String.valueOf(i) + ",");
			out.write("ldv_count_100000_infty" + "_" + String.valueOf(i) + ",");
			out.write("veloweg" + "_" + String.valueOf(i) + ",");
			out.write("velostreifen" + "_" + String.valueOf(i) + ",");
			out.write("ts_osm" + "_" + String.valueOf(i) + ",");
			out.write("ts_osm_2" + "_" + String.valueOf(i) + ",");
		}
		out.write("CHOICE" + "\n");
		
		
		for (Entry<String, Quadruple<Node, Node, List<Path>, Map<Path, String>>> entry : ods_alternatives.entrySet()) {
			String id = entry.getKey();
			
			// write id
			out.write(id + ",");
			
			// get bioroute's alternatives
			List<Path> alternatives = entry.getValue().getThird();
			
			// shortest path
			Path shortest_path = alternatives.get(0);
			
			
			Map<Path, String> frequencies = entry.getValue().getFourth();
			
			
			// assign to each a new id, internal to the choice set
			Map<Integer, Path> internal_ids = new HashMap<>();
			
			int j = 1;
			for (Path alternative : alternatives) {
				internal_ids.put(j, alternative);
				j ++;
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
				
				
				// path sizes 
				
				Double path_size = getPathSize(path, alternatives);
				
				out.write(path_size + ",");
				
				out.write(Math.log(path_size) + ",");
				
				// frequency
				
				out.write(frequencies.get(path) + ",");
				
				// normalised weight
				
				Double mu = - Math.log(2)/(0.2 * getPathLength(shortest_path));
				
				Double normalising_factor = (double) (number_of_links * (number_of_links - 1) * (number_of_links - 2) / 6);
				
				Double normalised_weight = Math.exp(- mu * length)/normalising_factor;
				
				out.write(normalised_weight + ",");
				
				// OSM tags
				
				// for each highway type, return the percentage of the route consisting of that highway type
				
				Map<String, Double> highway_share = new HashMap<String, Double>();
				for (String tag : osm_highway_tags) {
					highway_share.put(tag, 0.0);
				}
				
				for (String tag : osm_highway_tags) {
					for (Link l: path.links) {
						if (l.getAttributes().getAttribute("highway").equals(tag)) {
							highway_share.put(tag, highway_share.get(tag) + l.getLength());
						};
						
					}
					out.write(highway_share.get(tag)/length + ",");
				}
				
				
				// LANES
				
				// for each possible number of lanes, return the percentage consisting of that number of lanes
				
				Map<String, Double> lanes_percentages = new HashMap<String, Double>();
				for (String lanes : number_of_lanes) {
					lanes_percentages.put(lanes, 0.0);
				}
				
				for (Link l : path.links) {
					if (l.getNumberOfLanes() == 0.0) {
						lanes_percentages.put("0_lanes", lanes_percentages.get("0_lanes") + l.getLength());
					}
					else if (l.getNumberOfLanes() == 1.0) {
						lanes_percentages.put("1_lanes", lanes_percentages.get("1_lanes") + l.getLength());
					}
					else if (l.getNumberOfLanes() == 2.0) {
						lanes_percentages.put("2_lanes", lanes_percentages.get("2_lanes") + l.getLength());
					}
					else if (l.getNumberOfLanes() == 3.0) {
						lanes_percentages.put("3_lanes", lanes_percentages.get("3_lanes") + l.getLength());
					}
					else if (l.getNumberOfLanes() == 4.0) {
						lanes_percentages.put("4_lanes", lanes_percentages.get("4_lanes") + l.getLength());
					}
				}
				
				for (String lanes : number_of_lanes) {
					out.write(lanes_percentages.get(lanes)/length + ",");
				}
				
				// MAX, AVG link and AVG GRADE
				
				// compute the slope of the steepest link
				// and the average slope of any link and the average slope
				
				Double max_gradient = (double) -10;
				Double avg_gradient = 0.0;
				Double avg_link_gradient = 0.0;
				for (Link l : path.links) {
					String temp_gradient_str = (l.getAttributes().getAttribute("grade")).toString();
					Double temp_gradient = Double.valueOf(temp_gradient_str).doubleValue();
					max_gradient = Math.max(temp_gradient, max_gradient);
					avg_gradient += temp_gradient * l.getLength();
					avg_link_gradient += temp_gradient;
				}
				
				avg_gradient /= length;
				avg_link_gradient /= number_of_links;
				
				out.write(String.valueOf(max_gradient) + ",");
				out.write(String.valueOf(avg_gradient) + ",");
				out.write(String.valueOf(avg_link_gradient) + ",");
				
				// elevation gain
				
				Double elevation_gain = 0.0;
				Double uphill_distance = 0.0;
				
				for (Link l : path.links) {
					String temp_gradient_str = (l.getAttributes().getAttribute("grade")).toString();
					Double temp_gradient = Double.valueOf(temp_gradient_str).doubleValue();
					if (temp_gradient > 0.0) {
						elevation_gain += temp_gradient * l.getLength();
						uphill_distance += l.getLength();
					}
				}
				
				out.write(elevation_gain + ",");
				out.write(uphill_distance + ",");
				
				out.write(elevation_gain/length + ",");
				if (uphill_distance > 0.0) {
					out.write(elevation_gain/uphill_distance + ",");
				}
				else {
					out.write(elevation_gain + ","); // this is 0.0
				}
				
				
				
				// GRADIENT CATEGORIES
				
				Double grade_1_2 = 0.0;
				Double grade_2_3 = 0.0;
				Double grade_3_4 = 0.0;
				Double grade_4_5 = 0.0;
				Double grade_5_6 = 0.0;
				Double grade_6_7 = 0.0;
				Double grade_7_8 = 0.0;
				Double grade_8_9 = 0.0;
				Double grade_9_10 = 0.0;
				Double grade_10_infty = 0.0;
				
				
				
				for (Link l : path.links) {
					String grade_str = (l.getAttributes().getAttribute("grade")).toString();
					Double grade = Double.valueOf(grade_str).doubleValue();
					if (grade >= 0.01 && grade <= 0.02) {
						grade_1_2 += l.getLength();
					}
					else if (grade > 0.02 && grade <= 0.03) {
						grade_2_3 += l.getLength();
					}
					else if (grade > 0.03 && grade <= 0.04) {
						grade_3_4 += l.getLength();
					}
					else if (grade > 0.04 && grade <= 0.05) {
						grade_4_5 += l.getLength();
					}
					else if (grade > 0.05 && grade <= 0.06) {
						grade_5_6 += l.getLength();
					}
					else if (grade > 0.06 && grade <= 0.07) {
						grade_6_7 += l.getLength();
					}
					else if (grade > 0.07 && grade <= 0.08) {
						grade_7_8 += l.getLength();
					}
					else if (grade > 0.08 && grade <= 0.09) {
						grade_8_9 += l.getLength();
					}
					else if (grade > 0.09 && grade <= 0.10) {
						grade_9_10 += l.getLength();
					}
					else if (grade > 0.10) {
						grade_10_infty += l.getLength();
					}
					else {
						continue;
					}
				}
				

				out.write(grade_1_2/length + ",");
				out.write(grade_2_3/length + ",");
				out.write(grade_3_4/length + ",");
				out.write(grade_4_5/length + ",");
				out.write(grade_5_6/length + ",");
				out.write(grade_6_7/length + ",");
				out.write(grade_7_8/length + ",");
				out.write(grade_8_9/length + ",");
				out.write(grade_9_10/length + ",");
				out.write(grade_10_infty/length + ",");
				
				
				
				// MAXIMAL MAXSPEED
				
				// compute the maximal allowed car speed
				
				Double max_maxspeed = 0.0;
				for (Link l : path.links) {
					String temp_maxspeed_str = (l.getAttributes().getAttribute("max_speed")).toString();
					if (temp_maxspeed_str.equals("nan") || temp_maxspeed_str.equals("walk") || temp_maxspeed_str.equals("signals")) {
						max_maxspeed = Math.max(0.0, max_maxspeed);
					}
					else {
						Double temp_maxspeed = Double.valueOf(temp_maxspeed_str).doubleValue();
						max_maxspeed = Math.max(temp_maxspeed, max_maxspeed);
					}
				}
				
				out.write(max_maxspeed + ",");
				
				// Distance with maxspeed lower than 30
				
				Double maxspeed_lower_than_30 = 0.0;
				for (Link l : path.links) {
					String temp_maxspeed_str = (l.getAttributes().getAttribute("max_speed")).toString();
					if (temp_maxspeed_str.equals("nan") || temp_maxspeed_str.equals("walk") || temp_maxspeed_str.equals("signals")) {
						continue;
					}
					else if (Double.valueOf(temp_maxspeed_str).doubleValue() <= 30.0) {
						maxspeed_lower_than_30 += l.getLength();
					}
					else {
						continue;
					}
				}
				
				out.write(maxspeed_lower_than_30/length + ",");
				
				
				// Distance with maxspeed 30
				
				Double maxspeed_30 = 0.0;
				for (Link l : path.links) {
					String temp_maxspeed_str = (l.getAttributes().getAttribute("max_speed")).toString();
					if (temp_maxspeed_str.equals("nan") || temp_maxspeed_str.equals("walk") || temp_maxspeed_str.equals("signals")) {
						continue;
					}
					else if (Double.valueOf(temp_maxspeed_str).doubleValue() == 30.0) {
						maxspeed_30 += l.getLength();
					}
					else {
						continue;
					}
				}
				
				out.write(maxspeed_30/length + ",");
				
				
				// MAX and AVG LDV COUNT
				
				Double max_ldv_count = 0.0;
				Double avg_ldv_count = 0.0;
				for (Link l : path.links) {
					String temp_ldv_count_str = (l.getAttributes().getAttribute("ldv_count")).toString();
					if (temp_ldv_count_str.equals("nan")) {
						avg_ldv_count += 0.0;
						max_ldv_count = Math.max(0.0, max_ldv_count);
					}
					else {
						Double temp_ldv_count_cr = Double.valueOf(temp_ldv_count_str).doubleValue();
						max_ldv_count = Math.max(temp_ldv_count_cr, max_ldv_count);
						avg_ldv_count += temp_ldv_count_cr * l.getLength()/length;
					}
				}
				
				out.write(String.valueOf(max_ldv_count) + ",");
				out.write(avg_ldv_count + ",");
				
				
				
				
				// LDV COUNT CATEGORIES
				
				Double ldv_count_0_2500 = 0.0;
				Double ldv_count_2500_10000 = 0.0;
				Double ldv_count_10000_infty = 0.0;
				
				for (Link l : path.links) {
					String ldv_count = (l.getAttributes().getAttribute("ldv_count")).toString();
					if (ldv_count.equals("nan") || (double) Double.valueOf(ldv_count).doubleValue() <= 2500.0) {
						ldv_count_0_2500 += l.getLength();
					}
					else if ((double) Double.valueOf(ldv_count).doubleValue() > 2500.0 && (double) Double.valueOf(ldv_count).doubleValue() <= 10000.0) {
						ldv_count_2500_10000 += l.getLength();
					}
					else {
						ldv_count_10000_infty += l.getLength();
					}
				}
				
				out.write(ldv_count_0_2500/length + ",");
				out.write(ldv_count_2500_10000/length + ",");
				out.write(ldv_count_10000_infty/length + ",");
				
				// VELOWEG
				
				Double sep_biking_lanes_length = 0.0;
				for (Link l : path.links) {
					if (l.getAttributes().getAttribute("veloweg").equals("1") || l.getAttributes().getAttribute("cycleway").equals("track")) {
						sep_biking_lanes_length += l.getLength();
					}
				}
				out.write(sep_biking_lanes_length/length + ",");
				
				
				// VELOSTREIFEN
				
				Double biking_lanes_length = 0.0;
				for (Link l : path.links) {
					if (l.getAttributes().getAttribute("velostreifen").equals("1")) {
						biking_lanes_length += l.getLength();
					}
				}
				out.write(biking_lanes_length/length + ",");
				
				
				// TRAFFIC SIGNALS OSM
				
				Double number_of_ts_osm = 0.0;
				for (Link l : path.links) {
					if (l.getToNode().getAttributes().getAttribute("ts_osm").equals("1")) {
						number_of_ts_osm ++;
					}
				}
				out.write(number_of_ts_osm + ",");
				
				Double number_of_ts_osm_2 = 0.0;
				for (Link l : path.links) {
					if (l.getToNode().getAttributes().getAttribute("ts_osm_2").equals("1")) {
						number_of_ts_osm_2 ++;
					}
				}
				out.write(number_of_ts_osm_2 + ",");
				
			}
			out.write("\n");
		}
		out.close();
		fw.close();
	}
}
