package com.petshop.petshop_backend.controller;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.mybatisflex.core.query.QueryWrapper;
import com.petshop.petshop_backend.entity.PetProduct;
import com.petshop.petshop_backend.entity.PetVideo;
import com.petshop.petshop_backend.entity.PetVideoStats;
import com.petshop.petshop_backend.entity.base.Result;
import com.petshop.petshop_backend.entity.dto.PetProductDTO;
import com.petshop.petshop_backend.entity.dto.PetVideoDTO;
import com.petshop.petshop_backend.entity.vo.PetProductVO;
import com.petshop.petshop_backend.entity.vo.PetVideoStatsVO;
import com.petshop.petshop_backend.service.PetProductService;
import com.petshop.petshop_backend.service.PetVideoService;
import com.petshop.petshop_backend.service.PetVideoStatsService;
import com.petshop.petshop_backend.utils.BaseContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "2 产品管理接口接口")
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private PetProductService petProductService;

    @Autowired
    private PetVideoService petVideoService;

    @Autowired
    private PetVideoStatsService petVideoStatsService;

    @Operation(summary = "获取全部产品接口")
    @GetMapping("/all")
    @ApiOperationSupport(order = 10)
    public Result getAllProduct() {
        List<PetProduct> products = petProductService.list();

        ArrayList<PetProductVO> petProductVOS = new ArrayList<>();
        for (PetProduct product : products) {
            PetProductVO petProductVo = petProductService.getPetProductVo(product);
            petProductVOS.add(petProductVo);
        }

        return Result.success(petProductVOS);
    }


    @Operation(summary = "根据产品id获取产品详情")
    @GetMapping
    @ApiOperationSupport(order = 11)
    public Result getProduct(@RequestParam int productId) {
        PetProduct petProduct = petProductService.getById(productId);
        PetProductVO petProductVo = petProductService.getPetProductVo(petProduct);
        return Result.success(petProductVo);
    }

    @Operation(summary = "商家: 添加产品")
    @PostMapping
    @ApiOperationSupport(order = 20)
    public Result addProduct(@RequestBody PetProductDTO petProductDTO) {

        PetProduct petProduct = new PetProduct();
        BeanUtils.copyProperties(petProductDTO, petProduct);

        petProduct.setCreatedTime(LocalDateTime.now());
        petProduct.setProductStatus(0);
        petProductService.save(petProduct);
        PetProduct product = petProductService.getById(petProduct.getProductId());

        return Result.success(product);
    }
    @Operation(summary = "商家: 更新产品")
    @PutMapping
    @ApiOperationSupport(order = 21)
    public Result updateProduct(@RequestBody PetProductDTO petProductDTO) {

        PetProduct petProduct = new PetProduct();
        BeanUtils.copyProperties(petProductDTO, petProduct);

        petProduct.setCreatedTime(LocalDateTime.now());
        petProduct.setProductStatus(0);
        petProductService.updateById(petProduct);
        PetProduct product = petProductService.getById(petProduct.getProductId());

        return Result.success(product);
    }

    @Operation(summary = "商家: 获取全部产品")
    @GetMapping("/store")
    @ApiOperationSupport(order = 22)
    public Result getAllProduct(@RequestParam int storeId) {

        QueryWrapper queryWrapper = new QueryWrapper().eq(PetProduct::getStoreId, storeId);

        List<PetProduct> products = petProductService.list(queryWrapper);

        ArrayList<PetProductVO> petProductVOS = new ArrayList<>();
        for (PetProduct product : products) {
            PetProductVO petProductVo = petProductService.getPetProductVo(product);
            petProductVOS.add(petProductVo);
        }

        return Result.success(petProductVOS);
    }

    @Operation(summary = "商家上传视频接口")
    @PostMapping("/storeVideo")
    @ApiOperationSupport(order = 30)
    public Result addStoreVideo(@RequestBody PetVideoDTO petVideoDTO){
        //获取用户Id
        Integer currentId = BaseContext.getCurrentId();

        PetVideo petVideo = new PetVideo();
        BeanUtils.copyProperties(petVideoDTO, petVideo);
        petVideo.setMerchantId(Long.valueOf(currentId));
        petVideo.setProductId(petVideoDTO.getProductId());
        petVideo.setStatus(true);
        petVideo.setCreatedAt(LocalDateTime.now());
        petVideo.setUpdatedAt(LocalDateTime.now());
        petVideoService.save(petVideo);
        PetVideo video = petVideoService.getById(petVideo.getId());
        PetVideoStats petVideoStats = new PetVideoStats();
        petVideoStats.setVideoId(video.getId());
        petVideoStats.setUpdatedAt(LocalDateTime.now());
        petVideoStatsService.save(petVideoStats);
        return Result.success(video);
    }

    @Operation(summary = "商家修改视频接口")
    @PostMapping("/updateVideo")
    @ApiOperationSupport(order = 31)
    public Result updateStoreVideo(@RequestBody PetVideoDTO petVideoDTO){
        PetVideo petVideo = new PetVideo();
        BeanUtils.copyProperties(petVideoDTO, petVideo);
        petVideoService.updateById(petVideo);
        return Result.success(petVideo);
    }



    @Operation(summary = "商家删除视频接口")
    @PostMapping("/deleteVideo")
    @ApiOperationSupport(order = 32)
    public Result deleteStoreVideoById(@RequestParam Long videoId){
        petVideoService.removeById(videoId);
        return Result.success(true);
    }

    @Operation(summary = "获取指定商家视频接口")
    @GetMapping("/getVideo")
    @ApiOperationSupport(order = 33)
    public Result getStoreVideo(){
        Integer currentId = BaseContext.getCurrentId();

        QueryWrapper queryWrapper = new QueryWrapper().eq(PetVideo::getMerchantId, currentId);
        List<PetVideo>  petVideos = petVideoService.list(queryWrapper);

//        List<PetVideoStats> petVideoStatsList = new ArrayList<>();
//        for (PetVideo petVideo : petVideos) {
//            QueryWrapper queryWrapper1 = new QueryWrapper();
//            queryWrapper1.eq(PetVideoStats::getVideoId, petVideo.getId());
//            PetVideoStats petVideoStats = petVideoStatsService.getOne(queryWrapper1);
//            petVideoStatsList.add(petVideoStats);
//        }
        return Result.success(petVideos);
    }
    @Operation(summary = "根据产品id获取视频接口")
    @GetMapping("/getVideoByProductId")
    @ApiOperationSupport(order = 34)
    public Result getVideoByProductId(@RequestParam Integer productid){
        List<PetVideo> petVideos = new ArrayList<>();
        QueryWrapper queryWrapper = new QueryWrapper().eq(PetVideo::getProductId, productid);
        petVideos = petVideoService.list(queryWrapper);
        return Result.success(petVideos);
    }

    @Operation(summary = "根据视频id获取视频信息接口")
    @GetMapping("/getStatsByVideoId")
    @ApiOperationSupport(order = 35)
    public Result getStatsByVideoId(@RequestParam Long videoId){
//        PetVideo petVideo = petVideoService.getById(videoId);
//        QueryWrapper queryWrapper = new QueryWrapper();
//        queryWrapper.eq(PetVideoStats::getVideoId, petVideo.getId());
//        PetVideoStats petVideoStats = petVideoStatsService.getOne(queryWrapper);
//        PetVideoStatsVO petVideostatsVO = new PetVideoStatsVO();
//        BeanUtils.copyProperties(petVideostatsVO, petVideoStats);
//        petVideostatsVO.setProductId(petVideo.getProductId());
//        petVideostatsVO.setMerchantId(petVideo.getMerchantId());
//        petVideostatsVO.setDesc(petVideo.getDesc());
//        petVideostatsVO.setTitle(petVideo.getTitle());
//        petVideostatsVO.setVideoUrl(petVideo.getVideoUrl());
//        petVideostatsVO.setCoverUrl(petVideo.getCoverUrl());
        return Result.success(getVideoStatsById(videoId));
    }

    @Operation(summary = "获取所有视频接口")
    @GetMapping("/getAllVideo")
    @ApiOperationSupport(order = 36)
    public Result getAllVideo(){
        List<PetVideo> petVideos = petVideoService.list();
        List<PetVideoStatsVO> petVideoStatsVOS = new ArrayList<>();
        for (PetVideo petVideo : petVideos) {
            petVideoStatsVOS.add(getVideoStatsById(petVideo.getId()));
        }
        return Result.success(petVideoStatsVOS);
//        return Result.success(petVideos);
    }
    public PetVideoStatsVO getVideoStatsById(Long videoId){
        PetVideo petVideo = petVideoService.getById(videoId);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(PetVideoStats::getVideoId, petVideo.getId());
        PetVideoStats petVideoStats = petVideoStatsService.getOne(queryWrapper);
        PetVideoStatsVO petVideostatsVO = new PetVideoStatsVO();
        BeanUtils.copyProperties(petVideoStats, petVideostatsVO);
        petVideostatsVO.setProductId(petVideo.getProductId());
        petVideostatsVO.setMerchantId(petVideo.getMerchantId());
        petVideostatsVO.setDesc(petVideo.getDesc());
        petVideostatsVO.setTitle(petVideo.getTitle());
        petVideostatsVO.setVideoUrl(petVideo.getVideoUrl());
        petVideostatsVO.setCoverUrl(petVideo.getCoverUrl());
        return petVideostatsVO;
    }

}
