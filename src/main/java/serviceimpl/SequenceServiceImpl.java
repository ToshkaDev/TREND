package serviceimpl;

import static com.google.common.base.Strings.isNullOrEmpty;
import static converters.ConverterMain.fromSeqRequestToSeqInternal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

import biojobs.BioJobDao;
import biojobs.BioJobResultDao;
import enums.ParamPrefixes;
import exceptions.IncorrectRequestException;
import org.springframework.stereotype.Service;

import model.internal.SequenceInternal;
import model.request.SequenceRequest;
import service.SequenceService;
import service.StorageService;
import springconfiguration.AppProperties;

@Service
public class SequenceServiceImpl extends BioUniverseServiceImpl implements SequenceService {
	private final String getSeqByName;
	private final String makeUnique;



	public SequenceServiceImpl(final StorageService storageService, final AppProperties properties, final BioJobDao bioJobDao, final BioJobResultDao bioJobResultDao) {
		super(storageService, properties, bioJobResultDao, bioJobDao);
		this.getSeqByName = super.getProperties().getGetSeqByName();
		this.makeUnique = super.getProperties().getMakeUnique();
	}

	@Override
	public String getByName(SequenceRequest sequenceRequest) throws IncorrectRequestException {
		return launchProcessAndGetResultFileName(sequenceRequest, getSeqByName);
	}

	@Override
	public String makeUnique(SequenceRequest sequenceRequest) throws IncorrectRequestException {
		return launchProcessAndGetResultFileName(sequenceRequest, makeUnique);
	}

	@Override
	public String extract() {
		return "";
	}

	public String launchProcessAndGetResultFileName(SequenceRequest sequenceRequest, String commandName) throws IncorrectRequestException {
		SequenceInternal sequenceInternal = storeFileAndGetInternalRepresentation(sequenceRequest);
        String resultFileName = super.getPrefix() + UUID.randomUUID().toString() + super.getPostfix();
        sequenceInternal.setAllFields();

		List<String> commandArguments = sequenceInternal.getAllFields();
		commandArguments.add(0, super.getPython());
		commandArguments.add(1, commandName);
        commandArguments.add(ParamPrefixes.OUTPUT.getPrefix() + resultFileName);

        System.out.println(commandArguments.toString());

		super.launchProcess(commandArguments);
		return resultFileName;
	}

	public SequenceInternal storeFileAndGetInternalRepresentation(SequenceRequest sequenceRequest) throws IncorrectRequestException {
		String firstFileName = null;
		String secondFileName = null;

		if (sequenceRequest.getFirstFile() != null) {
			if (!isNullOrEmpty(sequenceRequest.getFirstFileTextArea())) {
				throw new IncorrectRequestException("firstFileTextArea and firstFileName are both not empty");
			} else {
				firstFileName = super.getStorageService().store(sequenceRequest.getFirstFile());
			}
		} else if (!isNullOrEmpty(sequenceRequest.getFirstFileTextArea())) {
			firstFileName = super.getStorageService().createAndStore(sequenceRequest.getFirstFileTextArea());
		}

		if (sequenceRequest.getSecondFile() != null) {
			if (!isNullOrEmpty(sequenceRequest.getSecondFileTextArea())) {
				throw new IncorrectRequestException("secondFileTextArea and firstFileName are both not empty");
			} else {
				secondFileName = super.getStorageService().store(sequenceRequest.getSecondFile());
			}
		} else if (!isNullOrEmpty(sequenceRequest.getSecondFileTextArea())) {
			secondFileName = super.getStorageService().createAndStore(sequenceRequest.getSecondFileTextArea());
		}
		SequenceInternal sequenceInternal = fromSeqRequestToSeqInternal(sequenceRequest, firstFileName, secondFileName);
		return sequenceInternal;
	}
}
