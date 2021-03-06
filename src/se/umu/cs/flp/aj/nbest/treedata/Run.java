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

import se.umu.cs.flp.aj.nbest.semiring.Weight;

public class Run implements Comparable<Run> {

	private Tree1 tree;

	private Weight weight;

	public Run(Tree1 tree, Weight weight) {
		this.tree = tree;
		this.weight = weight;
	}

	public Tree1 getTree() {
		return this.tree;
	}

	public Weight getWeight() {
		return this.weight;
	}

	@Override
	public int compareTo(Run o) {
		return this.getWeight().compareTo(o.getWeight());
	}

}
