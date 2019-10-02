#!/usr/bin/python3

#
# This code is derived from the ImageAI project
# See: https://github.com/OlafenwaMoses/ImageAI/blob/master/imageai/Prediction/README.md
#

from imageai.Prediction import ImagePrediction

import socket
import os
import sys


try:
    image_name = sys.argv[1]
    if image_name == '--help':
        print("Usage: " + sys.argv[0] + " [image_path] [algorithm]")
        print("        algorithm values: ['SqueezeNet', 'DenseNet', 'IncepptionV3', 'ResNet']")
        exit(0)
except IndexError:
    image_name = '/tmp/capture.jpg'

try:
    algorithm = sys.argv[2]
except IndexError:
    algorithm = 'DenseNet'


execution_path = os.getcwd()
prediction = ImagePrediction()

if algorithm == 'SqueezeNet':
    print('===> SqueezeNet');
    prediction.setModelTypeAsSqueezeNet()
    prediction.setModelPath(
        os.path.join(execution_path,
            "models/squeezenet_weights_tf_dim_ordering_tf_kernels.h5"))

elif algorithm == 'DenseNet':
    print('===> DenseNet');
    prediction.setModelTypeAsDenseNet()
    prediction.setModelPath(
        os.path.join(execution_path,
            "models/DenseNet-BC-121-32.h5"))

elif algorithm == 'InceptionV3':
    print('===> InceptionV3');
    prediction.setModelTypeAsInceptionV3()
    prediction.setModelPath(
        os.path.join(execution_path,
            "models/inception_v3_weights_tf_dim_ordering_tf_kernels.h5"))

else:
    algorithm = 'ResNet'
    print('===> ResNet');
    prediction.setModelTypeAsResNet()
    prediction.setModelPath(
        os.path.join(execution_path,
            "models/resnet50_weights_tf_dim_ordering_tf_kernels.h5"))

prediction.loadModel()


def predict(image_name) :
    results = ''
    try:
        predictions, probabilities = prediction.predictImage(
            os.path.join(execution_path, image_name), result_count=5 )
        new_predictions = zip(predictions, probabilities)
        for eachPrediction, eachProbability in new_predictions:
            results = results + \
                str(eachPrediction) + "=" + \
                str(int(eachProbability * 100) / 100.0) + "\n"
    except Exception as e:
        results = "ERROR: " + str(e)
    return results


def server():
    host = socket.gethostbyname("localhost")
    port = 9310  # Make sure it's within the > 1024 $$ <65535 range
    s = socket.socket()
    s.bind((host, port))
    print("waiting for a connection..")
    s.listen(1)
    try:
        comm, address = s.accept()
        print("Connection from: " + str(address))
        while True:
            data = comm.recv(1024).decode('utf-8')
            if not data:
                break
            print('REQUEST ID: ' + data)
            data = predict(data)
            print('IDENTIFIED: ' + data)
            comm.send(data.encode('utf-8'))
        comm.close()
    except ConnectionAbortedError as e:
        print("got connection request for closed socket. e=" + str(e))


if __name__ == '__main__':
    while True:
        print("\n")
        server()

