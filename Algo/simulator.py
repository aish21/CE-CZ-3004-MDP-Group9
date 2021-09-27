
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
            {"obstacleID": 2, "xPos": 9, "yPos": 5, "direction": NORTH, "distance": 0},
            {"obstacleID": 3, "xPos": 2, "yPos": 7, "direction": NORTH, "distance": 0},
            {"obstacleID": 4, "xPos": 4, "yPos": 3, "direction": EAST, "distance": 0},
            {"obstacleID": 5, "xPos": 8, "yPos": 6, "direction": SOUTH, "distance": 0},
            {"obstacleID": 6, "xPos": 3, "yPos": 9, "direction": SOUTH, "distance": 0},
            {"obstacleID": 7, "xPos": 7, "yPos": 2, "direction": SOUTH, "distance": 0}]
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
def calculateInitialCarToObtDistance(OBSTACLES, robotCurrPos):
    for item in OBSTACLES:
        dist = math.sqrt(((abs(item["xPos"]-robotCurrPos["xPos"]))**2) + ((abs(item["yPos"]-robotCurrPos["yPos"]))**2))
        item["distance"] = dist
    return OBSTACLE

# calculate the distances between obstacles
def calDistanceBetweenObs(distanceMatrix, OBSTACLES):
    for i in range(0, len(distanceMatrix)):
        for j in range(0, len(distanceMatrix)):
            if(i<=j):
                continue
            else: 
                dist = math.sqrt(((abs(OBSTACLES[i]["xPos"]-OBSTACLES[j]["xPos"]))**2) + ((abs(OBSTACLES[i]["yPos"]-OBSTACLES[j]["yPos"]))**2))
                distanceMatrix[i][j] = dist
                distanceMatrix[j][i] = dist
    return distanceMatrix

def nearestNeighbour(OBSTACLES, distanceMatrix):
    visitingSequence = []
    i=0
    while(i<len(distanceMatrix)):
        currDist = 40
        if(i==0):
            for j in range (0, len(distanceMatrix)):
                if(OBSTACLES[j]["distance"]<currDist):
                   currDist = OBSTACLES[j]["distance"]
                   target = OBSTACLES[j]["obstacleID"]
            for dist in distanceMatrix:
                dist[target-1] = math.inf
            visitingSequence.append(target)
        else:
            k=1
            for dist in distanceMatrix[target-1]:
                if(dist<currDist):
                    currDist = dist
                    target = k
                k+=1
            for dist in distanceMatrix:
                dist[target-1] = math.inf
            visitingSequence.append(target)
        i+=1
    return visitingSequence

#just testing and debugging
OBSTACLES = calculateInitialCarToObtDistance(OBSTACLES, {"xPos": 0, "yPos": 0, "direction": NORTH})
print(len(OBSTACLES))
print("----------")
distanceMatrix = calDistanceBetweenObs(distanceMatrix, OBSTACLES)
print(distanceMatrix)
ans = nearestNeighbour(OBSTACLES, distanceMatrix)
print(ans)