import socket
import sys
import time
import threading
from config import *

class pcComm(object):

	def __init__(self):
		self.ip_address = WIFI_IP
		self.port = WIFI_PORT
		self.isConnected= False

	def checkConnection(self):
		return self.isConnected

	def connectPC(self):
		try:
			self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
			self.socket.bind((self.ip_address, self.port))
			self.socket.listen(1)
			print("Waiting for connection from PC...")

			self.clientSocket, self.address = self.socket.accept()
			print("Connected to PC with the IP Address: ", self.address)
			self.isConnected = True

		except Exception as e:
			print("Failed to connect to PC!")
			print("Try again in a while!")

	def disconnectPC(self):
		try:
			if self.socket:
				self.socket.close()
				print("closing server socket")

			if self.clientSocket:
				self.clientSocket.close()
				print("closing client socket")

			self.isConnected = False

		except Exception as e:
			print("Failed to disconnect from PC!")
			print(e)

	def writeToPC(self, message):
		try:
			if self.isConnected:
				message = str(message)
				messageToPC = str.encode(message + '\n')
				self.clientSocket.sendto(messageToPC, self.address)
				print("message '%s' has been sent to PC" %message)
			else:
				print("No socket connections!")
				self.connectPC()

		except Exception as e:
			print('PC Write Error: %s' % str(e))
			self.connectPC()

	def readFromPC(self):
		try:
			receivedData = self.clientSocket.recv(1024)
			receivedData = receivedData.decode('utf-8')

			if receivedData:
				print("message '%s' has been received from PC" %receivedData)
				return receivedData
			else:
				print("No transmission from PC, attempting to reconnect")
				self.disconnectPC()
				self.connectPC()
				return receivedData

		except Exception as e:
			print("PC Read Error: %s" %str(e))
			self.connectPC()

if __name__ == '__main__':
	PC = pcComm()
	PC.connectPC()
	try:
		print("after connect")
		while True:
			print('before read from pc')
			PC.writeToPC(input('what to send?'))
			PC.readFromPC()
			print('after read from pc')
			time.sleep(5)
	except KeyboardInterrupt:
		print('disconnecting')
		PC.disconnectPC()