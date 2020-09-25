def manhattan_distance(dp1, dp2):
    indicies = ['PRCP', 'TMAX', 'TMIN']
    sum = 0
    for i in indicies:
        sum += abs(dp1[i] - dp2[i])
    
    return sum

def read_dataset(filename):
    with open(filename, 'r') as f:
        entries = []
        for line in f:
            splits = line.split()
            entries.append({'DATE': splits[0], 'PRCP': float(splits[1]), 'TMAX': int(splits[2]), 'TMIN': int(splits[3]), 'RAIN': splits[4]})
        return entries

def majority_vote(nearest_neighbors):
    size = len(nearest_neighbors)
    num_trues = 0
    for neighbor in nearest_neighbors:
        if (neighbor['RAIN'] == 'TRUE'):
            num_trues += 1
    
    if ( (num_trues / size) >= 0.5):
        return 'TRUE'
    else:
        return 'FALSE'

def k_nearest_neighbors(filename, test_point, k, year_interval):
    entries = read_dataset(filename)

    def in_range(date1, date2, year_interval):
        year1 = int(date1[:4])
        year2 = int(date2[:4])
        return abs(year2 - year1) < year_interval
    
    filtered_entries = list(filter(lambda el: in_range(el['DATE'], test_point['DATE'], year_interval), entries))

    sorted_entries = sorted(filtered_entries, key=lambda el: manhattan_distance(test_point, el))
    
    k_entries = sorted_entries[0:k]

    return majority_vote(k_entries)