import numpy as np
from matplotlib import pyplot as plt

f = lambda x: 1/2 * (x**2 + 0.3)

plt.figure()

for x0 in [0, 1.83, -1]:
    idx = np.arange(21)
    iters = np.empty(21);

    iters[0] = x0
    for k in idx[1:]:
        iters[k] = f(iters[k-1])
    
    plt.plot(idx, iters, label = f'From {x0}')

plt.xticks(np.arange(0, 21, 1.0))
plt.yticks(np.arange(-1.0, 2, 0.5))
plt.legend()
plt.show()