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
		self.STM_thread = STMConn()
		self.android_thread = bluetoothComm()


		self.STM_thread.connectSTM()
		self.android_thread.connectBluetooth()


		time.sleep(2)


	def sendtoSTM(self, STMmsg):
		if (self.STM_thread.checkConnection()):
			if STMmsg == 'i':
				self.STM_thread.writeToSTM(STMmsg)
				return True
			elif STMmsg == 'g':
				self.STM_thread.writeToSTM(STMmsg)
				return True
			elif STMmsg == 'X':
				self.STM_thread.writeToSTM(STMmsg)
				return True
		return True


	def receivefromandroid(self):
		global imagesToScan
		while True:
			btmsg = self.android_thread.readFromAndroid()
			btmsg = str(btmsg)
			if btmsg == 'STM,I':
				self.sendtoSTM('i')
			elif btmsg == 'g':
				self.sendtoSTM('g')
			elif btmsg == 'x':
				self.sendtoSTM('X')

	def startthread(self):

		#Android read and write thread
		read_android_thread = threading.Thread(target = self.receivefromandroid, args = (), name = "read_android_thread")


		#STM read and write thread
		write_STM_thread = threading.Thread(target = self.sendtoSTM, args = (), name = "write_STM_thread")

		#Set daemon for all thread

		read_android_thread.daemon = True

		write_STM_thread.daemon = True


		#start running thread for Android
		read_android_thread.start()

		#start running thread for STM
		#write_STM_thread.start()

	def close_all(self):
		#close component connection
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


