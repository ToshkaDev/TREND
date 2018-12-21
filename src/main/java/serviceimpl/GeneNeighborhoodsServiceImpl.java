package serviceimpl;

import biojobs.BioJob;
import biojobs.BioJobDao;
import biojobs.BioJobResultDao;
import enums.ParamPrefixes;
import exceptions.IncorrectRequestException;
import model.internal.ProtoTreeInternal;
import model.request.ProtoTreeRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import service.GeneNeighborhoodsService;
import service.StorageService;
import springconfiguration.AppProperties;

import java.util.*;

@Service
public class GeneNeighborhoodsServiceImpl extends BioUniverseServiceImpl implements GeneNeighborhoodsService {
    private Map<Integer, String> counterToStageOneInput = new HashMap<>();
    private final String bootstrapFilePostfix = "_consensus";

    public GeneNeighborhoodsServiceImpl(final StorageService storageService, final AppProperties properties, final BioJobDao bioJobDao, final BioJobResultDao bioJobResultDao) {
        super(storageService, properties, bioJobResultDao, bioJobDao);
        counterToStageOneInput.put(1, "['Aligning and building tree.']");
    }

    @Override
    public ProtoTreeInternal storeFilesAndPrepareCommandArguments(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {

        return pipelineProcessing(protoTreeRequest);
    }

    @Override
    public BioJob getBioJob(int jobId) {
        return super.getBioJobDao().findByJobId(jobId);
    }

    private ProtoTreeInternal pipelineProcessing(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {
        ProtoTreeInternal protoTreeInternal = storeFileAndGetInternalRepresentation(protoTreeRequest);
        List<String> listOfPrograms = new LinkedList<>();
        List<List<String>> listOfArgumentLists = new LinkedList<>();

        List<String> argsForPrepareNames = new LinkedList<>();
        List<String> argsForAlignmentAndTree = new LinkedList<>();
        List<String> argsForTreeEnumerate = new LinkedList<>();

        String firstPreparedFile = super.getRandomFileName();
        argsForPrepareNames.addAll(Arrays.asList(protoTreeInternal.getFirstFileName(), ParamPrefixes.OUTPUT.getPrefix() + firstPreparedFile));
        protoTreeInternal.setFirstFileName(ParamPrefixes.INPUT.getPrefix() + firstPreparedFile);
        listOfPrograms.add(super.getProperties().getPrepareNames());
        listOfArgumentLists.add(argsForPrepareNames);

        protoTreeInternal.setFields();

        String numberOfThreadsForTree = "7";
        String numberOfThreadsForAlgn = "7";

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
        String outOrderedAlgnFile = super.getPrefix() + UUID.randomUUID().toString() + ".fa";

        argsForTreeEnumerate.addAll(Arrays.asList(
                ParamPrefixes.INPUT_SECOND.getPrefix() + outAlgnFile,
                ParamPrefixes.INPUT_THIRD.getPrefix() + outNewickTree + ".nwk",
                ParamPrefixes.OUTPUT.getPrefix() + outOrderedAlgnFile,
                ParamPrefixes.OUTPUT_THIRD.getPrefix() + outNewickFile
        ));

        protoTreeInternal.setOutputFilesNames(Arrays.asList(outNewickFile, outOrderedAlgnFile));

        listOfPrograms.addAll(Arrays.asList(
                super.getProperties().getAlignAndBuildTree(),
                super.getProperties().getEnumerate()
        ));

        String[] arrayOfInterpreters = super.prepareInterpreters(listOfPrograms.size());
        String[] arrayOfPrograms = listOfPrograms.toArray(new String[listOfPrograms.size()]);

        listOfArgumentLists.addAll(Arrays.asList(
                argsForAlignmentAndTree,
                argsForTreeEnumerate
        ));

        super.prepareCommandArgumentsCommon(protoTreeInternal, arrayOfInterpreters, arrayOfPrograms, listOfArgumentLists);
        return protoTreeInternal;
    }

    @Override
    @Async
    public void runMainProgram(ProtoTreeInternal protoTreeInternal) throws IncorrectRequestException {
        int counter = 0;
        for (List<String> commandArgument : protoTreeInternal.getCommandsAndArguments()) {
            super.saveStage(protoTreeInternal, counter++, counterToStageOneInput);
            super.launchProcess(commandArgument);
        }
        super.saveResultToDb(protoTreeInternal);
    }
}
