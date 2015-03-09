package com.facilitator.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.InternationalFormatter;

import com.facilitator.utils.SpringUtilities;

/**
 * Matcher Settings dialog to get threshold parameter from user
 * 
 * @author Samuel Cauvin
 */
public class MatcherSettingsDialog extends JDialog {
	private static final long serialVersionUID = -5264341637411900950L;
	private MatchPanel matchPanel;
    private JFormattedTextField fieldThresholdTextField;
    private JButton submitSettingsButton;
    
    public MatcherSettingsDialog(java.awt.Frame parent, boolean modal, MatchPanel mp) {
        super(parent, modal);
        matchPanel = mp;
        
        initComponents();
    }
    
    private void initComponents() {
        this.setTitle(("Matcher Settings"));
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));
        pack();
        JPanel mainPanel = new JPanel(new SpringLayout());
	        JPanel settingsPanel = new JPanel(new SpringLayout());
	        InternationalFormatter valueFormat = new InternationalFormatter();
        	valueFormat.setMaximum(100);
        	valueFormat.setMinimum(0);
	        fieldThresholdTextField = new JFormattedTextField(valueFormat);
	        //fieldThresholdTextField.setMinimumSize(new Dimension(200,50));
	        fieldThresholdTextField.setValue(matchPanel.getFieldNumber());
	        fieldThresholdTextField.getDocument().addDocumentListener(new DocumentListener() {
	            @Override public void changedUpdate(DocumentEvent evt) {
	            }
	
	            @Override public void removeUpdate(DocumentEvent evt) {
	            }
	
	            @Override public void insertUpdate(DocumentEvent evt) {
	            }
	        });
	        settingsPanel.add(new JLabel("Field Threshold: "));
	        	JPanel thresholdPanel = new JPanel(new SpringLayout());
	        	thresholdPanel.add(fieldThresholdTextField);
	        	thresholdPanel.add(new JLabel("%"));
		   		SpringUtilities.makeCompactGrid(thresholdPanel,1,2,0,0,0,0);
	        settingsPanel.add(thresholdPanel);
	   		SpringUtilities.makeCompactGrid(settingsPanel,1,2,0,0,0,0);
   		  
        submitSettingsButton = new JButton("Submit Settings");
        submitSettingsButton.addActionListener(new ActionListener() {
	        @Override public void actionPerformed(ActionEvent actionEvent) {
	        	if (fieldThresholdTextField.getText().trim().equals("")) {
	        		JOptionPane.showMessageDialog(null, "One of the required values wasn't entered", "Error", JOptionPane.ERROR_MESSAGE);
	        	} else {
	        		try {
			        	int fieldThreshold = Integer.parseInt(fieldThresholdTextField.getText().trim());
		        		matchPanel.setFieldNumber(fieldThreshold);
			        	dispose();
	        		} catch (Exception e) {
		        		JOptionPane.showMessageDialog(null, "Invalid Value Entered", "Error", JOptionPane.ERROR_MESSAGE);
	        		}
	        	}
	        }
	    });
   		mainPanel.add(settingsPanel);      
   		mainPanel.add(submitSettingsButton);
   		SpringUtilities.makeCompactGrid(mainPanel,2,1,10,10,10,10);
        
        this.add(mainPanel);
    }
}
