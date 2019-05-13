package service;

import biojobs.BioJob;
import biojobs.BioJobDao;
import biojobs.BioJobResult;
import biojobs.BioJobResultDao;
import exceptions.IncorrectRequestException;
import model.internal.ProtoTreeInternal;
import model.request.ProtoTreeRequest;
import org.springframework.web.multipart.MultipartFile;
import springconfiguration.AppProperties;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    BioJobDao getBioJobDao();

    BioJobResultDao getBioJobResultDao();

    void launchProcess(List<String> commandArguments, ProtoTreeInternal protoTreeInternal) throws IncorrectRequestException;

    ProtoTreeInternal storeFileAndGetInternalRepresentation(final ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException;

    String storeAndGetFileName(final MultipartFile multipartFile, final String fileArea) throws IncorrectRequestException;

    String getRandomFileName(String postfix);

    String[] prepareInterpreters(Integer intepreterNum);

    void prepareCommandArgumentsCommon(ProtoTreeInternal protoTreeInternal, String[] arrayOfInterpreters,
                                       String[] arrayOfPrograms, List<List<String>> listOfArgumentLists);

    int saveBioJobToDB(ProtoTreeInternal protoTreeInternal);

    void saveResultToDb(ProtoTreeInternal protoTreeInternal);

    void saveResultFileToDB(String filename);

    Integer getLastJobId();

    void saveStage(ProtoTreeInternal protoTreeInternal, int counter, Map<Integer, String> counterToStageMap);

}
