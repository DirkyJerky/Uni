from numpy import *
import matplotlib.pyplot as plt

# Comments like so

T=1 # Final time
N=1000 # Number of timesteps
h=1.0*T/N

t=linspace(0.0,T,N) # N uniformly spaced points (N-1 intervals)

# Define y' = f(y)
def f(y):
    f = 5.0*y*(1.0-y)
    return f

y=zeros(N) # initialize an array
y[0]=2.0 # initial value, index starts at 0

for j in arange(0,N-1):
    y[j+1]=y[j]+h*f(y[j]) # indent the loop

# By the end of the course you will not be very impressed with this method. 
# You might even try to keep other people from using it...

# Plotting
plt.plot(t,y,'r-',linewidth=2) 
plt.show()
