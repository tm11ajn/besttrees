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

package se.umu.cs.flp.aj.nbest.helpers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map.Entry;

import se.umu.cs.flp.aj.heap.BinaryHeap;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Configuration;
import se.umu.cs.flp.aj.nbest.treedata.RuleKeeper;
import se.umu.cs.flp.aj.nbest.treedata.TreeKeeper2;
import se.umu.cs.flp.aj.nbest.util.LadderQueue;
import se.umu.cs.flp.aj.nbest.util.ResultConnector;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;

public class RuleQueue {
	
	private ResultConnector resultConnector;
	private BinaryHeap<RuleKeeper, Weight> queue;
	private BinaryHeap<RuleKeeper, Weight>.Node[] queueElems;
	int limit;

	@SuppressWarnings("unchecked")
	public RuleQueue(int limit, WTA wta) {
		this.queue = new BinaryHeap<>();
		this.queueElems = new BinaryHeap.Node[wta.getRuleCount()];
		this.limit = limit;
		this.resultConnector = new ResultConnector(wta, limit);
		initialiseLeafRuleElements(wta.getSourceRules());
	}
	
	/* Method that initialises the rule queue. It is only a separate method
	 * so that we can use makeheap to get a linear performance of the 
	 * initialisation instead of a n log n one. */
	private void initialiseLeafRuleElements(LinkedList<Rule> leafRules) {
		for (Rule r : leafRules) {
			RuleKeeper keeper = new RuleKeeper(r, limit);
			LadderQueue<TreeKeeper2> ladder = keeper.getLadderQueue();
			Configuration<TreeKeeper2> startConfig = ladder.getStartConfig();
			resultConnector.makeConnections(startConfig);
			BinaryHeap<RuleKeeper, Weight>.Node elem = queue.createNode(keeper);
			queueElems[r.getID()] = elem;
			Configuration<TreeKeeper2> config = ladder.peek();
			keeper.setBestTree(r.apply(config));
			queue.insertUnordered(elem, keeper.getBestTree().getDeltaWeight());
		}
		
		queue.makeHeap();
	}
	
	/* Initialises a never before seen rule: gives it a queue element etc. 
	 * Finally adds it to the queue if it has a next config. */ 
	private void initialiseRuleElement(Rule r) {
		RuleKeeper keeper = new RuleKeeper(r, limit);
		LadderQueue<TreeKeeper2> ladder = keeper.getLadderQueue();
		Configuration<TreeKeeper2> startConfig = ladder.getStartConfig();
		resultConnector.makeConnections(startConfig);
		BinaryHeap<RuleKeeper, Weight>.Node elem = queue.createNode(keeper);
		queueElems[r.getID()] = elem;

		if (ladder.hasNext()) {
			Configuration<TreeKeeper2> config = ladder.peek();
			keeper.setBestTree(r.apply(config));
			queue.insert(elem, keeper.getBestTree().getDeltaWeight());
		}
	}

	public void expandWith(TreeKeeper2 newTree) {
		State resState = newTree.getResultingState();
		boolean unseenState = resultConnector.isUnseen(resState.getID());
		
		/* START TRICK */
		for (Entry<State, Integer> entry : newTree.getBestContext().getStateOccurrences().entrySet()) {
			State state = entry.getKey();
			int stateUsageCount = entry.getValue();
			int resSizeForState = resultConnector.getResultSize(state.getID());
			int coveredTrees = 1 + (resSizeForState - 1) * stateUsageCount;
			
//System.out.println("Covered trees: " + coveredTrees);
//System.out.println("Limit: " + limit);

			if (coveredTrees >= limit) {
				state.markAsSaturated();
System.out.println("Tricket initieras");
			}
		}
		/* SLUT TRICK */
		
		/* Create rulekeepers for the rules that we have not yet seen
		 * and add them to the queue as well. */
		if (unseenState && !resState.isSaturated()) {
			for (Rule r : resState.getOutgoing()) {
				if (queueElems[r.getID()] == null) {
					initialiseRuleElement(r);
				} 
			}
		}
		
		if (!resState.isSaturated()) {
				
		/* Have the result connector propagate the result to the connected 
		 * configs, and get info on which rulekeepers should be updated 
		 * based on this propagation. */
		ArrayList<Integer> toUpdate = 
				resultConnector.addResult(resState.getID(), newTree);
		
		/* Then update the corresponding rulekeepers. */
		for (Integer index : toUpdate) {
			BinaryHeap<RuleKeeper, Weight>.Node elem = queueElems[index];
			RuleKeeper rk = elem.getObject();
			Rule rule = rk.getRule();
			TreeKeeper2 currentBest = rk.getBestTree();
			LadderQueue<TreeKeeper2> ladder = rk.getLadderQueue();
			Configuration<TreeKeeper2> config = ladder.peek();
			
			if (elem.isEnqueued()) {
				Weight currentWeight = currentBest.getRunWeight();
				Weight newWeight = config.getWeight().mult(rule.getWeight());
				
				if (currentWeight.compareTo(newWeight) > 0) {
					TreeKeeper2 newBest = rule.apply(config);
					rk.setBestTree(newBest);
					queue.decreaseWeight(elem, newBest.getDeltaWeight());
				} 
			} else {
				TreeKeeper2 newBest = rule.apply(config);
				rk.setBestTree(newBest);
				queue.insert(elem, newBest.getDeltaWeight());
			}
		}
		}
	}
	
	/* Returns the next tree in the queue. The configuration corresponding to
	 * the dequeued tree is used as a base for finding the next possible 
	 * configurations for that particular rule. */
	public TreeKeeper2 nextTree() {
		BinaryHeap<RuleKeeper, Weight>.Node elem;
		RuleKeeper ruleKeeper;
		TreeKeeper2 nextTree;
		
		/* Dequeue the next tree. */
//		do {
		elem = queue.dequeue();
		ruleKeeper = elem.getObject();
		nextTree = ruleKeeper.getBestTree();
//		} while (nextTree.getResultingState().isSaturated());
		
		ruleKeeper.setBestTree(null);
		LadderQueue<TreeKeeper2> ladder = ruleKeeper.getLadderQueue();
		Configuration<TreeKeeper2> config = ladder.dequeue();
		
		/* Add the new configs to the process. */
		ArrayList<Configuration<TreeKeeper2>> nextConfigs = 
				ladder.getNextConfigs(config);
		for (Configuration<TreeKeeper2> next : nextConfigs) {
			resultConnector.makeConnections(next);
		}
		
		/* START lite TRICK, anv�nder isSaturated f�r att kolla om vi faktiskt beh�ver 
		 * k�a p� n�sta tr�d f�r denna regel igen */
		State resState = ruleKeeper.getRule().getResultingState();
//		if (resState.isSaturated()) {
//System.out.println("SATURATEEEEEEEEEEEEED");			
//		}
		
		/* Re-queue the rulekeeper if it has another element in its ladder. */
		if (!resState.isSaturated() && 
				ladder.hasNext()) {
				Configuration<TreeKeeper2> c = ruleKeeper.getLadderQueue().peek();
				TreeKeeper2 newBest = ruleKeeper.getRule().apply(c);				
				ruleKeeper.setBestTree(newBest);
				queue.insert(elem, newBest.getDeltaWeight());
		}
		
		/* SLUT lite TRICK */
		
		
		return nextTree;
	}

	public boolean isEmpty() {
		return queue.empty();
	}

	public int size() {
		return queue.size();
	}

}