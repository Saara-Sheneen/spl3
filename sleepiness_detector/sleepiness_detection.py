import numpy as np
import cv2
import dlib
import imutils
import time
import os
from imutils import face_utils
from scipy.spatial import distance as dist
import matplotlib.pyplot as plt
from collections import OrderedDict
import argparse

face_cascade = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')

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


grid_x=[]
grid_y=[]

fig=plt.figure()
ax=fig.add_subplot(211)



grid_x1=[]
grid_y1=[]

ax1=fig.add_subplot(212)
fig.show()
k=0
EYE_AR_THRESH = 0.3
EYE_AR_CONSEC_FRAMES = 8


def sound_alarm(path):
	
	playsound.playsound(path)

def eye_aspect_ratio(eye):
        
	A = dist.euclidean(eye[1], eye[5])
    
	B = dist.euclidean(eye[2], eye[4])	
    
	C = dist.euclidean(eye[0], eye[3])	
    
	ear = (A + B) / (2.0 * C)
	
	return ear


def PolyArea2D(pts):
        lines = np.hstack([pts,np.roll(pts,-1,axis=0)])
        area = 0.5*abs(sum(x1*y2-x2*y1 for x1,y1,x2,y2 in lines))
        return area

def yawn(mouth):
        pts=np.array([mouth[0]])
        for i in range(11):
                arr2=np.array([mouth[i+1]])
                pts=np.concatenate((pts,arr2),axis=0)
        
        return PolyArea2D(pts),pts
        
def capturing_video_frame():
        print("[INFO] starting video stream thread...")
	#http://10.100.100.209:8080/video
	#"http://172.16.19.246:8080/video"
        cap = cv2.VideoCapture(0)


        time.sleep(1.0)
        
        return cap

def gray_image():
        ret,frame = vs.read()
        frame = imutils.resize(frame, width=450)
        gray_image = cv2.cvtColor(frame,cv2.COLOR_BGR2GRAY)
        return gray_image, frame


def left_eye_right_eye_mouth_location(shape):
        leftEye = shape[lStart:lEnd]
        rightEye = shape[rStart:rEnd]
        mouth= shape[mStart:mEnd]
        return leftEye,rightEye,mouth

def append_data_grid_X(amount_of_yawn):
        
	grid_x1.append(k)
	grid_y1.append(amount_of_yawn)
	ax1.plot(grid_x1,grid_y1,color='b')
	fig.canvas.draw()
	ax1.set_xlim(left=max(0,k-50),right=k+50)

def append_EAR_data_grid_X(ear):
        grid_x.append(k)
        grid_y.append(ear)
        ax.plot(grid_x,grid_y,color='r')
        fig.canvas.draw()
        ax.set_xlim(left=max(0,k-50),right=k+50)
        

def calculate_convex_hull_and_draw_contours(left_eye_location,right_eye_location,mouth_location):
        leftEyeHull = cv2.convexHull(left_eye_location)
        rightEyeHull = cv2.convexHull(right_eye_location)
        mouthHull=cv2.convexHull(mouth_location)
        cv2.drawContours(frame, [leftEyeHull], -1, (0, 255, 205), 1)
        cv2.drawContours(frame, [rightEyeHull], -1, (0, 255, 205), 1)
        #cv2.drawContours(frame, [mouthHull], -1, (0, 0, 255), 1)
        
        
def provide_alert(COUNTER, ALARM_ON):
        if ear < EYE_AR_THRESH:
                COUNTER += 1
                if COUNTER >= EYE_AR_CONSEC_FRAMES:
                        if not ALARM_ON:
                                ALARM_ON = True
                                os.system("aplay alarm.wav")
                        cv2.putText(frame, "DROWSINESS ALERT!", (10, 30),cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)
        else: 
                COUNTER = 0
                ALARM_ON = False
                cv2.putText(frame, "EAR: {:.2f}".format(ear), (300, 30),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)
        return COUNTER
        
        
ap = argparse.ArgumentParser()
ap.add_argument("-p", "--shape-predictor", required=True,
	help="path to facial landmark predictor")
ap.add_argument("-a", "--alarm", type=str, default="",
	help="path alarm.WAV file")
ap.add_argument("-w", "--webcam", type=int, default=0,
	help="index of webcam on system")
args = vars(ap.parse_args())
 

COUNTER = 0
ALARM_ON = False


print("[INFO] loading facial landmark predictor...")
detector = dlib.get_frontal_face_detector()
predictor = dlib.shape_predictor(args["shape_predictor"])


(lStart, lEnd) = face_utils.FACIAL_LANDMARKS_IDXS["left_eye"]
(rStart, rEnd) = face_utils.FACIAL_LANDMARKS_IDXS["right_eye"]
(mStart,mEnd) = face_utils.FACIAL_LANDMARKS_IDXS["mouth"]


print("[INFO] starting video stream thread...")
vs = capturing_video_frame()

#time.sleep(1.0)
#k=0

while True:
                                       
        gray,frame = gray_image()
        faces = face_cascade.detectMultiScale(gray, 1.3, 5)
        for(x,y,w,h) in faces:
            cv2.rectangle(frame,(x,y),(x+w,y+h),(255,0,0),2)
            roi_gray = gray[y:y+h, x:x+w]
            roi_color = frame[y:y+h, x:x+w]
            
                       
        rects = detector(gray, 0)
        	
        for rect in rects:
		
                shape = predictor(gray, rect)
                shape = face_utils.shape_to_np(shape)
                left_eye_location, right_eye_location, mouth_location = left_eye_right_eye_mouth_location(shape)
                left_eye_aspect_ratio = eye_aspect_ratio(left_eye_location)
                right_eye_aspect_ratio = eye_aspect_ratio(right_eye_location)
                amount_of_yawn,omouth=yawn(mouth_location)
                ear = (left_eye_aspect_ratio + right_eye_aspect_ratio) / 2.0

                append_data_grid_X(amount_of_yawn)
                append_EAR_data_grid_X(ear)
                k+=1

                calculate_convex_hull_and_draw_contours(left_eye_location, right_eye_location, mouth_location)
                
		
              
		
                if ear < EYE_AR_THRESH:
                        COUNTER += 1			
                        if COUNTER >= EYE_AR_CONSEC_FRAMES:
				
                                if not ALARM_ON:
                                        ALARM_ON = True
                                        os.system("aplay alarm.wav")

					

				
                                cv2.putText(frame, "drowsy!!!", (300, 30),cv2.FONT_HERSHEY_SIMPLEX, 0.7, (22, 180, 205), 2)

		
                else:
                        COUNTER = 0
                        ALARM_ON = False

		
                #cv2.putText(frame, "EAR: {:.2f}".format(ear), (300, 30),
                       #cv2.FONT_HERSHEY_SIMPLEX, 0.7, (22, 180, 255), 2)
 
        cv2.imshow("Frame", frame)
        key = cv2.waitKey(1) & 0xFF
 
	
        if key == ord("q"):
                break
	



#vs.stop()
vs.release()
cv2.destroyAllWindows()
