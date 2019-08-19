package de.l3s.extra;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;



/**
 * 						GROUNDTHRUTH !!!
 * @author renato 
 * 
 * This is just a parser to get the list of annotations (Groundthruth) from
 * 
 *         ("/home/renato/Downloads/CONLL/aida-yago2-dataset/aida-yago2-dataset/AIDA-YAGO2-annotations.tsv")
 */
public class AIDA_YAGO2_annotations_Parser {
	LinkedList<GenericDocument> listOfDocuments = new LinkedList<>();
	/*
	 * example
	 *
	 * -DOCSTART- (2 Rare) 
	 * 1 Jimi_Hendrix http://en.wikipedia.org/wiki/Jimi_Hendrix 16095 /m/01vsy3q
	 * 10 London http://en.wikipedia.org/wiki/London 17867 /m/04jpl
	 * 21 United_States http://en.wikipedia.org/wiki/United_States 3434750 /m/09c7w0
	 * 24 Jimi_Hendrix http://en.wikipedia.org/wiki/Jimi_Hendrix 16095 /m/01vsy3q
	 * 48 --NME--
	 * 62 --NME--
	 */

	public AIDA_YAGO2_annotations_Parser() {
		super();
	}

	
	/*
	
	/**
	 * ("/home/renato/Downloads/CONLL/aida-yago2-dataset/aida-yago2-dataset/AIDA-YAGO2-annotations.tsv")
	 * "/home/renato/Documents/LATEX/Temporally aware Named Entity Linking/CONLL/aida-yago2-dataset/aida-yago2-dataset/AIDA-YAGO2-annotations.tsv");" 
	 * @param inputFile
	 * @return 
	 * @throws NumberFormatException
	 * @throws IOException
	 */
/*	public LinkedList<KORE50Document> parse(String inputFile) throws NumberFormatException, IOException {
		LinkedList<KORE50Document> List = new LinkedList<>();
		BufferedReader bf = new BufferedReader(new FileReader(new File(inputFile)));
		String line = null;

		while ((line = bf.readLine()) != null) {
			if (line.contains("-DOCSTART-")) {
				// new documentl
				KORE50Document CD = new KORE50Document();
				while (!(line = bf.readLine()).isEmpty()) {
					//Charset.forName("UTF-8").encode(line);
					String[] elem = line.split("\t");
					if ((elem.length) >= 4) {
						String entity = elem[1];
						if (entity.equalsIgnoreCase("--NME--")) {
								String[] entities = line.split("\t");
								ConllAnnotation ca = new ConllAnnotation(Integer.parseInt(entities[0]), entities[1],entities[1]);
								CD.addAnnotation(ca);
							
						} else {
								String[] entities = line.split("\t");
								String ent = entities[1].replace("\\u0028","(").
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
										replace("\\u0021","!").replace("_"," ");
									 
								
								String wiki = entities[2];
								//System.out.println(entities[1]);
								//entities[1] = entities[1].trim().replaceAll("\u0028","(");
								//entities[1] = entities[1].replace("\u0029",")");
								ConllAnnotation ca = new ConllAnnotation(Integer.parseInt(entities[0]), ent, wiki);
								//System.out.println(ent );
								CD.addAnnotation(ca);
						}
					} else {
						continue;
					}
				}
				List.add(CD);
			}
		}
		bf.close();
		return List;
	}
	*/

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
	 * 
	 * @param dataset AIDA-YAGO2-dataset.tsv
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public LinkedList<GenericDocument> parseDataset_ORIG(String dataset) throws NumberFormatException, IOException{
		//String dataset = "/home/renato/Documents/LATEX/Temporally_aware_Named_Entity_Linking/CONLL/AIDA-YAGO2-dataset.tsv";
//		BufferedReader bf = new BufferedReader(new FileReader(dataset));
//		BufferedReader bf_AUX = new BufferedReader(new FileReader(dataset));
//		
		BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(dataset),StandardCharsets.UTF_8));
		BufferedReader bf_AUX = new BufferedReader(new InputStreamReader(new FileInputStream(dataset),StandardCharsets.UTF_8));
		
		String line = null;
		String line_aux = bf_AUX.readLine();
		LinkedList<GenericDocument> ConllDocsList = new LinkedList<GenericDocument>();
		GenericDocument CoNLLDoc = null;
		StringBuffer text = new StringBuffer();
		while ((line = bf.readLine())  != null) {
			if (line.contains("-DOCSTART-")) {
				AIDAYAGOAnnotation annotation = null;
				CoNLLDoc = new GenericDocument();
				CoNLLDoc.setTitle(line);
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
							//System.out.println(mention);
							if(elem[1].trim().equalsIgnoreCase("I")){ continue;}
							
						if (entity.equalsIgnoreCase("--NME--")) {
							annotation = new AIDAYAGOAnnotation(mention,entity,"--NME--","--NME--","");
							CoNLLDoc.addAnnotation(annotation);

						} else {
							String wikiLink = elem[4].trim();
							String entityId = elem[5].trim();
							annotation = new AIDAYAGOAnnotation(mention,entity,entityId,wikiLink,"");
							CoNLLDoc.addAnnotation(annotation);

							}
						
						}else{
							text.append(line+" ");
						}
						}
				}
			}
			String textContent = text.toString().trim();
			textContent = textContent.replaceAll("\\s+", " ");
			textContent = textContent.replaceAll(" \\.", ".");
			textContent = textContent.replaceAll(" \\,", ",");
			textContent = textContent.replaceAll(" \\'", "'");
			
			CoNLLDoc.setTxtContent(textContent);
			ConllDocsList.add(CoNLLDoc);
			text = new StringBuffer();
			
		}
		bf.close();
		bf_AUX.close();
		return ConllDocsList;
	}
	
	
	
	/**
	 * This method is supposed to parse the AIDA-YAGO2 conll annotations and
	 * return a list of ConllDocument documents.
	 * 
	 * @param dataset "/home/renato/Documents/LATEX/Temporally_aware_Named_Entity_Linking/CONLL/AIDA-YAGO2-dataset.tsv"
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LinkedList<GenericDocument> parseDataset(String dataset) throws NumberFormatException, IOException{
		//String dataset = "/home/renato/Documents/LATEX/Temporally_aware_Named_Entity_Linking/CONLL/AIDA-YAGO2-dataset.tsv";
		LinkedList<String> docsTexts = new LinkedList<String>();
		TObjectIntHashMap<String> sfCtrl = new TObjectIntHashMap<String>();
		
		BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(dataset),StandardCharsets.UTF_8));
		BufferedReader bf_AUX = new BufferedReader(new InputStreamReader(new FileInputStream(dataset),StandardCharsets.UTF_8));
		int numDocs = 0;
		int numCharsAgg = 0;
		String textContent = "";
		int offset = 0;
		String line = null;
		String line_aux = bf_AUX.readLine();
		LinkedList<GenericDocument> ConllDocsList = new LinkedList<GenericDocument>();
		GenericDocument CoNLLDoc = null;
		StringBuffer text = new StringBuffer();
		
		while ((line = bf.readLine())  != null) {
			if (line.contains("-DOCSTART-")) {
				CoNLLDoc = new GenericDocument();
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
				AIDAYAGOAnnotation annotation = null;
				sfCtrl = new TObjectIntHashMap<String>();
				numCharsAgg = 0;
				CoNLLDoc = new GenericDocument();
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
							numCharsAgg = textContent.length()-1;
							
						if (entity.equalsIgnoreCase("--NME--")) {
							annotation = new AIDAYAGOAnnotation(mention,Integer.toString(offset),entity,"--NME--","--NME--","");
							CoNLLDoc.addAnnotation(annotation);

						} else {
							String wikiLink = elem[4].trim();
							String entityId = elem[5].trim();
							annotation = new AIDAYAGOAnnotation(mention,Integer.toString(offset),entity,entityId,wikiLink,"");
							CoNLLDoc.addAnnotation(annotation);
							}
						
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
	public static void main_old(String[] args) throws NumberFormatException, IOException {
		String ConLLfiles = "./resource/listCONLL";
		String groundthruthConll = "./resource/groundthruthConll";
		PrintWriter pmapp = new PrintWriter(new File("./resource/conllMapping"));
		BufferedReader bf = new BufferedReader(new FileReader(ConLLfiles));
		BufferedReader bf2 = new BufferedReader(new FileReader(groundthruthConll));
		String line;
		String line2;
		while((line = bf.readLine()) != null){
			line2 = bf2.readLine();
			pmapp.println(line + " \t "+ line2);
		}
		
		pmapp.flush();
		pmapp.close();
		bf.close();
		bf2.close();
		
		
		//AIDA_YAGO2_annotations_Parser p = new AIDA_YAGO2_annotations_Parser();
		//HashMap<String,KORE50Document> cd = p.parseGroundthruth("/home/renato/Documents/LATEX/Temporally_aware_Named_Entity_Linking/CONLL/aida-yago2-dataset/aida-yago2-dataset/AIDA-YAGO2-annotations.tsv");
		//System.out.println(cd.size());
		//LinkedList<KORE50Document> list = p.parse("/home/renato/Documents/LATEX/Temporally aware Named Entity Linking/CONLL/aida-yago2-dataset/aida-yago2-dataset/AIDA-YAGO2-annotations.tsv");
		//for (int i=0; i<list.size(); i++){
		//	//System.out.println(list.get(i).listOfConllAnnotation.get(0).getEntity());
		//}
		
	}
}