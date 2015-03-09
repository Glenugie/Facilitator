package com.facilitator.controller;

import com.facilitator.model.Project;
import com.facilitator.view.HarmonisationPanel;

public class HarmonisationController {
	private MainController mainController;
	private Project project;
	private HarmonisationPanel harmonisationPanel;
	
	public HarmonisationController(MainController mc) {
		mainController = mc;
		project = mc.getProject();
    	
		harmonisationPanel = new HarmonisationPanel(this);		
	}
	
	public HarmonisationPanel getHarmonisationPanel() {
		return harmonisationPanel;
	}
	
	public boolean matchingRun() {
		return !mainController.getMatchController().getMatches().isEmpty();
	}
	
	public void runMatching() {
    	mainController.getMatchController().getMatchPanel().runMatching();
	}
    
    public Project getProject() {
    	return project;
    }
}
