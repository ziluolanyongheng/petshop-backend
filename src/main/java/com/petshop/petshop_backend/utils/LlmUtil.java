package com.petshop.petshop_backend.utils;


import com.petshop.petshop_backend.websocket.WebSocketServer;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class LlmUtil {
    @Value("${openapi.arkapikey}")
    private  String ARK_API_KEY;
    @Value("${openapi.arkbaseurl}")
    private String ARK_BASE_URL = "https://ark.cn-beijing.volces.com/api/v3";


    @Autowired
    private WebSocketServer webSocketServer;

    public String ask(String question) {
        System.out.println(ARK_API_KEY + " " + ARK_BASE_URL);
        StringBuilder answer = new StringBuilder();
//        String a = "";
        // 创建ArkService实例
        ArkService arkService = ArkService.builder().apiKey(ARK_API_KEY)
                .build();
        // 初始化消息列表
        List<ChatMessage> chatMessages = new ArrayList<>();
        // 创建用户消息
        ChatMessage userMessage = ChatMessage.builder()
                .role(ChatMessageRole.USER) // 设置消息角色为用户
                .content(question + "请以宠物商店店家的角度回答。请尽量输出50字以内，精简回答") // 设置消息内容
                .build();
        // 将用户消息添加到消息列表
        chatMessages.add(userMessage);
        // 创建聊天完成请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("deepseek-v3-250324")// 需要替换为模型的Model ID
                .messages(chatMessages) // 设置消息列表
                .build();
        // 发送聊天完成请求并打印响应
        try {
            // 获取响应并打印每个选择的消息内容
            arkService.streamChatCompletion(chatCompletionRequest)
                    .doOnError(Throwable::printStackTrace)
                    .blockingForEach(choice -> {
                        if (choice.getChoices().size() > 0) {
                            ChatMessage message = choice.getChoices().get(0).getMessage();
                            // 判断是否触发深度思考，触发则打印模型输出的思维链内容
                            if (message.getReasoningContent() != null && !message.getReasoningContent().isEmpty()) {
                                System.out.print(message.getReasoningContent());
                            }
                            // 打印模型输出的回答内容
//                            System.out.print(message.getContent());
                            answer.append(message.getContent());
                        }
                    });
        } catch (Exception e) {
            System.out.println("请求失败: " + e.getMessage());
        } finally {
            // 关闭服务执行器
            arkService.shutdownExecutor();
        }


        return answer.toString();
    }


    public void askWithStream(String question,String pushToken) {
        System.out.println(ARK_API_KEY + " " + ARK_BASE_URL);
//        StringBuilder answer = new StringBuilder();
//        String a = "";
        // 创建ArkService实例
        ArkService arkService = ArkService.builder().apiKey(ARK_API_KEY)
                .build();
        // 初始化消息列表
        List<ChatMessage> chatMessages = new ArrayList<>();
        // 创建用户消息
        ChatMessage userMessage = ChatMessage.builder()
                .role(ChatMessageRole.USER) // 设置消息角色为用户
                .content(question + "请以宠物商店店家的角度回答。请尽量输出50字以内，精简回答") // 设置消息内容
                .build();
        // 将用户消息添加到消息列表
        chatMessages.add(userMessage);
        // 创建聊天完成请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("deepseek-v3-250324")// 需要替换为模型的Model ID
                .messages(chatMessages) // 设置消息列表
                .build();
        // 发送聊天完成请求并打印响应
        //开始流式
        webSocketServer.sendToOneClient("Start",pushToken);
        try {
            // 获取响应并打印每个选择的消息内容
            arkService.streamChatCompletion(chatCompletionRequest)
                    .doOnError(Throwable::printStackTrace)
                    .blockingForEach(choice -> {
                        if (choice.getChoices().size() > 0) {
                            ChatMessage message = choice.getChoices().get(0).getMessage();
                            // 判断是否触发深度思考，触发则打印模型输出的思维链内容
                            if (message.getReasoningContent() != null && !message.getReasoningContent().isEmpty()) {
                                System.out.print(message.getReasoningContent());
                            }
                            // 打印模型输出的回答内容
//                            System.out.print(message.getContent());
//                            answer.append(message.getContent());
                            webSocketServer.sendToOneClient(message.getContent(),pushToken);
                        }
                    });
        } catch (Exception e) {
            System.out.println("请求失败: " + e.getMessage());
        } finally {
            webSocketServer.sendToOneClient("Stop",pushToken);
            // 关闭服务执行器
            arkService.shutdownExecutor();
        }


//        return answer.toString();
    }
}
