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
 *  实体类。
 *
 * @author 14020
 * @since 2025-06-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "pet_video_stats")
public class PetVideoStats implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long videoId;

    private Integer commentCount;

    private Integer likeCount;

    private Integer coinCount;

    private Integer collectCount;

    private Integer shareCount;

    private Integer watchingCount;

    private Integer viewCount;

    private Integer danmuCount;

    private LocalDateTime updatedAt;

}
