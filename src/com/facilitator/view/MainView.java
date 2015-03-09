package com.facilitator.view;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;

import com.facilitator.controller.MainController;

/**
 * Main UI class of the system, controls the main UI
 *
 * @author Samuel Cauvin
 */
public class MainView extends FrameView { 
	private MainController mainController;
	private JTabbedPane tabPanel;
    private JPanel mainPanel;
    private JMenuBar menuBar;    
	
	public MainView(Application app) {
		super(app);        
		this.getFrame().setTitle("Facilitator");
		
		mainController = new MainController(this);
        initComponents();
    }
    
    private void initComponents() {
        mainPanel = new JPanel();
        mainPanel.setName("mainPanel");
        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.PAGE_AXIS));
        setComponent(mainPanel);

        menuBar = new JMenuBar();
        menuBar.setName("menuBar");
        setMenuBar(menuBar);
        
        tabPanel = new JTabbedPane();
        tabPanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabPanel.addTab("Components", mainController.getComponentsController().getComponentsPanel());
        tabPanel.addTab("Matching", mainController.getMatchController().getMatchPanel());
        tabPanel.addTab("Harmonisation", mainController.getHarmonisationController().getHarmonisationPanel());
        mainPanel.add(tabPanel);
    }
}