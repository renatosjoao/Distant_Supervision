package de.l3s.baselines;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.compress.compressors.CompressorException;
import org.xml.sax.SAXException;

import com.opencsv.CSVReader;

import de.l3s.loaders.DataLoaders;
import de.l3s.loaders.DataLoaders_AQUAINT;
import de.l3s.loaders.DataLoaders_CONLL;
import de.l3s.loaders.DataLoaders_GERDAQ;
import de.l3s.loaders.DataLoaders_IITB;
import de.l3s.loaders.DataLoaders_MSNBC;
import de.l3s.loaders.DataLoaders_NEEL;
import de.l3s.loaders.DataLoaders_WP;

public class UpperLimit {


	public static void main(String[] args) throws Exception{
//		String corpus = "ace04";
//		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
		String corpus = "conll";   //Babelfy > Ambiverse > Spotlight >  Tagme
//		String corpus = "gerdaq";  // Tagme > Babelfy > Ambiverse 
//		String corpus = "iitb";    //Ambiverse > Spotlight >  Tagme > Babelfy
//		String corpus = "kore50";
// 		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
//	    String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
//		String corpus = "reuters128";   
// 		String corpus = "wp";      // Tagme > Babelfy > Ambiverse > Spotlight
		
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
		
//		Upper_Limit_MetaEL(d,corpus);
// 		upperBound(d,corpus,"vii");
		Upper_limit_MetaEL_STRICT_LOOSE(d,corpus);
//	 	UpperLimitEXTENDED_R1(d,corpus);
		
	//	UpperLimitEXTENDED_R1_R2(d,corpus);
	//	UpperLimitEXTENDED_R1_R2_R3(d,corpus);
	}
	 
	
	
	private static void upperBound(DataLoaders d , String corpus,String f) throws Exception{
		int TP = 0;
		int numRECOGNIZED = 0;

		
		TreeMap<String,String> GT_MAP = d.getGT_MAP();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap();
		TreeMap<String,String> BabMAP = d.getBabelfyMap();
		TreeMap<String,String> TagMAP = d.getTagmeMap();
		
		Iterator<?> it = GT_MAP.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
	    	
			String key = (String) pair.getKey();
			String row[] = key.split("\t");
			String docid = row[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
		    String mention = row[1].toLowerCase();
		    String offset = row[2];
		    
		    String k = docid+"\t"+mention+"\t"+offset;
		    String Alink = "NULL";
	        String Blink = "NULL";
	        String Tlink = "NULL";
	        
	        String GTlink = GT_MAP.get(k).toLowerCase();
	        if(GTlink==null){ 
	        	System.out.println(k);
	        	continue;
	        	}
	        
	        if(AmbMAP.containsKey(k)){
	            Alink = AmbMAP.get(k).toLowerCase();
	        }
	        if(BabMAP.containsKey(k)){
	            Blink = BabMAP.get(k).toLowerCase();
	        }
	        if(TagMAP.containsKey(k)){
	            Tlink = TagMAP.get(k).toLowerCase();
	        }
	        //# CASE 0 -  There is no link
	        /* This is the case when the mention is NOT recognized by any of the tools  -  No prediction needed */
	        if((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	continue;
	        }
	        // # CASE 1  - There is 1 link   - Ambiverse
	        /* This is the case when the mention is recognized by ONE of the tools  -  Ambiverse */
	        if ((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	            if(Alink.equalsIgnoreCase(GTlink)){
	            	TP+=1;
	            }
	            continue;
	        }
	        // # CASE 1  - There is 1 link  - Babelfy  
	        /* This is the case when the mention is recognized by ONE of the tools  -  Babelfy */
	        if ((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	if(Blink.equalsIgnoreCase(GTlink)){
	            	TP+=1;
	            }
	        	continue;
	        }
	        // # CASE 1  - There is 1 link   - Tagme
	        /* This is the case when the mention is recognized by ONE of the tools  -  Tagme */
	        if ((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	if(Tlink.equalsIgnoreCase(GTlink)){
	            	TP+=1;
	            }
	        	continue;
	        }
	        //# CASE 2 There are 2 links - Ambiverse and Babelfy
	        /* This is the case when the mention is recognized by TWO of the tools - Ambiverse and Babelfy */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink))  ){
	            	TP+=1;
        		}
	        	continue;
	        }
	        //# CASE 2 There are 2 links  - Ambiverse and Tagme  
	        /* This is the case when the mention is recognized by TWO of the tools - Ambiverse and Tagme */
	        if((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	if( (Alink.equalsIgnoreCase(GTlink))  || (Tlink.equalsIgnoreCase(GTlink)) ){
	        		TP+=1;
	        	}
	        }
	        //# CASE 2 There are 2 links  - Babelfy and Tagme 
	        /* This is the case when the mention is recognized by TWO of the tools - Babelfy and Tagme */
	        if((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	         	numRECOGNIZED++;
	        	if( (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        		TP+=1;
        		}
	        }
	        //# CASE 3 There are 3 links
	        /* This is the case when the mention is recognized by THREE of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	          	numRECOGNIZED++;
	          	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	          		TP+=1;
	          	}
	        }
   
		}
		double P = 0.0;//
		double R = 0.0;
		double F = 0.0;
		
		double scale = Math.pow(10, 3);
		P =  ( Math.round(( P ) * scale) / scale ) * 100.0;
		R =  ( Math.round(( R ) * scale) / scale ) * 100.0;
		
        P =  (double) TP / (double)numRECOGNIZED;
		R =  (double) TP/(double) GT_MAP.keySet().size();
		F = 2*((P*R)/(P+R));
		System.out.println("U B ");
		System.out.println("-----------------------------------------");
		System.out.println("["+corpus+"] -\t"+f+":\t"+P+"\t"+R+"\t"+F);
		System.out.println("-----------------------------------------");
		System.out.println("TP:"+TP);
		System.out.println("numRecog:"+numRECOGNIZED);
		
	}
	
	public static void upperBoundKFold(DataLoaders d , String corpus, TreeMap<String,String> foldMap)  throws Exception{
		int TP = 0;
		int numRECOGNIZED = 0;

		TreeMap<String,String> GT_test_MAP = d.getGT_MAP_test();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap_test();
		TreeMap<String,String> BabMAP = d.getBabelMap_test();
		TreeMap<String,String> TagMAP = d.getTagmeMap_test();
		
		Iterator<?> it = foldMap.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
	    	
			String key = (String) pair.getKey();
			String row[] = key.split("\t");
			String docid = row[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
		    String mention = row[1].toLowerCase();
		    String offset = row[2];
		    
		    String k = docid+"\t"+mention+"\t"+offset;
		    String Alink = "NULL";
	        String Blink = "NULL";
	        String Tlink = "NULL";
	        
	        String GTlink = GT_test_MAP.get(k).toLowerCase();
	        if(GTlink==null){ 
	        	continue;
	        	}
	        
	        if(AmbMAP.containsKey(k)){
	            Alink = AmbMAP.get(k).toLowerCase();
	        }
	        if(BabMAP.containsKey(k)){
	            Blink = BabMAP.get(k).toLowerCase();
	        }
	        if(TagMAP.containsKey(k)){
	            Tlink = TagMAP.get(k).toLowerCase();
	        }
	        //# CASE 0 -  There is no link
	        /* This is the case when the mention is NOT recognized by any of the tools  -  No prediction needed */
	        if((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	continue;
	        }
	        // # CASE 1  - There is 1 link   - Ambiverse
	        /* This is the case when the mention is recognized by ONE of the tools  -  Ambiverse */
	        if ((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	            if(Alink.equalsIgnoreCase(GTlink)){
	            	TP+=1;
	            }
	            continue;
	        }
	        // # CASE 1  - There is 1 link  - Babelfy  
	        /* This is the case when the mention is recognized by ONE of the tools  -  Babelfy */
	        if ((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	if(Blink.equalsIgnoreCase(GTlink)){
	            	TP+=1;
	            }
	        	continue;
	        }
	        // # CASE 1  - There is 1 link   - Tagme
	        /* This is the case when the mention is recognized by ONE of the tools  -  Tagme */
	        if ((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	if(Tlink.equalsIgnoreCase(GTlink)){
	            	TP+=1;
	            }
	        	continue;
	        }
	        //# CASE 2 There are 2 links - Ambiverse and Babelfy
	        /* This is the case when the mention is recognized by TWO of the tools - Ambiverse and Babelfy */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	            
	        	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink))  ){
	            	TP+=1;
        		}
	        	continue;
	        }
	        //# CASE 2 There are 2 links  - Ambiverse and Tagme  
	        /* This is the case when the mention is recognized by TWO of the tools - Ambiverse and Tagme */
	        if((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	if( (Alink.equalsIgnoreCase(GTlink))  || (Tlink.equalsIgnoreCase(GTlink)) ){
	        		TP+=1;
	        	}
	        }
	        //# CASE 2 There are 2 links  - Babelfy and Tagme 
	        /* This is the case when the mention is recognized by TWO of the tools - Babelfy and Tagme */
	        if((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	         	numRECOGNIZED++;
	        	if( (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        		TP+=1;
        		}
	        }
	        //# CASE 3 There are 3 links
	        /* This is the case when the mention is recognized by THREE of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	          	numRECOGNIZED++;
	          	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	          		TP+=1;
	          	}
	        }
   
		}
		double P = 0.0;//
		double R = 0.0;
		double F = 0.0;
		

		
        P =  (double) TP / (double)numRECOGNIZED;
		R =  (double) TP/(double) foldMap.keySet().size();

		
		double scale = Math.pow(10, 3);
		 
		P =  ( Math.round(( P ) * scale) / scale ) * 100.0;
		R =  ( Math.round(( R ) * scale) / scale ) * 100.0;
		F = 2*((P*R)/(P+R));
		
		System.out.println(P+"\t"+R+"\t"+F);
//		System.out.println("Upper Bound ["+corpus+"] -\t"+f+":\t"+P+"\t"+R+"\t"+F);

		
	}
	
	private static void Upper_Limit_MetaEL(DataLoaders d, String corpus) {
		
		
		TreeMap<String,String> GT_MAP = d.getGT_MAP();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap();
		TreeMap<String,String> BabMAP = d.getBabelfyMap();
		TreeMap<String,String> TagMAP = d.getTagmeMap();
		
		int TP = 0;
		int numRECOGNIZED = 0;
		int one_linkPredicted =0;
		int two_linksPredicted =0;
		int three_linksPredicted =0;
		
		int zero_link = 0;
		int one_link = 0;
		int one_linkA = 0;
		int one_linkA_correct = 0;
		int one_linkB = 0;
		int one_linkB_correct = 0;
		int one_linkT = 0;
		int one_linkT_correct = 0;
		
		int two_links = 0;
		int two_linksAB = 0;
		int two_linksAT = 0;
		int two_linksBT = 0;
		
		int one_linkPredictedA =0;
		int one_linkPredictedB =0;
		int one_linkPredictedT =0;
		
		int two_linksPredictedAB =0;
		int two_linksPredictedAT =0;
		int two_linksPredictedBT =0;
		
		int three_linksPredictedABT =0;
		int predictions_neededAB = 0;
		int predictions_neededAT = 0;
		int predictions_neededBT = 0;
		int predictions_neededABT = 0;
		
		int two_links_equal = 0;
		int two_links_diff = 0;
		int three_links = 0;
		int three_linksABT = 0;
		int three_links_equal = 0;
		int three_links_2equal = 0;
		int three_links_diff = 0;
		int predictions_needed = 0;
		int correct_provided = 0;
		
		int i=0;
		Iterator<?> it = GT_MAP.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
	    	
			String key = (String) pair.getKey();
	    	String val = (String) pair.getValue();
	    	
	    	String[] elems = key.split("\t");
	    	String docid = elems[0].toLowerCase();
	    	docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			String mention = elems[1].toLowerCase();
			
			String offset = elems[2];
	        
	        String Alink = "NULL";
	        String Blink = "NULL";
	        String Tlink = "NULL";
	        String[] row;

	        String k = docid+"\t"+mention+"\t"+offset;
	        System.out.println(k);
	        String GTlink = GT_MAP.get(k).toLowerCase();
		    System.out.println(k+"\t"+GTlink);
	       

        	
	        if(AmbMAP.containsKey(k)){
	            Alink = AmbMAP.get(k).toLowerCase();;
	        }
	        if(BabMAP.containsKey(k)){
	            Blink = BabMAP.get(k).toLowerCase();;
	        }
	        if(TagMAP.containsKey(k)){
	            Tlink = TagMAP.get(k).toLowerCase();;
	        }
		 //# CASE 0 -  There is no link
        /* This is the case when the mention is NOT recognized by any of the tools  -  No prediction needed */
        if((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
        	zero_link++;
        	continue;
        }
        	        
        // # CASE 1  - There is 1 link   -  Ambiverse
        /* This is the case when the mention is recognized by ONE of the tools  -  Ambiverse */
        if ((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
        	numRECOGNIZED++;
            one_link++;
            one_linkA++;
            if(Alink.equalsIgnoreCase(GTlink)){
            	TP+=1;
            	one_linkA_correct++;
            }
            continue;
        }
        // # CASE 1  - There is 1 link   -  Babelfy
        /* This is the case when the mention is recognized by ONE of the tools  -  Babelfy */
        if ((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
        	numRECOGNIZED++;
        	one_link++;
        	one_linkB++;
        	if(Blink.equalsIgnoreCase(GTlink)){
            	TP+=1;
            	one_linkB_correct++;
            }
//        	predOut.write(k+"\t"+Blink+"\n");
            System.out.println(k+"\tB:"+Blink+ "\tGT:"+GTlink);
        	continue;
        }
        // # CASE 1  - There is 1 link   -  Tagme 
        /* This is the case when the mention is recognized by ONE of the tools  -  Tagme */
        if ((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
        	numRECOGNIZED++;
        	one_link++;
        	one_linkT++;
        	if(Tlink.equalsIgnoreCase(GTlink)){
            	TP+=1;
            	one_linkT_correct++;
            }
        	continue;
        }
        
        //# CASE 2 There are 2 links - Ambiverse and Babelfy
        /* This is the case when the mention is recognized by TWO of the tools - Ambiverse and Babelfy */
        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
        	numRECOGNIZED++;
 			if(Alink.equalsIgnoreCase(GTlink)){ 
 				TP+=1;
 				continue;	
 			} 
	    	
	    	if(Blink.equalsIgnoreCase(GTlink)){ 
	    		TP+=1;  
	    		continue;	
	    	} 
       	}
        //# CASE 2 There are 2 links - Ambiverse and Tagme
        /* This is the case when the mention is recognized by TWO of the tools - Ambiverse and Tagme */
        if((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
        	numRECOGNIZED++;
        	if(Alink.equalsIgnoreCase(GTlink)){ 
        		TP+=1;
        		continue;	
        	} 
        	if(Tlink.equalsIgnoreCase(GTlink)){ 
        		TP+=1;   
            	continue;
        	}
        }	
        //# CASE 2 There are 2 links - Babelfy and Tagme 
        /* This is the case when the mention is recognized by TWO of the tools - Babelfy and Tagme */
        if((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
        	numRECOGNIZED++;
        	if(Blink.equalsIgnoreCase(GTlink)){ 
        		TP+=1;
        		continue;
        		} 
        	if(Tlink.equalsIgnoreCase(GTlink)){ 
        		TP+=1;
        		continue;
        		} 
        }
        //# CASE 3 There are 3 links
        /* This is the case when the mention is recognized by THREE of the tools */
        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
        	numRECOGNIZED++;
        	if(Alink.equalsIgnoreCase(GTlink)) {  
        		TP+=1;
        		continue;
        	}
        	if(Blink.equalsIgnoreCase(GTlink)){   
        		TP+=1;    
        		continue;  
        		}
        	if(Tlink.equalsIgnoreCase(GTlink)){ 
        		TP+=1; 
        		continue;
        		} 
        }	                
		}
		double P = 0.0;
		double R = 0.0;
		double F = 0.0;
		
		P =  (double) TP / (double)numRECOGNIZED;
		R =  (double) TP/(double) GT_MAP.keySet().size();
		F = 2*((P*R)/(P+R));
		System.out.println(" *********************** ");
		System.out.println("Upper bound Meta EL ");
	    System.out.println("Upper Limit performance on ["+corpus+"]");
		System.out.println(" *********************** ");
	    System.out.println("Upper Limit  P:"+ P +"\tR:"+ R +"\tF:"+ F);
		
		
	}




	private static void UpperLimitEXTENDED_R1_R2_R3(DataLoaders d,String corpus) throws CompressorException, IOException, NumberFormatException, SAXException, ParserConfigurationException {
		int TP = 0;
		int numRECOGNIZED = 0;
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
		if(corpus.equalsIgnoreCase("msnbc")){
			d = DataLoaders_MSNBC.getInstance();
		}
		TreeMap<String,String> GT_test_MAP = d.getGT_MAP_test();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap_test();
		TreeMap<String,String> BabMAP = d.getBabelMap_test();
		TreeMap<String,String> TagMAP = d.getTagmeMap_test();
		
		
		Iterator it = GT_test_MAP.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String k = (String) pair.getKey() ;
	        String GTlink = (String) pair.getValue();
	        String[] row = k.split("\t");
			String docid = row[0].toLowerCase();
	    	docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
		    String mention = row[1].toLowerCase();
		    String offset = row[2];
		    
		    k = docid+"\t"+mention+"\t"+offset;
		    
	        String Alink = "NULL";
	        String Blink = "NULL";
	        String Tlink = "NULL";
	        GTlink = GT_test_MAP.get(k).toLowerCase();

	        if(AmbMAP.containsKey(k)){
	            Alink = AmbMAP.get(k).toLowerCase();
	        }
	        if(BabMAP.containsKey(k)){
	            Blink = BabMAP.get(k).toLowerCase();
	        }
	        if(TagMAP.containsKey(k)){
	            Tlink = TagMAP.get(k).toLowerCase();
	        }
	        
	        // # CASE 3 There is 1 link  - Regra R1  
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	if(Alink.equalsIgnoreCase(GTlink)){
	        		numRECOGNIZED++;
	        		TP+=1;
	        	}
	            continue;
	        }
	        // # CASE 3 There is 1 link   - Regra R1  
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	if(Blink.equalsIgnoreCase(GTlink)){
	        		numRECOGNIZED++;
	        		TP+=1;
	        	}
	        	continue;
	        }
	        // # CASE 3 There is 1 link   
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	if(Tlink.equalsIgnoreCase(GTlink)){
	        		numRECOGNIZED++;
	            	TP+=1;
	        	}
	        	continue;
	        }
	        
	        // # CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	
	        	if((Alink.equalsIgnoreCase(GTlink)) ||  (Blink.equalsIgnoreCase(GTlink)) ){
	        		numRECOGNIZED++;
	        		TP+=1;
	        	}
        		continue;
	        }
	        // # CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	
	        	if((Alink.equalsIgnoreCase(GTlink)) ||  (Tlink.equalsIgnoreCase(GTlink)) ){
	        		numRECOGNIZED++;
	        		TP+=1;
	        	}
        		continue;
	        }
	        // # CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	
        		if((Blink.equalsIgnoreCase(GTlink)) ||  (Tlink.equalsIgnoreCase(GTlink)) ){
        			numRECOGNIZED++;
        			TP+=1;
        		}
        		continue;
	        }
	        // # CASE 3 There are 3 links
	        /* this is the case when the mention is recognized by THREE of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){

	        	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ) {  
	        		numRECOGNIZED++;
   	        		TP+=1;
   	        	}
       			continue;
       		}
	    }

	    double P = 0.0;
		double R = 0.0;
		double F = 0.0;
		
		P =  (double) TP / (double)numRECOGNIZED;
		R =  (double) TP/(double) GT_test_MAP.keySet().size();
		F = 2*((P*R)/(P+R));
		System.out.println(" *********************** ");
		System.out.println("Upper bound Meta EL++ R1 R2 R3");
	    System.out.println("Upper Limit performance on ["+corpus+"]");
		System.out.println(" *********************** ");
	    System.out.println("Upper Limit  P:"+ P +"\tR:"+ R +"\tF:"+ F);
	    }




	/**
	 *
	 * @param corpus
	 * @throws CompressorException
	 * @throws IOException
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws NumberFormatException 
	 */
	private static void UpperLimitEXTENDED_R1_R2(DataLoaders d,String corpus) throws CompressorException, IOException, NumberFormatException, SAXException, ParserConfigurationException{
		int TP = 0;
		int numRECOGNIZED = 0;

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
		if(corpus.equalsIgnoreCase("msnbc")){
			d = DataLoaders_MSNBC.getInstance();
		}

		
		TreeMap<String,String> GT_test_MAP = d.getGT_MAP_test();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap_test();
		TreeMap<String,String> BabMAP = d.getBabelMap_test();
		TreeMap<String,String> TagMAP = d.getTagmeMap_test();
		
		Iterator it = GT_test_MAP.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String k = (String) pair.getKey() ;
	        String GTlink = (String) pair.getValue();
	        String[] row = k.split("\t");
			String docid = row[0];
		    String mention = row[1];
		    String offset = row[2];
		    
//		    if(mention.contains("valfar")){  //************* STUPID WORKAROUND  FOR WP corpus
//		    	mention = mention +","+row[2];
//		    	offset = row[3];
//		    }
		    String predicted = row[row.length -1];
			Pattern p = Pattern.compile("\'([^\"]*)\'");
			Matcher m = p.matcher(docid);
			while (m.find()) {
				docid = m.group(1);
			}
			m = p.matcher(mention);
			while (m.find()) {
				mention = m.group(1).toLowerCase();
			}
		    k = docid+"\t"+mention+"\t"+offset;
		    
	        String Alink = "NULL";
	        String Blink = "NULL";
	        String Tlink = "NULL";
	        GTlink = GT_test_MAP.get(k).toLowerCase();

	        if(AmbMAP.containsKey(k)){
	            Alink = AmbMAP.get(k).toLowerCase();
	        }
	        if(BabMAP.containsKey(k)){
	            Blink = BabMAP.get(k).toLowerCase();
	        }
	        if(TagMAP.containsKey(k)){
	            Tlink = TagMAP.get(k).toLowerCase();
	        }
	        
	        // # CASE 3 There is 1 link  - Regra R1  
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	if(Alink.equalsIgnoreCase(GTlink)){
	        		numRECOGNIZED++;
	        		TP+=1;
	        	}
	            continue;
	        }
	        // # CASE 3 There is 1 link   - Regra R1  
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	if(Blink.equalsIgnoreCase(GTlink)){
	        		numRECOGNIZED++;
	        		TP+=1;
	        	}
	        	continue;
	        }
	        // # CASE 3 There is 1 link   
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	if(Tlink.equalsIgnoreCase(GTlink)){
	        		numRECOGNIZED++;
	            	TP+=1;
	        	}
	        	continue;
	        }
	        
	        // # CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	if(Alink.equalsIgnoreCase(Blink)) {
	        		if(Alink.equalsIgnoreCase(GTlink)){
	        			numRECOGNIZED++;
	        			TP+=1;
	        		}
	        		continue;
	        	}else{
	        	   	numRECOGNIZED++;
	        		if((Alink.equalsIgnoreCase(GTlink)) ||  (Blink.equalsIgnoreCase(GTlink)) ){
	        			TP+=1;
	        		}
	        		continue;
	        	}
	        }
	        // # CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	if(Alink.equalsIgnoreCase(Tlink)) {
	        		if(Alink.equalsIgnoreCase(GTlink)){
	        			numRECOGNIZED++;
	        			TP+=1;
	        		}
	        		continue;
	        	}else{
	        		numRECOGNIZED++;
	        		if((Alink.equalsIgnoreCase(GTlink)) ||  (Tlink.equalsIgnoreCase(GTlink)) ){
	        			TP+=1;
	        		}
	        		continue;
	        	}
	        }
	        // # CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	if(Blink.equalsIgnoreCase(Tlink)) {
	        		if(Blink.equalsIgnoreCase(GTlink)){
	        			numRECOGNIZED++;
	        			TP+=1;
	        		}
	        		continue;
	        		
	        	}else{
	        		numRECOGNIZED++;
	        		if((Blink.equalsIgnoreCase(GTlink)) ||  (Tlink.equalsIgnoreCase(GTlink)) ){
	        			TP+=1;
	        		}
	        		continue;
	        	}
	        }
	        // # CASE 3 There are 3 links
	        /* this is the case when the mention is recognized by THREE of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
       		if( (Alink.equalsIgnoreCase(Blink)) && (Alink.equalsIgnoreCase(Tlink)) && (Blink.equalsIgnoreCase(Tlink)) ) {  
       			if(Alink.equalsIgnoreCase(GTlink)){
       				numRECOGNIZED++;
   	        		TP+=1;
   	        	}
       			continue;
       		}

       		numRECOGNIZED++;
       		if(Alink.equalsIgnoreCase(GTlink)){
	        	TP+=1;
		       	continue;
	        }
	        if(Blink.equalsIgnoreCase(GTlink)){
	        	TP+=1;
	        	continue;
	        }
	        if(Tlink.equalsIgnoreCase(GTlink)){
	           	TP+=1;
	           	continue;
	        }
	        }
	    }

	    double P = 0.0;
		double R = 0.0;
		double F = 0.0;
		
		P =  (double) TP / (double)numRECOGNIZED;
		R =  (double) TP/(double) GT_test_MAP.keySet().size();
		F = 2*((P*R)/(P+R));
		System.out.println(" *********************** ");
		System.out.println("Upper bound Meta EL++ R1 R2");
	    System.out.println("Upper Limit performance on ["+corpus+"]");
		System.out.println(" *********************** ");
	    System.out.println("Upper Limit  P:"+ P +"\tR:"+ R +"\tF:"+ F);
	    }

	
	
	/**
	 * 	This is the upperLimit the METAEL++
	 * 
	 * 	
	 * @param corpus
	 * @param classifier
	 * @throws CompressorException
	 * @throws IOException
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws NumberFormatException 
	 */
	private static void UpperLimitEXTENDED_R1(DataLoaders d, String corpus) throws CompressorException, IOException, NumberFormatException, SAXException, ParserConfigurationException{
		int TP = 0;
		int numRECOGNIZED = 0;

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
		if(corpus.equalsIgnoreCase("msnbc")){
			d = DataLoaders_MSNBC.getInstance();
		}

		TreeMap<String,String> GT_test_MAP = d.getGT_MAP_test();
		
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap_test();
		TreeMap<String,String> BabMAP = d.getBabelMap_test();
		TreeMap<String,String> TagMAP = d.getTagmeMap_test();
		
		Iterator it = GT_test_MAP.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String k = (String) pair.getKey() ;
	        String GTlink = (String) pair.getValue();
	        String[] row = k.split("\t");
			String docid = row[0];
		    String mention = row[1];
		    String offset = row[2];
		    
//		    if(mention.contains("valfar")){  //************* STUPID WORKAROUND  FOR WP corpus
//		    	mention = mention +","+row[2];
//		    	offset = row[3];
//		    }
		    
		    String predicted = row[row.length -1];
			Pattern p = Pattern.compile("\'([^\"]*)\'");
			Matcher m = p.matcher(docid);
			while (m.find()) {
				docid = m.group(1);
			}
			m = p.matcher(mention);
			while (m.find()) {
				mention = m.group(1).toLowerCase();
			}
		    k = docid+"\t"+mention+"\t"+offset;
		    
	        String Alink = "NULL";
	        String Blink = "NULL";
	        String Tlink = "NULL";
	        GTlink = GT_test_MAP.get(k).toLowerCase();

	        if(AmbMAP.containsKey(k)){
	            Alink = AmbMAP.get(k).toLowerCase();
	        }
	        if(BabMAP.containsKey(k)){
	            Blink = BabMAP.get(k).toLowerCase();
	        }
	        if(TagMAP.containsKey(k)){
	            Tlink = TagMAP.get(k).toLowerCase();
	        }
	        
	        // # There is 1 link  - Regra R1  
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	if(Alink.equalsIgnoreCase(GTlink)){
	        		numRECOGNIZED++;
	        		TP+=1;
	        	}
	            continue;
	        }
	        // # There is 1 link     - Regra R1  
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	if(Blink.equalsIgnoreCase(GTlink)){
	        		numRECOGNIZED++;
	        		TP+=1;
	        	}
	        	continue;
	        }
	        // # There is 1 link   
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	if(Tlink.equalsIgnoreCase(GTlink)){
	        		numRECOGNIZED++;
	            	TP+=1;
	        	}
	        	continue;
	        }
	        // # CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
        		numRECOGNIZED++;
            	if((Alink.equalsIgnoreCase(GTlink)) ||  (Blink.equalsIgnoreCase(GTlink)) ){
            		TP+=1;
	        	}
	            continue;
	        }
	        // # CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
        		numRECOGNIZED++;
            	if((Alink.equalsIgnoreCase(GTlink)) ||  (Tlink.equalsIgnoreCase(GTlink)) ){
            		TP+=1;
	        	}
	            continue;
	        }	
	        // # CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
        		numRECOGNIZED++;
            	if((Blink.equalsIgnoreCase(GTlink)) ||  (Tlink.equalsIgnoreCase(GTlink)) ){
            		TP+=1;
	        	}
	            continue;
	        }
	        // # CASE 3 There are 3 links
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
        		numRECOGNIZED++;
	        	if(Alink.equalsIgnoreCase(GTlink)){
	        		TP+=1;
		        	continue;
	        	}
	        	if(Blink.equalsIgnoreCase(GTlink)){
	        		TP+=1;
		        	continue;
	        	}
	        	if(Tlink.equalsIgnoreCase(GTlink)){
	            	TP+=1;
	            	continue;
	        	}
	        }
	       
	    }
	    
	    double P = 0.0;//
		double R = 0.0;
		double F = 0.0;
		
		P =  (double) TP /(double)numRECOGNIZED;
		R =  (double) TP/(double) GT_test_MAP.keySet().size();
		F = 2*((P*R)/(P+R));
		System.out.println(" *********************** ");
		System.out.println("Upper bound Meta EL++ R1");
	    System.out.println("Upper Limit performance on ["+corpus+"]");
		System.out.println(" *********************** ");
	    System.out.println("Upper Limit  P:"+ P +"\tR:"+ R +"\tF:"+ F);
	}
	
	
	
	/**
	 * 			This method calculates the Upper limit for the MetaEL approach.
	 * 
	 * @param corpus
	 * @throws CompressorException
	 * @throws IOException
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws NumberFormatException 
	 */
	public static void Upper_limit_MetaEL_STRICT_LOOSE(DataLoaders d, String corpus) throws CompressorException, IOException, NumberFormatException, SAXException, ParserConfigurationException{
	

//		TreeMap<String,String> GT_MAP_test = d.getGT_MAP_test();
//
//		TreeMap<String,String>  ambiverseMap_test = d.getAmbiverseMap_test();
//		TreeMap<String,String>  babelMap_test = d.getBabelMap_test();
//		TreeMap<String,String>  tagmeMap_test = d.getTagmeMap_test();

		TreeMap<String,String> GT_MAP = d.getGT_MAP();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap();
		TreeMap<String,String> BabMAP = d.getBabelfyMap();
		TreeMap<String,String> TagMAP = d.getTagmeMap();
		
//		TreeMap<String,String>  spotlightMap_test = d.getSpotlightMap_test();
		
		int TP = 0;
		int numRECOGNIZED =0;
		Iterator it = GT_MAP.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String key = (String) pair.getKey() ;
	        String GTlink = (String) pair.getValue();
	        
	        if(AmbMAP.containsKey(key)){
	        	String lAmb = AmbMAP.get(key);
	        	if(lAmb.equalsIgnoreCase(GTlink)){
	        		numRECOGNIZED++;
	        		 TP += 1   ;
	        		 continue;
	        	}
	        }
	        
	        if(BabMAP.containsKey(key)){
	        	String lBab = BabMAP.get(key);
	        	if(lBab.equalsIgnoreCase(GTlink)){
	        		numRECOGNIZED++;
	        		TP += 1;
	        		continue;
	        	}
	        }
	        
	        if(TagMAP.containsKey(key)){
	        	String lTag = TagMAP.get(key);
	        	if(lTag.equalsIgnoreCase(GTlink)){
	        		numRECOGNIZED++;
	        		TP += 1;
	        		continue;
	        	}
	        }
	        
//	        if(spotlightMap_test.containsKey(key)){
//	        	String lSpot = spotlightMap_test.get(key);
//	        	if(lSpot.equalsIgnoreCase(GTlink)){
//    				numRECOGNIZED++;
//	        		TP += 1;
//	        		continue;
//	        	}
//	        }
	        
	    }
//	    
	    Set<String> keys_a = AmbMAP.keySet();
	    Set<String> keys_b = BabMAP.keySet();
	    Set<String> keys_t = TagMAP.keySet();
//	    Set<String> keys_s = spotlightMap_test.keySet();
	    
	    Set<String> keys_GT = GT_MAP.keySet();
//
	    System.out.println("True Positive :"+TP);
	    
	    keys_a.retainAll(keys_GT);
	    keys_b.retainAll(keys_GT);
	    keys_t.retainAll(keys_GT);
//	    keys_s.retainAll(keys_GT);
	    
//	    
	    Set<String> union = new TreeSet<String>();
	    union.addAll(keys_a);
	    union.addAll(keys_b);
	    union.addAll(keys_t);
//	    union.addAll(keys_s);

//	    System.out.println(union.size());
//	    double P = TP / (double) union.size();
	    double P = TP / (double) numRECOGNIZED;

	    double R = TP / (double) GT_MAP.size();
	    double F = 2*((P*R)/(P+R));
	    
	    System.out.println();
	    System.out.println("Upper Limit performance on ["+corpus+"]");
	    System.out.println("Upper Limit  P:"+ P +"\tR:"+ R +"\tF:"+ F);
//	    
			
	}
}
