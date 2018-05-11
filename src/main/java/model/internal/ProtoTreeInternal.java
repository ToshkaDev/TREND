package model.internal;

import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;

public class ProtoTreeInternal {
    private int jobId;

    private String firstFileName;
    private String secondFileName;
    private String thirdFileName;

    private String alignmentAlg;
    private String reorderOrNot;

    private String treeBuildMethod;
    private String aaSubstModel;
    private String aaSubstRate;
    private String initialTreeForMl;
    private String gapsAndMissingData;
    private String siteCovCutOff;
    private String phylogenyTest;
    private String numberOrReplicates;

    private String domainPredictionProgram;
    private String domainPredictionDb;
	//e-value for Hmmscan and RpsBlast
	private String eValue;

    private String  commandToBeProcessedBy;
    private List<List<String>> commandsAndArguments;
    private List<String> outputFilesNames = new LinkedList<>();

    private List<String> fieldsForAlignmentAndTreeBuild = new LinkedList<>();
    private List<String> allFields = new LinkedList<>();

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

    public List<String> getOutputFilesNames() {
        return outputFilesNames;
    }

    public void setOutputFilesNames(List<String> outputFilesNames) {
        this.outputFilesNames = outputFilesNames;
    }

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

    public String getCommandToBeProcessedBy() {
        return commandToBeProcessedBy;
    }

    public void setCommandToBeProcessedBy(String commandToBeProcessedBy) {
        this.commandToBeProcessedBy = commandToBeProcessedBy;
    }

    public String getThirdFileName() {
        return thirdFileName;
    }

    public void setThirdFileName(String thirdFileName) {
        this.thirdFileName = thirdFileName;
    }


    public String getAlignmentAlg() {
        return alignmentAlg;
    }

    public void setAlignmentAlg(String alignmentAlg) {
        this.alignmentAlg = alignmentAlg;
    }

    public String getTreeBuildMethod() {
        return treeBuildMethod;
    }

    public void setTreeBuildMethod(String treeBuildMethod) {
        this.treeBuildMethod = treeBuildMethod;
    }

    public String getAaSubstModel() {
        return aaSubstModel;
    }

    public void setAaSubstModel(String aaSubstModel) {
        this.aaSubstModel = aaSubstModel;
    }

    public String getAaSubstRate() {
        return aaSubstRate;
    }

    public void setAaSubstRate(String aaSubstRate) {
        this.aaSubstRate = aaSubstRate;
    }

    public String getInitialTreeForMl() {
        return initialTreeForMl;
    }

    public void setInitialTreeForMl(String initialTreeForMl) {
        this.initialTreeForMl = initialTreeForMl;
    }

    public String getGapsAndMissingData() {
        return gapsAndMissingData;
    }

    public void setGapsAndMissingData(String gapsAndMissingData) {
        this.gapsAndMissingData = gapsAndMissingData;
    }

    public String getSiteCovCutOff() {
        return siteCovCutOff;
    }

    public void setSiteCovCutOff(String siteCovCutOff) {
        this.siteCovCutOff = siteCovCutOff;
    }

    public String getPhylogenyTest() {
        return phylogenyTest;
    }

    public void setPhylogenyTest(String phylogenyTest) {
        this.phylogenyTest = phylogenyTest;
    }

    public String getNumberOrReplicates() {
        return numberOrReplicates;
    }

    public void setNumberOrReplicates(String numberOrReplicates) {
        this.numberOrReplicates = numberOrReplicates;
    }

    public String getReorderOrNot() {
        return reorderOrNot;
    }

    public void setReorderOrNot(String reorderOrNot) {
        this.reorderOrNot = reorderOrNot;
    }

    public String getDomainPredictionProgram() {
        return domainPredictionProgram;
    }

    public void setDomainPredictionProgram(String domainPredictionProgram) {
        this.domainPredictionProgram = domainPredictionProgram;
    }

    public String getDomainPredictionDb() {
        return domainPredictionDb;
    }

    public void setDomainPredictionDb(String domainPredictionDb) {
        this.domainPredictionDb = domainPredictionDb;
    }

    public String geteValue() {
        return eValue;
    }

    public void seteValue(String eValue) {
        this.eValue = eValue;
    }


    public void setFields() {
        fieldsForAlignmentAndTreeBuild.add(getFirstFileName());
        fieldsForAlignmentAndTreeBuild.add(getAlignmentAlg());

        String reorderOrNot = getReorderOrNot();
        if (reorderOrNot != null) {
            fieldsForAlignmentAndTreeBuild.add(reorderOrNot);
        }
        fieldsForAlignmentAndTreeBuild.add(getTreeBuildMethod());
        fieldsForAlignmentAndTreeBuild.add(getAaSubstModel());
        fieldsForAlignmentAndTreeBuild.add(getAaSubstRate());
        fieldsForAlignmentAndTreeBuild.add(getInitialTreeForMl());
        fieldsForAlignmentAndTreeBuild.add(getGapsAndMissingData());
        fieldsForAlignmentAndTreeBuild.add(getSiteCovCutOff());
        fieldsForAlignmentAndTreeBuild.add(getPhylogenyTest());
        fieldsForAlignmentAndTreeBuild.add(getNumberOrReplicates());

        allFields.addAll(fieldsForAlignmentAndTreeBuild);

        allFields.add(getFirstFileName());
        if (getSecondFileName() != null) {
            allFields.add(getSecondFileName());
        }
        if (getThirdFileName() != null) {
            allFields.add(getThirdFileName());
        }
    }

    public List<String> getAllFields() {
        return allFields;
    }

    public List<String> getFieldsForAlignmentAndTreeBuild() {
        return fieldsForAlignmentAndTreeBuild;
    }

}
