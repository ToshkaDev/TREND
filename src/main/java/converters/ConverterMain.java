package converters;

import enums.ParamPrefixes;
import model.internal.ProtoTreeInternal;
import model.request.ProtoTreeRequest;
import service.StorageService;

public class ConverterMain {

	public ConverterMain(StorageService storageService) {
	}

	public static ProtoTreeInternal fromProtoTreeRequestToProtoTreeInternal(ProtoTreeRequest protoTreeRequest,
                                                                            String firstFileName, String secondFileName) {
		ProtoTreeInternal protoTreeInternal = new ProtoTreeInternal();
        protoTreeInternal.setFirstFileName(checkForNullAndGet(ParamPrefixes.INPUT.getPrefix(), firstFileName));
		protoTreeInternal.setSecondFileName(checkForNullAndGet(ParamPrefixes.INPUT_SECOND.getPrefix(), secondFileName));
		protoTreeInternal.setCommandToBeProcessedBy(protoTreeRequest.getCommandToBeProcessedBy());

        protoTreeInternal.setTreeBuildMethod(checkForNullAndGet(ParamPrefixes.TREE_BUILD_METHOD.getPrefix(), protoTreeRequest.getTreeBuildMethod()));
        protoTreeInternal.setAaSubstModel(checkForNullAndGet(ParamPrefixes.AA_SUBST_MODEL.getPrefix(), protoTreeRequest.getAaSubstModel()));
        protoTreeInternal.setAaSubstRate(checkForNullAndGet(ParamPrefixes.AA_SUBST_RATE.getPrefix(), protoTreeRequest.getAaSubstRate()));
        protoTreeInternal.setInitialTreeForMl(checkForNullAndGet(ParamPrefixes.INITIAL_TREE_ML.getPrefix(), protoTreeRequest.getInitialTreeForMl()));
        protoTreeInternal.setGapsAndMissingData(checkForNullAndGet(ParamPrefixes.GAPS_AND_MISSING_DATA.getPrefix(), protoTreeRequest.getGapsAndMissingData()));
        protoTreeInternal.setSiteCovCutOff(checkForNullAndGet(ParamPrefixes.SITE_COV_CUTOFF.getPrefix(), protoTreeRequest.getSiteCovCutOff()));
        protoTreeInternal.setPhylogenyTest(checkForNullAndGet(ParamPrefixes.PHYLOGENY_TEST.getPrefix(), protoTreeRequest.getPhylogenyTest()));
        protoTreeInternal.setNumberOrReplicates(checkForNullAndGet(ParamPrefixes.NUMBER_OF_REPLICATES.getPrefix(), protoTreeRequest.getNumberOrReplicates()));
		protoTreeInternal.setAlignmentAlg(checkForNullAndGet(ParamPrefixes.ALGORITHM.getPrefix(), protoTreeRequest.getAlignmentAlg()));

		protoTreeInternal.setDomainPredictionProgram(checkForNullAndGet(ParamPrefixes.DOMAINS_PREDICTION_PROGRAM.getPrefix(), protoTreeRequest.getDomainPredictionProgram()));
		protoTreeInternal.setDomainPredictionDb(checkForNullAndGet(ParamPrefixes.DOMAINS_PREDICTION_DB.getPrefix(), protoTreeRequest.getDomainPredictionDb()));


        return protoTreeInternal;
	}

	private static String checkForNullAndGet(String paramPrefix, String param) {
		if (param != null) {
			return paramPrefix + param;
		}
		return null;
	}

}
