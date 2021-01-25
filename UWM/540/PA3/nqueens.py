import random

def succ(state, static_x, static_y):
    if state[static_x] != static_y:
        return []
    
    ret = []
    for i in range(len(state)):
        if i == static_x:
            continue
        
        # Move down
        if state[i] != 0:
            new_state = state.copy()
            new_state[i] -= 1
            ret.append(new_state)

        # Move up
        if state[i] != len(state) - 1:
            new_state = state.copy()
            new_state[i] += 1
            ret.append(new_state)

    ret.sort()
    return ret

def f(state):
    score = 0

    for victim in range(len(state)):
        for attacker in range(len(state)):
            if victim == attacker:
                continue

            # Same row,
            if state[victim] == state[attacker]:
                score += 1
                break

            # Same diagonal (difference in x == difference in y)
            if abs(victim - attacker) == abs(state[victim] - state[attacker]):
                score += 1
                break

    return score

def choose_next(curr, static_x, static_y):
    succs = succ(curr, static_x, static_y)

    if len(succs) == 0:
        return None

    succs.append(curr.copy())

    succs_n_score = list(map(lambda state: (f(state), state), succs))
    succs_n_score.sort()

    return succs_n_score[0][1]

def n_queens(initial_state, static_x, static_y, print_path=True):
    prev_score = None
    curr_score = None
    state = initial_state

    while True:
        prev_score = curr_score
        curr_score = f(state)

        if print_path:
            print("%s - f=%d" % (state, curr_score))

        if prev_score == curr_score or curr_score == 0:
            return state

        state = choose_next(state, static_x, static_y)

def n_queens_restart(n, k, static_x, static_y):
    random.seed(1)

    for _ in range(k):
        state = []
        for i in range(n):
            if i == static_x:
                state.append(static_y)
            else:
                state.append(random.randint(0,n))
        
        queens_result = n_queens(state, static_x, static_y, print_path=False)
        score = f(queens_result)

        print("%s - f=%d" % (queens_result, score))

        if score == 0:
            return
