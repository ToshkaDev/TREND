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
import service.ProtoTreeService;
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
@RequestMapping("/")
public class ProteinFeaturesController extends BioUniverseController {

    @Autowired
    public final ProtoTreeService proteinFeaturesService;


    private static final Log logger = LogFactory.getLog(ProteinFeaturesController.class);

    private final List<String> statusReady = Arrays.asList("ready");
    private final List<String> statusNotReady = Arrays.asList("notReady");
    private final List<String> noSuchBioJob = Arrays.asList("noSuchBioJob");


	public ProteinFeaturesController(StorageService storageService, ProtoTreeService proteinFeaturesService) {
    	super(storageService);
    	this.proteinFeaturesService = proteinFeaturesService;
    }

    @GetMapping(value={"/help"})
    public String help(Model model) {
        model.addAttribute("mainTab", "help");
        return "main-view  :: addContent(" +
                "fragmentsMain='about', help='help')";
    }

    @GetMapping(value={"", "/home"})
    public String protoTree(Model model) {
        model.addAttribute("mainTab", "home");
        addToModelCommon(model, "/js/send-and-process-data.js");
        return "main-view  :: addContent(" +
                "fragmentsMain='evolution-fragments', searchArea='proto-tree', filter='proto-tree-filter'" +
                ", navigation='navigator')";
    }

    @GetMapping(value={"gene-neighborhoods"})
    public String geneNeighborhoods(Model model) {
        model.addAttribute("mainTab", "gene-neighborhoods");
        model.addAttribute("newickJs", "/js/vendor/newick_modified.js");
        addToModelCommon(model, "/js/result-processing-gene-neighborhoods.js");
        return "main-view  :: addContent(" +
                "fragmentsMain='evolution-fragments', searchArea='gene-neighborhoods')";
    }

    @GetMapping(value={"gene-neighborhoods/tree/{jobId:.+}"})
    public String geneNeighborhoodsResult(Model model) {
        model.addAttribute("mainTab", "gene-neighborhoods");
        model.addAttribute("newickJs", "/js/vendor/newick_modified.js");
        addToModelCommon(model, "/js/result-processing-gene-neighborhoods.js");
        return "main-view  :: addContent(" +
                "fragmentsMain='evolution-fragments', searchArea='gene-neighborhoods')";
    }

    @GetMapping(value={"tree-for-you/{jobId:.+}"})
    public String result(@PathVariable Integer jobId, Model model) {
        model.addAttribute("mainTab", "home");
        addToModelCommon(model, "/js/result-processing.js");
        model.addAttribute("jobId", jobId);
        return "main-view  :: addContent(fragmentsMain='evolution-fragments', result='result')";
    }

    @PostMapping(value="process-request", produces="text/plain")
    @ResponseBody
    public String processTreeRequest(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException, ExecutionException, InterruptedException {
	    ProtoTreeInternal protoTreeInternal = null;
        //Split it to several functions because 'PROTO_TREE' method is asynchronous
        //and files in 'listOfFiles' field of evolutionRequest are got cleared at the end of request processing.
        if (protoTreeRequest.getCommandToBeProcessedBy().equals(BioPrograms.PROTO_TREE.getProgramName())) {
            protoTreeInternal = proteinFeaturesService.storeFilesAndPrepareCommandArguments(protoTreeRequest);
        }

        Integer jobId = protoTreeInternal.getJobId();
        proteinFeaturesService.runMainProgram(protoTreeInternal);

        return String.valueOf(jobId);
    }

    @GetMapping(value="tree-for-you/get-filename", produces="application/json")
    @ResponseBody
    public Map<String, List<String>> getFileNameIfReady(@RequestParam("jobId") String jobId) {
        BioJob bioJob;
        String urlPath = ServletUriComponentsBuilder.fromCurrentContextPath().path("univ_files/").build().toString();

        Map<String, List<String>> result = new HashMap<>();
        result.put("status", noSuchBioJob);

        List<String> listOfResultFileNames;

	     if (jobId != null ) {
	        int id = Integer.valueOf(jobId.split("-")[0]);
            bioJob = proteinFeaturesService.getBioJob(id);
	        if (bioJob != null) {
	            if (bioJob.isFinished()) {
                    listOfResultFileNames = bioJob.getBioJobResultList().stream().map(bjResult -> urlPath + bjResult.getResultFileName()).collect(Collectors.toList());
                    result.put("result", listOfResultFileNames);
                    result.put("status", statusReady);
                } else {
                    result.put("status", statusNotReady);
                }
                result.put("stage", new LinkedList<>(Arrays.asList(bioJob.getStage())));
            }
        }
        return result;
    }

    @GetMapping("univ_files/{filename:.+}")
    public void getFileFromDbP(@PathVariable String filename, HttpServletResponse response) throws IOException {
        BioJobResult bioJobResult = ((BioUniverseService) proteinFeaturesService).getBioJobResultDao().findByResultFileName(filename);
        if (filename.split("\\.")[1].equals("txt")) {
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
    void addToModelCommon(Model model, String sendOrGiveResult) {
        model.addAttribute("specificJs", "/js/get-fields-values.js");
        model.addAttribute("subnavigationTab", BioPrograms.PROTO_TREE.getProgramName());
        model.addAttribute("sendOrGiveResult", sendOrGiveResult);
    }

}
