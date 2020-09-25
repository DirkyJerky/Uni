def fill(state, max, which):
    new_state = state[:]
    new_state[which] = max[which]
    return new_state

def empty(state, max, which):
    new_state = state[:]
    new_state[which] = 0
    return new_state

def xfer(state, max, source, dest):
    new_state = state[:]

    dest_empty = max[dest] - state[dest]
    transfer_amount = min(state[source], dest_empty)
    new_state[source] -= transfer_amount
    new_state[dest] += transfer_amount

    return new_state

def succ(state, max):
    ret = []

    ret.append(fill(state, max, 0))
    ret.append(fill(state, max, 1))

    ret.append(empty(state, max, 0))
    ret.append(empty(state, max, 1))

    ret.append(xfer(state, max, 0, 1))
    ret.append(xfer(state, max, 1, 0))

    return [list(x) for x in (set([tuple(y) for y in ret]))] # Remove duplicates (list of lists -> set of tuples -> list of lists)