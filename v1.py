__author__ = 'michaelcaraccio'

import numpy as np
import cv2
import math

def intersection(x1, y1, r1, x2, y2, r2):
    limit = r1 + r2
    d = math.sqrt(int(math.pow((x2 - x1), 2)) + int(math.pow((y2 - y1), 2))) - (r2 + r1)

    return 0 if limit < d else 1


def in_circle(center_x, center_y, radius, x, y):
    # print("1:", math.sqrt((center_x - x) ** 2 + (center_y - y) ** 2),"2:",radius, "3:",1 if (math.sqrt((center_x - x) ** 2 + (center_y - y) ** 2) < radius) else 0)
    if math.sqrt((center_x - x) ** 2 + (center_y - y) ** 2) == 0.0:
        return 0
    return 1 if (math.sqrt((center_x - x) ** 2 + (center_y - y) ** 2) < radius) else 0


def intesect_list(circle_list, circle_list_new):
    #print("circle len: ", len(circle_list), "new len:", len(circle_list_new))
    for j in range(0, len(circle_list_new)):
        flag = True
        #print(j)
        if len(circle_list) != 0:

            for i in range(0, len(circle_list)):
                if in_circle(circle_list[i][0], circle_list[i][1], circle_list[i][2], circle_list_new[j][0],
                             circle_list_new[j][1]):
                    # circle_list.append(circle_list_new[j])
                    #print("caca")
                    flag = False

                elif intersection(circle_list[i][0], circle_list[i][1], circle_list[i][2], circle_list_new[j][0],
                                  circle_list_new[j][1], circle_list_new[j][2]):
                    flag = False

        if flag:
            #print("Append")
            # print("cercle",circle_list_new[j])
            circle_list.append(circle_list_new[j])

    return circle_list

#cimgG = cv2.morphologyEx(cimg, cv2.MORPH_CROSS ,kernel, iterations=1)
#cimgG = cv2.morphologyEx(cimg, cv2.MORPH_OPEN ,kernel, iterations=2)
#cimgG = cv2.morphologyEx(cimgG, cv2.MORPH_RECT ,kernel, iterations=1)

img = cv2.imread('images/c2.jpg', 0)

#cimg = cv2.GaussianBlur(img, (3, 3), 0)

cimg = cv2.cvtColor(img, cv2.COLOR_GRAY2BGR)
cimg = cv2.fastNlMeansDenoising(img,cimg,21,7,7)

kernel = np.ones((3, 3), np.uint8)
cimgG = cv2.morphologyEx(cimg, cv2.MORPH_RECT ,kernel, iterations=4)
cimgG = cv2.morphologyEx(cimg, cv2.MORPH_OPEN ,kernel, iterations=4)
#cimgG = cv2.morphologyEx(cimgG, cv2.MORPH_ERODE ,kernel, iterations=1)


#cv2.imshow('detected circles', img)
#laplacian = cv2.Laplacian(cimgG,cv2.CV_8U)

#kernel = np.ones((3,3),np.uint8)
#cimgG = cv2.erode(cimgG,kernel,iterations = 1)

#sobelx64f = cv2.Sobel(cimgG,cv2.CV_64F,1,0,ksize=5)
#abs_sobel64f = np.absolute(sobelx64f)
#sobel_8u = np.uint8(abs_sobel64f)

#sobelx64f = cv2.morphologyEx(sobelx64f, cv2.MORPH_GRADIENT, kernel, iterations=1)

#con = abs(img-cimg)
cv2.imwrite("withoutMask.tif",cimgG)

circles = cv2.HoughCircles(cimgG, cv2.HOUGH_GRADIENT, 1.5, 50, np.array([]), 100, 90, 20, 120)

print(circles)
cimg = cv2.cvtColor(cimgG , cv2.COLOR_GRAY2BGR)


for i in range(0, len(circles[0])):
    cv2.circle(cimg, (circles[0][i][0], circles[0][i][1]), circles[0][i][2], (0, 0, 255), 1, cv2.LINE_AA)
    cv2.circle(cimg, (circles[0][i][0], circles[0][i][1]), 2, (0, 255, 0), 3,cv2.LINE_AA)  # draw center of circle

cv2.imshow('cercles', cimg)

cv2.waitKey(0)