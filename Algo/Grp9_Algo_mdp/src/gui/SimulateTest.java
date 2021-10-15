package gui;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import algorithm.AStar;
import algorithm.MainConnect;
import algorithm.NearestNeighbour;
import constant.Constants.DIRECTION;
import constant.Constants.MOVEMENT;
import entity.Cell;
import entity.Map;
import entity.Robot;
import gui.main;

/**
 * @author Goh Cheng Guan, Cliev
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
	
	public void writeToErrorFile(main mGui, String error) throws IOException  {
		String s = mGui.getObsList().toString();
		BufferedWriter writer = new BufferedWriter(new FileWriter("error.txt",true));
		writer.append(" ");
		writer.append(error);
		writer.append(s+"\n");
		writer.close();
	}
	
	public void writeSussToFile(main mGui) throws IOException  {
		String s = mGui.getObsList().toString();
		BufferedWriter writer = new BufferedWriter(new FileWriter("error.txt",true));
		writer.append(" ");
		writer.append("Number of obstacle: " + mGui.getObsList().size() + " visited: " + visited +" ");
		writer.append(s+"\n");
		writer.close();
	}
	
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

	public static int generateNoOfObstacle(int min, int max) {
		int range = (max - min) + 1;
		return (int) (Math.random() * range) + min;
	}

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
	 * This method convert the list of movement into a string instruction that the
	 * physical robot could execute consecutively.
	 *
	 * @param fastestPathMovements Arraylist of movement to reach the destination
	 * @return String instruction that physical robot could execute consecutively.
	 */
	public String parseFPMovement(ArrayList<MOVEMENT> fastestPathMovements) {
		int i = 0;
		int j = 0;
		int counter = 1;
		String result = "HP";

		while (i < fastestPathMovements.size()) {

			switch (fastestPathMovements.get(i)) {

			case FORWARD:
				result += "W";
				break;
			case LEFT:
				result += "A";
				break;
			case RIGHT:
				result += "D";
				break;
			case BACKWARD:
				result += "S";
				break;
			default:
				break;

			}
			for (j = i + 1; j < fastestPathMovements.size(); j++) {
				if (fastestPathMovements.get(i) == fastestPathMovements.get(j)) {
					counter++;
				} else {
					break;
				}

			}

			if (fastestPathMovements.get(i) == MOVEMENT.FORWARD && counter < 10) {
				result += "0" + Integer.toString(counter);
			} else {
				result += Integer.toString(counter);
			}
			i = j;
			result += "";
			counter = 1;

		}
		System.out.println("R:" + result);
		return result;
	}

	/**
	 * This method convert a path(List of cell) that the robot should travel along
	 * into a string which consist of turns and movements to reach the destination.
	 *
	 * @param cellsInPath The arraylist of cell that forms a path the robot should
	 *                    take.
	 * @return String that consist of turns and movement to reach the destination
	 */
	public String convertCellsToMovements(ArrayList<Cell> cellsInPath) {

		Robot mBot = new Robot(this.robot.getPosRow(), this.robot.getPosCol(), this.robot.getCurrDir());
		int currRow = mBot.getPosRow();
		int currCol = mBot.getPosCol();

		ArrayList<MOVEMENT> fastestPathMovements = new ArrayList<MOVEMENT>();

		for (int i = 0; i < cellsInPath.size(); i++) {
			int destRow = cellsInPath.get(i).getRow();
			int destCol = cellsInPath.get(i).getCol();
			switch (mBot.getCurrDir()) {
			case NORTH:
				if (currCol == destCol) {
					if (currRow < destRow) {
						fastestPathMovements.add(MOVEMENT.FORWARD);
						mBot.move(MOVEMENT.FORWARD);
					} else if (currRow > destRow) {
						fastestPathMovements.add(MOVEMENT.BACKWARD);

						mBot.turn(MOVEMENT.RIGHT);
						mBot.turn(MOVEMENT.RIGHT);
						mBot.move(MOVEMENT.FORWARD);
					}
				} else if (currRow == destRow) {
					if (currCol < destCol) {
						fastestPathMovements.add(MOVEMENT.RIGHT);

						mBot.turn(MOVEMENT.RIGHT);
						mBot.move(MOVEMENT.FORWARD);
					} else if (currCol > destCol) {
						fastestPathMovements.add(MOVEMENT.LEFT);

						mBot.turn(MOVEMENT.LEFT);
						mBot.move(MOVEMENT.FORWARD);
					}
				}
				break;
			case SOUTH:
				if (currCol == destCol) {
					if (currRow < destRow) {
						fastestPathMovements.add(MOVEMENT.BACKWARD);

						mBot.turn(MOVEMENT.RIGHT);
						mBot.turn(MOVEMENT.RIGHT);
						mBot.move(MOVEMENT.FORWARD);
					} else if (currRow > destRow) {
						fastestPathMovements.add(MOVEMENT.FORWARD);
						mBot.move(MOVEMENT.FORWARD);
					}
				} else if (currRow == destRow) {
					if (currCol < destCol) {
						fastestPathMovements.add(MOVEMENT.LEFT);

						mBot.turn(MOVEMENT.LEFT);
						mBot.move(MOVEMENT.FORWARD);
					} else if (currCol > destCol) {
						fastestPathMovements.add(MOVEMENT.RIGHT);

						mBot.turn(MOVEMENT.RIGHT);
						mBot.move(MOVEMENT.FORWARD);
					}
				}
				break;
			case EAST:
				if (currCol == destCol) {
					if (currRow < destRow) {
						fastestPathMovements.add(MOVEMENT.LEFT);

						mBot.turn(MOVEMENT.LEFT);
						mBot.move(MOVEMENT.FORWARD);
					} else if (currRow > destRow) {
						fastestPathMovements.add(MOVEMENT.RIGHT);

						mBot.turn(MOVEMENT.RIGHT);
						mBot.move(MOVEMENT.FORWARD);
					}
				} else if (currRow == destRow) {
					if (currCol < destCol) {
						fastestPathMovements.add(MOVEMENT.FORWARD);
						mBot.move(MOVEMENT.FORWARD);
					} else if (currCol > destCol) {
						fastestPathMovements.add(MOVEMENT.BACKWARD);

						mBot.turn(MOVEMENT.RIGHT);
						mBot.turn(MOVEMENT.RIGHT);
						mBot.move(MOVEMENT.FORWARD);
					}
				}
				break;
			case WEST:
				if (currCol == destCol) {
					if (currRow < destRow) {
						fastestPathMovements.add(MOVEMENT.RIGHT);

						mBot.turn(MOVEMENT.RIGHT);
						mBot.move(MOVEMENT.FORWARD);
					} else if (currRow > destRow) {
						fastestPathMovements.add(MOVEMENT.LEFT);

						mBot.turn(MOVEMENT.LEFT);
						mBot.move(MOVEMENT.FORWARD);
					}
				} else if (currRow == destRow) {
					if (currCol < destCol) {
						fastestPathMovements.add(MOVEMENT.BACKWARD);

						mBot.turn(MOVEMENT.RIGHT);
						mBot.turn(MOVEMENT.RIGHT);
						mBot.move(MOVEMENT.FORWARD);
					} else if (currCol > destCol) {
						fastestPathMovements.add(MOVEMENT.FORWARD);
						mBot.move(MOVEMENT.FORWARD);
					}
				}
				break;
			}

			currRow = mBot.getPosRow();
			currCol = mBot.getPosCol();

		}

		String result = parseFPMovement(fastestPathMovements);
		return result;
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
