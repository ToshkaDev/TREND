package serviceimpl;

import static com.google.common.base.Strings.isNullOrEmpty;
import static converters.ConverterMain.fromProtoTreeRequestToProtoTreeInternal;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import biojobs.BioJob;
import biojobs.BioJobDao;
import biojobs.BioJobResult;
import biojobs.BioJobResultDao;
import enums.ParamPrefixes;
import model.internal.ProtoTreeInternal;
import model.request.ProtoTreeRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import service.EvolutionService;
import service.StorageService;
import exceptions.IncorrectRequestException;
import springconfiguration.AppProperties;

@Service

public class EvolutionServiceImpl extends BioUniverseServiceImpl implements EvolutionService {
	private final int defaultLastJobId = 1;
	private Map<Integer, String> counterToStage = new HashMap<>();
    private Map<Integer, String> counterToStagePartial = new HashMap<>();


	public EvolutionServiceImpl(final StorageService storageService, final AppProperties properties, final BioJobDao bioJobDao, final BioJobResultDao bioJobResultDao) {
		super(storageService, properties, bioJobResultDao, bioJobDao);
        counterToStage.put(1, "['Predicting proteins features.']");
        counterToStage.put(2, "['Predicting proteins features.', 'Aligning and building tree.']");
        counterToStage.put(3, "['Predicting proteins features.', 'Aligning and building tree.', 'Ordering alignment and putting features and tree together.-last']");
        counterToStagePartial.put(1, "['Predicting proteins features.']");
        counterToStagePartial.put(2, "['Predicting proteins features.', 'Ordering alignment and putting features and tree together.-last']");
	}

	private String getDomainPredictionDb(String dbName) {
	    String db = dbName.split(" ")[0] + " ";
        switch (dbName.split(" ")[1]) {
            case "cdd":
                db = db + super.getProperties().getRpsblastCddSuper();
                break;
            case "cdd_ncbi":
                db = db + super.getProperties().getRpsblastCddNcbi();
                break;
            case "pfam":
                db = db + super.getProperties().getRpsblastPfam();
                break;
            case "cog":
                db = db + super.getProperties().getRpsblastCog();
                break;
            case "kog":
                db = db + super.getProperties().getRpsblastKog();
                break;
            case "smart":
                db = db + super.getProperties().getRpsblastSmart();
                break;
            case "prk":
                db = db + super.getProperties().getRpsblastPrk();
                break;
            case "tigr":
                db = db + super.getProperties().getRpsblastTigr();
                break;
            case "pfam31":
                db = db + super.getProperties().getPfam();
        }
        return db;
    }

    @Override
    public BioJob getBioJob(int jobId) {
	    return super.getBioJobDao().findByJobId(jobId);
    }

    public ProtoTreeInternal storeFilesAndPrepareCommandArguments(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {
        ProtoTreeInternal protoTreeInternal;
	    if (protoTreeRequest.isFullPipeline().equals("true")) {
            protoTreeInternal = fullPipelineProcessing(protoTreeRequest);

        } else {
            protoTreeInternal = partialPipelineProcessing(protoTreeRequest);
        }
        return protoTreeInternal;
    }

    private ProtoTreeInternal fullPipelineProcessing(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {
        ProtoTreeInternal protoTreeInternal = storeFileAndGetInternalRepresentation(protoTreeRequest);
        List<String> listOfPrograms = new LinkedList<>();
        List<List<String>> listOfArgumentLists = new LinkedList<>();

        List<String> argsForPrepareNames = new LinkedList<>();
        List<String> argsForPrepareNamesSecond = new LinkedList<>();
        List<String> argsForProteinFeatures = new LinkedList<>();
        List<String> argsForAlignmentAndTree = new LinkedList<>();
        List<String> argsForTreeWithDomains = new LinkedList<>();

        String firstPreparedFile = getRandomFileName();
        argsForPrepareNames.addAll(Arrays.asList(protoTreeInternal.getFirstFileName(), ParamPrefixes.OUTPUT.getPrefix() + firstPreparedFile));
        protoTreeInternal.setFirstFileName(ParamPrefixes.INPUT.getPrefix() + firstPreparedFile);
        String inputFileNameForProtFeatures = protoTreeInternal.getFirstFileName();
        listOfPrograms.add(super.getProperties().getPrepareNames());
        listOfArgumentLists.add(argsForPrepareNames);

        if (protoTreeInternal.getSecondFileName() != null) {
            String secondPreparedFile = getRandomFileName();
            argsForPrepareNamesSecond.addAll(Arrays.asList(protoTreeInternal.getSecondFileName(), ParamPrefixes.OUTPUT.getPrefix() + secondPreparedFile));
            protoTreeInternal.setSecondFileName(ParamPrefixes.INPUT.getPrefix() + secondPreparedFile);
            inputFileNameForProtFeatures = protoTreeInternal.getSecondFileName();
            listOfPrograms.add(super.getProperties().getPrepareNames());
            listOfArgumentLists.add(argsForPrepareNamesSecond);
        }

        protoTreeInternal.setFields();

        String hmmscanOrRpsbOutFile = getRandomFileName();
        String rpsbProcOutFile = getRandomFileName();

        String tmhmmscanOutFile = getRandomFileName();
        String proteinFeaturesOutFile = getRandomFileName();
        String segmakserOutFile = getRandomFileName();

        String numberOfThreadsForProtFeatures = "4";
        String numberOfThreadsForTree = "4";
        String numberOfThreadsForAlgn = "4";
        argsForProteinFeatures.addAll(protoTreeInternal.getFieldsForFeaturesPrediction());
        argsForProteinFeatures.addAll(Arrays.asList(
                inputFileNameForProtFeatures,
                getDomainPredictionDb(protoTreeInternal.getDomainPredictionDb()),
                ParamPrefixes.OUTPUT_FOURTH.getPrefix() + hmmscanOrRpsbOutFile,
                ParamPrefixes.OUTPUT_FIFTH.getPrefix() + rpsbProcOutFile,
                ParamPrefixes.OUTPUT_SIXTH.getPrefix() + tmhmmscanOutFile,
                ParamPrefixes.OUTPUT_SEVENTH.getPrefix() + segmakserOutFile,
                ParamPrefixes.HMMSCAN_DB_PATH.getPrefix() + super.getProperties().getHmmscanDbPath(),
                ParamPrefixes.RPSBLAST_DB_PATH.getPrefix() + super.getProperties().getRpsblastDbPath(),
                ParamPrefixes.RPSBPROC_DB_PATH.getPrefix() + super.getProperties().getRpsprocDbPath(),
                ParamPrefixes.HMMSCAN_PATH.getPrefix() + super.getProperties().getHmmscanPath(),
                ParamPrefixes.RPSBLAST_PATH.getPrefix() + super.getProperties().getRpsblastPath(),
                ParamPrefixes.RPSBPROC_PATH.getPrefix() + super.getProperties().getRpsbprocPath(),
                ParamPrefixes.TMHMM_PATH.getPrefix() + super.getProperties().getTmhmm2Path(),
                ParamPrefixes.SEGMASKER_PATH.getPrefix() + super.getProperties().getSegmaskerPath(),
                ParamPrefixes.THREAD.getPrefix() + numberOfThreadsForProtFeatures,
                ParamPrefixes.OUTPUT_THIRD.getPrefix() + proteinFeaturesOutFile
        ));

        String outAlgnFile = super.getPrefix() + UUID.randomUUID().toString() + ".fa";
        String outNewickTree = super.getPrefix() + UUID.randomUUID().toString();
        argsForAlignmentAndTree.addAll(protoTreeInternal.getFieldsForAlignmentAndTreeBuild());
        argsForAlignmentAndTree.addAll(Arrays.asList(
                ParamPrefixes.MAFFT_PATH.getPrefix() + super.getProperties().getMafft(),
                ParamPrefixes.MEGACC_PATH.getPrefix() + super.getProperties().getMegacc(),
                ParamPrefixes.OUTPUT_PARAMS.getPrefix() + super.getPrefix() + UUID.randomUUID().toString() + super.getPostfix(),
                ParamPrefixes.OUTPUT_TREE.getPrefix() + outNewickTree,
                ParamPrefixes.THREAD_ALGN.getPrefix() + numberOfThreadsForAlgn,
                ParamPrefixes.THREAD.getPrefix() + numberOfThreadsForTree,
                ParamPrefixes.OUTPUT.getPrefix() + outAlgnFile
        ));

        String outNewickFile = super.getPrefix() + UUID.randomUUID().toString() + ".newick";
        String outSvgFile = super.getPrefix() + UUID.randomUUID().toString() + ".svg";
        String outOrderedAlgnFile = super.getPrefix() + UUID.randomUUID().toString() + ".fa";

        protoTreeInternal.setOutputFilesNames(Arrays.asList(outNewickFile, outSvgFile, outOrderedAlgnFile, proteinFeaturesOutFile));

        argsForTreeWithDomains.addAll(Arrays.asList(
                inputFileNameForProtFeatures,
                ParamPrefixes.INPUT_SECOND.getPrefix() + outAlgnFile,
                ParamPrefixes.INPUT_THIRD.getPrefix() + outNewickTree + ".nwk",
                ParamPrefixes.INPUT_FOURTH.getPrefix() + proteinFeaturesOutFile,
                ParamPrefixes.OUTPUT.getPrefix() + outOrderedAlgnFile,
                ParamPrefixes.OUTPUT_SECOND.getPrefix() + outSvgFile,
                ParamPrefixes.OUTPUT_THIRD.getPrefix() + outNewickFile
        ));

        listOfPrograms.addAll(Arrays.asList(
                super.getProperties().getCalculateProteinFeatures(),
                super.getProperties().getAlignAndBuildTree(),
                super.getProgram(protoTreeInternal.getCommandToBeProcessedBy())
        ));

        String[] arrayOfInterpreters = prepareInterpreters(listOfPrograms.size());
        String[] arrayOfPrograms = listOfPrograms.toArray(new String[listOfPrograms.size()]);

        listOfArgumentLists.addAll(Arrays.asList(
                argsForProteinFeatures,
                argsForAlignmentAndTree,
                argsForTreeWithDomains
        ));

        prepareCommandArgumentsCommon(protoTreeInternal, arrayOfInterpreters, arrayOfPrograms, listOfArgumentLists);

        return protoTreeInternal;
    }

    private ProtoTreeInternal partialPipelineProcessing(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {
        ProtoTreeInternal protoTreeInternal = storeFileAndGetInternalRepresentation(protoTreeRequest);
        List<String> argsForPrepareNames = new LinkedList<>();
        List<String> argsForProteinFeatures = new LinkedList<>();
        List<String> argsForTreeWithDomains = new LinkedList<>();

        String firstPreparedFile = getRandomFileName();
        argsForPrepareNames.addAll(Arrays.asList(protoTreeInternal.getFirstFileName(), ParamPrefixes.OUTPUT.getPrefix() + firstPreparedFile));
        protoTreeInternal.setFirstFileName(ParamPrefixes.INPUT.getPrefix() + firstPreparedFile);
        String inputFileNameForProtFeatures = protoTreeInternal.getFirstFileName();

        protoTreeInternal.setFields();

        String hmmscanOrRpsbOutFile = getRandomFileName();
        String rpsbProcOutFile = getRandomFileName();

        String tmhmmscanOutFile = getRandomFileName();
        String proteinFeaturesOutFile = getRandomFileName();
        String segmakserOutFile = getRandomFileName();

        String numberOfThreadsForProtFeatures = "4";
        String numberOfThreadsForTree = "4";
        argsForProteinFeatures.addAll(protoTreeInternal.getFieldsForFeaturesPrediction());
        argsForProteinFeatures.addAll(Arrays.asList(
                inputFileNameForProtFeatures,
                getDomainPredictionDb(protoTreeInternal.getDomainPredictionDb()),
                ParamPrefixes.OUTPUT_FOURTH.getPrefix() + hmmscanOrRpsbOutFile,
                ParamPrefixes.OUTPUT_FIFTH.getPrefix() + rpsbProcOutFile,
                ParamPrefixes.OUTPUT_SIXTH.getPrefix() + tmhmmscanOutFile,
                ParamPrefixes.OUTPUT_SEVENTH.getPrefix() + segmakserOutFile,
                ParamPrefixes.HMMSCAN_DB_PATH.getPrefix() + super.getProperties().getHmmscanDbPath(),
                ParamPrefixes.RPSBLAST_DB_PATH.getPrefix() + super.getProperties().getRpsblastDbPath(),
                ParamPrefixes.RPSBPROC_DB_PATH.getPrefix() + super.getProperties().getRpsprocDbPath(),
                ParamPrefixes.HMMSCAN_PATH.getPrefix() + super.getProperties().getHmmscanPath(),
                ParamPrefixes.RPSBLAST_PATH.getPrefix() + super.getProperties().getRpsblastPath(),
                ParamPrefixes.RPSBPROC_PATH.getPrefix() + super.getProperties().getRpsbprocPath(),
                ParamPrefixes.TMHMM_PATH.getPrefix() + super.getProperties().getTmhmm2Path(),
                ParamPrefixes.SEGMASKER_PATH.getPrefix() + super.getProperties().getSegmaskerPath(),
                ParamPrefixes.THREAD.getPrefix() + numberOfThreadsForProtFeatures,
                ParamPrefixes.OUTPUT_THIRD.getPrefix() + proteinFeaturesOutFile
        ));

        String outAlgnFile = protoTreeInternal.getAlignmentFile();
        String outNewickTree = protoTreeInternal.getTreeFile();

        String outNewickFile = super.getPrefix() + UUID.randomUUID().toString() + ".newick";
        String outSvgFile = super.getPrefix() + UUID.randomUUID().toString() + ".svg";

        if (outAlgnFile != null) {
            String outOrderedAlgnFile = super.getPrefix() + UUID.randomUUID().toString() + ".fa";
            protoTreeInternal.setOutputFilesNames(Arrays.asList(outNewickFile, outSvgFile, outOrderedAlgnFile, proteinFeaturesOutFile));
            argsForTreeWithDomains.add(ParamPrefixes.INPUT_SECOND.getPrefix() + outAlgnFile);
        } else {
            protoTreeInternal.setOutputFilesNames(Arrays.asList(outNewickFile, outSvgFile, proteinFeaturesOutFile));
        }

        argsForTreeWithDomains.addAll(Arrays.asList(
                inputFileNameForProtFeatures,
                ParamPrefixes.INPUT_THIRD.getPrefix() + outNewickTree,
                ParamPrefixes.INPUT_FOURTH.getPrefix() + proteinFeaturesOutFile,
                ParamPrefixes.OUTPUT_SECOND.getPrefix() + outSvgFile,
                ParamPrefixes.OUTPUT_THIRD.getPrefix() + outNewickFile
        ));

        List<String> listOfPrograms = new LinkedList<>();
        List<List<String>> listOfArgumentLists = new LinkedList<>();

        listOfPrograms.add(super.getProperties().getPrepareNames());
        listOfPrograms.addAll(Arrays.asList(
                super.getProperties().getCalculateProteinFeatures(),
                super.getProgram(protoTreeInternal.getCommandToBeProcessedBy())
        ));

        String[] arrayOfInterpreters = prepareInterpreters(listOfPrograms.size());
        String[] arrayOfPrograms = listOfPrograms.toArray(new String[listOfPrograms.size()]);

        listOfArgumentLists.add(argsForPrepareNames);
        listOfArgumentLists.addAll(Arrays.asList(
                argsForProteinFeatures,
                argsForTreeWithDomains
        ));
        prepareCommandArgumentsCommon(protoTreeInternal, arrayOfInterpreters, arrayOfPrograms, listOfArgumentLists);
        return protoTreeInternal;
    }

    private String[] prepareInterpreters(Integer intepreterNum) {
        String[] arrayOfInterpreters = new String[intepreterNum];
        for (int i=0; i < intepreterNum; i++) {
            arrayOfInterpreters[i] = super.getPython();
        }
	    return arrayOfInterpreters;
    }


    public void prepareCommandArgumentsCommon(ProtoTreeInternal protoTreeInternal, String[] arrayOfInterpreters,
                                              String[] arrayOfPrograms, List<List<String>> listOfArgumentLists) {
        List<List<String>> commandsAndArguments = new LinkedList<>();

        for (int i=0; i< arrayOfPrograms.length; i++) {
            List<String> listOfCommandsAndArgs= new LinkedList<>();
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
    @Async
    public void runMainProgram(ProtoTreeInternal protoTreeInternal) throws IncorrectRequestException {
	    int counter = 0;
        for (List<String> commandArgument : protoTreeInternal.getCommandsAndArguments()) {
            if (protoTreeInternal.isFullPipeline().equals("true"))
                saveStage(protoTreeInternal, counter, counterToStage);
            else if (protoTreeInternal.isFullPipeline().equals("false"))
                saveStage(protoTreeInternal, counter, counterToStagePartial);
            counter++;
            super.launchProcess(commandArgument);
        }
        saveResultToDb(protoTreeInternal);
    }

    private void saveStage(ProtoTreeInternal protoTreeInternal, int counter, Map <Integer, String> counterToStageMap) {
        if (counterToStageMap.containsKey(counter)) {
            BioJob bioJob = super.getBioJobDao().findByJobId(protoTreeInternal.getJobId());
            bioJob.setStage(counterToStageMap.get(counter));
            super.getBioJobDao().save(bioJob);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int saveBioJobToDB(ProtoTreeInternal protoTreeInternal) {
        int jobId = getLastJobId();

        BioJob bioJob = new BioJob();
        bioJob.setProgramNameName(super.getProgram(protoTreeInternal.getCommandToBeProcessedBy()));
        bioJob.setJobId(jobId);
        bioJob.setJobDate(LocalDateTime.now());
        bioJob.setFinished(false);
        for (String filename : protoTreeInternal.getOutputFilesNames()) {
            BioJobResult bioJobResult = new BioJobResult();
            bioJobResult.setResultFile("placeholder");
            bioJobResult.setResultFileName(filename);
            bioJobResult.setBiojob(bioJob);
            bioJob.addToBioJobResultList(bioJobResult);
        }
        super.getBioJobDao().save(bioJob);
        return jobId;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveResultToDb(ProtoTreeInternal protoTreeInternal) {
	    for (String filename : protoTreeInternal.getOutputFilesNames()) {
            saveResultFileToDB(filename);
        }
        BioJob bioJob = super.getBioJobDao().findByJobId(protoTreeInternal.getJobId());
        bioJob.setFinished(true);
        super.getBioJobDao().save(bioJob);
    }

    private void saveResultFileToDB(String filename) {
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

        BioJobResult bioJobResult = super.getBioJobResultDao().findByResultFileName(filename);
        bioJobResult.setResultFile(fileAsStringBuilder.toString());
        super.getBioJobResultDao().save(bioJobResult);
    }

	private Integer getLastJobId() {
        Integer lastJobId = super.getBioJobDao().getLastJobId();
        return lastJobId != null ? lastJobId + 1 : defaultLastJobId;
	}

    private ProtoTreeInternal storeFileAndGetInternalRepresentation(final ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {
        String firstFileName = storeAndGetFileName(protoTreeRequest.getFirstFile(), protoTreeRequest.getFirstFileArea());
        String secondFileName = storeAndGetFileName(protoTreeRequest.getSecondFile(), protoTreeRequest.getSecondFileArea());

        String alignedFileName = storeAndGetFileName(protoTreeRequest.getAlignmentFile(), null);
        String treeFileName = storeAndGetFileName(protoTreeRequest.getTreeFile(), null);

        return fromProtoTreeRequestToProtoTreeInternal(protoTreeRequest, firstFileName,
                secondFileName, alignedFileName, treeFileName);
    }

    private String storeAndGetFileName(final MultipartFile multipartFile, final String fileArea) throws IncorrectRequestException {
        String fileName = null;
        if (multipartFile != null) {
            if (!isNullOrEmpty(fileArea)) {
                throw new IncorrectRequestException("fileTextArea and fileName are both not empty");
            } else {
                fileName = super.getStorageService().store(multipartFile);
            }
        } else if (!isNullOrEmpty(fileArea)) {
            fileName = super.getStorageService().createAndStore(fileArea);
        }
        return fileName;
    }

    private String getRandomFileName() {
        return super.getPrefix() + UUID.randomUUID().toString() + super.getPostfix();
    }
}
