package de.l3s.loaders;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class DataLoaders {
	public static TObjectIntHashMap<String> DocFrequencyMap = null;
	public static TObjectIntHashMap<String> DocWordCountMap = null;
	public static TObjectIntHashMap<String> MentionEntityCountMap = null;
	public static TObjectIntHashMap<String> NumRecogMentionMap = null;
	
	public static TreeMap<String,String> GT_MAP_train = null;
	public static TreeMap<String,String> GT_MAP_test = null;
	public static TreeMap<String,String> GT_MAP = null;

	public static Set<String> NEs = null;
    
	public static DataLoaders instance = null;
	
	public static TreeMap<String,  String> ambiverseMap = null;
	public static TreeMap<String,  String> babelMap = null;
	public static TreeMap<String,  String> tagmeMap = null;
	public static TreeMap<String,  String> spotlightMap = null;
	
	public static TreeMap<String,  String> ambiverseMap_train = null;
	public static TreeMap<String,  String> babelMap_train = null;
	public static TreeMap<String,  String> tagmeMap_train = null;
	public static TreeMap<String,  String> spotlightMap_train = null;
	
	public static TreeMap<String,  String> ambiverseMap_test = null;
	public static TreeMap<String,  String> babelMap_test = null;
	public static TreeMap<String,  String> tagmeMap_test = null;
	public static TreeMap<String,  String> spotlightMap_test = null;
	
	public static TreeMap<String, String> docsContent = null;

	
	
	
	
	
	public DataLoaders() throws CompressorException, IOException {
		super();
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
		
		NEs = new TreeSet<String>();

		
//		docsContent = new TreeMap<String, String>();
		
//		loadDocsContent();
//		loadDocFrequencyMap();
//		loadWordCount();
		loadMentionEntityCountMap();
//		loadNumMentionsRecognized();
//		loadGT();
//		loadMappings();

		
	}

	public static BufferedReader getBufferedReaderForCompressedFile(String fileIn) throws FileNotFoundException, CompressorException {
		FileInputStream fin = new FileInputStream(fileIn);
		BufferedInputStream bis = new BufferedInputStream(fin);
		CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(input,StandardCharsets.UTF_8));
		return br2;
	}
	
	
	public static void loadMentionEntityCountMap() throws CompressorException, IOException {
		BufferedReader bffReader = getBufferedReaderForCompressedFile("/home/joao/git/METAEL/MetaEL/resources/mentionEntityCount.txt.bz2");
		String line ="";
		while((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			if(elems.length > 2) {
				continue;
			}
			String mention = elems[0].trim().toLowerCase();
			String candCount = elems[1].trim();
			int numCandidates = 0;
			if(candCount!=null) {
				numCandidates = Integer.parseInt(candCount);
			}
			MentionEntityCountMap.put(mention, numCandidates);
		}
//		logger.info("Total Number of Mentions in the KB (i.e. Wikipedia 2016): "+MentionEntityCountMap.keySet().size());
//		logger.info("Loaded Mention-> Cand. Entities Successfully.");
		bffReader.close();
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

//	public TreeMap<String, String> getSpotlightMap_train() {
//		return null;
//	}
//	
//	public TreeMap<String, String> getSpotlightMap_test() {
//		return null;
//	}

	public TreeMap<String,String> getDocsContent(){
		return docsContent;
	}

	public static void setDocsContent(TreeMap<String, String> docsContent) {
		DataLoaders.docsContent = docsContent;
	}




	public TreeMap<String, String> getGT_MAP() {
		return GT_MAP;
	}
	public TreeMap<String, String> getGT_MAP_train() {
		return GT_MAP_train;
	}
	public TreeMap<String, String> getGT_MAP_test() {
		return GT_MAP_test;
	}

	public static TObjectIntHashMap<String> getMentionEntityCountMap() {
		return MentionEntityCountMap;
	}

	public static void setMentionEntityCountMap(
			TObjectIntHashMap<String> mentionEntityCountMap) {
		MentionEntityCountMap = mentionEntityCountMap;
	}

	public static Set<String> getNEs() {
		return NEs;
	}

	public static void setNEs(Set<String> nEs) {
		NEs = nEs;
	}

	

	
}
