//package serviceimpl;
//
//import biojobs.BioJobDao;
//import biojobs.BioJobResultDao;
//import exceptions.IncorrectRequestException;
//import model.internal.ProtoTreeInternal;
//import model.request.ProtoTreeRequest;
//import service.GeneNeighborhoodsService;
//import service.StorageService;
//import springconfiguration.AppProperties;
//
//public class GeneNeighborhoodsServiceImpl extends BioUniverseServiceImpl implements GeneNeighborhoodsService {
//
//    public GeneNeighborhoodsServiceImpl(final StorageService storageService, final AppProperties properties, final BioJobDao bioJobDao, final BioJobResultDao bioJobResultDao) {
//        super(storageService, properties, bioJobResultDao, bioJobDao);
//        counterToStageOneInput.put(1, "['Predicting proteins features.']");
//        counterToStageOneInput.put(2, "['Predicting proteins features.', 'Aligning and building tree.']");
//        counterToStageOneInput.put(3, "['Predicting proteins features.', 'Aligning and building tree.', 'Ordering alignment and putting features and tree together.-last']");
//
//        counterToStageTwoInputs.put(2, "['Predicting proteins features.']");
//        counterToStageTwoInputs.put(3, "['Predicting proteins features.', 'Aligning and building tree.']");
//        counterToStageTwoInputs.put(4, "['Predicting proteins features.', 'Aligning and building tree.', 'Ordering alignment and putting features and tree together.-last']");
//
//        counterToStagePartial.put(1, "['Predicting proteins features.']");
//        counterToStagePartial.put(2, "['Predicting proteins features.', 'Ordering alignment and putting features and tree together.-last']");
//    }
//
//    public ProtoTreeInternal storeFilesAndPrepareCommandArguments(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException {
//        ProtoTreeInternal protoTreeInternal;
//        if (protoTreeRequest.isFullPipeline().equals("true")) {
//            protoTreeInternal = fullPipelineProcessing(protoTreeRequest);
//
//        } else {
//            protoTreeInternal = partialPipelineProcessing(protoTreeRequest);
//        }
//        return protoTreeInternal;
//    }
//}
