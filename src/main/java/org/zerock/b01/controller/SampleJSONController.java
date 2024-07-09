package org.zerock.b01.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Log4j2
public class SampleJSONController {
    @GetMapping("/api/helloArr")
    public String[] helloArr() {
        log.info("helloArr..............");

        return new String[]{"AAA", "BBB", "CCC"};
    }
}
