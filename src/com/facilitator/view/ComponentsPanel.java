package com.facilitator.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import com.facilitator.Cons;
import com.facilitator.controller.ComponentsController;
import com.facilitator.model.ClassRepresentation;
import com.facilitator.utils.SpringUtilities;
import com.facilitator.utils.Utilities;
import com.facilitator.view.utils.ComponentListRenderer;
import com.facilitator.view.utils.JavaComponentMouseListener;

public class ComponentsPanel extends JPanel {
	private static final long serialVersionUID = -6145306006343357776L;
	private ComponentsController componentsController;
	
    private JPanel javaSourcePanel;
    private JPanel ontoSourcePanel;
    private JButton toggleSourceButton;
    private JButton oSkeletonButton;
    private JButton loadJavaButton;
    private JButton loadOntoButton;
    private JButton clearOntoButton;
    private JButton jSkeletonButton;
    private JLabel javaProjectLabel;
    private JComboBox<String> javaComboBox;
    private JList<String> javaSourceList;
    private DefaultListModel<String> javaSourceModel;
    private JLabel ontoProjectLabel;
    private JComboBox<String> ontologySelectorComboBox;
    private JComboBox<String> ontoComboBox;
    private JList<String> ontoSourceList;
    private DefaultListModel<String> ontoSourceModel ;
    private int focusElement;
    public boolean sourceToggle = true;
    private boolean updatingJava = false;
    private boolean updatingOnto = false;
    private LinkedHashMap<String,ArrayList<Color>> javaColours;
    private LinkedHashMap<String,ArrayList<Color>> javaSourceColours;
    private LinkedHashMap<String,ArrayList<Color>> ontoColours;
	
	public ComponentsPanel(ComponentsController cc) {	
		componentsController = cc;
		javaColours = new LinkedHashMap<String,ArrayList<Color>>();
		javaSourceColours = new LinkedHashMap<String,ArrayList<Color>>();
		ontoColours = new LinkedHashMap<String,ArrayList<Color>>();
		
        initComponents();
    }
    
    private void initComponents() {        
    	setLayout(new BorderLayout());    	
        JPanel contentPanel = new JPanel(new SpringLayout());
        	JPanel labelsPanel = new JPanel(new GridLayout(1,2));
        	labelsPanel.setMaximumSize(new Dimension(1000,50));
    			JPanel javaLabelPanel = new JPanel(new GridLayout(2,1));
    				JPanel javaSubLabelPanel = new JPanel(new GridLayout(2,1));
	    			javaProjectLabel = new JLabel("No Java Project Loaded");
	    			javaProjectLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    			javaSubLabelPanel.add(javaProjectLabel);
	    			String buttonText = ""; if (!sourceToggle) { buttonText = "View Source";} else { buttonText = "View Parsed";}
	    			toggleSourceButton = new JButton(buttonText);
	        		toggleSourceButton.setEnabled(false);
	        		toggleSourceButton.addActionListener(new ActionListener() {
	        	        @Override public void actionPerformed(ActionEvent actionEvent) {
	        	        	disableButtons();
	        	        	if (sourceToggle) { 
	        	        		sourceToggle = false;
	        	        	} else { 
	        	        		sourceToggle = true;
	        	        	}
	        	        	if (!componentsController.getProject().getJClasses().isEmpty()) {
	        	        		fillJavaPanel((String) javaComboBox.getSelectedItem());
	        	        	}
	        	        	componentsController.updatePanels();	
	        	        }
	        	    });
	        		javaSubLabelPanel.add(toggleSourceButton);
        		javaLabelPanel.add(javaSubLabelPanel);
        			javaComboBox = new JComboBox<String>();
        			javaComboBox.addActionListener(new ActionListener() {
						@Override public void actionPerformed(ActionEvent arg0) {
							fillJavaPanel((String) javaComboBox.getSelectedItem());
						}
        			});
        			javaComboBox.setEnabled(false);
        		javaLabelPanel.add(javaComboBox);
        			
        		JPanel ontoLabelPanel = new JPanel(new GridLayout(2,1));
	        		JPanel ontoSubLabelPanel = new JPanel(new GridLayout(2,1));
	    			ontoProjectLabel = new JLabel("No Ontology Loaded");
	    			ontoProjectLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    			ontoSubLabelPanel.add(ontoProjectLabel);
	    			ontologySelectorComboBox = new JComboBox<String>();
	    			ontologySelectorComboBox.addActionListener(new ActionListener() {
						@Override public void actionPerformed(ActionEvent arg0) {
							fillOntoPanel((String) ontologySelectorComboBox.getSelectedItem(), (String) ontoComboBox.getSelectedItem());
						}
	    			});
	    			ontologySelectorComboBox.setEnabled(false);
	    			ontologySelectorComboBox.setMinimumSize(new Dimension(500,50));
	    			ontoSubLabelPanel.add(ontologySelectorComboBox);

    			ontoLabelPanel.add(ontoSubLabelPanel);
    			ontoComboBox = new JComboBox<String>();
    			ontoComboBox.addActionListener(new ActionListener() {
					@Override public void actionPerformed(ActionEvent arg0) {
						fillOntoPanel((String) ontologySelectorComboBox.getSelectedItem(), (String) ontoComboBox.getSelectedItem());
					}
    			});
    			ontoComboBox.setEnabled(false);
        		ontoLabelPanel.add(ontoComboBox);
    		labelsPanel.add(javaLabelPanel);
			labelsPanel.add(ontoLabelPanel);
    		
        	JPanel componentsPanel = new JPanel();
        	componentsPanel.setLayout(new GridLayout(1,2));
        		javaSourcePanel = new JPanel();
        		fillJavaPanel("");
        		ontoSourcePanel = new JPanel();
        		fillOntoPanel("","");
        	componentsPanel.add(javaSourcePanel);
        	componentsPanel.add(ontoSourcePanel);

        	JPanel controlPanel = new JPanel();
        	controlPanel.setLayout(new GridLayout(2,1));
        	controlPanel.setMaximumSize(new Dimension(1000,100));	
	        	JPanel controlPanel1 = new JPanel();
	        	controlPanel1.setLayout(new GridLayout(1,2));
	        	controlPanel1.setMaximumSize(new Dimension(1000,50));	        		
			        oSkeletonButton = new JButton("Create Ontology Skeleton from Java");
			        oSkeletonButton.setEnabled(false);
			        oSkeletonButton.addActionListener(new ActionListener() {
	        	        @Override public void actionPerformed(ActionEvent actionEvent) {
	        	        	disableButtons();
	        	        	componentsController.createOntologySkeleton();
	        	        	componentsController.updatePanels();	
	        	        }
	        	    });
			        jSkeletonButton = new JButton("Create Java Skeleton from Ontology");
			        jSkeletonButton.setEnabled(false);
			        jSkeletonButton.addActionListener(new ActionListener() {
	        	        @Override public void actionPerformed(ActionEvent actionEvent) {
	        	        	disableButtons();
	        	        	componentsController.createJavaSkeleton();
	        	        	componentsController.updatePanels();	
	        	        }
	        	    });
		        controlPanel1.add(oSkeletonButton);
		        controlPanel1.add(jSkeletonButton);
		        
	        	JPanel controlPanel2 = new JPanel();
	        	controlPanel2.setLayout(new GridLayout(1,3));
	        	controlPanel2.setMaximumSize(new Dimension(1000,50));	        	
			        loadJavaButton = new JButton("Load Java Project");
			        loadJavaButton.addActionListener(new ActionListener() {
	        	        @Override public void actionPerformed(ActionEvent actionEvent) {
	        	        	disableButtons();
	        	        	componentsController.parseJava();
	        	        	//if (!componentsController.getProject().getJClasses().isEmpty()) {
	        	        		fillJavaPanel((String) javaComboBox.getSelectedItem());
	        	        	//}
	        	        	componentsController.updatePanels();	
	        	        }
	        	    });
			        clearOntoButton = new JButton("Clear Loaded Ontology(s)");
			        clearOntoButton.setEnabled(false);
			        clearOntoButton.addActionListener(new ActionListener() {
	        	        @Override public void actionPerformed(ActionEvent actionEvent) {
	        	        	disableButtons();
	        	        	componentsController.getProject().getOClasses().clear();
	        	        	fillOntoPanel((String) ontologySelectorComboBox.getSelectedItem(), (String) ontoComboBox.getSelectedItem());
	        	        	componentsController.updatePanels();	
	        	        }
	        	    });
			        loadOntoButton = new JButton("Load Ontology");
			        loadOntoButton.addActionListener(new ActionListener() {
	        	        @Override public void actionPerformed(ActionEvent actionEvent) {
	        	        	disableButtons();
	        	        	componentsController.parseOnto();
	        	        	//if (!componentsController.getProject().getOClasses().isEmpty()) {
	        	        		fillOntoPanel((String) ontologySelectorComboBox.getSelectedItem(), (String) ontoComboBox.getSelectedItem()); 		
	        	        	//}
	        	        	componentsController.updatePanels();	
	        	        }
	        	    });
			        controlPanel2.add(loadJavaButton);
			        controlPanel2.add(clearOntoButton);
			        controlPanel2.add(loadOntoButton);
		        controlPanel.add(controlPanel1);
		        controlPanel.add(controlPanel2);
        contentPanel.add(controlPanel);
        contentPanel.add(labelsPanel);
        contentPanel.add(componentsPanel);

    	SpringUtilities.makeCompactGrid(contentPanel,3,1,10,10,10,10);
        this.add(contentPanel);
    }
    
    public void fillJavaPanel(String focusClassname) {
    	if (!updatingJava) {
	    	updatingJava = true;
	    	ArrayList<ClassRepresentation> jClasses = componentsController.getProject().getJClasses();
			LinkedHashMap<String,String> jSources = componentsController.getProject().getJSources();
			focusElement = -1;
			
			//Updates Java Combo Box
			javaComboBox.removeAllItems();
			for (ClassRepresentation jC : jClasses) { javaComboBox.addItem(jC.uniqueClassname);}
			
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

			//Updates Java List
			javaSourceModel = new DefaultListModel<String>();
			javaSourceList = new JList<String>(javaSourceModel);
			javaSourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			javaSourceList.setCellRenderer(new ComponentListRenderer(this,"J"));		
			javaSourceList.setLayoutOrientation(JList.VERTICAL);
			javaSourceList.setVisibleRowCount(-1);
			javaSourceList.addMouseListener(new JavaComponentMouseListener(this,javaComboBox));
			if (jSources.size() > 0) {
				if (!sourceToggle) {
					if (Cons.printResults) { System.out.println("Java: ");}
					for (ClassRepresentation jC : jClasses) {
						if (!focusClassname.equals("") && focusClassname.equals(jC.uniqueClassname)) { 
							if (!javaColours.containsKey(focusClassname)) { javaColours.put(focusClassname,new ArrayList<Color>());}
							String classNameLine = jC.classname; if (jC.parents.size() > 0) { classNameLine += " (Subclass of "+jC.parents+")";} classNameLine += ":";
							javaSourceModel.addElement(classNameLine); javaColours.get(focusClassname).add(Cons.classColour);
							if (Cons.printResults) { System.out.println("\t"+classNameLine);}
							
							if (jC.fields.keySet().size() == 0) { 
								if (Cons.printResults) { 
									System.out.println("\t\tNo Fields");
								}
							}
							for (String f : jC.fields.keySet()) { 		
								String fieldsLine = f+" ("+jC.fields.get(f)+") (Stemmed: "+Utilities.stem(f)+")";
								javaSourceModel.addElement("        "+fieldsLine); javaColours.get(focusClassname).add(Cons.fieldColour);
								if (Cons.printResults) { System.out.println("\t\t"+fieldsLine);}
							}
							if (Cons.printResults) { System.out.println("");}
				
							if (jC.inferredFields.keySet().size() == 0) { 
								if (Cons.printResults) { 
									System.out.println("\t\tNo Inferred Fields");
								}
							}
							for (String f : jC.inferredFields.keySet()) { 
								String infFieldsLine = "Inferred: "+f+" ("+jC.inferredFields.get(f)+") (Stemmed: "+Utilities.stem(f)+")";
								javaSourceModel.addElement("        "+infFieldsLine); javaColours.get(focusClassname).add(Cons.infFieldColour);
								if (Cons.printResults) { System.out.println("\t\t"+infFieldsLine);}
							}
							if (Cons.printResults) { System.out.println("");}
						}
					}
					
					if (focusElement != -1) {
						javaSourceList.setSelectedIndex(focusElement);
					}
				} else {
					for (String jSClass : jSources.keySet()) {
						if (!focusClassname.equals("") && focusClassname.equals(jSClass)) { 
							if (!javaSourceColours.containsKey(focusClassname)) { javaSourceColours.put(focusClassname,new ArrayList<Color>());}
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
								javaSourceModel.addElement(line);
							}
						}
					}
				}
			}		
	
			String javaLabelText = "";
			if (componentsController.getProject().getJSources().size() > 0) {
				if (!sourceToggle) {
					javaLabelText = "Parsed Java Components ("+componentsController.getProject().getJavaName()+")";
				} else {
					javaLabelText = "Java Source Code ("+componentsController.getProject().getJavaName()+")";
				}
			} else {
				javaLabelText = "No Java Project Loaded";
			}
			javaProjectLabel.setText(javaLabelText);
	
			javaSourcePanel.removeAll();
			javaSourcePanel.setLayout(new SpringLayout());
			javaSourcePanel.add(new JScrollPane(javaSourceList));
			SpringUtilities.makeCompactGrid(javaSourcePanel,1,1,10,10,10,10);
			javaSourcePanel.validate();
			updatingJava = false;
    	}
    }
    
    private void fillOntoPanel(String ontoName, String focusClassname) {
    	if (!updatingOnto) {
    		updatingOnto = true;
			
			//Updates Ontology Selector Combo Box
			ontologySelectorComboBox.removeAllItems();
			for (String onto : componentsController.getProject().getOClasses().keySet()) {
				ontologySelectorComboBox.addItem(onto);
			}
			if ((ontoName == null || ontoName.equals("")) && ontologySelectorComboBox.getItemCount() > 0) { 
				ontoName = ontologySelectorComboBox.getItemAt(0);
			}
			
			boolean found = false;
			for (int i = 0; i < ontologySelectorComboBox.getItemCount(); i += 1) {
				if (ontologySelectorComboBox.getItemAt(i).equals(ontoName)) {
					found = true;
					break;
				}
			}
			
	    	if (ontoName == null || !found) { 
	    		if (ontologySelectorComboBox.getSelectedItem() != null) {
	    			ontoName = (String) ontologySelectorComboBox.getSelectedItem();
				} else { 
					ontoName = "";
				}
	    	}
			if (!ontoName.equals("")) { ontologySelectorComboBox.setSelectedItem(ontoName);}
			
			ArrayList<ClassRepresentation> oClasses = new ArrayList<ClassRepresentation>();
			if (componentsController.getProject().getOClasses().containsKey(ontoName)) { 
				oClasses = componentsController.getProject().getOClasses().get(ontoName);
			}
	
			//Updates Ontology Combo Box
			ontoComboBox.removeAllItems();
			for (ClassRepresentation oC : oClasses) { 
				ontoComboBox.addItem(oC.classname);
			}
			
			found = false;
			for (int i = 0; i < ontoComboBox.getItemCount(); i += 1) {
				if (ontoComboBox.getItemAt(i).equals(focusClassname)) {
					found = true;
					break;
				}
			}
			
	    	if (focusClassname == null || !found) { 
	    		if (ontoComboBox.getSelectedItem() != null) {
	    			focusClassname = (String) ontoComboBox.getSelectedItem();
				} else { 
					focusClassname = "";
				}
	    	}
			if (!focusClassname.equals("")) { ontoComboBox.setSelectedItem(focusClassname);}
			
			//Updates Ontology List
	    	ontoSourceModel = new DefaultListModel<String>();
			ontoSourceList = new JList<String>(ontoSourceModel);
			ontoSourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			ontoSourceList.setCellRenderer(new ComponentListRenderer(this,"O"));			
			ontoSourceList.setLayoutOrientation(JList.VERTICAL);
			ontoSourceList.setVisibleRowCount(-1);
			if (oClasses.size() > 0) {
				if (Cons.printResults) { System.out.println("Ontology: ");}
				for (ClassRepresentation oC : oClasses) {
					if (!focusClassname.equals("") && focusClassname.equals(oC.classname)) { 
						if (!ontoColours.containsKey(focusClassname)) { ontoColours.put(focusClassname,new ArrayList<Color>());}
						String classNameLine = oC.classname; if (oC.parents.size() > 0) { classNameLine += " (Subclass of "+oC.parents+")";} classNameLine += ":";
						ontoSourceModel.addElement(classNameLine); ontoColours.get(focusClassname).add(new Color(150,255,150));
						if (Cons.printResults) { System.out.println("\t"+classNameLine);}
						
						if (oC.fields.keySet().size() == 0) { 
							if (Cons.printResults) { 
								System.out.println("\t\tNo Fields");
							}
						}
						for (String f : oC.fields.keySet()) { 
							String fieldsLine = f+" ("+oC.fields.get(f)+") (Stemmed: "+Utilities.stem(f)+")";
							ontoSourceModel.addElement("        "+fieldsLine); ontoColours.get(focusClassname).add(new Color(255,255,150));
							if (Cons.printResults) { System.out.println("\t\t"+fieldsLine);}
						}
						if (Cons.printResults) { System.out.println("");}
					}
				}   
			}
	
			//Updates Ontology Label
			String ontoLabelText = "";
			if (componentsController.getProject().getOClasses().size() > 0) {
				ontoLabelText = "Parsed Ontology Components"/* ("+componentsController.getProject().getOntoName()+")"*/;
			} else {
				ontoLabelText = "No Ontology Loaded";
			}
			ontoProjectLabel.setText(ontoLabelText);
			
			ontoSourcePanel.removeAll();
			ontoSourcePanel.setLayout(new SpringLayout());
			ontoSourcePanel.add(new JScrollPane(ontoSourceList));
			SpringUtilities.makeCompactGrid(ontoSourcePanel,1,1,10,10,10,10);
			ontoSourcePanel.validate();
			updatingOnto = false;
    	}
    }   
    
    public void update() {
    	boolean javaPresent = false; if (!componentsController.getProject().getJClasses().isEmpty()) { javaPresent = true;}
    	boolean ontoPresent = false; if (!componentsController.getProject().getOClasses().isEmpty()) { ontoPresent = true;}
    	
    	loadJavaButton.setEnabled(true);
    	loadOntoButton.setEnabled(true);
    	
    	if (javaPresent) { 
        	toggleSourceButton.setEnabled(true);
    		oSkeletonButton.setEnabled(true);
        	javaComboBox.setEnabled(true);
    	} else { 
        	toggleSourceButton.setEnabled(false);
    		oSkeletonButton.setEnabled(false);
        	javaComboBox.setEnabled(false);
    	}
    	
    	if (ontoPresent) { 
    		clearOntoButton.setEnabled(true);
    		jSkeletonButton.setEnabled(true);
        	ontoComboBox.setEnabled(true);
        	ontologySelectorComboBox.setEnabled(true);
    	} else { 
    		clearOntoButton.setEnabled(false);
    		jSkeletonButton.setEnabled(false);
        	ontoComboBox.setEnabled(false);
        	ontologySelectorComboBox.setEnabled(false);
    	}
    	

		String buttonText = ""; if (!sourceToggle) { buttonText = "View Source";} else { buttonText = "View Parsed";}
		toggleSourceButton.setText(buttonText);;
    	
    	this.validate();
    }
    
    private void disableButtons() {
    	toggleSourceButton.setEnabled(false);
    	oSkeletonButton.setEnabled(false);
    	loadJavaButton.setEnabled(false);
    	loadOntoButton.setEnabled(false);
    	clearOntoButton.setEnabled(false);
    	jSkeletonButton.setEnabled(false); 
    	javaComboBox.setEnabled(false);
    	ontoComboBox.setEnabled(false);
    	ontologySelectorComboBox.setEnabled(false);
    }
    
    public void clearJavaColours() {
    	javaColours.clear();
    	javaSourceColours.clear();
    }
    
    public void clearOntoColours() {
    	ontoColours.clear();
    }
    
    public JList<String> getJavaSourceList() {
    	return javaSourceList;
    }
    
    public String getSelectedOntology() {
    	return (String) ontologySelectorComboBox.getSelectedItem();
    }

	public Color getJavaColour(int index) {
		String classname = (String) javaComboBox.getSelectedItem();
		if (classname != null && javaColours.containsKey(classname)) {
			if (javaColours.get(classname).size() > index && javaColours.get(classname).get(index) != null) {
				return javaColours.get(classname).get(index);
			}
		}
		return new Color(255,255,255);
	}
	
	public LinkedHashMap<String, ArrayList<Color>> getJavaSourceColours() {
		return javaSourceColours;
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
	
	public void setJavaSourceColour(String key, String t, int index) {
		Color c = new Color(255,255,255);
		switch (t) {
		case "C": c = Cons.classColour; break;
		case "F": c = Cons.fieldColour; break;
		case "IF": c = Cons.infFieldColour; break;
		}
		
		if (!javaSourceColours.containsKey(key)) { javaSourceColours.put(key,new ArrayList<Color>());}
		if (index != -1) {
			while (index >= javaSourceColours.get(key).size()) { javaSourceColours.get(key).add(new Color(255,255,255));}
			javaSourceColours.get(key).set((index-1),c);
		} else {
			javaSourceColours.get(key).add(c);
		}
	}

	public Color getOntoColour(int index) {
		String classname = (String) ontoComboBox.getSelectedItem();
		if (classname != null && ontoColours.containsKey(classname)) {
			if (ontoColours.get(classname).size() > index && ontoColours.get(classname).get(index) != null) {
				return ontoColours.get(classname).get(index);
			}
		}
		return new Color(255,255,255);
	}
	
	public ComponentsController getComponentsController() {
		return componentsController;
	}
}
