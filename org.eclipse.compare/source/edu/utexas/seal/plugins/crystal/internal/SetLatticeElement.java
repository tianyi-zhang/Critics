/**
 * Copyright (c) 2017, UCLA Software Engineering and Analysis Laboratory (SEAL)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */
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
