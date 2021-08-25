
#library
import math

#attributes
SPEED = 1 # constant
MOVES = 0
START = [0,0]
BOUNDARY = [[0,0],(0,200),(200,0),(200,200)]
VISTED = []

#image of obstacle given in N,S,E,W
NORTH = math.pi/2
SOUTH = -math.pi/2
EAST = 0
WEST = -math.pi
OBSTACLE = []

#input
#stub method to get obstacle on terminal
def getObstacle():
    val = input("Enter obstacle coordinate (x,y):\n")
    while (val!="END"):
        x = val.split(",")
        obsCoor = [int(x[0]),int(x[1])]
        OBSTACLE.append(obsCoor)
        val = input("Enter next coordinate (enter END to end):\n")

#find distance from car to obstacles
def calculateDistance(OBSTACLE):


def nearestNeighbour(OBSTACLE):


