# bioroute_analysis

Bioroute is the Gunnar Flötteröd's choice set generation algorithm based on Metropolis-Hastings algorithm for sampling paths. The main reference is

G. Flötteröd and M. Bierlaire. Metropolis-Hastings sampling of paths. Transportation Research Part B, 48:53-66, 2013.

The following repository can be used as a pipeline to generate choice sets using bioroute and analysing its output.


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

More details:

Prerequisites:

  (1) fork the repository bioroute_analysis (https://github.com/MatteoFel/bioroute_analysis.git) and
  
      (1.1) import bioroute-analysis as a Maven project to Eclipse
      (1.2) import bioroute as a Maven project to Eclipse (this is the adapted version of Gunnar Floetteroed's original bioroute repository https://github.com/gunnarfloetteroed/java.git)
  
  (2) download the python scripts in the BiorouteAnalysis folder.

Starting input: 

  (1) tsv-file containing all observations, i.e. od_id | origin | destination | link_id 1 | ... | link_id last
  
  (2) a template bioroute configuration file
  
Steps:

---> write bioroute configuration files for all observations (100 OD pairs per config file) with write_config_file.py

---> run bioroute using the code adapted from Gunnar Floetteroed's repository; the output are xml-files containing all routes
     (one further input that is need here is the network which needs to be stored in the correct folder)

---> use xml_to_csv.py to convert the routes to a csv format for the analysis

---> run postprocessing_analysis_input_files.py

---> run the analysis using RunAnalysisBioroute... .java

---> run postprocessing_analysis_output_files.py

  
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

If you use this as an input for an (Expanded) Path Size Logit Model consider the following two references:

(1) Frejinger, E., Bierlaire, M., Ben-Akiva, M., 2009. Sampling of alternatives for route choice modeling. Transportation Research Part B 43 (10), 984–994.

(2) Sobhani, A., Aliabadi, H.A., Farooq, B., 2019. Metropolis-Hasting based Expanded Path Size Logit model for cyclists’ route choice using GPS data. International journal of transportation science and technology 8 (2), 161-175.


In particular, the main difference to other approaches is the inclusion of an additional correction term defined by the frequency k_i (i.e. the number of times a certain alternative was drawn) and the normalised weights b(i) (which are supposed to be proportional to the stationary probability). The utility function should read

U_i = V_i + beta_ps * PS_i + ln(k_i/b(i))

where V_i is the deterministic part of the utility function, PS_i is the path size correction term and ln(k_i/b(i)) is a sampling correction term.

As a small remark (so that I don't forget), in reference (1) this term comes as ln(k_i/q(i)) where q(i) is the stationary probability of state i. Since the assumption is that

q(i) = a * b(i) => ln(k_i/q(i)) = ln(k_i/(a * b(i)) = ln(k_i) - ln(a) - ln(b(i))

and -ln(a) is a constant that appears in the utility function for each of the alternatives, and can thus be omitted. The utilities may therefore be specified as above.

Unfortunately, I am not sure I understand the expanded path size term just yet.
