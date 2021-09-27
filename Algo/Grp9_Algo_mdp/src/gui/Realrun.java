package gui;

import java.util.Timer;

import communication.TCPComm2;
import entity.Cell;
import entity.Map;
import entity.Robot;

public class Realrun implements Runnable {

	private main mGui;
	private Map map;
	private Robot robot;
	private TCPComm2 tcpObj;
	private float playSpeed;
	private Timer mTimer;

	public Realrun(main mGui, Robot ro, Map map) {
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
			String msg = "";
			//checkandPlotRobot();
			int obstacleCount = 1;
			do {
				obstacleCount = checkandPlotOB();
				mGui.displayMsgToUI("Obstacle "+obstacleCount + " Plotted!");
			} while (obstacleCount != 0);
			displayToUI();
			mGui.displayMsgToUI("Initiating Astar and Nearest Neighbour Algorithm");
			
			

		}catch(

	InterruptedException e)
	{
		System.out.println("RealRun thread InterruptedException" + e.getMessage());
		e.printStackTrace();
		tcpObj.closeConnection();

	}catch(
	Exception e)
	{
		System.out.println("RealRun thread exception.." + e.getMessage());
		e.printStackTrace();
		tcpObj.closeConnection();

	}mGui.displayMsgToUI("RealRun Thread Ended Successfully!");

	}

	// ==================== Communication Methods with Android//
	// =======================

	/**
	 * This method will stall the program to read the input stream for the start
	 * coordinate of the robot from RPI. It will update the location of the robot
	 * object upon receiving from RPI.
	 *
	 * @throws Exception If the connection gets interrupted.
	 */
	private void checkandPlotRobot() throws Exception {
		String rmsg = "";
		// RO,[1,1,1], RO,[10,10,10]
		mGui.displayMsgToUI("Waiting for start coordinate...");
		do {
			rmsg = readMsg();
			if (rmsg.substring(0, 3).equals("RO,")) {
				String[] arr = rmsg.substring(4, rmsg.length() - 2).split(",");
				this.robot.setPosRow(Integer.parseInt(arr[0]));
				this.robot.setPosCol(Integer.parseInt(arr[1]));
				this.robot.setCurrDir(robot.intDirToConstantDir(Integer.parseInt(arr[2])));
				return;
			}

		} while (true);

	}

	/**
	 * This method will stall the program to read the input stream for the fastest
	 * path command from RPI.
	 *
	 * @throws Exception If the connection gets interrupted.
	 */
	private void waitForFastestPath() throws Exception {
		String rmsg = "";
		mGui.displayMsgToUI("Waiting for command to start FastestPath...");
		do {
			rmsg = readMsg();
			if (rmsg.substring(0, 3).equals("FP,")) {
				return;
			}
		} while (true);
	}

	/**
	 * This method will stall the program to read the input stream for the Obstacle
	 * coordinate from RPI. It will create the Obstacle on the map object upon
	 * receiving from RPI.
	 *
	 * @throws Exception If the connection gets interrupted.
	 */
	private int checkandPlotOB() throws Exception {
		String rmsg = "";
		// WP|[1,1,1] [row ,col, obDir]
		mGui.displayMsgToUI("Waiting for obstacles coordinate...");
		do {
			rmsg = readMsg();
			if (rmsg.substring(0, 3).equals("OB,")) {
				String[] arr = rmsg.substring(4, rmsg.length() - 2).split(",");
				mGui.addObstacle(Integer.parseInt(arr[1]), Integer.parseInt(arr[0]), Integer.parseInt(arr[3]));
				return 1;
			}
			else
			{
				String end = rmsg.substring(4,rmsg.length());
				if(end.equals("END"))
					return 0;
						
			}
		} while (true);
	}

	// ================= Communication Methods with RPI =======================

	/**
	 * This method establishes the connection with RPI via WiFi and will recursively
	 * attempt to connect until interrupted by GUI.
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
	 * This method stall the program to read the inputstream for messages from the
	 * RPI. This method will also send the MDF information and terminate the program
	 * upon receiving the command "N|" from RPI.
	 *
	 * @return The received message.
	 * @throws InterruptedException If the connection gets interrupted.
	 */
	private String readMsg() throws InterruptedException {

		String msg = "";
		do {
			msg = tcpObj.readMessage();
		} while (msg == null || msg.length() == 0);

//		if (msg.substring(0, 2).equals("N|")) {
//			sendMDFInfo();
//			throw new InterruptedException();
//		}
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
	 * This method concatenate the MDF1 and MDF2 of the map object and send it to
	 * RPI.
	 */
	private void sendMDFInfo() {
		String mdf2 = map.getMDF2();
		sendMsg("MDF|" + mdf2 + "|" + mdf2);
	}
	
    // ======================= GUI PAINTING ===================================

    /**
     * This method will paint the current map object perceived by the robot to GUI
     * for the users to see the current status of exploration.
     */
    private void displayToUI() {
        mGui.paintResult();
    }

}
