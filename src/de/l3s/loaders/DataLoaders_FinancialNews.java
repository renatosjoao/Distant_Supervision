package de.l3s.loaders;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.compress.compressors.CompressorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataLoaders_FinancialNews extends DataLoaders{
	
		public static void main(String[] args) throws CompressorException, IOException{
			DataLoaders_FinancialNews d = DataLoaders_FinancialNews.getInstance();
			System.out.println(ambiverseMap.keySet().size());
			System.out.println(babelMap.keySet().size());
			System.out.println(spotlightMap.keySet().size());
			System.out.println(tagmeMap.keySet().size());
			
			calculateAgreement();
		}
		
		
		private static void calculateAgreement() throws IOException {
			OutputStreamWriter pAnnSAME4 = new OutputStreamWriter(new FileOutputStream("./resources/ds/annotations.4.same.tsv"),StandardCharsets.UTF_8);
			int ABSTrec = 0, ABSTsame = 0;
			int ABSrec = 0, ABSsame = 0;
			int ABTrec = 0, ABTsame = 0;
			int ASTrec = 0, ASTsame = 0;
			int BSTrec = 0, BSTsame = 0;
			
			int ABrec = 0, ABsame = 0;
			int ASrec = 0, ASsame = 0;
			int ATrec = 0, ATsame = 0;
			int BSrec = 0, BSsame = 0;
			int BTrec = 0, BTsame = 0;
			int STrec = 0, STsame = 0;
			int Arec=0, Brec=0, Srec=0, Trec=0;
			
			TreeSet<String> fourMentions = new TreeSet<String>();
			TreeSet<String> threeMentions = new TreeSet<String>();
			TreeSet<String> twoMentions = new TreeSet<String>();
			TreeSet<String> oneMentions = new TreeSet<String>();

			Iterator it = tagmeMap.entrySet().iterator();  // iterating over Tagme because it is the smallest
			
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
				String key = (String) pair.getKey();
//				String GTLink = (String) pair.getValue();
				String Alink = ambiverseMap.get(key);
				String Blink = babelMap.get(key);
				String Slink = spotlightMap.get(key);
				String Tlink = tagmeMap.get(key);
			

//			//# mentions recognized by 4 EL tools
		    if( (ambiverseMap.containsKey(key)) && (babelMap.containsKey(key)) && (spotlightMap.containsKey(key)) && (tagmeMap.containsKey(key)) ) {
		    	ABSTrec++;
  				String mention = key.split("\t")[1];
   				fourMentions.add(mention);
//   				pAnn4.write(key+"\t"+"GT:"+GTLink+"\t"+"A:"+Alink+"\t"+"B:"+Blink+"\t"+"S:"+Slink+"\t"+"T:"+Tlink+"\n");
//	    	
   				if((Alink.equalsIgnoreCase(Blink)) && (Alink.equalsIgnoreCase(Slink)) &&  (Alink.equalsIgnoreCase(Tlink))  && (Blink.equalsIgnoreCase(Slink))  && (Blink.equalsIgnoreCase(Tlink))  && (Slink.equalsIgnoreCase(Tlink)) ){
  					ABSTsame++;
   					pAnnSAME4.write(key+"\t"+"\t"+"A:"+Alink+"\t"+"B:"+Blink+"\t"+"S:"+Slink+"\t"+"T:"+Tlink+"\n");

////		    					if(Alink.equalsIgnoreCase(GTLink)){
////		    						tp4++;
///		    					}
   				}
				continue;
		    }
//		    
//			  //# mentions recognized by 3 EL tools A B S
		    if( (ambiverseMap.containsKey(key)) && (babelMap.containsKey(key)) && (spotlightMap.containsKey(key)) && (!tagmeMap.containsKey(key)) ){
		    	ABSrec++;

		    	String mention = key.split("\t")[1];
		    	threeMentions.add(mention);
		    	
//		    	pAnn3.write(key+"\t"+"GT:"+GTLink+"\t"+"A:"+Alink+"\t"+"B:"+Blink+"\t"+"S:"+Slink+"\t"+"T:"+Tlink+"\n");
		    	
		    	if((Alink.equalsIgnoreCase(Blink)) && (Alink.equalsIgnoreCase(Slink))){
		    		ABSsame++;
//		    		pAnnSAME3.write(key+"\t"+"GT:"+GTLink+"\t"+"A:"+Alink+"\t"+"B:"+Blink+"\t"+"S:"+Slink+"\t"+"T:"+Tlink+"\n");
//		    		if(Alink.equalsIgnoreCase(GTLink)){
//		    			tp3++;
//		    		}
		    	}
		    	continue;
		    }
//		    //# mentions recognized by 3 EL tools
		    if((ambiverseMap.containsKey(key)) && (babelMap.containsKey(key)) && (!spotlightMap.containsKey(key)) && (tagmeMap.containsKey(key)) ){
		    	ABTrec++;

		    	String mention = key.split("\t")[1];
		    	threeMentions.add(mention);
//		    	
//		    	pAnn3.write(key+"\t"+"GT:"+GTLink+"\t"+"A:"+Alink+"\t"+"B:"+Blink+"\t"+"S:"+Slink+"\t"+"T:"+Tlink+"\n");
		    	
		    	if((Alink.equalsIgnoreCase(Blink)) && (Alink.equalsIgnoreCase(Tlink))){
		    		ABTsame++;
//		    		pAnnSAME3.write(key+"\t"+"GT:"+GTLink+"\t"+"A:"+Alink+"\t"+"B:"+Blink+"\t"+"S:"+Slink+"\t"+"T:"+Tlink+"\n");
//		    		if(Alink.equalsIgnoreCase(GTLink)){
//		    			tp3++;
//		    		}
		    	}
		    	continue;
		    }
		  //# mentions recognized by 3 EL tools
		    if( (ambiverseMap.containsKey(key)) && (!babelMap.containsKey(key)) && (spotlightMap.containsKey(key)) && (tagmeMap.containsKey(key)) ){
		    	ASTrec++;
		    	
		    	String mention = key.split("\t")[1];
		    	threeMentions.add(mention);

//		    	pAnn3.write(key+"\t"+"GT:"+GTLink+"\t"+"A:"+Alink+"\t"+"B:"+Blink+"\t"+"S:"+Slink+"\t"+"T:"+Tlink+"\n");
		    	
		    	if((Alink.equalsIgnoreCase(Slink)) && (Alink.equalsIgnoreCase(Tlink))){
		    		ASTsame++;
//		    		pAnnSAME3.write(key+"\t"+"GT:"+GTLink+"\t"+"A:"+Alink+"\t"+"B:"+Blink+"\t"+"S:"+Slink+"\t"+"T:"+Tlink+"\n");
//		    		
//		    		if(Alink.equalsIgnoreCase(GTLink)){
//		    			tp3++;
//		    		}
		    	}
		    	continue;
		    }
		    
		  //# mentions recognized by 3 EL tools
		    if( (!ambiverseMap.containsKey(key)) && (babelMap.containsKey(key)) && (spotlightMap.containsKey(key)) && (tagmeMap.containsKey(key)) ){
		    	BSTrec++;
		    	String mention = key.split("\t")[1];
		    	threeMentions.add(mention);
//		    	
//		    	pAnn3.write(key+"\t"+"GT:"+GTLink+"\t"+"A:"+Alink+"\t"+"B:"+Blink+"\t"+"S:"+Slink+"\t"+"T:"+Tlink+"\n");
//		    	
		    	if((Blink.equalsIgnoreCase(Slink)) && (Blink.equalsIgnoreCase(Tlink))){
		    		BSTsame++;
//			    	pAnnSAME3.write(key+"\t"+"GT:"+GTLink+"\t"+"A:"+Alink+"\t"+"B:"+Blink+"\t"+"S:"+Slink+"\t"+"T:"+Tlink+"\n");
//				    	if(Blink.equalsIgnoreCase(GTLink)){
//			    		tp3++;
//			    	}
		    	
		    	}
		    	continue;
		    }
//		  
		    //# mentions recognized by 2 EL tools
		    if( (ambiverseMap.containsKey(key)) && (babelMap.containsKey(key)) && (!spotlightMap.containsKey(key)) && (!tagmeMap.containsKey(key)) ){
		    	ABrec++;
		    	String mention = key.split("\t")[1];
		    	twoMentions.add(mention);
//		    	
//		    	pAnn2.write(key+"\t"+"GT:"+GTLink+"\t"+"A:"+Alink+"\t"+"B:"+Blink+"\n");
		    	
		    	if((Alink.equalsIgnoreCase(Blink))){
		    		ABsame++;
//		    		pAnnSAME2.write(key+"\t"+"GT:"+GTLink+"\t"+"A:"+Alink+"\t"+"B:"+Blink+"\n");
//		    		if(Alink.equalsIgnoreCase(GTLink)){
//		    			tp2++;
//		    		}
		    	}
	    	continue;
	    }
//		  //# mentions recognized by 2 EL tools
		    if( (ambiverseMap.containsKey(key)) && (!babelMap.containsKey(key)) && (spotlightMap.containsKey(key)) && (!tagmeMap.containsKey(key)) ){
		    	ASrec++;
		    	String mention = key.split("\t")[1];
		    	twoMentions.add(mention);
//		    	
//		    	pAnn2.write(key+"\t"+"GT:"+GTLink+"\t"+"A:"+Alink+"\t"+"S:"+Slink+"\n");
//
		    	if((Alink.equalsIgnoreCase(Slink))){
		    		ASsame++;
//		    		pAnnSAME2.write(key+"\t"+"GT:"+GTLink+"\t"+"A:"+Alink+"\t"+"S:"+Slink+"\n");
//		    		if(Alink.equalsIgnoreCase(GTLink)){
//		    			tp2++;
//		    		}
		    	}
		    	continue;
		    	
		    }
		  //# mentions recognized by 2 EL tools
		    if( (ambiverseMap.containsKey(key)) && (!babelMap.containsKey(key)) && (!spotlightMap.containsKey(key)) && (tagmeMap.containsKey(key)) ){
		    	ATrec++;
		    	String mention = key.split("\t")[1];
		    	twoMentions.add(mention);
		    	
//		    	pAnn2.write(key+"\t"+"GT:"+GTLink+"\t"+"A:"+Alink+"\t"+"T:"+Tlink+"\n");
//		    	
		    	if((Alink.equalsIgnoreCase(Tlink))){
		    		ATsame++;
//		    		pAnnSAME2.write(key+"\t"+"GT:"+GTLink+"\t"+"A:"+Alink+"\t"+"T:"+Tlink+"\n");
//		    		if(Alink.equalsIgnoreCase(GTLink)){
//		    			tp2++;
//		    		}
		    	}
		    }

		  //# mentions recognized by 2 EL tools
		    if( (!ambiverseMap.containsKey(key)) && (babelMap.containsKey(key)) && (spotlightMap.containsKey(key)) && (!tagmeMap.containsKey(key)) ){
		    	BSrec++;
		    	String mention = key.split("\t")[1];
		    	twoMentions.add(mention);
		    	
//		    	pAnn2.write(key+"\t"+"GT:"+GTLink+"\t"+"B:"+Blink+"\t"+"S:"+Slink+"\n");
//		    	
		    	if((Blink.equalsIgnoreCase(Slink))){
//		    		pAnnSAME2.write(key+"\t"+"GT:"+GTLink+"\t"+"B:"+Blink+"\t"+"S:"+Slink+"\n");
//	    		if(Blink.equalsIgnoreCase(GTLink)){
////		    			tp2++;
////		    		}
		    	}
		    }
//
//		  //# mentions recognized by 2 EL tools
		    if( (!ambiverseMap.containsKey(key)) && (babelMap.containsKey(key)) && (!spotlightMap.containsKey(key)) && (tagmeMap.containsKey(key)) ){
		    	BTrec++;
		    	String mention = key.split("\t")[1];
		    	twoMentions.add(mention);
//		    	
//		    	pAnn2.write(key+"\t"+"GT:"+GTLink+"\t"+"B:"+Blink+"\t"+"T:"+Tlink+"\n");
//		    	
		    	if((Blink.equalsIgnoreCase(Tlink))){
//		    		pAnnSAME2.write(key+"\t"+"GT:"+GTLink+"\t"+"B:"+Blink+"\t"+"T:"+Tlink+"\n");
//		    		if(Blink.equalsIgnoreCase(GTLink)){
//		    			tp2++;
//		    		}
		    	}
		    }
//		    
//			  //# mentions recognized by 2 EL tools
		    if( (!ambiverseMap.containsKey(key)) && (!babelMap.containsKey(key)) && (spotlightMap.containsKey(key)) && (tagmeMap.containsKey(key)) ){
		    	STrec++;
		    	String mention = key.split("\t")[1];
		    	twoMentions.add(mention);

//		    	pAnn2.write(key+"\t"+"GT:"+GTLink+"\t"+"S:"+Slink+"\t"+"T:"+Tlink+"\n");
//		    	
		    	if((Slink.equalsIgnoreCase(Tlink))){
//		    		pAnnSAME2.write(key+"\t"+"GT:"+GTLink+"\t"+"S:"+Slink+"\t"+"T:"+Tlink+"\n");
//		    		if(Slink.equalsIgnoreCase(GTLink)){
//		    			tp2++;
//		    		}
		    	}
		    }
//		    
//		    //# mentions recognized by 1 EL tools
		    if( (ambiverseMap.containsKey(key)) && (!babelMap.containsKey(key)) && (!spotlightMap.containsKey(key)) && (!tagmeMap.containsKey(key)) ){
		    	Arec++;
		    	String mention = key.split("\t")[1];
		    	oneMentions.add(mention);
//		    	pAnn1.write(key+"\t"+"GT:"+GTLink+"\t"+"A:"+Alink+"\n");
//		    	if(Alink.equalsIgnoreCase(GTLink)){
//		    		tp1++;
//		    	}
		    	
		    }
//
//		    //# mentions recognized by 1 EL tools
		    if( (!ambiverseMap.containsKey(key)) && (babelMap.containsKey(key)) && (!spotlightMap.containsKey(key)) && (!tagmeMap.containsKey(key)) ){
		    	Brec++;
		    	String mention = key.split("\t")[1];
		    	oneMentions.add(mention);
//		    	pAnn1.write(key+"\t"+"GT:"+GTLink+"\t"+"B:"+Blink+"\n");
//		    	if(Blink.equalsIgnoreCase(GTLink)){
//		    		tp1++;
//		    	}
		    }
//		    
//		    //# mentions recognized by 1 EL tools
		    if( (!ambiverseMap.containsKey(key)) && (!babelMap.containsKey(key)) && (spotlightMap.containsKey(key)) && (!tagmeMap.containsKey(key)) ){
		    	Srec++;
		    	String mention = key.split("\t")[1];
		    	oneMentions.add(mention);
//		    	pAnn1.write(key+"\t"+"GT:"+GTLink+"\t"+"S:"+Slink+"\n");
//		    	if(Slink.equalsIgnoreCase(GTLink)){
//		    		tp1++;
//		    	}
		    		
		    	
		    }
//
//		    //# mentions recognized by 1 EL tools
		    if( (!ambiverseMap.containsKey(key)) && (!babelMap.containsKey(key)) && (!spotlightMap.containsKey(key)) && (tagmeMap.containsKey(key)) ){
		    	Trec++;
		    	String mention = key.split("\t")[1];
		    	oneMentions.add(mention);
////		    	pAnn1.write(key+"\t"+"GT:"+GTLink+"\t"+"T:"+Tlink+"\n");
////		    	if(Tlink.equalsIgnoreCase(GTLink)){
////		    		tp1++;
////		    	}
//		    
		    }
//
//		  //# mentions recognized by 0 EL tools
		    if( (!ambiverseMap.containsKey(key)) && (!babelMap.containsKey(key)) && (!spotlightMap.containsKey(key)) && (!tagmeMap.containsKey(key)) ){
//		    	pAnn0.write(key+"\t"+"GT:"+GTLink+"\n");

		    }
//		    
		    it.remove(); // avoids a ConcurrentModificationException
		}
			System.out.println(" GT mentions recognized by 4 EL tools :"+ABSTrec);
			System.out.println(" GT mentions recognized by 4 EL tools ( full agreement ):"+ABSTsame);
			System.out.println(" GT mentions recognized by 3 EL tools :"+ (ABSrec + ABTrec + ASTrec + BSTrec));
			System.out.println(" GT mentions recognized by 3 EL tools ( full agreement ):"+ (ABSsame + ABTsame + ASTsame + BSTsame));
			System.out.println(" GT mentions recognized by 2 EL tools :"+(ABrec + ASrec + ATrec + BSrec + BTrec + STrec));
			System.out.println(" GT mentions recognized by 2 EL tools ( full agreement ):"+(ABsame + ASsame + ATsame + BSsame + BTsame + STsame));
			System.out.println("Total GT mentions recognized by 1 EL tool:"+(Arec + Brec + Srec + Trec));
			System.out.println("....A :"+ Arec);
			System.out.println("....B :"+ Brec);
			System.out.println("....S :"+ Srec);
			System.out.println("....T :"+ Trec);
			pAnnSAME4.close();
		}


		/**
	 *
	 * @return
	 * @throws IOException 
	 * @throws CompressorException 
	 */
	public static DataLoaders_FinancialNews getInstance() throws CompressorException, IOException {
		if(instance == null) {
			 synchronized(DataLoaders_FinancialNews.class) {
				 instance = new DataLoaders_FinancialNews();
			 }
	    }
		return (DataLoaders_FinancialNews) instance;
	}
	

	
	
	
	public DataLoaders_FinancialNews() throws CompressorException, IOException{
		
		ambiverseMap = new TreeMap<String, String>();
		babelMap = new TreeMap<String, String>();
		tagmeMap = new TreeMap<String, String>();
		spotlightMap = new TreeMap<String, String>();
		loadMappings();

	}
	
	private static void loadMappings() throws IOException{		
		BufferedReader bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/financial-news-dataset/mappings/financial-news-dataset_ambiverse.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/financial-news-dataset/mappings/financial-news-dataset_bfy.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/financial-news-dataset/mappings/financial-news-dataset_spotlight.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderSpotLight =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/financial-news-dataset/mappings/financial-news-dataset_tagme.mappings"),StandardCharsets.UTF_8));
		
		String line="";
		
		while ((line = bffReaderAmbiverse.readLine()) != null) {
			String[] elements = line.split("\t");
			if(elements.length >=4){
				String docId = elements[0];
				String mention = elements[1].toLowerCase();
				String offset =  elements[2];
				String entity = elements[3];
				entity = entity.replaceAll("_"," ").toLowerCase();
				ambiverseMap.put(docId+"\t"+mention+"\t"+offset,entity);
			}

		}
//		System.out.println("TOTAL Amb annotations :"+ambiverseMap.keySet().size());


		line="";
		while ((line = bffReaderBabelfy.readLine()) != null) {
			String[] elements = line.split("\t");
			if(elements.length >=4){
				String docId = elements[0];
				String mention = elements[1].toLowerCase();
				String offset =  elements[2];
				String entity = elements[3];
				entity = entity.replaceAll("_"," ").toLowerCase();
				babelMap.put(docId+"\t"+mention+"\t"+offset,entity);
			}
		}
//	
//		System.out.println("TOTAL Bab annotations :"+babelMap.keySet().size());
		
		line="";
		while ((line = bffReaderTagme.readLine()) != null) {
			String[] elements = line.split("\t");
			if(elements.length >=4){
				String docId = elements[0];
				docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
				String mention = elements[1].toLowerCase();
				String offset =  elements[2];
				String entity = elements[3];
				entity = entity.replaceAll("_"," ").toLowerCase();
				tagmeMap.put(docId+"\t"+mention+"\t"+offset,entity);
			}
		}
		
//		System.out.println("TOTAL Tag annotations :"+tagmeMap.keySet().size());
		
		
		
		line="";
		while ((line = bffReaderSpotLight.readLine()) != null) {
			String[] elements = line.split("\t");
			if(elements.length >=4){
				String docId = elements[0];
				docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
				String mention = elements[1].toLowerCase();
				String offset =  elements[2];
				String entity = elements[3];
				entity = entity.replaceAll("_"," ").toLowerCase();
				spotlightMap.put(docId+"\t"+mention+"\t"+offset,entity);
			}
		}
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();
		bffReaderSpotLight.close();
	
	}
	
	public TreeMap<String, String> getAmbiverseMap() {
		return ambiverseMap;
	}
	public TreeMap<String, String> getBabelfyMap() {
		return babelMap;
	}
	public TreeMap<String, String> getTagmeMap() {
		return tagmeMap;
	}
	
	
	public TreeMap<String, String> getAmbiverseMap_train() {
		return ambiverseMap_train;
	}
	
	public TreeMap<String, String> getBabelMap_train() {
		return babelMap_train;
	}

	public TreeMap<String, String> getTagmeMap_train() {
		return tagmeMap_train;
	}

	
	public TreeMap<String, String> getAmbiverseMap_test() {
		return ambiverseMap_test;
	}
	
	public TreeMap<String, String> getBabelMap_test() {
		return babelMap_test;
	}
	
	public TreeMap<String, String> getTagmeMap_test() {
		return tagmeMap_test;
	}
	
}
