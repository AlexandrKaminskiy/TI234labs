package com.example.TIFinal2.controllers;

import com.example.TIFinal2.Input;
import com.example.TIFinal2.service.GamalCipher;
import com.example.TIFinal2.service.InputBytesStream;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;


@Controller()
public class GamalController {

    static ArrayList<String> actionList = new ArrayList<>();
    static {
        actionList.add("Encrypt");
        actionList.add("Decrypt");
    }
    GamalCipher cipher;

    @GetMapping
    public String getPage() {

        return "startpage";
    }

    @PostMapping("inputKeys")
    public String inputKeys(@RequestParam String pValue,
                            @RequestParam String xValue,
                            @RequestParam String kValue,
                            Model model) {
        try {
            cipher = new GamalCipher(pValue,xValue,kValue);
        } catch (Exception e) {
            return "errorpage";
        }
        var roots = cipher.primitiveRoot();
        model.addAttribute("rootsList",roots);
        model.addAttribute("actionList",actionList);
        return "index";
    }


    @PostMapping("input_of_file")
    public String loadInfo(@RequestParam MultipartFile file,
                           @RequestParam String actionList,
                           @RequestParam String rootsList,
                           Model model) throws IOException {

        if (!file.isEmpty()) {
            byte inp[] = InputBytesStream.getOutString(Input.convert(file));

            switch (actionList) {
                case "Encrypt" -> {
                    cipher.setPlainFile(inp);
                    ArrayList<int[]> cip = cipher.getCipher(Integer.parseInt(rootsList));
                    cipher.writeToFile(cip);
                    model.addAttribute("inputStream",Input.plainToString(inp));
                    model.addAttribute("outputStream",Input.cipheredToString(cip));
                }
                case "Decrypt" -> {
                    ArrayList<int[]> decip = cipher.readFromCipheredFile(inp);
                    byte[] plain = cipher.getDecipher(Integer.parseInt(rootsList));
                    cipher.toPlainFile(plain);
                    model.addAttribute("inputStream",Input.cipheredToString(decip));
                    model.addAttribute("outputStream",Input.plainToString(plain));
                }
            }
            model.addAttribute("k",cipher.getK());
            model.addAttribute("x",cipher.getX());
            model.addAttribute("p",cipher.getP());
            model.addAttribute("y",cipher.getY());
            model.addAttribute("g",rootsList);
        }

        //gamalCipher.cipher();

        return "results";
    }
}
