__author__ = 'michaelcaraccio'

#http://blog.christianperone.com/?p=2711

import numpy as np
import cv2

if __name__ == "__main__":

    img = cv2.imread('images/c8.jpg', 0)
    img2 = cv2.imread('images/c8.jpg', 0)

    gray = cv2.cvtColor(img, cv2.COLOR_GRAY2BGR)

    img2 = cv2.pyrMeanShiftFiltering(gray, 20, 30,3)

    img2 = cv2.cvtColor(img2, cv2.COLOR_BGR2GRAY)

    #img2 = cv2.fastNlMeansDenoising(img2,None,5,7,21)

    gray = cv2.cvtColor(img, cv2.COLOR_GRAY2BGR)

    # http://stackoverflow.com/questions/24018552/how-do-i-enhance-an-image-then-convert-it-to-a-binary-image-using-python-and-ope
    enhanced_im  = np.array(img2, np.uint8)

    gray_blur = cv2.GaussianBlur(gray, (3, 3), 0)
    thresh = cv2.adaptiveThreshold(enhanced_im, 255, 1, 1, 11, 17)

    kernel = np.ones((3, 3), np.uint8)
    closing = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, kernel, iterations=2)

    cont_img = closing.copy()
    cv2.imshow('img2-1', img2)

    q, contours, hierarchy = cv2.findContours(cont_img,cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)

    for cnt in contours:
        area = cv2.contourArea(cnt)
        if area < 1000 or area > 15000:
            continue

        if len(cnt) < 5:
            continue

        ellipse = cv2.fitEllipse(cnt)

        x,y,w,h = cv2.boundingRect(cnt)
        aspect_ratio = float(w)/h

        print(aspect_ratio)

        if aspect_ratio <1.2 and aspect_ratio > 0.8 :
            cv2.ellipse(gray, ellipse, (0,255,0), 2)

    cv2.imshow('img2-2', img2)

    cimg = cv2.cvtColor(img, cv2.COLOR_GRAY2BGR)
    circles = cv2.HoughCircles(img, cv2.HOUGH_GRADIENT, 1.5, 50, np.array([]), 101, 100, 20, 220)
    print(circles)

    for i in range(0, len(circles[0]) - 1):
        cv2.circle(img, (circles[0][i][0], circles[0][i][1]), circles[0][i][2], (0, 0, 255), 1, cv2.LINE_AA)
        cv2.circle(img, (circles[0][i][0], circles[0][i][1]), 2, (0, 255, 0), 3,cv2.LINE_AA)  # draw center of circle

    cv2.imshow('cercles', cimg)
    cv2.imshow("Morphological Closing", closing)
    cv2.imshow("Adaptive Thresholding", thresh)
    cv2.imshow('Contours', gray)

    cv2.imshow('Hough circle img2', img)
    cv2.waitKey(0)
    #cv2.destroyAllWindows()