/*
 * Copyright 2018 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Umeå university
 *
 * This file is part of BestTrees.
 *
 * BestTrees is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BestTrees is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BestTrees.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.umu.cs.flp.aj.nbest.treedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;

public class TreeKeeper2 implements Comparable<TreeKeeper2> {

	private static HashMap<State, Weight> smallestCompletions;

//	private static HashMap<Node, LinkedHashMap<State, State>> optimalStates =
//			new HashMap<>();
//	private static HashMap<Node, ArrayList<State>> optimalStates =
//			new HashMap<>();
//	private static HashMap<Node, State> optimalState = new HashMap<>();
	private static HashMap<Node, HashMap<State, Weight>> optWeights =
			new HashMap<>();
//	private static HashMap<Node, Weight> smallestWeight =
//			new HashMap<>();
//	private static HashMap<Node, Weight> deltaWeight = new HashMap<>();
//	private static HashMap<Node, Weight> deltaStateWeight = new HashMap<>();


	private Node tree;
	private Weight runWeight;
	private State resultingState;
//	private boolean queueable;

	/* For testing */
	private ArrayList<Rule> usedRules;

//	// Remove this constructor in the end
//	public TreeKeeper2(Symbol ruleLabel, Weight ruleWeight,
//			State resultingState,
//			ArrayList<TreeKeeper2> trees) {
//
//		this.tree = new Node(ruleLabel);
//		this.resultingState = resultingState;
//		this.queueable = false;
//
//		Weight temp = ruleWeight.mult(ruleWeight.one());
//
//		for (TreeKeeper2 currentTree : trees) {
//			tree.addChild(currentTree.getTree());
//			temp = temp.mult(currentTree.getRunWeight());
//		}
//
//		this.runWeight = temp;
//		addStateWeight(resultingState, temp);
//
////System.out.println(tree);
////System.out.println(resultingState);
////System.out.println(resultingState.isFinal());
////System.out.println(runWeight);
////System.exit(1);
//	}

	public TreeKeeper2(Node tree, Weight treeWeight,
			State resultingState, ArrayList<Rule> usedRules) {
		this.tree = tree;
		this.runWeight = treeWeight.duplicate();
		this.resultingState = resultingState;
//		this.queueable = false;
		addStateWeight(resultingState, treeWeight);
//System.out.println(tree);
//System.out.println(resultingState);
//System.out.println(resultingState.isFinal());
//System.out.println(runWeight);
//System.exit(1);
		this.usedRules = usedRules;
	}

	public ArrayList<Rule> getUsedRules() {
		return usedRules;
	}

	public static void init(HashMap<State, Weight> smallestCompletionWeights) {
		smallestCompletions = smallestCompletionWeights;
	}

	public Node getTree() {
		return tree;
	}

	public Weight getRunWeight() {
		return this.runWeight;
	}

//	public Weight getOptWeight() {
//		return optWeights.get(tree).get(resultingState);
//	}

	public State getResultingState() {
		return resultingState;
	}

//	public LinkedHashMap<State, State> getOptimalStates() {
//		return optimalStates.get(tree);
//	}

//	public Weight getOptimalWeight(State s) {
//		return optWeights.get(tree).get(s);
//	}

//	private void addStateWeight(State s, Weight w) {
//
//		if (!optWeights.containsKey(tree)) {
//			optWeights.put(tree, new HashMap<>());
//		}
//
//		HashMap<State, Weight> treeWeights = optWeights.get(tree);
//
////		if (!treeWeights.containsKey(s)) {
////			this.queueable = true;
////			treeWeights.put(s, w);
////		if () {
////			this.queueable = true;
//
////System.out.println("current state=" + s);
//
//			Weight newDeltaWeight = null;
//
////			if (smallestCompletions.containsKey(s)) {
////				newDeltaWeight = w.mult(smallestCompletions.get(s));
////			} else {
////				newDeltaWeight = w.zero();
////			}
//
//			newDeltaWeight = w.mult(smallestCompletions.get(s));
////			newDeltaWeight = w.mult(smallestCompletions.get(resultingState));
//
////			if (optimalState.get(tree) == null) {
//			if (deltaWeight.get(tree) == null) {
////			if (deltaStateWeight.get(tree) == null) {
////				LinkedHashMap<State, State> newMap = new LinkedHashMap<>();
////				newMap.put(s, s);
////				optimalStates.put(tree, newMap);
////				ArrayList<State> newList = new ArrayList<>();
////				newList.add(s);
////				optimalStates.put(tree, newList);
////				optimalState.put(tree, s);
//				smallestWeight.put(tree, w);
//				deltaWeight.put(tree, newDeltaWeight);
////				deltaStateWeight.put(tree, newDeltaWeight);
//			} else {
//				Weight oldDeltaWeight = deltaWeight.get(tree); //getDeltaWeight();
////				Weight oldDeltaWeight = deltaStateWeight.get(tree); //getDeltaWeight();
//
//				if (newDeltaWeight.compareTo(oldDeltaWeight) == -1) {
////					LinkedHashMap<State, State> newMap = new LinkedHashMap<>();
////					newMap.put(s, s);
////					optimalStates.put(tree, newMap);
////					ArrayList<State> newList = new ArrayList<>();
////					newList.add(s);
////					optimalStates.put(tree, newList);
////					optimalState.put(tree, s);
//					smallestWeight.put(tree, w);
//					deltaWeight.put(tree, newDeltaWeight);
////					deltaStateWeight.put(tree, newDeltaWeight);
//				} //else if (newDeltaWeight.compareTo(oldDeltaWeight) == 0) {
////					optimalStates.get(tree).put(s, s);
////					optimalStates.get(tree).add(s);
////				}
//			}
////		}
//	}

	private void addStateWeight(State s, Weight w) {

		if (!optWeights.containsKey(tree)) {
			optWeights.put(tree, new HashMap<>());
		}

		HashMap<State, Weight> treeWeights = optWeights.get(tree);

		if (!treeWeights.containsKey(s) || treeWeights.get(s).compareTo(w) > 0) {
//			this.queueable = true;
			treeWeights.put(s, w);

//			Weight newDeltaWeight = w.mult(smallestCompletions.get(s));

//			if (optimalState.get(tree) == null) {
//				LinkedHashMap<State, State> newMap = new LinkedHashMap<>();
//				newMap.put(s, s);
//				optimalStates.put(tree, newMap);
//				optimalState.put(tree, s);
//				smallestWeight.put(tree, w);
//			} else {
//				Weight oldDeltaWeight = getDeltaWeight();
//
//				if (newDeltaWeight.compareTo(oldDeltaWeight) == -1) {
////					LinkedHashMap<State, State> newMap = new LinkedHashMap<>();
////					newMap.put(s, s);
////					optimalStates.put(tree, newMap);
////					optimalState.put(tree, s);
////					smallestWeight.put(tree, w);
//				}
//				else if (newDeltaWeight.compareTo(oldDeltaWeight) == 0) {
//					optimalStates.get(tree).put(s, s);
//				}
//			}
		}
	}

//	public boolean isQueueable() {
//		return queueable;
//	}

//	public Weight getSmallestWeight() {
//		return smallestWeight.get(tree);
//	}

	public Weight getDeltaWeight() {

//		if (!smallestCompletions.containsKey(optimalState.get(tree))) {
//			return runWeight.zero();
//		}

//		return smallestWeight.get(tree).mult(
//				smallestCompletions.get(optimalState.get(tree)));

//		return deltaWeight.get(tree);

//		return smallestWeight.get(tree).mult(smallestCompletions.get(resultingState));
		return runWeight.mult(smallestCompletions.get(resultingState));
//		return optWeights.get(tree).get(resultingState).mult(smallestCompletions.get(resultingState));
//		return getOptWeight().mult(smallestCompletions.get(resultingState));
	}

	@Override
	public int hashCode() {
		return this.tree.hashCode();
	}

//	@Override
//	public String toString() {
//
//		String optStatesString = "";
//
//		if (optimalStates != null) {
//			optStatesString = optimalStates.toString();
//		}
//
//		return "Tree: " + tree + " RunWeight: " + runWeight +
//				" Optstates: " + optStatesString +
//				" Optstate: " + optimalState +
//				" Optweights: " + optWeights;
//	}

	@Override
	public String toString() {

//		String optStatesString = "";

//		if (optimalStates != null) {
//			optStatesString = optimalStates.toString();
//		}

		return "Tree: " + tree + " RunWeight: " + runWeight +
				" Delta weight: " + getDeltaWeight() +
				" Resulting state: " + resultingState;// +
//				" Optstates: " + optStatesString +
//				" Optstate: " + optimalState +
//				" Optweights: " + optWeights;
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof TreeKeeper2)) {
			return false;
		}

		TreeKeeper2 o = (TreeKeeper2) obj;
//		int weightComparison = runWeight.compareTo(o.runWeight);
//		int treeComparison = this.tree.compareTo(o.tree);
//
//		if (weightComparison == 0 && treeComparison == 0) {
//			return true;
//		}

//		if (runWeight.compareTo(o.runWeight) == 0 &&
//				this.tree.compareTo(o.tree) == 0) {
//			return true;
//		}

		if (this.compareTo(o) == 0) {
			return true;
		}

//		if (getOptWeight().compareTo(o.getOptWeight()) == 0 &&
//				this.tree.compareTo(o.tree) == 0) {
//			return true;
//		}

		return false;
	}

	@Override
	public int compareTo(TreeKeeper2 o) {
//		int weightComparison = runWeight.compareTo(o.runWeight);
		int weightComparison = this.getDeltaWeight().compareTo(o.getDeltaWeight());
//		int weightComparison = getOptWeight().compareTo(o.getOptWeight());

		if (weightComparison != 0) {
			return weightComparison;
		}

		weightComparison = runWeight.compareTo(o.runWeight);

		if (weightComparison != 0) {
			return weightComparison;
		}

		int treeComparison = this.tree.compareTo(o.tree);

		if (treeComparison != 0) {
			return treeComparison;
		}

		int stateComparison = this.resultingState.compareTo(o.resultingState);

//		if (stateComparison != 0) {
//			return stateComparison;
//		}

		return stateComparison;

//		int treeComparison = this.tree.compareTo(o.tree);
//
//		if (treeComparison == 0) {
//			return runWeight.compareTo(o.runWeight);
//		}
//
//		return treeComparison;
	}
}

