package serviceimpl;

import biojobs.BioJobDao;
import biojobs.BioJobResultDao;
import enums.Status;
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
    private Map<Integer, String> counterToStageOneInputPartial = new HashMap<>();
    private Map<Integer, String> counterToStageOneInputFull = new HashMap<>();
    private Map<Integer, String> counterToStageOneInputFullWithRedund = new HashMap<>();
    private final String bootstrapFilePostfix = "_consensus";

    public GeneNeighborhoodsServiceImpl(final StorageService storageService, final AppProperties properties, final BioJobDao bioJobDao, final BioJobResultDao bioJobResultDao) {
        super(storageService, properties, bioJobResultDao, bioJobDao);
        counterToStageOneInputPartial.put(0, "['Processing input.']");
        counterToStageOneInputPartial.put(1, "['Processing input.', 'Identifying operons and clustering genes.-last']");

        counterToStageOneInputFull.put(0, "['Processing input.']");
        counterToStageOneInputFull.put(1, "['Processing input.', 'Aligning sequences and building phylogenetic tree.']");
        counterToStageOneInputFull.put(2, "['Processing input.', 'Aligning sequences and building phylogenetic tree.'," +
                "'Identifying operons and clustering genes.-last']");

        counterToStageOneInputFullWithRedund.put(0, "['Processing input.']");
        counterToStageOneInputFullWithRedund.put(1, "['Processing input.', 'Reducing sequence redundancy.']");
        counterToStageOneInputFullWithRedund.put(2, "['Processing input.', 'Reducing sequence redundancy.', 'Aligning sequences and building phylogenetic tree.']" );
        counterToStageOneInputFullWithRedund.put(3, "['Processing input.', 'Reducing sequence redundancy.', 'Aligning sequences and building phylogenetic tree.'," +
                "'Identifying operons and clustering genes.-last']");
    }

    @Override
    public ProtoTreeInternal storeFilesAndPrepareCommandArguments(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {
        if (protoTreeRequest.isFullPipeline().equals("false"))
            return pipelineProcessingPartial(protoTreeRequest);
        else
            return pipelineProcessing(protoTreeRequest);
    }

    private ProtoTreeInternal pipelineProcessingPartial(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {
        ProtoTreeInternal protoTreeInternal = storeFileAndGetInternalRepresentation(protoTreeRequest);
        List<String> listOfPrograms = new LinkedList<>();
        List<List<String>> listOfArgumentLists = new LinkedList<>();
        List<String> argsForPrepareNames = new LinkedList<>();
        List<String> argsForGeneNeighbors = new LinkedList<>();

        String outNewickFile = initArgsForPrepareNames(protoTreeInternal, argsForPrepareNames, listOfPrograms, listOfArgumentLists);
        protoTreeInternal.setFields();
        String outJsonFile = super.getRandomFileName(".json");
        argsForGeneNeighbors.addAll(protoTreeInternal.getFieldsForGeneNeighbors());
        argsForGeneNeighbors.addAll(Arrays.asList(
                ParamPrefixes.INPUT.getPrefix() + outNewickFile,
                ParamPrefixes.OUTPUT.getPrefix() + outJsonFile,
                ParamPrefixes.PROCESS_NUMBER.getPrefix() + super.getProperties().getFetchFromMistProcNum()
        ));

        protoTreeInternal.setOutputFilesNames(Arrays.asList(outNewickFile, outJsonFile));

        listOfPrograms.add(super.getProperties().getGeneNeighbors());
        String[] arrayOfInterpreters = super.prepareInterpreters(listOfPrograms.size());
        String[] arrayOfPrograms = listOfPrograms.toArray(new String[listOfPrograms.size()]);
        listOfArgumentLists.add(argsForGeneNeighbors);
        super.prepareCommandArgumentsCommon(protoTreeInternal, arrayOfInterpreters, arrayOfPrograms, listOfArgumentLists);

        return protoTreeInternal;
    }

    private ProtoTreeInternal pipelineProcessing(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {
        ProtoTreeInternal protoTreeInternal = storeFileAndGetInternalRepresentation(protoTreeRequest);
        String redundancy = protoTreeInternal.getRedundancy() != null
                && protoTreeInternal.getDoAlign().equals("-d yes") ? protoTreeInternal.getRedundancy() : null;

        List<String> listOfPrograms = new LinkedList<>();
        List<List<String>> listOfArgumentLists = new LinkedList<>();

        List<String> argsForPrepareNames = new LinkedList<>();
        List<String> argsForAlignmentAndTree = new LinkedList<>();
        List<String> argsForTreeEnumerate = new LinkedList<>();
        List<String> argsForGeneNeighbors = new LinkedList<>();
        List<String> argsForCdHit = new LinkedList<>();

        initArgsForPrepareNames(protoTreeInternal, argsForPrepareNames, listOfPrograms, listOfArgumentLists);

        String cdHitOutputFile = super.getRandomFileName(null);
        if (redundancy != null) {
            argsForCdHit.addAll(Arrays.asList(
                    protoTreeInternal.getFirstFileName(),
                    ParamPrefixes.OUTPUT.getPrefix() + cdHitOutputFile,
                    redundancy,
                    ParamPrefixes.CDHIT_PATH.getPrefix() + super.getProperties().getCdhit(),
                    ParamPrefixes.MEMORY.getPrefix() + super.getProperties().getCdhitMemory(),
                    ParamPrefixes.THREADS_GENERAL.getPrefix() + super.getProperties().getCdhitThreadNum()
            ));
            protoTreeInternal.setFirstFileName(ParamPrefixes.INPUT.getPrefix() + cdHitOutputFile);
            protoTreeInternal.setAlignmentFile(ParamPrefixes.INPUT.getPrefix() + cdHitOutputFile);
        }

        protoTreeInternal.setFields();
        String outAlgnFile = super.getRandomFileName(".fa");
        String outNewickTree = super.getRandomFileName("noPostfix");
        argsForAlignmentAndTree.addAll(protoTreeInternal.getFieldsForAlignmentAndTreeBuild());
        argsForAlignmentAndTree.addAll(Arrays.asList(
                ParamPrefixes.MAFFT_PATH.getPrefix() + super.getProperties().getMafft(),
                ParamPrefixes.MEGACC_PATH.getPrefix() + super.getProperties().getMegacc(),
                ParamPrefixes.OUTPUT_PARAMS.getPrefix() + super.getPrefix() + UUID.randomUUID().toString() + super.getPostfix(),
                ParamPrefixes.OUTPUT_TREE.getPrefix() + outNewickTree,
                ParamPrefixes.THREADS_MAFFT.getPrefix() + super.getProperties().getMafftThreadNum(),
                ParamPrefixes.THREADS_GENERAL.getPrefix() + super.getProperties().getMegaThreadNum(),
                ParamPrefixes.OUTPUT.getPrefix() + outAlgnFile
        ));

        if (!protoTreeRequest.getPhylogenyTest().equals("none")) {
            outNewickTree = outNewickTree + bootstrapFilePostfix;
        }

        String outNewickFile = super.getRandomFileName(".newick");
        String outOrderedAlgnFile = super.getRandomFileName(".fa");

        argsForTreeEnumerate.addAll(protoTreeInternal.getFieldsForTreeAndDomains());
        argsForTreeEnumerate.addAll(Arrays.asList(
                ParamPrefixes.INPUT_SECOND.getPrefix() + outAlgnFile,
                ParamPrefixes.INPUT_THIRD.getPrefix() + outNewickTree + ".nwk",
                ParamPrefixes.OUTPUT.getPrefix() + outOrderedAlgnFile,
                ParamPrefixes.OUTPUT_THIRD.getPrefix() + outNewickFile
        ));

        String outJsonFile = super.getRandomFileName(".json");
        argsForGeneNeighbors.addAll(protoTreeInternal.getFieldsForGeneNeighbors());
        argsForGeneNeighbors.addAll(Arrays.asList(
                ParamPrefixes.INPUT.getPrefix() + outNewickFile,
                ParamPrefixes.OUTPUT.getPrefix() + outJsonFile,
                ParamPrefixes.PROCESS_NUMBER.getPrefix() + super.getProperties().getFetchFromMistProcNum()
        ));

        if (redundancy == null)
            protoTreeInternal.setOutputFilesNames(Arrays.asList(outNewickFile, outJsonFile, outOrderedAlgnFile));
        else
            protoTreeInternal.setOutputFilesNames(Arrays.asList(outNewickFile, outJsonFile, outOrderedAlgnFile, cdHitOutputFile+".clstr"));

        listOfPrograms.addAll(Arrays.asList(
                super.getProperties().getAlignAndBuildTree(),
                super.getProperties().getEnumerate(),
                super.getProperties().getGeneNeighbors()
        ));
        listOfArgumentLists.addAll(Arrays.asList(
                argsForAlignmentAndTree,
                argsForTreeEnumerate,
                argsForGeneNeighbors
        ));
        if (redundancy != null) {
            listOfPrograms.add(1, super.getProperties().getReduceWithCdHit());
            listOfArgumentLists.add(1, argsForCdHit);
        }
        String[] arrayOfInterpreters = super.prepareInterpreters(listOfPrograms.size());
        String[] arrayOfPrograms = listOfPrograms.toArray(new String[listOfPrograms.size()]);

        super.prepareCommandArgumentsCommon(protoTreeInternal, arrayOfInterpreters, arrayOfPrograms, listOfArgumentLists);
        return protoTreeInternal;
    }

    private String initArgsForPrepareNames(ProtoTreeInternal protoTreeInternal, List<String> argsForPrepareNames,
                                           List<String> listOfPrograms, List<List<String>> listOfArgumentLists) {
        String preparedFile = null;
        protoTreeInternal.setFieldsForPrepareNames();
        if (!protoTreeInternal.isFullPipeline().equals("false")) {
            preparedFile = super.getRandomFileName(null);
            argsForPrepareNames.addAll(Arrays.asList(protoTreeInternal.getFirstFileName(), ParamPrefixes.OUTPUT.getPrefix() + preparedFile));
            argsForPrepareNames.addAll(protoTreeInternal.getFieldsForPrepareNames());
            if (protoTreeInternal.getDoAlign().equals("-d yes"))
                argsForPrepareNames.add(ParamPrefixes.REMOVE_DASHES.getPrefix() + "true");
            else
                argsForPrepareNames.add(ParamPrefixes.REMOVE_DASHES.getPrefix() + "false");
            argsForPrepareNames.add(ParamPrefixes.FETCH_FROM_MIST.getPrefix() + super.getProperties().getFetchFromMist());
            argsForPrepareNames.add(ParamPrefixes.FETCH_FROM_NCBI.getPrefix() + super.getProperties().getFetchFromNCBI());
            argsForPrepareNames.add(ParamPrefixes.PROCESS_NUMBER.getPrefix() + super.getProperties().getFetchFromMistProcNum());
            protoTreeInternal.setAlignmentFile(ParamPrefixes.INPUT.getPrefix() + preparedFile);
            protoTreeInternal.setFirstFileName(ParamPrefixes.INPUT.getPrefix() + preparedFile);
        } else if (protoTreeInternal.isFullPipeline().equals("false")) {
            preparedFile = super.getRandomFileName(".newick");
            argsForPrepareNames.addAll(Arrays.asList(protoTreeInternal.getTreeFile(), ParamPrefixes.OUTPUT_SECOND.getPrefix() + preparedFile));
        }
        listOfPrograms.add(super.getProperties().getPrepareNames());
        listOfArgumentLists.add(argsForPrepareNames);
        return preparedFile;
    }

    @Override
    @Async
    public void runMainProgram(ProtoTreeInternal protoTreeInternal) throws IncorrectRequestException {
        int counter = 0;
        for (List<String> commandArgument : protoTreeInternal.getCommandsAndArguments()) {
            if (protoTreeInternal.isFullPipeline().equals("false"))
                super.saveStage(protoTreeInternal, counter++, counterToStageOneInputPartial);
            else if (protoTreeInternal.isFullPipeline().equals("true")) {
                if(protoTreeInternal.getRedundancy() == null)
                    super.saveStage(protoTreeInternal, counter++, counterToStageOneInputFull);
                else
                    super.saveStage(protoTreeInternal, counter++, counterToStageOneInputFullWithRedund);
            }
            try {
                super.launchProcess(commandArgument, protoTreeInternal);
            } catch (Exception exception) {
                if (exception.getMessage().contains(Status.megaError.getStatusEnum()))
                    super.saveError(protoTreeInternal, exception.getMessage());
                else
                    super.saveError(protoTreeInternal, null);
                throw exception;
            }
        }
        super.saveResultToDb(protoTreeInternal);
    }


}
