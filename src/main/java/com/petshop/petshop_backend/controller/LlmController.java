package com.petshop.petshop_backend.controller;

import com.petshop.petshop_backend.entity.base.Result;
import com.petshop.petshop_backend.utils.BaiduLlmUtil;
import com.petshop.petshop_backend.utils.LlmUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/llm")
@Tag(name = "6 大模型调用聊天接口")
@Slf4j
public class LlmController {

    @Autowired
    LlmUtil llmUtil;

    @Autowired
    BaiduLlmUtil baiduLlmUtil;

    @GetMapping("/ask")
    @Operation(summary = "询问大模型接口")
    public Result askLlm(@RequestParam String question) {
        String answer = llmUtil.ask(question);
        System.out.println(answer);
        return Result.success(answer);
    }
    @GetMapping("/askByStream")
    @Operation(summary = "询问大模型接口(流式)")
    public Result askLlmaskByStream(@RequestParam String question, @RequestParam String pushToken) {
        llmUtil.askWithStream(question,pushToken);
        return Result.success("ok");
    }

    @GetMapping("/askBaidu")
    @Operation(summary = "询问百度大模型接口")
    public Result askBaidu(@RequestParam String question) {
        String answer = baiduLlmUtil.ask(question);
        System.out.println(answer);
        return Result.success(answer);
    }
}
