package jkind.excel;

import jkind.JKindException;
import jxl.format.CellFormat;
import jxl.CellView;
import jxl.format.Colour;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

public class ExcelUtil {
	public static CellFormat getBoldFormat() {
		WritableCellFormat boldFormat = new WritableCellFormat();
		WritableFont font = new WritableFont(boldFormat.getFont());
		try {
			font.setBoldStyle(WritableFont.BOLD);
		} catch (WriteException e) {
			throw new JKindException("Error creating bold font for Excel", e);
		}
		boldFormat.setFont(font);
		return boldFormat;
	}

	public static CellFormat getFadedFormat() {
		WritableCellFormat fadedFormat = new WritableCellFormat();
		WritableFont font = new WritableFont(fadedFormat.getFont());
		try {
			font.setColour(Colour.GREY_25_PERCENT);
		} catch (WriteException e) {
			throw new JKindException("Error creating grey font for Excel", e);
		}
		fadedFormat.setFont(font);
		return fadedFormat;
	}

	public static void autosize(WritableSheet sheet, int max) {
		for (int col = 0; col < max; col++) {
			CellView cell = sheet.getColumnView(col);
			cell.setAutosize(true);
			sheet.setColumnView(col, cell);
		}
	}

	public static String trimName(String name) {
		if (name.length() <= 31) {
			return name;
		} else {
			return name.substring(0, 28) + "...";
		}
	}
}
