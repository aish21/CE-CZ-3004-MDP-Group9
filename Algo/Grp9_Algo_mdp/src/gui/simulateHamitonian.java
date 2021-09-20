package gui;

import java.util.Timer;

import algorithm.AStar;
import algorithm.NearestNeighbour;
import entity.Map;
import entity.Robot;
import gui.main;

/**
 * @author Goh Cheng Guan, Cliev
 * @author 
 * @version 1.0
 * @since 2020-10-19
 */

public class simulateHamitonian implements Runnable {
	
    AStar as;
    NearestNeighbour nn;
    Map map;
    Robot robot;
    main main;
    private float playSpeed;
    Timer mTimer;

    /**
     * This method is the non-default constructor to create simulateFastestPath thread class
     *
     * @param maGUI  The GUI object where the result should be displayed to.
     * @param ro     The robot object which specifies the detail of robot.
     * @param expMap The Map object that the robot have explored.
     */
    public simulateHamitonian(main m, Robot ro, Map map) {

        this.map = map;
        this.main = m;
        this.robot = ro;
        this.playSpeed = 1 / m.getUserSpeed();
    }
    
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
