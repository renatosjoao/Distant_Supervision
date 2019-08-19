package de.l3s.loaders;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.l3s.extra.ConllDocument;
import de.l3s.extra.GenericDocument;
import de.l3s.extra.WP_annotations_Parser;

public class DataLoaders_WP extends DataLoaders {
	/**
	 *
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException{
//		docsContent = new TreeMap<String, String>();
//		dumpDocumentFrequencyFromWP();  
//		dumpNumRecognizedMentionsFromWP();
//		dumpWordCountFromWP();
// 
//		
		try {
			DataLoaders_WP d = DataLoaders_WP.getInstance();
			System.out.println(d.hashCode());
			d =  DataLoaders_WP.getInstance();
			System.out.println(d.hashCode());
			d =  DataLoaders_WP.getInstance();
			System.out.println(d.hashCode());
			d =  DataLoaders_WP.getInstance();
			System.out.println(d.hashCode());
//			d.dumpTextContent();
		} catch (CompressorException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 *
	 * @return
	 * @throws IOException 
	 * @throws CompressorException 
	 */
	public static DataLoaders_WP getInstance() throws CompressorException, IOException {
		if(instance == null) {
			 synchronized(DataLoaders_WP.class) {
				 instance = new DataLoaders_WP();
			 }
	    }
		return (DataLoaders_WP) instance;
	}
	
	
	
	public DataLoaders_WP() throws CompressorException, IOException{
		DocFrequencyMap = new TObjectIntHashMap<String>();
		DocWordCountMap = new TObjectIntHashMap<String>();
		MentionEntityCountMap = new TObjectIntHashMap<String>();
		NumRecogMentionMap = new TObjectIntHashMap<String>();
		GT_MAP = new TreeMap<String, String>();
		GT_MAP_train = new TreeMap<String, String>();
		GT_MAP_test = new TreeMap<String, String>();
		
		ambiverseMap = new TreeMap<String, String>();
		babelMap = new TreeMap<String, String>();
		tagmeMap = new TreeMap<String, String>();
		spotlightMap = new TreeMap<String, String>();
		
		ambiverseMap_train = new TreeMap<String, String>();
		babelMap_train = new TreeMap<String, String>();
		tagmeMap_train = new TreeMap<String, String>();
		spotlightMap_train = new TreeMap<String, String>();
		
		ambiverseMap_test = new TreeMap<String, String>();
		babelMap_test = new TreeMap<String, String>();
		tagmeMap_test = new TreeMap<String, String>();
		spotlightMap_test = new TreeMap<String, String>();
		
		docsContent = new TreeMap<String, String>();
		
		loadDocFrequencyMap();
		loadWordCount();
//		loadMentionEntityCountMap();
		loadNumMentionsRecognized();
		loadGT();
		loadMappings();
		loadDocsContent();
	}

	

	/***************************************************************************************************
	 # # # LOADERS START FROM HERE # # # 
	/**************************************************************************************************/
	/**
	 *  This function simply loads the plain text content from the corpus into a hashmap 
	 *  
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static void loadDocsContent() throws NumberFormatException, IOException {
		WP_annotations_Parser p = new WP_annotations_Parser();
		@SuppressWarnings("rawtypes")
		LinkedList<ConllDocument> KORE50DataSet = p.parseDataset("/home/joao/datasets/wp/WP.tsv");
		System.out.println("# files in the text dir/ :"+KORE50DataSet.size());
		for (int i = 0; i < KORE50DataSet.size();  i++) { 
			@SuppressWarnings("rawtypes")
			ConllDocument CO = KORE50DataSet.get(i);
			String docId = CO.getTitle().toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String articlecontent = CO.getTxtContent();
			articlecontent = articlecontent.trim();
			docsContent.put(docId,articlecontent);
		}
		System.out.println("# documents in the text dir/ :" + docsContent.keySet().size());
	}
	
	/**
	 *
	 *	This method is used to load the document frequency map.
	 *
	 * @throws CompressorException
	 * @throws IOException
	 */
	public static void loadDocFrequencyMap() throws CompressorException, IOException{
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/document_frequency.tsv"),StandardCharsets.UTF_8));
		String line ="";											
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String mention = elems[0].toLowerCase();
			String freq = elems[1];
			Integer df = Integer.parseInt(freq);			
			DocFrequencyMap.put(mention, df);
		}		
		bffReader.close();
//		System.out.println("...Loaded Document Frequency Map.");
	}

	/**
	 * This function reads a tab separated file (doc_word_count.tsv) with the document id and the number of words.
	 * 
	 * i.e.
	 * 
	 *  -DOCSTART- (1 EU)       433
		-DOCSTART- (2 Rare)     176
		-DOCSTART- (3 China)    219
		-DOCSTART- (4 China)    70


	 * @throws CompressorException 
	 * @throws IOException 
	 */
	public static void loadWordCount() throws CompressorException, IOException {
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/doc_word_count.tsv"),StandardCharsets.UTF_8));
		String line ="";
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String count = elems[1];
			DocWordCountMap.put(docId, Integer.parseInt(count));
		}
		bffReader.close();
		System.out.println("...Loaded Word Count Map Successfully.");
	}
	

	/**
	 *
	 *	This method loads a map of the number of recognized mentions per document
	 *
	 * @return
	 * @throws CompressorException
	 * @throws IOException				
	 */
	public static void loadNumMentionsRecognized() throws CompressorException, IOException {
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/num_recognized_mentions.tsv"),StandardCharsets.UTF_8));
		String line ="";
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String count = elems[1];
			NumRecogMentionMap.put(docId,Integer.parseInt(count));
		}
		bffReader.close();
		System.out.println("...Loaded Number Recognized Mentions Map Successfully.");
	}
	
	
	/**
	 * 			This method loads the GT 
	 * 				Training / Test   and also the complete GT
	 * @throws IOException
	 */
	public static void loadGT() throws IOException{
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/WP_GT.tsv"),StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String mention = elems[1].toLowerCase();
//		    mention = mention.replaceAll("\'", "");
//		    mention = mention.replaceAll("\"", "");
			String offset = elems[2];
			String key = docId+"\t"+mention+"\t"+offset;
			String value = elems[3].replace("_"," ").toLowerCase();
			GT_MAP.put(key, value);
		}
		bffReader.close();
		System.out.println("#GT annotations :" + GT_MAP.keySet().size());
		
		//Training set
		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/WP_GT.train.9.tsv"),StandardCharsets.UTF_8));
		line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String mention = elems[1].toLowerCase();
//		    mention = mention.replaceAll("\'", "");
//		    mention = mention.replaceAll("\"", "");
			String offset = elems[2];
			String key = docId+"\t"+mention+"\t"+offset;
			String value = elems[3].replace("_"," ").toLowerCase();
			GT_MAP_train.put(key, value);
		}
		bffReader.close();
		System.out.println("TRAIN :"+GT_MAP_train.keySet().size());
		//Test set
		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/WP_GT.test.1.tsv"),StandardCharsets.UTF_8));
		line = "";
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
	
	/**
	 *
	 * @throws IOException
	 */
	private static void loadMappings() throws IOException{

		BufferedReader bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/mappings/wp.ambiverse.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/mappings/wp.babelfy.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/mappings/wp.tagme.mappings"),StandardCharsets.UTF_8));
//		BufferedReader bffReaderSpotlight =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/mappings/wp_spotlight.mappings"),StandardCharsets.UTF_8));

		String line="";
		while ((line = bffReaderAmbiverse.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length >=4){
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
				String offset =  elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_"," ").toLowerCase();
				ambiverseMap.put(docId+"\t"+mention+"\t"+offset,entity);
			}

		}
		System.out.println("TOTAL Amb annotations :"+ambiverseMap.keySet().size());


		line="";
		while ((line = bffReaderBabelfy.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length >=4){
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
				String offset =  elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_"," ").toLowerCase();
				babelMap.put(docId+"\t"+mention+"\t"+offset,entity);
			}
		}
	
		System.out.println("TOTAL Bab annotations :"+babelMap.keySet().size());
		
		line="";
		while ((line = bffReaderTagme.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length >=4){
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
				docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
				String offset =  elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_"," ").toLowerCase();
				tagmeMap.put(docId+"\t"+mention+"\t"+offset,entity);
			}
		}
		System.out.println("TOTAL Tag annotations :"+tagmeMap.keySet().size());

//		line="";
//		while ((line = bffReaderSpotlight.readLine()) != null) {
//			String[] elements = line.split("\t");
//			if(elements.length >=4){
//				String docId = elements[0];
//				docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
//				String mention = elements[1].toLowerCase();
//				mention = mention.replaceAll("_"," ");
//				mention = mention.replaceAll("\"","");
//				String offset =  elements[2];
//				String entity = elements[3];
//				entity = entity.replaceAll("_"," ").toLowerCase();
//				spotlightMap.put(docId+"\t"+mention+"\t"+offset,entity);
//			}
//		}
//		System.out.println("TOTAL Spotlight annotations :"+spotlightMap.keySet().size());
		
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();
//		bffReaderSpotlight.close();
		
		
//		//////// TRAIN
		bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/mappings/wp_ambiverse.9.mappings"),StandardCharsets.UTF_8));
		bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/mappings/wp_bfy.9.mappings"),StandardCharsets.UTF_8));
		bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/mappings/wp_tagme.9.mappings"),StandardCharsets.UTF_8));
//	
		line="";
//		
		while ((line = bffReaderAmbiverse.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length >=4){
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
//			    mention = mention.replaceAll("\'", "");
//			    mention = mention.replaceAll("\"", "");
				String offset =  elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_"," ").toLowerCase();
				ambiverseMap_train.put(docId+"\t"+mention+"\t"+offset,entity);
			}

		}
		System.out.println("Amb TRAIN annotations :"+ambiverseMap_train.keySet().size());
//
//
		line="";
		while ((line = bffReaderBabelfy.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length >=4){
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
//			    mention = mention.replaceAll("\'", "");
//			    mention = mention.replaceAll("\"", "");
				String offset =  elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_"," ").toLowerCase();
				babelMap_train.put(docId+"\t"+mention+"\t"+offset,entity);
			}
		}
	
		System.out.println("Bab TRAIN annotations :"+babelMap_train.keySet().size());
		
		line="";
		while ((line = bffReaderTagme.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length >=4){
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
//			    mention = mention.replaceAll("\'", "");
//			    mention = mention.replaceAll("\"", "");
				docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
				String offset =  elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_"," ").toLowerCase();
				tagmeMap_train.put(docId+"\t"+mention+"\t"+offset,entity);
			}
		}
		System.out.println("Tag TRAIN annotations :"+tagmeMap_train.keySet().size());
		
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();
		
		
		/////// TEST 
		
		bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/mappings/wp_ambiverse.1.mappings"),StandardCharsets.UTF_8));
		bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/mappings/wp_bfy.1.mappings"),StandardCharsets.UTF_8));
		bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/mappings/wp_tagme.1.mappings"),StandardCharsets.UTF_8));
//		bffReaderSpotlight =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/mappings/wp_spotlight.1.mappings"),StandardCharsets.UTF_8));
	
		line="";
		
		while ((line = bffReaderAmbiverse.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length >=4){
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
//			    mention = mention.replaceAll("\'", "");
//			    mention = mention.replaceAll("\"", "");
				String offset =  elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_"," ").toLowerCase();
				ambiverseMap_test.put(docId+"\t"+mention+"\t"+offset,entity);
			}
		}
		System.out.println("Amb TEST annotations :"+ambiverseMap_test.keySet().size());

		line="";
		while ((line = bffReaderBabelfy.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length >=4){
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
//			    mention = mention.replaceAll("\'", "");
//			    mention = mention.replaceAll("\"", "");
				String offset =  elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_"," ").toLowerCase();
				babelMap_test.put(docId+"\t"+mention+"\t"+offset,entity);
			}
		}
	
		System.out.println("Bab TEST annotations :"+babelMap_test.keySet().size());
		
		line="";
		while ((line = bffReaderTagme.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length >=4){
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
//			    mention = mention.replaceAll("\'", "");
//			    mention = mention.replaceAll("\"", "");
				docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
				String offset =  elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_"," ").toLowerCase();
				tagmeMap_test.put(docId+"\t"+mention+"\t"+offset,entity);
			}
		}
		System.out.println("Tag TEST annotations :"+tagmeMap_test.keySet().size());
//		line="";
//		while ((line = bffReaderSpotlight.readLine()) != null) {
//			String[] elements = line.split("\t");
//			if(elements.length >=4){
//				String docId = elements[0];
//				docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
//				String mention = elements[1].toLowerCase();
//				mention = mention.replaceAll("_"," ");
//				mention = mention.replaceAll("\"","");
//				String offset =  elements[2];
//				String entity = elements[3];
//				entity = entity.replaceAll("_"," ").toLowerCase();
//				spotlightMap_test.put(docId+"\t"+mention+"\t"+offset,entity);
//			}
//		}
//		System.out.println("Spotlight TEST annotations :"+spotlightMap_test.keySet().size());

		
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();
//		bffReaderSpotlight.close();
		
	}
	
	
	/***************************************************************************************************
	 # # # DUMPERS START FROM HERE # # # 
	/**************************************************************************************************/
	
	
	/**
	 * 	This utility function is meant to fetch the number of words per document in the wp corpus.
	 * 
	 * 	It produces the file:
	 * 						/home/joao/datasets/wp/doc_word_count.tsv
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void dumpWordCountFromWP() throws NumberFormatException, IOException {
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/wp/doc_word_count.tsv"),StandardCharsets.UTF_8);
		WP_annotations_Parser p = new WP_annotations_Parser();
		
		LinkedList<ConllDocument> KORE50DataSet = p.parseDataset("/home/joao/datasets/wp/WP.tsv");
		
		HashMap<String,String> entitiesMap = new HashMap<String, String>();
		for (int i = 0; i < KORE50DataSet.size();  i++) { 
			@SuppressWarnings("rawtypes")
			ConllDocument CO = KORE50DataSet.get(i);
		
			String docId = CO.getTitle().trim().toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String articlecontent = CO.getTxtContent();
		
			String trim = articlecontent.trim();
			int wc = trim.split("\\s+").length;
			pAnn.write(docId+"\t"+wc+"\n");
		}
		pAnn.flush();
		pAnn.close();
		System.out.println("...Finished dumping the Word Count Successfully.");
		
	}
	/**
	 * This utility function is meant to dump the document frequency for all mentions in the files : 
	 * 
	 * wp_dataset.easy
	 * wp_dataset.medium
	 * wp_dataset.hard
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void dumpDocumentFrequencyFromWP() throws IOException{
		HashSet<String> mentionsSET = new HashSet<String>();
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/wp/document_frequency.tsv"),StandardCharsets.UTF_8);
		BufferedReader bff = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/WP_GT.tsv"),StandardCharsets.UTF_8));
		String line="";
		while((line = bff.readLine()) != null){
			String[] elems = line.split("\t");
			String mention = elems[1].trim().toLowerCase();
			mentionsSET.add(mention);
		}
		bff.close();
		
		WP_annotations_Parser p = new WP_annotations_Parser();
		LinkedList<ConllDocument> KORE50DataSet = p.parseDataset("/home/joao/datasets/wp/WP.tsv");
//		System.out.println(KORE50DataSet.size());
		for (String mention : mentionsSET){
			int df = 0;
			for(ConllDocument CO : KORE50DataSet){
				String articlecontent = CO.getTxtContent();
				String fullTXT = articlecontent.trim().toLowerCase();
				if(fullTXT.contains(mention)){
					df+=1;
				}	
			}
			pAnn.write(mention+"\t"+df+"\n");
		}
    	pAnn.flush();
    	pAnn.close();
    	System.out.println("...Finished dumping the Document Frequency Count Successfully.");
	}
	/**
	 *	This utility function is meant to fetch the number of recognized mentions per document in the wp corpus.
	 *	
	 *	It produces the file:
	 *
	 *			/home/joao/datasets/wp/num_recognized_mentions.tsv
	 *
	 * @throws NumberFormatException 
	 * @throws IOException
	 * @throws CompressorException
	 */
	public static void dumpNumRecognizedMentionsFromWP() throws NumberFormatException, IOException{
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/wp/num_recognized_mentions.tsv"),StandardCharsets.UTF_8);
		BufferedReader bff = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/wp/WP_GT.tsv"),StandardCharsets.UTF_8));
		TreeMap<String, Integer> hashTreeMap = new TreeMap<String, Integer>();
		String line="";
		while((line = bff.readLine()) != null){
			String[] elems = line.split("\t");
			String docId = elems[0].trim().toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			Integer count = hashTreeMap.get(docId);
			if(count == null){
				count = 1;
				hashTreeMap.put(docId, count);
			}else{
				count+=1;
				hashTreeMap.put(docId, count);
			}
		}
		bff.close();
		
		@SuppressWarnings("rawtypes")
		Iterator it = hashTreeMap.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
			String docId = ((String) pair.getKey()).toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			pAnn.write(docId + "\t"+pair.getValue()+"\n");
		    it.remove(); // avoids a ConcurrentModificationException
		}
		pAnn.flush();
		pAnn.close();
		System.out.println("...Finished dumping the number of recognized mentions per document.");
	}
	
	
	/**
	 * Utility function to dump the text content 
	 * 
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static void dumpTextContent() throws NumberFormatException, IOException {
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/wp/corpus.tsv"), StandardCharsets.UTF_8);
		WP_annotations_Parser p = new WP_annotations_Parser();
		@SuppressWarnings("rawtypes")
		LinkedList<ConllDocument> KORE50DataSet = p.parseDataset("/home/joao/datasets/wp/WP.tsv");
		for (int i = 0; i < KORE50DataSet.size();  i++) { 
			@SuppressWarnings("rawtypes")
			ConllDocument CO = KORE50DataSet.get(i);
			String articletitle = CO.getTitle();
			String articlecontent = CO.getTxtContent();
			articlecontent = articlecontent.trim();
	  		articlecontent = articlecontent.replaceAll("[^\\n\\r\\t\\p{Print}]", "");
			articletitle = articletitle.toLowerCase();
	        articlecontent = articlecontent.toLowerCase();
	    	out.write(articletitle+"\t"+articlecontent+"\n");
			
		}
		out.flush();
		out.close();
	} 
	

	public TObjectIntHashMap<String> getDocFrequencyMap() {
		return DocFrequencyMap;
	}
	public static void setDocFrequencyMap(TObjectIntHashMap<String> docFrequencyMap) {
		DocFrequencyMap = docFrequencyMap;
	}
	public TObjectIntHashMap<String> getDocWordCountMap() {
		return DocWordCountMap;
	}
	public static void setDocWordCountMap(TObjectIntHashMap<String> docWordCountMap) {
		DocWordCountMap = docWordCountMap;
	}
	public static TObjectIntHashMap<String> getMentionEntityCountMap() {
		return MentionEntityCountMap;
	}
	public static void setMentionEntityCountMap(TObjectIntHashMap<String> mentionEntityCountMap) {
		MentionEntityCountMap = mentionEntityCountMap;
	}
	public TObjectIntHashMap<String> getNumRecogMentionMap() {
		return NumRecogMentionMap;
	}
	public static void setNumRecogMentionMap(TObjectIntHashMap<String> numRecogMentionMap) {
		NumRecogMentionMap = numRecogMentionMap;
	}
	public TreeMap<String, String> getGT_MAP_train() {
		return GT_MAP_train;
	}
	public static void setGT_MAP_train(TreeMap<String, String> gT_MAP_train) {
		GT_MAP_train = gT_MAP_train;
	}
	public TreeMap<String, String> getGT_MAP_test() {
		return GT_MAP_test;
	}
	public static void setGT_MAP_test(TreeMap<String, String> gT_MAP_test) {
		GT_MAP_test = gT_MAP_test;
	}
	public TreeMap<String, String> getGT_MAP() {
		return GT_MAP;
	}
	public static void setGT_MAP(TreeMap<String, String> gT_MAP) {
		GT_MAP = gT_MAP;
	}
	public TreeMap<String, String> getAmbiverseMap() {
		return ambiverseMap;
	}
	public static void setAmbiverseMap(TreeMap<String, String> ambiverseMap) {
		DataLoaders_WP.ambiverseMap = ambiverseMap;
	}
	public TreeMap<String, String> getBabelMap() {
		return babelMap;
	}
	public static void setBabelMap(TreeMap<String, String> babelMap) {
		DataLoaders_WP.babelMap = babelMap;
	}
	public TreeMap<String, String> getTagmeMap() {
		return tagmeMap;
	}
	
	public static void setTagmeMap(TreeMap<String, String> tagmeMap) {
		DataLoaders_WP.tagmeMap = tagmeMap;
	}
	public static void setAmbiverseMap_train(TreeMap<String, String> ambiverseMap_train) {
		DataLoaders_WP.ambiverseMap_train = ambiverseMap_train;
	}
	public static void setBabelMONLLap_train(TreeMap<String, String> babelMap_train) {
		DataLoaders_WP.babelMap_train = babelMap_train;
	}
	public static void setTagmeMap_train(TreeMap<String, String> tagmeMap_train) {
		DataLoaders_WP.tagmeMap_train = tagmeMap_train;
	}
	
	public static Set<String> getNEs() {
		return NEs;
	}

	public static void setNEs(Set<String> nEs) {
		NEs = nEs;
	}

	
	public TreeMap<String,String> getDocsContent(){
		return docsContent;
	}
	public TreeMap<String, String> getAmbiverseMap_train() {
		return ambiverseMap_train;
	}
	public TreeMap<String, String> getAmbiverseMap_test() {
		return ambiverseMap_test;
	}
	public static void setAmbiverseMap_test(TreeMap<String, String> ambiverseMap_test) {
		DataLoaders_WP.ambiverseMap_test = ambiverseMap_test;
	}
	public TreeMap<String, String> getBabelMap_train() {
		return babelMap_train;
	}
	public TreeMap<String, String> getBabelMap_test() {
		return babelMap_test;
	}
	public static void setBabelMap_test(TreeMap<String, String> babelMap_test) {
		DataLoaders_WP.babelMap_test = babelMap_test;
	}
	public TreeMap<String, String> getTagmeMap_train() {
		return tagmeMap_train;
	}
	public TreeMap<String, String> getTagmeMap_test() {
		return tagmeMap_test;
	}
	public static void setTagmeMap_test(TreeMap<String, String> tagmeMap_test) {
		DataLoaders_WP.tagmeMap_test = tagmeMap_test;
	}
	
	public static TreeMap<String, String> getSpotlightMap() {
		return spotlightMap;
	}
	public  TreeMap<String, String> getSpotlightMap_train() {
		return spotlightMap_train;
	}
	
	public  TreeMap<String, String> getSpotlightMap_test() {
		return spotlightMap_test;
	}
	
	public static void setSpotlightMap(TreeMap<String, String> spotlightMap) {
		DataLoaders_WP.spotlightMap = spotlightMap;
	}
	public static void setSpotlightMap_train(TreeMap<String, String> spotlightMap_train) {
		DataLoaders_WP.spotlightMap_train = spotlightMap_train;
	}
	public static void setSpotlightMap_test(TreeMap<String, String> spotlightMap_test) {
		DataLoaders_WP.spotlightMap_test = spotlightMap_test;
	}
	
	
	
}
