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
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.compress.compressors.CompressorException;
import org.xml.sax.SAXException;
import de.l3s.loaders.DataLoaders;
import de.l3s.loaders.DataLoaders_AQUAINT;
import de.l3s.loaders.DataLoaders_CONLL;
import de.l3s.loaders.DataLoaders_GERDAQ;
import de.l3s.loaders.DataLoaders_IITB;
import de.l3s.loaders.DataLoaders_MSNBC;
import de.l3s.loaders.DataLoaders_NEEL;
import de.l3s.loaders.DataLoaders_WP;

public class MentionDifficulty_Calculator {
	
	public static TreeMap<String,String> GT_MAP_train = null;
	public static TreeMap<String,  String> ambiverseMap_train = null;
	public static TreeMap<String,  String> babelMap_train = null;
	public static TreeMap<String,  String> tagmeMap_train = null;

	public static void main(String[] args) throws IOException, CompressorException, NumberFormatException, SAXException, ParserConfigurationException{
//		String[] corpus = new String[]{"conll", "iitb", "wp",  "neel", "gerdaq"};
// 		String corpus = "conll";   //Babelfy > Ambiverse > Spotlight >  Tagme
 //		String corpus = "iitb";    //Ambiverse > Spotlight >  Tagme > Babelfy
 	String corpus = "wp";      // Tagme > Babelfy > Ambiverse > Spotlight
//  		String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
// 		String corpus = "gerdaq";  // Tagme > Babelfy > Ambiverse 
//  		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
// 		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy

		DataLoaders d = new DataLoaders();
		
		if(corpus.equalsIgnoreCase("conll")){
			d = DataLoaders_CONLL.getInstance();
		}
		if(corpus.equalsIgnoreCase("iitb")){
			d = DataLoaders_IITB.getInstance();
		}
		if(corpus.equalsIgnoreCase("wp")){
			d = DataLoaders_WP.getInstance();
		}
		if(corpus.equalsIgnoreCase("neel")){
			d = DataLoaders_NEEL.getInstance();
		}
		if(corpus.equalsIgnoreCase("gerdaq")){
			d = DataLoaders_GERDAQ.getInstance();
		}
		if(corpus.equalsIgnoreCase("aquaint")){
			d = DataLoaders_AQUAINT.getInstance();
		}
		if(corpus.equalsIgnoreCase("msnbc")){
			d = DataLoaders_MSNBC.getInstance();
		}
	
		train(d,corpus);
//		test();
	}
	
	 
	
	public static void train(DataLoaders d, String corpus) throws IOException, CompressorException {
		
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/"+corpus+"/mention_correct.train.tsv"),StandardCharsets.UTF_8);
		
		GT_MAP_train = new TreeMap<String, String>();

		ambiverseMap_train = new TreeMap<String, String>();
		babelMap_train = new TreeMap<String, String>();
		tagmeMap_train = new TreeMap<String, String>();
		
		GT_MAP_train = d.getGT_MAP_train();
		
		ambiverseMap_train = d.getAmbiverseMap_train();
		babelMap_train = d.getBabelMap_train();
		tagmeMap_train = d.getTagmeMap_train();
		
		TreeMap<String, Double> CorrectAmbMap = new TreeMap<String, Double>();
		TreeMap<String, Double> CorrectBabMap = new TreeMap<String, Double>();
		TreeMap<String, Double> CorrectTagMap = new TreeMap<String, Double>();

		TreeMap<String, Double> TotalAmbMap = new TreeMap<String, Double>();
		TreeMap<String, Double> TotalBabMap = new TreeMap<String, Double>();
		TreeMap<String, Double> TotalTagMap = new TreeMap<String, Double>();
		
		BufferedReader bffReaderAmbiverse = null;
		BufferedReader bffReaderBabelfy = null;
		BufferedReader bffReaderTagme = null;
		
		BufferedReader bffReader = null;
		
		if(corpus.equalsIgnoreCase("conll")){
			bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_ambiverse_train.mappings"),StandardCharsets.UTF_8));
			bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_bfy_train.mappings"),StandardCharsets.UTF_8));
			bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_tagme_train.mappings"),StandardCharsets.UTF_8));
		}
		if (corpus.equalsIgnoreCase("iitb")){
			bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb_ambiverse.9.mappings"),StandardCharsets.UTF_8));
			bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb_bfy.9.mappings"),StandardCharsets.UTF_8));
			bffReaderTagme  =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb_tagme.9.mappings"),StandardCharsets.UTF_8));
			bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/iitb_GT_NONIL.tsv"),StandardCharsets.UTF_8));
		}
		if(corpus.equalsIgnoreCase("wp")){
			bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/mappings/wp_ambiverse.9.mappings"),StandardCharsets.UTF_8));
			bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/mappings/wp_bfy.9.mappings"),StandardCharsets.UTF_8));
			bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/mappings/wp_tagme.9.mappings"),StandardCharsets.UTF_8));
			bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/WP_GT.train.9.tsv"),StandardCharsets.UTF_8));
		}
		if(corpus.equalsIgnoreCase("neel")){
			bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/mappings/NEEL2016-training_ambiverse.mappings"),StandardCharsets.UTF_8));
			bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/mappings/NEEL2016-training_bfy.mappings"),StandardCharsets.UTF_8));
			bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/mappings/NEEL2016-training_tagme.mappings"),StandardCharsets.UTF_8));
			bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/NEEL2016-train_NONNIL.gt"),StandardCharsets.UTF_8));
		}
		if(corpus.equalsIgnoreCase("gerdaq")){
			bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/mappings/gerdaq_ambiverse_train.mappings"),StandardCharsets.UTF_8));
			bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/mappings/gerdaq_bfy_train.mappings"),StandardCharsets.UTF_8));
			bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/mappings/gerdaq_tagme_train.mappings"),StandardCharsets.UTF_8));
			bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/gerdaq_training.GT.tsv"),StandardCharsets.UTF_8));
		}
		if(corpus.equalsIgnoreCase("aquaint")){
			bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/aquaint/mappings/AQUAINT_ambiverse_train.mappings"),StandardCharsets.UTF_8));
			bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/aquaint/mappings/AQUAINT_bfy_train.mappings"),StandardCharsets.UTF_8));
			bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/aquaint/mappings/AQUAINT_tagme_train.mappings"),StandardCharsets.UTF_8));
			bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/aquaint/AQUAINT_GT.train.9.tsv"),StandardCharsets.UTF_8));
		}
		if(corpus.equalsIgnoreCase("msnbc")){
			bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/msnbc/mappings/MSNBC_ambiverse_train.mappings"),StandardCharsets.UTF_8));
			bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/msnbc/mappings/MSNBC_bfy_train.mappings"),StandardCharsets.UTF_8));
			bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/msnbc/mappings/MSNBC_tagme_train.mappings"),StandardCharsets.UTF_8));
			bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/msnbc/MSNBC_GT_train.tsv"),StandardCharsets.UTF_8));

		}
		

		

		String line="";
		while ((line = bffReaderAmbiverse.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length >=4){
				String docId = elems[0].toLowerCase();
				if(corpus.equalsIgnoreCase("gerdaq")){
					docId = docId.replaceAll(" ", "_");
				}
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
				String offset =  elems[2];
				String entity = elems[3].toLowerCase();
				if(!entity.equalsIgnoreCase("null")){
					entity = entity.replaceAll("_"," ").toLowerCase();
					ambiverseMap_train.put(docId+"\t"+mention+"\t"+offset,entity);
					double count = 1;
					if(TotalAmbMap.containsKey(mention)){
						count = TotalAmbMap.get(mention);
						count+=1;
					}
					TotalAmbMap.put(mention, count);
				}
			}

		}
		System.out.println("Amb TRAIN annotations :"+ambiverseMap_train.keySet().size());

		line="";
		while ((line = bffReaderBabelfy.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length >=4){
				String docId = elems[0].toLowerCase();
				if(corpus.equalsIgnoreCase("gerdaq")){
					docId = docId.replaceAll(" ", "_");
				}
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
				String offset =  elems[2];
				String entity = elems[3].toLowerCase();
				if(!entity.equalsIgnoreCase("null")){
					entity = entity.replaceAll("_"," ").toLowerCase();
					babelMap_train.put(docId+"\t"+mention+"\t"+offset,entity);
					double count = 1;
					if(TotalBabMap.containsKey(mention)){
						count = TotalBabMap.get(mention);
						count+=1;
					}
					TotalBabMap.put(mention, count);
				}
			}
		}
		System.out.println("Bab TRAIN annotations :"+babelMap_train.keySet().size());

		line="";
		while ((line = bffReaderTagme.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length >=4){
				String docId = elems[0].toLowerCase();
				if(corpus.equalsIgnoreCase("gerdaq")){
					docId = docId.replaceAll(" ", "_");
				}
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
				docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
				String offset =  elems[2];
				String entity = elems[3].toLowerCase();
				if(!entity.equalsIgnoreCase("null")){
					entity = entity.replaceAll("_"," ").toLowerCase();
					tagmeMap_train.put(docId+"\t"+mention+"\t"+offset,entity);	
					double count = 1;
					if(TotalTagMap.containsKey(mention)){
						count = TotalTagMap.get(mention);
						count+=1;
					}
					TotalTagMap.put(mention, count);
				}
			}
		}
		System.out.println("Tag TRAIN annotations :"+tagmeMap_train.keySet().size());
		
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();

		
		
		
	
		line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			if(corpus.equalsIgnoreCase("gerdaq")){
				docId = docId.replaceAll(" ", "_");
			}
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String mention = elems[1].toLowerCase();
			String offset = elems[2];
			String link = elems[3];
			String GTlink = elems[3];
			GTlink = link.replaceAll("_"," ").toLowerCase();
//			
		
			if(GTlink.contains("null")){
				continue;
			}else{
				String key = docId + "\t" + mention + "\t" + offset;
				String Alink = "NULL";
				String Blink = "NULL";
				String Tlink = "NULL";

				if(ambiverseMap_train.containsKey(key)){	
					Alink = ambiverseMap_train.get(key);	
				}
				if(babelMap_train.containsKey(key)){ 		
					Blink = babelMap_train.get(key);		
				}
				if(tagmeMap_train.containsKey(key)){		
					Tlink = tagmeMap_train.get(key);		
				}
////				
//			System.out.println(GT_MAP_train.size());
//			System.out.println(ambiverseMap_train.size());
//			System.out.println(babelMap_train.size());
//			System.out.println(tagmeMap_train.size());
			
			double count;
//
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
////        
////        
////        
       HashSet<String> union = new HashSet<String>(cAmbMap);
       union.addAll(cBabMap);
       union.addAll(cTagMap);
       union.addAll(tAmbMap);
       union.addAll(tBabMap);
       union.addAll(tTagMap);
////		
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
////			
			if (CorrectAmbMap.get(s) != null ){
				a = CorrectAmbMap.get(s);
			}
////			
			if (CorrectBabMap.get(s) != null ){
				b = CorrectBabMap.get(s);
			}
////			
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
////			
		}
		bffReader.close();
		pAnn.flush();
		pAnn.close();
		System.out.println("Finished calculating S_corr and S_ratio.");

	}
}
