package serviceimpl;


import biojobs.BioJobResult;
import biojobs.BioJobResultDao;
import enums.BioPrograms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.BioUniverseService;
import service.StorageService;
import springconfiguration.AppProperties;
import biojobs.BioJobDao;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vadim on 8/14/17.
 */
@Service
public class BioUniverseServiceImpl implements BioUniverseService {
    private final AppProperties properties;
    private final StorageService storageService;
    private final BioJobResultDao bioJobResultDao;
    private final BioJobDao bioJobDao;
    private final Map<String, String> programs = new HashMap<>();

    @Autowired
    public BioUniverseServiceImpl(StorageService storageService, AppProperties properties, BioJobResultDao bioJobResultDao, BioJobDao bioJobDao) {
        this.storageService = storageService;
        this.properties = properties;
        this.bioJobResultDao = bioJobResultDao;
        this.bioJobDao = bioJobDao;
        programs.put(BioPrograms.CREATE_COGS.getProgramName(), properties.getCreateCogs());
        programs.put(BioPrograms.MAKE_UNIQUE.getProgramName(), properties.getMakeUnique());
        programs.put(BioPrograms.GET_SEQ_BYNAME.getProgramName(), properties.getGetSeqByName());
        programs.put(BioPrograms.CONCATENATE.getProgramName(), properties.getConcatenate());
    }

    @Override
    public String getWorkingDir() {
        return properties.getWorkingDirLocation();
    }
    @Override
    public String getMultipleWorkingFilesLocation() {
        return properties.getMultipleWorkingFilesLocation();
    }
    @Override
    public String getBash() {
        return properties.getBashLocation();
    }
    @Override
    public String getPython() {
        return properties.getPythonLocation();
    }
    @Override
    public String getPrefix() {
        return properties.getResultFilePrefix();
    }
    @Override
    public String getPostfix() {
        return properties.getPostfix();
    }
    @Override
    public AppProperties getProperties() {
        return properties;
    }
    @Override
    public StorageService getStorageService() {
        return storageService;
    }
    @Override
    public String getPathToMainDirFromBioProgs() {
        return properties.getPathToMainDirFromBioProgs();
    }
    @Override
    public String getProgram(String programName) {
        return programs.get(programName);
    }
    @Override
    public BioJobDao getBioJobDao() {
        return bioJobDao;
    }
    @Override
    public BioJobResultDao getBioJobResultDao() {
        return bioJobResultDao;
    }
    @Override
    public void launchProcess(List<String> commandArguments) {
        ProcessBuilder processBuilder = new ProcessBuilder(commandArguments);
        processBuilder.directory(new File(getWorkingDir()));
        try {
            System.out.println("processBuilder.directory() " + processBuilder.directory());
            System.out.println(processBuilder.command());

            Process process = processBuilder.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                System.out.println("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
