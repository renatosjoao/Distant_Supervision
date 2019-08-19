package de.l3s.loaders;

import java.io.IOException;

import org.apache.commons.compress.compressors.CompressorException;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;

public class Doc2VecLoader {

	public static ParagraphVectors doc2Vec = null;
	public static Doc2VecLoader instance = null;

	public Doc2VecLoader() {
		super();
//		loadDoc2Vecmodel();
	}
	
	
	/**
	 *
	 * @return
	 * @throws CompressorException
	 * @throws IOException
	 */
	public static Doc2VecLoader getInstance() throws CompressorException, IOException {
		if(instance == null) {
			 synchronized(Doc2VecLoader.class) {
				 instance = new Doc2VecLoader();
			 }
	    }
		return instance;
	}
	
	
//	/** 
//	 * 	Utility function just to load the word2vec model.
//	 * 
//	 */
//	public static void loadWord2Vecmodel(){
//		System.out.println("Loading vectors model....");
//		word2Vec = WordVectorSerializer.readWord2VecModel("/home/joao/Wiki2016/wiki.en.model.txt");
//		System.out.println("...........done....");
//	}


	
}
