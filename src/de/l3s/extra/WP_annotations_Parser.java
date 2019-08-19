package de.l3s.extra;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 						GROUNDTHRUTH !!!
 * @author renato 
 * 
 * This is just a parser to get the list of annotations (Groundthruth) from
 * 
 *         ("./resource/WP.tsv")
 */

public class WP_annotations_Parser {
	LinkedList<GenericDocument> listOfDocuments = new LinkedList<>();

	/*
	 * -DOCSTART- (2wo_0.txt)
  	 * 2
	 * wo
	 * was
	 * an
	 * industrial
	 * metal
     * band
	 * formed
	 * by
	 * former
	 * Judas   B       Judas Priest    Judas_Priest
	 * Priest  I       Judas Priest    Judas_Priest
     * lead
	*/
	
	public WP_annotations_Parser() {
		super();
	}
	
	/**
	 * @return the listOfDocuments
	 */
	public LinkedList<GenericDocument> getListOfDocuments() {
		return listOfDocuments;
	}

	/**
	 * @param listOfDocuments
	 *            the listOfDocuments to set
	 */
	public void setListOfDocuments(LinkedList<GenericDocument> listOfDocuments) {
		this.listOfDocuments = listOfDocuments;
	}

	/**
	 * This method is supposed to parse the AIDA-YAGO2 conll and WP annotations and
	 * return a list of ConllDocument documents.
	 * 
	 * @param dataset "./resource/WP.tsv"
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public LinkedList<ConllDocument> parseDataset(String dataset) throws NumberFormatException, IOException{
		//String dataset = "./resource/WP.tsv";
		LinkedList<String> docsTexts = new LinkedList<String>();
		TObjectIntHashMap<String> sfCtrl = new TObjectIntHashMap<String>();
		BufferedReader bf = new BufferedReader(new FileReader(dataset));
		BufferedReader bf_AUX = new BufferedReader(new FileReader(dataset));
		int numDocs = 0;
		int numCharsAgg = 0;
		String textContent = "";
		int offset = 0;
		String line = null;
		String line_aux = bf_AUX.readLine();
		LinkedList<ConllDocument> ConllDocsList = new LinkedList<ConllDocument>();
		ConllDocument<WPAnnotation> CoNLLDoc = null;
		StringBuffer text = new StringBuffer();
		
		while ((line = bf.readLine())  != null) {
			if (line.contains("-DOCSTART-")) {
				CoNLLDoc = new ConllDocument<WPAnnotation>();
				CoNLLDoc.setTitle(line);
				numDocs++;
				line_aux = bf_AUX.readLine();
				while ( !line_aux.contains("-DOCSTART-") ){
					line_aux = bf_AUX.readLine();	
					line = bf.readLine();
					if(line_aux == null ){
						text.append(line);
						break;
					}else {
						Charset.forName("UTF-8").encode(line);
						String[] elem = line.split("\t");
						if ((elem.length) >= 4) {
							text.append(elem[0]+" ");
						}else{
							text.append(line+" ");
						}
						}
				}
				numCharsAgg = 0;

			}
			textContent = text.toString().trim();
			textContent = textContent.replaceAll("\\s+", " ");
			textContent = textContent.replaceAll(" \\.", ".");
			textContent = textContent.replaceAll(" \\,", ",");
			textContent = textContent.replaceAll(" \\'", "'");
			textContent = textContent.replaceAll(" \\)", ")");
			textContent = textContent.replaceAll("\\( ", "(");
			textContent = textContent.replaceAll(" \\/", "\\/");
			textContent = textContent.replaceAll("\\/ ", "\\/");
			textContent = textContent.replaceAll(" \\-", "\\-");
			textContent = textContent.replaceAll("\\- ", "\\-");
			docsTexts.add(textContent);
			text = new StringBuffer();
			textContent = "";
			
		}
		
		bf.close();
		bf_AUX.close();

		bf = new BufferedReader(new FileReader(dataset));
		bf_AUX = new BufferedReader(new FileReader(dataset));
		
		numDocs = 0;
		numCharsAgg = 0;
		textContent = "";
		offset = 0;
		line = null;
		line_aux = bf_AUX.readLine();
		CoNLLDoc = null;
		text = new StringBuffer();
		
		String currentText ="";
		
		while ((line = bf.readLine())  != null) {
			if (line.contains("-DOCSTART-")) {
				WPAnnotation annotation = null;
				sfCtrl = new TObjectIntHashMap<String>();
				numCharsAgg = 0;
				CoNLLDoc = new ConllDocument<WPAnnotation>();
				CoNLLDoc.setTitle(line);
				currentText = docsTexts.get(numDocs);
				numDocs++;
				line_aux = bf_AUX.readLine();
				while ( !line_aux.contains("-DOCSTART-") ){
					line_aux = bf_AUX.readLine();	
					line = bf.readLine();
					if(line_aux == null ){
						text.append(line);
						break;
					}else {
						Charset.forName("UTF-8").encode(line);
						String[] elem = line.split("\t");
						if ((elem.length) >= 4) {
							text.append(elem[0]+" ");
							
							String mention = elem[2].trim();
							mention = mention.replaceAll("\\s+", " ").replaceAll(" \\.", ".").replaceAll(" \\,", ",").replaceAll(" \\'", "'");
							String entity = elem[3].replace("\\u0028","(").
									replace("\\u0029",")").replace("\\u0027","'").replace("\\u00fc","??").replace("\\u002c",",").
									replace("\\u0163","??").replace("\\u00e1s","??").replace("\\u0159","??").replace("\\u00e9","??").
									replace("\\u00ed","??").replace("\\u00e1","??").replace("\\u2013","-").replace("\\u0107","??").
									replace("\\u002e",".").replace("\\u00f3","??").replace("\\u002d","-").replace("\\u00e1","??").
									replace("\\u0160","??").replace("\\u0105","??").replace("\\u00eb","??").replace("\\u017d","??").
									replace("\\u00e7","??").replace("\\u00f8","??").replace("\\u0161","??").replace("\\u0107","??").
									replace("\\u00f6","??").replace("\\u010c","??").replace("\\u00fd","??").replace("\\u00d6","??").
									replace("\\u00c0","??").replace("\\u0026","&").replace("\\u00df","??").replace("\\u00ea","??").
									replace("\\u017","??").replace("\\u011b","??").replace("\\u00f6","??").replace("\\u00e3","??").
									replace("\\u0103","??").replace("\\u00c1","??").replace("\\u002f","/").replace("\\u00e4","??").
									replace("\\u00c5","??").replace("\\u0142","??").replace("\\u0117","??").replace("\\u00ff","??").
									replace("\\u00f1","??").replace("\\u015f","??").replace("\\u015e","??").replace("\\u0131","??").
									replace("\\u0131k","??").replace("\\u0144","??").replace("\\u0119","??").replace("\\u00c9","??").
									replace("\\u0111","??").replace("\\u00e2","??").replace("\\u010d","??").replace("\\u015a","??").
									replace("\\u0141","??").replace("\\u00e8","??").replace("\\u00c9","??").replace("\\u00e5","??").
									replace("\\u014d","??").replace("\\u00e6","??").replace("\\u00d3","??").replace("\\u00da","??").
									replace("\\u0151","??").replace("\\u0148","??").replace("\\u00fa","??").replace("\\u00ee","??").
									replace("\\u015b","??").replace("\\u00c7","??").replace("\\u00f4","??").replace("\\u013d","??").
									replace("\\u013e","??").replace("\\u011f","??").replace("\\u00e0","??").replace("\\u00dc","??").
									replace("\\u0021","!");
							entity = entity.replace("_"," ");
							
							
							
							
							if(elem[1].trim().equalsIgnoreCase("I")){ continue; }
							
							Integer position = sfCtrl.get(mention);
							if(position != 0){
								offset = currentText.indexOf(mention, numCharsAgg);
								sfCtrl.put(mention,offset);
							}else{
								offset = currentText.indexOf(mention, numCharsAgg);
								sfCtrl.put(mention,offset);
							}
							
							textContent = text.toString().trim();
							textContent = textContent.replaceAll("\\s+", " ");
							textContent = textContent.replaceAll(" \\.", ".");
							textContent = textContent.replaceAll(" \\,", ",");
							textContent = textContent.replaceAll(" \\'", "'");
							textContent = textContent.replaceAll(" \\)", ")");
							textContent = textContent.replaceAll("\\( ", "(");
							textContent = textContent.replaceAll(" \\/", "\\/");
							textContent = textContent.replaceAll("\\/ ", "\\/");
							textContent = textContent.replaceAll(" \\-", "\\-");
							textContent = textContent.replaceAll("\\- ", "\\-");
							numCharsAgg = textContent.length()-1;
							
						//if (entity.equalsIgnoreCase("--NME--")) {
						//	annotation = new WPAnnotation(mention,Integer.toString(offset),entity,"--NME--");
						//	CoNLLDoc.addAnnotation(annotation);

						///} else {
							//String wikiLink = elem[4].trim();
							//String entityId = elem[5].trim();
							annotation = new WPAnnotation(mention,Integer.toString(offset),entity);
							CoNLLDoc.addAnnotation(annotation);
						//	}
						
						}else{
							text.append(line+" ");
							textContent = text.toString().trim();
							textContent = textContent.replaceAll("\\s+", " ");
							textContent = textContent.replaceAll(" \\.", ".");
							textContent = textContent.replaceAll(" \\,", ",");
							textContent = textContent.replaceAll(" \\'", "'");
							textContent = textContent.replaceAll(" \\)", ")");
							textContent = textContent.replaceAll("\\( ", "(");
							textContent = textContent.replaceAll(" \\/", "\\/");
							textContent = textContent.replaceAll("\\/ ", "\\/");
							textContent = textContent.replaceAll(" \\-", "\\-");
							textContent = textContent.replaceAll("\\- ", "\\-");
							numCharsAgg = textContent.length()-1;
							
						}
						}
				}
				numCharsAgg = 0;

			}
			textContent = text.toString().trim();
			textContent = textContent.replaceAll("\\s+", " ");
			textContent = textContent.replaceAll(" \\.", ".");
			textContent = textContent.replaceAll(" \\,", ",");
			textContent = textContent.replaceAll(" \\'", "'");
			textContent = textContent.replaceAll(" \\)", ")");
			textContent = textContent.replaceAll("\\( ", "(");
			textContent = textContent.replaceAll(" \\/", "\\/");
			textContent = textContent.replaceAll("\\/ ", "\\/");
			textContent = textContent.replaceAll(" \\-", "\\-");
			textContent = textContent.replaceAll("\\- ", "\\-");
			
			CoNLLDoc.setTxtContent(textContent);
			ConllDocsList.add(CoNLLDoc);
			text = new StringBuffer();
			textContent = "";
			
		}
		bf.close();
		bf_AUX.close();
		return ConllDocsList;
	}
	
		/**
		 *
		 * @param tokens
		 * @return
		 */
	    public String detokenize(List<String> tokens) {
	        //Define list of punctuation characters that should NOT have spaces before or after 
	        List<String> noSpaceBefore = new LinkedList<String>(Arrays.asList(",", ".",";", ":", ")", "}", "]","-","\'","/"));
	        List<String> noSpaceAfter = new LinkedList<String>(Arrays.asList("(", "[","{", "\"","","-","\'","/"));

	        StringBuilder sentence = new StringBuilder();

	        tokens.add(0, "");  //Add an empty token at the beginning because loop checks as position-1 and "" is in noSpaceAfter
	        for (int i = 1; i < tokens.size(); i++) {
	            if (noSpaceBefore.contains(tokens.get(i)) || noSpaceAfter.contains(tokens.get(i - 1))) {
	                sentence.append(tokens.get(i));
	            } else {
	                sentence.append(" " + tokens.get(i));
	            }

	            // Assumption that opening double quotes are always followed by matching closing double quotes
	            // This block switches the " to the other set after each occurrence
	            // ie The first double quotes should have no space after, then the 2nd double quotes should have no space before
	            if ("\"".equals(tokens.get(i - 1))) {
	                if (noSpaceAfter.contains("\"")) {
	                    noSpaceAfter.remove("\"");
	                    noSpaceBefore.add("\"");
	                } else {
	                    noSpaceAfter.add("\"");
	                    noSpaceBefore.remove("\"");
	                }
	            }
	        }
	        return sentence.toString();
	    }		
	    
	    
	    
	    
	    
	    
	    
	    
	    
    //In this experiment I am only parsing the WP corpus and returning a list of tokens. I mean, I am returning a list of texts
	    public LinkedList<GenericDocument> parseWPDataset(String dataset) throws NumberFormatException, IOException{
		//String dataset = "./resource/WP.tsv";
	    	LinkedList<String> documentTokens = new LinkedList<String>();
	    	LinkedList<LinkedList<String>> docsList = new LinkedList<LinkedList<String>>();
	    	
	    	LinkedList<String> docsTexts = new LinkedList<String>();
			TObjectIntHashMap<String> sfCtrl = new TObjectIntHashMap<String>();
			BufferedReader bf = new BufferedReader(new FileReader(dataset));
			BufferedReader bf_AUX = new BufferedReader(new FileReader(dataset));
			int numDocs = 0;
			int numCharsAgg = 0;
			String textContent = "";
			int offset = 0;
			String line = null;
			String line_aux = bf_AUX.readLine();
			LinkedList<GenericDocument> ConllDocsList = new LinkedList<GenericDocument>();
			GenericDocument<WPAnnotation> CoNLLDoc = null;
			StringBuffer text = new StringBuffer();
			documentTokens = new LinkedList<String>();

			while ((line = bf.readLine())  != null) {
				if (line.contains("-DOCSTART-")) {
					CoNLLDoc = new GenericDocument<WPAnnotation>();
					CoNLLDoc.setTitle(line);
					numDocs++;
					line_aux = bf_AUX.readLine();
					while ( !line_aux.contains("-DOCSTART-") ){
						line_aux = bf_AUX.readLine();	
						line = bf.readLine();
						if(line_aux == null ){
							text.append(line);
							documentTokens.add(line);
							break;
						}else {
							Charset.forName("UTF-8").encode(line);
							String[] elem = line.split("\t");
							if ((elem.length) >= 4) {
								text.append(elem[0]+" ");
								documentTokens.add(elem[0]);
							}else{
								text.append(line+" ");
								documentTokens.add(line);
							}
							}
					}
					numCharsAgg = 0;

				}
				textContent = text.toString().trim();
				textContent = textContent.replaceAll("\\s+", " ");
				textContent = textContent.replaceAll(" \\.", ".");
				textContent = textContent.replaceAll(" \\,", ",");
				textContent = textContent.replaceAll(" \\'", "'");
				
				docsTexts.add(textContent);				
				text = new StringBuffer();
				textContent = "";				
				docsList.add(documentTokens);
				documentTokens = new LinkedList<String>();

				
				
			}
			
			bf.close();
			bf_AUX.close();

			
			
			//stop here 
			bf = new BufferedReader(new FileReader(dataset));
			bf_AUX = new BufferedReader(new FileReader(dataset));
			
			numDocs = 0;
			numCharsAgg = 0;
			textContent = "";
			offset = 0;
			line = null;
			line_aux = bf_AUX.readLine();
			CoNLLDoc = null;
			text = new StringBuffer();
			
			String currentText ="";
			
			while ((line = bf.readLine())  != null) {
				if (line.contains("-DOCSTART-")) {
					WPAnnotation annotation = null;
					sfCtrl = new TObjectIntHashMap<String>();
					numCharsAgg = 0;
					CoNLLDoc = new GenericDocument<WPAnnotation>();
					CoNLLDoc.setTitle(line);
					currentText = docsTexts.get(numDocs);
					
					//
					String fulltext = detokenize(docsList.get(numDocs));
					//
					numDocs++;
					line_aux = bf_AUX.readLine();
					while ( !line_aux.contains("-DOCSTART-") ){
						line_aux = bf_AUX.readLine();	
						line = bf.readLine();
						if(line_aux == null ){
							text.append(line);
							break;
						}else {
							Charset.forName("UTF-8").encode(line);
							String[] elem = line.split("\t");
							if ((elem.length) >= 4) {
								text.append(elem[0]+" ");
								
								String mention = elem[2].trim();
								mention = mention.replaceAll("\\s+", " ").replaceAll(" \\.", ".").replaceAll(" \\,", ",").replaceAll(" \\'", "'");
								String entity = elem[3].replace("\\u0028","(").
										replace("\\u0029",")").replace("\\u0027","'").replace("\\u00fc","??").replace("\\u002c",",").
										replace("\\u0163","??").replace("\\u00e1s","??").replace("\\u0159","??").replace("\\u00e9","??").
										replace("\\u00ed","??").replace("\\u00e1","??").replace("\\u2013","-").replace("\\u0107","??").
										replace("\\u002e",".").replace("\\u00f3","??").replace("\\u002d","-").replace("\\u00e1","??").
										replace("\\u0160","??").replace("\\u0105","??").replace("\\u00eb","??").replace("\\u017d","??").
										replace("\\u00e7","??").replace("\\u00f8","??").replace("\\u0161","??").replace("\\u0107","??").
										replace("\\u00f6","??").replace("\\u010c","??").replace("\\u00fd","??").replace("\\u00d6","??").
										replace("\\u00c0","??").replace("\\u0026","&").replace("\\u00df","??").replace("\\u00ea","??").
										replace("\\u017","??").replace("\\u011b","??").replace("\\u00f6","??").replace("\\u00e3","??").
										replace("\\u0103","??").replace("\\u00c1","??").replace("\\u002f","/").replace("\\u00e4","??").
										replace("\\u00c5","??").replace("\\u0142","??").replace("\\u0117","??").replace("\\u00ff","??").
										replace("\\u00f1","??").replace("\\u015f","??").replace("\\u015e","??").replace("\\u0131","??").
										replace("\\u0131k","??").replace("\\u0144","??").replace("\\u0119","??").replace("\\u00c9","??").
										replace("\\u0111","??").replace("\\u00e2","??").replace("\\u010d","??").replace("\\u015a","??").
										replace("\\u0141","??").replace("\\u00e8","??").replace("\\u00c9","??").replace("\\u00e5","??").
										replace("\\u014d","??").replace("\\u00e6","??").replace("\\u00d3","??").replace("\\u00da","??").
										replace("\\u0151","??").replace("\\u0148","??").replace("\\u00fa","??").replace("\\u00ee","??").
										replace("\\u015b","??").replace("\\u00c7","??").replace("\\u00f4","??").replace("\\u013d","??").
										replace("\\u013e","??").replace("\\u011f","??").replace("\\u00e0","??").replace("\\u00dc","??").
										replace("\\u0021","!");
								entity = entity.replace("_"," ");
								
								if(elem[1].trim().equalsIgnoreCase("I")){ continue; }
								
								Integer position = sfCtrl.get(mention);
								if(position != 0){
									//offset = currentText.indexOf(mention, numCharsAgg);
									offset = fulltext.indexOf(mention);
									sfCtrl.put(mention,offset);
								}else{
									
									//offset = currentText.indexOf(mention, numCharsAgg);
									offset = fulltext.indexOf(mention);
									sfCtrl.put(mention,offset);
								}
								
								textContent = text.toString().trim();
								textContent = textContent.replaceAll("\\s+", " ");
								textContent = textContent.replaceAll(" \\.", ".");
								textContent = textContent.replaceAll(" \\,", ",");
								textContent = textContent.replaceAll(" \\'", "'");
								numCharsAgg = textContent.length()-1;
								
							//if (entity.equalsIgnoreCase("--NME--")) {
							//	annotation = new WPAnnotation(mention,Integer.toString(offset),entity,"--NME--");
							//	CoNLLDoc.addAnnotation(annotation);

							///} else {
								//String wikiLink = elem[4].trim();
								//String entityId = elem[5].trim();
								annotation = new WPAnnotation(mention,Integer.toString(offset),entity);
								CoNLLDoc.addAnnotation(annotation);
							//	}
							
							}else{
								text.append(line+" ");
								textContent = text.toString().trim();
								textContent = textContent.replaceAll("\\s+", " ");
								textContent = textContent.replaceAll(" \\.", ".");
								textContent = textContent.replaceAll(" \\,", ",");
								textContent = textContent.replaceAll(" \\'", "'");
								numCharsAgg = textContent.length()-1;
								
							}
							}
					}
					numCharsAgg = 0;

				}
				textContent = text.toString().trim();
				textContent = textContent.replaceAll("\\s+", " ");
				textContent = textContent.replaceAll(" \\.", ".");
				textContent = textContent.replaceAll(" \\,", ",");
				textContent = textContent.replaceAll(" \\'", "'");
				
				CoNLLDoc.setTxtContent(textContent);
				ConllDocsList.add(CoNLLDoc);
				text = new StringBuffer();
				textContent = "";
				
			}
			bf.close();
			bf_AUX.close();
			return ConllDocsList;
		}
	    
	    
	    
	    
	    
	    

	/**
	 * 
	 * @param args
	 * @throws NumberFormatException
	 * @throws IOException
	 */
//	public static void main(String[] args) throws NumberFormatException, IOException {
//		WP_annotations_Parser wparser = new WP_annotations_Parser();		
//		LinkedList<ConllDocument> WPDataSet = wparser.parseDataset("./resources/WP.tsv");
//		
//		System.out.println(WPDataSet.size());
//		
//	}
	
	
}
