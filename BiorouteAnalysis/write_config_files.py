import xml.etree.ElementTree as ET
import pandas as pd

df_chosen = pd.read_csv("/Users/matteofelder/Documents/IVT/bike_zurich/greater_zurich/"
                         "ebike/chosen_routes/chosen_routes_ebike.tsv", sep='\t')

# df_chosen = pd.read_csv("/Users/matteofelder/Documents/IVT/ped_zurich/chosen_routes/chosen_routes_new_clean.tsv",
#                        sep='\t')

for i in range(1,11):
    df_temp = df_chosen.iloc[(i-1) * 100:i * 100, :]
    df_temp.to_csv("/Users/matteofelder/Documents/IVT/bioroute/zurich_ebike/observed/observed_" +
                   str((i-1)*100) + "_" + str(i*100) + ".tsv", sep='\t', index=False)

    main_config = ET.parse("/Users/matteofelder/Documents/IVT/bioroute/zurich_ebike/config_files/config_template.xml")

    root = main_config.getroot()

    route_ids = df_temp['route_id'].tolist()
    for route_id in route_ids:
        origin = df_chosen[df_chosen['route_id'] == route_id]['origin'].values
        destination = df_chosen[df_chosen['route_id'] == route_id]['destination'].values
        odpairs = root.findall('odpairs')[0]
        odpair = ET.SubElement(odpairs, "odpair")
        orig = ET.SubElement(odpair, "origin", value=str(origin[0]))
        dest = ET.SubElement(odpair, "destination", value=str(destination[0]))
        od_id = ET.SubElement(odpair, "id", value=str(route_id))

        pathwriter = root.findall('pathwriter')[0]
        filename = pathwriter.findall('filename')[0]
        filename.attrib['value'] = 'paths_' + str((i-1)*100) + "_" + str(i*100) + '.xml'

    ET.indent(main_config, space="\t", level=0)
    with open('/Users/matteofelder/Documents/IVT/bioroute/zurich_ebike/config_files/config_' + str((i-1)*100) + "_" +
              str(i*100) + '.xml', 'wb') as f:
        main_config.write(f, 'utf-8')




