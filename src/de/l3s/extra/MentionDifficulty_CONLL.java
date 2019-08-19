package de.l3s.extra;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;


public class MentionDifficulty_CONLL {
		public MentionDifficulty_CONLL(){
		
	}
		
		
	public static void main(String[] args) throws IOException{
		train();
//		test();
	}
	/**
	 *
	 * 
	 * @throws IOException
	 */
	public static void train() throws IOException {
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/conll/mention_correct.train.tsv"),StandardCharsets.UTF_8);
		TreeMap<String, Double> CorrectAmbMap = new TreeMap<String, Double>();
		TreeMap<String, Double> CorrectBabMap = new TreeMap<String, Double>();
		TreeMap<String, Double> CorrectTagMap = new TreeMap<String, Double>();
		TreeMap<String, Double> CorrectSpotMap = new TreeMap<String, Double>();

		TreeMap<String, Double> TotalAmbMap = new TreeMap<String, Double>();
		TreeMap<String, Double> TotalBabMap = new TreeMap<String, Double>();
		TreeMap<String, Double> TotalTagMap = new TreeMap<String, Double>();
		TreeMap<String, Double> TotalSpotMap = new TreeMap<String, Double>();

		// Here I am initially loading only the training part of the original GT
		TreeMap<String, String> GTMAP = new TreeMap<String, String>();
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/conllYAGO_train_GT.tsv"),StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docid = elems[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			String mention = elems[1].toLowerCase();
			String offset = elems[2];
			String link = elems[3].toLowerCase();
			String key = docid + "\t" + mention + "\t" + offset;
			String value = link.toLowerCase();
			GTMAP.put(key, value);
		}
		bffReader.close();
		System.out.println("Number of annotations in the training set [ GT ]  :"+GTMAP.keySet().size());
		System.out.println();
		
		// now I will load the mappings created by each of the EL tools.
		
		TreeMap<String, String> ambiverseMap = new TreeMap<String, String>();
		TreeMap<String, String> babelMap = new TreeMap<String, String>();
		TreeMap<String, String> tagmeMap = new TreeMap<String, String>();
		TreeMap<String, String> spotLightMap = new TreeMap<String, String>();

		BufferedReader bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_ambiverse_all.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_bfy_all.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderTagme = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_tagme_all.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderSpotlight = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_tagme_all.mappings"),StandardCharsets.UTF_8));

		line = "";

		while ((line = bffReaderAmbiverse.readLine()) != null) {
			String[] elements = line.split("\t");
			if (elements.length >= 4) {
				String docid = elements[0].toLowerCase();
				docid = docid.replaceAll("\'", "");
				docid = docid.replaceAll("\"", "");
				String mention = elements[1].toLowerCase();
				String offset = elements[2];
				String entity = elements[3].toLowerCase();
				entity = entity.replaceAll("_", " ");
				ambiverseMap.put(docid + "\t" + mention + "\t" + offset, entity);
				
				double count = 1;
				if(TotalAmbMap.containsKey(mention)){
					count = TotalAmbMap.get(mention);
					count+=1;
				}
				TotalAmbMap.put(mention, count);
			}
		}
		System.out.println("...Loaded Mappings from Ambiverse Successfully.");
		System.out.println("	...Total number of annotations created by Ambiverse : "+TotalAmbMap.keySet().size());

		
		line = "";
		while ((line = bffReaderBabelfy.readLine()) != null) {
			String[] elements = line.split("\t");
			if (elements.length >= 4) {
				String docid = elements[0].toLowerCase();
				docid = docid.replaceAll("\'", "");
				docid = docid.replaceAll("\"", "");
				String mention = elements[1].toLowerCase();
				String offset = elements[2];
				String entity = elements[3].toLowerCase();
				entity = entity.replaceAll("_", " ");
				babelMap.put(docid + "\t" + mention + "\t" + offset, entity);
				
				double count = 1;
				if(TotalBabMap.containsKey(mention)){
					count = TotalBabMap.get(mention);
					count+=1;
				}
				TotalBabMap.put(mention, count);
//				
				
			}
		}

		System.out.println("...Loaded Mappings from Babelfy Successfully.");
		System.out.println("... Number of annotations created by Babelfy : "+TotalBabMap.keySet().size());

		
		
//		for (String name: babelMap.keySet()){
//			String key =name.toString();
//			String value = babelMap.get(name).toString();  
//			if (key.contains("1208")){
//				System.out.println(key + " " + value);
//			}
//		} 
//		
		line = "";
		while ((line = bffReaderTagme.readLine()) != null) {
			String[] elements = line.split("\t");
			if (elements.length >= 4) {
				String docid = elements[0].toLowerCase();
				docid = docid.replaceAll("\'", "");
				docid = docid.replaceAll("\"", "");
				docid = docid.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
				String mention = elements[1].toLowerCase();
				String offset = elements[2];
				String entity = elements[3].toLowerCase();
				entity = entity.replaceAll("_", " ");
				tagmeMap.put(docid + "\t" + mention + "\t" + offset, entity);
				
				double count = 1;
				if(TotalTagMap.containsKey(mention)){
					count = TotalTagMap.get(mention);
					count+=1;
				}
				TotalTagMap.put(mention, count);
			}
		}
		System.out.println("...Loaded Mappings from Tagme Successfully.");
		System.out.println("	... Number of annotations created by Tagme : "+TotalTagMap.keySet().size());
		
		
		line = "";
		while ((line = bffReaderSpotlight.readLine()) != null) {
			String[] elements = line.split("\t");
			if (elements.length >= 4) {
				String docid = elements[0].toLowerCase();
				docid = docid.replaceAll("\'", "");
				docid = docid.replaceAll("\"", "");
				//docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
				String mention = elements[1].toLowerCase();
				String offset = elements[2];
				String entity = elements[3].toLowerCase();
				entity = entity.replaceAll("_", " ");
				spotLightMap.put(docid + "\t" + mention + "\t" + offset, entity);
				
				double count = 1;
				if(TotalSpotMap.containsKey(mention)){
					count = TotalSpotMap.get(mention);
					count+=1;
				}
				TotalSpotMap.put(mention, count);
			}
		}
		System.out.println("...Loaded Mappings from SpotLight Successfully.");
	
		
		
		bffReaderAmbiverse.close();
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderSpotlight.close();

		
		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/conllYAGO_train_GT.tsv"),StandardCharsets.UTF_8));
		line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elements = line.split("\t");
			String docid = elements[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			String mention = elements[1].toLowerCase();
			String offset = elements[2];
			String GTlink = elements[3].toLowerCase();
			
			if(GTlink.equalsIgnoreCase("--NME--")){
				continue;
			}else{
				String key = docid + "\t" + mention + "\t" + offset;
				String Alink = "";
				String Blink = "";
				String Tlink = "";
				String Slink = "";
								
				
				if(ambiverseMap.containsKey(key)){	
					Alink = ambiverseMap.get(key);	
				
				}else{
					Alink = "NULL";}
				
				if(babelMap.containsKey(key)){ 		
					Blink = babelMap.get(key);		
				}else{ 
					Blink = "NULL";}
				
				if(tagmeMap.containsKey(key)){		
					Tlink = tagmeMap.get(key);		
				}else{ 
					Tlink = "NULL";}
				
				if(spotLightMap.containsKey(key)){		
					Slink = spotLightMap.get(key);		
				}else{ 
					Slink = "NULL";}

//				
///				if (key.contains("piacenza")){
////					System.out.println(key + " GT:" + GTlink + " Amb:" +Alink + " Bfy:" +Blink + " Tag:" +Tlink);
////				
////				}
////				
			double count;

			//Ambiverse
			if (Alink.equalsIgnoreCase(GTlink)){
				count = 1;
				if(CorrectAmbMap.containsKey(mention)){
					count = CorrectAmbMap.get(mention);
					count+=1;
				}
				CorrectAmbMap.put(mention, count);
			}
			//Babelfy
			if (Blink.equalsIgnoreCase(GTlink)){
				count = 1;
				if(CorrectBabMap.containsKey(mention)){
					count = CorrectBabMap.get(mention);
					count+=1;
				}
				CorrectBabMap.put(mention, count);
			}
			///Tagme
			if (Tlink.equalsIgnoreCase(GTlink)){
				count = 1;
				if(CorrectTagMap.containsKey(mention)){
					count = CorrectTagMap.get(mention);
					count+=1;
				}
				CorrectTagMap.put(mention, count);
			}
			
			//SpotLight
			if (Slink.equalsIgnoreCase(GTlink)){
				count = 1;
				if(CorrectSpotMap.containsKey(mention)){
					count = CorrectSpotMap.get(mention);
					count+=1;
				}
				CorrectSpotMap.put(mention, count);
			}
			
			
			
			
			}
		}
		
		

		Set<String> cAmbMap = CorrectAmbMap.keySet();
		Set<String> cBabMap = CorrectBabMap.keySet();
        Set<String> cTagMap = CorrectTagMap.keySet();
        Set<String> cSpotMap = CorrectSpotMap.keySet();
        Set<String> tAmbMap = TotalAmbMap.keySet();
        Set<String> tBabMap = TotalBabMap.keySet();
        Set<String> tTagMap = TotalTagMap.keySet();
        Set<String> tSpotMap = TotalSpotMap.keySet();
        
       
       HashSet<String> union = new HashSet<String>(cAmbMap);
       union.addAll(cBabMap);
       union.addAll(cTagMap);
       union.addAll(tAmbMap);
       union.addAll(tBabMap);
       union.addAll(tTagMap);
//		
		for(String s :union){
			Double a = 0.0;
			Double b = 0.0;
			Double t = 0.0;
			Double totA = 0.0;
			Double totB = 0.0;
			Double totT = 0.0; 
			Double normA = 0.0;
			Double normB = 0.0;
			Double normT = 0.0 ;
//			
			if (CorrectAmbMap.get(s) != null ){
				a = CorrectAmbMap.get(s);
			}
//			
			if (CorrectBabMap.get(s) != null ){
				b = CorrectBabMap.get(s);
			}
//			
			if (CorrectTagMap.get(s) != null ){
				t = CorrectTagMap.get(s);
			}
//			
			if (TotalAmbMap.get(s) != null ){
				totA = TotalAmbMap.get(s);
			}
			if (TotalBabMap.get(s) != null ){
				totB = TotalBabMap.get(s);
			}
//			
			if (TotalTagMap.get(s) != null ){
				totT = TotalTagMap.get(s);
			}
			if ((a == 0 ) &&  (totA == 0)){
				normA = 0.0;
			}else{
				normA = a/totA;
			}
			if ((b == 0 ) &&  (totB == 0)){
				normB = 0.0;
			}else{
				normB = b/totB;
			}
			if ((t == 0 ) &&  (totT == 0)){
				normT = 0.0;
			}else{
				normT = t/totT;
			}
			
			
			DecimalFormat dec = new DecimalFormat("#0.00");
			pAnn.write(s+"\t"+a+"\t"+b+"\t"+t +"\t"+totA+"\t"+totB+"\t"+totT+"\t"+dec.format(normA)+"\t"+dec.format(normB)+"\t"+dec.format(normT)+"\n");
		}
		
		pAnn.flush();
		pAnn.close();
		System.out.println("Finished calculating s_corr and s_ratio.");

	}
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * 
	 * 
	 */
	
	
	
	public static void test() throws IOException {
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/conll/mention_correct.test.csv"),StandardCharsets.UTF_8);
		TreeMap<String, Double> CorrectAmbMap = new TreeMap<String, Double>();
		TreeMap<String, Double> CorrectBabMap = new TreeMap<String, Double>();
		TreeMap<String, Double> CorrectTagMap = new TreeMap<String, Double>();

		TreeMap<String, Double> TotalAmbMap = new TreeMap<String, Double>();
		TreeMap<String, Double> TotalBabMap = new TreeMap<String, Double>();
		TreeMap<String, Double> TotalTagMap = new TreeMap<String, Double>();

		// initially loading the GT
		TreeMap<String, String> GTMAP = new TreeMap<String, String>();
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/conllYAGO_testb_GT.tsv"),StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0];
			String mention = elems[1];
			String offset = elems[2];
			String link = elems[3];
			String key = docId + "\t" + mention + "\t" + offset;
			String value = link.toLowerCase();
			GTMAP.put(key, value);
		}
		bffReader.close();
		System.out.println("...Loaded Mappings from Ambiverse Successfully.");

//		for (String name: GTMAP.keySet()){
//
//            String key =name.toString();
//            String value = GTMAP.get(name).toString();  
//            if (key.contains("1208")){
//            	System.out.println(key + " " + value);
//            }
//
//
//		} 
		// now I will load the mappings
		TreeMap<String, String> ambiverseMap = new TreeMap<String, String>();
		TreeMap<String, String> babelMap = new TreeMap<String, String>();
		TreeMap<String, String> tagmeMap = new TreeMap<String, String>();

		BufferedReader bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_ambiverse_all.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_bfy_all.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderTagme = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_tagme_all.mappings"),StandardCharsets.UTF_8));

		line = "";

		while ((line = bffReaderAmbiverse.readLine()) != null) {
			String[] elements = line.split("\t");
			if (elements.length >= 4) {
				String docId = elements[0];
				String mention = elements[1];
				String offset = elements[2];
				String entity = elements[3];
				entity = entity.replaceAll("_", " ");
				ambiverseMap.put(docId + "\t" + mention + "\t" + offset, entity);
			}
		}
		System.out.println("...Loaded Mappings from Ambiverse Successfully.");
//		
//		for (String name: ambiverseMap.keySet()){
//			String key =name.toString();
//			String value = ambiverseMap.get(name).toString();  
//			if (key.contains("1208")){
//				System.out.println(key + " " + value);
//			  }
//		} 
//		
		line = "";
		while ((line = bffReaderBabelfy.readLine()) != null) {
			String[] elements = line.split("\t");
			if (elements.length >= 4) {
				String docId = elements[0];
				String mention = elements[1];
				String offset = elements[2];
				String entity = elements[3];
				entity = entity.replaceAll("_", " ");
				babelMap.put(docId + "\t" + mention + "\t" + offset, entity);
			}
		}

		System.out.println("...Loaded Mappings from Babelfy Successfully.");

		
		
//		for (String name: babelMap.keySet()){
//			String key =name.toString();
//			String value = babelMap.get(name).toString();  
//			if (key.contains("1208")){
//				System.out.println(key + " " + value);
//			}
//		} 
//		
		line = "";
		while ((line = bffReaderTagme.readLine()) != null) {
			String[] elements = line.split("\t");
			if (elements.length >= 4) {
				String docId = elements[0];
				docId = docId.replace(
						"http://query.nytimes.com/gst/fullpage.html?res=", "");
				String mention = elements[1];
				String offset = elements[2];
				String entity = elements[3];
				entity = entity.replaceAll("_", " ");
				tagmeMap.put(docId + "\t" + mention + "\t" + offset, entity);
			}
		}
		System.out.println("...Loaded Mappings from Tagme Successfully.");
		
//		for (String name: tagmeMap.keySet()){
//		
//			String key =name.toString();
//			String value = tagmeMap.get(name).toString();  
//			if (key.contains("1208")){
//				System.out.println(key + " " + value);
//			    }
//		} 
	
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();

		
		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/conllYAGO_testb_GT.tsv"),StandardCharsets.UTF_8));
		line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0];
			String mention = elems[1];
			String offset = elems[2];
			String GTlink = elems[3];
			
			if(GTlink.equalsIgnoreCase("--NME--")){
				continue;
			}else{
				String key = docId + "\t" + mention + "\t" + offset;
				String Alink = "";
				String Blink = "";
				String Tlink = "";
								
				
				if(ambiverseMap.containsKey(key)){	
					Alink = ambiverseMap.get(key);	
				
				}else{
					Alink = "NULL";}
				
				if(babelMap.containsKey(key)){ 		
					Blink = babelMap.get(key);		
				}else{ 
					Blink = "NULL";}
				
				if(tagmeMap.containsKey(key)){		
					Tlink = tagmeMap.get(key);		
				}else{ 
					Tlink = "NULL";}
//				
//				if (key.contains("piacenza")){
//					System.out.println(key + " GT:" + GTlink + " Amb:" +Alink + " Bfy:" +Blink + " Tag:" +Tlink);
//				
//				}
//				
			
			double count = 1;
			if(TotalAmbMap.containsKey(mention)){
				count = TotalAmbMap.get(mention);
				count+=1;
			}
			TotalAmbMap.put(mention, count);
//			
			count = 1;
			if(TotalBabMap.containsKey(mention)){
				count = TotalBabMap.get(mention);
				count+=1;
			}
			TotalBabMap.put(mention, count);
//			
			count = 1;
			if(TotalTagMap.containsKey(mention)){
				count = TotalTagMap.get(mention);
				count+=1;
			}
			TotalTagMap.put(mention, count);
			
			
			
			if (Alink.equalsIgnoreCase(GTlink)){
				count = 1;
				if(CorrectAmbMap.containsKey(mention)){
					count = CorrectAmbMap.get(mention);
					count+=1;
				}
				CorrectAmbMap.put(mention, count);
			}
			if (Blink.equalsIgnoreCase(GTlink)){
				count = 1;
				if(CorrectBabMap.containsKey(mention)){
					count = CorrectBabMap.get(mention);
					count+=1;
				}
				CorrectBabMap.put(mention, count);
			}
			if (Tlink.equalsIgnoreCase(GTlink)){
				count = 1;
				if(CorrectTagMap.containsKey(mention)){
					count = CorrectTagMap.get(mention);
					count+=1;
				}
				CorrectTagMap.put(mention, count);
			}
			
			}
		}
		
		

		Set<String> cAmbMap = CorrectAmbMap.keySet();
        Set<String> cBabMap = CorrectBabMap.keySet();
        Set<String> cTagMap = CorrectTagMap.keySet();
        Set<String> tAmbMap = TotalAmbMap.keySet();
        Set<String> tBabMap = TotalBabMap.keySet();
        Set<String> tTagMap = TotalTagMap.keySet();
//        
//        
//        
       HashSet<String> union = new HashSet<String>(cAmbMap);
       union.addAll(cBabMap);
       union.addAll(cTagMap);
       union.addAll(tAmbMap);
       union.addAll(tBabMap);
       union.addAll(tTagMap);
//        
////		System.out.println(CorrectAmbMap.size());
////		System.out.println(CorrectBabMap.size());
////		System.out.println(CorrectTagMap.size());
////		
////		System.out.println();
////
////		System.out.println(TotalAmbMap.size());
////		System.out.println(TotalBabMap.size());
////		System.out.println(TotalTagMap.size());
//		
		for(String s :union){
			Double a = 0.0;
			Double b = 0.0;
			Double t = 0.0;
			Double totA = 0.0;
			Double totB = 0.0;
			Double totT = 0.0; 
			Double normA = 0.0;
			Double normB = 0.0;
			Double normT = 0.0 ;
//			
			if (CorrectAmbMap.get(s) != null ){
				a = CorrectAmbMap.get(s);
			}
//			
			if (CorrectBabMap.get(s) != null ){
				b = CorrectBabMap.get(s);
			}
//			
			if (CorrectTagMap.get(s) != null ){
				t = CorrectTagMap.get(s);
			}
//			
			if (TotalAmbMap.get(s) != null ){
				totA = TotalAmbMap.get(s);
			}
			if (TotalBabMap.get(s) != null ){
				totB = TotalBabMap.get(s);
			}
//			
			if (TotalTagMap.get(s) != null ){
				totT = TotalTagMap.get(s);
			}
			if ((a == 0 ) &&  (totA == 0)){
				normA = 0.0;
			}else{
				normA = a/totA;
			}
			if ((b == 0 ) &&  (totB == 0)){
				normB = 0.0;
			}else{
				normB = b/totB;
			}
			if ((t == 0 ) &&  (totT == 0)){
				normT = 0.0;
			}else{
				normT = t/totT;
			}
			
			DecimalFormat dec = new DecimalFormat("#0.00");
			pAnn.write(s+"\t"+a+"\t"+b+"\t"+t +"\t"+totA+"\t"+totB+"\t"+totT+"\t"+dec.format(normA)+"\t"+dec.format(normB)+"\t"+dec.format(normT)+"\n");
		
		}
		pAnn.flush();
		pAnn.close();
		
		System.out.println("Finished calculating n_corr and n_ratio.");

	}
}
