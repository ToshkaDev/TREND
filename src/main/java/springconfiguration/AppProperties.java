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
    private String getSeqByName = bioProgramsDir + "/getSequencesByNames.py";
    private String makeUnique = bioProgramsDir + "/getUniqueSeqs.py";
    private String prepareNamesProgram = bioProgramsDir + "/prepareNames.sh";
    private String blastAllVsAllProgram = bioProgramsDir + "/blast-all-vs-all.sh";
    private String createCogs = bioProgramsDir + "/createCOGs.py";

    private String alignMultiple = bioProgramsDir + "/align_multiple.sh";
    private String concatenate = bioProgramsDir + "/concatenate.py";

    private String pythonLocation = "/usr/bin/python";
    private String bashLocation = "/bin/bash";
    private String blastpLocation = "/usr/bin/blastp";
    private String resultFilePrefix = "bio-universe-";
    private String postfix = ".txt";

    private String hmmscanDbPath = "/home/Soft/hmmer/pfam31_0/Pfam-A.hmm";
    private String rpsblastDbPath = "/home/vadim/Softs/rpsblastdb/";
    private String rpsprocDbPath = "/home/vadim/Softs/rpsbproc/data/";
    private String rpsblastSpDb = "Cdd_NCBI";

    private String hmmscanPath = "/usr/local/bin/hmmscan";
    private String rpsblastPath = "/home/Soft/blast/ncbi-blast-2.6.0+/bin/rpsblast";
    private String rpsbprocPath = "";
    private String tmhmm2Path = "/home/vadim/Soft/tmhmm-2.0c/bin/tmhmm";


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

    public String getGetSeqByName() {
        return getSeqByName;
    }

    public void setGetSeqByName(String getSeqByName) {
        this.getSeqByName = getSeqByName;
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

    public String getMakeUnique() {
        return makeUnique;
    }

    public void setMakeUnique(String makeUnique) {
        this.makeUnique = makeUnique;
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

    public String getPrepareNamesProgram() {
        return prepareNamesProgram;
    }

    public void setPrepareNamesProgram(String prepareNamesProgram) {
        this.prepareNamesProgram = prepareNamesProgram;
    }

    public String getBlastAllVsAllProgram() {
        return blastAllVsAllProgram;
    }

    public void setBlastAllVsAllProgram(String blastAllVsAllProgram) {
        this.blastAllVsAllProgram = blastAllVsAllProgram;
    }

    public String getCreateCogs() {
        return createCogs;
    }

    public void setCreateCogs(String createCogs) {
        this.createCogs = createCogs;
    }

    public String getPostfix() {
        return postfix;
    }

    public void setPostfix(String postFix) {
        this.postfix = postFix;
    }

    public String getAlignMultiple() {
        return alignMultiple;
    }

    public void setAlignMultiple(String alignMultiple) {
        this.alignMultiple = alignMultiple;
    }

    public String getConcatenate() {
        return concatenate;
    }

    public void setConcatenate(String concatenate) {
        this.concatenate = concatenate;
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

    public String getRpsblastSpDb() {
        return rpsblastSpDb;
    }

    public void setRpsblastSpDb(String rpsblastSpDb) {
        this.rpsblastSpDb = rpsblastSpDb;
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
}
