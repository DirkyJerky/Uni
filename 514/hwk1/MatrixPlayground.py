import numpy as np
from numpy import log, exp
from scipy import linalg as la
import matplotlib.pyplot as plt
plt.ion() # Turn on interactive mode for plotting
from scipy import stats

# Build two matrices...

A = np.zeros((4,4))
B = np.zeros((4,4))

# ... and fill in column by column...

A[0,:]=[1,2,3,0]
A[1,:]=[2,0,2,2]
A[2,:]=[1,-2,1,3]
A[3,:]=[5,1,7,8]

# ... or maybe using some pattern...

for j in np.arange(1,3):
    B[j,j]=2
    B[j,j+1]=-1
    B[j,j-1]=-1
    
B[0,:]=[2,-1,0,0]
B[3,:]=[0,0,-1,2]

# Define a special print function!

def pr(a,b):
    # A personalized print function which adds extra spaces for a cleaner presentation
    print()
    print(a,b)
    print()

# Let's test some matrix operations:

pr('A = ', A )
pr('B = ', B )
pr('|A| = ', la.det(A) )
pr('AB = ', np.dot(A,B) )
pr('*Element-wise* A*B = ', A*B )
pr('A^T = ', A.T)
pr('A^{-1} = ', la.inv(A) )
pr('A^{-1}A = ', np.dot(la.inv(A),A) )

b=np.ones((4,1))
x=la.solve(A,b)
print('Solution to Ax=[1,1,1,1]^T:')
pr('x = ', x )
pr('Ax = ', np.dot(A,x) )


# Now let's time out how long it takes to perform an LU Decomposition as a function of the matrix size.

import time
Nstart = 200
Nfinal = 1500
Ns = 20
sizes = np.zeros((int((Nfinal-Nstart)/Ns),))
times = np.zeros((int((Nfinal-Nstart)/Ns),))

step = 0
print('Running...')
for N in np.arange(Nstart,Nfinal,Ns):
    
    A = np.random.rand(N,N)

    start = time.time()    
    P,L,U = la.lu(A)
    end  = time.time()

    sizes[step] = N
    times[step] = end - start
    step += 1
print('Completed.')

slope, intercept, r_value, p_value, std_err = stats.linregress(log(sizes[-5:]),log(times[-5:]))
pr('slope = ', slope)

# Plotting results
fig = plt.figure(figsize=(8.5, 8.5))
plt.plot(sizes,times,'o')
plt.plot(sizes,exp(intercept + slope*log(sizes)),'r-')
plt.xlabel('N')
plt.ylabel('Time [s]')
plt.savefig('./Random.png')

print('Figure saved as ./Random.png.')
print('Have a nice day.')
