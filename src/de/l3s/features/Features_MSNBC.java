package de.l3s.features;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.io.FileUtils;
import de.l3s.loaders.DataLoaders_MSNBC;

public class Features_MSNBC {
	
	
	private static TreeMap<String,String> GTMAP = null;
	
	public static void main(String[] args) throws NumberFormatException, IOException, CompressorException {

		
		DataLoaders_MSNBC d = new DataLoaders_MSNBC();
		GTMAP = new TreeMap<String, String>();
		GTMAP = d.getGT_MAP();
		
		
		
		writeTrainingAndTestSets(d);
	}
	
	
	public static void writeTrainingAndTestSets(DataLoaders_MSNBC d) throws IOException, CompressorException{	
		OutputStreamWriter pw = new OutputStreamWriter(new FileOutputStream("./resources/msnbc/dataset.meta.train.msnbc.csv"),StandardCharsets.UTF_8);		
		OutputStreamWriter pwTest = new OutputStreamWriter(new FileOutputStream("./resources/msnbc/dataset.meta.test.msnbc.csv"),StandardCharsets.UTF_8);

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
		
		TObjectIntHashMap<String> DocWordCountMap = d.getDocWordCountMap();
		TObjectIntHashMap<String> NumRecogMentionMap = d.getNumRecogMentionMap();
		TObjectIntHashMap<String> MentionEntityCountMap = Features.loadMentionEntityCountMap();
		TObjectIntHashMap<String> DocFrequencyMap = d.getDocFrequencyMap();
		TreeMap<String, String> hashTreeMap = d.getDocsContent();
		
		////////////TRAIN
		TreeMap<String,String> DiffMap = new TreeMap<String, String>();
		BufferedReader trainbuffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/msnbc/mention_correct.train.tsv"),StandardCharsets.UTF_8));
		String line = "";
		while((line = trainbuffReader.readLine() )!= null) {
			String[] elems = line.split("\t");
			mention = elems[0].toLowerCase();
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
	
		System.out.println(hashTreeMap.keySet().size());
		
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/msnbc/MSNBC_GT_train.tsv"),StandardCharsets.UTF_8));
		
		line = "";
		while ((line = bffReader.readLine()) != null) {	
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			mention = elems[1].toLowerCase();
			String offset = elems[2];
			String link = elems[3];
			link = link.replaceAll("_"," ").toLowerCase();
			String key = docId + "\t" + mention + "\t" + offset;
		
			/** Mention-based features **/
			double mention_length =  Features.getMentionLength(mention);
			double mention_gran =    Features.getMentionGranularity(mention);
			String content = hashTreeMap.get(docId);
			content = content.toLowerCase();
			double mention_freq =    Features.getMentionFrequency(content, mention);
			double mention_doc_freq =  Features.getDocFreq(DocFrequencyMap,mention);
			double mention_num_cand =  (double)MentionEntityCountMap.get(mention);//getNumCandidates(mention);
			String mention_norm_pos =  Features.getMentionNormalizedPosition(content,offset);
			double mention_sent_size = Features.getSentenceSize(content, mention, offset);
			
			String GTLink = GTMAP.get(key).toLowerCase();
			
			String elements = null;
			try{
				elements = DiffMap.get(mention);
			}catch(Exception e){
				continue;
			}
//			System.out.println(key+"\t"+GTLink+"\t"+elements);
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
//		/** Document-based features **/
		double doc_size = (double) DocWordCountMap.get(docId);
		double doc_numMentions = (double)NumRecogMentionMap.get(docId);//getNumRecogMentions(docId);
		pw.write("\""+docId + "\"" + "," + "\"" +mention +"\"" + "," +offset + "," + "\"" + GTLink  +"\""+"," + mention_length+ ","+mention_gran+ ","+mention_freq+ ","+mention_doc_freq+ ","+mention_num_cand + ","+mention_norm_pos +","+ mention_sent_size +","+a+","+b+","+t+","+normA+","+normB+","+normT+ "," + totA+ "," +  totB + "," + totT+ "," + doc_size+","+doc_numMentions+ "\n");
	}
	
	bffReader.close();
	pw.flush();
	pw.close();
	System.out.println("Finished writing the training set");
	
/////// TEST
	line = "";
	
	bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/msnbc/MSNBC_GT_test.tsv"),StandardCharsets.UTF_8));
	line = "";
	while ((line = bffReader.readLine()) != null) {
		String[] elems = line.split("\t");
		String docId = elems[0].toLowerCase();
		docId = docId.replaceAll("\'", "");
		docId = docId.replaceAll("\"", "");
		mention = elems[1].toLowerCase();
		String offset = elems[2];
		String link = elems[3];
		link = link.replaceAll("_"," ").toLowerCase();
		String key = docId + "\t" + mention + "\t" + offset;
	
		/** Mention-based features **/
		double mention_length =  Features.getMentionLength(mention);
		double mention_gran =    Features.getMentionGranularity(mention);
		String content = hashTreeMap.get(docId);
		content = content.toLowerCase();
		double mention_freq =    Features.getMentionFrequency(content, mention);
		double mention_doc_freq =  Features.getDocFreq(DocFrequencyMap,mention);
		double mention_num_cand =  (double)MentionEntityCountMap.get(mention);//getNumCandidates(mention);
		String mention_norm_pos =  Features.getMentionNormalizedPosition(content,offset);
		double mention_sent_size = Features.getSentenceSize(content, mention, offset);
		
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
