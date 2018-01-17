package org.eclipse.compare.internal.search;

import java.util.ArrayList;
import java.util.List;

public class MethodBias implements InductiveBias{
	
	List<List<String>> allQuerries = new ArrayList<List<String>>();
	int maxIndex;
	public void ChooseSeedHornClause(){
		int maxMethodCount = 0;
		for(int i=0;i<allQuerries.size();i++){
			int methodCount = 0;
			for(int j=0;j<allQuerries.get(i).size();j++){
				if(allQuerries.get(i).get(j).contains("methodcall(")){
					methodCount ++;
				}
			}
			if(methodCount>maxMethodCount){
				maxMethodCount = methodCount;
				maxIndex = i;
			}
		}
	}
}
