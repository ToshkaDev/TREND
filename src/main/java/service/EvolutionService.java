package service;

import biojobs.BioJob;
import model.internal.EvolutionInternal;
import model.internal.ProtoTreeInternal;
import model.request.EvolutionRequest;
import exceptions.IncorrectRequestException;
import model.request.ProtoTreeRequest;


public interface EvolutionService {
	void runMainProgram(EvolutionInternal evolutionInternal) throws IncorrectRequestException;

	String[] createDirs();
	String[] createDirsConcat();

	ProtoTreeInternal storeFilesAndPrepareCommandArgumentsP(ProtoTreeRequest protoTreeRequest);
	EvolutionInternal storeFilesAndPrepareCommandArguments (EvolutionRequest evolutionRequest, String[] locations) throws IncorrectRequestException;
	EvolutionInternal storeFilesAndPrepareCommandArgumentsConcat(final EvolutionRequest evolutionRequest, String[] locations) throws IncorrectRequestException;
	BioJob getBioJobIfFinished(int jobId);

}
