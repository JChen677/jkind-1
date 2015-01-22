package jkind.writers;

import java.util.List;
import java.util.Map;

import jkind.invariant.Invariant;
import jkind.results.Counterexample;
import jkind.results.layout.Layout;

public class ConsoleWriter extends Writer {
	private final Layout layout;

	public ConsoleWriter(Layout layout) {
		super();
		this.layout = layout;
	}

	@Override
	public void begin() {
	}

	@Override
	public void end() {
	}

	private void writeLine() {
		System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}

	@Override
	public void writeValid(List<String> props, int k, double runtime, List<Invariant> invariants) {
		writeLine();
		System.out.println("VALID PROPERTIES: " + props + " || K = " + k + " || Time = " + runtime);
		if (!invariants.isEmpty()) {
			System.out.println("INVARIANTS:");
			for (Invariant invariant : invariants) {
				System.out.println("  " + invariant);
			}
		}
		writeLine();
		System.out.println();
	}

	@Override
	public void writeInvalid(String prop, Counterexample cex, double runtime) {
		writeLine();
		System.out.println("INVALID PROPERTY: " + prop + " || K = " + cex.getLength()
				+ " || Time = " + runtime);
		System.out.println(cex.toString(layout));
		writeLine();
		System.out.println();
	}

	@Override
	public void writeUnknown(List<String> props, int trueFor,
			Map<String, Counterexample> inductiveCounterexamples, double runtime) {
		writeLine();
		System.out.println("UNKNOWN PROPERTIES: " + props + " || True for " + trueFor + " steps"
				+ " || Time = " + runtime);
		writeLine();
		System.out.println();
		for (String prop : props) {
			Counterexample cex = inductiveCounterexamples.get(prop);
			if (cex != null) {
				writeLine();
				System.out.println("INDUCTIVE COUNTEREXAMPLE: " + prop + " || K = "
						+ cex.getLength());
				System.out.println(cex);
				writeLine();
				System.out.println();
			}
		}
	}
	
	@Override
	public void writeValidRealizability(List<String> reals, int k, double runtime, List<Invariant> invariants) {
		writeLine();
		System.out.println("VALID REALIZABILITIES: " + reals + " || K = " + k + " || Time = " + runtime);
		if (!invariants.isEmpty()) {
			System.out.println("INVARIANTS:");
			for (Invariant invariant : invariants) {
				System.out.println("  " + invariant);
			}
		}
		writeLine();
		System.out.println();
	}


	@Override
	public void writeInvalidRealizability(String real,
			Counterexample cex, double runtime) {
		writeLine();
		System.out.println("INVALID REALIZABILITY: " + real + " || K = " + cex.getLength()
				+ " || Time = " + runtime);
		System.out.println(cex.toString(layout));
		writeLine();
		System.out.println();
		
	}
	
	@Override
	public void writeUnknownRealizabilities(List<String> reals, int trueFor, 
			Map<String, Counterexample> inductiveCounterexamples, double runtime) {
		writeLine();
		System.out.println("UNKNOWN REALIZABILITIES: " + reals + " || True for " + trueFor + " steps" + " || Time = " + runtime);
		writeLine();
		System.out.println();
		for (String real : reals) {
			Counterexample cex = inductiveCounterexamples.get(real);
			if (cex != null) {
				writeLine();
				System.out.println("INDUCTIVE COUNTEREXAMPLE: " + real + " || K = "
						+ cex.getLength());
				System.out.println(cex);
				writeLine();
				System.out.println();
			}
		}
	}
}
