package ru.netology.diplom.reg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    private final UsServ usServ;

    @Autowired
    public RegistrationController(UsServ usServ) {
        this.usServ = usServ;
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "registration";
    }

    @PostMapping("/register")
    public String register(@RequestParam String email, @RequestParam String password) {
        usServ.registerUser (email, password);
        return "redirect:/success";
    }
    @GetMapping("/success")
    public String success() {
        return "success";
    }
}
