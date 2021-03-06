/*
 * Copyright 2018 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Ume� university
 *
 * This file is part of Betty.
 *
 * Betty is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Betty is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.umu.cs.flp.aj.heap;

import java.util.ArrayList;


public class BinaryHeap<O, W extends Comparable<W>> {

	private ArrayList<Node> nodes;
	private boolean minHeap;
	public static final int firstPosition = 1;

	public class Node {
		private int index;
		private boolean enqueued;
		O object;
		W weight;

		public Node(O object, W weight) {
			this.index = -1;
			this.enqueued = false;
			this.object = object;
			this.weight = weight;
		}

		public O getObject() {
			return object;
		}

		public W getWeight() {
			return weight;
		}
		
		public void setWeight(W weight) {
			this.weight = weight;
		}
		
		public boolean isEnqueued() {
			return enqueued;
		}

		@Override
		public String toString() {
			return "Index: " + index + " Weight: " + weight +
					" Object: " + object.toString();
		}
	}

	public BinaryHeap(boolean isMinHeap) {
		this.nodes = new ArrayList<>();
		this.minHeap = isMinHeap;
		for (int i = 0; i < firstPosition; i++) {
			BinaryHeap<O, W>.Node dummyNode = new Node(null, null);
			dummyNode.index = i;
			this.nodes.add(dummyNode);
		}
	}

	public BinaryHeap() {
		this(true);
	}

	public int size() {
		return nodes.size() - firstPosition;
	}

	public boolean empty() {
		return this.size() == 0;
	}

	public Node peek() {
		Node min = nodes.get(firstPosition);
		return min;
	}

	public Node add(O object, W weight) {
		Node newNode = new Node(object, weight);
		int addedIndex = getLastIndex() + 1;
		nodes.add(newNode);
		newNode.index = addedIndex;
		newNode.enqueued = true;
		heapifyUp(addedIndex);
		return newNode;
	}
	
	public Node addUnordered(O object, W weight) {
		Node newNode = new Node(object, weight);
		int addedIndex = getLastIndex() + 1;
		nodes.add(newNode);
		newNode.index = addedIndex;
		newNode.enqueued = true;
		return newNode;
	}
	
	public Node createNode(O object) {
		Node newNode = new Node(object, null);
		return newNode;
	}
	
	public Node insertUnordered(Node node, W weight) {
		node.weight = weight;
		int addedIndex = getLastIndex() + 1;
		nodes.add(node);
		node.index = addedIndex;
		node.enqueued = true;
		return node;
	}
	
	public Node insert(Node node, W weight) {
		node.weight = weight;
		int addedIndex = getLastIndex() + 1;
		nodes.add(node);
		node.index = addedIndex;
		node.enqueued = true;
		heapifyUp(addedIndex);
		return node;
	}
	
	public void makeHeap() {
		for (int i = getParentIndexOf(getLastIndex()); i >= firstPosition; i--) {
			heapifyDown(i);
		}
	}

	public void decreaseWeight(Node node, W newWeight) {
		node.weight = newWeight;
		heapifyUp(node.index);
	}

	public Node dequeue() {
		int lastIndex = getLastIndex();
		Node first = nodes.get(firstPosition);
		Node last = nodes.get(lastIndex);
		nodes.remove(lastIndex);

		if (lastIndex != firstPosition) {
			nodes.set(firstPosition, last);
			last.index = firstPosition;
			heapifyDown(firstPosition);
		}

		first.enqueued = false;
		return first;
	}

	public void printHeap() {
		for (BinaryHeap<O, W>.Node n : nodes) {
			System.out.println(n.getObject()+  " : "+ n.index + " : " + n.getWeight());
		}
	}

	private int getLastIndex() {
		return nodes.size() - 1;
	}

	private int getParentIndexOf(int index) {
		int parentIndex;

		if (index%2 == 0) {
			parentIndex = index/2;
		} else {
			parentIndex = (index - 1)/2;
		}

		return parentIndex;
	}

	private int getLeftChildIndexOf(int index) {
		return index * 2;
	}

	private int getRightChildIndexOf(int index) {
		return index * 2 + 1;
	}

	private void heapifyUp(int initialIndex) {
		int currentIndex = initialIndex;
		int parentIndex = getParentIndexOf(currentIndex);
		boolean done = false;

		while ((parentIndex > firstPosition - 1) && !done) {
			Node parentNode = nodes.get(parentIndex);
			Node currentNode = nodes.get(currentIndex);

			if (compare(currentNode.weight, parentNode.weight) < 0) {
				swap(parentNode, currentNode);
				currentIndex = parentIndex;
				parentIndex = getParentIndexOf(currentIndex);
			} else {
				done = true;
			}
		}
	}

	private void heapifyDown(int initialIndex) {
		int currentIndex = initialIndex;
		int lastIndex = getLastIndex();
		boolean done = false;

		while (!done) {
			int leftIndex = getLeftChildIndexOf(currentIndex);
			int rightIndex = getRightChildIndexOf(currentIndex);

			if (leftIndex > lastIndex) {
				done = true;
			} else {

				if (rightIndex > lastIndex) {
					rightIndex = leftIndex;
				}

				Node left = nodes.get(leftIndex);
				Node right = nodes.get(rightIndex);
				Node current = nodes.get(currentIndex);

				int smallerIndex;
				Node smaller;

				if (compare(left.weight, right.weight) <= 0) {
					smallerIndex = leftIndex;
					smaller = left;
				} else {
					smallerIndex = rightIndex;
					smaller = right;
				}

				if (compare(smaller.weight, current.weight) < 0) {
					swap(current, smaller);
					currentIndex = smallerIndex;
				} else {
					done = true;
				}
			}
		}
	}

	private void swap(Node node1, Node node2) {
		int index1 = node1.index;

		node1.index = node2.index;
		node2.index = index1;

		nodes.set(index1, node2);
		nodes.set(node1.index, node1);
	}

	private int compare(W weight1, W weight2) {

		if (minHeap) {
			return weight1.compareTo(weight2);
		}

		return weight2.compareTo(weight1);
	}

}
