import numpy as np
from matplotlib import pyplot as plt

def rk4(f, y0, T, N):
    # f(t_n, y_n) -> y'_n
    # y0 = Initial value
    # T = Final value of t, t varies from 0 to T inclusive
    # N = number of steps
    y = np.array(y0, dtype=float)
    h = float(T)/N
    ts = np.linspace(0, T, N+1)

    out = np.empty(shape=(N+1,y.shape[0]), dtype=float)

    for i, t in enumerate(ts[:-1]):
        out[i] = y
        k1 = f(t, y)
        k2 = f(t+0.5*h, y + 0.5*h*k1)
        k3 = f(t+0.5*h, y + 0.5*h*k2)
        k4 = f(t+h, y + h*k3)
        y += h* 1/6.0 * (k1 + k2+k2 + k3+k3 + k4)
    
    out[N] = y

    return out

def p3a():
    f = lambda t,y: y
    y0 = [1]
    T = 1

    return (np.e - rk4(f,y0,T,10)[10,0])/(np.e - rk4(f,y0,T,20)[20,0])

def p3b():
    f = lambda t,y: y
    y0 = [1]
    T = 1

    for n in range(1,100):
        approx = rk4(f, y0, T, n)[n,0]
        if str(approx)[:7] == '2.71828':
            return f'Step size N = {n} returns y_N = {approx}'

beta = 0.5 # S->I growth
gamma = 0.25 # I->R growth
rho1 = 0.01 # Group 1->2 growth
rho2 = 0.01 # Group 2->1 growth

y0 = np.array([.95,.05,0,1,0,0])
T = 60

def f(t,Phi): # f(t,y) = y'
    S1, I1, R1, S2, I2, R2 = Phi # Extract pre-step values
    f = np.zeros(6) # Output array
    f[0] = -beta*S1*I1-rho1*S1+rho2*S2          # New S1
    f[1] = beta*S1*I1-gamma*I1-rho1*I1+rho2*I2  # New I1
    f[2] = gamma*I1-rho1*R1+rho2*R2             # New R1
    f[3] = -beta*S2*I2-rho2*S2+rho1*S1          # New S2
    f[4] = beta*S2*I2-gamma*I2-rho2*I2+rho1*I1  # New I2
    f[5] = gamma*I2-rho2*R2+rho1*R1             # New R2
    return f   # Return new values

def p5d():
    N25 = rk4(f, y0, T, 25)[25,:]
    N50 = rk4(f, y0, T, 50)[50,:]
    N100 = rk4(f, y0, T, 100)[100,:]

    for i, label in enumerate(['S_1','I_1','R_1','S_2','I_2','R_2']):
        print(f'Ratio for {label} = {(N50[i] - N25[i])/(N100[i] - N50[i])}')

def p5efgh(r1, r2):
    global rho1, rho2

    rho1 = r1
    rho2 = r2

    arr = rk4(f, y0, T, 100)
    ts = np.linspace(0,T,101) # All t values

    plt.figure()  # Start a plot
    plt.plot(ts,arr[:,0],'-',linewidth=1)   # Plot S1
    plt.plot(ts,arr[:,1],'-',linewidth=1)   # Plot I1
    plt.plot(ts,arr[:,2],'-',linewidth=1)   # Plot R1
    plt.plot(ts,arr[:,3],'--',linewidth=1)   # Plot S2
    plt.plot(ts,arr[:,4],'--',linewidth=1)   # Plot I2
    plt.plot(ts,arr[:,5],'--',linewidth=1)   # Plot R2
    plt.legend(['S_1','I_1','R_1','S_2','I_2','R_2'])  # Label each plot accordingly
    plt.xlabel('t') # Label x axis
    plt.show() # Finish and show the plot



def ab2(f, y0, y1, T, N):
    # f(t_n, y_n) -> y'_n
    # y0,y1 = Initial values
    # T = Final value of t, t varies from 0 to T inclusive
    # N = number of steps
    h = float(T)/N
    ts = np.linspace(0, T, N+1)

    out = np.empty(shape=(N+1,y0.shape[0]), dtype=float)
    out[0] = np.array(y0, dtype=float)
    out[1] = np.array(y1, dtype=float)

    for i in range(2,N+1):
        tm1 = ts[i-1]
        tm2 = ts[i-2]
        ym1 = out[i-1]
        ym2 = out[i-2]

        out[i] = ym1 + h*(1.5*f(tm1,ym1) - 0.5*f(tm2, ym2))

    return out

def p5i():
    y1 = y0
    N25 = ab2(f, y0, y1, T, 25)[25,:]
    N50 = ab2(f, y0, y1, T, 50)[50,:]
    N100 = ab2(f, y0, y1, T, 100)[100,:]

    for i, label in enumerate(['S_1','I_1','R_1','S_2','I_2','R_2']):
        print(f'Ratio for {label} = {(N50[i] - N25[i])/(N100[i] - N50[i])}')