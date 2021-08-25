
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
OBSTACLES = [{"obstacleID": 1, "xPos": 1, "yPos": 1, "direction": NORTH, "distance": 0}, 
            {"obstacleID": 1, "xPos": 9, "yPos": 5, "direction": NORTH, "distance": 0},
            {"obstacleID": 1, "xPos": 2, "yPos": 7, "direction": NORTH, "distance": 50}]
            #in the format [{obstacleID: 1, xPos: 1, yPos: 1, direction: NORTH, distance: 0}, {}...]
            #values here just for testing and debugging 

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
#robotCurrPos is a dictionary {xPos: 1, yPos: 1, direction: NORTH}
def calculateDistance(OBSTACLES, robotCurrPos):
    for item in OBSTACLES:
        dist = math.sqrt(((abs(item["xPos"]-robotCurrPos["xPos"]))**2) + ((abs(item["yPos"]-robotCurrPos["yPos"]))**2))
        item["distance"] = dist
    return OBSTACLES

def nearestNeighbour(OBSTACLES):
    return

#just testing and debugging
ans = calculateDistance(OBSTACLES, {"xPos": 10, "yPos": 10, "direction": NORTH})
print(ans)