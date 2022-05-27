package com.example.TIFinal2.controllers;

import com.example.TIFinal2.Input;
import com.example.TIFinal2.service.InputBytesStream;
import com.example.TIFinal2.service.Shifr;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

@Controller()
@RequestMapping("/stream")
public class StreamController {

    static ArrayList<String> actionList = new ArrayList<>();
    static {
        actionList.add("Encrypt");
        actionList.add("Decrypt");
    }
    String inputStream = new String();
    String outputStream = new String();
    String key = new String();
    byte[] keyByte = null;
    @GetMapping("")
    public String showElements(Model model) {
        model.addAttribute("actionList",actionList);

        return "streamAlgo";
    }

    @PostMapping("/input_of_file")
    public String loadInfo(@RequestParam MultipartFile file,
                           @RequestParam String registerValue,
                           @RequestParam("actionList") String actionList,
                           Model model) throws IOException {

        System.out.println("T6757ER67DF867");
        if (!file.isEmpty()) {
            byte inp[] = InputBytesStream.getOutString(Input.convert(file));
            Shifr shifr = new Shifr(registerValue, inp);
            byte[] out = null;
            if (actionList.equals("Encrypt")) {
                out = shifr.encrypt(true);
                if (out != null) {
                    keyByte = shifr.getKey();
                } else keyByte = null;
            } else if (actionList.equals("Decrypt")) {
                if (keyByte != null) {
                    shifr.setKey(keyByte);
                    out = shifr.encrypt(false);
                }
                else {
                    model.addAttribute("inputStream", inputStream);
                    model.addAttribute("outputStream", outputStream);
                    model.addAttribute("key", key);
                    model.addAttribute("log", "Ошибка");
                }

            }
            if (out != null) {
                inputStream = String.valueOf(shifr.toBinary(inp));
                outputStream = String.valueOf(shifr.toBinary(out));
                key = String.valueOf(shifr.toBinary(shifr.getKey()));
                model.addAttribute("inputStream", inputStream);
                model.addAttribute("outputStream", outputStream);
                model.addAttribute("key", key);
                model.addAttribute("log", "Успех!");
            } else {
                model.addAttribute("inputStream", "");
                model.addAttribute("outputStream", "");
                model.addAttribute("key", "");
                model.addAttribute("log", "Ошибка");
            }
        } else {
            model.addAttribute("inputStream", "");
            model.addAttribute("outputStream", "");
            model.addAttribute("key", "");
            model.addAttribute("log", "Ошибка");
        }

        return "results";
    }
}
