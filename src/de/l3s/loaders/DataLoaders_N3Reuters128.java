package de.l3s.loaders;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.aksw.gerbil.io.nif.NIFParser;
import org.aksw.gerbil.io.nif.impl.TurtleNIFParser;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.apache.commons.compress.compressors.CompressorException;

import gnu.trove.map.hash.TObjectIntHashMap;
import oshi.jna.platform.mac.DiskArbitration.DASessionRef;

public class DataLoaders_N3Reuters128  extends DataLoaders {

	
	
	public static void main(String... args) throws NumberFormatException, IOException, CompressorException {
//		dumpGT();
//		dumpTextContent();
//		dumpNumRecognizedMentionsFromN3Reuters128 ();
//		dumpWordCountFromN3Reuters128 ();
//		dumpDocumentFrequencyFromN3Reuters128 ();
		
		DataLoaders_N3Reuters128 d = new DataLoaders_N3Reuters128();
		System.out.println(d.hashCode());
		
	}
	

	public static DataLoaders_N3Reuters128 getInstance() throws CompressorException, IOException {
		if(instance == null) {
			 synchronized(DataLoaders_N3Reuters128.class) {
				 instance = new DataLoaders_N3Reuters128();
			 }
	    }
		return  (DataLoaders_N3Reuters128) instance;
	}
	
	public DataLoaders_N3Reuters128() throws CompressorException, IOException {
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
// 		loadMentionEntityCountMap();
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
	private void loadDocsContent() throws IOException {
		String nifPath = "/home/joao/datasets/N3Reuters128/Reuters-128.ttl";
		StringBuffer nifBuffer = new StringBuffer();
		BufferedReader bffReader = new BufferedReader(	new InputStreamReader(new FileInputStream(nifPath), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			line = line.replaceAll("<E2><80><99>", "\'");
			line = line.replaceAll("<E2><80><9C>", "\"");
			line = line.replaceAll("<E2><80><9D>", "\"");
			nifBuffer.append(line);
			nifBuffer.append("\n");
		}
		bffReader.close();
		String nifString = nifBuffer.toString();
		NIFParser parser = new TurtleNIFParser();
		List<Document> listDocs = parser.parseNIF(nifString);
		for (Document d : listDocs) {
			String docName = d.getDocumentURI();
			docName = docName.replaceAll("\\/","_");
//			docName = "\'"+docName+  "\'.txt";
			String docContent = d.getText();
        	docsContent.put(docName, docContent);
        	
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
	private static void loadDocFrequencyMap() throws CompressorException, IOException {
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/N3Reuters128/document_frequency.tsv"), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
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
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/N3Reuters128/doc_word_count.tsv"), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
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
		BufferedReader bffReader = new BufferedReader(	new InputStreamReader(new FileInputStream("/home/joao/datasets/N3Reuters128/num_recognized_mentions.tsv"),StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String count = elems[1];
			NumRecogMentionMap.put(docId, Integer.parseInt(count));
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
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(	new FileInputStream("/home/joao/datasets/N3Reuters128/N3Reuters128_GT.tsv"), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String mention = elems[1].toLowerCase();
			String offset = elems[2];
			String key = docId + "\t" + mention + "\t" + offset;
			String value = elems[3].replace("_", " ").toLowerCase();
			if (!value.contains("*null*")) {
				GT_MAP.put(key, value);
			}
		}
		bffReader.close();
		System.out.println("#GT annotations :" + GT_MAP.keySet().size());
		
	}
	
	/**
	 * 
	 *  This method loads the annotations created by the  NEL tools
	 *
	 * @throws IOException
	 */
	private static void loadMappings() throws IOException {
		BufferedReader bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/N3Reuters128/mappings/N3Reuters128.ambiverse.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/N3Reuters128/mappings/N3Reuters128.babelfy.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderTagme = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/N3Reuters128/mappings/N3Reuters128.tagme.mappings"),StandardCharsets.UTF_8));

		String line = "";

		while ((line = bffReaderAmbiverse.readLine()) != null) {
			String[] elems = line.split("\t");
			if (elems.length >= 4) {
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
				String offset = elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_", " ").toLowerCase();
				if (!entity.equalsIgnoreCase("null")) {
					entity = entity.replaceAll("_", " ").toLowerCase();
					ambiverseMap.put(docId + "\t" + mention + "\t" + offset, entity);
				}
			}
		}
		System.out.println("TOTAL Amb annotations :" + ambiverseMap.keySet().size());

		line = "";
		while ((line = bffReaderBabelfy.readLine()) != null) {
			String[] elems = line.split("\t");
			if (elems.length >= 4) {
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
				String offset = elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_", " ").toLowerCase();
				if (!entity.equalsIgnoreCase("null")) {
					entity = entity.replaceAll("_", " ").toLowerCase();
					babelMap.put(docId + "\t" + mention + "\t" + offset, entity);
				}
			}
		}
		System.out.println("TOTAL Bab annotations :" + babelMap.keySet().size());

		line = "";
		while ((line = bffReaderTagme.readLine()) != null) {
			String[] elems = line.split("\t");
			if (elems.length >= 4) {
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
				docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
				String offset = elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_", " ").toLowerCase();
				if (!entity.equalsIgnoreCase("null")) {
					entity = entity.replaceAll("_", " ").toLowerCase();
					tagmeMap.put(docId + "\t" + mention + "\t" + offset, entity);
				}
			}
		}
		System.out.println("TOTAL Tag annotations :" + tagmeMap.keySet().size());
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();
	}
	
	

	/***************************************************************************************************
	 # # # DUMPERS START FROM HERE # # # 
	/**************************************************************************************************/	
	/**
	 *  This utility function is meant to fetch the number of words per document 
	 * 
	 * 	It produces the file:
	 * 						/home/joao/datasets/N3Reuters128/doc_word_count.tsv
	 * 
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	
	private static void dumpWordCountFromN3Reuters128() throws IOException {
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/N3Reuters128/doc_word_count.tsv"), StandardCharsets.UTF_8);
		String nifPath = "/home/joao/datasets/N3Reuters128/Reuters-128.ttl";
		StringBuffer nifBuffer = new StringBuffer();
		BufferedReader bffReader = new BufferedReader(	new InputStreamReader(new FileInputStream(nifPath), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			line = line.replaceAll("<E2><80><99>", "\'");
			line = line.replaceAll("<E2><80><9C>", "\"");
			line = line.replaceAll("<E2><80><9D>", "\"");
			nifBuffer.append(line);
			nifBuffer.append("\n");
		}
		bffReader.close();
		String nifString = nifBuffer.toString();
		NIFParser parser = new TurtleNIFParser();
		List<Document> listDocs = parser.parseNIF(nifString);
		for (Document d : listDocs) {
			String docName = d.getDocumentURI();
			docName = docName.replaceAll("\\/","_");
//			docName = "\'"+docName+  "\'.txt";
			String docContent = d.getText();
			int wc = docContent.split("\\s+").length;
			pAnn.write(docName + "\t" + wc + "\n");
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
	private static void dumpDocumentFrequencyFromN3Reuters128() throws IOException {
		HashSet<String> mentionsSET = new HashSet<String>();
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/N3Reuters128/document_frequency.tsv"), StandardCharsets.UTF_8);
		TreeMap<String, String> docsContentMap = new TreeMap<String, String>();
		String nifPath = "/home/joao/datasets/N3Reuters128/Reuters-128.ttl";
		StringBuffer nifBuffer = new StringBuffer();
		BufferedReader bffReader = new BufferedReader(	new InputStreamReader(new FileInputStream(nifPath), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			line = line.replaceAll("<E2><80><99>", "\'");
			line = line.replaceAll("<E2><80><9C>", "\"");
			line = line.replaceAll("<E2><80><9D>", "\"");
			nifBuffer.append(line);
			nifBuffer.append("\n");
		}
		bffReader.close();
		String nifString = nifBuffer.toString();
		NIFParser parser = new TurtleNIFParser();
		List<Document> listDocs = parser.parseNIF(nifString);
		for (Document d : listDocs) {
			String docName = d.getDocumentURI();
			docName = docName.replaceAll("\\/","_");
//			docName = "\'"+docName+  "\'.txt";
			String docContent = d.getText();
			docsContentMap.put(docName,docContent);
		}
		BufferedReader bff = new BufferedReader(new InputStreamReader(	new FileInputStream("/home/joao/datasets/N3Reuters128/N3Reuters128_GT.tsv"), StandardCharsets.UTF_8));
		line = "";
		while ((line = bff.readLine()) != null) {
			String[] elems = line.split("\t");
			String mention = elems[1].trim().toLowerCase();
			mentionsSET.add(mention);
		}
		bff.close();
		for (String mention : mentionsSET) {
			int df = 0;
			Iterator<?> it = docsContentMap.entrySet().iterator();
			while (it.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry pair = (Map.Entry) it.next();
				String articlecontent = (String) pair.getValue();
				if (articlecontent.toLowerCase().contains(mention)) {
					df += 1;
				}
			}
			pAnn.write(mention + "\t" + df + "\n");
		}
		pAnn.flush();
		pAnn.close();
		System.out.println("...Finished dumping the Document Frequency Count Successfully.");
	}
	/**
	 *	This utility function is meant to fetch the number of recognized mentions per document in the N3Reuters128 corpus.
	 *	
	 *	It produces the file:
	 *
	 *			/home/joao/datasets/N3Reuters128/num_recognized_mentions.tsv
	 *
	 * @throws NumberFormatException 
	 * @throws IOException
	 * @throws CompressorException
	 */
	private static void dumpNumRecognizedMentionsFromN3Reuters128() throws IOException {
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/N3Reuters128/num_recognized_mentions.tsv"),StandardCharsets.UTF_8);
		BufferedReader bff = new BufferedReader(new InputStreamReader(	new FileInputStream("/home/joao/datasets/N3Reuters128/N3Reuters128_GT.tsv"), StandardCharsets.UTF_8));
		TreeMap<String, Integer> hashTreeMap = new TreeMap<String, Integer>();
		String line = "";
		while ((line = bff.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].trim().toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			Integer count = hashTreeMap.get(docId);
			if (count == null) {
				count = 1;
				hashTreeMap.put(docId, count);
			} else {
				count += 1;
				hashTreeMap.put(docId, count);
			}
		}
		bff.close();
		@SuppressWarnings("rawtypes")
		Iterator it = hashTreeMap.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry) it.next();
			String docId = ((String) pair.getKey()).toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			pAnn.write(docId + "\t" + pair.getValue() + "\n");
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
	private static void dumpTextContent() throws NumberFormatException, IOException {
		String nifPath = "/home/joao/datasets/N3Reuters128/Reuters-128.ttl";
		StringBuffer nifBuffer = new StringBuffer();
		BufferedReader bffReader = new BufferedReader(	new InputStreamReader(new FileInputStream(nifPath), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			line = line.replaceAll("<E2><80><99>", "\'");
			line = line.replaceAll("<E2><80><9C>", "\"");
			line = line.replaceAll("<E2><80><9D>", "\"");
			nifBuffer.append(line);
			nifBuffer.append("\n");
		}
		bffReader.close();
		String nifString = nifBuffer.toString();
		NIFParser parser = new TurtleNIFParser();
		List<Document> listDocs = parser.parseNIF(nifString);
		for (Document d : listDocs) {
			String docName = d.getDocumentURI();
			docName = docName.replaceAll("\\/","_");
//			docName = "\'"+docName+  "\'.txt";
			String docContent = d.getText();
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/N3Reuters128/TEXT_FILES/"+docName), StandardCharsets.UTF_8);
        	out.write(docContent);
        	out.flush();
        	out.close();
		}
	}
	
	
	/**
	 * Utility funtion to dump the GT
	 * 
	 */
	public static void dumpGT() throws IOException {
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/N3Reuters128/N3Reuters128_GT.tsv"), StandardCharsets.UTF_8);
		String nifPath =  "/home/joao/datasets/N3Reuters128/Reuters-128.ttl";

		StringBuffer nifBuffer = new StringBuffer();
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream(nifPath), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			line = line.replaceAll("<E2><80><99>", "\'");
			line = line.replaceAll("<E2><80><9C>", "\"");
			line = line.replaceAll("<E2><80><9D>", "\"");
			nifBuffer.append(line);
			nifBuffer.append("\n");
		}
		bffReader.close();
		String nifString = nifBuffer.toString();
		NIFParser parser = new TurtleNIFParser();
		List<Document> listDocs = parser.parseNIF(nifString);
		for (Document d : listDocs) {
			String docName = d.getDocumentURI();
			docName = docName.replaceAll("\\/","_");
			String docContent = d.getText();
			List<Marking> markings = d.getMarkings();
			for (Marking m : markings) {
				NamedEntity ne = (NamedEntity) m;
				Pattern pattern = Pattern.compile("\\((.*?)\\)");
				Matcher matcher = pattern.matcher(ne.toString());
				while (matcher.find()) {
					String marking = matcher.group(1);
					String ms[] = marking.split(",");
					String offset = ms[0].trim();
					String length = ms[1].trim();
					String link = ms[2].trim();

					Pattern pattern2 = Pattern.compile("\\[(.*?)\\]");
					Matcher matcher2 = pattern2.matcher(link);
					while (matcher2.find()) {
						String url = matcher2.group(1);
						url = url.replace("http://dbpedia.org/resource/", "");
						url = url.replace("http://de.dbpedia.org/resource/", "");
						url = url.replace("http://aksw.org/notInWiki/", "");
						url = url.replace("_", " ");
	                	url =  URLDecoder.decode(url, "UTF-8");
	                	url = url.replaceAll("<E2><80><99>", "\'");
	                	url = url.replaceAll("<E2><80><9C>", "\"");
	                	url = url.replaceAll("<E2><80><9D>", "\"");
	        			
						String mention = docContent.substring(Integer.parseInt(offset),
								(Integer.parseInt(offset) + Integer.parseInt(length)));
						out.write(docName + "\t" + mention + "\t" + offset + "\t" + url + "\n");

					}

				}

			}
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
	
	public static void setAmbiverseMap(TreeMap<String, String> ambiverseMap) {
		DataLoaders_N3Reuters128.ambiverseMap = ambiverseMap;
	}
	public TreeMap<String, String> getBabelMap() {
		return babelMap;
	}
	public static void setBabelMap(TreeMap<String, String> babelMap) {
		DataLoaders_N3Reuters128.babelMap = babelMap;
	}

	public static void setTagmeMap(TreeMap<String, String> tagmeMap) {
		DataLoaders_N3Reuters128.tagmeMap = tagmeMap;
	}
	public static void setAmbiverseMap_train(TreeMap<String, String> ambiverseMap_train) {
		DataLoaders_N3Reuters128.ambiverseMap_train = ambiverseMap_train;
	}
	public static void setBabelMONLLap_train(TreeMap<String, String> babelMap_train) {
		DataLoaders_N3Reuters128.babelMap_train = babelMap_train;
	}
	public static void setTagmeMap_train(TreeMap<String, String> tagmeMap_train) {
		DataLoaders_N3Reuters128.tagmeMap_train = tagmeMap_train;
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

	public static void setAmbiverseMap_test(TreeMap<String, String> ambiverseMap_test) {
		DataLoaders_N3Reuters128.ambiverseMap_test = ambiverseMap_test;
	}

	public static void setBabelMap_test(TreeMap<String, String> babelMap_test) {
		DataLoaders_N3Reuters128.babelMap_test = babelMap_test;
	}

	public static void setTagmeMap_test(TreeMap<String, String> tagmeMap_test) {
		DataLoaders_N3Reuters128.tagmeMap_test = tagmeMap_test;
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
		DataLoaders_N3Reuters128.spotlightMap = spotlightMap;
	}
	public static void setSpotlightMap_train(TreeMap<String, String> spotlightMap_train) {
		DataLoaders_N3Reuters128.spotlightMap_train = spotlightMap_train;
	}
	public static void setSpotlightMap_test(TreeMap<String, String> spotlightMap_test) {
		DataLoaders_N3Reuters128.spotlightMap_test = spotlightMap_test;
	}
}
