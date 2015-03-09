package com.facilitator.model;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Class representing Java or Ontology classes within the system
 *
 * @author Samuel Cauvin
 */
public class ClassRepresentation {
	public String unmodifiedClassname;
	public String classname; //The name of the class
	public String uniqueClassname; //The classname (with folder structure) in a guaranteed unique format
	public LinkedHashSet<String> parents; //The superclass of the class, if any
	public String source; //The source of the class, if any
	public LinkedHashMap<String,LinkedHashSet<String>> fields; //List of fields that map field names and field types
	public LinkedHashMap<String,LinkedHashSet<String>> inferredFields; //List of inferred fields that map field names and field types
	
	public ClassRepresentation() {
		unmodifiedClassname = "";
		classname = "";
		uniqueClassname = "";
		parents = new LinkedHashSet<String>();
		source = "";
		fields = new LinkedHashMap<String,LinkedHashSet<String>>();
		inferredFields = new LinkedHashMap<String,LinkedHashSet<String>>();
	}
}
