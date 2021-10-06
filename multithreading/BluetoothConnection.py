import bluetooth
import threading
import time
import sys
import os
from config import *

class bluetoothComm(object):

	def __init__(self):
		self.serverSoc = None
		self.clientSoc = None
		self.isConnected = False

	def checkConnection(self):
		return self.isConnected

	def connectBluetooth(self):
		port = BLUETOOTH_PORT
		try:
			self.serverSoc = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
			self.serverSoc.bind(("192.168.9.9",1))
			self.serverSoc.settimeout(30)
			self.serverSoc.listen(1)
			self.port = self.serverSoc.getsockname()[1]

			bluetooth.advertise_service(self.serverSoc, "MDP9-Server",
				service_id = "00001101-0000-1000-8000-00805F9B34FB",
				service_classes=["00001101-0000-1000-8000-00805F9B34FB", bluetooth.SERIAL_PORT_CLASS],
				profiles = [bluetooth.SERIAL_PORT_PROFILE],)
			print("Waiting for BT connection on RFCOMM channel %d" %self.port)
			self.serverSoc.settimeout(15)
			self.clientSoc, clientInfo = self.serverSoc.accept()
			
			self.serverSoc.settimeout(15)
			print("Accepted connection from ", clientInfo)
			#self.serverSoc.close()
			#self.serverSoc = None
			self.isConnected = True

		except Exception as e:
			print("Failed to connect to Bluetooth!")
			print(e)
			self.serverSoc.close()
			self.serverSoc = None

	def disconnectBluetooth(self):
		try:
			if self.serverSoc is not None:
				self.serverSoc.close()
				self.serverSoc = None
				print("Bluetooth server has been disconnected")
			if self.clientSoc is not None:
				self.clientSoc.close()
				self.clientSoc = None
				print("Bluetooth client has been disconnected")
			self.isConnected = False

		except Exception as error:
			print("Failed to disconnect from Bluetooth!" + str(error))

	def writeToAndroid(self, message):
		try:
			if self.isConnected:
				self.clientSoc.send(str(message))
				print("message '%s' has been sent to Android" %message)
			else:
				print("No bluetooth connections available!")
				self.connectBluetooth()

		except BluetoothError as e:
			print("bluetooth error! Re-establishing connection.")
			self.connectBluetooth()

	def readFromAndroid(self):
		try:
			receivedData = self.clientSoc.recv(1024)
			if receivedData is None:
				print("No message received!")

			receivedData = receivedData.decode("utf-8")
			receivedData = str(receivedData)
			print("message '%s' has been received from Android" %receivedData)
			return receivedData

		except BluetoothError as e:
			print("bluetooth error! Re-establishing connection.")
			self.connectBluetooth()

	def disconnect(self):
		self.clientSoc.close()
		print("closing client socket")
		self.serverSoc.close()
		print("closing server socket")
		self.isConnected = False

if __name__ == '__main__':
	Android = bluetoothComm()
	Android.connectBluetooth()
	try:
		while True:
			Android.readFromAndroid()
			Android.writeToAndroid(input('what to send?'))
			time.sleep(5)
	except KeyboardInterrupt:
		print('disconnecting')
		Android.disconnectBluetooth()
