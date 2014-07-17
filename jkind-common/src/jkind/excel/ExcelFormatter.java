package jkind.excel;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;

import jkind.JKindException;
import jkind.results.Counterexample;
import jkind.results.InvalidProperty;
import jkind.results.InvalidRealizability;
import jkind.results.Property;
import jkind.results.Realizability;
import jkind.results.UnknownProperty;
import jkind.results.ValidProperty;
import jkind.results.ValidRealizability;
import jkind.results.layout.Layout;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableHyperlink;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelFormatter implements Closeable {
	private WritableWorkbook workbook;

	private WritableSheet summarySheet;
	private int summaryRow;

	private WritableSheet currSheet;
	private int currRow;

	/*
	 * CellFormats cannot be static, since JXL has strange results when a cell
	 * format is reused in another workbook. See {@link
	 * http://jexcelapi.sourceforge.net/resources/faq/}.
	 */
	private final CellFormat boldFormat = ExcelUtil.getBoldFormat();
	private final ExcelCounterexampleFormatter cexFormatter;

	public ExcelFormatter(File file, Layout layout) {
		try {
			workbook = Workbook.createWorkbook(file);

			summarySheet = workbook.createSheet("Summary", workbook.getNumberOfSheets());
			ExcelUtil.autosize(summarySheet, 4);
			summarySheet.addCell(new Label(0, 0, "Property", boldFormat));
			summarySheet.addCell(new Label(1, 0, "Result", boldFormat));
			summarySheet.addCell(new Label(2, 0, "K", boldFormat));
			summarySheet.addCell(new Label(3, 0, "Runtime", boldFormat));
			summaryRow = 1;
		} catch (WriteException | IOException e) {
			throw new JKindException("Error writing to Excel file", e);
		}

		cexFormatter = new ExcelCounterexampleFormatter(workbook, layout);
	}

	@Override
	public void close() {
		try {
			if (workbook != null) {
				workbook.write();
				workbook.close();
				workbook = null;
			}
		} catch (Exception e) {
			throw new JKindException("Error closing Excel file", e);
		}
	}

	public void write(List<Property> properties) {
		try {
			for (Property property : properties) {
				write(property);
			}
		} catch (WriteException e) {
			throw new JKindException("Error writing to Excel file", e);
		}
	}

	private void write(Property property) throws WriteException {
		if (property instanceof ValidProperty) {
			write((ValidProperty) property);
		} else if (property instanceof InvalidProperty) {
			write((InvalidProperty) property);
		} else if (property instanceof UnknownProperty) {
			write((UnknownProperty) property);
		} else {
			throw new IllegalArgumentException("Unknown property type: "
					+ property.getClass().getSimpleName());
		}
	}

	private void write(ValidProperty property) throws WriteException {
		String name = property.getName();
		int k = property.getK();
		List<String> invariants = property.getInvariants();
		double runtime = property.getRuntime();

		summarySheet.addCell(new Label(0, summaryRow, name));
		if (invariants.isEmpty()) {
			summarySheet.addCell(new Label(1, summaryRow, "Valid"));
		} else {
			WritableSheet invSheet = writeInvariants(name, invariants);
			WritableHyperlink link = new WritableHyperlink(1, summaryRow, "Valid", invSheet, 0, 0);
			summarySheet.addHyperlink(link);
		}
		summarySheet.addCell(new Number(2, summaryRow, k));
		summarySheet.addCell(new Number(3, summaryRow, runtime));
		summaryRow++;
	}

	private WritableSheet writeInvariants(String property, List<String> invariants)
			throws WriteException {
		currSheet = workbook
				.createSheet(ExcelUtil.trimName(property), workbook.getNumberOfSheets());
		currRow = 0;

		currSheet.addCell(new Label(0, currRow, "Invariants for " + property, boldFormat));
		currRow += 2;

		for (String invariant : invariants) {
			currSheet.addCell(new Label(0, currRow, invariant));
			currRow++;
		}

		ExcelUtil.autosize(currSheet, 1);
		return currSheet;
	}

	private void write(InvalidProperty property) throws WriteException {
		String name = property.getName();
		Counterexample cex = property.getCounterexample();
		int length = cex.getLength();
		double runtime = property.getRuntime();

		WritableSheet cexSheet = writeCounterexample(name, cex);
		summarySheet.addCell(new Label(0, summaryRow, name));
		summarySheet.addHyperlink(new WritableHyperlink(1, summaryRow, "Invalid", cexSheet, 0, 0));
		summarySheet.addCell(new Number(2, summaryRow, length));
		summarySheet.addCell(new Number(3, summaryRow, runtime));
		summaryRow++;
	}

	private void write(UnknownProperty property) throws WriteException {
		String name = property.getName();
		Counterexample cex = property.getInductiveCounterexample();

		summarySheet.addCell(new Label(0, summaryRow, name));
		if (cex == null) {
			summarySheet.addCell(new Label(1, summaryRow, "Unknown"));
		} else {
			WritableSheet cexSheet = writeCounterexample(name, cex);
			summarySheet.addHyperlink(new WritableHyperlink(1, summaryRow, "Unknown", cexSheet, 0,
					0));
		}
		summaryRow++;
	}
	
	public void writeReal(List<Realizability> realizabilities) {
		try {
			for (Realizability realizability : realizabilities) {
				writeReal(realizability);
			}
		} catch (WriteException e) {
			throw new JKindException("Error writing to Excel file", e);
		}
		
	}
	
	private void writeReal(Realizability realizability) throws WriteException {
		if (realizability instanceof ValidRealizability) {
			write((ValidRealizability) realizability);
		} else if (realizability instanceof InvalidRealizability) {
			write((InvalidRealizability) realizability);
		} else {
			throw new IllegalArgumentException("Unknown realizability type: "
					+ realizability.getClass().getSimpleName());
		}
	}

	private void write(ValidRealizability realizability) throws WriteException {
		String name = realizability.getName();
		int k = realizability.getK();
		double runtime = realizability.getRuntime();

		summarySheet.addCell(new Label(0, summaryRow, name.toString()));
		summarySheet.addCell(new Label(1, summaryRow, "Valid"));
		summarySheet.addCell(new Number(2, summaryRow, k));
		summarySheet.addCell(new Number(3, summaryRow, runtime));
		summaryRow++;
	}
	
	private void write(InvalidRealizability realizability) throws WriteException {
		String name = realizability.getName();
		Counterexample cex = realizability.getCounterexample();
		int length = cex.getLength();
		double runtime = realizability.getRuntime();

		WritableSheet cexSheet = writeCounterexample(name.toString(), cex);
		summarySheet.addCell(new Label(0, summaryRow, name.toString()));
		summarySheet.addHyperlink(new WritableHyperlink(1, summaryRow, "Invalid", cexSheet, 0, 0));
		summarySheet.addCell(new Number(2, summaryRow, length));
		summarySheet.addCell(new Number(3, summaryRow, runtime));
		summaryRow++;
	}

	private WritableSheet writeCounterexample(String name, Counterexample cex) {
		return cexFormatter.writeCounterexample(name, cex);
	}
}
