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

package se.umu.cs.flp.aj.knuth;

import java.util.Collection;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;

public class PriorityMap<O, P extends Comparable<P>> {

	private HashMap<O, Entry<O, P>> mapping;
	private PriorityQueue<Entry<O, P>> queue;

	public class Entry<OE, PE extends Comparable<PE>> implements Comparable<Entry<OE, PE>> {
		private OE object;
		private PE priority;

		public Entry(OE object, PE priority) {
			this.object = object;
			this.priority = priority;
		}

		public OE getKey() {
			return object;
		}

		public PE value() {
			return priority;
		}

		@Override
		public int compareTo(Entry<OE, PE> e) {
			return this.priority.compareTo(e.priority);
		}
	}

	public PriorityMap() {
		this.mapping = new HashMap<>();
		this.queue = new PriorityQueue<>();
	}

	public P getPriority(O object) {

		if (!mapping.containsKey(object)) {
			return null;
		}

		return mapping.get(object).priority;
	}

	public boolean decreasePriority(O object, P newPriority) {

		if (!mapping.containsKey(object)) {
			return false;
		}

		Entry<O, P> entry = mapping.get(object);
		P oldPriority = mapping.get(object).priority;

		if (oldPriority.compareTo(newPriority) < 0) {
			return false;
		}

		queue.remove(entry); // TODO make this better
		entry.priority = newPriority;
		queue.add(entry);

		return true;
	}

	public void add(O object, P priority) {
		Entry<O, P> newEntry = new Entry<>(object, priority);
		this.mapping.put(object, newEntry);
		queue.add(newEntry);
	}

	public O peek() {
		return queue.peek().object;
	}

	public O poll() {
		mapping.remove(peek());
		return queue.poll().object;
	}

	public Set<O> keySet() {
		return mapping.keySet();
	}

	public Collection<Entry<O, P>> values() {
		return mapping.values();
	}

}
