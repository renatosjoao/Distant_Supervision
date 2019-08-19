package de.l3s.loaders;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import com.opencsv.CSVReader;

public class DataLoaders_Prior {

	public static DataLoaders_Prior instance = null;

	
	public static void main(String[] args) throws NumberFormatException,  CompressorException, IOException{
		DataLoaders_Prior d = DataLoaders_Prior.getInstance();
		TreeMap<String,String> PriorMAP = new TreeMap<String, String>();
//		d.loadPrior("/home/joao/git/WikiParsing/resources/mentionentity.20160701.prior.csv.bz2");
		d.loadPrior("/home/joao/git/WikiParsing/resources/mentionentity.20181020.prior.csv.bz2");
		System.out.println(PriorMAP.get("american")); //united states
		System.out.println(PriorMAP.get("bay laurel")); //bay laurel
		System.out.println(PriorMAP.get("cartwright")); //cartwright new foundland and labrador
		System.out.println(PriorMAP.get("duke zhuang")); //duke zhuang of zheng
		System.out.println(PriorMAP.get("lamsdorf")); //łambinowice
		
		PriorMAP = d.loadPrior("/home/joao/git/WikiParsing/resources/mentionentity.20181020.prior.csv.bz2");

		

	}
	
	

	public static DataLoaders_Prior getInstance() throws CompressorException {
		if(instance == null) {
			 synchronized(DataLoaders_Prior.class) {
				 instance = new DataLoaders_Prior();
			 }
	    }
		return  (DataLoaders_Prior) instance;
	}
	
	
	
	@SuppressWarnings("deprecation")
	public TreeMap<String,String> loadPrior(String inputFile) throws IOException, CompressorException {
		TreeMap<String,String> pMap = new TreeMap<String, String>();
		FileInputStream fin = new FileInputStream(inputFile);
		BufferedInputStream bis = new BufferedInputStream(fin);
		CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);

		CSVReader reader = null;
		reader = new CSVReader(new InputStreamReader(input, StandardCharsets.UTF_8),',','\'');
		String[] row = null;
		while ((row = reader.readNext()) != null) {
			String mention = row[0].trim().toLowerCase();
			String entitylink  = row[1].trim().toLowerCase();
			if(!pMap.containsKey(mention)){
				pMap.put(mention, entitylink);
			}
			//String E = PriorMAP.get(mention);
//			System.out.println(mention);
			
		}
		reader.close();
		System.out.println(" # elements : "+pMap.keySet().size());
		return pMap;
		
	}
	

	



	public static BufferedReader getBufferedReaderForCompressedFile(String fileIn) throws FileNotFoundException, CompressorException {
		FileInputStream fin = new FileInputStream(fileIn);
		BufferedInputStream bis = new BufferedInputStream(fin);
		CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
		BufferedReader br2 = new BufferedReader(new InputStreamReader(input,StandardCharsets.UTF_8));
		return br2;
}

	
	
}
