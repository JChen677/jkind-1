package jkind.translation;

import java.util.ArrayList;
import java.util.List;

import jkind.lustre.Equation;
import jkind.lustre.Expr;
import jkind.lustre.Node;
import jkind.lustre.SubrangeIntType;
import jkind.lustre.Type;
import jkind.lustre.VarDecl;
import jkind.sexp.Cons;
import jkind.sexp.Sexp;
import jkind.sexp.Symbol;
import jkind.solvers.Lambda;
import jkind.solvers.StreamDecl;
import jkind.solvers.StreamDef;
import jkind.util.Util;

public class Lustre2Sexps {
	private StreamDef transition;
	private List<StreamDecl> declarations = new ArrayList<StreamDecl>();

	public Lustre2Sexps(Node node) {
		createDefinitions(node);
		createTransition(node);
	}

	private void createDefinitions(Node node) {
		for (VarDecl decl : Util.getVarDecls(node)) {
			declarations.add(new StreamDecl("$" + decl.id, getBaseType(decl.type)));
		}
	}
	
	private Type getBaseType(Type type) {
		if (type instanceof SubrangeIntType) {
			return Type.INT;
		} else {
			return type;
		}
	}

	private void createTransition(Node node) {
		Expr2SexpVisitor visitor = new Expr2SexpVisitor(Util.I);
		List<Sexp> conjuncts = new ArrayList<Sexp>();
		
		for (Equation eq : node.equations) {
			conjuncts.add(equation2Sexp(eq, Util.I, visitor));
		}

		for (VarDecl varDecl : Util.getVarDecls(node)) {
			if (varDecl.type instanceof SubrangeIntType) {
				conjuncts.add(Util.subrangeConstraint(varDecl.id, Util.I,
						(SubrangeIntType) varDecl.type));
			}
		}
		
		for (Expr assertion : node.assertions) {
			conjuncts.add(assertion.accept(new Expr2SexpVisitor(Util.I)));
		}
		
		if (visitor.hasSideConditions()) {
			declarations.addAll(visitor.getSideConditionDeclarations());
			conjuncts.addAll(visitor.getSideConditions());
		}

		Lambda lambda = new Lambda(Util.I, new Cons("and", conjuncts));
		transition = new StreamDef(Keywords.T, Type.BOOL, lambda);
	}

	private Sexp equation2Sexp(Equation eq, Symbol iSym, Expr2SexpVisitor visitor) {
		Sexp body = eq.expr.accept(visitor);
		return new Cons("=", new Cons("$" + eq.lhs.get(0).id, iSym), body);
	}

	public StreamDef getTransition() {
		return transition;
	}

	public List<StreamDecl> getDeclarations() {
		return declarations;
	}
}
