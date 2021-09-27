package gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import algorithm.AStar;
import algorithm.MainConnect;
import algorithm.MainConnectSpare;
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

public class simulateHamiltonian implements Runnable {
	
    private AStar as;
    private NearestNeighbour nn;
    private Map map;
    private Robot robot;
    private main mGui;
    private float playSpeed;
    private Timer mTimer;
    private ArrayList<Cell> obsList;

    /**
     * This method is the non-default constructor to create simulateFastestPath thread class
     *
     * @param maGUI  The GUI object where the result should be displayed to.
     * @param ro     The robot object which specifies the detail of robot.
     * @param expMap The Map object that the robot have explored.
     */
    public simulateHamiltonian(main mGui, Robot ro, Map map, ArrayList<Cell> obsList) {

        this.map = map;
        this.mGui = mGui;
        this.robot = ro;
        this.obsList = obsList;
        this.playSpeed = 1 / mGui.getUserSpeed();
        
        
    }
    
	@Override
	public void run() {
        try {
            initialiseTimer();
            mGui.displayMsgToUI("Simulate Hamitonian Path Thread Started");

            //ArrayList<Cell> cellsInPath = fastestPath.findAllWPEndPaths(exploreMap);
            //String moveString = convertCellsToMovements(cellsInPath);
            //MainConnectSpare mc = new MainConnectSpare();
            //String test = mc.fullPath(this.obsList);//"HPW5E1"
            
            //testcode
            for(int i=0; i<obsList.size(); i++) {
    			map.setMapTargetCell(obsList.get(i).getRow(), obsList.get(i).getCol(), obsList.get(i).getObsDir());
    		}
    		
    		List<Cell> tarList = new ArrayList<Cell>();
    		
    		for (int i=0; i<map.getMap().length; i++) {
    			for (int j=0; j<map.getMap()[i].length; j++) {
    				if(map.getMap()[i][j].isTargetCell()) {
    					tarList.add(map.getMap()[i][j]);
    				}
    			}
    		}
    		tarList = NearestNeighbour.calculateDistance(tarList, robot);
    		
    		// get nearest Neighbour
    		ArrayList<Cell> nnList = NearestNeighbour.findNearestNeighbour(tarList);
    		
    		int[] tarHeadRArr = new int[nnList.size()]; 
    		int[] tarHeadCArr = new int[nnList.size()]; 
    		int[] tarHeadDirArr = new int[nnList.size()+1]; 
    		
    		for (int i=0; i<=nnList.size(); i++) {
    			if(i==0) {
    				tarHeadDirArr[0] = 1;
    			}
    			else {
    				tarHeadRArr[i-1] = nnList.get(i-1).getRow();
    				tarHeadCArr[i-1] = nnList.get(i-1).getCol();
    				tarHeadDirArr[i] = nnList.get(i-1).getHeadDir();
    			}
    		}
    		String movementDir = "";
    		for(int i=0; i<tarHeadRArr.length; i++ ) {
    			int k = 1;
    			AStar astar = new AStar(map, robot, tarHeadRArr[i], tarHeadCArr[i], tarHeadDirArr[i+1]);
    			astar.process();
    			String currMoveDir = astar.displaySolution();
    			
    			while(currMoveDir == "") {
    				DIRECTION rStartHeadDir = robot.getCurrDir();
    				String turnDir = "";
    				switch(rStartHeadDir) {
    				case NORTH:
    					if(k == 1) {
    						robot.setCurrDir(DIRECTION.EAST);
    						turnDir = "D";
    					}
    					else {
    						robot.setCurrDir(DIRECTION.WEST);
    						turnDir = "A";
    					}
    					break;
    				case SOUTH:
    					if(k == 1) {
    						robot.setCurrDir(DIRECTION.EAST);
    						turnDir = "A";
    					}
    					else {
    						robot.setCurrDir(DIRECTION.WEST);
    						turnDir = "D";
    					}
    					break;
    				case EAST:
    					if(k == 1) {
    						robot.setCurrDir(DIRECTION.NORTH);
    						turnDir = "A";
    					}
    					else {
    						robot.setCurrDir(DIRECTION.SOUTH);
    						turnDir = "D";
    					}
    					break;
    				case WEST:
    					if(k == 1) {
    						robot.setCurrDir(DIRECTION.NORTH);
    						turnDir = "D";
    					}
    					else {
    						robot.setCurrDir(DIRECTION.SOUTH);
    						turnDir = "A";
    					}
    					break;
    				}
    				astar = new AStar(map, robot, tarHeadRArr[i], tarHeadCArr[i], tarHeadDirArr[i+1]);
    				astar.process();
    				currMoveDir = astar.displaySolution() + turnDir;
    				k += 1;
    			}
    			movementDir =  ",V," + currMoveDir + "" + movementDir;
    			robot.setPosRow(tarHeadRArr[i]);
    			robot.setPosCol(tarHeadCArr[i]);
    			robot.setCurrDir(robot.intDirToConstantDir(tarHeadDirArr[i+1]));
    		}
    		robot.setPosRow(1);
			robot.setPosCol(1);
			robot.setCurrDir(robot.intDirToConstantDir(1));
    		System.out.println(movementDir);
            
            
            
            printFastestPathMovement(movementDir);
            //printFastestPathMovement(moveString);

            this.mTimer.cancel();
            this.mTimer.purge();

        } catch (InterruptedException ex) {
            this.mTimer.cancel();
            this.mTimer.purge();
            mGui.displayMsgToUI("Hamitonian Path Thread Interrupted!");

        } catch (Exception ex) {
            this.mTimer.cancel();
            this.mTimer.purge();
            mGui.displayMsgToUI("Hamitonian Path Thread unknown Error: " + ex.getMessage());

        }
		
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
    *
    */

   /**
    * This method will paint the current map object perceived by the robot and its current location to GUI
    * for the users to see. The frame rate changes according to the specified steps by the user.
    *
    * @throws InterruptedException If the connection gets interrupted.
    */
   private void displayToUI() throws InterruptedException {

       mGui.paintResult();
       Thread.sleep((long) (playSpeed * 1000));

   }  
   
   private void changeObstacleColor(int row, int col) throws InterruptedException {

       mGui.changeObstacleColor(row, col);
       Thread.sleep((long) (playSpeed * 1000));

   } 
   
   
   /**
    * This method convert the list of movement into a string instruction that the physical robot
    * could execute consecutively.
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
    * This method convert a path(List of cell) that the robot should travel along into a string
    * which consist of turns and movements to reach the destination.
    *
    * @param cellsInPath The arraylist of cell that forms a path the robot should take.
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
    * This method display the string movements instruction on the virutal robot to reach its destination
    *
    * @param moveString The string which specifies the consecutive movement that the robot should execute.
    * @throws InterruptedException If the connection gets interrupted.
    */
   private void printFastestPathMovement(String moveString) throws InterruptedException {

       // FPF6R0F1L0F2
       String[] arr = moveString.split("\\,");

       try {
           for (int i=arr.length-1; i >= 0; i--) {
        	   int a = 0;
               switch (arr[i]) {
               	   case "V":
               		   mGui.displayMsgToUI("Obstacle Scanned!");
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
