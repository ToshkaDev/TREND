package controller;

import biojobs.BioJob;
import biojobs.BioJobResult;
import enums.Status;
import exceptions.IncorrectRequestException;
import model.internal.ProtoTreeInternal;
import model.request.ProtoTreeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import service.BioUniverseService;
import service.PipelineService;
import service.StorageService;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


/**
 * Created by vadim on 8/13/17.
 */
public abstract class BioUniverseController {

    private final StorageService storageService;
    protected final List<String> statusReady = Arrays.asList("ready");
    protected final List<String> statusNotReady = Arrays.asList("notReady");
    protected final List<String> statusNoSuchBioJob = Arrays.asList("noSuchBioJob");
    private final String baseUrl = "http://localhost:8080";
    // private final String baseUrl = "http://trend.zhulinlab.org";

    @Autowired
    public BioUniverseController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> handleFileDownload(@PathVariable("filename") String filename) {
        System.out.println("filename " + filename);
        System.out.println("file" + storageService.loadAsResource(filename).toString());
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getFilename()+"\"")
                .body(file);
    }

    public String processTreeRequestCommon(ProtoTreeRequest protoTreeRequest, PipelineService pipelineService) throws IncorrectRequestException, ExecutionException, InterruptedException {
        //Split it to several functions because 'PROTO_TREE' method is asynchronous
        //and files in 'listOfFiles' field of evolutionRequest are got cleared at the end of request processing.
        ProtoTreeInternal protoTreeInternal = pipelineService.storeFilesAndPrepareCommandArguments(protoTreeRequest);

        String fullOrPartialPipe = protoTreeRequest.isFullPipeline().equals("true") ? "pipeline=full" : "pipeline=partial";
        String reduceOrNotRedundancy = protoTreeRequest.getRedundancy() != null
                && protoTreeRequest.getSecondFile() == null
                && protoTreeRequest.getSecondFileArea() == null ? "reduce=true" : "reduce=false";
        String predictProteinFeatures = protoTreeRequest.getDoPredictFeatures().equals("yes") ? "features=true" : "features=false";
        String cookies = "eon=" + protoTreeInternal.getProtoTreeCookies();

        String jobId = String.format("%d?%s&%s&%s&%s", protoTreeInternal.getJobId(), fullOrPartialPipe, reduceOrNotRedundancy, predictProteinFeatures, cookies);
        pipelineService.runMainProgram(protoTreeInternal);
        return jobId;
    }

    public Map<String, List<String>> getFileNameIfReadyCommon(String jobId, String cookies, BioUniverseService bioUniverseService, String specificPath) {
        BioJob bioJob;
        // String urlPath = ServletUriComponentsBuilder.fromCurrentContextPath().path(specificPath + "/univ_files/").build().toString();
        String urlPath = ServletUriComponentsBuilder.fromHttpUrl(baseUrl).path(specificPath + "/univ_files/").build().toString();
        Map<String, List<String>> result = new HashMap<>();
        result.put(Status.status.getStatusEnum(), statusNoSuchBioJob);
        List<String> listOfResultFileNames;
        if (jobId != null ) {
            MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUriString(jobId).build().getQueryParams();
            bioJob = bioUniverseService.getBioJobDao().findByJobId(Integer.valueOf(jobId));
            if (bioJob != null && bioJob.getCookieId().equals(cookies)) {
                if (bioJob.isFinished()) {
                    listOfResultFileNames = bioJob.getBioJobResultList().stream().map(bjResult -> urlPath + bjResult.getResultFileName()).collect(Collectors.toList());
                    result.put(Status.result.getStatusEnum(), listOfResultFileNames);
                    result.put(Status.status.getStatusEnum(), statusReady);
                } else if (bioJob.getStage() != null && bioJob.getStage().contains(Status.error.getStatusEnum())) {
                    List<String> statusError = new ArrayList<>(Arrays.asList(Status.error.getStatusEnum()));
                    if (bioJob.getStage().contains(Status.megaError.getStatusEnum()))
                        statusError.add(bioJob.getStage());
                    result.put(Status.status.getStatusEnum(), statusError);
                } else {
                    result.put(Status.status.getStatusEnum(), statusNotReady);
                }
                result.put(Status.stage.getStatusEnum(), new LinkedList<>(Arrays.asList(bioJob.getStage())));
                result.put(Status.stageDetails.getStatusEnum(), new LinkedList<>(Arrays.asList(bioJob.getStageDetails())));
            }
        }
        return result;
    }

    public void getFileFromDbCommon(String filename, HttpServletResponse response, BioUniverseService bioUniverseService) throws IOException {
        BioJobResult bioJobResult = bioUniverseService.getBioJobResultDao().findByResultFileName(filename);
        if (!filename.split("\\.")[1].equals("svg")) {
            response.setContentType("text/plain");
        } else {
            response.setContentType("image/svg+xml");
        }

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + bioJobResult.getResultFileName());

        OutputStream outputStream = response.getOutputStream();
        OutputStream buffOutputStream= new BufferedOutputStream(outputStream);
        OutputStreamWriter outputwriter = new OutputStreamWriter(buffOutputStream);

        outputwriter.write(bioJobResult.getResultFile());
        outputwriter.flush();
        outputwriter.close();
    }

    abstract void addToModelCommon(Model model);
}
