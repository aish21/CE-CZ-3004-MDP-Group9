import os
import sys
import time
import serial
from config import *

class STMConn(object):
	def __init__(self):
		self.port = "/dev/ttyUSB0"
		self.baudrate = 115200
		self.ser = None
		self.isConnected = False

	def checkConnection(self):
		return self.isConnected

	def connectSTM(self):
		tryConnect = True

		try:

			while tryConnect:
				print("Attempting connection to STM...")
				self.ser = serial.Serial(SER_PORT, self.baudrate, timeout=3)
				time.sleep(1)

				if (self.ser != 0):
					print("Connection to STM is successful")
					self.isConnected = True
					tryConnect = False
					break

		except Exception as e:
			print("Failed to connect to STM!")
			print(e)

	def disconnectSTM(self):
		try:
			if self.ser is not None:
				self.ser.close()
				self.isConnected = False
				print("STM has been disconnected")

		except Exception as error:
			print("Failed to disconnect from STM!" + str(error))

	def writeToSTM(self, message):
		try:
			if self.isConnected:
				self.ser.write(str.encode(message))
				print("message '%s' has been sent to STM" %message)
			else:
				print("No STM connections available!")
				self.connectSTM()

		except Exception as e:
			print("STM Write Error: %s" %str(e))
			self.connectSTM()

	def readFromSTM(self):
		try:
			while True:
				self.ser.flush()
				receivedData = self.ser.read(4)
				receivedData = receivedData.decode("utf-8")
				receivedData = str(receivedData)
				if receivedData == '' or receivedData == None:
					continue
				else:
					print("message '%s' has been received from STM" %receivedData)
					return receivedData

		except Exception as e:
			print("STM Read Error: %s" %str(e))
			self.connectSTM()

if __name__ == '__main__':
	STM = STMConn()
	STM.connectSTM()
	try:
		while True:
			STM.writeToSTM(input('what to send?'))
			STM.readFromSTM()
			time.sleep(1)
	except KeyboardInterrupt:
		print('disconnecting')
		STM.disconnectSTM()