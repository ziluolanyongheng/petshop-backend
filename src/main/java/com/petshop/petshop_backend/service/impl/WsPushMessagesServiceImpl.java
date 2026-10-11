package com.petshop.petshop_backend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.petshop.petshop_backend.entity.WsPushMessages;
import com.petshop.petshop_backend.mapper.WsPushMessagesMapper;
import com.petshop.petshop_backend.service.WsPushMessagesService;
import org.springframework.stereotype.Service;

/**
 * WebSocket推送消息表 服务层实现。
 *
 * @author Xinzhe
 * @since 2025-06-24
 */
@Service
public class WsPushMessagesServiceImpl extends ServiceImpl<WsPushMessagesMapper, WsPushMessages> implements WsPushMessagesService {

}
