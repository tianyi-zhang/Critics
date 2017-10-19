package edu.utexas.seal.plugins.crystal.internal;

import java.util.HashSet;
import java.util.Set;

public class SetLatticeElement<LE> {
	protected final LE bot;
	protected final LE theDefault;
	protected Set<LE> elements;
	
	protected SetLatticeElement(LE b, LE d, Set<LE> e){
		bot = b;
		theDefault = d;
		elements = e;
	}
	
	public SetLatticeElement(SetLatticeElement<LE> original){
		this.bot = original.bot;
		this.theDefault = original.theDefault;
		this.elements = new HashSet<LE>();
		this.elements.addAll(original.elements);
	}
	
	public void add(LE e){
		try{
			elements.add(e);
		}catch (Exception exception){
			elements = new HashSet<LE>();
			elements.add(e);
		}
	}
	
	public void addAll(Set<LE> elements){
		this.elements.addAll(elements);
	}
	
	public boolean contains(LE e){
		if(elements == null) 
			return false;
		return elements.contains(e);
	}
	
	public boolean isEmpty(){
		return elements.isEmpty();
	}
	
	public void remove(SetLatticeElement<LE> set){
		for(LE e : set.getElements()){
			elements.remove(e);
		}
	}
	
	public void removeAll(Set<LE> set){
		elements.removeAll(set);
	}
	/**
	 * To get intersection between the two SetLatticeElement objects
	 * @param other
	 * @return
	 */
	public void retainAll(Set<DominateLE> elemSet){
		elements.retainAll(elemSet);
	}
	
	public boolean remove(LE e){
		return elements.remove(e);
	}
	
	public Set<LE> getElements(){
		return this.elements;
	}
	
	public String toString(){
		if(elements ==null){
			return "null";
		}
		boolean isFirst = true;
		StringBuffer buf = new StringBuffer("{");
		for(LE e : elements){
			if(!isFirst)
				buf.append(",");
			isFirst = false;
			buf.append(e.toString());
		}
		buf.append("}");
		return buf.toString();
	}
}
