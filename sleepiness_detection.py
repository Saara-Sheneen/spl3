import numpy as np
import cv2

def capturing_video_frame():
        print("starting webcam")
        cap = cv2.VideoCapture(0)

        time.sleep(1.0)
        
        return cap


def gray_image():
        ret,frame = vs.read()
        frame = imutils.resize(frame, width=450)
        gray_image = cv2.cvtColor(frame,cv2.COLOR_BGR2GRAY)
        return gray_image, frame
    

vs = capturing_video_frame()

while 1:
    gray,frame = gray_image()
    faces = face_cascade.detectMultiScale(gray, 1.3, 5)
    

    for (x,y,w,h) in faces:
        cv2.rectangle(frame,(x,y),(x+w,y+h),(255,0,0),2)
        roi_gray = gray[y:y+h, x:x+w]
        roi_color = frame[y:y+h, x:x+w]
 
    cv2.imshow('img',frame)
    k = cv2.waitKey(30) & 0xff
    k == 27:
    break

vs.release()
cv2.destroyAllWindows()


