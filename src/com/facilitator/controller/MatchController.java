package com.facilitator.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import com.facilitator.Cons;
import com.facilitator.model.ClassRepresentation;
import com.facilitator.model.Match;
import com.facilitator.model.Project;
import com.facilitator.utils.Utilities;
import com.facilitator.view.MatchPanel;

/**
 * The MatchController, responsible for performing all matching operations
 *
 * @author Samuel Cauvin
 */
public class MatchController {
	private MainController mainController;
	private Project project;
	private MatchPanel matchPanel;
	
	private HashMap<String, HashSet<String>> typeTaxonomy;	
	private HashMap<String, Integer> taxonomyRankings;
	
	private LinkedHashMap<Match,LinkedHashSet<Match>> classFieldMatches;
    
    public MatchController(MainController mc) {
		mainController = mc;
		project = mc.getProject();
		
		/*
		 * Generic Subsumes String
		 * String Subsumes Char, Boolean, Number, DateTime
		 * DateTime Subsumes Date, Time
		 * Number Subsumes Int, Real
		 * Int Subsumes Byte, Short, Long
		 * Real Subsumes Double, Float
		 */
		typeTaxonomy = new HashMap<String, HashSet<String>>();
		typeTaxonomy.put("generic",new HashSet<String>());
			typeTaxonomy.get("generic").add("string");
		typeTaxonomy.put("string",new HashSet<String>());
			typeTaxonomy.get("string").add("char");
			typeTaxonomy.get("string").add("boolean");
			typeTaxonomy.get("string").add("number");
			typeTaxonomy.get("string").add("datetime");
		typeTaxonomy.put("datetime",new HashSet<String>());
			typeTaxonomy.get("datetime").add("date");
			typeTaxonomy.get("datetime").add("time");
		typeTaxonomy.put("number",new HashSet<String>());
			typeTaxonomy.get("number").add("int");
			typeTaxonomy.get("number").add("real");
		typeTaxonomy.put("int",new HashSet<String>());
			typeTaxonomy.get("int").add("byte");
			typeTaxonomy.get("int").add("short");
			typeTaxonomy.get("int").add("long");
		typeTaxonomy.put("real",new HashSet<String>());
			typeTaxonomy.get("real").add("double");
			typeTaxonomy.get("real").add("float");
			
		taxonomyRankings= new HashMap<String, Integer>();
			taxonomyRankings.put("generic",1);
			taxonomyRankings.put("string",2);
			taxonomyRankings.put("datetime",3);
			taxonomyRankings.put("number",3);
			taxonomyRankings.put("int",4);
			taxonomyRankings.put("real",4);
			
    	classFieldMatches = new LinkedHashMap<Match,LinkedHashSet<Match>>();
    	
    	matchPanel = new MatchPanel(this);		    	
    }
    
    public void findMatches(int fieldNumber) {	
    	classFieldMatches.clear();
    	ArrayList<ClassRepresentation> oClasses = new ArrayList<ClassRepresentation>();	
		for (String oK : project.getOClasses().keySet()) {
			oClasses.addAll(project.getOClasses().get(oK));
		}
    	
		LinkedHashSet<Match> classMatches = new LinkedHashSet<Match>();	
		for (ClassRepresentation jC : project.getJClasses()) {		
			for (ClassRepresentation oC : oClasses) {
				if (oC.classname.equals(jC.classname)) {
					addMatch(classMatches,jC.classname,oC.classname,oC.source,false,"Names Match");
					if (jC.fields.keySet().size() == oC.fields.keySet().size()) {
						addMatch(classMatches,jC.classname,oC.classname,oC.source,false,"Number of Fields Match");
					}
				}
				
				if (jC.fields.keySet().size() > 0 && oC.fields.keySet().size() > 0) {					
					int fieldsMatched = 0;
					int inferredFieldsMatched = 0;
					for (String f : oC.fields.keySet()) {
						if (jC.fields.containsKey(f)) {
							fieldsMatched += 1;
						} else if (jC.inferredFields.containsKey(f)) {
							inferredFieldsMatched += 1;
						}
					}
					
					if (fieldsMatched > 0 && ((fieldsMatched/oC.fields.keySet().size())*100) >= fieldNumber) {
						if (fieldsMatched == oC.fields.keySet().size()) {
							addMatch(classMatches,jC.classname,oC.classname,oC.source,false,"All Fields Match");
						} else {
							addMatch(classMatches,jC.classname,oC.classname,oC.source,false,fieldsMatched+"/"+oC.fields.keySet().size()+" Fields Match");
						}
					}
					if (fieldsMatched > 0 && inferredFieldsMatched > 0 && (((fieldsMatched+inferredFieldsMatched)/oC.fields.keySet().size())*100) >= fieldNumber) {
						if ((fieldsMatched+inferredFieldsMatched) == oC.fields.keySet().size()) {
							addMatch(classMatches,jC.classname,oC.classname,oC.source,false,"All Fields Match when including Inferred Fields");
						} else {
							addMatch(classMatches,jC.classname,oC.classname,oC.source,false,(fieldsMatched+inferredFieldsMatched)+"/"+oC.fields.keySet().size()+" Fields Match when including Inferred Fields");
						}
					}
				}
			}
		}
		
		LinkedHashSet<Match> tempMatches = new LinkedHashSet<Match>();	
		for (Match m : classMatches) {
			String jChild = "";
			for (ClassRepresentation c : project.getJClasses()) {
				if (c.parents.contains(m.jComponent)) {
					jChild = c.classname;
					break;
				}
			}
			
			String oChild = "";
			for (ClassRepresentation c : oClasses) {
				if (c.parents.contains(m.oComponent)) {
					oChild = c.classname;
					break;
				}
			}
			
			if (!jChild.equals("") && !oChild.equals("")) {
				addMatch(tempMatches,jChild,oChild,"",false,"Superclasses Match");
			}
		}
		for (Match m : tempMatches) { addMatch(classMatches,m);}
		
		for (Match cM : classMatches) {
			LinkedHashSet<Match> fieldMatches = new LinkedHashSet<Match>();
			ClassRepresentation jClass = null; for (ClassRepresentation jC : project.getJClasses()) { if (jC.classname.equals(cM.jComponent)) { jClass = jC; break;}}
			ClassRepresentation oClass = null; for (ClassRepresentation oC : oClasses) { if (oC.classname.equals(cM.oComponent)) { oClass = oC; break;}}
			
			for (String jF : jClass.fields.keySet()) {
				for (String oF : oClass.fields.keySet()) {
					if (jF.equals(oF) || Utilities.stem(jF).equals(oF)) {
						if (jF.equals(oF)) { addMatch(fieldMatches,jF,oF,"",false,"Names Match");}
						else { addMatch(fieldMatches,jF,oF,"",false,"Names Match after Stemming ("+Utilities.stem(jF)+" == "+oF+")");}
						String typeMatch = compareTypes(jClass.fields.get(jF),oClass.fields.get(oF));
						if (typeMatch.equals("direct")) {
							addMatch(fieldMatches,jF,oF,"",false,"Types Match Directly");
						} else if (!typeMatch.equals("")) {
							addMatch(fieldMatches,jF,oF,"",false,"Types Match through Parent "+typeMatch+" ("+getMatchScope(jClass.fields.get(jF),oClass.fields.get(oF),typeMatch)+")");
						}
						break;
					}
				}
			}

			HashSet<String> unmatchedJFields = new HashSet<String>(); HashSet<String> unmatchedOFields = new HashSet<String>();
			calcUnmatchedFields(fieldMatches, jClass.classname, oClass.classname, unmatchedJFields, unmatchedOFields);
			for (String jF : jClass.inferredFields.keySet()) {
				for (String oF : unmatchedOFields) {
					if (jF.equals(oF) || Utilities.stem(jF).equals(oF)) {
						if (jF.equals(oF)) { addMatch(fieldMatches,jF,oF,"",true,"Names Match");}
						else { addMatch(fieldMatches,jF,oF,"",true,"Names Match after Stemming");}
						String typeMatch = compareTypes(jClass.inferredFields.get(jF),oClass.fields.get(oF));
						if (typeMatch.equals("direct")) {
							addMatch(fieldMatches,jF,oF,"",true,"Types Match Directly");
						} else if (!typeMatch.equals("")) {
							addMatch(fieldMatches,jF,oF,"",true,"Types Match through Parent "+typeMatch+" ("+getMatchScope(jClass.inferredFields.get(jF),oClass.inferredFields.get(oF),typeMatch)+")");
						}
						break;
					}
				}
			}

			unmatchedJFields = new HashSet<String>(); unmatchedOFields = new HashSet<String>();
			calcUnmatchedFields(fieldMatches, jClass.classname, oClass.classname, unmatchedJFields, unmatchedOFields);
			for (String oF : unmatchedOFields) {
				for (String jF : unmatchedJFields) {
					if (jClass.fields.containsKey(jF) && oClass.fields.containsKey(oF)) {
						String typeMatch = compareTypes(jClass.fields.get(jF),oClass.fields.get(oF));
						if (typeMatch.equals("direct")) {
							addMatch(fieldMatches,jF,oF,"",false,"No Match on Name but Types Match Directly");
						} else if (!typeMatch.equals("")) {
							addMatch(fieldMatches,jF,oF,"",false,"No Match on Name but Types Match through Parent "+typeMatch+" ("+getMatchScope(jClass.fields.get(jF),oClass.fields.get(oF),typeMatch)+")");
						}
					}
				}
			}		
						
			if (Cons.printResults) { 
				String cReasons = ""; for (String r : cM.reasons) { cReasons += r+", ";} if (cReasons.length() > 0) { cReasons = cReasons.substring(0,cReasons.length()-2);} 
				System.out.println("Class \""+cM.jComponent+"\" (Java) might match Class \""+cM.oComponent+"\" (Ontology) because: "+cReasons);
				
				boolean printedSuperclass = false;
				if (jClass.parents.size() == 0 && oClass.parents.size() > 0) { 
					System.out.println("\tInheritance Problem: Class \""+oClass.classname+"\" (Ontology) has a superclass \""+oClass.parents+"\" but Class \""+jClass.classname+"\" (Java) has none");
					printedSuperclass = true;
				} else if (jClass.parents.size() > 0 && oClass.parents.size() == 0) { 
					System.out.println("\tInheritance Problem: Class \""+jClass.classname+"\" (Java) has a superclass \""+jClass.parents+"\" but Class \""+oClass.classname+"\" (Ontology) has none");
					printedSuperclass = true;
				} else if (jClass.parents.size() > 0 && oClass.parents.size() > 0 && !oClass.parents.contains((String) jClass.parents.toArray()[0])) { 
					System.out.println("\tInheritance Problem: Class \""+jClass.classname+"\" (Java) has a superclass \""+jClass.parents+"\" but Class \""+oClass.classname+"\" (Ontology) has a superclass \""+oClass.parents+"\"");
					printedSuperclass = true;
				}
				if (printedSuperclass) { System.out.println("");}
	
				for (Match fM : fieldMatches) {
					String fReasons = ""; for (String r : fM.reasons) { fReasons += r+", ";} if (fReasons.length() > 0) { fReasons = fReasons.substring(0,fReasons.length()-2);}
					if (fM.inf){
						System.out.println("\tInferred Field \""+fM.jComponent+"\" (Java) might match Field \""+fM.oComponent+"\" (Ontology) because: "+fReasons);
					} else {
						System.out.println("\tField \""+fM.jComponent+"\" (Java) might match Field \""+fM.oComponent+"\" (Ontology) because: "+fReasons);
					}
				}
				if (fieldMatches.size() == 0) { System.out.println("\tNo Matching Fields");}
				System.out.println("");
			}

			unmatchedJFields = new HashSet<String>(); unmatchedOFields = new HashSet<String>();
			calcUnmatchedFields(fieldMatches, jClass.classname, oClass.classname, unmatchedJFields, unmatchedOFields);
			
			classFieldMatches.put(cM,fieldMatches);
		}
		if (classMatches.size() == 0) { if (Cons.printResults) { System.out.println("No Matching Classes");}}

		HashSet<String> unmatchedJClasses = new HashSet<String>(); HashSet<String> unmatchedOClasses = new HashSet<String>();
		calcUnmatchedClasses(classMatches, unmatchedJClasses, unmatchedOClasses);
    }
    
    private void calcUnmatchedClasses(HashSet<Match> classMatches, HashSet<String> unmatchedJClasses, HashSet<String> unmatchedOClasses) {
    	ArrayList<ClassRepresentation> oClasses = new ArrayList<ClassRepresentation>();	
		for (String oK : project.getOClasses().keySet()) {
			oClasses.addAll(project.getOClasses().get(oK));
		}
		
		for (ClassRepresentation c : project.getJClasses()) { 
			boolean matched = false;
			for (Match m : classMatches) {
				if (m.jComponent.equals(c.classname)) {
					matched = true;
					break;
				}
			}
			if (!matched) { unmatchedJClasses.add(c.classname);}
		}

		for (ClassRepresentation c : oClasses) { 
			boolean matched = false;
			for (Match m : classMatches) {
				if (m.oComponent.equals(c.classname)) {
					matched = true;
					break;
				}
			}
			if (!matched) { unmatchedOClasses.add(c.classname);}
		}
		
		if (Cons.printResults) {
			if (unmatchedJClasses.size() > 0) {
				System.out.println("Unmatched Classes (Java):");
				for (String c : unmatchedJClasses) {
					System.out.println("\t\""+c+"\"");				
				}
				System.out.println("");			
			} else {
				System.out.println("No Unmatched Classes (Java)\n");
			}
			
			if (unmatchedOClasses.size() > 0) {
				System.out.println("Unmatched Classes (Ontology):");
				for (String c : unmatchedOClasses) {
					System.out.println("\t\""+c+"\"");				
				}
				System.out.println("");			
			} else {
				System.out.println("No Unmatched Classes (Ontology)\n");
			} 
		}
    }
    
    private void calcUnmatchedFields(HashSet<Match> fieldMatches, String jCName, String oCName, HashSet<String> unmatchedJFields, HashSet<String> unmatchedOFields) {
    	ArrayList<ClassRepresentation> oClasses = new ArrayList<ClassRepresentation>();	
		for (String oK : project.getOClasses().keySet()) {
			oClasses.addAll(project.getOClasses().get(oK));
		}
		
		for (ClassRepresentation c : project.getJClasses()) { 
			if (c.classname.equals(jCName)) {
				for (String f : c.fields.keySet()) {
					boolean matched = false;
					for (Match m : fieldMatches) {
						if (m.jComponent.equals(f) && m.reasons.size() > 0) {
							matched = true;
							break;
						}
					}
					if (!matched) { unmatchedJFields.add(f);}
				}
			}
		}

		for (ClassRepresentation c : oClasses) { 
			if (c.classname.equals(oCName)) {
				for (String f : c.fields.keySet()) {
					boolean matched = false;
					for (Match m : fieldMatches) {
						if (m.oComponent.equals(f) && m.reasons.size() > 0) {
							matched = true;
							break;
						}
					}
					if (!matched) { unmatchedOFields.add(f);}
				}
			}
		}
		
		if (Cons.printResults) {
			if (unmatchedJFields.size() > 0) {
				System.out.println("\tUnmatched Fields (Java):");
				for (String f : unmatchedJFields) {
					System.out.println("\t\t\""+f+"\"");				
				}
				System.out.println("");			
			} else {
				System.out.println("\tNo Unmatched Fields (Java)\n");
			}
			
			if (unmatchedOFields.size() > 0) {
				System.out.println("\tUnmatched Fields (Ontology):");
				for (String f : unmatchedOFields) {
					System.out.println("\t\t\""+f+"\"");				
				}
				System.out.println("");			
			} else {
				System.out.println("\tNo Unmatched Fields (Ontology)\n");
			}    			
		}
    }
    
    private void addMatch(LinkedHashSet<Match> matches, String jComponent, String oComponent, String oSource, boolean inf, String reason) {
    	boolean found = false;
    	for (Match m : matches) {
    		if (m.jComponent.equals(jComponent) && m.oComponent.equals(oComponent)) {
    			m.reasons.add(reason);
    			found = true;
    			break;
    		}
    	}
    	
    	if (!found) {
    		matches.add(new Match(jComponent, oComponent, oSource, inf, reason));
    	}
    }
    
    private void addMatch(LinkedHashSet<Match> matches, Match mNew) {
    	boolean found = false;
    	for (Match m : matches) {
    		if (m.jComponent.equals(mNew.jComponent) && m.oComponent.equals(mNew.oComponent)) {
    			for (String r : mNew.reasons) {
    				m.reasons.add(r);
    			}
    			found = true;
    			break;
    		}
    	}
    	
    	if (!found) {
    		matches.add(new Match(mNew.jComponent, mNew.oComponent, mNew.oSource, mNew.inf, mNew.reasons));
    	}
    }
    
    public String compareTypes(LinkedHashSet<String> jTypes, LinkedHashSet<String> oTypes) {
    	String matched = "";    	
    	
    	boolean direct = false;
    	for (String t : oTypes) {
    		if (jTypes.contains(t)) {
    			direct = true;
    			break;
    		}
    	}
    	
    	if (direct) {
    		matched = "direct";
    	} else {
    		String jType = (String) jTypes.toArray()[0];
    		for (String oType : oTypes) {
	    		boolean done;
	    		ArrayList<String> jParents = new ArrayList<String>(); jParents.add(jType);
	    		ArrayList<String> oParents = new ArrayList<String>(); oParents.add(oType);
	    		done = false;
	    		String curJNode = jType, curONode = oType;
	    		while (!done) {
	    			boolean jParentFound = false, oParentFound = false;
	    			for (String parent : typeTaxonomy.keySet()) {
	    				if (typeTaxonomy.get(parent).contains(curJNode)) {
	    					curJNode = parent;
	    					jParents.add(parent);
	    					jParentFound = true;
	    				}
	    				if (typeTaxonomy.get(parent).contains(curONode)) {
	    					curONode = parent;
	    					oParents.add(parent);
	    					oParentFound = true;
	    				}
	    				if (jParentFound && oParentFound) { break;}
	    			}
	    			if (!jParentFound && !oParentFound) { done = true;}
	    		}
	    		
	    		String lastMatch = "";
	    		for (String jP : jParents) {
	    			for (String oP : oParents) {
	    				if (jP.equals(oP)) {
	    					lastMatch = jP;
	    				}
	    				if (!lastMatch.equals("")) { break;}
	    			}
					if (!lastMatch.equals("")) { break;}
	    		}
	    		if (matched.equals("") || taxonomyRankings.get(lastMatch) < taxonomyRankings.get(matched)) {
	    			matched = lastMatch;
	    		} else if (taxonomyRankings.get(lastMatch) == taxonomyRankings.get(matched)) {
	    			boolean alreadyExists = false;
	    			for (String m : matched.split(" and ")) {
	    				if (m.equals(lastMatch)) {
	    					alreadyExists = true;
	    					break;
	    				}
	    			}
	    			if (!alreadyExists) { matched += " and "+lastMatch;}
	    		}
    		}
    	}
    	
    	return matched;
    }
    
    public String getMatchScope(LinkedHashSet<String> jTypes, LinkedHashSet<String> oTypes, String typeMatch) {
    	String scope = "Incompatible";    	    	
    	typeMatch = typeMatch.split(" and ")[0];
    	
    	if (oTypes != null && jTypes != null) {
        	boolean direct = false;
	    	for (String t : oTypes) {
	    		if (jTypes.contains(t)) {
	    			direct = true;
	    			break;
	    		}
	    	}
    	
	    	if (direct) {
	    		scope = "Identical";
	    	} else {
	    		String jType = (String) jTypes.toArray()[0];
	    		String oType = (String) oTypes.toArray()[0];
	    		boolean done;
	    		ArrayList<String> jParents = new ArrayList<String>(); jParents.add(jType);
	    		ArrayList<String> oParents = new ArrayList<String>(); oParents.add(oType);
	    		done = false;
	    		String curJNode = jType, curONode = oType;
	    		while (!done) {
	    			boolean jParentFound = false, oParentFound = false;
	    			for (String parent : typeTaxonomy.keySet()) {
	    				if (typeTaxonomy.get(parent).contains(curJNode)) {
	    					curJNode = parent;
	    					jParents.add(parent);
	    					jParentFound = true;
	    				}
	    				if (typeTaxonomy.get(parent).contains(curONode)) {
	    					curONode = parent;
	    					oParents.add(parent);
	    					oParentFound = true;
	    				}
	    				if (jParentFound && oParentFound) { break;}
	    			}
	    			if (!jParentFound && !oParentFound) { done = true;}
	    		}
	    		
	    		boolean found = false;
	    		int jCounter = 0;
	    		for (String jP : jParents) {
	    			int oCounter = 0;
	    			for (String oP : oParents) {
	    				if (jP.equals(oP) && jP.equals(typeMatch) && (jCounter != 0 || oCounter != 0)) {
	    					if (jCounter != 0) {
	    						scope = "Widening";
	    					} else {
	    						scope = "Narrowing";
	    					}
	    					found = true;
	    				}
	    				oCounter += 1;
	    				if (found) { break;}
	    			}
	    			jCounter += 1;
					if (found) { break;}
	    		}
	    	}
    	}
    	
    	return scope;
    }
    
    public void clearMatches() {
    	classFieldMatches.clear();
    }
	
	public void updatePanels() {
		mainController.updatePanels();
	}
    
    public LinkedHashMap<Match,LinkedHashSet<Match>> getMatches() {
    	return classFieldMatches;
    }
    
    public Project getProject() {
    	return project;
    }

	public MatchPanel getMatchPanel() {
		return matchPanel;
	}

	public MainController getMainController() {
		return mainController;
	}
}
