import numpy as np

# Function definitions:
f = lambda x: np.sin(np.e**x)

# Statics
x0 = 0
xn = 2

def go(n):
    assert(n%2 == 0)
    xs = np.linspace(0,2,n+1)

    coeffs = np.full_like(xs,4)  # [4,4,4,4,4,...,4,4,4,4,4]
    coeffs[::2] = 2  # [2,4,2,4,2,...,2,4,2,4,2]
    coeffs[0] = 1  # [1,4,2,4,2,...,2,4,2,4,2]
    coeffs[-1] = 1  # [1,4,2,4,2,...,2,4,2,4,1]

    h3 = (xn-x0)/(3.0*n)   # h/3

    return h3 * np.dot(f(xs), coeffs) # = (h/3) * (f_0 + 4f_1 + 2f_2 + ... + 4f_(n-1) + f_n)