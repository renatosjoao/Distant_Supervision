package de.l3s.distantsupervision;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;
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

public class DistantSuperARFFGenerator {
	
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
		String testcsvFile = "./resources/ds/"+corpus+"/dataset.meta.test."+corpus+".csv";
		
		DistantSuperARFFGenerator dS = new DistantSuperARFFGenerator();
		
 		double[] p = new double[]{1.0,5.0,10.0,15.0,20.0,30.0,40.0,50.0,60.0,70.0,80.0,90.0,100.0};
// 		double[] p = new double[]{80.0};
//		double[] p = new double[]{10.0,20.0,40.0,80.0,100.0};
//
		for(double percent : p){
////////////			"./resources/ds/"+corpus+"/dataset.meta.train."+corpus+"."+percent+".csv"
////////			
			String traincsvFile = "./resources/ds/"+corpus+"/dataset.meta.train."+corpus+"."+percent+".csv";
//			
//			dS.writeARFFMulticlassTrainingSet(d,traincsvFile, percent, corpus);
//			
			dS.writeARFFMultilabelTrainingSet(d,traincsvFile, percent, corpus);
//			
			dS.writeARFFOneClassTrainingSetR1(d,traincsvFile,percent,corpus);
////			
		}
//		dS.writeARFFMulticlassTestSet(d,testcsvFile, corpus);
		dS.writeARFFMultilabelTestSet(d,testcsvFile, corpus);
		dS.writeARFFOneClassTestSetR1(d,testcsvFile,corpus);		
	}
	

	public DistantSuperARFFGenerator() {
		super();
	}
	
	/**
	 *  This utility function is meant to write the ARFF file for the distant supervision approach.
	 *  
	 * @param traincsvFile
	 * @param percent
	 * @param corpus
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	@SuppressWarnings("resource")
	public static void writeARFFMulticlassTrainingSet(DataLoaders d, String traincsvFile, double percent, String corpus) throws NumberFormatException, Exception{
		TreeMap<String,String> AmbiMap = d.getAmbiverseMap_train();
		TreeMap<String,String> BabMap = d.getBabelMap_train();
		TreeMap<String,String> TagMap = d.getTagmeMap_train();
		TreeMap<String,String> GT_train_MAP = d.getGT_MAP_train(); 

//		#This arff file has the DOCIDs, Mentions, Offset and the features for the prediction task
//		##############################################################################################################
		System.out.println("### Writing the training set for Multiclass###");

		OutputStreamWriter arffoutCOMPLETE = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.multiclass."+corpus+".train."+percent+".ORG.arff"), StandardCharsets.UTF_8);
//		CSVWriter csvWriter = new CSVWriter(pw, ',' ,  '\'', '\\');
		arffoutCOMPLETE.write("@RELATION \"multiclass_instances_train\""+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE docid        STRING"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE mention      STRING"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE offset       NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE link         STRING"+"\n");
//		arffoutCOMPLETE.write("@ATTRIBUTE slen       NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE swords  NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE sf         NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE sdf      NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE scand      NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE mpos      NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE msent     NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE dwords     NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE dents   NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE Class        {AMBIVERSE,BABELFY,TAGME}"+"\n");
		arffoutCOMPLETE.write("\n");
		arffoutCOMPLETE.write("@DATA"+"\n");
		CSVReader csvparser = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream(traincsvFile), StandardCharsets.UTF_8)),',','\''); 
		String[] row = null;
        while ((row = csvparser.readNext() ) != null) {
        	if(row.length==13){
			String docid = row[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			String mention = row[1].toLowerCase();
			String offset = row[2];
			String GTLINK = "NULL";
			double slen = Double.parseDouble(row[4]);
			double swords = Double.parseDouble(row[5]);
			double sf = Double.parseDouble(row[6]);
			double sdf = Double.parseDouble(row[7]);
			double scand = Double.parseDouble(row[8]);
			double mpos = Double.parseDouble(row[9]);
			double msent = Double.parseDouble(row[10]);
 			double dwords = Double.parseDouble(row[11]); 
			double dents =  Double.parseDouble(row[12]);
			
			String features = swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+dwords+","+dents;
//			String features = slen+","+swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+dwords+","+dents;

//			Pattern p = Pattern.compile("(.+([^\"]*))"); // this crap fixes mathias "warlord" nygord
//			Matcher m = p.matcher(docid);
//			while (m.find()) {
//				docid = m.group(1);
//			}
//			m = p.matcher(mention);
//			while (m.find()) {
//				mention = m.group(1).toLowerCase();
//			}
//			
			String k = docid+"\t"+mention.toLowerCase()+"\t"+offset;
//			System.out.println(k);
			mention = mention.replaceAll("\"", "\\\\\""); //tim " ripper " owens
//			GTLINK = GT_train_MAP.get(k);
//		
//			System.out.println(k);
//			
			//This is the case that no EL tool has recognized the mention at that specific position 
			if((!AmbiMap.containsKey(k)) && (!BabMap.containsKey(k)) && (!TagMap.containsKey(k))){
				continue;
			}
			//// Ambiverse 
		    if( AmbiMap.containsKey(k)){
		    	arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+"\"AMBIVERSE\"" +"\n"); 
		    }
		    //// Babelfy
		    if( BabMap.containsKey(k)){        
		    	arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+"\"BABELFY\"" +"\n");
		    }
		    //// Tagme
		    if( TagMap.containsKey(k)){
		    	arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+"\"TAGME\"" +"\n"); 
		    }
        	}
        }
    csvparser.close();
	arffoutCOMPLETE.close();
	System.out.println("### Done. Writing the training set for Multiclass ### : "+percent);
	}
	
	/**
	 *
	 * @param traincsvFile
	 * @param percent
	 * @param corpus
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	@SuppressWarnings("resource")
	private static void writeARFFMultilabelTrainingSet(DataLoaders d, String traincsvFile, double percent, String corpus) throws NumberFormatException, Exception{
		TreeMap<String,String> AmbiMap = d.getAmbiverseMap();
		TreeMap<String,String> BabMap = d.getBabelfyMap();
		TreeMap<String,String> TagMap = d.getTagmeMap();
		
//		//##############################################################################################################
//		//#This arff file has the DOCIDs, Mentions, Offset and the features for the prediction task
//		//##############################################################################################################
		System.out.println("### Writing the training set for Multilabel###");    
		OutputStreamWriter arffout = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+".train."+percent+".arff"), StandardCharsets.UTF_8);
		OutputStreamWriter arffoutCOMPLETE = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+".train."+percent+".ORG.arff"), StandardCharsets.UTF_8);

		arffout.write("@RELATION \"multilabel_instances_train: -C -3\""+"\n");
		arffoutCOMPLETE.write("@RELATION \"multilabel_instances_train: -C -3\""+"\n");
		
		arffoutCOMPLETE.write("@ATTRIBUTE docid        STRING"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE mention      STRING"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE offset       NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE link         STRING"+"\n");

//		arffout.write("@ATTRIBUTE slen       NUMERIC"+"\n");
//		arffoutCOMPLETE.write("@ATTRIBUTE slen       NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE swords  NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE swords  NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE sf         NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE sf         NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE sdf      NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE sdf      NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE scand      NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE scand      NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE mpos      NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE mpos      NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE msent     NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE msent     NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE dwords     NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE dwords     NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE dents    NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE dents   NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE Aclass        {0,1}"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE Aclass        {0,1}"+"\n");
		
		arffout.write("@ATTRIBUTE Bclass        {0,1}"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE Bclass        {0,1}"+"\n");
		
		arffout.write("@ATTRIBUTE Tclass        {0,1}"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE Tclass        {0,1}"+"\n");
		
		arffout.write("\n");
		arffoutCOMPLETE.write("\n");
		
		arffout.write("@DATA"+"\n");
		arffoutCOMPLETE.write("@DATA"+"\n");

		
		@SuppressWarnings("deprecation")
		CSVReader csvparser = new CSVReader( new BufferedReader(new InputStreamReader(new FileInputStream(traincsvFile), StandardCharsets.UTF_8)),',','\''); 
		String[] row = null;
        while ((row = csvparser.readNext() ) != null) {
        	if(row.length==13){
			String docid = row[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			String mention = row[1].toLowerCase();
//			mention = mention.replaceAll("\"","");
			String offset = row[2];
			String GTLINK = "NULL";
//			double slen = Double.parseDouble(row[4]);
			double swords = Double.parseDouble(row[5]);
			double sf = Double.parseDouble(row[6]);
			double sdf = Double.parseDouble(row[7]);
			double scand = Double.parseDouble(row[8]);
			double mpos = Double.parseDouble(row[9]);
			double msent = Double.parseDouble(row[10]);
			double dwords = Double.parseDouble(row[11]); 
			double dents =  Double.parseDouble(row[12]);

			String features =  swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+dwords+","+dents;
//			String features =  slen+","+swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+dwords+","+dents;

			String k = docid+"\t"+mention.toLowerCase()+"\t"+offset;

			mention = mention.replaceAll("\"", "\\\\\"");

			int l1 = 0;
			int l2 = 0;
			int l3 = 0;
       	
			//# CASE 0 -  There is no link
	        /* This is the case when the mention is NOT recognized by any of the tools  -  No prediction needed */
    		if((!AmbiMap.containsKey(k)) && (!BabMap.containsKey(k)) && (!TagMap.containsKey(k))){
    			continue;
    		}
    		
    		//# CASE 1  - One EL tool -- Ambiverse
    		if( (AmbiMap.containsKey(k)) && (!BabMap.containsKey(k)) && (!TagMap.containsKey(k)) ){
				l1 = 1;
				arffout.write(features+","+l1+","+l2+","+l3+"\n");
				arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+l1+","+l2+","+l3+"\n");
				continue;
			}
    		//# CASE 1  - One EL tool -- Babelfy
    		if( (!AmbiMap.containsKey(k)) && (BabMap.containsKey(k)) && (!TagMap.containsKey(k)) ){
				l2 = 1;
				arffout.write(features+","+l1+","+l2+","+l3+"\n");
				arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+l1+","+l2+","+l3+"\n");
				continue;
			}
    		//# CASE 1  - One EL tool -- Tagme
    		if( (!AmbiMap.containsKey(k)) && (!BabMap.containsKey(k)) && (TagMap.containsKey(k)) ){
				l3 = 1;
				arffout.write(features+","+l1+","+l2+","+l3+"\n");
				arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+l1+","+l2+","+l3+"\n");
				continue;
			}
    		
    		//# CASE 2  - Two EL tools -- Ambiverse and Babelfy
    		if( (AmbiMap.containsKey(k)) && (BabMap.containsKey(k)) && (!TagMap.containsKey(k)) ){
    			String Alink = AmbiMap.get(k);
    			String Blink = BabMap.get(k);
    			if(Alink.equalsIgnoreCase(Blink)){
    				l1 = 1;
    				l2 = 1;
    				arffout.write(features+","+l1+","+l2+","+l3+"\n");
    				arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+l1+","+l2+","+l3+"\n");
    				continue;
    			}
    		}
    		// CASE 2  - Two EL tools -- Ambiverse and Tagme
    		if( (AmbiMap.containsKey(k)) && (!BabMap.containsKey(k)) && (TagMap.containsKey(k)) ){
    			String Alink = AmbiMap.get(k);
    			String Tlink = TagMap.get(k);
    			if(Alink.equalsIgnoreCase(Tlink)){
    				l1 = 1;
    				l3 = 1;
    				arffout.write(features+","+l1+","+l2+","+l3+"\n");
    				arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+l1+","+l2+","+l3+"\n");
    				continue;
    			}
    		}
    		// CASE 2  - Two EL tools -- Babelfy and Tagme
    		if( (!AmbiMap.containsKey(k)) && (BabMap.containsKey(k)) && (TagMap.containsKey(k)) ){
    			String Blink = BabMap.get(k);
    			String Tlink = TagMap.get(k);
    			if(Blink.equalsIgnoreCase(Tlink)){
    				l2 = 1;
    				l3 = 1;
    				arffout.write(features+","+l1+","+l2+","+l3+"\n");
    				arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+l1+","+l2+","+l3+"\n");
    				continue;
    			}
    		}
    		// CASE 3  - Three  EL tools -- Ambiverse Babelfy and Tagme
    		if( (AmbiMap.containsKey(k)) && (BabMap.containsKey(k)) && (TagMap.containsKey(k)) ){
    			String Alink = AmbiMap.get(k);
    			String Blink = BabMap.get(k);
    			String Tlink = TagMap.get(k);
    			if( (Alink.equalsIgnoreCase(Blink)) && (Alink.equalsIgnoreCase(Tlink)) && (Blink.equalsIgnoreCase(Tlink)) ){
    				l1 = 1;
    				l2 = 1;
    				l3 = 1;
    				arffout.write(features+","+l1+","+l2+","+l3+"\n");
    				arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+l1+","+l2+","+l3+"\n");
    				continue;
    			}
    		}
       }
        }
   csvparser.close();
   arffoutCOMPLETE.close();
   arffout.close();
   System.out.println("### Done. Writing the training set for Multilabel ### : "+percent);
	}
	
	/**
	 *
	 *		This method creates the data for training the single label classifiers for the improved version of R1.
	 *
	 * @param traincsvFile
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	private static void writeARFFOneClassTrainingSetR1(DataLoaders d,String traincsvFile, double percent, String corpus) throws NumberFormatException, Exception{
		TreeMap<String,String> AmbiMap = d.getAmbiverseMap();
		TreeMap<String,String> BabMap = d.getBabelfyMap();
		TreeMap<String,String> TagMap = d.getTagmeMap();

		System.out.println("Writing training data for the single label classifiers of R1");
		
		OutputStreamWriter arffoutR1AmbCOMPLETE = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.oneclass."+corpus+"."+percent+".train.R1.Amb.ORG.arff"), StandardCharsets.UTF_8);
		OutputStreamWriter arffoutR1BabCOMPLETE = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.oneclass."+corpus+"."+percent+".train.R1.Bab.ORG.arff"), StandardCharsets.UTF_8);
		OutputStreamWriter arffoutR1TagCOMPLETE = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.oneclass."+corpus+"."+percent+".train.R1.Tag.ORG.arff"), StandardCharsets.UTF_8);
		
		arffoutR1AmbCOMPLETE.write("@RELATION \"oneclass_instances_R1_Amb_train\""+"\n");
		arffoutR1BabCOMPLETE.write("@RELATION \"oneclass_instances_R1_Bab_train\""+"\n");
		arffoutR1TagCOMPLETE.write("@RELATION \"oneclass_instances_R1_Tag_train\""+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE docid        STRING"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE docid        STRING"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE docid        STRING"+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE mention      STRING"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE mention      STRING"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE mention      STRING"+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE offset       NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE offset       NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE offset       NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE link         STRING"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE link         STRING"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE link         STRING"+"\n");

//		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE slen       NUMERIC"+"\n");
//		arffoutR1BabCOMPLETE.write("@ATTRIBUTE slen       NUMERIC"+"\n");
//		arffoutR1TagCOMPLETE.write("@ATTRIBUTE slen       NUMERIC"+"\n");

		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE swords  NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE swords  NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE swords  NUMERIC"+"\n");

		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE sf         NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE sf         NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE sf         NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE sdf      NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE sdf      NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE sdf      NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE scand      NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE scand      NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE scand      NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE mpos      NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE mpos      NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE mpos      NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE msent     NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE msent     NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE msent     NUMERIC"+"\n");

		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE dwords     NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE dwords     NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE dwords     NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE dents   NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE dents   NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE dents   NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE Class        {1}"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE Class        {1}"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE Class        {1}"+"\n");
		
		arffoutR1AmbCOMPLETE.write("\n");
		arffoutR1BabCOMPLETE.write("\n");
		arffoutR1TagCOMPLETE.write("\n");
		
		arffoutR1AmbCOMPLETE.write("@DATA"+"\n");
		arffoutR1BabCOMPLETE.write("@DATA"+"\n");
		arffoutR1TagCOMPLETE.write("@DATA"+"\n");
		
		CSVReader csvparser = new CSVReader( new BufferedReader(new InputStreamReader(new FileInputStream(traincsvFile), StandardCharsets.UTF_8)),',','\'');  
		String[] row = null;
        while ((row = csvparser.readNext() ) != null) {
//		if(row.length == 13){
			String docid = row[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			String mention = row[1].toLowerCase();
			String offset = row[2];
			String GTLINK = "NULL";
//			double slen = Double.parseDouble(row[4]);
			double swords = Double.parseDouble(row[5]);
			double sf = Double.parseDouble(row[6]);
			double sdf = Double.parseDouble(row[7]);
			double scand = Double.parseDouble(row[8]);
			double mpos = Double.parseDouble(row[9]);
			double msent = Double.parseDouble(row[10]);
			double dwords = Double.parseDouble(row[11]); 
			double dents =  Double.parseDouble(row[12]);

			/** EXCLUDING Scorr and Sratio **/
			String featuresR1Amb = swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+dwords+","+dents;
			String featuresR1Bab = swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+dwords+","+dents;
			String featuresR1Tag = swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+dwords+","+dents;

			/*** ORIGINAL ***/
//			String featuresR1Amb = slen+","+swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+scorrAmb+","+sratioAmb+","+dwords+","+dents;
//			String featuresR1Bab = slen+","+swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+scorrBab+","+sratioBab+","+dwords+","+dents;
//			String featuresR1Tag = slen+","+swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+scorrTag+","+sratioTag+","+dwords+","+dents;
			
			String k = docid+"\t"+mention.toLowerCase()+"\t"+offset;

			mention = mention.replaceAll("\"", "\\\\\"");

			//# CASE 0 -  There is no link
	        /* This is the case when the mention is NOT recognized by any of the tools  -  No prediction needed */
    		if((!AmbiMap.containsKey(k)) && (!BabMap.containsKey(k)) && (!TagMap.containsKey(k))){
    			continue;
    		}
			//R1 . Creating the data for Ambiverse classifier
			if (AmbiMap.containsKey(k)){
				arffoutR1AmbCOMPLETE.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+featuresR1Amb+","+ "1" +'\n');
//		            
			}
			//R1 . Creating the data for Babelfy classifier
			if (BabMap.containsKey(k)){
				arffoutR1BabCOMPLETE.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+featuresR1Bab+","+ "1" +'\n');
			}
			//R1 . Creating the data for Tagme classifier
			if (TagMap.containsKey(k)){
				arffoutR1TagCOMPLETE.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+featuresR1Tag+","+ "1" +'\n');
			}
			
		 }
//        }
        csvparser.close();
		arffoutR1AmbCOMPLETE.close();
		arffoutR1BabCOMPLETE.close();
		arffoutR1TagCOMPLETE.close();
		System.out.println("### Done. Writing the training set for OneClass Classifiers ###");
		
	}
	
	
	
	
	// **************************** TEST SETS START FROM HERE  ********************** //
	
	/**
	 *
	 * @param d
	 * @param testcsvFile
	 * @param corpus
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	private void writeARFFMulticlassTestSet(DataLoaders d, String testcsvFile, String corpus) throws NumberFormatException, Exception {
		TreeMap<String,String> AmbiMap = d.getAmbiverseMap_test();
		TreeMap<String,String> BabMap = d.getBabelMap_test();
		TreeMap<String,String> TagMap = d.getTagmeMap_test();
//		TreeMap<String,String>  GT_test_MAP = d.getGT_MAP_test();
		
		System.out.println("### Writing the test set for Multiclass###");
		
		OutputStreamWriter arffoutCOMPLETE = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.multiclass."+corpus+".test.ORG.arff"), StandardCharsets.UTF_8);	
		OutputStreamWriter arffoutCOMPLETELAB = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.multiclass."+corpus+".test.lab.ORG.arff"), StandardCharsets.UTF_8);

		arffoutCOMPLETE.write("@RELATION \"multiclass_instances_test\""+"\n");
		arffoutCOMPLETELAB.write("@RELATION \"multiclass_instances_test\""+"\n");
		
		arffoutCOMPLETE.write("@ATTRIBUTE docid        STRING"+"\n");
		arffoutCOMPLETELAB.write("@ATTRIBUTE docid        STRING"+"\n");

		arffoutCOMPLETE.write("@ATTRIBUTE mention      STRING"+"\n");
		arffoutCOMPLETELAB.write("@ATTRIBUTE mention      STRING"+"\n");

		arffoutCOMPLETE.write("@ATTRIBUTE offset       NUMERIC"+"\n");
		arffoutCOMPLETELAB.write("@ATTRIBUTE offset       NUMERIC"+"\n");
		
		arffoutCOMPLETE.write("@ATTRIBUTE link       STRING"+"\n");
		arffoutCOMPLETELAB.write("@ATTRIBUTE link       STRING"+"\n");
		
//		arffoutCOMPLETE.write("@ATTRIBUTE slen       NUMERIC"+"\n");
//		arffoutCOMPLETELAB.write("@ATTRIBUTE slen       NUMERIC"+"\n");
		
		arffoutCOMPLETE.write("@ATTRIBUTE swords  NUMERIC"+"\n");
		arffoutCOMPLETELAB.write("@ATTRIBUTE swords  NUMERIC"+"\n");
		
		arffoutCOMPLETE.write("@ATTRIBUTE sf         NUMERIC"+"\n");
		arffoutCOMPLETELAB.write("@ATTRIBUTE sf         NUMERIC"+"\n");
		
		arffoutCOMPLETE.write("@ATTRIBUTE sdf      NUMERIC"+"\n");
		arffoutCOMPLETELAB.write("@ATTRIBUTE sdf      NUMERIC"+"\n");
		
		arffoutCOMPLETE.write("@ATTRIBUTE scand      NUMERIC"+"\n");
		arffoutCOMPLETELAB.write("@ATTRIBUTE scand      NUMERIC"+"\n");
		
		arffoutCOMPLETE.write("@ATTRIBUTE mpos      NUMERIC"+"\n");
		arffoutCOMPLETELAB.write("@ATTRIBUTE mpos      NUMERIC"+"\n");
		
		arffoutCOMPLETE.write("@ATTRIBUTE msent     NUMERIC"+"\n");
		arffoutCOMPLETELAB.write("@ATTRIBUTE msent     NUMERIC"+"\n");
		
		arffoutCOMPLETE.write("@ATTRIBUTE dwords     NUMERIC"+"\n");
		arffoutCOMPLETELAB.write("@ATTRIBUTE dwords     NUMERIC"+"\n");
		
		arffoutCOMPLETE.write("@ATTRIBUTE dents   NUMERIC"+"\n");
		arffoutCOMPLETELAB.write("@ATTRIBUTE dents   NUMERIC"+"\n");
		
		arffoutCOMPLETE.write("@ATTRIBUTE Class        {AMBIVERSE,BABELFY,TAGME}"+"\n");
		arffoutCOMPLETELAB.write("@ATTRIBUTE Class        {AMBIVERSE,BABELFY,TAGME}"+"\n");
		
		arffoutCOMPLETE.write("\n");
		arffoutCOMPLETELAB.write("\n");
		
		arffoutCOMPLETE.write("@DATA"+"\n");
		arffoutCOMPLETELAB.write("@DATA"+"\n");
		
		
		CSVReader csvparser = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream(testcsvFile), StandardCharsets.UTF_8)),',','\'');  
		String[] row = null;
        while ((row = csvparser.readNext() ) != null) {
        	if(row.length==13){
        		String docid = row[0].toLowerCase();
        		docid = docid.replaceAll("\'", "");
        		docid = docid.replaceAll("\"", "");
        		String mention = row[1].toLowerCase();
        		String offset = row[2];
//        		String GTLINK = row[3];
        		double slen = Double.parseDouble(row[4]);
        		double swords = Double.parseDouble(row[5]);
        		double sf = Double.parseDouble(row[6]);
        		double sdf = Double.parseDouble(row[7]);
        		double scand = Double.parseDouble(row[8]);
        		double mpos = Double.parseDouble(row[9]);
        		double msent = Double.parseDouble(row[10]);
        		double dwords = Double.parseDouble(row[11]); 
        		double dents =  Double.parseDouble(row[12]);
			
        		String features =  swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+dwords+","+dents;
//			Pattern p = Pattern.compile("(.+([^\"]*))"); // this crap fixes mathias "warlord" nygord
//			Matcher m = p.matcher(docid);
//			while (m.find()) {
//				docid = m.group(1);
//			}
//			m = p.matcher(mention);
//			while (m.find()) {
//				mention = m.group(1).toLowerCase();
//			}
			String k = docid+"\t"+mention.toLowerCase()+"\t"+offset;
			mention = mention.replaceAll("\"", "\\\\\""); //tim " ripper " owens
//			System.out.println(k);
			String GTLINK ="NULL";
			String lAMB = "NULL";
			String lBAB = "NULL";
			String lTAG = "NULL";
			
			//This is the case that no EL tool has recognized the mention at that specific position 
//			if((!AmbiMap.containsKey(k)) && (!BabMap.containsKey(k)) && (!TagMap.containsKey(k))){
////				continue;
//				System.out.println(k);
//			}
//			
			arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+"?" +"\n");
//			//// Ambiverse 
		    if( AmbiMap.containsKey(k)){
		    	lAMB = AmbiMap.get(k);
				if(lAMB.equalsIgnoreCase(GTLINK)){
					arffoutCOMPLETELAB.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+"\"AMBIVERSE\"" +"\n");
					continue;
				}
		    }
		    //// Babelfy
		    if( BabMap.containsKey(k)){     
				lBAB  = BabMap.get(k);
				if(lBAB.equalsIgnoreCase(GTLINK)){
			    	arffoutCOMPLETELAB.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+"\"BABELFY\"" +"\n");
					arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+"?" +"\n");
					continue;
				}
		    }
		    //// Tagme
		    if( TagMap.containsKey(k)){
		    	lTAG = TagMap.get(k);
				if(lTAG.equalsIgnoreCase(GTLINK)){
					arffoutCOMPLETELAB.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+"\"TAGME\"" +"\n");
					arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+"?" +"\n");
					continue;
				}
		    }
        }
        }
        csvparser.close();
		arffoutCOMPLETE.close();
		arffoutCOMPLETELAB.close();
		System.out.println("### Done. Writing the test set for Multiclass ### ");
	}
	
	/**
	 * 
	 * @param d
	 * @param traincsvFile
	 * @param percent
	 * @param corpus
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	private static void writeARFFMultilabelTestSet(DataLoaders d, String testcsvFile, String corpus) throws NumberFormatException, Exception{
		TreeMap<String,String> AmbiMap = d.getAmbiverseMap();
		TreeMap<String,String> BabMap = d.getBabelfyMap();
		TreeMap<String,String> TagMap = d.getTagmeMap();
		TreeMap<String,String> GT_MAP  = d.getGT_MAP();

		int ZeroTools = 0;
		
		System.out.println("### Writing the test set for Multilabel###");
		//############# Test ############
		OutputStreamWriter arffout = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+".test.arff"), StandardCharsets.UTF_8);
		OutputStreamWriter arffoutCOMPLETE = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+".test.ORG.arff"), StandardCharsets.UTF_8);
		
		OutputStreamWriter noPRED = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.multilabel."+corpus+".NOPREDNEEDED"), StandardCharsets.UTF_8);

		

		arffout.write("@RELATION \"multilabel_instances_test: -C -3\""+"\n");
		arffoutCOMPLETE.write("@RELATION \"multilabel_instances_test: -C -3\""+"\n");
		
		arffoutCOMPLETE.write("@ATTRIBUTE docid        STRING"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE mention      STRING"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE offset       NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE link      STRING"+"\n");
		
//		arffout.write("@ATTRIBUTE slen       NUMERIC"+"\n");
//		arffoutCOMPLETE.write("@ATTRIBUTE slen       NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE swords  NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE swords  NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE sf         NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE sf         NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE sdf      NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE sdf      NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE scand      NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE scand      NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE mpos      NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE mpos      NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE msent     NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE msent     NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE dwords     NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE dwords     NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE dents    NUMERIC"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE dents   NUMERIC"+"\n");
		
		arffout.write("@ATTRIBUTE Aclass        {0,1}"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE Aclass        {0,1}"+"\n");
		
		arffout.write("@ATTRIBUTE Bclass        {0,1}"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE Bclass        {0,1}"+"\n");
		
		arffout.write("@ATTRIBUTE Tclass        {0,1}"+"\n");
		arffoutCOMPLETE.write("@ATTRIBUTE Tclass        {0,1}"+"\n");
		
		arffout.write("\n");
		arffoutCOMPLETE.write("\n");
		
		arffout.write("@DATA"+"\n");
		arffoutCOMPLETE.write("@DATA"+"\n");
		@SuppressWarnings("deprecation")
		CSVReader csvparser = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream(testcsvFile), StandardCharsets.UTF_8)),',','\''); 
		String[] row = null;
        while ((row = csvparser.readNext() ) != null) {
        	//System.out.println(row.length);
//        	if(row.length == 13){
        		String docid = row[0].toLowerCase();
        		docid = docid.replaceAll("\'", "");
        		docid = docid.replaceAll("\"", "");
        		String mention = row[1].toLowerCase();
        		String offset = row[2];
//        		double slen = Double.parseDouble(row[4]);
        		double swords = Double.parseDouble(row[5]);
        		double sf = Double.parseDouble(row[6]);
        		double sdf = Double.parseDouble(row[7]);
        		double scand = Double.parseDouble(row[8]);
        		double mpos = Double.parseDouble(row[9]);
        		double msent = Double.parseDouble(row[10]);
        		double dwords = Double.parseDouble(row[11]); 
        		double dents =  Double.parseDouble(row[12]);
//
        		String features =  swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+dwords+","+dents;
        		String k = docid+"\t"+mention.toLowerCase()+"\t"+offset;
        		String GTLINK = GT_MAP.get(k); 
    			mention = mention.replaceAll("\"", "\\\\\""); //tim " ripper " owens
        		int l1 = 0;
        		int l2 = 0;
        		int l3 = 0;
    			//# CASE 0 -  There is no link
        		/* This is the case when the mention is NOT recognized by any of the tools  -  No prediction needed */
        		if((!AmbiMap.containsKey(k)) && (!BabMap.containsKey(k)) && (!TagMap.containsKey(k))){
        			ZeroTools++;
        			continue;
        		}
        		//# CASE 1  - One EL tool -- Ambiverse
        		if( (AmbiMap.containsKey(k)) && (!BabMap.containsKey(k)) && (!TagMap.containsKey(k)) ){
        			noPRED.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\n");
//    				l1 = 1;
//    				arffout.write(features+","+l1+","+l2+","+l3+"\n");
//    				arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+l1+","+l2+","+l3+"\n");
    				continue;
    			}
        		//# CASE 1  - One EL tool -- Babelfy
        		if( (!AmbiMap.containsKey(k)) && (BabMap.containsKey(k)) && (!TagMap.containsKey(k)) ){
        			noPRED.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\n");
//    				l2 = 1;
//    				arffout.write(features+","+l1+","+l2+","+l3+"\n");
//    				arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+l1+","+l2+","+l3+"\n");
    				continue;
    			}
        		//# CASE 1  - One EL tool -- Tagme
        		if( (!AmbiMap.containsKey(k)) && (!BabMap.containsKey(k)) && (TagMap.containsKey(k)) ){
        			noPRED.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\n");
//    				l3 = 1;
//    				arffout.write(features+","+l1+","+l2+","+l3+"\n");
//    				arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+l1+","+l2+","+l3+"\n");
    				continue;
    			}
        		
        		//# CASE 2  - Two EL tools -- Ambiverse and Babelfy
        		if( (AmbiMap.containsKey(k)) && (BabMap.containsKey(k)) && (!TagMap.containsKey(k)) ){
        			String Alink = AmbiMap.get(k);
        			String Blink = BabMap.get(k);
        			
        			if(Alink.equalsIgnoreCase(Blink)){
        				noPRED.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\n");
        			}else{
//        			if(!Alink.equalsIgnoreCase(Blink)){
        				if(Alink.equalsIgnoreCase(GTLINK)){
        					l1 = 1;
        				}
        				if(Blink.equalsIgnoreCase(GTLINK)){
        					l2 = 1;
        				} 
        				arffout.write(features+","+l1+","+l2+","+l3+"\n");
        				arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+l1+","+l2+","+l3+"\n");
        				continue;
        			}
        			
        			
        		}
        		// CASE 2  - Two EL tools -- Ambiverse and Tagme
        		if( (AmbiMap.containsKey(k)) && (!BabMap.containsKey(k)) && (TagMap.containsKey(k)) ){
        			String Alink = AmbiMap.get(k);
        			String Tlink = TagMap.get(k);
        			if(Alink.equalsIgnoreCase(Tlink)){
        				noPRED.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\n");
        			}else{
//        			if(!Alink.equalsIgnoreCase(Tlink)){
        				if(Alink.equalsIgnoreCase(GTLINK)){
        					l1 = 1;
        				}
        				if(Tlink.equalsIgnoreCase(GTLINK)){
        					l3 = 1;
        				}
        				arffout.write(features+","+l1+","+l2+","+l3+"\n");
        				arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+l1+","+l2+","+l3+"\n");
        				continue;
        			}
        			
        	
        		}
        		// CASE 2  - Two EL tools -- Babelfy and Tagme
        		if( (!AmbiMap.containsKey(k)) && (BabMap.containsKey(k)) && (TagMap.containsKey(k)) ){
        			String Blink = BabMap.get(k);
        			String Tlink = TagMap.get(k);
        			if(Blink.equalsIgnoreCase(Tlink)){
        				noPRED.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\n");
        			}else{
        			
        				//        			if(!Blink.equalsIgnoreCase(Tlink)){
        				if(Blink.equalsIgnoreCase(GTLINK)){
        					l2 = 1;
        				}
        				if(Tlink.equalsIgnoreCase(GTLINK)){
        					l3 = 1;
        				}
        				arffout.write(features+","+l1+","+l2+","+l3+"\n");
        				arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+l1+","+l2+","+l3+"\n");
        				continue;
        			}
        		}
        		// CASE 3  - Three  EL tools -- Ambiverse Babelfy and Tagme
        		if( (AmbiMap.containsKey(k)) && (BabMap.containsKey(k)) && (TagMap.containsKey(k)) ){
        			String Alink = AmbiMap.get(k);
        			String Blink = BabMap.get(k);
        			String Tlink = TagMap.get(k);
        			if( (Alink.equalsIgnoreCase(Blink)) && (Alink.equalsIgnoreCase(Tlink)) && (Blink.equalsIgnoreCase(Tlink)) ){
        				noPRED.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\n");
        			}else{
        			
//        			if( (!Alink.equalsIgnoreCase(Blink)) || (!Alink.equalsIgnoreCase(Tlink)) || (!Blink.equalsIgnoreCase(Tlink)) ){
        				if(Alink.equalsIgnoreCase(GTLINK)){
        					l1 = 1;
        				}
        				if(Blink.equalsIgnoreCase(GTLINK)){
        					l2 = 1;
        				}
        				if(Tlink.equalsIgnoreCase(GTLINK)){
        					l3 = 1;
        				}
        				arffout.write(features+","+l1+","+l2+","+l3+"\n");
        				arffoutCOMPLETE.write("\""+docid+"\""+","+"\""+mention+"\""+","+offset+','+"\""+GTLINK+"\""+','+features+","+l1+","+l2+","+l3+"\n");
        				continue;
        			}
        			
        			
        		}
       		

	}
       noPRED.flush();
       noPRED.close();
       csvparser.close();
       arffoutCOMPLETE.close();
       arffout.close();
       System.out.println("### Done. Writing the test set for Multilabel classification task###");
       System.out.println("# mentions recognized by 0 tools : "+ ZeroTools);
	}
	
	
	/**
	 *
	 * @param testcsvFile
	 * @throws Exception 
	 * @throws NumberFormatException 
	 */
	private static void writeARFFOneClassTestSetR1(DataLoaders d, String testcsvFile, String corpus) throws NumberFormatException, Exception{
		
		TreeMap<String,String> AmbiMap = d.getAmbiverseMap();
		TreeMap<String,String> BabMap = d.getBabelfyMap();
		TreeMap<String,String> TagMap = d.getTagmeMap();
		TreeMap<String,String> GT_MAP = d.getGT_MAP();
		
		System.out.println("Writing test data for the one class classifiers.");
		
		OutputStreamWriter arffoutR1AmbCOMPLETE = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.oneclass."+corpus+".test.R1.Amb.arff"), StandardCharsets.UTF_8);
		OutputStreamWriter arffoutR1BabCOMPLETE = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.oneclass."+corpus+".test.R1.Bab.arff"), StandardCharsets.UTF_8);
		OutputStreamWriter arffoutR1TagCOMPLETE = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.oneclass."+corpus+".test.R1.Tag.arff"), StandardCharsets.UTF_8);
	
		OutputStreamWriter arffoutR1AmbCOMPLETELab = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.oneclass."+corpus+".test.lab.R1.Amb.arff"), StandardCharsets.UTF_8);
		OutputStreamWriter arffoutR1BabCOMPLETELab = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.oneclass."+corpus+".test.lab.R1.Bab.arff"), StandardCharsets.UTF_8);
		OutputStreamWriter arffoutR1TagCOMPLETELab = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.oneclass."+corpus+".test.lab.R1.Tag.arff"), StandardCharsets.UTF_8);

		arffoutR1AmbCOMPLETE.write("@RELATION \"oneclass_instances_R1_Amb_test\""+"\n");
		arffoutR1BabCOMPLETE.write("@RELATION \"oneclass_instances_R1_Bab_test\""+"\n");
		arffoutR1TagCOMPLETE.write("@RELATION \"oneclass_instances_R1_Tag_test\""+"\n");
		
		arffoutR1AmbCOMPLETELab.write("@RELATION \"oneclass_instances_R1_Amb_test\""+"\n");
		arffoutR1BabCOMPLETELab.write("@RELATION \"oneclass_instances_R1_Bab_test\""+"\n");
		arffoutR1TagCOMPLETELab.write("@RELATION \"oneclass_instances_R1_Tag_test\""+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE docid        STRING"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE docid        STRING"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE docid        STRING"+"\n");
		
		arffoutR1AmbCOMPLETELab.write("@ATTRIBUTE docid        STRING"+"\n");
		arffoutR1BabCOMPLETELab.write("@ATTRIBUTE docid        STRING"+"\n");
		arffoutR1TagCOMPLETELab.write("@ATTRIBUTE docid        STRING"+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE mention      STRING"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE mention      STRING"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE mention      STRING"+"\n");
		
		arffoutR1AmbCOMPLETELab.write("@ATTRIBUTE mention      STRING"+"\n");
		arffoutR1BabCOMPLETELab.write("@ATTRIBUTE mention      STRING"+"\n");
		arffoutR1TagCOMPLETELab.write("@ATTRIBUTE mention      STRING"+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE offset       NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE offset       NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE offset       NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETELab.write("@ATTRIBUTE offset       NUMERIC"+"\n");
		arffoutR1BabCOMPLETELab.write("@ATTRIBUTE offset       NUMERIC"+"\n");
		arffoutR1TagCOMPLETELab.write("@ATTRIBUTE offset       NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE link      STRING"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE link      STRING"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE link      STRING"+"\n");
		
		arffoutR1AmbCOMPLETELab.write("@ATTRIBUTE link      STRING"+"\n");
		arffoutR1BabCOMPLETELab.write("@ATTRIBUTE link      STRING"+"\n");
		arffoutR1TagCOMPLETELab.write("@ATTRIBUTE link      STRING"+"\n");
		
//		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE slen       NUMERIC"+"\n");
//		arffoutR1BabCOMPLETE.write("@ATTRIBUTE slen       NUMERIC"+"\n");
//		arffoutR1TagCOMPLETE.write("@ATTRIBUTE slen       NUMERIC"+"\n");
//		
//		arffoutR1AmbCOMPLETELab.write("@ATTRIBUTE slen       NUMERIC"+"\n");
//		arffoutR1BabCOMPLETELab.write("@ATTRIBUTE slen       NUMERIC"+"\n");
//		arffoutR1TagCOMPLETELab.write("@ATTRIBUTE slen       NUMERIC"+"\n");

		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE swords  NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE swords  NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE swords  NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETELab.write("@ATTRIBUTE swords  NUMERIC"+"\n");
		arffoutR1BabCOMPLETELab.write("@ATTRIBUTE swords  NUMERIC"+"\n");
		arffoutR1TagCOMPLETELab.write("@ATTRIBUTE swords  NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE sf         NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE sf         NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE sf         NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETELab.write("@ATTRIBUTE sf         NUMERIC"+"\n");
		arffoutR1BabCOMPLETELab.write("@ATTRIBUTE sf         NUMERIC"+"\n");
		arffoutR1TagCOMPLETELab.write("@ATTRIBUTE sf         NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE sdf      NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE sdf      NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE sdf      NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETELab.write("@ATTRIBUTE sdf      NUMERIC"+"\n");
		arffoutR1BabCOMPLETELab.write("@ATTRIBUTE sdf      NUMERIC"+"\n");
		arffoutR1TagCOMPLETELab.write("@ATTRIBUTE sdf      NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE scand      NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE scand      NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE scand      NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETELab.write("@ATTRIBUTE scand      NUMERIC"+"\n");
		arffoutR1BabCOMPLETELab.write("@ATTRIBUTE scand      NUMERIC"+"\n");
		arffoutR1TagCOMPLETELab.write("@ATTRIBUTE scand      NUMERIC"+"\n");

		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE mpos      NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE mpos      NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE mpos      NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETELab.write("@ATTRIBUTE mpos      NUMERIC"+"\n");
		arffoutR1BabCOMPLETELab.write("@ATTRIBUTE mpos      NUMERIC"+"\n");
		arffoutR1TagCOMPLETELab.write("@ATTRIBUTE mpos      NUMERIC"+"\n");

		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE msent     NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE msent     NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE msent     NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETELab.write("@ATTRIBUTE msent     NUMERIC"+"\n");
		arffoutR1BabCOMPLETELab.write("@ATTRIBUTE msent     NUMERIC"+"\n");
		arffoutR1TagCOMPLETELab.write("@ATTRIBUTE msent     NUMERIC"+"\n");

		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE dwords     NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE dwords     NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE dwords     NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETELab.write("@ATTRIBUTE dwords     NUMERIC"+"\n");
		arffoutR1BabCOMPLETELab.write("@ATTRIBUTE dwords     NUMERIC"+"\n");
		arffoutR1TagCOMPLETELab.write("@ATTRIBUTE dwords     NUMERIC"+"\n");

		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE dents   NUMERIC"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE dents   NUMERIC"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE dents   NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETELab.write("@ATTRIBUTE dents   NUMERIC"+"\n");
		arffoutR1BabCOMPLETELab.write("@ATTRIBUTE dents   NUMERIC"+"\n");
		arffoutR1TagCOMPLETELab.write("@ATTRIBUTE dents   NUMERIC"+"\n");
		
		arffoutR1AmbCOMPLETE.write("@ATTRIBUTE Class	{1}"+"\n");
		arffoutR1BabCOMPLETE.write("@ATTRIBUTE Class	{1}"+"\n");
		arffoutR1TagCOMPLETE.write("@ATTRIBUTE Class	{1}"+"\n");
		
		arffoutR1AmbCOMPLETELab.write("@ATTRIBUTE Class	{1}"+"\n");
		arffoutR1BabCOMPLETELab.write("@ATTRIBUTE Class	{1}"+"\n");
		arffoutR1TagCOMPLETELab.write("@ATTRIBUTE Class	{1}"+"\n");
		
		arffoutR1AmbCOMPLETE.write("\n");
		arffoutR1BabCOMPLETE.write("\n");
		arffoutR1TagCOMPLETE.write("\n");
		
		arffoutR1AmbCOMPLETELab.write("\n");
		arffoutR1BabCOMPLETELab.write("\n");
		arffoutR1TagCOMPLETELab.write("\n");
		
		arffoutR1AmbCOMPLETE.write("@DATA"+"\n");
		arffoutR1BabCOMPLETE.write("@DATA"+"\n");
		arffoutR1TagCOMPLETE.write("@DATA"+"\n");
		
		arffoutR1AmbCOMPLETELab.write("@DATA"+"\n");
		arffoutR1BabCOMPLETELab.write("@DATA"+"\n");
		arffoutR1TagCOMPLETELab.write("@DATA"+"\n");
		
		
		@SuppressWarnings("deprecation")
		CSVReader csvparser = new  CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream(testcsvFile), StandardCharsets.UTF_8)),',','\'');  
		String[] row = null;
        while ((row = csvparser.readNext() ) != null) {
        	if(row.length == 13){
			String docid = row[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			String mention = row[1].toLowerCase();
			String offset = row[2];
//			String GTLINK =row[3];
//			double slen = Double.parseDouble(row[4]);
			double swords = Double.parseDouble(row[5]);
			double sf = Double.parseDouble(row[6]);
			double sdf = Double.parseDouble(row[7]);
			double scand = Double.parseDouble(row[8]);
			double mpos = Double.parseDouble(row[9]);
			double msent = Double.parseDouble(row[10]);
			double dwords = Double.parseDouble(row[11]); 
			double dents =  Double.parseDouble(row[12]);

			/** EXCLUDING Scorr and Sratio **/
			String featuresR1Amb = swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+dwords+","+dents;
			String featuresR1Bab =  swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+dwords+","+dents;
			String featuresR1Tag =  swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+dwords+","+dents;
		
			/*** ORIGINAL */
//			String featuresR1Amb = slen+","+swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+scorrAmb+","+sratioAmb+","+dwords+","+dents;
//			String featuresR1Bab = slen+","+swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+scorrBab+","+sratioBab+","+dwords+","+dents;
//			String featuresR1Tag = slen+","+swords+","+sf+","+sdf+","+scand+","+mpos+","+msent+","+scorrTag+","+sratioTag+","+dwords+","+dents;

			String k = docid+"\t"+mention.toLowerCase()+"\t"+offset;
			
			mention = mention.replaceAll("\"", "\\\\\""); 
			
			String GTLINK = "NULL";
			
			//# CASE 0 -  There is no link
	        /* This is the case when the mention is NOT recognized by any of the tools  -  No prediction needed */
    		if((!AmbiMap.containsKey(k)) && (!BabMap.containsKey(k)) && (!TagMap.containsKey(k))){
    			continue;
    		}
			
			//R1 . Creating the data for Ambiverse classifier
			if (AmbiMap.containsKey(k)){
//				String lAmb = AmbiMap.get(k);
				arffoutR1AmbCOMPLETE.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+featuresR1Amb+","+ "?" +'\n');
//				if(GTLINK.equalsIgnoreCase(lAmb)){
//				arffoutR1AmbCOMPLETELab.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+featuresR1Amb+","+ "1" +'\n');
//				}
//				}else{
//					arffoutR1AmbCOMPLETELab.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+featuresR1Amb+","+ "0" +'\n');
//				}
			}
//			//R1 . Creating the data for Babelfy classifier
			if (BabMap.containsKey(k)){
//				String lBab = BabMap.get(k);
				arffoutR1BabCOMPLETE.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+featuresR1Bab+","+ "?" +'\n');
//				if(GTLINK.equalsIgnoreCase(lBab)){
//				arffoutR1BabCOMPLETELab.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+featuresR1Bab+","+ "1" +'\n');
//				}
//				}else{
//					arffoutR1BabCOMPLETELab.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+featuresR1Bab+","+ "0" +'\n');
//				}
			}
			//R1 . Creating the data for Tagme classifier
			if (TagMap.containsKey(k)){
//				String lTag = TagMap.get(k);
				arffoutR1TagCOMPLETE.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+featuresR1Tag+","+ "?" +'\n');
//				if(GTLINK.equalsIgnoreCase(lTag)){
//				arffoutR1TagCOMPLETELab.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+"\""+GTLINK+"\""+','+featuresR1Tag+","+ "1" +'\n');
//				}
//				}else{
//					arffoutR1TagCOMPLETELab.write("\""+docid+"\""+","+"\""+mention.toLowerCase()+"\""+","+offset+','+featuresR1Tag+","+ "0" +'\n');
//				}
			}
		 }
	}
		csvparser.close();
		arffoutR1AmbCOMPLETE.close();
		arffoutR1BabCOMPLETE.close();
		arffoutR1TagCOMPLETE.close();
		
		arffoutR1AmbCOMPLETELab.close();
		arffoutR1BabCOMPLETELab.close();
		arffoutR1TagCOMPLETELab.close();
		System.out.println("### Done. Writing the test set for OneClass Classifiers ###");

	}

	
}
