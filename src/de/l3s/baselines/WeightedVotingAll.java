package de.l3s.baselines;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.compress.compressors.CompressorException;

import de.l3s.loaders.DataLoaders;
import de.l3s.loaders.DataLoaders_AQUAINT;
import de.l3s.loaders.DataLoaders_CONLL;
import de.l3s.loaders.DataLoaders_GERDAQ;
import de.l3s.loaders.DataLoaders_IITB;
import de.l3s.loaders.DataLoaders_MSNBC;
import de.l3s.loaders.DataLoaders_NEEL;
import de.l3s.loaders.DataLoaders_WP;

public class WeightedVotingAll {

	public static void main(String[] args) throws Exception {
//		String corpus = "ace04";
//		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
		String corpus = "conll";   //Babelfy > Ambiverse > Spotlight >  Tagme
//		String corpus = "gerdaq";  // Tagme > Babelfy > Ambiverse 
//		String corpus = "iitb";    //Ambiverse > Spotlight >  Tagme > Babelfy
//		String corpus = "kore50";
// 		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
//	    String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
//		String corpus = "reuters128";   
// 		String corpus = "wp";      // Tagme > Babelfy > Ambiverse > Spotlight
		
		DataLoaders d = new DataLoaders();
		
		if(corpus.equalsIgnoreCase("conll")){
			d = DataLoaders_CONLL.getInstance();
		}
		if(corpus.equalsIgnoreCase("iitb")){
			d = DataLoaders_IITB.getInstance();
		}
		if(corpus.equalsIgnoreCase("wp")){
			d = DataLoaders_WP.getInstance();
		}
		if(corpus.equalsIgnoreCase("neel")){
			d = DataLoaders_NEEL.getInstance();
		}
		if(corpus.equalsIgnoreCase("gerdaq")){
			d = DataLoaders_GERDAQ.getInstance();
		}
		if(corpus.equalsIgnoreCase("aquaint")){
			d = DataLoaders_AQUAINT.getInstance();
		}
		if(corpus.equalsIgnoreCase("msnbc")){
			d = DataLoaders_MSNBC.getInstance();
		}
		

		WeightedVotingAll.WeightedVotingAll(d, corpus);

	}

	private static double vote(String ELTool) {
		// Ambiverse P:0.8070614400889631 R:0.6472686733556299
		// F:0.7183865379856471
		// Babelfy P:0.8152492668621701 R:0.6818283166109254
		// F:0.7425934919864011
		// Tagme P:0.7868204283360791 R:0.5324414715719064 F:0.6351063829787233

		double rank = 0.0; // rank of the EL tool on the reference corpus
		double N = 3.0; // number of EL tools
		double vote = 0.0;
		double alpha = 1.75;
		double Panr = 0.0; // EL tool's Precision on the ranking reference corpus
		if (ELTool.equalsIgnoreCase("Ambiverse")) {
//			Panr = 0.8070614400889631;
//			rank = 2.0; // for CONLL rank = 2
			Panr = 0.736; // complete data set
			rank = 3.0; 
		}
		if (ELTool.equalsIgnoreCase("Babelfy")) {
//			Panr = 0.8152492668621701;
//			rank = 1.0; // for CONLL rank = 2
			Panr = 0.801; //complete data set 
			rank = 1.0; // for CONLL rank = 1
		}
		if (ELTool.equalsIgnoreCase("Tagme")) {
//			Panr = 0.7868204283360791;
//			rank = 3.0;
			Panr = 0.799; //complete data set
			rank = 2.0;
		}

		vote = ((N - (rank - alpha)) * Panr) / N;
		return vote;
	}

	public static void WeightedVotingAll(DataLoaders d , String corpus) throws CompressorException, IOException {
		OutputStreamWriter predOut = new OutputStreamWriter(
				new FileOutputStream("./resources/" + corpus
						+ "/dataset.multiclass." + corpus
						+ ".baseline.WEIGHTEDALL.out"), StandardCharsets.UTF_8);

		double Pmax = 0.0; // maximum precision for all annotators on the
							// ranking corpus;
//		Pmax = 0.8152492668621701;
		Pmax = 0.801;
		int TP = 0;
		int numRECOGNIZED = 0;
		int one_linkPredicted = 0;
		int two_linksPredicted = 0;
		int three_linksPredicted = 0;

		int zero_link = 0;
		int one_link = 0;
		int one_linkA = 0;
		int one_linkA_correct = 0;
		int one_linkB = 0;
		int one_linkB_correct = 0;
		int one_linkT = 0;
		int one_linkT_correct = 0;

		int two_links = 0;
		int two_linksAB = 0;
		int two_linksAT = 0;
		int two_linksBT = 0;

		int one_linkPredictedA = 0;
		int one_linkPredictedB = 0;
		int one_linkPredictedT = 0;

		int two_linksPredictedAB = 0;
		int two_linksPredictedAT = 0;
		int two_linksPredictedBT = 0;

		int three_linksPredictedABT = 0;
		int predictions_neededAB = 0;
		int predictions_neededAT = 0;
		int predictions_neededBT = 0;
		int predictions_neededABT = 0;

		int two_links_equal = 0;
		int two_links_diff = 0;
		int three_links = 0;
		int three_linksABT = 0;
		int three_links_equal = 0;
		int three_links_2equal = 0;
		int three_links_diff = 0;
		int predictions_needed = 0;
		int correct_provided = 0;

		
		TreeMap<String,String> GT_MAP = d.getGT_MAP();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap();
		TreeMap<String,String> BabMAP = d.getBabelfyMap();
		TreeMap<String,String> TagMAP = d.getTagmeMap();

		int i = 0;
		Iterator<?> it = GT_MAP.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry) it.next();

			String key = (String) pair.getKey();
			String val = (String) pair.getValue();

			String[] elems = key.split("\t");
			String docid = elems[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			String mention = elems[1].toLowerCase();

			String offset = elems[2];

			String Alink = "NULL";
			String Blink = "NULL";
			String Tlink = "NULL";

			String k = docid + "\t" + mention + "\t" + offset;
			String GTlink = GT_MAP.get(k).toLowerCase();

			if (AmbMAP.containsKey(k)) {
				Alink = AmbMAP.get(k).toLowerCase();
			}
			if (BabMAP.containsKey(k)) {
				Blink = BabMAP.get(k).toLowerCase();
			}
			if (TagMAP.containsKey(k)) {
				Tlink = TagMAP.get(k).toLowerCase();
			}

			// # CASE 0 - There is no link
			/*
			 * This is the case when the mention is NOT recognized by any of the
			 * tools - No prediction needed
			 */
			if ((Alink.equalsIgnoreCase("NULL"))
					&& (Blink.equalsIgnoreCase("NULL"))
					&& (Tlink.equalsIgnoreCase("NULL"))) {
				zero_link++;
				continue;
			}

			// # CASE 1 - There is 1 link - Ambiverse
			/*
			 * This is the case when the mention is recognized by ONE of the
			 * tools - Ambiverse
			 */
			if ((!Alink.equalsIgnoreCase("NULL"))
					&& (Blink.equalsIgnoreCase("NULL"))
					&& (Tlink.equalsIgnoreCase("NULL"))) {
				// numRECOGNIZED++;
				one_link++;
				one_linkA++;

				// double voteA = vote("Ambiverse");
				// if(voteA > Pmax){
				numRECOGNIZED++;
				if (Alink.equalsIgnoreCase(GTlink)) {
					TP += 1;
					one_linkA_correct++;
				}
				predOut.write(k + "\t" + Alink + "\n");
				// }
				continue;
			}
			// # CASE 1 - There is 1 link - Babelfy
			/*
			 * This is the case when the mention is recognized by ONE of the
			 * tools - Babelfy
			 */
			if ((Alink.equalsIgnoreCase("NULL"))
					&& (!Blink.equalsIgnoreCase("NULL"))
					&& (Tlink.equalsIgnoreCase("NULL"))) {
				// numRECOGNIZED++;
				one_link++;
				one_linkB++;
				// double voteB = vote("Babelfy");
				// if(voteB > Pmax){
				numRECOGNIZED++;
				if (Blink.equalsIgnoreCase(GTlink)) {
					TP += 1;
					one_linkB_correct++;
				}
				predOut.write(k + "\t" + Blink + "\n");
				// }
				continue;
			}
			// # CASE 1 - There is 1 link - Tagme
			/*
			 * This is the case when the mention is recognized by ONE of the
			 * tools - Tagme
			 */
			if ((Alink.equalsIgnoreCase("NULL"))
					&& (Blink.equalsIgnoreCase("NULL"))
					&& (!Tlink.equalsIgnoreCase("NULL"))) {
				// numRECOGNIZED++;
				one_link++;
				one_linkT++;
				// double voteT = vote("Tagme");
				// if(voteT>Pmax){
				numRECOGNIZED++;
				if (Tlink.equalsIgnoreCase(GTlink)) {
					TP += 1;
					one_linkT_correct++;
				}
				predOut.write(k + "\t" + Tlink + "\n");
				// }
				continue;
			}
			// # CASE 2 There are 2 links - Ambiverse and Babelfy
			/*
			 * This is the case when the mention is recognized by TWO of the
			 * tools - Ambiverse and Babelfy
			 */
			if ((!Alink.equalsIgnoreCase("NULL"))
					&& (!Blink.equalsIgnoreCase("NULL"))
					&& (Tlink.equalsIgnoreCase("NULL"))) {
				// numRECOGNIZED++;
				two_links++;
				two_linksAB++;

				if (Alink.equalsIgnoreCase(Blink)) {
					two_links_equal++;
				} else {
					two_links_diff++;
					predictions_needed++;
					if ((Alink.equalsIgnoreCase(GTlink))
							|| (Blink.equalsIgnoreCase(GTlink))
							|| (Tlink.equalsIgnoreCase(GTlink))) {
						correct_provided++;
					}
				}

				double voteA = vote("Ambiverse");
				double voteB = vote("Babelfy");
				if (voteA >= voteB) {
					numRECOGNIZED++;
					if (Alink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					predOut.write(k + "\t" + Alink + "\n");
					continue;
				} else {
					numRECOGNIZED++;
					if (Blink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					predOut.write(k + "\t" + Blink + "\n");
					continue;
				}
			}
			// # CASE 2 There are 2 links - Ambiverse and Tagme
			/*
			 * This is the case when the mention is recognized by TWO of the
			 * tools - Ambiverse and Tagme
			 */
			if ((!Alink.equalsIgnoreCase("NULL"))
					&& (Blink.equalsIgnoreCase("NULL"))
					&& (!Tlink.equalsIgnoreCase("NULL"))) {
				// numRECOGNIZED++;
				two_links++;
				two_linksAT++;
				if (Alink.equalsIgnoreCase(Tlink)) {
					two_links_equal++;
				} else {
					two_links_diff++;
					predictions_needed++;
					if ((Alink.equalsIgnoreCase(GTlink))
							|| (Blink.equalsIgnoreCase(GTlink))
							|| (Tlink.equalsIgnoreCase(GTlink))) {
						correct_provided++;
					}
				}
				double voteA = vote("Ambiverse");
				double voteT = vote("Tagme");
				if (voteA > voteT) {
					numRECOGNIZED++;
					if (Alink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					predOut.write(k + "\t" + Alink + "\n");
					continue;
				} else {
					numRECOGNIZED++;
					if (Tlink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					predOut.write(k + "\t" + Tlink + "\n");
					continue;
				}
				//
			}
			// # CASE 2 There are 2 links - Babelfy and Tagme
			/*
			 * This is the case when the mention is recognized by TWO of the
			 * tools - Babelfy and Tagme
			 */
			if ((Alink.equalsIgnoreCase("NULL"))
					&& (!Blink.equalsIgnoreCase("NULL"))
					&& (!Tlink.equalsIgnoreCase("NULL"))) {
				// numRECOGNIZED++;
				two_links++;
				two_linksBT++;

				if (Blink.equalsIgnoreCase(Tlink)) {
					two_links_equal++;
				} else {
					two_links_diff++;
					predictions_needed++;
					if ((Alink.equalsIgnoreCase(GTlink))
							|| (Blink.equalsIgnoreCase(GTlink))
							|| (Tlink.equalsIgnoreCase(GTlink))) {
						correct_provided++;
					}
				}
				double voteB = vote("Babelfy");
				double voteT = vote("Tagme");
				if (voteB >= voteT) {
					numRECOGNIZED++;
					if (Blink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					predOut.write(k + "\t" + Blink + "\n");
					continue;
				} else {
					numRECOGNIZED++;
					if (Tlink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					predOut.write(k + "\t" + Tlink + "\n");
					continue;
				}
			}
			// # CASE 3 There are 3 links
			/*
			 * This is the case when the mention is recognized by THREE of the
			 * tools
			 */
			if ((!Alink.equalsIgnoreCase("NULL"))
					&& (!Blink.equalsIgnoreCase("NULL"))
					&& (!Tlink.equalsIgnoreCase("NULL"))) {
				// numRECOGNIZED++;
				three_links++;
				three_linksABT++;
				// The 3 links are the same
				if ((Alink.equalsIgnoreCase(Blink))
						&& (Alink.equalsIgnoreCase(Tlink))
						&& (Blink.equalsIgnoreCase(Tlink))) {
					three_links_equal++;
				}
				// Ambiverse == Babelfy != Tagme
				if ((Alink.equalsIgnoreCase(Blink))
						&& (!Alink.equalsIgnoreCase(Tlink))) {
					three_links_2equal++;
					predictions_needed++;
					if ((Alink.equalsIgnoreCase(GTlink))
							|| (Blink.equalsIgnoreCase(GTlink))
							|| (Tlink.equalsIgnoreCase(GTlink))) {
						correct_provided++;
					}
				}
				// Ambiverse != Babelfy == Tagme
				if ((!Alink.equalsIgnoreCase(Tlink))
						&& (Blink.equalsIgnoreCase(Tlink))) {
					three_links_2equal++;
					predictions_needed++;
					if ((Alink.equalsIgnoreCase(GTlink))
							|| (Blink.equalsIgnoreCase(GTlink))
							|| (Tlink.equalsIgnoreCase(GTlink))) {
						correct_provided++;
					}
				}
				// Ambiverse == Tagme != Babelfy
				if (!(Alink.equalsIgnoreCase(Blink))
						&& (Alink.equalsIgnoreCase(Tlink))) {
					three_links_2equal++;
					predictions_needed++;
					if ((Alink.equalsIgnoreCase(GTlink))
							|| (Blink.equalsIgnoreCase(GTlink))
							|| (Tlink.equalsIgnoreCase(GTlink))) {
						correct_provided++;
					}
				}
				// Ambiverse != Babelfy != Tagme
				if ((!Alink.equalsIgnoreCase(Blink))
						&& (!Alink.equalsIgnoreCase(Tlink))
						&& (!Blink.equalsIgnoreCase(Tlink))) {
					three_links_diff++;
					predictions_needed++;
					if ((Alink.equalsIgnoreCase(GTlink))
							|| (Blink.equalsIgnoreCase(GTlink))
							|| (Tlink.equalsIgnoreCase(GTlink))) {
						correct_provided++;
					}
				}
				double voteA = vote("Ambiverse");
				double voteB = vote("Babelfy");
				double voteT = vote("Tagme");
				if ((voteA >= voteB) && (voteA >= voteT)) {
					numRECOGNIZED++;
					if (Alink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					predOut.write(k + "\t" + Alink + "\n");
					continue;
				}

				if ((voteB >= voteA) && (voteB >= voteT)) {
					numRECOGNIZED++;
					if (Blink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					predOut.write(k + "\t" + Blink + "\n");
					continue;
				}

				if ((voteT >= voteA) && (voteT >= voteB)) {
					numRECOGNIZED++;
					if (Tlink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					predOut.write(k + "\t" + Tlink + "\n");
					continue;
				}
			}
		}
//		System.out.println();
//		// System.out.println();
//		System.out.println("-----------------------------------------");
//		System.out.println("GT mentions recognised by 0/3 systems :"
//				+ zero_link);
//		System.out.println("-----------------------------------------");
//		System.out
//				.println("GT mentions recognised by 1/3 systems :" + one_link);
//		System.out.println("-----------------------------------------");
//		System.out.println("......GT mentions recognised by Amb :" + one_linkA
//				+ "\tTP: " + one_linkA_correct);
//		System.out.println("......GT mentions recognised by Bab :" + one_linkB
//				+ "\tTP: " + one_linkB_correct);
//		System.out.println("......GT mentions recognised by Tag :" + one_linkT
//				+ "\tTP: " + one_linkT_correct);
//		System.out.println("...... ");
//		System.out.println("......  # mentions need binary class prediction :"
//				+ one_link);
//		System.out.println("......  # mentions predicted by binary Amb clf :"
//				+ one_linkPredictedA);
//		System.out.println("......  # mentions predicted by binary Bab clf :"
//				+ one_linkPredictedB);
//		System.out.println("......  # mentions predicted by binary Tag clf :"
//				+ one_linkPredictedT);
//		System.out.println("-----------------------------------------");
//		System.out.println("GT mentions recognised by 2/3 systems :"
//				+ two_links);
//		System.out.println("-----------------------------------------");
//		System.out.println("......GT mentions recognised by (Amb & Bab) :"
//				+ two_linksAB);
//		System.out.println("......GT mentions recognised by (Amb & Tag) :"
//				+ two_linksAT);
//		System.out.println("......GT mentions recognised by (Bab & Tag) :"
//				+ two_linksBT);
//		System.out.println(".........  2 systems provide the same entity :"
//				+ two_links_equal);
//		System.out.println(".........  2 systems provide different entity :"
//				+ two_links_diff);
//		System.out.println("......  # mentions need multiclass prediction :"
//				+ two_links_diff);
//		System.out
//				.println("...........# mentions need multiclass prediction (Amb & Bab) :"
//						+ predictions_neededAB);
//		System.out
//				.println("...........# mentions need multiclass prediction (Amb & Tag) :"
//						+ predictions_neededAT);
//		System.out
//				.println("...........# mentions need multiclass prediction (Bab & Tag) :"
//						+ predictions_neededBT);
//		System.out.println("......  # mentions need binary class prediction :"
//				+ two_links_equal);
//		System.out
//				.println("...........# mentions predicted by binary (Amb & Bab) clf:"
//						+ two_linksPredictedAB);
//		System.out
//				.println("...........# mentions predicted by binary (Amb & Tag) clf:"
//						+ two_linksPredictedAT);
//		System.out
//				.println("...........# mentions predicted by binary (Bab & Tag) clf:"
//						+ two_linksPredictedBT);
//		System.out.println("-----------------------------------------");
//		System.out.println("GT mentions recognised by 3/3 systems :"
//				+ three_links);
//		System.out.println("-----------------------------------------");
//		System.out.println(".........  3 systems provide the same entity :"
//				+ three_links_equal);
//		System.out.println(".........  2 systems provide the same entity :"
//				+ three_links_2equal);
//		System.out
//				.println(".........  each system provides a different entity :"
//						+ three_links_diff);
//		System.out.println("......  # mentions need multiclass prediction :"
//				+ predictions_neededABT);
//		System.out.println("......  # mentions need binary class prediction :"
//				+ three_links_equal);
//		System.out.println("...........# mentions predicted by binary class :"
//				+ three_linksPredicted);
//		System.out.println("-----------------------------------------");
//		System.out.println("GT mentions that need prediction :"
//				+ predictions_needed);
//		// System.out.println("GT mentions that need prediction :"
//		// +(predictions_neededAB+predictions_neededAT+predictions_neededBT+predictions_neededABT));
//		System.out.println("-----------------------------------------");
//		// System.out.println("......The correct entity is provided by at least 1 system :"+correct_provided);
//		// System.out.println("......The correct entity is not provided by at least 1 system :"+(predictions_needed-correct_provided));
//		// System.out.println("-----------------------------------------");
//		System.out.println("TOTAL "
//				+ (zero_link + one_link + two_links + three_links));
//		// System.out.println();
//		System.out.println();

		double P = 0.0;//
		double R = 0.0;
		double F = 0.0;

		P = (double) TP / (double) numRECOGNIZED;
		R = (double) TP / (double) GT_MAP.keySet().size();
		
		
		double scale = Math.pow(10, 3);
		P =  ( Math.round(( P ) * scale) / scale ) * 100.0;
		R =  ( Math.round(( R ) * scale) / scale ) * 100.0;
		F =  ( 2*((P*R)/(P+R)) );
		
		System.out.println("Weigthed voting all " +corpus );
		System.out.println(P + "\t& " + R + "\t& " + F);
//		System.out.println("TP:" + TP);
//		System.out.println("numRecog:" + numRECOGNIZED);
	}
	
	
	
	
	public static void WeightedVotingAllKFold(DataLoaders d , String corpus ,TreeMap<String,String> foldMap) throws CompressorException, IOException {
		double Pmax = 0.0; // maximum precision for all annotators on the
							// ranking corpus;
		Pmax = 0.8152492668621701;
		int TP = 0;
		int numRECOGNIZED = 0;
 
		TreeMap<String, String> GT_test_MAP = d.getGT_MAP_test();
		TreeMap<String, String> AmbMAP = d.getAmbiverseMap_test();
		TreeMap<String, String> BabMAP = d.getBabelMap_test();
		TreeMap<String, String> TagMAP = d.getTagmeMap_test();

		int i = 0;
		Iterator<?> it = foldMap.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry) it.next();

			String key = (String) pair.getKey();
			String val = (String) pair.getValue();

			String[] elems = key.split("\t");
			String docid = elems[0].toLowerCase();
			docid = docid.replaceAll("\'", "");
			docid = docid.replaceAll("\"", "");
			String mention = elems[1].toLowerCase();
//			mention = mention.replaceAll("\'", "");
//			mention = mention.replaceAll("\"", "");

			String offset = elems[2];

			String Alink = "NULL";
			String Blink = "NULL";
			String Tlink = "NULL";

			String k = docid + "\t" + mention + "\t" + offset;
			String GTlink = GT_test_MAP.get(k).toLowerCase();

			if (AmbMAP.containsKey(k)) {
				Alink = AmbMAP.get(k).toLowerCase();
			}
			if (BabMAP.containsKey(k)) {
				Blink = BabMAP.get(k).toLowerCase();
			}
			if (TagMAP.containsKey(k)) {
				Tlink = TagMAP.get(k).toLowerCase();
			}

			// # CASE 0 - There is no link
			/*
			 * This is the case when the mention is NOT recognized by any of the
			 * tools - No prediction needed
			 */
			if ((Alink.equalsIgnoreCase("NULL"))
					&& (Blink.equalsIgnoreCase("NULL"))
					&& (Tlink.equalsIgnoreCase("NULL"))) {
				continue;
			}

			// # CASE 1 - There is 1 link - Ambiverse
			/*
			 * This is the case when the mention is recognized by ONE of the
			 * tools - Ambiverse
			 */
			if ((!Alink.equalsIgnoreCase("NULL"))
					&& (Blink.equalsIgnoreCase("NULL"))
					&& (Tlink.equalsIgnoreCase("NULL"))) {
				// numRECOGNIZED++;

				// double voteA = vote("Ambiverse");
				// if(voteA > Pmax){
				numRECOGNIZED++;
				if (Alink.equalsIgnoreCase(GTlink)) {
					TP += 1;
				}
				// }
				continue;
			}
			// # CASE 1 - There is 1 link - Babelfy
			/*
			 * This is the case when the mention is recognized by ONE of the
			 * tools - Babelfy
			 */
			if ((Alink.equalsIgnoreCase("NULL"))
					&& (!Blink.equalsIgnoreCase("NULL"))
					&& (Tlink.equalsIgnoreCase("NULL"))) {
				// numRECOGNIZED++;
				// double voteB = vote("Babelfy");
				// if(voteB > Pmax){
				numRECOGNIZED++;
				if (Blink.equalsIgnoreCase(GTlink)) {
					TP += 1;
				}
				// }
				continue;
			}
			// # CASE 1 - There is 1 link - Tagme
			/*
			 * This is the case when the mention is recognized by ONE of the
			 * tools - Tagme
			 */
			if ((Alink.equalsIgnoreCase("NULL"))
					&& (Blink.equalsIgnoreCase("NULL"))
					&& (!Tlink.equalsIgnoreCase("NULL"))) {
				// numRECOGNIZED++;
				// double voteT = vote("Tagme");
				// if(voteT>Pmax){
				numRECOGNIZED++;
				if (Tlink.equalsIgnoreCase(GTlink)) {
					TP += 1;
				}
				// }
				continue;
			}
			// # CASE 2 There are 2 links - Ambiverse and Babelfy
			/*
			 * This is the case when the mention is recognized by TWO of the
			 * tools - Ambiverse and Babelfy
			 */
			if ((!Alink.equalsIgnoreCase("NULL"))
					&& (!Blink.equalsIgnoreCase("NULL"))
					&& (Tlink.equalsIgnoreCase("NULL"))) {
				// numRECOGNIZED++;

				if (Alink.equalsIgnoreCase(Blink)) {
				} else {
					if ((Alink.equalsIgnoreCase(GTlink))
							|| (Blink.equalsIgnoreCase(GTlink))
							|| (Tlink.equalsIgnoreCase(GTlink))) {
					}
				}

				double voteA = vote("Ambiverse");
				double voteB = vote("Babelfy");
				if (voteA >= voteB) {
					numRECOGNIZED++;
					if (Alink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					continue;
				} else {
					numRECOGNIZED++;
					if (Blink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					continue;
				}
			}
			// # CASE 2 There are 2 links - Ambiverse and Tagme
			/*
			 * This is the case when the mention is recognized by TWO of the
			 * tools - Ambiverse and Tagme
			 */
			if ((!Alink.equalsIgnoreCase("NULL"))
					&& (Blink.equalsIgnoreCase("NULL"))
					&& (!Tlink.equalsIgnoreCase("NULL"))) {
				// numRECOGNIZED++;
				if (Alink.equalsIgnoreCase(Tlink)) {
				} else {
					if ((Alink.equalsIgnoreCase(GTlink))
							|| (Blink.equalsIgnoreCase(GTlink))
							|| (Tlink.equalsIgnoreCase(GTlink))) {
					}
				}
				double voteA = vote("Ambiverse");
				double voteT = vote("Tagme");
				if (voteA > voteT) {
					numRECOGNIZED++;
					if (Alink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					continue;
				} else {
					numRECOGNIZED++;
					if (Tlink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					continue;
				}
				//
			}
			// # CASE 2 There are 2 links - Babelfy and Tagme
			/*
			 * This is the case when the mention is recognized by TWO of the
			 * tools - Babelfy and Tagme
			 */
			if ((Alink.equalsIgnoreCase("NULL"))
					&& (!Blink.equalsIgnoreCase("NULL"))
					&& (!Tlink.equalsIgnoreCase("NULL"))) {
				// numRECOGNIZED++;

				if (Blink.equalsIgnoreCase(Tlink)) {
				} else {
					if ((Alink.equalsIgnoreCase(GTlink))
							|| (Blink.equalsIgnoreCase(GTlink))
							|| (Tlink.equalsIgnoreCase(GTlink))) {
					}
				}
				double voteB = vote("Babelfy");
				double voteT = vote("Tagme");
				if (voteB >= voteT) {
					numRECOGNIZED++;
					if (Blink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					continue;
				} else {
					numRECOGNIZED++;
					if (Tlink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					continue;
				}
			}
			// # CASE 3 There are 3 links
			/*
			 * This is the case when the mention is recognized by THREE of the
			 * tools
			 */
			if ((!Alink.equalsIgnoreCase("NULL"))
					&& (!Blink.equalsIgnoreCase("NULL"))
					&& (!Tlink.equalsIgnoreCase("NULL"))) {
				// numRECOGNIZED++;
				// The 3 links are the same
				if ((Alink.equalsIgnoreCase(Blink))
						&& (Alink.equalsIgnoreCase(Tlink))
						&& (Blink.equalsIgnoreCase(Tlink))) {
				}
				// Ambiverse == Babelfy != Tagme
				if ((Alink.equalsIgnoreCase(Blink))
						&& (!Alink.equalsIgnoreCase(Tlink))) {
					if ((Alink.equalsIgnoreCase(GTlink))
							|| (Blink.equalsIgnoreCase(GTlink))
							|| (Tlink.equalsIgnoreCase(GTlink))) {
					}
				}
				// Ambiverse != Babelfy == Tagme
				if ((!Alink.equalsIgnoreCase(Tlink))
						&& (Blink.equalsIgnoreCase(Tlink))) {
					if ((Alink.equalsIgnoreCase(GTlink))
							|| (Blink.equalsIgnoreCase(GTlink))
							|| (Tlink.equalsIgnoreCase(GTlink))) {
					}
				}
				// Ambiverse == Tagme != Babelfy
				if (!(Alink.equalsIgnoreCase(Blink))
						&& (Alink.equalsIgnoreCase(Tlink))) {
					if ((Alink.equalsIgnoreCase(GTlink))
							|| (Blink.equalsIgnoreCase(GTlink))
							|| (Tlink.equalsIgnoreCase(GTlink))) {
					}
				}
				// Ambiverse != Babelfy != Tagme
				if ((!Alink.equalsIgnoreCase(Blink))
						&& (!Alink.equalsIgnoreCase(Tlink))
						&& (!Blink.equalsIgnoreCase(Tlink))) {
					if ((Alink.equalsIgnoreCase(GTlink))
							|| (Blink.equalsIgnoreCase(GTlink))
							|| (Tlink.equalsIgnoreCase(GTlink))) {
					}
				}
				double voteA = vote("Ambiverse");
				double voteB = vote("Babelfy");
				double voteT = vote("Tagme");
				if ((voteA >= voteB) && (voteA >= voteT)) {
					numRECOGNIZED++;
					if (Alink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					continue;
				}

				if ((voteB >= voteA) && (voteB >= voteT)) {
					numRECOGNIZED++;
					if (Blink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					continue;
				}

				if ((voteT >= voteA) && (voteT >= voteB)) {
					numRECOGNIZED++;
					if (Tlink.equalsIgnoreCase(GTlink)) {
						TP += 1;
					}
					continue;
				}
			}
		}

		double P = 0.0;//
		double R = 0.0;
		double F = 0.0;

		P = (double) TP / (double) numRECOGNIZED;
		R = (double) TP / (double) foldMap.keySet().size();
		F =  ( 2*((P*R)/(P+R)) )*100.0;
		
		double scale = Math.pow(10, 3);
		P =  ( Math.round(( P ) * scale) / scale ) * 100.0;
		R =  ( Math.round(( R ) * scale) / scale ) * 100.0;
		
		System.out.println( P+"\t "+R+"\t "+F);
//		System.out.println("Weigthed voting all  ["+corpus+"]" + "\t " + P+"\t "+R+"\t "+F);

	}
}
