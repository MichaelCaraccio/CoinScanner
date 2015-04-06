__author__ = 'michaelcaraccio'

import numpy as np
import cv2

img1 = cv2.imread('images/2chpile.png',0)          # queryImage
img2 = cv2.imread('images/c7.jpg',0) # trainImage

# Initiate SIFT detector
orb = cv2.ORB_create()

# find the keypoints and descriptors with SIFT
kp1, des1 = orb.detectAndCompute(img1,None)
kp2, des2 = orb.detectAndCompute(img2,None)

index_params= dict(algorithm = 10,
                   table_number = 12, # 12
                   key_size = 20,     # 20
                   multi_probe_level = 2) #2
search_params = dict(checks=4)   # or pass empty dictionary
# create BFMatcher object
bf = cv2.BFMatcher(cv2.NORM_HAMMING, crossCheck=True)
#bf = cv2.FlannBasedMatcher(index_params,search_params)

# Match descriptors.
matches = bf.match(des1,des2)

# Sort them in the order of their distance.
matches = sorted(matches, key = lambda x:x.distance)

img3 = np.zeros((1,1))
# Draw first 10 matches.
img4 = cv2.drawMatches(img1,kp1,img2,kp2,matches[:400000],img3, flags=2)

cv2.imshow('img2', img4)
cv2.waitKey(0)