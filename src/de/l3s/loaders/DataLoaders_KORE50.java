package de.l3s.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import de.l3s.extra.ConllDocument;
import de.l3s.extra.GenericDocument;
import de.l3s.extra.WP_annotations_Parser;
import gnu.trove.map.hash.TObjectIntHashMap;

public class DataLoaders_KORE50 extends DataLoaders{
	

	public static void main(String[] args) throws NumberFormatException, IOException {
//		docsContent = new TreeMap<String, String>();
//		dumpNumRecognizedMentionsFromkore50();
//		dumpWordCountFromkore50();
//		dumpDocumentFrequencyFromkore50();
//		
		try {
			DataLoaders_KORE50 d = DataLoaders_KORE50.getInstance();
			System.out.println(d.hashCode());
			d = DataLoaders_KORE50.getInstance();
			System.out.println(d.hashCode());
			d = DataLoaders_KORE50.getInstance();
			System.out.println(d.hashCode());
			d = DataLoaders_KORE50.getInstance();
			System.out.println(d.hashCode());
    		 d.dumpTextContent();

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
	public static DataLoaders_KORE50 getInstance() throws CompressorException, IOException {
		if(instance == null) {
			 synchronized(DataLoaders_KORE50.class) {
				 instance = new DataLoaders_KORE50();
			 }
	    }
		return (DataLoaders_KORE50) instance;
	}
	
	
	public DataLoaders_KORE50() throws CompressorException, IOException{
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
		
		ambiverseMap_train = new TreeMap<String, String>();
		babelMap_train = new TreeMap<String, String>();
		tagmeMap_train = new TreeMap<String, String>();
		
		ambiverseMap_test = new TreeMap<String, String>();
		babelMap_test = new TreeMap<String, String>();
		tagmeMap_test = new TreeMap<String, String>();
//		
		docsContent = new TreeMap<String, String>();
//		
		loadDocFrequencyMap();
		loadWordCount();
		loadNumMentionsRecognized();
		loadGT();
		loadMappings();
		loadDocsContent();
		
	}
	
	

	/***************************************************************************************************
	 # # # LOADERS START FROM HERE # # # 
	***************************************************************************************************/


	/**
	 * 					This method loads the documents contents into a map 
	 *
	 * @throws IOException 
	 * @throws NumberFormatException 
	 * 
	 **/
	private static TreeMap<String, String> loadDocsContent() throws NumberFormatException, IOException {
		File directory = new File("/home/joao/datasets/kore50/TEXT_FILES/"); 
   		String[] extensions = {"txt"};
       	List<File> listOfFiles =  (List<File>) FileUtils.listFiles(directory, extensions,true);
		System.out.println("# files in the text dir/ :"+listOfFiles.size());
       	for(File f : listOfFiles){
			String filePAth = f.getAbsolutePath();
			Scanner scanner = new Scanner( new File(filePAth), "UTF-8"  );
			String articlecontent = scanner.useDelimiter("\\A").next();
        	scanner.close();
        	String docId = f.getName().toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "").split(".txt")[0];
        	Charset.forName("UTF-8").encode(articlecontent);
        	docsContent.put(docId, articlecontent);
       	}
       	System.out.println("# documents in the text dir/ :" + docsContent.keySet().size());
		return docsContent;
	}
	/**
	 *
	 *	This method is used to load the document frequency map.
	 *
	 * @throws CompressorException
	 * @throws IOException
	 */
	private static void loadDocFrequencyMap() throws CompressorException, IOException{
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/document_frequency.tsv"),StandardCharsets.UTF_8));
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
	private static void loadWordCount() throws CompressorException, IOException {
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/doc_word_count.tsv"),StandardCharsets.UTF_8));
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
		System.out.println("Loaded Word Count Map Successfully.");
	}
	/**
	 *
	 *	This method loads a map of the number of recognized mentions per document
	 *
	 * @return
	 * @throws CompressorException
	 * @throws IOException				
	 */
	private static void loadNumMentionsRecognized() throws CompressorException, IOException {
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/num_recognized_mentions.tsv"),StandardCharsets.UTF_8));
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
		System.out.println("Loaded Number Recognized Mentions Map Successfully.");
	}
	/**
	 *     This method loads a map of the ground truth with the gold standard annotations
	 * @return
	 * @throws IOException
	 */
	
	private void loadGT() throws IOException {
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/KORE50_GT_NONNIL.tsv"),StandardCharsets.UTF_8));
		String line = "";
		while((line = bffReader.readLine() )!= null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String mention = elems[1].toLowerCase();
			String offset = elems[2];
			String key = docId+"\t"+mention+"\t"+offset;
			String value = elems[3].replace("_"," ").toLowerCase();
			if(!value.contains("--nme--")){
				GT_MAP.put(key,value);
			}
		}
		bffReader.close();
		System.out.println("#GT annotations :" + GT_MAP.keySet().size());
		
//		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/kore50_GT.train.9.tsv"),StandardCharsets.UTF_8));
//		line = "";
//		while((line = bffReader.readLine() )!= null) {
//			String[] elems = line.split("\t");
//			String docId = elems[0].toLowerCase();
//			docId = docId.replaceAll("\'", "");
//			docId = docId.replaceAll("\"", "");
//			String mention = elems[1].toLowerCase();
////		    mention = mention.replaceAll("\'", "");
////		    mention = mention.replaceAll("\"", "");
//			String offset = elems[2];
//			String key = docId+"\t"+mention+"\t"+offset;
//			String value = elems[3].replace("_"," ").toLowerCase();
//			if(!value.contains("--nme--")){
//				GT_MAP_train.put(key,value);
//			}
//			
//		}
//		bffReader.close();
//		System.out.println("TRAIN :"+GT_MAP_train.keySet().size());
//		
//		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/KORE50_GT.test.1.tsv"),StandardCharsets.UTF_8));
//		line = "";
//		while((line = bffReader.readLine() )!= null) {
//			String[] elems = line.split("\t");
//			String docId = elems[0].toLowerCase();
//			docId = docId.replaceAll("\'", "");
//			docId = docId.replaceAll("\"", "");
//			String mention = elems[1].toLowerCase();
////		    mention = mention.replaceAll("\'", "");
////		    mention = mention.replaceAll("\"", "");
//			String offset = elems[2];
//			String link = elems[3].toLowerCase();
//			String key = docId+"\t"+mention+"\t"+offset;
//			String value = link.toLowerCase();
//			if(!value.contains("--nme--")){
//				GT_MAP_test.put(key,value);	
//			}
//		}
//		bffReader.close();
//		System.out.println("TEST :"+GT_MAP_test.keySet().size());
	}
	/**
	 * 
	 *  This method loads the annotations created by the  NEL tools
	 *
	 * @throws IOException
	 */
	private static void loadMappings() throws IOException{
		BufferedReader bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/mappings/kore50.ambiverse.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/mappings/kore50.babelfy.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/mappings/kore50.tagme.mappings"),StandardCharsets.UTF_8));
//		BufferedReader bffReaderSpotLight =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/mappings/kore50_spotlight_all.mappings"),StandardCharsets.UTF_8));
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
				if(!entity.equalsIgnoreCase("null")){
					entity = entity.replaceAll("_"," ").toLowerCase();
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
				if(!entity.equalsIgnoreCase("null")){
					entity = entity.replaceAll("_"," ").toLowerCase();
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
				if(!entity.equalsIgnoreCase("null")){
					entity = entity.replaceAll("_"," ").toLowerCase();
					tagmeMap.put(docId+"\t"+mention+"\t"+offset,entity);
				}
			}
		}
		System.out.println("TOTAL Tag annotations :"+tagmeMap.keySet().size());
		
//		line="";
//		while ((line = bffReaderSpotLight.readLine()) != null) {
//			String[] elements = line.split("\t");
//			if(elements.length >=4){
//				String docId = elements[0];
//				docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
//				String mention = elements[1].toLowerCase();
//				String offset =  elements[2];
//				String entity = elements[3];
//				entity = entity.replaceAll("_"," ").toLowerCase();
//				spotlightMap.put(docId+"\t"+mention+"\t"+offset,entity);
//			}
//		}
		
//		System.out.println("TOTAL SpotLight annotations :"+spotlightMap.keySet().size());
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();
//		bffReaderSpotLight.close();
		
		//////// TRAIN
//		bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/mappings/kore50_ambiverse_train.mappings"),StandardCharsets.UTF_8));
//		bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/mappings/kore50_bfy_train.mappings"),StandardCharsets.UTF_8));
//		bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/mappings/kore50_tagme_train.mappings"),StandardCharsets.UTF_8));
//	
//		line="";
//		while ((line = bffReaderAmbiverse.readLine()) != null) {
//			String[] elems = line.split("\t");
//			if(elems.length >=4){
//				String docId = elems[0].toLowerCase();
//				docId = docId.replaceAll("\'", "");
//				docId = docId.replaceAll("\"", "");
//				String mention = elems[1].toLowerCase();
//			    mention = mention.replaceAll("\'", "");
//			    mention = mention.replaceAll("\"", "");
//				String offset =  elems[2];
//				String entity = elems[3].toLowerCase();
//				entity = entity.replaceAll("_"," ").toLowerCase();
//				if(!entity.equalsIgnoreCase("null")){
//					entity = entity.replaceAll("_"," ").toLowerCase();
//					ambiverseMap_train.put(docId+"\t"+mention+"\t"+offset,entity);
//				}
//			}
//		}
//		System.out.println("Amb TRAIN annotations :"+ambiverseMap_train.keySet().size());

//		line="";
//		while ((line = bffReaderBabelfy.readLine()) != null) {
//			String[] elems = line.split("\t");
//			if(elems.length >=4){
//				String docId = elems[0].toLowerCase();
//				docId = docId.replaceAll("\'", "");
//				docId = docId.replaceAll("\"", "");
//				String mention = elems[1].toLowerCase();
////			    mention = mention.replaceAll("\'", "");
////			    mention = mention.replaceAll("\"", "");
//				String offset =  elems[2];
//				String entity = elems[3].toLowerCase();
//				entity = entity.replaceAll("_"," ").toLowerCase();
//				if(!entity.equalsIgnoreCase("null")){
//					entity = entity.replaceAll("_"," ").toLowerCase();
//					babelMap_train.put(docId+"\t"+mention+"\t"+offset,entity);
//				}
//			}
//		}
//		System.out.println("Bab TRAIN annotations :"+babelMap_train.keySet().size());
//
//		line="";
//		while ((line = bffReaderTagme.readLine()) != null) {
//			String[] elems = line.split("\t");
//			if(elems.length >=4){
//				String docId = elems[0].toLowerCase();
//				docId = docId.replaceAll("\'", "");
//				docId = docId.replaceAll("\"", "");
//				String mention = elems[1].toLowerCase();
////			    mention = mention.replaceAll("\'", "");
////			    mention = mention.replaceAll("\"", "");
//				docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
//				String offset =  elems[2];
//				String entity = elems[3].toLowerCase();
//				entity = entity.replaceAll("_"," ").toLowerCase();
//				if(!entity.equalsIgnoreCase("null")){
//					entity = entity.replaceAll("_"," ").toLowerCase();
//					tagmeMap_train.put(docId+"\t"+mention+"\t"+offset,entity);
//				}
//			}
//		}
//		System.out.println("Tag TRAIN annotations :"+tagmeMap_train.keySet().size());
//		bffReaderBabelfy.close();
//		bffReaderTagme.close();
//		bffReaderAmbiverse.close();
//		bffReaderSpotLight.close();
		
		/////// TEST 
//		bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/mappings/kore50_ambiverse_test.mappings"),StandardCharsets.UTF_8));
//		bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/mappings/kore50_bfy_test.mappings"),StandardCharsets.UTF_8));
//		bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/mappings/kore50_tagme_test.mappings"),StandardCharsets.UTF_8));
//		bffReaderSpotLight =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/mappings/kore50_spotlight_test.mappings"),StandardCharsets.UTF_8));
		
//		line="";
//		while ((line = bffReaderAmbiverse.readLine()) != null) {
//			String[] elems = line.split("\t");
//			if(elems.length >=4){
//				String docId = elems[0].toLowerCase();
//				docId = docId.replaceAll("\'", "");
//				docId = docId.replaceAll("\"", "");
//				String mention = elems[1].toLowerCase();
//			    mention = mention.replaceAll("\'", "");
//			    mention = mention.replaceAll("\"", "");
//				String offset =  elems[2];
//				String entity = elems[3].toLowerCase();
//				entity = entity.replaceAll("_"," ").toLowerCase();
//				if(!entity.equalsIgnoreCase("null")){
//					entity = entity.replaceAll("_"," ").toLowerCase();
//					ambiverseMap_test.put(docId+"\t"+mention+"\t"+offset,entity);
//				}
//			}
//		}
//		System.out.println("Amb TEST annotations :"+ambiverseMap_test.keySet().size());
//
//		line="";
//		while ((line = bffReaderBabelfy.readLine()) != null) {
//			String[] elems = line.split("\t");
//			if(elems.length >=4){
//				String docId = elems[0].toLowerCase();
//				docId = docId.replaceAll("\'", "");
//				docId = docId.replaceAll("\"", "");
//				String mention = elems[1].toLowerCase();
////			    mention = mention.replaceAll("\'", "");
////			    mention = mention.replaceAll("\"", "");
//				String offset =  elems[2];
//				String entity = elems[3].toLowerCase();
//				entity = entity.replaceAll("_"," ").toLowerCase();
//				if(!entity.equalsIgnoreCase("null")){
//					entity = entity.replaceAll("_"," ").toLowerCase();
//					babelMap_test.put(docId+"\t"+mention+"\t"+offset,entity);
//				}
//			}
//		}
//		System.out.println("Bab TEST annotations :"+babelMap_test.keySet().size());
//		
//		line="";
//		while ((line = bffReaderTagme.readLine()) != null) {
//			String[] elems = line.split("\t");
//			if(elems.length >=4){
//				String docId = elems[0].toLowerCase();
//				docId = docId.replaceAll("\'", "");
//				docId = docId.replaceAll("\"", "");
//				String mention = elems[1].toLowerCase();
////			    mention = mention.replaceAll("\'", "");
////			    mention = mention.replaceAll("\"", "");
//				docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
//				String offset =  elems[2];
//				String entity = elems[3].toLowerCase();
//				entity = entity.replaceAll("_"," ").toLowerCase();
//				if(!entity.equalsIgnoreCase("null")){
//					entity = entity.replaceAll("_"," ").toLowerCase();
//					tagmeMap_test.put(docId+"\t"+mention+"\t"+offset,entity);
//				}
//			}
//		}
//		System.out.println("Tag TEST annotations :"+tagmeMap_test.keySet().size());

//		
//		line="";
//		while ((line = bffReaderSpotLight.readLine()) != null) {
//			String[] elements = line.split("\t");
//			if(elements.length >=4){
//				String docId = elements[0];
//				docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
//				String mention = elements[1].toLowerCase();
//				String offset =  elements[2];
//				String entity = elements[3];
//				entity = entity.replaceAll("_"," ").toLowerCase();
//				spotlightMap_test.put(docId+"\t"+mention+"\t"+offset,entity);
//			}
//		}
//		System.out.println("Spotlight TEST annotations :"+spotlightMap_test.keySet().size());

//		bffReaderBabelfy.close();
//		bffReaderTagme.close();
//		bffReaderAmbiverse.close();
//		bffReaderSpotLight.close();
		
	
	}
	
	/***************************************************************************************************
	 # # # DUMPERS START FROM HERE # # # 
	/**************************************************************************************************/	

	/**
	 * 	This utility function is meant to fetch the number of words per document in the kore50 corpus.
	 * 
	 * 	It produces the file:
	 * 						/home/joao/datasets/kore50/doc_word_count.tsv
	 * @throws NumberFormatException
	 * @throws IOException
	 */

	private static void dumpWordCountFromkore50() throws IOException {
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/kore50/doc_word_count.tsv"),StandardCharsets.UTF_8);
		File directory = new File("/home/joao/datasets/kore50/TEXT_FILES/"); 
   		String[] extensions = {"txt"};
       	List<File> listOfFiles =  (List<File>) FileUtils.listFiles(directory, extensions,true);
       	for(File f : listOfFiles){
			String filePAth = f.getAbsolutePath();
			Scanner scanner = new Scanner( new File(filePAth), "UTF-8"  );
			String articlecontent = scanner.useDelimiter("\\A").next();
        	scanner.close();
        	String docId = f.getName().toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "").split(".txt")[0];
        	Charset.forName("UTF-8").encode(articlecontent);
			int wc = articlecontent.split("\\s+").length;	
			pAnn.write(docId+"\t"+wc+"\n");
       	}
       	pAnn.close();
		System.out.println("...Finished dumping the Word Count Successfully.");
	}


	/**
	 * This utility function is meant to dump the document frequency for all mentions in the files : 
	 * 
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void dumpDocumentFrequencyFromkore50() throws IOException{
		HashSet<String> mentionsSET = new HashSet<String>();
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/kore50/document_frequency.tsv"),StandardCharsets.UTF_8);
		TreeMap<String, String>  docsContentMap = loadDocsContent();
		BufferedReader bff = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/KORE50_GT_NONNIL.tsv"),StandardCharsets.UTF_8));
		String line="";
		while((line = bff.readLine()) != null){
			String[] elems = line.split("\t");
			String mention = elems[1].trim().toLowerCase();
			mentionsSET.add(mention);
		}
		bff.close();
		
		for (String mention : mentionsSET){
			int df = 0;
			Iterator<?> it = docsContentMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
	            String articlecontent = (String) pair.getValue();
				if(articlecontent.toLowerCase().contains(mention)){
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
	 *	This utility function is meant to fetch the number of recognized mentions per document in the wp_corpus corpus.
	 *	
	 *	It produces the file:
	 *
	 *			num_recognized_mentions.tsv
	 *
	 * @throws NumberFormatException 
	 * @throws IOException
	 * @throws CompressorException
	 */
	public static void dumpNumRecognizedMentionsFromkore50() throws NumberFormatException, IOException{
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/kore50/num_recognized_mentions.tsv"),StandardCharsets.UTF_8);
		BufferedReader bff = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/kore50/KORE50_GT_NONNIL.tsv"),StandardCharsets.UTF_8));
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
	 *
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private void dumpTextContent() throws NumberFormatException, IOException {
		WP_annotations_Parser p = new WP_annotations_Parser();
		@SuppressWarnings("rawtypes")
		LinkedList<ConllDocument> KORE50DataSet = p.parseDataset("/home/joao/datasets/kore50/AIDA.tsv");
		for (int i = 0; i < KORE50DataSet.size(); i++) {
			
			@SuppressWarnings("rawtypes")
			ConllDocument CO = KORE50DataSet.get(i);

			String docName = CO.getTitle();
			String articlecontent = CO.getTxtContent();
			articlecontent = StringEscapeUtils.escapeJava(articlecontent);
//			System.out.println(docName);
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/kore50/TEXT_FILES/"+docName+".txt"), StandardCharsets.UTF_8);
			out.write(articlecontent);
			out.flush();
			out.close();

	}
	}
	
	public static TObjectIntHashMap<String> getDocFrequencyMap() {
		return DocFrequencyMap;
	}
	public static void setDocFrequencyMap(TObjectIntHashMap<String> docFrequencyMap) {
		DocFrequencyMap = docFrequencyMap;
	}
	public static TObjectIntHashMap<String> getDocWordCountMap() {
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
	public static TObjectIntHashMap<String> getNumRecogMentionMap() {
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
	
	public static void setAmbiverseMap(TreeMap<String, String> ambiverseMap) {
		DataLoaders_KORE50.ambiverseMap = ambiverseMap;
	}
	public static void setBabelMap(TreeMap<String, String> babelMap) {
		DataLoaders_KORE50.babelMap = babelMap;
	}

	public static void setTagmeMap(TreeMap<String, String> tagmeMap) {
		DataLoaders_KORE50.tagmeMap = tagmeMap;
	}
	public static void setAmbiverseMap_train(TreeMap<String, String> ambiverseMap_train) {
		DataLoaders_KORE50.ambiverseMap_train = ambiverseMap_train;
	}
	public static void setBabelMap_train(TreeMap<String, String> babelMap_train) {
		DataLoaders_KORE50.babelMap_train = babelMap_train;
	}
	public static void setTagmeMap_train(TreeMap<String, String> tagmeMap_train) {
		DataLoaders_KORE50.tagmeMap_train = tagmeMap_train;
	}
	public TreeMap<String,String> getDocsContent(){
		return docsContent;
	}
	
	public static void setBabelMap_test(TreeMap<String, String> babelMap_test) {
		DataLoaders_KORE50.babelMap_test = babelMap_test;
	}

	public static void setTagmeMap_test(TreeMap<String, String> tagmeMap_test) {
		DataLoaders_KORE50.tagmeMap_test = tagmeMap_test;
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