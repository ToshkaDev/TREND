package model.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class EvolutionRequest {
	private List<MultipartFile> listOfFiles;
	private String fileDelim;
	private Integer fileColumn;
	private Double identityThreshold;
	private Double coverageThreshold;
	private Double evalueThreshold;
	private String commandToBeProcessedBy;
	private String doMerge;

	private Integer organismNameColumn;
	private Integer proteinNameColumn;
	private Integer cogIdColumn;

	public List<MultipartFile> getListOfFiles() {
		return listOfFiles;
	}

	public void setListOfFiles(List<MultipartFile> listOfFiles) {
		this.listOfFiles = listOfFiles;
	}

	public String getFileDelim() {
		return fileDelim;
	}

	public void setFileDelim(String fileDelim) {
		this.fileDelim = fileDelim;
	}

	public Integer getFileColumn() {
		return fileColumn;
	}

	public void setFileColumn(Integer fileColumn) {
		this.fileColumn = fileColumn;
	}

	public String getCommandToBeProcessedBy() {
		return commandToBeProcessedBy;
	}

	public void setCommandToBeProcessedBy(String commandToBeProcessedBy) {
		this.commandToBeProcessedBy = commandToBeProcessedBy;
	}

	public Double getIdentityThreshold() {
		return identityThreshold;
	}

	public void setIdentityThreshold(Double identityThreshold) {
		this.identityThreshold = identityThreshold;
	}

	public Double getCoverageThreshold() {
		return coverageThreshold;
	}

	public void setCoverageThreshold(Double coverageThreshold) {
		this.coverageThreshold = coverageThreshold;
	}

	public Double getEvalueThreshold() {
		return evalueThreshold;
	}

	public void setEvalueThreshold(Double evalueThreshold) {
		this.evalueThreshold = evalueThreshold;
	}
	
	public String getDoMerge() {
		return doMerge;
	}

	public void setDoMerge(String doMerge) {
		this.doMerge = doMerge;
	}

	public Integer getOrganismNameColumn() {
		return organismNameColumn;
	}

	public void setOrganismNameColumn(Integer organismNameColumn) {
		this.organismNameColumn = organismNameColumn;
	}

	public Integer getProteinNameColumn() {
		return proteinNameColumn;
	}

	public void setProteinNameColumn(Integer proteinNameColumn) {
		this.proteinNameColumn = proteinNameColumn;
	}

	public Integer getCogIdColumn() {
		return cogIdColumn;
	}

	public void setCogIdColumn(Integer cogIdColumn) {
		this.cogIdColumn = cogIdColumn;
	}
}
