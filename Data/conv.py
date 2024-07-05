import csv
import re

# Path to the CSV file
inpfiles = ["Explore-data-2024-06-21 14_23_11.csv"]
for x in range(0,len(inpfiles)):
    oldfile = inpfiles[x]

    # Initialize variables
    measurement = None
    hostNode = None

    # Define the regular expression pattern to extract measurement and host node
    pattern = re.compile(r'(.+)\s\{host:\s(.+)\}')

    # Read the CSV file
    with open(oldfile, 'r') as csv_file:
        reader = csv.reader(csv_file)

        # Read the header
        header = next(reader)

        # Extract measurement and hostNode from the header
        for col in header:
            match = pattern.match(col)
            if match:
                measurement = match.group(1).strip()
                hostNode = match.group(2).strip()
                break

    w = open(measurement+hostNode+".csv",'w')
    w.write("#datatype measurement,dateTime,long,tag\nrecord,time,val,hostNode\n")

    with open(oldfile, mode='r') as file:
        csv_reader = csv.reader(file)
        header = next(csv_reader)  # Skip the header row if your CSV has one
        i = 0
        for row in csv_reader:
            i += 1
            if i <= 1 : continue
            v = row
    #         v[0] = convert_to_rfc3339(v[0])
            v = [measurement] + v + [hostNode]
            v[1] = v[1] + "0"*(19-len(v[1]))
            w.write(','.join(v) + "\n" )

# Print the extracted values
# print("Measurement:", measurement)
# print("Host Node:", hostNode)
