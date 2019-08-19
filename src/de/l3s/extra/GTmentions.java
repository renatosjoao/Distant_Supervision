package de.l3s.extra;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import de.l3s.loaders.DataLoaders_CONLL;
import de.l3s.loaders.DataLoaders_IITB;
import de.l3s.loaders.DataLoaders_NEEL;
import de.l3s.loaders.DataLoaders_WP;


public class GTmentions {
	public static void main(String[] args) throws CompressorException, IOException{
		OutputStreamWriter p = new OutputStreamWriter(new FileOutputStream("/home/joao/Wiki2016/mentions.30.txt"),StandardCharsets.UTF_8);
		BufferedReader buffReader = getBufferedReaderForCompressedFile("/home/joao/Wiki2016/mentionEntityLinks_PRIOR_1.txt.bz2");

		TreeMap<String, Double> priorMap = null;
		TreeMap<String,TreeMap<String,Double>> mentionEntityMap = new TreeMap<String,TreeMap<String,Double>>(); 
		String line = "";
		while ((line = buffReader.readLine()) != null) {

		String[] elems = line.split(";-;");
		String mention = elems[0].trim().toLowerCase();
		String candEnt = elems[1].trim().toLowerCase();
		Double prior =  Double.parseDouble(elems[2].trim());
		if(mentionEntityMap.containsKey(mention)){
			priorMap = mentionEntityMap.get(mention);
			priorMap.put(candEnt, prior);
		}else{
			priorMap = new TreeMap<String, Double>();
			priorMap.put(candEnt, prior);
		}
		mentionEntityMap.put(mention, priorMap);
		}

		buffReader.close();

		Set<String> mentionSet = new TreeSet<String>();
		String corpus = "conll";

		DataLoaders_CONLL dConll  = new DataLoaders_CONLL();
		TreeMap<String,String> GT_MAP_test = dConll.getGT_MAP_test();

		Iterator<?> mapIterator = GT_MAP_test.entrySet().iterator();

		while (mapIterator.hasNext()) {
			Map.Entry pair = (Map.Entry)mapIterator.next();
			String chave = (String) pair.getKey();			
			String[] elems = chave.split("\t");
			String mention = elems[1].trim().toLowerCase();
			mentionSet.add(mention);
		}
		corpus = "iitb";
		DataLoaders_IITB dIITB = new DataLoaders_IITB();	 	
		GT_MAP_test = dIITB.getGT_MAP_test();
		
		mapIterator = GT_MAP_test.entrySet().iterator();
		
		while (mapIterator.hasNext()) {
			Map.Entry pair = (Map.Entry)mapIterator.next();
			String chave = (String) pair.getKey();			
			String[] elems = chave.split("\t");
			String mention = elems[1].trim().toLowerCase();
			mentionSet.add(mention);
		}

	 	corpus = "wp";
	 	DataLoaders_WP dWP = new DataLoaders_WP();
		GT_MAP_test = dWP.getGT_MAP_test();
	 	
		mapIterator = GT_MAP_test.entrySet().iterator();
		
		while (mapIterator.hasNext()) {
			Map.Entry pair = (Map.Entry)mapIterator.next();
			String chave = (String) pair.getKey();			
			String[] elems = chave.split("\t");
			String mention = elems[1].trim().toLowerCase();
			mentionSet.add(mention);
		}

		corpus = "neel";
		DataLoaders_NEEL dNEEL = new DataLoaders_NEEL();
		GT_MAP_test = dNEEL.getGT_MAP_test();
		mapIterator = GT_MAP_test.entrySet().iterator();
		while (mapIterator.hasNext()) {
			Map.Entry pair = (Map.Entry)mapIterator.next();
			String chave = (String) pair.getKey();			
			String[] elems = chave.split("\t");
			String mention = elems[1].trim().toLowerCase();
			mentionSet.add(mention);
		}

		Iterator<?> setIterator = (Iterator<?>) mentionSet.iterator();

		while(setIterator.hasNext()){
			String mention = (String) setIterator.next();
			if(mentionEntityMap.containsKey(mention)){
				priorMap = mentionEntityMap.get(mention);
				TreeMap<String,Double> sortedMAP = sortByValues(priorMap);
				int c = 0;
				Iterator<?> innerIt = sortedMAP.entrySet().iterator();
				while (innerIt.hasNext()) {
					c++;
					@SuppressWarnings("rawtypes")
					Map.Entry key = (Map.Entry) innerIt.next();
					String candEnt = (String) key.getKey();
//					System.out.println(mention+":\t"+candEnt);
					p.write(candEnt+"\n");
//					it.remove();
					if(c == 30){
						break;
					}
				}
			}
		}
		
		p.close();
	}	
	
	/**
	 * Sort a TreeMap by values
	 *
	 * @param map
	 * @return
	 */
	public static <K, V extends Comparable<V>> TreeMap<K, V> sortByValues(final TreeMap<K, V> map) {
		Comparator<K> valueComparator = new Comparator<K>() {

			public int compare(K k1, K k2) {
				int compare = map.get(k2).compareTo(map.get(k1));
				// System.out.println(compare);
				if (compare == 0)
					return 1;
				else
					return compare;
			}
		};

		TreeMap<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
		sortedByValues.putAll(map);
		return sortedByValues;
	}
	public static BufferedReader getBufferedReaderForCompressedFile(String fileIn) throws FileNotFoundException, CompressorException {
		FileInputStream fin = new FileInputStream(fileIn);
		BufferedInputStream bis = new BufferedInputStream(fin);
		CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
		return br2;
	}
	
}
