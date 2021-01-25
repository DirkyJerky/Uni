import numpy as np
import tensorflow as tf
from tensorflow import keras

class_names = ['Zero', 'One', 'Two', 'Three', 'Four', 'Five', 'Six', 'Seven', 'Eight', 'Nine']

def get_dataset(training=True):
    return keras.datasets.mnist.load_data()[0 if training else 1]

def print_stats(train_images, train_labels):
    shape = train_images.shape
    print(shape[0])
    print('{}x{}'.format(shape[1], shape[2]))
    
    bins = np.bincount(train_labels, minlength=10)

    for i in range(len(class_names)):
        print('{}. {} - {}'.format(i, class_names[i], bins[i]))


def build_model():
    model = keras.Sequential(name="My_MNIST_Model")
    model.add(keras.Input(shape=(28,28)))
    model.add(keras.layers.Flatten(name="flatten"))
    model.add(keras.layers.Dense(128, activation=keras.activations.relu, name="dense_128"))
    model.add(keras.layers.Dense(64, activation=keras.activations.relu, name="dense_64"))
    model.add(keras.layers.Dense(10, name="dense_10"))

    optim = keras.optimizers.SGD(learning_rate=0.001)
    loss = keras.losses.SparseCategoricalCrossentropy(from_logits=True)
    metric = keras.metrics.SparseCategoricalAccuracy(name="accuracy")

    model.compile(optimizer=optim, loss=loss, metrics=[metric])

    return model

def train_model(model, train_images, train_labels, T):
    model.fit(train_images, train_labels, epochs=T)

def evaluate_model(model, test_images, test_labels, show_loss=True):
    test_loss, test_accuracy = model.evaluate(test_images, test_labels, verbose=False)
    if show_loss:
        print('Loss: {:.4f}'.format(test_loss))
    
    print('Accuracy: {:.2%}'.format(test_accuracy))

def predict_label(model, test_images, index):
    predicts = model.predict(test_images)[1]
    sort_indices = np.argsort(predicts)

    for i in range(1,4):
        ind = sort_indices[-i]
        print('{}: {:.2%}'.format(class_names[ind], predicts[ind]))

# if __name__ == "__main__":
#     (train_images, train_labels) = get_dataset()
#     model = build_model()
#     train_model(model, train_images, train_labels, 10)