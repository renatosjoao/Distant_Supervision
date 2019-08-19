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
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;

import de.l3s.extra.IITB_Utils;

public class DataLoaders_IITB extends DataLoaders {
	
	private static TreeSet<String> docsMap = new TreeSet<String>();

	public static void main(String[] args) throws IOException{
//		GT_MAP = new TreeMap<String, String>();
//		GT_MAP_train = new TreeMap<String, String>();
//		GT_MAP_test = new TreeMap<String, String>();
//		
//		DataLoaders_IITB.loadGT();
//		System.out.println(docsMap.size());
//		docsContent = new TreeMap<String, String>();
////		
//		dumpNumRecognizedMentionsFromIITB();
//		dumpWordCountFromIITB();
//		dumpDocumentFrequencyFromIITB();  /// ATTENTION here
//		
		try {
			DataLoaders_IITB d = DataLoaders_IITB.getInstance();
			System.out.println(d.hashCode());
			d =  DataLoaders_IITB.getInstance();
			System.out.println(d.hashCode());
			d =  DataLoaders_IITB.getInstance();
			System.out.println(d.hashCode());
			d =  DataLoaders_IITB.getInstance();
			System.out.println(d.hashCode());
			
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
	public static DataLoaders_IITB getInstance() throws CompressorException, IOException {
		if(instance == null) {
			 synchronized(DataLoaders_IITB.class) {
				 instance = new DataLoaders_IITB();
			 }
	    }
		return (DataLoaders_IITB) instance;
	}
	
	
	public DataLoaders_IITB() throws CompressorException, IOException{
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
		
//		dumpTextContent();
		
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
		File directory = new File("/home/joao/datasets/iitb/TEXT_FILES/");
		List<File> listOfFiles = (List<File>) FileUtils.listFiles(directory, null, false);
		System.out.println("# files in the text dir/ :"+listOfFiles.size());
		for (File f : listOfFiles) {
			String docName = f.getName();
			String filePAth = f.getAbsolutePath();
			FileInputStream fis = new FileInputStream(new File(filePAth));
			Scanner scanner = new Scanner(fis, "UTF-8"  );
            String articlecontent = scanner.useDelimiter("\\A").next();
			articlecontent = StringEscapeUtils.escapeJava(articlecontent);
            String docId = docName.toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			fis.close();
            scanner.close();      
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
	public static void loadDocFrequencyMap() throws CompressorException, IOException{
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/document_frequency.tsv"),StandardCharsets.UTF_8));

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
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/doc_word_count.tsv"),StandardCharsets.UTF_8));
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
	 * @return
	 * @throws CompressorException
	 * @throws IOException
	 */
	public static void loadNumMentionsRecognized() throws CompressorException, IOException {
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/num_recognized_mentions.tsv"),StandardCharsets.UTF_8));
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
	 *     This method loads a map of the ground truth with the gold standard annotations
	 * @return
	 * @throws IOException
	 */
	public static void loadGT() throws IOException{
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/iitb_GT_NONIL.tsv"),StandardCharsets.UTF_8));
//		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/iitb_GT.tsv"),StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			docsMap.add(docId);
			String mention = elems[1].toLowerCase();
			String offset = elems[2];
			String key = docId+"\t"+mention+"\t"+offset;
			String value = elems[3].replace("_"," ").toLowerCase();
			GT_MAP.put(key, value);
		}
		bffReader.close();
		System.out.println("#GT annotations :" + GT_MAP.keySet().size());
		
		
		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/iitb_GT_NONIL.train.9.tsv"),StandardCharsets.UTF_8));
		line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			docsMap.add(docId);
			String mention = elems[1].toLowerCase();
			String offset = elems[2];
			String key = docId+"\t"+mention+"\t"+offset;
			String value = elems[3].replace("_"," ").toLowerCase();
			GT_MAP_train.put(key, value);
		}
		bffReader.close();
		System.out.println("TRAIN GT annotations :"+GT_MAP_train.keySet().size());

		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/iitb_GT_NONIL.test.1.tsv"),StandardCharsets.UTF_8));
		line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			docsMap.add(docId);
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	private static void dumpTextContent()  throws NumberFormatException, IOException {
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/iitb/corpus.tsv"), StandardCharsets.UTF_8);
		
		IITB_Utils utils = new IITB_Utils();
		TreeMap<String,Integer> docsMap = utils.getDocsMap();
		Iterator<?> it = docsMap.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
			String articletitle = (String) pair.getKey();
			String filePAth = "/home/joao/datasets/iitb/crawledDocs/"+articletitle;
			Scanner scanner = new Scanner( new File(filePAth), "UTF-8"  );
            String articlecontent = scanner.useDelimiter("\\A").next();
            scanner.close();      
			articlecontent = articlecontent.replaceAll("[^\\n\\r\\t\\p{Print}]", "");
			articlecontent = articlecontent.replaceAll("\\s+", " ");
			articletitle = articletitle.toLowerCase();
            articlecontent = articlecontent.toLowerCase();
    		out.write(articletitle+"\t"+articlecontent+"\n");

    		it.remove();
		}
		out.flush();
		out.close();
	}
	/**
	 * 
	 *  This method loads the annotations created by the  NEL tools
	 *
	 * @throws IOException
	 */
	private static void loadMappings() throws IOException{
		
		BufferedReader bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb.ambiverse.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb.babelfy.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb.tagme.mappings"),StandardCharsets.UTF_8));
//		BufferedReader bffReaderSpotLight =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb_spotlight.mappings"),StandardCharsets.UTF_8));
		String line="";
	
		line="";
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
//			    mention = mention.replaceAll("\'", "");
//			    mention = mention.replaceAll("\"", "");
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
				if(entity.equalsIgnoreCase("null")){ 
					continue;
				}else{
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
		
//		System.out.println("TOTAL Spotlight annotations :"+spotlightMap.keySet().size());
		
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();
//		bffReaderSpotLight.close();

//		//////// TRAIN
		bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb_ambiverse.9.mappings"),StandardCharsets.UTF_8));
		bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb_bfy.9.mappings"),StandardCharsets.UTF_8));
		bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb_tagme.9.mappings"),StandardCharsets.UTF_8));
	
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
				ambiverseMap_train.put(docId+"\t"+mention+"\t"+offset,entity);
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
				if(entity.equalsIgnoreCase("null")){ 
					continue;
				}else{
					entity = entity.replaceAll("_"," ").toLowerCase();
					tagmeMap_train.put(docId+"\t"+mention+"\t"+offset,entity);	
				}
			}
		}
		System.out.println("Tag TRAIN annotations :"+tagmeMap_train.keySet().size());
		
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();


		/////// TEST 
		bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb_ambiverse.1.mappings"),StandardCharsets.UTF_8));
		bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb_bfy.1.mappings"),StandardCharsets.UTF_8));
		bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb_tagme.1.mappings"),StandardCharsets.UTF_8));
//		bffReaderSpotLight =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb_spotlight.1.mappings"),StandardCharsets.UTF_8));
	
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
//		System.out.println("Spot TEST annotations :"+spotlightMap_test.keySet().size());
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();
//		bffReaderSpotLight.close();
	}

	

	/***************************************************************************************************
	 # # # DUMPERS START FROM HERE # # # 
	/**************************************************************************************************/
	
	/**
	 * This utility function is meant to dump the document frequency for all mentions in the files : 
	 * 
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void dumpDocumentFrequencyFromIITB() throws IOException{
		HashSet<String> mentionsSET = new HashSet<String>();
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/iitb/document_frequency.tsv"),StandardCharsets.UTF_8);
		TreeMap<String, String>  docsContentMap = loadDocsContent();
		
		
		BufferedReader bff = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/iitb_GT_NONIL.tsv"),StandardCharsets.UTF_8));
		String line="";
		while((line = bff.readLine()) != null){
			String[] elems = line.split("\t");
			String mention = elems[1].trim().toLowerCase();
//			mention = mention.replaceAll("\'", "");
//			mention = mention.replaceAll("\"", "");
			mentionsSET.add(mention); 
		}
		bff.close();
		for (String mention : mentionsSET){
			int df = 0;
			Iterator<?> it = docsContentMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
	            String articlecontent = ((String) pair.getValue()).toLowerCase();
				if(articlecontent.contains(mention.toLowerCase())){
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
	 * 	This utility function is meant to fetch the number of words per document in the iitb corpus.
	 * 
	 * 	It produces the file:
	 * 						/home/joao/datasets/iitb/doc_word_count.tsv
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void dumpWordCountFromIITB() throws NumberFormatException, IOException {
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/iitb/doc_word_count.tsv"),StandardCharsets.UTF_8);
		IITB_Utils utils = new IITB_Utils();
		TreeMap<String,Integer> docsMap = utils.getDocsMap();
		Iterator<?> it = docsMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			String docName = (String) pair.getKey();
			String filePAth = "/home/joao/datasets/iitb/crawledDocs/"+docName;
			Scanner scanner = new Scanner( new File(filePAth), "UTF-8"  );
            String articlecontent = scanner.useDelimiter("\\A").next();
            scanner.close();      
			String trim = articlecontent.trim();
			int wc = trim.split("\\s+").length;
			String docId = docName.trim().toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			pAnn.write(docId+"\t"+wc+"\n");
			it.remove();
		}
		pAnn.flush();
		pAnn.close();
		System.out.println("...Finished dumping the Word Count Successfully.");
		
	}
	/**
	 *	This utility function is meant to fetch the number of recognized mentions per document in the iitb corpus.
	 *	
	 *	It produces the file:
	 *
	 *			/home/joao/datasets/iitb/num_recognized_mentions.tsv
	 *
	 * @throws NumberFormatException 
	 * @throws IOException
	 * @throws CompressorException
	 */
	public static void dumpNumRecognizedMentionsFromIITB() throws NumberFormatException, IOException{
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/iitb/num_recognized_mentions.tsv"),StandardCharsets.UTF_8);
		BufferedReader bff = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/iitb_GT_NONIL.tsv"),StandardCharsets.UTF_8));
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
	
	

	
//	private static TreeMap<String, String> loadDocsContent() throws NumberFormatException, IOException {
//		TreeMap<String, String> hashTreeMap = new TreeMap<String, String>();
//		IITB_Utils utils = new IITB_Utils();
//		TreeMap<String,Integer> docsMap = utils.getDocsMap();
//		Iterator<?> it = docsMap.entrySet().iterator();
//		while (it.hasNext()) {
//			Map.Entry pair = (Map.Entry)it.next();
//			String docName = (String) pair.getKey();
//			String filePAth = "/home/joao/datasets/iitb/crawledDocs/"+docName;
//			Scanner scanner = new Scanner( new File(filePAth), "UTF-8"  );
//            String articlecontent = scanner.useDelimiter("\\A").next();
//            scanner.close();
//            String docId = docName.toLowerCase();
//			docId = docId.replaceAll("\'", "");
//			docId = docId.replaceAll("\"", "");
//			hashTreeMap.put(docId, articlecontent);
//			it.remove();
//		}
//		System.out.println("...Finished loading files contents Successfully.");
//		return hashTreeMap;
//	}


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


	public TreeMap<String, String> getAmbiverseMap() {
		return ambiverseMap;
	}


	public static void setAmbiverseMap(TreeMap<String, String> ambiverseMap) {
		DataLoaders_IITB.ambiverseMap = ambiverseMap;
	}


	public TreeMap<String, String> getBabelMap() {
		return babelMap;
	}


	public static void setBabelMap(TreeMap<String, String> babelMap) {
		DataLoaders_IITB.babelMap = babelMap;
	}


	public TreeMap<String, String> getTagmeMap() {
		return tagmeMap;
	}


	public static void setTagmeMap(TreeMap<String, String> tagmeMap) {
		DataLoaders_IITB.tagmeMap = tagmeMap;
	}


	public TreeMap<String, String> getAmbiverseMap_train() {
		return ambiverseMap_train;
	}


	public static void setAmbiverseMap_train(
			TreeMap<String, String> ambiverseMap_train) {
		DataLoaders_IITB.ambiverseMap_train = ambiverseMap_train;
	}


	public TreeMap<String, String> getBabelMap_train() {
		return babelMap_train;
	}


	public static void setBabelMap_train(TreeMap<String, String> babelMap_train) {
		DataLoaders_IITB.babelMap_train = babelMap_train;
	}


	public TreeMap<String, String> getTagmeMap_train() {
		return tagmeMap_train;
	}


	public static void setTagmeMap_train(TreeMap<String, String> tagmeMap_train) {
		DataLoaders_IITB.tagmeMap_train = tagmeMap_train;
	}


	public TreeMap<String, String> getAmbiverseMap_test() {
		return ambiverseMap_test;
	}


	public static void setAmbiverseMap_test(
			TreeMap<String, String> ambiverseMap_test) {
		DataLoaders_IITB.ambiverseMap_test = ambiverseMap_test;
	}


	public TreeMap<String, String> getBabelMap_test() {
		return babelMap_test;
	}


	public static void setBabelMap_test(TreeMap<String, String> babelMap_test) {
		DataLoaders_IITB.babelMap_test = babelMap_test;
	}


	public TreeMap<String, String> getTagmeMap_test() {
		return tagmeMap_test;
	}

	public static void setTagmeMap_test(TreeMap<String, String> tagmeMap_test) {
		DataLoaders_IITB.tagmeMap_test = tagmeMap_test;
	}


	
	public static TreeMap<String, String> getSpotlightMap() {
		return spotlightMap;
	}
	public static void setSpotlightMap(TreeMap<String, String> spotlightMap) {
		DataLoaders_CONLL.spotlightMap = spotlightMap;
	}
	public TreeMap<String, String> getSpotlightMap_train() {
		return spotlightMap_train;
	}
	public static void setSpotlightMap_train(
			TreeMap<String, String> spotlightMap_train) {
		DataLoaders_CONLL.spotlightMap_train = spotlightMap_train;
	}
	public TreeMap<String, String> getSpotlightMap_test() {
		return spotlightMap_test;
	}
	public static void setSpotlightMap_test(
			TreeMap<String, String> spotlightMap_test) {
		DataLoaders_CONLL.spotlightMap_test = spotlightMap_test;
	}
	
	
}
