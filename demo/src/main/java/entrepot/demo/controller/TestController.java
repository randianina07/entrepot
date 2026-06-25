package entrepot.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test")
    public String tester(Model model) {
        model.addAttribute("monMessage", "Si tu vois ce message, ton architecture est parfaite !");
        return "test";
    }
}