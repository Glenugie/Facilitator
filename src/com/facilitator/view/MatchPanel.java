package com.facilitator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.facilitator.App;
import com.facilitator.Cons;
import com.facilitator.controller.MatchController;
import com.facilitator.model.ClassRepresentation;
import com.facilitator.model.Match;
import com.facilitator.utils.SpringUtilities;
import com.facilitator.view.utils.MatchListRenderer;

/**
 * Main UI class of the system, controls the main UI
 *
 * @author Samuel Cauvin
 */
public class MatchPanel extends JPanel { 
	private static final long serialVersionUID = 9089578910656230988L;
	private MatchController matchController;
    
	private JTabbedPane resultTabs;
    private JPanel simpleResultPanel;
    private JPanel overlayResultPanel;
    private JPanel overlayMatchDetailsPanel;
    private JPanel reportResultPanel;
    private JPanel statsResultPanel;
    private JButton matchingButton;
    private JButton clearMatchesButton;
    private Integer fieldNumber;
    
    private JList<String> matchList;
    
    private JList<String> overlayMatchList;
    private JComboBox<String> javaComboBox;
    private LinkedHashMap<String,ArrayList<Color>> javaSourceColours;    
	
	public MatchPanel(MatchController mc) {
		matchController = mc;
		fieldNumber = 50;
		javaSourceColours = new LinkedHashMap<String,ArrayList<Color>>();
		
        initComponents();
    }
    
    private void initComponents() {      
    	setLayout(new BorderLayout());  	
    	JPanel matchingPanel = new JPanel(new SpringLayout());
        	JPanel matchingControlPanel = new JPanel();
        	matchingControlPanel.setLayout(new GridLayout(1,2));
        	matchingControlPanel.setMaximumSize(new Dimension(1000,50));	     
		        matchingButton = new JButton("Run Matching");
	    		matchingButton.setEnabled(false);
		        matchingButton.addActionListener(new ActionListener() {
        	        @Override public void actionPerformed(ActionEvent actionEvent) {
        	        	runMatching();
        	        }
        	    });
		        clearMatchesButton = new JButton("Clear Matches");
		        clearMatchesButton.setEnabled(false);
		        clearMatchesButton.addActionListener(new ActionListener() {
        	        @Override public void actionPerformed(ActionEvent actionEvent) {
        	        	disableButtons();
        	        	matchController.clearMatches();
        	        	fillSimpleMatchPanel("");
        	        	fillOverlayMatchPanel("");
        	        	fillReportMatchPanel("");
        	        	fillStatsMatchPanel("");
        	        	resultTabs.setSelectedIndex(0);
        	        	resultTabs.setEnabled(false);
        	        	matchController.updatePanels();
        	        }
        	    });
		        matchingControlPanel.add(matchingButton);
		        matchingControlPanel.add(clearMatchesButton);

	    		simpleResultPanel = new JPanel();
	    		fillSimpleMatchPanel("");

	    		javaComboBox = new JComboBox<String>();
	    		javaComboBox.addActionListener(new ActionListener() {
	    			@Override public void actionPerformed(ActionEvent arg0) {
	    				fillOverlayMatchPanel((String) javaComboBox.getSelectedItem());
	    			}
	    		});
	    		javaComboBox.setEnabled(false);
	    		overlayResultPanel = new JPanel();
	    		overlayMatchDetailsPanel = new JPanel();
	    		fillOverlayMatchPanel("");
	    		
	    		reportResultPanel = new JPanel();
	    		fillReportMatchPanel("");
	    		
	    		statsResultPanel = new JPanel();
	    		fillStatsMatchPanel("");
	        
		    resultTabs = new JTabbedPane();
		    resultTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		    resultTabs.addTab("Simple", simpleResultPanel);
		    resultTabs.addTab("Java Source Overlay", overlayResultPanel);
		    resultTabs.addTab("Report", reportResultPanel);
		    resultTabs.addTab("Stats", statsResultPanel);
		    resultTabs.setEnabled(false);
		    
	    	matchingPanel.add(matchingControlPanel);
	    	matchingPanel.add(resultTabs);
	    	SpringUtilities.makeCompactGrid(matchingPanel,2,1,10,10,10,10);
    	this.add(matchingPanel);
    }
    
    private void fillSimpleMatchPanel(String component) {    	
    	LinkedHashMap<Match,LinkedHashSet<Match>> matches = matchController.getMatches();
		ArrayList<ClassRepresentation> jClasses = matchController.getProject().getJClasses();
		//ArrayList<ClassRepresentation> oClasses = matchController.getProject().getOClasses();
		ArrayList<ClassRepresentation> oClasses = new ArrayList<ClassRepresentation>();	
		for (String oK : matchController.getProject().getOClasses().keySet()) {
			oClasses.addAll(matchController.getProject().getOClasses().get(oK));
		}
    	
    	String compTypeCF = ""; //Whether the Component is a Class or a Field
    	String compTypeJO = ""; //Whether the Component is from Java or Ontology

    	if (Cons.printResults) { System.out.println("\""+component+"\"");}
    	if (!component.equals("")) {
    		compTypeCF = component.substring(0,1).toLowerCase();
    		compTypeJO = component.substring(1,2).toLowerCase();
			component = component.substring(2);
    	}
    	if (Cons.printResults) { System.out.println("\t\""+compTypeCF+"\"");}
    	if (Cons.printResults) { System.out.println("\t\""+compTypeJO+"\"");}
    	if (Cons.printResults) { System.out.println("\t\""+component+"\"");}
    	

		DefaultListModel<String> matchSourceModel = new DefaultListModel<String>();
		matchList = new JList<String>(matchSourceModel);
		matchList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//matchList.setCellRenderer(new MatchListRenderer(this,""));
		matchList.setLayoutOrientation(JList.VERTICAL);
		matchList.setVisibleRowCount(-1);
		matchList.addListSelectionListener(new ListSelectionListener() {
			@Override public void valueChanged(ListSelectionEvent e) {
				
			}
		});
		//matchList.setCellRenderer(new ComponentListRenderer(this,"M"));
		
		String labelText = "", hStart = "<b>", hEnd = "</b>";
		if (matches.size() > 0) {
			labelText = "Matching Results ("+matchController.getProject().getJavaName()+" versus "+matchController.getProject().getOntoName()+")";
	    	for (Match cM : matches.keySet()) {
	    		ClassRepresentation jClass = null; for (ClassRepresentation jC : jClasses) { if (jC.classname.equals(cM.jComponent)) { jClass = jC; break;}}
	    		ClassRepresentation oClass = null; for (ClassRepresentation oC : oClasses) { if (oC.classname.equals(cM.oComponent)) { oClass = oC; break;}}
	    		if (
		    		component.equals("") || (
						!component.equals("") && (
							(compTypeCF.equals("f") && (
								(compTypeJO.equals("j") && jClass.fields.containsKey(component)) || 
								(compTypeJO.equals("o") && oClass.fields.containsKey(component))
							)) || 
							(compTypeCF.equals("c") && (
								(compTypeJO.equals("j") && cM.jComponent.equals(component)) || 
								(compTypeJO.equals("o") && cM.oComponent.equals(component))
							))
						)
					)
				) {
	    			String tabString = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
	    			String cReasons = ""; for (String r : cM.reasons) { cReasons += r+", ";} if (cReasons.length() > 0) { cReasons = cReasons.substring(0,cReasons.length()-2);} 
	    			matchSourceModel.addElement("<html>Class "+hStart+cM.jComponent+hEnd+" (Java) might match Class "+hStart+cM.oComponent+hEnd+" (Ontology) because: "+cReasons+"</html>");
	    			
	    			if (jClass.parents.size() == 0 && oClass.parents.size() > 0) { 
	    				matchSourceModel.addElement("<html>"+tabString+"Inheritance Problem: Class "+hStart+oClass.classname+hEnd+" (Ontology) has a superclass "+hStart+oClass.parents+hEnd+" but Class "+hStart+jClass.classname+hEnd+" (Java) has none</html>");
	    			} else if (jClass.parents.size() > 0 && oClass.parents.size() == 0) {
	    				matchSourceModel.addElement("<html>"+tabString+"Inheritance Problem: Class "+hStart+jClass.classname+hEnd+" (Java) has a superclass "+hStart+jClass.parents+hEnd+" but Class "+hStart+oClass.classname+hEnd+" (Ontology) has none</html>");
	    			} else if (jClass.parents.size() > 0 && oClass.parents.size() > 0 && !oClass.parents.contains((String) jClass.parents.toArray()[0])) { 
	    				matchSourceModel.addElement("<html>"+tabString+"Inheritance Problem: Class "+hStart+jClass.classname+hEnd+" (Java) has a superclass "+hStart+jClass.parents+hEnd+" but Class "+hStart+oClass.classname+hEnd+" (Ontology) has a superclass "+hStart+oClass.parents+hEnd+"</html>");
	    			}
	    			
		    		for (Match fM : matches.get(cM)) {
		        		if (
	        	    		component.equals("") || (
	        					!component.equals("") && (
	        						compTypeCF.equals("f") && (
	    								(compTypeJO.equals("j") && fM.jComponent.equals(component)) || 
	    								(compTypeJO.equals("o") && fM.oComponent.equals(component))
	        						)
	        					)
	        				)
	        			) {
	        				String fReasons = ""; for (String r : fM.reasons) { fReasons += r+", ";} if (fReasons.length() > 0) { fReasons = fReasons.substring(0,fReasons.length()-2);}
	        				if (fM.inf){
	        					String jFType = jClass.inferredFields.get(fM.jComponent).toString();
	        					String oFType = oClass.fields.get(fM.oComponent).toString();
	        					matchSourceModel.addElement("<html>"+tabString+"Inferred Field "+hStart+fM.jComponent+hEnd+" "+jFType+" (Java) might match Field "+hStart+fM.oComponent+hEnd+" "+oFType+" (Ontology) because: "+fReasons+"</html>");
	        				} else {
	        					String jFType = jClass.fields.get(fM.jComponent).toString();
	        					String oFType = oClass.fields.get(fM.oComponent).toString();
	        					matchSourceModel.addElement("<html>"+tabString+"Field "+hStart+fM.jComponent+hEnd+" "+jFType+" (Java) might match Field "+hStart+fM.oComponent+hEnd+" "+oFType+" (Ontology) because: "+fReasons+"</html>");
	        				}
		        		}	    			
		    		}
	    		}
	    	}
		} else {
			labelText = "No Matching Results";
		}
    	
		simpleResultPanel.removeAll();
		simpleResultPanel.setLayout(new SpringLayout());
		JPanel labelPanel = new JPanel();
			labelPanel.setMaximumSize(new Dimension(1000,50));
			labelPanel.add(new JLabel(labelText));
			simpleResultPanel.add(labelPanel);
		simpleResultPanel.add(new JScrollPane(matchList));
		SpringUtilities.makeCompactGrid(simpleResultPanel,2,1,10,10,10,10);
		simpleResultPanel.validate();
    }
    
    private void fillOverlayMatchPanel(String focusClassname) {  	
    	LinkedHashMap<Match,LinkedHashSet<Match>> matches = matchController.getMatches();
		LinkedHashMap<String,String> jSources = matchController.getProject().getJSources();
		ArrayList<ClassRepresentation> jClasses = matchController.getProject().getJClasses();
		//ArrayList<ClassRepresentation> oClasses = matchController.getProject().getOClasses();
		ArrayList<ClassRepresentation> oClasses = new ArrayList<ClassRepresentation>();	
		for (String oK : matchController.getProject().getOClasses().keySet()) {
			oClasses.addAll(matchController.getProject().getOClasses().get(oK));
		}
		
		javaComboBox.removeAllItems();
		//if (matches.size() > 0) {
			for (ClassRepresentation jC : jClasses) { javaComboBox.addItem(jC.uniqueClassname);}
		//}
		
		boolean found = false;
		for (int i = 0; i < javaComboBox.getItemCount(); i += 1) {
			if (javaComboBox.getItemAt(i).equals(focusClassname)) {
				found = true;
				break;
			}
		}
		
    	if (focusClassname == null || !found) { 
    		if (javaComboBox.getSelectedItem() != null) {
    			focusClassname = (String) javaComboBox.getSelectedItem();
			} else { 
				focusClassname = "";
			}
    	}
    	
		if (!focusClassname.equals("")) { javaComboBox.setSelectedItem(focusClassname);}

		DefaultListModel<String> matchSourceModel = new DefaultListModel<String>();
		overlayMatchList = new JList<String>(matchSourceModel);
		overlayMatchList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		overlayMatchList.setCellRenderer(new MatchListRenderer(this,"SO"));
		overlayMatchList.setLayoutOrientation(JList.VERTICAL);
		overlayMatchList.setVisibleRowCount(-1);
		overlayMatchList.addListSelectionListener(new ListSelectionListener() {
			@Override public void valueChanged(ListSelectionEvent e) {
				String selectedElement = overlayMatchList.getSelectedValue();
	            String classname = "", fieldname = "", fieldtype = "";
				
				for (int i = overlayMatchList.getSelectedIndex(); i >= 0; i -= 1) {
	            	try {
		            	if (overlayMatchList.getModel().getElementAt(i).split(" ")[1].equals("class")) {
		            		classname = overlayMatchList.getModel().getElementAt(i).split(" ")[2].toLowerCase();
		            		break;
		            	}
	            	} catch (Exception ex) {
	            		//Not a class
	            	}
	            }

	            if (selectedElement.startsWith(" ")){
	            	try {
						selectedElement = selectedElement.trim();
						fieldname = selectedElement.split(" ")[2].substring(0,selectedElement.split(" ")[2].length()-1).toLowerCase();
						fieldtype = selectedElement.split(" ")[1].toLowerCase();
	            	} catch (Exception ex) {
	            		//Not a field
	            	}
	            }
	            
				String matchFocus = "";
				if (!fieldname.equals("")) {
					matchFocus = "field "+fieldname+" "+fieldtype+" "+classname;
				} else if (selectedElement.contains("class")) {
					matchFocus = "class "+classname;
				}
				fillOverlayMatchDetailsPanel(matchFocus);
			}
		});
		
		javaSourceColours = matchController.getMainController().getComponentsController().getComponentsPanel().getJavaSourceColours();
		if (/*matches.size() > 0 && */jSources.size() > 0) {
			for (String jSClass : jSources.keySet()) {
				if (!focusClassname.equals("") && focusClassname.equals(jSClass)) { 
					//if (!javaSourceColours.containsKey(focusClassname)) { javaSourceColours.put(focusClassname,new ArrayList<Color>());}
					for (String line : jSources.get(jSClass).split("\n")) {
						if (Cons.printResults) { System.out.println(line);}
						
						//System.out.println("Before: \""+line+"\"");
						int spaces = 0;
						for (int i = 0; i < line.length(); i += 1) {
							if (line.substring(i,i+1).equals(" ")) {
								spaces += 1;
							} else {
								break;
							}
						}
						//System.out.println("\t"+spaces);
						line = line.substring(spaces);
						for (int i = 0; i < Math.ceil(spaces/4); i += 1) { line = "\t"+line;}
						line = line.replaceAll("\t","        ");
						
						if (line.equals("")) { line = " ";}
						//System.out.println("After: \""+line+"\"");
						matchSourceModel.addElement(line);
					}
				}
			}
		}
		javaComboBox.setMaximumSize(new Dimension(200,50));	
		
		fillOverlayMatchDetailsPanel("");    	
		overlayResultPanel.removeAll();
		overlayResultPanel.setLayout(new GridLayout(1,2));
			JPanel leftPanel = new JPanel(new SpringLayout());
			leftPanel.add(javaComboBox);
			leftPanel.add(new JScrollPane(overlayMatchList));
			SpringUtilities.makeCompactGrid(leftPanel,2,1,10,10,10,10);
		overlayResultPanel.add(leftPanel);
		overlayResultPanel.add(new JScrollPane(overlayMatchDetailsPanel));
		overlayResultPanel.validate();
    }
    
    private void fillOverlayMatchDetailsPanel(String matchFocus) {
		ArrayList<ClassRepresentation> jClasses = matchController.getProject().getJClasses();
		//ArrayList<ClassRepresentation> oClasses = matchController.getProject().getOClasses();
		ArrayList<ClassRepresentation> oClasses = new ArrayList<ClassRepresentation>();	
		for (String oK : matchController.getProject().getOClasses().keySet()) {
			oClasses.addAll(matchController.getProject().getOClasses().get(oK));
		}
		
    	overlayMatchDetailsPanel.removeAll();
    	overlayMatchDetailsPanel.setLayout(new BoxLayout(overlayMatchDetailsPanel, BoxLayout.PAGE_AXIS));
    	overlayMatchDetailsPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    	
    	String hStart = "<b>", hEnd = "</b>";
		if (!matchFocus.equals("")) {
			LinkedHashMap<Match,LinkedHashSet<Match>> matches = getMatchController().getMatches();
			
			String[] matchFocusSplit = matchFocus.split(" ");
			if (matchFocusSplit.length == 2) {
				//overlayMatchDetailsPanel.add(new JLabel("Class "+matchFocusSplit[1]+":"));
				for (Match m : matches.keySet()) {
					if (m.jComponent.equals(matchFocusSplit[1])) {
						overlayMatchDetailsPanel.add(new JLabel("<html>Class "+hStart+m.jComponent+hEnd+" (Java) might match Class "+hStart+m.oComponent+hEnd+" (Ontology) because:</html>"));
						for (String r : m.reasons) {
							overlayMatchDetailsPanel.add(new JLabel("        "+r));
						}
						overlayMatchDetailsPanel.add(new JLabel(""));
					}
				}
			} else if (matchFocusSplit.length == 4) {
				//overlayMatchDetailsPanel.add(new JLabel("Field "+matchFocusSplit[1]+" ("+matchFocusSplit[2]+") of Class "+matchFocusSplit[3]));
				for (Match cM : matches.keySet()) {
					if (cM.jComponent.equals(matchFocusSplit[3])) {
						for (Match m : matches.get(cM)) {
							if (m.jComponent.equals(matchFocusSplit[1])) {
								ClassRepresentation jClass = null; for (ClassRepresentation jC : jClasses) { if (jC.classname.equals(cM.jComponent)) { jClass = jC; break;}}
					    		ClassRepresentation oClass = null; for (ClassRepresentation oC : oClasses) { if (oC.classname.equals(cM.oComponent)) { oClass = oC; break;}}
					    		String jFType = jClass.fields.get(m.jComponent).toString();
								String oFType = oClass.fields.get(m.oComponent).toString();        				
								overlayMatchDetailsPanel.add(new JLabel("<html>Field "+hStart+m.jComponent+hEnd+" "+jFType+" of Class "+hStart+cM.jComponent+hEnd+" (Java) might match Field "+hStart+m.oComponent+hEnd+" "+oFType+" of Class "+hStart+cM.oComponent+hEnd+" (Ontology) because:</html>"));
								for (String r : m.reasons) {
									overlayMatchDetailsPanel.add(new JLabel("        "+r));
								}
								overlayMatchDetailsPanel.add(new JLabel(""));
							}
						}
					}
				}
			}
		}
		
		overlayMatchDetailsPanel.validate();
    }
    
    private void fillReportMatchPanel(String component) {  
    	LinkedHashMap<Match,LinkedHashSet<Match>> matches = matchController.getMatches();
		ArrayList<ClassRepresentation> jClasses = new ArrayList<ClassRepresentation>(); jClasses.addAll(matchController.getProject().getJClasses());
		ArrayList<ClassRepresentation> tempJClasses = new ArrayList<ClassRepresentation>(); tempJClasses.addAll(matchController.getProject().getJClasses());
		//ArrayList<ClassRepresentation> oClasses = matchController.getProject().getOClasses();
		ArrayList<ClassRepresentation> oClasses = new ArrayList<ClassRepresentation>();	
		for (String oK : matchController.getProject().getOClasses().keySet()) {
			oClasses.addAll(matchController.getProject().getOClasses().get(oK));
		}
		//ArrayList<ClassRepresentation> tempOClasses = new ArrayList<ClassRepresentation>(); tempOClasses.addAll(matchController.getProject().getOClasses());
		ArrayList<ClassRepresentation> tempOClasses = new ArrayList<ClassRepresentation>();	
		for (String oK : matchController.getProject().getOClasses().keySet()) {
			tempOClasses.addAll(matchController.getProject().getOClasses().get(oK));
		}
		
		String reportText = "";
		//if (matches.size() > 0) {			
			for (ClassRepresentation jClass : tempJClasses) {
				if (jClasses.contains(jClass)) {
					boolean found = false;
					for (Match m : matches.keySet()) {
						if (m.jComponent.equals(jClass.classname)){ 
							if (m.reasons.contains("Names Match") && m.reasons.contains("Number of Fields Match") && m.reasons.contains("All Fields Match")) {
								reportText += "Class "+m.jComponent+" (Java) and Class "+m.oComponent+" (Ontology) match perfectly\n";
								reportText += "\n";
								
								ClassRepresentation toRemove = new ClassRepresentation();
								for (ClassRepresentation tJClass : jClasses) {
									if (tJClass.classname.equals(m.jComponent)){
										toRemove = tJClass;
										break;
									}
								}
								jClasses.remove(toRemove);
								for (ClassRepresentation tOClass : oClasses) {
									if (tOClass.classname.equals(m.oComponent)){
										toRemove = tOClass;
										break;
									}
								}
								oClasses.remove(toRemove);
								
								found = true;
								break;
							}
						}
					}
					
					if (!found) {
						boolean cMatched = false;
						for (Match m : matches.keySet()) {
							if (m.jComponent.equals(jClass.classname)) {
								cMatched = true;
								break;
							}
						}
						
						if (!cMatched) { 
							reportText += "No match for Class "+jClass.classname+" (Java)\n";
							reportText += "\tSuggested Fix: Remove Class "+jClass.classname+" from Java\n";
							reportText += "\n";
						} else {
							for (String f : jClass.fields.keySet()) {	
								HashSet<Match> fieldMatches = new HashSet<Match>();
								for (Match cM : matches.keySet()) {
									if (cM.jComponent.equals(jClass.classname)) {
										for (Match m : matches.get(cM)) {
											if (m.jComponent.equals(f)) {
												fieldMatches.add(m);
											}
										}
									}
								}
								
								boolean fMatched = false;
								for (Match m : fieldMatches) {
									if (m.jComponent.equals(f) && m.reasons.size() > 0) {
										fMatched = true;
										break;
									}
								}
								if (!fMatched) {
									reportText += "No match for Field "+f+" of Class "+jClass.classname+" (Java)\n";
									reportText += "\tSuggested Fix: Remove Field "+f+" from Class "+jClass.classname+" in Java\n";
									reportText += "\n";
								}
							}
						}
					}
				}
			}
			
			for (ClassRepresentation oClass : tempOClasses) {
				if (oClasses.contains(oClass)) {
					boolean found = false;
					for (Match m : matches.keySet()) {
						if (m.oComponent.equals(oClass.classname)){ 
							if (m.reasons.contains("Names Match") && m.reasons.contains("Number of Fields Match") && m.reasons.contains("All Fields Match")) {
								reportText += m.jComponent+" (Java) and "+m.oComponent+" (Ontology) match perfectly\n";
								reportText += "\n";
								
								ClassRepresentation toRemove = new ClassRepresentation();
								for (ClassRepresentation tJClass : jClasses) {
									if (tJClass.classname.equals(m.jComponent)){
										toRemove = tJClass;
										break;
									}
								}
								jClasses.remove(toRemove);
								for (ClassRepresentation tOClass : oClasses) {
									if (tOClass.classname.equals(m.oComponent)){
										toRemove = tOClass;
										break;
									}
								}
								oClasses.remove(toRemove);
								
								found = true;
								break;
							}
						}
					}
					
					if (!found) {
						boolean cMatched = false;
						for (Match m : matches.keySet()) {
							if (m.oComponent.equals(oClass.classname)) {
								cMatched = true;
								break;
							}
						}
						
						if (!cMatched) { 
							reportText += "No match for Class "+oClass.classname+" (Ontology)\n";
							reportText += "\tSuggested Fix: Remove Class "+oClass.classname+" from Ontology\n";
							reportText += "\n";
						} else {
							for (String f : oClass.fields.keySet()) {	
								HashSet<Match> fieldMatches = new HashSet<Match>();
								for (Match cM : matches.keySet()) {
									if (cM.oComponent.equals(oClass.classname)) {
										for (Match m : matches.get(cM)) {
											if (m.oComponent.equals(f)) {
												fieldMatches.add(m);
											}
										}
									}
								}
								
								boolean fMatched = false;
								for (Match m : fieldMatches) {
									if (m.oComponent.equals(f) && m.reasons.size() > 0) {
										fMatched = true;
										break;
									}
								}
								if (!fMatched) {
									reportText += "No match for Field "+f+" of Class "+oClass.classname+" (Ontology)\n";
									reportText += "\tSuggested Fix: Remove Field "+f+" from Class "+oClass.classname+" in Ontology\n";
									reportText += "\n";
								}
							}
						}
					}
				}
			}
		//} else {
		//	reportText = "No Matching Results";
		//}
		
		JTextArea report = new JTextArea(reportText);
		report.setFont(new Font("Tahoma",Font.PLAIN,12));
		report.setEditable(false);
    	
		reportResultPanel.removeAll();
		reportResultPanel.setLayout(new BorderLayout());
		reportResultPanel.add(new JScrollPane(report));
		reportResultPanel.validate();
    }
    
    private void fillStatsMatchPanel(String component) {		
    	LinkedHashMap<Match,LinkedHashSet<Match>> matches = matchController.getMatches();
		ArrayList<ClassRepresentation> jClasses = new ArrayList<ClassRepresentation>(); jClasses.addAll(matchController.getProject().getJClasses());
		LinkedHashMap<String, ArrayList<ClassRepresentation>> oClasses = matchController.getProject().getOClasses();
    	
    	String reportText = "";
		for (String oK : oClasses.keySet()) {
			reportText += "Ontology: "+oK+"\n";
			int totalClasses = 0, perfMatchedClasses = 0, adeqMatchedClasses = 0;
			for (ClassRepresentation oClass : oClasses.get(oK)) {
				for (Match m : matches.keySet()) {
					if (m.oSource.equals(oK) && m.oComponent.equals(oClass.classname)){ 
						if (m.reasons.contains("Names Match") && m.reasons.contains("Number of Fields Match") && m.reasons.contains("All Fields Match")) {
							perfMatchedClasses += 1;
							adeqMatchedClasses += 1;
						} else if (m.reasons.contains("Names Match")) {
							boolean found = false;
							for (String r : m.reasons) {
								if (r.endsWith("Fields Match") && r.contains("/")) {
									found = true;
								}
							}
							if (!found && m.reasons.contains("All Fields Match")) { found = true;}
							if (found) { adeqMatchedClasses += 1;}
						}
					}
				}
				totalClasses += 1;
			}
			reportText += "        "+perfMatchedClasses+" / "+totalClasses+" ("+((perfMatchedClasses/(float)totalClasses)*100)+"%) Classes matched the Java project Perfectly\n";
			reportText += "        "+adeqMatchedClasses+" / "+totalClasses+" ("+((adeqMatchedClasses/(float)totalClasses)*100)+"%) Classes matched the Java project Adequatetly\n";
		}
    	
		JTextArea statsReport = new JTextArea(reportText);
		statsReport.setFont(new Font("Tahoma",Font.PLAIN,12));
		statsReport.setEditable(false);
		
		statsResultPanel.removeAll();
		statsResultPanel.setLayout(new BorderLayout());
		statsResultPanel.add(new JScrollPane(statsReport));
		statsResultPanel.validate();
    }
    
    public void update() {
    	boolean javaPresent = false; if (!matchController.getProject().getJClasses().isEmpty()) { javaPresent = true;}
    	boolean ontoPresent = false; if (!matchController.getProject().getOClasses().isEmpty()) { ontoPresent = true;}
    	boolean matchPresent = false; if (!matchController.getMatches().isEmpty()) { matchPresent = true;}
    	
    	if (javaPresent && ontoPresent) {
    		matchingButton.setEnabled(true);
    		javaComboBox.setEnabled(true);
    	} else {
    		matchingButton.setEnabled(false);
    		javaComboBox.setEnabled(false);
    	}
    	
    	if (matchPresent) {
    		clearMatchesButton.setEnabled(true);
    	} else {
    		clearMatchesButton.setEnabled(false);    
    	}
    	
    	if (javaComboBox.getModel().getSize() == 0) {
    		javaComboBox.setEnabled(false);
    	}
    	
    	this.validate();
    }
    
    private void disableButtons() {
    	matchingButton.setEnabled(false);
    	clearMatchesButton.setEnabled(false); 
    }
    
    public void runMatching() {
    	disableButtons();
    	
		MatcherSettingsDialog settingsDialog = new MatcherSettingsDialog(App.getApplication().getMainFrame(),true,this);
		settingsDialog.setPreferredSize(new Dimension(200,120));
		settingsDialog.pack();
		settingsDialog.setLocationRelativeTo(null);
		settingsDialog.setVisible(true);

    	long startTime = System.currentTimeMillis();
		if (fieldNumber != null) {
			matchController.findMatches(fieldNumber);
        	//if (!matchController.getMatches().isEmpty()) {
        		fillSimpleMatchPanel("");
        		fillOverlayMatchPanel("");
        		fillReportMatchPanel("");
        		fillStatsMatchPanel("");
        	//}
		}
    	System.out.println("Matching Run Time: "+((System.currentTimeMillis() - startTime)/1000)+" seconds");
    	
    	resultTabs.setEnabled(true);
    	matchController.updatePanels();
    }
    
    public int getFieldNumber() {
    	return fieldNumber;
    }
    
    public void setFieldNumber(int fN) {
    	fieldNumber = fN;
    }

	public Color getJavaSourceColour(int index) {
		String classname = (String) javaComboBox.getSelectedItem();
		if (classname != null && javaSourceColours.containsKey(classname)) {
			if (javaSourceColours.get(classname).size() > index && javaSourceColours.get(classname).get(index) != null) {
				return javaSourceColours.get(classname).get(index);
			}
		}
		return new Color(255,255,255);
	}
	
	public MatchController getMatchController() {
		return matchController;
	}
}