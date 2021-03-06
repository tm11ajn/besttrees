/*
 * Copyright 2015 Anna Jonsson for the research group Foundations of Language
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

package se.umu.cs.flp.aj.nbest.treedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;

public class Tree1 implements Comparable<Tree1> {
	
	private static Context[] bestContexts;

	private Node node;
	private LinkedHashMap<State,State> optimalStates;
	private State optimalState;
	private HashMap<State, Weight> optWeights;
	private Weight smallestWeight;

	public Tree1(Node node, Semiring semiring) {
		this.node = node;
		this.optWeights = new HashMap<>();
		this.smallestWeight = semiring.zero();
	}

	public Tree1(Symbol ruleLabel, Weight ruleWeight,
			ArrayList<Tree1> trees) {

		node = new Node(ruleLabel);
		this.optWeights = new HashMap<>();

		int counter = 0;

		for (Tree1 currentTree : trees) {
			node.addChild(currentTree.getNode());

			if (counter == 0) {
				smallestWeight = currentTree.getSmallestWeight();
			} else {
				smallestWeight = smallestWeight.mult(
						currentTree.getSmallestWeight());
			}

			counter++;
		}

		if (counter == 0) {
			smallestWeight = ruleWeight;
		} else {
			smallestWeight = smallestWeight.mult(ruleWeight);
		}

	}

	public static void init(Context[] smallestCompletionWeights) {
		bestContexts = smallestCompletionWeights;
	}

	public Node getNode() {
		return node;
	}

	public LinkedHashMap<State, State> getOptimalStates() {
		return optimalStates;
	}

	public Weight getOptimalWeight(State s) {
		return optWeights.get(s);
	}

	public void addStateWeight(State s, Weight w) {

		if (this.optWeights.containsKey(s)) {
			if (this.optWeights.get(s).compareTo(w) == 1) {
				this.optWeights.put(s, w);
			}
		} else {
			this.optWeights.put(s, w);
		}

		if (w.compareTo(smallestWeight) == -1) {
			optimalStates = new LinkedHashMap<>();
			optimalStates.put(s, s);
			optimalState = s;
			smallestWeight = w;
		} else if (w.compareTo(smallestWeight) == 0) {
			optimalStates.put(s, s);
		}
	}

	public void addWeightsFrom(Tree1 t) {
		HashMap<State, Weight> map = t.optWeights;

		for (Entry<State, Weight> e : map.entrySet()) {
			addStateWeight(e.getKey(), e.getValue());
		}
	}

	public Weight getSmallestWeight() {
		return smallestWeight;
	}

	public Weight getDeltaWeight() {
//		return smallestWeight.mult(smallestCompletions.get(optimalState));
		return smallestWeight.mult(
				bestContexts[optimalState.getID()].getWeight());
//		return smallestWeight.mult(optimalState.getBestContext().getWeight());
	}

	@Override
	public int hashCode() {
		return this.node.hashCode();
	}

	@Override
	public String toString() {

		String optStatesString = "";

		if (optimalStates != null) {
			optStatesString = optimalStates.toString();
		}

		return node.toString() + optStatesString + optWeights.toString();
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof Tree1)) {
			return false;
		}

		Tree1 o = (Tree1) obj;

		int weightComparison = this.smallestWeight.compareTo(o.smallestWeight);
		int treeComparison = this.node.compareTo(o.node);

		if (weightComparison == 0 && treeComparison == 0) {
			return true;
		}

		return false;
	}

	@Override
	public int compareTo(Tree1 o) {
		int weightComparison = this.smallestWeight.compareTo(o.smallestWeight);

		if (weightComparison == 0) {
			return this.node.compareTo(o.node);
		}

		return weightComparison;
	}
}
