package service;

import biojobs.BioJob;
import exceptions.IncorrectRequestException;
import model.internal.ProtoTreeInternal;
import model.request.ProtoTreeRequest;

public interface GeneNeighborhoodsService {
    void runMainProgram(ProtoTreeInternal protoTreeInternal) throws IncorrectRequestException;
    ProtoTreeInternal storeFilesAndPrepareCommandArguments(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException;
    BioJob getBioJob(int jobId);
}
