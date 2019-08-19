package de.l3s.loaders;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.DocumentImpl;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DataLoaders_GERDAQ  extends DataLoaders {
	
    private static final String WIKIPEDIA_URI = "http://en.wikipedia.org/wiki/";
    private static final String DBPEDIA_URI = "http://dbpedia.org/resource/";
    private static final String ANNOTATION_TAG = "annotation";
    private static final String DOCUMENT_TAG = "instance";
    private String file;
    private static String name = "gerdaq";
    private List<org.aksw.gerbil.transfer.nif.Document> documents;

	public static void main(String[] args) throws Exception{
//		dumpGT();
//		dumpTextContent();
		
//		docsContent = new TreeMap<String, String>();
//		dumpNumRecognizedMentionsFromGERDAQ();
//		try {
//			dumpWordCountFromGERDAQ();
//			dumpDocumentFrequencyFromGERDAQ();
//		} catch (ParserConfigurationException e) {
//			e.printStackTrace();
//		} catch (SAXException e) {
//			e.printStackTrace();
//		}
		
		try {
			DataLoaders_GERDAQ d = DataLoaders_GERDAQ.getInstance();
			System.out.println(d.hashCode());
			d =  DataLoaders_GERDAQ.getInstance();
			System.out.println(d.hashCode());
			d =  DataLoaders_GERDAQ.getInstance();
			System.out.println(d.hashCode());
			d =  DataLoaders_GERDAQ.getInstance();
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
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws NumberFormatException 
	 */
	public static DataLoaders_GERDAQ getInstance() throws CompressorException, IOException, NumberFormatException, SAXException, ParserConfigurationException {
		if(instance == null) {
			synchronized(DataLoaders_GERDAQ.class) {
				instance = new DataLoaders_GERDAQ();
			}
	    }
		return (DataLoaders_GERDAQ) instance;
	}
	
	
	public DataLoaders_GERDAQ() throws CompressorException, IOException, NumberFormatException, SAXException, ParserConfigurationException{
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
	private void loadDocsContent() throws NumberFormatException, IOException, SAXException, ParserConfigurationException {
		String filesNames[] = {"gerdaq_trainingA","gerdaq_trainingB","gerdaq_devel","gerdaq_test"};
		for(String ff : filesNames){
			File inputFile = new File("/home/joao/datasets/gerdaq/"+ff+".xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("instance");
			for (int temp = 0; temp < nList.getLength(); temp++) {
//	       	Thread.sleep(10000);
				Node nNode = nList.item(temp);
				String text = nNode.getTextContent().trim();
				String docId = text.toLowerCase();
				docId = docId.replaceAll(" ", "_");
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				Charset.forName("UTF-8").encode(text);
		   		docsContent.put(docId, text);
			}
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
	private static void loadDocFrequencyMap() throws CompressorException, IOException{
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/document_frequency.tsv"),StandardCharsets.UTF_8));
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
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/doc_word_count.tsv"),StandardCharsets.UTF_8));
		String line ="";
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll(" ", "_");
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
	private static void loadNumMentionsRecognized() throws CompressorException, IOException {
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/num_recognized_mentions.tsv"),StandardCharsets.UTF_8));
		String line ="";
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll(" ", "_");
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
	private static void loadGT() throws IOException{
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/gerdaq_GT.tsv"),StandardCharsets.UTF_8));
		String line = "";
		while((line = bffReader.readLine() )!= null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll(" ", "_");
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
	
//		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/gerdaq_training.GT.tsv"),StandardCharsets.UTF_8));
//		line = "";
//		while((line = bffReader.readLine() )!= null) {
//			String[] elems = line.split("\t");
//			String docId = elems[0].toLowerCase();
//			docId = docId.replaceAll(" ", "_");
//			docId = docId.replaceAll("\'", "");
//			docId = docId.replaceAll("\"", "");
//			String mention = elems[1].toLowerCase();
//			String offset = elems[2];
//			String link = elems[3].toLowerCase();
//			String key = docId+"\t"+mention+"\t"+offset;
//			String value = link.toLowerCase();
//			if(!value.contains("--nme--")){
//				GT_MAP_train.put(key,value);	
//			}
//		}
//		bffReader.close();
		System.out.println("TRAIN :"+GT_MAP_train.keySet().size());
		
		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/gerdaq_test.GT.tsv"),StandardCharsets.UTF_8));
		line = "";
		while((line = bffReader.readLine() )!= null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll(" ", "_");
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
		System.out.println("TEST GT :"+GT_MAP_test.keySet().size());
	}
	
	
	/**
	 * 
	 *  This method loads the annotations created by the  NEL tools
	 *
	 * @throws IOException
	 */
	private static void loadMappings() throws IOException{
		
		BufferedReader bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/mappings/gerdaq.ambiverse.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/mappings/gerdaq.babelfy.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/mappings/gerdaq.tagme.mappings"),StandardCharsets.UTF_8));
	
		String line="";
		
		while ((line = bffReaderAmbiverse.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length >=4){
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll(" ", "_");
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
				docId = docId.replaceAll(" ", "_");
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
				String offset =  elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_"," ").toLowerCase();
				babelMap.put(docId+"\t"+mention+"\t"+offset,entity);
			}
		}
//	
		System.out.println("TOTAL Bab annotations :"+babelMap.keySet().size());
		
		line="";
		while ((line = bffReaderTagme.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length >=4){
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll(" ", "_");
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
		
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();
		
//		//////// TRAIN
		bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/mappings/gerdaq_ambiverse_train.mappings"),StandardCharsets.UTF_8));
		bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/mappings/gerdaq_bfy_train.mappings"),StandardCharsets.UTF_8));
		bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/mappings/gerdaq_tagme_train.mappings"),StandardCharsets.UTF_8));
	
		line="";
		while ((line = bffReaderAmbiverse.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length >=4){
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll(" ", "_");
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
				docId = docId.replaceAll(" ", "_");
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
				docId = docId.replaceAll(" ", "_");
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
		
		/////// TEST 
		bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/mappings/gerdaq_ambiverse_test.mappings"),StandardCharsets.UTF_8));
		bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/mappings/gerdaq_bfy_test.mappings"),StandardCharsets.UTF_8));
		bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/mappings/gerdaq_tagme_test.mappings"),StandardCharsets.UTF_8));
		
		line="";
		while ((line = bffReaderAmbiverse.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length >=4){
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll(" ", "_");
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
				docId = docId.replaceAll(" ", "_");
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
				docId = docId.replaceAll(" ", "_");
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

		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();
	
	}
	
	/***************************************************************************************************
	 # # # DUMPERS START FROM HERE # # # 
	/**************************************************************************************************/
	
	/**
	 * 	This utility function is meant to fetch the number of words per document in the gerdaq corpus.
	 * 
	 * 	It produces the file:
	 * 						/home/joao/datasets/gerdaq/doc_word_count.tsv
	 * 
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	
	public static void dumpWordCountFromGERDAQ() throws NumberFormatException, IOException, ParserConfigurationException, SAXException{
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/gerdaq/doc_word_count.tsv"),StandardCharsets.UTF_8);
		String filesNames[] = {"gerdaq_trainingA","gerdaq_trainingB","gerdaq_devel","gerdaq_test"};
		for(String ff : filesNames){
			File inputFile = new File("/home/joao/datasets/gerdaq/"+ff+".xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("instance");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				String text = nNode.getTextContent().trim().toLowerCase();
				String docId = text.trim().toLowerCase();
				docId = docId.replaceAll(" ", "_");
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				Charset.forName("UTF-8").encode(text);
				int wc = text.split("\\s+").length;			
		    	pAnn.write(docId+"\t"+wc+"\n");
			}
		}
		pAnn.flush();
		pAnn.close();
		System.out.println("...Finished dumping the Word Count Successfully.");
	}
	
	/**
	 *
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException 
	 */
	public static void dumpDocumentFrequencyFromGERDAQ() throws IOException, ParserConfigurationException, SAXException{
		String filesNames[] = {"gerdaq_trainingA","gerdaq_trainingB","gerdaq_devel","gerdaq_test"};
		HashSet<String> mentionsSET = new HashSet<String>();
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/gerdaq/document_frequency.tsv"),StandardCharsets.UTF_8);
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/gerdaq_GT.tsv"),StandardCharsets.UTF_8));
		String line = "";
		while((line = bffReader.readLine() )!= null) {
			String[] elems = line.split("\t");
			String mention = elems[1].toLowerCase();
			mentionsSET.add(mention);
		}
		bffReader.close();
		
		for (String mention : mentionsSET){
			int df = 0;
			for(String ff : filesNames){
				File inputFile = new File("/home/joao/datasets/gerdaq/"+ff+".xml");
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(inputFile);
				doc.getDocumentElement().normalize();
				NodeList nList = doc.getElementsByTagName("instance");
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					String text = nNode.getTextContent().trim().toLowerCase();
					Charset.forName("UTF-8").encode(text);
					if(text.contains(mention)){
						df+=1;
					}
				}
			}
			pAnn.write(mention+"\t"+df+"\n");
		}
		
		pAnn.close();
   	System.out.println("...Finished dumping the Document Frequency Count Successfully.");
	}
	
	/**
	 *	This utility function is meant to fetch the number of recognized mentions per document in the gerdaq corpus.
	 *	
	 *	It produces the file:
	 *
	 *			/home/joao/datasets/gerdaq/num_recognized_mentions.tsv
	 *
	 * @throws NumberFormatException 
	 * @throws IOException
	 * @throws CompressorException
	 */
	public static void dumpNumRecognizedMentionsFromGERDAQ() throws NumberFormatException, IOException{
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/gerdaq/num_recognized_mentions.tsv"),StandardCharsets.UTF_8);
		BufferedReader bff = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/gerdaq/gerdaq_GT.tsv"),StandardCharsets.UTF_8));
		TreeMap<String, Integer> hashTreeMap = new TreeMap<String, Integer>();
		String line="";
		while((line = bff.readLine()) != null){
			String[] elems = line.split("\t");
			String docId = elems[0].trim().toLowerCase();
			docId = docId.replaceAll(" ", "_");
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
			docId = docId.replaceAll(" ", "_");
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			pAnn.write(docId + "\t"+pair.getValue()+"\n");
		    it.remove(); // avoids a ConcurrentModificationException
		}
		pAnn.flush();
		pAnn.close();
		System.out.println("...Finished dumping the number of recognized mentions per document.");
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
	public static void setAmbiverseMap(TreeMap<String, String> ambiverseMap) {
		DataLoaders_GERDAQ.ambiverseMap = ambiverseMap;
	}
	public static TreeMap<String, String> getBabelMap() {
		return babelMap;
	}
	public static void setBabelMap(TreeMap<String, String> babelMap) {
		DataLoaders_GERDAQ.babelMap = babelMap;
	}
	public static void setTagmeMap(TreeMap<String, String> tagmeMap) {
		DataLoaders_GERDAQ.tagmeMap = tagmeMap;
	}
	public static void setAmbiverseMap_train(TreeMap<String, String> ambiverseMap_train) {
		DataLoaders_GERDAQ.ambiverseMap_train = ambiverseMap_train;
	}
	public static void setBabelMap_train(TreeMap<String, String> babelMap_train) {
		DataLoaders_GERDAQ.babelMap_train = babelMap_train;
	}
	public static void setTagmeMap_train(TreeMap<String, String> tagmeMap_train) {
		DataLoaders_GERDAQ.tagmeMap_train = tagmeMap_train;
	}
	
	public TreeMap<String,String> getDocsContent(){
		return docsContent;
	}
	public static void setAmbiverseMap_test(TreeMap<String, String> ambiverseMap_test) {
		DataLoaders_GERDAQ.ambiverseMap_test = ambiverseMap_test;
	}
	public static void setBabelMap_test(TreeMap<String, String> babelMap_test) {
		DataLoaders_GERDAQ.babelMap_test = babelMap_test;
	}
	public static void setTagmeMap_test(TreeMap<String, String> tagmeMap_test) {
		DataLoaders_GERDAQ.tagmeMap_test = tagmeMap_test;
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
		DataLoaders_GERDAQ.spotlightMap = spotlightMap;
	}
	public static void setSpotlightMap_train(TreeMap<String, String> spotlightMap_train) {
		DataLoaders_GERDAQ.spotlightMap_train = spotlightMap_train;
	}
	public static void setSpotlightMap_test(TreeMap<String, String> spotlightMap_test) {
		DataLoaders_GERDAQ.spotlightMap_test = spotlightMap_test;
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
	
	
	
	public static void dumpGT() throws Exception {
		//gerdaq_trainingA.xml
		OutputStreamWriter outTrainingA = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/gerdaq/gerdaq_trainingA.GT.tsv"),StandardCharsets.UTF_8);
		//gerdaq_trainingB.xml
		OutputStreamWriter outTrainingB = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/gerdaq/gerdaq_trainingB.GT.tsv"),StandardCharsets.UTF_8);
		//gerdaq_devel.xml
		OutputStreamWriter outDevel = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/gerdaq/gerdaq_devel.GT.tsv"),StandardCharsets.UTF_8);
		//gerdaq_test.xml
		OutputStreamWriter outTest = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/gerdaq/gerdaq_test.GT.tsv"),StandardCharsets.UTF_8);
		//gerdaq_All.xml
		OutputStreamWriter outComplete = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/gerdaq/gerdaq_GT.tsv"),StandardCharsets.UTF_8);

		
		List<org.aksw.gerbil.transfer.nif.Document> documentsTrainingA  = loadDocuments(new File("/home/joao/datasets/gerdaq/gerdaq_trainingA.xml"));
		System.out.println(documentsTrainingA.size());
		for (org.aksw.gerbil.transfer.nif.Document doc : documentsTrainingA) {
			String docName = doc.getDocumentURI();
			docName = docName.replaceAll("\\/","_");
			String text = doc.getText();
			List<Marking> markings = doc.getMarkings();
			for (Marking m : markings) {
				NamedEntity ne = (NamedEntity) m;
				Pattern pattern = Pattern.compile("\\((.*?)\\)$");
				Matcher matcher = pattern.matcher(ne.toString());
				if(matcher.find()) {
					String marking = matcher.group(1);
					String ms[] = marking.split(",");
					String offset = ms[0].trim();
					String length = ms[1].trim();
					String link = ms[2].trim();
					String url = link;
					url = url.replace("[http://dbpedia.org/resource/", "");
					url = url.replace("[http://de.dbpedia.org/resource/", "");
					url = url.replace("[http://en.wikipedia.org/wiki/", "");
					url = url.replace("[http://aksw.org/notInWiki/", "");
					url = url.replace("_", " ");
						
					String mention = text.substring(Integer.parseInt(offset),(Integer.parseInt(offset) + Integer.parseInt(length)));
					outTrainingA.write(docName + "\t" + mention + "\t" + offset + "\t" + url + "\n");
					outComplete.write(docName + "\t" + mention + "\t" + offset + "\t" + url + "\n");
					}
			}
		}
		outComplete.flush();
		
		outTrainingA.flush();
		outTrainingA.close();
		
		List<org.aksw.gerbil.transfer.nif.Document> documentsTrainingB  = loadDocuments(new File("/home/joao/datasets/gerdaq/gerdaq_trainingB.xml"));
		System.out.println(documentsTrainingB.size());
		for (org.aksw.gerbil.transfer.nif.Document doc : documentsTrainingB) {
			String docName = doc.getDocumentURI();
			docName = docName.replaceAll("\\/","_");
			String text = doc.getText();
			List<Marking> markings = doc.getMarkings();
			for (Marking m : markings) {
				NamedEntity ne = (NamedEntity) m;
				Pattern pattern = Pattern.compile("\\((.*?)\\)$");
				Matcher matcher = pattern.matcher(ne.toString());
				if(matcher.find()) {
					String marking = matcher.group(1);
					String ms[] = marking.split(",");
					String offset = ms[0].trim();
					String length = ms[1].trim();
					String link = ms[2].trim();
					String url = link;
					url = url.replace("[http://dbpedia.org/resource/", "");
					url = url.replace("[http://de.dbpedia.org/resource/", "");
					url = url.replace("[http://en.wikipedia.org/wiki/", "");
					url = url.replace("[http://aksw.org/notInWiki/", "");
					url = url.replace("_", " ");
						
					String mention = text.substring(Integer.parseInt(offset),(Integer.parseInt(offset) + Integer.parseInt(length)));
					outTrainingB.write(docName + "\t" + mention + "\t" + offset + "\t" + url + "\n");
					outComplete.write(docName + "\t" + mention + "\t" + offset + "\t" + url + "\n");
					}
			}
		}
		outComplete.flush();

		outTrainingB.flush();
		outTrainingB.close();
		
		
		List<org.aksw.gerbil.transfer.nif.Document> documentsDevel  = loadDocuments(new File("/home/joao/datasets/gerdaq/gerdaq_devel.xml"));
		System.out.println(documentsDevel.size());
		for (org.aksw.gerbil.transfer.nif.Document doc : documentsDevel) {
			String docName = doc.getDocumentURI();
			docName = docName.replaceAll("\\/","_");
			String text = doc.getText();
			List<Marking> markings = doc.getMarkings();
			for (Marking m : markings) {
				NamedEntity ne = (NamedEntity) m;
				Pattern pattern = Pattern.compile("\\((.*?)\\)$");
				Matcher matcher = pattern.matcher(ne.toString());
				if(matcher.find()) {
					String marking = matcher.group(1);
					String ms[] = marking.split(",");
					String offset = ms[0].trim();
					String length = ms[1].trim();
					String link = ms[2].trim();
					String url = link;
					url = url.replace("[http://dbpedia.org/resource/", "");
					url = url.replace("[http://de.dbpedia.org/resource/", "");
					url = url.replace("[http://en.wikipedia.org/wiki/", "");
					url = url.replace("[http://aksw.org/notInWiki/", "");
					url = url.replace("_", " ");
						
					String mention = text.substring(Integer.parseInt(offset),(Integer.parseInt(offset) + Integer.parseInt(length)));
					outDevel.write(docName + "\t" + mention + "\t" + offset + "\t" + url + "\n");
					outComplete.write(docName + "\t" + mention + "\t" + offset + "\t" + url + "\n");
					
					}
//				}
			}
		}
		outComplete.flush();
		
		outDevel.flush();
		outDevel.close();
		
		List<org.aksw.gerbil.transfer.nif.Document> documentsTest  = loadDocuments(new File("/home/joao/datasets/gerdaq/gerdaq_test.xml"));
		System.out.println(documentsTest.size());
		for (org.aksw.gerbil.transfer.nif.Document doc : documentsTest) {
			String docName = doc.getDocumentURI();
			docName = docName.replaceAll("\\/","_");
			String text = doc.getText();
			List<Marking> markings = doc.getMarkings();
			for (Marking m : markings) {
				NamedEntity ne = (NamedEntity) m;
				Pattern pattern = Pattern.compile("\\((.*?)\\)$");
				Matcher matcher = pattern.matcher(ne.toString());
				if(matcher.find()) {
					String marking = matcher.group(1);
					String ms[] = marking.split(",");
					String offset = ms[0].trim();
					String length = ms[1].trim();
					String link = ms[2].trim();
					String url = link;
					url = url.replace("[http://dbpedia.org/resource/", "");
					url = url.replace("[http://de.dbpedia.org/resource/", "");
					url = url.replace("[http://en.wikipedia.org/wiki/", "");
					url = url.replace("[http://aksw.org/notInWiki/", "");
					url = url.replace("_", " ");
						
					String mention = text.substring(Integer.parseInt(offset),(Integer.parseInt(offset) + Integer.parseInt(length)));
					outTest.write(docName + "\t" + mention + "\t" + offset + "\t" + url + "\n");
					outComplete.write(docName + "\t" + mention + "\t" + offset + "\t" + url + "\n");


					}
//				}
			}
		}
		outTest.flush();
		outTest.close();

		outComplete.flush();
		outComplete.close();
		
	}
	
    public static List<org.aksw.gerbil.transfer.nif.Document> loadDocuments(File filePath) throws Exception {
        List<org.aksw.gerbil.transfer.nif.Document> docs = new ArrayList<>();
        if (!filePath.exists()) {
            throw new Exception("The given file (" + filePath.getAbsolutePath() + ") is not existing.");
        }

        if (filePath.isDirectory()) {

            String directoryPath = filePath.getAbsolutePath();
            if (!directoryPath.endsWith(File.separator)) {
                directoryPath = directoryPath + File.separator;
            }

            for (File tmpFile : new File(directoryPath).listFiles()) {
                docs.addAll(createDocument(tmpFile));
            }

        } else {
            docs.addAll(createDocument(filePath));
        }
        return docs;
    }
	
    private static List<org.aksw.gerbil.transfer.nif.Document> createDocument(File file) throws Exception {
        List<org.aksw.gerbil.transfer.nif.Document> documents = new ArrayList<org.aksw.gerbil.transfer.nif.Document>();
        String documentUriStart = generateDocumentUri(name, file.getName());
        InputStream inputStream = null;
        InputSource is = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            is = new InputSource(inputStream);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            saxParser.parse(is, new DefaultHandler() {

                private StringBuilder text = new StringBuilder();
                private int markingStart;
                private String markingTitle;
                private List<Marking> markings;

                @Override
                public void startDocument() throws SAXException {
                    super.startDocument();
                }
                @Override
                public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
                    if (qName.equals(ANNOTATION_TAG)) {
                        markingTitle = atts.getValue("rank_0_title");
                        if (markingTitle != null) {
                            markingStart = text.length();
                        } else {
                            System.out.println("Found a marking without the necessary \"rank_0_title\" attribute.");
                        }
                        markingTitle = markingTitle.replace(' ', '_');
                    } else if (qName.equals(DOCUMENT_TAG)) {
                        this.markings = new ArrayList<>();
                    }
                }

                @Override
                public void characters(char[] ch, int start, int length) {
                    text.append(ch, start, length);
                }

                @Override
                public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
                    if (qName.equals(DOCUMENT_TAG)) {
                        documents.add(new DocumentImpl(text.toString(), documentUriStart + documents.size(), markings));
                        text.delete(0, text.length());
                    } else if (qName.equals(ANNOTATION_TAG) && (markingTitle != null)) {
                        markings.add(new NamedEntity(markingStart, text.length() - markingStart, new HashSet<String>(
                                Arrays.asList(DBPEDIA_URI + markingTitle, WIKIPEDIA_URI + markingTitle))));
                    }
                }
            });
        } catch (Exception e) {
            throw new Exception("Exception while reading dataset.");
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        return documents;
    }
    
	public void init() throws Exception  {
	        this.documents = loadDocuments(new File(file));
	}
    
	protected static String generateDocumentUri(String datasetName, String fileName) {
        StringBuilder builder = new StringBuilder();
        builder.append("http://");
        builder.append(datasetName.replace(' ', '_'));
        builder.append('/');
        builder.append(fileName);
        builder.append('_');
        return builder.toString();
    }
	
	
	
}
