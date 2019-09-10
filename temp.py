import imutils
import numpy as np
import argparse
import imutils
import dlib
import cv2
from collections import OrderedDict

def rect_to_bb(rect):
	
	x = rect.left()
	y = rect.top()
	w = rect.right() - x
	h = rect.bottom() - y

	
	return (x, y, w, h)

def shape_to_np(shape, dtype="int"):
	
	coords = np.zeros((shape.num_parts, 2), dtype=dtype)

	
	for i in range(0, shape.num_parts):
		coords[i] = (shape.part(i).x, shape.part(i).y)

	
	return coords
    

FACIAL_LANDMARKS_68_IDXS = OrderedDict([
	("mouth", (48, 68)),
	("inner_mouth", (60, 68)),
	("right_eyebrow", (17, 22)),
	("left_eyebrow", (22, 27)),
	("right_eye", (36, 42)),
	("left_eye", (42, 48)),
	("nose", (27, 36)),
	("jaw", (0, 17))
])

FACIAL_LANDMARKS_5_IDXS = OrderedDict([
	("right_eye", (2, 3)),
	("left_eye", (0, 1)),
	("nose", (4))
])

FACIAL_LANDMARKS_IDXS = FACIAL_LANDMARKS_68_IDXS

ap = argparse.ArgumentParser()
ap.add_argument("-p", "--shape-predictor", required=True,
	help="path to facial landmark predictor")
ap.add_argument("-i", "--image", required=True,
	help="path to input image")
args = vars(ap.parse_args())

detector = dlib.get_frontal_face_detector()
predictor = dlib.shape_predictor(args["shape_predictor"])

image = cv2.imread('sara.jpg',1)
image = imutils.resize(image, width=500)
gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
rects = detector(gray, 1)
for (i, rect) in enumerate(rects):
    shape = predictor(gray, rect)
    shape = shape_to_np(shape)
    (x, y, w, h) = rect_to_bb(rect)
   # cv2.rectangle(image, (x, y), (x + w, y + h), (0, 255, 0), 2)
   # cv2.putText(image, "Face #{}".format(i + 1), (x - 10, y - 10),
    #cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)
    for (x, y) in shape:
        cv2.circle(image, (x, y), 1, (0, 0, 255), -1)
cv2.imshow("Output", image)
cv2.waitKey(0)


























	

