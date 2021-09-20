package gui;

import communication.TCPComm2;
import algorithm.AStar;
import algorithm.NearestNeighbour;
import constant.Constants;
import constant.Constants.DIRECTION;
import constant.Constants.MOVEMENT;
import entity.Cell;
import entity.Map;
import entity.Robot;

import java.util.ArrayList;

/**
 * @author Goh Cheng Guan, Clive
 * @author 
 * @version 1.0
 * @since 2021-10-20
 */
public class simulateRealRun implements Runnable {

    private main mGui;
    private Map map;
    private Robot robot;
    private TCPComm2 tcpObj;

    /**
     * This method is the non-default constructor to create simulateRealRun thread class
     *
     * @param mGUI The GUI object where the result should be displayed to.
     * @param r The robot object which specifies the detail of robot.
     * @param map The Map object that the robot should explore.
     */
    public simulateRealRun(main mGUI, Robot r, Map map) {
        this.mGui = mGUI;
        this.map = map;
        this.robot = r;
        this.tcpObj = TCPComm2.getInstance();

    }

    /**
     * This method represent the starting method for this thread to execute when called from GUI.
     * It will perform the establishment of connection to RPI and direct the physical robot to complete
     * the exploration and fastest path.
     */
    @Override
    public void run() {

        mGui.displayMsgToUI("RealRunThread Started!");
        int forwardCount = 0;
        try {
            establishCommsToRPI();
            checkandPlotSC(); // waiting for start coord from RPI
            checkandPlotWP();
            displayToUI();
            mGui.displayMsgToUI("Exploration Started, Waiting for sensor data...");


           
            // ============== Sending MDF to Android ===========================

            sendMDFInfo(); // Sending MDF1&2 to RPI
            System.out.println("MDF1: " + map.getMDF1());
            System.out.println("MDF2: " + map.getMDF2());

            // ==================== Fastest Path =========================
            mGui.displayMsgToUI("Starting Fastest Path..");
            FastestPath fastestPath = new FastestPath(this.robot, map);
            ArrayList<Cell> cellsInPath = fastestPath.findAllWPEndPaths(map);
            String movementString = convertCellsToMovements(cellsInPath); // Generate movement string based on cell
            // list.
            waitForFastestPath(); // Waiting for fastest path command
            sendMsg(movementString);

        } catch (InterruptedException e) {
            System.out.println("RealRun thread InterruptedException" + e.getMessage());
            e.printStackTrace();
            tcpObj.closeConnection();

        } catch (Exception e) {
            System.out.println("RealRun thread exception.." + e.getMessage());
            e.printStackTrace();
            tcpObj.closeConnection();

        }

        mGui.displayMsgToUI("****** RealRun Thread Ended Successfully! ******** ");

    }

    /**
     * This method take in path (List of cells) and send the respective movement commands to guide the physical robot
     * to move from cell to cell in the list.
     *
     * @param cellStep      ArrayList of cells the robot need to move to.
     * @param targetCell    The destination cell that robot should face after reaching the destination.
     * @param setSensorData To accept sensor data and send obstacle data to android.
     *                      TRUE is to send. FALSE to ignore sensor
     * @throws InterruptedException If the connection gets interrupted.
     */
    private void printMovement(ArrayList<Cell> cellStep, Cell targetCell, boolean setSensorData)
            throws InterruptedException {
        int currRow = robot.getPosRow();
        int currCol = robot.getPosCol();
        String recMsg = "";
        int forwardCount = 0;

        for (int i = 0; i < cellStep.size(); i++) {
            int destRow = cellStep.get(i).getRow();
            int destCol = cellStep.get(i).getCol();

            switch (robot.getCurrDir()) {
                case NORTH:
                    if (currCol == destCol) {
                        if (currRow < destRow) {
                            forwardCount++;
                            robot.move(MOVEMENT.FORWARD);
                            sendMsg("EX|F01" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            forwardCount = centerRepos(forwardCount, recMsg);
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }

                        } else if (currRow > destRow) {
                            robot.turn(MOVEMENT.RIGHT);
                            sendMsg("EX|R0" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                            displayToUI();

                            robot.turn(MOVEMENT.RIGHT);
                            sendMsg("EX|R0" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                            displayToUI();
                            forwardCount++;
                            robot.move(MOVEMENT.FORWARD);
                            sendMsg("EX|F01" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            forwardCount = centerRepos(forwardCount, recMsg);
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                        }
                    } else if (currRow == destRow) {
                        if (currCol < destCol) {
                            robot.turn(MOVEMENT.RIGHT);
                            sendMsg("EX|R0" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                            displayToUI();
                            forwardCount++;
                            robot.move(MOVEMENT.FORWARD);
                            sendMsg("EX|F01" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            forwardCount = centerRepos(forwardCount, recMsg);
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }

                        } else if (currCol > destCol) {
                            robot.turn(MOVEMENT.LEFT);
                            sendMsg("EX|L0" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                            displayToUI();
                            forwardCount++;
                            robot.move(MOVEMENT.FORWARD);
                            sendMsg("EX|F01" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            forwardCount = centerRepos(forwardCount, recMsg);
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                        }
                    }
                    break;
                case SOUTH:
                    if (currCol == destCol) {
                        if (currRow < destRow) {
                            robot.turn(MOVEMENT.RIGHT);
                            sendMsg("EX|R0" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                            displayToUI();

                            robot.turn(MOVEMENT.RIGHT);
                            sendMsg("EX|R0" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                            displayToUI();
                            forwardCount++;
                            robot.move(MOVEMENT.FORWARD);
                            sendMsg("EX|F01" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            forwardCount = centerRepos(forwardCount, recMsg);
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }

                        } else if (currRow > destRow) {
                            forwardCount++;
                            robot.move(MOVEMENT.FORWARD);
                            sendMsg("EX|F01" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            forwardCount = centerRepos(forwardCount, recMsg);
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                        }
                    } else if (currRow == destRow) {
                        if (currCol < destCol) {
                            robot.turn(MOVEMENT.LEFT);
                            sendMsg("EX|L0" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                            displayToUI();
                            forwardCount++;
                            robot.move(MOVEMENT.FORWARD);
                            sendMsg("EX|F01" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            forwardCount = centerRepos(forwardCount, recMsg);
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                        } else if (currCol > destCol) {
                            robot.turn(MOVEMENT.RIGHT);
                            sendMsg("EX|R0" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                            displayToUI();
                            forwardCount++;
                            robot.move(MOVEMENT.FORWARD);
                            sendMsg("EX|F01" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            forwardCount = centerRepos(forwardCount, recMsg);
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                        }
                    }
                    break;
                case EAST:
                    if (currCol == destCol) {
                        if (currRow < destRow) {
                            robot.turn(MOVEMENT.LEFT);
                            sendMsg("EX|L0" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                            displayToUI();
                            forwardCount++;
                            robot.move(MOVEMENT.FORWARD);
                            sendMsg("EX|F01" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            forwardCount = centerRepos(forwardCount, recMsg);
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                        } else if (currRow > destRow) {
                            robot.turn(MOVEMENT.RIGHT);
                            sendMsg("EX|R0" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                            displayToUI();
                            forwardCount++;
                            robot.move(MOVEMENT.FORWARD);
                            sendMsg("EX|F01" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            forwardCount = centerRepos(forwardCount, recMsg);
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                        }
                    } else if (currRow == destRow) {
                        if (currCol < destCol) {
                            forwardCount++;
                            robot.move(MOVEMENT.FORWARD);
                            sendMsg("EX|F01" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            forwardCount = centerRepos(forwardCount, recMsg);
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                        } else if (currCol > destCol) {
                            robot.turn(MOVEMENT.RIGHT);
                            sendMsg("EX|R0" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                            displayToUI();

                            robot.turn(MOVEMENT.RIGHT);
                            sendMsg("EX|R0" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                            displayToUI();
                            forwardCount++;
                            robot.move(MOVEMENT.FORWARD);
                            sendMsg("EX|F01" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            forwardCount = centerRepos(forwardCount, recMsg);
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                        }
                    }
                    break;
                case WEST:
                    if (currCol == destCol) {
                        if (currRow < destRow) {

                            robot.turn(MOVEMENT.RIGHT);
                            sendMsg("EX|R0" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                            displayToUI();
                            forwardCount++;
                            robot.move(MOVEMENT.FORWARD);
                            sendMsg("EX|F01" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            forwardCount = centerRepos(forwardCount, recMsg);
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                        } else if (currRow > destRow) {

                            robot.turn(MOVEMENT.LEFT);
                            sendMsg("EX|L0" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                            displayToUI();
                            forwardCount++;
                            robot.move(MOVEMENT.FORWARD);
                            sendMsg("EX|F01" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            forwardCount = centerRepos(forwardCount, recMsg);
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                        }
                    } else if (currRow == destRow) {
                        if (currCol < destCol) {

                            robot.turn(MOVEMENT.RIGHT);
                            sendMsg("EX|R0" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                            displayToUI();

                            robot.turn(MOVEMENT.RIGHT);
                            sendMsg("EX|R0" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                            displayToUI();
                            forwardCount++;
                            robot.move(MOVEMENT.FORWARD);
                            sendMsg("EX|F01" + map.rpiImageString(robot));
                            recMsg = readMsg();
                            forwardCount = centerRepos(forwardCount, recMsg);
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }

                        } else if (currCol > destCol) {
                            forwardCount++;
                            robot.move(MOVEMENT.FORWARD);
                            sendMsg("EX|F01" + map.rpiImageString(robot));

                            recMsg = readMsg();
                            forwardCount = centerRepos(forwardCount, recMsg);
                            if (setSensorData) {
                                map.setExploredCells(robot, recMsg);
                                sendMDFInfo();
                            }
                        }
                    }
                    break;
            }

            displayToUI();

            if (targetCell != null && this.map.getMapGrid()[targetCell.getRow()][targetCell.getCol()].getExploredState()) {
                return;
            }

            if (i == cellStep.size() - 1) {
                if (targetCell != null && !targetCell.getExploredState()) {
                    destRow = targetCell.getRow();
                    destCol = targetCell.getCol();

                    switch (robot.getCurrDir()) {
                        case NORTH:
                            if (currCol == destCol) {
                                if (currRow < destRow) {
                                    // do nothing
                                } else if (currRow > destRow) {
                                    robot.turn(MOVEMENT.RIGHT);
                                    sendMsg("EX|R0" + map.rpiImageString(robot));
                                    recMsg = readMsg();
                                    if (setSensorData) {
                                        map.setExploredCells(robot, recMsg);
                                        sendMDFInfo();
                                    }
                                    displayToUI();

                                    robot.turn(MOVEMENT.RIGHT);
                                    sendMsg("EX|R0" + map.rpiImageString(robot));
                                    recMsg = readMsg();
                                    if (setSensorData) {
                                        map.setExploredCells(robot, recMsg);
                                        sendMDFInfo();
                                    }
                                    displayToUI();
                                }
                            } else if (currRow == destRow) {
                                if (currCol < destCol) {
                                    robot.turn(MOVEMENT.RIGHT);
                                    sendMsg("EX|R0" + map.rpiImageString(robot));
                                    recMsg = readMsg();
                                    if (setSensorData) {
                                        map.setExploredCells(robot, recMsg);
                                        sendMDFInfo();
                                    }
                                    displayToUI();

                                } else if (currCol > destCol) {
                                    robot.turn(MOVEMENT.LEFT);
                                    sendMsg("EX|L0" + map.rpiImageString(robot));
                                    recMsg = readMsg();
                                    if (setSensorData) {
                                        map.setExploredCells(robot, recMsg);
                                        sendMDFInfo();
                                    }
                                    displayToUI();
                                }
                            }
                            break;
                        case SOUTH:
                            if (currCol == destCol) {
                                if (currRow < destRow) {
                                    robot.turn(MOVEMENT.RIGHT);
                                    sendMsg("EX|R0" + map.rpiImageString(robot));
                                    recMsg = readMsg();
                                    if (setSensorData) {
                                        map.setExploredCells(robot, recMsg);
                                        sendMDFInfo();
                                    }
                                    displayToUI();

                                    robot.turn(MOVEMENT.RIGHT);
                                    sendMsg("EX|R0" + map.rpiImageString(robot));
                                    recMsg = readMsg();
                                    if (setSensorData) {
                                        map.setExploredCells(robot, recMsg);
                                        sendMDFInfo();
                                    }
                                    displayToUI();

                                } else if (currRow > destRow) {
                                    // do nothing
                                }
                            } else if (currRow == destRow) {
                                if (currCol < destCol) {
                                    robot.turn(MOVEMENT.LEFT);
                                    sendMsg("EX|L0" + map.rpiImageString(robot));
                                    recMsg = readMsg();
                                    if (setSensorData) {
                                        map.setExploredCells(robot, recMsg);
                                        sendMDFInfo();
                                    }
                                    displayToUI();
                                } else if (currCol > destCol) {
                                    robot.turn(MOVEMENT.RIGHT);
                                    sendMsg("EX|R0" + map.rpiImageString(robot));
                                    recMsg = readMsg();
                                    if (setSensorData) {
                                        map.setExploredCells(robot, recMsg);
                                        sendMDFInfo();
                                    }
                                    displayToUI();
                                }
                            }
                            break;
                        case EAST:
                            if (currCol == destCol) {
                                if (currRow < destRow) {
                                    robot.turn(MOVEMENT.LEFT);
                                    sendMsg("EX|L0" + map.rpiImageString(robot));
                                    recMsg = readMsg();
                                    if (setSensorData) {
                                        map.setExploredCells(robot, recMsg);
                                        sendMDFInfo();
                                    }
                                    displayToUI();
                                } else if (currRow > destRow) {
                                    robot.turn(MOVEMENT.RIGHT);
                                    sendMsg("EX|R0" + map.rpiImageString(robot));
                                    recMsg = readMsg();
                                    if (setSensorData) {
                                        map.setExploredCells(robot, recMsg);
                                        sendMDFInfo();
                                    }
                                    displayToUI();
                                }
                            } else if (currRow == destRow) {
                                if (currCol < destCol) {
                                    // do nothing
                                } else if (currCol > destCol) {
                                    robot.turn(MOVEMENT.RIGHT);
                                    sendMsg("EX|R0" + map.rpiImageString(robot));
                                    recMsg = readMsg();
                                    if (setSensorData) {
                                        map.setExploredCells(robot, recMsg);
                                        sendMDFInfo();
                                    }
                                    displayToUI();

                                    robot.turn(MOVEMENT.RIGHT);
                                    sendMsg("EX|R0" + map.rpiImageString(robot));
                                    recMsg = readMsg();
                                    if (setSensorData) {
                                        map.setExploredCells(robot, recMsg);
                                        sendMDFInfo();
                                    }
                                    displayToUI();
                                }
                            }
                            break;
                        case WEST:
                            if (currCol == destCol) {
                                if (currRow < destRow) {
                                    robot.turn(MOVEMENT.RIGHT);
                                    sendMsg("EX|R0" + map.rpiImageString(robot));
                                    recMsg = readMsg();
                                    if (setSensorData) {
                                        map.setExploredCells(robot, recMsg);
                                        sendMDFInfo();
                                    }
                                    displayToUI();
                                } else if (currRow > destRow) {
                                    robot.turn(MOVEMENT.LEFT);
                                    sendMsg("EX|L0" + map.rpiImageString(robot));
                                    recMsg = readMsg();
                                    if (setSensorData) {
                                        map.setExploredCells(robot, recMsg);
                                        sendMDFInfo();
                                    }
                                    displayToUI();
                                }
                            } else if (currRow == destRow) {
                                if (currCol < destCol) {
                                    robot.turn(MOVEMENT.RIGHT);
                                    sendMsg("EX|R0" + map.rpiImageString(robot));
                                    recMsg = readMsg();
                                    if (setSensorData) {
                                        map.setExploredCells(robot, recMsg);
                                        sendMDFInfo();
                                    }
                                    displayToUI();

                                    robot.turn(MOVEMENT.RIGHT);
                                    sendMsg("EX|R0" + map.rpiImageString(robot));
                                    recMsg = readMsg();
                                    if (setSensorData) {
                                        map.setExploredCells(robot, recMsg);
                                        sendMDFInfo();
                                    }
                                    displayToUI();

                                } else if (currCol > destCol) {
                                    // do nothing
                                }
                            }
                            break;
                    }
                }

            }
            currRow = robot.getPosRow();
            currCol = robot.getPosCol();
        }
    }

    /**
     * This method return a list of cells that are unexplored in the arena.
     *
     * @param exploredMap The arena/map object
     * @return ArrayList of cell that contain all unexplored cell of the input arena/map.
     */
    private ArrayList<Cell> getUnexploredList(Map exploredMap) {

        ArrayList<Cell> unexploredList = new ArrayList<Cell>();

        for (int r = 0; r < Constants.MAX_ROW; r++) {
            for (int c = 0; c < Constants.MAX_COL; c++) {
                Cell cell = exploredMap.getMapGrid()[r][c];
                if (cell.getExploredState() == false) {
                    unexploredList.add(cell);
                }
            }
        }
        return unexploredList;

    }

    /**
     * This method return an updated list of cells that are unexplored.
     *
     * @param clist An existing arraylist of cell that are unexplored.
     * @return ArrayList of unexplored cell that are updated according to the latest arena/map object.
     */
    private ArrayList<Cell> updateUnexploreList(ArrayList<Cell> clist) {
        for (int i = 0; i < clist.size(); i++) {
            Cell cObj = clist.get(i);
            if (map.getMapGrid()[cObj.getRow()][cObj.getCol()].getExploredState()) {
                clist.remove(cObj);
                i--;
            }
        }
        return clist;
    }


    /**
     * This method is for calibrating the physical Robot to perform corner calibration while facing south
     * and to face North direction upon completion.
     *
     * @throws InterruptedException If the connection gets interrupted.
     */
    private void endExploreCalibrate() throws InterruptedException {
        mGui.displayMsgToUI("Calibrating ROBOT..!");
        faceDirection(DIRECTION.SOUTH);
        sendMsg("EX|V0|(0),(0)|(0),(0)");
        faceDirection(DIRECTION.NORTH);
    }

    /**
     * This method direct the robot to face the specified direction regardless of its current facing direction
     *
     * @param dir The direction that the robot should face.
     * @throws InterruptedException If the connection gets interrupted.
     */
    private void faceDirection(DIRECTION dir) throws InterruptedException {

        String recMsg = "";

        switch (dir) {

            case NORTH:
                switch (this.robot.getCurrDir()) {

                    case NORTH:    //do nothing
                        break;

                    case EAST: //turn left
                        this.robot.turn(MOVEMENT.LEFT);
                        sendMsg("EX|L0" + map.rpiImageString(robot));
                        recMsg = readMsg();
                        map.setExploredCells(robot, recMsg);
                        sendMDFInfo();
                        displayToUI();
                        break;

                    case WEST: //turn right
                        this.robot.turn(MOVEMENT.RIGHT);
                        sendMsg("EX|R0" + map.rpiImageString(robot));
                        recMsg = readMsg();
                        map.setExploredCells(robot, recMsg);
                        sendMDFInfo();
                        displayToUI();
                        break;

                    case SOUTH: //turn backward
                        robot.turn(MOVEMENT.RIGHT);
                        sendMsg("EX|R0" + map.rpiImageString(robot));
                        recMsg = readMsg();
                        map.setExploredCells(robot, recMsg);
                        sendMDFInfo();
                        displayToUI();

                        robot.turn(MOVEMENT.RIGHT);
                        sendMsg("EX|R0" + map.rpiImageString(robot));
                        recMsg = readMsg();
                        map.setExploredCells(robot, recMsg);
                        sendMDFInfo();
                        displayToUI();

                        break;
                }
                break;


            case EAST:
                switch (this.robot.getCurrDir()) {

                    case NORTH:
                        this.robot.turn(MOVEMENT.RIGHT);
                        sendMsg("EX|R0" + map.rpiImageString(robot));
                        recMsg = readMsg();
                        map.setExploredCells(robot, recMsg);
                        sendMDFInfo();
                        displayToUI();

                        break;

                    case EAST: //do nothing
                        break;

                    case WEST:
                        robot.turn(MOVEMENT.RIGHT);
                        sendMsg("EX|R0" + map.rpiImageString(robot));
                        recMsg = readMsg();
                        map.setExploredCells(robot, recMsg);
                        sendMDFInfo();
                        displayToUI();

                        robot.turn(MOVEMENT.RIGHT);
                        sendMsg("EX|R0" + map.rpiImageString(robot));
                        recMsg = readMsg();
                        map.setExploredCells(robot, recMsg);
                        sendMDFInfo();
                        displayToUI();
                        break;

                    case SOUTH:
                        this.robot.turn(MOVEMENT.LEFT);
                        sendMsg("EX|L0" + map.rpiImageString(robot));
                        recMsg = readMsg();
                        map.setExploredCells(robot, recMsg);
                        sendMDFInfo();
                        displayToUI();
                        break;
                }
                break;


            case WEST:
                switch (this.robot.getCurrDir()) {

                    case NORTH:    //turn left
                        this.robot.turn(MOVEMENT.LEFT);
                        sendMsg("EX|L0" + map.rpiImageString(robot));
                        recMsg = readMsg();
                        map.setExploredCells(robot, recMsg);
                        sendMDFInfo();
                        displayToUI();
                        break;

                    case EAST: //turn backward
                        robot.turn(MOVEMENT.RIGHT);
                        sendMsg("EX|R0" + map.rpiImageString(robot));
                        recMsg = readMsg();
                        map.setExploredCells(robot, recMsg);
                        sendMDFInfo();
                        displayToUI();

                        robot.turn(MOVEMENT.RIGHT);
                        sendMsg("EX|R0" + map.rpiImageString(robot));
                        recMsg = readMsg();
                        map.setExploredCells(robot, recMsg);
                        sendMDFInfo();
                        displayToUI();
                        break;

                    case WEST: //do nothing
                        break;

                    case SOUTH: //turn right
                        robot.turn(MOVEMENT.RIGHT);
                        sendMsg("EX|R0" + map.rpiImageString(robot));
                        recMsg = readMsg();
                        map.setExploredCells(robot, recMsg);
                        sendMDFInfo();
                        displayToUI();

                        break;
                }
                break;

            case SOUTH:
                switch (this.robot.getCurrDir()) {

                    case NORTH:    //turn backward
                        robot.turn(MOVEMENT.RIGHT);
                        sendMsg("EX|R0" + map.rpiImageString(robot));
                        recMsg = readMsg();
                        map.setExploredCells(robot, recMsg);
                        sendMDFInfo();
                        displayToUI();

                        robot.turn(MOVEMENT.RIGHT);
                        sendMsg("EX|R0" + map.rpiImageString(robot));
                        recMsg = readMsg();
                        map.setExploredCells(robot, recMsg);
                        sendMDFInfo();
                        displayToUI();
                        break;

                    case EAST: //turn right
                        robot.turn(MOVEMENT.RIGHT);
                        sendMsg("EX|R0" + map.rpiImageString(robot));
                        recMsg = readMsg();
                        map.setExploredCells(robot, recMsg);
                        sendMDFInfo();
                        displayToUI();

                        break;

                    case WEST: //turn left
                        this.robot.turn(MOVEMENT.LEFT);
                        sendMsg("EX|L0" + map.rpiImageString(robot));
                        recMsg = readMsg();
                        map.setExploredCells(robot, recMsg);
                        sendMDFInfo();
                        displayToUI();
                        break;

                    case SOUTH: //do nothing
                        break;
                }
                break;
        }


    }
    // ================ FastestPath ===========================

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
        String result = "FP|";

        while (i < fastestPathMovements.size()) {

            switch (fastestPathMovements.get(i)) {

                case FORWARD:
                    result += "F";
                    break;
                case LEFT:
                    result += "L0";
                    break;
                case RIGHT:
                    result += "R0";
                    break;
                case BACKWARD:
                    result += "B1";
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

            if (fastestPathMovements.get(i) == MOVEMENT.FORWARD) {
                if (counter < 10) {
                    result += "0" + Integer.toString(counter);
                } else {
                    result += Integer.toString(counter);
                }
            }

            i = j;
            result += "|";
            counter = 1;
        }

        result += "D0|";
        System.out.println("parseFPMovements():" + result);
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
                            i--;
                        } else if (currCol > destCol) {
                            fastestPathMovements.add(MOVEMENT.LEFT);
                            mBot.turn(MOVEMENT.LEFT);
                            i--;
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
                            i--;
                        } else if (currCol > destCol) {
                            fastestPathMovements.add(MOVEMENT.RIGHT);
                            mBot.turn(MOVEMENT.RIGHT);
                            i--;
                        }
                    }
                    break;
                case EAST:
                    if (currCol == destCol) {
                        if (currRow < destRow) {
                            fastestPathMovements.add(MOVEMENT.LEFT);
                            mBot.turn(MOVEMENT.LEFT);
                            i--;
                        } else if (currRow > destRow) {
                            fastestPathMovements.add(MOVEMENT.RIGHT);
                            mBot.turn(MOVEMENT.RIGHT);
                            i--;
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
                            i--;
                        } else if (currRow > destRow) {
                            fastestPathMovements.add(MOVEMENT.LEFT);
                            mBot.turn(MOVEMENT.LEFT);
                            i--;
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

    // ===================== Arduino Commands ===============================

    /**
     * This method issue the physical robot with alignment and calibration instructions depending
     * on the received sensor reading and number of forward step taken.
     *
     * @param forwardCount       The current number of forward step that the physical robot have taken.
     * @param sensorDataInString The sensor reading from the physical robot.
     * @return The updated forwardCount according to the calibration performed.
     */
    private int centerRepos(int forwardCount, String sensorDataInString) {
        int rightFront = Character.getNumericValue(sensorDataInString.charAt(1));
        int rightBack = Character.getNumericValue(sensorDataInString.charAt(0));
        int frontRight = Character.getNumericValue(sensorDataInString.charAt(3));
        int frontCenter = Character.getNumericValue(sensorDataInString.charAt(4));
        int frontLeft = Character.getNumericValue(sensorDataInString.charAt(5));

        if (rightFront == 1 && rightBack == 1 && frontRight == 1 && frontCenter == 1 && frontLeft == 1) {
            sendMsg("EX|V0|(0),(0)|(0),(0)");
            return 0;
        } else if (frontRight == 1 && frontCenter == 1 && frontLeft == 1) {
            sendMsg("EX|Q0|(0),(0)|(0),(0)");
        } else if (forwardCount >= 4 && rightFront == 1 && rightBack == 1) {
            sendMsg("EX|P0|(0),(0)|(0),(0)");
            return 0;
        } else if (rightFront == 1 && rightBack == 1) {
            sendMsg("EX|E0|(0),(0)|(0),(0)");
        }

        return forwardCount;
    }


    // ======================= GUI PAINTING ===================================

    /**
     * This method will paint the current map object perceived by the robot to GUI
     * for the users to see the current status of exploration.
     */
    private void displayToUI() {
        mGui.paintResult();
    }

    // ================= Communication Methods with RPI =======================

    /**
     * This method establishes the connection with RPI via WiFi and will recursively attempt
     * to connect until interrupted by GUI.
     *
     * @throws InterruptedException If the connection gets interrupted.
     */
    private void establishCommsToRPI() throws InterruptedException {
        String msg = "";

        do {
            mGui.displayMsgToUI("Establishing connection to RPI..  :D ");
            msg = this.tcpObj.establishConnection();
            if (msg.length() != 0) {
                mGui.displayMsgToUI(msg);

                Thread.sleep((long) (1 * 1000));

            } else {
                mGui.displayMsgToUI("Connected Successfully :DD ");
                break;
            }

        } while (!Thread.currentThread().isInterrupted());
    }

    /**
     * This method stall the program to read the inputstream for messages from the RPI.
     * This method will also send the MDF information and terminate the program upon receiving the command "N|" from RPI.
     *
     * @return The received message.
     * @throws InterruptedException If the connection gets interrupted.
     */
    private String readMsg() throws InterruptedException {

        String msg = "";
        do {
            msg = tcpObj.readMessage();
        } while (msg == null || msg.length() == 0);

        if (msg.substring(0, 2).equals("N|")) {
            sendMDFInfo();
            throw new InterruptedException();
        }
        mGui.displayMsgToUI("Received: " + msg);
        return msg;
    }

    /**
     * This method will send the string parameter as a message to RPI.
     *
     * @param msg The string message to be transmitted to RPI.
     */
    private void sendMsg(String msg) {

        tcpObj.sendMessage(msg + "!");
        mGui.displayMsgToUI("Sent: " + msg);

    }

    // ==================== Communication Methods with Android =======================

    /**
     * This method will stall the program to read the input stream for the start coordinate of the robot from RPI.
     * It will update the location of the robot object upon receiving from RPI.
     *
     * @throws Exception If the connection gets interrupted.
     */
    private void checkandPlotSC() throws Exception {
        String rmsg = "";
        // SC|[1,1] = 8, SC|[10,10] = 10
        mGui.displayMsgToUI("Waiting for start coordinate...");
        do {

            rmsg = readMsg();

            if (rmsg.substring(0, 3).equals("SC|")) {
                String[] arr = rmsg.substring(4, rmsg.length() - 2).split(",");
                this.robot.setPosRow(Integer.parseInt(arr[0]));
                this.robot.setPosCol(Integer.parseInt(arr[1]));
                return;
            }

        } while (true);

    }

    /**
     * This method will stall the program to read the input stream for the fastest path command from RPI.
     *
     * @throws Exception If the connection gets interrupted.
     */
    private void waitForFastestPath() throws Exception {
        String rmsg = "";
        mGui.displayMsgToUI("Waiting for command to start FastestPath...");
        do {
            rmsg = readMsg();

            if (rmsg.substring(0, 3).equals("FP|")) {
                return;
            }
        } while (true);
    }

    /**
     * This method will stall the program to read the input stream for the WayPoint coordinate from RPI.
     * It will create the WayPoint on the map object upon receiving from RPI.
     *
     * @throws Exception If the connection gets interrupted.
     */
    private void checkandPlotWP() throws Exception {
        String rmsg = "";
        // WP|[1,1,1] [row ,col, obDir]
        mGui.displayMsgToUI("Waiting for obstacles coordinate...");
        do {
            rmsg = readMsg();
            if (rmsg.substring(0, 3).equals("OB|")) {
                String[] arr = rmsg.substring(4, rmsg.length() - 2).split(",");
                this.map.setObstacle(Integer.parseInt(arr[1]), Integer.parseInt(arr[0]),Integer.parseInt(arr[3]));
                return;
            }
        } while (true);
    }


    /**
     * This method concatenate the MDF1 and MDF2 of the map object
     * and send it to RPI.
     */
    private void sendMDFInfo() {
        String mdf1 = map.getMDF1();
        String mdf2 = map.getMDF2();
        sendMsg("MDF|" + mdf1 + "|" + mdf2);
    }

}
