package controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping(value={"", "/home"})
    public String home(Model model) {
        model.addAttribute("mainTab", "home");
        return "main-view  :: addContent(" +
                "fragmentsMain='help-and-about', infoFragment='about')";
    }

    @GetMapping(value={"/help"})
    public String help(Model model) {
        model.addAttribute("mainTab", "help");
        return "main-view  :: addContent(" +
                "fragmentsMain='help-and-about', infoFragment='help')";
    }

    @GetMapping(value={"/protips"})
    public String proTips(Model model) {
        model.addAttribute("mainTab", "protips");
        return "main-view  :: addContent(" +
                "fragmentsMain='help-and-about', infoFragment='protips')";
    }

    @GetMapping(value={"/contact"})
    public String contact(Model model) {
        model.addAttribute("mainTab", "contact");
        return "main-view  :: addContent(" +
                "fragmentsMain='help-and-about', infoFragment='contact')";
    }
}
