#!/usr/bin/python3

#
# This code is from the ImageAI project
# See: https://github.com/OlafenwaMoses/ImageAI/blob/master/imageai/Prediction/README.md
#

from imageai.Prediction import ImagePrediction
import os
import sys

try:
    image_name = sys.argv[1]
except IndexError:
    image_name = '/tmp/capture.jpg'

execution_path = os.getcwd()

prediction = ImagePrediction()

prediction.setModelTypeAsSqueezeNet()

prediction.setModelPath(os.path.join(execution_path, "models/squeezenet_weights_tf_dim_ordering_tf_kernels.h5"))

prediction.loadModel()

predictions, probabilities = prediction.predictImage(os.path.join(execution_path, image_name), result_count=5 )

for eachPrediction, eachProbability in zip(predictions, probabilities):
    print(eachPrediction , " : " , eachProbability)

