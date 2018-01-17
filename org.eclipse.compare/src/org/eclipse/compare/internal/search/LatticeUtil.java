package org.eclipse.compare.internal.search;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import ut.learner.HornClause;
import ut.learner.Node;
import ut.learner.Predicate;
import ut.learner.PredicateValue;

public class LatticeUtil {
	
	static List<Predicate> testPredicate;
	static Node<HornClause> root ;
	static List<Predicate> parmPredicate = new ArrayList<Predicate>();
	static int lastCount;
	static BufferedWriter bw = null;
	static FileWriter fw = null;
	
	public static Node<HornClause> getChildren(HornClause clause){
//		lattice = new Lattice();
//		lattice.id = nodeId;
//		lattice.hornClause = clause;
//		testPredicate = new ArrayList<Predicate>();
//		Predicate a = new Predicate();
//		a.arguments = new ArrayList<PredicateValue>();
//		a.arguments.add(new PredicateValue("x"));
//		a.arguments.add(new PredicateValue("b"));
//		a.name = "P";
//		testPredicate.add(a);
//		a = new Predicate();
//		a.arguments = new ArrayList<PredicateValue>();
//		a.arguments.add(new PredicateValue("x"));
//		a.name = "Q";
//		testPredicate.add(a);
//		a = new Predicate();
//		a.arguments = new ArrayList<PredicateValue>();
//		a.arguments.add(new PredicateValue("x"));
//		a.name = "G";
//		testPredicate.add(a);
//		a = new Predicate();
//		a.arguments = new ArrayList<PredicateValue>();
//		a.arguments.add(new PredicateValue("x"));
//		a.name = "R";
//		testPredicate.add(a);
//		
//		a = new Predicate();
//		a.arguments = new ArrayList<PredicateValue>();
//		a.arguments.add(new PredicateValue("x"));
//		a.name = "S";
//		testPredicate.add(a);
		root = new Node<>(clause);
		
		int count=clause.predicates.size()-1;
//		lastCount = count-4;
		System.out.println("Size of predicates "+clause.predicates.size());
		System.out.println("Last Count "+lastCount);
		
		
		combinations(root,clause.predicates, new ArrayList<Predicate>(), 0, clause.predicates.size()-1, 0, count);
//		combinations(clause.predicates,new ArrayList<Predicate>(),0,clause.predicates.size()-1,0,count);
//		int count=testPredicate.size()-1;
//		combinations(root,testPredicate,new ArrayList<Predicate>(),0,count,0,count);
//		addAllChildren(root, testPredicate.size()-1);
		addAllChildren(root, count-1);
		printLatticeToFile();
		
		printLattice(root, " ");
		try{
		      if(bw!=null)
			 bw.close();
		   }catch(Exception ex){
		       System.out.println("Error in closing the BufferedWriter"+ex);
		    }
		
		return root;		
	}
	
	public static void printLatticeToFile(){
		File file = new File("/home/whirlwind/dev/Critics/lattice.txt");
		 if (!file.exists()) {
		     try {
				file.createNewFile();					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		 }
		 try {
			fw = new FileWriter(file);
			 bw = new BufferedWriter(fw);				 
	         System.out.println("File written Successfully");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		
	}
	
	public static void printLattice(Node<HornClause> node,String appender){
		try {
			System.out.println(appender+node.getData().getClauseAsString());
			bw.write(appender+node.getData().getClauseAsString());
			bw.newLine();
			node.getChildren().forEach(each-> printLattice(each, appender+appender));			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static Node<HornClause> getClauseAtIndex(Node<HornClause> node,int index){		
		return node.getChildren().get(index);
	}
	
	public static BigInteger combinationNumber(int total, int choice){
		BigInteger ret = BigInteger.ONE;
		for(int k=0;k<choice;k++){
			ret = ret.multiply(BigInteger.valueOf(total-k)).divide(BigInteger.valueOf(k+1));
		}
		return ret;
	}
	
	static void addAllChildren(Node<HornClause> currentNode,int count){
		if(count==lastCount){
			return;
		} else{
			for(int i=0;i<currentNode.getChildren().size();i++){
				parmPredicate.clear();
				combinations(currentNode.getChildren().get(i), currentNode.getChildren().get(i).getData().predicates,new ArrayList<Predicate>() , 0, count, 0, count);
				addAllChildren(currentNode.getChildren().get(i), count-1);
			}
//			currentNode.getChildren().forEach(each -> combinations(each,((Node<HornClause>) each).getData().predicates,new ArrayList<Predicate>(),0,count-1,0,count-1));
		}
	}
	
	 static void combinations(Object currentNode,List<Predicate> predicates, List<Predicate> child, int start, int end,
	            int index, int r) 
    {
        if (index == r) 
        {
//            for (int j = 0; j < r; j++)
//                System.out.print(child.get(j).printPredicate() + ".");
//            System.out.println();
            HornClause clause = new HornClause();            
            clause.predicates.addAll(child.subList(0, r));
            ((Node<HornClause>) currentNode).addChild(new Node<HornClause>(clause));
            return;
            
        }
 
        for (int i = start; i <= end && ((end - i + 1) >= (r - index)); i++) 
        {
        	child.add(index,predicates.get(i));
            combinations(currentNode,predicates, child, i + 1, end, index + 1, r);
        }
		
    }
	 
	 static void combinationsAsArray(List<List<String>> chosenPredicates, List<String> predicates, List<String> child, int start, int end,
	            int index, int r) 
 {
     if (index == r) 
     {
    	 List<String> newChild = new ArrayList<String>();
         for (int j = 0; j < r; j++){
             System.out.print(child.get(j) + ",");
             newChild.add(child.get(j));
         }
         System.out.println();
         
//         newChild.addAll(child);
         chosenPredicates.add(newChild);
     
//         HornClause clause = new HornClause();            
//         clause.predicates.addAll(child.subList(0, r));
//         ((Node<HornClause>) currentNode).addChild(new Node<HornClause>(clause));
         return;
         
     }

     for (int i = start; i <= end && ((end - i + 1) >= (r - index)); i++) 
     {
     	child.add(index,predicates.get(i));
         combinationsAsArray(chosenPredicates,predicates, child, i + 1, end, index + 1, r);
     }
		
 }
	  
	 static void testCombinationArray(){
		 List<String> testArray = new ArrayList<String>();
		 List<String> child = new ArrayList<String>();
		 
		 testArray.add("p");
		 testArray.add("q");
		 testArray.add("r");
		 
		 
//		 combinationsAsArray(testArray, child, 0, 2, 0, 2);
		 
	 }
	 
}
