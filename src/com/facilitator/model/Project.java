package com.facilitator.model;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Project {
	private String javaPath;
	private String ontoPath;
	private LinkedHashMap<String,String> jSources;
	private ArrayList<ClassRepresentation> jClasses;
	private LinkedHashMap<String,ArrayList<ClassRepresentation>> oClasses;
	
	public Project() {
		Path currentRelativePath = Paths.get("");		
    	javaPath = currentRelativePath.toAbsolutePath().toString();
    	ontoPath = currentRelativePath.toAbsolutePath().toString();
    	jSources = new LinkedHashMap<String,String>();
    	jClasses = new ArrayList<ClassRepresentation>();
    	oClasses = new LinkedHashMap<String,ArrayList<ClassRepresentation>>();
	}
    
    public LinkedHashMap<String,String> getJSources() {
    	return jSources;
    }
    
    public ArrayList<ClassRepresentation> getJClasses() {
    	return jClasses;
    }
    
    public LinkedHashMap<String,ArrayList<ClassRepresentation>> getOClasses() {
    	
    	return oClasses;
    }
    
    public String getJavaPath() {
    	return javaPath;
    }
    
    public void setJavaPath(String path) {
    	javaPath = path;
    }
    
    public String getJavaName() {
    	return new File(javaPath).getName();
    }
    
    public String getOntoPath() {
    	return ontoPath;
    }
    
    public void setOntoPath(String path) {
    	ontoPath = path;
    }
    
    public String getOntoName() {
    	String ontoName = new File(ontoPath).getName();
    	if (ontoName.contains(".")) { ontoName = ontoName.substring(0,new File(ontoPath).getName().lastIndexOf("."));}
		return ontoName;
    }
}
