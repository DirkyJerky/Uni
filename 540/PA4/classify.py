import os, glob, collections, math

def create_bow(vocab, filepath):
    """ Create a single dictionary for the data
        Note: label may be None
    """
    bow = collections.defaultdict(lambda: 0)
    
    with open(filepath, "r") as f:
        for line in f:
            line = line.strip()

            if line in vocab:
                bow[line] += 1
            else:
                bow[None] += 1

    return dict(bow)

def load_training_data(vocab, directory):
    """ Create the list of dictionaries """
    dataset = []

    for year in ['2016', '2020']:
        files = glob.glob(os.path.join(directory, year, "*"))
        for file in files:
            bow = create_bow(vocab, file)
            dataset.append({'label': year, 'bow': bow})


    return dataset

def create_vocabulary(directory, cutoff):
    """ Create a vocabulary from the training directory
        return a sorted vocabulary list
    """

    training_files = glob.glob(os.path.join(directory, "*", "*"))

    freq = collections.defaultdict(lambda: 0)

    for file in training_files:
        with open(file,"r") as f:
            for line in f:
                line = line.strip()
                freq[line] += 1

    vocab = [k for k, v in freq.items() if v >= cutoff]

    vocab.sort()

    return vocab

def prior(training_data, label_list):
    """ return the prior probability of the label in the training set
        => frequency of DOCUMENTS
    """

    smooth = 1 # smoothing factor
    logprob = {}

    num_files_by_label = collections.defaultdict(lambda: 0)

    for data_point in training_data:
        num_files_by_label[data_point['label']] += 1
    
    for label in label_list:
        prob = (num_files_by_label[label] + smooth) / (len(training_data) + smooth*len(label_list))

        logprob[label] = math.log(prob)

    return logprob

def p_word_given_label(vocab, training_data, label):
    """ return the class conditional probability of label over all words, with smoothing """
    
    smooth = 1 # smoothing factor
    word_prob = {}

    total_bow = collections.defaultdict(lambda: 0)

    for data_point in training_data:
        if data_point['label'] == label:
            for k,v in data_point['bow'].items():
                total_bow[k] += v
    
    for word in vocab + [None]:
        total_bow[word] += smooth

    size = sum(total_bow.values())

    for word in vocab + [None]:
        word_prob[word] = math.log(total_bow[word] / size)
    return word_prob

    
##################################################################################
def train(training_directory, cutoff):
    """ return a dictionary formatted as follows:
            {
             'vocabulary': <the training set vocabulary>,
             'log prior': <the output of prior()>,
             'log p(w|y=2016)': <the output of p_word_given_label() for 2016>,
             'log p(w|y=2020)': <the output of p_word_given_label() for 2020>
            }
    """
    retval = {}

    vocab = create_vocabulary(training_directory, cutoff)

    training_data = load_training_data(vocab, training_directory)

    retval['vocabulary'] = vocab
    retval['log prior'] = prior(training_data, ['2016', '2020'])
    retval['log p(w|y=2020)'] = p_word_given_label(vocab, training_data, '2020')
    retval['log p(w|y=2016)'] = p_word_given_label(vocab, training_data, '2016')

    return retval


def classify(model, filepath):
    """ return a dictionary formatted as follows:
            {
             'predicted y': <'2016' or '2020'>, 
             'log p(y=2016|x)': <log probability of 2016 label for the document>, 
             'log p(y=2020|x)': <log probability of 2020 label for the document>
            }
    """
    retval = {}
    
    prob_2020 = model['log prior']['2020']
    prob_2016 = model['log prior']['2016']

    with open(filepath, "r") as f:
        for line in f:
            line = line.strip()

            if not line in model['vocabulary']:
                line = None
            
            prob_2020 += model['log p(w|y=2020)'][line]
            prob_2016 += model['log p(w|y=2016)'][line]

    retval['log p(y=2016|x)'] = prob_2016
    retval['log p(y=2020|x)'] = prob_2020

    if prob_2016 > prob_2020:
        retval['predicted y'] = '2016'
    else:
        retval['predicted y'] = '2020'

    return retval

