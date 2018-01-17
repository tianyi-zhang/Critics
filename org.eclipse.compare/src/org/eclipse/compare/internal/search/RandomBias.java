package org.eclipse.compare.internal.search;

import java.util.Random;

public class RandomBias implements InductiveBias{

	int[] ints;
	int noOfHornClauses;
	@Override
	public void ChooseSeedHornClause() {
		// TODO Auto-generated method stub
		//it was 5 earlier
		ints = new Random().ints(1,noOfHornClauses).distinct().limit(1).toArray();		
	}

	public void setNoHornClause(int count){
		this.noOfHornClauses = count;
	}

	public int[] getInts() {
		return ints;
	}

	public void setInts(int[] ints) {
		this.ints = ints;
	}

	public int getNoOfHornClauses() {
		return noOfHornClauses;
	}

	public void setNoOfHornClauses(int noOfHornClauses) {
		this.noOfHornClauses = noOfHornClauses;
	}
	
	
}
