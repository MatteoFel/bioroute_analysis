# bioroute_analysis

Bioroute is the Gunnar Flötteröd's choice set generation algorithm based on Metropolis-Hastings algorithm for sampling paths. The main reference is

G. Flötteröd and M. Bierlaire. Metropolis-Hastings sampling of paths. Transportation Research Part B, 48:53-66, 2013.

The following repository can be used as a pipeline to generate choice sets using bioroute and analysing its output.


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

More details:

Prerequisites:

  (1) fork the repository bioroute_analysis (https://github.com/MatteoFel/bioroute_analysis.git) and import it as a Maven project to Eclipse
  
  (2) fork the repository floetteroed_bioroute, this is the adapted version (https://github.com/MatteoFel/floetteroed_bioroute.git) and import it as a Maven project to Eclipse
  
  (3) download the python scripts 

Starting input: 

  (1) tsv-file containing all observations, i.e. od_id | origin | destination | link_id 1 | ... | link_id last
  
  (2) a template bioroute configuration file

---> write bioroute configuration files for all observations (100 OD pairs per config file) with write_config_file.py

---> run bioroute using the code adapted from Gunnar's repository; the output are xml-files containing all routes
     (one further input that is need here is the network!)

---> use xml_to_csv.py to convert the routes to a csv format for the analysis

---> run postprocessing_analysis_input_files.py

---> run the analysis using RunAnalysisBioroute... .java

---> run postprocessing_analysis_output_files.py

  
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

If you use this as an input for an (Expanded) Path Size Logit Model consider the following two references:

(1) Frejinger, E., Bierlaire, M., Ben-Akiva, M., 2009. Sampling of alternatives for route choice modeling. Transportation Research Part B 43 (10), 984–994.

(2) Sobhani, A., Aliabadi, H.A., Farooq, B., 2019. Metropolis-Hasting based Expanded Path Size Logit model for cyclists’ route choice using GPS data. International journal of transportation science and technology 8 (2), 161-175.





