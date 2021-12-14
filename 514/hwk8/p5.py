import numpy as np

beta = 0.5
gamma = 0.25

x0 = np.array([0.95, 0.05, 0])

def f(x):
    assert x.shape == (3,)
    dS = -beta * x[0] * x[1]
    dR = gamma * x[1]
    dI = -dS - dR
    return np.array([dS,dI,dR])

def approxS(N):
    x = x0.copy()
    h = 40.0/N

    for t in range(N):
        xp = x + h * f(x)
        x = x + (h/2) * (f(x) + f(xp))
    
    return x[0]

def main():
    print ((approxS(4000) - approxS(8000))/((approxS(8000)) - approxS(16000)))