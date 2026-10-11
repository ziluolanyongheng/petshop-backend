package com.petshop.petshop_backend.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统推送消息表 实体类。
 *
 * @author Xinzhe
 * @since 2025-06-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "push_messages")
public class PushMessages implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID，主键
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

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

    /**
     * 阅读状态（0未读，1已读）
     */
    private Boolean isRead;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

}
