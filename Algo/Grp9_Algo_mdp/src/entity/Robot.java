package entity;

import constant.Constants.DIRECTION;
import constant.Constants.MOVEMENT;

/**
 * @author Goh Cheng Guan, Clive
 * @author 
 * @version 1.0
 * @since 2020-09-10
 */
public class Robot {

    private int posRow;                     // Y/Row coord of the robot
    private int posCol;                     // X/Col coord of the robot
    private DIRECTION currDir;              // Direction the robot is facing

    /**
     * Non-default constructor
     *
     * @param posRow row value of robot's current location
     * @param posCol column value of robot's current location
     * @param curDir Direction the robot is currently facing
     */
    public Robot(int posRow, int posCol, DIRECTION curDir) {
        this.posRow = posRow;
        this.posCol = posCol;
        this.currDir = curDir;
    }

    /**
     * @return row value of the robot's location
     */
    public int getPosRow() {
        return posRow;
    }

    /**
     * @param posRow new row value to specify robot's location
     */
    public void setPosRow(int posRow) {
        this.posRow = posRow;
    }

    /**
     * @return column value of the robot's location
     */
    public int getPosCol() {
        return posCol;
    }

    /**
     * @param posCol new column value to specify robot's location
     */
    public void setPosCol(int posCol) {
        this.posCol = posCol;
    }

    /**
     * @return direction the robot is currently facing
     */
    public DIRECTION getCurrDir() {
        return currDir;
    }

    /**
     * @param currDir new value to update the robot's facing direction
     */
    public void setCurrDir(DIRECTION currDir) {
        this.currDir = currDir;
    }
    
    /**
     * @param return direction with int value
     */
    public int ToDirectionHead(DIRECTION currDir) {
    	switch (currDir) {
		case NORTH:
			return 1;
		case SOUTH:
			return 2;
		case EAST:
			return 3;
		case WEST:
			return 4;
		default:
			return 0;
		}
    }

    /**
     * This method move the robot forward regardless of its current direction
     *
     * @param m The movement.
     */
    public void move(MOVEMENT m) {
        switch (m) {
            case FORWARD:
                if (this.currDir == DIRECTION.NORTH) this.setPosRow(posRow + 1);
                else if (this.currDir == DIRECTION.SOUTH) this.setPosRow(posRow - 1);
                else if (this.currDir == DIRECTION.EAST) this.setPosCol(posCol + 1);
                else if (this.currDir == DIRECTION.WEST) this.setPosCol(posCol - 1);
                break;
            case BACKWARD:
                 if(this.currDir == DIRECTION.NORTH) this.setPosRow(posRow - 1);
                 else if(this.currDir == DIRECTION.SOUTH) this.setPosRow(posRow + 1);
                 else if(this.currDir == DIRECTION.EAST) this.setPosCol(posCol - 1);
                 else if(this.currDir == DIRECTION.WEST) this.setPosCol(posCol + 1);
                 break;
            case LEFT:
            case RIGHT:
                break;
        }
    }

    /**
     * This method rotate the robot by changing it facing direction
     *
     * @param m The movement which cause robot to turn
     */
    public void turn(MOVEMENT m) {
        switch (this.currDir) {
            case NORTH:
                if (m == MOVEMENT.RIGHT) this.setCurrDir(DIRECTION.EAST);
                else if (m == MOVEMENT.LEFT) this.setCurrDir(DIRECTION.WEST);
                break;
            case SOUTH:
                if (m == MOVEMENT.RIGHT) this.setCurrDir(DIRECTION.WEST);
                else if (m == MOVEMENT.LEFT) this.setCurrDir(DIRECTION.EAST);
                break;
            case EAST:
                if (m == MOVEMENT.RIGHT) this.setCurrDir(DIRECTION.SOUTH);
                else if (m == MOVEMENT.LEFT) this.setCurrDir(DIRECTION.NORTH);
                break;
            case WEST:
                if (m == MOVEMENT.RIGHT) this.setCurrDir(DIRECTION.NORTH);
                else if (m == MOVEMENT.LEFT) this.setCurrDir(DIRECTION.SOUTH);
                break;
        }
    }


    /**
     * This method determine if specified movement is valid for the robot to move.
     *
     * @param exploredMap The map object explored by robot
     * @param m           The movement the robot should take
     * @return true if the movement is valid for robot, else false
     */
    public boolean isMovementValid(Map exploredMap, MOVEMENT m) {
        Cell destinationCell_1 = new Cell(-1, -1);
        Cell destinationCell_2 = new Cell(-1, -1);
        Cell destinationCell_3 = new Cell(-1, -1);

        DIRECTION absoluteDir = null;

        switch (this.currDir) {
            case NORTH:
                if (m == MOVEMENT.FORWARD) absoluteDir = DIRECTION.NORTH;
                else if (m == MOVEMENT.BACKWARD) absoluteDir = DIRECTION.SOUTH;
                else if (m == MOVEMENT.RIGHT) absoluteDir = DIRECTION.EAST;
                else if (m == MOVEMENT.LEFT) absoluteDir = DIRECTION.WEST;
                break;
            case SOUTH:
                if (m == MOVEMENT.FORWARD) absoluteDir = DIRECTION.SOUTH;
                else if (m == MOVEMENT.BACKWARD) absoluteDir = DIRECTION.NORTH;
                else if (m == MOVEMENT.RIGHT) absoluteDir = DIRECTION.WEST;
                else if (m == MOVEMENT.LEFT) absoluteDir = DIRECTION.EAST;
                break;
            case EAST:
                if (m == MOVEMENT.FORWARD) absoluteDir = DIRECTION.EAST;
                else if (m == MOVEMENT.BACKWARD) absoluteDir = DIRECTION.WEST;
                else if (m == MOVEMENT.RIGHT) absoluteDir = DIRECTION.SOUTH;
                else if (m == MOVEMENT.LEFT) absoluteDir = DIRECTION.NORTH;
                break;
            case WEST:
                if (m == MOVEMENT.FORWARD) absoluteDir = DIRECTION.WEST;
                else if (m == MOVEMENT.BACKWARD) absoluteDir = DIRECTION.EAST;
                else if (m == MOVEMENT.RIGHT) absoluteDir = DIRECTION.NORTH;
                else if (m == MOVEMENT.LEFT) absoluteDir = DIRECTION.SOUTH;
                break;
        }

        //determine position of cell that are 2 blocks infront of robot in the direction it is heading
        switch (absoluteDir) {
            case NORTH:
                destinationCell_1.setRow(this.getPosRow() + 2);
                destinationCell_1.setCol(this.getPosCol());
                destinationCell_2.setRow(this.getPosRow() + 2);
                destinationCell_2.setCol(this.getPosCol() - 1);
                destinationCell_3.setRow(this.getPosRow() + 2);
                destinationCell_3.setCol(this.getPosCol() + 1);
                break;
            case SOUTH:
                destinationCell_1.setRow(this.getPosRow() - 2);
                destinationCell_1.setCol(this.getPosCol());
                destinationCell_2.setRow(this.getPosRow() - 2);
                destinationCell_2.setCol(this.getPosCol() + 1);
                destinationCell_3.setRow(this.getPosRow() - 2);
                destinationCell_3.setCol(this.getPosCol() - 1);
                break;
            case WEST:
                destinationCell_1.setRow(this.getPosRow());
                destinationCell_1.setCol(this.getPosCol() - 2);
                destinationCell_2.setRow(this.getPosRow() - 1);
                destinationCell_2.setCol(this.getPosCol() - 2);
                destinationCell_3.setRow(this.getPosRow() + 1);
                destinationCell_3.setCol(this.getPosCol() - 2);
                break;
            case EAST:
                destinationCell_1.setRow(this.getPosRow());
                destinationCell_1.setCol(this.getPosCol() + 2);
                destinationCell_2.setRow(this.getPosRow() + 1);
                destinationCell_2.setCol(this.getPosCol() + 2);
                destinationCell_3.setRow(this.getPosRow() - 1);
                destinationCell_3.setCol(this.getPosCol() + 2);
                break;
        }
        
        System.out.format("cell[%d][%d]"+exploredMap.getMap()[destinationCell_1.getRow()][destinationCell_1.getCol()].isObstacle(),destinationCell_1.getRow(),destinationCell_1.getCol());
        System.out.format("cell[%d][%d]"+exploredMap.getMap()[destinationCell_2.getRow()][destinationCell_2.getCol()].isObstacle(),destinationCell_2.getRow(),destinationCell_2.getCol());
        System.out.format("cell[%d][%d]"+exploredMap.getMap()[destinationCell_3.getRow()][destinationCell_3.getCol()].isObstacle(),destinationCell_3.getRow(),destinationCell_3.getCol());

        if (destinationCell_1.isCellValid() && destinationCell_2.isCellValid() && destinationCell_3.isCellValid()) {
            if (!exploredMap.getMap()[destinationCell_1.getRow()][destinationCell_1.getCol()].isObstacle() &&
                    !exploredMap.getMap()[destinationCell_2.getRow()][destinationCell_2.getCol()].isObstacle() &&
                    !exploredMap.getMap()[destinationCell_3.getRow()][destinationCell_3.getCol()].isObstacle()) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method determine if robot could move forward in a specific direction
     *
     * @param exploredMap The map object explored by robot
     * @param d           The direction that robot should go toward
     * @return true if robot could move toward the specified direction , else false
     */
    public boolean isDisplacementValid(Map exploredMap, DIRECTION d) {

        Cell destinationCell_1 = new Cell(-1, -1);
        Cell destinationCell_2 = new Cell(-1, -1);
        Cell destinationCell_3 = new Cell(-1, -1);

        switch (d) {
            case NORTH:
                destinationCell_1.setRow(this.getPosRow() + 2);
                destinationCell_1.setCol(this.getPosCol());
                destinationCell_2.setRow(this.getPosRow() + 2);
                destinationCell_2.setCol(this.getPosCol() - 1);
                destinationCell_3.setRow(this.getPosRow() + 2);
                destinationCell_3.setCol(this.getPosCol() + 1);
                break;
            case SOUTH:
                destinationCell_1.setRow(this.getPosRow() - 2);
                destinationCell_1.setCol(this.getPosCol());
                destinationCell_2.setRow(this.getPosRow() - 2);
                destinationCell_2.setCol(this.getPosCol() + 1);
                destinationCell_3.setRow(this.getPosRow() - 2);
                destinationCell_3.setCol(this.getPosCol() - 1);
                break;
            case WEST:
                destinationCell_1.setRow(this.getPosRow());
                destinationCell_1.setCol(this.getPosCol() - 2);
                destinationCell_2.setRow(this.getPosRow() - 1);
                destinationCell_2.setCol(this.getPosCol() - 2);
                destinationCell_3.setRow(this.getPosRow() + 1);
                destinationCell_3.setCol(this.getPosCol() - 2);
                break;
            case EAST:
                destinationCell_1.setRow(this.getPosRow());
                destinationCell_1.setCol(this.getPosCol() + 2);
                destinationCell_2.setRow(this.getPosRow() + 1);
                destinationCell_2.setCol(this.getPosCol() + 2);
                destinationCell_3.setRow(this.getPosRow() - 1);
                destinationCell_3.setCol(this.getPosCol() + 2);
                break;
        }

        if (destinationCell_1.isCellValid() && destinationCell_2.isCellValid() && destinationCell_3.isCellValid()) {
            if (!exploredMap.getMap()[destinationCell_1.getRow()][destinationCell_1.getCol()].isObstacle() &&
                    !exploredMap.getMap()[destinationCell_2.getRow()][destinationCell_2.getCol()].isObstacle() &&
                    !exploredMap.getMap()[destinationCell_3.getRow()][destinationCell_3.getCol()].isObstacle()) {
                return true;
            }
        }
        return false;
    }
}
