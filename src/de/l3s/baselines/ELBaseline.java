package de.l3s.baselines;

import java.io.IOException;
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

public class ELBaseline {

	

	public static void main(String[] args) throws CompressorException, IOException, NumberFormatException, SAXException, ParserConfigurationException{
//		String[] corpus = new String[]{"conll", "iitb", "wp",  "neel", "gerdaq"};
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
		
		ELBaseline.ELBaseline(d, corpus);
	}

	

	
	
	private static void ELBaseline(DataLoaders d,String corpus) throws CompressorException, IOException, NumberFormatException, SAXException, ParserConfigurationException {
		
		TreeMap<String,String> GT_MAP = d.getGT_MAP();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap();
		TreeMap<String,String> BabMAP = d.getBabelfyMap();
		TreeMap<String,String> TagMAP = d.getTagmeMap();
		
		
		double Amb_COMMON_GT = 0;
		double Bab_COMMON_GT = 0;
		double Tag_COMMON_GT = 0;
		
		double AmbGT_TP = 0;
		double BabGT_TP = 0;
		double TagGT_TP = 0;
		
		
		Iterator it = GT_MAP.entrySet().iterator();
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
	        
	        key = docid+"\t"+mention+"\t"+offset;
	        String GTlink =val.replaceAll("_"," ").toLowerCase();
	        
	        if(AmbMAP.containsKey(key)){
	        	Amb_COMMON_GT++;
	        	String lAmb = AmbMAP.get(key);
	        	if(lAmb.equalsIgnoreCase(GTlink)){
	        		AmbGT_TP++;
	        	}
//	        	}else{
// 	        		System.out.println(key + "\t" +GTlink + "\t" +lAmb);
//	        	}
	        }
	        
	        if(BabMAP.containsKey(key)){
	        	Bab_COMMON_GT++;
	        	String lBab = BabMAP.get(key);
	        	if(lBab.equalsIgnoreCase(GTlink)){
	        		BabGT_TP++;
	        	}else{
//	        		System.out.println(key + "\t" +GTlink + "\t" +lBab);
	        	}
	        }
	        
	        if(TagMAP.containsKey(key)){
	        	Tag_COMMON_GT++;
	        	String lTag = TagMAP.get(key);
	        	if(lTag.equalsIgnoreCase(GTlink)){
	        		TagGT_TP++;
	        	}
	        	
	        }else{
//	        	System.out.println(key);
	        }
	        
	    }
	    double PAmb = 0.0, RAmb = 0.0, FAmb = 0.0;
	    double PBab = 0.0, RBab = 0.0, FBab = 0.0;
	    double PTag = 0.0, RTag = 0.0, FTag = 0.0;
//	    double PSpot = 0.0, RSpot = 0.0, FSpot = 0.0;
	    
//	    double scale = Math.pow(10, 3);
	    
	    
	    
	    PAmb = AmbGT_TP/Amb_COMMON_GT;
//	    PAmb = AmbGT_TP/AmbMAP.size();
	    RAmb = AmbGT_TP/GT_MAP.size();
	    FAmb = 2*((PAmb*RAmb)/(PAmb+RAmb));
	    
	    
	    PBab = BabGT_TP/Bab_COMMON_GT;
//	    PBab = BabGT_TP/BabMAP.size();
	    RBab = BabGT_TP/GT_MAP.size();
	    FBab = 2*((PBab*RBab)/(PBab+RBab));
	    
	    PTag = TagGT_TP/Tag_COMMON_GT;
//	    PTag = TagGT_TP/TagMAP.size();
	    RTag = TagGT_TP/GT_MAP.size();  
	    FTag = 2*((PTag*RTag)/(PTag+RTag)); 
	    
//	    PSpot = SpotGT_TP/Spot_COMMON_GT;
//	    RSpot = SpotGT_TP/GT_MAP_test.size();
//	    FSpot = 2*((PSpot*RSpot)/(PSpot+RSpot));
	    
	    System.out.println();
	    System.out.println("Baseline performance on ["+corpus+"]");
	    System.out.println(AmbGT_TP);
	    System.out.println("Ambiverse  	"+ PAmb +"\t& "+ RAmb +"\t& "+ FAmb);
	    System.out.println(BabGT_TP);
	    System.out.println("Babelfy 	"+ PBab +"\t& "+ RBab +"\t& "+ FBab);
//	    System.out.println("Spotlight  	P:"+ PSpot +"\tR:"+ RSpot +"\tF:"+ FSpot);
	    System.out.println(TagGT_TP);
	    System.out.println("Tagme	  	"+ PTag +"\t& "+ RTag +"\t& "+ FTag);


	    
	}
	
	public static void ELBaselineKFold(DataLoaders d,String corpus ,TreeMap<String,String> foldMap)  throws CompressorException, IOException, NumberFormatException, SAXException, ParserConfigurationException {
		
		TreeMap<String,String> GT_MAP_test = d.getGT_MAP_test();

		TreeMap<String,String>  ambiverseMap_test = d.getAmbiverseMap_test();
		TreeMap<String,String>  babelMap_test = d.getBabelMap_test();
		TreeMap<String,String>  tagmeMap_test = d.getTagmeMap_test();
		
		double Amb_COMMON_GT = 0;
		double Bab_COMMON_GT = 0;
		double Tag_COMMON_GT = 0;
		
		double AmbGT_TP = 0;
		double BabGT_TP = 0;
		double TagGT_TP = 0;
		
		Iterator it = foldMap.entrySet().iterator();
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
	        
	        key = docid+"\t"+mention+"\t"+offset;
	        String GTlink =val.toLowerCase();
	        
	        if(ambiverseMap_test.containsKey(key)){
	        	Amb_COMMON_GT++;
	        	String lAmb = ambiverseMap_test.get(key);
	        	if(lAmb.equalsIgnoreCase(GTlink)){
	        		AmbGT_TP++;
	        	}else{
// 	        		System.out.println(key + "\t" +GTlink + "\t" +lAmb);
	        	}
	        }
	        
	        if(babelMap_test.containsKey(key)){
	        	Bab_COMMON_GT++;
	        	String lBab = babelMap_test.get(key);
	        	if(lBab.equalsIgnoreCase(GTlink)){
	        		BabGT_TP++;
	        	}else{
//	        		System.out.println(key + "\t" +GTlink + "\t" +lBab);
	        	}
	        }
	        
	        if(tagmeMap_test.containsKey(key)){
	        	Tag_COMMON_GT++;
	        	String lTag = tagmeMap_test.get(key);
	        	if(lTag.equalsIgnoreCase(GTlink)){
	        		TagGT_TP++;
	        	}
	        	
	        }
	    }
	    double PAmb = 0.0, RAmb = 0.0, FAmb = 0.0;
	    double PBab = 0.0, RBab = 0.0, FBab = 0.0;
	    double PTag = 0.0, RTag = 0.0, FTag = 0.0;
//	    double PSpot = 0.0, RSpot = 0.0, FSpot = 0.0;
	    
	    double scale = Math.pow(10, 3);
	    
	    PAmb = ( Math.round(( AmbGT_TP/Amb_COMMON_GT ) * scale) / scale ) * 100.0;
	    RAmb = ( Math.round(( AmbGT_TP/foldMap.size() ) * scale) / scale ) * 100.0;
	    FAmb = 2*((PAmb*RAmb)/(PAmb+RAmb));
	    
	    
	    PBab = ( Math.round(( BabGT_TP/Bab_COMMON_GT ) * scale) / scale ) * 100.0;
	    RBab = ( Math.round((BabGT_TP/foldMap.size() ) * scale) / scale ) * 100.0;
	    FBab = 2*((PBab*RBab)/(PBab+RBab));
	    
	    PTag = ( Math.round((TagGT_TP/Tag_COMMON_GT) * scale) / scale ) * 100.0;  
	    RTag = ( Math.round((TagGT_TP/foldMap.size()) * scale) / scale ) * 100.0;  
	    FTag = 2*((PTag*RTag)/(PTag+RTag)); 
	    
	    System.out.println("Baseline performance on ["+corpus+"]");
	    System.out.println(PAmb +"\t "+ RAmb +"\t  "+ FAmb+"\t "+ PBab +"\t  "+ RBab +"\t  "+ FBab+"\t "+ PTag +"\t  "+ RTag +"\t  "+ FTag);

	}
	
}
