
#libraries
import math

#attributes
SPEED = 0.25
MOVES = 0
START = [0,0]
BOUNDARY = [[0,0],(0,200),(200,0),(200,200)]
VISTED = []

#image of obstacle given in N,S,E,W
NORTH = math.pi
SOUTH = math.pi/2
EAST = -math.pi/2
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

def pathplanning(OBSTACLE):

def nearestNeighbour():

