package gui;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import algorithm.*;
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

public class simulateShortestPath implements Runnable {
	
    AStar as;
    NearestNeighbour nn;
    Map map;
    Robot robot;
    main mGui;
    private float playSpeed;
    Timer mTimer;
    ArrayList<Cell> obsList;

    /**
     * This method is the non-default constructor to create simulateFastestPath thread class
     *
     * @param maGUI  The GUI object where the result should be displayed to.
     * @param ro     The robot object which specifies the detail of robot.
     * @param expMap The Map object that the robot have explored.
     */
    public simulateShortestPath(main mGui, Robot ro, Map map) {

        this.map = map;
        this.mGui = mGui;
        this.robot = ro;
        this.obsList = mGui.getObsList();
        this.playSpeed = 1 / mGui.getUserSpeed();
    }
    
    /**
     * This method represent the starting method for this thread to execute when called from GUI.
     * It will perform the various method required to create the path to the obstacles (nearest neighbour & a* algorithm)
     * and move the virtual robot accordingly.
     */
	@Override
	public void run() {
        try {
            initialiseTimer();
            mGui.displayMsgToUI("Simulate Hamitonian Path Thread Started");

            //ArrayList<Cell> cellsInPath = fastestPath.findAllWPEndPaths(exploreMap);
            //String moveString = convertCellsToMovements(cellsInPath);
            MainConnect mc = new MainConnect();
            String test = mc.fullPath(mGui);//"HP|W5|E1|";
            printFastestPathMovement(test);
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
   
   /**
    * This method display the string movements instruction on the virutal robot to reach its destination
    *
    * @param moveString The string which specifies the consecutive movement that the robot should execute.
    * @throws InterruptedException If the connection gets interrupted.
    */
   private void printFastestPathMovement(String moveString) throws InterruptedException {

       // FP|F6|R0|F1|L0|F2
       String[] arr = moveString.split("\\,");

       try {
           for (int i=arr.length-1; i >= 0; i--) {
               switch (arr[i]) {
               	   case "V":
               		   Cell c = mGui.getObstacleQueue().remove();
            		   c.setVisited(true);
            		   map.getMap()[c.getRow()][c.getCol()] = c;
            		   mGui.displayMsgToUI("Obstacle Scanned!");
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
