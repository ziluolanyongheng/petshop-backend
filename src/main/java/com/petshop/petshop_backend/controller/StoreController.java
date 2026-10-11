package com.petshop.petshop_backend.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.mybatisflex.core.query.QueryWrapper;
import com.petshop.petshop_backend.entity.PetProduct;
import com.petshop.petshop_backend.entity.PetStore;
import com.petshop.petshop_backend.entity.PetUser;
import com.petshop.petshop_backend.entity.base.Result;
import com.petshop.petshop_backend.entity.dto.PetStoreDTO;
import com.petshop.petshop_backend.entity.vo.PetProductVO;
import com.petshop.petshop_backend.entity.vo.PetStoreVO;
import com.petshop.petshop_backend.service.PetProductService;
import com.petshop.petshop_backend.service.PetStoreService;
import com.petshop.petshop_backend.service.PetUserService;
import com.petshop.petshop_backend.utils.BaseContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "3 商店管理接口")
@RestController
@RequestMapping("/store")
public class StoreController {
    @Autowired
    private PetStoreService petStoreService;

    @Autowired
    private OssController ossController;
    @Autowired
    private PetProductService petProductService;

    @Autowired
    private PetUserService petUserService;
    @Autowired
    private StringHttpMessageConverter stringHttpMessageConverter;

    @Operation(summary = "获取全部商店接口")
    @GetMapping("/all")
    @ApiOperationSupport(order = 10)
    public Result getAllProduct() {
        List<PetStore> stores = petStoreService.list();
        List<PetStoreVO> vos = new ArrayList<>();
        for (PetStore store : stores) {
            PetStore petStore = petStoreService.getById(store.getStoreId());
            PetStoreVO petStoreVO = new PetStoreVO();
            BeanUtils.copyProperties(petStore, petStoreVO);
            petStoreVO.setImageUrl(ossController.getImageUrl("store", (long) store.getStoreId()));
            vos.add(petStoreVO);
        }
        return Result.success(vos);
    }

    @Operation(summary = "根据商店id获取商店详情接口")
    @GetMapping
    @ApiOperationSupport(order = 11)
    public Result getStore(@RequestParam int storeId) {
        PetStore petStore = petStoreService.getById(storeId);
        PetStoreVO petStoreVO = new PetStoreVO();
        BeanUtils.copyProperties(petStore, petStoreVO);
        petStoreVO.setImageUrl(ossController.getImageUrl("store", (long) storeId));
        return Result.success(petStoreVO);
    }
    @Operation(summary = "根据商店id获取商品接口")
    @GetMapping("/item")
    @ApiOperationSupport(order = 12)
    public Result getStoreItem(@RequestParam int storeId) {
        PetStore petStore = petStoreService.getById(storeId);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(PetProduct::getStoreId, petStore.getStoreId());
        List<PetProduct> petProducts = petProductService.list(queryWrapper);

        ArrayList<PetProductVO> petProductVOS = new ArrayList<>();
        for (PetProduct petProduct : petProducts) {
            PetProductVO petProductVo = petProductService.getPetProductVo(petProduct);
            petProductVOS.add(petProductVo);
        }

        return Result.success(petProductVOS);
    }



    @Operation(summary = "商家：创建新商店")
    @PostMapping
    @ApiOperationSupport(order = 20)
    public Result addStore(@RequestBody PetStoreDTO petStoreDTO) {

        //获取商家id
        Integer currentId = BaseContext.getCurrentId();

        PetUser petUser = petUserService.getById(currentId);
        //只能是商家
        if(petUser.getUserRole() != 1){
            return Result.error("仅支持商家创建");
        }

        PetStore serviceOne = petStoreService.getOne(new QueryWrapper().eq(PetStore::getOwnerId, currentId));
        if(serviceOne != null){
            return Result.error("只能创建一个商店");
        }


        PetStore store = new PetStore();
        BeanUtils.copyProperties(petStoreDTO, store);
        store.setOwnerId(Long.valueOf(currentId));

        petStoreService.save(store);
        PetStore petStore = petStoreService.getById(store.getStoreId());

        PetStoreVO petStoreVO = new PetStoreVO();
        BeanUtils.copyProperties(petStore, petStoreVO);
        petStoreVO.setImageUrl(ossController.getImageUrl("store", (long) store.getStoreId()));

        return Result.success(petStoreVO);
    }


    @Operation(summary = "商家：更新商店信息")
    @PostMapping("/update")
    @ApiOperationSupport(order = 21)
    public Result updateStore(@RequestBody PetStoreDTO petStoreDTO) {

        PetStore store = new PetStore();
        BeanUtils.copyProperties(petStoreDTO, store);

        petStoreService.updateById(store);
        PetStore petStore = petStoreService.getById(store.getStoreId());

        PetStoreVO petStoreVO = new PetStoreVO();
        BeanUtils.copyProperties(petStore, petStoreVO);
        petStoreVO.setImageUrl(ossController.getImageUrl("store", (long) store.getStoreId()));

        return Result.success(petStoreVO);
    }

    @Operation(summary = "商家：获取商店信息")
    @PostMapping("/storeInfo")
    @ApiOperationSupport(order = 22)
    public Result getStore() {

        Integer currentId = BaseContext.getCurrentId();

        PetStore petStore = petStoreService.getOne(new QueryWrapper().eq(PetStore::getOwnerId, currentId));
        if(petStore == null){
            return Result.error("没有商店信息请创建商店");
        }
        PetStoreVO petStoreVO = new PetStoreVO();
        BeanUtils.copyProperties(petStore, petStoreVO);
        petStoreVO.setImageUrl(ossController.getImageUrl("store", (long) petStore.getStoreId()));

        return Result.success(petStoreVO);
    }



}
