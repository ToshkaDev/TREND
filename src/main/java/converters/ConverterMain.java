package converters;

import enums.ParamPrefixes;
import model.internal.ProtoTreeInternal;
import model.request.ProtoTreeRequest;
import service.StorageService;

public class ConverterMain {

	public ConverterMain(StorageService storageService) {
	}

	public static ProtoTreeInternal fromProtoTreeRequestToProtoTreeInternal(ProtoTreeRequest protoTreeRequest,
                                                                            String firstFileName, String secondFileName,
																			String alignedFileName, String treeFileName) {
		ProtoTreeInternal protoTreeInternal = new ProtoTreeInternal();
        protoTreeInternal.setFirstFileName(checkForNullAndGet(ParamPrefixes.INPUT.getPrefix(), firstFileName));
		protoTreeInternal.setSecondFileName(checkForNullAndGet(ParamPrefixes.INPUT.getPrefix(), secondFileName));
		protoTreeInternal.setAlignmentFile(checkForNullAndGet(ParamPrefixes.INPUT.getPrefix(), alignedFileName));
		protoTreeInternal.setTreeFile(checkForNullAndGet(ParamPrefixes.INPUT_SECOND.getPrefix(),treeFileName));
		protoTreeInternal.setCommandToBeProcessedBy(protoTreeRequest.getCommandToBeProcessedBy());

		protoTreeInternal.setDoAlign(checkForNullAndGet(ParamPrefixes.DO_ALIGN.getPrefix(), protoTreeRequest.getDoAlign()));
        protoTreeInternal.setTreeBuildMethod(checkForNullAndGet(ParamPrefixes.TREE_BUILD_METHOD.getPrefix(), protoTreeRequest.getTreeBuildMethod()));
        protoTreeInternal.setAaSubstModel(checkForNullAndGet(ParamPrefixes.AA_SUBST_MODEL.getPrefix(), protoTreeRequest.getAaSubstModel()));
        protoTreeInternal.setAaSubstRate(checkForNullAndGet(ParamPrefixes.AA_SUBST_RATE.getPrefix(), protoTreeRequest.getAaSubstRate()));
        protoTreeInternal.setInitialTreeForMl(checkForNullAndGet(ParamPrefixes.INITIAL_TREE_ML.getPrefix(), protoTreeRequest.getInitialTreeForMl()));
        protoTreeInternal.setGapsAndMissingData(checkForNullAndGet(ParamPrefixes.GAPS_AND_MISSING_DATA.getPrefix(), protoTreeRequest.getGapsAndMissingData()));
        protoTreeInternal.setSiteCovCutOff(checkForNullAndGet(ParamPrefixes.SITE_COV_CUTOFF.getPrefix(), protoTreeRequest.getSiteCovCutOff()));
        protoTreeInternal.setPhylogenyTest(checkForNullAndGet(ParamPrefixes.PHYLOGENY_TEST.getPrefix(), protoTreeRequest.getPhylogenyTest()));
        protoTreeInternal.setNumberOrReplicates(checkForNullAndGet(ParamPrefixes.NUMBER_OF_REPLICATES.getPrefix(), protoTreeRequest.getNumberOrReplicates()));
		protoTreeInternal.setAlignmentAlg(checkForNullAndGet(ParamPrefixes.ALGORITHM.getPrefix(), protoTreeRequest.getAlignmentAlg()));

		protoTreeInternal.setDoPredictFeatures(checkForNullAndGet(ParamPrefixes.DO_PREDICT_FETURES.getPrefix(), protoTreeRequest.getDoPredictFeatures()));
		protoTreeInternal.setDomainPredictionProgram(checkForNullAndGet(ParamPrefixes.DOMAINS_PREDICTION_PROGRAM.getPrefix(), protoTreeRequest.getDomainPredictionProgram()));
		protoTreeInternal.setDomainPredictionDb(checkForNullAndGet(ParamPrefixes.DOMAINS_PREDICTION_DB.getPrefix(), protoTreeRequest.getDomainPredictionDb()));
		protoTreeInternal.seteValue(checkEvalueAndGet(ParamPrefixes.EVAL_THRESH.getPrefix(), protoTreeRequest.geteValue()));
		protoTreeInternal.setProbability(checkProbabilityAndGet(ParamPrefixes.PROBABILITY.getPrefix(), protoTreeRequest.getProbability()));
		protoTreeInternal.setLcrPrediction(checkCheckBox(ParamPrefixes.RUN_SEGMASKER.getPrefix(), protoTreeRequest.getLcrPrediction()));
		protoTreeInternal.setEnumerate(checkCheckBox(ParamPrefixes.ENUMERATE.getPrefix(), protoTreeRequest.getEnumerate()));

		protoTreeInternal.setDomainTolerance(checkForNullAndGet(ParamPrefixes.NOT_SHARED_DOMAIN_TOLERANCE.getPrefix(), protoTreeRequest.getDomainTolerance()));
		protoTreeInternal.setOperonTolerance(checkForNullAndGet(ParamPrefixes.OPERON_TOLERANCE.getPrefix(), protoTreeRequest.getOperonTolerance()));
		protoTreeInternal.setNumberOfNeighbors(checkForNullAndGet(ParamPrefixes.NUMBER_OF_NEIGHBORS.getPrefix(), protoTreeRequest.getNumberOfNeighbors()));

		protoTreeInternal.setFetchFromIds(checkCheckBox(ParamPrefixes.FETCH_FROM_IDS.getPrefix(), protoTreeRequest.getFetchFromIds()));
		protoTreeInternal.setFetchFromTree(checkCheckBox(ParamPrefixes.FETCH_FROM_TREE.getPrefix(), protoTreeRequest.getFetchFromTree()));
		protoTreeInternal.setFullPipeline(protoTreeRequest.isFullPipeline());
		protoTreeInternal.setProtoTreeCookies(protoTreeRequest.getProtoTreeCookies());
		protoTreeInternal.setRedundancy(checkForNullAndGet(ParamPrefixes.REDUNDANCY.getPrefix(), protoTreeRequest.getRedundancy()));
		return protoTreeInternal;
	}

	private static String checkForNullAndGet(String paramPrefix, String param) {
		if (param != null) {
			return paramPrefix + param;
		}
		return null;
	}

	// Should be less or equal to 1 and bigger or equal to 0
	private static String checkEvalueAndGet(String paramPrefix, String evalue) {
		if (evalue != null) {
			Double doubleEvalue = Double.valueOf(evalue);
			if (doubleEvalue <= 1 && doubleEvalue >= 0) {
				return paramPrefix + evalue;
			}
		}
		return null;
	}

	// Should be bigger or equal to 0
	private static String checkProbabilityAndGet(String paramPrefix, String probability) {
		if (probability != null) {
			Double doubleProbability = Double.valueOf(probability);
			if (doubleProbability >= 0) {
				return paramPrefix + probability;
			}
		}
		return null;
	}

	private static String checkCheckBox(String paramPrefix, String checkBox) {
		if (checkBox != null && (checkBox.equals("checked") || checkBox.equals("true"))) {
			return paramPrefix + "true";
		}
		return null;
	}
}
