package de.l3s.extra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class IITB_Utils {
	private static final Logger logger_ = LoggerFactory.getLogger(IITB_Utils.class);

	
	public static void main(String[] args) throws ParserConfigurationException, IOException {

		dumpGT();
		//calculateStatistics();
//		generalStats();
	
	
	}
	
	
	public IITB_Utils(){
		
	}
	
	
	public TreeMap<String, Integer> getDocsMap() throws IOException{
		TreeMap<String,Integer> docsMap = new TreeMap<String,Integer>();
		
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/iitb_GT.tsv"),StandardCharsets.UTF_8));
		String line = "";
		while((line = bffReader.readLine()) != null){
			String[] elems = line.split("\t");
			String docName = elems[0];
		
			Integer count = docsMap.get(docName);
			if (count == null) {
				docsMap.put(docName,1);
			}else{
				count+=1;
				docsMap.put(docName,count);
			}
		}
		bffReader.close();
		return docsMap;
        
	}
	
	/**
	 * 
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public static void dumpGT() throws ParserConfigurationException, IOException {
		int numannotations = 0;
		int numNIL = 0;
		TreeMap<String,Integer> docsMap = new TreeMap<String,Integer>();
		OutputStreamWriter pp = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/iitb/iitb_GT.tsv"),StandardCharsets.UTF_8);
		OutputStreamWriter ppnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/iitb/iitb_GT_NONIL.tsv"),StandardCharsets.UTF_8);

		 try {
	         File inputFile = new File("/home/joao/datasets/iitb/CSAW_Annotations.xml");
	         DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         Document doc = dBuilder.parse(inputFile);
	         doc.getDocumentElement().normalize();
	         System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	         NodeList nList = doc.getElementsByTagName("annotation");
	         System.out.println("----------------------------");
//	         
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	            Node nNode = nList.item(temp);
	            numannotations+=1;
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	               Element eElement = (Element) nNode;
	               String docName = eElement.getElementsByTagName("docName").item(0).getTextContent();
	               String wikiName =  eElement.getElementsByTagName("wikiName").item(0).getTextContent();
	               String offset = eElement.getElementsByTagName("offset").item(0).getTextContent();
	               String length = eElement.getElementsByTagName("length").item(0).getTextContent();
	              
	               String filePAth = "/home/joao/datasets/iitb/crawledDocs/"+docName;
	               Scanner scanner = new Scanner( new File(filePAth), "UTF-8"  );
	               String text = scanner.useDelimiter("\\A").next();
	               scanner.close(); 
	                
	               Integer position = Integer.parseInt(offset);
	       		   Integer len = Integer.parseInt(length);

	       		   String mention = text.substring(position,position+len);
	       		   System.out.println(docName+" > "+mention+" > "+wikiName+" > "+position+" > "+len);
//
	               if(wikiName.isEmpty()){
	            	   wikiName = "null";
	            	   numNIL+=1;
	               }else{
		               ppnn.write(docName+"\t"+mention+"\t"+offset+"\t"+wikiName+"\n");
	               }
	               pp.write(docName+"\t"+mention+"\t"+offset+"\t"+wikiName+"\n");

	               Integer count = docsMap.get(docName);
	               if (count == null) {
	            	   docsMap.put(docName,1);
	               }else{
	            	   count+=1;
	            	   docsMap.put(docName,count);
	               }
	            }
	         }
	         System.out.println("Number of docs : "+docsMap.keySet().size());
	         System.out.println("Number of annotations : "+(numannotations-numNIL));
	         System.out.println("Number of nils : "+numNIL);
			 System.out.println("Total Number of annotations : "+numannotations);
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
		pp.flush();
		ppnn.flush();
		pp.close();
		ppnn.close();
	}
	
	
	
	
	
	
	
	
	public static void calculateStatistics() throws IOException{
		
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);

			int numMentionsAmbiverse, numMentionsTagme, numMentionsBabelfy = 0;
			TreeMap<String,  String> ambiverseMap = new TreeMap<String,  String>();	
			TreeMap<String,  String> babelMap = new TreeMap<String,  String>();
			TreeMap<String,  String> tagmeMap = new TreeMap<String,  String>();
																				
			BufferedReader bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb_ambiverse.mappings"),StandardCharsets.UTF_8));
			BufferedReader bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb_bfy.mappings"),StandardCharsets.UTF_8));
			BufferedReader bffReaderTagme =   new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb_tagme.mappings"),StandardCharsets.UTF_8));
		
//			OutputStreamWriter pwC1  =  new OutputStreamWriter(new FileOutputStream("/home/joao/iitb/annotations/agreements/all/iitb_ALL.aggreement"),StandardCharsets.UTF_8);
//			OutputStreamWriter pwC21 =  new OutputStreamWriter(new FileOutputStream("/home/joao/iitb/annotations/agreements/ambiv_tagme/iitb_ambiv_tagme.aggreement"),StandardCharsets.UTF_8);
//			OutputStreamWriter pwC31 =  new OutputStreamWriter(new FileOutputStream("/home/joao/iitb/annotations/agreements/ambiv_babel/iitb_ambiv_babel.aggreement"),StandardCharsets.UTF_8);
//			OutputStreamWriter pwC41 =  new OutputStreamWriter(new FileOutputStream("/home/joao/iitb/annotations/agreements/babel_tagme/iitb_babel_tagme.aggreement"),StandardCharsets.UTF_8);
//			
//			OutputStreamWriter pwC22 =  new OutputStreamWriter(new FileOutputStream("/home/joao/iitb/annotations/disagreements/ambiv_tagme/iitb_ambiv_tagme.disag"),StandardCharsets.UTF_8);
//			OutputStreamWriter pwC32 =  new OutputStreamWriter(new FileOutputStream("/home/joao/iitb/annotations/disagreements/ambiv_babel/iitb_ambiv_babel.disag"),StandardCharsets.UTF_8);
//			OutputStreamWriter pwC42 =  new OutputStreamWriter(new FileOutputStream("/home/joao/iitb/annotations/disagreements/babel_tagme/iitb_babel_tagme.disag"),StandardCharsets.UTF_8);
//			OutputStreamWriter pwC5  =  new OutputStreamWriter(new FileOutputStream("/home/joao/iitb/annotations/disagreements/all/iitb_ALL.disag"),StandardCharsets.UTF_8);
//		
			String line="";
		
			while ((line = bffReaderAmbiverse.readLine()) != null) {
				String[] elements = line.split("\t");
				if(elements.length >=4){
					String docId = elements[0];
					//System.out.println(docId);
					String mention = elements[1];
					String offset =  elements[2];
					String entity = elements[3];
					entity = entity.replaceAll("_"," ");
					ambiverseMap.put(docId+"\t"+mention+"\t"+offset,entity);
				}

			}

			line="";
			while ((line = bffReaderTagme.readLine()) != null) {
				String[] elements = line.split("\t");
				if(elements.length >=4){
					String docId = elements[0];
					//docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
					String mention = elements[1];
					String offset =  elements[2];
					String entity = elements[3];
					entity = entity.replaceAll("_"," ");
					tagmeMap.put(docId+"\t"+mention+"\t"+offset,entity);
				}
			}

			line="";
			while ((line = bffReaderBabelfy.readLine()) != null) {
				String[] elements = line.split("\t");
				if(elements.length >=4){
					String docId = elements[0];
					String mention = elements[1];
					String offset =  elements[2];
					String entity = elements[3];
					entity = entity.replaceAll("_"," ");
					babelMap.put(docId+"\t"+mention+"\t"+offset,entity);
				}
			}
		
			bffReaderBabelfy.close();
			bffReaderTagme.close();
			bffReaderAmbiverse.close();
			
			//[Exp] Derive the following statistics PER YEAR (as well as for all 20 years):
			int common = 0,equals = 0;
			
			Set<String> chavesAm = ambiverseMap.keySet();
			Set<String> chavesB = babelMap.keySet();
			Set<String> chavesT = tagmeMap.keySet();
			
			numMentionsAmbiverse = chavesAm.size();
			numMentionsTagme = chavesT.size();
			numMentionsBabelfy = chavesB.size();	
			
			common = 0;
			equals = 0;
			//[Exp1] Number and Percentage of mentions in articles mapped to the same entity ID by ALL entity linking systems.
			for(String value : chavesAm) {
				if(tagmeMap.containsKey(value)){
					if(babelMap.containsKey(value)){
						common++;
						String eFromT = tagmeMap.get(value);
						//String eFromA = aidaMap.get(value).iterator().next();
						String eFromAm = ambiverseMap.get(value);
						String eFromB = babelMap.get(value);
						if( (eFromT.equalsIgnoreCase(eFromAm)) && (eFromAm.equalsIgnoreCase(eFromB)) ){
							equals++;
//							pwC1.write(value + "\t"+"ambiverse:"+eFromAm+ "; "+"babelfy:"+eFromB+"; "+"tagme:"+eFromT+"\n");
						}else{
						//logger_.info(value + " : "+eFromA+ " : "+eFromB+" : "+eFromT);
						}
					}
				}
			
			}
			logger_.info("[Exp 1]. "+numMentionsAmbiverse+":(AMBIVERSE), "+numMentionsTagme+":(TAGME) "+numMentionsBabelfy+":(Babelfy) " + " => " +common + " & "+ equals +" "+ df.format((((double)equals/common)*100.0)) );
		
			common = 0;
			equals = 0;
			//[Exp2] Number and Percentage of mentions in articles, mapped to the same entity ID by Ambiverse and TagMe
			for(String value : chavesAm) {
				if(tagmeMap.containsKey(value)){
					common++;
					String eFromT = tagmeMap.get(value);
					String eFromAm = ambiverseMap.get(value);

					String eFromB = "";
					if(babelMap.containsKey(value)){
						eFromB = babelMap.get(value);
					}else{
						eFromB = "null";
					}
				
					if(eFromT.equalsIgnoreCase(eFromAm)){
						equals++;
//						pwC21.write(value + "\t"+"ambiverse:"+eFromAm+ "; " +"tagme:"+eFromT+ "; " +"babelfy:"+eFromB+"\n");
					}else{
//						pwC22.write(value + "\t"+"ambiverse:"+eFromAm+ "; " +"tagme:"+eFromT+ "; " +"babelfy:"+eFromB+"\n");
					}
				}
			}
			logger_.info("[Exp 2]. "+numMentionsAmbiverse+":(AMBIVERSE), "+numMentionsTagme+":(TAGME) " + " => " +common + " & "+ equals +"  "+ df.format((((double)equals/common)*100.0)));
		
			common = 0;
			equals = 0;
			//[Exp3] Number and Percentage of mentions in articles, mapped to the same entity ID by Ambiverse and BabelFy
			for(String value : chavesAm) {
				if(babelMap.containsKey(value)){
					common++;
					String eFromB = babelMap.get(value);
					String eFromAm = ambiverseMap.get(value);
				
					String eFromT = "";
					if(tagmeMap.containsKey(value)){
						eFromT = tagmeMap.get(value);
					}else{
						eFromT = "null";
					}
				
					if(eFromB.equalsIgnoreCase(eFromAm)){
						equals++;
//						pwC31.write(value + "\t"+"ambiverse:"+eFromAm+ "; " +"babelfy:"+eFromB+ "; " +"tagme:"+eFromT+"\n");
					}else{
//						pwC32.write(value + "\t"+"ambiverse:"+eFromAm+ "; " +"babelfy:"+eFromB+ "; " +"tagme:"+eFromT+"\n");
					}
				}
			}
			logger_.info("[Exp 3]. "+numMentionsAmbiverse+":(AMBIVERSE), "+numMentionsBabelfy+":(Babelfy) " + " => " +common + " & "+ equals +" "+df.format((((double)equals/common)*100.0)));
		
			//[Exp4] Number and Percentage of mentions in articles, mapped to the same entity ID by BabefLy and TagMe
			common = 0;
			equals = 0;
			for(String value : chavesB) {
				if(tagmeMap.containsKey(value)){
					common++;
					String eFromB = babelMap.get(value);
					String eFromT = tagmeMap.get(value);
				
					String eFromAm ="";
					if(ambiverseMap.containsKey(value)){
						eFromAm = ambiverseMap.get(value);
					}else{
					eFromAm = "null";
					}
				
					if(eFromB.equalsIgnoreCase(eFromT)){
						equals++;
//						pwC41.write(value + "\t"+"tagme:"+eFromT+ "; " +"babelfy:"+eFromB+ "\t"+"ambiverse:"+eFromAm+"\n");
					}else{
//						pwC42.write(value + "\t"+"tagme:"+eFromT+ "; " +"babelfy:"+eFromB+ "\t"+"ambiverse:"+eFromAm+"\n");
					}
				}
			}
			logger_.info("[Exp 4]. "+numMentionsTagme+":(Tagme), "+numMentionsBabelfy+":(Babelfy) " + " => " +common + " & "+ equals +" "+df.format((((double)equals/common)*100.0)));
		
			common = 0;
			equals = 0;
			//[Exp5] Number and Percentage of mentions in articles, mapped to different entity ID by all entity linking systems (I guess this should be very small or maybe zero)
			for(String value : chavesAm) {
				if(tagmeMap.containsKey(value)){
					if(babelMap.containsKey(value)){
						common++;
						String eFromT = tagmeMap.get(value);
						String eFromAm = ambiverseMap.get(value);
						String eFromB = babelMap.get(value);
						if( (!eFromT.equalsIgnoreCase(eFromAm)) && (!eFromAm.equalsIgnoreCase(eFromB)) && (!eFromT.equalsIgnoreCase(eFromB)) ){
//							pwC5.write(value + "\t"+"ambiverse:"+eFromAm+ "; "+"babelfy:"+eFromB+"; "+"tagme:"+eFromT+"\n");
							equals++;
						}
					}
				}
			
			}
//			pwC1.flush();
//			pwC1.close();
//			pwC21.flush();
//			pwC21.close();
//			pwC22.close();
//			pwC22.close();
//			pwC31.flush();
//			pwC31.close();
//			pwC32.flush();
//			pwC32.close();
//			pwC41.flush();
//			pwC41.close();
//			pwC42.flush();
//			pwC42.close();
//			pwC5.flush();
//			pwC5.close();
//			System.gc();
			logger_.info("[Exp 5]. "+numMentionsAmbiverse+":(AMBIVERSE), "+numMentionsTagme+":(TAGME) "+numMentionsBabelfy+":(Babelfy) " + " => " +common + " & "+ equals +" "+df.format((((double)equals/common)*100.0)));
//			Process p1 = null, p2 = null, p3 = null, p4 = null, p5 = null, p6 = null, p7 = null, p8 = null;
//			try {
//				p1 = Runtime.getRuntime().exec("bzip2 "+path+"/annotations/agreements/all/"+year+"_ALL.aggreement &");
//				p2 = Runtime.getRuntime().exec("bzip2 "+path+"/annotations/agreements/ambiv_tagme/"+year+"_ambiv_tagme.aggreement &");
//				p3 = Runtime.getRuntime().exec("bzip2 "+path+"/annotations/agreements/ambiv_babel/"+year+"_ambiv_babel.aggreement &");
//				p4 = Runtime.getRuntime().exec("bzip2 "+path+"/annotations/agreements/babel_tagme/"+year+"_babel_tagme.aggreement &");
//				
//				p5 = Runtime.getRuntime().exec("bzip2 "+path+"/annotations/disagreements/ambiv_tagme/"+year+"_ambiv_tagme.disag &");
//				p6 = Runtime.getRuntime().exec("bzip2 "+path+"/annotations/disagreements/ambiv_babel/"+year+"_ambiv_babel.disag &");
//				p7 = Runtime.getRuntime().exec("bzip2 "+path+"/annotations/disagreements/babel_tagme/"+year+"_babel_tagme.disag &");
//				p8 = Runtime.getRuntime().exec("bzip2 "+path+"/annotations/disagreements/all/"+year+"_ALL.disag &");
//				//p.waitFor();
//				//p.destroy();
//			} catch (Exception e) {
//			}
//			
		
	}
	
	
	
	
	
	
	
	/**
	 *
	 * @throws IOException
	 * @throws CompressorException
	 */
	public static void generalStats() throws IOException{
			int i=0;
			int doc = 0;
			BufferedReader bffReaderAMBIVERSE = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb_ambiverse.mappings"),StandardCharsets.UTF_8));
			BufferedReader bffReaderBABELFY = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb_bfy.mappings"),StandardCharsets.UTF_8));
			BufferedReader bffReaderTAGME = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/iitb/mappings/iitb_tagme.mappings"),StandardCharsets.UTF_8));
			
			int numMentions = 0;
			TreeMap<String,Integer> docMap = new TreeMap<>();
			String line;
			//### =A=M=B=I=V=E=R=S=E ###//
			numMentions = 0;
			docMap = new TreeMap<>();
			line = "";
			while ((line = bffReaderAMBIVERSE.readLine()) != null) {
				String[] elements = line.split("\t");
				String docId = elements[0];
				Integer count = docMap.get(docId);
				if(count == null){
					doc++;
					docMap.put(docId,1);
					//System.out.println(docId);
				}else{
					docMap.put(docId,count+=1);
				}
			}
			Iterator<?> it = docMap.entrySet().iterator();
		    while (it.hasNext()) {
				Map.Entry<?, ?> pair = (Map.Entry<?, ?>)it.next();
		        String mFromMap = (String) pair.getKey();
				Integer numM = (Integer) pair.getValue();
				numMentions += numM;
		    }
			
		    logger_.info("Ambiverse *-*-*");
		    logger_.info("Num Docs : & "+docMap.keySet().size());
		    logger_.info("Num Ment : & "+numMentions);

			bffReaderAMBIVERSE.close();
			
			//### =B=A=B=E=L=F=Y= ### //
			numMentions = 0;
			docMap = new TreeMap<>();
			doc = 0;
			line=""; 
			while ((line = bffReaderBABELFY.readLine()) != null) {
				String[] elements = line.split("\t");
				String docId = elements[0];
				Integer count = docMap.get(docId);
				if(count == null){
					doc++;
					docMap.put(docId,1);	
				}else{
					docMap.put(docId,count+=1);
				}
				
			}
			it = docMap.entrySet().iterator();
		    while (it.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry<?, ?> pair = (Map.Entry<?, ?>)it.next();
		        String mFromMap = (String) pair.getKey();
				Integer numM = (Integer) pair.getValue();
				numMentions += numM;
		    }
			
		    logger_.info("Babelfy *-*-*");
		    logger_.info("Num Docs : & "+docMap.keySet().size());
		    logger_.info("Num Ment : & "+numMentions);
			
			bffReaderBABELFY.close();

			//### =T=A=G=M=E= ### //
			numMentions = 0;
			docMap = new TreeMap<>();
			line=""; 
			doc = 0;
			while ((line = bffReaderTAGME.readLine()) != null) {
				String[] elements = line.split("\t");
				String docId = elements[0];
				Integer count = docMap.get(docId);
				if(count == null){
					doc++;
					docMap.put(docId,1);					
				}else{
					docMap.put(docId,count+=1);
				}
				
			}
			it = docMap.entrySet().iterator();
		    while (it.hasNext()) {
				Map.Entry<?, ?> pair = (Map.Entry<?, ?>)it.next();
		        String mFromMap = (String) pair.getKey();
				Integer numM = (Integer) pair.getValue();
				numMentions += numM;
		    }
			
		    logger_.info("Tagme *-*-*");
			logger_.info("Num Docs : & "+docMap.keySet().size());
			logger_.info("Num Ment : & "+numMentions);
			
			bffReaderTAGME.close();
			
	}
}
