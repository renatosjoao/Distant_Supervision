package de.l3s.distantsupervision;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

import de.l3s.loaders.DataLoaders;
import de.l3s.loaders.DataLoaders_ACE2004;
import de.l3s.loaders.DataLoaders_AQUAINT;
import de.l3s.loaders.DataLoaders_CONLL;
import de.l3s.loaders.DataLoaders_GERDAQ;
import de.l3s.loaders.DataLoaders_IITB;
import de.l3s.loaders.DataLoaders_KORE50;
import de.l3s.loaders.DataLoaders_MSNBC;
import de.l3s.loaders.DataLoaders_NEEL;
import de.l3s.loaders.DataLoaders_N3Reuters128;
import de.l3s.loaders.DataLoaders_WP;

public class Evaluation {

	
	public static void main(String[] args) throws Exception{
		String corpus = "ace2004";
//		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
//		String corpus = "conll";   //Babelfy > Ambiverse > Spotlight >  Tagme
//		String corpus = "derczynkski";
//		String corpus = "gerdaq";  // Tagme > Babelfy > Ambiverse 
//		String corpus = "iitb";    //Ambiverse > Spotlight >  Tagme > Babelfy
//		String corpus = "kore50";
// 		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
//		String corpus = "N3News100";  
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
		if(corpus.equalsIgnoreCase("neel")){
			d = DataLoaders_NEEL.getInstance();
		}
		if(corpus.equalsIgnoreCase("reuters128")){
			d = DataLoaders_N3Reuters128.getInstance();
		}
		if(corpus.equalsIgnoreCase("wp")){
			d = DataLoaders_WP.getInstance();
		}

//		double[] p = new double[]{10.0,20.0,30.0,40.0,50.0,60.0,70.0,80.0,90.0,100.0};
		double[] p = new double[]{10.0,20.0,40.0,80.0,100.0};
		
		for(double ratio : p){
			String predictionsFile = "./resources/ds/"+corpus+"/dataset.multilabel."+corpus+"."+ratio+".STRICT.pred.out";
			Evaluation.eval(d, ratio, predictionsFile);
		}

		
	}
	
	public static void eval(DataLoaders d,double  ratio, String predictionsFile) throws IOException{
		TreeMap<String,String> GT_MAP = d.getGT_MAP();
		
		BufferedReader predBuff = new BufferedReader(new InputStreamReader(new FileInputStream(predictionsFile),StandardCharsets.UTF_8));
		String line = "";
		int TP = 0;
		int numRECOGNIZED = 0;
		
		while((line = predBuff.readLine() )!= null) {
			
			numRECOGNIZED++;
			String[] elems = line.split("\t");
			String docid = elems[0];
			String mention = elems[1];
			String offset = elems[2];
			String PredLink = elems[3];
	        
			String key = docid + "\t" + mention + "\t" + offset;
			String GTLink = "NULL";
			GTLink = GT_MAP.get(key);
			if(PredLink.equalsIgnoreCase(GTLink)){
				TP+=1;
			}
		}
		predBuff.close();
		
		double P = 0.0;//
		double R = 0.0;
		double F = 0.0;
		
		P =  (double) TP / (double)numRECOGNIZED;
		R =  (double) TP/(double) GT_MAP.keySet().size();
		F = 2*((P*R)/(P+R));
//		P = round(P,4)*100.0;
//		R = round(R,4)*100.0;
//		F = round(F,4)*100.0;
		
		
		System.out.println("@"+ratio+" \t& "+P+" &\t"+R+" &\t"+F);
//		System.out.println(TP);
//		System.out.println(numRECOGNIZED);
		
	
	}
	
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
}
