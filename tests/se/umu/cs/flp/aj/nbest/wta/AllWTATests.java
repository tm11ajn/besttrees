package se.umu.cs.flp.aj.nbest.wta;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ RankedAlphabetTest.class, RuleTest.class, SymbolTest.class,
		WeightTest.class, WTATest.class })
public class AllWTATests {

}
