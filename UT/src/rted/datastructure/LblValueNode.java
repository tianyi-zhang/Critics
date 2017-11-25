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
package rted.datastructure;

import ut.seal.plugins.utils.UTStr;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

/**
 * @author Myoungkyu Song
 * @date Nov 11, 2013
 * @since J2SE-1.5 (Java SE 7 [1.7.0_40])
 */
public class LblValueNode {
	Object	o;

	public void setNode(Object o) {
		this.o = o;
	}

	public String toString() {
		Node node = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sblabel = new StringBuilder();
		if (o == null)
			return null;
		if (o instanceof Node) {
			node = (Node) o;
			Object fValue = node.getValue();
			Object fLabel = node.getLabel();
			sblabel.append("(");
			if (fLabel == null) {
				sblabel.append("none)");
			} else {
				String str = fLabel.toString();
				str = str.replaceAll("\\{", "[");
				str = str.replaceAll("\\}", "]");
				sblabel.append(str).append(')');
			}
			sb.append("(");
			if (fValue == null || fValue.equals("")) {
				sb.append("none)");
			} else {
				if (node.getValuePartialGen() != null) {
					sb.append(node.getValuePartialGen().replace(":", "`:`"). //
							replaceAll("\\{", "["). //
							replaceAll("\\}", "]")).append(')');
				} else {
					sb.append(fValue.toString().replace(":", "`:`"). //
							replaceAll("\\{", "["). //
							replaceAll("\\}", "]")).append(')');
				}
			}
		} else {
			sb.append(o.toString());
		}
		return sblabel.toString() + sb.toString();
	}

	public String toStringAllGen() {
		Node node = null;
		StringBuilder sb = new StringBuilder();
		StringBuilder sblabel = new StringBuilder();
		if (o == null)
			return null;
		if (o instanceof Node) {
			node = (Node) o;
			Object fValue = node.getValue();
			Object fValueNew = node.getValueNew();
			Object fLabel = node.getLabel();
			sblabel.append("(");
			if (fLabel == null) {
				sblabel.append("none)");
			} else {
				String str = fLabel.toString();
				str = str.replaceAll("\\{", "[");
				str = str.replaceAll("\\}", "]");
				// for (int i = 0; i < 10; i++) {
				// str += str;
				// }
				sblabel.append(str).append(')');
			}
			sb.append("(");
			if (fValue == null || fValue.equals("")) {
				sb.append("none)");
			} else {
				if (fValueNew != null) {
					String valNew = fValueNew.toString();
					String valNewWoQuot = UTStr.removeStrInQuotMark(valNew);
					String valNewUpdated = valNewWoQuot.replace(":", "`:`");
					sb.append(valNewUpdated.replaceAll("\\{", "["). //
							replaceAll("\\}", "]")).append(')');
				} else {
					String val = fValue.toString();
					String valWoQuot = UTStr.removeStrInQuotMark(val);
					String valUpdated = valWoQuot.replace(":", "`:`");
					sb.append(valUpdated.replaceAll("\\{", "["). //
							replaceAll("\\}", "]")).append(')');
				}
			}
		} else {
			sb.append(o.toString());
		}
		return sblabel.toString() + sb.toString();
	}

	public String getLabel() {
		Node node = null;
		StringBuilder sb = new StringBuilder();
		if (o == null)
			return null;
		if (o instanceof Node) {
			node = (Node) o;
			// Object fValue = node.getValue();
			// Object fValueNew = node.getValueNew();
			Object fLabel = node.getLabel();
			sb.append("(");
			if (fLabel == null) {
				sb.append("none)");
			} else {
				String str = fLabel.toString();
				str = str.replaceAll("\\{", "[");
				str = str.replaceAll("\\}", "]");
				sb.append(str).append(')');
			}
			// sb.append("(");
			// if (fValue == null || fValue.equals("")) {
			// sb.append("none)");
			// } else {
			// if (fValueNew != null) {
			// String valNew = fValueNew.toString();
			// String valNewWoQuot = UTStr.removeStrInQuotMark(valNew);
			// String valNewUpdated = valNewWoQuot.replace(":", "`:`");
			// sb.append(valNewUpdated.replaceAll("\\{", "["). //
			// replaceAll("\\}", "]")).append(')');
			// } else {
			// String val = fValue.toString();
			// String valWoQuot = UTStr.removeStrInQuotMark(val);
			// String valUpdated = valWoQuot.replace(":", "`:`");
			// sb.append(valUpdated.replaceAll("\\{", "["). //
			// replaceAll("\\}", "]")).append(')');
			// }
			// }
		} else {
			sb.append(o.toString());
		}
		return sb.toString();
	}

}
