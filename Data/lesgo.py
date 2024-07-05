
import csv
import re
from datetime import datetime

# def convert_to_rfc3339(timestamp):
#     dt = datetime.strptime(timestamp, "%Y-%m-%d %H:%M:%S")
#     return dt.isoformat() + 'Z'

oldfile = input("enter name of old file ")
newfile = input("enter name of new file ")
measurement = input("enter the metrics being measured in data: ")
node = input("node ")

w = open(newfile,'w')
w.write("#datatype measurement,dateTime,long,tag\nrec,time,val,hostNode\n")

with open(oldfile, mode='r') as file:
    csv_reader = csv.reader(file)
    header = next(csv_reader)  # Skip the header row if your CSV has one
    i = 0
    for row in csv_reader:
        i += 1
        if i <= 1 : continue
        v = row
#         v[0] = convert_to_rfc3339(v[0])
        v = [measurement] + v + [node]
        v[1] = v[1] + "0"*(19-len(v[1]))
        w.write(','.join(v) + "\n" )

