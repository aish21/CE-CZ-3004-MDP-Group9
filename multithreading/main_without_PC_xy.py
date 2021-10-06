
from PCConnection import *
from STMConnection import *
from config import *
from BluetoothConnection import *

from multiprocessing import Queue
from multiprocessing import Process

import sys
import time
import threading

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

		if (self.STM_thread.checkConnection() and STMmsg):
			self.STM_thread.writeToSTM(STMmsg)
			return True
		return True

	def receivefromSTM(self):
		while True:
			serialmsg = self.STM_thread.readFromSTM()
			serialmsgArray = serialmsg.split(",")
			header = serialmsgArray[0]

			if (self.STM_thread.checkConnection() and serialmsg != ''):
				print(serialmsgArray)
				self.sendtopc('FP,'+str(serialmsgArray[1]))
				self.sendtoandroid(serialmsgArray[1])
			else:
				print("Empty string! Not going to send.")

	def sendtopc(self, pcmsg):
		if (self.pc_thread.checkConnection() and pcmsg):
			self.pc_thread.writeToPC(pcmsg)

			return True
		return False

	def receivefrompc(self):
			while True:
				pcmsg = self.pc_thread.readFromPC()
				pcmsg = str(pcmsg)
				pcmsgArray = pcmsg.split(",")
				header = pcmsgArray[0]
				obDetails = []
				if (self.pc_thread.checkConnection() and pcmsg and header == 'FP'):
					#for i in pcmsgArray[1:3]:
						#obDetails.append(i) #get the obstacle x,y that is about to be scanned, remember that algo is 0-19 and android is 1-20
						#elif i == 'V':
							#start image rec
							#get a return value of the target ID
							#need to map the value of the obstacle x,y coordinate to obstaclesArray
							#send to android 'TARGET,obstacleID,targetID' as a string. TARGET is just a normal word.
							#return 'FP,V' back to algo to get next path section.
					for i in pcmsgArray[:0:-1]: #read instructions backwards due to path being backwards.
						if i == 'V':
							print('starting scan!')
							print('scan finished!')
							self.sendtopc('FP,V') #signify pseudo end of scan.
						else:
							self.sendtoSTM(i) #send instructions 1 by 1 to STM.
							time.sleep(1)
					#print(obDetails)
				else:
					print("Unknown message received! Header is not FP.")

	def sendtoandroid(self,androidmsg):
		if (self.android_thread.checkConnection() and androidmsg):
			self.android_thread.writeToAndroid(androidmsg)

			return True
		return False

	def receivefromandroid(self):
		obstaclesArray = []
		while True:
			btmsg = self.android_thread.readFromAndroid()
			btmsg = str(btmsg)
			if btmsg == 'OB,END':
				self.sendtopc('OB,END')
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
				obArray.append(rawbtmsgArray[i]) #append obstacleID,direction
			print(obArray)
			obstaclesArray.append(obArray[0:3]) #obstacleID, x, y, direction (in algo form)
			print('new obstacle added!\n',obstaclesArray)

			if (self.android_thread.checkConnection() and btmsg != "None"):
				print('sending OB,' + str(obArray[1:4]))
				self.sendtopc('OB,' + str(obArray[1:4])) #x and y coordinate and direction, must minus 1 from x and y to get algo's coordinates.


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

	try:
		main.startthread()
		main.maintaining()
	except KeyboardInterrupt:
		print("Exiting the program")
		main.close_all()

