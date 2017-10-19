package edu.utexas.seal.plugins.crystal.internal;

import java.util.HashSet;
import java.util.Set;

public class DominateLE {
	public Set<SourceCodeRange> ranges;
	
	public DominateLE(){
		ranges = new HashSet<SourceCodeRange>();
	}
	
	public void add(SourceCodeRange range) {
		this.ranges.add(range);
	}
	
	public DominateLE(DominateLE le){
		this.ranges = new HashSet<SourceCodeRange>();
		ranges.addAll(le.ranges);
	}

	public static DominateLE bottom(){
		return new DominateLE();
	}
	
	public boolean equals(Object obj){
		if (obj == null)
			return false;
		if (!(obj instanceof DominateLE))
			return false;
		DominateLE other = (DominateLE) obj;
		if (this.ranges.size() == other.ranges.size()
				&& this.ranges.containsAll(other.ranges))
			return true;
		else {
			return false;
		}
	}
	
	public int hashCode(){
		return this.ranges.hashCode();
	}
	
	public String toString(){
		return this.ranges.toString();
	}
}
