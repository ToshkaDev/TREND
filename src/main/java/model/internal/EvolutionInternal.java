package model.internal;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by vadim on 8/14/17.
 */
public class EvolutionInternal {
    private String fileDelim;
    private String fileColumn;
    private String identityThreshold;
    private String coverageThreshold;
    private String evalueThreshold;
    private String commandToBeProcessedBy;
    private String doMerge;
    private int jobId;
    private String outputFileName;

    private List<List<String>> commandsAndArguments;

	private List<String> fieldsForIntermediateScript = new LinkedList<>();
    private List<String> allFields = new LinkedList<>();

    private String organismNameColumn;
    private String proteinNameColumn;
    private String cogIdColumn;

    public String getFileDelim() {
        return fileDelim;
    }

    public void setFileDelim(String fileDelim) {
        this.fileDelim = fileDelim;
    }

    public String getFileColumn() {
        return fileColumn;
    }

    public void setFileColumn(String fileColumn) {
        this.fileColumn = fileColumn;
    }

    public List<String> getFieldForIntermScript() {
        return fieldsForIntermediateScript;
    }

    public String getIdentityThreshold() {
        return identityThreshold;
    }

    public void setIdentityThreshold(String identityThreshold) {
        this.identityThreshold = identityThreshold;
    }

    public String getCoverageThreshold() {
        return coverageThreshold;
    }

    public void setCoverageThreshold(String coverageThreshold) {
        this.coverageThreshold = coverageThreshold;
    }

    public String getEvalueThreshold() {
        return evalueThreshold;
    }

    public void setEvalueThreshold(String evalueThreshold) {
        this.evalueThreshold = evalueThreshold;
    }

    public void setCommandToBeProcessedBy(String commandToBeProcessedBy) {
        this.commandToBeProcessedBy = commandToBeProcessedBy;
    }
    
    public String getCommandToBeProcessedBy() {
        return commandToBeProcessedBy;
    }
    
    public String getDoMerge() {
		return doMerge;
	}

	public void setDoMerge(String doMerge) {
		this.doMerge = doMerge;
	}
	
    public List<String> getAllFields() {
        return allFields;
    }

    public void setFields() {
        if (fileDelim != null) {
            fieldsForIntermediateScript.add(fileDelim);
        }
        if (fileColumn != null) {
            fieldsForIntermediateScript.add(fileColumn);
        }
        allFields.addAll(fieldsForIntermediateScript);

        if (identityThreshold != null) {
            allFields.add(identityThreshold);
        }
        if (coverageThreshold != null) {
            allFields.add(coverageThreshold);
        }
        if (evalueThreshold != null) {
            allFields.add(evalueThreshold);
        }

        if (organismNameColumn != null) {
            allFields.add(organismNameColumn);
        }
        if (proteinNameColumn != null) {
            allFields.add(proteinNameColumn);
        }
        if (cogIdColumn != null) {
            allFields.add(cogIdColumn);
        }
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public List<List<String>> getCommandsAndArguments() {
        return commandsAndArguments;
    }

    public void setCommandsAndArguments(List<List<String>> commandsAndArguments) {
        this.commandsAndArguments = commandsAndArguments;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public String getOrganismNameColumn() {
        return organismNameColumn;
    }

    public void setOrganismNameColumn(String organismNameColumn) {
        this.organismNameColumn = organismNameColumn;
    }

    public String getProteinNameColumn() {
        return proteinNameColumn;
    }

    public void setProteinNameColumn(String proteinNameColumn) {
        this.proteinNameColumn = proteinNameColumn;
    }

    public String getCogIdColumn() {
        return cogIdColumn;
    }

    public void setCogIdColumn(String cogIdColumn) {
        this.cogIdColumn = cogIdColumn;
    }
}
