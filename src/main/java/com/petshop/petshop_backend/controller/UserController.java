package com.petshop.petshop_backend.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.mybatisflex.core.query.QueryWrapper;
import com.petshop.petshop_backend.controller.OssController;
import com.petshop.petshop_backend.entity.*;
import com.petshop.petshop_backend.entity.base.Result;
import com.petshop.petshop_backend.entity.dto.*;
import com.petshop.petshop_backend.entity.vo.PetCartVO;
import com.petshop.petshop_backend.entity.vo.PetCartWithStoreInfoVO;
import com.petshop.petshop_backend.entity.vo.PetUserInfoVO;
import com.petshop.petshop_backend.entity.vo.PetUserVO;
import com.petshop.petshop_backend.service.*;
import com.petshop.petshop_backend.utils.BaiduLlmUtil;
import com.petshop.petshop_backend.utils.BaseContext;
import com.petshop.petshop_backend.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.petshop.petshop_backend.entity.table.PetCartTableDef.PET_CART;
import static com.petshop.petshop_backend.entity.table.PetStoreTableDef.PET_STORE;
import static com.petshop.petshop_backend.entity.table.PetUserTableDef.PET_USER;
import static com.petshop.petshop_backend.entity.table.PushMessagesTableDef.PUSH_MESSAGES;

@Tag(name = "1 用户管理接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private OssController ossController;

    @Autowired
    private PetUserService petUserService;

    @Autowired
    private PetImageRelService petImageRelService;

    @Autowired
    private PetImageService petImageService;
    @Autowired
    private PetUserAddressService petUserAddressService;

    @Autowired
    private PetProductService petProductService;

    @Autowired
    private PetCartService  petCartService;
    @Autowired
    private PetStoreService petStoreService;
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private PushMessagesService pushMessagesService;

    //登录接口
    @Operation(summary = "登录接口")
    @GetMapping("/login")
    @ApiOperationSupport(order = 10)
    public Result login(@RequestParam String userName, @RequestParam String password) {
        PetUser petUser = new PetUser();
        petUser.setUserName(userName);
        petUser.setUserPassword(password);
        return (Result) petUserService.login(petUser);
    }

    //登录接口 authType ： password 、 email 、
    @Operation(summary = "统一登录接口")
    @PostMapping("/uniLogin")
    @ApiOperationSupport(order = 12)
    public Result uniLogin(@RequestBody AuthParamsDTO authParamsDTO) {

        try{
            //认证类型
            String authType = authParamsDTO.getAuthType();

            //根据认证类型从spring容器中取出指定的bean
            String beanName = authType+"_authservice";
            AuthService authService = applicationContext.getBean(beanName, AuthService.class);

            //登录后的用户
            PetUser petUser = authService.execute(authParamsDTO);

            //返回数据
            PetUserVO petUserVO = new PetUserVO();
            BeanUtils.copyProperties(petUser, petUserVO);
            petUserVO.setToken(JwtUtil.generateToken(petUser));

            return Result.success(petUserVO);
        }catch (Exception e){
            e.printStackTrace();

            return Result.error(e.getMessage());
        }

    }

    //注册接口
    @Operation(summary = "注册接口")
    @PostMapping("/register")
    @ApiOperationSupport(order = 11)
    @Transactional
    public Result register(@RequestBody PetUserRegisterDTO petUserRegisterDTO) {

        Result register =(Result) petUserService.register(petUserRegisterDTO);
        return (Result)register ;
    }


    @Operation(summary = "获取全部地址接口")
    @GetMapping("/address/all")
    @ApiOperationSupport(order = 20)
    public Result getAllAddress() {
        List<PetUserAddress> addresses = petUserAddressService.list();
        return Result.success(addresses);
    }

    @Operation(summary = "根据用户Id获取地址接口")
    @GetMapping("/address")
    @ApiOperationSupport(order = 21)
    public Result getAddress() {
        Integer currentId = BaseContext.getCurrentId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(PetUserAddress::getUserId, currentId);

        List<PetUserAddress> addresses = petUserAddressService.list(queryWrapper);
        return Result.success(addresses);
    }
    @Operation(summary = "根据地址Id修改默认地址")
    @PostMapping("/addressSetDefault")
    @ApiOperationSupport(order = 21)
    public Result addAddressSetDefault(@RequestBody PetUserAddressDTO petUserAddressDTO) {
        Integer currentId = BaseContext.getCurrentId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(PetUserAddress::getUserId, currentId);
        List<PetUserAddress> addresses = petUserAddressService.list(queryWrapper);
        for (PetUserAddress petUserAddress : addresses) {
            petUserAddress.setIsDefault(false);
            petUserAddressService.updateById(petUserAddress);
        }
        PetUserAddress petUserAddress = petUserAddressService.getById(petUserAddressDTO.getAddressId());
        petUserAddress.setIsDefault(true);
        boolean updated = petUserAddressService.updateById(petUserAddress);
        return Result.success(updated);
    }
    @Operation(summary = "根据用户Id添加地址接口")
    @PostMapping("/address")
    @ApiOperationSupport(order = 22)
    public Result addAddress(@RequestBody PetUserAddressDTO petUserAddressDTO) {
        Integer currentId = BaseContext.getCurrentId();

        PetUserAddress userAddress = new PetUserAddress();
        BeanUtils.copyProperties(petUserAddressDTO, userAddress);

        userAddress.setCreatedTime(LocalDateTime.now());
        userAddress.setUserId(Long.valueOf(currentId));
        userAddress.setIsDefault(false);

        //查询判断用户地址是否一条都没有
        QueryWrapper queryWrapper = new QueryWrapper().eq(PetUserAddress::getUserId, currentId);
        List<PetUserAddress> addresses = petUserAddressService.list(queryWrapper);
        if(addresses.size() == 0){
            //如果一条都没有，直接设为默认
            userAddress.setIsDefault(true);
        }

        boolean save = petUserAddressService.save(userAddress);
        PetUserAddress petUserAddress = petUserAddressService.getById(userAddress.getAddressId());

        return Result.success(petUserAddress);
    }
    @Operation(summary = "根据用户Id更新地址接口")
    @PutMapping("/address")
    @ApiOperationSupport(order = 23)
    public Result updateAddress(@RequestBody PetUserAddressDTO petUserAddressDTO) {
        Integer currentId = BaseContext.getCurrentId();

        PetUserAddress userAddress = new PetUserAddress();
        BeanUtils.copyProperties(petUserAddressDTO, userAddress);

        userAddress.setUserId(Long.valueOf(currentId));

        boolean updated = petUserAddressService.updateById(userAddress);

        PetUserAddress petUserAddress = petUserAddressService.getById(userAddress.getAddressId());

        return Result.success(petUserAddress);
    }
    @Operation(summary = "根据地址Id删除地址接口")
    @PutMapping("/addressDelete")
    @ApiOperationSupport(order = 23)
    public Result deleteAddress(@RequestBody PetUserAddressDTO petUserAddressDTO) {
        Integer currentId = BaseContext.getCurrentId();

        PetUserAddress userAddress = new PetUserAddress();
        BeanUtils.copyProperties(petUserAddressDTO, userAddress);

        userAddress.setUserId(Long.valueOf(currentId));

        boolean deleted = petUserAddressService.removeById(userAddress);

        return Result.success(deleted);
    }
    @Operation(summary = "获取全部购物车接口")
    @GetMapping("/cart/all")
    @ApiOperationSupport(order = 30)
    public Result getAllCart() {
        List<PetCart> carts = petCartService.list();
        return Result.success(carts);
    }

    @Operation(summary = "根据用户Id获取购物车接口")
    @GetMapping("/cart")
    @ApiOperationSupport(order = 31)
    public Result getCart() {

        //获取用户id
        Integer currentId = BaseContext.getCurrentId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(PetCart::getUserId, currentId);

        //查询到用户购物车
        List<PetCart> carts = petCartService.list(queryWrapper);

        List<PetCartWithStoreInfoVO> petCartWithStoreInfoVOList = new ArrayList<>();

        //key是商店id，value是index,商店去重
        HashMap<Long,Integer> hashMap = new HashMap<Long, Integer>();

        //遍历购物车，然后查询商品信息，填充购物车
        carts.forEach( item ->{
            //获取商品信息
            PetProduct petProduct = petProductService.getById(item.getProductId());
            PetCartVO petCartVO = new PetCartVO();
            BeanUtils.copyProperties(item, petCartVO);

            //按照店铺归类购物车
            PetCartWithStoreInfoVO petCartWithStoreInfoVO = new PetCartWithStoreInfoVO();

            //填充购物车显示Vo
            petCartVO.setProductThumb(petProduct.getProductThumb());
            petCartVO.setProductName(petProduct.getProductName());
            petCartVO.setProductPrice(petProduct.getProductPrice());
            Integer index= hashMap.get(petProduct.getStoreId());
            //没有查询过这个商店，才开始查询
            if(index==null){
                //获取商店信息
                PetStore petStore = petStoreService.getById(petProduct.getStoreId());
                petCartWithStoreInfoVOList.add(petCartWithStoreInfoVO);
                petCartWithStoreInfoVO.setStoreName(petStore.getStoreName());
                petCartWithStoreInfoVO.setStoreId(petStore.getStoreId());

                //查询店铺图片
                List<PetImageRel> storeImgs = petImageRelService.getByEntityTypeAndEntityId("store", petStore.getStoreId());
                PetImage petImage = petImageService.getById(storeImgs.get(0).getImgId());
                petCartWithStoreInfoVO.setStoreImg(petImage.getImgUrl());

                index = petCartWithStoreInfoVOList.size()-1;
                hashMap.put(petProduct.getStoreId(),petCartWithStoreInfoVOList.size()-1);
            }

            petCartWithStoreInfoVOList.get(index).getPetCartVOList().add(petCartVO);

        });

        return Result.success(petCartWithStoreInfoVOList);
    }

    @Operation(summary = "根据用户Id添加购物车接口")
    @PostMapping("/cart")
    @ApiOperationSupport(order = 32)
    public Result addCart(@RequestBody PetCartDTO petCartDTO) {

        Integer currentId = BaseContext.getCurrentId();
        if(petCartDTO.getCartId() == null) petCartDTO.setCartId(0L);
        QueryWrapper queryWrapper = new QueryWrapper();
//        queryWrapper.where(PET_CART.CART_ID.eq(petCartDTO.getCartId()));
        queryWrapper.eq(PetCart::getCartId, petCartDTO.getCartId())
                .eq(PetCart::getUserId, currentId);
        PetCart one = petCartService.getOne(queryWrapper);

        if(one == null){
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq(PetCart::getProductId, petCartDTO.getProductId())
                    .eq(PetCart::getUserId, currentId);
            one = petCartService.getOne(wrapper);
        }

        //添加数量
        if( one != null) {

            one.setCartQuantity(one.getCartQuantity()+1);
            petCartService.updateById(one);
            PetCart cart = petCartService.getById(one.getCartId());
            return Result.success(cart);
        }
        else{

            QueryWrapper queryWrapper2 = new QueryWrapper();
            queryWrapper2.eq(PetCart::getUserId, currentId);

            PetCart petCart = new PetCart();
            BeanUtils.copyProperties(petCartDTO, petCart);
            petCart.setUserId(Long.valueOf(currentId));
            petCart.setCreatedTime(LocalDateTime.now());
            petCart.setIsSelected(false);

            petCartService.save(petCart);
            PetCart cart = petCartService.getById(petCart.getCartId());
            return Result.success(cart);
        }
    }

    @Operation(summary = "更新购物车数量接口")
    @PutMapping("/cart")
    @ApiOperationSupport(order = 33)
    public Result updateCart(@RequestBody PetCartDTO petCartDTO) {

        Integer currentId = BaseContext.getCurrentId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(PetCart::getUserId, currentId).eq(PetCart::getCartId, petCartDTO.getCartId());
        PetCart one = petCartService.getOne(queryWrapper);

        one.setCartQuantity(petCartDTO.getCartQuantity());

        petCartService.updateById(one);

        PetCart petCart = petCartService.getById(one.getCartId());
        return Result.success(petCart);
    }


    @Operation(summary = "获取用户信息")
    @GetMapping("/userInfo")
    @ApiOperationSupport(order = 40)
    public Result getUserInfo() {

        Integer currentId = BaseContext.getCurrentId();
        List<String> PetImageUrls= ossController.getImageUrl("avatar",Long.valueOf(currentId));

        PetUser petUser = petUserService.getById(currentId);

        PetUserInfoVO petUserInfoVO = new PetUserInfoVO();
        petUserInfoVO.setPetImageUrl(PetImageUrls.get(0));
        BeanUtils.copyProperties(petUser, petUserInfoVO);

        return Result.success(petUserInfoVO);
    }

    @Operation(summary = "更新用户信息")
    @PostMapping("/userInfo")
    @ApiOperationSupport(order = 41)
    public Result updateUserInfo(@RequestBody PetUserInfoDTO petUserInfoDTO) {

        Integer currentId = BaseContext.getCurrentId();
        List<String> imageUrls =ossController.getImageUrl("avatar",Long.valueOf(currentId));
        PetUser petUser = new PetUser();
        BeanUtils.copyProperties(petUserInfoDTO, petUser);
        petUser.setUserId(Long.valueOf(currentId));
        petUser.setUpdatedTime(LocalDateTime.now());
        petUserService.updateById(petUser);
        PetUser user = petUserService.getById(currentId);
        PetUserInfoVO userInfoVO = new PetUserInfoVO();
        userInfoVO.setPetImageUrl(imageUrls.get(0));
        BeanUtils.copyProperties(user, userInfoVO);

        return Result.success(userInfoVO);
    }

    @Operation(summary = "推送信息接口")
    @PostMapping("/push")
    @ApiOperationSupport(order = 50)
    public Result pushMessage(@RequestBody PushMessagesDTO pushMessagesDTO) {

        petUserService.pushMessage(pushMessagesDTO);



        return Result.success("推送成功");
    }

    @Operation(summary = "查询推送消息接口")
    @GetMapping("/push")
    @ApiOperationSupport(order = 51)
    public Result pushMessage() {
        Integer currentId = BaseContext.getCurrentId();

        QueryWrapper wrapper = new QueryWrapper().where(PUSH_MESSAGES.USER_ID.eq(currentId));
        List<PushMessages> messagesList = pushMessagesService.list(wrapper);

        return Result.success(messagesList);
    }

    @Operation(summary = "推送消息已读确认")
    @GetMapping("/push/isRead")
    @ApiOperationSupport(order = 52)
    public Result isReadPushMessage(@RequestParam Integer pushMessageId) {

        PushMessages pushMessages = new PushMessages();
        pushMessages.setId(Long.valueOf(pushMessageId));
        pushMessages.setIsRead(true);

        pushMessagesService.updateById(pushMessages);

        return Result.success("已确认");
    }

}
