package gui;

import communication.TCPComm2;
import constant.Constants;
import constant.Constants.DIRECTION;
import entity.Cell;
import entity.Map;
import entity.Robot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class MainGUI extends JFrame {

    private JPanel controlPanel;
    private settingPanel acontrolPanel;
    private MapPanel simulationMap;
    private JPanel designMap;
    private JLabel[][] resultMap;            // Use to display simulation

    Thread simExplore, simFastest, simRealRun;
    Map initialMap;
    Robot rBot;
    static MainGUI mGui = null;

    /**
     * This method is a private default constructor of this class and it creates the GUI controls
     * and instantiate objects for running this simulation.
     */
    private MainGUI() {

        setMapRobotObj();
        initLayout();
        paintResult();
    }

    /**
     * This method create the initial Map object and robot object.
     */
    private void setMapRobotObj() {
        initialMap = new Map();
        rBot = new Robot(1, 1, DIRECTION.SOUTH);
    }

    /**
     * This method return the singleton object of this class
     *
     * @return MainGUI object
     */
    public static MainGUI getInstance() {
        if (mGui == null) {
            mGui = new MainGUI();
        }
        return mGui;
    }

    /**
     * This method create the main layout and create all the necessary panel
     * for the simulator
     */
    private void initLayout() {
        // Creates and set up a frame window

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        simulationMap = new MapPanel(false);
        setupControlPanel();
        designMap = new MapPanel(true);
        acontrolPanel = new settingPanel();
        resultMap = simulationMap.getJLabelMap();

        setLayout(new FlowLayout());
        setTitle("CX3004 MDP Group 8");
        add(simulationMap);
        add(controlPanel);
        add(acontrolPanel);
        pack();
        setVisible(true);

    }

    /**
     * This method create the panel with main buttons to execute various algorithm
     */
    private void setupControlPanel() {
        // Define control buttons

        JComboBox fileDDL;
        JButton exploreBtn = new JButton("Explore");
        JButton fastestBtn = new JButton("Fastest Path");
        JButton resetBtn = new JButton("Reset");
        JButton realrunBtn = new JButton("Real Run");

        String[] fileList = FileManager.getAllFileNames();
        fileDDL = new JComboBox(fileList);

        exploreBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {

                simExplore = new Thread(new simulateExploration(mGui, initialMap, rBot, fileDDL.getSelectedItem().toString()));
                simExplore.start();

                exploreBtn.setEnabled(false);
            }
        });

        fastestBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                simFastest = new Thread(new simulateFastestPath(mGui, rBot, initialMap));
                simFastest.start();

                fastestBtn.setEnabled(false);
            }

        });

        realrunBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                simRealRun = new Thread(new simulateRealRun(mGui, rBot, initialMap));
                simRealRun.start();

                realrunBtn.setEnabled(false);
            }

        });
        resetBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (simExplore != null) {
                    simExplore.interrupt();
                    simExplore = null;
                }
                if (simFastest != null) {
                    simFastest.interrupt();
                    simFastest = null;
                }
                if (simRealRun != null) {
                    //Close the socket to end any existing reading from RPI
                    TCPComm2.getInstance().closeConnection();
                    simRealRun.interrupt();
                    simRealRun = null;
                }

                setMapRobotObj();
                paintResult();    //repaint the map UI

                //reseting UI control
                acontrolPanel.waypointRow_cb.setSelectedIndex(0);
                acontrolPanel.waypointCol_cb.setSelectedIndex(0);
                acontrolPanel.lb_percentDisplay.setText("0%");
                acontrolPanel.lb_timerDisplay.setText("0");
                exploreBtn.setEnabled(true);
                fastestBtn.setEnabled(true);
                realrunBtn.setEnabled(true);
                displayMsgToUI("Map and robot has been reset!");
            }

        });


        // Define panels to hold the control buttons
        controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        controlPanel.setLayout(new GridLayout(11, 1, 0, 10));

        controlPanel.add(fileDDL);
        controlPanel.add(exploreBtn);
        controlPanel.add(fastestBtn);
        controlPanel.add(resetBtn);
        controlPanel.add(realrunBtn);

        controlPanel.setPreferredSize(new Dimension(300, 700));
        controlPanel.setBackground(Color.blue);
    }

    private class settingPanel extends JPanel {

        int mainXcoord = 0;
        int mainYcoord = 0;

        JLabel lb_movecontrol = new JLabel("Robot Control:");
        JLabel lb_waypoint = new JLabel("WayPoint(Y,X):");
        JLabel lb_percent = new JLabel("Coverage Percentage:");
        JLabel lb_speed = new JLabel("Speed(steps/sec):");
        JLabel lb_timelimit = new JLabel("Time Limit:");
        JLabel lb_timerDisplayHeader = new JLabel("Timer(sec) : ");
        JLabel lb_percentDisplayHeader = new JLabel("Current Coverage: ");
        JLabel lb_percentDisplay = new JLabel("0%");
        JLabel lb_timerDisplay = new JLabel("0");

        JTextArea ta_percent = new JTextArea("100");
        JTextArea ta_speed = new JTextArea("10");
        JTextArea ta_timelimit = new JTextArea("0");


        JComboBox waypointRow_cb = new JComboBox(getNumString(1, 18));
        JComboBox waypointCol_cb = new JComboBox(getNumString(1, 13));

        JTextArea editTextArea;
        JScrollPane scroll;

        public settingPanel() {

            scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            editTextArea = new JTextArea();
            editTextArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
            editTextArea.setLineWrap(true);
            editTextArea.setEditable(false);
            scroll.getViewport().add(editTextArea);


            lb_percentDisplayHeader.setBounds(mainXcoord + 10, mainYcoord + 10, 120, 40);
            lb_percentDisplay.setBounds(mainXcoord + 150, mainYcoord + 10, 120, 40);

            lb_timerDisplayHeader.setBounds(mainXcoord + 10, mainYcoord + 60, 100, 40);
            lb_timerDisplay.setBounds(mainXcoord + 150, mainYcoord + 60, 120, 40);

            lb_waypoint.setBounds(mainXcoord + 10, mainYcoord + 110, 100, 20);
            waypointRow_cb.setBounds(mainXcoord + 150, mainYcoord + 110, 100, 30);
            waypointCol_cb.setBounds(mainXcoord + 300, mainYcoord + 110, 100, 30);

            lb_percent.setBounds(mainXcoord + 10, mainYcoord + 150, 150, 20);
            ta_percent.setBounds(mainXcoord + 150, mainYcoord + 150, 150, 20);

            lb_speed.setBounds(mainXcoord + 10, mainYcoord + 180, 100, 20);
            ta_speed.setBounds(mainXcoord + 150, mainYcoord + 180, 100, 20);

            lb_timelimit.setBounds(mainXcoord + 10, mainYcoord + 210, 100, 20);
            ta_timelimit.setBounds(mainXcoord + 150, mainYcoord + 210, 100, 20);

            scroll.setBounds(mainXcoord + 10, mainYcoord + 250, 550, 500);

            lb_percentDisplay.setPreferredSize(new Dimension(300, 700));
            lb_percentDisplay.setOpaque(true);
            lb_percentDisplay.setBackground(Color.WHITE);
            lb_percentDisplay.setFont(new Font("Monospaced", Font.BOLD, 25));
            lb_percentDisplay.setHorizontalAlignment(JLabel.CENTER);

            lb_timerDisplay.setPreferredSize(new Dimension(300, 700));
            lb_timerDisplay.setOpaque(true);
            lb_timerDisplay.setBackground(Color.WHITE);
            lb_timerDisplay.setFont(new Font("Monospaced", Font.BOLD, 25));
            lb_timerDisplay.setHorizontalAlignment(JLabel.CENTER);

            setLayout(null);
            setBackground(Color.yellow);
            setPreferredSize(new Dimension(576, 756));
            add(lb_percentDisplayHeader);
            add(lb_timerDisplayHeader);
            add(lb_timerDisplay);
            add(lb_percentDisplay);

            add(lb_waypoint);
            add(waypointRow_cb);
            add(waypointCol_cb);

            add(lb_percent);
            add(ta_percent);

            add(lb_speed);
            add(ta_speed);

            add(lb_timelimit);
            add(ta_timelimit);

            add(scroll);

            waypointRow_cb.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    initialMap.setWayPoint(Integer.parseInt(waypointRow_cb.getSelectedItem().toString())
                            , Integer.parseInt(waypointCol_cb.getSelectedItem().toString()));
                    paintResult();
                }
            });

            waypointCol_cb.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    initialMap.setWayPoint(Integer.parseInt(waypointRow_cb.getSelectedItem().toString())
                            , Integer.parseInt(waypointCol_cb.getSelectedItem().toString()));
                    paintResult();
                }
            });
        }

        /**
         * This method generate a string array with all numeric value that exist between 2 specified numbers
         *
         * @param startNum The first number to be in the array
         * @param endNum   The last number to be in the array
         * @return String array that contains all numeric value between specified first and last number
         */
        private String[] getNumString(int startNum, int endNum) {
            String[] numArr = new String[endNum - startNum + 1];
            int counter = 0;
            for (int i = startNum; i <= endNum; i++) {
                numArr[counter] = Integer.toString(i);
                counter++;
            }
            return numArr;
        }
    }

    /**
     * This method get the waypoint coordinates specified by user on GUI
     *
     * @return int array which represent row and column coordinate of the Waypoint
     */
    public int[] getUserWayPoint() {
        int[] waypointArr = new int[2];
        waypointArr[0] = Integer.parseInt(this.acontrolPanel.waypointRow_cb.getSelectedItem().toString());
        waypointArr[1] = Integer.parseInt(this.acontrolPanel.waypointCol_cb.getSelectedItem().toString());

        return waypointArr;
    }

    /**
     * This method get the coverage percentage of exploration specified by user from GUI
     *
     * @return The coverage percentage specified by user on GUI
     */
    public int getUserPercentage() {
        int coverage = -1;
        coverage = Integer.parseInt(this.acontrolPanel.ta_percent.getText().toString());
        return coverage;
    }

    /**
     * This method get the time limit for exploration specified by user on GUI
     *
     * @return the time(seconds) value as type int.
     */
    public int getUserTimeLimit() {
        int timeLimit = 0;
        timeLimit = Integer.parseInt(this.acontrolPanel.ta_timelimit.getText().toString());
        return timeLimit;
    }

    /**
     * This method get the step per second input by user on GUI
     *
     * @return float value specified by user from GUI
     */
    public float getUserSpeed() {
        float speed = 1;
        speed = Float.parseFloat(this.acontrolPanel.ta_speed.getText().toString());
        return speed;
    }

    /**
     * This method display string text in the scrollview on GUI
     *
     * @param msg String value to be displayed on GUI.
     */
    public void displayMsgToUI(String msg) {
        this.acontrolPanel.editTextArea.append(msg + "\n");
        this.acontrolPanel.editTextArea.setCaretPosition(this.acontrolPanel.editTextArea.getText().length());
    }

    /**
     * This method display the percentage of map coverage on GUI
     *
     * @param percent the percentage in type double to be displayed
     */
    public void displayMapCoverToUI(double percent) {
        this.acontrolPanel.lb_percentDisplay.setText((int) percent + " %");
    }

    /**
     * This method display the timer value(seconds) on GUI
     *
     * @param timeValue the int value which represents time in seconds to be displayed
     */
    public void displayTimerToUI(int timeValue) {
        this.acontrolPanel.lb_timerDisplay.setText(String.valueOf(timeValue));
    }

    /**
     * This method updates the GUI on how the current map looks like with the robot on it.
     * It does not modifies the map object with robot object
     */
    public void paintResult() {

        for (int i = 0; i < Constants.MAX_ROW; i++) {
            for (int y = 0; y < Constants.MAX_COL; y++) {
                Cell cellObj = initialMap.getMapGrid()[i][y];

                if (cellObj.getExploredState()) {

                    if (cellObj.isObstacle()) {
                        resultMap[i + 1][y + 1].setBackground(getMapColorForCell('W'));
                    } else {
                        //Cell is a path.
                        resultMap[i + 1][y + 1].setBackground(getMapColorForCell('P'));
                    }
                } else {
                    //Cell is Unexplored.
                    resultMap[i + 1][y + 1].setBackground(getMapColorForCell('U'));
                }
            }
        }
        Cell startZone = initialMap.getStartGoalPosition();
        Cell endZone = initialMap.getEndGoalPosition();

        //Paint the waypoint on label
        resultMap[initialMap.getWayPoint().getRowPos() + 1][initialMap.getWayPoint().getColPos() + 1].setBackground(getMapColorForCell('B'));

        //Paint the Start and End Zone (3x3 grid)
        for (int i : Constants.WITHIN_3BY3) {
            for (int y : Constants.WITHIN_3BY3) {

                resultMap[startZone.getRowPos() + i + 1][startZone.getColPos() + y + 1].setBackground(getMapColorForCell('S'));
                resultMap[endZone.getRowPos() + i + 1][endZone.getColPos() + y + 1].setBackground(getMapColorForCell('E'));

            }
        }
        //Paint the robot (3x3 grid)
        for (int i : Constants.WITHIN_3BY3) {
            for (int y : Constants.WITHIN_3BY3) {
                resultMap[rBot.getPosRow() + i + 1][rBot.getPosCol() + y + 1].setBackground(getMapColorForCell('R'));

            }
        }

        //Paint the front of the robot
        switch (rBot.getCurrDir().toString()) {

            case "NORTH":
                resultMap[rBot.getPosRow() + 2][rBot.getPosCol() + 1].setBackground(getMapColorForCell('H'));

                break;
            case "EAST":
                resultMap[rBot.getPosRow() + 1][rBot.getPosCol() + 2].setBackground(getMapColorForCell('H'));

                break;
            case "SOUTH":
                resultMap[rBot.getPosRow()][rBot.getPosCol() + 1].setBackground(getMapColorForCell('H'));

                break;
            case "WEST":
                resultMap[rBot.getPosRow() + 1][rBot.getPosCol()].setBackground(getMapColorForCell('H'));

                break;

        }

    }

    /**
     * This method print the map based what each cell represents and the explored/unexplored state
     * on the GUI for user to see.
     */
    public void printFinal() {

        String msg = "Way Point -  Row: " + initialMap.getWayPoint().getRowPos() + " , Col: " + initialMap.getWayPoint().getColPos() + "\n";

        msg += "----------------Map---------------------\n";

        for (int i = Constants.MAX_ROW - 1; i >= 0; i--) {

            for (int y = 0; y < Constants.MAX_COL; y++) {
                Cell cellObj = initialMap.getMapGrid()[i][y];
                if (cellObj.getExploredState()) {
                    if (cellObj.isObstacle()) {
                        msg += "W ";
                    } else {
                        msg += "0 ";
                    }
                } else {
                    msg += "X ";
                }
            }
            msg += "\n";
        }

        msg += "X - Unexplored, 0 - ExploredPath, W - Wall/Obstacles \n\n ";
        msg += "----------------ExploreState----------------------\n";
        for (int i = Constants.MAX_ROW - 1; i >= 0; i--) {

            for (int y = 0; y < Constants.MAX_COL; y++) {
                Cell cellObj = initialMap.getMapGrid()[i][y];
                if (cellObj.getExploredState()) {
                    msg += "O ";
                } else {
                    msg += "X ";
                }
            }

            msg += "\n";
        }
        msg += "X - Unexplored, O - Explored";
        displayMsgToUI(msg);
    }

    /**
     * This method return the color of what each celltype represents
     *
     * @param cellType char representation
     * @return cellColor the assigned color based on the input
     */
    private Color getMapColorForCell(char cellType) {

        Color cellColor = null;
        switch (cellType) {
            case 'U':
                cellColor = Color.white;
                break;        //Unexplored color
            case 'R':
                cellColor = Color.cyan;
                break;        //Robot color
            case 'S':
                cellColor = Color.orange;
                break;        //startZone color
            case 'E':
                cellColor = Color.yellow;
                break;        //endZone color
            case 'P':
                cellColor = Color.green;
                break;        //Explored path color
            case 'W':
                cellColor = Color.red;
                break;        //Wall,Obstacles color
            case 'H':
                cellColor = Color.pink;
                break;        //Robot head color
            case 'B':
                cellColor = Color.gray;
                break;        //Waypoint color
            default:
                cellColor = Color.black;                //Error color
        }
        return cellColor;
    }

    private class MapPanel extends JPanel {
        private JLabel[][] cellLabels;
        private boolean isClickable; // Allows mouse click on map when true

        /**
         * Creates a MapPanel
         *
         * @param isClickable Allow for mouse click when true.
         */
        public MapPanel(boolean isClickable) {
            super(new GridLayout(Constants.MAX_ROW + 1, Constants.MAX_COL + 1));
            this.isClickable = isClickable;
            this.setPreferredSize(new Dimension(576, 756));
            populateMapPanel();
            if (isClickable)
                setupClick();
        }

        public JLabel[][] getJLabelMap() {
            return this.cellLabels;
        }

        private void populateMapPanel() {
            cellLabels = new JLabel[Constants.MAX_ROW + 1][Constants.MAX_COL + 1];

            for (int row = Constants.MAX_ROW; row >= 0; row--) {
                for (int col = 0; col < Constants.MAX_COL + 1; col++) {
                    int xCoord = col - 1;
                    int yCoord = row - 1;
                    cellLabels[row][col] = new JLabel("", SwingConstants.CENTER);

                    if (row == 0 && col == 0)
                        // Set label of bottom left most cell to be empty
                        cellLabels[row][col].setText("");
                    else if (row == 0)
                        // Labels for x axis
                        cellLabels[row][col].setText(Integer.toString(xCoord));
                    else if (col == 0)
                        // Labels for y axis
                        cellLabels[row][col].setText(Integer.toString(yCoord));

                    // Border for the map's cell
                    if (row != 0 && col != 0)
                        cellLabels[row][col].setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    cellLabels[row][col].setOpaque(true);
                    this.add(cellLabels[row][col]);
                }
            }
        }

        private void setupClick() {
            for (int row = Constants.MAX_ROW; row >= 0; row--) {
                for (int col = 0; col < Constants.MAX_COL + 1; col++) {
                    // Adding mouse listener to the labels to handle mouse click event
                    cellLabels[row][col].addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                            Object source = e.getSource();
                            for (int
                            		row = Constants.MAX_ROW; row >= 0; row--) {
                                for (int col = 0; col < Constants.MAX_COL + 1; col++) {
                                    if (cellLabels[row][col] == source) {
                                        cellLabels[row][col].setText("X");

                                    }
                                }
                            }
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {

                        }
                    });
                }
            }
        }
    }

}
