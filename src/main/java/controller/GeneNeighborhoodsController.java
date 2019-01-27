package controller;

import biojobs.BioJob;
import biojobs.BioJobResult;
import enums.BioPrograms;
import exceptions.IncorrectRequestException;
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
import service.GeneNeighborhoodsService;
import service.StorageService;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/gene-neighborhoods")
public class GeneNeighborhoodsController extends BioUniverseController {
    @Autowired
    public final GeneNeighborhoodsService geneNeighborhoodsService;


    private static final Log logger = LogFactory.getLog(GeneNeighborhoodsController.class);

    private final List<String> statusReady = Arrays.asList("ready");
    private final List<String> statusNotReady = Arrays.asList("notReady");
    private final List<String> noSuchBioJob = Arrays.asList("noSuchBioJob");


    public GeneNeighborhoodsController(StorageService storageService, GeneNeighborhoodsService geneNeighborhoodsService) {
        super(storageService);
        this.geneNeighborhoodsService = geneNeighborhoodsService;
    }

    @GetMapping(value="")
    public String geneNeighborhoods(Model model) {
        model.addAttribute("sendOrGiveResult", "/js/send-and-process-data.js");
        model.addAttribute("specificJs","/js/result-processing-gn.js");
        addToModelCommon(model);
        return "main-view  :: addContent(" +
                "fragmentsMain='neighborhoods-fragments', searchArea='gene-neighborhoods', filter='proto-tree-filter')";
    }

    @GetMapping(value={"tree/{jobId:.+}"})
    public String geneNeighborhoodsResult(@PathVariable Integer jobId, Model model) {
        model.addAttribute("sendOrGiveResult","/js/result-processing-gn.js");
        model.addAttribute("specificJs", "/js/result-processing-gn-server.js");
        model.addAttribute("jobId", jobId);
        addToModelCommon(model);
        return "main-view  :: addContent(" +
                "fragmentsMain='neighborhoods-fragments', result='result')";
    }

    @PostMapping(value="process-request", produces="text/plain")
    @ResponseBody
    public String processTreeRequest(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException, ExecutionException, InterruptedException {
        //Split it to several functions because 'PROTO_TREE' method is asynchronous
        //and files in 'listOfFiles' field of evolutionRequest are got cleared at the end of request processing.
        ProtoTreeInternal protoTreeInternal = geneNeighborhoodsService.storeFilesAndPrepareCommandArguments(protoTreeRequest);
        Integer jobId = protoTreeInternal.getJobId();
        geneNeighborhoodsService.runMainProgram(protoTreeInternal);
        return String.valueOf(jobId);
    }

    @GetMapping(value="tree/get-filename", produces="application/json")
    @ResponseBody
    public Map<String, List<String>> getFileNameIfReady(@RequestParam("jobId") String jobId) {
        BioJob bioJob;
        String urlPath = ServletUriComponentsBuilder.fromCurrentContextPath().path("gene-neighborhoods/univ_files/").build().toString();

        Map<String, List<String>> result = new HashMap<>();
        result.put("status", noSuchBioJob);

        List<String> listOfResultFileNames;

        if (jobId != null ) {
            int id = Integer.valueOf(jobId.split("-")[0]);
            bioJob = geneNeighborhoodsService.getBioJob(id);
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
        BioJobResult bioJobResult = ((BioUniverseService) geneNeighborhoodsService).getBioJobResultDao().findByResultFileName(filename);
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
        model.addAttribute("mainTab", "gene-neighborhoods");
        model.addAttribute("newickJs", "/js/vendor/newick_modified.js");
        model.addAttribute("getFieldsValues", "/js/get-fields-values.js");
        model.addAttribute("subnavigationTab", BioPrograms.PROTO_TREE.getProgramName());
    }
}
