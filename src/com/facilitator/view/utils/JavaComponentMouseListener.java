package com.facilitator.view.utils;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import com.facilitator.Cons;
import com.facilitator.view.ComponentsPanel;

public class JavaComponentMouseListener implements MouseListener {
	private ComponentsPanel parent;
	private JComboBox<String> javaComboBox;
	
	public JavaComponentMouseListener(ComponentsPanel parent, JComboBox<String> javaComboBox) {
		this.parent = parent;
		this.javaComboBox = javaComboBox;
	}

	@Override public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            int target = parent.getJavaSourceList().locationToIndex(e.getPoint());
            String selectedElement = parent.getJavaSourceList().getModel().getElementAt(target);
            String classname = "", fieldname = "", fieldtype = "";
            if (!parent.sourceToggle) { 
	            //Find class of form "[class] (Subclass of [parent]):"
	            for (int i = target; i >= 0; i -= 1) {
	            	if (!parent.getJavaSourceList().getModel().getElementAt(i).startsWith("        ")) {
	            		classname = parent.getJavaSourceList().getModel().getElementAt(i).split(" ")[0];
	            		if (classname.endsWith(":")) { classname = classname.substring(0,classname.length()-1);}
	            		break;
	            	}
	            }
	            
	            //Find field of form "        [field] ([type]) (Stemmed: [field stemmed])):"
				if (selectedElement.startsWith("        ")) { 
					selectedElement = selectedElement.trim();
					fieldname = selectedElement.split(" ")[0];
					selectedElement = selectedElement.substring(fieldname.length()+1);
					fieldtype = selectedElement.split(" ")[0];
					fieldtype = fieldtype.substring(1,fieldtype.length()-1);
				}
				
				parent.sourceToggle = true;
	            parent.fillJavaPanel((String) javaComboBox.getSelectedItem());
	            
	            String currentClass = "";
	            for (int i = 0; i < parent.getJavaSourceList().getModel().getSize(); i += 1) {
	            	String element = parent.getJavaSourceList().getModel().getElementAt(i).trim();
	            	try {
	            		if (element.split(" ")[2].toLowerCase().equals(classname)) {
	            			currentClass = classname;
	            			if (fieldname.equals("")) {
	            				parent.getJavaSourceList().setSelectedIndex(i);
	            				break;
	            			}
	            		}
	            	} catch (Exception ex) {
	            		//Not a class
	            	}

	            	try {
	            		if (currentClass.equals(classname) && element.split(" ")[2].substring(0,element.split(" ")[2].length()-1).toLowerCase().equals(fieldname) && element.split(" ")[1].toLowerCase().equals(fieldtype)) {
	            			parent.getJavaSourceList().setSelectedIndex(i);
            				break;
	            		}
	            	} catch (Exception ex) {
	            		//Not a field
	            	}
	            }
            } else {
	            for (int i = target; i >= 0; i -= 1) {
	            	try {
		            	if (parent.getJavaSourceList().getModel().getElementAt(i).split(" ")[1].equals("class")) {
		            		classname = parent.getJavaSourceList().getModel().getElementAt(i).split(" ")[2].toLowerCase();
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
	            
	            parent.sourceToggle = false;
	            parent.fillJavaPanel((String) javaComboBox.getSelectedItem());
	            
	            String currentClass = "";
	            for (int i = 0; i < parent.getJavaSourceList().getModel().getSize(); i += 1) {
	            	String element = parent.getJavaSourceList().getModel().getElementAt(i);
	            	try {
	            		if (element.startsWith(classname)) {
	            			currentClass = classname;
	            			if (fieldname.equals("")) {
	            				parent.getJavaSourceList().setSelectedIndex(i);
	            				break;
	            			}
	            		}
	            	} catch (Exception ex) {
	            		//Not a class
	            	}

	            	try {
	            		element = element.trim();
						String rowname = element.split(" ")[0].toLowerCase();
						element = element.substring(rowname.length()+1);
						String rowtype = element.split(" ")[0].toLowerCase();
						rowtype = rowtype.substring(1,rowtype.length()-1);
	            		if (currentClass.equals(classname) && rowname.equals(fieldname) && rowtype.equals(fieldtype)) {
	            			parent.getJavaSourceList().setSelectedIndex(i);
            				break;
	            		}
	            	} catch (Exception ex) {
	            		//Not a field
	            	}
	            }
            }
            if (Cons.printResults) { System.out.println("Class: "+classname+", Field: "+fieldname+", Type: "+fieldtype);}
        }
	}

	@Override public void mouseEntered(MouseEvent e) {
	}

	@Override public void mouseExited(MouseEvent e) {
	}

	@Override public void mousePressed(MouseEvent e) {
	}

	@Override public void mouseReleased(MouseEvent e) {
	}
}
