package jkind.results;


/**
 * A unknown property
 */
public final class UnknownProperty extends Property {
	private final Counterexample cex;

	public UnknownProperty(String name, Counterexample cex) {
		super(name);
		this.cex = cex;
	}

	/**
	 * Inductive counterexample for the property, only available if
	 * JKindApi.setInductiveCounterexamples()
	 */
	public Counterexample getInductiveCounterexample() {
		return cex;
	}
}
