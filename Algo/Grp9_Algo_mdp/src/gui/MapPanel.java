package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.PopupMenu;
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
import javax.swing.event.PopupMenuEvent;

import constant.Constants;

import javax.swing.JSplitPane;

public class MapPanel extends JPanel {

	private JLabel[][] cellLabels;
	private boolean isClickable; // Allows mouse click on map when true
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
	 * Creates a MapPanel
	 *
	 * @param isClickable Allow for mouse click when true.
	 */
	public MapPanel(boolean isClickable) {
		super(new GridLayout(Constants.MAX_ROW + 1, Constants.MAX_COL + 1));
		this.isClickable = isClickable;
		this.setPreferredSize(new Dimension(600, 650));
		populateMapPanel();
		if (isClickable)
			setupClick();
		//popup menu items
		popupmenu.add(normal);
		popupmenu.add(obstacle);
		popupmenu.add(obstacleTop);
		popupmenu.add(obstacleBottom);
		popupmenu.add(obstacleLeft);
		popupmenu.add(obstacleRight);

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

		normal.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		obstacle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getJLabelMap()[cellRow][cellCol].setBackground(main.getMapColorForCell('U'));
				cellLabels[cellRow][cellCol].setText("");

			}
		});
		obstacleTop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getJLabelMap()[cellRow][cellCol].setBackground(main.getMapColorForCell('T'));
				cellLabels[cellRow][cellCol].setText("T");

			}
		});
		obstacleBottom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getJLabelMap()[cellRow][cellCol].setBackground(main.getMapColorForCell('W'));
				cellLabels[cellRow][cellCol].setText("Top");

			}
		});
		obstacleLeft.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getJLabelMap()[cellRow][cellCol].setBackground(main.getMapColorForCell('W'));
				cellLabels[cellRow][cellCol].setText("Top");

			}
		});
		obstacleRight.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getJLabelMap()[cellRow][cellCol].setBackground(main.getMapColorForCell('W'));
				cellLabels[cellRow][cellCol].setText("Top");

			}
		});
	}
	
	
	
	
}
