import xml.etree.ElementTree as ET
import pandas as pd

for i in range(1,10):
    df_chosen = pd.read_csv("/Users/matteofelder/Documents/IVT/bioroute/zurich_ebike/observed/observed_" +
                            str((i-1)*100) + "_" + str(i*100) + ".tsv", sep='\t')

    tree = ET.parse("/Users/matteofelder/Documents/IVT/bioroute/zurich_ebike/paths/paths_" +
                            str((i-1)*100) + "_" + str(i*100) + ".xml")

    root = tree.getroot()

    df_result = pd.DataFrame([], columns=[])

    for elem in root.findall('odpair'):
        from_node = elem.get("from")
        to_node = elem.get("to")
        paths = elem.findall("path")
        frequencies = elem.findall("frequencies")
        df_temp_freq = pd.DataFrame([], columns=[])
        for frequency in frequencies:
            freq_ids = list(filter(None, frequency.get("id").split(" ")))
            freqs = list(filter(None, frequency.get("freq").split(" ")))
            df_temp_freq['path_id'] = freq_ids
            df_temp_freq['frequency'] = freqs
        j = 1
        df_temp = pd.DataFrame([], columns=[])
        df_temp['id'] = [from_node + '_' + to_node]
        df_temp['origin'] = [from_node]
        df_temp['destination'] = [to_node]
        for path in paths:
            links = path.get("links")
            path_id = path.get("id")
            freq = df_temp_freq[df_temp_freq['path_id'] == path_id]['frequency'].tolist()[0]
            links = links + " " + str(path_id) + " " + str(freq)
            df_temp['links_' + str(j)] = [str(links)]
            j += 1
            links = []
        df_result = df_result.append(df_temp, ignore_index=True)

    df_result.insert(0, 'route_id', df_chosen['route_id'].tolist())

    df_result.to_csv('/Users/matteofelder/Documents/IVT/bioroute/zurich_ebike/analysis_input/input_' +
                            str((i-1)*100) + "_" + str(i*100) + ".tsv", sep='\t', index=False)



