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
import de.l3s.loaders.DataLoaders_Prior;
import de.l3s.loaders.DataLoaders_WP;

public class PriorBaseline {
	
	public static void main(String[] args) throws CompressorException, IOException, NumberFormatException, SAXException, ParserConfigurationException{
		TreeMap<String, String> priorMap  = new TreeMap<String, String>();
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
		
 		DataLoaders_Prior dP = new DataLoaders_Prior();
 		priorMap = dP.loadPrior("/home/joao/git/WikiParsing/resources/mentionentity.20160701.prior.csv.bz2");
		System.out.println(priorMap.keySet().size());
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
		
		PriorBaseline.PriorBaseline(d,priorMap, corpus);
	}
	
	
	private static void PriorBaseline(DataLoaders d,TreeMap<String, String> priorMap ,String corpus) throws CompressorException, IOException, NumberFormatException, SAXException, ParserConfigurationException {
		int TP = 0;
		int PriorCommon = 0;
		int i = 0;
		TreeMap<String,String> GT_MAP_test = d.getGT_MAP_test();
		Iterator it = GT_MAP_test.entrySet().iterator();
		
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
	        String GTlink =val.toLowerCase();
	        
	        if(priorMap.containsKey(mention)){
	        	PriorCommon++;
	        	String linkPrior = priorMap.get(mention);
	        	if(linkPrior.equalsIgnoreCase(GTlink)){
	        		TP++;
	        	}else{
// 	        		System.out.println(mention + "\t" +GTlink + "\t" +linkPrior);
	        	}
//	        	System.out.println(i+++"\tM:"+mention + "\tGT:" +GTlink + "\tP:"+linkPrior );
	        }
	    	

	        
	    }
//	    double scale = Math.pow(10, 3);
	    double P = 0.0, R = 0.0, F = 0.0;
	 
	    P  =  (double) TP / (double)PriorCommon;
	    R  =  (double)TP / (double)GT_MAP_test.size();
	    F  = 2*((P*R)/(P+R));
	    
	    System.out.println();
	    System.out.println("Prior Baseline performance on ["+corpus+"]");
	    System.out.println(TP);
	    System.out.println(PriorCommon);
	    System.out.println(P  +"\t "+ R +"\t "+ F);
	}
		
	
}
