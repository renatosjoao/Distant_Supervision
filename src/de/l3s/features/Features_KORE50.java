package de.l3s.features;

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
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import de.l3s.extra.ConllDocument;
import de.l3s.extra.GenericDocument;
import de.l3s.extra.WP_annotations_Parser;

public class Features_KORE50 {
	private static TreeMap<String,String> GTMAP;

	
	
	public static void main(String[] args) throws NumberFormatException, IOException, CompressorException {
		GTMAP = loadGT();
		dumpNumRecognizedMentionsFromKORE50();
		dumpWordCountFromKORE50();
		dumpDocumentFrequencyFromKORE50();
		writeTrainingAndTestSets();
	}
	
	
	
	
	
	public static void writeTrainingAndTestSets() throws IOException, CompressorException{	
		OutputStreamWriter pw = new OutputStreamWriter(new FileOutputStream("/home/joao/KORE50/dataset.meta.train.csv"),StandardCharsets.UTF_8);		
		OutputStreamWriter pwTest = new OutputStreamWriter(new FileOutputStream("/home/joao/KORE50/dataset.meta.test.csv"),StandardCharsets.UTF_8);

		String mention;
		String a;
		String b;
		String t;
		String totA;
		String totB;
		String totT;
		String normA;
		String normB;
		String normT;
		
		TObjectIntHashMap<String> DocWordCountMap = Features_KORE50.loadWordCount();
		TObjectIntHashMap<String> NumRecogMentionMap = Features_KORE50.loadNumMentionsRecognized();
		TObjectIntHashMap<String> MentionEntityCountMap = Features_KORE50.loadMentionEntityCountMap();
		TObjectIntHashMap<String> DocFrequencyMap = Features_KORE50.loadDocFrequencyMap();
		
		
		TreeMap<String, String> hashTreeMap = Features_KORE50.loadDocsContent();
		
		////////////TRAIN
		TreeMap<String,String> DiffMap = new TreeMap<String, String>();
		BufferedReader trainbuffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/KORE50/mention_correct.train.tsv"),StandardCharsets.UTF_8));
	
		String line = "";
		while((line = trainbuffReader.readLine() )!= null) {
			String[] elems = line.split("\t");
			mention = elems[0];
			mention = mention.replaceAll("_"," ");
			mention = mention.toLowerCase();
			a = elems[1];
			b = elems[2];
			t = elems[3];
			totA = elems[4];
			totB = elems[5];
			totT = elems[6];
			normA = elems[7];
			normB = elems[8];
			normT = elems[9];
			String vals = a+"\t"+b+"\t"+t +"\t"+totA+"\t"+totB+"\t"+totT+"\t"+normA+"\t"+normB+"\t"+normT;
			DiffMap.put(mention, vals);
			}
		trainbuffReader.close();
	
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/KORE50/KORE50_GT_NONNIL.tsv"),StandardCharsets.UTF_8));
		line = "";
		while ((line = bffReader.readLine()) != null) {	
			String[] elems = line.split("\t");
			String docId = elems[0];
			mention = elems[1];
			mention = mention.replaceAll("_"," ");
			String offset = elems[2];
			String link = elems[3];
			link = link.replaceAll("_"," ").toLowerCase();
			String key = docId + "\t" + mention + "\t" + offset;
			
			/** Mention-based features **/
			double mention_length =  getMentionLength(mention);
			double mention_gran =    getMentionGranularity(mention);
			String content = hashTreeMap.get(docId);
			content = content.toLowerCase();
			double mention_freq =    getMentionFrequency(content, mention);
			double mention_doc_freq =  getDocFreq(DocFrequencyMap,mention);
			double mention_num_cand =  (double)MentionEntityCountMap.get(mention);//getNumCandidates(mention);
			String mention_norm_pos =  getMentionNormalizedPosition(content,offset);
			double mention_sent_size = getSentenceSize(content, mention, offset);
			
			String GTLink = GTMAP.get(key); 
			
			String elements = DiffMap.get(mention);
			if(elements!=null){
				elems = elements.split("\t");
				a = elems[0];
				b = elems[1];
				t = elems[2];
				totA = elems[3];
				totB = elems[4];
				totT = elems[5];
				normA = elems[6];
				normB = elems[7];
				normT = elems[8];
			}else{
				a = "0.0";
				b = "0.0";
				t =  "0.0";
				totA =  "0.0";
				totB =  "0.0";
				totT =  "0.0";
				normA =  "0.0";
				normB =  "0.0";
				normT =  "0.0";
			}
		
		/** Document-based features **/
		double doc_size = (double) DocWordCountMap.get(docId);
		String doc_lex  =  getDocumentLex(content);
		double doc_numMentions = (double)NumRecogMentionMap.get(docId);//getNumRecogMentions(docId);
		pw.write("\""+docId + "\"" + "," + "\"" +mention +"\"" + "," +offset + "," + "\"" + GTLink  +"\""+"," + mention_length+ ","+mention_gran+ ","+mention_freq+ ","+mention_doc_freq+ ","+mention_num_cand + ","+mention_norm_pos +","+ mention_sent_size +","+a+","+b+","+t+","+normA+","+normB+","+normT+ "," + totA+ "," +  totB + "," + totT+ "," + doc_size+","+doc_numMentions+ "\n");
	}
	
	bffReader.close();

	pw.flush();
	pw.close();
	
	
	
	
	
	/////// TEST
	line = "";
		
	DiffMap = new TreeMap<String, String>();
	//BufferedReader testbuffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/KORE50/mention_correct.train.tsv"),StandardCharsets.UTF_8));
	BufferedReader testbuffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/conll_corpus/mention_correct.train.tsv"),StandardCharsets.UTF_8));

	while((line = testbuffReader.readLine() )!= null) {
		String[] elems = line.split("\t");
		mention = elems[0];
		mention = mention.replaceAll("_"," ");
		mention = mention.toLowerCase();
		a = elems[1];
		b = elems[2];
		t = elems[3];
		totA = elems[4];
		totB = elems[5];
		totT = elems[6];
		normA = elems[7];
		normB = elems[8];
		normT = elems[9];
		String vals = a+"\t"+b+"\t"+t +"\t"+totA+"\t"+totB+"\t"+totT+"\t"+normA+"\t"+normB+"\t"+normT;
		DiffMap.put(mention, vals);
	}
	testbuffReader.close();
	
	
	bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/KORE50/KORE50_GT_NONNIL.tsv"),StandardCharsets.UTF_8));
	line = "";
	while ((line = bffReader.readLine()) != null) {
		String[] elems = line.split("\t");
		String docId = elems[0];
		mention = elems[1];
		mention = mention.replaceAll("_"," ");
		mention = mention.toLowerCase();
		String offset = elems[2];
		String link = elems[3];
		link = link.replaceAll("_"," ").toLowerCase();
		String key = docId + "\t" + mention + "\t" + offset;
	
		/** Mention-based features **/
		double mention_length =  getMentionLength(mention);
		double mention_gran =    getMentionGranularity(mention);
		String content = hashTreeMap.get(docId);
		content = content.toLowerCase();
		double mention_freq =    getMentionFrequency(content, mention);
		double mention_doc_freq =  getDocFreq(DocFrequencyMap,mention);
		double mention_num_cand =  (double)MentionEntityCountMap.get(mention);//getNumCandidates(mention);
		String mention_norm_pos =  getMentionNormalizedPosition(content,offset);
		double mention_sent_size = getSentenceSize(content, mention, offset);
	
		String GTLink = GTMAP.get(key); 
		
		String elements = DiffMap.get(mention);
		if(elements!=null){
			elems = elements.split("\t");
			a = elems[0];
			b = elems[1];
			t = elems[2];
			totA = elems[3];
			totB = elems[4];
			totT = elems[5];
			normA = elems[6];
			normB = elems[7];
			normT = elems[8];
			
		}else{
			a = "0.0";
			b = "0.0";
			t =  "0.0";
			totA =  "0.0";
			totB =  "0.0";
			totT =  "0.0";
			normA =  "0.0";
			normB =  "0.0";
			normT =  "0.0";
		}
		
		/** Document-based features **/
		double doc_size = (double) DocWordCountMap.get(docId);
		String doc_lex  =  getDocumentLex(content);
		double doc_numMentions = (double)NumRecogMentionMap.get(docId);//getNumRecogMentions(docId);
		pwTest.write("\""+docId + "\"" + "," + "\"" +mention +"\"" + "," +offset + "," + "\"" + GTLink  +"\""+"," + mention_length+ ","+mention_gran+ ","+mention_freq+ ","+mention_doc_freq+ ","+mention_num_cand + ","+mention_norm_pos +","+ mention_sent_size +","+a+","+b+","+t+","+normA+","+normB+","+normT+ "," + totA+ "," +  totB + "," + totT+ "," + doc_size+","+doc_numMentions+ "\n");
	}
	
	bffReader.close();
	pwTest.flush();
	pwTest.close();
	
	
	}
	
	
	
	
	
	/***************************************************************************************************
	 # # # LOADERS START FROM HERE # # # 
	/**************************************************************************************************/
	
	
	/**
	 *
	 *	This method is used to load the document frequency map.
	 *
	 * @throws CompressorException
	 * @throws IOException
	 */
	public static TObjectIntHashMap<String> loadDocFrequencyMap() throws CompressorException, IOException{
		TObjectIntHashMap<String> DocFrequencyMap = new TObjectIntHashMap<>();		
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/conll_corpus/document_frequency.tsv"),StandardCharsets.UTF_8));
		//BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/KORE50/document_frequency.tsv"),StandardCharsets.UTF_8));

		
		String line ="";											
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String mention = elems[0];
			String freq = elems[1];
			Integer df = Integer.parseInt(freq);			
			DocFrequencyMap.put(mention, df);
		}		
		bffReader.close();
		System.out.println("...Loaded Document Frequency Map.");
		return DocFrequencyMap;
	}
	
	/**
	 *
	 * @param DocumentFrequency
	 * @param content
	 * @param mention
	 * @return
	 */
	public static double getTFxIDF(String content, String mention, double doc_freq, double corpus_size){
		double tfxidf = 0.0;
		double tf = 0.0;
		double idf = 0.0;
		
		String txtContent = content.toString().toLowerCase();
		int nWords = txtContent.split("\\s+").length;
		double count = 0;
	    int idx = 0;
	    while ((idx = txtContent.indexOf(mention, idx)) != -1) {
	    	count++;
	    	idx += mention.length();
		}
	    /*TF(t,d) = Term Frequency(t,d): is the number of times that term t occurs in document d */
	    tf = (double) count / (double) nWords;
	    
	    /*IDF(t,D) = Inverse Term Frequency(t,D): measures the importance of term t in all documents (D), we obtain this measure by dividing the total number of documents by the number of documents containing the term, and then taking the logarithm of that quotient*/
	    idf  = Math.log10(corpus_size/ doc_freq);
	    
	    tfxidf = (double) tf * (double)idf;
	    
	    return tfxidf;
	}

	
	/**
	 * 				tf -  Normalized term frequency.
	 * 
	 *
	 * @param docId
	 * @param mention
	 * @return
	 * @throws IOException 
	 */
	public static double getTermFrequency(String content, String mention) throws IOException {				
		String txtContent = content.toString().toLowerCase();
		int wc = txtContent.split("\\s+").length;
       if (isEmpty(txtContent) || isEmpty(mention)) {
         return 0;
       }
       double count = 0;
	    int idx = 0;
	    while ((idx = txtContent.indexOf(mention, idx)) != -1) {
	    	count++;
	    	idx += mention.length();
		}
		return ((double) count/(double)wc);
	}
	
	/**
	 * 		This method is used to load the documents content into a map.
	 *
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private static TreeMap<String, String> loadDocsContent() throws NumberFormatException, IOException {
		TreeMap<String, String> hashTreeMap = new TreeMap<String, String>();
		WP_annotations_Parser p = new WP_annotations_Parser();
		@SuppressWarnings("rawtypes")
		LinkedList<ConllDocument> KORE50DataSet = p.parseDataset("/home/joao/KORE50/AIDA.tsv");
		for (int i = 0; i < KORE50DataSet.size();  i++) { 
			@SuppressWarnings("rawtypes")
			ConllDocument CO = KORE50DataSet.get(i);
		
			String id = CO.getTitle();
			String articlecontent = CO.getTxtContent();
		
			String trim = articlecontent.trim();
			hashTreeMap.put(id,articlecontent);
		}
		return hashTreeMap;
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
	public static TObjectIntHashMap<String> loadWordCount() throws CompressorException, IOException {
		TObjectIntHashMap<String> DocWordCountMap = new TObjectIntHashMap<>();		
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/KORE50/doc_word_count.tsv"),StandardCharsets.UTF_8));
		String line ="";
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0];
			String count = elems[1];
			DocWordCountMap.put(docId, Integer.parseInt(count));
		}
		bffReader.close();
		System.out.println("...Loaded Word Count Map Successfully.");
		return DocWordCountMap;
	}
	
	
	/**
	 *
	 *	This method loads a map of the number of recognized mentions per document
	 *
	 * @return
	 * @throws CompressorException
	 * @throws IOException				
	 */
	public static TObjectIntHashMap<String> loadNumMentionsRecognized() throws CompressorException, IOException {
		TObjectIntHashMap<String> NumRecogMentionMap = new TObjectIntHashMap<String>();
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/KORE50/num_recognized_mentions.tsv"),StandardCharsets.UTF_8));
		String line ="";
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0];
			String count = elems[1];
			NumRecogMentionMap.put(docId,Integer.parseInt(count));
		}
		bffReader.close();
		System.out.println("...Loaded Number Recognized Mentions Map Successfully.");
		return NumRecogMentionMap;
	}
	
//	/**
//	 *
//	 * @return
//	 * @throws IOException
//	 */
	public static TreeMap<String,String> loadGT() throws IOException{
		TreeMap<String, String> GTMAP = new TreeMap<String, String>();
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/KORE50/KORE50_GT_NONNIL.tsv"),StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0];
			String mention = elems[1];
			mention = mention.replaceAll("_"," ");
			mention = mention.toLowerCase();
			String offset = elems[2];
			String link = elems[3];
			link = link.replaceAll("_"," ").toLowerCase();
			String key = docId + "\t" + mention + "\t" + offset;
			GTMAP.put(key, link);
		}
		bffReader.close();
		return GTMAP;		
		
	}		
	
	
	/**
	 *			
	 *	This method loads a map of the mention/entity counts which is the number of entities
	 *	for every mention based on a Wikipedia dump from 2016.
	 *
	 *		It requires the file mentionEntityCount.txt.bz2 
	 *	
	 * 
	 * @throws CompressorException
	 * @throws IOException
	 */
	public static TObjectIntHashMap<String> loadMentionEntityCountMap() throws CompressorException, IOException {
		TObjectIntHashMap<String> MentionEntityCountMap = new TObjectIntHashMap<>();
		BufferedReader bffReader = getBufferedReaderForCompressedFile("/home/joao/CIKM_2018/mentionEntityCount.tsv.bz2");
		String line ="";
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length > 2) {continue;}
			String mention = elems[0].trim();
			mention = mention.replaceAll("_"," ");
			mention = mention.toLowerCase();
			String candCount = elems[1].trim();
			int numCandidates = 0;
			if(candCount!=null) {
				numCandidates = Integer.parseInt(candCount);
			}
			MentionEntityCountMap.put(mention, numCandidates);
		}
		System.out.println("...Total Number of Mentions in the KB (i.e. Wikipedia 2016): "+MentionEntityCountMap.keySet().size());
		System.out.println("...Loaded Mention-> Cand. Entities Successfully.");
		bffReader.close();
		return MentionEntityCountMap;
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
		BufferedReader br2 = new BufferedReader(new InputStreamReader(input));
		return br2;
	}
	
	
	/***************************************************************************************************
	 # # # DUMPERS START FROM HERE # # # 
	/**************************************************************************************************/
	
	
	/**
	 * 	This utility function is meant to fetch the number of words per document in the Microposts2016 corpus.
	 * 
	 * 	It produces the file:
	 * 						/home/joao/microposts/doc_word_count.tsv
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void dumpWordCountFromKORE50() throws NumberFormatException, IOException {
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/KORE50/doc_word_count.tsv"),StandardCharsets.UTF_8);
		WP_annotations_Parser p = new WP_annotations_Parser();
		
		@SuppressWarnings("rawtypes")
		LinkedList<ConllDocument> KORE50DataSet = p.parseDataset("/home/joao/KORE50/AIDA.tsv");
		
		HashMap<String,String> entitiesMap = new HashMap<String, String>();
		for (int i = 0; i < KORE50DataSet.size();  i++) { 
			@SuppressWarnings("rawtypes")
			ConllDocument CO = KORE50DataSet.get(i);
		
			String id = CO.getTitle();
			String articlecontent = CO.getTxtContent();
		
			String trim = articlecontent.trim();
			int wc = trim.split("\\s+").length;
			pAnn.write(id+"\t"+wc+"\n");
		}
		pAnn.flush();
		pAnn.close();
		System.out.println("...Finished dumping the Word Count Successfully.");
		
	}
	
	
	/**
	 *	This utility function is meant to fetch the number of recognized mentions per document in the CONLL corpus.
	 *	
	 *	It produces the file:
	 *
	 *			/home/joao/KORE50/num_recognized_mentions.tsv
	 *
	 * @throws NumberFormatException 
	 * @throws IOException
	 * @throws CompressorException
	 */
	public static void dumpNumRecognizedMentionsFromKORE50() throws NumberFormatException, IOException{
		System.out.println("Dumping the number of recognized mentions per document.");

		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/KORE50/num_recognized_mentions.tsv"),StandardCharsets.UTF_8);
		BufferedReader bff = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/KORE50/KORE50_GT.tsv"),StandardCharsets.UTF_8));

		TreeMap<String, Integer> hashTreeMap = new TreeMap<String, Integer>();
		String line="";
		while((line = bff.readLine()) != null){
			String[] elems = line.split("\t");
			String id = elems[0].trim();
			Integer count = hashTreeMap.get(id);
			if(count == null){
				count = 1;
				hashTreeMap.put(id, count);
			}else{
				count+=1;
				hashTreeMap.put(id, count);
			}
		}
		bff.close();
		
		@SuppressWarnings("rawtypes")
		Iterator it = hashTreeMap.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
//		    System.out.println(pair.getKey() + " = " + pair.getValue());
			pAnn.write(pair.getKey() + "\t"+pair.getValue()+"\n");
		    it.remove(); // avoids a ConcurrentModificationException
		}
		pAnn.flush();
		pAnn.close();
		System.out.println("... Done.");


	
	}
	
	
	/**
	 * This utility function is meant to dump the document frequency for all mentions in the files : 
	 * 
	 * KORE50.easy
	 * KORE50.medium
	 * KORE50.hard
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static void dumpDocumentFrequencyFromKORE50() throws IOException{
		HashSet<String> mentionsSET = new HashSet<String>();
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/KORE50/document_frequency.tsv"),StandardCharsets.UTF_8);
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/KORE50/mappings/KORE50.easy"),StandardCharsets.UTF_8));
		String line = "";
		while((line = bffReader.readLine() )!= null) {
			String[] elems = line.split("\t");
			String docId = elems[0];
			String mention = elems[1];
			mention = mention.replaceAll("_"," ");
			mention = mention.toLowerCase();
			mentionsSET.add(mention);
		}
		bffReader.close();

		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/KORE50/mappings/KORE50.medium"),StandardCharsets.UTF_8));
		line = "";
		while((line = bffReader.readLine() )!= null) {
			String[] elems = line.split("\t");
			//String docId = elems[0];
			String mention = elems[1];
			mention = mention.replaceAll("_"," ");
			mention = mention.toLowerCase();
			mention = mention.replaceAll("_"," ");
			mention = mention.toLowerCase();
			mentionsSET.add(mention);
		}
		bffReader.close();

		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/KORE50/mappings/KORE50.hard"),StandardCharsets.UTF_8));
		line = "";
		while((line = bffReader.readLine() )!= null) {
			String[] elems = line.split("\t");
			//String docId = elems[0];
			String mention = elems[1];
			mention = mention.replaceAll("_"," ");
			mention = mention.toLowerCase();
			mentionsSET.add(mention);
		}
		bffReader.close();
		
		
		BufferedReader bff = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/KORE50/KORE50_GT.tsv"),StandardCharsets.UTF_8));

		line="";
		while((line = bff.readLine()) != null){
			String[] elems = line.split("\t");
			String id = elems[0].trim();
			String mention = elems[1].trim();
			mention = mention.toLowerCase();
			mentionsSET.add(mention);
		}
		bff.close();
		
		
		
		WP_annotations_Parser p = new WP_annotations_Parser();
		LinkedList<ConllDocument> KORE50DataSet = p.parseDataset("/home/joao/KORE50/AIDA.tsv");
		
		
		for (String mention : mentionsSET){
			int df = 0;
			for(ConllDocument CO : KORE50DataSet){
		
				String id = CO.getTitle();
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
	
	
	
	
	
	
	/** Mention-based features **/

	/**
 	 * 
 	 * 					Mention length
 	 * 
	 *  The number of mention's characters. 
	 *  Intuition: short mentions are usually more ambiguous compared to long mentions 
	 *  (e.g., Adams vs Schwarzenegger). 
	 * 
	 * 
	 * @param mention
	 * @return
	 */
	public static double getMentionLength(String mention) {
		int mention_size = mention.length();
		return (double)mention_size;
	}

	/**
	 * 				Mention words 
	 * 
	 * The number of words of a mention. 
	 * Intuition: unigram mentions are usually more ambiguous than mentions with more 
	 * than one word (e.g., John vs John McCain). 
	 * 
	 * 	 
	 * @param mention
	 * @return
	 */
	public static double getMentionGranularity(String mention) {		
		int mention_granularity = mention.split("\\s+").length;
		return (double)mention_granularity;
	}
	
	
	/**
	 * 				Mf - Number of times the mention appears in the document.
	 *
	 * @param docId
	 * @param mention
	 * @return
	 * @throws IOException 
	 */
	public static double getMentionFrequency(String content, String mention) throws IOException {				
		String txtContent = content.toString().toLowerCase();
        if (isEmpty(txtContent) || isEmpty(mention)) {
          return 0;
        }
        double count = 0;
	    int idx = 0;
	    while ((idx = txtContent.indexOf(mention, idx)) != -1) {
	    	count++;
	    	idx += mention.length();
		}
		return count;
	}
	
	
	
	/**
	 *			Mdf - Mention document frequency
	 * @param mention
	 * @return
	 */
	private static double getDocFreq(TObjectIntHashMap<String>DocumentFrequency, String mention) {
		
		return (double) DocumentFrequency.get(mention);
		
	}
	/**
	 *				Mp - Mention normalized position.
	 *	
	 * @param offset
	 * @param docID
	 * @return
	 * @throws IOException 
	 */
	public static String getMentionNormalizedPosition(String content, String offset) throws IOException {		
		double length = (double)content.length();	
		double position = Double.parseDouble(offset);
		double normalized;
		normalized = position/length;	
		DecimalFormat dec = new DecimalFormat("#0.00");
		return dec.format(normalized);

	}
	
	/**
	 * 				Ms	-	Num of chars of the sentence containing the mention
	 * 
	 * @param docId
	 * @param mention
	 * @param position
	 * @return
	 */
	public static int getSentenceSize(String content, String mention, String position ) {
		int tamEsq = 0;
		int tamDir = 0;
		int offset = Integer.parseInt(position);
		for(int i=0; i < offset; i++){
			tamEsq++;			
			if((content.charAt(i)=='.') ||(content.charAt(i)==';') || (content.charAt(i)=='!' || (content.charAt(i)=='?'))){
				tamEsq = 0;
 			}
		}
		
		for(int i = offset + mention.length() ; i < content.length(); i++){
			tamDir++;			
			if((content.charAt(i)=='.') ||(content.charAt(i)==';') || (content.charAt(i)=='!' || (content.charAt(i)=='?'))){
				break;
			}		
		}
		
		return tamEsq + mention.length()+ tamDir;
	}
	
	public static boolean isEmpty(String str) {
	      return str == null || str.length() == 0;
	  }
	
	/**
	 * 		Doc_lex -   Document lexical diversity(doc_lex) 
	 * 
	 * 				The number of unique words divided by the total number of words in the document.
	 * 
	 * @param content
	 * @return
	 * @throws IOException
	 */
	public static String getDocumentLex(String content) throws IOException {	
	TreeMap<String, Integer> hashTreeMap = new TreeMap<String, Integer>();
	String txtContent = content.toString().toLowerCase();
    if (isEmpty(txtContent)) {
      return "0.0";
    }
    
    String[] splited = content.split("\\s+");
    for(String s : splited){
    	
    	if(hashTreeMap.containsKey(s)){
    		Integer c = hashTreeMap.get(s);
    		c+=1;
    		hashTreeMap.put(s, c);
    	}else{
    		hashTreeMap.put(s, 1);
    	}
    }
    double doc_lex = 0;
    doc_lex = (double) hashTreeMap.size() / (double) content.length();
    DecimalFormat dec = new DecimalFormat("#0.00");
    String lex = dec.format(doc_lex);
	return lex;
}


}
