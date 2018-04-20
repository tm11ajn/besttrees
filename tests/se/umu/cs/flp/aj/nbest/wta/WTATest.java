package se.umu.cs.flp.aj.nbest.wta;

import static org.junit.Assert.*;

import org.junit.Test;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.TropicalSemiring;
import se.umu.cs.flp.aj.nbest.wta.Rule;
import se.umu.cs.flp.aj.nbest.wta.State;
import se.umu.cs.flp.aj.nbest.wta.Symbol;
import se.umu.cs.flp.aj.nbest.wta.WTA;
import se.umu.cs.flp.aj.nbest.wta.exceptions.DuplicateRuleException;
import se.umu.cs.flp.aj.nbest.wta.exceptions.SymbolUsageException;

public class WTATest {

	private Semiring semiring = new TropicalSemiring();
	private WTA wta = new WTA(semiring);
	private Symbol aSymb = new Symbol("a", 0);
	private Symbol fSymb = new Symbol("f", 2);
	private State state = new State("q0");
	private State finalState = new State("qf");


	@Test
	public void shouldBeEqualToStateWithSameLabel()
			throws SymbolUsageException {
		wta.addState("q0");
		assertEquals(state, wta.addState("q0"));
	}

	@Test
	public void shouldBeEqualToSymbolWithSameLabel()
			throws SymbolUsageException {
		wta.addSymbol("a", 0);
		assertEquals(aSymb, wta.addSymbol("a", 0));
	}

	@Test
	public void shouldCreateAndSetStateToFinal() throws SymbolUsageException {
		wta.setFinalState("q0");
		assertTrue(wta.addState("q0").isFinal());
	}

	@Test
	public void shouldGetRulesByState()
			throws SymbolUsageException, DuplicateRuleException {

		State resState = wta.addState("q0");
		Symbol symbol = wta.addSymbol("a", 0);
		wta.getTransitionFunction().addRule(new Rule<Symbol>(symbol,
				semiring.one(), resState));
		Rule<Symbol> rule = wta.getTransitionFunction().
				getRulesByResultingState(state).get(0);
		assertEquals(aSymb, rule.getSymbol());
	}

	@Test
	public void shouldGetRulesBySymbol()
			throws SymbolUsageException, DuplicateRuleException {

		State resState = wta.addState("qf");
		Symbol symbol = wta.addSymbol("f", 2);
		wta.getTransitionFunction().addRule(new Rule<Symbol>(symbol,
				semiring.one(), resState));
		Rule<Symbol> rule = wta.getTransitionFunction().getRulesBySymbol(fSymb).get(0);
		assertEquals(finalState, rule.getResultingState());
	}

	@Test
	public void shouldHaveTwoRulesByResultingState()
			throws DuplicateRuleException {

		wta.getTransitionFunction().addRule(new Rule<Symbol>(new Symbol("f", 2),
				semiring.one(), new State("q0")));
		wta.getTransitionFunction().addRule(new Rule<Symbol>(new Symbol("g", 2),
				semiring.one(), new State("q0")));

		assertEquals(2, wta.getTransitionFunction().
				getRulesByResultingState(new State("q0")).size());
	}

	@Test
	public void shouldHaveTwoRulesBySymbol()
			throws SymbolUsageException, DuplicateRuleException {

		wta.addSymbol("f", 2);
		wta.getTransitionFunction().addRule(new Rule<Symbol>(new Symbol("f", 2),
				semiring.one(), new State("q0")));
		wta.getTransitionFunction().addRule(new Rule<Symbol>(new Symbol("f", 2),
				semiring.one(), new State("q1")));

		assertEquals(2, wta.getTransitionFunction().
				getRulesBySymbol(new Symbol("f", 2)).size());
	}

	@Test
	public void shouldGetFinalStates() throws Exception {
		wta.addState("qf");
		wta.addState("q0");
		wta.setFinalState("qf");
		assertEquals(finalState, wta.getFinalStates().get(0));
	}

}