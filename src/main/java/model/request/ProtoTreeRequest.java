package model.request;
import org.springframework.web.multipart.MultipartFile;

public class ProtoTreeRequest {
    private MultipartFile firstFile;
    private String firstFileArea;
    private MultipartFile secondFile;
    private String secondFileArea;
    private MultipartFile alignmentFile;
    private MultipartFile treeFile;

    private String treeBuildMethod;
    private String aaSubstModel;
    private String aaSubstRate;
    private String initialTreeForMl;
    private String gapsAndMissingData;
    private String siteCovCutOff;
    private String phylogenyTest;
    private String numberOrReplicates;
    private String alignmentAlg;
    private String domainPredictionProgram;
    private String domainPredictionDb;
    private String eValue;
    private String probability;
    private String lcrPrediction;

    private String commandToBeProcessedBy;
    private String isFullPipeline;

    public MultipartFile getFirstFile() {
        return firstFile;
    }
    public void setFirstFile(MultipartFile file) {
        this.firstFile = file;
    }
    public String getFirstFileArea() {
        return firstFileArea;
    }

    public void setFirstFileArea(String firstFileArea) {
        this.firstFileArea = firstFileArea;
    }

    public MultipartFile getSecondFile() {
        return secondFile;
    }
    public void setSecondFile(MultipartFile secondFile) {
        this.secondFile = secondFile;
    }
    public String getSecondFileArea() {
        return secondFileArea;
    }

    public void setSecondFileArea(String secondFileArea) {
        this.secondFileArea = secondFileArea;
    }

    public MultipartFile getAlignmentFile() {
        return alignmentFile;
    }

    public void setAlignmentFile(MultipartFile alignmentFile) {
        this.alignmentFile = alignmentFile;
    }

    public MultipartFile getTreeFile() {
        return treeFile;
    }

    public void setTreeFile(MultipartFile treeFile) {
        this.treeFile = treeFile;
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

    public String getCommandToBeProcessedBy() {
        return commandToBeProcessedBy;
    }

    public void setCommandToBeProcessedBy(String commandToBeProcessedBy) {
        this.commandToBeProcessedBy = commandToBeProcessedBy;
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

    public String isFullPipeline() {
        return isFullPipeline;
    }

    public void setIsFullPipeline(String fullPipeline) {
        isFullPipeline = fullPipeline;
    }
}


