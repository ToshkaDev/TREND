package model.internal;

import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;

public class ProtoTreeInternal {
    private int jobId;

    private String firstFileName;
    private String secondFileName;
    private String alignmentFile;
    private String treeFile;
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
    private String probability;
    private String lcrPrediction;

    private String  commandToBeProcessedBy;
    private String isFullPipeline;
    private List<List<String>> commandsAndArguments;
    private List<String> outputFilesNames = new LinkedList<>();

    private List<String> fieldsForAlignmentAndTreeBuild = new LinkedList<>();
    private List<String> fieldsForFeaturesPrediction = new LinkedList<>();

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

    public String getAlignmentFile() {
        return alignmentFile;
    }

    public void setAlignmentFile(String alignmentFile) {
        this.alignmentFile = alignmentFile;
    }

    public String getTreeFile() {
        return treeFile;
    }

    public void setTreeFile(String treeFile) {
        this.treeFile = treeFile;
    }

    public String getCommandToBeProcessedBy() {
        return commandToBeProcessedBy;
    }

    public void setCommandToBeProcessedBy(String commandToBeProcessedBy) {
        this.commandToBeProcessedBy = commandToBeProcessedBy;
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

    public String getProbability() {
        return probability;
    }

    public void setProbability(String probability) {
        this.probability = probability;
    }

    public String getLcrPrediction() {
        return lcrPrediction;
    }

    public void setLcrPrediction(String lcrPrediction) {
        this.lcrPrediction = lcrPrediction;
    }

    public void setFields() {
        String reorderOrNot = getReorderOrNot();
        String lcrPrediction = getLcrPrediction();
        if (reorderOrNot != null) {
            fieldsForAlignmentAndTreeBuild.add(reorderOrNot);
        }
        fieldsForAlignmentAndTreeBuild.add(getFirstFileName());
        fieldsForAlignmentAndTreeBuild.add(getAlignmentAlg());
        fieldsForAlignmentAndTreeBuild.add(getTreeBuildMethod());
        fieldsForAlignmentAndTreeBuild.add(getAaSubstModel());
        fieldsForAlignmentAndTreeBuild.add(getAaSubstRate());
        fieldsForAlignmentAndTreeBuild.add(getInitialTreeForMl());
        fieldsForAlignmentAndTreeBuild.add(getGapsAndMissingData());
        fieldsForAlignmentAndTreeBuild.add(getSiteCovCutOff());
        fieldsForAlignmentAndTreeBuild.add(getPhylogenyTest());
        fieldsForAlignmentAndTreeBuild.add(getNumberOrReplicates());

        fieldsForFeaturesPrediction.add(geteValue());
        fieldsForFeaturesPrediction.add(getProbability());
        if (lcrPrediction != null) {
            fieldsForFeaturesPrediction.add(getLcrPrediction());
        }
        fieldsForFeaturesPrediction.add(getDomainPredictionProgram());
    }

    public List<String> getFieldsForAlignmentAndTreeBuild() {
        return fieldsForAlignmentAndTreeBuild;
    }

    public List<String> getFieldsForFeaturesPrediction() {
        return fieldsForFeaturesPrediction;
    }

    public String isFullPipeline() {
        return isFullPipeline;
    }

    public void setFullPipeline(String fullPipeline) {
        isFullPipeline = fullPipeline;
    }
}
