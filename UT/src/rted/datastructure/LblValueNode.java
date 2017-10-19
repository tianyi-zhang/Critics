/*
 * @(#) TNode.java
 *
 * Copyright 2013 The Software Evolution and Analysis Laboratory Lab 
 * Electrical and Computer Engineering, The University of Texas at Austin
 * ACES 5.118, C5000, 201 E 24th Street, Austin, TX 78712-0240
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
