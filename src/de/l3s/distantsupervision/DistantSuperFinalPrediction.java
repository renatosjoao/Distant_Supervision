package de.l3s.distantsupervision;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.CSVReader;

import de.l3s.loaders.DataLoaders;
import de.l3s.loaders.DataLoaders_ACE2004;
import de.l3s.loaders.DataLoaders_AQUAINT;
import de.l3s.loaders.DataLoaders_CONLL;
import de.l3s.loaders.DataLoaders_Derczynski;
import de.l3s.loaders.DataLoaders_GERDAQ;
import de.l3s.loaders.DataLoaders_IITB;
import de.l3s.loaders.DataLoaders_KORE50;
import de.l3s.loaders.DataLoaders_MSNBC;
import de.l3s.loaders.DataLoaders_N3RSS500;
import de.l3s.loaders.DataLoaders_NEEL;
import de.l3s.loaders.DataLoaders_Spotlight;
import de.l3s.loaders.DataLoaders_N3Reuters128;
import de.l3s.loaders.DataLoaders_WP;



public class DistantSuperFinalPrediction {
	
	public static void main(String[] args) throws Exception{
		String corpus = "ace2004";
//		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
//		String corpus = "conll";   //Babelfy > Ambiverse > Spotlight >  Tagme
//		String corpus = "derczynkski";
//		String corpus = "gerdaq";  // Tagme > Babelfy > Ambiverse 
//		String corpus = "iitb";    //Ambiverse > Spotlight >  Tagme > Babelfy
//		String corpus = "kore50";
// 		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
//		String corpus = "N3News100";   			// 	GERMAN NEWS
//		String corpus = "N3Reuters128";
//		String corpus = "N3RSS500";
//	    String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
//		String corpus = "spotlight" ;
// 		String corpus = "wp";      // Tagme > Babelfy > Ambiverse > Spotlight
		DataLoaders d = new DataLoaders();
		if(corpus.equalsIgnoreCase("ace2004")){
			d = DataLoaders_ACE2004.getInstance();
		}
		if(corpus.equalsIgnoreCase("aquaint")){
			d = DataLoaders_AQUAINT.getInstance();
		}
		if(corpus.equalsIgnoreCase("conll")){
			d = DataLoaders_CONLL.getInstance();
		}
		if(corpus.equalsIgnoreCase("derczynkski")){
			d = DataLoaders_Derczynski.getInstance();
		}
		if(corpus.equalsIgnoreCase("gerdaq")){
			d = DataLoaders_GERDAQ.getInstance();
		}
		if(corpus.equalsIgnoreCase("iitb")){
			d = DataLoaders_IITB.getInstance();
		}
		if(corpus.equalsIgnoreCase("kore50")){
			d = DataLoaders_KORE50.getInstance();
		}
		if(corpus.equalsIgnoreCase("msnbc")){
			d = DataLoaders_MSNBC.getInstance();
		}
		if(corpus.equalsIgnoreCase("N3Reuters128")){
			d = DataLoaders_N3Reuters128.getInstance();
		}
		if(corpus.equalsIgnoreCase("N3RSS500")){
			d = DataLoaders_N3RSS500.getInstance();
		}
		if(corpus.equalsIgnoreCase("neel")){
			d = DataLoaders_NEEL.getInstance();
		}
		if(corpus.equalsIgnoreCase("spotlight")){
			d = DataLoaders_Spotlight.getInstance();
		}
		if(corpus.equalsIgnoreCase("wp")){
			d = DataLoaders_WP.getInstance();
		}

//		String[] feat = new String[]{"i","ii","iii","iv","v","vi","vii","viii","ix","x","xi","xii","xiii","xiv","xv","xvi","xvii","xviii"};
//		for (String f : feat){
		
		
//		 double[] p = new double[]{1.0,5.0,10.0,15.0,20.0,30.0,40.0,50.0,60.0,70.0,80.0,90.0,100.0};
		double[] p = new double[]{10.0,20.0,40.0,80.0,100.0};
//		
//		String cl = "smo";
		
		for(double r : p){
			
//  			predictMSCLOOSE(d,corpus, r);
  			predictMSCSTRICT(d,corpus, r);
 			

		}

		
}
	
	/**
	 *
	 * @param output
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	@SuppressWarnings("deprecation")
	private static void predictMSCSTRICT(DataLoaders d, String corpus,double percent) throws NumberFormatException, Exception{
		OutputStreamWriter predOut = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+"."+percent+".STRICT.pred.out"), StandardCharsets.UTF_8);
		
		TreeMap<String,String> GT_MAP = d.getGT_MAP();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap();
		TreeMap<String,String> BabMAP = d.getBabelfyMap();
		TreeMap<String,String> TagMAP = d.getTagmeMap();
		
//		/** initially writing the mentions that do not need predictions **/
		String testcsvFile = "./resources/ds/"+corpus+"/dataset.meta.test."+corpus+".csv";
//		
		int ZeroTools = 0;
		CSVReader csvparser = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream(testcsvFile), StandardCharsets.UTF_8)),',','\''); 
		String[] row = null;
        while ((row = csvparser.readNext() ) != null) {
        	String docid = row[0].toLowerCase();
        	docid = docid.replaceAll("\'", "");
        	docid = docid.replaceAll("\"", "");
        	String mention = row[1].toLowerCase();
        	String offset = row[2];
        	String k = docid+"\t"+mention.toLowerCase()+"\t"+offset;
        	String GTLINK = GT_MAP.get(k); 
        	String Alink = AmbMAP.get(k);
        	String Blink = BabMAP.get(k);
        	String Tlink = TagMAP.get(k);

        	//# CASE 0 -  There is no link
        	/* This is the case when the mention is NOT recognized by any of the tools  -  No prediction needed */
        	if((!AmbMAP.containsKey(k)) && (!BabMAP.containsKey(k)) && (!TagMAP.containsKey(k))){
        		ZeroTools++;
        		continue;
        	}
        	//# CASE 1  - One EL tool -- Ambiverse
        	if( (AmbMAP.containsKey(k)) && (!BabMAP.containsKey(k)) && (!TagMAP.containsKey(k)) ){
//        		predOut.write(k+"\t"+Alink+"\n");
        		continue;
        	}
        	//# CASE 1  - One EL tool -- Babelfy
        	if( (!AmbMAP.containsKey(k)) && (BabMAP.containsKey(k)) && (!TagMAP.containsKey(k)) ){
//        		predOut.write(k+"\t"+Blink+"\n");
        		continue;
        	}
        	//# CASE 1  - One EL tool -- Tagme
        	if( (!AmbMAP.containsKey(k)) && (!BabMAP.containsKey(k)) && (TagMAP.containsKey(k)) ){
//        		predOut.write(k+"\t"+Tlink+"\n");
        		continue;
        	}
        	//# CASE 2  - Two EL tools -- Ambiverse and Babelfy
        	if( (AmbMAP.containsKey(k)) && (BabMAP.containsKey(k)) && (!TagMAP.containsKey(k)) ){
        		if(Alink.equalsIgnoreCase(Blink)){
        			predOut.write(k+"\t"+Alink+"\n");
        			continue;
        		}
        	}
        	// CASE 2  - Two EL tools -- Ambiverse and Tagme
        	if( (AmbMAP.containsKey(k)) && (!BabMAP.containsKey(k)) && (TagMAP.containsKey(k)) ){
        		if(Alink.equalsIgnoreCase(Tlink)){
        			predOut.write(k+"\t"+Alink+"\n");
        			continue;
        		}
        	}
        	// CASE 2  - Two EL tools -- Babelfy and Tagme
        	if( (!AmbMAP.containsKey(k)) && (BabMAP.containsKey(k)) && (TagMAP.containsKey(k)) ){
        		if(Blink.equalsIgnoreCase(Tlink)){
        			predOut.write(k+"\t"+Blink+"\n");
        			continue;
        		}
        	}
        	// CASE 3  - Three  EL tools -- Ambiverse Babelfy and Tagme
        	if( (AmbMAP.containsKey(k)) && (BabMAP.containsKey(k)) && (TagMAP.containsKey(k)) ){
        		if( (Alink.equalsIgnoreCase(Blink)) && (Alink.equalsIgnoreCase(Tlink)) && (Blink.equalsIgnoreCase(Tlink)) ){
        			predOut.write(k+"\t"+Blink+"\n");
        			continue;
        		}
        	}
        }
        csvparser.close();
//        System.out.println("# mentions recognized by 0 tools : "+ ZeroTools);
        /** **/
        /** Here I am writing the mentions from predictions **/

		TreeMap<String,String> AmbR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.oneclass."+corpus+".R1.occ.Amb."+percent+".predictions");
		TreeMap<String,String> BabR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.oneclass."+corpus+".R1.occ.Bab."+percent+".predictions");
		TreeMap<String,String> TagR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.oneclass."+corpus+".R1.occ.Tag."+percent+".predictions");
		
		TreeMap<String,String> MultilabelPRED =  loadMultilabelClassifierPredictions("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+"."+percent+".predictions");
	
		csvparser = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+"."+percent+".predictions"), StandardCharsets.UTF_8)), ',','\''); 
        while ((row = csvparser.readNext() ) != null) {
        	String docid = row[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
		    String mention = row[1].toLowerCase();
		    String offset = row[2];
	        
	        String Alink = "NULL";
	        String Blink = "NULL";
	        String Tlink = "NULL";

	        String k = docid+"\t"+mention+"\t"+offset;
	    	
	        double predT = 0;
	    	double predB = 0;
	    	double predA = 0;
	        
	    	double confT = 0;
        	double confB = 0;
        	double confA = 0;
        	
	    	if(MultilabelPRED.containsKey(k)){
	    		row = MultilabelPRED.get(k).split("\t");
	        	confT = Double.parseDouble(row[row.length -1]);
	        	confB = Double.parseDouble(row[row.length -2]);
	        	confA = Double.parseDouble(row[row.length -3]);
	        	predT = Double.parseDouble(row[row.length -4]);
	        	predB = Double.parseDouble(row[row.length -5]);
	        	predA = Double.parseDouble(row[row.length -6]);
	        	
	        	if(AmbMAP.containsKey(k)){
		            Alink = AmbMAP.get(k).toLowerCase();
		        }
		        if(BabMAP.containsKey(k)){
		            Blink = BabMAP.get(k).toLowerCase();
		        }
		        if(TagMAP.containsKey(k)){
		            Tlink = TagMAP.get(k).toLowerCase();
		        }
	    	}
	        //# CASE 0 -  There is no link
	        /* This is the case when the mention is NOT recognized by any of the tools  -  No prediction needed */
	        if((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	continue;
	        }
		    
	        // # CASE 1  - There is 1 link   - Ambiverse
	        /* This is the case when the mention is recognized by ONE of the tools  -  Ambiverse */
	        if ((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	if(AmbR1.containsKey(k)){
	        		Alink = AmbR1.get(k).toLowerCase();
	        		predOut.write(k+"\t"+Alink+"\n");
	        	}
	        	continue;
	        }
	        // # CASE 1  - There is 1 link  - Babelfy  
	        /* This is the case when the mention is recognized by ONE of the tools  -  Babelfy */
	        if ((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	if(BabR1.containsKey(k)){
        			predOut.write(k+"\t"+Blink+"\n");
        		}
	        	continue;
	        }
	        // # CASE 1  - There is 1 link   - Tagme
	        /* This is the case when the mention is recognized by ONE of the tools  -  Tagme */
	        if ((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	if(TagR1.containsKey(k)){
	        		predOut.write(k+"\t"+Tlink+"\n");
	        	}
	        	continue;
	        }
//	        //# CASE 2 There are 2 links - Ambiverse and Babelfy
//	        /* This is the case when the mention is recognized by TWO of the tools - Ambiverse and Babelfy */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	if((predA == 0) && (predB == 0)){
	        		if (confA > confB ){
	        			predOut.write(k+"\t"+Alink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Blink+"\n");
	        		}
	        		continue;
	        	}
	        	if((predA == 1) && (predB == 0)){
	        		predOut.write(k+"\t"+Alink+"\n");
	        		continue;
	        	}
	        	if((predA == 0) && (predB == 1)){ 
	        		predOut.write(k+"\t"+Blink+"\n");
	        		continue;
	        	}
	        	if((predA == 1) && (predB == 1)){ 
	        		if (confA > confB ){
	        			predOut.write(k+"\t"+Alink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Blink+"\n");
	        		}
	        		continue;
	        	}
	        }
//	        //# CASE 2 There are 2 links  - Ambiverse and Tagme  
//	        /* This is the case when the mention is recognized by TWO of the tools - Ambiverse and Tagme */
	        if((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	if((predA == 0) && (predT == 0)){
	        		if (confA > confT ){
	        			predOut.write(k+"\t"+Alink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Tlink+"\n");
	        		}
	        		continue;
	        	}
	        	if((predA == 1) && (predT == 0)){
	        		predOut.write(k+"\t"+Alink+"\n");
	        		continue;
	        	}
	        	if((predA == 0) && (predT == 1)){
	        		predOut.write(k+"\t"+Tlink+"\n");
	        		continue;
	        	}
	        	if((predA == 1) && (predT == 1)){
	        		if (confA > confT ){
	        			predOut.write(k+"\t"+Alink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Tlink+"\n");
	        		}
	        		continue;
	        	}
	       }
	        
	        //# CASE 2 There are 2 links  - Babelfy and Tagme 
	        /* This is the case when the mention is recognized by TWO of the tools - Babelfy and Tagme */
	        if((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	if((predB == 0) && (predT == 0)){
	        		if (confB > confT ){
	        			predOut.write(k+"\t"+Blink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Tlink+"\n");
	        		}
	        		continue;
	        	}
	        	if((predB == 1) && (predT == 0)){
	        		predOut.write(k+"\t"+Blink+"\n");
	        		continue;
	        	}
	        	if((predB == 0) && (predT == 1)){
	        		predOut.write(k+"\t"+Tlink+"\n");
	        		continue;
	        	}
	        	if((predB == 1) && (predT == 1)){
	        		if (confB > confT ){
	        			predOut.write(k+"\t"+Blink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Tlink+"\n");
	        		}
	        		continue;
	        	}
	        }
	        //# CASE 3 There are 3 links
	        /* This is the case when the mention is recognized by THREE of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	// No EL predicted
	        	if ((predA == 0) && (predB == 0) && (predT == 0)){
	        		if ((confA >= confB ) && (confA >= confT )){
	        			predOut.write(k+"\t"+Alink+"\n");
	        			continue;
	        		}
	        		if ((confB >= confA ) && (confB >= confT )){
	        			predOut.write(k+"\t"+Blink+"\n");
	        			continue;
	        		}
	        		if ((confT >= confA ) && (confT >= confB )){
	        			predOut.write(k+"\t"+Tlink+"\n");
	        			continue;
	        		}
	        		
	        	}
	        	//Ambiverse
	        	if ((predA == 1) && (predB == 0) && (predT == 0)){
	        		predOut.write(k+"\t"+Alink+"\n");
	        		continue;
	        	}
	        	//Babelfy
	        	if ((predA == 0) && (predB == 1) && (predT == 0)){
	        		predOut.write(k+"\t"+Blink+"\n");
	        		continue;
	        	}
	        	//Tagme
	        	if ((predA == 0) && (predB == 0) && (predT == 1)){
	        		predOut.write(k+"\t"+Tlink+"\n");
	        		continue;
	        	}
	        	//Ambiverse and Babelfy
	        	if ((predA == 1) && (predB == 1) && (predT == 0)){
	        		if (confA > confB ){
	        			predOut.write(k+"\t"+Alink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Blink+"\n");
	        		}
	        		continue;
	        	}
	        	//Ambiverse and Tagme
	        	if ((predA == 1) && (predB == 0) && (predT == 1)){
	        		if (confA > confT ){
	        			predOut.write(k+"\t"+Alink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Tlink+"\n");
	        		}
	        		continue;
	        	}
	        	//Babelfy and Tagme
	        	if ((predA == 0) && (predB == 1) && (predT == 1)){
	        		if (confB > confT ){
	        			predOut.write(k+"\t"+Blink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Tlink+"\n");
	        		}
	        		continue;
	        	}
	        	
	        	//Ambiverse, Babelfy and Tagme 
	        	if ((predA == 1) && (predB == 1) && (predT == 1)){
	        		if ((confA >= confB ) && (confA >= confT )){
	        			predOut.write(k+"\t"+Alink+"\n");
	        			continue;
	        		}
	        		if ((confB >= confA ) && (confB >= confT )){
	        			predOut.write(k+"\t"+Blink+"\n");
	        			continue;
	        		}
	        		if ((confT >= confA ) && (confT >= confB )){
	        			predOut.write(k+"\t"+Tlink+"\n");
	        			continue;
	        		}
	        	}
	        }
        }
        predOut.close();
	}
	
	private static void predictMulticlassMOD(	DataLoaders d ,String corpus, double f) throws Exception{
		OutputStreamWriter predOut = new OutputStreamWriter(new FileOutputStream("./resources/"+corpus+"/dataset.multiclass."+corpus+"."+f+".pred.out"), StandardCharsets.UTF_8);
		TreeMap<String,String> MulticlassPRED =  loadMulticlassClassifierPredictions("./resources/"+corpus+"/dataset.multiclass."+corpus+"."+f+".pred");
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


		TreeMap<String,String> GT_test_MAP = d.getGT_MAP_test();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap_test();
		TreeMap<String,String> BabMAP = d.getBabelMap_test();
		TreeMap<String,String> TagMAP = d.getTagmeMap_test();
		
		int i=0;
		Iterator<?> it = GT_test_MAP.entrySet().iterator();
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
	        String GTlink = GT_test_MAP.get(k).toLowerCase();
	        String predictedTool = "";
	       
        	row = MulticlassPRED.get(k).split("\t"); 
        	predictedTool = row[row.length -1];
        	
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
//	            predOut.write(k+"\t"+Alink+"\n");
	        	continue;
	        }
	        	        
	        // # CASE 1  - There is 1 link   -  Ambiverse
	        /* This is the case when the mention is recognized by ONE of the tools  -  Ambiverse */
	        if ((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	            one_link++;
	            one_linkA++;
	            if(predictedTool.equalsIgnoreCase("AMBIVERSE")){
		        	numRECOGNIZED++;
	            	if(Alink.equalsIgnoreCase(GTlink)) {
	            		TP++;
	            		one_linkA_correct++;
	            	}
//	            	predOut.write(k+"\t"+Alink+"\n"); 
	            }
	            continue;
	        }
	        // # CASE 1  - There is 1 link   -  Babelfy
	        /* This is the case when the mention is recognized by ONE of the tools  -  Babelfy */
	        if ((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	one_link++;
	        	one_linkB++;
	        	if(predictedTool.equalsIgnoreCase("BABELFY")){ 
		        	numRECOGNIZED++;
	        		if(Blink.equalsIgnoreCase(GTlink)){
	        			TP++;  
		            	one_linkB_correct++;
	        		}  
//	        		predOut.write(k+"\t"+Blink+"\n");
	        	}
	        	continue;
	        }
	        // # CASE 1  - There is 1 link   -  Tagme 
	        /* This is the case when the mention is recognized by ONE of the tools  -  Tagme */
	        if ((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	one_link++;
	        	one_linkT++;
	        	if(predictedTool.equalsIgnoreCase("TAGME")){
	        		numRECOGNIZED++;
	        		if(Tlink.equalsIgnoreCase(GTlink)){
	        			TP+=1;
	        			one_linkT_correct++;
	        			}
//	        		predOut.write(k+"\t"+Tlink+"\n");  continue;  
	        	}
	        	continue;
	        }
	        
	        //# CASE 2 There are 2 links - Ambiverse and Babelfy
	        /* This is the case when the mention is recognized by TWO of the tools - Ambiverse and Babelfy */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
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
	        	if(predictedTool.equalsIgnoreCase("AMBIVERSE")){ 
	        		numRECOGNIZED++;
	        		if(Alink.equalsIgnoreCase(GTlink)) { 
	        			TP+=1; 
	        		}  
	        		continue;	
	        	}
	        	if(predictedTool.equalsIgnoreCase("BABELFY")){
	        		numRECOGNIZED++;
	        		if(Blink.equalsIgnoreCase(GTlink)){  
	        			TP+=1;  
	        			}	        		
	        		continue; 
	        	}
	        	if(predictedTool.equalsIgnoreCase("TAGME")){ //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	        		numRECOGNIZED++;
	              	
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
	            continue; 
	        	}//predOut.write(k+"\t"+Blink+"\n");
        }
	        //# CASE 2 There are 2 links - Ambiverse and Tagme
	        /* This is the case when the mention is recognized by TWO of the tools - Ambiverse and Tagme */
	        if((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
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
	            if(predictedTool.equalsIgnoreCase("AMBIVERSE")){ 	        		
	            	numRECOGNIZED++;
	            	if(Alink.equalsIgnoreCase(GTlink)) {  
	            		TP+=1; 
	            	}  
	            	predOut.write(k+"\t"+Alink+"\n"); 
	            	continue;	
	            }
	            if(predictedTool.equalsIgnoreCase("BABELFY")){//# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 
	            	numRECOGNIZED++;
	            	
 

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
	            	continue;
	            	
	            	
	            } 
	            if(predictedTool.equalsIgnoreCase("TAGME")){
	            	numRECOGNIZED++;
	            	if(Tlink.equalsIgnoreCase(GTlink)){ 
	            		TP+=1;  
	            	} 
	            	predOut.write(k+"\t"+Tlink+"\n");  
	            	continue;  
	            }
	        }	
	        //# CASE 2 There are 2 links - Babelfy and Tagme 
	        /* This is the case when the mention is recognized by TWO of the tools - Babelfy and Tagme */
	        if((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
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
	            if(predictedTool.equalsIgnoreCase("AMBIVERSE")){ //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	            	numRECOGNIZED++;

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
		    			if(Blink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
		    			 // predOut.write(k+"\t"+Blink+"\n");
		    			 continue;	
		    		}
		    		if(corpus.equalsIgnoreCase("neel")){//	    		String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
		    			 if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
		    			 // predOut.write(k+"\t"+Blink+"\n");
		    			 continue;	
		    		}
	            continue; 
	            } 
	            if(predictedTool.equalsIgnoreCase("BABELFY")){
		        	numRECOGNIZED++;
	            	if(Blink.equalsIgnoreCase(GTlink)){ 
	            		TP+=1;  
	            	} 
	            	predOut.write(k+"\t"+Blink+"\n");
	            	continue; 
	            }
	            if(predictedTool.equalsIgnoreCase("TAGME")){
	            	numRECOGNIZED++;
	            	if(Tlink.equalsIgnoreCase(GTlink)){ 
	            		TP+=1;  
	            	} 
	            	predOut.write(k+"\t"+Tlink+"\n"); 
	            	continue;  
	            }
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
	        	
	        	if(predictedTool.equalsIgnoreCase("AMBIVERSE")){if(Alink.equalsIgnoreCase(GTlink)) {  TP+=1;} predOut.write(k+"\t"+Alink+"\n");  continue;	}
	            if(predictedTool.equalsIgnoreCase("BABELFY")){ if(Blink.equalsIgnoreCase(GTlink)){   TP+=1;  }  predOut.write(k+"\t"+Blink+"\n"); continue;  }
	            if(predictedTool.equalsIgnoreCase("TAGME")){ if(Tlink.equalsIgnoreCase(GTlink)){  TP+=1;  } predOut.write(k+"\t"+Tlink+"\n");  continue;  }
	        }
		}
		predOut.flush();
		predOut.close();
		System.out.println();
//		System.out.println();
		System.out.println("-----------------------------------------");
		System.out.println("GT mentions recognised by 0/3 systems :" +zero_link);
		System.out.println("-----------------------------------------");
		System.out.println("GT mentions recognised by 1/3 systems :" +one_link);
		System.out.println("-----------------------------------------");
		System.out.println("......GT mentions recognised by Amb :" +one_linkA  + "\tTP: "+one_linkA_correct ) ;
		System.out.println("......GT mentions recognised by Bab :" +one_linkB  + "\tTP: "+one_linkB_correct ) ;
		System.out.println("......GT mentions recognised by Tag :" +one_linkT  + "\tTP: "+one_linkT_correct ) ;
		System.out.println("...... ");
		System.out.println("......  # mentions need binary class prediction :" +one_link);
		System.out.println("......  # mentions predicted by binary Amb clf :" +one_linkPredictedA);
		System.out.println("......  # mentions predicted by binary Bab clf :" +one_linkPredictedB);
		System.out.println("......  # mentions predicted by binary Tag clf :" +one_linkPredictedT);
		System.out.println("-----------------------------------------");
		System.out.println("GT mentions recognised by 2/3 systems :"+two_links);
		System.out.println("-----------------------------------------");
		System.out.println("......GT mentions recognised by (Amb & Bab) :" +two_linksAB);
		System.out.println("......GT mentions recognised by (Amb & Tag) :" +two_linksAT);
		System.out.println("......GT mentions recognised by (Bab & Tag) :" +two_linksBT);
		System.out.println(".........  2 systems provide the same entity :" +two_links_equal);
		System.out.println(".........  2 systems provide different entity :" +two_links_diff);
		System.out.println("......  # mentions need multiclass prediction :" +two_links_diff);
		System.out.println("...........# mentions need multiclass prediction (Amb & Bab) :" +predictions_neededAB);
		System.out.println("...........# mentions need multiclass prediction (Amb & Tag) :" +predictions_neededAT);
		System.out.println("...........# mentions need multiclass prediction (Bab & Tag) :" +predictions_neededBT);
		System.out.println("......  # mentions need binary class prediction :" + two_links_equal );
		System.out.println("...........# mentions predicted by binary (Amb & Bab) clf:" +two_linksPredictedAB);
		System.out.println("...........# mentions predicted by binary (Amb & Tag) clf:" +two_linksPredictedAT);
		System.out.println("...........# mentions predicted by binary (Bab & Tag) clf:" +two_linksPredictedBT);
		System.out.println("-----------------------------------------");
		System.out.println("GT mentions recognised by 3/3 systems :"+three_links);
		System.out.println("-----------------------------------------");
		System.out.println(".........  3 systems provide the same entity :"+three_links_equal);
		System.out.println(".........  2 systems provide the same entity :"+three_links_2equal);
		System.out.println(".........  each system provides a different entity :"+three_links_diff);
		System.out.println("......  # mentions need multiclass prediction :" +predictions_neededABT);
		System.out.println("......  # mentions need binary class prediction :" + three_links_equal );
		System.out.println("...........# mentions predicted by binary class :" +three_linksPredicted);
		System.out.println("-----------------------------------------");
		System.out.println("GT mentions that need prediction :" +predictions_needed);
//		System.out.println("GT mentions that need prediction :" +(predictions_neededAB+predictions_neededAT+predictions_neededBT+predictions_neededABT));
		System.out.println("-----------------------------------------");
		System.out.println("......The correct entity is provided by at least 1 system :"+correct_provided);
		System.out.println("......The correct entity is not provided by at least 1 system :"+(predictions_needed-correct_provided));
		System.out.println("-----------------------------------------");
		System.out.println("TOTAL "+(zero_link+one_link+two_links+three_links) );
//		System.out.println();
		System.out.println();

		double P = 0.0;//
		double R = 0.0;
		double F = 0.0;
		
		P =  (double) TP / (double)numRECOGNIZED;
		R =  (double) TP/(double) GT_test_MAP.keySet().size();
		F = 2*((P*R)/(P+R));
		System.out.println(f+"\t"+P+"\t"+R+"\t"+F);
		System.out.println("TP:"+TP);
		System.out.println("numRecog:"+numRECOGNIZED);
		
	}
	
	/**
	 *
	 * @param corpus
	 * @param classifier
	 * @param f
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	private static void predictMultilabelEXTENDED_LOOSE(DataLoaders d,String corpus,double percentage) throws NumberFormatException, Exception{
		TreeMap<String,String> AmbR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Amb."+percentage+".pred");
		TreeMap<String,String> BabR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Bab."+percentage+".pred");
		TreeMap<String,String> TagR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Tag."+percentage+".pred");

		int countNOTpred = 0;
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
		
		TreeMap<String,String> GT_test_MAP = d.getGT_MAP_test();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap_test();
		TreeMap<String,String> BabMAP = d.getBabelMap_test();
		TreeMap<String,String> TagMAP = d.getTagmeMap_test();
		
		CSVReader csvparser = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+"."+percentage+".predictions"), StandardCharsets.UTF_8)),',','\'');  
		String[] row = null;
        while ((row = csvparser.readNext() ) != null) {
        	String docid = row[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
		    String mention = row[1].toLowerCase();
//		    mention = mention.replaceAll("\'", "");
//		    mention = mention.replaceAll("\"", "");
		    String offset = row[2];
		    
		    double confT = Double.parseDouble(row[row.length -1]);
		    double confB = Double.parseDouble(row[row.length -2]);
		    double confA = Double.parseDouble(row[row.length -3]);
		    
		    double predT = Double.parseDouble(row[row.length -4]);
		    double predB = Double.parseDouble(row[row.length -5]);
		    double predA = Double.parseDouble(row[row.length -6]);
		    
//		    
//			Pattern p = Pattern.compile("\'([^\"]*)\'");
//			Matcher m = p.matcher(docid);
//			while (m.find()) {
//				docid = m.group(1);
//			}
//			m = p.matcher(mention);
//			while (m.find()) {
//				mention = m.group(1).toLowerCase();
//			}
		    
		    String predictedTool = row[row.length -1];   ///  *** Predicted Tool  ***
		    
		    String k = docid+"\t"+mention+"\t"+offset;
		    String Alink = "NULL";
	        String Blink = "NULL";
	        String Tlink = "NULL";
	        
	        String GTlink = GT_test_MAP.get(k).toLowerCase();

	        if(AmbMAP.containsKey(k)){
	            Alink = AmbMAP.get(k);
	        }
	        if(BabMAP.containsKey(k)){
	            Blink = BabMAP.get(k);
	        }
	        if(TagMAP.containsKey(k)){
	            Tlink = TagMAP.get(k);
	        }

		    //# CASE 0 -  There is no link
		    /* This is the case when the mention is NOT recognized by any of the tools  -  No prediction needed */
		    if( (!AmbMAP.containsKey(k)) && (!BabMAP.containsKey(k))  && (!TagMAP.containsKey(k)) ) {
	        	zero_link++;
//		        check.write("NOT RECOGN:"+"\t"+key+"\tSELECTION:"+Blink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
	        	continue;
		    }
		    
		    // # CASE 1  - There is 1 link  --- Ambiverse
		     /* This is the case when the mention is recognized by ONE of the tools   - Check prediction from binary classifier */
		    if ((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	one_link++;
	        	one_linkA++;
	        	
	        	if(AmbR1.containsKey(k)){
	        		numRECOGNIZED++;
	        		one_linkPredicted++;
	        		one_linkPredictedA++;
	        		Alink = AmbMAP.get(k).toLowerCase();
	        		if(Alink.equalsIgnoreCase(GTlink)){
	        			TP+=1;
	            		one_linkA_correct++;
	        		}
	        	}
	        	continue;
		    }
		    // # CASE 1  - There is 1 link  --- Babelfy
		    /* This is the case when the mention is recognized by ONE of the tools - Check prediction from binary classifier */
		    if ((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
		    	one_link++;
	        	one_linkB++;
	        	if(BabR1.containsKey(k)){
	        		numRECOGNIZED++;
	        		one_linkPredicted++;
	        		one_linkPredictedB++;
	        		Blink = BabMAP.get(k).toLowerCase();
	        		if(Blink.equalsIgnoreCase(GTlink)){
	        			TP+=1;
		            	one_linkB_correct++;
	        		}
	        	}
	        	continue;
		    }
		    // # CASE 1  - There is 1 link  --- Tagme
		    /* This is the case when the mention is recognized by ONE of the tools  -  No prediction needed */
		    if ((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
		    	one_link++;
		    	one_linkT++;
		    	if(TagR1.containsKey(k)){
		    		numRECOGNIZED++;
		       		one_linkPredicted++;
	        		one_linkPredictedT++;
	        		Tlink = TagMAP.get(k).toLowerCase();
	        		if(Tlink.equalsIgnoreCase(GTlink)){
	        			TP+=1;
	        			one_linkT_correct++;

	        		}
		    	}
		    	continue;
		    }
		  //# CASE 2 There are 2 links --- Ambiverse and Babelfy
	        /* This is the case when the mention is recognized by TWO of the tools */
		    if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ) {
		    	two_links++;
	        	two_linksAB++;
	        	if (Alink.equalsIgnoreCase(Blink)){
	        		two_links_equal++;
		    		if((AmbR1.containsKey(k)) || ((BabR1.containsKey(k)))){
		    			numRECOGNIZED++;
		        		two_linksPredicted++;
		        		two_linksPredictedAB++;
		        		Alink = AmbMAP.get(k).toLowerCase();
		        		Blink = BabMAP.get(k).toLowerCase();
		    			if(Alink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    	}else{
		    		predictions_needed++;
	            	predictions_neededAB++;
	            	two_links_diff++;
			        //IF THE LINKS ARE NOT THE SAME THEN USE TRADITIONAL PREDICTION
		    		numRECOGNIZED++;
		    		if((predA == 0) && (predB == 0) && (predT == 0)) {
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
		            continue; 
		    		}
		    		if ((predA ==1) && (predB == 0) ){                    
		    			if(Alink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    		if ((predA ==0) && (predB == 1) ) {                            
		    			if(Blink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    		if (confA > confB ){
		    			if(Alink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}else{
		    			if(Blink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    	}
		    }
		  //# CASE 2 There are 2 links --- Ambiverse and Tagme
	        /* This is the case when the mention is recognized by TWO of the tools */
		    if((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ) {
		    	two_links++;
	        	two_linksAT++;
		    	if (Alink.equalsIgnoreCase(Tlink)){
	            	two_links_equal++;
		    		if((AmbR1.containsKey(k))  ||  ((TagR1.containsKey(k)))){
		    			numRECOGNIZED++;
		    			two_linksPredicted++;
		            	two_linksPredictedAT++;
		            	Alink = AmbMAP.get(k).toLowerCase();
	        			Tlink = TagMAP.get(k).toLowerCase();
		    			if(GTlink.equalsIgnoreCase(Alink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    	}else{
		    		predictions_needed++;
	            	predictions_neededAT++;
	            	two_links_diff++;
			        //IF THE LINKS ARE NOT THE SAME THEN USE TRADITIONAL PREDICTION
		    		numRECOGNIZED+=1;
		    		if ((predA == 0) && (predB == 0) && (predT == 0)){
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
		            	continue;
		    		}
		    		if ((predA ==1) && (predT ==0) ){
		    			if(Alink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		              	
		    		if((predA ==0) && (predT ==1)){
		    			if(Tlink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    		
		    		if (confA >= confT ){
		    			if(Alink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}else{
		    			if(Tlink.equalsIgnoreCase(GTlink)){
		        		TP+=1;
		    			}
		    			continue;
		    		}
		    	}
		    }
		    //# CASE 2 There are 2 links --- Babelfy and Tagme
	        /* This is the case when the mention is recognized by TWO of the tools */
		    if((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ) {
		    	two_links++;
	        	two_linksBT++;
		    	if (Blink.equalsIgnoreCase(Tlink)){
	            	two_links_equal++;
		    		if((BabR1.containsKey(k)) || ((TagR1.containsKey(k)))){
		    			numRECOGNIZED+=1;
		    			two_linksPredicted++;
		            	two_linksPredictedBT++;
		            	Blink = BabMAP.get(k).toLowerCase();
	        			Tlink = TagMAP.get(k).toLowerCase();
		    			if(Blink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}		
		    	}else{
		    		predictions_needed++;
	            	predictions_neededBT++;
	            	two_links_diff++;
	            	//IF THE LINKS ARE NOT THE SAME THEN USE TRADITIONAL PREDICTION
		    		numRECOGNIZED++;
		    		if((predA ==0) && (predB == 0) && (predT == 0)){
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
			    			if(Blink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
			    			 // predOut.write(k+"\t"+Blink+"\n");
			    			 continue;	
			    		}
			    		if(corpus.equalsIgnoreCase("neel")){//	    		String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
			    			 if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
			    			 // predOut.write(k+"\t"+Blink+"\n");
			    			 continue;	
			    		}
		            continue;                               
		    		}
		    		if( (predB ==1) && (predT ==0) ){
		    			if(Blink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    		if( (predB ==0) && (predT ==1 ) ){
		    			if(Tlink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    		if(confB >= confT){
		    			if(Blink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}else{
		    			if(Tlink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    	}	
		    }
		  //# CASE 3 There are 3 links
	        /* This is the case when the mention is recognized by THREE of the tools */
		    if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ) {
		    	three_links++;
		    	//The 3 links are the same
	        	if ( (Alink.equalsIgnoreCase(Blink)) &&  (Alink.equalsIgnoreCase(Tlink)) &&  (Blink.equalsIgnoreCase(Tlink)) ){
	        		three_links_equal++;

	        		if((AmbR1.containsKey(k)) || ((BabR1.containsKey(k))) || ((TagR1.containsKey(k)))){
	        			numRECOGNIZED++;
	        		 	three_linksPredicted++;
		            	three_linksPredictedABT++;
		            	
		            	Alink = AmbMAP.get(k).toLowerCase();
	        			
		        		Blink = BabMAP .get(k).toLowerCase();
		        		
	        			Tlink = TagMAP.get(k).toLowerCase();
	        		
	        			if((Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
		        			TP+=1;
		        		}
	        			continue;
	        		}
	        	}else{
            	//IF THE LINKS ARE NOT THE SAME THEN USE TRADITIONAL PREDICTION
	        	numRECOGNIZED++;
        		predictions_needed++;
            	predictions_neededABT++;
            	if ((predA ==1) && (predB ==0) && (predT ==0)){
            		if(Alink.equalsIgnoreCase(GTlink)){
            			TP+=1;
            		}
            		continue;				        								        					        					        					        					        		        	
            	}
            	if ((predA == 0) && (predB ==1) && (predT ==0)){
            		if(Blink.equalsIgnoreCase(GTlink)){
            			TP+=1;
            		}
            		continue;
            	}
            	if((predA == 0) && (predB ==0) && (predT ==1)){
            		if(Tlink.equalsIgnoreCase(GTlink)){
            			TP+=1;
            		}
            		continue;
            	}
            	if((predA ==1) && (predB ==1) && (predT ==0)) {
            		if(confA >= confB){
            			if(Alink.equalsIgnoreCase(GTlink)){
            				TP+=1;
            			}
            			continue;
            		}else{
            			if(Blink.equalsIgnoreCase(GTlink)){
            				TP+=1;
            			}
            			continue;
            		}
            	}   
            	if((predA ==1) && (predB ==0) && (predT ==1)){
            		if(confA >= confT){
            			if(Alink.equalsIgnoreCase(GTlink)){
            				TP+=1;
            			}
            			continue;
            		}else{
            			if(Tlink.equalsIgnoreCase(GTlink)){
            				TP+=1;
            			}
            			continue;
            		}
            	}
            	if((predA ==0) && (predB ==1) && (predT ==1)){
            		if (confB >= confT){
            			if (Blink.equalsIgnoreCase(GTlink)){
            				TP+=1;
//	                            status = "CORRECT:"
            			}
//	                        check.write(status+"\t"+key+"\tSELECTION:"+Blink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
            			continue;
            		}else{
//	                        predicted.write(key+ "\t" + Tlink+"\n")
            			if(Tlink.equalsIgnoreCase(GTlink)){
            				TP+=1;
//	                            status = "CORRECT:"
            			}
//	                        check.write(status+"\t"+key+"\tSELECTION:"+Tlink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
            			continue;
            		}
            	}
            	if( (predA ==1) && (predB ==1) && (predT ==1) ){
            		
            		if( (confB >= confA) && (confB >= confT)){
//	                        predicted.write(key+ "\t" + Blink+"\n")
            			if(Blink.equalsIgnoreCase(GTlink)){
            				TP+=1;
//	                            status = "CORRECT:"
            			}
//	                        check.write(status+"\t"+key+"\tSELECTION:"+Blink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
            			continue;
            		}
            		if((confA >= confB) && (confA >= confT)){
//	                        predicted.write(key+ "\t" + Alink+"\n")                        
            			if(Alink.equalsIgnoreCase(GTlink)){
            				TP+=1;
//	                            status = "CORRECT:"
            			}
//	                        check.write(status+"\t"+key+"\tSELECTION:"+Alink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
            			continue;
	                    	
            		}
            		if ( (confT >= confA) && (confT >= confB) ){
//	                        largest = "tagme"
//	                        predicted.write(key+ "\t" + Tlink+"\n")
            			if(Tlink.equalsIgnoreCase(GTlink)){
            				TP+=1;
//	                            status = "CORRECT:"
            			}
//	                        check.write(status+"\t"+key+"\tSELECTION:"+Tlink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
            			continue; 
            		}
            	}
            	if ((predA ==0) && (predB ==0) && (predT ==0)){
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
           continue; 
		    }
	        	}
		    }	
       
		}
		

//		
		csvparser.close();
//		System.out.println();
//		System.out.println("-----------------------------------------");
//		System.out.println("GT mentions recognised by 0/3 systems :" +( GT_test_MAP.keySet().size() - (zero_link+one_link+two_links+three_links) ));
//		System.out.println("-----------------------------------------");
//		System.out.println("GT mentions recognised by 1/3 systems :" +one_link);
//		System.out.println("-----------------------------------------");
//		System.out.println("......GT mentions recognised by Amb :" +one_linkA);
//		System.out.println("......GT mentions recognised by Bab :" +one_linkB);
//		System.out.println("......GT mentions recognised by Tag :" +one_linkT);
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
		R =  (double) TP/(double) GT_test_MAP.keySet().size();
		F = 2*((P*R)/(P+R));

		P = round(P,3)*100.0;
		R = round(R,3)*100.0;
		F = round(F,3)*100.0;
		System.out.println(percentage+"\t&\t"+P+"\t&\t"+R+"\t&\t"+F);
		
//		System.out.println(" *********************** ");
//		System.out.println("Meta EL Prediction after classification for MultiLabel classification task ["+classifier+"]");
//		System.out.println(" *********************** ");
//		System.out.println("P:"+ P);
//		System.out.println("R:"+ R);
//		System.out.println("F:"+ F);
//		System.out.println("TP:"+TP);
//		System.out.println("numRecog:"+numRECOGNIZED);
		
	}
	
	/**
	 * 
	 * @param corpus
	 * @param classifier
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	private static void predictMultilabelEXTENDED_STRICT(DataLoaders d, String corpus,double percentage) throws NumberFormatException, Exception{
		TreeMap<String,String> AmbR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Amb."+percentage+".pred");
		TreeMap<String,String> BabR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Bab."+percentage+".pred");
		TreeMap<String,String> TagR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Tag."+percentage+".pred");

		int countNOTpred = 0;
		
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

		
		TreeMap<String,String> GT_test_MAP = d.getGT_MAP_test();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap_test();
		TreeMap<String,String> BabMAP = d.getBabelMap_test();
		TreeMap<String,String> TagMAP = d.getTagmeMap_test();
			
		CSVReader csvparser = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+"."+percentage+".predictions"), StandardCharsets.UTF_8)),',','\'' ); 
		String[] row = null;
        while ((row = csvparser.readNext() ) != null) {
        	String docid = row[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
		    String mention = row[1].toLowerCase();
		    String offset = row[2];
		    
		    double confT = Double.parseDouble(row[row.length -1]);
		    double confB = Double.parseDouble(row[row.length -2]);
		    double confA = Double.parseDouble(row[row.length -3]);
		    
		    double predT = Double.parseDouble(row[row.length -4]);
		    double predB = Double.parseDouble(row[row.length -5]);
		    double predA = Double.parseDouble(row[row.length -6]);
//			Pattern p = Pattern.compile("\'(.+([^\"]*))\'");
//			Matcher m = p.matcher(docid);
//			while (m.find()) {
//				docid = m.group(1);
//			}
//			m = p.matcher(mention);
//			while (m.find()) {
//				mention = m.group(1).toLowerCase();
//			}
		    String k = docid+"\t"+mention+"\t"+offset;
		    
		    String Alink = "NULL";
	        String Blink = "NULL";
	        String Tlink = "NULL";
	        
	        String GTlink = GT_test_MAP.get(k).toLowerCase();

	        
	        if(AmbMAP.containsKey(k)){
		    	Alink = AmbMAP.get(k).toLowerCase();
		    }
		    if(BabMAP.containsKey(k)){
		    	Blink = BabMAP.get(k).toLowerCase();
		    }
		    if(TagMAP.containsKey(k)){
		    	Tlink = TagMAP.get(k).toLowerCase();
		    }

//		    System.out.println(k+"\tA:"+Alink+"\tB:"+Blink+"\tT:"+Tlink+"\tG:"+GTlink);
		    //# CASE 0 -  There is no link
		    /* This is the case when the mention is NOT recognized by any of the tools  -  No prediction needed */
		    if( (!AmbMAP.containsKey(k)) && (!BabMAP.containsKey(k))  && (!TagMAP.containsKey(k)) ) {
	        	zero_link++;
//		        check.write("NOT RECOGN:"+"\t"+key+"\tSELECTION:"+Blink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
	        	continue;
		    }
		        
		     // # CASE 1  - There is 1 link  --- Ambiverse
		     /* This is the case when the mention is recognized by ONE of the tools   - Check prediction from binary classifier */
		    if ((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	one_link++;
	        	one_linkA++;
	        	
	        	if(AmbR1.containsKey(k)){
	        		numRECOGNIZED++;
	        		one_linkPredicted++;
	        		one_linkPredictedA++;
	        		Alink = AmbMAP.get(k).toLowerCase();
	        		if(Alink.equalsIgnoreCase(GTlink)){
	        			TP+=1;
	            		one_linkA_correct++;
	        		}
	        	}
	        	continue;
		    }
		    // # CASE 1  - There is 1 link  --- Babelfy
		    /* This is the case when the mention is recognized by ONE of the tools - Check prediction from binary classifier */
		    if ((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
		    	one_link++;
	        	one_linkB++;
	        	if(BabR1.containsKey(k)){
	        		numRECOGNIZED++;
	        		one_linkPredicted++;
	        		one_linkPredictedB++;
	        		Blink = BabMAP.get(k).toLowerCase();
	        		if(Blink.equalsIgnoreCase(GTlink)){
	        			TP+=1;
		            	one_linkB_correct++;
	        		}
	        	}
	        	continue;
		    }
		    // # CASE 1  - There is 1 link  --- Tagme
		    /* This is the case when the mention is recognized by ONE of the tools  -  No prediction needed */
		    if ((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
		    	one_link++;
		    	one_linkT++;
		    	if(TagR1.containsKey(k)){
		    		numRECOGNIZED++;
		       		one_linkPredicted++;
	        		one_linkPredictedT++;
	        		Tlink = TagMAP.get(k).toLowerCase();
	        		if(Tlink.equalsIgnoreCase(GTlink)){
	        			TP+=1;
	        			one_linkT_correct++;
	        		}
		    	}
		    	continue;
		    }
			//# CASE 2 There are 2 links --- Ambiverse and Babelfy
	        /* This is the case when the mention is recognized by TWO of the tools */
		    if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ) {
		    	two_links++;
	        	two_linksAB++;
	        	if (Alink.equalsIgnoreCase(Blink)){
	        		two_links_equal++;
		    		if((AmbR1.containsKey(k)) && ((BabR1.containsKey(k)))){
		    			numRECOGNIZED++;
		        		two_linksPredicted++;
		        		two_linksPredictedAB++;
		        		Alink = AmbMAP.get(k).toLowerCase();
		        		Blink = BabMAP.get(k).toLowerCase();
		    			if(Alink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    	}else{
		    		predictions_needed++;
	            	predictions_neededAB++;
	            	two_links_diff++;
			        //IF THE LINKS ARE NOT THE SAME THEN USE TRADITIONAL PREDICTION
		    		numRECOGNIZED++;
		    		if((predA == 0) && (predB == 0) && (predT == 0)) {
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
		            continue; 
		    		}
		    		if ((predA ==1) && (predB == 0) ){                    
		    			if(Alink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    		if ((predA ==0) && (predB == 1) ) {                            
		    			if(GTlink.equalsIgnoreCase(Blink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    		if (confA > confB ){
		    			if(Alink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}else{
		    			if(Blink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    	}
		    }
		    //# CASE 2 There are 2 links --- Ambiverse and Tagme
	        /* This is the case when the mention is recognized by TWO of the tools */
		    if((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ) {
		    	two_links++;
	        	two_linksAT++;
		    	if (Alink.equalsIgnoreCase(Tlink)){
	            	two_links_equal++;
		    		if((AmbR1.containsKey(k)) && ((TagR1.containsKey(k)))){
		    			numRECOGNIZED++;
		    			two_linksPredicted++;
		            	two_linksPredictedAT++;
		            	Alink = AmbMAP.get(k).toLowerCase();
	        			Tlink = TagMAP.get(k).toLowerCase();
		    			if(Alink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    	}else{
		    		predictions_needed++;
	            	predictions_neededAT++;
	            	two_links_diff++;
			        //IF THE LINKS ARE NOT THE SAME THEN USE TRADITIONAL PREDICTION
		    		numRECOGNIZED+=1;
		    		if ((predA == 0) && (predB == 0) && (predT == 0)){

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
		            	continue;
		            	
		    		}
		    		if ((predA ==1) && (predT ==0) ){
		    			if(Alink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		              	
		    		if((predA ==0) && (predT ==1)){
		    			if(Tlink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    		
		    		if (confA >= confT ){
		    			if(Alink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}else{
		    			if(Tlink.equalsIgnoreCase(GTlink)){
		        		TP+=1;
		    			}
		    			continue;
		    		}
		    	}
		    }
		    //# CASE 2 There are 2 links --- Babelfy and Tagme
	        /* This is the case when the mention is recognized by TWO of the tools */
		    if((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ) {
		    	two_links++;
	        	two_linksBT++;
		    	if (Blink.equalsIgnoreCase(Tlink)){
	            	two_links_equal++;
		    		if((BabR1.containsKey(k)) && ((TagR1.containsKey(k)))){
		    			numRECOGNIZED+=1;
		    			two_linksPredicted++;
		            	two_linksPredictedBT++;
		            	Blink = BabMAP.get(k).toLowerCase();
	        			Tlink = TagMAP.get(k).toLowerCase();
		    			if(Blink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}		
		    	}else{
		    		predictions_needed++;
	            	predictions_neededBT++;
	            	two_links_diff++;
	            	//IF THE LINKS ARE NOT THE SAME THEN USE TRADITIONAL PREDICTION
		    		numRECOGNIZED++;
		    		if((predA ==0) && (predB == 0) && (predT == 0)){
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
			    			if(Blink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
			    			 // predOut.write(k+"\t"+Blink+"\n");
			    			 continue;	
			    		}
			    		if(corpus.equalsIgnoreCase("neel")){//	    		String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
			    			 if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
			    			 // predOut.write(k+"\t"+Blink+"\n");
			    			 continue;	
			    		}
		            continue;                                   
		    		}
		    		if( (predB ==1) && (predT ==0) ){
		    			if(Blink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    		if( (predB ==0) && (predT ==1 ) ){
		    			if(Tlink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    		if(confB >= confT){
		    			if(Blink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}else{
		    			if(Tlink.equalsIgnoreCase(GTlink)){
		    				TP+=1;
		    			}
		    			continue;
		    		}
		    	}	
		    }
		    //# CASE 3 There are 3 links
	        /* This is the case when the mention is recognized by THREE of the tools */
		    if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ) {
		    	three_links++;
		    	//The 3 links are the same
	        	if ( (Alink.equalsIgnoreCase(Blink)) &&  (Alink.equalsIgnoreCase(Tlink)) &&  (Blink.equalsIgnoreCase(Tlink)) ){
	        		three_links_equal++;

	        		if((AmbR1.containsKey(k)) && ((BabR1.containsKey(k))) && ((TagR1.containsKey(k)))){
	        			numRECOGNIZED++;
	        		 	three_linksPredicted++;
		            	three_linksPredictedABT++;
		            	
		            	Alink = AmbMAP.get(k).toLowerCase();
	        			
		        		Blink = BabMAP .get(k).toLowerCase();
		        		
	        			Tlink = TagMAP.get(k).toLowerCase();
	        		
	        			if((Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
		        			TP+=1;
		        		}
	        		}
	        		continue;
	        	}else{
            	//IF THE LINKS ARE NOT THE SAME THEN USE TRADITIONAL PREDICTION
	        	numRECOGNIZED++;
        		predictions_needed++;
            	predictions_neededABT++;
            	if ((predA ==1) && (predB ==0) && (predT ==0)){
            		if(Alink.equalsIgnoreCase(GTlink)){
            			TP+=1;
            		}
            		continue;				        								        					        					        					        					        		        	
            	}
            	if ((predA == 0) && (predB ==1) && (predT ==0)){
            		if(Blink.equalsIgnoreCase(GTlink)){
            			TP+=1;
            		}
            		continue;
            	}
            	if((predA == 0) && (predB ==0) && (predT ==1)){
            		if(Tlink.equalsIgnoreCase(GTlink)){
            			TP+=1;
            		}
            		continue;
            	}
            	if((predA ==1) && (predB ==1) && (predT ==0)) {
            		if(confA >= confB){
            			if(Alink.equalsIgnoreCase(GTlink)){
            				TP+=1;
            			}
            			continue;
            		}else{
            			if(Blink.equalsIgnoreCase(GTlink)){
            				TP+=1;
            			}
            			continue;
            		}
            	}   
            	if((predA ==1) && (predB ==0) && (predT ==1)){
            		if(confA >= confT){
            			if(Alink.equalsIgnoreCase(GTlink)){
            				TP+=1;
            			}
            			continue;
            		}else{
            			if(Tlink.equalsIgnoreCase(GTlink)){
            				TP+=1;
            			}
            			continue;
            		}
            	}
            	if((predA ==0) && (predB ==1) && (predT ==1)){
            		if (confB >= confT){
            			if (Blink.equalsIgnoreCase(GTlink)){
            				TP+=1;
//	                            status = "CORRECT:"
            			}
//	                        check.write(status+"\t"+key+"\tSELECTION:"+Blink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
            			continue;
            		}else{
//	                        predicted.write(key+ "\t" + Tlink+"\n")
            			if(Tlink.equalsIgnoreCase(GTlink)){
            				TP+=1;
//	                            status = "CORRECT:"
            			}
//	                        check.write(status+"\t"+key+"\tSELECTION:"+Tlink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
            			continue;
            		}
            	}
            	if( (predA ==1) && (predB ==1) && (predT ==1) ){
            		
            		if( (confB >= confA) && (confB >= confT)){
//	                        predicted.write(key+ "\t" + Blink+"\n")
            			if(Blink.equalsIgnoreCase(GTlink)){
            				TP+=1;
//	                            status = "CORRECT:"
            			}
//	                        check.write(status+"\t"+key+"\tSELECTION:"+Blink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
            			continue;
            		}
            		if((confA >= confB) && (confA >= confT)){
//	                        predicted.write(key+ "\t" + Alink+"\n")                        
            			if(Alink.equalsIgnoreCase(GTlink)){
            				TP+=1;
//	                            status = "CORRECT:"
            			}
//	                        check.write(status+"\t"+key+"\tSELECTION:"+Alink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
            			continue;
	                    	
            		}
            		if ( (confT >= confA) && (confT >= confB) ){
//	                        largest = "tagme"
//	                        predicted.write(key+ "\t" + Tlink+"\n")
            			if(Tlink.equalsIgnoreCase(GTlink)){
            				TP+=1;
//	                            status = "CORRECT:"
            			}
//	                        check.write(status+"\t"+key+"\tSELECTION:"+Tlink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
            			continue; 
            		}
            	}
            	if ((predA ==0) && (predB ==0) && (predT ==0)){
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
	            continue; 
            	}
		    }
		    }
			}
        csvparser.close();
//		System.out.println();
//		System.out.println("-----------------------------------------");
//		System.out.println("GT mentions recognised by 0/3 systems :" +( GT_test_MAP.keySet().size() - (zero_link+one_link+two_links+three_links) ));
//		System.out.println("-----------------------------------------");
//		System.out.println("GT mentions recognised by 1/3 systems :" +one_link);
//		System.out.println("-----------------------------------------");
//		System.out.println("......GT mentions recognised by Amb :" +one_linkA);
//		System.out.println("......GT mentions recognised by Bab :" +one_linkB);
//		System.out.println("......GT mentions recognised by Tag :" +one_linkT);
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
		R =  (double) TP/(double) GT_test_MAP.keySet().size();
		F = 2*((P*R)/(P+R));
		P = round(P,3)*100.0;
		R = round(R,3)*100.0;
		F = round(F,3)*100.0;
		System.out.println(percentage+"\t&\t"+P+"\t&\t"+R+"\t&\t"+F);

//		System.out.println(" *********************** ");
//		System.out.println("Meta EL Prediction after classification for MultiLabel classification task ["+classifier+"]");
//		System.out.println(" *********************** ");
//		System.out.println("P:"+ P);
//		System.out.println("R:"+ R);
//		System.out.println("F:"+ F);
//		System.out.println("TP:"+TP);
//		System.out.println("numRecog:"+numRECOGNIZED);
		
	}
	
	/**
	 *
	 * @param output
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	private static void predictMultilabelEXTENDED_R1(DataLoaders d, String corpus,double percentage) throws NumberFormatException, Exception{
		TreeMap<String,String> AmbR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Amb."+percentage+".pred");
		TreeMap<String,String> BabR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Bab."+percentage+".pred");
		TreeMap<String,String> TagR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Tag."+percentage+".pred");

		int TP = 0;
		int numRECOGNIZED = 0;	
		int zero_link = 0;
		int one_link = 0;
		int two_links = 0;
		int two_links_equal = 0;
		int two_links_diff = 0;
		int three_links = 0;
		int three_links_equal = 0;
		int three_links_2equal = 0;
		int three_links_diff = 0;
		int predictions_needed = 0;
		int correct_provided = 0;
		
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
		
		TreeMap<String,String> GT_test_MAP = d.getGT_MAP_test();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap_test();
		TreeMap<String,String> BabMAP = d.getBabelMap_test();
		TreeMap<String,String> TagMAP = d.getTagmeMap_test();
		
		CSVReader csvparser = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+"."+percentage+".predictions"), StandardCharsets.UTF_8)),',','\''); 
		String[] row = null;
        while ((row = csvparser.readNext() ) != null) {

			String docid = row[0];
		    String mention = row[1];
		    String offset = row[2];
		    
		    double confT = Double.parseDouble(row[row.length -1]);
		    double confB = Double.parseDouble(row[row.length -2]);
		    double confA = Double.parseDouble(row[row.length -3]);
		    
		    double predT = Double.parseDouble(row[row.length -4]);
		    double predB = Double.parseDouble(row[row.length -5]);
		    double predA = Double.parseDouble(row[row.length -6]);
		    
			Pattern p = Pattern.compile("\'([^\"]*)\'");
			Matcher m = p.matcher(docid);
			while (m.find()) {
				docid = m.group(1);
			}
			m = p.matcher(mention);
			while (m.find()) {
				mention = m.group(1).toLowerCase();
			}
		    String k = docid+"\t"+mention+"\t"+offset;
		    String Alink = "NULL";
	        String Blink = "NULL";
	        String Tlink = "NULL";
	        String GTlink = GT_test_MAP.get(k);

	        if(AmbMAP.containsKey(k)){
	            Alink = AmbMAP.get(k);
	        }
	        if(BabMAP.containsKey(k)){
	            Blink = BabMAP.get(k);
	        }
	        if(TagMAP.containsKey(k)){
	            Tlink = TagMAP.get(k);
	        }
		    //######################################################################################
	        //# CASE 4 There is no link
	        /* this is the case when the mention is not recognized by any of the tools */
	        //######################################################################################
		    if(!AmbMAP.containsKey(k)){
		    	if(!BabMAP.containsKey(k)){
		    		if(!TagMAP.containsKey(k)){
		                zero_link+=1;
//		                predicted.write(key+ "\t" + Blink+"\n")
//		                check.write("NOT RECOGN:"+"\t"+key+"\tSELECTION:"+Blink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
		                continue;
		    		}
		    	}
		    }
		    
		    //######################################################################################
		    // # CASE 3 There is 1 link  
	        /* this is the case when the mention is recognized by ONE of the tools */
		    /* Thus I use the prediction from second level binary classifier */
		    //######################################################################################
		    if(AmbMAP.containsKey(k)){
		    	if(!BabMAP.containsKey(k)){
		    		if(!TagMAP.containsKey(k)){
//		                predicted.write(key+ "\t" + Alink+"\n")
		    			
		    	      	if(AmbR1.containsKey(k)){
			        		Alink = AmbR1.get(k).toLowerCase();
			        		numRECOGNIZED++;
			        		one_link++;
			        		if(Alink.equalsIgnoreCase(GTlink)){
			        			TP+=1;
			        		}
			        	}
		    			
//		                check.write(status+"\t"+key+"\tSELECTION:"+Alink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
		                continue;
		    		}
		    	}
		    }
		    //######################################################################################
		    // # CASE 3 There is 1 link  
	        /* this is the case when the mention is recognized by ONE of the tools */
		    /* Thus I use the prediction from second level binary classifier */
		    //######################################################################################
         if(!AmbMAP.containsKey(k)){
         	if(BabMAP.containsKey(k)){
         		if(!TagMAP.containsKey(k)){

         	     	if(BabR1.containsKey(k)){
   	        		Blink = BabR1.get(k).toLowerCase();
   	        		numRECOGNIZED++;
   		        	one_link++;
   		        	if(Blink.equalsIgnoreCase(GTlink)){
   		        		TP+=1;
   		        	}
   	        	}
   	        	continue;
         		}
         	}
         }
         //######################################################################################
         // # CASE 3 There is 1 link  
	        /* this is the case when the mention is recognized by ONE of the tools */
         /* Thus I use the prediction from second level binary classifier */
         //######################################################################################
		    if(!AmbMAP.containsKey(k)){
		    	if(!BabMAP.containsKey(k)){
		    		  if(TagMAP.containsKey(k)){
		    		    	if(TagR1.containsKey(k)){
		    	        		Tlink = TagR1.get(k).toLowerCase();
		    		        	numRECOGNIZED++;
		    		        	one_link++;
		    		        	if(Tlink.equalsIgnoreCase(GTlink)){
		    		            	TP+=1;
		    		            }
		    	        	}
		    	        	continue;
		    		  }
		    	}
		    }
//		    ######################################################################################
		    //# CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
//		    ######################################################################################
//		    ### This is the case when the docid_mention is linked by 2 tools
		    if(AmbMAP.containsKey(k)){
		    	if(BabMAP.containsKey(k)){
		    		if(!TagMAP.containsKey(k)){
		    			two_links+=1;
		    			numRECOGNIZED+=1;
		                if((predA == 0) && (predB == 0) && (predT == 0)) {
		                    if(GTlink.equalsIgnoreCase(Blink)){
		                        TP+=1;
		                    }
		                    continue;
		                }
		                if ((predA ==1) && (predB == 0) ){                    
		                    if(GTlink.equalsIgnoreCase(Alink)){
		                        TP+=1;
		                    }
		                    continue;
		    			}
		                if ((predA ==0) && (predB == 1) ) {                            
		                    if(GTlink.equalsIgnoreCase(Blink)){
		                        TP+=1;
		                    }
		                    continue;
		                }
		                
		                if ((predA ==1) && (predB == 1) ){ 
		                    if (confA > confB ){
		                        if(GTlink.equalsIgnoreCase(Alink)){
		                            TP+=1;
		                        }
		                        continue;
		                    }else{
		                        if(GTlink.equalsIgnoreCase(Blink)){
		                            TP+=1;
		                        }
		                        continue;
		                    }
		                }
		                if(GTlink.equalsIgnoreCase(Blink)){
		                    TP+=1;
		                }
		                continue;
		    		}
		    	}
		    }
//		    ######################################################################################
//		    //# CASE 2 There are 2 links
//	        /* this is the case when the mention is recognized by TWO of the tools */
//		    ######################################################################################
		    if(AmbMAP.containsKey(k)){
		    	if(!BabMAP.containsKey(k)){
		    		if(TagMAP.containsKey(k)){
		    			two_links+=1;
		    			numRECOGNIZED+=1;
		                if ((predA == 0) && (predB == 0) && (predT == 0)){
		                    if(GTlink.equalsIgnoreCase(Alink)){
		                        TP+=1;
							}
		                    continue;
		    			}
		                if ((predA ==1) && (predT ==0) ){
		                    if(GTlink.equalsIgnoreCase(Alink)){
		                        TP+=1;
							}
		                    continue;
						}
		              
		                if((predA ==0) && (predT ==1)){
		                    if(GTlink.equalsIgnoreCase(Tlink)){
		                        TP+=1;
		                    }
		                    continue;
						}
		            
		                if (confA >= confT ){
		                    if(GTlink.equalsIgnoreCase(Alink)){
		                        TP+=1;
		                    }
		                    continue;
						}else{
		                    if(GTlink.equalsIgnoreCase(Tlink)){
		                        TP+=1;
		                    }
		                    continue;
		                }
		    		}
		    	}
		    }
		    //######################################################################################
		    //# CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
//		    ######################################################################################
		    if(!AmbMAP.containsKey(k)){
		    	if(BabMAP.containsKey(k)){
		    		if(TagMAP.containsKey(k)){
		    			two_links+=1;
		    			numRECOGNIZED+=1;
		                if((predA ==0) && (predB == 0) && (predT == 0)){
		                    if(GTlink.equalsIgnoreCase(Blink)){
		                        TP+=1;
		                    }
		                    continue;                                       
		                }
		                if( (predB ==1) && (predT ==0) ){
		                    if(GTlink.equalsIgnoreCase(Blink)){
		                        TP+=1;
		    				}
		                    continue;
		    			}
		                if( (predB ==0) && (predT ==1 ) ){
		                    if(GTlink.equalsIgnoreCase(Tlink)){
		                        TP+=1;
		    				}
		                    continue;
		                }
		                if(confB >= confT){
		                    if(GTlink.equalsIgnoreCase(Blink)){
		                        TP+=1;
		                    }
		                    continue;
		    			}else{
		                    if(GTlink.equalsIgnoreCase(Tlink)){
		                        TP+=1;
		                    }
		                    continue;
		    			}
		    		}
		    	}
		    }
		    //######################################################################################
		    ////# CASE 1 There are 3 links
	        /// this is the case when the mention is recognized by THREE of the tools */
		    //######################################################################################
		    if(AmbMAP.containsKey(k)){
		    	if(BabMAP.containsKey(k)){
		    		if(TagMAP.containsKey(k)){
		    			three_links+=1;
		    			numRECOGNIZED+=1;
		                if ((predA ==1) && (predB ==0) && (predT ==0)){
		                    if(GTlink.equalsIgnoreCase(Alink)){
		                        TP+=1;
		                    }
		                    continue;				        								        					        					        					        					        		        	
		    			}
		                if ((predA == 0) && (predB ==1) && (predT ==0)){
		                    if(GTlink.equalsIgnoreCase(Blink)){
		                        TP+=1;
		                    }
		                    continue;
		                }
		                if((predA == 0) && (predB ==0) && (predT ==1)){
		                    if(GTlink.equalsIgnoreCase(Tlink)){
		                        TP+=1;
		                    }
		                    continue;
		    			}
		                if((predA ==1) && (predB ==1) && (predT ==0)) {
		                    if(confA >= confB){
		                        if(GTlink.equalsIgnoreCase(Alink)){
		                            TP+=1;
		                        }
		                        continue;
		                    }else{
		                        if(GTlink.equalsIgnoreCase(Blink)){
		                            TP+=1;
		                        }
		                        continue;
		                    }
		                }   
		                if((predA ==1) && (predB ==0) && (predT ==1)){
		                    if(confA >= confT){
		                        if(GTlink.equalsIgnoreCase(Alink)){
		                            TP+=1;
		                        }
		                        continue;
		                    }else{
		                        if(GTlink.equalsIgnoreCase(Tlink)){
		                            TP+=1;
		                        }
		                        continue;
		                    }
		                }
		                if((predA ==0) && (predB ==1) && (predT ==1)){
		                    if (confB >= confT){
		                        if (GTlink.equalsIgnoreCase(Blink)){
		                            TP+=1;
//		                            status = "CORRECT:"
		                        }
//		                        check.write(status+"\t"+key+"\tSELECTION:"+Blink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
		                        continue;
		                    }else{
//		                        predicted.write(key+ "\t" + Tlink+"\n")
		                        if(GTlink.equalsIgnoreCase(Tlink)){
		                            TP+=1;
//		                            status = "CORRECT:"
		                        }
//		                        check.write(status+"\t"+key+"\tSELECTION:"+Tlink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
		                        continue;
		                    }
		                }
		                if( (predA ==1) && (predB ==1) && (predT ==1) ){
		                    
		                    if( (confB >= confA) && (confB >= confT)){
//		                        predicted.write(key+ "\t" + Blink+"\n")
		                        if(GTlink.equalsIgnoreCase(Blink)){
		                            TP+=1;
//		                            status = "CORRECT:"
		                        }
//		                        check.write(status+"\t"+key+"\tSELECTION:"+Blink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
		                        continue;
		                    }
		                    if((confA >= confB) && (confA >= confT)){
//		                        predicted.write(key+ "\t" + Alink+"\n")                        
		                        if(GTlink.equalsIgnoreCase(Alink)){
		                            TP+=1;
//		                            status = "CORRECT:"
		                        }
//		                        check.write(status+"\t"+key+"\tSELECTION:"+Alink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
		                        continue;
		                    
		                    }
		                    if ( (confT >= confA) && (confT >= confB) ){
//		                        largest = "tagme"
//		                        predicted.write(key+ "\t" + Tlink+"\n")
		                        if(GTlink.equalsIgnoreCase(Tlink)){
		                            TP+=1;
//		                            status = "CORRECT:"
		                        }
//		                        check.write(status+"\t"+key+"\tSELECTION:"+Tlink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
		                        continue; 
		                        
		                    }
		                }
		                if ((predA ==0) && (predB ==0) && (predT ==0)){
//		                    predicted.write(key+ "\t" + Blink+"\n")                    
		                    if(GTlink.equalsIgnoreCase(Blink)){
		                        TP+=1;
//		                        status = "CORRECT:"
		                    }
//		                    check.write(status+"\t"+key+"\tSELECTION:"+Blink+"\tGT:"+GTlink+"\tAMB:"+Alink+"\tBF:"+Blink+"\tTAGME:"+Tlink+"\t"+"["+str(A)+","+str(B)+","+str(T)+"]["+str(confA)+","+str(confB)+","+str(confT)+"]["+predictions+"]"+"\n")
		                    continue;
		    			}
		    		}
		    	}
		    }
 
		}
		csvparser.close();
//		System.out.println();
//		System.out.println();
//		System.out.println("GT mentions recognised by 0/3 systems :" +zero_link);
//		System.out.println("GT mentions recognised by 1/3 systems :" +one_link);
//		System.out.println("GT mentions recognised by 2/3 systems :"+two_links);
//		System.out.println("......The 2 systems provide the same entity :" +two_links_equal);
//		System.out.println("......The 2 systems provide different entity :" +two_links_diff);
//		System.out.println("GT mentions recognised by 3/3 systems :"+three_links);
//		System.out.println(".....The 3 systems provide the same entity :"+three_links_equal);
//		System.out.println("......2 systems provide the same entity :"+three_links_2equal);
//		System.out.println("......Each system provides a different entity :"+three_links_diff);
//		System.out.println("GT mentions that need prediction :" +predictions_needed);
////
////		logger.info("......The correct entity is provided by at least 1 system :"+correct_provided);
////		logger.info("......The correct entity is not provided by at least 1 system :"+(predictions_needed-correct_provided));
//		System.out.println("TOTAL "+(zero_link+one_link+two_links+three_links) );
//		System.out.println();
//		System.out.println();
		
		double P = 0.0;//
		double R = 0.0;
		double F = 0.0;
		
		P =  (double) TP / (double)numRECOGNIZED;
		R =  (double) TP/(double) GT_test_MAP.keySet().size();
		F = 2*((P*R)/(P+R));
		P = round(P,3)*100.0;
		R = round(R,3)*100.0;
		F = round(F,3)*100.0;
		System.out.println(percentage+"\t&\t"+P+"\t&\t"+R+"\t&\t"+F);
//		System.out.println(" *********************** ");
//		System.out.println("Meta EL Prediction after classification for MultiLabel classification task ["+classifier+"]");
//		System.out.println(" *********************** ");
//		System.out.println("P:"+ P);
//		System.out.println("R:"+ R);
//		System.out.println("F:"+ F);
//		System.out.println("TP:"+TP);
//		System.out.println("numRecog:"+numRECOGNIZED);
//	
		
	}
	
	
	
	
	/**
	 *
	 * @param corpus
	 * @param classifier
	 * @param f
	 * @throws Exception 
	 */
	private static void predictMulticlassEXTENDED_R1_R2_NOTSTRICT(DataLoaders d, String corpus, double percentage) throws Exception{
		TreeMap<String,String> AmbR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Amb."+percentage+".pred");
		TreeMap<String,String> BabR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Bab."+percentage+".pred");
		TreeMap<String,String> TagR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Tag."+percentage+".pred");

		int TP = 0;
		int numRECOGNIZED = 0;
		int zero_link = 0;
		int one_link = 0;
		int two_links = 0;
		int two_links_equal = 0;
		int two_links_diff = 0;
		int three_links = 0;
		int three_links_equal = 0;
		int three_links_2equal = 0;
		int three_links_diff = 0;
		int predictions_needed = 0;
		int correct_provided = 0;

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
		
		TreeMap<String,String> GT_test_MAP = d.getGT_MAP_test();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap_test();
		TreeMap<String,String> BabMAP = d.getBabelMap_test();
		TreeMap<String,String> TagMAP = d.getTagmeMap_test();
		
		CSVReader csvparser = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream("./resources/ds/"+corpus+"/dataset.multiclass."+corpus+"."+percentage+".pred"), StandardCharsets.UTF_8)),',','\''); 
		String[] row = null;
        while ((row = csvparser.readNext() ) != null) {
			String docid = row[0];
		
		    String mention = row[1];
		    String offset = row[2];
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
		    String k = docid+"\t"+mention+"\t"+offset;
		    
	        String Alink = "NULL";
	        String Blink = "NULL";
	        String Tlink = "NULL";
	        String GTlink = GT_test_MAP.get(k).toLowerCase();

	        if(AmbMAP.containsKey(k)){
	            Alink = AmbMAP.get(k).toLowerCase();
	        }
	        if(BabMAP.containsKey(k)){
	            Blink = BabMAP.get(k).toLowerCase();
	        }
	        if(TagMAP.containsKey(k)){
	            Tlink = TagMAP.get(k).toLowerCase();
	        }
	        
	        //# CASE 1 There are 3 links i.e. the mention is recognized by THREE of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
       		
	        	//The 3 links are the same
	        	if ( (Alink.equalsIgnoreCase(Blink)) &&  (Alink.equalsIgnoreCase(Tlink)) &&  (Blink.equalsIgnoreCase(Tlink)) ){
	        		three_links++;
	        		three_links_equal++;
	        		if(AmbR1.containsKey(k)){Alink = AmbR1.get(k).toLowerCase();}
	        		if(BabR1.containsKey(k)){Blink = BabR1.get(k).toLowerCase();} 
	        		if(TagR1.containsKey(k)){Tlink = TagR1.get(k).toLowerCase();}
       			numRECOGNIZED++;
       			if((Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			TP+=1;
	        		}
       			continue;
	        	}
	        	//IF THE LINKS ARE NOT THE SAME THEN USE TRADITIONAL PREDICTION
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
	        	numRECOGNIZED++;
	        	three_links++;
	        	if(predicted.equalsIgnoreCase("AMBIVERSE")){ if(Alink.equalsIgnoreCase(GTlink)) { TP+=1; } continue; }
	            if(predicted.equalsIgnoreCase("BABELFY")){ if(Blink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	            if(predicted.equalsIgnoreCase("TAGME")){ if(Tlink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	        }
//	        
	        //# CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	//IN CASE THE LINKS ARE THE SAME  STRICT mode
	            if(Alink.equalsIgnoreCase(Blink)){
	            	two_links_equal++;
	            	two_links++;
		        	if(AmbR1.containsKey(k)){Alink = AmbR1.get(k).toLowerCase();}
		        	if(BabR1.containsKey(k)){Blink = BabR1.get(k).toLowerCase();} 
		        	numRECOGNIZED++;
		        	if((Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) ){
		        		TP+=1;
		        	}
		        	continue;
	            }
	            two_links_diff++;
	            //IF THE LINKS ARE NOT THE SAME THEN USE TRADITIONAL PREDICTION
	            numRECOGNIZED++;
		        two_links++;
		        predictions_needed++;
	            if(predicted.equalsIgnoreCase("AMBIVERSE")){ if(Alink.equalsIgnoreCase(GTlink)) { TP+=1; } continue;	}
	            if(predicted.equalsIgnoreCase("BABELFY")){ if(Blink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	            if(predicted.equalsIgnoreCase("TAGME")){ if(Blink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  } //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	        }
	        //# CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	//IN CASE THE LINKS ARE THE SAME  STRICT mode
	        	if(Alink.equalsIgnoreCase(Tlink)){
	            	two_links_equal++;
		            two_links++;
		            if(AmbR1.containsKey(k)){Alink = AmbR1.get(k).toLowerCase();}
		            if(TagR1.containsKey(k)){Tlink = TagR1.get(k).toLowerCase();}
		            numRECOGNIZED++;
                  	if((Alink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink))){
	            		TP+=1;
	            	}
	            	continue;
	        	}
		        two_links_diff++;
		        //IF THE LINKS ARE NOT THE SAME THEN USE TRADITIONAL PREDICTION
       		numRECOGNIZED++;
       		two_links++;
		        predictions_needed++;
       		if(predicted.equalsIgnoreCase("AMBIVERSE")){ if(Alink.equalsIgnoreCase(GTlink)) { TP+=1; } continue;	}
       		if(predicted.equalsIgnoreCase("BABELFY")){ if(Alink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  } //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
       		if(predicted.equalsIgnoreCase("TAGME")){ if(Tlink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	        }
	        //# CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	//IN CASE THE LINKS ARE THE SAME  STRICT mode
	            if(Blink.equalsIgnoreCase(Tlink)){
	                two_links_equal++;
		        	two_links++;
		        	if(BabR1.containsKey(k)){Blink = BabR1.get(k).toLowerCase();} 
		        	if(TagR1.containsKey(k)){Tlink = TagR1.get(k).toLowerCase();}
		        	numRECOGNIZED++;
		           	if((Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink))){
		           		TP+=1;
		           	}
		           	continue;
		        }
	        	two_links_diff++;	                
	            //IF THE LINKS ARE NOT THE SAME THEN USE TRADITIONAL PREDICTION
	            numRECOGNIZED++;
		        two_links++;
		        predictions_needed++;
		        if(predicted.equalsIgnoreCase("AMBIVERSE")){ if(Blink.equalsIgnoreCase(GTlink)) { TP+=1; } continue; }  //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		        if(predicted.equalsIgnoreCase("BABELFY")){ if(Blink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
		        if(predicted.equalsIgnoreCase("TAGME")){ if(Tlink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	        }
	        
	        // # CASE 3 There is 1 link  - Regra R1  
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	if(AmbR1.containsKey(k)){
	        		Alink = AmbR1.get(k).toLowerCase();
	        		one_link++;
	        		if(Alink.equalsIgnoreCase(GTlink)){
	        			numRECOGNIZED++;
	        			TP+=1;
	        		}
	        	}
	            continue;
	        }
	        // # CASE 3 There is 1 link     - Regra R1  
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	if(BabR1.containsKey(k)){
	        		Blink = BabR1.get(k).toLowerCase();
		        	one_link++;
		        	if(Blink.equalsIgnoreCase(GTlink)){
		        		numRECOGNIZED++;
		        		TP+=1;
		        	}
	        	}
	        	continue;
	        }
	        // # CASE 3 There is 1 link   
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	if(TagR1.containsKey(k)){
	        		Tlink = TagR1.get(k).toLowerCase();
		        	one_link++;
		        	if(Tlink.equalsIgnoreCase(GTlink)){
		        		numRECOGNIZED++;
		            	TP+=1;
		            }
	        	}
	        	continue;
	        }
    
	        //# CASE 4 There is no link
	        /* this is the case when the mention is not recognized by any of the tools */
	        if((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	zero_link++;
	        	continue;
	        }
	        
		}
		
//		System.out.println();
//		System.out.println();
//		System.out.println("GT mentions recognised by 0/3 systems :" +zero_link);
//		System.out.println("GT mentions recognised by 1/3 systems :" +one_link);
//		System.out.println("GT mentions recognised by 2/3 systems :"+two_links);
//		System.out.println("......The 2 systems provide the same entity :" +two_links_equal);
//		System.out.println("......The 2 systems provide different entity :" +two_links_diff);
//		System.out.println("GT mentions recognised by 3/3 systems :"+three_links);
//		System.out.println(".....The 3 systems provide the same entity :"+three_links_equal);
//		System.out.println("......2 systems provide the same entity :"+three_links_2equal);
//		System.out.println("......Each system provides a different entity :"+three_links_diff);
//		System.out.println("GT mentions that need prediction :" +predictions_needed);
////
//		System.out.println("......The correct entity is provided by at least 1 system :"+correct_provided);
//		System.out.println("......The correct entity is not provided by at least 1 system :"+(predictions_needed-correct_provided));
//		System.out.println("TOTAL "+(zero_link+one_link+two_links+three_links) );
//		System.out.println();
//		System.out.println();
		csvparser.close();
		double P = 0.0;//
		double R = 0.0;
		double F = 0.0;
		
		P =  (double) TP / (double)numRECOGNIZED;
		R =  (double) TP/(double) GT_test_MAP.keySet().size();
		F = 2*((P*R)/(P+R));
		P = round(P,3)*100.0;
		R = round(R,3)*100.0;
		F = round(F,3)*100.0;
		System.out.println(percentage+"\t&\t"+P+"\t&\t"+R+"\t&\t"+F);
//		System.out.println("True Positive :" +TP);
//		System.out.println("#Recog. Mentions :" +numRECOGNIZED);
//		System.out.println(" *********************** ");
//		System.out.println("Meta EL Prediction after classification [" + classifier+ "]");
//		System.out.println(" *********************** ");
//		System.out.println("P:"+ P);
//		System.out.println("R:"+ R);
//		System.out.println("F:"+ F);
		//System.out.println("### Done. ###");
//		System.out.println("TP:"+TP);
//		System.out.println("numRecog:"+numRECOGNIZED);
	}
	
	
	/**
	 * 	This is the extended version of the Multiclass classification task.
	 * 
	 * 	
	 * @param corpus
	 * @param classifier
	 * @throws Exception 
	 */
	private static void predictMulticlassEXTENDED_R1_R2_STRICT(DataLoaders d,String corpus, double percentage) throws Exception{
		TreeMap<String,String> AmbR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Amb."+percentage+".pred");
		TreeMap<String,String> BabR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Bab."+percentage+".pred");
		TreeMap<String,String> TagR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Tag."+percentage+".pred");

		int TP = 0;
		int numRECOGNIZED = 0;
		int zero_link = 0;
		int one_link = 0;
		int two_links = 0;
		int two_links_equal = 0;
		int two_links_diff = 0;
		int three_links = 0;
		int three_links_equal = 0;
		int three_links_2equal = 0;
		int three_links_diff = 0;
		int predictions_needed = 0;
		int correct_provided = 0;

		
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
		
		TreeMap<String,String> GT_test_MAP = d.getGT_MAP_test();
		
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap_test();
		TreeMap<String,String> BabMAP = d.getBabelMap_test();
		TreeMap<String,String> TagMAP = d.getTagmeMap_test();
		
		CSVReader csvparser = new CSVReader( new BufferedReader(new InputStreamReader(new FileInputStream("./resources/ds/"+corpus+"/dataset.multiclass."+corpus+"."+percentage+".pred"), StandardCharsets.UTF_8)), ',', '\'');
		String[] row;
        while ((row = csvparser.readNext() ) != null) {
			String docid = row[0];
		
		    String mention = row[1];
		    String offset = row[2];
		    
		    if(mention.contains("valfar")){  //************* STUPID WORKAROUND  FOR WP corpus
		    	mention = mention +","+row[2];
		    	offset = row[3];
		    }
		    
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
		    String k = docid+"\t"+mention+"\t"+offset;
		    
	        String Alink = "NULL";
	        String Blink = "NULL";
	        String Tlink = "NULL";
	        String GTlink = GT_test_MAP.get(k).toLowerCase();

	        if(AmbMAP.containsKey(k)){
	            Alink = AmbMAP.get(k).toLowerCase();
	        }
	        if(BabMAP.containsKey(k)){
	            Blink = BabMAP.get(k).toLowerCase();
	        }
	        if(TagMAP.containsKey(k)){
	            Tlink = TagMAP.get(k).toLowerCase();
	        }
	        
	        //# CASE 1 There are 3 links i.e. the mention is recognized by THREE of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
        		
	        	//The 3 links are the same
	        	if ( (Alink.equalsIgnoreCase(Blink)) &&  (Alink.equalsIgnoreCase(Tlink)) &&  (Blink.equalsIgnoreCase(Tlink)) ){
	        		if((AmbR1.containsKey(k)) && ((BabR1.containsKey(k)))   && ((TagR1.containsKey(k)))){
	        			numRECOGNIZED++;
	        			three_links_equal++;
	        			three_links++;
	        			Alink = AmbR1.get(k).toLowerCase();
	        			Blink = BabR1.get(k).toLowerCase();
	        			Tlink = TagR1.get(k).toLowerCase();
	        			if(Alink.equalsIgnoreCase(GTlink)){
		        			TP+=1;
		        		}
	        			continue;
	        		}
	                
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
	        	numRECOGNIZED++;
	        	three_links++;
	        	if(predicted.equalsIgnoreCase("AMBIVERSE")){ if(Alink.equalsIgnoreCase(GTlink)) { TP+=1; } continue; }
	            if(predicted.equalsIgnoreCase("BABELFY")){ if(Blink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	            if(predicted.equalsIgnoreCase("TAGME")){ if(Tlink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	        }
//	        
	        //# CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	//IN CASE THE LINKS ARE THE SAME  STRICT mode
	            if(Alink.equalsIgnoreCase(Blink)){
	            	two_links_equal++;
		            two_links++;
		        	if((AmbR1.containsKey(k)) && ((BabR1.containsKey(k)))){
		        		numRECOGNIZED++;
		        		Alink = AmbR1.get(k).toLowerCase();
		        		Blink = BabR1.get(k).toLowerCase();
		        		if(Alink.equalsIgnoreCase(GTlink)){
		        			TP+=1;
		        		}
		        		continue;
		        	}
	            //IF THE LINKS ARE NOT THE SAME THEN USE TRADITIONAL PREDICTION
	            }else{
	            	numRECOGNIZED++;
	            	two_links_diff++;
		            two_links++;
		            predictions_needed++;
	            	if(predicted.equalsIgnoreCase("AMBIVERSE")){ if(Alink.equalsIgnoreCase(GTlink)) { TP+=1; } continue;	}
	            	if(predicted.equalsIgnoreCase("BABELFY")){ if(Blink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	            	if(predicted.equalsIgnoreCase("TAGME")){ if(Blink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  } //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	            }
	        }
	        //# CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	//IN CASE THE LINKS ARE THE SAME  STRICT mode
	        	if(Alink.equalsIgnoreCase(Tlink)){
	            	two_links_equal++;
		            two_links++;
		            if((AmbR1.containsKey(k)) && ((TagR1.containsKey(k)))){
		            	numRECOGNIZED++;
		            	Alink = AmbR1.get(k).toLowerCase();
		            	Tlink = TagR1.get(k).toLowerCase();
		            	if(Alink.equalsIgnoreCase(GTlink)){
		            		TP+=1;
		            	}
		            	continue;
		            }
		        //IF THE LINKS ARE NOT THE SAME THEN USE TRADITIONAL PREDICTION
	        	}else{
	        		numRECOGNIZED++;
	            	two_links_diff++;
	        		two_links++;
	        		predictions_needed++;
	        		if(predicted.equalsIgnoreCase("AMBIVERSE")){ if(Alink.equalsIgnoreCase(GTlink)) { TP+=1; } continue;	}
	        		if(predicted.equalsIgnoreCase("BABELFY")){ if(Alink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  } //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	        		if(predicted.equalsIgnoreCase("TAGME")){ if(Tlink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	        	}	
	        }
	        //# CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	//IN CASE THE LINKS ARE THE SAME  STRICT mode
	            if(Blink.equalsIgnoreCase(Tlink)){
	                two_links_equal++;
		        	two_links++;
		        	if((BabR1.containsKey(k)) && ((TagR1.containsKey(k)))){
		        		numRECOGNIZED++;
		        		Blink = BabR1.get(k).toLowerCase();
		        		Tlink = TagR1.get(k).toLowerCase();
		            	if(Blink.equalsIgnoreCase(GTlink)){
		            		TP+=1;
		            	}
		            	continue;
		        	}
	            //IF THE LINKS ARE NOT THE SAME THEN USE TRADITIONAL PREDICTION
	            }else{
	            	numRECOGNIZED++;
	            	two_links_diff++;
		            two_links++;
		            predictions_needed++;
		            if(predicted.equalsIgnoreCase("AMBIVERSE")){ if(Blink.equalsIgnoreCase(GTlink)) { TP+=1; } continue; }  //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
		            if(predicted.equalsIgnoreCase("BABELFY")){ if(Blink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
		            if(predicted.equalsIgnoreCase("TAGME")){ if(Tlink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	            }
	        }
	        
	        // # CASE 3 There is 1 link  - Regra R1  
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	if(AmbR1.containsKey(k)){
	        		Alink = AmbR1.get(k).toLowerCase();
	        		
	        		one_link++;
	        		if(Alink.equalsIgnoreCase(GTlink)){
	        			numRECOGNIZED++;
	        			TP+=1;
	        		}
	        	}
	            continue;
	        }
	        // # CASE 3 There is 1 link     - Regra R1  
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	if(BabR1.containsKey(k)){
	        		Blink = BabR1.get(k).toLowerCase();
		        	one_link++;
		        	if(Blink.equalsIgnoreCase(GTlink)){
		        		numRECOGNIZED++;
		        		TP+=1;
		        	}
	        	}
	        	continue;
	        }
	        // # CASE 3 There is 1 link   
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	if(TagR1.containsKey(k)){
	        		Tlink = TagR1.get(k).toLowerCase();
		        	one_link++;
		        	if(Tlink.equalsIgnoreCase(GTlink)){
		        		numRECOGNIZED++;
		            	TP+=1;
		            }
	        	}
	        	continue;
	        }
     
	        //# CASE 4 There is no link
	        /* this is the case when the mention is not recognized by any of the tools */
	        if((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	zero_link++;
	        	continue;
	        }
	        
		}
		
//		System.out.println();
//		System.out.println();
//		System.out.println("GT mentions recognised by 0/3 systems :" +zero_link);
//		System.out.println("GT mentions recognised by 1/3 systems :" +one_link);
//		System.out.println("GT mentions recognised by 2/3 systems :"+two_links);
//		System.out.println("......The 2 systems provide the same entity :" +two_links_equal);
//		System.out.println("......The 2 systems provide different entity :" +two_links_diff);
//		System.out.println("GT mentions recognised by 3/3 systems :"+three_links);
//		System.out.println(".....The 3 systems provide the same entity :"+three_links_equal);
//		System.out.println("......2 systems provide the same entity :"+three_links_2equal);
//		System.out.println("......Each system provides a different entity :"+three_links_diff);
//		System.out.println("GT mentions that need prediction :" +predictions_needed);
////
//		System.out.println("......The correct entity is provided by at least 1 system :"+correct_provided);
//		System.out.println("......The correct entity is not provided by at least 1 system :"+(predictions_needed-correct_provided));
//		System.out.println("TOTAL "+(zero_link+one_link+two_links+three_links) );
//		System.out.println();
//		System.out.println();
		
		csvparser.close();
		
		double P = 0.0;//
		double R = 0.0;
		double F = 0.0;
		
		P =  (double) TP / (double)numRECOGNIZED;
		R =  (double) TP/(double) GT_test_MAP.keySet().size();
		F = 2*((P*R)/(P+R));
		P = round(P,3)*100.0;
		R = round(R,3)*100.0;
		F = round(F,3)*100.0;
		System.out.println(percentage+"\t&\t"+P+"\t&\t"+R+"\t&\t"+F);
//		System.out.println("True Positive :" +TP);
//		System.out.println("#Recog. Mentions :" +numRECOGNIZED);classifier
//		System.out.println(" *********************** ");
//		System.out.println("Meta EL Prediction after classification [" + classifier+ "]");
//		System.out.println(" *********************** ");
//		System.out.println("P:"+ P);
//		System.out.println("R:"+ R);
//		System.out.println("F:"+ F);
		
//		System.out.println("### Done. ###");
//		System.out.println("TP:"+TP);
//		System.out.println("numRecog:"+numRECOGNIZED);
	}
	
	/**
	 * 	This is the extended version of the Multiclass classification task.
	 * 
	 * 	
	 * @param corpus
	 * @param classifier
	 * @throws Exception 
	 */
	private static void predictMulticlassEXTENDED_R1(DataLoaders d, String corpus,double percentage) throws Exception{
		TreeMap<String,String> AmbR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Amb."+percentage+".pred");
		TreeMap<String,String> BabR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Bab."+percentage+".pred");
		TreeMap<String,String> TagR1 = loadBinaryClassifierPredictions("./resources/ds/"+corpus+"/modelsR1/dataset.multiclass."+corpus+".R1.occ.Tag."+percentage+".pred");
//		
		int TP = 0;
		int numRECOGNIZED = 0;
		int zero_link = 0;
		int one_link = 0;
		int two_links = 0;
		int two_links_equal = 0;
		int two_links_diff = 0;
		int three_links = 0;
		int three_links_equal = 0;
		int three_links_2equal = 0;
		int three_links_diff = 0;
		int predictions_needed = 0;
		int correct_provided = 0;

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
		
		TreeMap<String,String> GT_test_MAP = d.getGT_MAP_test();
		
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap_test();
		TreeMap<String,String> BabMAP = d.getBabelMap_test();
		TreeMap<String,String> TagMAP = d.getTagmeMap_test();
		CSVReader csvparser = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream("./resources/ds/"+corpus+"/dataset.multiclass."+corpus+"."+percentage+".pred"), StandardCharsets.UTF_8)), ',','\''); 
		String[] row = null;
        while ((row = csvparser.readNext() ) != null) {
			String docid = row[0];
		
		    String mention = row[1];
		    String offset = row[2];
		    
		    if(mention.contains("valfar")){  //************* STUPID WORKAROUND  FOR WP corpus
		    	mention = mention +","+row[2];
		    	offset = row[3];
		    }
		    
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
		    String k = docid+"\t"+mention+"\t"+offset;
		    
	        String Alink = "NULL";
	        String Blink = "NULL";
	        String Tlink = "NULL";
	        
	        String GTlink = "NULL";
	        GTlink = GT_test_MAP.get(k);

	        if(AmbMAP.containsKey(k)){
	            Alink = AmbMAP.get(k).toLowerCase();
	        }
	        if(BabMAP.containsKey(k)){
	            Blink = BabMAP.get(k).toLowerCase();
	        }
	        if(TagMAP.containsKey(k)){
	            Tlink = TagMAP.get(k).toLowerCase();
	        }
	        
	        //# CASE 1 There are 3 links i.e. the mention is recognized by THREE of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	three_links++;
        		
	        	//The 3 links are the same
	        	if ( (Alink.equalsIgnoreCase(Blink)) &&  (Alink.equalsIgnoreCase(Tlink)) &&  (Blink.equalsIgnoreCase(Tlink)) ){
	                three_links_equal++; // No need for prediction here..
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
	        	if(predicted.equalsIgnoreCase("AMBIVERSE")){ if(Alink.equalsIgnoreCase(GTlink)) { TP+=1; } continue; }
	            if(predicted.equalsIgnoreCase("BABELFY")){ if(Blink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	            if(predicted.equalsIgnoreCase("TAGME")){ if(Tlink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	        }
//	        
	        //# CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	two_links++;
	            if(Alink.equalsIgnoreCase(Blink)){
	                two_links_equal++;
	            }else{
	            	two_links_diff++;
	            	predictions_needed++;
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			correct_provided++;
	        		}
	            }
	            if(predicted.equalsIgnoreCase("AMBIVERSE")){ if(Alink.equalsIgnoreCase(GTlink)) { TP+=1; } continue;	}
	            if(predicted.equalsIgnoreCase("BABELFY")){ if(Blink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	            if(predicted.equalsIgnoreCase("TAGME")){ if(Blink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  } //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	        }
	      //# CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	            two_links++;
	            if(Alink.equalsIgnoreCase(Tlink)){
	                two_links_equal++;
	            }else{
	                two_links_diff++;
	                predictions_needed++;
	                if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			correct_provided++;
	        		}
	            }
	            if(predicted.equalsIgnoreCase("AMBIVERSE")){ if(Alink.equalsIgnoreCase(GTlink)) { TP+=1; } continue;	}
	            if(predicted.equalsIgnoreCase("BABELFY")){ if(Alink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  } //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	            if(predicted.equalsIgnoreCase("TAGME")){ if(Tlink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	        }	
	        //# CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	two_links++;
	        	
	            if(Blink.equalsIgnoreCase(Tlink)){
	                two_links_equal++;
	            }else{
	            	two_links_diff++;	                
	            	predictions_needed++;
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			correct_provided++;
	        		}
	            }
	            if(predicted.equalsIgnoreCase("AMBIVERSE")){ if(Blink.equalsIgnoreCase(GTlink)) { TP+=1; } continue; }  //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	            if(predicted.equalsIgnoreCase("BABELFY")){ if(Blink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	            if(predicted.equalsIgnoreCase("TAGME")){ if(Tlink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	        }
	        
	        // # CASE 3 There is 1 link  - Regra R1  
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	if(AmbR1.containsKey(k)){
	        		Alink = AmbR1.get(k).toLowerCase();
	        		
	        		one_link++;
	        		if(Alink.equalsIgnoreCase(GTlink)){
	        			numRECOGNIZED++;
	        			TP+=1;
	        		}
	        	}
	            continue;
	        }
	        // # CASE 3 There is 1 link     - Regra R1  
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	if(BabR1.containsKey(k)){
	        		System.out.println(k);
	        		Blink = BabR1.get(k).toLowerCase();
	        		
		        	one_link++;
		        	if(Blink.equalsIgnoreCase(GTlink)){
		        		numRECOGNIZED++;
		        		TP+=1;
		        	}
	        	}
	        	continue;
	        }
	        // # CASE 3 There is 1 link   
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	if(TagR1.containsKey(k)){
	        		Tlink = TagR1.get(k).toLowerCase();
		        	
		        	one_link++;
		        	if(Tlink.equalsIgnoreCase(GTlink)){
		        		numRECOGNIZED++;
		            	TP+=1;
		            }
	        	}
	        	continue;
	        }
       
	        //# CASE 4 There is no link
	        /* this is the case when the mention is not recognized by any of the tools */
	        if((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	zero_link++;
	        	continue;
	        }
	        
		}
		
//		System.out.println();
//		System.out.println();
//		System.out.println("GT mentions recognised by 0/3 systems :" +zero_link);
//		System.out.println("GT mentions recognised by 1/3 systems :" +one_link);
//		System.out.println("GT mentions recognised by 2/3 systems :"+two_links);
//		System.out.println("......The 2 systems provide the same entity :" +two_links_equal);
//		System.out.println("......The 2 systems provide different entity :" +two_links_diff);
//		System.out.println("GT mentions recognised by 3/3 systems :"+three_links);
//		System.out.println(".....The 3 systems provide the same entity :"+three_links_equal);
//		System.out.println("......2 systems provide the same entity :"+three_links_2equal);
//		System.out.println("......Each system provides a different entity :"+three_links_diff);
//		System.out.println("GT mentions that need prediction :" +predictions_needed);
////
//		System.out.println("......The correct entity is provided by at least 1 system :"+correct_provided);
//		System.out.println("......The correct entity is not provided by at least 1 system :"+(predictions_needed-correct_provided));
//		System.out.println("TOTAL "+(zero_link+one_link+two_links+three_links) );
//		System.out.println();
//		System.out.println();
		
		csvparser.close();
		
		
		double P = 0.0;//
		double R = 0.0;
		double F = 0.0;
		
		P =  (double) TP / (double)numRECOGNIZED;
		R =  (double) TP/(double) GT_test_MAP.keySet().size();
		F = 2*((P*R)/(P+R));
		P = round(P,3)*100.0;
		R = round(R,3)*100.0;
		F = round(F,3)*100.0;
		System.out.println(percentage+"\t&\t"+P+"\t&\t"+R+"\t&\t"+F);
//		System.out.println("True Positive :" +TP);
//		System.out.println("#Recog. Mentions :" +numRECOGNIZED);
//		System.out.println(" *********************** ");
//		System.out.println("Meta EL Prediction after classification ["+classifier+"]");
//		System.out.println(" *********************** ");
//		System.out.println("P:"+ P);
//		System.out.println("R:"+ R);
//		System.out.println("F:"+ F);
//		
//		System.out.println("### Done. ###");
//		System.out.println("TP:"+TP);
//		System.out.println("numRecog:"+numRECOGNIZED);
	}
	
	
	
	/**
	 *
	 * @param output
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	private static void predictMSCLOOSE(DataLoaders d, String corpus,double percent) throws NumberFormatException, Exception{
	
		OutputStreamWriter predOut = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+"."+percent+".LOOSE.pred.out"), StandardCharsets.UTF_8);

		TreeMap<String,String> GT_MAP = d.getGT_MAP();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap();
		TreeMap<String,String> BabMAP = d.getBabelfyMap();
		TreeMap<String,String> TagMAP = d.getTagmeMap();
		
//		/** initially writing the mentions that do not need predictions **/
		String testcsvFile = "./resources/ds/"+corpus+"/dataset.meta.test."+corpus+".csv";
 		
		int ZeroTools = 0;
		CSVReader csvparser = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream(testcsvFile), StandardCharsets.UTF_8)),',','\''); 
		String[] row = null;
        while ((row = csvparser.readNext() ) != null) {
        	String docid = row[0].toLowerCase();
        	docid = docid.replaceAll("\'", "");
        	docid = docid.replaceAll("\"", "");
        	String mention = row[1].toLowerCase();
        	String offset = row[2];
        	String k = docid+"\t"+mention.toLowerCase()+"\t"+offset;
        	String GTLINK = GT_MAP.get(k); 
        	String Alink = AmbMAP.get(k);
        	String Blink = BabMAP.get(k);
        	String Tlink = TagMAP.get(k);

        	//# CASE 0 -  There is no link
        	/* This is the case when the mention is NOT recognized by any of the tools  -  No prediction needed */
        	if((!AmbMAP.containsKey(k)) && (!BabMAP.containsKey(k)) && (!TagMAP.containsKey(k))){
        		ZeroTools++;
        		continue;
        	}
        	//# CASE 1  - One EL tool -- Ambiverse
        	if( (AmbMAP.containsKey(k)) && (!BabMAP.containsKey(k)) && (!TagMAP.containsKey(k)) ){
        		predOut.write(k+"\t"+Alink+"\n");
        		continue;
        	}
        	//# CASE 1  - One EL tool -- Babelfy
        	if( (!AmbMAP.containsKey(k)) && (BabMAP.containsKey(k)) && (!TagMAP.containsKey(k)) ){
        		predOut.write(k+"\t"+Blink+"\n");
        		continue;
        	}
        	//# CASE 1  - One EL tool -- Tagme
        	if( (!AmbMAP.containsKey(k)) && (!BabMAP.containsKey(k)) && (TagMAP.containsKey(k)) ){
        		predOut.write(k+"\t"+Tlink+"\n");
        		continue;
        	}
        	//# CASE 2  - Two EL tools -- Ambiverse and Babelfy
        	if( (AmbMAP.containsKey(k)) && (BabMAP.containsKey(k)) && (!TagMAP.containsKey(k)) ){
        		if(Alink.equalsIgnoreCase(Blink)){
        			predOut.write(k+"\t"+Alink+"\n");
        			continue;
        		}
        	}
        	// CASE 2  - Two EL tools -- Ambiverse and Tagme
        	if( (AmbMAP.containsKey(k)) && (!BabMAP.containsKey(k)) && (TagMAP.containsKey(k)) ){
        		if(Alink.equalsIgnoreCase(Tlink)){
        			predOut.write(k+"\t"+Alink+"\n");
        			continue;
        		}
        	}
        	// CASE 2  - Two EL tools -- Babelfy and Tagme
        	if( (!AmbMAP.containsKey(k)) && (BabMAP.containsKey(k)) && (TagMAP.containsKey(k)) ){
        		if(Blink.equalsIgnoreCase(Tlink)){
        			predOut.write(k+"\t"+Blink+"\n");
        			continue;
        		}
        	}
        	// CASE 3  - Three  EL tools -- Ambiverse Babelfy and Tagme
        	if( (AmbMAP.containsKey(k)) && (BabMAP.containsKey(k)) && (TagMAP.containsKey(k)) ){
        		if( (Alink.equalsIgnoreCase(Blink)) && (Alink.equalsIgnoreCase(Tlink)) && (Blink.equalsIgnoreCase(Tlink)) ){
        			predOut.write(k+"\t"+Blink+"\n");
        			continue;
        		}
        	}
        }
        csvparser.close();
//        System.out.println("# mentions recognized by 0 tools : "+ ZeroTools);
        /** **/

        /** Here I am writing the mentions from predictions **/
        TreeMap<String,String> MultilabelPRED =  loadMultilabelClassifierPredictions("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+"."+percent+".predictions");
 		
////		TreeSet<String> trainMap = loadFromTraining("./resources/ds/"+corpus+"/dataset.meta.train."+corpus+"."+percent+".csv");

		csvparser = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+"."+percent+".predictions"), StandardCharsets.UTF_8)), ',','\''); 
//		row[] = null;
        while ((row = csvparser.readNext() ) != null) {
        	String docid = row[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
		    String mention = row[1].toLowerCase();
		    String offset = row[2];
		 
	        String Alink = "NULL";
	        String Blink = "NULL";
	        String Tlink = "NULL";

	        String k = docid+"\t"+mention+"\t"+offset;

	        double predT = 0;
        	double predB = 0;
        	double predA = 0;
        	
        	double confT = 0;
        	double confB = 0;
        	double confA = 0;
        	
	        if(MultilabelPRED.containsKey(k)){
         
	        	row = MultilabelPRED.get(k).split("\t");
	        	confT = Double.parseDouble(row[row.length -1]);
	        	confB = Double.parseDouble(row[row.length -2]);
	        	confA = Double.parseDouble(row[row.length -3]);
	        	predT = Double.parseDouble(row[row.length -4]);
	        	predB = Double.parseDouble(row[row.length -5]);
	        	predA = Double.parseDouble(row[row.length -6]);
	        
	        	if(AmbMAP.containsKey(k)){
	        		Alink = AmbMAP.get(k).toLowerCase();
		        }
		        if(BabMAP.containsKey(k)){
		            Blink = BabMAP.get(k).toLowerCase();
		        }
		        if(TagMAP.containsKey(k)){
		            Tlink = TagMAP.get(k).toLowerCase();
		        }
	        }
	        //# CASE 0 -  There is no link
	        /* This is the case when the mention is NOT recognized by any of the tools  -  No prediction needed */
	        if((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	continue;
	        }
		    
	        // # CASE 1  - There is 1 link   - Ambiverse
	        /* This is the case when the mention is recognized by ONE of the tools  -  Ambiverse */
	        if ((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	continue;
	        }
	        // # CASE 1  - There is 1 link  - Babelfy  
	        /* This is the case when the mention is recognized by ONE of the tools  -  Babelfy */
	        if ((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	continue;
	        }
	        // # CASE 1  - There is 1 link   - Tagme
	        /* This is the case when the mention is recognized by ONE of the tools  -  Tagme */
	        if ((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	continue;
	        }

//	        //# CASE 2 There are 2 links - Ambiverse and Babelfy
//	        /* This is the case when the mention is recognized by TWO of the tools - Ambiverse and Babelfy */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	if((predA == 0) && (predB == 0)){
	        		if (confA > confB ){
	        			predOut.write(k+"\t"+Alink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Blink+"\n");
	        		}
	        		continue;
	        	}
	        	if((predA == 1) && (predB == 0)){
	        		predOut.write(k+"\t"+Alink+"\n");
	        		continue;
	        	}
	        	if((predA == 0) && (predB == 1)){ 
	        		predOut.write(k+"\t"+Blink+"\n");
	        		continue;
	        	}
	        	if((predA == 1) && (predB == 1)){ 
	        		if (confA > confB ){
	        			predOut.write(k+"\t"+Alink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Blink+"\n");
	        		}
	        		continue;
	        	}
	        }
//	        //# CASE 2 There are 2 links  - Ambiverse and Tagme  
//	        /* This is the case when the mention is recognized by TWO of the tools - Ambiverse and Tagme */
	        if((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	if((predA == 0) && (predT == 0)){
	        		if (confA > confT ){
	        			predOut.write(k+"\t"+Alink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Tlink+"\n");
	        		}
	        		continue;
	        	}
	        	if((predA == 1) && (predT == 0)){
	        		predOut.write(k+"\t"+Alink+"\n");
	        		continue;
	        	}
	        	if((predA == 0) && (predT == 1)){
	        		predOut.write(k+"\t"+Tlink+"\n");
	        		continue;
	        	}
	        	if((predA == 1) && (predT == 1)){
	        		if (confA > confT ){
	        			predOut.write(k+"\t"+Alink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Tlink+"\n");
	        		}
	        		continue;
	        	}
	       }
	        //# CASE 2 There are 2 links  - Babelfy and Tagme 
	        /* This is the case when the mention is recognized by TWO of the tools - Babelfy and Tagme */
	        if((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	if((predB == 0) && (predT == 0)){
	        		if (confB > confT ){
	        			predOut.write(k+"\t"+Blink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Tlink+"\n");
	        		}
	        		continue;
	        	}
	        	if((predB == 1) && (predT == 0)){
	        		predOut.write(k+"\t"+Blink+"\n");
	        		continue;
	        	}
	        	if((predB == 0) && (predT == 1)){
	        		predOut.write(k+"\t"+Tlink+"\n");
	        		continue;
	        	}
	        	if((predB == 1) && (predT == 1)){
	        		if (confB > confT ){
	        			predOut.write(k+"\t"+Blink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Tlink+"\n");
	        		}
	        		continue;
	        	}
	        }
	        //# CASE 3 There are 3 links
	        /* This is the case when the mention is recognized by THREE of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	// No EL predicted
	        	if ((predA == 0) && (predB == 0) && (predT == 0)){
	        		if ((confA >= confB ) && (confA >= confT )){
	        			predOut.write(k+"\t"+Alink+"\n");
	        			continue;
	        		}
	        		if ((confB >= confA ) && (confB >= confT )){
	        			predOut.write(k+"\t"+Blink+"\n");
	        			continue;
	        		}
	        		if ((confT >= confA ) && (confT >= confB )){
	        			predOut.write(k+"\t"+Tlink+"\n");
	        			continue;
	        		}
	        		
	        	}
	        	//Ambiverse
	        	if ((predA == 1) && (predB == 0) && (predT == 0)){
	        		predOut.write(k+"\t"+Alink+"\n");
	        		continue;
	        	}
	        	//Babelfy
	        	if ((predA == 0) && (predB == 1) && (predT == 0)){
	        		predOut.write(k+"\t"+Blink+"\n");
	        		continue;
	        	}
	        	//Tagme
	        	if ((predA == 0) && (predB == 0) && (predT == 1)){
	        		predOut.write(k+"\t"+Tlink+"\n");
	        		continue;
	        	}
	        	//Ambiverse and Babelfy
	        	if ((predA == 1) && (predB == 1) && (predT == 0)){
	        		if (confA > confB ){
	        			predOut.write(k+"\t"+Alink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Blink+"\n");
	        		}
	        		continue;
	        	}
	        	//Ambiverse and Tagme
	        	if ((predA == 1) && (predB == 0) && (predT == 1)){
	        		if (confA > confT ){
	        			predOut.write(k+"\t"+Alink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Tlink+"\n");
	        		}
	        		continue;
	        	}
	        	//Babelfy and Tagme
	        	if ((predA == 0) && (predB == 1) && (predT == 1)){
	        		if (confB > confT ){
	        			predOut.write(k+"\t"+Blink+"\n");
	        		}else{
	        			predOut.write(k+"\t"+Tlink+"\n");
	        		}
	        		continue;
	        	}
	        	
	        	//Ambiverse, Babelfy and Tagme 
	        	if ((predA == 1) && (predB == 1) && (predT == 1)){
	        		if ((confA >= confB ) && (confA >= confT )){
	        			predOut.write(k+"\t"+Alink+"\n");
	        			continue;
	        		}
	        		if ((confB >= confA ) && (confB >= confT )){
	        			predOut.write(k+"\t"+Blink+"\n");
	        			continue;
	        		}
	        		if ((confT >= confA ) && (confT >= confB )){
	        			predOut.write(k+"\t"+Tlink+"\n");
	        			continue;
	        		}
	        	}
	        }
        }
        predOut.close();
	}
	
	
	
	
	private static TreeSet<String> loadFromTraining(String traincsvFile) throws IOException {
		TreeSet<String> trainSet = new TreeSet<String>();
		CSVReader reader = null;
		reader = new CSVReader (new InputStreamReader(new FileInputStream(traincsvFile), StandardCharsets.UTF_8),',','\''); 
		String[] row;
        while ((row = reader.readNext()) != null) {
			String docid = row[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			String mention = row[1].toLowerCase();
			String offset = row[2];
			String k = docid+"\t"+mention.toLowerCase()+"\t"+offset;
			trainSet.add(k);
        }
        return trainSet;
	}


	private static TreeMap<String,String> loadMultilabelClassifierPredictions(String inputFile) throws NumberFormatException, Exception{
		TreeMap<String,String> mapR1 = new TreeMap<String, String>();
		CSVReader csvparser = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8)), ',','\''); 
		String[] row = null;
        while ((row = csvparser.readNext() ) != null) {
        	String docid = row[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
		    String mention = row[1].toLowerCase();
//		    mention = mention.replaceAll("\'", "");
//		    mention = mention.replaceAll("\"", "");
		    String offset = row[2];
		    
		    double confT = Double.parseDouble(row[row.length -1]);
		    double confB = Double.parseDouble(row[row.length -2]);
		    double confA = Double.parseDouble(row[row.length -3]);
		    
		    double predT = Double.parseDouble(row[row.length -4]);
		    double predB = Double.parseDouble(row[row.length -5]);
		    double predA = Double.parseDouble(row[row.length -6]);
		   
//		    Pattern p = Pattern.compile("\'(.+([^\"]*))\'");
//			Matcher m = p.matcher(docid);
//			while (m.find()) {
//				docid = m.group(1);
//			}
//			m = p.matcher(mention);
//			while (m.find()) {
//				mention = m.group(1).toLowerCase();
//			}
	    	String k = docid+"\t"+mention+"\t"+offset;
	    	String predString = predA+"\t"+ predB +"\t"+ predT +"\t"+ confA +"\t"+confB +"\t"+confT;
//	    	System.out.println(k+"\t"+predString);
	    	mapR1.put(k, predString);
		    }
		csvparser.close();
		return mapR1;
	}

	/**
	 *		MetaEL + Original
	 * @throws Exception 
	 */
	private static void predictMulticlass(DataLoaders d, String corpus, double percent) throws Exception{
		
		TreeMap<String,String> MulticlassPRED =  loadMulticlassClassifierPredictions("./resources/ds/"+corpus+"/dataset.multiclass."+corpus+"."+percent+".pred");

//		System.out.println("Predictions size:  "+MulticlassPRED.size());
		
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

		TreeMap<String,String> GT_test_MAP = d.getGT_MAP_test();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap_test();
		TreeMap<String,String> BabMAP = d.getBabelMap_test();
		TreeMap<String,String> TagMAP = d.getTagmeMap_test();
		
		int i=0;
		Iterator<?> it = GT_test_MAP.entrySet().iterator();
		while (it.hasNext()) {
//		CSVParser csvparser = null;
//		BufferedReader bb  = new BufferedReader(new InputStreamReader(new FileInputStream("./resources/ds/"+corpus+"/dataset.multiclass."+corpus+"."+percent+".pred"), StandardCharsets.UTF_8)); 
//		String[] row = null;
//        while ((row = csvparser.readNext(bb) ) != null) {
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
//	        System.out.println(k);
	        String GTlink = GT_test_MAP.get(k).toLowerCase();
//		    System.out.println(k+"\t"+GTlink);
	       
	        String predictedTool = "";
	       
        	row = MulticlassPRED.get(k).split("\t"); 
        	predictedTool = row[row.length -1];
        	
	        if(AmbMAP.containsKey(k)){
	            Alink = AmbMAP.get(k).toLowerCase();;
	        }
	        if(BabMAP.containsKey(k)){
	            Blink = BabMAP.get(k).toLowerCase();;
	        }
	        if(TagMAP.containsKey(k)){
	            Tlink = TagMAP.get(k).toLowerCase();;
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
//	            System.out.println(k+"\tA:"+Alink+ "\tGT:"+GTlink);
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
//	            System.out.println(k+"\tB:"+Blink+ "\tGT:"+GTlink);
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
//	            System.out.println(k+"\tT:"+Tlink+ "\tGT:"+GTlink);
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
	            if(predictedTool.equalsIgnoreCase("AMBIVERSE")){ if(Alink.equalsIgnoreCase(GTlink)) {TP+=1; } 
//	            predOut.write(k+"\t"+Alink+"\n");
//	            System.out.println(k+"\tA:"+Alink+ "\tGT:"+GTlink);
	            continue;	}
	            if(predictedTool.equalsIgnoreCase("BABELFY")){ if(Blink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
//	            predOut.write(k+"\t"+Blink+"\n");
//	            System.out.println(k+"\tA:"+Blink+ "\tGT:"+GTlink);
	            continue;  }
	            if(predictedTool.equalsIgnoreCase("TAGME")){ //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	            	
	            	
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
	            continue; 
	            } //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
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
	            if(predictedTool.equalsIgnoreCase("AMBIVERSE")){ if(Alink.equalsIgnoreCase(GTlink)) {  TP+=1; }  
//	            predOut.write(k+"\t"+Alink+"\n"); 
	            continue;	}
	            if(predictedTool.equalsIgnoreCase("BABELFY")){//# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	            	
	            	
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
	            	continue;
	            }//# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	            if(predictedTool.equalsIgnoreCase("TAGME")){ if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
//	            predOut.write(k+"\t"+Tlink+"\n");  
	            continue;  }
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
	            if(predictedTool.equalsIgnoreCase("AMBIVERSE")){  //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	            	
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
		    			if(Blink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
		    			 // predOut.write(k+"\t"+Blink+"\n");
		    			 continue;	
		    		}
		    		if(corpus.equalsIgnoreCase("neel")){//	    		String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
		    			 if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
		    			 // predOut.write(k+"\t"+Blink+"\n");
		    			 continue;	
		    		}
	            continue; 
	            
	            }  //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	            if(predictedTool.equalsIgnoreCase("BABELFY")){ if(Blink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
//	            predOut.write(k+"\t"+Blink+"\n"); 
	            continue;  }
	            if(predictedTool.equalsIgnoreCase("TAGME")){ if(Tlink.equalsIgnoreCase(GTlink)){ TP+=1;  } 
//	            predOut.write(k+"\t"+Tlink+"\n"); 
	            continue;  }
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
	        	
	        	if(predictedTool.equalsIgnoreCase("AMBIVERSE")){if(Alink.equalsIgnoreCase(GTlink)) {  TP+=1;} 
//	        	predOut.write(k+"\t"+Alink+"\n");  
	        	continue;	}
	            if(predictedTool.equalsIgnoreCase("BABELFY")){ if(Blink.equalsIgnoreCase(GTlink)){   TP+=1;  }  
//	            predOut.write(k+"\t"+Blink+"\n"); 
	            continue;  }
	            if(predictedTool.equalsIgnoreCase("TAGME")){ if(Tlink.equalsIgnoreCase(GTlink)){  TP+=1;  } 
//	            predOut.write(k+"\t"+Tlink+"\n");  
	            continue;  }
	        }	                
		}
//		predOut.flush();
//		predOut.close();
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
		R =  (double) TP/(double) GT_test_MAP.keySet().size();
		F = 2*((P*R)/(P+R));
		
		P = round(P,3)*100.0;
		R = round(R,3)*100.0;
		F = F*100.0;
		System.out.println(percent+":\t &"+P+"\t & "+R+"\t & "+F);
//		System.out.println("TP:"+TP);
//		System.out.println("numRecog:"+numRECOGNIZED);
		
//		P = round(P,3)*100.0;
//		R = round(R,3)*100.0;
//		F = round(F,3)*100.0;
	}
	
	private static TreeMap<String,String> loadMulticlassClassifierPredictions(String inputFile) throws Exception{
		TreeMap<String,String> mapR1 = new TreeMap<String, String>();
		
		CSVReader csvparser = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8)), ',', '\''); 
		String[] row = null;
        while ((row = csvparser.readNext() ) != null) {
//		CSVReader reader = null;
//		reader = new CSVReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8));
//		String[] row;
//
//		while ((row = reader.readNext()) != null) {
			String docid = row[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			
		    String mention = row[1].toLowerCase();
		    String offset = row[2];
		    
//		    if(mention.contains("valfar")){  //************* STUPID WORKAROUND  FOR WP corpus
//		    	mention = mention +","+row[2];
//		    	offset = row[3];
//		    }
//		    
		    String predictedTool = row[row.length -1];   ///  *** Predicted Tool  ***
		    
			Pattern p = Pattern.compile("\'(.+([^\"]*))\'");
			Matcher m = p.matcher(docid);
			while (m.find()) {
				docid = m.group(1);
			}
			m = p.matcher(mention);
			while (m.find()) {
				mention = m.group(1).toLowerCase();
			}
		    String k = docid+"\t"+mention+"\t"+offset;
		    mapR1.put(k,predictedTool);
		    
		}
		
		csvparser.close();
//		System.out.println(mapR1.keySet().size());
		return mapR1;
		
	}


	/**
	 *
	 * @param inputFile
	 * @return
	 * @throws Exception 
	 */
	private static TreeMap<String,String> loadBinaryClassifierPredictions(String inputFile) throws Exception{
		TreeMap<String,String> mapR1 = new TreeMap<String, String>();
		CSVReader csvparser = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8)),',','\''); 
		String[] row = null;
        while ((row = csvparser.readNext() ) != null) {
			String docid = row[0].toLowerCase();
	    	docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
		    String mention = row[1].toLowerCase();;
		    String offset = row[2];
		    String predictedLink  = row[3];
		    
		    predictedLink = predictedLink.replaceAll("\'", "");
		    String prediction = row[row.length -1];
		    if(prediction.equals("1")){
		    	String k = docid+"\t"+mention+"\t"+offset;
		    	mapR1.put(k, predictedLink);
		    }
		}
        csvparser.close();
		return mapR1;
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
}
