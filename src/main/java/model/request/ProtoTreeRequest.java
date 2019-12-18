package model.request;
import org.springframework.web.multipart.MultipartFile;

public class ProtoTreeRequest {
    private MultipartFile firstFile;
    private String firstFileArea;
    private MultipartFile secondFile;
    private String secondFileArea;
    private MultipartFile alignmentFile;
    private MultipartFile treeFile;
    private String treeFileArea;

    private String treeBuildingProgram;
    private String doAlign;
    private String treeBuildMethod;
    private String aaSubstModel;
    private String aaSubstRate;
    private String initialTreeForMl;
    private String gapsAndMissingData;
    private String siteCovCutOff;
    private String phylogenyTest;
    private String numberOrReplicates;
    private String alignmentAlg;

    private String aaSubstModelFt;
    private String pseudoCountsFt;
    private String phylogenyTestFt;
    private String numberOrReplicatesFt;

    private String doPredictFeatures;
    private String domainPredictionProgram;
    private String domainPredictionDb;
    private String eValue;
    private String probability;
    private String lcrPrediction;
    private String enumerate;

    private String domainTolerance;
    private String operonTolerance;
    private String numberOfNeighbors;

    private String commandToBeProcessedBy;
    private String isFullPipeline;
    private String protoTreeCookies;
    private String fetchFromIds;
    private String fetchFromTree;
    private String redundancy;

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

    public String getDoAlign() {
        return doAlign;
    }

    public void setDoAlign(String doAlign) {
        this.doAlign = doAlign;
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

    public String getTreeFileArea() {
        return treeFileArea;
    }

    public void setTreeFileArea(String treeFileArea) {
        this.treeFileArea = treeFileArea;
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

    public String getTreeBuildingProgram() {
        return treeBuildingProgram;
    }

    public void setTreeBuildingProgram(String treeBuildingProgram) {
        this.treeBuildingProgram = treeBuildingProgram;
    }

    public String getAaSubstModelFt() {
        return aaSubstModelFt;
    }

    public void setAaSubstModelFt(String aaSubstModelFt) {
        this.aaSubstModelFt = aaSubstModelFt;
    }

    public String getPseudoCountsFt() {
        return pseudoCountsFt;
    }

    public void setPseudoCountsFt(String pseudoCountsFt) {
        this.pseudoCountsFt = pseudoCountsFt;
    }

    public String getPhylogenyTestFt() {
        return phylogenyTestFt;
    }

    public void setPhylogenyTestFt(String phylogenyTestFt) {
        this.phylogenyTestFt = phylogenyTestFt;
    }

    public String getNumberOrReplicatesFt() {
        return numberOrReplicatesFt;
    }

    public void setNumberOrReplicatesFt(String numberOrReplicatesFt) {
        this.numberOrReplicatesFt = numberOrReplicatesFt;
    }

    public String getDoPredictFeatures() {
        return doPredictFeatures;
    }

    public void setDoPredictFeatures(String doPredictFeatures) {
        this.doPredictFeatures = doPredictFeatures;
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

    public String getProtoTreeCookies() {
        return protoTreeCookies;
    }

    public void setProtoTreeCookies(String protoTreeCookies) {
        this.protoTreeCookies = protoTreeCookies;
    }

    public String getDomainTolerance() {
        return domainTolerance;
    }

    public void setDomainTolerance(String domainTolerance) {
        this.domainTolerance = domainTolerance;
    }

    public String getOperonTolerance() {
        return operonTolerance;
    }

    public void setOperonTolerance(String operonTolerance) {
        this.operonTolerance = operonTolerance;
    }

    public String getNumberOfNeighbors() {
        return numberOfNeighbors;
    }

    public void setNumberOfNeighbors(String numberOfNeighbors) {
        this.numberOfNeighbors = numberOfNeighbors;
    }

    public String getEnumerate() {
        return enumerate;
    }

    public void setEnumerate(String enumerate) {
        this.enumerate = enumerate;
    }

    public String getFetchFromIds() {
        return fetchFromIds;
    }

    public void setFetchFromIds(String fetchFromIds) {
        this.fetchFromIds = fetchFromIds;
    }

    public String getFetchFromTree() {
        return fetchFromTree;
    }

    public void setFetchFromTree(String fetchFromTree) {
        this.fetchFromTree = fetchFromTree;
    }

    public String getRedundancy() {
        return redundancy;
    }

    public void setRedundancy(String redundancy) {
        this.redundancy = redundancy;
    }
}


