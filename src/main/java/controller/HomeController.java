package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import service.BioUniverseService;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    public final BioUniverseService bioUniverseService;

    public HomeController(BioUniverseService bioUniverseService) {
        this.bioUniverseService = bioUniverseService;
    }

    @GetMapping(value={"", "/home"})
    public String home(Model model) {
        model.addAttribute("mainTab", "home");
        addToModelCommon(model);
        return "main-view  :: addContent(" +
                "fragmentsMain='help-and-about', infoFragment='about')";
    }

    @GetMapping(value={"/help"})
    public String help(Model model) {
        model.addAttribute("mainTab", "help");
        addToModelCommon(model);
        return "main-view  :: addContent(" +
                "fragmentsMain='help-and-about', infoFragment='help')";
    }

    @GetMapping(value={"/protips"})
    public String proTips(Model model) {
        model.addAttribute("mainTab", "protips");
        addToModelCommon(model);
        return "main-view  :: addContent(" +
                "fragmentsMain='help-and-about', infoFragment='protips')";
    }

    @GetMapping(value={"/news"})
    public String news (Model model) {
        model.addAttribute("mainTab", "news");
        addToModelCommon(model);
        return "main-view  :: addContent(" +
                "fragmentsMain='help-and-about', infoFragment='news')";
    }

    @GetMapping(value={"/contact"})
    public String contact(Model model) {
        model.addAttribute("mainTab", "contact");
        addToModelCommon(model);
        return "main-view  :: addContent(" +
                "fragmentsMain='help-and-about', infoFragment='contact')";
    }

    void addToModelCommon(Model model) {
        Boolean maintenanceAnnounce = bioUniverseService.getProperties().getMaintenanceAnnounce();
        String maintenanceDate = bioUniverseService.getProperties().getMaintenanceDate();
        String startDate = bioUniverseService.getProperties().getStartDate();
        String systemMessage = bioUniverseService.getProperties().getSystemMessage();
        String systemMessageEnabled = bioUniverseService.getProperties().getSystemMessageEnabled();
        model.addAttribute("maintenanceAnnounce", maintenanceAnnounce);
        model.addAttribute("maintenanceDate", maintenanceDate);
        model.addAttribute("startDate", startDate);
        model.addAttribute("systemMessage", systemMessage);
        model.addAttribute("systemMessageEnabled", systemMessageEnabled);
    }
}
