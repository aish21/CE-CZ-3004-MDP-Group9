
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
OBSTACLE = [{"obstacleID": 1, "xPos": 1, "yPos": 1, "direction": NORTH, "distance": 0}, 
            {"obstacleID": 1, "xPos": 9, "yPos": 5, "direction": NORTH, "distance": 0},
            {"obstacleID": 1, "xPos": 2, "yPos": 7, "direction": NORTH, "distance": 50}]
            #in the format [{obstacleID: 1, xPos: 1, yPos: 1, direction: NORTH, distance: 0}, {}...]
            #values here just for testing and debugging 
distanceMatrix = [[math.inf,0,0,0,0,0,0],
                  [0,math.inf,0,0,0,0,0],
                  [0,0,math.inf,0,0,0,0],
                  [0,0,0,math.inf,0,0,0],
                  [0,0,0,0,math.inf,0,0],
                  [0,0,0,0,0,math.inf,0],
                  [0,0,0,0,0,0,math.inf]] # assume 5 obstacles

#input
#stub method to get obstacle on terminal
def getObstacle():
    val = input("Enter obstacle coordinate (x,y,direction):\n")
    obsID = 1
    while (val!="END"):
        x = val.split(",")
        obsCoor = {"obstacleID": obsID, "xPos": int(x[0]), "yPos": int(x[1]), "direction": x[2], "distance": 0}
        obsID += 1
        OBSTACLES.append(obsCoor)
        val = input("Enter next coordinate (enter END to end):\n")

#find distance from car to obstacles
#robotCurrPos is a dictionary {xPos: 1, yPos: 1, direction: NORTH}
def calculateDistance(OBSTACLE, robotCurrPos):
    for item in OBSTACLE:
        dist = math.sqrt(((abs(item["xPos"]-robotCurrPos["xPos"]))**2) + ((abs(item["yPos"]-robotCurrPos["yPos"]))**2))
        item["distance"] = dist
    return OBSTACLE

def nearestNeighbour(OBSTACLE):
    return

#just testing and debugging
ans = calculateDistance(OBSTACLE, {"xPos": 10, "yPos": 10, "direction": NORTH})
print(ans)