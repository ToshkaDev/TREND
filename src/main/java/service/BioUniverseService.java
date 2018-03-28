package service;

import biojobs.BioJobDao;
import biojobs.BioJobResult;
import biojobs.BioJobResultDao;
import springconfiguration.AppProperties;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by vadim on 8/14/17.
 */
public interface BioUniverseService {

    String getWorkingDir();

    String getMultipleWorkingFilesLocation();

    String getBash();

    String getPython();

    String getPrefix();
    
    String getPostfix();

    AppProperties getProperties();

    StorageService getStorageService();

    String getPathToMainDirFromBioProgs();

    String getProgram(String programName);

    BioJobDao getBioJobDao();

    BioJobResultDao getBioJobResultDao();

    void launchProcess(List<String> commandArguments);

}
