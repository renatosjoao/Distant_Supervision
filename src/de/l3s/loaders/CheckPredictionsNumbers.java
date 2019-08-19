package de.l3s.loaders;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.compress.compressors.CompressorException;
import org.xml.sax.SAXException;

public class CheckPredictionsNumbers {

	
	
	public static void main(String[] args) throws CompressorException, IOException, NumberFormatException, SAXException, ParserConfigurationException{
//		String corpus = "conll";   //Babelfy > Ambiverse > Spotlight >  Tagme
//		String corpus = "iitb";    //Ambiverse > Spotlight >  Tagme > Babelfy
		String corpus = "wp";      // Tagme > Babelfy > Ambiverse > Spotlight
//		String corpus = "neel";    //Ambiverse > Tagme > Spotlight > Babelfy
//		String corpus = "gerdaq";  // Tagme > Babelfy > Ambiverse 
//		String corpus = "aquaint"; // Tagme > Ambiverse  >   Babelfy
//		String corpus = "msnbc";   // Ambiverse > Tagme > Spotlight > Babelfy
		int TP = 0;
		int numRECOGNIZED = 0;
		int zero_link = 0;
		int one_link = 0;
		int two_links = 0;
		int two_links_equal = 0;
		int two_links_diff = 0;
		int three_links = 0;
		int three_links_equal = 0;
		int three_links_2equal = 0;
		int three_links_diff = 0;
		int predictions_needed = 0;
		int correct_provided = 0;

		
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
		
		TreeMap<String,String> GT_test_MAP = d.getGT_MAP_test();
		TreeMap<String,String> AmbMAP = d.getAmbiverseMap_test();
		TreeMap<String,String> BabMAP = d.getBabelMap_test();
		TreeMap<String,String> TagMAP = d.getTagmeMap_test();
		
		Iterator<?> it = GT_test_MAP.entrySet().iterator();
		
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
	    	String chave  = (String) pair.getKey();
	    	//Integer num = (Integer)pair.getValue();	
	    	String[] elements = chave.split("\t");
	    	
	    	String docId = elements[0].toLowerCase();
			docId = docId.replaceAll("\'", "");
			docId = docId.replaceAll("\"", "");
			
	    	String mention = elements[1].toLowerCase();
//		    mention = mention.replaceAll("\'", "");
//		    mention = mention.replaceAll("\"", "");
		    
	    	String offset = elements[2];
			String k = docId+"\t"+mention+"\t"+offset;
			String Alink = "NULL";
			String Blink = "NULL";
			String Tlink = "NULL";
			String GTlink = GT_test_MAP.get(k);
			
			
			if(AmbMAP.containsKey(k)){
				Alink = AmbMAP.get(k);
			}
			if(BabMAP.containsKey(k)){
				Blink = BabMAP.get(k);
			}
			if(TagMAP.containsKey(k)){
				Tlink = TagMAP.get(k);
			}
			
			//# CASE 1 There are 3 links i.e. the mention is recognized by THREE of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	three_links++;
        		
	        	//The 3 links are the same
	        	if ( (Alink.equalsIgnoreCase(Blink)) &&  (Alink.equalsIgnoreCase(Tlink)) &&  (Blink.equalsIgnoreCase(Tlink)) ){
	                three_links_equal++;
	        	}
	        	//Ambiverse == Babelfy != Tagme
	        	if ( (Alink.equalsIgnoreCase(Blink)) &&  (!Alink.equalsIgnoreCase(Tlink))){
	        		three_links_2equal++;
	            	predictions_needed++;
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			correct_provided++;
	        		}
	        	}
	        	//Ambiverse != Babelfy == Tagme
	        	if ( (!Alink.equalsIgnoreCase(Tlink)) &&  (Blink.equalsIgnoreCase(Tlink)) ){
	        		three_links_2equal++;
	            	predictions_needed++;
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			correct_provided++;
	        		}
	        	}
	        	//Ambiverse == Tagme != Babelfy        
	        	if ( !(Alink.equalsIgnoreCase(Blink)) &&  (Alink.equalsIgnoreCase(Tlink))  ){
	        		three_links_2equal++;
	            	predictions_needed++;
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			correct_provided++;
	        		}
	        	}
	        	//Ambiverse != Babelfy != Tagme
	        	if ( (!Alink.equalsIgnoreCase(Blink)) &&  (!Alink.equalsIgnoreCase(Tlink)) &&  (!Blink.equalsIgnoreCase(Tlink)) ){
	                three_links_diff++;
	            	predictions_needed++;
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			correct_provided++;
	        		}
	        	}
//	        	if(predicted.equalsIgnoreCase("AMBIVERSE")){if(Alink.equalsIgnoreCase(GTlink)) { TP+=1;} continue;	}
//	            if(predicted.equalsIgnoreCase("BABELFY")){ if(Blink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
//	            if(predicted.equalsIgnoreCase("TAGME")){ if(Tlink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	        }
//	        
	        //# CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	two_links++;
	            if(Alink.equalsIgnoreCase(Blink)){
	                two_links_equal++;
	            }else{
	            	two_links_diff++;
	            	predictions_needed++;
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			correct_provided++;
	        		}
	            }
//	            if(predicted.equalsIgnoreCase("AMBIVERSE")){ if(Alink.equalsIgnoreCase(GTlink)) { TP+=1; } continue;	}
//	            if(predicted.equalsIgnoreCase("BABELFY")){ if(Blink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
//	            if(predicted.equalsIgnoreCase("TAGME")){ if(Blink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  } //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
	        }
	      //# CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	            two_links++;
	            if(Alink.equalsIgnoreCase(Tlink)){
	                two_links_equal++;
	            }else{
	                two_links_diff++;
	                predictions_needed++;
	                if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			correct_provided++;
	        		}
	            }
//	            if(predicted.equalsIgnoreCase("AMBIVERSE")){ if(Alink.equalsIgnoreCase(GTlink)) { TP+=1; } continue;	}
//	            if(predicted.equalsIgnoreCase("BABELFY")){ if(Alink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  } //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//	            if(predicted.equalsIgnoreCase("TAGME")){ if(Tlink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	        }	
	        //# CASE 2 There are 2 links
	        /* this is the case when the mention is recognized by TWO of the tools */
	        if((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	two_links++;
	        	
	            if(Blink.equalsIgnoreCase(Tlink)){
	                two_links_equal++;
	            }else{
	            	two_links_diff++;	                
	            	predictions_needed++;
	            	if( (Alink.equalsIgnoreCase(GTlink)) || (Blink.equalsIgnoreCase(GTlink)) || (Tlink.equalsIgnoreCase(GTlink)) ){
	        			correct_provided++;
	        		}
	            }
//	            if(predicted.equalsIgnoreCase("AMBIVERSE")){ if(Blink.equalsIgnoreCase(GTlink)) { TP+=1; } continue; }  //# < < < @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
//	            if(predicted.equalsIgnoreCase("BABELFY")){ if(Blink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
//	            if(predicted.equalsIgnoreCase("TAGME")){ if(Tlink.equalsIgnoreCase(GTlink)){  TP+=1;  } continue;  }
	        }
	        // # CASE 3 There is 1 link  
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((!Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	            one_link++;
	            if(Alink.equalsIgnoreCase(GTlink)){
	            	TP+=1;
	            }
	            continue;
	        }
	        // # CASE 3 There is 1 link  
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((Alink.equalsIgnoreCase("NULL")) && (!Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	one_link++;
	        	if(Blink.equalsIgnoreCase(GTlink)){
	            	TP+=1;
	            }
	        	continue;
	        }
	        // # CASE 3 There is 1 link  
	        /* this is the case when the mention is recognized by ONE of the tools */
	        if ((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (!Tlink.equalsIgnoreCase("NULL")) ){
	        	numRECOGNIZED++;
	        	one_link++;
	        	if(Tlink.equalsIgnoreCase(GTlink)){
	            	TP+=1;
	            }
	        	continue;
	        }
	        
	        //# CASE 4 There is no link
	        /* this is the case when the mention is not recognized by any of the tools */
	        if((Alink.equalsIgnoreCase("NULL")) && (Blink.equalsIgnoreCase("NULL")) && (Tlink.equalsIgnoreCase("NULL")) ){
	        	zero_link++;
	        	continue;
	        }
	   
		}
		System.out.println();
		System.out.println();
		System.out.println("GT mentions recognised by 0/3 systems :" +zero_link);
		System.out.println("GT mentions recognised by 1/3 systems :" +one_link);
		System.out.println("GT mentions recognised by 2/3 systems :"+two_links);
		System.out.println("......The 2 systems provide the same entity :" +two_links_equal);
		System.out.println("......The 2 systems provide different entity :" +two_links_diff);
		System.out.println("GT mentions recognised by 3/3 systems :"+three_links);
		System.out.println(".....The 3 systems provide the same entity :"+three_links_equal);
		System.out.println("......2 systems provide the same entity :"+three_links_2equal);
		System.out.println("......Each system provides a different entity :"+three_links_diff);
		System.out.println("GT mentions that need prediction :" +predictions_needed);

		System.out.println("......The correct entity is provided by at least 1 system :"+correct_provided);
		System.out.println("......The correct entity is not provided by at least 1 system :"+(predictions_needed-correct_provided));
		System.out.println("TOTAL "+(zero_link+one_link+two_links+three_links) );
		System.out.println();
		System.out.println();
	}
}
