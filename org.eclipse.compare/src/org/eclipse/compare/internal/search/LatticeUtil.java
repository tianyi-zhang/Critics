package org.eclipse.compare.internal.search;

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
	public static Node<HornClause> getChildren(HornClause clause){
//		lattice = new Lattice();
//		lattice.id = nodeId;
//		lattice.hornClause = clause;
		testPredicate = new ArrayList<Predicate>();
		Predicate a = new Predicate();
		a.arguments = new ArrayList<PredicateValue>();
		a.arguments.add(new PredicateValue("x"));
		a.arguments.add(new PredicateValue("b"));
		a.name = "P";
		testPredicate.add(a);
		a = new Predicate();
		a.arguments = new ArrayList<PredicateValue>();
		a.arguments.add(new PredicateValue("x"));
		a.name = "Q";
		testPredicate.add(a);
		a = new Predicate();
		a.arguments = new ArrayList<PredicateValue>();
		a.arguments.add(new PredicateValue("x"));
		a.name = "G";
		testPredicate.add(a);
		a = new Predicate();
		a.arguments = new ArrayList<PredicateValue>();
		a.arguments.add(new PredicateValue("x"));
		a.name = "R";
		testPredicate.add(a);
		
		a = new Predicate();
		a.arguments = new ArrayList<PredicateValue>();
		a.arguments.add(new PredicateValue("x"));
		a.name = "S";
		testPredicate.add(a);
		root = new Node<>(clause);
		
		int count=clause.predicates.size()-1;
		lastCount = count-4;
		System.out.println("Size of predicates "+clause.predicates.size());
		
		
		combinations(root,clause.predicates, new ArrayList<Predicate>(), 0, clause.predicates.size()-1, 0, count);
//		combinations(clause.predicates,new ArrayList<Predicate>(),0,clause.predicates.size()-1,0,count);
//		int count=testPredicate.size()-1;
//		combinations(root,testPredicate,new ArrayList<Predicate>(),0,count,0,count);
//		addAllChildren(root, testPredicate.size()-1);
		addAllChildren(root, count-1);
		printLattice(root, " ");
		return root;		
	}
	
	public static void printLattice(Node<HornClause> node,String appender){
		System.out.println(appender+node.getData().getClauseAsString());
		node.getChildren().forEach(each-> printLattice(each, appender+appender));
	}
	
	static void addAllChildren(Node<HornClause> currentNode,int count){
		if(count==lastCount){
			return;
		} else{
			for(int i=0;i<currentNode.getChildren().size();i++){
				parmPredicate.clear();
//				clause.predicates.clear();
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
	 
	 
}
