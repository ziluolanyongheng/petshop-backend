package com.petshop.petshop_backend.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.petshop.petshop_backend.controller.OssController;
import com.petshop.petshop_backend.entity.PetUser;
import com.petshop.petshop_backend.entity.PetUserAuth;
import com.petshop.petshop_backend.entity.base.Result;
import com.petshop.petshop_backend.entity.vo.PetUserVO;
import com.petshop.petshop_backend.mapper.PetUserMapper;
import com.petshop.petshop_backend.service.PetUserAuthService;
import com.petshop.petshop_backend.service.PetUserService;
import com.petshop.petshop_backend.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class QQAuthServiceImpl {

    @Autowired
    private PetUserService petUserService;

    @Autowired
    private PetUserAuthService petUserAuthService;

    @Autowired
    private PetUserMapper petUserMapper;

    @Autowired
    private OssController ossController;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 处理QQ登录
     */
    public Result handleQQLogin(String code) {
        try {
            // 1. 获取用户信息
            String userInfo = getUserInfoFromAPI(code);
            System.out.println("获取到的用户信息: " + userInfo);
            
            // 2. 解析用户信息
            Map<String, Object> userInfoMap = parseUserInfo(userInfo);
            System.out.println("解析后的用户信息: " + userInfoMap);
            
            // 3. 处理用户登录或注册
            return processUserLogin(userInfoMap);
            
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("QQ登录处理失败：" + e.getMessage());
        }
    }

    /**
     * 从第三方API获取用户信息
     */
    private String getUserInfoFromAPI(String code) {
        String userInfoUrl = "https://qq.wch666.com/api/get_user_info.php?code=" + code;
        System.out.println("调用第三方API获取用户信息，URL: " + userInfoUrl);
        
        String result = restTemplate.getForObject(userInfoUrl, String.class);
        System.out.println("第三方API返回原始数据: " + result);
        
        return result;
    }

    /**
     * 解析用户信息
     */
    private Map<String, Object> parseUserInfo(String userInfo) throws Exception {
        Map<String, Object> result = new HashMap<>();
        
        System.out.println("开始解析用户信息，原始数据: " + userInfo);
        
        if (userInfo == null || userInfo.trim().isEmpty()) {
            throw new Exception("用户信息为空");
        }
        
        try {
            // 首先解析外层JSON
            JsonNode rootNode = objectMapper.readTree(userInfo);
            System.out.println("JSON解析成功，根节点: " + rootNode.toString());
            
            // 检查是否有data字段，如果有则解析data字段中的JSON字符串
            JsonNode dataNode = rootNode.get("data");
            if (dataNode != null && !dataNode.isNull()) {
                String dataString = dataNode.asText();
                System.out.println("找到data字段，内容: " + dataString);
                
                if (dataString != null && !dataString.isEmpty()) {
                    // 解析data字段中的JSON字符串
                    JsonNode userDataNode = objectMapper.readTree(dataString);
                    System.out.println("data字段JSON解析成功: " + userDataNode.toString());
                    
                    // 根据实际返回的字段名解析用户信息
                    String openid = getFieldValue(userDataNode, "open_id", "openid", "id", "user_id");
                    String nickname = getFieldValue(userDataNode, "nickname", "nick_name", "name", "user_name");
                    String figureurl = getFieldValue(userDataNode, "figureurl", "figure_url", "avatar", "headimgurl", "head_img_url");
                    String gender = getFieldValue(userDataNode, "gender", "sex");
                    
                    result.put("openid", openid);
                    result.put("nickname", nickname);
                    result.put("figureurl", figureurl);
                    result.put("gender", gender);
                    
                    System.out.println("解析结果 - openid: " + openid + ", nickname: " + nickname + ", figureurl: " + figureurl + ", gender: " + gender);
                    
                    // 如果解析成功，直接返回
                    if (openid != null && !openid.isEmpty()) {
                        return result;
                    }
                }
            }
            
            // 如果没有data字段，尝试直接解析
            System.out.println("尝试直接解析根节点");
            String openid = getFieldValue(rootNode, "open_id", "openid", "id", "user_id");
            String nickname = getFieldValue(rootNode, "nickname", "nick_name", "name", "user_name");
            String figureurl = getFieldValue(rootNode, "figureurl", "figure_url", "avatar", "headimgurl", "head_img_url");
            String gender = getFieldValue(rootNode, "gender", "sex");
            
            result.put("openid", openid);
            result.put("nickname", nickname);
            result.put("figureurl", figureurl);
            result.put("gender", gender);
            
            System.out.println("直接解析结果 - openid: " + openid + ", nickname: " + nickname + ", figureurl: " + figureurl + ", gender: " + gender);
            
            // 如果解析成功，直接返回
            if (openid != null && !openid.isEmpty()) {
                return result;
            }
            
        } catch (Exception e) {
            System.out.println("JSON解析失败，尝试其他方式: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 如果不是JSON格式，尝试解析其他格式
        System.out.println("尝试解析非JSON格式数据");
        result.put("rawData", userInfo);
        
        // 尝试从原始数据中提取信息
        if (userInfo.contains("openid") || userInfo.contains("open_id")) {
            // 简单的字符串解析
            String[] parts = userInfo.split("&");
            for (String part : parts) {
                if (part.contains("=")) {
                    String[] keyValue = part.split("=");
                    if (keyValue.length == 2) {
                        result.put(keyValue[0], keyValue[1]);
                    }
                }
            }
        }
        
        System.out.println("最终解析结果: " + result);
        return result;
    }
    
    /**
     * 从JsonNode中获取字段值，支持多个可能的字段名
     */
    private String getFieldValue(JsonNode jsonNode, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode node = jsonNode.get(fieldName);
            if (node != null && !node.isNull() && !node.asText().isEmpty()) {
                return node.asText();
            }
        }
        return null;
    }

    /**
     * 处理用户登录或注册
     */
    private Result processUserLogin(Map<String, Object> userInfo) {
        String openid = (String) userInfo.get("openid");
        String nickname = (String) userInfo.get("nickname");
        
        System.out.println("处理登录，openid: " + openid + ", nickname: " + nickname);
        System.out.println("完整用户信息: " + userInfo);
        
        if (openid == null || openid.isEmpty()) {
            // 如果没有openid，尝试使用其他字段作为唯一标识
            openid = (String) userInfo.get("rawData");
            System.out.println("使用rawData作为openid: " + openid);
            
            if (openid == null || openid.isEmpty()) {
                System.out.println("无法获取用户OpenID，用户信息: " + userInfo);
                return Result.error("无法获取用户OpenID，用户信息: " + userInfo);
            }
        }

        // 查找是否已存在该QQ用户
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq(PetUserAuth::getAuthKey, openid)
                   .eq(PetUserAuth::getAuthType, "qq");
        
        System.out.println("查询QQ用户认证记录，openid: " + openid);
        PetUserAuth existingAuth = petUserAuthService.getOne(queryWrapper);
        
        if (existingAuth != null) {
            // 用户已存在，执行登录
            System.out.println("用户已存在，执行登录，userId: " + existingAuth.getUserId());
            return loginExistingUser(existingAuth.getUserId());
        } else {
            // 用户不存在，执行注册
            System.out.println("用户不存在，执行注册");
            return registerNewUser(userInfo);
        }
    }

    /**
     * 登录已存在的用户
     */
    private Result loginExistingUser(Long userId) {
        System.out.println("开始登录已存在用户，userId: " + userId);
        
        PetUser petUser = petUserService.getById(userId);
        if (petUser == null) {
            System.out.println("用户不存在，userId: " + userId);
            return Result.error("用户不存在");
        }

        PetUserVO petUserVO = new PetUserVO();
        BeanUtils.copyProperties(petUser, petUserVO);
        petUserVO.setToken(JwtUtil.generateToken(petUser));
        
        System.out.println("用户登录成功，用户名: " + petUser.getUserName() + ", token: " + petUserVO.getToken());
        return Result.success( petUserVO);
    }

    /**
     * 注册新用户
     */
    private Result registerNewUser(Map<String, Object> userInfo) {
        System.out.println("开始注册新用户，用户信息: " + userInfo);
        
        String openid = (String) userInfo.get("openid");
        String nickname = (String) userInfo.get("nickname");
        String figureurl = (String) userInfo.get("figureurl");
        String gender = (String) userInfo.get("gender");

        // 如果nickname为空，使用默认值
        if (nickname == null || nickname.isEmpty()) {
            nickname = "QQ用户";
        }

        // 生成唯一的用户名和邮箱
        String baseUserName = "QQ_" + nickname;
        String baseEmail = "qq_" + openid.substring(0, Math.min(openid.length(), 10)) + "@qq.com";
        
        // 检查用户名是否已存在，如果存在则添加随机后缀
        String finalUserName = baseUserName;
        String finalEmail = baseEmail;
        int suffix = 1;
        
        while (true) {
            try {
                // 检查用户名是否存在
                QueryWrapper userNameQuery = new QueryWrapper();
                userNameQuery.eq(PetUser::getUserName, finalUserName);
                PetUser existingUser = petUserService.getOne(userNameQuery);
                
                if (existingUser != null) {
                    finalUserName = baseUserName + "_" + suffix;
                    suffix++;
                    continue;
                }
                
                // 检查邮箱是否存在
                QueryWrapper emailQuery = new QueryWrapper();
                emailQuery.eq(PetUser::getUserEmail, finalEmail);
                PetUser existingEmailUser = petUserService.getOne(emailQuery);
                
                if (existingEmailUser != null) {
                    finalEmail = "qq_" + openid.substring(0, Math.min(openid.length(), 8)) + "_" + suffix + "@qq.com";
                    suffix++;
                    continue;
                }
                
                // 如果用户名和邮箱都不存在，跳出循环
                break;
                
            } catch (Exception e) {
                System.out.println("检查用户名/邮箱时出错: " + e.getMessage());
                // 如果检查失败，使用带时间戳的后缀确保唯一性
                long timestamp = System.currentTimeMillis() % 10000;
                finalUserName = baseUserName + "_" + timestamp;
                finalEmail = "qq_" + openid.substring(0, Math.min(openid.length(), 8)) + "_" + timestamp + "@qq.com";
                break;
            }
        }

        // 创建新用户
        PetUser newUser = new PetUser();
        newUser.setUserName(finalUserName); // 使用确保唯一的用户名
        // 为QQ用户设置一个默认密码（加密后的）
        newUser.setUserPassword(passwordEncoder.encode("qq123456"));
        // 为QQ用户设置默认邮箱
        newUser.setUserEmail(finalEmail);
//        newUser.setUserNickname(nickname);
//        newUser.setUserAvatar(figureurl);
//        newUser.setUserGender("男".equals(gender) ? 1 : "女".equals(gender) ? 2 : 0);
        newUser.setUserRole(0); // 权限
        newUser.setUserType(0); // 普通用户
        newUser.setCreatedTime(LocalDateTime.now());
        newUser.setUpdatedTime(LocalDateTime.now());

        try {
            // 保存用户
            petUserService.save(newUser);
            System.out.println("用户创建成功: " + finalUserName + ", 邮箱: " + finalEmail);
            
            // 保存图片（如果有头像URL）
            if (figureurl != null && !figureurl.isEmpty()) {
                try {
                    QueryWrapper queryWrapper = new QueryWrapper();
                    queryWrapper.eq(PetUser::getUserName, finalUserName);
                    PetUser petUser = petUserService.getOne(queryWrapper);
                    if (petUser != null) {
                        ossController.uploadOtherImage(figureurl,"avatar","avatar",petUser.getUserId(),true,0);
                        System.out.println("头像保存成功");
                    }
                } catch (Exception e) {
                    System.out.println("保存头像失败: " + e.getMessage());
                }
            }

            // 创建用户认证记录
            PetUserAuth userAuth = new PetUserAuth();
            userAuth.setUserId(newUser.getUserId());
            userAuth.setAuthType("qq");
            userAuth.setAuthKey(openid);
            userAuth.setAuthToken(openid); // 使用openid作为token
            userAuth.setCreatedTime(LocalDateTime.now());
            userAuth.setUserEmail(finalEmail); // 设置邮箱

            petUserAuthService.save(userAuth);
            System.out.println("用户认证记录创建成功");

            // 返回用户信息
            PetUserVO petUserVO = new PetUserVO();
            BeanUtils.copyProperties(newUser, petUserVO);
            petUserVO.setToken(JwtUtil.generateToken(newUser));

            return Result.success(petUserVO);
            
        } catch (Exception e) {
            System.out.println("保存用户时出错: " + e.getMessage());
            e.printStackTrace();
            return Result.error("用户注册失败: " + e.getMessage());
        }
    }
} 