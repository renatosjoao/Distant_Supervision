package de.l3s.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.compress.compressors.CompressorException;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.plot.BarnesHutTsne;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.io.ClassPathResource;
import org.nd4j.linalg.primitives.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Word2VecLoader {

    private static Logger log = LoggerFactory.getLogger(Word2VecLoader.class);

    private static Word2Vec word2Vec = null;
	public static Word2VecLoader instance = null;
	
    
    /**
     *
     */
	public Word2VecLoader() {
		super();
		word2Vec = new Word2Vec();
		loadWord2Vecmodel();
	}

	/**
	 *
	 * @return
	 * @throws CompressorException
	 * @throws IOException
	 */
	public static Word2VecLoader getInstance() throws CompressorException, IOException {
		if(instance == null) {
			 synchronized(Word2VecLoader.class) {
				 instance = new Word2VecLoader();
			 }
	    }
		return instance;
	}
	
	
	
	/** 
	 * 	Utility function just to load the word2vec model.
	 * 
	 */
	public static void loadWord2Vecmodel(){
		System.out.println("Loading vectors model....");
		word2Vec = WordVectorSerializer.readWord2VecModel("/home/joao/Wiki2016/model/word2vec_gensim.txt");
		System.out.println("...........done....");
	}




	/**
	 * 			This method simply sums up two vectors.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double[] SumVectors(double[] a, double[] b){
		int sizeA =  a.length;
//		int sizeB =  b.length;
		double [] sum = new double [sizeA];
		for (int i = 0; i < b.length; i++){
			sum[i] = 0.0;
			if (i > b.length){ 
				sum[i] = b[i] + 0;
			}else{
				sum[i] = a[i] + b[i];
				}
		}
		double[] resultingVec = new double [sizeA];
		for (int i = 0; i < b.length; i++){
			resultingVec[i] = sum[i] / 2.0;
		}
		return sum;
	}
	
	/**
	 * 
	 * 			This is a utility function to calculate a mean vector from all the words in the input text.
	 * 
	 * 
	 * @param Wmodel
	 * @param text
	 * @return
	 */
	public static double[] gettextVector(Word2Vec Wmodel, String text){
		String sElems[] =  text.split("\\s+");
		double[] resultingVec = new double [200];
		for (int i = 0; i < resultingVec.length; i++){
			resultingVec[i] =  0.0;
		}
		for(String term : sElems){
			double[] termVec = null;
			try{
				termVec = Wmodel.getWordVector(term);
			}catch(Exception e){
				continue;
			}
			if(termVec!=null){
				resultingVec = SumVectors(resultingVec,termVec);
			}
		}
		return resultingVec;
	}
	
	/**
	 * 		Utility function to calculate cosine similarity
	 * 
	 * @param vectorA
	 * @param vectorB
	 * @return
	 */
	public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
	    double dotProduct = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < vectorA.length; i++) {
	        dotProduct += vectorA[i] * vectorB[i];
	        normA += Math.pow(vectorA[i], 2);
	        normB += Math.pow(vectorB[i], 2);
	    }   
	    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}
	
	
	
	
	
	
	public static Word2Vec getWord2Vec() {
		return word2Vec;
	}

	public static void setWord2Vec(Word2Vec word2Vec) {
		Word2VecLoader.word2Vec = word2Vec;
	}

//	/**
//	 *
//	 * @param args
//	 * @throws IOException
//	 */
	public static void main(String[] args) throws IOException {
//		BufferedReader bff = new BufferedReader(new FileReader(new File("/home/joao/Wiki2016/wiki.en.tsv")));
//		String line = "";
//		int i=0;
//		while ((line = bff.readLine()) != null) {
//			String elems[] = line.split("\t");
//			String titulo = elems[0];
//			System.out.println(titulo);
//			
//			if(elems.length == 1){
//				continue;
////				for(int j = 1; j < elems.length ; j++)
////				conteudo = conteudo + " " + elems[j];
//			}
//			String conteudo = elems[1];
//			conteudo = conteudo.replaceAll("\\s+", " "); 
////			System.out.println(elems.length);
////			System.out.println(i++);
//			
//		}
//		
//		System.exit(1);
		Word2VecLoader Wmodel = new Word2VecLoader();
	    Word2Vec word2Vec = WordVectorSerializer.readWord2VecModel("/home/joao/Wiki2016/model/word2vec_gensim");
//      Collection<String> kingList = word2Vec.wordsNearest(Arrays.asList("king", "woman"), Arrays.asList("queen"), 10);
//      System.out.println(kingList);

//	    log.info("Save vectors....");
//      WordVectorSerializer.writeWord2VecModel(vec, "pathToSaveModel.txt");
	}
//		
//        WeightLookupTable weightLookupTable = word2Vec.lookupTable();
//        Iterator<INDArray> vectors = weightLookupTable.vectors();
//        INDArray wordVectorMatrix = word2Vec.getWordVectorMatrix("and");

//		
//		double[] billwordVector = word2Vec.getWordVector("bill");
//        double[] clintonwordVector = word2Vec.getWordVector("clinton");
//        double[] hillarywordVector = word2Vec.getWordVector("hillary");
//        double[] kingwordVector = word2Vec.getWordVector("king");
//        double[] queenwordVector = word2Vec.getWordVector("queen");

//        
//        double[] billbill = Wmodel.SumVectors(billwordVector,billwordVector);
//        double[] billclinton = Wmodel.SumVectors(billwordVector,clintonwordVector);
//        double[] hillaryhillary = Wmodel.SumVectors(hillarywordVector,hillarywordVector);
//        double[] hillaryclinton = Wmodel.SumVectors(hillarywordVector,clintonwordVector); 
//        double[] billhillary = Wmodel.SumVectors(billwordVector,hillarywordVector);
//        double[] kingqueen = Wmodel.SumVectors(billwordVector,billwordVector);
//        
//        System.out.println();
//        System.out.println(Wmodel.cosineSimilarity(billclinton, billclinton));
//        System.out.println();
//        System.out.println(Wmodel.cosineSimilarity(billclinton, hillaryclinton));
//        System.out.println();
//        System.out.println(Wmodel.cosineSimilarity(hillaryclinton, hillaryclinton));
//        System.out.println();
//        System.out.println(Wmodel.cosineSimilarity(billwordVector, hillarywordVector));
//        System.out.println();
//        System.out.println(Wmodel.cosineSimilarity(kingwordVector, queenwordVector));
//        System.out.println();
//        
//      System.out.println();
//      System.out.println(word2Vec.similarity("bill","bill"));
//      System.out.println();
//      System.out.println(word2Vec.similarity("bill","clinton"));
//      System.out.println();
//      System.out.println(word2Vec.similarity("hillary","hillary"));
//      System.out.println();
//      System.out.println(word2Vec.similarity("hillary","clinton"));
//      System.out.println();
//      System.out.println(word2Vec.similarity("bill","hillary"));
//      System.out.println();
//		System.out.println(word2Vec.similarity("king","queen"));
////        
        
//        String sbill = "bill";
////        String shillary = "hillary";
////        String sclinton = "clinton";
////      
////        String sbillbill  = "bill bill";
//		String sbillclinton  = "bill clinton";
////        String shillaryhillary = "hillary hillary";
////        String shillaryclinton = "hillary clinton";
////        String sbillhillary = "bill hillary";
////        String skingqueen = "king queen";
////        
////        String text1 = "president barack obama invited george bush for a meeting about the united states economical situation";
////        String text2 = "president george bush discussed with hillary rodham clinton";
////        String text3 = "tennis player selina won the australia open";
////        String text4 = "michael schumaker is the world champion in formula 1";
////        System.out.println();
//        System.out.println(Wmodel.cosineSimilarity(Wmodel.gettextVector(word2Vec, text1),Wmodel.gettextVector(word2Vec, text1)));
//        System.out.println();
//        System.out.println(Wmodel.cosineSimilarity(Wmodel.gettextVector(word2Vec, text1),Wmodel.gettextVector(word2Vec, text2)));
//        System.out.println();
//        System.out.println(Wmodel.cosineSimilarity(Wmodel.gettextVector(word2Vec, text1),Wmodel.gettextVector(word2Vec, text3)));
//        
//        System.out.println();
//        System.out.println(Wmodel.cosineSimilarity(Wmodel.gettextVector(word2Vec, text2),Wmodel.gettextVector(word2Vec, text3)));
//        System.out.println();
//        System.out.println(Wmodel.cosineSimilarity(Wmodel.gettextVector(word2Vec, text2),Wmodel.gettextVector(word2Vec, text4)));
//        System.out.println();
//        System.out.println(Wmodel.cosineSimilarity(Wmodel.gettextVector(word2Vec, text3),Wmodel.gettextVector(word2Vec, text4)));
//
//        
        
        
//      System.out.println(Wmodel.cosineSimilarity(hillarywordVector, hillarywordVector));
//      System.out.println();
//      System.out.println(Wmodel.cosineSimilarity(hillarywordVector, clintonwordVector));
//      System.out.println();
//      System.out.println(Wmodel.cosineSimilarity(billwordVector, hillarywordVector));
//      System.out.println();
//      System.out.println(Wmodel.cosineSimilarity(kingwordVector, queenwordVector));
//      System.out.println();
        
//      System.out.println(word2Vec.similarity("hillary","clinton"));
        
//
//		Nd4j.setDataType(DataBuffer.Type.DOUBLE);
//
//		List<String> cacheList = new ArrayList<>(); // cacheList is a dynamic
//													// array of strings used to
//													// hold all words
//
//		// STEP 2: Turn text input into a list of words
//		System.out.println("Load & Vectorize data....");
//		File wordFile = new ClassPathResource("words.txt").getFile(); // Open
//																		// the
//																		// file
//		// Get the data of all unique word vectors
//		Pair<InMemoryLookupTable, VocabCache> vectors = WordVectorSerializer.loadTxt(wordFile);
//		VocabCache cache = vectors.getSecond();
//		INDArray weights = vectors.getFirst().getSyn0(); // seperate weights of
//															// unique words into
//															// their own list
//
//		for (int i = 0; i < cache.numWords(); i++)
//			// seperate strings of words into their own list
//			cacheList.add(cache.wordAtIndex(i));
//
//		// STEP 3: build a dual-tree tsne to use later
//		System.out.println("Build model....");
//		BarnesHutTsne tsne = new BarnesHutTsne.Builder().setMaxIter(iterations)
//				.theta(0.5).normalize(false).learningRate(500)
//				.useAdaGrad(false)
//				// .usePca(false)
//				.build();
//
//		// STEP 4: establish the tsne values and save them to a file
//		System.out.println("Store TSNE Coordinates for Plotting....");
//		String outputFile = "target/archive-tmp/tsne-standard-coords.csv";
//		(new File(outputFile)).getParentFile().mkdirs();
//
//		tsne.fit(weights);
//		tsne.saveAsFile(cacheList, outputFile);
//	}

}
