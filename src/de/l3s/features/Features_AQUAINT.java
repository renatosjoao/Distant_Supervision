package de.l3s.features;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.io.FileUtils;
import de.l3s.loaders.DataLoaders_AQUAINT;

public class Features_AQUAINT extends Features {
	private static TreeMap<String,String> GTMAP;

	
	public static void main(String[] args) throws NumberFormatException, IOException, CompressorException {

		DataLoaders_AQUAINT d = new DataLoaders_AQUAINT();
		GTMAP = new TreeMap<String, String>();
		GTMAP = d.getGT_MAP();
		writeTrainingAndTestSets(d);
	}
	
	
	
	



	public Features_AQUAINT() throws CompressorException, IOException {
		super();
	}
	

	
	/**
	 *
	 * @throws IOException
	 * @throws CompressorException
	 */
	public static void writeTrainingAndTestSets(DataLoaders_AQUAINT d) throws IOException, CompressorException{	
		OutputStreamWriter pw = new OutputStreamWriter(new FileOutputStream("./resources/aquaint/dataset.meta.train.aquaint.csv"),StandardCharsets.UTF_8);		
		OutputStreamWriter pwTest = new OutputStreamWriter(new FileOutputStream("./resources/aquaint/dataset.meta.test.aquaint.csv"),StandardCharsets.UTF_8);

		////////////TRAIN
		TreeMap<String,String> DiffMap = new TreeMap<String, String>();
		BufferedReader trainbuffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/aquaint/mention_correct.train.tsv"),StandardCharsets.UTF_8));

		String line = "";
		while((line = trainbuffReader.readLine() )!= null) {
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
			String vals = a+"\t"+b+"\t"+t +"\t"+totA+"\t"+totB+"\t"+totT+"\t"+normA+"\t"+normB+"\t"+normT;
			DiffMap.put(mention, vals);
			}
		trainbuffReader.close();
		
		TObjectIntHashMap<String> DocWordCountMap = d.getDocWordCountMap();
		TObjectIntHashMap<String> NumRecogMentionMap = d.getNumRecogMentionMap();
		TObjectIntHashMap<String> MentionEntityCountMap = Features.loadMentionEntityCountMap();
		TObjectIntHashMap<String> DocFrequencyMap = d.getDocFrequencyMap();
		TreeMap<String, String> hashTreeMap = d.getDocsContent();
		
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/aquaint/AQUAINT_GT.train.9.tsv"),StandardCharsets.UTF_8));
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
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String mention = elems[1].toLowerCase();
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
			
//			double tf_idf = getTFxIDF(content, mention, mention_doc_freq, corpus_size);
//			double tf = getTermFrequency(content, mention);
			
			String GTLink = GTMAP.get(key).toLowerCase();
			String elements = null;
			
			try{
				elements = DiffMap.get(mention);
			}catch(Exception e){
				continue;
			}
			
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
//		String doc_lex  =  getDocumentLex(content);
		double doc_numMentions = (double)NumRecogMentionMap.get(docId);//getNumRecogMentions(docId);
		pw.write("\""+docId + "\"" + "," + "\"" +mention +"\"" + "," +offset + "," + "\"" + GTLink  +"\""+"," + mention_length+ ","+mention_gran+ ","+mention_freq+ ","+mention_doc_freq+ ","+mention_num_cand + ","+mention_norm_pos +","+ mention_sent_size +","+a+","+b+","+t+","+normA+","+normB+","+normT+ "," + totA+ "," +  totB + "," + totT+ "," + doc_size+","+doc_numMentions+ "\n");
	}
	
	bffReader.close();
	pw.flush();
	pw.close();
	System.out.println("Finished writing the training set");
//	
//	/////// TEST
	line = "";
	
	bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/aquaint/AQUAINT_GT.test.1.tsv"),StandardCharsets.UTF_8));
	line = "";
	while ((line = bffReader.readLine()) != null) {
		String[] elems = line.split("\t");
		String docId = elems[0].toLowerCase();
		docId = docId.replaceAll("\'", "");
		docId = docId.replaceAll("\"", "");
		String mention = elems[1].toLowerCase();
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
	
		String GTLink = GTMAP.get(key).toLowerCase();
		String elements = null;
		try{
			elements = DiffMap.get(mention);
		}catch(Exception e){
			continue;
		}
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
//		String doc_lex  =  getDocumentLex(content);
		double doc_numMentions = (double)NumRecogMentionMap.get(docId);//getNumRecogMentions(docId);
		pwTest.write("\""+docId + "\"" + "," + "\"" +mention +"\"" + "," +offset + "," + "\"" + GTLink  +"\""+"," + mention_length+ ","+mention_gran+ ","+mention_freq+ ","+mention_doc_freq+ ","+mention_num_cand + ","+mention_norm_pos +","+ mention_sent_size +","+a+","+b+","+t+","+normA+","+normB+","+normT+ "," + totA+ "," +  totB + "," + totT+ "," + doc_size+","+doc_numMentions+ "\n");
	}
	bffReader.close();
	pwTest.flush();
	pwTest.close();	
	System.out.println("Finished writing the test set");

	}
	
	
	

}
