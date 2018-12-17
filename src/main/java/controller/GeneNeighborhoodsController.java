package controller;

import enums.BioPrograms;
import exceptions.IncorrectRequestException;
import model.internal.ProtoTreeInternal;
import model.request.ProtoTreeRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import service.ProtoTreeService;
import service.StorageService;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/")
public class GeneNeighborhoodsController extends BioUniverseController {
    @Autowired
    public final ProtoTreeService proteinFeaturesService;


    private static final Log logger = LogFactory.getLog(GeneNeighborhoodsController.class);

    private final List<String> statusReady = Arrays.asList("ready");
    private final List<String> statusNotReady = Arrays.asList("notReady");
    private final List<String> noSuchBioJob = Arrays.asList("noSuchBioJob");


    public GeneNeighborhoodsController(StorageService storageService, ProtoTreeService proteinFeaturesService) {
        super(storageService);
        this.proteinFeaturesService = proteinFeaturesService;
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

    @PostMapping(value="process-request-neighbor-genes", produces="text/plain")
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

    @Override
    void addToModelCommon(Model model, String sendOrGiveResult) {
        model.addAttribute("specificJs", "/js/get-fields-values.js");
        model.addAttribute("subnavigationTab", BioPrograms.PROTO_TREE.getProgramName());
        model.addAttribute("sendOrGiveResult", sendOrGiveResult);
    }
}
