package com.facilitator.model;

import java.util.ArrayList;

/**
 * Class representing a match within the system
 *
 * @author Samuel Cauvin
 */
public class Match {
	public String jComponent; //The name of the java component which matches a given ontology component
	public String oComponent; //The name of the ontology component which the given java component matches
	public String oSource; //The name of the ontology the match has come from
	public boolean inf; //Whether the jComponent is an inferred field
	public ArrayList<String> reasons; //The description of why the match was made

	/**
	 * Constructs a match with a single reason
	 * @param c1 Java component of the match
	 * @param c2 Ontology component of the match
	 * @param i Whether the Java component is an inferred field
	 * @param r Reason for match
	 */
	public Match(String c1, String c2, String s, boolean i, String r) {
		jComponent = c1;
		oComponent = c2;
		oSource = s;
		inf = i;
		reasons = new ArrayList<String>();
		reasons.add(r);
	}
	
	/**
	 * Constructs a match with a list of reasons
	 * @param c1 Java component of the match
	 * @param c2 Ontology component of the match
	 * @param i Whether the Java component is an inferred field
	 * @param r L:st of reasons
	 */
	public Match(String c1, String c2, String s, boolean i, ArrayList<String> r) {
		jComponent = c1;
		oComponent = c2;
		oSource = s;
		inf = i;
		reasons = new ArrayList<String>();
		reasons.addAll(r);
	}
}
