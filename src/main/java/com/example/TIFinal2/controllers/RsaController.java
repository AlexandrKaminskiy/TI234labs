package com.example.TIFinal2.controllers;

import com.example.TIFinal2.Input;
import com.example.TIFinal2.service.InputBytesStream;
import com.example.TIFinal2.service.RsaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/rsa")
public class RsaController {

    @GetMapping()
    public String startpage() {
        return "rsa_start_page";
    }

    @PostMapping()
    public String calculate(@RequestParam MultipartFile file,
                            @RequestParam int p,
                            @RequestParam int q,
                            @RequestParam int openExp) throws IOException {

        if (!file.isEmpty()) {
            byte inp[] = InputBytesStream.getOutString(Input.convert(file));
            RsaService rsaService = new RsaService(inp,p,q,openExp);
            System.out.println();
        }
        return "result_rsa_page";
    }
}
