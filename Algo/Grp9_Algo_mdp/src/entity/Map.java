package entity;

import constant.Constants;
import java.util.*;

public class Map {
	private Cell[][] map;
	private int robPosRow; // start pos of rob
	private int robPosCol; // start pos of rob

	private Cell startGoal;

	public Map() {
		this.map = new Cell[Constants.MAX_ROW][Constants.MAX_COL];

		// setting all box with 0 to indicate new map
		for (int i = 0; i < Constants.MAX_ROW; i++) {
			for (int j = 0; j < Constants.MAX_COL; j++) {
				this.map[i][j] = new Cell(i, j);
			}
		}

		startGoal = this.map[1][1];

		// setting start position of robot (center of robot)
		this.robPosRow = 1;
		this.robPosCol = 1;
	}

	/**
	 * @return Map 2D array of cell object which represent the arena
	 */
	public Cell[][] getMap() {
		return this.map;
	}

	/**
	 * @return StartGoal cell
	 */
	public Cell getStartGoalPosition() {
		return this.startGoal;
	}

	public int getStartRow() {
		return this.robPosRow;
	}

	public int getStartCol() {
		return this.robPosCol;
	}

	public void setStartRowCol(int startRow, int startCol) {
		this.robPosRow = startRow;
		this.robPosCol = startCol;
	}

	public void setMapTargetCell(int obsRow, int obsCol, int dir) { // 1=Top, 2=Bottom, 3=Left, 4=Right
		this.map[obsRow][obsCol].setObstacle(dir);
		switch (dir) {
		case 1:

			this.map[obsRow + 4][obsCol].setTargetCell(true);
			this.map[obsRow + 4][obsCol].setHeadDir(2);

			break;
		case 2:

			this.map[obsRow - 4][obsCol].setTargetCell(true);
			this.map[obsRow - 4][obsCol].setHeadDir(1);
			break;
		case 3:

			this.map[obsRow][obsCol + 4].setTargetCell(true);
			this.map[obsRow][obsCol + 4].setHeadDir(4);
			break;
		case 4:

			this.map[obsRow][obsCol - 4].setTargetCell(true);
			this.map[obsRow][obsCol - 4].setHeadDir(3);
			break;
		}
	}
	
	public Cell targetToObstacle(Cell c) {
		int row = c.getRow();
		int col = c.getCol();
		switch (c.getHeadDir()) {
		case 1:
			row = row + 4;
			break;
		case 2:
			row = row - 4;
			break;
		case 3:
			col = col + 4;
			break;
		case 4:
			col = col - 4;
			break;
		}
		return this.map[row][col];
	}

	public void setMapObstacle(ArrayList<Cell> obstacles) {
		for (int i = 0; i < obstacles.size(); i++) {
			try {
				this.getMap()[obstacles.get(i).getRow()][obstacles.get(i).getCol()] = (Cell) obstacles.get(i).clone();
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setMapHeuristicCost(int targetRow, int targetCol) {
		for (int i = 0; i < this.getMap().length; i++) {
			for (int j = 0; j < this.getMap()[i].length; j++) {
				double heuristicCost = Math.abs(i - targetRow) + Math.abs(j - targetCol);
				this.getMap()[i][j].setHeuristicCost(heuristicCost);
				this.getMap()[i][j].setSolution(false);
			}
		}
	}

	public void setRobotMap(Robot r) {
		// final cost
		for (int i = r.getPosRow() - 1; i < r.getPosRow() + 2; i++) {
			for (int j = r.getPosCol() - 1; j < r.getPosCol(); j++) {
				this.getMap()[i][j].setFinalCost(0);
			}
		}
		// set start head
		int dirHead = r.ToDirectionHead(r.getCurrDir());
		this.getMap()[r.getPosRow()][r.getPosCol()].setHeadDir(dirHead);
		System.out.println("-----map.setrobotmap---");
		System.out.print(r.getCurrDir());
		System.out.println(this.getMap()[r.getPosRow()][r.getPosCol()].getHeadDir());
		System.out.println("----End-------");
	}

	public void setObstacle(int row, int col, int obsDir) {
		this.getMap()[row][col].setObstacle(true);
		this.getMap()[row][col].setObsDir(obsDir);
	}

	/**
	 * This method generates the MDF based on each cell in the 2D
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
				msg += (map[i][y].isObstacle()) ? 1 : 0; // if cell is explored but is not an obstacle, it must be a
															// path.
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
     * This method generate the 3 cell coordinates that are directly on the right of the robot based on the robot's current facing direction
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
    }
    
    /**
     * This method generate the 3 cell coordinates that are directly on the right of the robot while it is facing North.
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
     * This method generate the 3 cell coordinates that are directly on the right of the robot while it is facing South.
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
     * This method generate the 3 cell coordinates that are directly on the right of the robot while it is facing East.
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
     * This method generate the 3 cell coordinates that are directly on the right of the robot while it is facing West.
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
