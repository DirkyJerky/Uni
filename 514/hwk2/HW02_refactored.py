# Starting code for HW 2. Add to this!
import numpy as np
from matplotlib import pyplot as plt

def go(f, fprime):
    tol = 1e-12 # tolerance

    xk = 0 # Initial guess x_k

    residuals_newton = [np.abs(f(xk))]

    itnum_newton = 0 # iteration counter
    success = True # Assume success unless we fail!

    while np.abs(f(xk))>tol:
        xk = xk-f(xk)/fprime(xk)
            
        itnum_newton += 1 # Clean version of itnum = itnum + 1. Works with subtraction/multiplication too!
        residuals_newton.append(np.abs(f(xk)))

        if itnum_newton>10000:
            print('Something is wrong, too many iterations, let\'s break out of this loop...!')
            success = False
            break

    
    xk = 0.1 # Initial guess x_k
    xkm1 = 0 # x_{k-1}

    residuals_secant = [np.abs(f(xkm1)), np.abs(f(xk))]

    itnum_secant = 1 # iteration counter
    success = True # Assume success unless we fail!

    while np.abs(f(xk))>tol:
        old_xk = xk
        xk -= f(xk) * (xk - xkm1) / (f(xk) - f(xkm1))
        xkm1 = old_xk
            
        itnum_secant += 1 # Clean version of itnum = itnum + 1. Works with subtraction/multiplication too!
        residuals_secant.append(np.abs(f(xk)))

        if itnum_secant>10000:
            print('Something is wrong, too many iterations, let\'s break out of this loop...!')
            success = False
            break

    if success:
        # Plotting using matplotlib, imported at the top of the code.
        plt.plot(np.arange(itnum_newton+1),residuals_newton,'o', label='Newton')
        plt.plot(np.arange(itnum_secant+1),residuals_secant,'o', label='Secant')
        plt.xlabel('$k$')
        plt.ylabel('$r_k := |f(x_k)|$')
        plt.yscale("log")
        plt.title('Root-finding comparison')
        plt.legend()
        plt.show()

go(lambda x: x-np.cos(x), lambda x: 1 + np.sin(x))