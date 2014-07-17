package jkind.api.ui;

import java.io.IOException;
import java.io.InputStream;

import jkind.api.results.AnalysisResult;
import jkind.api.results.PropertyResult;
import jkind.api.results.RealizabilityResult;
import jkind.api.results.ResultsUtil;
import jkind.api.results.Status;
import jkind.api.ui.AnalysisResultColumnViewer.Column;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

public class AnalysisResultLabelProvider extends ColumnLabelProvider {
	private final Column column;

	public AnalysisResultLabelProvider(Column column) {
		this.column = column;
	}

	public AnalysisResultLabelProvider(Column column, ColumnViewer viewer) {
		this.column = column;
		workingSpinner = new Spinner("/working.gif", viewer);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof PropertyResult) {
			PropertyResult pr = (PropertyResult) element;
			switch (column) {
			case PROPERTY:
				return pr.getName();
			case RESULT:
				switch (pr.getStatus()) {
				case WAITING:
					return pr.getStatus().toString();
				case WORKING:
					return pr.getStatus().toString() + "... (" + pr.getElapsed() + "s)";
				default:
					return pr.getStatus().toString() + " (" + pr.getElapsed() + "s)";
				}
			case REALIZABILITY:
				break;
			}
		} else if (element instanceof RealizabilityResult) {
			RealizabilityResult re = (RealizabilityResult) element;
			switch (column) {
			case PROPERTY:
				return "Realizability Check";
			case REALIZABILITY:
				return re.getName();
			case RESULT:
				switch (re.getStatus()) {
				case WAITING:
					return re.getStatus().toString();
				case WORKING:
					return re.getStatus().toString() + "... (" + re.getElapsed() + "s)";
				default:
					return re.getStatus().toString() + " (" + re.getElapsed() + "s)";
				}
			}
		} else if (element instanceof AnalysisResult) {
			AnalysisResult result = (AnalysisResult) element;
			switch (column) {
			case PROPERTY:
				return result.getName();
			case RESULT:
				return ResultsUtil.getMultiStatus(result).toString();
			case REALIZABILITY:
				return result.getName();
			}
		}

		return "";
	}

	private static final Image EMPTY_IMAGE = loadImage("/empty.png");
	private static final Image VALID_IMAGE = loadImage("/valid.png");
	private static final Image INVALID_IMAGE = loadImage("/invalid.png");
	private static final Image UNKNOWN_IMAGE = loadImage("/unknown.png");
	private static final Image WAITING_IMAGE = loadImage("/waiting.png");
	private static final Image CANCEL_IMAGE = loadImage("/cancel.png");
	private static final Image ERROR_IMAGE = loadImage("/error.png");
	private Spinner workingSpinner;

	private static Image loadImage(String filename) {
		try (InputStream stream = AnalysisResultLabelProvider.class.getResourceAsStream(filename)) {
			return new Image(null, new ImageData(stream));
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public Image getImage(Object element) {
		if (column == Column.PROPERTY) {
			if (element instanceof PropertyResult) {
				PropertyResult pr = (PropertyResult) element;
				return getStatusImage(pr.getStatus());
			} else if (element instanceof RealizabilityResult) {
				RealizabilityResult re = (RealizabilityResult) element;
				return getStatusImage(re.getStatus());
			} else if (element instanceof AnalysisResult) {
				AnalysisResult result = (AnalysisResult) element;
				return getStatusImage(ResultsUtil.getMultiStatus(result).getOverallStatus());
			}
		} else if (column == Column.REALIZABILITY) {
			if (element instanceof PropertyResult) {
				PropertyResult pr = (PropertyResult) element;
				return getStatusImage(pr.getStatus());
			} else if (element instanceof RealizabilityResult) {
				RealizabilityResult re = (RealizabilityResult) element;
				return getStatusImage(re.getStatus());
			} else if (element instanceof AnalysisResult) {
				AnalysisResult result = (AnalysisResult) element;
				return getStatusImage(ResultsUtil.getMultiStatus(result).getOverallStatus());
			}
		}

		return null;
	}

	private Image getStatusImage(Status status) {
		if (status == null) {
			return EMPTY_IMAGE;
		}

		switch (status) {
		case WORKING:
			return workingSpinner.getFrame();
		case VALID:
			return VALID_IMAGE;
		case INVALID:
			return INVALID_IMAGE;
		case UNKNOWN:
			return UNKNOWN_IMAGE;
		case CANCELED:
			return CANCEL_IMAGE;
		case ERROR:
			return ERROR_IMAGE;
		case WAITING:
			return WAITING_IMAGE;
		default:
			return WAITING_IMAGE;
		}
	}

	@Override
	public void dispose() {
		if (workingSpinner != null) {
			workingSpinner.dispose();
		}
	}
}
