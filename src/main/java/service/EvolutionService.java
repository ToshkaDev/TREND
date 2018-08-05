package service;

import biojobs.BioJob;
import model.internal.ProtoTreeInternal;
import exceptions.IncorrectRequestException;
import model.request.ProtoTreeRequest;


public interface EvolutionService {
	void runMainProgram(ProtoTreeInternal protoTreeInternal) throws IncorrectRequestException;
	ProtoTreeInternal storeFilesAndPrepareCommandArguments(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException;
	BioJob getBioJobIfFinished(int jobId);

}
