import numpy as np
from matplotlib import pyplot as plt
import matplotlib.ticker as mtick

beta = 0.5
gamma = 0.25

x0 = np.array([0.95, 0.05, 0])

def f(x):
    assert x.shape == (3,)
    dS = -beta * x[0] * x[1]
    dR = gamma * x[1]
    dI = -dS - dR
    return np.array([dS,dI,dR])

def approx(N):
    x = x0.copy()
    h = 40.0/N

    out = np.empty(shape=(N+1,3))

    for i in range(N):
        out[i] = x
        x += h * f(x)
    
    out[N] = x

    return out

def p4a():
    global beta, gamma
    beta = 0.5
    gamma = 0.25

    print ((approx(4000)[-1,0] - approx(8000)[-1,0])/((approx(8000)[-1,0]) - approx(16000)[-1,0]))

def p4bc():
    N = 8000
    arr = approx(N)
    ts = np.linspace(0,40,N+1)

    plt.figure()
    plt.plot(ts, arr[:,0], '-', label='S(t)')
    plt.plot(ts, arr[:,1], '--', label='I(t)')
    plt.plot(ts, arr[:,2], ':', label='R(t)')
    plt.legend()
    plt.ylabel("Population")
    plt.xlabel("t")
    plt.gca().get_yaxis().set_major_formatter(mtick.PercentFormatter(1))
    plt.show()
    
def p4b():
    global gamma, beta
    beta = 1
    gamma = 0.5
    p4bc()
    
def p4c():
    global gamma, beta
    beta = 1
    gamma = 0.8
    p4bc()