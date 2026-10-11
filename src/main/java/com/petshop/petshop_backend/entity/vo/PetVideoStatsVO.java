package com.petshop.petshop_backend.entity.vo;

import com.petshop.petshop_backend.entity.PetVideoStats;
import lombok.Data;

@Data
public class PetVideoStatsVO extends PetVideoStats {

    private Long productId;

    private Long merchantId;

    private String title;

    private String desc;

    private String videoUrl;

    private String coverUrl;
}
