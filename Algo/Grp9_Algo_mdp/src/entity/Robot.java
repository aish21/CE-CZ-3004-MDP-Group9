package entity;

import constant.Constants.DIRECTION;
import constant.Constants.MOVEMENT;
/**
 * @author Goh Cheng Guan, Clive
 * @author Lau Zhen Jie
 * @version 1.0
 * @since 2021-9-15
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
    
    //added new function
    public DIRECTION intDirToConstantDir(int dir) {
    	switch (dir) {
		case 1:
			return DIRECTION.NORTH;
		case 2:
			return DIRECTION.SOUTH;
		case 3:
			return DIRECTION.EAST;
		case 4:
			return DIRECTION.WEST;
		default:
			return null;
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




    
    
}
