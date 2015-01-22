package jkind.solvers.yices2;

import jkind.JKindException;
import jkind.lustre.parsing.StdoutErrorListener;
import jkind.sexp.Sexp;
import jkind.solvers.Model;
import jkind.solvers.Result;
import jkind.solvers.smtlib2.SmtLib2Solver;
import jkind.solvers.yices2.Yices2Parser.ModelContext;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Yices2Solver extends SmtLib2Solver {
	public Yices2Solver() {
		super(new ProcessBuilder("yices-smt2", "--incremental"), "Yices2");
	}

	@Override
	public void initialize() {
		send("(set-option :produce-models true)");
		send("(set-logic QF_LIRA)");
	}

	@Override
	protected Model parseModel(String string) {
		CharStream stream = new ANTLRInputStream(string);
		Yices2Lexer lexer = new Yices2Lexer(stream);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		Yices2Parser parser = new Yices2Parser(tokens);
		ModelContext ctx;

		parser.removeErrorListeners();
		parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
		parser.setErrorHandler(new BailErrorStrategy());
		try {
			ctx = parser.model();
		} catch (ParseCancellationException e) {
			tokens.reset();
			parser.addErrorListener(new StdoutErrorListener());
			parser.setErrorHandler(new DefaultErrorStrategy());
			parser.getInterpreter().setPredictionMode(PredictionMode.LL);
			ctx = parser.model();
		}

		if (parser.getNumberOfSyntaxErrors() > 0) {
			throw new JKindException("Error parsing " + name + " output: " + string);
		}

		ParseTreeWalker walker = new ParseTreeWalker();
		ModelExtractorListener extractor = new ModelExtractorListener(varTypes);
		walker.walk(extractor, ctx);
		return extractor.getModel();
	}
	
	@Override
	public Result realizability_query(Sexp inputs, Sexp outputs, Sexp transition, Sexp properties) {
		throw new IllegalArgumentException("Yices2 not supported for realizability checks");
	}
}