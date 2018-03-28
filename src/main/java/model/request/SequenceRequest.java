package model.request;

import org.springframework.web.multipart.MultipartFile;

public class SequenceRequest {
	private MultipartFile firstFile;
	private String firstFileTextArea;
	private String firstFileDelim;
	private Integer firstFileColumn;
	
	private MultipartFile secondFile;
	private String secondFileTextArea;
	private String secondFileDelim;
	private Integer secondFileColumn;

	private String commandToBeProcessedBy;
	
	public MultipartFile getFirstFile() {
		return firstFile;
	}
	public void setFirstFile(MultipartFile file) {
		this.firstFile = file;
	}
	public String getFirstFileTextArea() {
		return firstFileTextArea;
	}

	public void setFirstFileTextArea(String firstFileTextArea) {
		this.firstFileTextArea = firstFileTextArea;
	}
	public String getFirstFileDelim() {
		return firstFileDelim;
	}
	public void setFirstFileDelim(String firstFileDelim) {
		this.firstFileDelim = firstFileDelim;
	}
	public Integer getFirstFileColumn() {
		return firstFileColumn;
	}
	public void setFirstFileColumn(Integer firstFileColumn) {
		this.firstFileColumn = firstFileColumn;
	}
	
	public MultipartFile getSecondFile() {
		return secondFile;
	}
	public void setSecondFile(MultipartFile secondFile) {
		this.secondFile = secondFile;
	}
	public String getSecondFileTextArea() {
		return secondFileTextArea;
	}

	public void setSecondFileTextArea(String secondFileTextArea) {
		this.secondFileTextArea = secondFileTextArea;
	}
	public String getSecondFileDelim() {
		return secondFileDelim;
	}
	public void setSecondFileDelim(String secondFileDelim) {
		this.secondFileDelim = secondFileDelim;
	}
	public Integer getSecondFileColumn() {
		return secondFileColumn;
	}
	public void setSecondFileColumn(Integer secondFileColumn) {
		this.secondFileColumn = secondFileColumn;
	}


	public String getCommandToBeProcessedBy() {
		return commandToBeProcessedBy;
	}
	public void setCommandToBeProcessedBy(String commandToBeProcessedBy) {
		this.commandToBeProcessedBy = commandToBeProcessedBy;
	}
}
