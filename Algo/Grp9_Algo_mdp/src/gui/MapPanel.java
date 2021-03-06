package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

import constant.Constants;
import entity.Map;
/**
 * @author Goh Cheng Guan, Clive
 * @author Lau Zhen Jie
 * @version 1.0
 * @since 2021-9-15
 */
public class MapPanel extends JPanel {

	private JLabel[][] cellLabels;
	private int cellRow = 0;
	private int cellCol = 0;

	// Popup Menu
	final JPopupMenu popupmenu = new JPopupMenu("Options");
	JMenuItem normal = new JMenuItem("Normal");
	JMenuItem obstacle = new JMenuItem("Obstacle");
	JMenuItem obstacleBottom = new JMenuItem("Obstacle Bottom");
	JMenuItem obstacleTop = new JMenuItem("Obstacle Top");
	JMenuItem obstacleLeft = new JMenuItem("Obstacle Left");
	JMenuItem obstacleRight = new JMenuItem("Obstacle Right");

	/**
	 * Creates a MapPanel with popup menu and their respective action listener
	 *
	 * @param main m
	 * @param Map map
	 * @param isClickable Allow for mouse click when true.
	 * 
	 */
	public MapPanel(main m,boolean isClickable, Map map) {
		super(new GridLayout(Constants.MAX_ROW + 1, Constants.MAX_COL + 1));
		this.setPreferredSize(new Dimension(600, 650));
		populateMapPanel();
		if (isClickable)
			setupClick();
		// popup menu items
		popupmenu.add(normal);
		popupmenu.add(obstacle);
		popupmenu.add(obstacleTop);
		popupmenu.add(obstacleBottom);
		popupmenu.add(obstacleLeft);
		popupmenu.add(obstacleRight);
		

		normal.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(cellLabels[cellRow][cellCol].getBackground()!=main.getMapColorForCell('U')) {
					cellLabels[cellRow][cellCol].setBackground(main.getMapColorForCell('U'));
					cellLabels[cellRow][cellCol].setText("");
					m.removeObstacle(cellRow, cellCol);
					
						
					
				}

			}
		});
		obstacle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cellLabels[cellRow][cellCol].setBackground(main.getMapColorForCell('O'));
				cellLabels[cellRow][cellCol].setText("O");
				
				//add obstacle and add to obstacle list
				m.addObstacle(cellRow, cellCol, 1);
				

			}
		});
		obstacleTop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cellLabels[cellRow][cellCol].setBackground(main.getMapColorForCell('O'));
				cellLabels[cellRow][cellCol].setText(setSymbol(1));
				m.addObstacle(cellRow, cellCol, 1);
				
			}
		});
		obstacleBottom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cellLabels[cellRow][cellCol].setBackground(main.getMapColorForCell('O'));
				cellLabels[cellRow][cellCol].setText(setSymbol(2));
				m.addObstacle(cellRow, cellCol, 2);

			}
		});
		obstacleLeft.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cellLabels[cellRow][cellCol].setBackground(main.getMapColorForCell('O'));
				cellLabels[cellRow][cellCol].setText(setSymbol(4));
				m.addObstacle(cellRow, cellCol, 4);

			}
		});
		obstacleRight.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cellLabels[cellRow][cellCol].setBackground(main.getMapColorForCell('O'));
				cellLabels[cellRow][cellCol].setText(setSymbol(3));
				m.addObstacle(cellRow, cellCol, 3);
			

			}
		});
	}


	
	/**
	 * This method return the Map Labels
	 * @return JLabel[][]
	 */
	public JLabel[][] getJLabelMap() {
		return this.cellLabels;
	}

	
	/**
	 * This method creates 20x20 map grid of the arena
	 */
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

	/**
	 * This method allows mouse events on the map grid. Allow user to right-click to place obstacles.
	 */
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
						for (int row = Constants.MAX_ROW; row >= 0; row--) {
							for (int col = 0; col < Constants.MAX_COL + 1; col++) {
								if (cellLabels[row][col] == source & e.getButton() == 3) {
									popupmenu.show(e.getComponent(), e.getX(), e.getY());
									cellRow = row;
									cellCol = col;

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
	
	/**
	 * This method changes numerical obstacle direction to their respective arrow symbol in the map grid
	 * @return string value of arrow direction
	 */
	public String setSymbol(int ObsDir) {
		String s = "";
		switch (ObsDir) {
		case 1:
			s = "\u2191";
			break;
		case 2:
			s = "\u2193";
			break;
		case 3:
			s = "\u2192";
			break;
		case 4:
			s = "\u2190";
			break;
		default:
			break;
		}
		return s;
	}

}
