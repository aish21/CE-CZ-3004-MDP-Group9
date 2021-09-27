package gui;

import java.util.Timer;

import communication.TCPComm2;
import entity.Map;
import entity.Robot;

public class testConnect implements Runnable {

    private main mGui;
    private Map map;
    private Robot robot;
    private TCPComm2 tcpObj;
    private float playSpeed;
    private Timer mTimer;
    
    public testConnect(main mGui, Robot ro, Map map) {
    	this.map = map;
        this.mGui = mGui;
        this.robot = ro;
        this.playSpeed = 1 / mGui.getUserSpeed();
        this.tcpObj = TCPComm2.getInstance();
    }
    
    
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			 establishCommsToRPI();
			 while(true) {
				 String msg = tcpObj.readMessage();
				 mGui.displayMsgToUI(msg);
			 }
		}
		catch (InterruptedException e) {
            System.out.println("RealRun thread InterruptedException" + e.getMessage());
            e.printStackTrace();
            tcpObj.closeConnection();

        } catch (Exception e) {
            System.out.println("RealRun thread exception.." + e.getMessage());
            e.printStackTrace();
            tcpObj.closeConnection();

        }
		mGui.displayMsgToUI("RealRun Thread Ended Successfully!");
		
	}
	
	
    // ================= Communication Methods with RPI =======================

    /**
     * This method establishes the connection with RPI via WiFi and will recursively attempt
     * to connect until interrupted by GUI.
     *
     * @throws InterruptedException If the connection gets interrupted.
     */
    private void establishCommsToRPI() throws InterruptedException {
        String msg = "";

        do {
            mGui.displayMsgToUI("Establishing connection to RPI..  :D ");
            msg = this.tcpObj.establishConnection();
            if (msg.length() != 0) {
                mGui.displayMsgToUI(msg);

                Thread.sleep((long) (1 * 1000));

            } else {
                mGui.displayMsgToUI("Connected Successfully :DD ");
                break;
            }

        } while (!Thread.currentThread().isInterrupted());
    }

    /**
     * This method stall the program to read the inputstream for messages from the RPI.
     * This method will also send the MDF information and terminate the program upon receiving the command "N|" from RPI.
     *
     * @return The received message.
     * @throws InterruptedException If the connection gets interrupted.
     */
    private String readMsg() throws InterruptedException {

        String msg = "";
        do {
            msg = tcpObj.readMessage();
        } while (msg == null || msg.length() == 0);

        if (msg.substring(0, 2).equals("N|")) {
            sendMDFInfo();
            throw new InterruptedException();
        }
        mGui.displayMsgToUI("Received: " + msg);
        return msg;
    }

    /**
     * This method will send the string parameter as a message to RPI.
     *
     * @param msg The string message to be transmitted to RPI.
     */
    private void sendMsg(String msg) {

        tcpObj.sendMessage(msg + "!");
        mGui.displayMsgToUI("Sent: " + msg);

    }
    
    /**
     * This method concatenate the MDF1 and MDF2 of the map object
     * and send it to RPI.
     */
    private void sendMDFInfo() {
        String mdf2 = map.getMDF2();
        sendMsg("MDF|" + mdf2 + "|" + mdf2);
    }

}
