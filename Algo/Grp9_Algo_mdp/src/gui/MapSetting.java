package gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;

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

public class MapSetting extends JPanel {

	JTextArea editTextArea;
	JScrollPane scroll;
	JButton hamiltonianBtn;
	JButton shortestBtn;
	JButton connectBtn;
	JButton ResetBtn;

	JButton upBtn;
	JButton downBtn;
	JButton leftBtn;
	JButton rightBtn;
	
	private float playSpeed;

	/**
	 * Create the panel.
	 */
	public MapSetting(main m,MapPanel mPanel,Robot rBot) {


        setLayout(null);
        setPreferredSize(new Dimension(450, 650));
        
        hamiltonianBtn = new JButton();
        hamiltonianBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });
        hamiltonianBtn.setBounds(10, 521, 135, 30);
        hamiltonianBtn.setText("Hamiliton Path");
        add(hamiltonianBtn);
        
        shortestBtn = new JButton();
        shortestBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	}
        });
        shortestBtn.setBounds(10, 563, 135, 30);
        shortestBtn.setText("Shortest Path");
        add(shortestBtn);
        
        connectBtn = new JButton();
        connectBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		editTextArea.setText("Connecting to Rpi...");
        	}
        });
        connectBtn.setBounds(10, 604, 135, 30);
        connectBtn.setText("Connect to Rpi");
        add(connectBtn);
        
        ResetBtn = new JButton();
        ResetBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		
        	}
        });
        ResetBtn.setText("Reset Arena ");
        ResetBtn.setBounds(155, 521, 110, 30);
        add(ResetBtn);
        
        upBtn = new JButton();
        upBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		//TODO check if movement valid
        		rBot.move(MOVEMENT.FORWARD);
        		m.paintResult();
        	}
        });
        upBtn.setBounds(308, 521, 103, 30);
        upBtn.setText("Forward");
        add(upBtn);
        
        downBtn = new JButton();
        downBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		rBot.move(MOVEMENT.BACKWARD);
        		m.paintResult();
        	}
        });
        downBtn.setBounds(308, 604, 103, 30);
        downBtn.setText("Reverse");
        add(downBtn);
        
        leftBtn = new JButton();
        leftBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		rBot.turn(MOVEMENT.LEFT);
        		m.paintResult();
        		
        	}
        });
        leftBtn.setBounds(293, 563, 71, 30);
        leftBtn.setText("Left");
        add(leftBtn);
        
        rightBtn = new JButton();
        rightBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		rBot.turn(MOVEMENT.RIGHT);
        		m.paintResult();
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
        editTextArea.setText("Instructions: Click on the grid to add\nobstacles.");
        
        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);
        separator.setBounds(275, 521, 8, 113);
        add(separator);   
        
	}
	
}