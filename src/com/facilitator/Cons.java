package com.facilitator;

import java.awt.Color;

public final class Cons {
	public static boolean printResults = false; //Whether to print messages to the terminal
	public static final String[] javaKeywords = {		"abstract",	"assert",	"boolean",	"break",	"byte",		"case",		"catch",	"char",		"class",	"const",	"continue",	"default",	"do",		"double",	"else",		"enum",		"extends",	"final",	"finally",	"float",	"for",		"goto",		"if",		"implements",	"import",	"instanceof",	"int",		"interface",	"long",		"native",	"new",		"package",	"private",	"protected",	"public",	"return",	"short",	"static",	"strictfp",	"super",	"switch",	"synchronized",	"this",		"throw",	"throws",	"transient",	"try",		"void",		"volatile",	"while"};
	public static final String[] javaKeywordColours = {	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",		"#7F0055",	"#7F0055",		"#7F0055",	"#7F0055",		"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",		"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",		"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055",		"#7F0055",	"#7F0055",	"#7F0055",	"#7F0055"};
	
	public static final boolean presetFiles = true; //Whether to load preset files. Change locations below	
		//public static final String presetJavaPath = "C:/Users/Sam/Dropbox/Research/Java/Xerces-J-src.2.11.0";
		//public static final String presetJavaPath = "C:/Users/Sam/Dropbox/TDWB/dist-5.0/src";
		public static final String presetJavaPath = "C:/Users/Sam/Dropbox/Research/CupsAdvanced";
		//public static final String presetJavaPath = "C:/Users/Sam/Dropbox/Research/Java/InheritanceTest";
		
		//public static final String presetOntoPath = "C:/Users/Sam/Dropbox/Research/Facilitator/ontologySkeleton/Xerces-J-src.2.11.0.owl";
		public static final String presetOntoPath = "C:/Users/Sam/Dropbox/Research/CupsAdvanced";
	
	//Component Colours
	public static final Color classColour = new Color(150,255,150);
	public static final Color fieldColour = new Color(255,255,150);
	public static final Color infFieldColour = new Color(255,150,150);
}