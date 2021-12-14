import numpy as np
from matplotlib import pyplot as plt


T=60 # final simulation time
N=100 # Step count
h=T/N # Step size

beta = 0.5 # S->I growth
gamma = 0.25 # I->R growth
rho1 = 0.01 # Group 1->2 growth
rho2 = 0.01 # Group 2->1 growth

Phi = np.zeros((N+1,6)) # Array for storing all step results
t = np.linspace(0,T,N+1) # All t values

Phi[0]=np.array([.95,.05,0,1,0,0]) # Initial value
S1, I1, R1, S2, I2, R2 = Phi[0] # ...

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

for j in np.arange(0,N):  # For each step
    k1 = f(t[j],Phi[j])   # Compute k1
    k2 = f(t[j]+h/2,Phi[j]+h/2*k1) # Compute k2
    k3 = f(t[j]+h/2,Phi[j]+h/2*k2) # Compute k3
    k4 = f(t[j]+h,Phi[j]+h*k3) # Compute k4
    k = (k1+2*k2+2*k3+k4)/6 # Weighted average of ks
    Phi[j+1] = Phi[j] + h*k # Perform the step



plt.figure()  # Start a plot
plt.plot(t,Phi[:,0],'-',linewidth=1)   # Plot S1
plt.plot(t,Phi[:,1],'-',linewidth=1)   # Plot I1
plt.plot(t,Phi[:,2],'-',linewidth=1)   # Plot R1
plt.plot(t,Phi[:,3],'--',linewidth=1)   # Plot S2
plt.plot(t,Phi[:,4],'--',linewidth=1)   # Plot I2
plt.plot(t,Phi[:,5],'--',linewidth=1)   # Plot R2
plt.legend(['S_1','I_1','R_1','S_2','I_2','R_2'])  # Label each plot accordingly
plt.xlabel('t') # Label x axis
plt.show() # Finish and show the plot