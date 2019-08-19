package de.l3s.loaders;
/**
 *  
 * @author Renato Stoffalette Joao
 * @mail renatosjoao@gmail.com
 * @version 1.0
 * @date 11.2018
 * @since 1.0
 */
import gnu.trove.map.hash.TObjectIntHashMap;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.compress.compressors.CompressorException;
import de.l3s.extra.AIDA_YAGO2_annotations_Parser;
import de.l3s.extra.GenericDocument;

public class DataLoaders_CONLL  extends DataLoaders{

	public static void main(String[] args) throws NumberFormatException, IOException{
//		System.out.println(GTMAP.size());
//		dumpNumRecognizedMentionsFromCONLL();
//		dumpWordCountFromCONLL();
//		dumpDocumentFrequencyFromCONLL(); 
		
		try {
			DataLoaders_CONLL d = DataLoaders_CONLL.getInstance();
			System.out.println(d.hashCode());
			d =  DataLoaders_CONLL.getInstance();
			System.out.println(d.hashCode());
			d =  DataLoaders_CONLL.getInstance();
			System.out.println(d.hashCode());
			d =  DataLoaders_CONLL.getInstance();
			System.out.println(d.hashCode());
			
//			d.dumpTextContent();
			
		} catch (CompressorException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 *
	 * @return
	 * @throws IOException 
	 * @throws CompressorException 
	 */
	public static DataLoaders_CONLL getInstance() throws CompressorException, IOException {
		if(instance == null) {
			 synchronized(DataLoaders_CONLL.class) {
				 instance = new DataLoaders_CONLL();
			 }
	    }
		return (DataLoaders_CONLL) instance;
	}
	
	
	public DataLoaders_CONLL() throws CompressorException, IOException{
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
		NEs = new TreeSet<String>();
		
		loadDocFrequencyMap();
		loadWordCount();
// 		loadMentionEntityCountMap();
		loadNumMentionsRecognized();
		loadGT();
		loadMappings();
		loadDocsContent();
//		System.out.println(NEs.size());
		
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
	@SuppressWarnings("rawtypes")
	public void loadDocsContent() throws NumberFormatException, IOException {
		AIDA_YAGO2_annotations_Parser p = new AIDA_YAGO2_annotations_Parser();
		LinkedList<GenericDocument> ConllDataSet = p.parseDataset("/home/joao/datasets/conll/AIDA-YAGO2-dataset.tsv");
		for(GenericDocument CO  : ConllDataSet){
			String articletitle = CO.getTitle();
			String docid =  articletitle.toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
    		String articlecontent = CO.getTxtContent().trim();
    		docsContent.put(docid, articlecontent);
		}
		System.out.println("# documents in the text dir/ :" + ConllDataSet.size());
	}
	/**
	 *
	 *	This method is used to load the document frequency map.
	 *
	 * @throws CompressorException
	 * @throws IOException
	 */
	public static void loadDocFrequencyMap() throws CompressorException, IOException{
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/document_frequency.tsv"),StandardCharsets.UTF_8));
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
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/doc_word_count.tsv"),StandardCharsets.UTF_8));
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
	public static void loadNumMentionsRecognized() throws CompressorException, IOException {
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/num_recognized_mentions.tsv"),StandardCharsets.UTF_8));
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
	public static void loadGT() throws IOException{
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/conllYAGO_all_GT.tsv"),StandardCharsets.UTF_8));
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
		
		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/conllYAGO_train_GT.tsv"),StandardCharsets.UTF_8));
		line = "";
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
				GT_MAP_train.put(key,value);
			}
			
		}
		bffReader.close();
		System.out.println("TRAIN :"+GT_MAP_train.keySet().size());

		
		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/conllYAGO_testb_GT_NONIL.tsv"),StandardCharsets.UTF_8));
		line = "";
		while((line = bffReader.readLine() )!= null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String mention = elems[1].toLowerCase();
			String offset = elems[2];
			String link = elems[3].toLowerCase();
			String key = docId+"\t"+mention+"\t"+offset;
			String value = link.toLowerCase();
			if(!value.contains("--nme--")){
				GT_MAP_test.put(key,value);	
			}
		}
		bffReader.close();
		System.out.println("TEST :"+GT_MAP_test.keySet().size());
	}
	
	/**
	 *
	 * @throws CompressorException
	 * @throws IOException
	 */
	public static HashMap<String,String> loadTopicMap() throws CompressorException, IOException {
		HashMap<String,String> DocTopicMap = new HashMap<String, String>();
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/doc_topics.tsv"),StandardCharsets.UTF_8));
		String line ="";
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String topic = elems[1];
			DocTopicMap.put(docId, topic);
		}
		bffReader.close();
		return DocTopicMap;
	}

	/**
	 * 
	 *  This method loads the annotations created by the  NEL tools
	 *
	 * @throws IOException
	 */
	public static void loadMappings() throws IOException{
		BufferedReader bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_ambiverse_all.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_bfy_all.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_tagme_all.mappings"),StandardCharsets.UTF_8));
//		BufferedReader bffReaderSpotLight =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_spotlight_all.mappings"),StandardCharsets.UTF_8));
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
				NEs.add(mention);
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
				NEs.add(mention);
			}
		}
//	
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
				NEs.add(mention);
			}
		}		
		System.out.println("TOTAL Tag annotations :"+tagmeMap.keySet().size());
		
//		line="";
//		while ((line = bffReaderSpotLight.readLine()) != null) {
//			String[] elements = line.split("\t");
//			if(elements.length >=4){
//				String docId = elements[0].toLowerCase();
//				docId = docId.replaceAll("\'", "");
//				String mention = elements[1].toLowerCase();
//			    mention = mention.replaceAll("\'", "");
//				docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
//				String offset =  elements[2];
//				String entity = elements[3].toLowerCase();
//				entity = entity.replaceAll("_"," ").toLowerCase();
//				spotlightMap.put(docId+"\t"+mention+"\t"+offset,entity);
//			}
//		}
		
//		System.out.println("TOTAL SpotLight annotations :"+spotlightMap.keySet().size());
		

		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();
//		bffReaderSpotLight.close();
		
//		//////// TRAIN
		bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_ambiverse_train.mappings"),StandardCharsets.UTF_8));
		bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_bfy_train.mappings"),StandardCharsets.UTF_8));
		bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_tagme_train.mappings"),StandardCharsets.UTF_8));
	
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
//		bffReaderSpotLight.close();
		
		/////// TEST 
		bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_ambiverse_testb.mappings"),StandardCharsets.UTF_8));
		bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_bfy_testb.mappings"),StandardCharsets.UTF_8));
		bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_tagme_testb.mappings"),StandardCharsets.UTF_8));
//		bffReaderSpotLight =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_spotlight_testb.mappings"),StandardCharsets.UTF_8));
		
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
//			String[] elems = line.split("\t");
//			if(elems.length >=4){
//				String docId = elems[0].toLowerCase();
//				docId = docId.replaceAll("\'", "");
//				docId = docId.replaceAll("\"", "");
//				String mention = elems[1].toLowerCase();
//			    mention = mention.replaceAll("\'", "");
//			    mention = mention.replaceAll("\"", "");
//				docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
//				String offset =  elems[2];
//				String entity = elems[3].toLowerCase();
//				entity = entity.replaceAll("_"," ").toLowerCase();
//				spotlightMap_test.put(docId+"\t"+mention+"\t"+offset,entity);
//			}
//		}
		
//		System.out.println("Spotlight TEST annotations :"+spotlightMap_test.keySet().size());

		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();
//		bffReaderSpotLight.close();
	
	}


	/***************************************************************************************************
	 # # # DUMPERS START FROM HERE # # # 
	/**************************************************************************************************/	
	/**
	 *  This utility function is meant to fetch the number of words per document 
	 * 
	 * 	It produces the file:
	 * 						/home/joao/datasets/conll/doc_word_count.tsv
	 * 
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void dumpWordCountFromCONLL() throws NumberFormatException, IOException{
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/conll/doc_word_count.tsv"),StandardCharsets.UTF_8);
		AIDA_YAGO2_annotations_Parser p = new AIDA_YAGO2_annotations_Parser();
		LinkedList<GenericDocument> ConllDataSet = p.parseDataset("/home/joao/datasets/conll/AIDA-YAGO2-dataset.tsv");
		for(GenericDocument CO : ConllDataSet){
			String docid = CO.getTitle().toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			String articlecontent = CO.getTxtContent();
			String trim = articlecontent.trim();
			int wc = trim.split("\\s+").length;			
			pAnn.write(docid+"\t"+wc+"\n");
		}
		pAnn.flush();
		pAnn.close();
		System.out.println("...Finished dumping the Word Count Successfully.");
	}
	
	/**
	 * This utility function is meant to dump the document frequency for all mentions in the files  
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void dumpDocumentFrequencyFromCONLL() throws IOException{
		HashSet<String> mentionsSET = new HashSet<String>();
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/conll/document_frequency.tsv"),StandardCharsets.UTF_8);
//		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_all_dataset.easy"),StandardCharsets.UTF_8));
//		String line = "";
//		while((line = bffReader.readLine() )!= null) {
//			String[] elems = line.split("\t");
//			//String docId = elems[0];
//			String mention = elems[1];
//			mentionsSET.add(mention);
//		}
//		bffReader.close();
//
//		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_all_dataset.medium"),StandardCharsets.UTF_8));
//		line = "";
//		while((line = bffReader.readLine() )!= null) {
//			String[] elems = line.split("\t");
//			//String docId = elems[0];
//			String mention = elems[1];
//			mentionsSET.add(mention);
//		}
//		bffReader.close();
//
//		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mappings/conll_all_dataset.hard"),StandardCharsets.UTF_8));
//		line = "";
//		while((line = bffReader.readLine() )!= null) {
//			String[] elems = line.split("\t");
//			//String docId = elems[0];
//			String mention = elems[1];
//			mentionsSET.add(mention);
//		}
//		
//		bffReader.close();
//		
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/conllYAGO_all_GT.tsv"),StandardCharsets.UTF_8));
		String line = "";
		while((line = bffReader.readLine() )!= null) {
			String[] elems = line.split("\t");
			String mention = elems[1].toLowerCase();
			mentionsSET.add(mention);
		}
		bffReader.close();
		
		AIDA_YAGO2_annotations_Parser p = new AIDA_YAGO2_annotations_Parser();
		LinkedList<GenericDocument> ConllDataSet = p.parseDataset("/home/joao/datasets/conll/AIDA-YAGO2-dataset.tsv");
		
		for (String mention : mentionsSET){
			int df = 0;
			for(GenericDocument CO : ConllDataSet){
				//String articletitle = CO.getTitle();
				String articlecontent = CO.getTxtContent().toLowerCase();
				if(articlecontent.contains(mention)){
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
	 *	This utility function is meant to fetch the number of recognized mentions per document in the CONLL corpus.
	 *	
	 *	It produces the file:
	 *
	 *			/home/joao/datasets/conll/num_recognized_mentions.tsv
	 *
	 * @throws NumberFormatException 
	 * @throws IOException
	 * @throws CompressorException
	 */
	public static void dumpNumRecognizedMentionsFromCONLL() throws NumberFormatException, IOException{
		System.out.println("Dumping the number of recognized mentions per document.");

		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/conll/num_recognized_mentions.tsv"),StandardCharsets.UTF_8);
		AIDA_YAGO2_annotations_Parser p = new AIDA_YAGO2_annotations_Parser();
		@SuppressWarnings("rawtypes")
		LinkedList<GenericDocument> ConllDataSet = p.parseDataset("/home/joao/datasets/conll/AIDA-YAGO2-dataset.tsv");
		for(int i = 0; i < ConllDataSet.size(); i++){  // Writing only the training set !!!!for(int i = 1162; i < ConllDataSet.size(); i++){ 
			@SuppressWarnings("rawtypes")
			GenericDocument CO  = ConllDataSet.get(i);
			String docid = CO.getTitle().toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			@SuppressWarnings("unchecked")
			LinkedList<String> annotationsL = CO.getListOfAnnotation();
			pAnn.write(docid+"\t"+annotationsL.size()+"\n");
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
	@SuppressWarnings("rawtypes")
	public void dumpTextContent() throws NumberFormatException, IOException {
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/conll/corpus.tsv"), StandardCharsets.UTF_8);

		AIDA_YAGO2_annotations_Parser p = new AIDA_YAGO2_annotations_Parser();
		LinkedList<GenericDocument> ConllDataSet = p.parseDataset("/home/joao/datasets/conll/AIDA-YAGO2-dataset.tsv");
		for(GenericDocument CO  : ConllDataSet){
			String docid = CO.getTitle().toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			String articlecontent = CO.getTxtContent().trim();
			articlecontent = articlecontent.replaceAll("[^\\n\\r\\t\\p{Print}]", "");
			articlecontent = articlecontent.toLowerCase();
			out.write(docid+"\t"+articlecontent+"\n");
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
		DataLoaders_CONLL.ambiverseMap = ambiverseMap;
	}
	public TreeMap<String, String> getBabelMap() {
		return babelMap;
	}
	public static void setBabelMap(TreeMap<String, String> babelMap) {
		DataLoaders_CONLL.babelMap = babelMap;
	}
	public TreeMap<String, String> getTagmeMap() {
		return tagmeMap;
	}
	
	public static void setTagmeMap(TreeMap<String, String> tagmeMap) {
		DataLoaders_CONLL.tagmeMap = tagmeMap;
	}
	public static void setAmbiverseMap_train(TreeMap<String, String> ambiverseMap_train) {
		DataLoaders_CONLL.ambiverseMap_train = ambiverseMap_train;
	}
	public static void setBabelMONLLap_train(TreeMap<String, String> babelMap_train) {
		DataLoaders_CONLL.babelMap_train = babelMap_train;
	}
	public static void setTagmeMap_train(TreeMap<String, String> tagmeMap_train) {
		DataLoaders_CONLL.tagmeMap_train = tagmeMap_train;
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
		DataLoaders_CONLL.ambiverseMap_test = ambiverseMap_test;
	}
	public TreeMap<String, String> getBabelMap_train() {
		return babelMap_train;
	}
	public TreeMap<String, String> getBabelMap_test() {
		return babelMap_test;
	}
	public static void setBabelMap_test(TreeMap<String, String> babelMap_test) {
		DataLoaders_CONLL.babelMap_test = babelMap_test;
	}
	public TreeMap<String, String> getTagmeMap_train() {
		return tagmeMap_train;
	}
	public TreeMap<String, String> getTagmeMap_test() {
		return tagmeMap_test;
	}
	public static void setTagmeMap_test(TreeMap<String, String> tagmeMap_test) {
		DataLoaders_CONLL.tagmeMap_test = tagmeMap_test;
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
		DataLoaders_CONLL.spotlightMap = spotlightMap;
	}
	public static void setSpotlightMap_train(TreeMap<String, String> spotlightMap_train) {
		DataLoaders_CONLL.spotlightMap_train = spotlightMap_train;
	}
	public static void setSpotlightMap_test(TreeMap<String, String> spotlightMap_test) {
		DataLoaders_CONLL.spotlightMap_test = spotlightMap_test;
	}
	
}