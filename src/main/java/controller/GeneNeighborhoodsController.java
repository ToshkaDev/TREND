package controller;

import exceptions.IncorrectRequestException;
import model.request.ProtoTreeRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.BioUniverseService;
import service.GeneNeighborhoodsService;
import service.StorageService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/gene-neighborhoods")
public class GeneNeighborhoodsController extends BioUniverseController {
    @Autowired
    public final GeneNeighborhoodsService geneNeighborhoodsService;
    private static final Log logger = LogFactory.getLog(GeneNeighborhoodsController.class);

    public GeneNeighborhoodsController(StorageService storageService, GeneNeighborhoodsService geneNeighborhoodsService) {
        super(storageService);
        this.geneNeighborhoodsService = geneNeighborhoodsService;
    }

    @GetMapping(value="")
    public String geneNeighborhoods(Model model) {
        model.addAttribute("sendOrGiveResult", "/js/send-and-process-data.js");
        model.addAttribute("getFieldsValues", "/js/get-fields-values.js");
        model.addAttribute("fieldsProcessing", "/js/fields-processing.js");
        addToModelCommon(model);
        return "main-view  :: addContent(" +
                "fragmentsMain='neighborhoods-fragments', searchArea='gene-neighborhoods', filter='proto-tree-filter')";
    }

    @GetMapping(value={"tree/{jobId:.+}"})
    public String geneNeighborhoodsResult(@PathVariable String jobId, Model model) {
        model.addAttribute("sendOrGiveResult","/js/result-processing-gn.js");
        model.addAttribute("specificJs", "/js/result-processing-gn-server.js");
        model.addAttribute("resultProcessingCommon", "/js/result-processing-common.js");
        model.addAttribute("jobId", jobId);
        addToModelCommon(model);
        return "main-view  :: addContent(" +
                "fragmentsMain='neighborhoods-fragments', result='result')";
    }

    @PostMapping(value="process-request", produces="text/plain")
    @ResponseBody
    public String processTreeRequest(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException, ExecutionException, InterruptedException {
        protoTreeRequest.setCommandToBeProcessedBy("gene-neighborhoods");
        return super.processTreeRequestCommon(protoTreeRequest, geneNeighborhoodsService);
    }

    @GetMapping(value="tree/get-filename", produces="application/json")
    @ResponseBody
    public Map<String, List<String>> getFileNameIfReady(@RequestParam("jobId") String jobId, @RequestParam("pipeline") String pipeline, @RequestParam("reduce") String reduce,
                                                        @RequestParam("features") String features, @RequestParam("eon") String eon, Model model)  {
        return getFileNameIfReadyCommon(jobId, eon, (BioUniverseService) geneNeighborhoodsService, "gene-neighborhoods");
    }

    @GetMapping("univ_files/{filename:.+}")
    public void getFileFromDb(@PathVariable String filename, HttpServletResponse response) throws IOException {
        super.getFileFromDbCommon(filename, response, (BioUniverseService) geneNeighborhoodsService);
    }

    @Override
    void addToModelCommon(Model model) {
        Boolean maintenanceAnnounce = ((BioUniverseService) geneNeighborhoodsService).getProperties().getMaintenanceAnnounce();
        String maintenanceDate = ((BioUniverseService) geneNeighborhoodsService).getProperties().getMaintenanceDate();
        String startDate = ((BioUniverseService) geneNeighborhoodsService).getProperties().getStartDate();
        String systemMessage = ((BioUniverseService) geneNeighborhoodsService).getProperties().getSystemMessage();
        String systemMessageEnabled = ((BioUniverseService) geneNeighborhoodsService).getProperties().getSystemMessageEnabled();
        model.addAttribute("maintenanceAnnounce", maintenanceAnnounce);
        model.addAttribute("maintenanceDate", maintenanceDate);
        model.addAttribute("startDate", startDate);
        model.addAttribute("systemMessage", systemMessage);
        model.addAttribute("systemMessageEnabled", systemMessageEnabled);
        model.addAttribute("mainTab", "gene-neighborhoods");
    }
}
