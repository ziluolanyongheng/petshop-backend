package com.petshop.petshop_backend.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.petshop.petshop_backend.entity.*;
import com.petshop.petshop_backend.entity.base.Result;
import com.petshop.petshop_backend.entity.dto.PetCartDTO;
import com.petshop.petshop_backend.entity.vo.PetOrderItemVO;
import com.petshop.petshop_backend.entity.vo.PetOrderVO;
import com.petshop.petshop_backend.service.*;
import com.petshop.petshop_backend.utils.BaseContext;
import com.petshop.petshop_backend.utils.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.checkerframework.checker.units.qual.A;
import org.simpleframework.xml.Order;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.petshop.petshop_backend.entity.table.PetOrderTableDef.PET_ORDER;
import static com.petshop.petshop_backend.entity.table.PetProductTableDef.PET_PRODUCT;

/**
 * 订单表 控制层。
 *
 * @author 14020
 * @since 2025-06-24
 */
@RestController
@Tag(name = "7 订单管理接口")
@RequestMapping("/Order")
public class OrderController {

    @Autowired
    private PetOrderService petOrderService;

    @Autowired
    private PetCartService petCartService;

    @Autowired
    private PetProductService petProductService;

    @Autowired
    private PetUserAddressService petUserAddressService;
    @Autowired
    private PetOrderItemService petOrderItemService;

    @Autowired
    private WsPushMessagesService wsPushMessagesService;

    @Autowired
    private PetStoreService petStoreService;

    @Autowired
    private PetImageRelService petImageRelService;

    @Autowired
    private PetImageService petImageService;

    @Autowired
    private  UserController userController;
    /**
     * 添加订单表。
     *
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @Operation(summary = "根据购物车生成订单接口") 
    @ApiOperationSupport(order = 10)
    @PostMapping("saveCartOrder")
    @Transactional
    Result saveCartOrder() {
        Integer currentId = BaseContext.getCurrentId();
        //创建订单
        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq(PetUserAddress::getUserId, currentId)
                .eq(PetUserAddress::getIsDefault,1);
        PetUserAddress petUserAddress = petUserAddressService.getOne(queryWrapper1);
        PetOrder petOrder = new PetOrder();
        String jsonUtil = null;
        try {
            jsonUtil= JsonUtil.toJson(petUserAddress);
        } catch (Exception ignored) {

        }
        petOrder.setAddressDesc(jsonUtil);
        petOrder.setAddressId(petUserAddress.getAddressId());
        petOrder.setOrderNo(System.currentTimeMillis()+"_"+currentId);
        petOrder.setOrderStatus(0);
        petOrder.setUserId(Long.valueOf(currentId));
        petOrder.setCreatedTime(LocalDateTime.now());
        //
        QueryWrapper queryWrapper2 = new QueryWrapper();
        queryWrapper2.eq(PetCart::getUserId, currentId)
                    .eq(PetCart::getIsSelected,1);
        List<PetCart> carts = petCartService.list(queryWrapper2);
        double totalPrice = 0.0;
        for (PetCart item : carts) {
            PetProduct petProduct = petProductService.getById(item.getProductId());
            totalPrice+= item.getCartQuantity() * petProduct.getProductPrice().doubleValue();
        }
        petCartService.remove(queryWrapper2);
        petOrder.setOrderTotal(BigDecimal.valueOf(totalPrice));
        petOrder.setOrderStatus(0);
        petOrderService.save(petOrder);
        QueryWrapper queryWrapper3 = new QueryWrapper();
        queryWrapper3.eq(PetOrder::getOrderNo, String.valueOf(petOrder.getOrderNo()));
        PetOrder petOrder1= petOrderService.getOne(queryWrapper3);
        carts.forEach( item ->{

            //查询商品表得到商店Id
            PetProduct product = petProductService.getOne(new QueryWrapper().eq(PetProduct::getProductId, item.getProductId()));

            PetOrderItem petOrderItem = new PetOrderItem();
            petOrderItem.setStoreId(product.getStoreId());
            petOrderItem.setProductId(item.getProductId());
            petOrderItem.setOrderId(petOrder1.getOrderId());
            PetProduct petProduct = petProductService.getById(item.getProductId());
            petOrderItem.setProductDesc(petProduct.getProductDesc());
            petOrderItem.setProductSpec(item.getSelectedSpec());
            petOrderItem.setCreatedTime(LocalDateTime.now());
            petOrderItem.setItemPrice(petProduct.getProductPrice());
            petOrderItem.setItemQuantity(item.getCartQuantity());
            petOrderItemService.save(petOrderItem);
        });
        return Result.success(petOrder1) ;
    }


    @Operation(summary = "直接购买商品接口")
    @ApiOperationSupport(order = 11)
    @PostMapping("saveOrder")
    @Transactional
    Result saveOrder(@RequestBody PetCartDTO petCartDTO) {
        Integer currentId = BaseContext.getCurrentId();
        //创建订单
        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq(PetUserAddress::getUserId, currentId)
                .eq(PetUserAddress::getIsDefault,1);
        PetUserAddress petUserAddress = petUserAddressService.getOne(queryWrapper1);
        PetOrder petOrder = new PetOrder();
        String jsonUtil = null;
        try {
            jsonUtil= JsonUtil.toJson(petUserAddress);
        } catch (Exception ignored) {
        }
        petOrder.setAddressDesc(jsonUtil);
        petOrder.setAddressId(petUserAddress.getAddressId());
        petOrder.setOrderNo(System.currentTimeMillis()+"_"+currentId);
        petOrder.setOrderStatus(0);
        petOrder.setUserId(Long.valueOf(currentId));
        petOrder.setCreatedTime(LocalDateTime.now());
        //
        QueryWrapper queryWrapper2 = new QueryWrapper();
        queryWrapper2.eq(PetCart::getUserId, currentId)
                .eq(PetCart::getIsSelected,1);
        PetProduct petProduct = petProductService.getById(petCartDTO.getProductId());
        double  totalPrice= petCartDTO.getCartQuantity()*petProduct.getProductPrice().doubleValue();
        petOrder.setOrderTotal(BigDecimal.valueOf(totalPrice));
        petOrder.setOrderStatus(0);
        petOrderService.save(petOrder);
        QueryWrapper queryWrapper3 = new QueryWrapper();
        queryWrapper3.eq(PetOrder::getOrderNo, String.valueOf(petOrder.getOrderNo()));
        PetOrder petOrder1= petOrderService.getOne(queryWrapper3);
        PetOrderItem petOrderItem = new PetOrderItem();
        petOrderItem.setStoreId(petProduct.getStoreId());
        petOrderItem.setProductId(petCartDTO.getProductId());
        petOrderItem.setOrderId(petOrder1.getOrderId());
        petOrderItem.setProductDesc(petProduct.getProductDesc());
        petOrderItem.setProductSpec(petCartDTO.getSelectedSpec());
        petOrderItem.setCreatedTime(LocalDateTime.now());
        petOrderItem.setItemPrice(petProduct.getProductPrice());
        petOrderItem.setItemQuantity(petCartDTO.getCartQuantity());
        petOrderItemService.save(petOrderItem);
        return Result.success(petOrder1) ;
    }

    @Operation(summary = "选择购物车商品接口")
    @ApiOperationSupport(order = 12)
    @PostMapping("select")
    @Transactional
    Result setIsSelect(@RequestParam Integer cartId) {
        PetCart petCart = petCartService.getById(cartId);
        petCart.setIsSelected(!petCart.getIsSelected());
        petCartService.updateById(petCart);
        return Result.success("success") ;
    }

    @Operation(summary = "订单查询接口")
    @ApiOperationSupport(order = 13)
    @GetMapping("status")
    @Transactional
    Result getOrderStatus(@RequestParam String orderNo) {

        QueryWrapper queryWrapper = new QueryWrapper().eq(PetOrder::getOrderNo, orderNo);
        PetOrder petOrder = petOrderService.getOne(queryWrapper);

        return Result.success(petOrder) ;
    }
    @Operation(summary = "订单、订单明细查询接口")
    @ApiOperationSupport(order = 13)
    @GetMapping("detail")
    @Transactional
    Result getOrderDetail(@RequestParam String orderNo) {

        //查询得到订单
        QueryWrapper queryWrapper = new QueryWrapper().eq(PetOrder::getOrderNo, orderNo);
        PetOrder petOrder = petOrderService.getOne(queryWrapper);

        PetOrderVO petOrderVO = new PetOrderVO();
        BeanUtils.copyProperties(petOrder, petOrderVO);

        //查询到订单明细
        List<PetOrderItem> orderItems = petOrderItemService.list(new QueryWrapper().eq(PetOrderItem::getOrderId, petOrder.getOrderId()));
        ArrayList<PetOrderItemVO> petOrderItemVOS = new ArrayList<>();
        for (PetOrderItem petOrderItem : orderItems) {
            PetOrderItemVO orderItemDetail = petOrderService.getOrderItemDetail(petOrderItem);
            petOrderItemVOS.add(orderItemDetail);
        }

        petOrderVO.setPetOrderItems(petOrderItemVOS);

        return Result.success(petOrderVO) ;
    }

    @Operation(summary = "查询商店下所有订单明细接口")
    @ApiOperationSupport(order = 14)
    @GetMapping("/all")
    @Transactional
    Result getOrderByStoreId(@RequestParam String storeId) {

        QueryWrapper queryWrapper = new QueryWrapper().eq(PetOrderItem::getStoreId, storeId);
        List<PetOrderItem> orderItems = petOrderItemService.list(queryWrapper);

        ArrayList<PetOrderVO> petOrderVOS = new ArrayList<>();
        for (PetOrderItem petOrderItem : orderItems) {
            PetOrderVO petOrderVO = new PetOrderVO();

            PetOrder petOrder = petOrderService.getById(petOrderItem.getOrderId());
            BeanUtils.copyProperties(petOrder, petOrderVO);

            PetOrderItemVO orderItemDetail = petOrderService.getOrderItemDetail(petOrderItem);

            petOrderVO.getPetOrderItems().add(orderItemDetail);
            petOrderVOS.add(petOrderVO);
        }


        return Result.success(petOrderVOS) ;
    }
    @Operation(summary = "查询用户的所有订单接口")
    @ApiOperationSupport(order = 15)
    @GetMapping("/user/all")
    @Transactional
    Result getOrderByUserId() {

        Integer currentId = BaseContext.getCurrentId();

        QueryWrapper queryWrapper = new QueryWrapper().eq(PetOrder::getUserId, currentId);
        List<PetOrder> petOrders = petOrderService.list(queryWrapper);

        List<PetOrderVO> petOrderVOS = new ArrayList<>();

        for (PetOrder petOrder : petOrders) {
            PetOrderVO petOrderVO = new PetOrderVO();
            BeanUtils.copyProperties(petOrder, petOrderVO);

            List<PetOrderItem> orderItems = petOrderItemService.list(new QueryWrapper().eq(PetOrderItem::getOrderId, petOrder.getOrderId()));
            ArrayList<PetOrderItemVO> petOrderItemVOS = new ArrayList<>();
            for (PetOrderItem orderItem : orderItems) {
                PetOrderItemVO orderItemDetail = petOrderService.getOrderItemDetail(orderItem);

                petOrderItemVOS.add(orderItemDetail);
            }

            petOrderVO.setPetOrderItems(petOrderItemVOS);
            petOrderVOS.add(petOrderVO);
        }


        return Result.success(petOrderVOS) ;
    }

    @Operation(summary = "管理员查询所有订单接口")
    @ApiOperationSupport(order = 16)
    @GetMapping("/getAll")
    @Transactional
    Result getAllOrder() {

        List<PetOrder> petOrders = petOrderService.list();

        List<PetOrderVO> petOrderVOS = new ArrayList<>();

        for (PetOrder petOrder : petOrders) {
            PetOrderVO petOrderVO = new PetOrderVO();
            BeanUtils.copyProperties(petOrder, petOrderVO);

            List<PetOrderItem> orderItems = petOrderItemService.list(new QueryWrapper().eq(PetOrderItem::getOrderId, petOrder.getOrderId()));
            ArrayList<PetOrderItemVO> petOrderItemVOS = new ArrayList<>();
            for (PetOrderItem orderItem : orderItems) {
                PetOrderItemVO orderItemDetail = petOrderService.getOrderItemDetail(orderItem);

                petOrderItemVOS.add(orderItemDetail);
            }

            petOrderVO.setPetOrderItems(petOrderItemVOS);
            petOrderVOS.add(petOrderVO);
        }


        return Result.success(petOrderVOS) ;
    }

    @Operation(summary = "修改订单状态")
    @ApiOperationSupport(order = 20)
    @PutMapping
    Result updateOrderStatus(@RequestParam String orderNo, @RequestParam Integer status) {

        PetOrder petOrder = petOrderService.getOne(new QueryWrapper().eq(PetOrder::getOrderNo, orderNo));
        petOrder.setOrderStatus(status);

        petOrderService.updateById(petOrder);

        return Result.success(petOrder) ;
    }


    @Operation(summary = "设置订单支付ws的push_token")
    @ApiOperationSupport(order = 20)
    @PostMapping("/ws")
    @Transactional
    Result setOrderPushToken(@RequestParam String business_id,@RequestParam String push_token) {
        //如果订单存在，先删除
        wsPushMessagesService.remove(new QueryWrapper().eq(WsPushMessages::getBusinessId, business_id));

        String business_type = "order_payment";

        WsPushMessages wsPushMessages = new WsPushMessages();
        wsPushMessages.setBusinessId(business_id);
        wsPushMessages.setPushToken(push_token);
        wsPushMessages.setBusinessType(business_type);
        wsPushMessagesService.save(wsPushMessages);

        return Result.success("ws设置成功") ;
    }

    @Operation(summary = "删除订单支付ws的push_token")
    @ApiOperationSupport(order = 21)
    @DeleteMapping("/ws")
    @Transactional
    Result delOrderPushToken(@RequestParam String business_id) {
        String business_type = "order_payment";

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(WsPushMessages::getBusinessId, business_id);
        queryWrapper.eq(WsPushMessages::getBusinessType, business_type);
        wsPushMessagesService.remove(queryWrapper);

        return Result.success("ws删除成功") ;
    }
}
