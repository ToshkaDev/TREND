package model.request;
import org.springframework.web.multipart.MultipartFile;

public class ProtoTreeRequest {
    private MultipartFile firstFile;
    private String firstFileTextArea;

    private MultipartFile secondFile;
    private String secondFileTextArea;

    private MultipartFile thirdFile;
    private String thirdFileTextArea;

    private String alignmentAlg;
    private String treeBuildMethod;
    private String aaSubstModel;
    private String aaSubstRate;
    private String initialTreeForMl;
    private String gapsAndMissingData;
    private String SiteCovCutOff;
    private String phylogenyTest;
    private String numberOrReplicates;
    private String alignThreads;
    private String treeThreads;
    private String reorderOrNot;

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

    public MultipartFile getThirdFile() {
        return thirdFile;
    }

    public void setThirdFile(MultipartFile thirdFile) {
        this.thirdFile = thirdFile;
    }

    public String getThirdFileTextArea() {
        return thirdFileTextArea;
    }

    public void setThirdFileTextArea(String thirdFileTextArea) {
        this.thirdFileTextArea = thirdFileTextArea;
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
        return SiteCovCutOff;
    }

    public void setSiteCovCutOff(String siteCovCutOff) {
        SiteCovCutOff = siteCovCutOff;
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

    public String getAlignThreads() {
        return alignThreads;
    }

    public void setAlignThreads(String alignThreads) {
        this.alignThreads = alignThreads;
    }

    public String getTreeThreads() {
        return treeThreads;
    }

    public void setTreeThreads(String treeThreads) {
        this.treeThreads = treeThreads;
    }

    public String getReorderOrNot() {
        return reorderOrNot;
    }

    public void setReorderOrNot(String reorderOrNot) {
        this.reorderOrNot = reorderOrNot;
    }

    public String getCommandToBeProcessedBy() {
        return commandToBeProcessedBy;
    }

    public void setCommandToBeProcessedBy(String commandToBeProcessedBy) {
        this.commandToBeProcessedBy = commandToBeProcessedBy;
    }

}


