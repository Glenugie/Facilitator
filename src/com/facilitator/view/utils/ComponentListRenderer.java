package com.facilitator.view.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.facilitator.Cons;
import com.facilitator.model.ClassRepresentation;
import com.facilitator.model.Project;
import com.facilitator.view.ComponentsPanel;

public class ComponentListRenderer extends JLabel implements ListCellRenderer<String> {
	private static final long serialVersionUID = 7782628018732199860L;
	
	private ComponentsPanel parent;
	private String listType;
	//private boolean comment;
	
    public ComponentListRenderer(ComponentsPanel p, String lT) {
    	parent = p;
    	listType = lT;
    	
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }

	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
    	Color base = Color.WHITE;
    	if (listType.equals("J") && !parent.sourceToggle) {
			base = parent.getJavaColour(index);
        } else if (listType.equals("J") && parent.sourceToggle) {
			base = parent.getJavaSourceColour(index);
        } else if (listType.equals("O")) {
			base = parent.getOntoColour(index);
        }
    	
    	if (listType.equals("J") && parent.sourceToggle) {
        	Project p = parent.getComponentsController().getProject();
    		String oValue = value;
    		
    		boolean comment = false;
    		if (oValue.trim().startsWith("//") || oValue.trim().startsWith("/*") || oValue.trim().endsWith("*/")) { comment = true;}
    		if (!comment) {
    			for (int i = index; i >= 0; i -= 1) {
    				String preVal = list.getModel().getElementAt(i).trim();
    				if (preVal.startsWith("/*") && !preVal.endsWith("*/")) { comment = true;}
    				if (preVal.endsWith("*/")) { break;}    				
    			}
    		}
    		
    		if (comment) { value = "<font color=#3F7F5F>"+value+"</font>";}
    		if (!value.startsWith("<html>")) { 
    			value = "<html>"+value+"</html>";
    			value = value.replaceAll("        ", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    		}
    		if (!comment) {
    			String classOfField = "";
    			for (int i = index; i >= 0; i -= 1) {
    				String[] preVals = list.getModel().getElementAt(i).trim().split(" ");
    				int j = 0;
    				for (String pV : preVals) {
    					if (pV.equals("class") && (j+1) < preVals.length) {
    						classOfField = preVals[j+1].toLowerCase();
    						break;
    					}
    					j += 1;
    				}
    			}
    			
    			if (!classOfField.equals("")) {
		    		for (ClassRepresentation c : p.getJClasses()) {
		    			if (c.classname.equals(classOfField)) {
			    			for (String f : c.fields.keySet()) {
			    				int pos = 0;
			    				f = " "+f;
				    			if (value.toLowerCase().contains(f)) {
				    				while (value.toLowerCase().indexOf(f,pos) != -1) {
				    					pos = value.toLowerCase().indexOf(f,pos);
				    					value = value.substring(0,pos)+"<font color=#0000C0>"+value.substring(pos,pos+f.length())+"</font>"+value.substring(pos+f.length());
				    					pos += (20+f.length()+6+1);
			    					}
				    			}
			    			}
		    			}
		    		}
    			}
    			
	    		int kwId = 0;
	    		for (String kw : Cons.javaKeywords) {
	    			if (value.contains(kw)) {
	    				value = value.replaceAll(kw,"<font color="+Cons.javaKeywordColours[kwId]+">"+kw+"</font>");
	    			}
	    			kwId += 1;
	    		}
    		}
    	}
    	
		if (isSelected) { base = new Color((base.getRed()/8)*5,(base.getGreen()/8)*5,(base.getBlue()/8)*5);}
		setBackground(base);
		
		setText(value);
        setFont(new Font("Consolas",Font.PLAIN,12));

        return this;
	}	
}
