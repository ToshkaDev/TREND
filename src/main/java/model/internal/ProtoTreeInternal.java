package model.internal;

import java.util.LinkedList;
import java.util.List;

public class ProtoTreeInternal {
    private String firstFileName;
    private String secondFileName;
    private String thirdFileName;

    private String alignmentOutputFileName;
    private String alignmentAlg;
    private String alignThreads;
    private String reorderOrNot;

    private String svgOutputFileName;
    private String newickOutputFileName;
    private String treeBuildMethod;
    private String aaSubstModel;
    private String aaSubstRate;
    private String initialTreeForMl;
    private String gapsAndMissingData;
    private String siteCovCutOff;
    private String phylogenyTest;
    private String numberOrReplicates;
    private String treeThreads;

    private String iFileTypeForDomainsProc;
    private String domainPredictionType;
	private String tmhhmmResultFileName;
	//e-value for Hmmscan and RpsBlast
	private String eValue;
	private String domainPredThreads;

    private String  commandToBeProcessedBy;
    private List<String> fieldsForAlignment = new LinkedList<>();
    private List<String> fieldsForTreeBuild = new LinkedList<>();
    private List<String> fieldsForProtFeature = new LinkedList<>();

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

    public String getNewickOutputFileName() {
        return newickOutputFileName;
    }

    public void setNewickOutputFileName(String newickOutputFileName) {
        this.newickOutputFileName = newickOutputFileName;
    }

    public String getAlignmentOutputFileName() {
        return alignmentOutputFileName;
    }

    public void setAlignmentOutputFileName(String alignmentOutputFileName) {
        this.alignmentOutputFileName = alignmentOutputFileName;
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

    public String getiFileTypeForDomainsProc() {
        return iFileTypeForDomainsProc;
    }

    public void setiFileTypeForDomainsProc(String iFileTypeForDomainsProc) {
        this.iFileTypeForDomainsProc = iFileTypeForDomainsProc;
    }

    public String getDomainPredictionType() {
        return domainPredictionType;
    }

    public void setDomainPredictionType(String domainPredictionType) {
        this.domainPredictionType = domainPredictionType;
    }

    public String getTmhhmmResultFileName() {
        return tmhhmmResultFileName;
    }

    public void setTmhhmmResultFileName(String tmhhmmResultFileName) {
        this.tmhhmmResultFileName = tmhhmmResultFileName;
    }

    public String geteValue() {
        return eValue;
    }

    public void seteValue(String eValue) {
        this.eValue = eValue;
    }

    public String getDomainPredThreads() {
        return domainPredThreads;
    }

    public void setDomainPredThreads(String domainPredThreads) {
        this.domainPredThreads = domainPredThreads;
    }

    public void setFields() {
        fieldsForAlignment.add(getAlignmentOutputFileName());
        fieldsForAlignment.add(getAlignmentAlg());
        fieldsForAlignment.add(getAlignThreads());
        fieldsForAlignment.add(getReorderOrNot());
        fieldsForTreeBuild.add(getSvgOutputFileName());
        fieldsForTreeBuild.add(getNewickOutputFileName());
        fieldsForTreeBuild.add(getTreeBuildMethod());
        fieldsForTreeBuild.add(getAaSubstModel());
        fieldsForTreeBuild.add(getAaSubstRate());
        fieldsForTreeBuild.add(getInitialTreeForMl());
        fieldsForTreeBuild.add(getGapsAndMissingData());
        fieldsForTreeBuild.add(getSiteCovCutOff());
        fieldsForTreeBuild.add(getPhylogenyTest());
        fieldsForTreeBuild.add(getNumberOrReplicates());
        fieldsForTreeBuild.add(getTreeThreads());
        fieldsForProtFeature.add(getiFileTypeForDomainsProc());
        fieldsForProtFeature.add(getDomainPredictionType());
        fieldsForProtFeature.add(getTmhhmmResultFileName());
        fieldsForProtFeature.add(geteValue());
        fieldsForProtFeature.add(getDomainPredThreads());

        allFields.addAll(fieldsForAlignment);
        allFields.addAll(fieldsForTreeBuild);
        allFields.addAll(fieldsForProtFeature);

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

    public String getSvgOutputFileName() {
        return svgOutputFileName;
    }

    public void setSvgOutputFileName(String svgOutputFileName) {
        this.svgOutputFileName = svgOutputFileName;
    }
}
