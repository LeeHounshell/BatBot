#!/usr/bin/python3

from PIL import Image
from PIL import ImageChops
from functools import reduce

import math
import operator
import sys


prev_image_name = ''
curr_image_name = ''
try:
    prev_image_name = sys.argv[1]
    curr_image_name = sys.argv[2]
except IndexError as e:
    print('Usage: compare_images.py image1.jpg image2.jpg')
    exit()

def compare(name1, name2):
    try:
        image1 = Image.open(name1)
        image2 = Image.open(name2)

        if image1.size != image2.size or image1.getbands() != image2.getbands():
            return False

        h = ImageChops.difference(image1, image2).histogram()

        # from: https://stackoverflow.com/questions/1927660/compare-two-images-the-python-linux-way
        # calculate rms
        return math.sqrt(reduce(operator.add,
            map(lambda h, i: h*(i**2), h, range(256))
        ) / (float(image1.size[0]) * image1.size[1]))
        
    except Exception as e:
        print("IMAGE COMPARE FAILED: e=" + str(e))
    return False

if __name__ == '__main__':
    diff = compare(prev_image_name, curr_image_name)
    if diff < 6:
        print("the same: " + str(diff))
    else:
        print("different: " + str(diff))
