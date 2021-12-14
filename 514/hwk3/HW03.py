# Starting code for HW 2. Add to this!
import numpy as np
import scipy.linalg as linalg

tol = 1e-12 # tolerance

# Function definitions:
f1 = lambda x: np.cos(x[0] + x[1]) - x[1]**2
f2 = lambda x: np.sin(x[0] - x[1]) - x[0]*x[1]
f = lambda x: (f1(x), f2(x))

f1d1 = lambda x : -np.sin(x[0] + x[1])
f1d2 = lambda x : -np.sin(x[0] + x[1]) - 2*x[1]
f2d1 = lambda x:  np.cos(x[0] - x[1]) - x[1]
f2d2 = lambda x: -np.cos(x[0] - x[1]) - x[0]
    
xk = np.array([1,1]) # Initial guess x_k

k = [0]
residuals = [np.abs(f(xk))]

itnum = 0 # iteration counter
success = True # Assume success unless we fail!

while linalg.norm(f(xk))>tol:
    A = np.array([[f1d1(xk), f1d2(xk)],[f2d1(xk), f2d2(xk)]])
    xk = xk - np.matmul(linalg.inv(A), f(xk))
    	
    print(f"x_{itnum} = {xk}")
    itnum += 1
    k.append(itnum)
    residuals.append(linalg.norm(f(xk)))

    if itnum>10000:
        print('Something is wrong, too many iterations, let\'s break out of this loop...!')
        success = False
        break


if success:
    # Plotting using matplotlib, imported at the top of the code.
    print()
    print(f"Done in {itnum} iterations")
    print(f"x1 = {xk[0]}")
    print(f"x2 = {xk[1]}")
