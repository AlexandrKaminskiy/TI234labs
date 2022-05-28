package com.example.TIFinal2.controllers;

import com.example.TIFinal2.Input;
import com.example.TIFinal2.service.InputBytesStream;
import com.example.TIFinal2.service.RsaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Controller
@RequestMapping("/rsa")
public class RsaController {
    private RsaService rsaService;
    @GetMapping()
    public String startpage() {
        return "rsa_start_page";
    }

    @PostMapping()
    public String calculate(@RequestParam(defaultValue = "") MultipartFile file,
                            @RequestParam long p,
                            @RequestParam long q,
                            @RequestParam long closedExp,
                            Model model) throws IOException {
        byte inp[] = new byte[0];
        if (!file.isEmpty()) {
            inp = InputBytesStream.getOutString(Input.convert(file));
        }

        try {
            if (Objects.equals(file.getOriginalFilename(), "")) {
                throw new Exception();
            }
            rsaService = new RsaService(inp,p,q,closedExp);
            model.addAttribute("openKey", "(" + rsaService.getOpenExp() + ", " + rsaService.getR() + ")");
            model.addAttribute("closedKey", "(" + rsaService.getClosedExp() + ", " + rsaService.getR() + ")");
            model.addAttribute("P", rsaService.getP());
            model.addAttribute("Q", rsaService.getQ());
            model.addAttribute("hash", rsaService.getHash());
            model.addAttribute("sign", rsaService.getS());

        } catch (Exception e) {
            return "errorpage";
        }
        System.out.println();

        return "result_rsa_page";
    }

    @PostMapping("/validatefile")
    public String validatefile(@RequestParam MultipartFile file,
                                Model model) throws IOException {

        boolean isValid = false;
        if (!file.isEmpty()) {
            isValid = rsaService.validateFile(Input.convert(file));
        }
        model.addAttribute("hash", rsaService.getHash());
        model.addAttribute("sign", rsaService.getS());
        model.addAttribute("hash2", rsaService.getHash2());
        String valid = isValid ? "валиден" : "не вадиден";
        model.addAttribute("valid",valid);
        return "validation_results";
    }

}
