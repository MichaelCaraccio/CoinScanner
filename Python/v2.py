__author__ = 'michaelcaraccio'

import numpy as np
import cv2

img = cv2.imread('images/c4.jpg', 0)


cimg = cv2.cvtColor(img, cv2.COLOR_GRAY2BGR)

cimg = cv2.fastNlMeansDenoising(img,cimg,21,7,7)

kernel = np.ones((3, 3), np.uint8)
#cimgG = cv2.morphologyEx(cimg, cv2.MORPH_RECT ,kernel, iterations=4)
cimgG = cv2.morphologyEx(cimg, cv2.MORPH_OPEN ,kernel, iterations=5)

circles = cv2.HoughCircles(cimgG, cv2.HOUGH_GRADIENT, 1.5, 50, np.array([]), 100, 90, 20, 120)

print(circles)

cimg_dst = cv2.cvtColor(cimgG , cv2.COLOR_GRAY2BGR)

for i in range(0, len(circles[0])):
    cv2.circle(cimg_dst, (circles[0][i][0], circles[0][i][1]), circles[0][i][2], (0, 0, 255), 1, cv2.LINE_AA)
    cv2.circle(cimg_dst, (circles[0][i][0], circles[0][i][1]), 2, (0, 255, 0), 3,cv2.LINE_AA)  # draw center of circle

cv2.imwrite("images_finale.tif",cimg_dst)

cv2.imshow('cercles', cimg_dst)

cv2.waitKey(0)