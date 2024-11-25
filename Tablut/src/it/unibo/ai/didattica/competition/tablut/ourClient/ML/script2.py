import json

"""
    Simple script to clean up the json dataset of states
"""


raw_dataset_path = "./dataset_x.json"
dataset_path = "./dataset_x_vector.json"


with open(raw_dataset_path, 'r') as rd:
    data = json.load(rd)
    print(len(data))
    new_data = []

    i = 0
    for state in data:
        i += 1

        state_str = state.replace("O", "0")
        
        state_str = state_str.replace("O", "0")
        state_str = state_str.replace("T", "0")
        state_str = state_str.replace("W", "1")
        state_str = state_str.replace("K", "2")
        state_str = state_str.replace("B", "3")
        
        state_vector = [int(x) for x in  list(state_str)]
        state_vector = [x if x != 3 else -1 for x in state_vector]

        new_data.append(str(state_vector))

    print(len(new_data))
    json_obj = json.dumps(new_data, indent=0)


    str_to_write = ''
    skip = 0
    for char in json_obj:
        if (skip == 1) and ((char == '\n') or (char == ' ')):
            pass
        elif (char == '['):
            skip = 1
            str_to_write = str_to_write + char
        elif (char == ']'):
            skip = 0
            str_to_write = str_to_write + char
        else:
            str_to_write = str_to_write + char

    with open(dataset_path, 'w') as d:
        d.write(str_to_write)
