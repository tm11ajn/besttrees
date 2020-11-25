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

package se.umu.cs.flp.aj.nbest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import se.umu.cs.flp.aj.nbest.helpers.RuleQueue;
import se.umu.cs.flp.aj.nbest.treedata.Context;
import se.umu.cs.flp.aj.nbest.treedata.Node;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper2;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;


public class BestTrees2 {

	private static HashMap<Node, TreeKeeper2>
			outputtedTrees;
	private static HashMap<State, HashMap<Node, Node>> seenTrees;
	private static RuleQueue ruleQueue;

//	public static List<String> run(WTA wta, int N, boolean derivations) {
	public static List<String> run(WTA wta, int N, Context[] bestContexts, 
			boolean derivations, boolean trick) {
		
		TreeKeeper2.init(bestContexts);

		/* For result. */
		List<String> nBest = new ArrayList<String>(N);

		// T <- empty. 
		outputtedTrees = new HashMap<>();
		seenTrees = new HashMap<>();
		
		// K <- empty
		/* Initialises implicitly by enqueuing rules without states. */
		ruleQueue = new RuleQueue(N, wta, trick);

		// i <- 0
		int foundTrees = 0;

		// while i < N and K nonempty do
		while (foundTrees < N && !ruleQueue.isEmpty()) {
			
			// t <- dequeue(K)
			TreeKeeper2 currentTree = ruleQueue.nextTree();

			/* If there are no more derivations with a non-infinity weight,
			 * we end the search. */
			if (currentTree.getDeltaWeight().compareTo(
					wta.getSemiring().zero()) >= 0) {
				break;
			}

			/* Blocks duplicates unless we are solving the N best derivations/runs 
			 * problem */
			if (derivations || !outputtedTrees.containsKey(currentTree.getTree())) {

				if (currentTree.getResultingState().isFinal()) {
					String outputString = "";

					if (wta.isGrammar()) {
						outputString = currentTree.getTree().toRTGString();
					} else {
						outputString = currentTree.getTree().toWTAString();
					}

					outputString += (" # " +
								currentTree.getRunWeight().toString());

					// output(t)
					nBest.add(outputString);
					currentTree.markAsOutputted();
					if (!derivations) {
						outputtedTrees.put(currentTree.getTree(), null);
					}
					foundTrees++;
				}
			}

			HashMap<Node, Node> temp = null;
			if (!derivations) {
				temp = seenTrees.get(currentTree.getResultingState());

				if (temp == null) {
					temp = new HashMap<>();
					seenTrees.put(currentTree.getResultingState(), temp);
				}
			}

			/* Blocks duplicates from being added to the state result lists 
			 * unless we are solving the best derivations/runs */
			if (derivations || !temp.containsKey(currentTree.getTree())) {

				// Expand search space with current tree
				if (foundTrees < N && !currentTree.getResultingState().isSaturated()) {
					ruleQueue.expandWith(currentTree);
				}

				if (!derivations) {
					temp.put(currentTree.getTree(), null);
				}
			}			
		}

		return nBest;
	}

}
