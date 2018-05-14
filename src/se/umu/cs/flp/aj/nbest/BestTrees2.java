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

package se.umu.cs.flp.aj.nbest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import se.umu.cs.flp.aj.nbest.helpers.RuleQueue;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper2;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.WTA;


public class BestTrees2 {

	private static HashMap<TreeKeeper2<Symbol>, TreeKeeper2<Symbol>> exploredTrees; // T
	private static RuleQueue<Symbol> ruleQueue; // K

	public static void setSmallestCompletions(
			HashMap<State, Weight> smallestCompletions) {
		TreeKeeper2.init(smallestCompletions);
	}

	public static List<String> run(WTA wta, int N) {

		/* For result. */
		List<String> nBest = new ArrayList<String>();

		// T <- empty. K <- empty
		exploredTrees = new HashMap<>();
		ruleQueue = new RuleQueue<>(wta.getTransitionFunction());

		// i <- 0
		int counter = 0;

		// while i < N and K nonempty do
		while (counter < N && !ruleQueue.isEmpty()) {

			// t <- dequeue(K)
			TreeKeeper2<Symbol> currentTree = ruleQueue.nextTree();

			if (!exploredTrees.containsKey(currentTree)) {

				if (currentTree.getSmallestWeight().equals(
					currentTree.getDeltaWeight())) {

					exploredTrees.put(currentTree, currentTree);

					// output(t)
					nBest.add(currentTree.getTree().toString() + " " +
							currentTree.getDeltaWeight().toString());

					counter++;
				}
			}

			if (counter < N) {
				ruleQueue.addTree(currentTree);
			}
		}

		return nBest;
	}

}
