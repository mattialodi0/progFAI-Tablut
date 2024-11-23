import json
import csv
 
 
with open('dataset_vector.json') as json_file:
    data = json.load(json_file)

states = data['state']
evals = data['eval']

assert len(states) == len(evals)

data_file = open('dataset_vector.csv', 'w')

csv_writer = csv.writer(data_file)

csv_writer.writerow(['state', 'eval'])
for i in range(len(states)):
    csv_writer.writerow([states[i], evals[i]])

data_file.close()