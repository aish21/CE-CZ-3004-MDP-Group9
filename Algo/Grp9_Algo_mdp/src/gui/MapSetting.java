package gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BoxLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JScrollBar;
import javax.swing.JTextPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import constant.Constants.MOVEMENT;
import entity.Robot;
import entity.Map;

public class MapSetting extends JPanel {


	private static final long serialVersionUID = 1L;
	
	JTextArea editTextArea;
	JScrollPane scroll;
	JButton hamiltonianBtn;
	JButton shortestBtn;
	JButton connectBtn;
	JButton resetBtn;
	JButton clearBtn;

	JButton upBtn;
	JButton downBtn;
	JButton leftBtn;
	JButton rightBtn;
	
	private float playSpeed;

	/**
	 * Create the panel.
	 */
	public MapSetting(main m,Map map,Robot rBot) {


        setLayout(null);
        setPreferredSize(new Dimension(450, 650));
        
        hamiltonianBtn = new JButton();
        hamiltonianBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		m.simHamitonian = new Thread(new simulateHamiltonian(m, rBot, map));
        		m.simHamitonian.start();

        		hamiltonianBtn.setEnabled(false);
        		shortestBtn.setEnabled(false);
        		connectBtn.setEnabled(false);
        		upBtn.setEnabled(false);
        		downBtn.setEnabled(false);
        		leftBtn.setEnabled(false);
        		rightBtn.setEnabled(false);
        	}
        });
        hamiltonianBtn.setBounds(10, 521, 135, 30);
        hamiltonianBtn.setText("Hamiliton Path");
        add(hamiltonianBtn);
        
        shortestBtn = new JButton();
        shortestBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		
        		m.simShortestPath = new Thread(new simulateShortestPath(m, rBot, map));
        		m.simShortestPath.start();
        		
        		hamiltonianBtn.setEnabled(false);
        		shortestBtn.setEnabled(false);
        		connectBtn.setEnabled(false);
        		upBtn.setEnabled(false);
        		downBtn.setEnabled(false);
        		leftBtn.setEnabled(false);
        		rightBtn.setEnabled(false);
        	}
        });
        shortestBtn.setBounds(10, 563, 135, 30);
        shortestBtn.setText("Shortest Path");
        add(shortestBtn);
        
        connectBtn = new JButton();
        connectBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		editTextArea.setText("Connecting to Rpi...\n");
        		editTextArea.setCaretPosition(editTextArea.getText().length());
        		
        		m.simRealRun = new Thread(new Realrun(m, rBot, map));
        		m.simRealRun.start();
        		
        		hamiltonianBtn.setEnabled(false);
        		shortestBtn.setEnabled(false);
        		connectBtn.setEnabled(false);
        		upBtn.setEnabled(false);
        		downBtn.setEnabled(false);
        		leftBtn.setEnabled(false);
        		rightBtn.setEnabled(false);
        	}
        });
        connectBtn.setBounds(10, 604, 135, 30);
        connectBtn.setText("Connect to Rpi");
        add(connectBtn);
        
        resetBtn = new JButton();
        resetBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		editTextArea.append("Arena Resetting!\n");
        		editTextArea.setCaretPosition(editTextArea.getText().length());
        		hamiltonianBtn.setEnabled(true);
        		shortestBtn.setEnabled(true);
        		connectBtn.setEnabled(true);
        		m.resetArena();
        		editTextArea.append("Arena Resetted!\n");
        		editTextArea.setCaretPosition(editTextArea.getText().length());
        		
        		upBtn.setEnabled(true);
        		downBtn.setEnabled(true);
        		leftBtn.setEnabled(true);
        		rightBtn.setEnabled(true);
        	}
        });
        resetBtn.setText("Reset Arena ");
        resetBtn.setBounds(155, 563, 110, 30);
        add(resetBtn);
        
        clearBtn = new JButton();
        clearBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		editTextArea.setText("Arena Cleared!\n");
        		editTextArea.append("Obstacle List Cleared!\n");
        		editTextArea.setCaretPosition(editTextArea.getText().length());
        		
        		hamiltonianBtn.setEnabled(true);
        		shortestBtn.setEnabled(true);
        		connectBtn.setEnabled(true);
        		m.clearArena();
        		
        		upBtn.setEnabled(true);
        		downBtn.setEnabled(true);
        		leftBtn.setEnabled(true);
        		rightBtn.setEnabled(true);
        		
        	}
        });
        clearBtn.setText("Clear Arena ");
        clearBtn.setBounds(155, 521, 110, 30);
        add(clearBtn);
        
        
        upBtn = new JButton();
        upBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(rBot.isMovementValid(map, MOVEMENT.FORWARD)) {
        			rBot.move(MOVEMENT.FORWARD);
            		m.paintResult();
        		}
        	}
        });
        upBtn.setBounds(308, 521, 103, 30);
        upBtn.setText("Forward");
        add(upBtn);
        
        downBtn = new JButton();
        downBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if (rBot.isMovementValid(map, MOVEMENT.BACKWARD)) {
        		rBot.move(MOVEMENT.BACKWARD);
        		m.paintResult();
        		}
        	}
        });
        downBtn.setBounds(308, 604, 103, 30);
        downBtn.setText("Reverse");
        add(downBtn);
        
        leftBtn = new JButton();
        leftBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(rBot.isMovementValid(map, MOVEMENT.LEFT)) {
        			rBot.turn(MOVEMENT.LEFT);
            		m.paintResult();
        		}
        	}
        });
        leftBtn.setBounds(293, 563, 71, 30);
        leftBtn.setText("Left");
        add(leftBtn);
        
        rightBtn = new JButton();
        rightBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(rBot.isMovementValid(map, MOVEMENT.RIGHT)) {
        			rBot.turn(MOVEMENT.RIGHT);
            		m.paintResult();
        		}
        	}
        });
        rightBtn.setBounds(369, 563, 71, 30);
        rightBtn.setText("Right");
        add(rightBtn);
        
        
        scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        editTextArea = new JTextArea();
        editTextArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        editTextArea.setLineWrap(true);
        editTextArea.setEditable(false);
        scroll.setViewportView(editTextArea);
        scroll.setBounds(0, 10, 450, 500);
        add(scroll);
        editTextArea.setText("Instructions: Right-click on the grid to add\nobstacles.\n");
        
        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);
        separator.setBounds(275, 521, 8, 113);
        add(separator);   
        
	}
	
	
}
