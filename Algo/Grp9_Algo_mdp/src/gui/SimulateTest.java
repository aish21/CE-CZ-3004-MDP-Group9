package gui;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import algorithm.AStar;
import algorithm.MainConnect;
import algorithm.NearestNeighbour;
import constant.Constants.MOVEMENT;
import entity.Cell;
import entity.Map;
import entity.Robot;

/**
 * This method is to simulate the shortest path algorithm automatically in a loop for 100 times.
 * While running, the program will take a screen capture of the obstacles placed and save it as an image.
 * 
 * @author Goh Cheng Guan, Clive
 * @author Lau Zhen Jie
 * @version 1.0
 * @since 2020-10-19
 */

public class SimulateTest implements Runnable {

	private AStar as;
	private NearestNeighbour nn;
	private Map map;
	private Robot robot;
	private main mGui;
	private float playSpeed;
	private Timer mTimer;
	private ArrayList<Cell> obsList;
	private int visited;

	/**
	 * This method is the non-default constructor to create simulateFastestPath
	 * thread class
	 *
	 * @param maGUI  The GUI object where the result should be displayed to.
	 * @param ro     The robot object which specifies the detail of robot.
	 * @param expMap The Map object that the robot have explored.
	 */
	public SimulateTest(main mGui, Robot ro, Map map) {

		this.map = map;
		this.mGui = mGui;
		this.robot = ro;
		this.obsList = mGui.getObsList();
		this.playSpeed = 1/10;

	}

	@Override
	public void run() {
		int n = 0;
		int pass = 0;
		int fail = 0;
		do {
			try {
				initialiseTimer();

				// set obstacle
				int noOfObs = generateNoOfObstacle(4, 6);
				for (int i = 0; i < noOfObs; i++) {
					int ok = generateObstacle(mGui);
					while(ok!=0) {
						ok = generateObstacle(mGui);
					}
				}
				displayToUI();
				takeScreen(mGui,n);
				mGui.displayMsgToUI("Simulate Hamitonian Path Test Thread Started");

				// ArrayList<Cell> cellsInPath = fastestPath.findAllWPEndPaths(exploreMap);
				// String moveString = convertCellsToMovements(cellsInPath);
				MainConnect mc = new MainConnect();
				System.out.println("here working");
				String test = mc.fullPath(mGui);// "HPW5E1"
				visited = 0;
				printFastestPathMovement(test);
				// printFastestPathMovement(moveString);
				writeSussToFile(mGui);
				pass++;
				n++;
				mGui.clearArena();
				this.mTimer.cancel();
	            this.mTimer.purge();

			} catch (InterruptedException ex) {
				mGui.displayMsgToUI("Hamitonian Path Thread Interrupted!");
				try {
					writeToErrorFile(mGui,ex.getMessage());
				} catch (IOException e) {
					e.printStackTrace();
				}
				mGui.clearArena();
				fail++;
				n++;
				this.mTimer.cancel();
	            this.mTimer.purge();

			} catch (Exception ex) {
				mGui.displayMsgToUI("Hamitonian Path Thread unknown Error: " + ex.getMessage());
				try {
					writeToErrorFile(mGui,ex.getMessage());
				} catch (IOException e) {
					e.printStackTrace();
				}
				mGui.clearArena();
				fail++;
				n++;
				this.mTimer.cancel();
	            this.mTimer.purge();

			}
		} while (n != 100);
		WriteToFileFinal(pass,fail);
	}
	
	/**
	 * This method take a screenshot and save the image into image folder
	 * @param mGUI
	 * @param n number of the run in the simulation
	 */
	public void takeScreen(main mGUI, int n) {
		BufferedImage img = new BufferedImage(mGUI.getWidth(), mGUI.getHeight(), BufferedImage.TYPE_INT_RGB);
		mGUI.paint(img.getGraphics());
		File outputfile = new File("image\\saved"+n+".png");
		try {
			ImageIO.write(img, "png", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * write to a textfile of the error that occur for logging and debugging
	 * 
	 * @param mGui
	 * @param error
	 * @throws IOException
	 */
	public void writeToErrorFile(main mGui, String error) throws IOException  {
		String s = mGui.getObsList().toString();
		BufferedWriter writer = new BufferedWriter(new FileWriter("error.txt",true));
		writer.append(" ");
		writer.append(error);
		writer.append(s+"\n");
		writer.close();
	}
	
	/**
	 * write success message which contains the number of obstacle in the simulation and how many obstacles cleared.
	 * @param mGui
	 * @throws IOException
	 */
	public void writeSussToFile(main mGui) throws IOException  {
		String s = mGui.getObsList().toString();
		BufferedWriter writer = new BufferedWriter(new FileWriter("error.txt",true));
		writer.append(" ");
		writer.append("Number of obstacle: " + mGui.getObsList().size() + " visited: " + visited +" ");
		writer.append(s+"\n");
		writer.close();
	}
	
	/**
	 * write final text to the document to record total number of passed and failed runs
	 * @param pass
	 * @param fail
	 */
	public void WriteToFileFinal(int pass, int fail) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("error.txt",true));
			writer.append("Pass: " + pass +" Fail: " +fail);
			writer.append("\n");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * This method generate a random number of obstacles between min and max
	 * 
	 * @param min number of obstacles
	 * @param max number of obstacles
	 * @return number of obstacles
	 */
	public static int generateNoOfObstacle(int min, int max) {
		int range = (max - min) + 1;
		return (int) (Math.random() * range) + min;
	}

	/**
	 * generate the obstacles in the map grid
	 * @param mGui
	 * @return
	 */
	public static int generateObstacle(main mGui) {
		int range = 19;
		int row = 0;
		int col = 0;
		int obsdir = 1;
		int cellRow = 0;
		int cellCol = 0;
		
		do {
			row = (int) (Math.random() * range);
			col = (int) (Math.random() * range);
			obsdir = (int) (Math.random() * 4) + 1;
			
		}
		//while loop need to change
		while(row == 0 || row == 1 || row == 2 || col == 0 || col == 1 || col == 2);
		
		ArrayList<Cell> a = mGui.getObsList();
		for(int k = 0; k <a.size(); k ++) {
			cellRow = a.get(k).getRow();
			cellCol = a.get(k).getCol();
			if(row == cellRow && col == cellCol) {
				return 1;
			}
		}
		mGui.addObstacle(row, col, obsdir);
		return 0;
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

	/**
	 * This method will paint the current map object perceived by the robot and its
	 * current location to GUI for the users to see. The frame rate changes
	 * according to the specified steps by the user.
	 *
	 * @throws InterruptedException If the connection gets interrupted.
	 */
	private void displayToUI() throws InterruptedException {

		mGui.paintResult();
		Thread.sleep((long) (playSpeed * 1000));

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

		// FPF6R0F1L0F2
		String[] arr = moveString.split("\\,");
		try {
			for (int i = arr.length - 1; i >= 0; i--) {
				switch (arr[i]) {
				case "V":
					System.out.println("simulHam:" + mGui.getObstacleQueue().toString());
					Cell c = mGui.getObstacleQueue().poll();
					c.setVisited(true);
					map.getMap()[c.getRow()][c.getCol()] = c;
					visited++;
					mGui.displayMsgToUI("Obstacle[" + c.getCol() + "][" + c.getRow() + "] Scanned!");
					displayToUI();
					break;
				case "W":
					this.robot.move(MOVEMENT.FORWARD);
					displayToUI();
//                       for (int y = 0; y < Integer.parseInt(arr[i].substring(1, arr[i].length())); y++) {
//                           this.robot.move(MOVEMENT.FORWARD);
//                           displayToUI();
//                       }
					break;
				case "A":
					this.robot.turn(MOVEMENT.LEFT);
					displayToUI();
//                       for (int y = 0; y < Integer.parseInt(arr[i].substring(1, arr[i].length())); y++) {
//                           this.robot.turn(MOVEMENT.LEFT);
//                           displayToUI();
//                           //this.robot.move(MOVEMENT.FORWARD);
//                           //displayToUI();
//                       }
					break;
				case "D":
					this.robot.turn(MOVEMENT.RIGHT);
					displayToUI();
//                       for (int y = 0; y < Integer.parseInt(arr[i].substring(1, arr[i].length())); y++) {
//                           this.robot.turn(MOVEMENT.RIGHT);
//                           displayToUI();
//                           //this.robot.move(MOVEMENT.FORWARD);
//                           //displayToUI();
//                       }
					break;
				case "S":
					this.robot.move(MOVEMENT.BACKWARD);
					displayToUI();
//                       for (int y = 0; y < Integer.parseInt(arr[i].substring(1, arr[i].length())); y++) {
//                           this.robot.move(MOVEMENT.BACKWARD);
//                           displayToUI();
//                           //this.robot.turn(MOVEMENT.RIGHT);
//                           //displayToUI();
//                           //this.robot.move(MOVEMENT.FORWARD);
//                           //displayToUI();
//                       }
					break;
				case "B":
					this.robot.turn(MOVEMENT.RIGHT);
					displayToUI();
					this.robot.turn(MOVEMENT.RIGHT);
					displayToUI();
					this.robot.move(MOVEMENT.FORWARD);
					displayToUI();
//                       for (int y = 0; y < Integer.parseInt(arr[i].substring(1, arr[i].length())); y++) {
//                           this.robot.turn(MOVEMENT.RIGHT);
//                           displayToUI();
//                           this.robot.turn(MOVEMENT.RIGHT);
//                           displayToUI();
//                           this.robot.move(MOVEMENT.FORWARD);
//                           displayToUI();
//                       }
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
//                       for (int y = 0; y < Integer.parseInt(arr[i].substring(1, arr[i].length())); y++) {
//                           this.robot.move(MOVEMENT.FORWARD);
//                           displayToUI();
//                           this.robot.move(MOVEMENT.FORWARD);
//                           displayToUI();
//                           this.robot.move(MOVEMENT.FORWARD);
//                           displayToUI();
//                           this.robot.turn(MOVEMENT.LEFT);
//                           displayToUI();
//                           this.robot.move(MOVEMENT.FORWARD);
//                           displayToUI();
//                           this.robot.move(MOVEMENT.FORWARD);
//                           displayToUI();
//                           this.robot.move(MOVEMENT.FORWARD);
//                           displayToUI();
//                       }
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
//                       for (int y = 0; y < Integer.parseInt(arr[i].substring(1, arr[i].length())); y++) {
//                           this.robot.move(MOVEMENT.FORWARD);
//                           displayToUI();
//                           this.robot.move(MOVEMENT.FORWARD);
//                           displayToUI();
//                           this.robot.move(MOVEMENT.FORWARD);
//                           displayToUI();
//                           this.robot.turn(MOVEMENT.RIGHT);
//                           displayToUI();
//                           this.robot.move(MOVEMENT.FORWARD);
//                           displayToUI();
//                           this.robot.move(MOVEMENT.FORWARD);
//                           displayToUI();
//                           this.robot.move(MOVEMENT.FORWARD);
//                           displayToUI();
//                       }
					break;

				default:
					break;
				}
			}
		} catch (Exception ex) {
			System.out.println("printfastestPathmovement error:" + ex.getMessage());
		}

	}

}
