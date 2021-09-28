package gui;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import algorithm.MainConnect;
import communication.TCPComm2;
import constant.Constants.MOVEMENT;
import entity.Cell;
import entity.Map;
import entity.Robot;

/**
 * @author Goh Cheng Guan, Clive
 * @author Lau Zhen Jie
 * @version 1.0
 * @since 2020-10-19
 */

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
			
//			//=====================RPI===================================================
//			
//			// ==================== Establish connection to RPI =========================
//			establishCommsToRPI();
//			// checkandPlotRobot();
//			//checkandPlotOBTest();
//			
//			// ==================== Waiting for Obstacles =========================
//			int obCount = 1;
//			do {
//				obCount = checkandPlotOB();
//				displayToUI();
//			} while (obCount != 0);
//			displayToUI();
//			
//			// ==================== Astar and Nearest Neighbour Algorithm =========================
//			mGui.displayMsgToUI("Initiating Astar and Nearest Neighbour\n Algorithm");
//			mGui.displayMsgToUI("Starting Fastest Path..");
//			MainConnect mc = new MainConnect();
//			System.out.println(mGui.getObsList());
//			String path = mc.fullPath(mGui);
//			if(path.charAt(path.length()-1) == ',') {
//				path = path.substring(0,path.length()-1);
//			}
//			sendMsg("FP,"+path);
//			waitForFastestPath(); // Waiting for fastest path command
//			
//			//TODO substring of path
//            //sendMsg(sendMsg("FP,"+path));
//			// ====================  =========================
			
			//=========================Testing====================================================
			initialiseTimer();
            mGui.displayMsgToUI("Real run test Thread Started");
            checkandPlotOBTest();
            //ArrayList<Cell> cellsInPath = fastestPath.findAllWPEndPaths(exploreMap);
            //String moveString = convertCellsToMovements(cellsInPath);
            MainConnect mc = new MainConnect();
            String test = mc.fullPath(mGui);//"HPW5E1"            
            printFastestPathMovement(test);
			this.mTimer.cancel();
			this.mTimer.purge();
		} catch (

		InterruptedException e) {
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

	/**
	 * This method display the string movements instruction on the virutal robot to
	 * reach its destination
	 *
	 * @param moveString The string which specifies the consecutive movement that
	 *                   the robot should execute.
	 * @throws InterruptedException If the connection gets interrupted.
	 */
	private void printFastestPathMovement(String moveString) throws InterruptedException {

		// FP|F6|R0|F1|L0|F2
		String[] arr = moveString.split("\\,");
		try {
			for (int i = arr.length - 1; i >= 0; i--) {
				switch (arr[i]) {
				case "V":
					Cell c = mGui.getObstacleQueue().poll();
					c.setVisited(true);
					map.getMap()[c.getRow()][c.getCol()] = c;
					mGui.displayMsgToUI("Obstacle[" + c.getCol() + "][" + c.getRow() + "] Scanned!");
					displayToUI();
					break;
				case "W":
					this.robot.move(MOVEMENT.FORWARD);
					displayToUI();
//	                       for (int y = 0; y < Integer.parseInt(arr[i].substring(1, arr[i].length())); y++) {
//	                           this.robot.move(MOVEMENT.FORWARD);
//	                           displayToUI();
//	                       }
					break;
				case "A":
					this.robot.turn(MOVEMENT.LEFT);
					displayToUI();
//	                       for (int y = 0; y < Integer.parseInt(arr[i].substring(1, arr[i].length())); y++) {
//	                           this.robot.turn(MOVEMENT.LEFT);
//	                           displayToUI();
//	                           //this.robot.move(MOVEMENT.FORWARD);
//	                           //displayToUI();
//	                       }
					break;
				case "D":
					this.robot.turn(MOVEMENT.RIGHT);
					displayToUI();
//	                       for (int y = 0; y < Integer.parseInt(arr[i].substring(1, arr[i].length())); y++) {
//	                           this.robot.turn(MOVEMENT.RIGHT);
//	                           displayToUI();
//	                           //this.robot.move(MOVEMENT.FORWARD);
//	                           //displayToUI();
//	                       }
					break;
				case "S":
					this.robot.move(MOVEMENT.BACKWARD);
					displayToUI();
//	                       for (int y = 0; y < Integer.parseInt(arr[i].substring(1, arr[i].length())); y++) {
//	                           this.robot.move(MOVEMENT.BACKWARD);
//	                           displayToUI();
//	                           //this.robot.turn(MOVEMENT.RIGHT);
//	                           //displayToUI();
//	                           //this.robot.move(MOVEMENT.FORWARD);
//	                           //displayToUI();
//	                       }
					break;
				case "B":
					this.robot.turn(MOVEMENT.RIGHT);
					displayToUI();
					this.robot.turn(MOVEMENT.RIGHT);
					displayToUI();
					this.robot.move(MOVEMENT.FORWARD);
					displayToUI();
//	                       for (int y = 0; y < Integer.parseInt(arr[i].substring(1, arr[i].length())); y++) {
//	                           this.robot.turn(MOVEMENT.RIGHT);
//	                           displayToUI();
//	                           this.robot.turn(MOVEMENT.RIGHT);
//	                           displayToUI();
//	                           this.robot.move(MOVEMENT.FORWARD);
//	                           displayToUI();
//	                       }
					break;
				case "Q":
					this.robot.move(MOVEMENT.FORWARD);
					displayToUI();
					this.robot.move(MOVEMENT.FORWARD);
					displayToUI();
					this.robot.move(MOVEMENT.FORWARD);
					displayToUI();
					this.robot.turn(MOVEMENT.LEFT);
					displayToUI();
					this.robot.move(MOVEMENT.FORWARD);
					displayToUI();
					this.robot.move(MOVEMENT.FORWARD);
					displayToUI();
					this.robot.move(MOVEMENT.FORWARD);
					displayToUI();
//	                       for (int y = 0; y < Integer.parseInt(arr[i].substring(1, arr[i].length())); y++) {
//	                           this.robot.move(MOVEMENT.FORWARD);
//	                           displayToUI();
//	                           this.robot.move(MOVEMENT.FORWARD);
//	                           displayToUI();
//	                           this.robot.move(MOVEMENT.FORWARD);
//	                           displayToUI();
//	                           this.robot.turn(MOVEMENT.LEFT);
//	                           displayToUI();
//	                           this.robot.move(MOVEMENT.FORWARD);
//	                           displayToUI();
//	                           this.robot.move(MOVEMENT.FORWARD);
//	                           displayToUI();
//	                           this.robot.move(MOVEMENT.FORWARD);
//	                           displayToUI();
//	                       }
					break;
				case "E":
					this.robot.move(MOVEMENT.FORWARD);
					displayToUI();
					this.robot.move(MOVEMENT.FORWARD);
					displayToUI();
					this.robot.move(MOVEMENT.FORWARD);
					displayToUI();
					this.robot.turn(MOVEMENT.RIGHT);
					displayToUI();
					this.robot.move(MOVEMENT.FORWARD);
					displayToUI();
					this.robot.move(MOVEMENT.FORWARD);
					displayToUI();
					this.robot.move(MOVEMENT.FORWARD);
					displayToUI();
//	                       for (int y = 0; y < Integer.parseInt(arr[i].substring(1, arr[i].length())); y++) {
//	                           this.robot.move(MOVEMENT.FORWARD);
//	                           displayToUI();
//	                           this.robot.move(MOVEMENT.FORWARD);
//	                           displayToUI();
//	                           this.robot.move(MOVEMENT.FORWARD);
//	                           displayToUI();
//	                           this.robot.turn(MOVEMENT.RIGHT);
//	                           displayToUI();
//	                           this.robot.move(MOVEMENT.FORWARD);
//	                           displayToUI();
//	                           this.robot.move(MOVEMENT.FORWARD);
//	                           displayToUI();
//	                           this.robot.move(MOVEMENT.FORWARD);
//	                           displayToUI();
//	                       }
					break;

				default:
					break;
				}
			}
		} catch (Exception ex) {
			System.out.println("printfastestPathmovement error:" + ex.getMessage());
		}

	}

	// ==================== Communication Methods with Android////
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

	private void checkandPlotOBTest() throws Exception {
		String msg1 = "OB,['5','9','4']";
		String msg2 = "OB,['7', '14', '3']";
		String msg3 = "OB,['12', '9', '2']";
		String msg4 = "OB,['15', '4', '2']";
		String msg5 = "OB,['15', '15', '2']";
		String msg6 = "OB,END";

		ArrayList<String> a = new ArrayList<String>();
		a.add(msg1);
		a.add(msg2);
		a.add(msg3);
		a.add(msg4);
		a.add(msg5);
		a.add(msg6);

		// RO,[1,1,1], RO,[10,10,10]
		mGui.displayMsgToUI("Waiting for start coordinate...");
		int i = 1;
		for (int j = 0; j < a.size(); j++) {
			if (a.get(j).substring(0, 3).equals("OB,")) {
				if (a.get(j).equals("OB,END")){
					System.out.println("OB,END");
				}
				else
				{
					
					String[] arr = a.get(j).substring(4, a.get(j).length() - 2).split(",");
					int col = Integer.parseInt(arr[0].replaceAll("[^a-zA-Z0-9]", ""));
					int row = Integer.parseInt(arr[1].replaceAll("[^a-zA-Z0-9]", ""));
					int dir = Integer.parseInt(arr[2].replaceAll("[^a-zA-Z0-9]", ""));
					mGui.addObstacle(col, row, dir);
				}
			}
		}

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
		// OB,[1,1,1] [row ,col, obDir]
		mGui.displayMsgToUI("Waiting for obstacles coordinate...");
		rmsg = readMsg();
		if (rmsg.substring(0, 3).equals("OB,")) {
			if (rmsg.equals("OB,END\n")) {
				return 0;
			}
			else
			{
				String[] arr = rmsg.substring(4, rmsg.length() - 2).split(",");
				int col = Integer.parseInt(arr[0].replaceAll("[^a-zA-Z0-9]", ""));
				int row = Integer.parseInt(arr[1].replaceAll("[^a-zA-Z0-9]", ""));
				int dir = Integer.parseInt(arr[2].replaceAll("[^a-zA-Z0-9]", ""));
				mGui.addObstacle(row, col, dir);
				return 1;
			}
			
		}
		return 1;
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

		tcpObj.sendMessage(msg);
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
	private void displayToUI() throws InterruptedException {
		mGui.paintResult();
		Thread.sleep((long) (playSpeed * 1000));
	}

	/**
	 * This method create a timer object to display the time elapsed on the GUi.
	 */
	private void initialiseTimer() {
		/* Count up */
		this.mTimer = new Timer();
		this.mTimer.scheduleAtFixedRate(new TimerTask() {
			private long startTime = System.currentTimeMillis();
			private long timeElapsed;

			/* Update timer every second */
			@Override
			public void run() {

				timeElapsed = (System.currentTimeMillis() - startTime) / 1000;
			}
		}, 0, 1000);
	}

}
