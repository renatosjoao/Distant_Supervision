package de.l3s.features;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeMap;
import org.apache.commons.compress.compressors.CompressorException;
import de.l3s.extra.AIDA_YAGO2_annotations_Parser;
import de.l3s.extra.GenericDocument;
import de.l3s.loaders.DataLoaders_CONLL;

public class Features_CONLL extends Features {

	private static TreeMap<String, String> GTMAP;

	public Features_CONLL() throws CompressorException, IOException {
	}
	
	public static void main(String[] args) throws NumberFormatException,	IOException, CompressorException {
		

		DataLoaders_CONLL d = new DataLoaders_CONLL();
		GTMAP = new TreeMap<String, String>();
		GTMAP = d.getGT_MAP();
		writeTrainingAndTestSets(d);
	}

	/**
	 *
	 * This function is a modification from the previous one because it does not  only write what is common but all the mentions that are in the ground
	 * truth.
	 * 
	 * The format is the following :
	 * 
	 * doc1 mention1 pos1 link1 feature1_value feature2_value … featuren_value
	 * TAGME
	 * 
	 * @throws IOException
	 * @throws CompressorException
	 */
	public static void writeTrainingAndTestSets(DataLoaders_CONLL d ) throws IOException,	CompressorException {
		OutputStreamWriter pw = new OutputStreamWriter(new FileOutputStream("./resources/conll/dataset.meta.train.conll.csv"),StandardCharsets.UTF_8);
		OutputStreamWriter pwTest = new OutputStreamWriter(new FileOutputStream("./resources/conll/dataset.meta.test.conll.csv"),StandardCharsets.UTF_8);

		TreeMap<String, String> hashTreeMap = new TreeMap<String, String>();
		AIDA_YAGO2_annotations_Parser p = new AIDA_YAGO2_annotations_Parser();
		LinkedList<GenericDocument> ConllDataSet = p.parseDataset("/home/joao/datasets/conll/AIDA-YAGO2-dataset.tsv");

		double corpus_size = ConllDataSet.size();
		TreeMap<String, String> DiffMap = new TreeMap<String, String>();
		BufferedReader trainbuffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/mention_correct.train.tsv"),StandardCharsets.UTF_8));
		String line = "";
		while ((line = trainbuffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String mention = elems[0].toLowerCase();
			String a = elems[1];
			String b = elems[2];
			String t = elems[3];
			String totA = elems[4];
			String totB = elems[5];
			String totT = elems[6];
			String normA = elems[7];
			String normB = elems[8];
			String normT = elems[9];
			String vals = a + "\t" + b + "\t" + t + "\t" + totA + "\t" + totB + "\t" + totT + "\t" + normA + "\t" + normB + "\t" + normT;
			DiffMap.put(mention, vals);
		}
		trainbuffReader.close();

		TObjectIntHashMap<String> DocWordCountMap = d.getDocWordCountMap();
//		
		TObjectIntHashMap<String> NumRecogMentionMap = d.getNumRecogMentionMap();
//		
		TObjectIntHashMap<String> MentionEntityCountMap = Features.loadMentionEntityCountMap();
		
		TObjectIntHashMap<String> DocFrequencyMap = d.getDocFrequencyMap();
		// HashMap<String,String> DocTopicMap = loadTopicMap();

		for (int i = 0; i < 1162; i++) { // Writing only the training set  !!!!for(int i = 1162; i <  ConllDataSet.size(); i++){
			GenericDocument CO = ConllDataSet.get(i);
			String docid = CO.getTitle().toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			String articlecontent = CO.getTxtContent().trim().toLowerCase();
			hashTreeMap.put(docid, articlecontent);
		}
		System.out.println(hashTreeMap.size());

		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/conllYAGO_all_GT.tsv"),StandardCharsets.UTF_8));
		String a;
		String b;
		String t;
		String totA;
		String totB;
		String totT;
		String normA;
		String normB;
		String normT;

		line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docid = elems[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			
			if (hashTreeMap.containsKey(docid)) {
				String mention = elems[1].toLowerCase();
				String offset = elems[2];
				String link = elems[3].toLowerCase();
				if (link.equalsIgnoreCase("--NME--")) {
					continue;
				}
				String key = docid + "\t" + mention + "\t" + offset;
				/** Mention-based features **/
				double mention_length = getMentionLength(mention);
				double mention_gran = getMentionGranularity(mention);
				String content = hashTreeMap.get(docid);
				content = content.toLowerCase();
				double mention_freq = getMentionFrequency(content, mention);
				double mention_doc_freq = getDocFreq(DocFrequencyMap, mention);
				double mention_num_cand = (double) MentionEntityCountMap.get(mention);// getNumCandidates(mention);
				String mention_norm_pos = getMentionNormalizedPosition(content,	offset);
				double mention_sent_size = getSentenceSize(content, mention,offset);
//
//				double tf_idf = getTFxIDF(content, mention, mention_doc_freq,corpus_size);
//				double tf = getTermFrequency(content, mention);

				String GTLink = GTMAP.get(key).toLowerCase();
				
				String elements = DiffMap.get(mention);
				if (elements != null) {
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
				} else {
					a = "0.0";
					b = "0.0";
					t = "0.0";
					totA = "0.0";
					totB = "0.0";
					totT = "0.0";
					normA = "0.0";
					normB = "0.0";
					normT = "0.0";
				}

				/** Document-based features **/
				double doc_size = (double) DocWordCountMap.get(docid);
//				String doc_lex = getDocumentLex(content);
				double doc_numMentions = (double) NumRecogMentionMap.get(docid);// getNumRecogMentions(docId);

				// pw.write("\""+docId + "\"" + "," + "\"" +mention +"\"" + ","
				// +offset + "," + "\"" + GTLink +"\""+"," + mention_length+
				// ","+mention_gran+ ","+mention_freq+ ","+mention_doc_freq+
				// ","+mention_num_cand + ","+mention_norm_pos +","+
				// mention_sent_size
				// +","+a+","+b+","+t+","+normA+","+normB+","+normT+ "," + totA+
				// "," + totB + "," + totT+ "," + tf_idf
				// +","+doc_size+","+doc_numMentions+ "\n");
				pw.write("\"" + docid + "\"" + "," + "\"" + mention + "\""+ "," + offset + "," + "\"" + GTLink + "\"" + ","
						+ mention_length + "," + mention_gran + ","	+ mention_freq + "," + mention_doc_freq + ","
						+ mention_num_cand + "," + mention_norm_pos + ","+ mention_sent_size + "," + a + "," + b + "," + t + ","	+ normA + "," + normB + "," + normT + "," + totA + ","+ totB + "," + totT + "," + doc_size + ","+ doc_numMentions + "\n");

			}
		}
		bffReader.close();

		pw.flush();
		pw.close();
		System.out.println("Finished writing the training set");

		
		// ///// TEST
		line = "";
		hashTreeMap = new TreeMap<String, String>();
		for (int i = 1162; i < ConllDataSet.size(); i++) { // Writing only the test set !!!!
			GenericDocument CO = ConllDataSet.get(i);
			String docid = CO.getTitle().toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			String articlecontent = CO.getTxtContent().trim().toLowerCase();
			hashTreeMap.put(docid, articlecontent);
		}
		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/conllYAGO_all_GT.tsv"),StandardCharsets.UTF_8));
		line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docid = elems[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			if (hashTreeMap.containsKey(docid)) {
				String mention = elems[1].toLowerCase();
				String offset = elems[2];
				String link = elems[3].toLowerCase();
				if (link.equalsIgnoreCase("--NME--")) {
					continue;
				}
				String key = docid + "\t" + mention + "\t" + offset;
				/** Mention-based features **/
				double mention_length = getMentionLength(mention);
				double mention_gran = getMentionGranularity(mention);
				String content = hashTreeMap.get(docid);
				content = content.toLowerCase();
				double mention_freq = getMentionFrequency(content, mention);
				double mention_doc_freq = getDocFreq(DocFrequencyMap, mention);
				double mention_num_cand = (double) MentionEntityCountMap.get(mention);// getNumCandidates(mention);
				String mention_norm_pos = getMentionNormalizedPosition(content,	offset);
				double mention_sent_size = getSentenceSize(content, mention,offset);

				// double tf_idf = getTFxIDF(content, mention, mention_doc_freq,
				// corpus_size);
				// double tf = getTermFrequency(content, mention);

				String GTLink = GTMAP.get(key).toLowerCase();
				//
				String elements = DiffMap.get(mention);
				if (elements != null) {
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

				} else {
					a = "0.0";
					b = "0.0";
					t = "0.0";
					totA = "0.0";
					totB = "0.0";
					totT = "0.0";
					normA = "0.0";
					normB = "0.0";
					normT = "0.0";
				}

				/** Document-based features **/
				double doc_size = (double) DocWordCountMap.get(docid);
				// String doc_lex = getDocumentLex(content);
				double doc_numMentions = (double) NumRecogMentionMap.get(docid);// getNumRecogMentions(docId);

				pwTest.write("\"" + docid + "\"" + "," + "\"" + mention + "\""
						+ "," + offset + "," + "\"" + GTLink + "\"" + ","
						+ mention_length + "," + mention_gran + ","
						+ mention_freq + "," + mention_doc_freq + ","
						+ mention_num_cand + "," + mention_norm_pos + ","
						+ mention_sent_size + "," + a + "," + b + "," + t + ","
						+ normA + "," + normB + "," + normT + "," + totA + ","
						+ totB + "," + totT + "," + doc_size + ","
						+ doc_numMentions + "\n");

				// pwTest.write("\""+docId + "\"" + "," + "\"" +mention +"\"" +
				// "," +offset + "," + "\"" + GTLink +"\""+"," + mention_length+
				// ","+mention_gran+ ","+mention_freq+ ","+mention_doc_freq+
				// ","+mention_num_cand + ","+mention_norm_pos + ","+
				// mention_sent_size
				// +","+a+","+b+","+t+","+normA+","+normB+","+normT+ "," + totA+
				// "," + totB + "," + totT+ ","+ tf_idf+ ","+
				// doc_size+","+doc_numMentions+ "\n");

			}
		}
		bffReader.close();

		pwTest.flush();
		pwTest.close();

		System.out.println("Finished writing the test set");

	}
//
//	/**
//	 *
//	 * @throws IOException
//	 * @throws CompressorException
//	 */
//	public static void writeTestSet() throws IOException, CompressorException {
//		// System.out.println(docId + "," + mention + "," +offset + "," +
//		// mention_length+ ","+mention_gran+ ","+mention_freq+
//		// ","+mention_doc_freq+ ","+mention_num_cand + ","+mention_norm_pos +
//		// ","+ mention_sent_size+","+doc_size+","+doc_numMentions+ ","+"EASY");
//		OutputStreamWriter pw = new OutputStreamWriter(new FileOutputStream(
//				"/home/joao/datasets/conll/dataset.test.multi.arff"),
//				StandardCharsets.UTF_8);
//		// PrintWriter pw = new PrintWriter(new FileOutputStream(new
//		// File("/home/joao/CIKM_2018/datasets/experiments/fulldataset.multi.arff")));
//		pw.write("@RELATION multiclass_instances" + "\n");
//		pw.write("@ATTRIBUTE docid        STRING" + "\n");
//		pw.write("@ATTRIBUTE mention      STRING" + "\n");
//		pw.write("@ATTRIBUTE offset       NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE length       NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE granularity  NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE freq         NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE docfreq      NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE numcand      NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE normpos      NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE sentsize     NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE docsize     NUMERIC" + "\n");
//		// pw.println("@ATTRIBUTE doctopic     {Arts,Automobiles,Books,Business,Education,Health,Home_and_Garden,Job_Market,Magazine,Miscelaneous,Movies,New_York_and_Region,Obituaries,Real_Estate,Science,Sports,Style,Technology,Theater,Travel,Week_in_Review,World}");
//		pw.write("@ATTRIBUTE docmentions    NUMERIC" + "\n");
//		// pw.println("@ATTRIBUTE pub_date     NUMERIC");
//		// pw.println("@ATTRIBUTE tdf  NUMERIC");
//		// pw.println("@ATTRIBUTE min_jcc_change    NUMERIC");
//		// pw.println("@ATTRIBUTE max_jcc_change    NUMERIC");
//		// pw.println("@ATTRIBUTE avg_jcc_change    NUMERIC");
//		pw.write("@ATTRIBUTE class        {EASY,MEDIUM,HARD}" + "\n");
//		pw.write("\n");
//		pw.write("@DATA" + "\n");
//		TreeMap<String, String> hashTreeMap = new TreeMap<String, String>();
//		AIDA_YAGO2_annotations_Parser p = new AIDA_YAGO2_annotations_Parser();
//		LinkedList<GenericDocument> ConllDataSet = p
//				.parseDataset("/home/joao/datasets/conll/AIDA-YAGO2-dataset.tsv");
//
//		for (int i = 1162; i < ConllDataSet.size(); i++) { // Writing only the
//															// test set !!!!
//			GenericDocument CO = ConllDataSet.get(i);
//			String docid = CO.getTitle();
//			docid = docid.replaceAll("\'", "");
//			docid = docid.replaceAll("\"", "");
//			String articlecontent = CO.getTxtContent().trim();
//			hashTreeMap.put(docid, articlecontent);
//		}
//		// Features_CONLL.dumpWordCountFromCONLL();
//		// Features_CONLL.dumpNumRecognizedMentionsFromCONLL();
//		// Features_CONLL.dumpDocumentFrequencyFromCONLL();
//
//		// CANNOT CALCULATE NEIGHBORHOOD SCORE
//		// TreeMap<String, String> NeighborhoodScoreMap =
//		// loadNeighborhoodScore();
//
//		TObjectIntHashMap<String> DocWordCountMap =
//		TObjectIntHashMap<String> NumRecogMentionMap = Features_CONLL.loadNumMentionsRecognized();
//		TObjectIntHashMap<String> MentionEntityCountMap = Features.loadMentionEntityCountMap();
//		TObjectIntHashMap<String> DocFrequencyMap = Features_CONLL.loadDocFrequencyMap();
//
//		BufferedReader bffReader = new BufferedReader(
//				new InputStreamReader(
//						new FileInputStream(
//								"/home/joao/datasets/conll/mappings/conll_testb_dataset.easy"),
//						StandardCharsets.UTF_8));
//		String line = "";
//		while ((line = bffReader.readLine()) != null) {
//			String[] elems = line.split("\t");
//			String docid = elems[0];
//			docid = docid.replaceAll("\'", "");
//			docid = docid.replaceAll("\"", "");
//			if (hashTreeMap.containsKey(docid)) {
//				String mention = elems[1];
//				String offset = elems[2];
//				// mentionsSET.add(mention);
//
//				/** Mention-based features **/
//				double mention_length = getMentionLength(mention);
//				double mention_gran = getMentionGranularity(mention);
//				String content = hashTreeMap.get(docid);
//				content = content.toLowerCase();
//				double mention_freq = getMentionFrequency(content, mention);
//				double mention_doc_freq = getDocFreq(DocFrequencyMap, mention);
//				double mention_num_cand = (double) MentionEntityCountMap
//						.get(mention);// getNumCandidates(mention);
//				String mention_norm_pos = getMentionNormalizedPosition(content,
//						offset);
//				double mention_sent_size = getSentenceSize(content, mention,
//						offset);
//
//				/** Document-based features **/
//				double doc_size = (double) DocWordCountMap.get(docid);
//				// String doc_topic = getDocumentTopic(docId);
//				double doc_numMentions = (double) NumRecogMentionMap.get(docid);// getNumRecogMentions(docId);
//
//				/** Temporal-based features **/
//				// double publication_date = getPublicationDate(docId);
//				// int year = DocMentionMap.get(docId+"\t"+mention);
//				// if(year == 0 ) {
//				// continue;
//				// }
//				pw.write("\"" + docid + "\"" + "," + "\"" + mention + "\""
//						+ "," + offset + "," + mention_length + ","
//						+ mention_gran + "," + mention_freq + ","
//						+ mention_doc_freq + "," + mention_num_cand + ","
//						+ mention_norm_pos + "," + mention_sent_size + ","
//						+ doc_size + "," + doc_numMentions + "," + "EASY"
//						+ "\n");
//				// System.out.println();
//
//			}
//		}
//		bffReader.close();
//
//		bffReader = new BufferedReader(
//				new InputStreamReader(
//						new FileInputStream(
//								"/home/joao/datasets/conll/mappings/conll_testb_dataset.medium"),
//						StandardCharsets.UTF_8));
//		line = "";
//		while ((line = bffReader.readLine()) != null) {
//			String[] elems = line.split("\t");
//			String docid = elems[0].toLowerCase();
//			docid = docid.replaceAll("\'", "");
//			docid = docid.replaceAll("\"", "");
//			if (hashTreeMap.containsKey(docid)) {
//				String mention = elems[1];
//				String offset = elems[2];
//				// mentionsSET.add(mention);
//
//				/** Mention-based features **/
//				double mention_length = getMentionLength(mention);
//				double mention_gran = getMentionGranularity(mention);
//				String content = hashTreeMap.get(docid);
//				content = content.toLowerCase();
//				double mention_freq = getMentionFrequency(content, mention);
//				double mention_doc_freq = getDocFreq(DocFrequencyMap, mention);
//				double mention_num_cand = (double) MentionEntityCountMap
//						.get(mention);// getNumCandidates(mention);
//				String mention_norm_pos = getMentionNormalizedPosition(content,
//						offset);
//				double mention_sent_size = getSentenceSize(content, mention,
//						offset);
//
//				/** Document-based features **/
//				double doc_size = (double) DocWordCountMap.get(docid);
//				// String doc_topic = getDocumentTopic(docId);
//				double doc_numMentions = (double) NumRecogMentionMap.get(docid);// getNumRecogMentions(docId);
//
//				/** Temporal-based features **/
//				// double publication_date = getPublicationDate(docId);
//				// int year = DocMentionMap.get(docId+"\t"+mention);
//				// if(year == 0 ) {
//				// continue;
//				// }
//				pw.write("\"" + docid + "\"" + "," + "\"" + mention + "\""
//						+ "," + offset + "," + mention_length + ","
//						+ mention_gran + "," + mention_freq + ","
//						+ mention_doc_freq + "," + mention_num_cand + ","
//						+ mention_norm_pos + "," + mention_sent_size + ","
//						+ doc_size + "," + doc_numMentions + "," + "MEDIUM"
//						+ "\n");
//			}
//		}
//		bffReader.close();
//
//		bffReader = new BufferedReader(
//				new InputStreamReader(
//						new FileInputStream(
//								"/home/joao/datasets/conll/mappings/conll_testb_dataset.hard"),
//						StandardCharsets.UTF_8));
//		line = "";
//		while ((line = bffReader.readLine()) != null) {
//			String[] elems = line.split("\t");
//			String docid = elems[0];
//			docid = docid.replaceAll("\'", "");
//			docid = docid.replaceAll("\"", "");
//			if (hashTreeMap.containsKey(docid)) {
//				String mention = elems[1];
//				String offset = elems[2];
//				// mentionsSET.add(mention);
//
//				/** Mention-based features **/
//				double mention_length = getMentionLength(mention);
//				double mention_gran = getMentionGranularity(mention);
//				String content = hashTreeMap.get(docid);
//				content = content.toLowerCase();
//				double mention_freq = getMentionFrequency(content, mention);
//				double mention_doc_freq = getDocFreq(DocFrequencyMap, mention);
//				double mention_num_cand = (double) MentionEntityCountMap
//						.get(mention);// getNumCandidates(mention);
//				String mention_norm_pos = getMentionNormalizedPosition(content,
//						offset);
//				double mention_sent_size = getSentenceSize(content, mention,
//						offset);
//
//				/** Document-based features **/
//				double doc_size = (double) DocWordCountMap.get(docid);
//				// String doc_topic = getDocumentTopic(docId);
//				double doc_numMentions = (double) NumRecogMentionMap.get(docid);// getNumRecogMentions(docId);
//
//				/** Temporal-based features **/
//				// double publication_date = getPublicationDate(docId);
//				// int year = DocMentionMap.get(docId+"\t"+mention);
//				// if(year == 0 ) {
//				// continue;
//				// }
//				pw.write("\"" + docid + "\"" + "," + "\"" + mention + "\""
//						+ "," + offset + "," + mention_length + ","
//						+ mention_gran + "," + mention_freq + ","
//						+ mention_doc_freq + "," + mention_num_cand + ","
//						+ mention_norm_pos + "," + mention_sent_size + ","
//						+ doc_size + "," + doc_numMentions + "," + "HARD"
//						+ "\n");
//			}
//		}
//		bffReader.close();
//
//		pw.flush();
//		pw.close();
//
//		// BINARY ##########################
//		pw = new OutputStreamWriter(new FileOutputStream(
//				"/home/joao/datasets/conll/dataset.test.bin.arff"),
//				StandardCharsets.UTF_8);
//		// PrintWriter pw = new PrintWriter(new FileOutputStream(new
//		// File("/home/joao/CIKM_2018/datasets/experiments/fulldataset.multi.arff")));
//		pw.write("@RELATION binclass_instances" + "\n");
//		pw.write("@ATTRIBUTE docid        STRING" + "\n");
//		pw.write("@ATTRIBUTE mention      STRING" + "\n");
//		pw.write("@ATTRIBUTE offset       NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE length       NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE granularity  NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE freq         NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE docfreq      NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE numcand      NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE normpos      NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE sentsize     NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE docsize     NUMERIC" + "\n");
//		// pw.println("@ATTRIBUTE doctopic     {Arts,Automobiles,Books,Business,Education,Health,Home_and_Garden,Job_Market,Magazine,Miscelaneous,Movies,New_York_and_Region,Obituaries,Real_Estate,Science,Sports,Style,Technology,Theater,Travel,Week_in_Review,World}");
//		pw.write("@ATTRIBUTE docmentions    NUMERIC" + "\n");
//		// pw.println("@ATTRIBUTE pub_date     NUMERIC");
//		// pw.println("@ATTRIBUTE tdf  NUMERIC");
//		// pw.println("@ATTRIBUTE min_jcc_change    NUMERIC");
//		// pw.println("@ATTRIBUTE max_jcc_change    NUMERIC");
//		// pw.println("@ATTRIBUTE avg_jcc_change    NUMERIC");
//		pw.write("@ATTRIBUTE class        {NOT_HARD,HARD}" + "\n");
//		pw.write("\n");
//		pw.write("@DATA" + "\n");
//
//		hashTreeMap = new TreeMap<String, String>();
//		// AIDA_YAGO2_annotations_Parser p = new
//		// AIDA_YAGO2_annotations_Parser();
//		// LinkedList<ConllDocument> ConllDataSet =
//		// p.parseDataset("/home/joao/datasets/conll/AIDA-YAGO2-dataset.tsv");
//		for (int i = 1162; i < ConllDataSet.size(); i++) { // Writing only the
//															// test set !!!!
//			GenericDocument CO = ConllDataSet.get(i);
//			String docid = CO.getTitle();
//			docid = docid.replaceAll("\'", "");
//			docid = docid.replaceAll("\"", "");
//			String articlecontent = CO.getTxtContent().trim();
//			hashTreeMap.put(docid, articlecontent);
//		}
//		// Features_CONLL.dumpWordCountFromCONLL();
//		// Features_CONLL.dumpNumRecognizedMentionsFromCONLL();
//		// Features_CONLL.dumpDocumentFrequencyFromCONLL();
//
//		// CANNOT CALCULATE NEIGHBORHOOD SCORE
//		// TreeMap<String, String> NeighborhoodScoreMap =
//		// loadNeighborhoodScore();
//
//		// TObjectIntHashMap<String> DocWordCountMap =
//		// Features_CONLL.loadWordCount();
//		// TObjectIntHashMap<String> NumRecogMentionMap =
//		// Features_CONLL.loadNumMentionsRecognized();
//		// TObjectIntHashMap<String> MentionEntityCountMap =
//		// Features_CONLL.loadMentionEntityCountMap();
//		// TObjectIntHashMap<String> DocFrequencyMap =
//		// Features_CONLL.loadDocFrequencyMap();
//
//		bffReader = new BufferedReader(
//				new InputStreamReader(
//						new FileInputStream(
//								"/home/joao/datasets/conll/mappings/conll_testb_dataset.easy"),
//						StandardCharsets.UTF_8));
//		line = "";
//		while ((line = bffReader.readLine()) != null) {
//			String[] elems = line.split("\t");
//			String docid = elems[0];
//			docid = docid.replaceAll("\'", "");
//			docid = docid.replaceAll("\"", "");
//			if (hashTreeMap.containsKey(docid)) {
//				String mention = elems[1];
//				String offset = elems[2];
//				// mentionsSET.add(mention);
//
//				/** Mention-based features **/
//				double mention_length = getMentionLength(mention);
//				double mention_gran = getMentionGranularity(mention);
//				String content = hashTreeMap.get(docid);
//				content = content.toLowerCase();
//				double mention_freq = getMentionFrequency(content, mention);
//				double mention_doc_freq = getDocFreq(DocFrequencyMap, mention);
//				double mention_num_cand = (double) MentionEntityCountMap
//						.get(mention);// getNumCandidates(mention);
//				String mention_norm_pos = getMentionNormalizedPosition(content,
//						offset);
//				double mention_sent_size = getSentenceSize(content, mention,
//						offset);
//
//				/** Document-based features **/
//				double doc_size = (double) DocWordCountMap.get(docid);
//				// String doc_topic = getDocumentTopic(docId);
//				double doc_numMentions = (double) NumRecogMentionMap.get(docid);// getNumRecogMentions(docId);
//
//				/** Temporal-based features **/
//				// double publication_date = getPublicationDate(docId);
//				// int year = DocMentionMap.get(docId+"\t"+mention);
//				// if(year == 0 ) {
//				// continue;
//				// }
//				pw.write("\"" + docid + "\"" + "," + "\"" + mention + "\""
//						+ "," + offset + "," + mention_length + ","
//						+ mention_gran + "," + mention_freq + ","
//						+ mention_doc_freq + "," + mention_num_cand + ","
//						+ mention_norm_pos + "," + mention_sent_size + ","
//						+ doc_size + "," + doc_numMentions + "," + "NOT_HARD"
//						+ "\n");
//				// System.out.println();
//			}
//		}
//		bffReader.close();
//
//		bffReader = new BufferedReader(
//				new InputStreamReader(
//						new FileInputStream(
//								"/home/joao/datasets/conll/mappings/conll_testb_dataset.medium"),
//						StandardCharsets.UTF_8));
//		line = "";
//		while ((line = bffReader.readLine()) != null) {
//			String[] elems = line.split("\t");
//			String docid = elems[0];
//			docid = docid.replaceAll("\'", "");
//			docid = docid.replaceAll("\"", "");
//			if (hashTreeMap.containsKey(docid)) {
//				String mention = elems[1];
//				String offset = elems[2];
//				// mentionsSET.add(mention);
//
//				/** Mention-based features **/
//				double mention_length = getMentionLength(mention);
//				double mention_gran = getMentionGranularity(mention);
//				String content = hashTreeMap.get(docid);
//				content = content.toLowerCase();
//				double mention_freq = getMentionFrequency(content, mention);
//				double mention_doc_freq = getDocFreq(DocFrequencyMap, mention);
//				double mention_num_cand = (double) MentionEntityCountMap
//						.get(mention);// getNumCandidates(mention);
//				String mention_norm_pos = getMentionNormalizedPosition(content,
//						offset);
//				double mention_sent_size = getSentenceSize(content, mention,
//						offset);
//
//				/** Document-based features **/
//				double doc_size = (double) DocWordCountMap.get(docid);
//				// String doc_topic = getDocumentTopic(docId);
//				double doc_numMentions = (double) NumRecogMentionMap.get(docid);// getNumRecogMentions(docId);
//
//				/** Temporal-based features **/
//				// double publication_date = getPublicationDate(docId);
//				// int year = DocMentionMap.get(docId+"\t"+mention);
//				// if(year == 0 ) {
//				// continue;
//				// }
//				pw.write("\"" + docid + "\"" + "," + "\"" + mention + "\""
//						+ "," + offset + "," + mention_length + ","
//						+ mention_gran + "," + mention_freq + ","
//						+ mention_doc_freq + "," + mention_num_cand + ","
//						+ mention_norm_pos + "," + mention_sent_size + ","
//						+ doc_size + "," + doc_numMentions + "," + "NOT_HARD"
//						+ "\n");
//			}
//		}
//		bffReader.close();
//
//		bffReader = new BufferedReader(
//				new InputStreamReader(
//						new FileInputStream(
//								"/home/joao/datasets/conll/mappings/conll_testb_dataset.hard"),
//						StandardCharsets.UTF_8));
//		line = "";
//		while ((line = bffReader.readLine()) != null) {
//			String[] elems = line.split("\t");
//			String docid = elems[0];
//			docid = docid.replaceAll("\'", "");
//			docid = docid.replaceAll("\"", "");
//			if (hashTreeMap.containsKey(docid)) {
//				String mention = elems[1];
//				String offset = elems[2];
//				// mentionsSET.add(mention);
//
//				/** Mention-based features **/
//				double mention_length = getMentionLength(mention);
//				double mention_gran = getMentionGranularity(mention);
//				String content = hashTreeMap.get(docid);
//				content = content.toLowerCase();
//				double mention_freq = getMentionFrequency(content, mention);
//				double mention_doc_freq = getDocFreq(DocFrequencyMap, mention);
//				double mention_num_cand = (double) MentionEntityCountMap
//						.get(mention);// getNumCandidates(mention);
//				String mention_norm_pos = getMentionNormalizedPosition(content,
//						offset);
//				double mention_sent_size = getSentenceSize(content, mention,
//						offset);
//
//				/** Document-based features **/
//				double doc_size = (double) DocWordCountMap.get(docid);
//				// String doc_topic = getDocumentTopic(docId);
//				double doc_numMentions = (double) NumRecogMentionMap.get(docid);// getNumRecogMentions(docId);
//
//				/** Temporal-based features **/
//				// double publication_date = getPublicationDate(docId);
//				// int year = DocMentionMap.get(docId+"\t"+mention);
//				// if(year == 0 ) {
//				// continue;
//				// }
//				pw.write("\"" + docid + "\"" + "," + "\"" + mention + "\""
//						+ "," + offset + "," + mention_length + ","
//						+ mention_gran + "," + mention_freq + ","
//						+ mention_doc_freq + "," + mention_num_cand + ","
//						+ mention_norm_pos + "," + mention_sent_size + ","
//						+ doc_size + "," + doc_numMentions + "," + "HARD"
//						+ "\n");
//			}
//		}
//		bffReader.close();
//
//		pw.flush();
//		pw.close();
//	}

	/**
	 *
	 * @throws IOException
	 * @throws CompressorException
	 */
//	public static void writeTrainingSet() throws IOException,
//			CompressorException {
//		// System.out.println(docId + "," + mention + "," +offset + "," +
//		// mention_length+ ","+mention_gran+ ","+mention_freq+
//		// ","+mention_doc_freq+ ","+mention_num_cand + ","+mention_norm_pos +
//		// ","+ mention_sent_size+","+doc_size+","+doc_numMentions+ ","+"EASY");
//		OutputStreamWriter pw = new OutputStreamWriter(new FileOutputStream(
//				"/home/joao/datasets/conll/dataset.train.multi.arff"),
//				StandardCharsets.UTF_8);
//		// PrintWriter pw = new PrintWriter(new FileOutputStream(new
//		// File("/home/joao/CIKM_2018/datasets/experiments/fulldataset.multi.arff")));
//		pw.write("@RELATION multiclass_instances" + "\n");
//		pw.write("@ATTRIBUTE docid        STRING" + "\n");
//		pw.write("@ATTRIBUTE mention      STRING" + "\n");
//		pw.write("@ATTRIBUTE offset       NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE length       NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE granularity  NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE freq         NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE docfreq      NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE numcand      NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE normpos      NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE sentsize     NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE docsize     NUMERIC" + "\n");
//		// pw.println("@ATTRIBUTE doctopic     {Arts,Automobiles,Books,Business,Education,Health,Home_and_Garden,Job_Market,Magazine,Miscelaneous,Movies,New_York_and_Region,Obituaries,Real_Estate,Science,Sports,Style,Technology,Theater,Travel,Week_in_Review,World}");
//		pw.write("@ATTRIBUTE docmentions    NUMERIC" + "\n");
//		// pw.println("@ATTRIBUTE pub_date     NUMERIC");
//		// pw.println("@ATTRIBUTE tdf  NUMERIC");
//		// pw.println("@ATTRIBUTE min_jcc_change    NUMERIC");
//		// pw.println("@ATTRIBUTE max_jcc_change    NUMERIC");
//		// pw.println("@ATTRIBUTE avg_jcc_change    NUMERIC");
//		pw.write("@ATTRIBUTE class        {EASY,MEDIUM,HARD}" + "\n");
//		pw.write("\n");
//		pw.write("@DATA" + "\n");
//
//		TreeMap<String, String> hashTreeMap = new TreeMap<String, String>();
//		AIDA_YAGO2_annotations_Parser p = new AIDA_YAGO2_annotations_Parser();
//		LinkedList<GenericDocument> ConllDataSet = p
//				.parseDataset("/home/joao/datasets/conll/AIDA-YAGO2-dataset.tsv");
//
//		for (int i = 0; i < 1162; i++) { // Writing only the training set
//											// !!!!for(int i = 1162; i <
//											// ConllDataSet.size(); i++){
//			GenericDocument CO = ConllDataSet.get(i);
//			String articletitle = CO.getTitle();
//			String articlecontent = CO.getTxtContent().trim();
//			hashTreeMap.put(articletitle, articlecontent);
//		}
//		// Features_CONLL.dumpWordCountFromCONLL();
//		// Features_CONLL.dumpNumRecognizedMentionsFromCONLL();
//		// Features_CONLL.dumpDocumentFrequencyFromCONLL();
//
//		// CANNOT CALCULATE NEIGHBORHOOD SCORE
//		// TreeMap<String, String> NeighborhoodScoreMap =
//		// loadNeighborhoodScore();
//
//		TObjectIntHashMap<String> DocWordCountMap = Features_CONLL.loadWordCount();
//		TObjectIntHashMap<String> NumRecogMentionMap = Features_CONLL.loadNumMentionsRecognized();
//		TObjectIntHashMap<String> MentionEntityCountMap = Features_CONLL.loadMentionEntityCountMap();
//		TObjectIntHashMap<String> DocFrequencyMap = Features_CONLL.loadDocFrequencyMap();
//
//		BufferedReader bffReader = new BufferedReader(
//				new InputStreamReader(
//						new FileInputStream(
//								"/home/joao/datasets/conll/mappings/conll_all_dataset.easy"),
//						StandardCharsets.UTF_8));
//		String line = "";
//		while ((line = bffReader.readLine()) != null) {
//			String[] elems = line.split("\t");
//			String docId = elems[0];
//			if (hashTreeMap.containsKey(docId)) {
//				String mention = elems[1];
//				String offset = elems[2];
//				// mentionsSET.add(mention);
//
//				/** Mention-based features **/
//				double mention_length = getMentionLength(mention);
//				double mention_gran = getMentionGranularity(mention);
//				String content = hashTreeMap.get(docId);
//				content = content.toLowerCase();
//				double mention_freq = getMentionFrequency(content, mention);
//				double mention_doc_freq = getDocFreq(DocFrequencyMap, mention);
//				double mention_num_cand = (double) MentionEntityCountMap
//						.get(mention);// getNumCandidates(mention);
//				String mention_norm_pos = getMentionNormalizedPosition(content,
//						offset);
//				double mention_sent_size = getSentenceSize(content, mention,
//						offset);
//
//				/** Document-based features **/
//				double doc_size = (double) DocWordCountMap.get(docId);
//				// String doc_topic = getDocumentTopic(docId);
//				double doc_numMentions = (double) NumRecogMentionMap.get(docId);// getNumRecogMentions(docId);
//
//				/** Temporal-based features **/
//				// double publication_date = getPublicationDate(docId);
//				// int year = DocMentionMap.get(docId+"\t"+mention);
//				// if(year == 0 ) {
//				// continue;
//				// }
//				pw.write("\"" + docId + "\"" + "," + "\"" + mention + "\""
//						+ "," + offset + "," + mention_length + ","
//						+ mention_gran + "," + mention_freq + ","
//						+ mention_doc_freq + "," + mention_num_cand + ","
//						+ mention_norm_pos + "," + mention_sent_size + ","
//						+ doc_size + "," + doc_numMentions + "," + "EASY"
//						+ "\n");
//				// System.out.println();
//
//			}
//		}
//		bffReader.close();
//
//		bffReader = new BufferedReader(
//				new InputStreamReader(
//						new FileInputStream(
//								"/home/joao/datasets/conll/mappings/conll_all_dataset.medium"),
//						StandardCharsets.UTF_8));
//		line = "";
//		while ((line = bffReader.readLine()) != null) {
//			String[] elems = line.split("\t");
//			String docId = elems[0];
//			if (hashTreeMap.containsKey(docId)) {
//
//				String mention = elems[1];
//				String offset = elems[2];
//				// mentionsSET.add(mention);
//
//				/** Mention-based features **/
//				double mention_length = getMentionLength(mention);
//				double mention_gran = getMentionGranularity(mention);
//				String content = hashTreeMap.get(docId);
//				content = content.toLowerCase();
//				double mention_freq = getMentionFrequency(content, mention);
//				double mention_doc_freq = getDocFreq(DocFrequencyMap, mention);
//				double mention_num_cand = (double) MentionEntityCountMap
//						.get(mention);// getNumCandidates(mention);
//				String mention_norm_pos = getMentionNormalizedPosition(content,
//						offset);
//				double mention_sent_size = getSentenceSize(content, mention,
//						offset);
//
//				/** Document-based features **/
//				double doc_size = (double) DocWordCountMap.get(docId);
//				// String doc_topic = getDocumentTopic(docId);
//				double doc_numMentions = (double) NumRecogMentionMap.get(docId);// getNumRecogMentions(docId);
//
//				/** Temporal-based features **/
//				// double publication_date = getPublicationDate(docId);
//				// int year = DocMentionMap.get(docId+"\t"+mention);
//				// if(year == 0 ) {
//				// continue;
//				// }
//				pw.write("\"" + docId + "\"" + "," + "\"" + mention + "\""
//						+ "," + offset + "," + mention_length + ","
//						+ mention_gran + "," + mention_freq + ","
//						+ mention_doc_freq + "," + mention_num_cand + ","
//						+ mention_norm_pos + "," + mention_sent_size + ","
//						+ doc_size + "," + doc_numMentions + "," + "MEDIUM"
//						+ "\n");
//			}
//		}
//		bffReader.close();
//
//		bffReader = new BufferedReader(
//				new InputStreamReader(
//						new FileInputStream(
//								"/home/joao/datasets/conll/mappings/conll_all_dataset.hard"),
//						StandardCharsets.UTF_8));
//		line = "";
//		while ((line = bffReader.readLine()) != null) {
//			String[] elems = line.split("\t");
//			String docId = elems[0];
//			if (hashTreeMap.containsKey(docId)) {
//
//				String mention = elems[1];
//				String offset = elems[2];
//				// mentionsSET.add(mention);
//
//				/** Mention-based features **/
//				double mention_length = getMentionLength(mention);
//				double mention_gran = getMentionGranularity(mention);
//				String content = hashTreeMap.get(docId);
//				content = content.toLowerCase();
//				double mention_freq = getMentionFrequency(content, mention);
//				double mention_doc_freq = getDocFreq(DocFrequencyMap, mention);
//				double mention_num_cand = (double) MentionEntityCountMap
//						.get(mention);// getNumCandidates(mention);
//				String mention_norm_pos = getMentionNormalizedPosition(content,
//						offset);
//				double mention_sent_size = getSentenceSize(content, mention,
//						offset);
//
//				/** Document-based features **/
//				double doc_size = (double) DocWordCountMap.get(docId);
//				// String doc_topic = getDocumentTopic(docId);
//				double doc_numMentions = (double) NumRecogMentionMap.get(docId);// getNumRecogMentions(docId);
//
//				/** Temporal-based features **/
//				// double publication_date = getPublicationDate(docId);
//				// int year = DocMentionMap.get(docId+"\t"+mention);
//				// if(year == 0 ) {
//				// continue;
//				// }
//				pw.write("\"" + docId + "\"" + "," + "\"" + mention + "\""
//						+ "," + offset + "," + mention_length + ","
//						+ mention_gran + "," + mention_freq + ","
//						+ mention_doc_freq + "," + mention_num_cand + ","
//						+ mention_norm_pos + "," + mention_sent_size + ","
//						+ doc_size + "," + doc_numMentions + "," + "HARD"
//						+ "\n");
//			}
//		}
//		bffReader.close();
//
//		pw.flush();
//		pw.close();
//
//		// BINARY ##########################
//		pw = new OutputStreamWriter(new FileOutputStream(
//				"/home/joao/datasets/conll/dataset.train.bin.arff"),
//				StandardCharsets.UTF_8);
//
//		// PrintWriter pw = new PrintWriter(new FileOutputStream(new
//		// File("/home/joao/CIKM_2018/datasets/experiments/fulldataset.multi.arff")));
//		pw.write("@RELATION binclass_instances" + "\n");
//		pw.write("@ATTRIBUTE docid        STRING" + "\n");
//		pw.write("@ATTRIBUTE mention      STRING" + "\n");
//		pw.write("@ATTRIBUTE offset       NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE length       NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE granularity  NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE freq         NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE docfreq      NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE numcand      NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE normpos      NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE sentsize     NUMERIC" + "\n");
//		pw.write("@ATTRIBUTE docsize     NUMERIC" + "\n");
//		// pw.println("@ATTRIBUTE doctopic     {Arts,Automobiles,Books,Business,Education,Health,Home_and_Garden,Job_Market,Magazine,Miscelaneous,Movies,New_York_and_Region,Obituaries,Real_Estate,Science,Sports,Style,Technology,Theater,Travel,Week_in_Review,World}");
//		pw.write("@ATTRIBUTE docmentions    NUMERIC" + "\n");
//		// pw.println("@ATTRIBUTE pub_date     NUMERIC");
//		// pw.println("@ATTRIBUTE tdf  NUMERIC");
//		// pw.println("@ATTRIBUTE min_jcc_change    NUMERIC");
//		// pw.println("@ATTRIBUTE max_jcc_change    NUMERIC");
//		// pw.println("@ATTRIBUTE avg_jcc_change    NUMERIC");
//		pw.write("@ATTRIBUTE class        {NOT_HARD,HARD}" + "\n");
//		pw.write("\n");
//		pw.write("@DATA" + "\n");
//
//		hashTreeMap = new TreeMap<String, String>();
//		// AIDA_YAGO2_annotations_Parser p = new
//		// AIDA_YAGO2_annotations_Parser();
//		// LinkedList<ConllDocument> ConllDataSet =
//		// p.parseDataset("/home/joao/datasets/conll/AIDA-YAGO2-dataset.tsv");
//		for (int i = 0; i < 1162; i++) { // Writing only the training set
//											// !!!!for(int i = 1162; i <
//											// ConllDataSet.size(); i++){
//			GenericDocument CO = ConllDataSet.get(i);
//			String articletitle = CO.getTitle();
//			String articlecontent = CO.getTxtContent().trim();
//			hashTreeMap.put(articletitle, articlecontent);
//		}
//		// Features_CONLL.dumpWordCountFromCONLL();
//		// Features_CONLL.dumpNumRecognizedMentionsFromCONLL();
//		// Features_CONLL.dumpDocumentFrequencyFromCONLL();
//
//		// CANNOT CALCULATE NEIGHBORHOOD SCORE
//		// TreeMap<String, String> NeighborhoodScoreMap =
//		// loadNeighborhoodScore();
//
//		// TObjectIntHashMap<String> DocWordCountMap =
//		// Features_CONLL.loadWordCount();
//		// TObjectIntHashMap<String> NumRecogMentionMap =
//		// Features_CONLL.loadNumMentionsRecognized();
//		// TObjectIntHashMap<String> MentionEntityCountMap =
//		// Features_CONLL.loadMentionEntityCountMap();
//		// TObjectIntHashMap<String> DocFrequencyMap =
//		// Features_CONLL.loadDocFrequencyMap();
//
//		bffReader = new BufferedReader(
//				new InputStreamReader(
//						new FileInputStream(
//								"/home/joao/datasets/conll/mappings/conll_all_dataset.easy"),
//						StandardCharsets.UTF_8));
//		line = "";
//		while ((line = bffReader.readLine()) != null) {
//			String[] elems = line.split("\t");
//			String docId = elems[0];
//			if (hashTreeMap.containsKey(docId)) {
//
//				String mention = elems[1];
//				String offset = elems[2];
//				// mentionsSET.add(mention);
//
//				/** Mention-based features **/
//				double mention_length = getMentionLength(mention);
//				double mention_gran = getMentionGranularity(mention);
//				String content = hashTreeMap.get(docId);
//				content = content.toLowerCase();
//				double mention_freq = getMentionFrequency(content, mention);
//				double mention_doc_freq = getDocFreq(DocFrequencyMap, mention);
//				double mention_num_cand = (double) MentionEntityCountMap
//						.get(mention);// getNumCandidates(mention);
//				String mention_norm_pos = getMentionNormalizedPosition(content,
//						offset);
//				double mention_sent_size = getSentenceSize(content, mention,
//						offset);
//
//				/** Document-based features **/
//				double doc_size = (double) DocWordCountMap.get(docId);
//				// String doc_topic = getDocumentTopic(docId);
//				double doc_numMentions = (double) NumRecogMentionMap.get(docId);// getNumRecogMentions(docId);
//
//				/** Temporal-based features **/
//				// double publication_date = getPublicationDate(docId);
//				// int year = DocMentionMap.get(docId+"\t"+mention);
//				// if(year == 0 ) {
//				// continue;
//				// }
//				pw.write("\"" + docId + "\"" + "," + "\"" + mention + "\""
//						+ "," + offset + "," + mention_length + ","
//						+ mention_gran + "," + mention_freq + ","
//						+ mention_doc_freq + "," + mention_num_cand + ","
//						+ mention_norm_pos + "," + mention_sent_size + ","
//						+ doc_size + "," + doc_numMentions + "," + "NOT_HARD"
//						+ "\n");
//				// System.out.println();
//			}
//
//		}
//		bffReader.close();
//
//		bffReader = new BufferedReader(
//				new InputStreamReader(
//						new FileInputStream(
//								"/home/joao/datasets/conll/mappings/conll_all_dataset.medium"),
//						StandardCharsets.UTF_8));
//		line = "";
//		while ((line = bffReader.readLine()) != null) {
//			String[] elems = line.split("\t");
//			String docId = elems[0];
//			if (hashTreeMap.containsKey(docId)) {
//
//				String mention = elems[1];
//				String offset = elems[2];
//				// mentionsSET.add(mention);
//
//				/** Mention-based features **/
//				double mention_length = getMentionLength(mention);
//				double mention_gran = getMentionGranularity(mention);
//				String content = hashTreeMap.get(docId);
//				content = content.toLowerCase();
//				double mention_freq = getMentionFrequency(content, mention);
//				double mention_doc_freq = getDocFreq(DocFrequencyMap, mention);
//				double mention_num_cand = (double) MentionEntityCountMap
//						.get(mention);// getNumCandidates(mention);
//				String mention_norm_pos = getMentionNormalizedPosition(content,
//						offset);
//				double mention_sent_size = getSentenceSize(content, mention,
//						offset);
//
//				/** Document-based features **/
//				double doc_size = (double) DocWordCountMap.get(docId);
//				// String doc_topic = getDocumentTopic(docId);
//				double doc_numMentions = (double) NumRecogMentionMap.get(docId);// getNumRecogMentions(docId);
//
//				/** Temporal-based features **/
//				// double publication_date = getPublicationDate(docId);
//				// int year = DocMentionMap.get(docId+"\t"+mention);
//				// if(year == 0 ) {
//				// continue;
//				// }
//				pw.write("\"" + docId + "\"" + "," + "\"" + mention + "\""
//						+ "," + offset + "," + mention_length + ","
//						+ mention_gran + "," + mention_freq + ","
//						+ mention_doc_freq + "," + mention_num_cand + ","
//						+ mention_norm_pos + "," + mention_sent_size + ","
//						+ doc_size + "," + doc_numMentions + "," + "NOT_HARD"
//						+ "\n");
//			}
//		}
//		bffReader.close();
//
//		bffReader = new BufferedReader(
//				new InputStreamReader(
//						new FileInputStream(
//								"/home/joao/datasets/conll/mappings/conll_all_dataset.hard"),
//						StandardCharsets.UTF_8));
//		line = "";
//		while ((line = bffReader.readLine()) != null) {
//			String[] elems = line.split("\t");
//			String docId = elems[0];
//			if (hashTreeMap.containsKey(docId)) {
//
//				String mention = elems[1];
//				String offset = elems[2];
//				// mentionsSET.add(mention);
//
//				/** Mention-based features **/
//				double mention_length = getMentionLength(mention);
//				double mention_gran = getMentionGranularity(mention);
//				String content = hashTreeMap.get(docId);
//				content = content.toLowerCase();
//				double mention_freq = getMentionFrequency(content, mention);
//				double mention_doc_freq = getDocFreq(DocFrequencyMap, mention);
//				double mention_num_cand = (double) MentionEntityCountMap
//						.get(mention);// getNumCandidates(mention);
//				String mention_norm_pos = getMentionNormalizedPosition(content,
//						offset);
//				double mention_sent_size = getSentenceSize(content, mention,
//						offset);
//
//				/** Document-based features **/
//				double doc_size = (double) DocWordCountMap.get(docId);
//				String doc_topic = getDocumentTopic(docId);
//				double doc_numMentions = (double) NumRecogMentionMap.get(docId);// getNumRecogMentions(docId);
//
//				/** Temporal-based features **/
//				// double publication_date = getPublicationDate(docId);
//				// int year = DocMentionMap.get(docId+"\t"+mention);
//				// if(year == 0 ) {
//				// continue;
//				// }
//				pw.write("\"" + docId + "\"" + "," + "\"" + mention + "\""
//						+ "," + offset + "," + mention_length + ","
//						+ mention_gran + "," + mention_freq + ","
//						+ mention_doc_freq + "," + mention_num_cand + ","
//						+ mention_norm_pos + "," + mention_sent_size + ","
//						+ doc_size + "," + doc_numMentions + "," + "HARD"
//						+ "\n");
//			}
//		}
//		bffReader.close();
//
//		pw.flush();
//		pw.close();
//
//	}
//	
	
	
	
//	/**
//	 *	This utility function is meant to fetch the number of recognized mentions per document in the CONLL corpus.
//	 *	
//	 *	It produces the file:
//	 *
//	 *			/home/joao/datasets/conll/num_recognized_mentions.tsv
//	 *
//	 * @throws NumberFormatException 
//	 * @throws IOException
//	 * @throws CompressorException
//	 */
//	public static void dumpNumRecognizedMentionsFromCONLL() throws NumberFormatException, IOException{
//		System.out.println("Dumping the number of recognized mentions per document.");
//
//		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/conll/num_recognized_mentions.tsv"),StandardCharsets.UTF_8);
//		HashMap<String,Integer> numMentionsDoc = new HashMap<String,Integer>();
//		
//		//Lets start with the easy cases //
//		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/annotations/agreements/all/conll_ALL.aggreement"),StandardCharsets.UTF_8));
//		String line = "";
//		while((line = bffReader.readLine() )!= null) {
//			String[] elems = line.split("\t");
//			String docId = elems[0];
//			String mention = elems[1];
//			String offset = elems[2];				
//			String concatStr = docId+"\t"+mention+"\t"+offset;				
//			Integer counter = numMentionsDoc.get(concatStr);
//			if(counter == null) {
//				counter = 1;
//			}else {
//				counter += 1;
//			}
//			numMentionsDoc.put(concatStr, counter);
//		}
//		bffReader.close();
//		System.out.println("Done with EASY");
//		//Now lets go for the hard cases //
//		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/annotations/disagreements/all/conll_ALL.disag"),StandardCharsets.UTF_8));
//		line = "";
//		while((line = bffReader.readLine() )!= null) {
//			String[] elems = line.split("\t");
//			String docId = elems[0];
//			String mention = elems[1];
//			String offset = elems[2];				
//			String concatStr = docId+"\t"+mention+"\t"+offset;
//			Integer counter = numMentionsDoc.get(concatStr);
//			if(counter == null) {
//				counter = 1;
//			}else {
//				counter += 1;
//			}
//			numMentionsDoc.put(concatStr, counter);
//		}
//		bffReader.close();
//		System.out.println("Done with HARD");
//		
//		//Now lets go for the medium cases //
//		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/annotations/disagreements/ambiv_babel/conll_ambiv_babel.disag"),StandardCharsets.UTF_8));
//		line = "";
//		while((line = bffReader.readLine() )!= null) {
//			String[] elems = line.split("\t");
//			String docId = elems[0];
//			String mention = elems[1];
//			String offset = elems[2];
//			
//			String concatStr = docId+"\t"+mention+"\t"+offset;
//			
//			Integer counter = numMentionsDoc.get(concatStr);
//			if(counter == null) {
//				counter = 1;
//			}else {
//				counter += 1;
//			}
//			numMentionsDoc.put(concatStr, counter);
//		}
//		bffReader.close();
//
//		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/annotations/disagreements/ambiv_tagme/conll_ambiv_tagme.disag"),StandardCharsets.UTF_8));
//		line = "";
//		while((line = bffReader.readLine() )!= null) {
//			String[] elems = line.split("\t");
//			String docId = elems[0];
//			String mention = elems[1];
//			String offset = elems[2];
//			
//			String concatStr = docId+"\t"+mention+"\t"+offset;
//			
//			Integer counter = numMentionsDoc.get(concatStr);
//			if(counter == null) {
//				counter = 1;
//			}else {
//				counter += 1;
//			}
//			numMentionsDoc.put(concatStr, counter);
//		}
//		bffReader.close();
//
//		bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/conll/annotations/disagreements/babel_tagme/conll_babel_tagme.disag"),StandardCharsets.UTF_8));
//		line = "";
//		while((line = bffReader.readLine() )!= null) {
//			String[] elems = line.split("\t");
//			String docId = elems[0];
//			String mention = elems[1];
//			String offset = elems[2];
//			
//			String concatStr = docId+"\t"+mention+"\t"+offset;
//			
//			Integer counter = numMentionsDoc.get(concatStr);
//			if(counter == null) {
//				counter = 1;
//			}else {
//				counter += 1;
//			}
//			numMentionsDoc.put(concatStr, counter);
//		}
//		bffReader.close();
//		System.out.println("Done with MEDIUM");
//		
//		
//		HashMap<String,Integer> numMentionsMap = new HashMap<>();
//		Iterator<?> it = numMentionsDoc.entrySet().iterator();
//		while (it.hasNext()) {
//			@SuppressWarnings("rawtypes")
//			Map.Entry pair = (Map.Entry)it.next();
//	    	String chave  = (String) pair.getKey();
//	    	//Integer num = (Integer)pair.getValue();	
//	    	String[] elements = chave.split("\t");
//	    	String docId = elements[0];
//	    	
//	    	Integer counter = numMentionsMap.get(docId);
//			if(counter == null) {
//				counter = 1;
//			}else {
//				counter += 1;
//			}
//			numMentionsMap.put(docId, counter);
//	    	it.remove(); // avoids a ConcurrentModificationException
//		}
//		
//		it = numMentionsMap.entrySet().iterator();
//		while (it.hasNext()) {
//			@SuppressWarnings("rawtypes")
//			Map.Entry pair = (Map.Entry)it.next();
//	    	String docId  = (String) pair.getKey();
//	    	Integer num = (Integer)pair.getValue();	
//			
//	    	pAnn.write(docId+"\t"+num+"\n");
//	    	
//	    	it.remove(); // avoids a ConcurrentModificationException
//		}
//		
//		pAnn.flush();
//		pAnn.close();
//		System.out.println("... Done.");
//	}
//	
	
	
	




}
