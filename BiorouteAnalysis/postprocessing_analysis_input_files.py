import pandas as pd

df_result = pd.DataFrame([],columns=[])
df_result_observed = pd.DataFrame([],columns=[])

for i in range(1,10):
    df_observed = pd.read_csv("/Users/matteofelder/Documents/IVT/bioroute/zurich_ebike/observed/observed_" +
                              str((i-1)*100) + "_" + str(i*100) + ".tsv", sep='\t')
    df_result_observed = df_result_observed.append(df_observed, ignore_index=True)
    df_input = pd.read_csv("/Users/matteofelder/Documents/IVT/bioroute/zurich_ebike/analysis_input/input_"
                              + str((i-1)*100) + "_" + str(i*100) + ".tsv", sep='\t')
    df_result = df_result.append(df_input, ignore_index=True)

df_result_observed.to_csv("/Users/matteofelder/Documents/IVT/bioroute/zurich_ebike/observed/observed.tsv", sep='\t',
                          index=False)
df_result.to_csv("/Users/matteofelder/Documents/IVT/bioroute/zurich_ebike/analysis_input/analysis_input.tsv", sep='\t',
                 index=False)

print("stop")