package serviceimpl;

import static com.google.common.base.Strings.isNullOrEmpty;
import static converters.ConverterMain.fromEvolRequestToEvolInternal;
import static converters.ConverterMain.fromProtoTreeRequestToProtoTreeInternal;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import biojobs.BioJob;
import biojobs.BioJobDao;
import biojobs.BioJobResult;
import biojobs.BioJobResultDao;
import enums.ParamPrefixes;
import model.internal.EvolutionInternal;
import model.internal.ProtoTreeInternal;
import model.request.EvolutionRequest;
import model.request.ProtoTreeRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import service.EvolutionService;
import service.StorageService;
import exceptions.IncorrectRequestException;
import springconfiguration.AppProperties;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class EvolutionServiceImpl extends BioUniverseServiceImpl implements EvolutionService {
	private final String prepareNames;
	private final String blastAllVsAll;
	private final String alignMultiple;

	private final int defaultLastJobId = 1;


	public EvolutionServiceImpl(final StorageService storageService, final AppProperties properties, final BioJobDao bioJobDao, final BioJobResultDao bioJobResultDao) {
		super(storageService, properties, bioJobResultDao, bioJobDao);
		this.prepareNames = properties.getPrepareNamesProgram();
		this.blastAllVsAll = properties.getBlastAllVsAllProgram();
		this.alignMultiple = properties.getAlignMultiple();
	}

	@Override
	public BioJob getBioJobIfFinished(int jobId) {
		BioJob bioJob = super.getBioJobDao().findByJobId(jobId);
		return bioJob.isFinished() ? bioJob : null;
	}

    @Override
	public String[] createDirsConcat() {
		//i-input, o-output
		String iFilesLocationAlign= super.getProperties().getMultipleWorkingFilesLocation();
		String oFilesLocationAlign = super.getProperties().getMultipleWorkingFilesLocation();
		String iFilesLocationConcatenate = oFilesLocationAlign;
		super.getStorageService().createMultipleDirs(Arrays.asList(iFilesLocationAlign, oFilesLocationAlign));
		return new String[] {iFilesLocationAlign, oFilesLocationAlign, iFilesLocationConcatenate};
	}


    @Override
	public String[] createDirs() {
	    //i-input, o-output
        String iFilesLocationPrepNames = super.getProperties().getMultipleWorkingFilesLocation();
        String oFilesLocationPrepNames = super.getProperties().getMultipleWorkingFilesLocation();
        String iFilesLocationBlast = oFilesLocationPrepNames;
        String oFilesLocationBlast = super.getProperties().getMultipleWorkingFilesLocation();
        String iFilesLocationCreateCogs = oFilesLocationBlast;
        super.getStorageService().createMultipleDirs(Arrays.asList(iFilesLocationPrepNames, oFilesLocationPrepNames, oFilesLocationBlast));
        return new String[] {iFilesLocationPrepNames, oFilesLocationPrepNames, iFilesLocationBlast, oFilesLocationBlast, iFilesLocationCreateCogs};
    }

    @Override
    public EvolutionInternal storeFilesAndPrepareCommandArgumentsConcat(final EvolutionRequest evolutionRequest, String[] locations) throws IncorrectRequestException {
        EvolutionInternal evolutionInternal = storeFileAndGetInternalRepresentation(evolutionRequest, locations[0]);
        evolutionInternal.setFields();
        evolutionInternal.setOutputFileName(super.getPrefix() + UUID.randomUUID().toString() + super.getPostfix());


        List<String> argsForAlignMultiple = new LinkedList<>();
        List<String> argsForConcatenate = new LinkedList<>();

        argsForAlignMultiple.addAll(Arrays.asList(ParamPrefixes.INPUT.getPrefix()+locations[0], ParamPrefixes.OUTPUT.getPrefix()+locations[1]));

        argsForConcatenate.add(ParamPrefixes.INPUT.getPrefix()+locations[2]);
        argsForConcatenate.add(ParamPrefixes.OUTPUT.getPrefix() + evolutionInternal.getOutputFileName());
        argsForConcatenate.addAll(evolutionInternal.getAllFields());

        String[] arrayOfInterpreters = {super.getBash(), super.getPython()};
        String[] arrayOfPrograms = {alignMultiple, super.getProgram(evolutionInternal.getCommandToBeProcessedBy())};
        List<List<String>> listOfArgumentLists = new LinkedList<>(Arrays.asList(argsForAlignMultiple, argsForConcatenate));
        prepareCommandArgumentsCommon(evolutionInternal, arrayOfInterpreters, arrayOfPrograms, listOfArgumentLists);

        return evolutionInternal;
    }

    public ProtoTreeInternal storeFilesAndPrepareCommandArgumentsP(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {
        ProtoTreeInternal protoTreeInternal = storeFileAndGetInternalRepresentationP(protoTreeRequest);
        protoTreeInternal.setFields();


        List<String> argsForProteinFeatures = new LinkedList<>();
        List<String> argsForAlignmentAndTree = new LinkedList<>();
        List<String> argsForTreeWithDomains = new LinkedList<>();


        String hmmscanOrRpsbOutFile = super.getPrefix() + UUID.randomUUID().toString() + super.getPostfix();
        String rpsbProcOutFile = super.getPrefix() + UUID.randomUUID().toString() + super.getPostfix();
        String tmhmmscanOutFile = super.getPrefix() + UUID.randomUUID().toString() + super.getPostfix();
        String proteinFeaturesOutFile = super.getPrefix() + UUID.randomUUID().toString() + super.getPostfix();
        String eValueThreashold = "0.01";
        String numberOfThreads = "4";
        argsForProteinFeatures.addAll(Arrays.asList(
                protoTreeInternal.getFirstFileName(),
                protoTreeInternal.getDomainPredictionProgram(),
                ParamPrefixes.OUTPUT_FOURTH.getPrefix() + hmmscanOrRpsbOutFile,
                ParamPrefixes.OUTPUT_FIFTH.getPrefix() + rpsbProcOutFile,
                ParamPrefixes.OUTPUT_SIXTH.getPrefix() + tmhmmscanOutFile,
                ParamPrefixes.HMMSCAN_DB_PATH.getPrefix() + super.getProperties().getHmmscanDbPath(),
                ParamPrefixes.RPSBLAST_DB_PATH.getPrefix() + super.getProperties().getRpsblastDbPath(),
                ParamPrefixes.RPSBPROC_DB_PATH.getPrefix() + super.getProperties().getRpsprocDbPath(),
                ParamPrefixes.RPSBLAST_SP_DB.getPrefix() + super.getProperties().getRpsblastSpDb(),
                ParamPrefixes.HMMSCAN_PATH.getPrefix() + super.getProperties().getHmmscanPath(),
                ParamPrefixes.RPSBLAST_PATH.getPrefix() + super.getProperties().getRpsblastPath(),
                ParamPrefixes.RPSBPROC_PATH.getPrefix() + super.getProperties().getRpsbprocPath(),
                ParamPrefixes.TMHMM_PATH.getPrefix() + super.getProperties().getTmhmm2Path(),
                ParamPrefixes.EVAL_THRESH.getPrefix() + eValueThreashold,
                ParamPrefixes.THREAD.getPrefix() + numberOfThreads,
                ParamPrefixes.OUTPUT_PROTEIN_FEAUTURES.getPrefix() + proteinFeaturesOutFile
        ));


        String numberOfThreadsForTree = "4";
        String numberOfThreadsForAlgn = "4";
        String outAlgnFile = super.getPrefix() + UUID.randomUUID().toString() + super.getPostfix();
        String outNewickTree = super.getPrefix() + UUID.randomUUID().toString() + super.getPostfix();

        argsForAlignmentAndTree.addAll(protoTreeInternal.getFieldsForAlignmentAndTreeBuild());
        argsForAlignmentAndTree.addAll(Arrays.asList(
                ParamPrefixes.OUTPUT_PARAMS.getPrefix() + super.getPrefix() + UUID.randomUUID().toString() + super.getPostfix(),
                ParamPrefixes.OUTPUT_TREE.getPrefix() + outNewickTree,
                ParamPrefixes.THREAD.getPrefix() + numberOfThreadsForAlgn,
                ParamPrefixes.TREE_THREAD + numberOfThreadsForTree,
                ParamPrefixes.OUTPUT.getPrefix() + outAlgnFile
        ));

        String outNewickFile = super.getPrefix() + UUID.randomUUID().toString() + super.getPostfix();
        String outSvgFile = super.getPrefix() + UUID.randomUUID().toString() + super.getPostfix();
        String outOrderedAlgnFile = super.getPrefix() + UUID.randomUUID().toString() + super.getPostfix();

        protoTreeInternal.setOutputFilesNames(Arrays.asList(outNewickFile, outSvgFile, outOrderedAlgnFile));

        argsForTreeWithDomains.addAll(Arrays.asList(
                protoTreeInternal.getFirstFileName(),
                ParamPrefixes.INPUT_SECOND.getPrefix() + outAlgnFile,
                ParamPrefixes.INPUT_THIRD.getPrefix() + outNewickTree,
                ParamPrefixes.INPUT_FOURTH.getPrefix() + proteinFeaturesOutFile,
                ParamPrefixes.OUTPUT.getPrefix() + outOrderedAlgnFile,
                ParamPrefixes.OUTPUT_SECOND.getPrefix() + outSvgFile,
                ParamPrefixes.OUTPUT_THIRD.getPrefix() + outNewickFile
        ));

        String[] arrayOfInterpreters = {super.getPython(), super.getPython(), super.getPython()};

        String[] arrayOfPrograms = {super.getProperties().getCalculateProteinFeatures(),
                super.getProperties().getAlignAndBuildTree(),
                super.getProgram(protoTreeInternal.getCommandToBeProcessedBy())};
        List<List<String>> listOfArgumentLists = new LinkedList<>(Arrays.asList(argsForProteinFeatures, argsForAlignmentAndTree, argsForTreeWithDomains));

        prepareCommandArgumentsCommonP(protoTreeInternal, arrayOfInterpreters, arrayOfPrograms, listOfArgumentLists);

        return protoTreeInternal;

    }

    @Override
    public EvolutionInternal storeFilesAndPrepareCommandArguments (final EvolutionRequest evolutionRequest, String[] locations) throws IncorrectRequestException {
	    EvolutionInternal evolutionInternal = storeFileAndGetInternalRepresentation(evolutionRequest, locations[0]);
        evolutionInternal.setFields();
        evolutionInternal.setOutputFileName(super.getPrefix() + UUID.randomUUID().toString() + super.getPostfix());

        List<String> argsForPrepNames = new LinkedList<>();
        List<String> argsForBlast = new LinkedList<>();
        List<String> argsForCreateCogs = new LinkedList<>();

        argsForPrepNames.addAll(Arrays.asList(ParamPrefixes.INPUT.getPrefix()+locations[0], ParamPrefixes.OUTPUT.getPrefix()+locations[1]));
        argsForPrepNames.addAll(evolutionInternal.getFieldForIntermScript());

        argsForBlast.add(ParamPrefixes.WDIR.getPrefix() + super.getPathToMainDirFromBioProgs() + super.getWorkingDir()+"/");
        argsForBlast.addAll(Arrays.asList(ParamPrefixes.INPUT.getPrefix()+locations[2], ParamPrefixes.OUTPUT.getPrefix()+locations[3]));

        argsForCreateCogs.add(ParamPrefixes.INPUT.getPrefix()+locations[4]);
        argsForCreateCogs.add(ParamPrefixes.OUTPUT.getPrefix() + evolutionInternal.getOutputFileName());
        argsForCreateCogs.addAll(evolutionInternal.getAllFields());


        String[] arrayOfInterpreters = {super.getBash(), super.getBash(), super.getPython()};
        String[] arrayOfPrograms = {prepareNames, blastAllVsAll, super.getProgram(evolutionInternal.getCommandToBeProcessedBy())};
        List<List<String>> listOfArgumentLists = new LinkedList<>(Arrays.asList(argsForPrepNames, argsForBlast, argsForCreateCogs));

        prepareCommandArgumentsCommon(evolutionInternal, arrayOfInterpreters, arrayOfPrograms, listOfArgumentLists);

        return evolutionInternal;
    }

    public void prepareCommandArgumentsCommon(EvolutionInternal evolutionInternal, String[] arrayOfInterpreters,
                                              String[] arrayOfPrograms, List<List<String>> listOfArgumentLists) {
        List<List<String>> commandsAndArguments = new LinkedList<>();

        for (int i=0; i< arrayOfPrograms.length; i++) {
            List<String> listOfCommandsAndArgs= new LinkedList<>();
            listOfCommandsAndArgs.add(arrayOfInterpreters[i]);
            listOfCommandsAndArgs.add(arrayOfPrograms[i]);
            listOfCommandsAndArgs.addAll(listOfArgumentLists.get(i));
            commandsAndArguments.add(listOfCommandsAndArgs);
        }
        int jobId = saveBioJobToDB(evolutionInternal);
        evolutionInternal.setJobId(jobId);
        evolutionInternal.setCommandsAndArguments(commandsAndArguments);
    }

    public void prepareCommandArgumentsCommonP(ProtoTreeInternal protoTreeInternal, String[] arrayOfInterpreters,
                                              String[] arrayOfPrograms, List<List<String>> listOfArgumentLists) {
        List<List<String>> commandsAndArguments = new LinkedList<>();

        for (int i=0; i< arrayOfPrograms.length; i++) {
            List<String> listOfCommandsAndArgs= new LinkedList<>();
            listOfCommandsAndArgs.add(arrayOfInterpreters[i]);
            listOfCommandsAndArgs.add(arrayOfPrograms[i]);
            listOfCommandsAndArgs.addAll(listOfArgumentLists.get(i));
            commandsAndArguments.add(listOfCommandsAndArgs);
        }
        int jobId = saveBioJobToDBP(protoTreeInternal);
        protoTreeInternal.setJobId(jobId);
        protoTreeInternal.setCommandsAndArguments(commandsAndArguments);
    }


    @Override
    @Async
    public void runMainProgram(EvolutionInternal evolutionInternal) throws IncorrectRequestException {
        for (List<String> commandArgument : evolutionInternal.getCommandsAndArguments()) {
            super.launchProcess(commandArgument);
        }
        saveResultFileToDB(evolutionInternal);
    }

    @Override
    @Async
    public void runMainProgramP(ProtoTreeInternal protoTreeInternal) throws IncorrectRequestException {
        for (List<String> commandArgument : protoTreeInternal.getCommandsAndArguments()) {
            super.launchProcess(commandArgument);
        }
        saveResultToDb(protoTreeInternal);
    }

	public int saveBioJobToDB(EvolutionInternal evolutionInternal) {
		int jobId = getLastJobId();

		BioJob bioJob = new BioJob();
		bioJob.setProgramNameName(super.getProgram(evolutionInternal.getCommandToBeProcessedBy()));
		bioJob.setJobId(jobId);
		bioJob.setJobDate(LocalDateTime.now());
		bioJob.setFinished(false);

		BioJobResult bioJobResult = new BioJobResult();
		bioJobResult.setResultFile("placeholder");
		bioJobResult.setResultFileName(evolutionInternal.getOutputFileName());
        bioJobResult.setBiojob(bioJob);

        bioJob.addToBioJobResultList(bioJobResult);
        super.getBioJobDao().save(bioJob);
		return jobId;
	}

    public int saveBioJobToDBP(ProtoTreeInternal protoTreeInternal) {
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


    public void saveResultFileToDB(EvolutionInternal evolutionInternal) {
		File file = null;
		try {
			file = getStorageService().loadAsResource(evolutionInternal.getOutputFileName()).getFile();
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

		BioJobResult bioJobResult = super.getBioJobResultDao().findByResultFileName(evolutionInternal.getOutputFileName());
		bioJobResult.setResultFile(fileAsStringBuilder.toString());
        super.getBioJobResultDao().save(bioJobResult);

		BioJob bioJob = super.getBioJobDao().findByJobId(evolutionInternal.getJobId());
		bioJob.setFinished(true);
        super.getBioJobDao().save(bioJob);
	}

    public void saveResultToDb(ProtoTreeInternal protoTreeInternal) {
	    for (String filename : protoTreeInternal.getOutputFilesNames()) {
            saveResultFileToDBP(filename);
        }
        BioJob bioJob = super.getBioJobDao().findByJobId(protoTreeInternal.getJobId());
        bioJob.setFinished(true);
        super.getBioJobDao().save(bioJob);
    }

    private void saveResultFileToDBP(String filename) {
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

	private EvolutionInternal storeFileAndGetInternalRepresentation(final EvolutionRequest evolutionRequest, String inputFilesLocation1) throws IncorrectRequestException {
		super.getStorageService().storeMultipleFiles(evolutionRequest.getListOfFiles(), inputFilesLocation1);
		return fromEvolRequestToEvolInternal(evolutionRequest);
	}

    private ProtoTreeInternal storeFileAndGetInternalRepresentationP(final ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {
        String firstFileName = null;
        String secondFileName = null;
        String thirdFileName = null;

        if (protoTreeRequest.getFirstFile() != null) {
            if (!isNullOrEmpty(protoTreeRequest.getFirstFileTextArea())) {
                throw new IncorrectRequestException("firstFileTextArea and firstFileName are both not empty");
            } else {
                firstFileName = super.getStorageService().store(protoTreeRequest.getFirstFile());
            }
        } else if (!isNullOrEmpty(protoTreeRequest.getFirstFileTextArea())) {
            firstFileName = super.getStorageService().createAndStore(protoTreeRequest.getFirstFileTextArea());
        }

        if (protoTreeRequest.getSecondFile() != null) {
            if (!isNullOrEmpty(protoTreeRequest.getSecondFileTextArea())) {
                throw new IncorrectRequestException("secondFileTextArea and firstFileName are both not empty");
            } else {
                secondFileName = super.getStorageService().store(protoTreeRequest.getSecondFile());
            }
        } else if (!isNullOrEmpty(protoTreeRequest.getSecondFileTextArea())) {
            secondFileName = super.getStorageService().createAndStore(protoTreeRequest.getSecondFileTextArea());
        }
        if (protoTreeRequest.getThirdFile() != null) {
            if (!isNullOrEmpty(protoTreeRequest.getThirdFileTextArea())) {
                throw new IncorrectRequestException("thirdFileTextArea and thirdFileName are both not empty");
            } else {
                thirdFileName = super.getStorageService().store(protoTreeRequest.getThirdFile());
            }
        } else if (!isNullOrEmpty(protoTreeRequest.getThirdFileTextArea())) {
            secondFileName = super.getStorageService().createAndStore(protoTreeRequest.getSecondFileTextArea());
        }

        return fromProtoTreeRequestToProtoTreeInternal(protoTreeRequest, firstFileName, secondFileName, thirdFileName);
    }
}
