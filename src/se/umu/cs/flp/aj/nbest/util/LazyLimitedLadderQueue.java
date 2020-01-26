/*
 * Copyright 2018 Anna Jonsson for the research group Foundations of Language
 * Processing, Department of Computing Science, Ume� university
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

package se.umu.cs.flp.aj.nbest.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import se.umu.cs.flp.aj.nbest.treedata.Configuration;

/*
 * TODO: Rename to LimitedLadderQueue or just LadderQueue
 */
public class LazyLimitedLadderQueue<V extends Comparable<V>> {
	
	private int id;
	private int rank;
	private int limit;
	private PriorityQueue<Configuration<V>> configQueue;
	private Object[] seenConfigurations;
	private int dequeueCounter;

	public LazyLimitedLadderQueue(int id, int rank,
			Comparator<Configuration<V>> comparator,
			int limit) {
		this.id = id;
		this.rank = rank;
		this.configQueue = new PriorityQueue<>(comparator);
		this.seenConfigurations = new Object[limit];
		this.limit = limit;
		this.dequeueCounter = 0;
	}
	
	private boolean isSeen(Configuration<V> config) {
		boolean answer = true;
		int[] indices = config.getIndices();
		Object[] currentList = seenConfigurations;
		for (int i = 0; i < rank; i++) {
			Object currentObject = currentList[indices[i]];
			
			if (currentObject == null) {
				answer = false;
				Object[] newList = new Object[limit];
				currentList[indices[i]] = newList;
				currentList = newList;
			} else {
				currentList = (Object[]) currentObject;
			}
		}
		
//System.out.println("isSeen " + config + "?" + answer);
		return answer;
	}

	public int getID() {
		return id;
	}

	public void insert(Configuration<V> config) {
		configQueue.add(config);
	}

	public boolean isEmpty() {
		return configQueue.size() == 0;
	}

	public boolean hasNext() {

		if (dequeueCounter >= limit) {
			return false;
		}

//		if (configQueue.isEmpty()) {
		if (configQueue.size() == 0) {
			return false;
		}

		return true;
	}

	public Configuration<V> dequeue() {
		Configuration<V> config = configQueue.poll();
		dequeueCounter++;
		return config;
	}

	public Configuration<V> peek() {
		return configQueue.peek();
	}

	public ArrayList<Configuration<V>> getNextConfigs(Configuration<V> config) {
		
		/* Return an empty list if we have already dequeued all we are allowed*/
		if (dequeueCounter >= limit) {
			return new ArrayList<>();
		}
		
		int[] newIndices;
		int[] indices = config.getIndices();
		Configuration<V> newConfig;
		ArrayList<Configuration<V>> newConfigs = new ArrayList<>();

		for (int i = 0; i < rank; i++) {
			newIndices = new int[rank];

			for (int j = 0; j < rank; j++) {
				if (i == j) {
					newIndices[j] = indices[j] + 1;
				} else {
					newIndices[j] = indices[j];
				}
			}

			newConfig = new Configuration<>(newIndices, rank, this);

			if (!isSeen(newConfig)) {
				newConfigs.add(newConfig);
			}
		}

		return newConfigs;
	}
	
	public Configuration<V> getStartConfig() {
		return new Configuration<>(new int[rank], rank, this); 
	}

	public int size() {
		return configQueue.size();
	}

//	public boolean hasReachedLimit() {
//		return dequeueCounter >= limit;
//	}
	
	public boolean hasNotDequeuedYet() {
		return dequeueCounter == 0;
	}

}

