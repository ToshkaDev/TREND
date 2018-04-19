package controller;

import biojobs.BioJob;
import biojobs.BioJobResult;
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
import service.EvolutionService;
import service.StorageService;
import exceptions.IncorrectRequestException;
import enums.BioPrograms;


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
@RequestMapping("/prototree")
public class EvolutionController extends BioUniverseController {

    @Autowired
    public final EvolutionService evolutionService;


    private static final Log logger = LogFactory.getLog(EvolutionController.class);

    private final List<String> statusReady = Arrays.asList("ready");
    private final List<String> statusNotReady = Arrays.asList("notReady");


	public EvolutionController(StorageService storageService, EvolutionService evolutionService) {
    	super(storageService);
    	this.evolutionService = evolutionService;
    }

    @GetMapping(value={"", "/"})
    public String protoTree(Model model) {
        addToModelCommon(model, "/js/send-and-process-data.js");
        model.addAttribute("subnavigationTab", BioPrograms.PROTO_TREE.getProgramName());
        return "main-view  :: addContent(" +
                "fragmentsMain='evolution-fragments', searchArea='proto-tree', filter='proto-tree-filter')";
    }

    @GetMapping(value={"/tree-for-you/{jobId:.+}"})
    public String result(@PathVariable Integer jobId, Model model) {
        addToModelCommon(model, "/js/result-processing.js");
        System.out.println("jobId " + jobId);
        model.addAttribute("jobId", jobId);
        model.addAttribute("subnavigationTab", BioPrograms.PROTO_TREE.getProgramName());
        return "main-view  :: addContent(" +
                "fragmentsMain='evolution-fragments', " +
                "tab='evolution-navbar', result='result')";
    }

    @PostMapping(value="/process-request", produces="text/plain")
    @ResponseBody
    public String processTreeRequest(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException, ExecutionException, InterruptedException {
        ProtoTreeInternal protoTreeInternal = null;

        //Split it to several functions because 'PROTO_TREE' method is asynchronous
        //and files in 'listOfFiles' field of evolutionRequest are got cleared at the end of request processing.
        if (protoTreeRequest.getCommandToBeProcessedBy().equals(BioPrograms.PROTO_TREE.getProgramName())) {
            protoTreeInternal = evolutionService.storeFilesAndPrepareCommandArgumentsP(protoTreeRequest);
        }

        Integer jobId = protoTreeInternal.getJobId();
        evolutionService.runMainProgramP(protoTreeInternal);

        return String.valueOf(jobId);
    }

    @GetMapping(value="/tree-for-you/get-filename", produces="application/json")
    @ResponseBody
    public Map<String, List<String>> getFileNameIfReady(@RequestParam("jobId") String jobId) {
        BioJob bioJob;
        String urlPath = ServletUriComponentsBuilder.fromCurrentContextPath().path("/prototree/univ_files/").build().toString();

        Map<String, List<String>> result = new HashMap<>();
        result.put("status", statusNotReady);

        List<String> listOfResultFileNames = null;

	     if (jobId != null ) {
            bioJob = evolutionService.getBioJobIfFinished(Integer.valueOf(jobId.split("-")[0]));
            if (bioJob != null)
                listOfResultFileNames = bioJob.getBioJobResultList().stream().map(bjResult -> urlPath + bjResult.getResultFileName()).collect(Collectors.toList());
                result.put("result", listOfResultFileNames);
                result.put("status", statusReady);
        }
        return result;
    }

    @GetMapping("/univ_files/{filename:.+}")
    public void getFileFromDbP(@PathVariable String filename, HttpServletResponse response) throws IOException {
        BioJobResult bioJobResult = ((BioUniverseService) evolutionService).getBioJobResultDao().findByResultFileName(filename);

        response.setContentType("image/svg+xml");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + bioJobResult.getResultFileName());

        OutputStream outputStream = response.getOutputStream();
        OutputStream buffOutputStream= new BufferedOutputStream(outputStream);
        OutputStreamWriter outputwriter = new OutputStreamWriter(buffOutputStream);

        outputwriter.write(bioJobResult.getResultFile());
        outputwriter.flush();
        outputwriter.close();
    }

    @Override
    void addToModelCommon(Model model, String sendOrGiveResult) {
        model.addAttribute("mainTab", "home");
        model.addAttribute("sendOrGiveResult", sendOrGiveResult);
        model.addAttribute("specificJs", "/js/evolution.js");
    }

}
