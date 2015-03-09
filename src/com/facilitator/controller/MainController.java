package com.facilitator.controller;

import com.facilitator.model.Project;
import com.facilitator.view.MainView;

public class MainController {
    private MainView mainView;
    private ComponentsController componentsController;
    private MatchController matchController;
    private HarmonisationController harmonisationController;
    private Project project;
    
    public MainController(MainView mainView) {
    	this.mainView = mainView;
    	project = new Project();
    	
    	componentsController = new ComponentsController(this);
    	matchController = new MatchController(this);
    	harmonisationController = new HarmonisationController(this);
    }
    
    public Project getProject() {
    	return project;
    }
    
    public void updatePanels() {
    	componentsController.getComponentsPanel().update();
    	matchController.getMatchPanel().update();
    	harmonisationController.getHarmonisationPanel().update();
    }
    
    public ComponentsController getComponentsController() {
    	return componentsController;
    }
    
    public MatchController getMatchController() {
    	return matchController;
    }
    
    public HarmonisationController getHarmonisationController() {
    	return harmonisationController;
    }
}
