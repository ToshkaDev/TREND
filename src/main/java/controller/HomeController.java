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
                "fragmentsMain='help-and-about', help='about')";
    }

    @GetMapping(value={"/help"})
    public String help(Model model) {
        model.addAttribute("mainTab", "help");
        return "main-view  :: addContent(" +
                "fragmentsMain='help-and-about', help='help')";
    }

    @GetMapping(value={"/credits"})
    public String credit(Model model) {
        model.addAttribute("mainTab", "credits");
        return "main-view  :: addContent(" +
                "fragmentsMain='help-and-about', help='credits')";
    }

    @GetMapping(value={"/contact"})
    public String contact(Model model) {
        model.addAttribute("mainTab", "contact");
        return "main-view  :: addContent(" +
                "fragmentsMain='help-and-about', help='contact')";
    }
}
