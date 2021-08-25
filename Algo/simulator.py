
#attributes
SPEED = 0.25
MOVES = 0
START = [0,0]
BOUNDARY = [[0,0],(0,200),(200,0),(200,200)]
VISTED = []
OBSTACLE = []


#input
#stub method to get obstacle on terminal
def getObstacle():
    val = input("Enter obstacle coordinate (x,y,image):\n")
    while (val!="END"):
        x = val.split(",")
        obsCoor = [int(x[0]),int(x[1])]
        OBSTACLE.append(obsCoor)
        val = input("Enter next coordinate (enter END to end):\n")

def findObstaclePth(OBSTACLE):


