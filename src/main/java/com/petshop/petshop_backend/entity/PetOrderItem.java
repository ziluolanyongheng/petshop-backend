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
 * 订单商品表 实体类。
 *
 * @author 14020
 * @since 2025-06-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "pet_order_item")
public class PetOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单项ID（主键）
     */
    @Id(keyType = KeyType.Auto)
    private Long itemId;

    /**
     * 订单ID（外键）
     */
    private Long orderId;

    /**
     * 商店ID（外键）
     */
    private Long storeId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品规格
     */
    private String productSpec;

    /**
     * 商品快照(JSON)
     */
    private String productDesc;

    /**
     * 购买数量
     */
    private Integer itemQuantity;

    /**
     * 购买时单价
     */
    private BigDecimal itemPrice;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

}
