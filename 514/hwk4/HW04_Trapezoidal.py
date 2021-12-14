import numpy as np

# Function definitions:
f = lambda x: np.sin(np.e**x)

# Statics
x0 = 0
xn = 2

def go(n):
    xs = np.linspace(0,2,n+1)

    coeffs = np.full_like(xs,2)  # [2,2,...,2,2]
    coeffs[0] = 1  # [1,2,...,2,2]
    coeffs[-1] = 1  # [1,2,...,2,1]

    h2 = (xn-x0)/(2.0*n)   # h/2

    return h2 * np.dot(f(xs), coeffs) # = (h/2) * (f_0 + 2f_1 + 2f_2 + ... + 2f_(n-1) + f_n)