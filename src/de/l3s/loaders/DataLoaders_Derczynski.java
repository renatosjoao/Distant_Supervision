package de.l3s.loaders;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.compress.compressors.CompressorException;
import org.xml.sax.SAXException;
import gnu.trove.map.hash.TObjectIntHashMap;

public class DataLoaders_Derczynski extends DataLoaders {

	public static void main(String[] args)	throws NumberFormatException, IOException, ParserConfigurationException, SAXException, CompressorException {
		DataLoaders_Derczynski d = new DataLoaders_Derczynski();
		//		dumpGT();
//		dumpTextContent();
//		dumpNumRecognizedMentionsFromDerczynski();
//		dumpWordCountFromDerczynski();
//		dumpDocumentFrequencyFromDerczynski();
//		loadGT();
	}

	public static DataLoaders_Derczynski getInstance() throws CompressorException, IOException {
		if (instance == null) {
			synchronized (DataLoaders_Derczynski.class) {
				instance = new DataLoaders_Derczynski();
			}
		}
		return (DataLoaders_Derczynski) instance;
	}

	public DataLoaders_Derczynski() throws CompressorException, IOException {
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
		loadNumMentionsRecognized();
		loadGT();
		loadMappings();
		loadDocsContent();

	}

	/***************************************************************************************************
	 * # # # LOADERS START FROM HERE # # #
	 ***************************************************************************************************/
	/**
	 * This method loads the documents contents into a map
	 *
	 * @throws IOException
	 * @throws NumberFormatException
	 * 
	 **/

	private static TreeMap<String, String> loadDocsContent() throws IOException {

		BufferedReader bffReader = new BufferedReader(new InputStreamReader(
				new FileInputStream("/home/joao/datasets/derczynski/ipm_nel.conll"), StandardCharsets.UTF_8));
		String line = null;
		int c = 0;
		StringBuffer fileContent = new StringBuffer();
		while ((line = bffReader.readLine()) != null) {
			if (line.equalsIgnoreCase("\t\t\t")) {
				String textContent = fileContent.toString();
				textContent = textContent.replaceAll("\\s+", " ");
				textContent = textContent.replaceAll(" \\.", ".");
				textContent = textContent.replaceAll(" \\,", ",");
				textContent = textContent.replaceAll(" \\!", "!");
				textContent = textContent.replaceAll(" \\.\\.\\.", "...");
				textContent = textContent.replaceAll(" \\'s", "'s");
				textContent = textContent.replaceAll(" \\'m", "'m");
				textContent = textContent.replaceAll(" \\'re", "''re");
				textContent = textContent.replaceAll(" \\'ve", "'ve");
				textContent = textContent.replaceAll("\\( ", "(");
				textContent = textContent.replaceAll(" \\)", ")");
				textContent = textContent.replaceAll(" \\:", ":");
				textContent = textContent.replaceAll("&amp;", "&");
				textContent = textContent.replaceAll("\\@ ", "@");
				textContent = textContent.replaceAll(" \\?\\?", "??");
				textContent = textContent.replaceAll(" \\?", "?");
				textContent = textContent.replaceAll("&lt;", "<");
				textContent = textContent.replaceAll("\\[ ", "[");
				textContent = textContent.replaceAll(" \\]", "]");
				textContent = textContent.replaceAll("\\$ ", "\\$");
				textContent = textContent.replaceAll("~ ", "~");
				textContent = textContent.replaceAll(" \\.", ".");

				String docId = "tw_" + c + ".txt";
				c++;
				docsContent.put(docId, textContent);

				fileContent = new StringBuffer();
			} else {
				String split[] = line.split("\t");
				fileContent.append(split[0]);
				fileContent.append(" ");
			}
		}
		// last document
		String textContent = fileContent.toString();
		String docId = "tw_" + c + ".txt";
		docsContent.put(docId, textContent);
		bffReader.close();
		System.out.println("# documents in the text dir/ :" + docsContent.keySet().size());

		return docsContent;
	}

	/**
	 *
	 * This method is used to load the document frequency map.
	 *
	 * @throws CompressorException
	 * @throws IOException
	 */
	private static void loadDocFrequencyMap() throws CompressorException, IOException {
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(
				new FileInputStream("/home/joao/datasets/derczynski/document_frequency.tsv"), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String mention = elems[0].toLowerCase();
			String freq = elems[1];
			Integer df = Integer.parseInt(freq);
			DocFrequencyMap.put(mention, df);
		}
		bffReader.close();
		System.out.println("Loaded Document Frequency Map.");
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
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(
				new FileInputStream("/home/joao/datasets/derczynski/doc_word_count.tsv"), StandardCharsets.UTF_8));
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
		BufferedReader bffReader = new BufferedReader(
				new InputStreamReader(new FileInputStream("/home/joao/datasets/derczynski/num_recognized_mentions.tsv"),
						StandardCharsets.UTF_8));
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
	private static void loadGT() throws IOException {
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/derczynski/derczynski_GT.tsv"), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length >=4) {
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String mention = elems[1].toLowerCase();
			String offset = elems[2];
			String key = docId + "\t" + mention + "\t" + offset;
//			System.out.println(key);
			String value = elems[3];
			if(value != null) {
				value = elems[3].replace("_", " ").toLowerCase();
				if (!value.contains("NIL")) {
					GT_MAP.put(key, value);
				}	
			}
			
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
		BufferedReader bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/derczynski/mappings/derczynski.ambiverse.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/derczynski/mappings/derczynski.babelfy.mappings"),	StandardCharsets.UTF_8));
		BufferedReader bffReaderTagme = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/derczynski/mappings/derczynski.tagme.mappings"),StandardCharsets.UTF_8));

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

		bffReaderAmbiverse.close();
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

		bffReaderBabelfy.close();
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

		bffReaderTagme.close();

	}

	/***************************************************************************************************
	 # # # DUMPERS START FROM HERE # # # 
	/**************************************************************************************************/	
	/**
	 *  This utility function is meant to fetch the number of words per document in
	 * 
	 * 	It produces the file:
	 * 						/home/joao/datasets/derczynski/doc_word_count.tsv
	 * 
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static void dumpWordCountFromDerczynski() throws IOException {
		OutputStreamWriter pAnn = new OutputStreamWriter(
				new FileOutputStream("/home/joao/datasets/derczynski/doc_word_count.tsv"), StandardCharsets.UTF_8);

		BufferedReader bffReader = new BufferedReader(new InputStreamReader(
				new FileInputStream("/home/joao/datasets/derczynski/ipm_nel.conll"), StandardCharsets.UTF_8));
		String line = null;
		int c = 0;
		StringBuffer fileContent = new StringBuffer();
		while ((line = bffReader.readLine()) != null) {
			if (line.equalsIgnoreCase("\t\t\t")) {
				String textContent = fileContent.toString();
				textContent = textContent.replaceAll("\\s+", " ");
				textContent = textContent.replaceAll(" \\.", ".");
				textContent = textContent.replaceAll(" \\,", ",");
				textContent = textContent.replaceAll(" \\!", "!");
				textContent = textContent.replaceAll(" \\.\\.\\.", "...");
				textContent = textContent.replaceAll(" \\'s", "'s");
				textContent = textContent.replaceAll(" \\'m", "'m");
				textContent = textContent.replaceAll(" \\'re", "''re");
				textContent = textContent.replaceAll(" \\'ve", "'ve");
				textContent = textContent.replaceAll("\\( ", "(");
				textContent = textContent.replaceAll(" \\)", ")");
				textContent = textContent.replaceAll(" \\:", ":");
				textContent = textContent.replaceAll("&amp;", "&");
				textContent = textContent.replaceAll("\\@ ", "@");
				textContent = textContent.replaceAll(" \\?\\?", "??");
				textContent = textContent.replaceAll(" \\?", "?");
				textContent = textContent.replaceAll("&lt;", "<");
				textContent = textContent.replaceAll("\\[ ", "[");
				textContent = textContent.replaceAll(" \\]", "]");
				textContent = textContent.replaceAll("\\$ ", "\\$");
				textContent = textContent.replaceAll("~ ", "~");
				textContent = textContent.replaceAll(" \\.", ".");

				String docId = "tw_" + c + ".txt";
				c++;
				int wc = textContent.split("\\s+").length;
				pAnn.write(docId + "\t" + wc + "\n");

				fileContent = new StringBuffer();
			} else {
				String split[] = line.split("\t");
				fileContent.append(split[0]);
				fileContent.append(" ");
			}
		}
		// last document
		String textContent = fileContent.toString();
		String docId = "tw_" + c + ".txt";
		int wc = textContent.split("\\s+").length;
		pAnn.write(docId + "\t" + wc + "\n");
		bffReader.close();

		pAnn.flush();
		pAnn.close();
		System.out.println("...Finished dumping the Word Count Successfully.");
	}
	/**
	 * This utility function is meant to dump the document frequency for all mentions in the files  
	 * 
	 */
	private static void dumpDocumentFrequencyFromDerczynski() throws IOException {
		HashSet<String> mentionsSET = new HashSet<String>();
		TreeMap<String, String> docsContentMap = new TreeMap<String, String>();
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/derczynski/document_frequency.tsv"), StandardCharsets.UTF_8);
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/derczynski/ipm_nel.conll"), StandardCharsets.UTF_8));
		String line = null;
		int c = 0;
		StringBuffer fileContent = new StringBuffer();
		while ((line = bffReader.readLine()) != null) {
			if (line.equalsIgnoreCase("\t\t\t")) {
				String textContent = fileContent.toString();
				textContent = textContent.replaceAll("\\s+", " ");
				textContent = textContent.replaceAll(" \\.", ".");
				textContent = textContent.replaceAll(" \\,", ",");
				textContent = textContent.replaceAll(" \\!", "!");
				textContent = textContent.replaceAll(" \\.\\.\\.", "...");
				textContent = textContent.replaceAll(" \\'s", "'s");
				textContent = textContent.replaceAll(" \\'m", "'m");
				textContent = textContent.replaceAll(" \\'re", "''re");
				textContent = textContent.replaceAll(" \\'ve", "'ve");
				textContent = textContent.replaceAll("\\( ", "(");
				textContent = textContent.replaceAll(" \\)", ")");
				textContent = textContent.replaceAll(" \\:", ":");
				textContent = textContent.replaceAll("&amp;", "&");
				textContent = textContent.replaceAll("\\@ ", "@");
				textContent = textContent.replaceAll(" \\?\\?", "??");
				textContent = textContent.replaceAll(" \\?", "?");
				textContent = textContent.replaceAll("&lt;", "<");
				textContent = textContent.replaceAll("\\[ ", "[");
				textContent = textContent.replaceAll(" \\]", "]");
				textContent = textContent.replaceAll("\\$ ", "\\$");
				textContent = textContent.replaceAll("~ ", "~");
				textContent = textContent.replaceAll(" \\.", ".");

				String docId = "tw_" + c + ".txt";
				c++;
				docsContentMap.put(docId, textContent);

				fileContent = new StringBuffer();
			} else {
				String split[] = line.split("\t");
				fileContent.append(split[0]);
				fileContent.append(" ");
			}
		}
		// last document
		String textContent = fileContent.toString();
		String docId = "tw_" + c + ".txt";
		docsContentMap.put(docId, textContent);
		bffReader.close();

		BufferedReader bff = new BufferedReader(new InputStreamReader(
				new FileInputStream("/home/joao/datasets/derczynski/derczynski_GT.tsv"), StandardCharsets.UTF_8));
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
	 *	This utility function is meant to fetch the number of recognized mentions per document in the derczynski corpus.
	 *	
	 *	It produces the file:
	 *
	 *			/home/joao/datasets/derczynski/num_recognized_mentions.tsv
	 *
	 * @throws NumberFormatException 
	 * @throws IOException
	 * @throws CompressorException
	 */
	private static void dumpNumRecognizedMentionsFromDerczynski() throws IOException {
		OutputStreamWriter pAnn = new OutputStreamWriter(
				new FileOutputStream("/home/joao/datasets/derczynski/num_recognized_mentions.tsv"),
				StandardCharsets.UTF_8);
		BufferedReader bff = new BufferedReader(new InputStreamReader(
				new FileInputStream("/home/joao/datasets/derczynski/derczynski_GT.tsv"), StandardCharsets.UTF_8));
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
	 *
	 * @throws IOException
	 */
	public static void dumpTextContent() throws IOException {
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(
				new FileInputStream("/home/joao/datasets/derczynski/ipm_nel.conll"), StandardCharsets.UTF_8));
		String line = null;
		int c = 0;
		StringBuffer fileContent = new StringBuffer();
		while ((line = bffReader.readLine()) != null) {
			if (line.equalsIgnoreCase("\t\t\t")) {
				String textContent = fileContent.toString();
				textContent = textContent.replaceAll("\\s+", " ");
				textContent = textContent.replaceAll(" \\.", ".");
				textContent = textContent.replaceAll(" \\,", ",");
				textContent = textContent.replaceAll(" \\!", "!");
				textContent = textContent.replaceAll(" \\.\\.\\.", "...");
				textContent = textContent.replaceAll(" \\'s", "'s");
				textContent = textContent.replaceAll(" \\'m", "'m");
				textContent = textContent.replaceAll(" \\'re", "''re");
				textContent = textContent.replaceAll(" \\'ve", "'ve");
				textContent = textContent.replaceAll("\\( ", "(");
				textContent = textContent.replaceAll(" \\)", ")");
				textContent = textContent.replaceAll(" \\:", ":");
				textContent = textContent.replaceAll("&amp;", "&");
				textContent = textContent.replaceAll("\\@ ", "@");
				textContent = textContent.replaceAll(" \\?\\?", "??");
				textContent = textContent.replaceAll(" \\?", "?");
				textContent = textContent.replaceAll("&lt;", "<");
				textContent = textContent.replaceAll("\\[ ", "[");
				textContent = textContent.replaceAll(" \\]", "]");
				textContent = textContent.replaceAll("\\$ ", "\\$");
				textContent = textContent.replaceAll("~ ", "~");
				textContent = textContent.replaceAll(" \\.", ".");

				OutputStreamWriter out = new OutputStreamWriter(
						new FileOutputStream("/home/joao/datasets/derczynski/TEXT_FILES/tw_" + c + ".txt"),
						StandardCharsets.UTF_8);
				c++;
				out.write(textContent);
				out.flush();
				out.close();
				fileContent = new StringBuffer();
			} else {
				String split[] = line.split("\t");
				fileContent.append(split[0]);
				fileContent.append(" ");
			}
		}
		// last document
		OutputStreamWriter out = new OutputStreamWriter(
				new FileOutputStream("/home/joao/datasets/derczynski/TEXT_FILES/tw_" + c + ".txt"),
				StandardCharsets.UTF_8);
		out.write(fileContent.toString());
		out.flush();
		out.close();
		bffReader.close();
	}


	/**
	 *
	 * @throws IOException
	 */
	public static void dumpGT() throws IOException {
		OutputStreamWriter out = new OutputStreamWriter(
				new FileOutputStream("/home/joao/datasets/derczynski/derczynski_GT.tsv"), StandardCharsets.UTF_8);
		LinkedList<String> TweetsTexts = new LinkedList<String>();
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(
				new FileInputStream("/home/joao/datasets/derczynski/ipm_nel.conll"), StandardCharsets.UTF_8));
		String line = null;
		int c = 0;
		StringBuffer fileContent = new StringBuffer();
		while ((line = bffReader.readLine()) != null) {
			if (line.equalsIgnoreCase("\t\t\t")) {
				String textContent = fileContent.toString();
				textContent = textContent.replaceAll("\\s+", " ");
				textContent = textContent.replaceAll(" \\.", ".");
				textContent = textContent.replaceAll(" \\,", ",");
				textContent = textContent.replaceAll(" \\!", "!");
				textContent = textContent.replaceAll(" \\.\\.\\.", "...");
				textContent = textContent.replaceAll(" \\'s", "'s");
				textContent = textContent.replaceAll(" \\'m", "'m");
				textContent = textContent.replaceAll(" \\'re", "''re");
				textContent = textContent.replaceAll(" \\'ve", "'ve");
				textContent = textContent.replaceAll("\\( ", "(");
				textContent = textContent.replaceAll(" \\)", ")");
				textContent = textContent.replaceAll(" \\:", ":");
				textContent = textContent.replaceAll("&amp;", "&");
				textContent = textContent.replaceAll("\\@ ", "@");
				textContent = textContent.replaceAll(" \\?\\?", "??");
				textContent = textContent.replaceAll(" \\?", "?");
				textContent = textContent.replaceAll("&lt;", "<");
				textContent = textContent.replaceAll("\\[ ", "[");
				textContent = textContent.replaceAll(" \\]", "]");
				textContent = textContent.replaceAll("\\$ ", "\\$");
				textContent = textContent.replaceAll("~ ", "~");
				textContent = textContent.replaceAll(" \\.", ".");

				TweetsTexts.add(c, textContent);
				c++;
				fileContent = new StringBuffer();
			} else {
				String split[] = line.split("\t");
				fileContent.append(split[0]);
				fileContent.append(" ");
			}
		}
		// last document
		TweetsTexts.add(c, fileContent.toString());
		bffReader.close();
		bffReader = new BufferedReader(new InputStreamReader(
				new FileInputStream("/home/joao/datasets/derczynski/ipm_nel.conll"), StandardCharsets.UTF_8));
		line = null;
		c = 0;
		while ((line = bffReader.readLine()) != null) {
			if (line.equalsIgnoreCase("\t\t\t")) {
				// new file starts here
				c++;
			} else {
				String elem[] = line.split("\t");
				String token = elem[2];
//				String mention = elem[0];
//				String entity = elem[1];
				if ((!token.startsWith("B-")) && (!token.startsWith("I-"))) {
					// useless token
					continue;
				}
				if (token.startsWith("B-")) {
					String mention = elem[0];
					String entity = elem[1];
					entity = entity.replace("http://dbpedia.org/resource/", "");
					entity = entity.replace("_", " ");
					entity = URLDecoder.decode(entity);
					// beginning of a mention read ahead until the end ...
					while ((line = bffReader.readLine()) != null) {
						if (line.equalsIgnoreCase("\t\t\t")) {
							c++;
						}
						String elemAux[] = line.split("\t");
						if (elemAux.length >= 3) {
							String tokenAux = elemAux[2];
							if (!tokenAux.startsWith("I-")) {
//								mention = elem[0];
								entity = elem[1];
								entity = entity.replace("http://dbpedia.org/resource/", "");
								entity = entity.replace("_", " ");
								entity = URLDecoder.decode(entity);

								String text = TweetsTexts.get(c);
								mention = mention.replaceAll("\\s+", " ");
								mention = mention.replaceAll(" \\.", ".");
								mention = mention.replaceAll(" \\,", ",");
								mention = mention.replaceAll(" \\!", "!");
								mention = mention.replaceAll(" \\.\\.\\.", "...");
								mention = mention.replaceAll(" \\'s", "'s");
								mention = mention.replaceAll(" \\'m", "'m");
								mention = mention.replaceAll(" \\'re", "''re");
								mention = mention.replaceAll(" \\'ve", "'ve");
								mention = mention.replaceAll("\\( ", "(");
								mention = mention.replaceAll(" \\)", ")");
								mention = mention.replaceAll(" \\:", ":");
								mention = mention.replaceAll("&amp;", "&");
								mention = mention.replaceAll("\\@ ", "@");
								mention = mention.replaceAll(" \\?\\?", "??");
								mention = mention.replaceAll(" \\?", "?");
								mention = mention.replaceAll("&lt;", "<");
								mention = mention.replaceAll("\\[ ", "[");
								mention = mention.replaceAll(" \\]", "]");
								mention = mention.replaceAll("\\$ ", "\\$");
								mention = mention.replaceAll("~ ", "~");
								mention = mention.replaceAll(" \\.", ".");

								int offset = text.indexOf(mention);

								System.out.println("tw_" + c + "\t" + mention + "\t" + offset + "\t" + entity);
								out.write("tw_" + c + ".txt\t" + mention + "\t" + offset + "\t" + entity + "\n");
//								System.out.println(mention + "\t" + entity);
								break;
							} else {
								// agregate mention
								mention = mention + " " + elemAux[0];

							}
						} else {
//							
							break;
						}
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
	public static void setAmbiverseMap(TreeMap<String, String> ambiverseMap) {
		DataLoaders_Derczynski.ambiverseMap = ambiverseMap;
	}
	public TreeMap<String, String> getBabelMap() {
		return babelMap;
	}
	public static void setBabelMap(TreeMap<String, String> babelMap) {
		DataLoaders_Derczynski.babelMap = babelMap;
	}
	public TreeMap<String, String> getTagmeMap() {
		return tagmeMap;
	}
	
	public static void setTagmeMap(TreeMap<String, String> tagmeMap) {
		DataLoaders_Derczynski.tagmeMap = tagmeMap;
	}
	public static void setAmbiverseMap_train(TreeMap<String, String> ambiverseMap_train) {
		DataLoaders_Derczynski.ambiverseMap_train = ambiverseMap_train;
	}
	public static void setBabelMONLLap_train(TreeMap<String, String> babelMap_train) {
		DataLoaders_Derczynski.babelMap_train = babelMap_train;
	}
	public static void setTagmeMap_train(TreeMap<String, String> tagmeMap_train) {
		DataLoaders_Derczynski.tagmeMap_train = tagmeMap_train;
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
		DataLoaders_Derczynski.ambiverseMap_test = ambiverseMap_test;
	}
	public TreeMap<String, String> getBabelMap_train() {
		return babelMap_train;
	}
	public TreeMap<String, String> getBabelMap_test() {
		return babelMap_test;
	}
	public static void setBabelMap_test(TreeMap<String, String> babelMap_test) {
		DataLoaders_Derczynski.babelMap_test = babelMap_test;
	}
	public TreeMap<String, String> getTagmeMap_train() {
		return tagmeMap_train;
	}
	public TreeMap<String, String> getTagmeMap_test() {
		return tagmeMap_test;
	}
	public static void setTagmeMap_test(TreeMap<String, String> tagmeMap_test) {
		DataLoaders_Derczynski.tagmeMap_test = tagmeMap_test;
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
		DataLoaders_Derczynski.spotlightMap = spotlightMap;
	}
	public static void setSpotlightMap_train(TreeMap<String, String> spotlightMap_train) {
		DataLoaders_Derczynski.spotlightMap_train = spotlightMap_train;
	}
	public static void setSpotlightMap_test(TreeMap<String, String> spotlightMap_test) {
		DataLoaders_Derczynski.spotlightMap_test = spotlightMap_test;
	}
	
}
