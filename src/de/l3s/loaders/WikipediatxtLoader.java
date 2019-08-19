package de.l3s.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

import org.apache.commons.compress.compressors.CompressorException;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;

public class WikipediatxtLoader {
	
	
	public static WikipediatxtLoader instance = null;
	private static TreeMap<String,String> textMap = null;
	
	/**
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		WikipediatxtLoader wl = new WikipediatxtLoader();
		textMap = wl.getTextMap();
		if(textMap.containsKey("ryan harris (cricketer)")){
			System.out.println("ryan harris (cricketer)");
			System.out.println(textMap.get("ryan harris (cricketer)"));
		}
		if(textMap.containsKey("china")){
			System.out.println("china");
			System.out.println(textMap.get("china"));
		}
		
		
			
	}
	
    /**
     * @throws IOException 
    *
    */
	public WikipediatxtLoader() throws IOException {
		super();
		textMap = new TreeMap<String,String>();
		loadWikitextcorpus();
	}
	
	/**
	 *
	 * @return
	 * @throws CompressorException
	 * @throws IOException
	 */
	public static WikipediatxtLoader getInstance() throws CompressorException, IOException {
		if(instance == null) {
			 synchronized(WikipediatxtLoader.class) {
				 instance = new WikipediatxtLoader();
			 }
	    }
		return instance;
	}
	
	
	
	/** 
	 * 	Utility function just to load the word2vec model.
	 * @throws IOException 
	 * 
	 */
	public static void loadWikitextcorpus() throws IOException{
		BufferedReader bff = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/Wiki2016/wiki.en.tsv"),StandardCharsets.UTF_8));
		String line = "";
		while ((line = bff.readLine()) != null) {
			String elems[] = line.split("\t");
			String titulo = elems[0].trim();
			if(elems.length == 1){
				continue;
			}
			String conteudo = elems[1];
			conteudo = conteudo.replaceAll("\\s+", " ");
			conteudo = conteudo.trim();
			titulo = titulo.toLowerCase();
			textMap.put(titulo, conteudo);
		}
		bff.close();
		System.out.println("Loaded Wikipedia successfully");
	}

	

	public static TreeMap<String, String> getTextMap() {
		return textMap;
	}



	public static void setTextMap(TreeMap<String, String> textMap) {
		WikipediatxtLoader.textMap = textMap;
	}



	

}
