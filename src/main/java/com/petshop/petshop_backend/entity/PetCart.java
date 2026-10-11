package com.petshop.petshop_backend.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author Xinzhe
 * @since 2025-06-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "pet_cart")
public class PetCart implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long cartId;

    private Long userId;

    private Long productId;

    private Integer cartQuantity;


    private String selectedSpec;

    private Boolean isSelected;

    private LocalDateTime createdTime;

}
