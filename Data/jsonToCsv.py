import json
import csv

# Define the input JSON file path and output CSV file path
json_file_path = "/Users/chatraraj.regmi/Desktop/FirstProject/Data/OldGc02_23_34.json"  # Replace with your JSON file path
csv_file_path = "path/to/your/output.csv"  # Replace with your desired CSV output path

# Function to extract required data and write to CSV
def json_to_csv(json_file_path, csv_file_path):
    with open(json_file_path, 'r') as json_file:
        data = json.load(json_file)



        # Write CSV header
#         csv_writer.writerow(["diskUsage", "timestamp", "value", "hostNode", "disk"])

        # Iterate through each object in the root array
        for obj in data:
            host_node = obj["schema"]["fields"][1]["labels"]["host"]
            full_name = obj["schema"]["name"]
            measurement = full_name.split(" ")[0]
            timestamps = obj["data"]["values"][0]
            values = obj["data"]["values"][1]
            csv_file_path= measurement+"_"+host_node+".csv"

#             with open(csv_file_path, 'w', newline='') as csv_file:
#                 csv_writer = csv.writer(csv_file)
#                 csv_writer.writerow(["#datatype", "measurement","dateTime","long","tag","tag"])
#                 csv_writer.writerow(["record","time","val","hostNode","disk"])
            w = open(csv_file_path,'w')

            w.write("#datatype measurement,dateTime,long,tag,tag,tag\nrecord,time,val,hostNode,clusterName")

            # Write each row to the CSV file
            for timestamp, value in zip(timestamps, values):
                timestamp_ns = timestamp * 1000000  # Convert to nanosecond precision
                v="\n"+measurement+","+str(timestamp_ns)+","+str(value)+","+host_node+","+","+"paid1-es7"
                w.write(v)

# Call the function to perform the conversion
json_to_csv(json_file_path, csv_file_path)
