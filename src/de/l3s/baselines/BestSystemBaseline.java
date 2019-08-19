package de.l3s.baselines;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
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

public class BestSystemBaseline {
	
	public static void main(String[] args) throws CompressorException, IOException, NumberFormatException, SAXException, ParserConfigurationException {
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
		
		BestSystemBaseline.BestSystemBaseline(d,corpus);
		
	}
	
	public static void BestSystemBaseline(DataLoaders d, String corpus) throws CompressorException, IOException{
		OutputStreamWriter predOut = new OutputStreamWriter(new FileOutputStream("./resources/"+corpus+"/dataset.multiclass."+corpus+".baseline.BESTSYSTEM.out"), StandardCharsets.UTF_8);
		
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

		
		TreeMap<String,String> GT_MAP = d.getGT_MAP();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap();
		TreeMap<String,String> BabMAP = d.getBabelfyMap();
		TreeMap<String,String> TagMAP = d.getTagmeMap();
		
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
//		    mention = mention.replaceAll("\'", "");
//		    mention = mention.replaceAll("\"", "");
			
			String offset = elems[2];
	        
	        String Alink = "NULL";
	        String Blink = "NULL";
	        String Tlink = "NULL";
	        String[] row;

	        String k = docid+"\t"+mention+"\t"+offset;
	        String GTlink = GT_MAP.get(k).toLowerCase();
	        String predictedTool = "";
	       
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
//	            predOut.write(k+"\t"+Alink+"\n");
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
//	        	predOut.write(k+"\t"+Blink+"\n");
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
//	        	predOut.write(k+"\t"+Tlink+"\n");
	        	continue;
	        }
	        //# CASE 2 There are 2 links - Ambiverse and Babelfy
	        /* This is the case when the mention is recognized by TWO of the tools - Ambiverse and Babelfy */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	two_links++;
	        	two_linksAB++;
	        	
	            if(Alink.equalsIgnoreCase(Blink)){
	                two_links_equal++;
	            }else{
	            	two_links_diff++;
	            	predictions_needed++;
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			correct_provided++;
	        		}
	            }
	            
	    		if(corpus.equalsIgnoreCase("conll")){   //Babelfy > Ambiverse > Spotlight >  Tagme 
	    			 if(Blink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("iitb")){ //	String corpus = "iitb";    //Ambiverse > Spotlight >  Tagme > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("wp")){ //String corpus = "wp";      // Tagme > Babelfy > Ambiverse > Spotlight
	    			if(Blink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("neel")){//	    		String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("aquaint")){//	    		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("msnbc")){//	    		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
//	    		String corpus = "gerdaq";  // Tagme > Babelfy > Ambiverse 
	            
	        }
	        //# CASE 2 There are 2 links - Ambiverse and Tagme
	        /* This is the case when the mention is recognized by TWO of the tools - Ambiverse and Tagme */
	        if((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	            two_links++;
	            two_linksAT++;
	            if(Alink.equalsIgnoreCase(Tlink)){
	                two_links_equal++;
	            }else{
	                two_links_diff++;
	                predictions_needed++;
	                if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			correct_provided++;
	        		}
	            }
	            if(corpus.equalsIgnoreCase("conll")){   //Babelfy > Ambiverse > Spotlight >  Tagme 
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("iitb")){ //	String corpus = "iitb";    //Ambiverse > Spotlight >  Tagme > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("wp")){ //String corpus = "wp";      // Tagme > Babelfy > Ambiverse > Spotlight
	    			if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("neel")){//	    		String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("aquaint")){//	    		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
	    			 if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("msnbc")){//	    		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
//	    		String corpus = "gerdaq";  // Tagme > Babelfy > Ambiverse 
//	    		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
//	    		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
	            
	        }	
	        //# CASE 2 There are 2 links - Babelfy and Tagme 
	        /* This is the case when the mention is recognized by TWO of the tools - Babelfy and Tagme */
	        if((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	two_links++;
	        	two_linksBT++;
	
	            if(Blink.equalsIgnoreCase(Tlink)){
	                two_links_equal++;
	            }else{
	            	two_links_diff++;	                
	            	predictions_needed++;
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			correct_provided++;
	        		}
	            }
	            if(corpus.equalsIgnoreCase("conll")){   //Babelfy > Ambiverse > Spotlight >  Tagme 
	    			 if(Blink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("iitb")){ //	String corpus = "iitb";    //Ambiverse > Spotlight >  Tagme > Babelfy
	    			 if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("wp")){ //String corpus = "wp";      // Tagme > Babelfy > Ambiverse > Spotlight
	    			if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("neel")){//	    		String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
	    			 if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("aquaint")){//	    		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
	    			 if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("msnbc")){//	    		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
	    			 if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
//	    		String corpus = "gerdaq";  // Tagme > Babelfy > Ambiverse 
//	    		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
//	    		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
	        }
	        //# CASE 3 There are 3 links
	        /* This is the case when the mention is recognized by THREE of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	three_links++;
        		three_linksABT++;
	        	//The 3 links are the same
	        	if ( (Alink.equalsIgnoreCase(Blink)) &&  (Alink.equalsIgnoreCase(Tlink)) &&  (Blink.equalsIgnoreCase(Tlink)) ){
	                three_links_equal++;
	        	}
	        	//Ambiverse == Babelfy != Tagme
	        	if ( (Alink.equalsIgnoreCase(Blink)) &&  (!Alink.equalsIgnoreCase(Tlink))){
	        		three_links_2equal++;
	            	predictions_needed++;
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			correct_provided++;
	        		}
	        	}
	        	//Ambiverse != Babelfy == Tagme
	        	if ( (!Alink.equalsIgnoreCase(Tlink)) &&  (Blink.equalsIgnoreCase(Tlink)) ){
	        		three_links_2equal++;
	            	predictions_needed++;
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			correct_provided++;
	        		}
	        	}
	        	//Ambiverse == Tagme != Babelfy        
	        	if ( !(Alink.equalsIgnoreCase(Blink)) &&  (Alink.equalsIgnoreCase(Tlink))  ){
	        		three_links_2equal++;
	            	predictions_needed++;
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			correct_provided++;
	        		}
	        	}
	        	//Ambiverse != Babelfy != Tagme
	        	if ( (!Alink.equalsIgnoreCase(Blink)) &&  (!Alink.equalsIgnoreCase(Tlink)) &&  (!Blink.equalsIgnoreCase(Tlink)) ){
	                three_links_diff++;
	            	predictions_needed++;
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			correct_provided++;
	        		}
	        	}
	        	if(corpus.equalsIgnoreCase("conll")){   //Babelfy > Ambiverse > Spotlight >  Tagme 
	    			 if(Blink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("iitb")){ //	String corpus = "iitb";    //Ambiverse > Spotlight >  Tagme > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("wp")){ //String corpus = "wp";      // Tagme > Babelfy > Ambiverse > Spotlight
	    			if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("neel")){//	    		String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("aquaint")){//	    		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
	    			 if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("msnbc")){//	    		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
//	    		String corpus = "gerdaq";  // Tagme > Babelfy > Ambiverse 
//	    		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
//	    		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
	        }	
	        
		}
//		System.out.println();
////		System.out.println();
//		System.out.println("-----------------------------------------");
//		System.out.println("GT mentions recognised by 0/3 systems :" +zero_link);
//		System.out.println("-----------------------------------------");
//		System.out.println("GT mentions recognised by 1/3 systems :" +one_link);
//		System.out.println("-----------------------------------------");
//		System.out.println("......GT mentions recognised by Amb :" +one_linkA  + "\tTP: "+one_linkA_correct ) ;
//		System.out.println("......GT mentions recognised by Bab :" +one_linkB  + "\tTP: "+one_linkB_correct ) ;
//		System.out.println("......GT mentions recognised by Tag :" +one_linkT  + "\tTP: "+one_linkT_correct ) ;
//		System.out.println("...... ");
//		System.out.println("......  # mentions need binary class prediction :" +one_link);
//		System.out.println("......  # mentions predicted by binary Amb clf :" +one_linkPredictedA);
//		System.out.println("......  # mentions predicted by binary Bab clf :" +one_linkPredictedB);
//		System.out.println("......  # mentions predicted by binary Tag clf :" +one_linkPredictedT);
//		System.out.println("-----------------------------------------");
//		System.out.println("GT mentions recognised by 2/3 systems :"+two_links);
//		System.out.println("-----------------------------------------");
//		System.out.println("......GT mentions recognised by (Amb & Bab) :" +two_linksAB);
//		System.out.println("......GT mentions recognised by (Amb & Tag) :" +two_linksAT);
//		System.out.println("......GT mentions recognised by (Bab & Tag) :" +two_linksBT);
//		System.out.println(".........  2 systems provide the same entity :" +two_links_equal);
//		System.out.println(".........  2 systems provide different entity :" +two_links_diff);
//		System.out.println("......  # mentions need multiclass prediction :" +two_links_diff);
//		System.out.println("...........# mentions need multiclass prediction (Amb & Bab) :" +predictions_neededAB);
//		System.out.println("...........# mentions need multiclass prediction (Amb & Tag) :" +predictions_neededAT);
//		System.out.println("...........# mentions need multiclass prediction (Bab & Tag) :" +predictions_neededBT);
//		System.out.println("......  # mentions need binary class prediction :" + two_links_equal );
//		System.out.println("...........# mentions predicted by binary (Amb & Bab) clf:" +two_linksPredictedAB);
//		System.out.println("...........# mentions predicted by binary (Amb & Tag) clf:" +two_linksPredictedAT);
//		System.out.println("...........# mentions predicted by binary (Bab & Tag) clf:" +two_linksPredictedBT);
//		System.out.println("-----------------------------------------");
//		System.out.println("GT mentions recognised by 3/3 systems :"+three_links);
//		System.out.println("-----------------------------------------");
//		System.out.println(".........  3 systems provide the same entity :"+three_links_equal);
//		System.out.println(".........  2 systems provide the same entity :"+three_links_2equal);
//		System.out.println(".........  each system provides a different entity :"+three_links_diff);
//		System.out.println("......  # mentions need multiclass prediction :" +predictions_neededABT);
//		System.out.println("......  # mentions need binary class prediction :" + three_links_equal );
//		System.out.println("...........# mentions predicted by binary class :" +three_linksPredicted);
//		System.out.println("-----------------------------------------");
//		System.out.println("GT mentions that need prediction :" +predictions_needed);
////		System.out.println("GT mentions that need prediction :" +(predictions_neededAB+predictions_neededAT+predictions_neededBT+predictions_neededABT));
//		System.out.println("-----------------------------------------");
////		System.out.println("......The correct entity is provided by at least 1 system :"+correct_provided);
////		System.out.println("......The correct entity is not provided by at least 1 system :"+(predictions_needed-correct_provided));
////		System.out.println("-----------------------------------------");
//		System.out.println("TOTAL "+(zero_link+one_link+two_links+three_links) );
////		System.out.println();
//		System.out.println();

		double P = 0.0;//
		double R = 0.0;
		double F = 0.0;
		
		P =  (double) TP / (double)numRECOGNIZED;
		R =  (double) TP/(double) GT_MAP.keySet().size();
	
		
		double scale = Math.pow(10, 3);
		P =  ( Math.round(( P ) * scale) / scale ) * 100.0;
		R =  ( Math.round(( R ) * scale) / scale ) * 100.0;
		F =  ( 2*((P*R)/(P+R)) );
		
		System.out.println("Best System "+corpus);
		System.out.println(P+"\t& "+R+"\t& "+F);
//		System.out.println("TP:"+TP);
//		System.out.println("numRecog:"+numRECOGNIZED);
		
	}

	
	
	
	
	
	public static void BestSystemBaselineKFold(DataLoaders d, String corpus, TreeMap<String,String> foldMap) throws CompressorException, IOException{
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
	    	
	    	String[] elems = key.split("\t");
	    	String docid = elems[0].toLowerCase();
	    	docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			String mention = elems[1].toLowerCase();
			
			String offset = elems[2];
	        
	        String Alink = "NULL";
	        String Blink = "NULL";
	        String Tlink = "NULL";

	        String k = docid+"\t"+mention+"\t"+offset;
	        String GTlink = GT_test_MAP.get(k).toLowerCase();;
	       
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
	        	continue;
	        }
	        	        
	        // # CASE 1  - There is 1 link   -  Ambiverse
	        /* This is the case when the mention is recognized by ONE of the tools  -  Ambiverse */
	        if ((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	            if(Alink.equalsIgnoreCase(GTlink)){
	            	TP+=1;
	            }
	            continue;
	        }
	        // # CASE 1  - There is 1 link   -  Babelfy
	        /* This is the case when the mention is recognized by ONE of the tools  -  Babelfy */
	        if ((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	if(Blink.equalsIgnoreCase(GTlink)){
	            	TP+=1;
	            }
//	        	predOut.write(k+"\t"+Blink+"\n");
	        	continue;
	        }
	        // # CASE 1  - There is 1 link   -  Tagme 
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
	        	
	            if(Alink.equalsIgnoreCase(Blink)){
	            }else{
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        		}
	            }
	            
	    		if(corpus.equalsIgnoreCase("conll")){   //Babelfy > Ambiverse > Spotlight >  Tagme 
	    			 if(Blink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("iitb")){ //	String corpus = "iitb";    //Ambiverse > Spotlight >  Tagme > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("wp")){ //String corpus = "wp";      // Tagme > Babelfy > Ambiverse > Spotlight
	    			if(Blink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("neel")){//	    		String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("aquaint")){//	    		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("msnbc")){//	    		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
//	    		String corpus = "gerdaq";  // Tagme > Babelfy > Ambiverse 
	            
	        }
	        //# CASE 2 There are 2 links - Ambiverse and Tagme
	        /* This is the case when the mention is recognized by TWO of the tools - Ambiverse and Tagme */
	        if((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	            if(Alink.equalsIgnoreCase(Tlink)){
	            }else{
	                if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        		}
	            }
	            if(corpus.equalsIgnoreCase("conll")){   //Babelfy > Ambiverse > Spotlight >  Tagme 
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("iitb")){ //	String corpus = "iitb";    //Ambiverse > Spotlight >  Tagme > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("wp")){ //String corpus = "wp";      // Tagme > Babelfy > Ambiverse > Spotlight
	    			if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("neel")){//	    		String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("aquaint")){//	    		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
	    			 if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("msnbc")){//	    		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
//	    		String corpus = "gerdaq";  // Tagme > Babelfy > Ambiverse 
//	    		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
//	    		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
	            
	        }	
	        //# CASE 2 There are 2 links - Babelfy and Tagme 
	        /* This is the case when the mention is recognized by TWO of the tools - Babelfy and Tagme */
	        if((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	
	            if(Blink.equalsIgnoreCase(Tlink)){
	            }else{
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        		}
	            }
	            if(corpus.equalsIgnoreCase("conll")){   //Babelfy > Ambiverse > Spotlight >  Tagme 
	    			 if(Blink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("iitb")){ //	String corpus = "iitb";    //Ambiverse > Spotlight >  Tagme > Babelfy
	    			 if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("wp")){ //String corpus = "wp";      // Tagme > Babelfy > Ambiverse > Spotlight
	    			if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("neel")){//	    		String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
	    			 if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("aquaint")){//	    		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
	    			 if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("msnbc")){//	    		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
	    			 if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 // predOut.write(k+"\t"+Blink+"\n");
	    			 continue;	
	    		}
//	    		String corpus = "gerdaq";  // Tagme > Babelfy > Ambiverse 
//	    		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
//	    		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
	        }
	        //# CASE 3 There are 3 links
	        /* This is the case when the mention is recognized by THREE of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	//The 3 links are the same
	        	if ( (Alink.equalsIgnoreCase(Blink)) &&  (Alink.equalsIgnoreCase(Tlink)) &&  (Blink.equalsIgnoreCase(Tlink)) ){
	        	}
	        	//Ambiverse == Babelfy != Tagme
	        	if ( (Alink.equalsIgnoreCase(Blink)) &&  (!Alink.equalsIgnoreCase(Tlink))){
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        		}
	        	}
	        	//Ambiverse != Babelfy == Tagme
	        	if ( (!Alink.equalsIgnoreCase(Tlink)) &&  (Blink.equalsIgnoreCase(Tlink)) ){
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        		}
	        	}
	        	//Ambiverse == Tagme != Babelfy        
	        	if ( !(Alink.equalsIgnoreCase(Blink)) &&  (Alink.equalsIgnoreCase(Tlink))  ){
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        		}
	        	}
	        	//Ambiverse != Babelfy != Tagme
	        	if ( (!Alink.equalsIgnoreCase(Blink)) &&  (!Alink.equalsIgnoreCase(Tlink)) &&  (!Blink.equalsIgnoreCase(Tlink)) ){
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        		}
	        	}
	        	if(corpus.equalsIgnoreCase("conll")){   //Babelfy > Ambiverse > Spotlight >  Tagme 
	    			 if(Blink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("iitb")){ //	String corpus = "iitb";    //Ambiverse > Spotlight >  Tagme > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("wp")){ //String corpus = "wp";      // Tagme > Babelfy > Ambiverse > Spotlight
	    			if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("neel")){//	    		String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("aquaint")){//	    		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
	    			 if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 continue;	
	    		}
	    		if(corpus.equalsIgnoreCase("msnbc")){//	    		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
	    			 if(Alink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
	    			 continue;	
	    		}
//	    		String corpus = "gerdaq";  // Tagme > Babelfy > Ambiverse 
//	    		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
//	    		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
	        }	
	        
		}



		double P = 0.0;//
		double R = 0.0;
		double F = 0.0;
		
		P =  (double) TP / (double)numRECOGNIZED;
		R =  (double) TP/(double) foldMap.keySet().size();
		F =  ( 2*((P*R)/(P+R)) )*100.0;
		
		double scale = Math.pow(10, 3);
		P =  ( Math.round(( P ) * scale) / scale ) * 100.0;
		R =  ( Math.round(( R ) * scale) / scale ) * 100.0;
		
		
		System.out.println("Best System ["+corpus+"]" + "\t " + P+"\t "+R+"\t "+F);
//		System.out.println("TP:"+TP);
//		System.out.println("numRecog:"+numRECOGNIZED);
		
	}
	
	
	
	
}
