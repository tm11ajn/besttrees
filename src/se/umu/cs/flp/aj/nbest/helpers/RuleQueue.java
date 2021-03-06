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

package se.umu.cs.flp.aj.nbest.helpers;

import java.util.ArrayList;
import java.util.LinkedList;

import se.umu.cs.flp.aj.heap.BinaryHeap;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Configuration;
import se.umu.cs.flp.aj.nbest.treedata.RuleKeeper;
import se.umu.cs.flp.aj.nbest.treedata.Tree;
import se.umu.cs.flp.aj.nbest.util.LadderQueue;
import se.umu.cs.flp.aj.nbest.util.ResultConnector;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.WTA;

public class RuleQueue {
	
	private ResultConnector resultConnector;
	private BinaryHeap<RuleKeeper, Tree> queue;
	private BinaryHeap<RuleKeeper, Tree>.Node[] queueElems;
	int limit;
	private boolean trick;

	@SuppressWarnings("unchecked")
	public RuleQueue(WTA wta, int limit, boolean trick) {
		this.queue = new BinaryHeap<>();
		this.queueElems = new BinaryHeap.Node[wta.getRuleCount()];
		this.limit = limit;
		this.trick = trick;
		this.resultConnector = new ResultConnector(wta, limit);
		initialiseLeafRuleElements(wta.getSourceRules());
	}

	
	/* Method that initialises the rule queue. It is only a separate method
	 * so that we can use makeheap to get a linear performance of the 
	 * initialisation instead of a n log n one. */
	private void initialiseLeafRuleElements(LinkedList<Rule> leafRules) {
		for (Rule r : leafRules) {
			RuleKeeper keeper = new RuleKeeper(r, limit);
			LadderQueue<Tree> ladder = keeper.getLadderQueue();
			Configuration<Tree> startConfig = ladder.getStartConfig();
			resultConnector.makeConnections(startConfig);
			BinaryHeap<RuleKeeper, Tree>.Node elem = queue.createNode(keeper);
			queueElems[r.getID()] = elem;
			Configuration<Tree> config = ladder.peek();
			keeper.setBestTree(r.apply(config));
			queue.insertUnordered(elem, keeper.getBestTree());
		}
		
		queue.makeHeap();
	}

	
	/* Initialises a never before seen rule: gives it a queue element etc. 
	 * Finally adds it to the queue if it has a next config. */ 
	private void initialiseRuleElement(Rule r) {
		RuleKeeper keeper = new RuleKeeper(r, limit);
		LadderQueue<Tree> ladder = keeper.getLadderQueue();
		Configuration<Tree> startConfig = ladder.getStartConfig();
		resultConnector.makeConnections(startConfig);
		BinaryHeap<RuleKeeper, Tree>.Node elem = queue.createNode(keeper);
		queueElems[r.getID()] = elem;

		if (ladder.hasNext()) {
			Configuration<Tree> config = ladder.peek();
			keeper.setBestTree(r.apply(config));
			queue.insert(elem, keeper.getBestTree());
		}
	}


	public void expandWith(Tree newTree) {
		State resState = newTree.getResultingState();
		boolean unseenState = resultConnector.isUnseen(resState.getID());
		
		/* Trick cannot be applied to the very first trees for a given state. */
		if (trick) {
			int resSizeForState = resultConnector.getResultSize(resState.getID());
			if (resSizeForState > 1 && resSizeForState * newTree.getBestContext().getfValue() >= limit) {
				resState.markAsSaturated();
			}
		}
		
		/* Create rulekeepers for the rules that we have not yet seen
		 * and add them to the queue as well. */
		if (unseenState && !resState.isSaturated()) {
			for (Rule r : resState.getOutgoing()) {
				if (queueElems[r.getID()] == null) {
					initialiseRuleElement(r);
				} 
			}
		}

		
		if (resState.isSaturated()) {
			return;
		}
				
		/* Have the result connector propagate the result to the connected 
		 * configs, and get info on which rulekeepers should be updated 
		 * based on this propagation. */
		ArrayList<Integer> toUpdate = resultConnector.addResult(newTree);
		
		/* Then update the corresponding rulekeepers. */
		for (Integer index : toUpdate) {
			BinaryHeap<RuleKeeper, Tree>.Node elem = queueElems[index];
			RuleKeeper rk = elem.getObject();
			Rule rule = rk.getRule();
			Tree currentBest = rk.getBestTree();
			LadderQueue<Tree> ladder = rk.getLadderQueue();
			Configuration<Tree> config = ladder.peek();
			
			if (elem.isEnqueued()) {
				Weight currentWeight = currentBest.getRunWeight();
				Weight newWeight = config.getWeight().mult(rule.getWeight());
				
				if (currentWeight.compareTo(newWeight) > 0) {
					Tree newBest = rule.apply(config);
					rk.setBestTree(newBest);
					queue.decreaseWeight(elem, newBest);
				} 
			} else {
				Tree newBest = rule.apply(config);
				rk.setBestTree(newBest);
				if (!rk.isLocked()) {
					queue.insert(elem, newBest);
				}
			}
		}
	}
	
	/* Returns the next tree in the queue. The configuration corresponding to
	 * the dequeued tree is used as a base for finding the next possible 
	 * configurations for that particular rule. */
	public Tree nextTree() {
		BinaryHeap<RuleKeeper, Tree>.Node elem;
		RuleKeeper ruleKeeper;
		Tree nextTree;
		
		/* Dequeue the next tree. */
		elem = queue.dequeue();
		ruleKeeper = elem.getObject();
		nextTree = ruleKeeper.getBestTree();
		ruleKeeper.setBestTree(null);
		Rule rule = ruleKeeper.getRule();
		State resState = rule.getResultingState();
		LadderQueue<Tree> ladder = ruleKeeper.getLadderQueue();
		boolean hasDequeued = ladder.hasDequeuedAtLeastOnce();
		if (!hasDequeued && !resState.isFinal() && rule.getNumberOfStates() > 0 && rule.getWeight().isOne()) {
			ruleKeeper.lock();
			for (Rule parentRule : rule.getTo().getOutgoing()) {
				if (parentRule.getResultingState() != resState) {
					if (queueElems[parentRule.getID()] == null) {
						initialiseRuleElement(parentRule);
					}
					RuleKeeper parentRK = queueElems[parentRule.getID()].getObject();
					parentRK.addToWaitingList(ruleKeeper);
				}
			}
		}
		
		Configuration<Tree> config = ladder.dequeue();		
		if (!hasDequeued) {
			for (RuleKeeper rk : ruleKeeper.unlock()) {
				if (rk.getLadderQueue().hasNext() && !queueElems[rk.getRule().getID()].isEnqueued()) {
					queue.insert(queueElems[rk.getRule().getID()], rk.getBestTree());
				}
			}
		}
		
		/* Add the new configs to the process. */
		ArrayList<Configuration<Tree>> nextConfigs = 
				ladder.getNextConfigs(config);
		for (Configuration<Tree> next : nextConfigs) {
			resultConnector.makeConnections(next);
		}
		
		/* Re-queue the rulekeeper if it has another element in its ladder. */
		if (!resState.isSaturated() && ladder.hasNext()) {
			Configuration<Tree> c = ruleKeeper.getLadderQueue().peek();
			Tree newBest = ruleKeeper.getRule().apply(c);
			ruleKeeper.setBestTree(newBest);
			if (!ruleKeeper.isLocked()) {
				queue.insert(elem, newBest);
			}
		}
		
		return nextTree;
	}

	public boolean isEmpty() {
		return queue.empty();
	}

	public int size() {
		return queue.size();
	}
	
	public void printFinalQueueSizes() {
		for (BinaryHeap<RuleKeeper, Tree>.Node node : queueElems) {
			if (node == null) {
				continue;
			}
			int size = node.getObject().getLadderQueue().size();
			Rule rule = node.getObject().getRule();
			System.out.println(rule + "\n has " + size + "elements");
		}
	}

}