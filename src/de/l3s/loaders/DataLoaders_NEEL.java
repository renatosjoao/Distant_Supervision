package de.l3s.loaders;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class DataLoaders_NEEL  extends DataLoaders {
	
	private static TreeSet<String> validDocs = null;

	/**
	 *
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException{
//		docsContent = new TreeMap<String, String>();
//		dumpNumRecognizedMentionsFromNEEL();
//		dumpWordCountFromNEEL();
//		dumpDocumentFrequencyFromNEEL();
//		
		try {
			DataLoaders_NEEL d = DataLoaders_NEEL.getInstance();
			System.out.println(d.hashCode());
			d =  DataLoaders_NEEL.getInstance();
			System.out.println(d.hashCode());
			d =  DataLoaders_NEEL.getInstance();
			System.out.println(d.hashCode());
			d =  DataLoaders_NEEL.getInstance();
			System.out.println(d.hashCode());
//			dumpTextContent() ;
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
	public static DataLoaders_NEEL getInstance() throws CompressorException, IOException {
		if(instance == null) {
			 synchronized(DataLoaders_NEEL.class) {
				 instance = new DataLoaders_NEEL();
			 }
	    }
		return  (DataLoaders_NEEL) instance;
	}
	
	
	public DataLoaders_NEEL() throws CompressorException, IOException{
		
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
		
		validDocs = new TreeSet<String>();
		
		docsContent = new TreeMap<String, String>();

		loadDocFrequencyMap();
		loadWordCount();
		loadMentionEntityCountMap();
		loadNumMentionsRecognized();
		loadGT();
		loadMappings();
		loadDocsContent();
	}

	
	
	
	
	
	
	
	/***************************************************************************************************
	 # # # LOADERS START FROM HERE # # # 
	/**************************************************************************************************/
	
	/**
	 * 					This method loads the documents contents into a map 
	 *
	 * @throws IOException 
	 * @throws NumberFormatException 
	 * 
	 **/
	public void loadDocsContent() throws IOException{

//		File twitterFileTrain	  = new File("/home/joao/datasets/neel/TrainingSet/NEEL2016-training_neel.gs");
		File annotationsFileTrain  = new File("/home/joao/datasets/neel/TrainingSet/NEEL2016-training.tsv");
		
//		File twitterFileTest = new File("/home/joao/datasets/neel/TestSet/NEEL2016-test_neel.gs");
		File annotationsFileTest = new File("/home/joao/datasets/neel/TestSet/NEEL2016-test.tsv");
		
//		File twitterFileDev = new File("/home/joao/datasets/neel/DevSet/NEEL2016-dev.gs");
		File annotationsFileDev = new File("/home/joao/datasets/neel/DevSet/NEEL2016-dev.tsv");
		
		//BufferedReader bffTrain = new BufferedReader(new InputStreamReader(new FileInputStream(twitterFileTrain),StandardCharsets.UTF_8));
		BufferedReader bffAnTrain = new BufferedReader(new InputStreamReader(new FileInputStream(annotationsFileTrain),StandardCharsets.UTF_8));
		
		//BufferedReader bffDev = new BufferedReader(new InputStreamReader(new FileInputStream(twitterFileDev),StandardCharsets.UTF_8));
		BufferedReader bffAnDev = new BufferedReader(new InputStreamReader(new FileInputStream(annotationsFileDev),StandardCharsets.UTF_8));
				
		//BufferedReader bffTest = new BufferedReader(new InputStreamReader(new FileInputStream(twitterFileTest),StandardCharsets.UTF_8));
		BufferedReader bffAnTest = new BufferedReader(new InputStreamReader(new FileInputStream(annotationsFileTest),StandardCharsets.UTF_8));
		
		String line = "";
		while((line = bffAnTrain.readLine()) != null){
			String[] elems = line.split("\t");
			String docId = elems[0].trim().toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String fullTXT = elems[1].trim();
			docsContent.put(docId,fullTXT);
		}
		bffAnTrain.close();
		line = "";
		while((line = bffAnDev.readLine()) != null){
			String[] elems = line.split("\t");
			String docId = elems[0].trim().toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String fullTXT = elems[1].trim();
			docsContent.put(docId,fullTXT);		}
		line="";
		bffAnDev.close();
		while((line = bffAnTest.readLine()) != null){
			String[] elems = line.split("\t");
			String docId = elems[0].trim().toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String fullTXT = elems[1].trim();
			docsContent.put(docId,fullTXT);
		}
		bffAnTest.close();
		
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
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/document_frequency.tsv"),StandardCharsets.UTF_8));
		String line ="";											
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String mention = elems[0].toLowerCase();
			String freq = elems[1];
			Integer df = Integer.parseInt(freq);			
			DocFrequencyMap.put(mention, df);
		}		
		bffReader.close();
		System.out.println("...Loaded Document Frequency Map.");
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
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/doc_word_count.tsv"),StandardCharsets.UTF_8));
		String line ="";
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0];
			String count = elems[1];
			DocWordCountMap.put(docId, Integer.parseInt(count));
		}
		bffReader.close();
//		System.out.println("...Loaded Word Count Map Successfully.");
	}
	/**
	 *
	 * 	@param fileIn
	 *  @return
	 *  @throws FileNotFoundException
	 * 	@throws CompressorException
	*/
	public static BufferedReader getBufferedReaderForCompressedFile(String fileIn) throws FileNotFoundException, CompressorException {
		FileInputStream fin = new FileInputStream(fileIn);
		BufferedInputStream bis = new BufferedInputStream(fin);
		CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(input,StandardCharsets.UTF_8));
		return br2;
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
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/num_recognized_mentions.tsv"),StandardCharsets.UTF_8));
		String line ="";
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0];
			String count = elems[1];
			NumRecogMentionMap.put(docId,Integer.parseInt(count));
		}
		bffReader.close();
//		System.out.println("...Loaded Number Recognized Mentions Map Successfully.");
	}
	
	
	/**
	 *     This method loads a map of the ground truth with the gold standard annotations
	 * @return
	 * @throws IOException
	 */
	public static void loadGT() throws IOException{
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/NEEL2016-all_NONNIL.gt"),StandardCharsets.UTF_8));
		String line = "";
		while((line = bffReader.readLine() )!= null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String mention = elems[1].toLowerCase();
			String offset = elems[2];
			String value = elems[3].replace("_"," ").toLowerCase();
			value = value.replaceAll("http://dbpedia.org/resource/", "");
			value = value.replaceAll("_"," ").toLowerCase();
			String key = docId + "\t" + mention + "\t" + offset;
			GT_MAP.put(key, value);
			validDocs.add(docId);
		}
		bffReader.close();
		System.out.println("#GT annotations :" + GT_MAP.keySet().size());
		//Training
		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/NEEL2016-train_NONNIL.gt"),StandardCharsets.UTF_8));
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
			value = value.replaceAll("http://dbpedia.org/resource/", "");
			value = value.replaceAll("_"," ").toLowerCase();
			GT_MAP_train.put(key, value);
		}
		bffReader.close();
//		System.out.println("TRAIN :"+GT_MAP_train.keySet().size());

		//Test
		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/NEEL2016-test_NONNIL.gt"),StandardCharsets.UTF_8));
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
			String value = elems[3].toLowerCase();
			value = value.replaceAll("http://dbpedia.org/resource/", "");
			value = value.replaceAll("_"," ").toLowerCase();
			String key = docId + "\t" + mention + "\t" + offset;
			GT_MAP_test.put(key, value);
		}
		bffReader.close();
		System.out.println("TEST GT:"+GT_MAP_test.keySet().size());

	}
	
	/**
	 * 
	 *  This method loads the annotations created by the  NEL tools
	 *
	 * @throws IOException
	 */
	private static void loadMappings() throws IOException{

		BufferedReader bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/mappings/neel.ambiverse.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/mappings/neel.babelfy.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/mappings/neel.tagme.mappings"),StandardCharsets.UTF_8));
//		BufferedReader bffReaderSpotlight =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/mappings/NEEL2016_spotlight.mappings"),StandardCharsets.UTF_8));
	
		String line="";
		
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
				if(validDocs.contains(docId)){
					ambiverseMap.put(docId+"\t"+mention+"\t"+offset,entity);
				}
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
//			    mention = mention.replaceAll("\'", "");
//			    mention = mention.replaceAll("\"", "");
				String offset =  elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_"," ").toLowerCase();
				if(validDocs.contains(docId)){
					babelMap.put(docId+"\t"+mention+"\t"+offset,entity);
				}
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
//			    mention = mention.replaceAll("\'", "");
//			    mention = mention.replaceAll("\"", "");
				docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
				String offset =  elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_"," ").toLowerCase();
				if(validDocs.contains(docId)){
					tagmeMap.put(docId+"\t"+mention+"\t"+offset,entity);
				}
			}
		}
		
		System.out.println("TOTAL Tag annotations :"+tagmeMap.keySet().size());
//
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
//				if(validDocs.contains(docId)){
//					spotlightMap.put(docId+"\t"+mention+"\t"+offset,entity);
//				}
//			}
//		}
//		System.out.println("TOTAL Spotlight annotations :"+spotlightMap.keySet().size());
		
		
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();
//		bffReaderSpotlight.close();

	
		//////// TRAIN
		bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/mappings/NEEL2016-training_ambiverse.mappings"),StandardCharsets.UTF_8));
		bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/mappings/NEEL2016-training_bfy.mappings"),StandardCharsets.UTF_8));
		bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/mappings/NEEL2016-training_tagme.mappings"),StandardCharsets.UTF_8));
	
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
				if(validDocs.contains(docId)){
					ambiverseMap_train.put(docId+"\t"+mention+"\t"+offset,entity);
				}
			}
		}
		System.out.println("Amb TRAIN annotations :"+ambiverseMap_train.keySet().size());

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
				if(validDocs.contains(docId)){
					babelMap_train.put(docId+"\t"+mention+"\t"+offset,entity);
				}
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
				if(validDocs.contains(docId)){
					tagmeMap_train.put(docId+"\t"+mention+"\t"+offset,entity);
				}
			}
		}
		System.out.println("Tag TRAIN annotations :"+tagmeMap_train.keySet().size());

		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();
		
		/////// TEST 
		bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/mappings/NEEL2016-test_ambiverse.mappings"),StandardCharsets.UTF_8));
		bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/mappings/NEEL2016-test_bfy.mappings"),StandardCharsets.UTF_8));
		bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/mappings/NEEL2016-test_tagme.mappings"),StandardCharsets.UTF_8));
//		bffReaderSpotlight =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/mappings/NEEL2016-test_spotlight.mappings"),StandardCharsets.UTF_8));

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
				if(validDocs.contains(docId)){
					ambiverseMap_test.put(docId+"\t"+mention+"\t"+offset,entity);
				}
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
				if(validDocs.contains(docId)){
					babelMap_test.put(docId+"\t"+mention+"\t"+offset,entity);
				}
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
				if(validDocs.contains(docId)){
					tagmeMap_test.put(docId+"\t"+mention+"\t"+offset,entity);
				}
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
//				if(validDocs.contains(docId)){
//					spotlightMap_test.put(docId+"\t"+mention+"\t"+offset,entity);
//				}
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
	 * 	This utility function is meant to fetch the number of words per document in the Microposts2016 corpus.
	 * 
	 * 	It produces the file:
	 * 						/home/joao/microposts/doc_word_count.tsv
	 * 
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static void dumpWordCountFromNEEL() throws NumberFormatException, IOException{
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/neel/doc_word_count.tsv"),StandardCharsets.UTF_8);
		
		//File twitterFileTrain	  = new File("/home/joao/datasets/neel/TrainingSet/NEEL2016-training_neel.gs");
		File annotationsFileTrain  = new File("/home/joao/datasets/neel/TrainingSet/NEEL2016-training.tsv");
		
		//File twitterFileTest = new File("/home/joao/datasets/neel/TestSet/NEEL2016-test_neel.gs");
		File annotationsFileTest = new File("/home/joao/datasets/neel/TestSet/NEEL2016-test.tsv");
		
		//File twitterFileDev = new File("/home/joao/datasets/neel/DevSet/NEEL2016-dev_neel.gs");
		File annotationsFileDev = new File("/home/joao/datasets/neel/DevSet/NEEL2016-dev.tsv");
		
		//BufferedReader bffTrain = new BufferedReader(new InputStreamReader(new FileInputStream(twitterFileTrain),StandardCharsets.UTF_8));
		BufferedReader bffAnTrain = new BufferedReader(new InputStreamReader(new FileInputStream(annotationsFileTrain),StandardCharsets.UTF_8));
		
		//BufferedReader bffDev = new BufferedReader(new InputStreamReader(new FileInputStream(twitterFileDev),StandardCharsets.UTF_8));
		BufferedReader bffAnDev = new BufferedReader(new InputStreamReader(new FileInputStream(annotationsFileDev),StandardCharsets.UTF_8));
				
		//BufferedReader bffTest = new BufferedReader(new InputStreamReader(new FileInputStream(twitterFileTest),StandardCharsets.UTF_8));
		BufferedReader bffAnTest = new BufferedReader(new InputStreamReader(new FileInputStream(annotationsFileTest),StandardCharsets.UTF_8));
		
		String line = "";
		while((line = bffAnTrain.readLine()) != null){
			String[] elems = line.split("\t");
			String docId = elems[0].trim().toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String fullTXT = elems[1].trim();
			int wc = fullTXT.split("\\s+").length;			
	    	pAnn.write(docId+"\t"+wc+"\n");
		}
		pAnn.flush();
		line = "";
		while((line = bffAnDev.readLine()) != null){
			String[] elems = line.split("\t");
			String docId = elems[0].trim().toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String fullTXT = elems[1].trim();
			int wc = fullTXT.split("\\s+").length;			
	    	pAnn.write(docId+"\t"+wc+"\n");
		}
		pAnn.flush();
		line="";
		while((line = bffAnTest.readLine()) != null){
			String[] elems = line.split("\t");
			String docId = elems[0].trim().toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String fullTXT = elems[1].trim();
			int wc = fullTXT.split("\\s+").length;			
	    	pAnn.write(docId+"\t"+wc+"\n");
		}
		pAnn.flush();
		pAnn.close();
		System.out.println("...Finished dumping the Word Count Successfully.");
		bffAnTrain.close();
		bffAnDev.close();
		bffAnTest.close();
		
	}
	
	/**
	 * This utility function is meant to dump the document frequency for all mentions in the files : 
	 * 
	 * 
	 * @throws IOException
	 */
	public static void dumpDocumentFrequencyFromNEEL() throws IOException{
		HashSet<String> mentionsSET = new HashSet<String>();
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/neel/document_frequency.tsv"),StandardCharsets.UTF_8);
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/NEEL2016-all_NONNIL.gt"),StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String mention = elems[1].trim().toLowerCase();
			mentionsSET.add(mention);
		}
		bffReader.close();
//		
		for (String mention : mentionsSET){
			int df = 0;
			File annotationsFileTrain  = new File("/home/joao/datasets/neel/TrainingSet/NEEL2016-training.tsv");
			BufferedReader bffAnTrain = new BufferedReader(new InputStreamReader(new FileInputStream(annotationsFileTrain),StandardCharsets.UTF_8));
			line = "";
			while((line = bffAnTrain.readLine()) != null){
				String[] elems = line.split("\t");
				String fullTXT = elems[1].trim();
				fullTXT = fullTXT.trim().toLowerCase();
				if(fullTXT.contains(mention)){
					df+=1;
				}
				
			}
			bffAnTrain.close();
			pAnn.write(mention+"\t"+df+"\n");

		}
    	pAnn.flush();
    	pAnn.close();
    	System.out.println("...Finished dumping the Document Frequency Count Successfully.");
	}
	
	
	/**
	 *	This utility function is meant to fetch the number of recognized mentions per document in the neel corpus.
	 *	
	 *	It produces the file:
	 *
	 *			/home/joao/datasets/neel/num_recognized_mentions.tsv
	 *
	 * @throws NumberFormatException 
	 * @throws IOException
	 * @throws CompressorException
	 */
	public static void dumpNumRecognizedMentionsFromNEEL() throws NumberFormatException, IOException{
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/neel/num_recognized_mentions.tsv"),StandardCharsets.UTF_8);
		BufferedReader bff = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/neel/NEEL2016-all.gt"),StandardCharsets.UTF_8));
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
	public static void dumpTextContent() throws IOException{
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/neel/corpus.tsv"), StandardCharsets.UTF_8);
		
		TreeMap<String, String> hashTreeMap = new TreeMap<String, String>();

		File twitterFileTrain	  = new File("/home/joao/datasets/neel/TrainingSet/NEEL2016-training_neel.gs");
		File annotationsFileTrain  = new File("/home/joao/datasets/neel/TrainingSet/NEEL2016-training.tsv");
		
		File twitterFileTest = new File("/home/joao/datasets/neel/TestSet/NEEL2016-test_neel.gs");
		File annotationsFileTest = new File("/home/joao/datasets/neel/TestSet/NEEL2016-test.tsv");
		
		File twitterFileDev = new File("/home/joao/datasets/neel/DevSet/NEEL2016-dev.gs");
		File annotationsFileDev = new File("/home/joao/datasets/neel/DevSet/NEEL2016-dev.tsv");
		
		//BufferedReader bffTrain = new BufferedReader(new InputStreamReader(new FileInputStream(twitterFileTrain),StandardCharsets.UTF_8));
		BufferedReader bffAnTrain = new BufferedReader(new InputStreamReader(new FileInputStream(annotationsFileTrain),StandardCharsets.UTF_8));
		
		//BufferedReader bffDev = new BufferedReader(new InputStreamReader(new FileInputStream(twitterFileDev),StandardCharsets.UTF_8));
		BufferedReader bffAnDev = new BufferedReader(new InputStreamReader(new FileInputStream(annotationsFileDev),StandardCharsets.UTF_8));
				
		//BufferedReader bffTest = new BufferedReader(new InputStreamReader(new FileInputStream(twitterFileTest),StandardCharsets.UTF_8));
		BufferedReader bffAnTest = new BufferedReader(new InputStreamReader(new FileInputStream(annotationsFileTest),StandardCharsets.UTF_8));
		
		String line = "";
		while((line = bffAnTrain.readLine()) != null){
			String[] elems = line.split("\t");
			String articletitle = elems[0].trim();
			String articlecontent = elems[1].trim();
			articlecontent = articlecontent.trim();
	  		articlecontent = articlecontent.replaceAll("[^\\n\\r\\t\\p{Print}]", "");
			articletitle = articletitle.toLowerCase();
	        articlecontent = articlecontent.toLowerCase();
	    	out.write(articletitle+"\t"+articlecontent+"\n");
		}
		bffAnTrain.close();
		
		line = "";
		while((line = bffAnDev.readLine()) != null){
			String[] elems = line.split("\t");
			String articletitle = elems[0].trim();
			String articlecontent = elems[1].trim();
			articlecontent = articlecontent.trim();
	  		articlecontent = articlecontent.replaceAll("[^\\n\\r\\t\\p{Print}]", "");
			articletitle = articletitle.toLowerCase();
	        articlecontent = articlecontent.toLowerCase();
	    	out.write(articletitle+"\t"+articlecontent+"\n");
			
		}
		
		bffAnDev.close();
		
		line="";
		while((line = bffAnTest.readLine()) != null){
			String[] elems = line.split("\t");
			String articletitle = elems[0].trim();
			String articlecontent = elems[1].trim();
			articlecontent = articlecontent.trim();
	  		articlecontent = articlecontent.replaceAll("[^\\n\\r\\t\\p{Print}]", "");
			articletitle = articletitle.toLowerCase();
	        articlecontent = articlecontent.toLowerCase();
	    	out.write(articletitle+"\t"+articlecontent+"\n");
		}
		bffAnTest.close();
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

	public static void setMentionEntityCountMap(
			TObjectIntHashMap<String> mentionEntityCountMap) {
		MentionEntityCountMap = mentionEntityCountMap;
	}

	public TObjectIntHashMap<String> getNumRecogMentionMap() {
		return NumRecogMentionMap;
	}

	public static void setNumRecogMentionMap(
			TObjectIntHashMap<String> numRecogMentionMap) {
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
	public static void setAmbiverseMap(TreeMap<String, String> ambiverseMap) {
		DataLoaders_NEEL.ambiverseMap = ambiverseMap;
	}
	public static void setBabelMap(TreeMap<String, String> babelMap) {
		DataLoaders_NEEL.babelMap = babelMap;
	}
	public static void setTagmeMap(TreeMap<String, String> tagmeMap) {
		DataLoaders_NEEL.tagmeMap = tagmeMap;
	}
	public static void setAmbiverseMap_train(
			TreeMap<String, String> ambiverseMap_train) {
		DataLoaders_NEEL.ambiverseMap_train = ambiverseMap_train;
	}
	public static void setBabelMap_train(TreeMap<String, String> babelMap_train) {
		DataLoaders_NEEL.babelMap_train = babelMap_train;
	}
	public static void setTagmeMap_train(TreeMap<String, String> tagmeMap_train) {
		DataLoaders_NEEL.tagmeMap_train = tagmeMap_train;
	}
	public static void setAmbiverseMap_test(
			TreeMap<String, String> ambiverseMap_test) {
		DataLoaders_NEEL.ambiverseMap_test = ambiverseMap_test;
	}
	public static void setBabelMap_test(TreeMap<String, String> babelMap_test) {
		DataLoaders_NEEL.babelMap_test = babelMap_test;
	}
	public static void setTagmeMap_test(TreeMap<String, String> tagmeMap_test) {
		DataLoaders_NEEL.tagmeMap_test = tagmeMap_test;
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
	public static TreeMap<String, String> getSpotlightMap() {
		return spotlightMap;
	}
	public static void setSpotlightMap(TreeMap<String, String> spotlightMap) {
		DataLoaders_NEEL.spotlightMap = spotlightMap;
	}
	public TreeMap<String, String> getSpotlightMap_train() {
		return spotlightMap_train;
	}
	public static void setSpotlightMap_train(TreeMap<String, String> spotlightMap_train) {
		DataLoaders_NEEL.spotlightMap_train = spotlightMap_train;
	}
	public TreeMap<String, String> getSpotlightMap_test() {
		return spotlightMap_test;
	}
	public static void setSpotlightMap_test(TreeMap<String, String> spotlightMap_test) {
		DataLoaders_NEEL.spotlightMap_test = spotlightMap_test;
	}
	
	
}
