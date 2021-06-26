/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opennlp.tools.parse_thicket.pattern_structure;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Elizaveta
 */
public class MyReadPhrase {
	
	ArrayList<String> obNames = null;
	ArrayList<String> atNames = null;
	Map<Integer,String> binContext = null;
	int objectsNumber = 0;
	int attributesNumber = 0;
	
	public void ReadContextFromCxt(String filename) throws FileNotFoundException, IOException{

		obNames = new ArrayList<String>();
		atNames = new ArrayList<String>();
					
		BufferedReader br = new BufferedReader(new FileReader(filename));
		try	{
		    String line;
		    objectsNumber = Integer.parseInt(br.readLine());
		    attributesNumber = Integer.parseInt(br.readLine());
		    br.readLine();	
	    
		    binContext = new HashMap<Integer,String>();
//		    
//		    for (int i=0;i<objectsNumber;i++){
//		    	obNames.add(br.readLine());
//		    }
//		    
//		    for (int i=0;i<attributesNumber;i++){
//		    	atNames.add(br.readLine());
//		    }
		    
		    int i=0;
		    while ((line = br.readLine()) != null) {
                        binContext.put(i, line);
		    	i+=1;	    	
		    }	    
		} catch (Exception e){
			e.printStackTrace();
		}
	}
        
        public HashMap<Integer, String> getContext(){
            return (HashMap<Integer, String>)binContext;
        }
}
