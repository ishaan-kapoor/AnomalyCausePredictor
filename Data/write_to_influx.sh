#!/bin/bash

# Assuming your CSV files are in the directory path/to/csv_files
csv_dir="OldGcMetric02_23"

# Iterate over each CSV file in the directory
for file in "$csv_dir"/*.csv; do
    influx write -b refined -f "$file"
done
