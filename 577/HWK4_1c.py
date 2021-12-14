import numpy as np
from numpy.core.numeric import Infinity
from matplotlib import pyplot as plt

g = 9.81
Y = 500
T = 100
m = 1.0
dt = 1

def E(y, dy):
    return 0.5*m*((dy*dy)/(dt*dt) - g*(2*y + dy))

def process():
    opts = np.zeros((Y,T))
    preds_y = np.zeros_like(opts)

    for t in np.arange(1,T):
        for y in np.arange(Y):
            pred = None
            min_E = np.infty
            for y_ in np.arange(Y):
                action = opts[y_, t-dt] + E(y_, y_-y)
                if (action < min_E):
                    min_E = action
                    pred = y_
            opts[y,t] = min_E
            preds_y[y,t] = pred

    return preds_y

def plot(preds):
    ys = [0]
    prev_y = 0

    for t in np.arange(T-1, 0, -1): # [T-1, T-2, ..., 1]
        prev_y = round(preds[prev_y, t])
        ys.append(prev_y)
    
    ys.reverse()
    ys = np.array(ys)
    i = np.argwhere(ys == Y-1)[-1][0]
    return ys[i:]

def main():
    preds = process()
    ys = plot(preds)
    plt.plot(np.arange(len(ys)), ys)
    plt.title(str(ys))
    plt.show()