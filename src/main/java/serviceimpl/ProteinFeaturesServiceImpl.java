package serviceimpl;

import java.util.*;

import biojobs.BioJob;
import biojobs.BioJobDao;
import biojobs.BioJobResultDao;
import enums.ParamPrefixes;
import model.internal.ProtoTreeInternal;
import model.request.ProtoTreeRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import service.ProtoTreeService;
import service.StorageService;
import exceptions.IncorrectRequestException;
import springconfiguration.AppProperties;

@Service
public class ProteinFeaturesServiceImpl extends BioUniverseServiceImpl implements ProtoTreeService {
	private final int defaultLastJobId = 1;
	private final String bootstrapFilePostfix = "_consensus";
	private Map<Integer, String> counterToStageOneInput = new HashMap<>();
	private Map<Integer, String> counterToStageTwoInputs = new HashMap<>();
	private Map<Integer, String> counterToStagePartialOneInput = new HashMap<>();
	private Map<Integer, String> counterToStagePartialTwoInputs = new HashMap<>();


	public ProteinFeaturesServiceImpl(final StorageService storageService, final AppProperties properties, final BioJobDao bioJobDao, final BioJobResultDao bioJobResultDao) {
		super(storageService, properties, bioJobResultDao, bioJobDao);
        counterToStageOneInput.put(1, "['Predicting proteins features.']");
        counterToStageOneInput.put(2, "['Predicting proteins features.', 'Aligning and building tree.']");
        counterToStageOneInput.put(3, "['Predicting proteins features.', 'Aligning and building tree.', 'Ordering alignment and putting features and tree together.-last']");

        counterToStageTwoInputs.put(2, "['Predicting proteins features.']");
        counterToStageTwoInputs.put(3, "['Predicting proteins features.', 'Aligning and building tree.']");
        counterToStageTwoInputs.put(4, "['Predicting proteins features.', 'Aligning and building tree.', 'Ordering alignment and putting features and tree together.-last']");

        counterToStagePartialOneInput.put(1, "['Predicting proteins features.']");
        counterToStagePartialOneInput.put(2, "['Predicting proteins features.', 'Ordering alignment and putting features and tree together.-last']");

        counterToStagePartialTwoInputs.put(2, "['Predicting proteins features.']");
        counterToStagePartialTwoInputs.put(3, "['Predicting proteins features.', 'Ordering alignment and putting features and tree together.-last']");
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
            case "pfam-only":
                db = db + super.getProperties().getPfam();
                break;
            case "pfam_and_mist":
                db = db + super.getProperties().getPfamAndMist();
                break;
        }
        return db;
    }

    @Override
    public ProtoTreeInternal storeFilesAndPrepareCommandArguments(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {
        ProtoTreeInternal protoTreeInternal;
	    if (protoTreeRequest.isFullPipeline().equals("true")) {
            protoTreeInternal = fullPipelineProcessing(protoTreeRequest);

        } else {
            protoTreeInternal = partialPipelineProcessing(protoTreeRequest);
        }
        return protoTreeInternal;
    }

    @Override
    public BioJob getBioJob(int jobId) {
        return super.getBioJobDao().findByJobId(jobId);
    }

    private ProtoTreeInternal fullPipelineProcessing(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {
        ProtoTreeInternal protoTreeInternal = super.storeFileAndGetInternalRepresentation(protoTreeRequest);
        List<String> listOfPrograms = new LinkedList<>();
        List<List<String>> listOfArgumentLists = new LinkedList<>();

        List<String> argsForPrepareNames = new LinkedList<>();
        List<String> argsForPrepareNamesSecond = new LinkedList<>();
        List<String> argsForProteinFeatures = new LinkedList<>();
        List<String> argsForAlignmentAndTree = new LinkedList<>();
        List<String> argsForTreeWithDomains = new LinkedList<>();

        String firstPreparedFile = super.getRandomFileName();
        argsForPrepareNames.addAll(Arrays.asList(protoTreeInternal.getFirstFileName(), ParamPrefixes.OUTPUT.getPrefix() + firstPreparedFile));
        protoTreeInternal.setFirstFileName(ParamPrefixes.INPUT.getPrefix() + firstPreparedFile);
        String inputFileNameForProtFeatures = protoTreeInternal.getFirstFileName();
        listOfPrograms.add(super.getProperties().getPrepareNames());
        listOfArgumentLists.add(argsForPrepareNames);

        if (protoTreeInternal.getSecondFileName() != null) {
            String secondPreparedFile = super.getRandomFileName();
            argsForPrepareNamesSecond.addAll(Arrays.asList(protoTreeInternal.getSecondFileName(), ParamPrefixes.OUTPUT.getPrefix() + secondPreparedFile));
            protoTreeInternal.setSecondFileName(ParamPrefixes.INPUT.getPrefix() + secondPreparedFile);
            inputFileNameForProtFeatures = protoTreeInternal.getSecondFileName();
            listOfPrograms.add(super.getProperties().getPrepareNames());
            listOfArgumentLists.add(argsForPrepareNamesSecond);
        }

        protoTreeInternal.setFields();

        String hmmscanOrRpsbOutFile = super.getRandomFileName();
        String rpsbProcOutFile = super.getRandomFileName();

        String tmhmmscanOutFile = super.getRandomFileName();
        String proteinFeaturesOutFile = super.getRandomFileName();
        String segmakserOutFile = super.getRandomFileName();

        String numberOfThreadsForProtFeatures = "7";
        String numberOfThreadsForTree = "7";
        String numberOfThreadsForAlgn = "7";
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

        if (!protoTreeRequest.getPhylogenyTest().equals("none")) {
            outNewickTree = outNewickTree + bootstrapFilePostfix;
        }

        String outNewickFile = super.getPrefix() + UUID.randomUUID().toString() + ".newick";
        String outSvgFile = super.getPrefix() + UUID.randomUUID().toString() + ".svg";
        String outOrderedAlgnFile = super.getPrefix() + UUID.randomUUID().toString() + ".fa";

        String proteinFeaturesChangedOutFile = super.getRandomFileName();
        argsForTreeWithDomains.addAll(Arrays.asList(
                inputFileNameForProtFeatures,
                ParamPrefixes.INPUT_SECOND.getPrefix() + outAlgnFile,
                ParamPrefixes.INPUT_THIRD.getPrefix() + outNewickTree + ".nwk",
                ParamPrefixes.INPUT_FOURTH.getPrefix() + proteinFeaturesOutFile,
                ParamPrefixes.OUTPUT.getPrefix() + outOrderedAlgnFile,
                ParamPrefixes.OUTPUT_SECOND.getPrefix() + outSvgFile,
                ParamPrefixes.OUTPUT_THIRD.getPrefix() + outNewickFile,
                ParamPrefixes.OUTPUT_FOURTH.getPrefix() + proteinFeaturesChangedOutFile
        ));

        protoTreeInternal.setOutputFilesNames(Arrays.asList(outNewickFile, outSvgFile, outOrderedAlgnFile, proteinFeaturesChangedOutFile));

        listOfPrograms.addAll(Arrays.asList(
                super.getProperties().getCalculateProteinFeatures(),
                super.getProperties().getAlignAndBuildTree(),
                super.getProgram(protoTreeInternal.getCommandToBeProcessedBy())
        ));

        String[] arrayOfInterpreters = super.prepareInterpreters(listOfPrograms.size());
        String[] arrayOfPrograms = listOfPrograms.toArray(new String[listOfPrograms.size()]);

        listOfArgumentLists.addAll(Arrays.asList(
                argsForProteinFeatures,
                argsForAlignmentAndTree,
                argsForTreeWithDomains
        ));

        super.prepareCommandArgumentsCommon(protoTreeInternal, arrayOfInterpreters, arrayOfPrograms, listOfArgumentLists);

        return protoTreeInternal;
    }

    private ProtoTreeInternal partialPipelineProcessing(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {
        ProtoTreeInternal protoTreeInternal = super.storeFileAndGetInternalRepresentation(protoTreeRequest);
        List<String> listOfPrograms = new LinkedList<>();
        List<List<String>> listOfArgumentLists = new LinkedList<>();

        List<String> argsForPrepareNames = new LinkedList<>();
        List<String> argsForPrepareNamesSecond = new LinkedList<>();
        List<String> argsForProteinFeatures = new LinkedList<>();
        List<String> argsForTreeWithDomains = new LinkedList<>();

        String sequencePreparedFile = super.getRandomFileName();
        String treePreparedFile = super.getRandomFileName();
        argsForPrepareNames.addAll(Arrays.asList(
                protoTreeInternal.getFirstFileName(),
                ParamPrefixes.OUTPUT.getPrefix() + sequencePreparedFile,
                protoTreeInternal.getTreeFile(),
                ParamPrefixes.OUTPUT_SECOND.getPrefix() + treePreparedFile
        ));
        listOfPrograms.add(super.getProperties().getPrepareNames());
        listOfArgumentLists.add(argsForPrepareNames);

        protoTreeInternal.setFirstFileName(ParamPrefixes.INPUT.getPrefix() + sequencePreparedFile);
        protoTreeInternal.setTreeFile(ParamPrefixes.INPUT_THIRD.getPrefix() + treePreparedFile);

        if (protoTreeInternal.getAlignmentFile() != null) {
            String alignmentPreparedFile = super.getRandomFileName();
            argsForPrepareNamesSecond.addAll(Arrays.asList(protoTreeInternal.getAlignmentFile(), ParamPrefixes.OUTPUT.getPrefix() + alignmentPreparedFile));
            protoTreeInternal.setAlignmentFile(ParamPrefixes.INPUT_SECOND.getPrefix() + alignmentPreparedFile);
            listOfPrograms.add(super.getProperties().getPrepareNames());
            listOfArgumentLists.add(argsForPrepareNamesSecond);
        }

        protoTreeInternal.setFields();

        String hmmscanOrRpsbOutFile = super.getRandomFileName();
        String rpsbProcOutFile = super.getRandomFileName();

        String tmhmmscanOutFile = super.getRandomFileName();
        String proteinFeaturesOutFile = super.getRandomFileName();
        String segmakserOutFile = super.getRandomFileName();

        String numberOfThreadsForProtFeatures = "7";
        argsForProteinFeatures.addAll(protoTreeInternal.getFieldsForFeaturesPrediction());
        argsForProteinFeatures.addAll(Arrays.asList(
                protoTreeInternal.getFirstFileName(),
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

        String outNewickFile = super.getPrefix() + UUID.randomUUID().toString() + ".newick";
        String outSvgFile = super.getPrefix() + UUID.randomUUID().toString() + ".svg";
        String proteinFeaturesChangedOutFile = super.getRandomFileName();
        argsForTreeWithDomains.addAll(Arrays.asList(
                protoTreeInternal.getFirstFileName(),
                protoTreeInternal.getTreeFile(),
                ParamPrefixes.INPUT_FOURTH.getPrefix() + proteinFeaturesOutFile,
                ParamPrefixes.OUTPUT_SECOND.getPrefix() + outSvgFile,
                ParamPrefixes.OUTPUT_THIRD.getPrefix() + outNewickFile,
                ParamPrefixes.OUTPUT_FOURTH.getPrefix() + proteinFeaturesChangedOutFile
        ));

        if (protoTreeInternal.getAlignmentFile() != null) {
            String outOrderedAlgnFile = super.getPrefix() + UUID.randomUUID().toString() + ".fa";
            argsForTreeWithDomains.add(protoTreeInternal.getAlignmentFile());
            argsForTreeWithDomains.add(ParamPrefixes.OUTPUT.getPrefix() + outOrderedAlgnFile);
            protoTreeInternal.setOutputFilesNames(Arrays.asList(outNewickFile, outSvgFile, outOrderedAlgnFile, proteinFeaturesChangedOutFile));
        } else {
            protoTreeInternal.setOutputFilesNames(Arrays.asList(outNewickFile, outSvgFile, proteinFeaturesChangedOutFile));
        }


        listOfPrograms.addAll(Arrays.asList(
                super.getProperties().getCalculateProteinFeatures(),
                super.getProgram(protoTreeInternal.getCommandToBeProcessedBy())
        ));

        String[] arrayOfInterpreters = super.prepareInterpreters(listOfPrograms.size());
        String[] arrayOfPrograms = listOfPrograms.toArray(new String[listOfPrograms.size()]);

        listOfArgumentLists.addAll(Arrays.asList(
                argsForProteinFeatures,
                argsForTreeWithDomains
        ));
        super.prepareCommandArgumentsCommon(protoTreeInternal, arrayOfInterpreters, arrayOfPrograms, listOfArgumentLists);
        return protoTreeInternal;
    }

    @Override
    @Async
    public void runMainProgram(ProtoTreeInternal protoTreeInternal) throws IncorrectRequestException {
	    int counter = 0;
        for (List<String> commandArgument : protoTreeInternal.getCommandsAndArguments()) {
            if (protoTreeInternal.isFullPipeline().equals("true")) {
                if (protoTreeInternal.getSecondFileName() == null)
                    super.saveStage(protoTreeInternal, counter, counterToStageOneInput);
                else
                    super.saveStage(protoTreeInternal, counter, counterToStageTwoInputs);
            } else if (protoTreeInternal.isFullPipeline().equals("false")) {
                if (protoTreeInternal.getAlignmentFile() == null)
                    super.saveStage(protoTreeInternal, counter, counterToStagePartialOneInput);
                else
                    super.saveStage(protoTreeInternal, counter, counterToStagePartialTwoInputs);
            }
            counter++;
            super.launchProcess(commandArgument);
        }
        super.saveResultToDb(protoTreeInternal);
    }


}
