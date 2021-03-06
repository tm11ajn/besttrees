package se.umu.cs.flp.aj.nbest.wta;

import static org.junit.Assert.*;

import org.junit.Test;

import se.umu.cs.flp.aj.nbest.semiring.Semiring;
import se.umu.cs.flp.aj.nbest.semiring.SemiringFactory;
import se.umu.cs.flp.aj.nbest.semiring.Weight;
import se.umu.cs.flp.aj.nbest.treedata.Configuration;
import se.umu.cs.flp.aj.nbest.treedata.Context;
import se.umu.cs.flp.aj.nbest.treedata.Node;
import se.umu.cs.flp.aj.nbest.treedata.Tree;
import se.umu.cs.flp.aj.nbest.util.Hypergraph;

public class RuleTest {

	SemiringFactory sf = new SemiringFactory();
	Semiring semiring = sf.getSemiring("tropical");
	Symbol fSym = new Symbol("f", 2);
	Symbol aSym = new Symbol("a", 0);
	Symbol bSym = new Symbol("b", 0);
	Symbol cSym = new Symbol("c", 0);
	Symbol dSym = new Symbol("d", 0);
	Symbol eSym = new Symbol("e", 1);
	Node fNode = new Node(fSym);
	Node aNode = new Node(aSym);
	Node bNode = new Node(bSym);
	Node cNode = new Node(cSym);
	Node dNode = new Node(dSym);
	Node eNode = new Node(eSym);

	Symbol q0Sym = new Symbol("q0", 0);
	Symbol q1Sym = new Symbol("q1", 0);
	Symbol q2Sym = new Symbol("q2", 0);
	Symbol q3Sym = new Symbol("q3", 0);
	Symbol qfSym = new Symbol("qf", 0);

	State resState = new State(qfSym);
	State q0 = new State(q0Sym);
	State q1 = new State(q1Sym);
	State q2 = new State(q2Sym);
	State q3 = new State(q3Sym);
	Context[] smallestCompletions = {
			new Context(semiring.one()),
			new Context(semiring.createWeight(2)),
			new Context(semiring.createWeight(1))};

	Rule rule = new Rule(fNode, semiring.createWeight(0.5), resState, q0, q1);
	Rule rule2 = new Rule(fNode, semiring.createWeight(0.5), resState);
	Tree[] tklist = new Tree[rule.getNumberOfStates()];
	Hypergraph<State, Rule> graph = new Hypergraph<>();

	Node n0 = new Node(q0Sym);
	Node n1 = new Node(q1Sym);
	
	Configuration<Tree> config;

	private void init() {
//		q0.setBestContext(new Context(semiring.one()));
//		q1.setBestContext(new Context(semiring.createWeight(2)));
//		q2.setBestContext(new Context(semiring.createWeight(1)));

		State.init(smallestCompletions);
		
		graph.addNode(resState);
		graph.addNode(q0);
		graph.addNode(q1);
		graph.addNode(q2);
		graph.addNode(q3);

		q0Sym.setNonterminal(true);
		q1Sym.setNonterminal(true);
		q2Sym.setNonterminal(true);
		q3Sym.setNonterminal(true);
		qfSym.setNonterminal(true);

		n0 = new Node(q0Sym);
		n1 = new Node(q1Sym);
	}

	private void init1a() {
		init();

		eNode.addChild(aNode);

		fNode.addChild(aNode);
		fNode.addChild(n0);
		fNode.addChild(cNode);
		fNode.addChild(n1);
		fNode.addChild(eNode);

		tklist = new Tree[rule.getNumberOfStates()];
		tklist[0] = new Tree(eNode, semiring.createWeight(1), q0);
		tklist[1] = new Tree(cNode, semiring.createWeight(2), q1);
		config = new Configuration<>(new int[tklist.length], tklist.length, null);
		config.setWeight(semiring.createWeight(3));
		config.setValues(tklist);
	}
	
	private void init1b() {
		init();

		eNode.addChild(aNode);

		fNode.addChild(aNode);
		fNode.addChild(n0);
		fNode.addChild(cNode);
		fNode.addChild(eNode);
		fNode.addChild(n1);

		tklist = new Tree[rule.getNumberOfStates()];
		tklist[0] = new Tree(eNode, semiring.createWeight(1), q0);
		tklist[1] = new Tree(cNode, semiring.createWeight(2), q1);
		config = new Configuration<>(new int[tklist.length], tklist.length, null);
		config.setWeight(semiring.createWeight(3));
		config.setValues(tklist);
	}

	private void init2() {
		init();

		fNode.addChild(n0);
		fNode.addChild(n1);
		
		tklist = new Tree[rule.getNumberOfStates()];
		tklist[0] = new Tree(aNode, semiring.createWeight(1), q0);
		tklist[1] = new Tree(aNode, semiring.createWeight(2), q1);
		config = new Configuration<>(new int[tklist.length], tklist.length, null);
		config.setWeight(semiring.createWeight(3));
		config.setValues(tklist);
	}

	private void init3() {
		init();

		fNode.addChild(aNode);
		fNode.addChild(bNode);

		tklist = new Tree[rule2.getNumberOfStates()];
		config = new Configuration<>(new int[tklist.length], tklist.length, null);
		config.setWeight(semiring.createWeight(0));
		config.setValues(tklist);
	}

	@Test
	public void testRuleApplication1a() {
		init1a();
		Tree result = rule.apply(config);
		String resString = result.getNode().toString();
		assertEquals("f[a, e[a], c, c, e[a]]", resString);
	}
	
	@Test
	public void testRuleApplication1b() {
		init1b();
		Tree result = rule.apply(config);
		String resString = result.getNode().toString();
		assertEquals("f[a, e[a], c, e[a], c]", resString);
	}

	@Test
	public void testRuleApplication2a() {
		init2();
		Tree result = rule.apply(config);
		String resString = result.getNode().toString();
		assertEquals("f[a, a]", resString);
	}

	@Test
	public void testRuleApplication2b() {
		init3();
		Tree result = rule.apply(config);
		String resString = result.getNode().toString();
		assertEquals("f[a, b]", resString);
	}

	@Test
	public void testApplicationWeight() {
		init1a();
		Tree result = rule.apply(config);
		Weight resWeight = semiring.createWeight(3.5);
		assertEquals(resWeight, result.getRunWeight());
	}

	@Test
	public void testFinalState() {
		init1a();
		Tree result = rule.apply(config);
		assertEquals(resState, result.getResultingState());
	}

	@Test
	public void testFinalState2() {
		init3();
		Tree result = rule2.apply(config);
		assertEquals(resState, result.getResultingState());
	}

}
