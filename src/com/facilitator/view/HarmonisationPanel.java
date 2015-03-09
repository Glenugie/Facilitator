package com.facilitator.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import com.facilitator.controller.HarmonisationController;
import com.facilitator.utils.SpringUtilities;

public class HarmonisationPanel extends JPanel { 
	private static final long serialVersionUID = 9089578910656230988L;
	private HarmonisationController harmonisationController;
	
	private JButton initButton;
	
	public HarmonisationPanel(HarmonisationController hc) {	
		harmonisationController = hc;
		
        initComponents();
        update();
    }
	
    private void initComponents() {     
    	setLayout(new BorderLayout());  
        JPanel contentPanel = new JPanel(new SpringLayout());
	        initButton = new JButton("Initialise");
	        initButton.addActionListener(new ActionListener() {
    	        @Override public void actionPerformed(ActionEvent actionEvent) {
    	        	harmonisationController.runMatching();
    	        	initButton.setEnabled(false);
    	        }
    	    });

        contentPanel.add(initButton);   
    	SpringUtilities.makeCompactGrid(contentPanel,1,1,10,10,10,10);
        this.add(contentPanel); 	
    }
    
    public void update() {
    	boolean javaPresent = !harmonisationController.getProject().getJClasses().isEmpty();
    	boolean ontoPresent = !harmonisationController.getProject().getOClasses().isEmpty();
    	boolean matchPresent = harmonisationController.matchingRun();
    		
		initButton.setEnabled(!matchPresent && javaPresent && ontoPresent);
    	
    	this.validate();
    }
}
