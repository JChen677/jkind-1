package jkind.util;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * An arbitrary sized fractional value
 * 
 * Stored as <code>numerator</code> / <code>denominator</code> where the
 * fraction is in reduced form and <code>denominator</code> is always positive
 */
public class BigFraction implements Comparable<BigFraction> {
	final public static BigFraction ZERO = new BigFraction(BigInteger.ZERO);
	final public static BigFraction ONE = new BigFraction(BigInteger.ONE);
	
	// The numerator and denominator are always stored in reduced form with the
	// denominator always positive
	final private BigInteger num;
	final private BigInteger denom;

	public BigFraction(BigInteger num, BigInteger denom) {
		if (num == null || denom == null) {
			throw new NullPointerException();
		}
		if (denom.equals(BigInteger.ZERO)) {
			throw new ArithmeticException("Divide by zero");
		}

		BigInteger gcd = num.gcd(denom);
		if (denom.compareTo(BigInteger.ZERO) > 0) {
			this.num = num.divide(gcd);
			this.denom = denom.divide(gcd);
		} else {
			this.num = num.negate().divide(gcd);
			this.denom = denom.negate().divide(gcd);
		}
	}

	public BigFraction(BigInteger num) {
		this(num, BigInteger.ONE);
	}

	public BigFraction(BigDecimal value) {
		this(value.unscaledValue(), BigInteger.valueOf(10).pow(value.scale()));
	}

	public BigInteger getNumerator() {
		return num;
	}

	public BigInteger getDenominator() {
		return denom;
	}

	public BigFraction add(BigFraction val) {
		return new BigFraction(num.multiply(val.denom).add(val.num.multiply(denom)),
				denom.multiply(val.denom));
	}

	public BigFraction add(BigInteger val) {
		return add(new BigFraction(val));
	}

	public BigFraction subtract(BigFraction val) {
		return new BigFraction(num.multiply(val.denom).subtract(val.num.multiply(denom)),
				denom.multiply(val.denom));
	}

	public BigFraction subtract(BigInteger val) {
		return subtract(new BigFraction(val));
	}

	public BigFraction multiply(BigFraction val) {
		return new BigFraction(num.multiply(val.num), denom.multiply(val.denom));
	}

	public BigFraction multiply(BigInteger val) {
		return multiply(new BigFraction(val));
	}

	public BigFraction divide(BigFraction val) {
		return new BigFraction(num.multiply(val.denom), denom.multiply(val.num));
	}

	public BigFraction divide(BigInteger val) {
		return divide(new BigFraction(val));
	}

	public BigFraction negate() {
		return new BigFraction(num.negate(), denom);
	}
	
	public int signum() {
		return num.signum();
	}

	public double doubleValue() {
		return num.doubleValue() / denom.doubleValue();
	}

	@Override
	public int compareTo(BigFraction other) {
		return num.multiply(other.denom).compareTo(other.num.multiply(denom));
	}

	@Override
	public String toString() {
		if (denom.equals(BigInteger.ONE)) {
			return num.toString();
		} else {
			return num + "/" + denom;
		}
	}

	@Override
	public int hashCode() {
		return num.hashCode() + denom.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BigFraction)) {
			return false;
		}
		BigFraction other = (BigFraction) obj;
		return num.equals(other.num) && denom.equals(other.denom);
	}
}
