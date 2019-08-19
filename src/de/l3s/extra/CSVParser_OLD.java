package de.l3s.extra;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class CSVParser_OLD {
    
	 
	 
	 
    public static void main(String[] args)throws Exception {
        String test = 
                "123," +
                "456," +
                "\"Simple Quote\"," +
                "\"Simple Quote, with comma\"," +
                "\"Quote with escape \\\\ \"," +
                " \"Quote with double quote \"\"Inner Content\"\"\"," +
                " Tricky \"Quote\" Example," +
                "\"Quote with new line \n inside.\"," +
                "\"  Quote with starting space\"," +
                "\"\"Start with double quote," +
                "End\n";
        //create 2 lines
        test = test + test;
        System.out.println("Parsing string : \n" + test);
        
        BufferedReader br = new BufferedReader(new StringReader(test));
        String[] s = null;
        int rowNo = 1;
        while((s=readNext(br))!=null){
            System.out.println("********************************************************");
            System.out.println("Record " + rowNo);
            System.out.println("********************************************************");
            for(int i =0;i<s.length;i++){
                System.out.println("Column(" + i + ") : " + s[i]);
            }
            rowNo++;
        }
        
        
    }
    
    public static String[] readNext(Reader reader)throws Exception{
        BufferedReader br = null;
        if(reader.getClass().isAssignableFrom(BufferedReader.class)){
            br = (BufferedReader)reader;
        }else{
            br = new BufferedReader(reader);
        }
        
        int lineNo = 1;
        String s = br.readLine();
        boolean isWithinQuote=false;
        boolean isIgnoreWhiteSpace = true;
        StringBuffer currentValue = new StringBuffer(128);
        //1.5
        //List<String> resultList = new ArrayList<String>(0);
        //1.4
        List resultList = new ArrayList();
        while(s!=null){
            char cArray[] = s.toCharArray();
            for(int i=0;i<cArray.length;i++){
                if(cArray[i]==WHITESPACE_CHAR){
                    if(isIgnoreWhiteSpace){
                        //do nothing
                    }else{
                        currentValue.append(cArray[i]);
                    }
                }else if(cArray[i]==COMMA_CHAR){
                    if(isWithinQuote){
                         currentValue.append(cArray[i]);
                    }else{
                        //add to list and start capture next value
                        resultList.add(currentValue.toString());
                        currentValue.setLength(0);
                        isWithinQuote = false;
                        isIgnoreWhiteSpace = true;
                    }
                }else if(cArray[i]==QUOTE_CHAR){
                    if(isWithinQuote){
                        if(cArray.length>i+1){
                            if(cArray[i+1]==QUOTE_CHAR){
                                //special char "" for "
                                currentValue.append(QUOTE_CHAR); i++;
                            }else{
                                //close of quote
                                isWithinQuote = false;
                                isIgnoreWhiteSpace = true;
                            }    
                        }else{
                            isWithinQuote = false;
                            isIgnoreWhiteSpace = true;
                        }
                        
                    }else{
                        
                        if(currentValue.length()>0){
                            //quote in the middle: a,bc"d"ef,g
                            currentValue.append(QUOTE_CHAR);
                        }else{
                            //start quote
                            isWithinQuote = true;    
                            isIgnoreWhiteSpace = false;
                        }
                        
                    }
                }else if(cArray[i]==ESCAPE_CHAR){
                    if(cArray.length>i+1){
                        //special char \\ for \
                        if ( isWithinQuote && cArray[i+1]==ESCAPE_CHAR){
                            currentValue.append(ESCAPE_CHAR); i++;
                        }else{
                            currentValue.append(ESCAPE_CHAR);
                        }
                    }else{
                        currentValue.append(ESCAPE_CHAR);
                    }
                }else{
                    currentValue.append(cArray[i]);
                    isIgnoreWhiteSpace = false;
                }
            }
            
            
            if(currentValue.length()>0){
                if(isWithinQuote){
                    //end of line read.. but still within quote... append back the new line 
                    currentValue.append("\n");
                    //and read next line
                    s = br.readLine();
                    lineNo++;
                    if(s==null){
                        throw new Exception("Unclosed quote. Expecting char '\"' ");
                    }
                }else{
                    resultList.add(currentValue.toString());
                    s=null;
                }
            }else{
                //empty String
                resultList.add(currentValue.toString());
                s=null;
            }
        }
        
        if(resultList.size()>0){
            String[] result = new String[resultList.size()];
            resultList.toArray(result);
            resultList.clear();
            return result;    
        }else{
            return null;
        }
        
    
        
    }
 
    private static final char WHITESPACE_CHAR = ' ';
    private static final char COMMA_CHAR = ',';
    private static final char QUOTE_CHAR = '"';
    private static final char ESCAPE_CHAR = '\\';
 
}