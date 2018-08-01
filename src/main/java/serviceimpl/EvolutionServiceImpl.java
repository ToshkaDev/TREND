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
@Transactional(propagation = Propagation.REQUIRED)
public class EvolutionServiceImpl extends BioUniverseServiceImpl implements EvolutionService {
	private final int defaultLastJobId = 1;


	public EvolutionServiceImpl(final StorageService storageService, final AppProperties properties, final BioJobDao bioJobDao, final BioJobResultDao bioJobResultDao) {
		super(storageService, properties, bioJobResultDao, bioJobDao);
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
	public BioJob getBioJobIfFinished(int jobId) {
		BioJob bioJob = super.getBioJobDao().findByJobId(jobId);
		return bioJob.isFinished() ? bioJob : null;
	}

    public ProtoTreeInternal storeFilesAndPrepareCommandArgumentsP(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {
        ProtoTreeInternal protoTreeInternal = storeFileAndGetInternalRepresentationP(protoTreeRequest);

        List<String> argsForPrepareNames = new LinkedList<>();
        List<String> argsForProteinFeatures = new LinkedList<>();
        List<String> argsForAlignmentAndTree = new LinkedList<>();
        List<String> argsForTreeWithDomains = new LinkedList<>();

        String firstPreparedFile = getRandomFileName();
        argsForPrepareNames.addAll(Arrays.asList(protoTreeInternal.getFirstFileName(), ParamPrefixes.OUTPUT.getPrefix() + firstPreparedFile));
        String secondPreparedFile = getRandomFileName();
        argsForPrepareNames.addAll(Arrays.asList(protoTreeInternal.getFirstFileName(), ParamPrefixes.OUTPUT.getPrefix() + secondPreparedFile));
        protoTreeInternal.setFirstFileName(ParamPrefixes.INPUT.getPrefix() + firstPreparedFile);
        protoTreeInternal.setSecondFileName(ParamPrefixes.INPUT.getPrefix() + secondPreparedFile);
        protoTreeInternal.setFields();

        String inputFileNameForAlignAndTree = protoTreeInternal.getSecondFileName() != null
                ? protoTreeInternal.getSecondFileName()
                : protoTreeInternal.getFirstFileName();

        String hmmscanOrRpsbOutFile = getRandomFileName();
        String rpsbProcOutFile = getRandomFileName();

        String tmhmmscanOutFile = getRandomFileName();
        String proteinFeaturesOutFile = getRandomFileName();

        String eValueThreashold = "0.01";
        String numberOfThreads = "4";
        argsForProteinFeatures.addAll(Arrays.asList(
                inputFileNameForAlignAndTree,
                protoTreeInternal.getDomainPredictionProgram(),
                getDomainPredictionDb(protoTreeInternal.getDomainPredictionDb()),
                ParamPrefixes.OUTPUT_FOURTH.getPrefix() + hmmscanOrRpsbOutFile,
                ParamPrefixes.OUTPUT_FIFTH.getPrefix() + rpsbProcOutFile,
                ParamPrefixes.OUTPUT_SIXTH.getPrefix() + tmhmmscanOutFile,
                ParamPrefixes.HMMSCAN_DB_PATH.getPrefix() + super.getProperties().getHmmscanDbPath(),
                ParamPrefixes.RPSBLAST_DB_PATH.getPrefix() + super.getProperties().getRpsblastDbPath(),
                ParamPrefixes.RPSBPROC_DB_PATH.getPrefix() + super.getProperties().getRpsprocDbPath(),
                ParamPrefixes.HMMSCAN_PATH.getPrefix() + super.getProperties().getHmmscanPath(),
                ParamPrefixes.RPSBLAST_PATH.getPrefix() + super.getProperties().getRpsblastPath(),
                ParamPrefixes.RPSBPROC_PATH.getPrefix() + super.getProperties().getRpsbprocPath(),
                ParamPrefixes.TMHMM_PATH.getPrefix() + super.getProperties().getTmhmm2Path(),
                ParamPrefixes.EVAL_THRESH.getPrefix() + eValueThreashold,
                ParamPrefixes.THREAD.getPrefix() + numberOfThreads,
                ParamPrefixes.OUTPUT_THIRD.getPrefix() + proteinFeaturesOutFile
        ));

        String numberOfThreadsForTree = "4";
        String numberOfThreadsForAlgn = "4";
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
        String outOrderedAlgnFile = getRandomFileName();

        protoTreeInternal.setOutputFilesNames(Arrays.asList(outNewickFile, outSvgFile, outOrderedAlgnFile, proteinFeaturesOutFile));

        argsForTreeWithDomains.addAll(Arrays.asList(
                inputFileNameForAlignAndTree,
                ParamPrefixes.INPUT_SECOND.getPrefix() + outAlgnFile,
                ParamPrefixes.INPUT_THIRD.getPrefix() + outNewickTree + ".nwk",
                ParamPrefixes.INPUT_FOURTH.getPrefix() + proteinFeaturesOutFile,
                ParamPrefixes.OUTPUT.getPrefix() + outOrderedAlgnFile,
                ParamPrefixes.OUTPUT_SECOND.getPrefix() + outSvgFile,
                ParamPrefixes.OUTPUT_THIRD.getPrefix() + outNewickFile
        ));

        String[] arrayOfInterpreters = {super.getPython(), super.getPython(), super.getPython(), super.getPython()};

        String[] arrayOfPrograms = {
                super.getProperties().getPrepareNames(),
                super.getProperties().getCalculateProteinFeatures(),
                super.getProperties().getAlignAndBuildTree(),
                super.getProgram(protoTreeInternal.getCommandToBeProcessedBy())
        };

        List<List<String>> listOfArgumentLists = new LinkedList<>(Arrays.asList(
                argsForPrepareNames, argsForProteinFeatures, argsForAlignmentAndTree, argsForTreeWithDomains));
        prepareCommandArgumentsCommonP(protoTreeInternal, arrayOfInterpreters, arrayOfPrograms, listOfArgumentLists);

        return protoTreeInternal;
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
    public void runMainProgramP(ProtoTreeInternal protoTreeInternal) throws IncorrectRequestException {
        for (List<String> commandArgument : protoTreeInternal.getCommandsAndArguments()) {
            super.launchProcess(commandArgument);
        }
        saveResultToDb(protoTreeInternal);
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

    private ProtoTreeInternal storeFileAndGetInternalRepresentationP(final ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {
        String firstFileName = storeAndGetFileName(protoTreeRequest.getFirstFile(), protoTreeRequest.getFirstFileArea());
        String secondFileName = storeAndGetFileName(protoTreeRequest.getSecondFile(), protoTreeRequest.getSecondFileArea());

        return fromProtoTreeRequestToProtoTreeInternal(protoTreeRequest, firstFileName, secondFileName);
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
