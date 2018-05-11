package springconfiguration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import java.util.UUID;

@ConfigurationProperties("program")
@PropertySource("classpath:application.properties")
public class AppProperties {

    private String workingDirLocation = "bioinformatics-programs-workingDir";
    private String bioProgramsDir = "../bioinformatics-programs";
    private String pathToMainDirFromBioProgs = "../";

    private String prepareNames = bioProgramsDir + "/prepareNames.py";
    private String calculateProteinFeatures = bioProgramsDir + "/getDomainsFromHMMScanOrRpsAndTm.py";
    private String alignAndBuildTree = bioProgramsDir + "/alignAndCunstructTree.py";
    private String addProtFeaturesToTree = bioProgramsDir + "/orderSequencesBasedOnTree_andAddDomains.py";

    public String getProtoTreeProgram() {
        return getAddProtFeaturesToTree();
    }

    private String pythonLocation = "/usr/bin/python";
    private String bashLocation = "/bin/bash";
    private String blastpLocation = "/usr/bin/blastp";
    private String resultFilePrefix = "proto-tree-";
    private String postfix = ".txt";

    private String hmmscanDbPath = "/home/vadim/UTOakRidge/Soft/hmmer3_data/";
    private String rpsblastDbPath = "/home/vadim/Softs/rpsblastdb/";
    private String rpsprocDbPath = "/home/vadim/Softs/rpsbproc/data/";
    private String pfam = "Pfam31/Pfam-A.hmm";
    private String rpsblastCdd = "Cdd_NCBI";
    private String rpsblastCog = "Cog";
    private String rpsblastKog = "Kog";
    private String rpsblastPfam = "Pfam";
    private String rpsblastPrk = "Prk";
    private String rpsblastSmart = "Smart";
    private String rpsblastTigr = "Tigr";
    private String hmmscanPath = "/usr/local/bin/hmmer3/bin/hmmscan";
    private String rpsblastPath = "/home/vadim/Softs/ncbi-blast-2.6.0+/bin/rpsblast";
    private String rpsbprocPath = "/home/vadim/bin/rpsbproc";
    private String tmhmm2Path = "/home/vadim/Softs/tmhmm-2.0c/bin/tmhmm";
    private String mafft = "/usr/bin/mafft";
    private String megacc = "/usr/bin/megacc";


    public String getWorkingDirLocation() {
        return workingDirLocation;
    }

    public void setWorkingDirLocation(String workingDirLocation) {
        this.workingDirLocation = workingDirLocation;
    }

    public String getMultipleWorkingFilesLocation() {
        return "files-" + UUID.randomUUID().toString();
    }

    public String getBioProgramsDir() {
        return bioProgramsDir;
    }

    public void setBioProgramsDir(String bioProgramsDir) {
        this.bioProgramsDir = bioProgramsDir;
    }

    public String getPathToMainDirFromBioProgs() {
        return pathToMainDirFromBioProgs;
    }

    public void setPathToMainDirFromBioProgs(String pathToMainDirFromBioProgs) {
        this.pathToMainDirFromBioProgs = pathToMainDirFromBioProgs;
    }

    public String getPythonLocation() {
        return pythonLocation;
    }

    public void setPythonLocation(String pythonLocation) {
        this.pythonLocation = pythonLocation;
    }

    public String getResultFilePrefix() {
        return resultFilePrefix;
    }

    public void setResultFilePrefix(String resultFilePrefix) {
        this.resultFilePrefix = resultFilePrefix;
    }

    public String getBashLocation() {
        return bashLocation;
    }

    public void setBashLocation(String bashLocation) {
        this.bashLocation = bashLocation;
    }

    public String getBlastpLocation() {
        return blastpLocation;
    }

    public void setBlastpLocation(String blastpLocation) {
        this.blastpLocation = blastpLocation;
    }

    public String getPostfix() {
        return postfix;
    }

    public void setPostfix(String postFix) {
        this.postfix = postFix;
    }

    public String getPrepareNames() {
        return prepareNames;
    }

    public void setPrepareNames(String prepareNames) {
        this.prepareNames = prepareNames;
    }

    public String getCalculateProteinFeatures() {
        return calculateProteinFeatures;
    }

    public void setCalculateProteinFeatures(String calculateProteinFeatures) {
        this.calculateProteinFeatures = calculateProteinFeatures;
    }

    public String getAlignAndBuildTree() {
        return alignAndBuildTree;
    }

    public void setAlignAndBuildTree(String alignAndBuildTree) {
        this.alignAndBuildTree = alignAndBuildTree;
    }

    public String getAddProtFeaturesToTree() {
        return addProtFeaturesToTree;
    }

    public void setAddProtFeaturesToTree(String addProtFeaturesToTree) {
        this.addProtFeaturesToTree = addProtFeaturesToTree;
    }

    public String getHmmscanDbPath() {
        return hmmscanDbPath;
    }

    public void setHmmscanDbPath(String hmmscanDbPath) {
        this.hmmscanDbPath = hmmscanDbPath;
    }

    public String getRpsblastDbPath() {
        return rpsblastDbPath;
    }

    public void setRpsblastDbPath(String rpsblastDbPath) {
        this.rpsblastDbPath = rpsblastDbPath;
    }

    public String getRpsprocDbPath() {
        return rpsprocDbPath;
    }

    public void setRpsprocDbPath(String rpsprocDbPath) {
        this.rpsprocDbPath = rpsprocDbPath;
    }

    public String getPfam() {
        return pfam;
    }

    public void setPfam(String pfam) {
        this.pfam = pfam;
    }

    public String getRpsblastCdd() {
        return rpsblastCdd;
    }

    public void setRpsblastCdd(String rpsblastCdd) {
        this.rpsblastCdd = rpsblastCdd;
    }

    public String getRpsblastCog() {
        return rpsblastCog;
    }

    public void setRpsblastCog(String rpsblastCog) {
        this.rpsblastCog = rpsblastCog;
    }

    public String getRpsblastKog() {
        return rpsblastKog;
    }

    public void setRpsblastKog(String rpsblastKog) {
        this.rpsblastKog = rpsblastKog;
    }

    public String getRpsblastPfam() {
        return rpsblastPfam;
    }

    public void setRpsblastPfam(String rpsblastPfam) {
        this.rpsblastPfam = rpsblastPfam;
    }

    public String getRpsblastPrk() {
        return rpsblastPrk;
    }

    public void setRpsblastPrk(String rpsblastPrk) {
        this.rpsblastPrk = rpsblastPrk;
    }

    public String getRpsblastSmart() {
        return rpsblastSmart;
    }

    public void setRpsblastSmart(String rpsblastSmart) {
        this.rpsblastSmart = rpsblastSmart;
    }

    public String getRpsblastTigr() {
        return rpsblastTigr;
    }

    public void setRpsblastTigr(String rpsblastTigr) {
        this.rpsblastTigr = rpsblastTigr;
    }

    public String getHmmscanPath() {
        return hmmscanPath;
    }

    public void setHmmscanPath(String hmmscanPath) {
        this.hmmscanPath = hmmscanPath;
    }

    public String getRpsblastPath() {
        return rpsblastPath;
    }

    public void setRpsblastPath(String rpsblastPath) {
        this.rpsblastPath = rpsblastPath;
    }

    public String getRpsbprocPath() {
        return rpsbprocPath;
    }

    public void setRpsbprocPath(String rpsbprocPath) {
        this.rpsbprocPath = rpsbprocPath;
    }

    public String getTmhmm2Path() {
        return tmhmm2Path;
    }

    public void setTmhmm2Path(String tmhmm2Path) {
        this.tmhmm2Path = tmhmm2Path;
    }

    public String getMafft() {
        return mafft;
    }

    public void setMafft(String mafft) {
        this.mafft = mafft;
    }

    public String getMegacc() {
        return megacc;
    }

    public void setMegacc(String megacc) {
        this.megacc = megacc;
    }
}
