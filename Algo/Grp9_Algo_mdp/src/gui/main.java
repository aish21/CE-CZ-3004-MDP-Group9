package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import constant.Constants;
import constant.Constants.DIRECTION;
import entity.Cell;
import entity.Map;
import entity.Robot;

public class main extends JFrame {

	private MapPanel MapPanel;
	private JPanel MapSetting;
	private MapPanel simulationMap;
	private JPanel designMap;

	private JLabel[][] resultMap; // Use to display simulation

	Thread simHamitonian, simFastest, simRealRun;
	Map initialMap;
	Robot rBot;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					main frame = new main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public main() {
		setMapRobotObj();
		initLayout();
		paintResult();

	}

	private void setMapRobotObj() {
		initialMap = new Map();
		rBot = new Robot(1, 1, DIRECTION.NORTH);
	}

	private void initLayout() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 650);
		setLayout(new FlowLayout());
		setTitle("CZ3004 MDP Group 9");
		
		MapPanel = new MapPanel(true);
		MapSetting = new MapSetting(this,MapPanel,rBot);
		resultMap = MapPanel.getJLabelMap();
		add(MapPanel);
		add(MapSetting);
		pack();
		setVisible(true);
	}

	/**
	 * This method updates the GUI on how the current map looks like with the robot
	 * on it. It does not modifies the map object with robot object
	 */
	public void paintResult() {

		for (int i = 0; i < Constants.MAX_ROW; i++) {
			for (int y = 0; y < Constants.MAX_COL; y++) {
				Cell cellObj = initialMap.getMapGrid()[i][y];
				
				//resultMap[i + 1][y + 1].setBackground(getMapColorForCell('U'));
			}
		}
		Cell startZone = initialMap.getStartGoalPosition();


		// Paint the Start and End Zone (3x3 grid)
		for (int i : Constants.WITHIN_3BY3) {
			for (int y : Constants.WITHIN_3BY3) {

				resultMap[startZone.getRowPos() + i + 1][startZone.getColPos() + y + 1]
						.setBackground(getMapColorForCell('S'));
			}
		}
		// Paint the robot (3x3 grid)
		for (int i : Constants.WITHIN_3BY3) {
			for (int y : Constants.WITHIN_3BY3) {
				resultMap[rBot.getPosRow() + i + 1][rBot.getPosCol() + y + 1].setBackground(getMapColorForCell('R'));

			}
		}

		// Paint the front of the robot
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
	 * This method return the color of what each celltype represents
	 *
	 * @param cellType char representation
	 * @return cellColor the assigned color based on the input
	 */
	public static Color getMapColorForCell(char cellType) {

		Color cellColor = null;
		switch (cellType) {
		case 'U':
			cellColor = Color.white;
			break; // Normal color
		case 'R':
			cellColor = Color.cyan;
			break; // Robot color
		case 'S':
			cellColor = Color.orange;
			break; // startZone color
		case 'E':
			cellColor = Color.yellow;
			break; // endZone color
		case 'P':
			cellColor = Color.green;
			break; // Explored path color
		case 'W':
			cellColor = Color.red;
			break; // Wall,Obstacles color
		case 'H':
			cellColor = Color.pink;
			break; // Robot head color
		case 'B':
			cellColor = Color.gray;
			break; // Waypoint color
		case 'O':
			cellColor = Color.decode("#F8C471");
			break; // top obstacle color
		default:
			cellColor = Color.black; // Error color
		}
		return cellColor;
	}
	


}
