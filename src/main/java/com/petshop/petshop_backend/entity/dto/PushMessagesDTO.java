package com.petshop.petshop_backend.entity.dto;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PushMessagesDTO {

//    /**
//     * 消息ID，主键
//     */
//    @Id(keyType = KeyType.Auto)
//    private Long id;

    /**
     * 推送消息类型
     */
    private String messageType;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 接收用户ID、-1 ： 向全体用户推送
     */
    private Long userId;

//    /**
//     * 阅读状态（0未读，1已读）
//     */
//    private Boolean isRead;
//
//    /**
//     * 创建时间
//     */
//    private LocalDateTime createdAt;
//
//    /**
//     * 更新时间
//     */
//    private LocalDateTime updatedAt;
//
}
