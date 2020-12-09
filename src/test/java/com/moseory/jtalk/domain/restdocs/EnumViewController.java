package com.moseory.jtalk.domain.restdocs;

import com.moseory.jtalk.global.standard.ResultResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnumViewController {

    @GetMapping("/docs")
    public ResultResponse<String> docs() {
        return ResultResponse.<String>builder()
                .status(200)
                .message("message")
                .data("dd")
                .build();
    }
}
