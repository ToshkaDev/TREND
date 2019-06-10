package springconfiguration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import java.security.SecureRandom;
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
    private String enumerate = bioProgramsDir + "/orderSequencesBasedOnTree.py";
    private String geneNeighbors = bioProgramsDir + "/gene_neighbors.py";
    private String reduceWithCdHit = bioProgramsDir + "/runCdHit.py";

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
    private String pfam = "Pfam-A.hmm";
    private String pfamAndMist = "Pfam-A_and_Mist-specific.hmm";
    private String rpsblastCddSuper = "Cdd";
    private String rpsblastCddNcbi = "Cdd_NCBI";
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
    private String segmaskerPath = "/home/vadim/Softs/ncbi-blast-2.6.0+/bin/segmasker";
    private String mafft = "/usr/bin/mafft";
    private String megacc = "/usr/bin/megacc";
    private String cdhit = "/usr/local/bin/cd-hit";
    private String cdhitThreadNum = "4";
    //2000 MB
    private String cdhitMemory = "2000";

    private String fetchFromMistProcNum = "50";
    private String megaThreadNum = "7";
    private String mafftThreadNum = "7";
    private String hmmscanThreadNum = "10";
    private String fetchFromMist = "true";
    private String fetchFromNCBI = "false";


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

    public String getEnumerate() {
        return enumerate;
    }

    public void setEnumerate(String enumerate) {
        this.enumerate = enumerate;
    }

    public String getGeneNeighbors() {
        return geneNeighbors;
    }

    public void setGeneNeighbors(String geneNeighbors) {
        this.geneNeighbors = geneNeighbors;
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

    public String getRpsblastCddNcbi() {
        return rpsblastCddNcbi;
    }

    public void setRpsblastCddNcbi(String rpsblastCddNcbi) {
        this.rpsblastCddNcbi = rpsblastCddNcbi;
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

    public String getSegmaskerPath() {
        return segmaskerPath;
    }

    public void setSegmaskerPath(String segmaskerPath) {
        this.segmaskerPath = segmaskerPath;
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

    public String getRpsblastCddSuper() {
        return rpsblastCddSuper;
    }

    public void setRpsblastCddSuper(String rpsblastCddSuper) {
        this.rpsblastCddSuper = rpsblastCddSuper;
    }

    public String getPfamAndMist() {
        return pfamAndMist;
    }

    public void setPfamAndMist(String pfamAndMist) {
        this.pfamAndMist = pfamAndMist;
    }

    public String getFetchFromMistProcNum() {
        return fetchFromMistProcNum;
    }

    public void setFetchFromMistProcNum(String fetchFromMistProcNum) {
        this.fetchFromMistProcNum = fetchFromMistProcNum;
    }

    public String getMegaThreadNum() {
        return megaThreadNum;
    }

    public void setMegaThreadNum(String megaThreadNum) {
        this.megaThreadNum = megaThreadNum;
    }

    public String getMafftThreadNum() {
        return mafftThreadNum;
    }

    public void setMafftThreadNum(String mafftThreadNum) {
        this.mafftThreadNum = mafftThreadNum;
    }

    public String getHmmscanThreadNum() {
        return hmmscanThreadNum;
    }

    public void setHmmscanThreadNum(String hmmscanThreadNum) {
        this.hmmscanThreadNum = hmmscanThreadNum;
    }

    public String getFetchFromMist() {
        return fetchFromMist;
    }

    public void setFetchFromMist(String fetchFromMist) {
        this.fetchFromMist = fetchFromMist;
    }

    public String getFetchFromNCBI() {
        return fetchFromNCBI;
    }

    public void setFetchFromNCBI(String fetchFromNCBI) {
        this.fetchFromNCBI = fetchFromNCBI;
    }

    public String getCdhit() {
        return cdhit;
    }

    public void setCdhit(String cdhit) {
        this.cdhit = cdhit;
    }

    public String getCdhitThreadNum() {
        return cdhitThreadNum;
    }

    public void setCdhitThreadNum(String cdhitThreadNum) {
        this.cdhitThreadNum = cdhitThreadNum;
    }

    public String getCdhitMemory() {
        return cdhitMemory;
    }

    public void setCdhitMemory(String cdhitMemory) {
        this.cdhitMemory = cdhitMemory;
    }

    public String getReduceWithCdHit() {
        return reduceWithCdHit;
    }

    public void setReduceWithCdHit(String reduceWithCdHit) {
        this.reduceWithCdHit = reduceWithCdHit;
    }
}
