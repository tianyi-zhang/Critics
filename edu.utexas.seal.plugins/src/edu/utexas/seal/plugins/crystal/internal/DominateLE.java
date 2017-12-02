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
