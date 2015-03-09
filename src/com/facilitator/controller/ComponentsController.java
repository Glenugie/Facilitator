package com.facilitator.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import com.facilitator.Cons;
import com.facilitator.model.ClassRepresentation;
import com.facilitator.model.Project;
import com.facilitator.utils.Utilities;
import com.facilitator.view.ComponentsPanel;
import com.habelitz.jsobjectizer.jsom.api.ClassDeclaration;
import com.habelitz.jsobjectizer.jsom.api.ConstructorDefinition;
import com.habelitz.jsobjectizer.jsom.api.EnumConstant;
import com.habelitz.jsobjectizer.jsom.api.EnumDeclaration;
import com.habelitz.jsobjectizer.jsom.api.ImportDeclaration;
import com.habelitz.jsobjectizer.jsom.api.InterfaceDeclaration;
import com.habelitz.jsobjectizer.jsom.api.JavaSource;
import com.habelitz.jsobjectizer.jsom.api.MethodDefinition;
import com.habelitz.jsobjectizer.jsom.api.Type;
import com.habelitz.jsobjectizer.jsom.api.VariableDeclarator;
import com.habelitz.jsobjectizer.jsom.util.TraverseActionAdapter;
import com.habelitz.jsobjectizer.unmarshaller.JSourceUnmarshaller;

public class ComponentsController {
	private MainController mainController;
	private Project project;
	private ComponentsPanel componentsPanel;
	
	private OWLOntologyManager man;
	private OWLDataFactory df;
	private OWLOntology o;
	private IRI oIRI;
	
	public String selectedOntology;
	
    public ComponentsController(MainController mc) {
		mainController = mc;
		project = mc.getProject();
		
		componentsPanel = new ComponentsPanel(this);
    }
    
    public boolean parseJava() {
    	project.getJClasses().clear();
    	project.getJSources().clear();
    	componentsPanel.clearJavaColours();
    	
    	File selected = null;    
		if (!Cons.presetFiles) {
	        JFileChooser fc = new JFileChooser(project.getJavaPath());
	        fc.setDialogTitle("Select Java Project Folder...");
	        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	        fc.setFileFilter(new FileNameExtensionFilter("Java Files", "Java"));
	        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				selected = fc.getSelectedFile();
	        }
	    } else {
			selected = new File(Cons.presetJavaPath);
		}

        boolean javaFile = false;
		if (selected != null && selected.exists()) {
        	long startTime = System.currentTimeMillis();
	    	project.setJavaPath(selected.getAbsolutePath());
	    	ArrayList<File> foundFiles = new ArrayList<File>();

	    	String classpathFile = "";
	    	if (selected.isDirectory()) {
    			for (File file : selected.listFiles()) {
    				if (!file.isDirectory()) {
    					if (file.getName().equals(".classpath")) {
    						classpathFile = file.getPath();
    					}
    				}
    			}
    			if (!classpathFile.equals("")) {
					try {
						BufferedReader in = new BufferedReader(new FileReader(new File(classpathFile)));
	    				String line = "";
	    				while((line = in.readLine()) != null) {
	    					line = line.trim();
	    					line = line.substring(1,line.length()-1);
	    					//<classpathentry kind="src" path="src"/>
	    					if (line.startsWith("classpathentry")) {
		    					line = line.substring(15);
	    						while (!line.endsWith("\"")) { line = line.substring(0,line.length()-1);}
	    						String[] lineSplit = line.split(" ");
		    					
	    						boolean src = false;
	    						for (String kindCheck : lineSplit) {
	    							if (kindCheck.equals("kind=\"src\"")) {
	    								src = true;
	    								break;
	    							}
	    						}
	    						
	    						if (src) {
		    						for (String pathCheck : lineSplit) {
		    							if (pathCheck.startsWith("path")) {
		    								pathCheck = pathCheck.substring(6,pathCheck.length()-1);
		    								foundFiles.add(new File(pathCheck));
		    							}
		    						}
	    						}
	    					}
	    				}
	    				in.close();
					} catch (Exception e) {
						System.err.println("Could not read .classpath file");
					}
    			}
	    	}
	
			if (foundFiles.size() == 0) { foundFiles.add(selected);}
	    	if (selected.isDirectory()) {
	    		boolean dirLeft = true;
	    		while (dirLeft) {
	    			ArrayList<File> toAdd = new ArrayList<File>();
	    			ArrayList<File> toRemove = new ArrayList<File>();
	    			dirLeft = false;
	    			
	    			for (File file : foundFiles) {
	    				if (file.isDirectory()) {
	    		    		for (File child : file.listFiles()) {
	    		    			toAdd.add(child);
	    		    			if (child.isDirectory()) { dirLeft = true;}
	    		    			else if (child.getName().toLowerCase().endsWith(".java")) { javaFile = true;}
	    		    		}
	    		    		toRemove.add(file);
	    				}
	    			}
	    			foundFiles.addAll(toAdd);
	    			for (File file : toRemove) { foundFiles.remove(file);}
	    		}
	    	} else {
	    		if (selected.getName().toLowerCase().endsWith(".java")) {
	    			javaFile = true;
	    		}
	    	}

        	HashMap<String,HashMap<String,String>> classImportMappings = new HashMap<String,HashMap<String,String>>();
	        if (javaFile) {
		    	for (final File file : foundFiles) {
		    		if (file.isFile() && file.getName().toLowerCase().endsWith(".java")) {	    					    		
			    		JavaSource javaSource = null;
			    		try {
			    			ArrayList<String> errors = new ArrayList<String>();
			    			javaSource = new JSourceUnmarshaller().unmarshal(file,errors);
			    		} catch (Exception e) {
			    			System.err.println("Unable to parse file "+file.getAbsolutePath());
			    		}

			    		final ClassRepresentation c = new ClassRepresentation();
			    		final String selectedFilepath = selected.getAbsolutePath();
			    		final HashMap<String,String> importMappings = new HashMap<String,String>();
			    		if (javaSource != null) {
			    			javaSource.traverseAll(new TraverseActionAdapter() {
			    				private String lastType = "";
			    				private String lastEnum = "";
			    				//The parser groups all fields to the top of the tree interestingly, so after any method/constructor appears the field list is done
			    				private boolean field = true;  
			    				private boolean classFound = false;
			    				
			    				@Override
			    				public void performAction(ImportDeclaration obj) {
			    					String importPath = obj.getImportPath().toString().toLowerCase();
			    					String importName = obj.getImportPath().getIdentifier(obj.getImportPath().getIdentifierCount()-1).toLowerCase();
			    					if (obj.isMultiImport()) {
			    						importPath += ".*";
			    						importName = "*";
			    					}
			    					importMappings.put(importName,importPath);
			    					//System.out.println(importPath+" - "+importName);
			    				}
			    				
			    				@Override
			    				public void performAction(ClassDeclaration obj) {
			    					if (obj.getIdentifier().equals(file.getName().substring(0,file.getName().length()-5))) {
				    			    	c.classname = obj.getIdentifier().toLowerCase();
				    			    	c.unmodifiedClassname = c.classname;
				    			    	if (!file.getAbsolutePath().equals(selectedFilepath)) {
				    			    		//Create a Package String
				    			    		String packageString = "", relPath = file.getAbsolutePath().substring(selectedFilepath.length()+1);
				    			    		String[] relPathComponents = relPath.split("\\\\");
				    			    		for (int i = 0; i < (relPathComponents.length-1); i += 1) {
			    			    				packageString += relPathComponents[i]+".";
				    			    		}
				    			    		c.uniqueClassname = packageString+c.classname;
				    			    	}
				    			    	//c.uniqueClassname = obj.getIdentifier().toLowerCase()+" ("+file.getAbsolutePath()+")";
				    			    	//c.uniqueClassname = c.classname;
				    			    	classFound = true;
				    					componentsPanel.setJavaSourceColour(c.uniqueClassname,"C",obj.getLineNumber());
				    			    	if (obj.hasExtendsClause()) {
				    			    		String extendsClause = "";
				    			    		if (obj.getExtendsClause().getType().isComplexType()) {
					    						extendsClause = obj.getExtendsClause().getType().getQualifiedTypeIdentifier().get(0).getIdentifier().toLowerCase();
					    					} else {
					    						extendsClause = obj.getExtendsClause().getType().getPrimitiveType().getTypeKeyword().getTypeAsString().toLowerCase();
					    					}
					    					c.parents.add(extendsClause);
				    			    	}
			    					}
			    				}
			    				
			    				@Override
			    				public void performAction(InterfaceDeclaration obj) {
					    			if (!classFound) {
				    			    	c.classname = obj.getIdentifier().toLowerCase();			
				    			    	c.unmodifiedClassname = c.classname;	    			    	
				    			    	if (!file.getAbsolutePath().equals(selectedFilepath)) {
				    			    		//Create a Package String
				    			    		String packageString = "", relPath = file.getAbsolutePath().substring(selectedFilepath.length()+1);
				    			    		String[] relPathComponents = relPath.split("\\\\");
				    			    		for (int i = 0; i < (relPathComponents.length-1); i += 1) {
			    			    				packageString += relPathComponents[i]+".";
				    			    		}
				    			    		c.uniqueClassname = packageString+c.classname;
				    			    	}
				    			    	//c.uniqueClassname = obj.getIdentifier().toLowerCase()+" ("+file.getAbsolutePath()+")";
				    			    	//c.uniqueClassname = c.classname;
				    					componentsPanel.setJavaSourceColour(c.uniqueClassname,"C",obj.getLineNumber());
				    			    	if (obj.hasExtendsClause()) {
				    			    		String extendsClause = "";
					    					if (obj.getExtendsClause().getTypes().get(0).isComplexType()) {
					    						extendsClause = obj.getExtendsClause().getTypes().get(0).getQualifiedTypeIdentifier().get(0).getIdentifier().toLowerCase();
					    					} else {
					    						extendsClause = obj.getExtendsClause().getTypes().get(0).getPrimitiveType().getTypeKeyword().getTypeAsString().toLowerCase();
					    					}
					    					c.parents.add(extendsClause);
				    			    	}
					    			}
			    				}
			    				
			    				@Override
			    				public void performAction(EnumDeclaration obj) {
		    						lastEnum = obj.getIdentifier().toLowerCase();
			    				}
			    				
			    				@Override
			    				public void performAction(EnumConstant obj) {
			    					System.out.println(lastEnum+": "+obj.getIdentifier().toLowerCase());
			    				}
			    				
			    				@Override
			    				public void performAction(Type obj) {
			    					if (obj.isComplexType()) {
			    						lastType = obj.getQualifiedTypeIdentifier().get(0).getIdentifier();
			    						if (lastType.toLowerCase().equals("object")) { lastType = "generic";}
			    					} else {
			    						lastType = obj.getPrimitiveType().getTypeKeyword().getTypeAsString();
			    					}
			    				}
			    				
			    				@Override
			    				public void performAction(VariableDeclarator obj) {
			    					if (field) {
				    					componentsPanel.setJavaSourceColour(c.uniqueClassname,"F",obj.getLineNumber());
				    					LinkedHashSet<String> types = new LinkedHashSet<String>(); types.add(lastType.toLowerCase());
			    						c.fields.put(obj.getIdentifier().getIdentifier().toLowerCase(),types);
			    					}
			    				}
			    				
			    				@Override
			    				public void performAction(ConstructorDefinition obj) {
			    					field = false;
			    				}
			    				
			    				@Override
			    				public void performAction(MethodDefinition obj) {
			    					field = false;
			    				}
			    			});
			    		}
			    		
			    		if (!c.classname.equals("")) {
			    			project.getJClasses().add(c);
			    			classImportMappings.put(c.uniqueClassname,importMappings);
				    		
				    		StringBuffer buffer = new StringBuffer();
				    		try {
				    			BufferedReader in = new BufferedReader(new FileReader(file));
				    			String line = null, source = "";
				    			while ((line = in.readLine()) != null) {
				    				buffer.append(line).append("\n");
				    				source += line+"\n";
				    				componentsPanel.setJavaSourceColour(c.uniqueClassname,"",-1);
				    			}
				    			project.getJSources().put(c.uniqueClassname,source);
					    		in.close();
				    		} catch (Exception e) {
				    			e.printStackTrace();
				    		}
			    		}	
		    		}
		    	}
		
				int depth;
				for (ClassRepresentation jC : project.getJClasses()) {
					for (String f : jC.fields.keySet()) {
						depth = 0;
						boolean complexClass = false;
						do {
							for (ClassRepresentation innerJC : project.getJClasses()) {
								if (innerJC.classname.equals(jC.fields.get(f))) {
									jC.inferredFields.putAll(innerJC.fields);
									complexClass = true;
								}
							}
							depth += 1;
						} while (complexClass && depth < 10);
						
						depth = 0;
						boolean hasParent = true; if (jC.parents.size() == 0) { hasParent = false;}
						String parent = "";
						if (hasParent) { parent = (String) jC.parents.toArray()[0];}
						while (hasParent && depth < 10) {
							hasParent = false;
							for (ClassRepresentation innerJC : project.getJClasses()) {
								if (innerJC.classname.equals(parent)) {
									jC.inferredFields.putAll(innerJC.fields);
									hasParent = true;
									if (innerJC.parents.size() > 0) {
										parent = (String) innerJC.parents.toArray()[0];
									}
									depth += 1;
									break;
								}
							}
						}
					}
				}
	        } else {
	            JOptionPane.showMessageDialog(null, "No Java Files Selected", "Error", JOptionPane.ERROR_MESSAGE);
	        }	        

	        ArrayList<String> classNames = new ArrayList<String>();
	        ArrayList<String> dupClassNames = new ArrayList<String>();
			for (ClassRepresentation jC : project.getJClasses()) {
				if (!classNames.contains(jC.classname)) { 
					classNames.add(jC.classname);
				} else {
					dupClassNames.add(jC.classname);
				}
			}
			
			HashMap<String,Integer> dupCounts = new HashMap<String,Integer>();
			for (String dCN : dupClassNames) { dupCounts.put(dCN, 1);}			

			for (ClassRepresentation jC : project.getJClasses()) {
				if (dupCounts.containsKey(jC.classname)) {
					String curKey = jC.classname;
					int curVal = dupCounts.get(jC.classname);
					jC.classname = jC.classname+"_"+curVal;
					dupCounts.remove(curKey);
					dupCounts.put(curKey,curVal+1);
				}
			}
			
			for (ClassRepresentation jC : project.getJClasses()) {
				if (!jC.parents.isEmpty()) {
					LinkedHashSet<String> newParents = new LinkedHashSet<String>();
					for (String p : jC.parents) {
						String newParent = p;						
						if (classImportMappings.get(jC.uniqueClassname).containsKey(p)) {
							String parentUniqueName = classImportMappings.get(jC.uniqueClassname).get(p);
							for (ClassRepresentation jPC : project.getJClasses()) {
								if (jPC.uniqueClassname.equals(parentUniqueName)) {
									newParent = jPC.classname;
									break;
								}
							}
						} else {
							//Need a way to catch ambiguous imports (i.e. com.test.package1.*)
							HashMap<String,String> uniqueParents = new HashMap<String,String>();
							for (ClassRepresentation jPC : project.getJClasses()) {
								if (jPC.classname.equals(p)) {
									uniqueParents.put(jPC.classname,jPC.uniqueClassname);
								}
							}
							
							boolean ambiguousImport = false, importResolved = false;;
							for (String i : classImportMappings.get(jC.uniqueClassname).values()){
								if (i.contains("*")) { 
									ambiguousImport = true;
									String iReplaced = i.replaceAll("\\*", p);
									for (ClassRepresentation jPC : project.getJClasses()) {
										if (jPC.uniqueClassname.equals(iReplaced)) {
											newParent = jPC.classname;
											importResolved = true;
											break;											
										}
									}
								}
								if (importResolved) { break;}
							}
							
							if (!ambiguousImport) {
								for (ClassRepresentation jPC : project.getJClasses()) {
									if (jPC.classname.contains("_")) {
										String childRoot = jC.uniqueClassname.substring(0,(jC.uniqueClassname.length()-(jC.classname.length()-1)));
										String originalName = jPC.classname.split("_")[jPC.classname.split("_").length-2];
										if (jPC.uniqueClassname.startsWith(childRoot) && originalName.equals(p)) {
											newParent = jPC.classname;
											break;
										}
									}
								}
							}
						}
						newParents.add(newParent);
						//System.out.println(p+" becomes "+newParents.toArray()[newParents.size()-1]);
					}
					jC.parents = newParents;
				}
			}
	        
	        int sourceLines = 0; 
	        for (String key : project.getJSources().keySet()) {
	        	sourceLines += project.getJSources().get(key).split("\n").length;
	        }

	    	Collections.sort(project.getJClasses(), new Comparator<ClassRepresentation>() {
				@Override
				public int compare (final ClassRepresentation entry1, final ClassRepresentation entry2) {
					return entry1.classname.compareTo(entry2.classname);
				}
			});
	    	
	        System.out.println("Java: "+sourceLines+" Lines ("+project.getJClasses().size()+" Classes) Parsed in "+((System.currentTimeMillis() - startTime)/1000)+" seconds");
		}		
    	
    	if (!javaFile && selected != null) { System.err.println("No valid java files found at \""+project.getJavaPath()+"\"");}
    	return javaFile;
    }
    
    public boolean parseOnto() {
    	//project.getOClasses().clear();
    	componentsPanel.clearOntoColours();
    	
    	File selected = null;    
		if (!Cons.presetFiles) {
	        JFileChooser fc = new JFileChooser(project.getOntoPath());
	        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	        fc.setDialogTitle("Select Ontology...");
            fc.setFileFilter(new FileNameExtensionFilter("OWL Ontologies", "XML", "owl"));
	        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				selected = fc.getSelectedFile();
	        }
	    } else {
			selected = new File(Cons.presetOntoPath);
		}

    	ArrayList<File> selectedFiles = new ArrayList<File>();
    	boolean validFile = false;
    	if (selected != null && selected.exists()) {
    		if (selected.isDirectory()) {
	    		for (File child : selected.listFiles()) {
	    			if (!child.isDirectory() && child.getName().toLowerCase().endsWith(".owl")) { 
	        			selectedFiles.add(child);
	    			}
	    		}
    		} else {
    			selectedFiles.add(selected);
    		}
    	}
    	
    	for (File selectedFile : selectedFiles) {
	    	project.setOntoPath(selectedFile.getAbsolutePath());
	    	try {
				man = OWLManager.createOWLOntologyManager(); ;
				df = man.getOWLDataFactory();
				o = man.loadOntologyFromOntologyDocument(selectedFile);
				oIRI = o.getOntologyID().getOntologyIRI();  
				validFile = true;
	    	} catch (Exception e) {
	    		System.err.println("No ontology file found at \""+project.getOntoPath()+"\"");
	    	}
	    	
	    	//Remove Old Ontology Definition
	    	if (project.getOClasses().containsKey(selectedFile.getAbsolutePath())) {
	    		project.getOClasses().remove(selectedFile.getAbsolutePath());
	    	}
	    	
	    	if (validFile && !project.getOClasses().containsKey(selectedFile.getAbsolutePath())) {
	    		project.getOClasses().put(selectedFile.getAbsolutePath(),new ArrayList<ClassRepresentation>());
	        	long startTime = System.currentTimeMillis();
	    		for (OWLClass cls : o.getClassesInSignature()) {    			
	    			ClassRepresentation c = new ClassRepresentation();
	    			c.classname = cls.getIRI().getFragment().toLowerCase();
	    			c.source = selectedFile.getAbsolutePath();
	    			
	    			try {
	    				if (cls.getSuperClasses(o).size() > 0) { 
	    					for (OWLClassExpression sCls : cls.getSuperClasses(o)) {
		    					c.parents.add(sCls.asOWLClass().getIRI().getFragment().toLowerCase());
	    					}
	    				}
	    			} catch (Exception e) {
	    				//Invalid Superclass, treat as no parent
	    			}
	    			
	    			//Something in here is wrong
					for (OWLDataProperty p : o.getDataPropertiesInSignature()) {
						boolean correctDomain = false;
						for (OWLClassExpression d : p.getDomains(o)) {
							if (((OWLClass) d.getClassesInSignature().toArray()[0]).getIRI().getFragment().toLowerCase().equals(c.classname)) {
								correctDomain = true;
								break;
							}
						}
						
						if (correctDomain) {
							LinkedHashSet<String> ranges = new LinkedHashSet<String>();
							if (p.getRanges(o).size() > 0) {
								for (OWLDataRange range : p.getRanges(o)) {
									//datarangerestriction(xsd:string facetrestriction(pattern "[smooth,mottled]"))
									String cleanType = "";
									System.out.println(p.getIRI().getFragment().toLowerCase());
									System.out.println("\t"+range.toString().toLowerCase());
									System.out.println("");
									switch ((range.getDatatypesInSignature().toArray())[0].toString().toLowerCase()) {
										case "xsd:boolean": cleanType = "boolean"; break;
										case "xsd:int": case "xsd:integer": cleanType = "int"; break;
										case "xsd:long": cleanType = "long"; break;
										case "xsd:string": cleanType = "string"; break;
										case "xsd:float": cleanType = "float"; break;
										case "xsd:date": cleanType = "date"; break;
										case "xsd:time": cleanType = "time"; break;
										case "xsd:datetime": cleanType = "datetime"; break;
										default: cleanType = "generic"; break;
									}
									ranges.add(cleanType);
								}
							} else {
								ranges.add("generic");
							}
							c.fields.put(p.getIRI().getFragment().toLowerCase(),ranges);
						}
					}
	    			
					for (OWLObjectProperty p : o.getObjectPropertiesInSignature()) {
						boolean correctDomain = false;
						for (OWLClassExpression d : p.getDomains(o)) {
							if (((OWLClass) d.getClassesInSignature().toArray()[0]).getIRI().getFragment().toLowerCase().equals(c.classname)) {
								correctDomain = true;
								break;
							}
						}
						
						if (correctDomain) {
							LinkedHashSet<String> ranges = new LinkedHashSet<String>();
							if (p.getRanges(o).size() > 0) {
								for (OWLClassExpression range : p.getRanges(o)) {
									ranges.add(range.asOWLClass().getIRI().getFragment().toLowerCase());
								}
							} else {
								ranges.add("generic");
							}
							c.fields.put(p.getIRI().getFragment().toLowerCase(),ranges);
						}
					}
					
					project.getOClasses().get(selectedFile.getAbsolutePath()).add(c);
	    		}

	        	Collections.sort(project.getOClasses().get(selectedFile.getAbsolutePath()), new Comparator<ClassRepresentation>() {
	    			@Override
	    			public int compare (final ClassRepresentation entry1, final ClassRepresentation entry2) {
	    				return entry1.classname.compareTo(entry2.classname);
	    			}
	    		});
	        	
		        System.out.println("Ontology: "+project.getOClasses().get(selectedFile.getAbsolutePath()).size()+" Concepts Parsed in "+((System.currentTimeMillis() - startTime)/1000)+" seconds");
	    	}
    	}
    	
    	return validFile;
    }
    
    public void createJavaSkeleton() {
		if (!new File("javaSkeleton").isDirectory()) {
			new File("javaSkeleton").mkdir();
		}
		String ontoName = project.getOntoName();
		if (!new File("javaSkeleton/"+ontoName).isDirectory()) {
			new File("javaSkeleton/"+ontoName).mkdir();
		}
		
    	for (ClassRepresentation c : project.getOClasses().get(componentsPanel.getSelectedOntology())) {
    		try {
            	File javaFile = new File("javaSkeleton/"+ontoName+"/"+c.classname+".java");
        		if (javaFile.exists()) { javaFile.delete();}
        		javaFile.createNewFile();
        		
    	    	BufferedWriter out = new BufferedWriter(new FileWriter(javaFile,true));
    	    	
    	    	//Header
    	    	out.write("public class "+cleanName(c.classname.substring(0,1).toUpperCase()+c.classname.substring(1)));    	    	
    	    	if (c.parents.size() > 0) {  out.write(" "+ "extends "+cleanName(((String)c.parents.toArray()[0]).substring(0,1).toUpperCase()+((String)c.parents.toArray()[0]).substring(1)));}
    	    	out.write(" {\n");
    	    	
    	    	//Fields
    	    	for (String f : c.fields.keySet()) {
    	    		String type = Utilities.getCleanType((String)c.fields.get(f).toArray()[0]);
    	    		out.write("\tprivate "+type+" "+cleanName(f)+";\n");
    	    	}
    	    	
    	    	//Constructor
    	    	out.write("\n\tpublic "+c.classname.substring(0,1).toUpperCase()+c.classname.substring(1)+"(");
    	    	String constructor = "";
    	    	for (String f : c.fields.keySet()) {
    	    		String type = Utilities.getCleanType((String)c.fields.get(f).toArray()[0]);
    	    		constructor += type+" "+cleanName(f)+",";
    	    	}
    	    	if (!constructor.equals("")) { constructor = constructor.substring(0,constructor.length()-1);}
    	    	out.write(constructor+") {\n");
    	    	for (String f : c.fields.keySet()) { out.write("\t\tthis."+cleanName(f)+" = "+cleanName(f)+";\n");}
    	    	out.write("\t}\n\n");
    	    	
    	    	//Setters
    	    	for (String f : c.fields.keySet()) { 
    	    		String type = Utilities.getCleanType((String)c.fields.get(f).toArray()[0]);
    	    		out.write("\tpublic void set"+cleanName(f.substring(0,1).toUpperCase()+f.substring(1))+"("+type+" "+cleanName(f)+") {\n\t\tthis."+cleanName(f)+" = "+cleanName(f)+";\n\t}\n\n");
    	    	}
    	    	
    	    	//Getters
    	    	for (String f : c.fields.keySet()) { 
    	    		String type = Utilities.getCleanType((String)c.fields.get(f).toArray()[0]);
    	    		out.write("\tpublic "+type+" get"+cleanName(f.substring(0,1).toUpperCase()+f.substring(1))+"() {\n\t\treturn "+cleanName(f)+";\n\t}\n\n");
    	    	}
    	    	
    	    	out.write("}");

    		    out.close();
    		} catch (Exception e) {
    			e.printStackTrace();
    			System.err.println("Unable to create file");
    		}
    	}
    }
    
    public void createOntologySkeleton() {		
    	if (!new File("ontologySkeleton").isDirectory()) {
			new File("ontologySkeleton").mkdir();
		}
    	
		String javaName = project.getJavaName();
		man = OWLManager.createOWLOntologyManager();
		df = man.getOWLDataFactory(); 
		
		try {
			File output = new File("ontologySkeleton/"+javaName+".owl");
			IRI documentIRI = IRI.create(output);
	    	OWLOntology oSkel = man.createOntology(documentIRI);
	    	
	    	for (ClassRepresentation c : project.getJClasses()) {	    		 
	    		PrefixManager pm = new DefaultPrefixManager(documentIRI.toString()); 
	    		OWLClass cls = df.getOWLClass("#"+c.classname,pm); 
	    		OWLDeclarationAxiom declarationAxiom = df.getOWLDeclarationAxiom(cls);
	    		man.addAxiom(oSkel, declarationAxiom); 
	    		
	    		if (c.parents.size() > 0) {
	    			OWLClass clsP = df.getOWLClass("#"+((String)c.parents.toArray()[0]),pm); 
	    			OWLAxiom axiom = df.getOWLSubClassOfAxiom(cls, clsP); 
	    			AddAxiom addAxiom = new AddAxiom(oSkel, axiom); 
	    			man.applyChange(addAxiom); 	    			
	    		}
	    		
	    		for (String f : c.fields.keySet()) {
	    			try {
		    			OWLDataProperty field = df.getOWLDataProperty("#"+f,pm); 
		    			OWLDataPropertyDomainAxiom domainAssertation = df.getOWLDataPropertyDomainAxiom(field, cls); 
		    			
		    			OWLDatatype fieldType = null;
		    			switch ((String)c.fields.get(f).toArray()[0]) {
		    				case "int": fieldType = df.getOWLDatatype(OWL2Datatype.XSD_INT.getIRI()); break;
		    				case "float": fieldType = df.getOWLDatatype(OWL2Datatype.XSD_FLOAT.getIRI()); break;
		    				case "double": fieldType = df.getOWLDatatype(OWL2Datatype.XSD_DOUBLE.getIRI()); break;
		    				case "boolean": fieldType = df.getOWLDatatype(OWL2Datatype.XSD_BOOLEAN.getIRI()); break;
		    				case "string": fieldType = df.getOWLDatatype(OWL2Datatype.XSD_STRING.getIRI()); break;
		    				case "datetime": fieldType = df.getOWLDatatype(OWL2Datatype.XSD_DATE_TIME.getIRI()); break;
		    				default: fieldType = df.getOWLDatatype(OWL2Datatype.XSD_ANY_URI.getIRI()); break;
		    			}
		    			OWLDataPropertyRangeAxiom rangeAssertation = df.getOWLDataPropertyRangeAxiom(field,fieldType);
		    			
		    			AddAxiom addDomainAxiomChange = new AddAxiom(oSkel, domainAssertation); 
		    			AddAxiom addRangeAxiomChange = new AddAxiom(oSkel, rangeAssertation); 
		    			man.applyChange(addDomainAxiomChange); 
		    			man.applyChange(addRangeAxiomChange); 
		    			
		    			if (c.classname.startsWith("wml")) {
			    			System.out.println(c.classname+": "+f);
			    			System.out.println("\t"+cls.getIRI().getFragment().toLowerCase());
			    			System.out.println("\t"+field.getIRI().getFragment().toLowerCase());
			    			System.out.println("\tDomain Assertation:");
			    			System.out.println("\t\t"+((OWLClass) domainAssertation.getClassesInSignature().toArray()[0]).getIRI().getFragment().toLowerCase());
			    			System.out.println("\t\t"+((OWLDataProperty) domainAssertation.getDataPropertiesInSignature().toArray()[0]).getIRI().getFragment().toLowerCase());
			    			System.out.println("\tRange Assertation:");
			    			System.out.println("\t\t"+((OWLDataProperty) rangeAssertation.getDataPropertiesInSignature().toArray()[0]).getIRI().getFragment().toLowerCase());
			    			System.out.println("\t\t"+(rangeAssertation.getRange().getDatatypesInSignature().toArray())[0].toString().toLowerCase());
		    			}
	    			} catch (Exception e) {
	    				System.err.println("Unable to create field");
	    			}
	    		}
	    	}
	    	
	    	man.saveOntology(oSkel);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to create file");
		}
    }
    
    public String cleanName(String input) {
    	boolean found = false; for (String kw : Cons.javaKeywords) { if (kw.equals(input.toLowerCase())) { found = true; break;}}
    	if (found) { input = "ont_"+input;}
    	return input;
    }
	
	public void updatePanels() {
		mainController.updatePanels();
	}
    
    public Project getProject() {
    	return project;
    }

	public ComponentsPanel getComponentsPanel() {
		return componentsPanel;
	}
}
