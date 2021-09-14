package entity;

import constant.Constants;
import constant.Constants.DIRECTION;

import java.util.ArrayList;

/**
 * @author Nicholas Yeo Ming Jie
 * @author Neo Zhao Wei
 * @author David Loh Shun Hao
 * @version 1.0
 * @since 2020-10-27
 */
public class Map {

	private Cell[][] mapArena;
	private Cell start;

	/**
	 * Default Constructor - instantiate default 2D arena, start goal
	 */
	public Map() {
		this.mapArena = new Cell[Constants.MAX_ROW][Constants.MAX_COL];

		for (int i = 0; i < Constants.MAX_ROW; i++) {
			for (int y = 0; y < Constants.MAX_COL; y++) {
				this.mapArena[i][y] = new Cell(i, y);
			}
		}
		// Setting start/end goal as explored
		start = this.mapArena[1][1];
	}

	/**
	 * Non-default constructor
	 *
	 * @param sMap The 2D array of cell object which represent the arena
	 */
	public Map(Cell[][] sMap) {
		this.mapArena = sMap;
	}

	/**
	 * @return 2D array of cell object which represent the arena
	 */
	public Cell[][] getMapGrid() {
		return this.mapArena;
	}

	/**
	 * @return StartGoal cell
	 */
	public Cell getStartGoalPosition() {
		return this.start;
	}

	/**
	 * This method check if robot was at the specified start goal cell.
	 *
	 * @param r The robot object
	 * @return true if robot is at start goal, else false.
	 */
	public boolean checkIfRobotAtStartPos(Robot r) {

		if (r.getPosRow() == start.getRowPos() && r.getPosCol() == start.getColPos()) {
			return true;
		}
		return false;
	}

	/**
	 * This method retrieve the cells that the robot could see from its current
	 * location and with reference to an actual map representation of arena. The
	 * robot is facing North direction.
	 *
	 * @param robot   The robot object
	 * @param realMap The Map object that will be verified against.
	 * @return arraylist of cells that the robot have explored at it current
	 *         location.
	 */
	public ArrayList<Cell> northExploredCells(Robot robot, Map realMap) {
		ArrayList<Cell> explorableCells = new ArrayList<Cell>();

		// find explorable cells in front of robot:
		// X X X
		// X X X
		// R F R
		// R R R
		// R R R
		for (int c : Constants.WITHIN_3BY3) {
			for (int r : Constants.SHORT_SENSOR_ADD) {
				Cell tempCell = new Cell(robot.getPosRow() + r, robot.getPosCol() + c);

				if (tempCell.isCellValid()) {
					explorableCells.add(tempCell);
				}
				if (tempCell.isCellValid() && realMap.getMapGrid()[robot.getPosRow() + r][robot.getPosCol() + c]
						.getObstacleType().equals("O")) {
					break;
				}
			}
		}

		// find explorable cells at right of robot:
		// R F R X X
		// R R R X X
		// R R R X X
		for (int r : Constants.WITHIN_3BY3) {
			for (int c : Constants.SHORT_SENSOR_ADD) {
				Cell tempCell = new Cell(robot.getPosRow() + r, robot.getPosCol() + c);
				if (tempCell.isCellValid()) {

					explorableCells.add(tempCell);
				}
				if (tempCell.isCellValid() && realMap.getMapGrid()[robot.getPosRow() + r][robot.getPosCol() + c]
						.getObstacleType().equals("O")) {

					break;
				}
			}
		}

		// find explorable cells at left of robot:
		// X X R F R
		// X X X X R R R
		// X X X X R R R
		for (int r = -1; r < 1; r++) {
			for (int c : Constants.LONG_SENSOR_SUB) {
				Cell tempCell = new Cell(robot.getPosRow() + r, robot.getPosCol() + c);
				if (tempCell.isCellValid()) {
					explorableCells.add(tempCell);
				}
				if (tempCell.isCellValid() && realMap.getMapGrid()[robot.getPosRow() + r][robot.getPosCol() + c]
						.getObstacleType().equals("O")) {
					break;
				}
			}
		}
		for (int c : Constants.SHORT_SENSOR_SUB) {
			Cell tempCell = new Cell(robot.getPosRow() + 1, robot.getPosCol() + c);
			if (tempCell.isCellValid()) {
				explorableCells.add(tempCell);
			}
			if (tempCell.isCellValid()
					&& realMap.getMapGrid()[robot.getPosRow() + 1][robot.getPosCol() + c].getObstacleType().equals("O")) {
				break;
			}
		}

		return explorableCells;
	}

	/**
	 * This method retrieve the cells that the robot could see from its current
	 * location and with reference to an actual map representation of arena. The
	 * robot is facing East direction.
	 *
	 * @param robot   The robot object
	 * @param realMap The Map object that will be verified against.
	 * @return arraylist of cells that the robot have explored at it current
	 *         location.
	 */
	public ArrayList<Cell> eastExploredCells(Robot robot, Map realMap) {
		ArrayList<Cell> explorableCells = new ArrayList<Cell>();

		// find explorable cells in front of robot:
		// R R R X X
		// R R F X X
		// R R R X X
		for (int r : Constants.WITHIN_3BY3) {
			for (int c : Constants.SHORT_SENSOR_ADD) {
				Cell tempCell = new Cell(robot.getPosRow() + r, robot.getPosCol() + c);
				if (tempCell.isCellValid()) {
					explorableCells.add(tempCell);
				}
				if (tempCell.isCellValid() && realMap.getMapGrid()[robot.getPosRow() + r][robot.getPosCol() + c]
						.getObstacleType().equals("O")) {
					break;
				}
			}
		}

		// find explorable cells in right of robot:
		// R R R
		// R R F
		// R R R
		// X X X
		// X X X
		for (int c : Constants.WITHIN_3BY3) {
			for (int r : Constants.SHORT_SENSOR_SUB) {
				Cell tempCell = new Cell(robot.getPosRow() + r, robot.getPosCol() + c);
				if (tempCell.isCellValid()) {
					explorableCells.add(tempCell);
				}
				if (tempCell.isCellValid() && realMap.getMapGrid()[robot.getPosRow() + r][robot.getPosCol() + c]
						.getObstacleType().equals("O")) {
					break;
				}
			}
		}

		// find explorable cells in left of robot:
		// X X
		// X X
		// X X X
		// X X X
		// R R R
		// R R F
		// R R R
		for (int c = -1; c < 1; c++) {
			for (int r : Constants.LONG_SENSOR_ADD) {
				Cell tempCell = new Cell(robot.getPosRow() + r, robot.getPosCol() + c);
				if (tempCell.isCellValid()) {
					explorableCells.add(tempCell);
				}
				if (tempCell.isCellValid() && realMap.getMapGrid()[robot.getPosRow() + r][robot.getPosCol() + c]
						.getObstacleType().equals("O")) {
					break;
				}
			}
		}
		for (int r : Constants.SHORT_SENSOR_ADD) {
			Cell tempCell = new Cell(robot.getPosRow() + r, robot.getPosCol() + 1);
			if (tempCell.isCellValid()) {
				explorableCells.add(tempCell);
			}
			if (tempCell.isCellValid()
					&& realMap.getMapGrid()[robot.getPosRow() + r][robot.getPosCol() + 1].getObstacleType().equals("O")) {
				break;
			}
		}

		return explorableCells;
	}

	/**
	 * This method retrieve the cells that the robot could see from its current
	 * location and with reference to an actual map representation of arena. The
	 * robot is facing South direction.
	 *
	 * @param robot   The robot object
	 * @param realMap The Map object that will be verified against.
	 * @return arraylist of cells that the robot have explored at it current
	 *         location.
	 */
	public ArrayList<Cell> southExploredCells(Robot robot, Map realMap) {
		ArrayList<Cell> explorableCells = new ArrayList<Cell>();

		// find explorable cells in front of robot:
		// R R R
		// R R R
		// R F R
		// X X X
		// X X X
		for (int c : Constants.WITHIN_3BY3) {
			for (int r : Constants.SHORT_SENSOR_SUB) {
				Cell tempCell = new Cell(robot.getPosRow() + r, robot.getPosCol() + c);
				if (tempCell.isCellValid()) {
					explorableCells.add(tempCell);
				}
				if (tempCell.isCellValid() && realMap.getMapGrid()[robot.getPosRow() + r][robot.getPosCol() + c]
						.getObstacleType().equals("O")) {
					break;
				}
			}
		}

		// find explorable cells in right of robot:
		// X X R R R
		// X X R R R
		// X X R F R
		for (int r : Constants.WITHIN_3BY3) {
			for (int c : Constants.SHORT_SENSOR_SUB) {
				Cell tempCell = new Cell(robot.getPosRow() + r, robot.getPosCol() + c);
				if (tempCell.isCellValid()) {
					explorableCells.add(tempCell);
				}
				if (tempCell.isCellValid() && realMap.getMapGrid()[robot.getPosRow() + r][robot.getPosCol() + c]
						.getObstacleType().equals("O")) {
					break;
				}
			}
		}

		// find explorable cells in left of robot:
		// R R R X X X X
		// R R R X X X X
		// R F R X X
		for (int r = 0; r < 2; r++) {
			for (int c : Constants.LONG_SENSOR_ADD) {
				Cell tempCell = new Cell(robot.getPosRow() + r, robot.getPosCol() + c);
				if (tempCell.isCellValid()) {
					explorableCells.add(tempCell);
				}
				if (tempCell.isCellValid() && realMap.getMapGrid()[robot.getPosRow() + r][robot.getPosCol() + c]
						.getObstacleType().equals("O")) {
					break;
				}
			}
		}
		for (int c : Constants.SHORT_SENSOR_ADD) {
			Cell tempCell = new Cell(robot.getPosRow() - 1, robot.getPosCol() + c);
			if (tempCell.isCellValid()) {
				explorableCells.add(tempCell);
			}
			if (tempCell.isCellValid()
					&& realMap.getMapGrid()[robot.getPosRow() - 1][robot.getPosCol() + c].getObstacleType().equals("O")) {
				break;
			}
		}

		return explorableCells;
	}

	/**
	 * This method retrieve the cells that the robot could see from its current
	 * location and with reference to an actual map representation of arena. The
	 * robot is facing West direction.
	 *
	 * @param robot   The robot object
	 * @param realMap The Map object that will be verified against.
	 * @return arraylist of cells that the robot have explored at it current
	 *         location.
	 */
	public ArrayList<Cell> westExploredCells(Robot robot, Map realMap) {
		ArrayList<Cell> explorableCells = new ArrayList<Cell>();

		// find explorable cells in front of robot:
		// X X R R R
		// X X F R R
		// X X R R R
		for (int r : Constants.WITHIN_3BY3) {
			for (int c : Constants.SHORT_SENSOR_SUB) {
				Cell tempCell = new Cell(robot.getPosRow() + r, robot.getPosCol() + c);
				if (tempCell.isCellValid()) {
					explorableCells.add(tempCell);
				}
				if (tempCell.isCellValid() && realMap.getMapGrid()[robot.getPosRow() + r][robot.getPosCol() + c]
						.getObstacleType().equals("O")) {
					break;
				}
			}
		}

		// find explorable cells in right of robot:
		// X X X
		// X X X
		// R R R
		// F R R
		// R R R
		for (int c : Constants.WITHIN_3BY3) {
			for (int r : Constants.SHORT_SENSOR_ADD) {
				Cell tempCell = new Cell(robot.getPosRow() + r, robot.getPosCol() + c);
				if (tempCell.isCellValid()) {
					explorableCells.add(tempCell);
				}
				if (tempCell.isCellValid() && realMap.getMapGrid()[robot.getPosRow() + r][robot.getPosCol() + c]
						.getObstacleType().equals("O")) {
					break;
				}
			}
		}

		// find explorable cells in left of robot:
		// R R R
		// F R R
		// R R R
		// X X X
		// X X X
		// X X
		// X X
		for (int c = 0; c < 2; c++) {
			for (int r : Constants.LONG_SENSOR_SUB) {
				Cell tempCell = new Cell(robot.getPosRow() + r, robot.getPosCol() + c);
				if (tempCell.isCellValid()) {
					explorableCells.add(tempCell);
				}
				if (tempCell.isCellValid() && realMap.getMapGrid()[robot.getPosRow() + r][robot.getPosCol() + c]
						.getObstacleType().equals("O")) {
					break;
				}
			}
		}
		for (int r : Constants.SHORT_SENSOR_SUB) {
			Cell tempCell = new Cell(robot.getPosRow() + r, robot.getPosCol() - 1);
			if (tempCell.isCellValid()) {
				explorableCells.add(tempCell);
			}
			if (tempCell.isCellValid()
					&& realMap.getMapGrid()[robot.getPosRow() + r][robot.getPosCol() - 1].getObstacleType().equals("O")) {
				break;
			}
		}

		return explorableCells;
	}


	/**
	 * This method generates the MDF2 based on the explored each cell in the 2D
	 * array of cells
	 *
	 * @return String value of the path and obstacles in the explored arena,
	 *         represented in hexadecimal
	 */
	public String getMDF2() {
		String msg = "";
		String mdfString = "";
		for (int i = 0; i < Constants.MAX_ROW; i++) {
			for (int y = 0; y < Constants.MAX_COL; y++) {
				msg += (mapArena[i][y].getObstacleType().equals("O")) ? 1 : 0; // it must be a path.

			}
		}
		int bytelength = msg.length() % 4;
		if (bytelength != 0) {
			switch (bytelength) {
			case 1:
				msg += "000";
				break;
			case 2:
				msg += "00";
				break;
			case 3:
				msg += "0";
				break;

			}
		}

		for (int i = 0; i < msg.length(); i += 4) {
			mdfString += Integer.toHexString(Integer.parseInt(msg.substring(i, i + 4), 2));
		}

		return mdfString;
	}

	/**
	 * This method retrieve the obstacle cells that the physical robot could see
	 * from its current location and with reference to the received sensor reading.
	 * The robot is facing North direction.
	 *
	 * @param robot      The robot object which represent the physical robot
	 * @param sensorData The reading from each of the sensor of physical robot
	 * @return arraylist of obstacle cells that the robot cab see from its current
	 *         location.
	 */
	public ArrayList<Cell> northObstacleCells(Robot robot, ArrayList<Integer> sensorData) {

		ArrayList<Cell> obstacleCells = new ArrayList<Cell>();

		int row = robot.getPosRow();
		int col = robot.getPosCol();

		Integer rightBack = sensorData.get(0);
		Integer rightFront = sensorData.get(1);
		Integer left = sensorData.get(2);
		Integer frontRight = sensorData.get(3);
		Integer frontLeft = sensorData.get(4);
		Integer frontMiddle = sensorData.get(5);

		switch (frontRight) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row + 2, col + 1, "f1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row + 3, col + 1, "f2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (frontMiddle) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row + 2, col, "f1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row + 3, col, "f2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (frontLeft) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row + 2, col - 1, "f1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row + 3, col - 1, "f2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (rightBack) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row - 1, col + 2, "r1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row - 1, col + 3, "r2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (rightFront) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row + 1, col + 2, "r1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row + 1, col + 3, "r2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (left) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row, col - 4, "l"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row, col - 5, "l"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		return obstacleCells;
	}

	/**
	 * This method retrieve the empty cells that the physical robot could see from
	 * its current location and with reference to the received sensor reading. The
	 * robot is facing North direction.
	 *
	 * @param robot      The robot object which represent the physical robot
	 * @param sensorData The reading from each of the sensor of physical robot
	 * @return arraylist of empty cells that the robot cab see from its current
	 *         location.
	 */
	public ArrayList<Cell> northEmptyCells(Robot robot, ArrayList<Integer> sensorData) {

		ArrayList<Cell> emptyCells = new ArrayList<Cell>();

		int row = robot.getPosRow();
		int col = robot.getPosCol();

		Integer rightBack = sensorData.get(0);
		Integer rightFront = sensorData.get(1);
		Integer left = sensorData.get(2);
		Integer frontRight = sensorData.get(3);
		Integer frontLeft = sensorData.get(4);
		Integer frontMiddle = sensorData.get(5);

		switch (frontRight) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row + 2, col + 1, "f1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row + 2, col + 1, "f1"));
			emptyCells.add(new Cell(row + 3, col + 1, "f2"));
			break;
		}

		switch (frontMiddle) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row + 2, col, "f1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row + 2, col, "f1"));
			emptyCells.add(new Cell(row + 3, col, "f2"));
			break;
		}

		switch (frontLeft) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row + 2, col - 1, "f1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row + 2, col - 1, "f1"));
			emptyCells.add(new Cell(row + 3, col - 1, "f2"));
			break;
		}

		switch (rightBack) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row - 1, col + 2, "r1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row - 1, col + 2, "r1"));
			emptyCells.add(new Cell(row - 1, col + 3, "r2"));
			break;
		}

		switch (rightFront) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row + 1, col + 2, "r1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row + 1, col + 2, "r1"));
			emptyCells.add(new Cell(row + 1, col + 3, "r2"));
			break;
		}

		switch (left) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row, col - 4, "l"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row, col - 4, "l"));
			emptyCells.add(new Cell(row, col - 5, "l"));
			break;
		}

		return emptyCells;
	}

	/**
	 * This method retrieve the obstacle cells that the physical robot could see
	 * from its current location and with reference to the received sensor reading.
	 * The robot is facing South direction.
	 *
	 * @param robot      The robot object which represent the physical robot
	 * @param sensorData The reading from each of the sensor of physical robot
	 * @return arraylist of obstacle cells that the robot cab see from its current
	 *         location.
	 */
	public ArrayList<Cell> southObstacleCells(Robot robot, ArrayList<Integer> sensorData) {

		ArrayList<Cell> obstacleCells = new ArrayList<Cell>();

		int row = robot.getPosRow();
		int col = robot.getPosCol();

		Integer rightBack = sensorData.get(0);
		Integer rightFront = sensorData.get(1);
		Integer left = sensorData.get(2);
		Integer frontRight = sensorData.get(3);
		Integer frontLeft = sensorData.get(4);
		Integer frontMiddle = sensorData.get(5);

		switch (frontRight) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row - 2, col - 1, "f1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row - 3, col - 1, "f2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (frontMiddle) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row - 2, col, "f1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row - 3, col, "f2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (frontLeft) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row - 2, col + 1, "f1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row - 3, col + 1, "f2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (rightBack) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row + 1, col - 2, "r1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row + 1, col - 3, "r2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (rightFront) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row - 1, col - 2, "r1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row - 1, col - 3, "r2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (left) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row, col + 4, "l"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row, col + 5, "l"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		return obstacleCells;
	}

	/**
	 * This method retrieve the empty cells that the physical robot could see from
	 * its current location and with reference to the received sensor reading. The
	 * robot is facing South direction.
	 *
	 * @param robot      The robot object which represent the physical robot
	 * @param sensorData The reading from each of the sensor of physical robot
	 * @return arraylist of empty cells that the robot cab see from its current
	 *         location.
	 */
	public ArrayList<Cell> southEmptyCells(Robot robot, ArrayList<Integer> sensorData) {

		ArrayList<Cell> emptyCells = new ArrayList<Cell>();

		int row = robot.getPosRow();
		int col = robot.getPosCol();

		Integer rightBack = sensorData.get(0);
		Integer rightFront = sensorData.get(1);
		Integer left = sensorData.get(2);
		Integer frontRight = sensorData.get(3);
		Integer frontLeft = sensorData.get(4);
		Integer frontMiddle = sensorData.get(5);

		switch (frontRight) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row - 2, col - 1, "f1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row - 2, col - 1, "f1"));
			emptyCells.add(new Cell(row - 3, col - 1, "f2"));
			break;
		}

		switch (frontMiddle) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row - 2, col, "f1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row - 2, col, "f1"));
			emptyCells.add(new Cell(row - 3, col, "f2"));
			break;
		}

		switch (frontLeft) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row - 2, col + 1, "f1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row - 2, col + 1, "f1"));
			emptyCells.add(new Cell(row - 3, col + 1, "f2"));
			break;
		}

		switch (rightBack) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row + 1, col - 2, "r1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row + 1, col - 2, "r1"));
			emptyCells.add(new Cell(row + 1, col - 3, "r2"));
			break;
		}

		switch (rightFront) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row - 1, col - 2, "r1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row - 1, col - 2, "r1"));
			emptyCells.add(new Cell(row - 1, col - 3, "r2"));
			break;
		}

		switch (left) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row, col + 4, "l"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row, col + 4, "l"));
			emptyCells.add(new Cell(row, col + 5, "l"));
			break;
		}

		return emptyCells;
	}

	/**
	 * This method retrieve the obstacle cells that the physical robot could see
	 * from its current location and with reference to the received sensor reading.
	 * The robot is facing East direction.
	 *
	 * @param robot      The robot object which represent the physical robot
	 * @param sensorData The reading from each of the sensor of physical robot
	 * @return arraylist of obstacle cells that the robot cab see from its current
	 *         location.
	 */
	public ArrayList<Cell> eastObstacleCells(Robot robot, ArrayList<Integer> sensorData) {

		ArrayList<Cell> obstacleCells = new ArrayList<Cell>();

		int row = robot.getPosRow();
		int col = robot.getPosCol();

		Integer rightBack = sensorData.get(0);
		Integer rightFront = sensorData.get(1);
		Integer left = sensorData.get(2);
		Integer frontRight = sensorData.get(3);
		Integer frontLeft = sensorData.get(4);
		Integer frontMiddle = sensorData.get(5);

		switch (frontRight) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row - 1, col + 2, "f1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row - 1, col + 3, "f2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (frontMiddle) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row, col + 2, "f1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row, col + 3, "f2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (frontLeft) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row + 1, col + 2, "f1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row + 1, col + 3, "f2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (rightBack) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row - 2, col - 1, "r1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row - 3, col - 1, "r2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (rightFront) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row - 2, col + 1, "r1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row - 3, col + 1, "r2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (left) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row + 4, col, "l"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row + 5, col, "l"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		return obstacleCells;
	}

	/**
	 * This method retrieve the empty cells that the physical robot could see from
	 * its current location and with reference to the received sensor reading. The
	 * robot is facing East direction.
	 *
	 * @param robot      The robot object which represent the physical robot
	 * @param sensorData The reading from each of the sensor of physical robot
	 * @return arraylist of empty cells that the robot cab see from its current
	 *         location.
	 */
	public ArrayList<Cell> eastEmptyCells(Robot robot, ArrayList<Integer> sensorData) {

		ArrayList<Cell> emptyCells = new ArrayList<Cell>();

		int row = robot.getPosRow();
		int col = robot.getPosCol();

		Integer rightBack = sensorData.get(0);
		Integer rightFront = sensorData.get(1);
		Integer left = sensorData.get(2);
		Integer frontRight = sensorData.get(3);
		Integer frontLeft = sensorData.get(4);
		Integer frontMiddle = sensorData.get(5);

		switch (frontRight) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row - 1, col + 2, "f1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row - 1, col + 2, "f1"));
			emptyCells.add(new Cell(row - 1, col + 3, "f2"));
			break;
		}

		switch (frontMiddle) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row, col + 2, "f1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row, col + 2, "f1"));
			emptyCells.add(new Cell(row, col + 3, "f2"));
			break;
		}

		switch (frontLeft) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row + 1, col + 2, "f1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row + 1, col + 2, "f1"));
			emptyCells.add(new Cell(row + 1, col + 3, "f2"));
			break;
		}

		switch (rightBack) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row - 2, col - 1, "r1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row - 2, col - 1, "r1"));
			emptyCells.add(new Cell(row - 3, col - 1, "r2"));
			break;
		}

		switch (rightFront) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row - 2, col + 1, "r1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row - 2, col + 1, "r1"));
			emptyCells.add(new Cell(row - 3, col + 1, "r2"));
			break;
		}

		switch (left) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row + 4, col, "l"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row + 4, col, "l"));
			emptyCells.add(new Cell(row + 5, col, "l"));
			break;
		}

		return emptyCells;
	}

	/**
	 * This method retrieve the obstacle cells that the physical robot could see
	 * from its current location and with reference to the received sensor reading.
	 * The robot is facing West direction.
	 *
	 * @param robot      The robot object which represent the physical robot
	 * @param sensorData The reading from each of the sensor of physical robot
	 * @return arraylist of obstacle cells that the robot cab see from its current
	 *         location.
	 */
	public ArrayList<Cell> westObstacleCells(Robot robot, ArrayList<Integer> sensorData) {

		ArrayList<Cell> obstacleCells = new ArrayList<Cell>();

		int row = robot.getPosRow();
		int col = robot.getPosCol();

		Integer rightBack = sensorData.get(0);
		Integer rightFront = sensorData.get(1);
		Integer left = sensorData.get(2);
		Integer frontRight = sensorData.get(3);
		Integer frontLeft = sensorData.get(4);
		Integer frontMiddle = sensorData.get(5);

		switch (frontRight) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row + 1, col - 2, "f1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row + 1, col - 3, "f2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (frontMiddle) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row, col - 2, "f1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row, col - 3, "f2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (frontLeft) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row - 1, col - 2, "f1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row - 1, col - 3, "f2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (rightBack) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row + 2, col + 1, "r1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row + 3, col + 1, "r2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (rightFront) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row + 2, col - 1, "r1"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row + 3, col - 1, "r2"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		switch (left) {
		case Constants.OBSTACLE_IMMEDIATE:
			obstacleCells.add(new Cell(row - 4, col, "l"));
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			obstacleCells.add(new Cell(row - 5, col, "l"));
			break;
		case Constants.NO_OBSTACLE:
			break;
		}

		return obstacleCells;
	}

	/**
	 * This method retrieve the empty cells that the physical robot could see from
	 * its current location and with reference to the received sensor reading. The
	 * robot is facing West direction.
	 *
	 * @param robot      The robot object which represent the physical robot
	 * @param sensorData The reading from each of the sensor of physical robot
	 * @return arraylist of empty cells that the robot cab see from its current
	 *         location.
	 */
	public ArrayList<Cell> westEmptyCells(Robot robot, ArrayList<Integer> sensorData) {

		ArrayList<Cell> emptyCells = new ArrayList<Cell>();

		int row = robot.getPosRow();
		int col = robot.getPosCol();

		Integer rightBack = sensorData.get(0);
		Integer rightFront = sensorData.get(1);
		Integer left = sensorData.get(2);
		Integer frontRight = sensorData.get(3);
		Integer frontLeft = sensorData.get(4);
		Integer frontMiddle = sensorData.get(5);

		switch (frontRight) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row + 1, col - 2, "f1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row + 1, col - 2, "f1"));
			emptyCells.add(new Cell(row + 1, col - 3, "f2"));
			break;
		}

		switch (frontMiddle) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row, col - 2, "f1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row, col - 2, "f1"));
			emptyCells.add(new Cell(row, col - 3, "f2"));
			break;
		}

		switch (frontLeft) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row - 1, col - 2, "f1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row - 1, col - 2, "f1"));
			emptyCells.add(new Cell(row - 1, col - 3, "f2"));
			break;
		}

		switch (rightBack) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row + 2, col + 1, "r1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row + 2, col + 1, "r1"));
			emptyCells.add(new Cell(row + 3, col + 1, "r2"));
			break;
		}

		switch (rightFront) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row + 2, col - 1, "r1"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row + 2, col - 1, "r1"));
			emptyCells.add(new Cell(row + 3, col - 1, "r2"));
			break;
		}

		switch (left) {
		case Constants.OBSTACLE_IMMEDIATE:
			break;
		case Constants.OBSTACLE_ONE_BLOCK_AWAY:
			emptyCells.add(new Cell(row - 4, col, "l"));
			break;
		case Constants.NO_OBSTACLE:
			emptyCells.add(new Cell(row - 4, col, "l"));
			emptyCells.add(new Cell(row - 5, col, "l"));
			break;
		}

		return emptyCells;
	}

	/**
	 * Returns true if cell is within the width and length of the arena.
	 *
	 * @param row Row coordinate of the cell.
	 * @param col Column coordinate of the cell.
	 * @return true if cell is within the width and length of the arena.
	 */
	public boolean isCellValid(int row, int col) {
		if (row >= Constants.MAX_ROW) {
			return false;
		}
		if (col >= Constants.MAX_COL) {
			return false;
		}
		if (row < 0) {
			return false;
		}
		if (col < 0) {
			return false;
		}
		return true;
	}

	/**
	 * This method set the surrounding cells of an input cell as virtual wall type.
	 *
	 * @param cell The cell object
	 */
	public void setVirtualWall(Cell cell) {
		for (int r : Constants.WITHIN_3BY3) {
			for (int c : Constants.WITHIN_3BY3) {
				Cell tempCell = new Cell(cell.getRowPos() + r, cell.getColPos() + c);
				if (tempCell.isCellValid()) {
					this.getMapGrid()[tempCell.getRowPos()][tempCell.getColPos()].setVirtualWall(true);
				}
			}
		}
	}

	/**
	 * This method generate the 3 cell coordinates that are directly on the right of
	 * the robot based on the robot's current facing direction
	 *
	 * @param robot The robot object
	 * @return String value of the 3 cell coordinates
	 */
	public String rpiImageString(Robot robot) {

		switch (robot.getCurrDir()) {
		case EAST:
			return eastImageString(robot);
		case NORTH:
			return northImageString(robot);
		case SOUTH:
			return southImageString(robot);
		case WEST:
			return westImageString(robot);
		default:
			return "";

		}

		// return "";
	}

	/**
	 * This method generate the 3 cell coordinates that are directly on the right of
	 * the robot while it is facing North.
	 *
	 * @param robot The robot object
	 * @return String value of the 3 cell coordinates
	 */
	public String northImageString(Robot robot) {

		String imageString;

		int row = robot.getPosRow();
		int col = robot.getPosCol();

		String x1 = Integer.toString(col + 2);
		String y1 = Integer.toString(row + 1);

		String x2 = Integer.toString(col + 2);
		String y2 = Integer.toString(row);

		String x3 = Integer.toString(col + 2);
		String y3 = Integer.toString(row - 1);

		imageString = "|(" + x1 + "),(" + y1 + ")|(" + x2 + "),(" + y2 + ")|(" + x3 + "),(" + y3 + ")";

		return imageString;

	}

	/**
	 * This method generate the 3 cell coordinates that are directly on the right of
	 * the robot while it is facing South.
	 *
	 * @param robot The robot object
	 * @return String value of the 3 cell coordinates
	 */
	public String southImageString(Robot robot) {

		String imageString;

		int row = robot.getPosRow();
		int col = robot.getPosCol();

		String x1 = Integer.toString(col - 2);
		String y1 = Integer.toString(row - 1);

		String x2 = Integer.toString(col - 2);
		String y2 = Integer.toString(row);

		String x3 = Integer.toString(col - 2);
		String y3 = Integer.toString(row + 1);

		imageString = "|(" + x1 + "),(" + y1 + ")|(" + x2 + "),(" + y2 + ")|(" + x3 + "),(" + y3 + ")";

		return imageString;

	}

	/**
	 * This method generate the 3 cell coordinates that are directly on the right of
	 * the robot while it is facing East.
	 *
	 * @param robot The robot object
	 * @return String value of the 3 cell coordinates
	 */
	public String eastImageString(Robot robot) {

		String imageString;

		int row = robot.getPosRow();
		int col = robot.getPosCol();

		String x1 = Integer.toString(col + 1);
		String y1 = Integer.toString(row - 2);

		String x2 = Integer.toString(col);
		String y2 = Integer.toString(row - 2);

		String x3 = Integer.toString(col - 1);
		String y3 = Integer.toString(row - 2);

		imageString = "|(" + x1 + "),(" + y1 + ")|(" + x2 + "),(" + y2 + ")|(" + x3 + "),(" + y3 + ")";

		return imageString;

	}

	/**
	 * This method generate the 3 cell coordinates that are directly on the right of
	 * the robot while it is facing West.
	 *
	 * @param robot The robot object
	 * @return String value of the 3 cell coordinates
	 */
	public String westImageString(Robot robot) {

		String imageString;

		int row = robot.getPosRow();
		int col = robot.getPosCol();

		String x1 = Integer.toString(col - 1);
		String y1 = Integer.toString(row + 2);

		String x2 = Integer.toString(col);
		String y2 = Integer.toString(row + 2);

		String x3 = Integer.toString(col + 1);
		String y3 = Integer.toString(row + 2);

		imageString = "|(" + x1 + "),(" + y1 + ")|(" + x2 + "),(" + y2 + ")|(" + x3 + "),(" + y3 + ")";

		return imageString;

	}
}
