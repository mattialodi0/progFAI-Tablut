# import json
# import random

# def sample_states(input_json_file, output_json_file, sample_size):
#     # Step 1: Read the input JSON file
#     with open(input_json_file, 'r') as infile:
#         data = json.load(infile)

#     if isinstance(data, dict):
#         states = data.get('x', [])  # Access 'states' if it's inside a dictionary
#     else:
#         states = data 

#     total_states = len(states)
#     if total_states < sample_size:
#         raise ValueError(f"Cannot sample {sample_size} states because only {total_states} states are available.")
#     # Step 2: Randomly sample 'sample_size' states from the dataset
#     sampled_states = random.sample(states, sample_size)
    
#     # Step 3: Write the sampled states to a new JSON file
#     with open(output_json_file, 'w') as outfile:
#         json.dump(sampled_states, outfile, indent=4)
    
#     print(f"Sampled {sample_size} states and saved them to {output_json_file}")

# Example usage
# input_json_file = 'dataset.json'  # Replace with the path to your original JSON file
# output_json_file = 'sampled_states.json'   # Output file for the sampled data
# sample_size = 5000

# sample_states(input_json_file, output_json_file, sample_size)



import json
import random

# Function to count the number of 'B' (Black) and 'W' (White) pieces
def count_pieces(state):
    black_count = state.count('B')
    white_count = state.count('W')
    return black_count, white_count

# Function to check if the King is encircled by Black pawns
def is_king_encircled(state):
    king_position = state.find('K')  # Assuming 'K' represents the King
    if king_position == -1:
        return False  # No King found

    # Check the adjacent squares for 'B' (Black pawns)
    board_size = len(state)  # Assuming a flat 1D representation of the board
    adjacent_positions = []
    
    # Determine the row and column of the King (assuming 5x5 board, index math applies)
    row, col = divmod(king_position, 9)
    
    # Define the 8 possible adjacent squares (considering board boundaries)
    directions = [(-1, 0), (1, 0), (0, -1), (0, 1), (-1, -1), (-1, 1), (1, -1), (1, 1)]
    
    for dr, dc in directions:
        r, c = row + dr, col + dc
        if 0 <= r < 9 and 0 <= c < 9:  # Check within bounds
            adjacent_position = r * 9 + c
            adjacent_positions.append(state[adjacent_position])
    
    # Check if all adjacent positions are 'B' (Black pawns)
    return any(pos == 'B' for pos in adjacent_positions)

# Function to categorize states into the different categories
def categorize_state(state):
    black_count, white_count = count_pieces(state)
    
    # Category 1: Black is at least twice the number of White
    if black_count == 2 * white_count:
        return "black_twice_white"
    
    # Category 2: Black is more than twice White
    if black_count > 2 * white_count:
        return "black_more_than_twice_white"
    
    # Category 3: White is at least half of Black
    if white_count >= 0.5 * black_count:
        return "white_half_black"
    
    return "other"  # Category for other cases

# Function to sample and balance the dataset
def sample_and_balance_states(input_json_file, probabilities_file, output_json_file, sample_size):
    with open(input_json_file, 'r') as infile:
        data = json.load(infile)
    
    states = data.get('x', [])
    print(f"Loaded {len(states)} states.")

    with open(probabilities_file, 'r') as infile:
        probabilities = json.load(infile)

    # Ensure the number of probabilities matches the number of states
    if len(states) != len(probabilities):
        print("Error: The number of states in dataset.json does not match the number of probabilities in y.json.")
        return

    # Initialize categories
    black_twice_white = []
    black_more_than_twice_white = []
    white_half_black = []
    king_encircled = []
    king_free = []

    # Classify each state
    for idx, state in enumerate(states):
        # Classify by Black/White ratio
        category = categorize_state(state)
        probability = probabilities[idx]

        state_with_prob = {'state': state, 'probability': probability}
        
        if category == "black_twice_white":
            black_twice_white.append(state_with_prob)
        elif category == "black_more_than_twice_white":
            black_more_than_twice_white.append(state_with_prob)
        elif category == "white_half_black":
            white_half_black.append(state_with_prob)
        
        # Classify based on King's position
        if is_king_encircled(state):
            king_encircled.append(state_with_prob)
        else:
            king_free.append(state_with_prob)
    
    print(f"black_twice_white: {len(black_twice_white)}")
    print(f"black_more_than_twice_white: {len(black_more_than_twice_white)}")
    print(f"white_half_black: {len(white_half_black)}")
    print(f"king_encircled: {len(king_encircled)}")
    print(f"king_free: {len(king_free)}")

    # Balance the categories to have equal number of states in each category
    min_count = min(len(black_twice_white), len(black_more_than_twice_white),
                   len(white_half_black), len(king_encircled), len(king_free), sample_size)

    # Sample the states to make them equal in number
    sampled_black_twice_white = random.sample(black_twice_white, min_count)
    sampled_black_more_than_twice_white = random.sample(black_more_than_twice_white, min_count)
    sampled_white_half_black = random.sample(white_half_black, min_count)
    sampled_king_encircled = random.sample(king_encircled, min_count)
    sampled_king_free = random.sample(king_free, min_count)

    # Combine all the sampled states
    balanced_states = (sampled_black_twice_white + sampled_black_more_than_twice_white +
                       sampled_white_half_black + sampled_king_encircled + sampled_king_free)
    
    # Shuffle the final balanced dataset
    random.shuffle(balanced_states)

    # Write the sampled and balanced states to the output JSON file
    with open(output_json_file, 'w') as outfile:
        json.dump(balanced_states, outfile, indent=4)
    
    print(f"Created a balanced dataset with {len(balanced_states)} states and saved it to {output_json_file}")

# Example usage
input_json_file = 'dataset.json'  # Path to your input JSON file
output_json_file = 'balanced_sampled_states.json'  # Path to your output JSON file
probabilities_file = 'dataset_y.json'
sample_size = 1500

sample_and_balance_states(input_json_file, probabilities_file, output_json_file, sample_size)
