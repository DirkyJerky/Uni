import csv
import numpy as np
import sys
import math
from scipy.cluster.hierarchy import linkage

def load_data(filepath):
    with open(filepath, "r") as file:
        pokemons = list(csv.DictReader(file))
        for poke in pokemons:
            del poke['Generation']
            del poke['Legendary']

        return pokemons[:20]
        # return pokemons


def calculate_x_y(stats):
    attack_feature = int(stats['Attack']) + int(stats['Sp. Atk']) + int(stats['Speed'])
    defense_feature = int(stats['Defense']) + int(stats['Sp. Def']) + int(stats['HP'])

    return (attack_feature, defense_feature)

def hac(dataset):
    cluster_index = {}

    Z_data = np.empty((len(dataset)-1, 4))

    for i in range(len(dataset)):
        cluster_index[dataset[i]] = i

    def distance(dp1, dp2):
        return math.sqrt((dp1[0] - dp2[0]) ** 2 + (dp1[1] - dp2[1]) ** 2)

    for iteration in range(len(dataset)-1):
        cluster_with_min_dist_from = None
        cluster_with_min_dist_to = None
        min_dist = sys.maxsize

        for each_cluster_index in range(len(dataset) + iteration):
            for each_cluster in dataset:
                if cluster_index[each_cluster] != each_cluster_index:
                    continue

                for each_other_cluster in dataset:
                    if cluster_index[each_other_cluster] == each_cluster_index:
                        continue
                    
                    dist_between = distance(each_cluster, each_other_cluster)
                    if dist_between < min_dist:
                        cluster_with_min_dist_from = each_cluster
                        cluster_with_min_dist_to = each_other_cluster
                        min_dist = dist_between
        
        Z_data[iteration, 0] = cluster_index[cluster_with_min_dist_from]
        Z_data[iteration, 1] = cluster_index[cluster_with_min_dist_to]
        Z_data[iteration, 2] = min_dist

        num_in_new_cluster = 0

        for i in range(len(dataset)):
            cluster = dataset[i]
            if cluster_index[cluster] == Z_data[iteration, 0] or cluster_index[cluster] == Z_data[iteration, 1]:
                num_in_new_cluster += 1
                cluster_index[cluster] = len(dataset) + iteration

        Z_data[iteration, 3] = num_in_new_cluster
    
    return Z_data

if __name__ == "__main__":
    pokemons = load_data('Pokemon.csv')
    
    pokemons_xy = [calculate_x_y(poke) for poke in pokemons]

    scipy_correct = linkage(pokemons_xy)

    our_hac = hac(pokemons_xy)

    print('scipy_correct = ')
    print(scipy_correct)
    print('our_hac = ')
    print(our_hac)