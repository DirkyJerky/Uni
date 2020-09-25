import numpy
import heapq

# This program works for any 2-dimensional puzzle! (3x3, 4x4, 3x7, 1x1 etc)
# Try it out!  Just set the goal state to a 2d array of same size as the input
# Some may take a while

GOAL = numpy.array([[1,2,3],[4,5,6],[7,8,0]])
# GOAL = numpy.array([[1,2],[3,0]])
# GOAL = numpy.array([[1,2,3,4],[5,6,7,8],[9,10,11,12],[13,14,15,0]])


def heuristic(state):

    def goal_point_idx(num):
        with numpy.nditer(GOAL, flags=['multi_index']) as goal_iter:
            for x in goal_iter:
                if x == num:
                    return goal_iter.multi_index

    score = 0
    with numpy.nditer(state, flags=['multi_index']) as state_iter:
        for x in state_iter:
            if x == 0:
                continue

            goal_idx = goal_point_idx(x)
            curr_idx = state_iter.multi_index

            score += abs(goal_idx[0] - curr_idx[0])
            score += abs(goal_idx[1] - curr_idx[1])

    return score


def succs(state):
    zero_idx = numpy.unravel_index(numpy.argmin(state), state.shape) # Source:  Numpy docs

    ret = []

    relative_idxs = [(-1,0), (0, -1), (1,0), (0,1)]

    for relative_idx in relative_idxs:
        swap_idx = tuple(map(sum, zip(zero_idx, relative_idx)))

        if not 0 <= swap_idx[0] < state.shape[0]:
            continue
        if not 0 <= swap_idx[1] < state.shape[1]:
            continue

        new_state = numpy.copy(state)
        temp = new_state[swap_idx]
        new_state[swap_idx] = new_state[zero_idx]
        new_state[zero_idx] = temp

        ret.append(new_state)

    ret.sort(key= lambda xs: list(xs.flatten()))  # Sort like it was a list of 1d python arrays
    return ret

def to_arr(xs):
    return numpy.array(xs).reshape(GOAL.shape)

def from_arr(arr):
    return list(arr.flatten())

# Pretty print
def _print_succ(state):
    for new_state in succs(to_arr(state)):
        for row in new_state:
            print(*row, sep=' ')
        print("h =", heuristic(new_state))
        print()

def print_succ(state):
    for new_state in succs(to_arr(state)):
        print(from_arr(new_state), " h=", heuristic(new_state), sep="")

def solve(state):
    pq = [] # [(f=g+h, state, (g,h))] 
    pointer_dict = {} # Key = state, Value = (parent state, state's g cost)

    h = heuristic(to_arr(state))
    heapq.heappush(pq, (h, state, (0, h)))

    next = None # Keep a reference for reconstructing goal path later

    while pq:
        next = heapq.heappop(pq)
        # print("popped", next)

        if next[2][1] == 0:  # Break out if we are at the goal (iff h = 0)
            break

        for neigh_state in succs(to_arr(next[1])):
            neigh_cost = next[2][0] + 1
            key = tuple(from_arr(neigh_state))
            if (not key in pointer_dict) or (pointer_dict[key][1] > neigh_cost):
                new_h = heuristic(neigh_state)
                heapq.heappush(pq, (new_h + neigh_cost, from_arr(neigh_state), (neigh_cost, new_h)))
                pointer_dict[key] = (next[1], neigh_cost)
            

    # Reconstruct path
    out_lines = []

    final_state = next[1]

    if (not numpy.array_equal(to_arr(final_state), GOAL)):
        print("Goal not achievable")
        return


    while tuple(final_state) in pointer_dict and final_state != state:
        # print(final_state, "came from", pointer_dict[tuple(final_state)])
        out_lines.insert(0, "%s h=%d moves: %d" % (str(final_state), heuristic(to_arr(final_state)), pointer_dict[tuple(final_state)][1]))

        final_state = pointer_dict[tuple(final_state)][0]

    out_lines.insert(0, "%s h=%d moves: 0" % (str(state), h))


    for line in out_lines:
        print(line)

    return

