import numpy as np
import numpy.linalg as la
from numpy import sin
from matplotlib import pyplot as plt
import os

# Prepare folders to collect temporary image files
os.system("mkdir temp_images/") 
os.system("rm temp_images/*.png")

T=10 # final simulation time
N=400 # number of timesteps
h=T/N # timestep size
t = np.linspace(0,T,N+1)

n = 7 # number of masses
L = 10.0 # Distance between walls

# Wall amplitudes
alpha_0 = 1.0
alpha_L = -1.0

# Wall frequencies
omega_0 = 1.0
omega_L = 1.0

print_counter = 0 # counts saved images
print_often = np.round(N/100) # how frequently to print images. Denomenator is # of images.

x = np.zeros((N+1,n)) # Mass positions
X = L/(n+1)*np.arange(1,n+1)
x[0] = X # initial positions

def plot(t,x,print_counter):
    fig = plt.figure()
    plt.plot(x,x*0,'bo')
    plt.plot(np.array([alpha_0*sin(omega_0*t),L+alpha_L*sin(omega_L*t)]),np.array([0,0]),'ks')
    d = (L+alpha_L)/10
    plt.axis([-d, L+d, -d, d])
    plt.title('t = '+str(round(t,2)))
    plt.savefig("temp_images/image" + str(print_counter).zfill(4) + ".png") 
    plt.close()

for j in np.arange(N):
    
    # Update positions
    x[j+1] = X+.5*sin(t[j])

    # Printing?
    if np.mod(j,print_often)==0:
        plot(t[j],x[j],print_counter)
        print_counter += 1

# Final trajectory plot:
plt.figure()
for i in np.arange(n):
    plt.plot(t,x[:,i],'-',linewidth=2)
plt.xlabel('t')
plt.ylabel('$x_j(t)$')
plt.savefig("trajectories.png") 
plt.close()

# Create movie file (will need to conda install ffmpeg first)
os.system("ffmpeg -y -framerate 30 -i temp_images/image%04d.png -c:v libx264 -vf \"scale=trunc(iw/2)*2:trunc(ih/2)*2\" -pix_fmt yuv420p movie.mp4")