package com.spnetwork.verifycode.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author aiker
 * @implSpec Nu bug no errors
 * @since 2022/12/11
 */
@Controller
@RequestMapping("/pages/verify-code")
public class IndexController {

    @GetMapping
    public String getIndex() {
        return "verifycode/index.html";
    }

}
