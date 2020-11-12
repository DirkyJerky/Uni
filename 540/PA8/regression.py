import numpy as np
import random

# Feel free to import other packages, if needed.
# As long as they are supported by CSL machines.


def get_dataset(filename):
    """
    TODO: implement this function.

    INPUT: 
        filename - a string representing the path to the csv file.

    RETURNS:
        An n by m+1 array, where n is # data points and m is # features.
        The labels y should be in the first column.
    """
    return np.genfromtxt(filename, delimiter=',', skip_header=1, usecols=range(1,17))


def print_stats(dataset, col):
    """
    TODO: implement this function.

    INPUT: 
        dataset - the body fat n by m+1 array
        col     - the index of feature to summarize on. 
                  For example, 1 refers to density.

    RETURNS:
        None
    """
    arr = dataset[:, col]
    print(arr.shape[0])
    print('{:.2f}'.format(np.mean(arr)))
    print('{:.2f}'.format(np.std(arr)))

def construct_f_out_matrix(dataset, cols, betas):
    """
    Return a (252,1) array of the computation of `f` on each row of the dataset,
    where `f` is defined by the cols and betas provided.
    """
    # A coefficient matrix representing the calculation of `f(dp) - beta[0]` on each row of the dataset
    # The addition of beta[0] in f isnt possible using just matrix multiplication, is added later
    f_mat = np.zeros((dataset.shape[1],1))
    f_mat[0,0] = -1
    for i in range(len(cols)):
        f_mat[cols[i], 0] = betas[i+1]
    
    # shape = (252,1), the result of `f(dp) - beta[0]` on each row of the dataset
    f_out_mat = np.matmul(dataset, f_mat)

    f_out_mat += betas[0]

    return f_out_mat

def regression(dataset, cols, betas):
    """
    TODO: implement this function.

    INPUT: 
        dataset - the body fat n by m+1 array
        cols    - a list of feature indices to learn.
                  For example, [1,8] refers to density and abdomen.
        betas   - a list of elements chosen from [beta0, beta1, ..., betam]

    RETURNS:
        mse of the regression model
    """ 
    return np.mean(construct_f_out_matrix(dataset, cols, betas) ** 2)


def gradient_descent(dataset, cols, betas):
    """
    TODO: implement this function.

    INPUT: 
        dataset - the body fat n by m+1 array
        cols    - a list of feature indices to learn.
                  For example, [1,8] refers to density and abdomen.
        betas   - a list of elements chosen from [beta0, beta1, ..., betam]

    RETURNS:
        An 1D array of gradients
    """
    f_out_mat = construct_f_out_matrix(dataset, cols, betas)

    half_grads = np.empty((len(betas)))

    for i in range(len(cols)):
        f_out_copy = np.copy(f_out_mat)
        f_out_copy *= np.reshape(dataset[:, cols[i]], f_out_mat.shape)
        half_grads[i+1] = np.mean(f_out_copy)

    half_grads[0] = np.mean(f_out_mat)
    return 2 * half_grads


def iterate_gradient(dataset, cols, betas, T, eta):
    """
    TODO: implement this function.

    INPUT: 
        dataset - the body fat n by m+1 array
        cols    - a list of feature indices to learn.
                  For example, [1,8] refers to density and abdomen.
        betas   - a list of elements chosen from [beta0, beta1, ..., betam]
        T       - # iterations to run
        eta     - learning rate

    RETURNS:
        None
    """
    def gen_out_str(index, reg, betas):
        out = str(index)
        out += ' '
        out += '{:.2f}'.format(reg)
        for beta in betas:
            out += ' '
            out += '{:.2f}'.format(beta)
        return out
    
    betas = np.copy(betas).astype(float)
    print(gen_out_str(0, regression(dataset, cols, betas), betas))
    for i in range(1,T+1):
        betas -= eta * gradient_descent(dataset, cols, betas)

        print(gen_out_str(i, regression(dataset, cols, betas), betas))


def compute_betas(dataset, cols):
    """
    TODO: implement this function.

    INPUT: 
        dataset - the body fat n by m+1 array
        cols    - a list of feature indices to learn.
                  For example, [1,8] refers to density and abdomen.

    RETURNS:
        A tuple containing corresponding mse and several learned betas
    """
    betas = None
    mse = None
    return (mse, *betas)


def predict(dataset, cols, features):
    """
    TODO: implement this function.

    INPUT: 
        dataset - the body fat n by m+1 array
        cols    - a list of feature indices to learn.
                  For example, [1,8] refers to density and abdomen.
        features- a list of observed values

    RETURNS:
        The predicted body fat percentage value
    """
    result = None
    return result


def random_index_generator(min_val, max_val, seed=42):
    """
    DO NOT MODIFY THIS FUNCTION.
    DO NOT CHANGE THE SEED.
    This generator picks a random value between min_val and max_val,
    seeded by 42.
    """
    random.seed(seed)
    while True:
        yield random.randrange(min_val, max_val)


def sgd(dataset, cols, betas, T, eta):
    """
    TODO: implement this function.
    You must use random_index_generator() to select individual data points.

    INPUT: 
        dataset - the body fat n by m+1 array
        cols    - a list of feature indices to learn.
                  For example, [1,8] refers to density and abdomen.
        betas   - a list of elements chosen from [beta0, beta1, ..., betam]
        T       - # iterations to run
        eta     - learning rate

    RETURNS:
        None
    """
    pass


if __name__ == '__main__':
    # Your debugging code goes here.
    pass