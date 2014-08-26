package jkind.lustre;

import java.util.Iterator;
import java.util.List;

import jkind.lustre.visitors.TypeVisitor;
import jkind.util.Util;

public class TupleType extends Type {
	final public List<Type> types;

	public TupleType(List<? extends Type> types) {
		super(Location.NULL);
		if (types != null && types.size() == 1) {
			throw new IllegalArgumentException("Cannot construct singleton tuple type");
		}
		this.types = Util.safeList(types);
	}

	public static Type compress(List<? extends Type> types) {
		if (types.size() == 1) {
			return types.get(0);
		}
		return new TupleType(types);
	}
	
	@Override
	public String toString() {
		if (types.isEmpty()) {
			return "()";
		}

		StringBuilder sb = new StringBuilder();

		Iterator<Type> iterator = types.iterator();
		sb.append("(");
		sb.append(iterator.next());
		while (iterator.hasNext()) {
			sb.append(", ");
			sb.append(iterator.next());
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return types.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TupleType) {
			TupleType tt = (TupleType) obj;
			return types.equals(tt.types);
		}
		return false;
	}

	@Override
	public <T> T accept(TypeVisitor<T> visitor) {
		return visitor.visit(this);
	}
}