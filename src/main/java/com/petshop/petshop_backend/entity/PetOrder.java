package com.petshop.petshop_backend.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单表 实体类。
 *
 * @author 14020
 * @since 2025-06-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "pet_order")
public class PetOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID（主键）
     */
    @Id(keyType = KeyType.Auto)
    private Long orderId;

    /**
     * 订单号（唯一）
     */
    private String orderNo;



    /**
     * 用户ID（外键）
     */
    private Long userId;

    /**
     * 地址ID（外键）
     */
    private Long addressId;

    /**
     * 地址快照(JSON)
     */
    private String addressDesc;

    /**
     * 订单总额
     */
    private BigDecimal orderTotal;

    /**
     * 订单状态：0=待支付,1=已支付,2=已发货,3=已完成,4=已取消
     */
    private Integer orderStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

}
