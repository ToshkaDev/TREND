package serviceimpl;


import biojobs.BioJob;
import biojobs.BioJobResult;
import biojobs.BioJobResultDao;
import enums.Status;
import exceptions.IncorrectRequestException;
import model.internal.ProtoTreeInternal;
import model.request.ProtoTreeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import service.BioUniverseService;
import service.StorageService;
import springconfiguration.AppProperties;
import biojobs.BioJobDao;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;
import static converters.ConverterMain.fromProtoTreeRequestToProtoTreeInternal;

/**
 * Created by vadim on 8/14/17.
 */
@Service
public class BioUniverseServiceImpl implements BioUniverseService {
    private final int defaultLastJobId = 1;
    private final AppProperties properties;
    private final StorageService storageService;
    private final BioJobResultDao bioJobResultDao;
    private final BioJobDao bioJobDao;
    private final String stageArray[] = {"Conducting Bootstrap Test", "Computing distances", "Optimizing initial tree",
            "Searching ML tree", "Optimizing final tree", "Bootstrapping Tree", "Analysis Complete"};
    @Autowired
    public BioUniverseServiceImpl(StorageService storageService, AppProperties properties, BioJobResultDao bioJobResultDao, BioJobDao bioJobDao) {
        this.storageService = storageService;
        this.properties = properties;
        this.bioJobResultDao = bioJobResultDao;
        this.bioJobDao = bioJobDao;
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
    public BioJobDao getBioJobDao() {
        return bioJobDao;
    }
    @Override
    public BioJobResultDao getBioJobResultDao() {
        return bioJobResultDao;
    }
    @Override
    public ProtoTreeInternal storeFileAndGetInternalRepresentation(final ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {
        String firstFileName = storeAndGetFileName(protoTreeRequest.getFirstFile(), protoTreeRequest.getFirstFileArea());
        String secondFileName = storeAndGetFileName(protoTreeRequest.getSecondFile(), protoTreeRequest.getSecondFileArea());

        String alignedFileName = storeAndGetFileName(protoTreeRequest.getAlignmentFile(), null);
        String treeFileName = storeAndGetFileName(protoTreeRequest.getTreeFile(), protoTreeRequest.getTreeFileArea());

        return fromProtoTreeRequestToProtoTreeInternal(protoTreeRequest, firstFileName,
                secondFileName, alignedFileName, treeFileName);
    }
    @Override
    public String storeAndGetFileName(final MultipartFile multipartFile, final String fileArea) throws IncorrectRequestException {
        String fileName = null;
        if (multipartFile != null) {
            if (!isNullOrEmpty(fileArea)) {
                throw new IncorrectRequestException("fileTextArea and fileName are both not empty");
            } else {
                fileName = getStorageService().store(multipartFile);
            }
        } else if (!isNullOrEmpty(fileArea)) {
            fileName = getStorageService().createAndStore(fileArea);
        }
        return fileName;
    }

    @Override
    public String getRandomFileName(String postfix) {
        if (postfix == null)
            return getPrefix() + UUID.randomUUID().toString() + getPostfix();
        else if (postfix.equals("noPostfix"))
            return getPrefix() + UUID.randomUUID().toString();
        else
            return getPrefix() + UUID.randomUUID().toString() + postfix;
    }

    @Override
    public String[] prepareInterpreters(Integer intepreterNum) {
        String[] arrayOfInterpreters = new String[intepreterNum];
        for (int i=0; i < intepreterNum; i++) {
            arrayOfInterpreters[i] = getPython();
        }
        return arrayOfInterpreters;
    }

    @Override
    public void prepareCommandArgumentsCommon(ProtoTreeInternal protoTreeInternal, String[] arrayOfInterpreters,
                                              String[] arrayOfPrograms, List<List<String>> listOfArgumentLists) {
        List<List<String>> commandsAndArguments = new LinkedList<>();

        for (int i=0; i<arrayOfPrograms.length; i++) {
            List<String> listOfCommandsAndArgs= new LinkedList<>();
            // We need Xserver when rendering ete2 tree on the server
            if (arrayOfPrograms[i].equals(properties.getAddProtFeaturesToTree()))
                listOfCommandsAndArgs.add(properties.getXvfbrun());
            listOfCommandsAndArgs.add(arrayOfInterpreters[i]);
            listOfCommandsAndArgs.add(arrayOfPrograms[i]);
            listOfCommandsAndArgs.addAll(listOfArgumentLists.get(i));
            commandsAndArguments.add(listOfCommandsAndArgs);
        }
        int jobId = saveBioJobToDB(protoTreeInternal);
        protoTreeInternal.setJobId(jobId);
        protoTreeInternal.setCommandsAndArguments(commandsAndArguments);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int saveBioJobToDB(ProtoTreeInternal protoTreeInternal) {
        int jobId = getLastJobId();

        BioJob bioJob = new BioJob();
        bioJob.setProgramNameName(protoTreeInternal.getCommandToBeProcessedBy());
        bioJob.setJobId(jobId);
        bioJob.setJobDate(LocalDateTime.now());
        bioJob.setFinished(false);
        bioJob.setCookieId(protoTreeInternal.getProtoTreeCookies());
        for (String filename : protoTreeInternal.getOutputFilesNames()) {
            BioJobResult bioJobResult = new BioJobResult();
            bioJobResult.setResultFile("placeholder");
            bioJobResult.setResultFileName(filename);
            bioJobResult.setBiojob(bioJob);
            bioJob.addToBioJobResultList(bioJobResult);
        }
        getBioJobDao().save(bioJob);
        return jobId;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveResultToDb(ProtoTreeInternal protoTreeInternal) {
        for (String filename : protoTreeInternal.getOutputFilesNames()) {
            saveResultFileToDB(filename);
        }
        BioJob bioJob = getBioJobDao().findByJobId(protoTreeInternal.getJobId());
        bioJob.setFinished(true);
        getBioJobDao().save(bioJob);
    }

    @Override
    public void saveResultFileToDB(String filename) {
        File file = null;
        try {
            file = getStorageService().loadAsResource(filename).getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder fileAsStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                fileAsStringBuilder.append(line + "\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Can't find file " + file.toString());
        } catch (IOException e) {
            System.out.println("Unable to read file " + file.toString());
        }

        BioJobResult bioJobResult = getBioJobResultDao().findByResultFileName(filename);
        bioJobResult.setResultFile(fileAsStringBuilder.toString());
        getBioJobResultDao().save(bioJobResult);
    }

    @Override
    public void saveStage(ProtoTreeInternal protoTreeInternal, int counter, Map <Integer, String> counterToStageMap) {
        if (counterToStageMap.containsKey(counter)) {
            BioJob bioJob = getBioJobDao().findByJobId(protoTreeInternal.getJobId());
            bioJob.setStage(counterToStageMap.get(counter));
            getBioJobDao().save(bioJob);
        }
    }

    public void saveError(ProtoTreeInternal protoTreeInternal, String errorMessage) {
        BioJob bioJob = getBioJobDao().findByJobId(protoTreeInternal.getJobId());
        if (errorMessage != null)
            bioJob.setStage(errorMessage);
        else
            bioJob.setStage(Status.error.getStatusEnum());
        getBioJobDao().save(bioJob);
    }

    private void saveStageDetails(ProtoTreeInternal protoTreeInternal, String stageDetails) {
        BioJob bioJob = getBioJobDao().findByJobId(protoTreeInternal.getJobId());
        bioJob.setStageDetails(stageDetails);
        getBioJobDao().save(bioJob);
    }


    @Override
    public Integer getLastJobId() {
        Integer lastJobId = getBioJobDao().getLastJobId();
        return lastJobId != null ? lastJobId + 1 : defaultLastJobId;
    }

    @Override
    public void launchProcess(List<String> commandArguments, ProtoTreeInternal protoTreeInternal) throws IncorrectRequestException {
        ProcessBuilder processBuilder = new ProcessBuilder(commandArguments).redirectErrorStream(true);
        processBuilder.directory(new File(getWorkingDir()));
        try {
            System.out.println("processBuilder.directory() " + processBuilder.directory());
            System.out.println(processBuilder.command());

            Process process = processBuilder.start();

            BufferedReader iebr = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String megaErrorMessage = null;
            boolean errorHappened = false;
            while ((line = iebr.readLine()) != null) {
                if (line.contains("Error") && line.contains("Message"))
                    megaErrorMessage = line;
                if (line.toLowerCase().contains("error") || line.contains("Traceback"))
                    errorHappened = true;
                for (int i=0; i < stageArray.length; i++) {
                    if(line.contains(stageArray[i])) {
                        saveStageDetails(protoTreeInternal, line);
                        break;
                    }
                }
                System.out.println("Input/Error stream: " + line);
                System.out.println("\n");
            }
            if (errorHappened || megaErrorMessage != null) {
                if (megaErrorMessage != null)
                    throw new IncorrectRequestException(Status.megaError.getStatusEnum() + ": " + megaErrorMessage.split("=")[1]);
                else
                    throw new IncorrectRequestException("Error happened in launchProcess(List<String> commandArguments).");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("===IOException in subprocess.===");
            throw new IncorrectRequestException("Error happened in launchProcess(List<String> commandArguments).");
        }
    }
}
