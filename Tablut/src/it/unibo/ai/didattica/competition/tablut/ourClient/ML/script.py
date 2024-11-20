import json

"""
    Simple script to clean up the json dataset of states
"""


raw_dataset_path = "./all_board_states.json"
dataset_path = "./dataset.json"


def checkNonFinal(str):

    if not 'W' in str:
        return False

    if not 'K' in str:
        return False

    if not 'B' in str:
        return False
    
    escaping = [1,2,6,7, 9,18,54,63, 17,26,62,71, 73,74,78,79]

    pos = str.index('K')

    if pos in escaping:
        return False
    
    return True


with open(raw_dataset_path, 'r') as rd:
    data = json.load(rd)
    print(len(data))
    new_data = []

    i = 0
    for state in data:
        i += 1

        state_str = state.replace("\n", "")

        if checkNonFinal(state_str):
            new_data.append(state_str)

    new_data = list(dict.fromkeys(new_data))
    print(len(new_data))
    json_obj = json.dumps(new_data, indent=4)

    with open(dataset_path, 'w') as d:
        d.write(json_obj)