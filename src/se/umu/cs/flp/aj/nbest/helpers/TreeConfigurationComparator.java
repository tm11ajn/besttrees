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

import java.util.Comparator;

import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Tree;
import se.umu.cs.flp.aj.nbest.treedata.Configuration;

/* Compares treekeeper configurations */
public class TreeConfigurationComparator
			implements Comparator<Configuration<Tree>> {

	@Override
	public int compare(
			Configuration<Tree> config1,
			Configuration<Tree> config2) {

		Weight weight1 = null;
		Weight weight2 = null;

		int size1 = config1.getSize();
		int size2 = config2.getSize();

		if (size1 < size2) {
			return -1;
		} else if (size1 > size2) {
			return 1;
		}
		
		weight1 = config1.getWeight();
		weight2 = config2.getWeight();

		int comparison = weight1.compareTo(weight2);

		if (comparison !=  0) {
			return comparison;
		}
		
//		int sum1 = 0;
//		int sum2 = 0;
//		
//		for (int i = 0; i < size1; i++) {
//			sum1 += config1.getIndices()[i];
//			sum2 += config2.getIndices()[i];
//		}
//		
//		if (sum1 < sum2) {
//			return -1;
//		} else if (sum1 > sum2) {
//			return 1;
//		}

		return 0;
	}

}
