package model.internal;

import java.util.LinkedList;
import java.util.List;

public class SequenceInternal {
	private String firstFileName;
	private String secondFileName;
	private String outputFileName;
	private String firstFileDelim;
	private String firstFileColumn;
	private String secondFileDelim;
	private String secondFileColumn;
	private String  commandToBeProcessedBy;
	private List<String> allFields = new LinkedList<>();
	
	public String getFirstFileName() {
		return firstFileName;
	}
	public void setFirstFileName(String firstFileName) {
		this.firstFileName = firstFileName;
	}
	public String getSecondFileName() {
		return secondFileName;
	}
	public void setSecondFileName(String secondFileName) {
		this.secondFileName = secondFileName;
	}
	public String getFirstFileDelim() {
		return firstFileDelim;
	}
	public void setFirstFileDelim(String firstFileDelim) {
		this.firstFileDelim = firstFileDelim;
	}
	public String getFirstFileColumn() {
		return firstFileColumn;
	}
	public void setFirstFileColumn(String firstFileColumn) {
		this.firstFileColumn = firstFileColumn;
	}
	public String getSecondFileDelim() {
		return secondFileDelim;
	}
	public void setSecondFileDelim(String secondFileDelim) {
		this.secondFileDelim = secondFileDelim;
	}
	public String getSecondFileColumn() {
		return secondFileColumn;
	}
	public void setSecondFileColumn(String secondFileColumn) {
		this.secondFileColumn = secondFileColumn;
	}
	public String getCommandToBeProcessedBy() {
		return commandToBeProcessedBy;
	}

	public void setCommandToBeProcessedBy(String commandToBeProcessedBy) {
		this.commandToBeProcessedBy = commandToBeProcessedBy;
	}

	public void setAllFields() {
		allFields.add(getFirstFileName());
		if (getSecondFileName() != null) {
			allFields.add(getSecondFileName());
		}
		if (getFirstFileDelim() != null) {
			allFields.add(getFirstFileDelim());
		}
		if (getFirstFileColumn() != null) {
			allFields.add(getFirstFileColumn());
		}
		if (getSecondFileDelim() != null) {
			allFields.add(getSecondFileDelim());
		}
		if (getSecondFileColumn() != null) {
			allFields.add(getSecondFileColumn());
		}
	}

	public List<String> getAllFields() {
		return allFields;
	}

	public String getOutputFileName() {
		return outputFileName;
	}

	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}
}
