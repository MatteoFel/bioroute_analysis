import pandas as pd

df_analysis = pd.read_csv("/Users/matteofelder/Documents/IVT/bioroute/zurich_ped/biogeme/biogeme_bioroute.csv")
df_analysis['CHOICE'] = 1
df_analysis = df_analysis.fillna(0)
df_analysis.to_csv("/Users/matteofelder/Documents/IVT/bioroute/zurich_ped/biogeme/biogeme_bioroute_clean.csv", index=False)

print("stop")