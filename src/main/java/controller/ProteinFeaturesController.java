package controller;

import biojobs.BioJob;
import biojobs.BioJobResult;
import enums.Status;
import model.internal.ProtoTreeInternal;

import model.request.ProtoTreeRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import service.BioUniverseService;
import service.ProtoTreeService;
import service.StorageService;
import exceptions.IncorrectRequestException;


import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by vadim on 03/30/18.
 */

@Controller
@RequestMapping("/domains")
public class ProteinFeaturesController extends BioUniverseController {

    @Autowired
    public final ProtoTreeService proteinFeaturesService;


    private static final Log logger = LogFactory.getLog(ProteinFeaturesController.class);

	public ProteinFeaturesController(StorageService storageService, ProtoTreeService proteinFeaturesService) {
    	super(storageService);
    	this.proteinFeaturesService = proteinFeaturesService;
    }


    @GetMapping(value={""})
    public String protoTree(Model model) {
        model.addAttribute("sendOrGiveResult", "/js/send-and-process-data.js");
        addToModelCommon(model);
        return "main-view  :: addContent(" +
                "fragmentsMain='features-fragments', searchArea='proto-tree', filter='proto-tree-filter'" +
                ", navigation='navigator')";
    }

    @GetMapping(value={"tree/{jobId:.+}"})
    public String result(@PathVariable String jobId, Model model) {
        model.addAttribute("sendOrGiveResult", "/js/result-processing.js");
        model.addAttribute("jobId", jobId);
        addToModelCommon(model);
        return "main-view  :: addContent(fragmentsMain='features-fragments', result='result')";
    }

    @PostMapping(value="process-request", produces="text/plain")
    @ResponseBody
    public String processTreeRequest(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException, ExecutionException, InterruptedException {
	    ProtoTreeInternal protoTreeInternal = null;
        //Split it to several functions because 'PROTO_TREE' method is asynchronous
        //and files in 'listOfFiles' field of evolutionRequest are got cleared at the end of request processing.
        protoTreeRequest.setCommandToBeProcessedBy("domains");
        protoTreeInternal = proteinFeaturesService.storeFilesAndPrepareCommandArguments(protoTreeRequest);
        String fullOrPartialPipe = protoTreeRequest.isFullPipeline().equals("true") ? "f" : "p";
        String jobId = protoTreeInternal.getJobId() + "-" + fullOrPartialPipe + "-" + protoTreeInternal.getProtoTreeCookies();
        proteinFeaturesService.runMainProgram(protoTreeInternal);
        return jobId;
    }

    @GetMapping(value="tree/get-filename", produces="application/json")
    @ResponseBody
    public Map<String, List<String>> getFileNameIfReady(@RequestParam("jobId") String jobId) {
        BioJob bioJob;
        String urlPath = ServletUriComponentsBuilder.fromCurrentContextPath().path("domains/univ_files/").build().toString();

        Map<String, List<String>> result = new HashMap<>();
        result.put(Status.status.getStatusEnum(), super.statusNoSuchBioJob);

        List<String> listOfResultFileNames;

	     if (jobId != null ) {
	        String jobIdSplitted[] = jobId.split("-");
	        int id = Integer.valueOf(jobIdSplitted[0]);
	        String cookieId = jobIdSplitted[2];
            bioJob = proteinFeaturesService.getBioJob(id);
	        if (bioJob != null && bioJob.getCookieId().equals(cookieId)) {
	            if (bioJob.isFinished()) {
                    listOfResultFileNames = bioJob.getBioJobResultList().stream().map(bjResult -> urlPath + bjResult.getResultFileName()).collect(Collectors.toList());
                    result.put(Status.result.getStatusEnum(), listOfResultFileNames);
                    result.put(Status.status.getStatusEnum(), super.statusReady);
                } else if (bioJob.getStage().contains(Status.error.getStatusEnum())) {
                    List<String> statusError = new ArrayList<>(Arrays.asList(Status.error.getStatusEnum()));
                    if (bioJob.getStage().contains(Status.megaError.getStatusEnum()))
                        statusError.add(bioJob.getStage());
                    result.put(Status.status.getStatusEnum(), statusError);
                } else {
                    result.put(Status.status.getStatusEnum(), super.statusNotReady);
                }
                result.put(Status.stage.getStatusEnum(), new LinkedList<>(Arrays.asList(bioJob.getStage())));
            }
        }
        return result;
    }

    @GetMapping("univ_files/{filename:.+}")
    public void getFileFromDbP(@PathVariable String filename, HttpServletResponse response) throws IOException {
        BioJobResult bioJobResult = ((BioUniverseService) proteinFeaturesService).getBioJobResultDao().findByResultFileName(filename);
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

    @Override
    void addToModelCommon(Model model) {
        model.addAttribute("mainTab", "domains");
        model.addAttribute("getFieldsValues", "/js/get-fields-values.js");
    }

}
