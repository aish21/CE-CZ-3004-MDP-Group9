
from PCConnection import *
from STMConnection import *
from config import *
from BluetoothConnection import *
import detect
import collage

from multiprocessing import Queue
from multiprocessing import Process

import sys
import time
import threading

instrCounter = 0
instrLen = 0
obDetails = []
imagesScanned = 0
imagesToScan = 0

class RPi(threading.Thread):
	def __init__(self):
		threading.Thread.__init__(self)
		self.debug = False
		#Initialization of component
		self.android_thread = bluetoothComm()
		self.pc_thread = pcComm()
		self.STM_thread = STMConn()

		self.android_thread.connectBluetooth()
		self.pc_thread.connectPC()
		self.STM_thread.connectSTM()

		time.sleep(2)


	def sendtoSTM(self, STMmsg):
		if (self.STM_thread.checkConnection()):
			if STMmsg == 'Q' or STMmsg == 'W' or STMmsg == 'E' or STMmsg == 'A' or STMmsg == 'S' or STMmsg == 'D':
				self.STM_thread.writeToSTM(STMmsg)
				return True
		return True

	def receivefromSTM(self):
		global instrCounter
		global instrLen
		global imagesScanned
		global imagesToScan
		while True:
			serialmsg = self.STM_thread.readFromSTM()
			serialmsgArray = serialmsg.split(",")
			header = serialmsgArray[0]

			if (self.STM_thread.checkConnection() and serialmsg != ''):
				self.sendtopc('FP,'+str(serialmsgArray[1]))
				self.sendtoandroid(serialmsgArray[1])
				instrCounter += 1
				print('current instruction counter:', instrCounter)
				print('current instruction len', instrLen)
				if instrCounter == instrLen:
					instrCounter = 0 #reset instructions counter once scan since end alr.
					obstacleID = 1 #start with 1st obstacleID everytime starting scan

					#start image rec
					targetID = detect.main() #get a return value of the target ID
					for i in obstaclesArray:
						print(obDetails)
						print(i)
						if obDetails[0] == i[1] and obDetails[1] == i[2]:
							print('Scanned obstacle %d!' %obstacleID)
							break
						else:
							obstacleID += 1
							continue
					self.sendtoandroid('TARGET,%d,%d' %(obstacleID,targetID)) #send to android 'TARGET,obstacleID,targetID' as a string. TARGET is just a normal word.
					self.sendtopc('FP,V') #to tell algo scan is finished
					imagesScanned += 1
					print('Images scanned so far:', imagesScanned)
					if imagesScanned == imagesToScan:
						collage.stitch()

			else:
				print("Empty string! Not going to send.")

	def sendtopc(self, pcmsg):
		if (self.pc_thread.checkConnection() and pcmsg):
			self.pc_thread.writeToPC(pcmsg)

			return True
		return False

	def receivefrompc(self):
		global instrCounter
		global instrLen
		global obDetails
		while True:
			pcmsg = self.pc_thread.readFromPC()
			pcmsg = str(pcmsg)
			pcmsgArray = pcmsg.split(",")
			print(pcmsgArray)
			header = pcmsgArray[0]
			instrLen = len(pcmsgArray) - 4
			print('number of instructions = ',instrLen)
			obDetails = []
			if (self.pc_thread.checkConnection() and pcmsg and header == 'FP'):
				for i in pcmsgArray[1:3]:
					obDetails.append(str(int(i)+1)) #get the obstacle x,y that is about to be scanned, +1 to x and y to get android coordinates.
				for i in pcmsgArray[:2:-1]:
					if i == 'V':
						print('starting image rec once last instruction received!')
					else:
						self.sendtoSTM(i) #send instructions 1 by 1 to STM.
						time.sleep(1)
			else:
				print("Unknown message received! Header is not FP.")

	def sendtoandroid(self,androidmsg):
		if (self.android_thread.checkConnection() and androidmsg):
			self.android_thread.writeToAndroid(androidmsg)

			return True
		return False

	def receivefromandroid(self):
		global imagesToScan
		while True:
			btmsg = self.android_thread.readFromAndroid()
			btmsg = str(btmsg)
			if btmsg == 'OB,END': #no more obstacles
				self.sendtopc('OB,END') #tell PC to start calculating path.
				imagesToScan = len(obstaclesArray)
				print('Number of obstacles:',imagesToScan)
				break
			rawbtmsgArray = btmsg.split(",") #[PC,obstacleID,x,y,direction]
			print(rawbtmsgArray) 
			while True:
				if rawbtmsgArray[0] != 'PC': #continue to read until the most updated and correct one.
					btmsg = self.android_thread.readFromAndroid()
					btmsg = str(btmsg)
					rawbtmsgArray = btmsg.split(",")
					print(rawbtmsgArray)
				else:
					break
			obArray = []
			for i in range(1,5):
				if rawbtmsgArray[1] == 'RP':
					break
				else:
					obArray.append(rawbtmsgArray[i]) #append obstacleID,x,y,direction
			print('printing obArray')
			print(obArray)
					

			if (self.android_thread.checkConnection() and obArray):
				obstaclesArray.append(obArray[0:3]) #obstacleID, x, y, direction (in algo form)
				print('new obstacle added!\n',obstaclesArray)
				print('sending OB,' + str(obArray[1:4]))
				self.sendtopc('OB,' + str(obArray[1:4])) #x and y coordinate and direction, but algo will minus 1 on their side.



	def startthread(self):

		#PC read and write thread
		read_pc_thread = threading.Thread(target = self.receivefrompc, args = (), name = "read_pc_thread")
		write_pc_thread = threading.Thread(target = self.sendtopc, args = (), name = "write_pc_thread")

		#Android read and write thread
		read_android_thread = threading.Thread(target = self.receivefromandroid, args = (), name = "read_android_thread")
		write_android_thread = threading.Thread(target = self.sendtoandroid, args = (), name = "write_android_thread")


		#STM read and write thread
		read_STM_thread = threading.Thread(target = self.receivefromSTM, args = (), name = "read_STM_thread")
		write_STM_thread = threading.Thread(target = self.sendtoSTM, args = (), name = "write_STM_thread")

		#Set daemon for all thread
		read_pc_thread.daemon = True
		write_pc_thread.daemon = True

		read_android_thread.daemon = True
		write_android_thread.daemon = True

		read_STM_thread.daemon = True
		write_STM_thread.daemon = True

		#start running the thread for PC
		read_pc_thread.start()

		#start running thread for Android
		read_android_thread.start()

		#start running thread for STM
		read_STM_thread.start()

	def close_all(self):
		#close component connection
		self.pc_thread.disconnectPC()
		self.android_thread.disconnectBluetooth()
		self.STM_thread.disconnectSTM()

	def maintaining(self):
		while True:
			time.sleep(1)

if __name__ == "__main__":
	#Start the progam
	print("Starting the program...")
	main = RPi()
	obstaclesArray = []

	try:
		main.startthread()
		main.maintaining()
	except KeyboardInterrupt:
		print("Exiting the program")
		main.close_all()


