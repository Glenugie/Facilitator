package com.facilitator.utils;

import org.tartarus.snowball.EnglishSnowballStemmerFactory;

public class Utilities {
    public static String stem(String word) {
		String wStemmed = "";
		try {
			wStemmed = EnglishSnowballStemmerFactory.getInstance().process(word);
		} catch (Exception e) {
			System.err.println("Failed to stem "+word);
		}
		return wStemmed;
    }

    public static String getCleanType(String f) {
		String type = "";
		if (f.equals("byte") || f.equals("short") || f.equals("int") || f.equals("long") || f.equals("float") || f.equals("double") || f.equals("boolean") || f.equals("char")) {
			type = f;
		} else {
			type = f.substring(0,1).toUpperCase()+f.substring(1);
		}
		return type;
    }
}
