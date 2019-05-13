package service;

import exceptions.IncorrectRequestException;
import model.internal.ProtoTreeInternal;
import model.request.ProtoTreeRequest;

public interface PipelineService {
    ProtoTreeInternal storeFilesAndPrepareCommandArguments(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException;
    void runMainProgram(ProtoTreeInternal protoTreeInternal) throws IncorrectRequestException;
}
