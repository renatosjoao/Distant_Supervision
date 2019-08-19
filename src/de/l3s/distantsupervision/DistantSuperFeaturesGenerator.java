package de.l3s.distantsupervision;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.compress.compressors.CompressorException;
import org.xml.sax.SAXException;

import com.opencsv.CSVWriter;

import de.l3s.features.Features;
import de.l3s.loaders.DataLoaders;
import de.l3s.loaders.DataLoaders_ACE2004;
import de.l3s.loaders.DataLoaders_AQUAINT;
import de.l3s.loaders.DataLoaders_CONLL;
import de.l3s.loaders.DataLoaders_Derczynski;
import de.l3s.loaders.DataLoaders_GERDAQ;
import de.l3s.loaders.DataLoaders_IITB;
import de.l3s.loaders.DataLoaders_KORE50;
import de.l3s.loaders.DataLoaders_MSNBC;
import de.l3s.loaders.DataLoaders_N3RSS500;
import de.l3s.loaders.DataLoaders_NEEL;
import de.l3s.loaders.DataLoaders_Spotlight;
import de.l3s.loaders.DataLoaders_N3Reuters128;
import de.l3s.loaders.DataLoaders_WP;

public class DistantSuperFeaturesGenerator {
	
	
	public static void main (String[] args) throws CompressorException, IOException, NumberFormatException, SAXException, ParserConfigurationException{
		String corpus = "ace2004";
//		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
//		String corpus = "conll";   //Babelfy > Ambiverse > Spotlight >  Tagme
//		String corpus = "derczynkski";
//		String corpus = "gerdaq";  // Tagme > Babelfy > Ambiverse 
//		String corpus = "iitb";    //Ambiverse > Spotlight >  Tagme > Babelfy
//		String corpus = "kore50";
// 		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
//		String corpus = "N3News100";   			// 	GERMAN NEWS
//		String corpus = "N3Reuters128";
//		String corpus = "N3RSS500";
//	    String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
//		String corpus = "spotlight" ;
// 		String corpus = "wp";      // Tagme > Babelfy > Ambiverse > Spotlight
		DataLoaders d = new DataLoaders();
		if(corpus.equalsIgnoreCase("ace2004")){
			d = DataLoaders_ACE2004.getInstance();
		}
		if(corpus.equalsIgnoreCase("aquaint")){
			d = DataLoaders_AQUAINT.getInstance();
		}
		if(corpus.equalsIgnoreCase("conll")){
			d = DataLoaders_CONLL.getInstance();
		}
		if(corpus.equalsIgnoreCase("derczynski")){
			d = DataLoaders_Derczynski.getInstance();
		}
		if(corpus.equalsIgnoreCase("gerdaq")){
			d = DataLoaders_GERDAQ.getInstance();
		}
		if(corpus.equalsIgnoreCase("iitb")){
			d = DataLoaders_IITB.getInstance();
		}
		if(corpus.equalsIgnoreCase("kore50")){
			d = DataLoaders_KORE50.getInstance();
		}
		if(corpus.equalsIgnoreCase("msnbc")){
			d = DataLoaders_MSNBC.getInstance();
		}
		if(corpus.equalsIgnoreCase("N3Reuters128")){
			d = DataLoaders_N3Reuters128.getInstance();
		}
		if(corpus.equalsIgnoreCase("N3RSS500")){
			d = DataLoaders_N3RSS500.getInstance();
		}
		if(corpus.equalsIgnoreCase("neel")){
			d = DataLoaders_NEEL.getInstance();
		}
		if(corpus.equalsIgnoreCase("spotlight")){
			d = DataLoaders_Spotlight.getInstance();
		}
		if(corpus.equalsIgnoreCase("wp")){
			d = DataLoaders_WP.getInstance();
		}
//		dumpDocumentFrequency(corpus,d);
		
		double[] p = new double[]{1.0,5.0,10.0,15.0,20.0,30.0,40.0,50.0,60.0,70.0,80.0,90.0,100.0};
		
		for(double dd : p){
			writeTrainingFeaturesDistantSupervision(d,dd,corpus);
		}
		
		writeTestFeaturesDistantSupervision(d, corpus);
	}
	
	
	
	
	/**
	 *  This method is supposed to write the features for the test set
	 * 
	 * @param d
	 * @param corpus
	 * @throws IOException
	 * @throws CompressorException
	 * @throws NumberFormatException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public static void writeTestFeaturesDistantSupervision(DataLoaders d , String corpus) throws IOException, CompressorException, NumberFormatException, SAXException, ParserConfigurationException{
		
		OutputStreamWriter pwTEST = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.meta.test."+corpus+".csv"),StandardCharsets.UTF_8);	
		CSVWriter csvWriter = new CSVWriter(pwTEST,  ',' ,  '\'', '\\');

		TreeMap<String,String> GTMAP = d.getGT_MAP();

		TObjectIntHashMap<String> DocWordCountMap = null;
		TObjectIntHashMap<String> NumRecogMentionMap = null;
		TObjectIntHashMap<String> MentionEntityCountMap = null;
		TObjectIntHashMap<String> DocFrequencyMap = null;
		TreeMap<String,String> DocContent = null;
		
		
		if(corpus.equalsIgnoreCase("ace2004")){
			DocWordCountMap = ((DataLoaders_ACE2004) d).getDocWordCountMap();
			NumRecogMentionMap = ((DataLoaders_ACE2004) d).getNumRecogMentionMap();
			MentionEntityCountMap = Features.loadMentionEntityCountMap();
			DocFrequencyMap = ((DataLoaders_ACE2004) d).getDocFrequencyMap();
			DocContent = ((DataLoaders_ACE2004) d).getDocsContent();
		}
		if(corpus.equalsIgnoreCase("aquaint")){
			DocWordCountMap = ((DataLoaders_AQUAINT) d).getDocWordCountMap();
			NumRecogMentionMap = ((DataLoaders_AQUAINT) d).getNumRecogMentionMap();
			MentionEntityCountMap = Features.loadMentionEntityCountMap();
			DocFrequencyMap = ((DataLoaders_AQUAINT) d).getDocFrequencyMap();
			DocContent = ((DataLoaders_AQUAINT) d).getDocsContent();
		}
		
		if(corpus.equalsIgnoreCase("conll")){
			DocWordCountMap = ((DataLoaders_CONLL) d).getDocWordCountMap();
			NumRecogMentionMap = ((DataLoaders_CONLL) d).getNumRecogMentionMap();
			MentionEntityCountMap = Features.loadMentionEntityCountMap();
			DocFrequencyMap = ((DataLoaders_CONLL) d).getDocFrequencyMap();
			DocContent = ((DataLoaders_CONLL) d).getDocsContent();
		}
		if(corpus.equalsIgnoreCase("iitb")){
			DocWordCountMap = ((DataLoaders_IITB) d).getDocWordCountMap();
			NumRecogMentionMap = ((DataLoaders_IITB) d).getNumRecogMentionMap();
			MentionEntityCountMap =  Features.loadMentionEntityCountMap();
			DocFrequencyMap = ((DataLoaders_IITB) d).getDocFrequencyMap();
			DocContent = ((DataLoaders_IITB) d).getDocsContent();
		}
		if(corpus.equalsIgnoreCase("kore50")){
			DocWordCountMap = ((DataLoaders_KORE50) d).getDocWordCountMap();
			NumRecogMentionMap = ((DataLoaders_KORE50) d).getNumRecogMentionMap();
			MentionEntityCountMap =  Features.loadMentionEntityCountMap();
			DocFrequencyMap = ((DataLoaders_KORE50) d).getDocFrequencyMap();
			DocContent = ((DataLoaders_KORE50) d).getDocsContent();
		}
		if(corpus.equalsIgnoreCase("msnbc")){
			DocWordCountMap = ((DataLoaders_MSNBC) d).getDocWordCountMap();
			NumRecogMentionMap = ((DataLoaders_MSNBC) d).getNumRecogMentionMap();
			MentionEntityCountMap = Features.loadMentionEntityCountMap();
			DocFrequencyMap = ((DataLoaders_MSNBC) d).getDocFrequencyMap();
			DocContent = ((DataLoaders_MSNBC) d).getDocsContent();
		}
		if(corpus.equalsIgnoreCase("neel")){
			DocWordCountMap = ((DataLoaders_NEEL) d).getDocWordCountMap();
			NumRecogMentionMap = ((DataLoaders_NEEL) d).getNumRecogMentionMap();
			MentionEntityCountMap = Features.loadMentionEntityCountMap();
			DocFrequencyMap = ((DataLoaders_NEEL) d).getDocFrequencyMap();
			DocContent = ((DataLoaders_NEEL) d).getDocsContent();
		}
		if(corpus.equalsIgnoreCase("wp")){
			DocWordCountMap = ((DataLoaders_WP) d).getDocWordCountMap();
			NumRecogMentionMap = ((DataLoaders_WP) d).getNumRecogMentionMap();
			MentionEntityCountMap = Features.loadMentionEntityCountMap();
			DocFrequencyMap = ((DataLoaders_WP) d).getDocFrequencyMap();
			DocContent = ((DataLoaders_WP) d).getDocsContent();
		}


		Iterator<?> it = GTMAP.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
	    	String key = (String) pair.getKey();
	    	String[] elems = key.split("\t");
			String docid = elems[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			String mention = elems[1].toLowerCase();
			String offset = elems[2];

			String GTLink = "NULL";
			/** Mention-based features **/
			double slen =  getMentionLength(mention);
			double swords =    getMentionGranularity(mention);
			String content = DocContent.get(docid);
			content = content.toLowerCase();
			double sf =    getMentionFrequency(content, mention);
			double sdf =  getDocFreq(DocFrequencyMap,mention);
			double scand =  (double)MentionEntityCountMap.get(mention);//getNumCandidates(mention);
			String mpos =  getMentionNormalizedPosition(content,offset);
			double msent = getSentenceSize(content, mention, offset);
			
			/** Document-based features **/
			double dwords = (double) DocWordCountMap.get(docid);
			double dents = (double)NumRecogMentionMap.get(docid);//getNumRecogMentions(docId);
			
			csvWriter.writeNext(new String[]{docid,mention,offset,GTLink,Double.toString(slen),Double.toString(swords),Double.toString(sf),Double.toString(sdf),Double.toString(scand),mpos,Double.toString(msent),Double.toString(dwords),Double.toString(dents)});

			it.remove();
		}
		csvWriter.close();
		System.out.println("Finished writing the features for the test set.");

	}

	
	
	/**
	 *			This method is meant to calculate the features for the training part of the distant supervision approach.
	 *
	 * @throws IOException
	 * @throws CompressorException
	 */
	
	public static void writeTrainingFeaturesDistantSupervision(DataLoaders d, double percent,String corpus) throws IOException, CompressorException{
		
		OutputStreamWriter pw = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/dataset.meta.train."+corpus+"."+percent+".csv"),StandardCharsets.UTF_8);		
		
		@SuppressWarnings("deprecation")
		CSVWriter csvWriter = new CSVWriter(pw, ',' ,  '\'', '\\');
		
		
		TreeMap<String,String> GTMAP = d.getGT_MAP();
//		System.out.println("Complete set has :"+GTMAP.keySet().size());
//		Set<String> NES  = d.getNEs();
//		System.out.println("num nes : "+NES.size());
		
		TObjectIntHashMap<String> DocWordCountMap = null;
		TObjectIntHashMap<String> NumRecogMentionMap = null;
		TObjectIntHashMap<String> MentionEntityCountMap = null;
		TObjectIntHashMap<String> DocFrequencyMap = null;
		TreeMap<String,String> DocContent = null;
 
		if(corpus.equalsIgnoreCase("ace2004")){
			DocWordCountMap = ((DataLoaders_ACE2004) d).getDocWordCountMap();
			NumRecogMentionMap = ((DataLoaders_ACE2004) d).getNumRecogMentionMap();
			MentionEntityCountMap = Features.loadMentionEntityCountMap();
			DocFrequencyMap = ((DataLoaders_ACE2004) d).getDocFrequencyMap();
			DocContent = ((DataLoaders_ACE2004) d).getDocsContent();
		}
		if(corpus.equalsIgnoreCase("aquaint")){
			DocWordCountMap = ((DataLoaders_AQUAINT) d).getDocWordCountMap();
			NumRecogMentionMap = ((DataLoaders_AQUAINT) d).getNumRecogMentionMap();
			MentionEntityCountMap = Features.loadMentionEntityCountMap();
			DocFrequencyMap = ((DataLoaders_AQUAINT) d).getDocFrequencyMap();
			DocContent = ((DataLoaders_AQUAINT) d).getDocsContent();
		}
		
		if(corpus.equalsIgnoreCase("conll")){
			DocWordCountMap = ((DataLoaders_CONLL) d).getDocWordCountMap();
			NumRecogMentionMap = ((DataLoaders_CONLL) d).getNumRecogMentionMap();
			MentionEntityCountMap = Features.loadMentionEntityCountMap();
			DocFrequencyMap = ((DataLoaders_CONLL) d).getDocFrequencyMap();
			DocContent = ((DataLoaders_CONLL) d).getDocsContent();
		}
		if(corpus.equalsIgnoreCase("iitb")){
			DocWordCountMap = ((DataLoaders_IITB) d).getDocWordCountMap();
			NumRecogMentionMap = ((DataLoaders_IITB) d).getNumRecogMentionMap();
			MentionEntityCountMap =  Features.loadMentionEntityCountMap();
			DocFrequencyMap = ((DataLoaders_IITB) d).getDocFrequencyMap();
			DocContent = ((DataLoaders_IITB) d).getDocsContent();
		}
		if(corpus.equalsIgnoreCase("kore50")){
			DocWordCountMap = ((DataLoaders_KORE50) d).getDocWordCountMap();
			NumRecogMentionMap = ((DataLoaders_KORE50) d).getNumRecogMentionMap();
			MentionEntityCountMap =  Features.loadMentionEntityCountMap();
			DocFrequencyMap = ((DataLoaders_KORE50) d).getDocFrequencyMap();
			DocContent = ((DataLoaders_KORE50) d).getDocsContent();
		}
		if(corpus.equalsIgnoreCase("msnbc")){
			DocWordCountMap = ((DataLoaders_MSNBC) d).getDocWordCountMap();
			NumRecogMentionMap = ((DataLoaders_MSNBC) d).getNumRecogMentionMap();
			MentionEntityCountMap = Features.loadMentionEntityCountMap();
			DocFrequencyMap = ((DataLoaders_MSNBC) d).getDocFrequencyMap();
			DocContent = ((DataLoaders_MSNBC) d).getDocsContent();
		}
		if(corpus.equalsIgnoreCase("neel")){
			DocWordCountMap = ((DataLoaders_NEEL) d).getDocWordCountMap();
			NumRecogMentionMap = ((DataLoaders_NEEL) d).getNumRecogMentionMap();
			MentionEntityCountMap = Features.loadMentionEntityCountMap();
			DocFrequencyMap = ((DataLoaders_NEEL) d).getDocFrequencyMap();
			DocContent = ((DataLoaders_NEEL) d).getDocsContent();
		}
		if(corpus.equalsIgnoreCase("wp")){
			DocWordCountMap = ((DataLoaders_WP) d).getDocWordCountMap();
			NumRecogMentionMap = ((DataLoaders_WP) d).getNumRecogMentionMap();
			MentionEntityCountMap = Features.loadMentionEntityCountMap();
			DocFrequencyMap = ((DataLoaders_WP) d).getDocFrequencyMap();
			DocContent = ((DataLoaders_WP) d).getDocsContent();
		}


		
		String[] sys = new String[]{"amb","bab","tag"};
		for(String s : sys){
//			"./resources/ds/ds.amb."+p+".txt"
//			"./resources/ds/ds.bab."+p+".txt"
//			"./resources/ds/ds.tag."+p+".txt"
			BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("./resources/ds/"+corpus+"/ds."+s+"."+percent+".txt"),StandardCharsets.UTF_8));
			String line = "";
			while((line = bffReader.readLine() )!= null) {
				String[] elems = line.split("\t");
				String docid = elems[0].toLowerCase();
				docid = docid.replaceAll("\'", "");
				docid = docid.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
				String offset = elems[2];

				String GTLink = "NULL";
				
				if(DocContent.containsKey(docid)){
			
					/** Mention-based features **/
					double slen =  getMentionLength(mention);
					double swords =    getMentionGranularity(mention);
					String content = DocContent.get(docid);
					content = content.toLowerCase();
					double sf =    getMentionFrequency(content, mention);
					double sdf =  getDocFreq(DocFrequencyMap,mention);
					double scand =  (double)MentionEntityCountMap.get(mention);//getNumCandidates(mention);
					String mpos =  getMentionNormalizedPosition(content,offset);
					double msent = getSentenceSize(content, mention, offset);
					
					/** Document-based features **/
					double dwords = (double) DocWordCountMap.get(docid);
					double dents = (double)NumRecogMentionMap.get(docid);//getNumRecogMentions(docId);
					
					csvWriter.writeNext(new String[]{docid,mention,offset,GTLink,Double.toString(slen),Double.toString(swords),Double.toString(sf),Double.toString(sdf),Double.toString(scand),mpos,Double.toString(msent),Double.toString(dwords),Double.toString(dents)});
					
				}
			}
			bffReader.close();
			}
		csvWriter.close();
		
		System.out.println("Finished writing the features for the training set "+percent);
		}



	
	
		
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


		public static String getMentionNormalizedPosition(String content, String offset) throws IOException {		
			double length = (double)content.length();	
			double position = Double.parseDouble(offset);
			double normalized;
			normalized = position/length;	
			DecimalFormat dec = new DecimalFormat("#0.00");
			return dec.format(normalized);
		}

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

	
		public static boolean isEmpty(String str) {
			return str == null || str.length() == 0;
		}

		public static double getMentionGranularity(String mention) {		
			int mention_granularity = mention.split("\\s+").length;
			return (double)mention_granularity;
		}

		public static double getMentionLength(String mention) {
			int mention_size = mention.length();
			return (double)mention_size;
		}
		
		public static double getDocFreq(TObjectIntHashMap<String>DocumentFrequency, String mention) {
			return (double) DocumentFrequency.get(mention);
			
		}
		
		
//		/**
//		 *
//		 * @param corpus
//		 * @param d
//		 * @throws IOException
//		 */
//		public static void dumpDocumentFrequency(String corpus, DataLoaders d) throws IOException{
//			HashSet<String> mentionsSET = new HashSet<String>();
//
//			OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/document_frequency.tsv"),StandardCharsets.UTF_8);
//			
//			BufferedReader bffReaderAmb = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/"+corpus+"/mappings/"+corpus+"_ambiverse.training.mappings.sorted"),StandardCharsets.UTF_8));
//			BufferedReader bffReaderBab = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/"+corpus+"/mappings/"+corpus+"_bfy.training.mappings.sorted"),StandardCharsets.UTF_8));
//			BufferedReader bffReaderTag = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/"+corpus+"/mappings/"+corpus+"_tagme.training.mappings.sorted"),StandardCharsets.UTF_8));
//
//			String line = "";
//			while((line = bffReaderAmb.readLine() )!= null) {
//				String[] elems = line.split("\t");
//				String mention = elems[1].toLowerCase().trim();
//				mentionsSET.add(mention);
//			}
//			bffReaderAmb.close();
//	
//			line = "";
//			while((line = bffReaderBab.readLine() )!= null) {
//				String[] elems = line.split("\t");
//				String mention = elems[1].toLowerCase().trim();
//				mentionsSET.add(mention);
//			}
//			bffReaderBab.close();
//
//			line = "";
//			while((line = bffReaderTag.readLine() )!= null) {
//				String[] elems = line.split("\t");
//				String mention = elems[1].toLowerCase().trim();
//				mentionsSET.add(mention);
//			}
//			bffReaderTag.close();
//			
//			TreeMap<String,String> DocContent = d.getDocsContent();
//			
//			for (String mention : mentionsSET){
//				int df = 0;
//				
//				Iterator<?> it = DocContent.entrySet().iterator();
//				while (it.hasNext()) {
//					@SuppressWarnings("rawtypes")
//					Map.Entry pair = (Map.Entry)it.next();
//			    	//String articletitle = (String) pair.getKey();
//					String articlecontent = (String) pair.getValue();
//					articlecontent = articlecontent.toLowerCase();
//					if(articlecontent.contains(mention)){
//						df+=1;
//					}							
//				}
//				pAnn.write(mention+"\t"+df+"\n");
//			}
//				
//	    	pAnn.flush();
//	    	pAnn.close();
//	    	System.out.println("...Finished dumping the Document Frequency Count Successfully.");
//		}
//		
//		
//		
//		
//		public static TObjectIntHashMap<String> loadDocFrequencyMap(String corpus) throws CompressorException, IOException{
//			TObjectIntHashMap<String> DocFrequencyMap = new TObjectIntHashMap<>();		
//			BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("./resources/ds/"+corpus+"/document_frequency.tsv"),StandardCharsets.UTF_8));
//			String line ="";											
//			while((line = bffReader.readLine()) != null) {
//				String[] elems = line.split("\t");
//				String mention = elems[0];
//				String freq = elems[1];
//				Integer df = Integer.parseInt(freq);			
//				DocFrequencyMap.put(mention, df);
//			}		
//			bffReader.close();
////			System.out.println("...Loaded Document Frequency Map.");
//			//		DocFrequencyMap = new TObjectIntHashMap<String>();
//			return DocFrequencyMap;
//		}
		

	
}
