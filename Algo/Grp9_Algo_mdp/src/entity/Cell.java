package entity;

import constant.Constants;

/**
 * @author Nicholas Yeo Ming Jie
 * @author Neo Zhao Wei
 * @author David Loh Shun Hao
 * @version 1.0
 * @since 2020-10-27
 */
public class Cell {

    private int rowPos;                         // Specifies row of map
    private int colPos;                         // Specifies column of map
    private boolean isVirtualWall;
    private String obstacleType;					//Specifies obstacle type
    private String sensor;

    /**
     * Non-default Constructor
     *
     * @param rowValue row value of cell
     * @param colValue column value of cell
     */
    public Cell(int rowValue, int colValue) {
        this.rowPos = rowValue;
        this.colPos = colValue;
        this.sensor = "none";
    }

    /**
     * Non-default Constructor
     *
     * @param rowValue  row value of cell
     * @param colValue  column value of cell
     * @param obstacleType type of obstacle
     */
    public Cell(int rowValue, int colValue, String obstacleType ) {
        this.rowPos = rowValue;
        this.colPos = colValue;
        this.obstacleType = obstacleType;
        this.sensor = "none";
    }

    /**
     * Non-default Constructor
     *
     * @param rowValue    row value of cell
     * @param colValue    column value of cell
     * @param obstacleType type of obstacle
     * @param sensorValue Sensor value that read the cell
     */
    public Cell(int rowValue, int colValue,String obstacleType, String sensorValue) {
        this.rowPos = rowValue;
        this.colPos = colValue;
        this.obstacleType = obstacleType;
        this.sensor = sensorValue;
    }

    /**
     * @param sensorValue The new value specify which sensor the cell is read by
     */
    public void setSensor(String sensorValue) {
        this.sensor = sensorValue;
    }

    /**
     * @return sensor which read the cell
     */
    public String getSensor() {
        return this.sensor;
    }

    /**
     * @param rowValue The new value to set the row value of cell
     */
    public void setRowPos(int rowValue) {
        this.rowPos = rowValue;
    }

    /**
     * @param colValue The new value to set the column value of cell
     */
    public void setColPos(int colValue) {
        this.colPos = colValue;
    }


    /**
     * @return row value of cell
     */
    public int getRowPos() {
        return this.rowPos;
    }

    /**
     * @return column value of cell
     */
    public int getColPos() {
        return this.colPos;
    }

    
    /**
     * @param set obstacle type
     */
    public void setObstacleType (String type) {
        this.obstacleType = type;
    }
    
    /**
     * @return get obstacle type
     */
    public String getObstacleType () {
        return obstacleType;
    }
    
    
    /**
     * @return the virtual wall status of cell
     */
    public boolean isVirtualWall() {
        return isVirtualWall;
    }

    /**
     * @param isVirtualWall The new value to specify if cell is a virtual wall
     */
    public void setVirtualWall(boolean isVirtualWall) {
        this.isVirtualWall = isVirtualWall;
    }

    /**
     * @return true if cell is within the width and length of arena
     */
    public boolean isCellValid() {
        if (this.rowPos >= Constants.MAX_ROW) {
            return false;
        }
        if (this.colPos >= Constants.MAX_COL) {
            return false;
        }
        if (this.rowPos < 0) {
            return false;
        }
        if (this.colPos < 0) {
            return false;
        }

        return true;
    }

}
