import scipy
from scipy.linalg import eigh
import numpy as np
import matplotlib.pyplot as plt

def load_and_center_dataset(filename):
    data = np.load(filename)
    data = np.reshape(data, (2000,784))
    data -= np.mean(data, axis=0)
    return data

def get_covariance(dataset):
    S = np.dot(dataset.T, dataset)
    S /= (dataset.shape[0] - 1)
    return S

def get_eig(S, m):
    eigh_vals, eigh_vecs = eigh(S, subset_by_index=[S.shape[0]-m, S.shape[0]-1])
    return np.diag(np.flip(eigh_vals)), np.flip(eigh_vecs, axis=1)

def get_eig_perc(S, perc):
    perc_var = eigh(S, eigvals_only=True)
    perc_var /= np.sum(perc_var)

    num_gt = S.shape[0] - np.argmax(perc_var > perc)

    return get_eig(S, num_gt)

def project_image(image, U):
    return U @ U.T.dot(image)

def display_image(orig, proj):
    orig = np.reshape(orig, (28,28))
    proj = np.reshape(proj, (28,28))

    fig, (ax1, ax2) = plt.subplots(1, 2)

    ax1.set_title('Original')
    ax2.set_title('Projection')

    img1 = ax1.imshow(orig, aspect='equal', cmap='gray')
    img2 = ax2.imshow(proj, aspect='equal', cmap='gray')

    fig.colorbar(img1, ax=ax1)
    fig.colorbar(img2, ax=ax2)

    plt.show()