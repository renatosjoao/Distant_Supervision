package de.l3s.extra;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

public class tests {
 
	

	public static void main(String[] args) throws IOException{
	TreeMap<String,String> GT_MAP_test = new TreeMap<String, String>();

		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/WP_GT.test.1.tsv"),StandardCharsets.UTF_8));
		String line ;
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String mention = elems[1].toLowerCase();
//		    mention = mention.replaceAll("\'", "");
//		    mention = mention.replaceAll("\"", "");
			String offset = elems[2];
			String link = elems[3].toLowerCase();
			String key = docId+"\t"+mention+"\t"+offset;
			String value = link.toLowerCase();
			GT_MAP_test.put(key, value);
		}
		bffReader.close();
		System.out.println("TEST GT :"+GT_MAP_test.keySet().size());
		
	}
}
