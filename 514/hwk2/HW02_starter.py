# Starting code for HW 2. Add to this!
import numpy as np
from matplotlib import pyplot as plt

method = 'Newton' # enter 'Newton' or 'secant' here.

# Function definitions:
def f(x):
    f = x-np.cos(x)
    return f

def fprime(x):
    return 1+np.sin(x)

# I just showed you two different ways to return function values...


tol = 1e-12 # tolerance
xk = 0 # Initial guess x_k
xkm1 = 0 # x_{k-1}

k = [0]
residuals = [np.abs(f(xk))]

itnum = 0 # iteration counter
success = True # Assume success unless we fail!

while np.abs(f(xk))>tol:

    if method == 'Newton':
        fp = fprime(xk)
        xk = xk-f(xk)/fp 
    elif method == 'secant':
        old_xk = xk
        xk -= f(xk) * (xk - xkm1) / (f(xk) - f(xkm1))
        xkm1 = old_xk
    else:
        print('No such method implemented here!')
        success = False
        break
    	
    itnum += 1 # Clean version of itnum = itnum + 1. Works with subtraction/multiplication too!
    k.append(itnum)
    residuals.append(np.abs(f(xk)))

    if itnum>10000:
        print('Something is wrong, too many iterations, let\'s break out of this loop...!')
        success = False
        break


if success:
    # Plotting using matplotlib, imported at the top of the code.
    plt.plot(k,residuals,'o')
    plt.xlabel('$k$')
    plt.ylabel('$r_k := |f(x_k)|$')
    plt.title('Root-finding comparison')
    plt.show()
