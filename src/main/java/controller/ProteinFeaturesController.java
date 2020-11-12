package controller;

import model.request.ProtoTreeRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import service.BioUniverseService;
import service.ProteinFeaturesService;
import service.StorageService;
import exceptions.IncorrectRequestException;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by vadim on 03/30/18.
 */

@Controller
@RequestMapping("/domains")
public class ProteinFeaturesController extends BioUniverseController {

    @Autowired
    public final ProteinFeaturesService proteinFeaturesService;
    private static final Log logger = LogFactory.getLog(ProteinFeaturesController.class);

	public ProteinFeaturesController(StorageService storageService, ProteinFeaturesService proteinFeaturesService) {
    	super(storageService);
    	this.proteinFeaturesService = proteinFeaturesService;
    }

    @GetMapping(value={""})
    public String protoTree(Model model) {
        model.addAttribute("sendOrGiveResult", "/js/send-and-process-data.js");
        model.addAttribute("fieldsProcessing", "/js/fields-processing.js");
        addToModelCommon(model);
        return "main-view  :: addContent(" +
                "fragmentsMain='features-fragments', searchArea='proto-tree', filter='proto-tree-filter'" +
                ", navigation='navigator')";
    }

    @GetMapping(value={"tree/{jobId:.+}"})
    public String result(@PathVariable String jobId, @RequestParam("pipeline") String pipeline, @RequestParam("reduce") String reduce,
                         @RequestParam("features") String features, @RequestParam("eon") String eon, Model model) {
        model.addAttribute("sendOrGiveResult", "/js/result-processing.js");
        model.addAttribute("resultProcessingCommon", "/js/result-processing-common.js");
        model.addAttribute("jobId", jobId);
        addToModelCommon(model);
        return "main-view  :: addContent(fragmentsMain='features-fragments', result='result')";
    }

    @PostMapping(value="process-request", produces="text/plain")
    @ResponseBody
    public String processTreeRequest(ProtoTreeRequest protoTreeRequest) throws IncorrectRequestException, ExecutionException, InterruptedException {
        protoTreeRequest.setCommandToBeProcessedBy("domains");
	    return super.processTreeRequestCommon(protoTreeRequest, proteinFeaturesService);
    }

    @GetMapping(value="tree/get-filename", produces="application/json")
    @ResponseBody
    public Map<String, List<String>> getFileNameIfReady(@RequestParam("jobId") String jobId, @RequestParam("pipeline") String pipeline, @RequestParam("reduce") String reduce,
                                                        @RequestParam("features") String features, @RequestParam("eon") String eon, Model model) {
        return getFileNameIfReadyCommon(jobId, eon, (BioUniverseService) proteinFeaturesService, "domains");
    }

    @GetMapping("univ_files/{filename:.+}")
    public void getFileFromDb(@PathVariable String filename, HttpServletResponse response) throws IOException {
        super.getFileFromDbCommon(filename, response, (BioUniverseService) proteinFeaturesService);
    }

    @Override
    void addToModelCommon(Model model) {
        Boolean maintenanceAnnounce = ((BioUniverseService) proteinFeaturesService).getProperties().getMaintenanceAnnounce();
        String maintenanceDate = ((BioUniverseService) proteinFeaturesService).getProperties().getMaintenanceDate();
        String startDate = ((BioUniverseService) proteinFeaturesService).getProperties().getStartDate();
        String systemMessage = ((BioUniverseService) proteinFeaturesService).getProperties().getSystemMessage();
        String systemMessageEnabled = ((BioUniverseService) proteinFeaturesService).getProperties().getSystemMessageEnabled();
        model.addAttribute("maintenanceAnnounce", maintenanceAnnounce);
        model.addAttribute("maintenanceDate", maintenanceDate);
        model.addAttribute("startDate", startDate);
        model.addAttribute("systemMessage", systemMessage);
        model.addAttribute("systemMessageEnabled", systemMessageEnabled);
        model.addAttribute("mainTab", "domains");
        model.addAttribute("getFieldsValues", "/js/get-fields-values.js");
    }
}
