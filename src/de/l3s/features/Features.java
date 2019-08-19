package de.l3s.features;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.xml.sax.SAXException;

import de.l3s.loaders.DataLoaders;
import de.l3s.loaders.DataLoaders_AQUAINT;
import de.l3s.loaders.DataLoaders_CONLL;
import de.l3s.loaders.DataLoaders_GERDAQ;
import de.l3s.loaders.DataLoaders_IITB;
import de.l3s.loaders.DataLoaders_MSNBC;
import de.l3s.loaders.DataLoaders_NEEL;
import de.l3s.loaders.DataLoaders_WP;
import gnu.trove.map.hash.TObjectIntHashMap;

public class Features {	
	//private static TObjectIntHashMap<String> DocWordCountMap;
	private static TreeMap<String, String> filePathMap;
	//private static TObjectIntHashMap<String> MentionEntityCountMap ;
	private static TObjectIntHashMap<String> DocumentFrequency;
	//private static TObjectIntHashMap<String> NumRecogMentionMap;
	private static HashMap<String,String> DocTopicMap;
	private static TObjectIntHashMap<String> PublicationDateMap;
	private static TObjectIntHashMap<String> DocMentionMap;
	//private static TreeMap<String,VectorChange> VectorChangeMap;
	//private static TreeMap<String,String> NeighborhoodScoreMap ;
 
	private static TreeMap<String, String> GTMAP;
	
	public static void main(String[] args) throws CompressorException, IOException, NumberFormatException, SAXException, ParserConfigurationException{
//		String[] corpus = new String[]{"conll", "iitb", "wp",  "neel", "gerdaq"};
// 		String corpus = "conll";   //Babelfy > Ambiverse > Spotlight >  Tagme
//		String corpus = "iitb";    //Ambiverse > Spotlight >  Tagme > Babelfy
 //		String corpus = "wp";      // Tagme > Babelfy > Ambiverse > Spotlight
//  		String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
// 		String corpus = "gerdaq";  // Tagme > Babelfy > Ambiverse 
//  		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
 		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy

		DataLoaders d = new DataLoaders();
		
		if(corpus.equalsIgnoreCase("conll")){
			d = DataLoaders_CONLL.getInstance();
		}
		if(corpus.equalsIgnoreCase("iitb")){
			d = DataLoaders_IITB.getInstance();
		}
		if(corpus.equalsIgnoreCase("wp")){
			d = DataLoaders_WP.getInstance();
		}
		if(corpus.equalsIgnoreCase("neel")){
			d = DataLoaders_NEEL.getInstance();
		}
		if(corpus.equalsIgnoreCase("gerdaq")){
			d = DataLoaders_GERDAQ.getInstance();
		}
		if(corpus.equalsIgnoreCase("aquaint")){
			d = DataLoaders_AQUAINT.getInstance();
		}
		if(corpus.equalsIgnoreCase("msnbc")){
			d = DataLoaders_MSNBC.getInstance();
		}	
		
		GTMAP = new TreeMap<String, String>();
		GTMAP = d.getGT_MAP(); 

//		writeTrainingAndTestSets(d);
		
		
	}
	
	
	public Features() throws CompressorException, IOException {
		super();
//		filePathMap = new TreeMap<>();
//		//MentionEntityCountMap = new TObjectIntHashMap<>();
//		DocumentFrequency = new TObjectIntHashMap<>();
//		//DocWordCountMap = new TObjectIntHashMap<>();
//		//NumRecogMentionMap = new TObjectIntHashMap<>();
//		DocTopicMap = new HashMap<>();
//		PublicationDateMap = new TObjectIntHashMap<>();
//		DocMentionMap = new TObjectIntHashMap<>();
		//VectorChangeMap = new TreeMap<>();
		//NeighborhoodScoreMap = new TreeMap<>();
		
//		loadFilesPathHashMap();		 
		//loadMentionEntityCountMap();
				//loadDocFrequencyMap(); Commented out because I am passing it with an argument for different cases. (HARD, NORMAL, EASY)
//		loadWordCount();
//		loadNumMentionsRecognized();
//		loadTopicMap();
				//loadPublicationDateMap();
		
		//loadTemporalMaps();
		
	}
	
	public static void dumpNumRecognizedMentions(String corpus) throws NumberFormatException, IOException{
		System.out.println("Dumping the number of recognized mentions per document.");
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/"+corpus+"/num_recognized_mentions.tsv"),StandardCharsets.UTF_8);
		BufferedReader bff = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/"+corpus+"/"+corpus+"_GT_NONIL.tsv"),StandardCharsets.UTF_8));
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
		System.out.println("... Done.");
	}
	
//	private void loadTemporalMaps() throws IOException {
//		for(int i = 0; i < 21; i++) {
//			int year = 1987+i;
//			//tdfs[i] = new TObjectIntHashMap<>();
//			
//			File f = new File("/home/joao/CIKM_2018/temporal/"+year+".hard.df");
//			BufferedReader bf = new BufferedReader(new FileReader(f));
//			String l = "";
//			TObjectIntHashMap<String> map = tdfs.get(i);
//			while((l = bf.readLine() )!=null) {
//				String[] elems = l.split("\t");
//				String mention = elems[0];
//				String df = elems[1];
//				Integer temdf = Integer.parseInt(df);
//				map.put(mention, temdf);	
//			}
//			tdfs.add(i, map);
//			bf.close();
//
//		}
// 			System.out.println("...Loaded Temporal Maps successfully.");
//	}
		
	/**
	 *
	 * @throws IOException
	 * @throws CompressorException
	 */
	//private void loadPublicationDateMap() throws IOException, CompressorException {
	//	BufferedReader bffReader = getBufferedReaderForCompressedFile("/home/joao/CIKM_2018/topics.tsv.bz2");
	//	String line ="";
	//	while((line = bffReader.readLine()) != null) {
	//		String[] elems = line.split("\t");
	//		String docId = elems[0];
	//		String pubDate = elems[1];
	//		Integer pubDateFeat = Integer.parseInt(pubDate);
	//		PublicationDateMap.put(docId, pubDateFeat);
	//	}
	//	
	//	bffReader.close();
	//}

	/**
	 *
	 * @param string
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws CompressorException 
	 */
	private void loadAllMentions(String string) throws NumberFormatException, IOException, CompressorException {
		BufferedReader bffReader = getBufferedReaderForCompressedFile(string);
		String line ="";
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0];
			String mention = elems[1];
			Integer year = Integer.parseInt(elems[2]);
			DocMentionMap.put(docId+"\t"+mention, year);
		}
		System.out.println("...Loaded All Mentions Map Successfully.");
		bffReader.close();		
	}

	
	/**
	 *
	 * @param string
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws CompressorException 
	 */
	private static TreeMap<String,String> loadNeighborhoodScore() throws NumberFormatException, IOException, CompressorException {
		TreeMap<String,String> NeighborhoodScoreMap = new TreeMap<>();
		BufferedReader bffReader = getBufferedReaderForCompressedFile("/home/joao/CIKM_2018/mentions_all.ns.bz2");
		String line ="";
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String mention = elems[0];
			Double min = Double.parseDouble(elems[1]);
			Double max = Double.parseDouble(elems[2]);
			Double avg = Double.parseDouble(elems[3]);				
			DecimalFormat dec = new DecimalFormat("#0.000");
			String minstr = dec.format(min);
			String maxstr = dec.format(max);
			String avgstr = dec.format(avg);	
			String scores = minstr+","+maxstr+","+avgstr;
			NeighborhoodScoreMap.put(mention,scores);
		}
		bffReader.close();
		
		System.out.println("Total Number of Unique Mentions : "+NeighborhoodScoreMap.keySet().size());
		System.out.println("...Loaded Neighborhood Scores Map successfully.");
		return NeighborhoodScoreMap;
	}

	/**
	 *			
	 *	This method loads a map of the mention/entity counts which is the number of entities
	 *	for every mention based on a Wikipedia dump from 2016.
	 *
	 *		It requires the file mentionEntityCount.txt.bz2 
	 *	
	 * branched chain amino acids
	 * @throws CompressorException
	 * @throws IOException
	 */
	public static TObjectIntHashMap<String> loadMentionEntityCountMap() throws CompressorException, IOException {
		TObjectIntHashMap<String> MentionEntityCountMap = new TObjectIntHashMap<>();
		BufferedReader bffReader = getBufferedReaderForCompressedFile("/home/joao/git/METAEL/MetaEL/resources/mentionEntityCount.txt.bz2");
		String line ="";
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length > 2) {continue;}
			String mention = elems[0].trim().toLowerCase();
			String candCount = elems[1].trim();
			int numCandidates = 0;
			if(candCount!=null) {
				numCandidates = Integer.parseInt(candCount);
			}
			MentionEntityCountMap.put(mention, numCandidates);
		}
//		System.out.println("...Total Number of Mentions in the KB (i.e. Wikipedia 2016): "+MentionEntityCountMap.keySet().size());
//		System.out.println("...Loaded Mention-> Cand. Entities Successfully.");
		bffReader.close();
		return MentionEntityCountMap;
	}
	
	
	/**
	 *	This method loads a Map with the files path into <<< filePathMap >>>.  
	 *	
	 * /home/joao/CIKM_2018/filesPathMap.tsv.bz2 is in the following format :
	 * 
	 *	i.e. 
		9B0DE3D9123FF935A25755C0A961948260.txt  /home/joao/NYT/nyt_corpus/TEXT_FILES/1987/9B0DE3D9123FF935A25755C0A961948260.txt
		9B0DEEDF1F3EF932A05756C0A961948260.txt  /home/joao/NYT/nyt_corpus/TEXT_FILES/1987/9B0DEEDF1F3EF932A05756C0A961948260.txt
		9B0DE6DA1630F931A1575BC0A961948260.txt  /home/joao/NYT/nyt_corpus/TEXT_FILES/1987/9B0DE6DA1630F931A1575BC0A961948260.txt
		9B0DE7D81338F936A15753C1A961948260.txt  /home/joao/NYT/nyt_corpus/TEXT_FILES/1987/9B0DE7D81338F936A15753C1A961948260.txt
	 *
	 * @throws CompressorException
	 * @throws IOException
	 */
	public static void loadFilesPathHashMap() throws CompressorException, IOException{
		BufferedReader bffReader = getBufferedReaderForCompressedFile("/home/joao/CIKM_2018/filesPathMap.tsv.bz2");
		String line ="";
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].replace(".txt","");
			String docPath = elems[1];
			filePathMap.put(docId,docPath);
		}
		System.out.println("...Total Num of Files in the Corpus :"+filePathMap.keySet().size());
		System.out.println("...Loaded Files Path Map Successfully.");
		bffReader.close();
	}

	/**
	 *
	 *	This method is used to load the document frequency map based on the mentions file that is passed as argument.
	 *
	 * @throws CompressorException
	 * @throws IOException
	 */
	public void loadDocFrequencyMap(String mentions_file) throws CompressorException, IOException{ 
		BufferedReader bffReader = getBufferedReaderForCompressedFile(mentions_file); 
		String line ="";											
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String mention = elems[0];
			String freq = elems[1];
			Integer df = Integer.parseInt(freq);		
			DocumentFrequency.put(mention, df);
			
		}		
		bffReader.close();
		System.out.println("...Loaded Document Frequency Map from "+mentions_file+".");
	}
	
	
	/**
	 * This function reads a tab separated file (Documents_word_count.tsv) with the document id and the number of words.
	 * i.e.
	 * 
	 *  9C01E7D61F39F931A15751C1A9669C8B63	521
		9905E3D8133CF93BA35753C1A9669C8B63	43
		9F05EFDD103BF93BA25754C0A9669C8B63	47
		9E03EED9153CF934A35753C1A9669C8B63	1344

	 * @throws CompressorException 
	 * @throws IOException 
	 */
//	public static TObjectIntHashMap<String> loadWordCount() throws CompressorException, IOException {
//		TObjectIntHashMap<String> DocWordCountMap = new TObjectIntHashMap<>();
//		BufferedReader bffReader = getBufferedReaderForCompressedFile("/home/joao/CIKM_2018/doc_word_count.tsv.bz2");
//		String line ="";
//		while((line = bffReader.readLine()) != null) {
//			String[] elems = line.split("\t");
//			String docId = elems[0];
//			String count = elems[1];
//			DocWordCountMap.put(docId, Integer.parseInt(count));
//		}
//		bffReader.close();
//		System.out.println("...Loaded Word Count Map Successfully.");
//		return DocWordCountMap;
//		
//	}
	
	/**
	 *
	 * @param filePath
	 * @throws CompressorException 
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
//	private void loadvectorChange(String filePath) throws CompressorException, NumberFormatException, IOException {
//		BufferedReader bffReader = getBufferedReaderForCompressedFile(filePath);
//		 ///home/renato/CIKM_2018/mentions_vector_change_hard.tsv
//		String line ="";
//		while((line = bffReader.readLine()) != null) {
//			String[] elems = line.split("\t");
//			String mention = elems[0];
//			double min = Double.parseDouble(elems[1]);
//			double max = Double.parseDouble(elems[2]);
//			double avg = Double.parseDouble(elems[3]);
//			VectorChange vc = new VectorChange(min, max, avg);
//			VectorChangeMap.put(mention, vc);
//		}
//		bffReader.close();
//		System.out.println("...Loaded Vector change map successfully.");
//	}

	
	/**
	 *	This method loads a map of the number of recognized mentions per document	 *	
	 *
	 * @return
	 * @throws CompressorException
	 * @throws IOException				GENERATED by fetchNumRecognizedMentions()
	 */
//	public static TObjectIntHashMap<String> loadNumMentionsRecognized() throws CompressorException, IOException {
//		TObjectIntHashMap<String> NumRecogMentionMap = new TObjectIntHashMap<String>();
//		BufferedReader bffReader = getBufferedReaderForCompressedFile("/home/joao/CIKM_2018/num_recognized_mentions.tsv.bz2");
//		String line ="";
//		while((line = bffReader.readLine()) != null) {
//			String[] elems = line.split("\t");
//			String docId = elems[0];
//			String count = elems[1];
//			NumRecogMentionMap.put(docId,Integer.parseInt(count));
//		}
//		bffReader.close();
//		System.out.println("...Loaded Number Recognized Mentions Map Successfully.");
//		return NumRecogMentionMap;
//		
//	}
	
	
	/**
	 * This utility function is meant to load the count of entities per mention mined from the latest wikipedia dump.
	 * 
	 * @throws CompressorException
	 * @throws IOException
	 */
	//public static TObjectIntHashMap<String> loadCandidatesCountMap() throws CompressorException, IOException {
	//	TObjectIntHashMap<String> candidatesCountMap = new TObjectIntHashMap<>();
	//	BufferedReader bffReader = getBufferedReaderForCompressedFile("/home/joao/git/METAEL/MetaEL/resources/mentionEntityCount.txt.bz2");
	//	String line="";
		
	//	while ((line = bffReader.readLine()) != null) {
	//		String[] elements = line.split("\t");
	//		if(elements.length > 2) {
	//			continue;
	//		}else {
	//			String mention = elements[0].trim();
	//			String count = elements[1].trim();
				//System.out.println(line);
	//			candidatesCountMap.put(mention,Integer.parseInt(count));
	//		}

	//	}
	//	System.out.println(candidatesCountMap.keySet().);
	//	bffReader.close();
	//	return candidatesCountMap;
	//}
	
	/**
	 * 
	 * 
	 *
	 * @param docID
	 * @return
	 * @throws IOException
	 */
	//public static TObjectIntHashMap<String> calculateTermFrequency(String docID) throws IOException{
	//	String docPath = getDocPath(docID);//9D02EED71530F937A15750C0A9619C8B63		
	//	File f = new File(docPath);
	//	TObjectIntHashMap<String> tmpTFMap = new TObjectIntHashMap<>();
	//	BufferedReader bf = new BufferedReader(new FileReader(f));
	//	String l = "";
	//	StringBuffer txt = new StringBuffer();
	//	while((l = bf.readLine() )!=null) {
	//		txt.append(l);
	//		
	//	}
	//	String txtContent = txt.toString().toLowerCase();
	//	bf.close();

		//calculating token trigram
	//	for(int i =1 ; i <=4; i++) {
	//		List<String> tokens = ngrams(i,txtContent);
	//		for(String t : tokens) {
	//			int tfreq = tmpTFMap.get(t);
	//			if(tfreq == 0) {
	//				tfreq = 1;
	//			}else {
	//				tfreq += 1;
	//			}
	//			tmpTFMap.put(t,tfreq);
	//		}
//		}
								
//		return tmpTFMap;
//	}
	
	

	
	/**
	 *
	 * @throws CompressorException
	 * @throws IOException
	 */
	//public static void writeDocumentFrequency() throws CompressorException, IOException{	
	//	int i =0;
	//	HashMap<String,Integer> mentionDF = new HashMap<>();
	//	BufferedReader bffReader = getBufferedReaderForCompressedFile("/home/joao/CIKM_2018/mentions_disag_all.tsv.bz2");
	//	String l = bffReader.readLine();
	//	while((l = bffReader.readLine() )!=null) {

	//		String elements[] = l.split("\t");
	//		String mention = elements[0];
	//		//populating the map of mentions 
	//		mentionDF.put(mention, 0);
	//		System.out.println(i++);
	//		File directory = new File("/home/joao/NYT/nyt_corpus/TEXT_FILES/2007/");			
	//		String[] extensions = {"txt"};
	 //   	List<File> listOfFiles =  (List<File>) FileUtils.listFiles(directory, extensions,true);
	    	//System.out.println(listOfFiles.size());
	//    	for(File f : listOfFiles) {
	//    		StringBuffer txt = new StringBuffer();
	 //   		BufferedReader bf = new BufferedReader(new FileReader(f));
	//    		while((l = bf.readLine() )!=null) {
	 //   			txt.append(l);	    		
	 //   		}
	    		
	 //   		String articleContent = txt.toString().toLowerCase();
	  //  		if(articleContent.contains(mention)) {
	 //   			Integer count  = mentionDF.get(mention);
	 //   			if(count == null) {
	 //   				mentionDF.put(mention, 1);
	 //   			}else {
	 //   				mentionDF.put(mention,count + 1);
	 //   			}
	    			
	 //   			continue;
	    			
	 //   		}else {
	    			
	 //   		}
	  //  		bf.close();

	 //   	}
	    	
	//	}
		
		
	//	Iterator it = mentionDF.entrySet().iterator();
	 //   while (it.hasNext()) {
	 //   	Map.Entry pair = (Map.Entry)it.next();
	 //   	String mention = (String) pair.getKey();
	 //   	Integer df = (Integer)pair.getValue();
			//System.out.println(i++);
	        //documentFrequency.put(mention, df.size());
	 //       it.remove(); // avoids a ConcurrentModificationException
	//		System.out.println(mention+"\t"+df);
	//    }
	    
		
		

	//}

	public static List<String> ngrams(int n, String str) {
		 List<String> ngrams = new ArrayList<String>();
		 String[] words = str.replaceAll("\\p{P}", "").toLowerCase().split("\\s+");
		 for (int i = 0; i < words.length - n + 1; i++)
			 ngrams.add(concat(words, i, i+n));
		 return ngrams;
	    }

	 public static String concat(String[] words, int start, int end) {
		 StringBuilder sb = new StringBuilder();
		 for (int i = start; i < end; i++)
			 sb.append((i > start ? " " : "") + words[i]);
		 return sb.toString();
	    }
	 
	 

	
	

		
//	/**
//	 *					 MAIN
//	 * @param args
//	 * @throws IOException
//	 * @throws CompressorException 
//	 */
//	public static void main(String[] args) throws IOException, CompressorException {
//		
//		PrintWriter pw = new PrintWriter(new FileOutputStream(new File("/home/joao/CIKM_2018/datasets/experiments/fulldataset.multi.arff")));
//
//		pw.println("@RELATION multiclass_instances");
//		pw.println("@ATTRIBUTE docid        STRING");
//		pw.println("@ATTRIBUTE mention      STRING");
//		pw.println("@ATTRIBUTE offset       NUMERIC");
//		pw.println("@ATTRIBUTE length       NUMERIC");
//		pw.println("@ATTRIBUTE granularity  NUMERIC");
//		pw.println("@ATTRIBUTE freq         NUMERIC");
//		pw.println("@ATTRIBUTE docfreq      NUMERIC");
//		pw.println("@ATTRIBUTE numcand      NUMERIC");
//		pw.println("@ATTRIBUTE normpos      NUMERIC");
//		pw.println("@ATTRIBUTE sentsize     NUMERIC");
//		pw.println("@ATTRIBUTE docsize     NUMERIC");
//		pw.println("@ATTRIBUTE doctopic     {Arts,Automobiles,Books,Business,Education,Health,Home_and_Garden,Job_Market,Magazine,Miscelaneous,Movies,New_York_and_Region,Obituaries,Real_Estate,Science,Sports,Style,Technology,Theater,Travel,Week_in_Review,World}");
//		pw.println("@ATTRIBUTE docmentions    NUMERIC");
//		pw.println("@ATTRIBUTE pub_date     NUMERIC");
//		pw.println("@ATTRIBUTE tdf  NUMERIC");
//		pw.println("@ATTRIBUTE min_jcc_change    NUMERIC");
//		pw.println("@ATTRIBUTE max_jcc_change    NUMERIC");
//		pw.println("@ATTRIBUTE avg_jcc_change    NUMERIC");	
//		pw.println("@ATTRIBUTE class        {EASY,MEDIUM,HARD}");
//		pw.println("");
//		pw.println("@DATA");
//		
//		
//		TreeMap<String,String> NeighborhoodScoreMap  = loadNeighborhoodScore();
////		TObjectIntHashMap<String> DocWordCountMap = loadWordCount();
////		TObjectIntHashMap<String> NumRecogMentionMap = loadNumMentionsRecognized();
//		TObjectIntHashMap<String> MentionEntityCountMap = loadMentionEntityCountMap(); 
//		
//		
//		/************
//		 * HARD
//		 ************/
//		Features ft = new Features();
//		ft.loadDocFrequencyMap("/home/joao/CIKM_2018/mentions.hard.df.bz2");
//		ft.loadAllMentions("/home/joao/CIKM_2018/mentions_hard_all.tsv.bz2");
//		
//		List<TObjectIntHashMap<String>> tdfs = new LinkedList<>();
//		for(int i = 0; i < 21; i++) {	
//			int year = 1987+i;
//			TObjectIntHashMap<String> tempdfMap = new TObjectIntHashMap<>();			
//			BufferedReader bf = getBufferedReaderForCompressedFile("/home/joao/CIKM_2018/temporal/"+year+".hard.df.bz2");
//			String l = "";
//			while((l = bf.readLine() )!=null) {
//				String[] elems = l.split("\t");
//				String mention = elems[0];
//				String df = elems[1];
//				Integer temdf = Integer.parseInt(df);
//				tempdfMap.put(mention, temdf);	
//			}
//			tdfs.add(tempdfMap);
//			bf.close();
//		}
//		System.out.println("...Loaded Temporal Document Frequency for HARD instances Successfully.");
//
//		BufferedReader bff = new BufferedReader(new FileReader("/home/joao/CIKM_2018/datasets/original/dataset.hard"));
//
//		String line="";
//		int contador = 0;
//		while ((line = bff.readLine()) != null) {
//			System.out.println("...creating instace :"+contador++);
//			String[] elements = line.split("\t");
//			String docId = elements[0];
//			String mention = elements[1];			
//			String offset =  elements[2];			
//	
//			/** Mention-based features **/
//			double mention_length =  getMentionLength(mention);
//			double mention_gran =    getMentionGranularity(mention);
//			double mention_freq =    getMentionFrequency(docId, mention);
//			double mention_doc_freq =  getDocFreq(mention);
//			double mention_num_cand =  (double)MentionEntityCountMap.get(mention);//getNumCandidates(mention);
//			String mention_norm_pos =  getMentionNormalizedPosition(offset,docId);
//			double mention_sent_size = getSentenceSize(docId, mention, offset);
//			
//			/** Document-based features **/
//			//double doc_lex_diversity = 
//			double doc_size = (double) DocWordCountMap.get(docId);
//			String doc_topic = getDocumentTopic(docId);
//			double doc_numMentions = (double)NumRecogMentionMap.get(docId);//getNumRecogMentions(docId);
//			
//			/** Temporal-based features **/
//			double publication_date =  getPublicationDate(docId);
//			int year = DocMentionMap.get(docId+"\t"+mention);
//			if(year == 0 ) {
//				continue;
//			}
//			year = year - 1987;
//			TObjectIntHashMap<String> tmpMap = tdfs.get(year);
//			double  tdf = (double) tmpMap.get(mention);
////			VectorChange VC = VectorChangeMap.get(mention);
////			double min = VC.getMin();
////			double max = VC.getMax();
////			double avg = VC.getAvg();
//			String neighScore = NeighborhoodScoreMap.get(mention);
//			if(neighScore==null) {
//				neighScore = "?,?,?";
//			}		
//			pw.println(docId + "," + "\""+mention +"\""+ "," +offset + "," + mention_length+ ","+mention_gran+ ","+mention_freq+ ","+mention_doc_freq+ ","+mention_num_cand + ","+mention_norm_pos + ","+ mention_sent_size+","+doc_size+","+"\""+doc_topic+"\""+","+doc_numMentions+ ","+publication_date+","+ tdf+","+neighScore+","+"HARD");
//
//		}		
//		bff.close();
//		
//		/************
//		 * MEDIUM
//		 ************/
//		tdfs = new LinkedList<>();
//		for(int i = 0; i < 21; i++) {	
//			int year = 1987+i;
//			TObjectIntHashMap<String> tempdfMap = new TObjectIntHashMap<>();			
//			BufferedReader bf = getBufferedReaderForCompressedFile("/home/joao/CIKM_2018/temporal/"+year+".normal.df.bz2");
//			String l = "";
//			while((l = bf.readLine() )!=null) {
//				String[] elems = l.split("\t");
//				String mention = elems[0];
//				String df = elems[1];
//				Integer temdf = Integer.parseInt(df);
//				if(temdf == 0) {
//					continue;
//				}
//				tempdfMap.put(mention, temdf);	
//			}
//			tdfs.add(tempdfMap);
//			bf.close();
//		}
//		System.out.println("...Loaded Temporal Document Frequency for MEDIUM instances Successfully.");
//
//		ft = new Features();
//		ft.loadDocFrequencyMap("/home/joao/CIKM_2018/mentions.normal.df.bz2");
//		ft.loadAllMentions("/home/joao/CIKM_2018/mentions_medium_all.tsv.bz2");
//
//		bff = new BufferedReader(new FileReader("/home/joao/CIKM_2018/datasets/original/dataset.medium"));
//		line="";
//		while ((line = bff.readLine()) != null) {
//			System.out.println("...creating instace :"+contador++);
//			String[] elements = line.split("\t");
//			String docId = elements[0];
//			String mention = elements[1];			
//			String offset =  elements[2];			
//	
//			/** Mention-based features **/
//			double mention_length =  getMentionLength(mention);
//			double mention_gran =    getMentionGranularity(mention);
//			double mention_freq =    getMentionFrequency(docId, mention);
//			double mention_doc_freq =  getDocFreq(mention);
//			double mention_num_cand =  (double)MentionEntityCountMap.get(mention);//getNumCandidates(mention);
//			String mention_norm_pos =  getMentionNormalizedPosition(offset,docId);
//			double mention_sent_size = getSentenceSize(docId, mention, offset);
//			
//			/** Document-based features **/
//			double doc_size = (double) DocWordCountMap.get(docId);
//			String doc_topic = getDocumentTopic(docId);
//			double doc_numMentions = (double)NumRecogMentionMap.get(docId);//getNumRecogMentions(docId);
//		
//			/** Temporal-based features **/
//			double publication_date =  getPublicationDate(docId);
//			int year = DocMentionMap.get(docId+"\t"+mention);
//			if(year == 0 ) {
//				continue;
//			}
//			
//			year = year - 1987;
//			TObjectIntHashMap<String> tmpMap = tdfs.get(year);
//			double  tdf = (double) tmpMap.get(mention);
////			VectorChange VC = VectorChangeMap.get(mention);
////			double min = VC.getMin();
////			double max = VC.getMax();
////			double avg = VC.getAvg();
//			String neighScore = NeighborhoodScoreMap.get(mention);
//			if(neighScore==null) {
//				neighScore = "?,?,?";
//			}
//			
//			pw.println(docId + "," + "\""+mention +"\""+ "," +offset + "," + mention_length+ ","+mention_gran+ ","+mention_freq+ ","+mention_doc_freq+ ","+mention_num_cand + ","+mention_norm_pos + ","+ mention_sent_size+","+doc_size+","+"\""+doc_topic+"\""+","+doc_numMentions+ ","+publication_date+","+ tdf+","+neighScore+","+"MEDIUM");
//
//			
//		}
//		bff.close();
//	
//		/************
//		 * EASY
//		 ************/
//		tdfs = new LinkedList<>();
//		for(int i = 0; i < 21; i++) {	
//			int year = 1987+i;
//			TObjectIntHashMap<String> tempdfMap = new TObjectIntHashMap<>();
//			
//			BufferedReader bf = getBufferedReaderForCompressedFile("/home/joao/CIKM_2018/temporal/"+year+".easy.df.bz2");
//			String l = "";
//			while((l = bf.readLine() )!=null) {
//				String[] elems = l.split("\t");
//				String mention = elems[0];
//				String df = elems[1];
//				Integer temdf = Integer.parseInt(df);
//				if(temdf == 0) {
//					continue;
//				}
//				tempdfMap.put(mention, temdf);	
//			}
//			tdfs.add(tempdfMap);
//			bf.close();
//		}
//		System.out.println("...Loaded Temporal Document Frequency for EASY instances Successfully.");
//		
//		ft = new Features();
//		ft.loadDocFrequencyMap("/home/joao/CIKM_2018/mentions.easy.df.bz2");
//		ft.loadAllMentions("/home/joao/CIKM_2018/mentions_easy_all.tsv.bz2");
//		//ft_easy.loadvectorChange("/home/joao/CIKM_2018/mentions_vector_change_easy.tsv.bz2");
//		BufferedReader bff_easy = new BufferedReader(new FileReader("/home/joao/CIKM_2018/datasets/original/dataset.easy"));
//					
//		//PrintWriter pw_hard = new PrintWriter(new FileOutputStream(new File("/home/joao/CIKM_2018/datasets/unbalanced/dataset_unbalanced_all_features.hard.arff")));
//		line="";
//		while ((line = bff_easy.readLine()) != null) {
//			System.out.println("...creating instace :"+contador++);
//			String[] elements = line.split("\t");
//			String docId = elements[0];
//			String mention = elements[1];			
//			String offset =  elements[2];			
//			
//			/** Mention-based features **/
//			double mention_length =  getMentionLength(mention);
//			double mention_gran =    getMentionGranularity(mention);
//			double mention_freq =    getMentionFrequency(docId, mention);
//			double mention_doc_freq =  getDocFreq(mention);
//			double mention_num_cand =  (double)MentionEntityCountMap.get(mention);//getNumCandidates(mention);
//			String mention_norm_pos =  getMentionNormalizedPosition(offset,docId);
//			double mention_sent_size = getSentenceSize(docId, mention, offset);
//			
//				
//			/** Document-based features **/
//			double doc_size = (double) DocWordCountMap.get(docId);
//			String doc_topic = getDocumentTopic(docId);
//			double doc_numMentions = (double)NumRecogMentionMap.get(docId); //getNumRecogMentions(docId);
//			
//			/** Temporal-based features **/
//			double publication_date =  getPublicationDate(docId);
//			int year = DocMentionMap.get(docId+"\t"+mention);
//			if(year == 0 ) {
//				continue;
//			}			
//			year = year - 1987;
//			TObjectIntHashMap<String> tmpMap = tdfs.get(year);
//			double  tdf = (double) tmpMap.get(mention);
////			VectorChange VC = VectorChangeMap.get(mention);
////			double min = VC.getMin();
////			double max = VC.getMax();
////			double avg = VC.getAvg();
//			String neighScore = NeighborhoodScoreMap.get(mention);
//			if(neighScore==null) {
//				neighScore = "?,?,?";
//			}
//			pw.println(docId + "," + "\""+mention +"\""+ "," +offset + "," + mention_length+ ","+mention_gran+ ","+mention_freq+ ","+mention_doc_freq+ ","+mention_num_cand + ","+mention_norm_pos + ","+ mention_sent_size+","+doc_size+","+"\""+doc_topic+"\""+","+doc_numMentions+ ","+publication_date+","+ tdf+","+neighScore+","+"EASY");
//			
//			}		
//			bff_easy.close();
//			pw.close();
//	}
//	
	




	/**
	 *				Mdf - Mention document frequency
	 *
	 * @param mention
	 * @return
	 */
	public static int getDocumentFrequency(String mention) {		
		
		return(DocumentFrequency.get(mention));
	
	}
	

	
	/**
	 *			Mdf - Mention document frequency
	 * @param mention
	 * @return
	 */
	public  static double getDocFreq(String mention) {
		
		return (double) DocumentFrequency.get(mention);
		
	}

	/**
	 *
	 *				Mc - Mention num candidate entities.
	 *
	 * @param mention
	 * @return
	 */
//	public static double getNumCandidates(String mention) {
//		double num_cand = MentionEntityCountMap.get(mention);		
//		return num_cand ;
//	}

//	/**
//	 *				Mp - Mention normalized position.
//	 *	
//	 * @param offset
//	 * @param docID
//	 * @return
//	 * @throws IOException 
//	 */
//	public static String getMentionNormalizedPosition(String offset, String docID) throws IOException {		
//		String docPath = getDocPath(docID);
//		String content ="";
//		try {	
//	    	//content = new Scanner(new File(docPath)).useDelimiter("\\Z").next();
//	    	BufferedReader bf = new BufferedReader(new FileReader(docPath));
//			String l = "";
//			StringBuffer txt = new StringBuffer();
//			while((l = bf.readLine() )!=null) {
//					txt.append(l);
//			}
//			bf.close();
//			content = txt.toString().toLowerCase();
//	      } catch (IOException e) {
//	          System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
//	      }
//		double length = (double)content.length();	
//		double position = Double.parseDouble(offset);
//		double normalized;
//		normalized = position/length;	
//		DecimalFormat dec = new DecimalFormat("#0.00");
//		return dec.format(normalized);
//
//	
//	}

	


	
	/** Document-based features **/
	
	/**
	 *				
	 * 				Ds	-	Number of words of the document containing the mention.
	 * @param docId
	 * @param docId
	 * @return
	 */
	//public static double getDocumentSize(String docId) {
	//	double numWords = (double) DocWordCountMap.get(docId);
	//	return numWords;
				
	//}
	
	/**
	 * 				Dtm - the main topic (subject) discussed in the document containing the mention,
	 * @param docId
	 * @return
	 */
	public static String getDocumentTopic(String docId) {
		String topic = DocTopicMap.get(docId);
		return topic;
	}
	

	/**
	 * 			De		- the total number of entity mentions recognized in the document containing the mention.
	 *
	 * @param docId
	 * @return
	 */
//	public static double getNumRecogMentions(String docId) {
//	
//		double numMentions = (double)NumRecogMentionMap.get(docId);
//		
//		return numMentions;
//		
//	}
//	

	/** Temporal-based features **/

	/**
	 *
	 * @param docId
	 * @return
	 */
	private static double getPublicationDate(String docId) {
		int pubdate = PublicationDateMap.get(docId);
		double PubDate = (double) pubdate;
		return PubDate;
	}




	/**
	 *
	 * @param docID
	 * @return
	 */
	private static String getDocPath(String docID) {
		return filePathMap.get(docID);
		
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

		

	
	public static boolean isEmpty(String str) {
	      return str == null || str.length() == 0;
	  }

	
	/**
	 * Mf - Number of times the mention appears in the document.
	 * 
	 *
	 * @param docId
	 * @param mention
	 * @return
	 * @throws IOException
	 */
	public static double getMentionFrequency(String content, String mention)
			throws IOException {
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
	 * Mdf - Mention document frequency
	 * 
	 * @param mention
	 * @return
	 */
	public static double getDocFreq(
			TObjectIntHashMap<String> DocumentFrequency, String mention) {

		return (double) DocumentFrequency.get(mention);

	}

	/**
	 * Mp - Mention normalized position.
	 *
	 * @param offset
	 * @param docID
	 * @return
	 * @throws IOException
	 */
	public static String getMentionNormalizedPosition(String content,
			String offset) throws IOException {
		double length = (double) content.length();
		double position = Double.parseDouble(offset);
		double normalized;
		normalized = position / length;
		DecimalFormat dec = new DecimalFormat("#0.00");
		return dec.format(normalized);

	}

	/**
	 * Ms - Num of chars of the sentence containing the mention
	 * 
	 * @param docId
	 * @param mention
	 * @param position
	 * @return
	 */
	public static int getSentenceSize(String content, String mention,
			String position) {
		int tamEsq = 0;
		int tamDir = 0;
		int offset = Integer.parseInt(position);
		for (int i = 0; i < offset; i++) {
			tamEsq++;
			if ((content.charAt(i) == '.') || (content.charAt(i) == ';')
					|| (content.charAt(i) == '!' || (content.charAt(i) == '?'))) {
				tamEsq = 0;
			}
		}

		for (int i = offset + mention.length(); i < content.length(); i++) {
			tamDir++;
			if ((content.charAt(i) == '.') || (content.charAt(i) == ';')
					|| (content.charAt(i) == '!' || (content.charAt(i) == '?'))) {
				break;
			}
		}

		return tamEsq + mention.length() + tamDir;
	}

	/**
	 * Dtm - the main topic (subject) discussed in the document containing the
	 * mention,
	 * 
	 * @param docId
	 * @return
	 */
	// public static String getDocumentTopic(String docId) {
	// String topic = DocTopicMap.get(docId);
	// return topic;
	// }
	public static double getMentionGranularity(String mention) {		
		int mention_granularity = mention.split("\\s+").length;
		return (double)mention_granularity;
	}
	



	public static double getMentionLength(String mention) {
		int mention_size = mention.length();
		return (double)mention_size;
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
	
	
}
/**

The Header of the ARFF file contains the name of the relation, a list of the attributes (the columns in the data), and their types. An example header on the standard IRIS dataset looks like this:

//	   % 1. Title: Iris Plants Database
//	   % 
//	   % 2. Sources:
//	   %      (a) Creator: R.A. Fisher
//	   %      (b) Donor: Michael Marshall (MARSHALL%PLU@io.arc.nasa.gov)
//	   %      (c) Date: July, 1988
//	   % 
//	   @RELATION iris
//
//	   @ATTRIBUTE sepallength  NUMERIC
//	   @ATTRIBUTE sepalwidth   NUMERIC
//	   @ATTRIBUTE petallength  NUMERIC
//	   @ATTRIBUTE petalwidth   NUMERIC
//	   @ATTRIBUTE class        {Iris-setosa,Iris-versicolor,Iris-virginica}

The Data of the ARFF file looks like the following:

   @DATA
   5.1,3.5,1.4,0.2,Iris-setosa
   4.9,3.0,1.4,0.2,Iris-setosa
   4.7,3.2,1.3,0.2,Iris-setosa
   4.6,3.1,1.5,0.2,Iris-setosa

*/