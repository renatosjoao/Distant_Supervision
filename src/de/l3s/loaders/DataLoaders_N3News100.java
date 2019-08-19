package de.l3s.loaders;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aksw.gerbil.io.nif.NIFParser;
import org.aksw.gerbil.io.nif.impl.TurtleNIFParser;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.apache.commons.compress.compressors.CompressorException;

import gnu.trove.map.hash.TObjectIntHashMap;

public class DataLoaders_N3News100 extends DataLoaders {
			// 	GERMAN NEWS
	
	public static void main(String... args) throws NumberFormatException, IOException {
		dumpGT();
		dumpTextContent();
		dumpNumRecognizedMentionsFromN3News100();
		dumpWordCountFromN3News100();
		dumpDocumentFrequencyFromN3News100();
	}
	


	public DataLoaders_N3News100() throws CompressorException, IOException {
		DocFrequencyMap = new TObjectIntHashMap<String>();
		DocWordCountMap = new TObjectIntHashMap<String>();
		MentionEntityCountMap = new TObjectIntHashMap<String>();
		NumRecogMentionMap = new TObjectIntHashMap<String>();
		GT_MAP = new TreeMap<String, String>();
		GT_MAP_train = new TreeMap<String, String>();
		GT_MAP_test = new TreeMap<String, String>();
		
		ambiverseMap = new TreeMap<String, String>();
		babelMap = new TreeMap<String, String>();
		tagmeMap = new TreeMap<String, String>();
		spotlightMap = new TreeMap<String, String>();
		
		ambiverseMap_train = new TreeMap<String, String>();
		babelMap_train = new TreeMap<String, String>();
		tagmeMap_train = new TreeMap<String, String>();
		spotlightMap_train = new TreeMap<String, String>();
		
		ambiverseMap_test = new TreeMap<String, String>();
		babelMap_test = new TreeMap<String, String>();
		tagmeMap_test = new TreeMap<String, String>();
		spotlightMap_test = new TreeMap<String, String>();
		
		docsContent = new TreeMap<String, String>();
		
		dumpTextContent();
		
		loadDocFrequencyMap();
		loadWordCount();
 		loadMentionEntityCountMap();
		loadNumMentionsRecognized();
		loadGT();
		loadMappings();
		loadDocsContent();
	}


	
	private void loadDocsContent() throws IOException {
		String nifPath = "/home/joao/datasets/N3News100/News-100.ttl";
		StringBuffer nifBuffer = new StringBuffer();
		BufferedReader bffReader = new BufferedReader(	new InputStreamReader(new FileInputStream(nifPath), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			line = line.replaceAll("<E2><80><99>", "\'");
			line = line.replaceAll("<E2><80><9C>", "\"");
			line = line.replaceAll("<E2><80><9D>", "\"");
			nifBuffer.append(line);
			nifBuffer.append("\n");
		}
		bffReader.close();
		String nifString = nifBuffer.toString();
		NIFParser parser = new TurtleNIFParser();
		List<Document> listDocs = parser.parseNIF(nifString);
		for (Document d : listDocs) {
			String docName = d.getDocumentURI();
			docName = docName.replaceAll("\\/","_");
//			docName = "\'"+docName+  "\'.txt";
			String docContent = d.getText();
    		docsContent.put(docName, docContent);
		}
		System.out.println("# documents in the text dir/ :" + docsContent.keySet().size());
	}



	public static DataLoaders_N3News100 getInstance() throws CompressorException, IOException {
		if(instance == null) {
			 synchronized(DataLoaders_N3News100.class) {
				 instance = new DataLoaders_N3News100();
			 }
	    }
		return (DataLoaders_N3News100) instance;
	}
	
	
	private void loadGT() throws IOException {
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(	new FileInputStream("/home/joao/datasets/N3News100/N3News100_GT.tsv"), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String mention = elems[1].toLowerCase();
//		    mention = mention.replaceAll("\'", "");
//		    mention = mention.replaceAll("\"", "");
			String offset = elems[2];
			String key = docId + "\t" + mention + "\t" + offset;
			String value = elems[3].replace("_", " ").toLowerCase();
			if (!value.contains("*null*")) {
				GT_MAP.put(key, value);
			}
		}
		bffReader.close();
		System.out.println("GT :" + GT_MAP.keySet().size());
		
	}
	private static void loadNumMentionsRecognized() throws CompressorException, IOException {
		BufferedReader bffReader = new BufferedReader(	new InputStreamReader(new FileInputStream("/home/joao/datasets/N3News100/num_recognized_mentions.tsv"),StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String count = elems[1];
			NumRecogMentionMap.put(docId, Integer.parseInt(count));
		}
		bffReader.close();
	}

	private static void loadWordCount() throws CompressorException, IOException {
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/N3News100/doc_word_count.tsv"), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			String count = elems[1];
			DocWordCountMap.put(docId, Integer.parseInt(count));
		}
		bffReader.close();
	}
	private static void loadDocFrequencyMap() throws CompressorException, IOException {
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/N3News100/document_frequency.tsv"), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			String[] elems = line.split("\t");
			String mention = elems[0].toLowerCase();
			String freq = elems[1];
			Integer df = Integer.parseInt(freq);
			DocFrequencyMap.put(mention, df);
		}
		bffReader.close();
	}
	private static void loadMappings() throws IOException {
		BufferedReader bffReaderAmbiverse = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/N3News100/mappings/N3News100.ambiverse.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderBabelfy = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/N3News100/mappings/N3News100.babelfy.mappings"),StandardCharsets.UTF_8));
		BufferedReader bffReaderTagme = new BufferedReader(new InputStreamReader(new FileInputStream("/home/joao/datasets/N3News100/mappings/N3News100.tagme.mappings"),StandardCharsets.UTF_8));

		String line = "";

		while ((line = bffReaderAmbiverse.readLine()) != null) {
			String[] elems = line.split("\t");
			if (elems.length >= 4) {
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
				String offset = elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_", " ").toLowerCase();
				if (!entity.equalsIgnoreCase("null")) {
					entity = entity.replaceAll("_", " ").toLowerCase();
					ambiverseMap.put(docId + "\t" + mention + "\t" + offset, entity);
				}
			}
		}
		System.out.println("TOTAL Amb annotations :" + ambiverseMap.keySet().size());

		line = "";
		while ((line = bffReaderBabelfy.readLine()) != null) {
			String[] elems = line.split("\t");
			if (elems.length >= 4) {
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
				String offset = elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_", " ").toLowerCase();
				if (!entity.equalsIgnoreCase("null")) {
					entity = entity.replaceAll("_", " ").toLowerCase();
					babelMap.put(docId + "\t" + mention + "\t" + offset, entity);
				}
			}
		}
		System.out.println("TOTAL Bab annotations :" + babelMap.keySet().size());

		line = "";
		while ((line = bffReaderTagme.readLine()) != null) {
			String[] elems = line.split("\t");
			if (elems.length >= 4) {
				String docId = elems[0].toLowerCase();
				docId = docId.replaceAll("\'", "");
				docId = docId.replaceAll("\"", "");
				String mention = elems[1].toLowerCase();
				docId = docId.replace("http://query.nytimes.com/gst/fullpage.html?res=", "");
				String offset = elems[2];
				String entity = elems[3].toLowerCase();
				entity = entity.replaceAll("_", " ").toLowerCase();
				if (!entity.equalsIgnoreCase("null")) {
					entity = entity.replaceAll("_", " ").toLowerCase();
					tagmeMap.put(docId + "\t" + mention + "\t" + offset, entity);
				}
			}
		}
		System.out.println("TOTAL Tag annotations :" + tagmeMap.keySet().size());
		bffReaderBabelfy.close();
		bffReaderTagme.close();
		bffReaderAmbiverse.close();
	}
	
	private static void dumpDocumentFrequencyFromN3News100() throws IOException {
		HashSet<String> mentionsSET = new HashSet<String>();
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/N3News100/document_frequency.tsv"), StandardCharsets.UTF_8);
		TreeMap<String, String> docsContentMap = new TreeMap<String, String>();
		String nifPath = "/home/joao/datasets/N3News100/News-100.ttl";
		StringBuffer nifBuffer = new StringBuffer();
		BufferedReader bffReader = new BufferedReader(	new InputStreamReader(new FileInputStream(nifPath), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			line = line.replaceAll("<E2><80><99>", "\'");
			line = line.replaceAll("<E2><80><9C>", "\"");
			line = line.replaceAll("<E2><80><9D>", "\"");
			nifBuffer.append(line);
			nifBuffer.append("\n");
		}
		bffReader.close();
		String nifString = nifBuffer.toString();
		NIFParser parser = new TurtleNIFParser();
		List<Document> listDocs = parser.parseNIF(nifString);
		for (Document d : listDocs) {
			String docName = d.getDocumentURI();
			docName = docName.replaceAll("\\/","_");
//			docName = "\'"+docName+  "\'.txt";
			String docContent = d.getText();
			docsContentMap.put(docName,docContent);
      
		}
		BufferedReader bff = new BufferedReader(new InputStreamReader(	new FileInputStream("/home/joao/datasets/N3News100/N3News100_GT.tsv"), StandardCharsets.UTF_8));
		line = "";
		while ((line = bff.readLine()) != null) {
			String[] elems = line.split("\t");
			String mention = elems[1].trim().toLowerCase();
			mentionsSET.add(mention);
		}
		bff.close();

		for (String mention : mentionsSET) {
			int df = 0;
			Iterator<?> it = docsContentMap.entrySet().iterator();
			while (it.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry pair = (Map.Entry) it.next();
				String articlecontent = (String) pair.getValue();
				if (articlecontent.toLowerCase().contains(mention)) {
					df += 1;
				}
			}
			pAnn.write(mention + "\t" + df + "\n");
		}
		pAnn.flush();
		pAnn.close();
		System.out.println("...Finished dumping the Document Frequency Count Successfully.");
	}




	private static void dumpWordCountFromN3News100() throws IOException {
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/N3News100/doc_word_count.tsv"), StandardCharsets.UTF_8);

		String nifPath = "/home/joao/datasets/N3News100/News-100.ttl";
		StringBuffer nifBuffer = new StringBuffer();
		BufferedReader bffReader = new BufferedReader(	new InputStreamReader(new FileInputStream(nifPath), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			line = line.replaceAll("<E2><80><99>", "\'");
			line = line.replaceAll("<E2><80><9C>", "\"");
			line = line.replaceAll("<E2><80><9D>", "\"");
			nifBuffer.append(line);
			nifBuffer.append("\n");
		}
		bffReader.close();
		String nifString = nifBuffer.toString();
		NIFParser parser = new TurtleNIFParser();
		List<Document> listDocs = parser.parseNIF(nifString);
		for (Document d : listDocs) {
			String docName = d.getDocumentURI();
			docName = docName.replaceAll("\\/","_");
//			docName = "\'"+docName+  "\'.txt";
			String docContent = d.getText();
			int wc = docContent.split("\\s+").length;
			pAnn.write(docName + "\t" + wc + "\n");
      
		}
		pAnn.flush();
		pAnn.close();
		System.out.println("...Finished dumping the Word Count Successfully.");
		
	}




	private static void dumpNumRecognizedMentionsFromN3News100() throws IOException {
		OutputStreamWriter pAnn = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/N3News100/num_recognized_mentions.tsv"),StandardCharsets.UTF_8);
		BufferedReader bff = new BufferedReader(new InputStreamReader(	new FileInputStream("/home/joao/datasets/N3News100/News100_GT.tsv"), StandardCharsets.UTF_8));
		TreeMap<String, Integer> hashTreeMap = new TreeMap<String, Integer>();
		String line = "";
		while ((line = bff.readLine()) != null) {
			String[] elems = line.split("\t");
			String docId = elems[0].trim().toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			Integer count = hashTreeMap.get(docId);
			if (count == null) {
				count = 1;
				hashTreeMap.put(docId, count);
			} else {
				count += 1;
				hashTreeMap.put(docId, count);
			}
		}
		bff.close();
		@SuppressWarnings("rawtypes")
		Iterator it = hashTreeMap.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry) it.next();
			String docId = ((String) pair.getKey()).toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			pAnn.write(docId + "\t" + pair.getValue() + "\n");
			it.remove(); // avoids a ConcurrentModificationException
		}
		pAnn.flush();
		pAnn.close();
		System.out.println("...Finished dumping the number of recognized mentions per document.");
	}



	
	public static void dumpGT() throws IOException {
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/N3News100/N3News100_GT.tsv"), StandardCharsets.UTF_8);
		String nifPath =  "/home/joao/datasets/N3News100/News-100.ttl";

		StringBuffer nifBuffer = new StringBuffer();
		BufferedReader bffReader = new BufferedReader(new InputStreamReader(new FileInputStream(nifPath), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			line = line.replaceAll("<E2><80><99>", "\'");
			line = line.replaceAll("<E2><80><9C>", "\"");
			line = line.replaceAll("<E2><80><9D>", "\"");
			nifBuffer.append(line);
			nifBuffer.append("\n");
		}
		bffReader.close();
		String nifString = nifBuffer.toString();
		NIFParser parser = new TurtleNIFParser();
		List<Document> listDocs = parser.parseNIF(nifString);
		for (Document d : listDocs) {
			String docName = d.getDocumentURI();
			docName = docName.replaceAll("\\/","_");
			String docContent = d.getText();
			List<Marking> markings = d.getMarkings();
			for (Marking m : markings) {
				NamedEntity ne = (NamedEntity) m;
				Pattern pattern = Pattern.compile("\\((.*?)\\)");
				Matcher matcher = pattern.matcher(ne.toString());
				while (matcher.find()) {
					String marking = matcher.group(1);
					String ms[] = marking.split(",");
					String offset = ms[0].trim();
					String length = ms[1].trim();
					String link = ms[2].trim();

					Pattern pattern2 = Pattern.compile("\\[(.*?)\\]");
					Matcher matcher2 = pattern2.matcher(link);
					while (matcher2.find()) {
						String url = matcher2.group(1);
						url = url.replace("http://dbpedia.org/resource/", "");
						url = url.replace("http://de.dbpedia.org/resource/", "");
						url = url.replace("http://aksw.org/notInWiki/", "");
						url = url.replace("_", " ");
	                	url =  URLDecoder.decode(url, "UTF-8");
	                	url = url.replaceAll("<E2><80><99>", "\'");
	                	url = url.replaceAll("<E2><80><9C>", "\"");
	                	url = url.replaceAll("<E2><80><9D>", "\"");
	        			
						String mention = docContent.substring(Integer.parseInt(offset),(Integer.parseInt(offset) + Integer.parseInt(length)));
						out.write(docName + "\t" + mention + "\t" + offset + "\t" + url + "\n");
					}

				}

			}
		}
		out.flush();
		out.close();
	}
	
	
	private static void dumpTextContent() throws NumberFormatException, IOException {
		String nifPath = "/home/joao/datasets/N3News100/News-100.ttl";
		StringBuffer nifBuffer = new StringBuffer();
		BufferedReader bffReader = new BufferedReader(	new InputStreamReader(new FileInputStream(nifPath), StandardCharsets.UTF_8));
		String line = "";
		while ((line = bffReader.readLine()) != null) {
			line = line.replaceAll("<E2><80><99>", "\'");
			line = line.replaceAll("<E2><80><9C>", "\"");
			line = line.replaceAll("<E2><80><9D>", "\"");
			nifBuffer.append(line);
			nifBuffer.append("\n");
		}
		bffReader.close();
		String nifString = nifBuffer.toString();
		NIFParser parser = new TurtleNIFParser();
		List<Document> listDocs = parser.parseNIF(nifString);
		for (Document d : listDocs) {
			String docName = d.getDocumentURI();
			docName = docName.replaceAll("\\/","_");
//			docName = "\'"+docName+  "\'.txt";
			String docContent = d.getText();
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream("/home/joao/datasets/N3News100/TEXT_FILES/"+docName), StandardCharsets.UTF_8);
        	out.write(docContent);
        	out.flush();
        	out.close();
		}
	}
	public TreeMap<String, String> getAmbiverseMap() {
		return ambiverseMap;
	}
	public TreeMap<String, String> getBabelfyMap() {
		return babelMap;
	}
	public TreeMap<String, String> getTagmeMap() {
		return tagmeMap;
	}
	
	
	public TreeMap<String, String> getAmbiverseMap_train() {
		return ambiverseMap_train;
	}
	
	public TreeMap<String, String> getBabelMap_train() {
		return babelMap_train;
	}

	public TreeMap<String, String> getTagmeMap_train() {
		return tagmeMap_train;
	}

	
	public TreeMap<String, String> getAmbiverseMap_test() {
		return ambiverseMap_test;
	}
	
	public TreeMap<String, String> getBabelMap_test() {
		return babelMap_test;
	}
	
	public TreeMap<String, String> getTagmeMap_test() {
		return tagmeMap_test;
	}
}
