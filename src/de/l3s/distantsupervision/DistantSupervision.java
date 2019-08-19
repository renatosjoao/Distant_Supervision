package de.l3s.distantsupervision;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.compress.compressors.CompressorException;
import org.xml.sax.SAXException;

import com.opencsv.CSVWriter;

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

public class DistantSupervision {

	public static void main(String[] args) throws CompressorException, IOException, NumberFormatException, SAXException, ParserConfigurationException{
//		String[] corpora = new String[]{"ace2004", "aquaint", "conll", "derczynski",  "gerdaq", "iitb",  "kore50", "msnbc",  "N3Reuters128", "N3RSS500", "neel", "spotlight", "wp"};
//		for(String corpus : corpora) {
//		String corpus = "ace2004";
//		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
// 		String corpus = "conll";   //Babelfy > Ambiverse > Spotlight >  Tagme
		String corpus = "derczynski";
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
		double[] p = new double[]{1.0,5.0,10.0,15.0,20.0,30.0,40.0,50.0,60.0,70.0,80.0,90.0,100.0};
//		double[] p = new double[]{100.0};

		
		for(double dd : p){
			produceFiles(d,dd,corpus);
		}
//
		TreeMap<String,String> GT_MAP = d.getGT_MAP();
		for(double dd : p){
			checkAccuracy(GT_MAP, dd, corpus);
			System.out.println();	
		}
//		}
		
	}
	
	
	
	
	/**
	 *
	 *	This method is meant to produce the percent files for the distant supervision approach.
	 *
	 * @param d
	 * @param p
	 * @throws CompressorException
	 * @throws IOException
	 */
	public static void produceFiles(DataLoaders d, double p, String corpus) throws CompressorException, IOException{
//		sort -t$'\t' -k5 -nr conll.ambiverse.mappings > conll.ambiverse.mappings.sorted
//		sort -t$'\t' -k5 -nr conll.babelfy.mappings > conll.babelfy.mappings.sorted
//		sort -t$'\t' -k5 -nr conll.tagme.mappings > conll.tagme.mappings.sorted
		
//		head -n 23865 conll.tagme.mappings > conll_tagme_train.mappings
		
		double prop = 0;

//		TreeMap<String,String> ambiverseMap = new TreeMap<String, String>(); 
//		TreeMap<String,String> babelfyMap = new TreeMap<String, String>();
//		TreeMap<String,String> tagmeMap = new TreeMap<String, String>();
//		
	
		OutputStreamWriter Ambp = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/ds.amb."+p+".txt"), StandardCharsets.UTF_8);
		OutputStreamWriter Babp = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/ds.bab."+p+".txt"), StandardCharsets.UTF_8);
		OutputStreamWriter Tagp = new OutputStreamWriter(new FileOutputStream("./resources/ds/"+corpus+"/ds.tag."+p+".txt"), StandardCharsets.UTF_8);
		CSVWriter csvWriterAmbp = new CSVWriter(Ambp, ',' ,  '\'', '\\');
		CSVWriter csvWriterBabp = new CSVWriter(Babp, ',' ,  '\'', '\\');
		CSVWriter csvWriterTagp = new CSVWriter(Tagp, ',' ,  '\'', '\\');
		
		BufferedReader bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/"+corpus+"/mappings/"+corpus+".ambiverse.mappings.sorted"),StandardCharsets.UTF_8));
		BufferedReader bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/"+corpus+"/mappings/"+corpus+".babelfy.mappings.sorted"),StandardCharsets.UTF_8));
		BufferedReader bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/"+corpus+"/mappings/"+corpus+".tagme.mappings.sorted"),StandardCharsets.UTF_8));
	
		
		String line="";
		int countAmbiverse = 0;
		while ((line = bffReaderAmbiverse.readLine()) != null) {
			String[] elements = line.split("\t");
			if(elements.length >=4){
				String docid = elements[0].toLowerCase();
		    	docid = docid.replaceAll("\'", "");
				docid = docid.replaceAll("\"", "");
				String mention = elements[1].toLowerCase();
				String offset =  elements[2];
				String entity = elements[3];
				if(entity.equalsIgnoreCase("null")){ 
					continue;
				}else{
					String confidence = elements[4];
					entity = entity.replaceAll("_"," ").toLowerCase();
					String key = docid+"\t"+mention+"\t"+offset;
					countAmbiverse++;
				}
			}
		}
		
		line="";
		int countBabelfy = 0;
		while ((line = bffReaderBabelfy.readLine()) != null) {
			String[] elements = line.split("\t");
			if(elements.length >=4){
				String docid = elements[0].toLowerCase();
		    	docid = docid.replaceAll("\'", "");
				docid = docid.replaceAll("\"", "");
				String mention = elements[1].toLowerCase();
				String offset =  elements[2];
				String entity = elements[3];
				if(entity.equalsIgnoreCase("null")){ 
					continue;
				}else{
					String confidence = elements[4];
					entity = entity.replaceAll("_"," ").toLowerCase();
					String key = docid+"\t"+mention+"\t"+offset;
					countBabelfy++;
				}
			}
		}
		
		line="";
		int countTagme = 0;
		while ((line = bffReaderTagme.readLine()) != null) {
			String[] elements = line.split("\t");
			if(elements.length >=4){
				String docid = elements[0].toLowerCase();
		    	docid = docid.replaceAll("\'", "");
				docid = docid.replaceAll("\"", "");
				String mention = elements[1].toLowerCase();
				mention = mention.replaceAll("\"", " ");
				String offset =  elements[2];
				String entity = elements[3];
				if(entity.equalsIgnoreCase("null")){ 
					continue;
				}else{
					String confidence = elements[4];
					entity = entity.replaceAll("_"," ").toLowerCase();
					String key = docid+"\t"+mention+"\t"+offset;
					countTagme++;
				}
			}
		}
		
		bffReaderAmbiverse.close();
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		
/* End */
		
		
//		// *** Producing  files  ***//
		bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/"+corpus+"/mappings/"+corpus+".ambiverse.mappings.sorted"),StandardCharsets.UTF_8));
	
		prop = ( p/100.0 )* countAmbiverse;
		
		int count = 0;
		line="";
		while ((line = bffReaderAmbiverse.readLine()) != null) {
			String[] elements = line.split("\t");
			if(elements.length >=4){
				String docid = elements[0].toLowerCase();
		    	docid = docid.replaceAll("\'", "");
				docid = docid.replaceAll("\"", "");
				String mention = elements[1].toLowerCase();
				String offset =  elements[2];
				String entity = elements[3];
				String confidence = elements[4];
				entity = entity.replaceAll("_"," ").toLowerCase();
				count++;
				if(count <= prop ){
					Ambp.write(docid+"\t"+mention+"\t"+offset+"\t"+entity+"\t"+confidence+"\n");
				}
			}
		}
//		System.out.println(prop);
		
		bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/"+corpus+"/mappings/"+corpus+".babelfy.mappings.sorted"),StandardCharsets.UTF_8));
		prop = ( p/100.0 )* countBabelfy;
		count = 0;
		line="";
		while ((line = bffReaderBabelfy.readLine()) != null) {
			String[] elements = line.split("\t");
			if(elements.length >=4){
				String docid = elements[0].toLowerCase();
		    	docid = docid.replaceAll("\'", "");
				docid = docid.replaceAll("\"", "");
				String mention = elements[1].toLowerCase();
				String offset =  elements[2];
				String entity = elements[3];
				String confidence = elements[4];
				entity = entity.replaceAll("_"," ").toLowerCase();
				count++;
				if(count <= prop){
					Babp.write(docid+"\t"+mention+"\t"+offset+"\t"+entity+"\t"+confidence+"\n");
				}
			}
		}
//		System.out.println(prop);
		
		bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/"+corpus+"/mappings/"+corpus+".tagme.mappings.sorted"),StandardCharsets.UTF_8));
		prop = ( p/100.0 )* countTagme;
		count = 0;
		line="";
		while ((line = bffReaderTagme.readLine()) != null) {
			String[] elements = line.split("\t");
			if(elements.length >=4){
				String docid = elements[0].toLowerCase();
		    	docid = docid.replaceAll("\'", "");
				docid = docid.replaceAll("\"", "");
				String mention = elements[1].toLowerCase();
				String offset =  elements[2];
				String entity = elements[3];
				String confidence = elements[4];
				entity = entity.replaceAll("_"," ").toLowerCase();
				count++;
				if(count <= prop){
					Tagp.write(docid+"\t"+mention+"\t"+offset+"\t"+entity+"\t"+confidence+"\n");
				}
			}
		}
//		System.out.println(prop);
		
		bffReaderAmbiverse.close();
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		
		Ambp.close();
		Babp.close();
		Tagp.close();
		
		
	}
	/**
	 *
	 * @param GT_test_MAP
	 * @param p
	 * @throws IOException
	 */
	public static void checkAccuracy(TreeMap<String,String> GT_MAP, double p, String corpus) throws IOException{
		BufferedReader bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("./resources/ds/"+corpus+"/ds.amb."+p+".txt"),StandardCharsets.UTF_8));
		BufferedReader bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("./resources/ds/"+corpus+"/ds.bab."+p+".txt"),StandardCharsets.UTF_8));
		BufferedReader bffReaderTagme = new BufferedReader(new InputStreamReader(new FileInputStream("./resources/ds/"+corpus+"/ds.tag."+p+".txt"),StandardCharsets.UTF_8));
		double P = 0.0;
		double R = 0.0;
		double F = 0.0;
		String line;
		
		int TP = 0;
		int count = 0;
		int rec  = 0 ;
		while ((line = bffReaderAmbiverse.readLine()) != null) {
			String[] elements = line.split("\t");
			if(elements.length >=4){
				rec++;
				String docid = elements[0].toLowerCase();
		    	docid = docid.replaceAll("\'", "");
				docid = docid.replaceAll("\"", "");
				String mention = elements[1].toLowerCase();
				String offset =  elements[2];
				String entity = elements[3];
//				String confidence = elements[4];
				entity = entity.replaceAll("_"," ").toLowerCase();
				
				String key = docid+"\t"+mention+"\t"+offset;
				
				if(GT_MAP.containsKey(key)){
					count++;
					String Elink = GT_MAP.get(key); 
					if(Elink.equalsIgnoreCase(entity)){
						TP++;
					}else{
//						System.out.println(key+"\t"+Elink+"\t"+entity);
					}
				}else{
				}
			}
		}
		P =  (double) TP / (double)rec;
		R =  (double) TP/(double)count;
		double AccA =  (double) TP/(double)count;
		double scale = Math.pow(10, 3);
		AccA =  ( Math.round(AccA * scale) / scale ) * 100.0;
		F = 2*((P*R)/(P+R));
//		System.out.println(TP);
		System.out.println("Ratio :" +p +" &\t"+(P*100.0)+" &\t"+(R*100.0)+" &\t"+(F*100.0));

		
		TP = 0;
		count = 0;
		rec =0;
		while ((line = bffReaderBabelfy.readLine()) != null) {
			String[] elements = line.split("\t");
			if(elements.length >=4){
				rec++;
				String docid = elements[0].toLowerCase();
		    	docid = docid.replaceAll("\'", "");
				docid = docid.replaceAll("\"", "");
				String mention = elements[1].toLowerCase();
				String offset =  elements[2];
				String entity = elements[3];
//				String confidence = elements[4];
				entity = entity.replaceAll("_"," ").toLowerCase();
				String key = docid+"\t"+mention+"\t"+offset;
				if(GT_MAP.containsKey(key)){
					count++;
					String Elink = GT_MAP.get(key); 
					if(Elink.equalsIgnoreCase(entity)){
						TP++;
					}
				}else{
				}
			}
		}
		P =  (double) TP / (double)rec;
		R =  (double) TP/(double)count;
		double AccB =  (double) TP/(double)count;
		AccB =  ( Math.round(AccB * scale) / scale ) * 100.0;
		F = 2*((P*R)/(P+R));
//		System.out.println(TP);
		System.out.println("Ratio :" +p +" &\t"+(P*100.0)+" &\t"+(R*100.0)+" &\t"+(F*100.0));
		
		
		TP = 0;
		count = 0;
		rec=0;
		while ((line = bffReaderTagme.readLine()) != null) {
			String[] elements = line.split("\t");
//			if(elements.length >=4){
				rec++;
				String docid = elements[0].toLowerCase();
		    	docid = docid.replaceAll("\'", "");
				docid = docid.replaceAll("\"", "");
				String mention = elements[1].toLowerCase();
				String offset =  elements[2];
				String entity = elements[3];
//				String confidence = elements[4];
				entity = entity.replaceAll("_"," ").toLowerCase();
				String key = docid+"\t"+mention+"\t"+offset;
				if(GT_MAP.containsKey(key)){
					count++;
					String Elink = GT_MAP.get(key); 
					if(Elink.equalsIgnoreCase(entity)){
						TP++;
					}
				} 
//			}
		}
		P =  (double) TP / (double)rec;
		R =  (double) TP/(double)count;
		double AccT =  (double) TP/(double)count;
		AccT =  ( Math.round(AccT * scale) / scale ) * 100.0;
		F = 2*((P*R)/(P+R));
//		System.out.println(TP);
		System.out.println("Ratio :" +p +" &\t"+(P*100.0)+" &\t"+(R*100.0)+" &\t"+(F*100.0));
//		System.out.println("# Instances Tagme "+p+"% :"+count);
		
		
		bffReaderAmbiverse.close();
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		
		System.out.println("Ratio :" +p +" &\t"+AccA+" &\t"+AccB+" &\t"+AccT);
		
	}
}

//
//for i in AQUAINT_ambiverse_train.mappings AQUAINT_bfy_train.mappings AQUAINT_tagme_train.mappings; do sort -t$'\t' -k5 -nr $i > $i.sorted; done
//
//sort -t$'\t' -k5 -nr conll_ambiverse_train.mappings > conll_ambiverse_train.mappings.sorted
//sort -t$'\t' -k5 -nr conll_bfy_train.mappings > conll_bfy_train.mappings.sorted
//sort -t$'\t' -k5 -nr conll_tagme_train.mappings > conll_tagme_train.mappings.sorted
//
//for i in gerdaq_ambiverse_train.mappings gerdaq_bfy_train.mappings gerdaq_tagme_train.mappings; do sort -t$'\t' -k5 -nr $i > $i.sorted; done
//
//
//sort -t$'\t' -k5 -nr  iitb_ambiverse.9.mappings > iitb_ambiverse_train.mappings.sorted
//sort -t$'\t' -k5 -nr  iitb_bfy.9.mappings > iitb_bfy_train.mappings.sorted
//sort -t$'\t' -k5 -nr  iitb_tagme.9.mappings > iitb_tagme_train.mappings.sorted
//
//for i in MSNBC_ambiverse_train.mappings MSNBC_bfy_train.mappings MSNBC_tagme_train.mappings; do sort -t$'\t' -k5 -nr $i > $i.sorted; done
//
//sort -t$'\t' -k5 -nr   NEEL2016-training_ambiverse.mappings > neel_ambiverse_train.mappings.sorted
//sort -t$'\t' -k5 -nr   NEEL2016-training_bfy.mappings > neel_bfy_train.mappings.sorted
//sort -t$'\t' -k5 -nr   NEEL2016-training_tagme.mappings > neel_tagme_train.mappings.sorted
//
//
//sort -t$'\t' -k5 -nr wp_ambiverse.9.mappings  > wp_ambiverse_train.mappings.sorted
//[joao@master03 mappings]$ sort -t$'\t' -k5 -nr wp_bfy.9.mappings  > wp_bfy_train.mappings.sorted
//[joao@master03 mappings]$ sort -t$'\t' -k5 -nr wp_tagme.9.mappings  > wp_tagme_train.mappings.sorted
