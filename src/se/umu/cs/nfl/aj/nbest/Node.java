package se.umu.cs.nfl.aj.nbest;

import java.util.ArrayList;
import java.util.List;

public class Node {

	private String label;

	// M^q(this), index with state
//	private HashMap<String, Double> minRunWeight = new HashMap<>();

	private Node parent;
	private List<Node> children = new ArrayList<Node>();
	private int nOfChildren;

	public Node(String label) {
		this.label = label;
		nOfChildren = 0;
		parent = null;
	}

	public Node(String label, List<Node> children) {
		this.label = label;
		this.children = children;
		nOfChildren = children.size();

		for (Node n : children) {
			n.setParent(this);
		}
	}

	public String getLabel() {
		return label;
	}

	public void addChild(Node child) {
		children.add(child);
		child.setParent(this);
		nOfChildren++;
	}

	public Node getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	public int getChildCount() {
		return nOfChildren;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getParent() {
		return parent;
	}

	public boolean isLeaf() {
		return nOfChildren == 0;
	}

	@Override
	public String toString() {

		String treeString = "" + label;

		if (!this.isLeaf()) {
			treeString += "[";

			for (int i = 0; i < nOfChildren; i++) {
				treeString += children.get(i);

				if (i != nOfChildren - 1) {
					treeString += ", ";
				}
			}

			treeString += "]";
		}

		return treeString;
	}
}
