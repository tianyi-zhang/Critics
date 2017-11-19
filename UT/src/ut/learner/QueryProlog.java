package ut.learner;

import org.jpl7.Atom;
import org.jpl7.Query;
import org.jpl7.Term;

public class QueryProlog {

	public void consultFactBase(){
		String t1 = "consult('facts.pl')";		
		System.out.println( t1 + (Query.hasSolution(t1) ? "succeeded" : "failed"));		
	}
	
}
