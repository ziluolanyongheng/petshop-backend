package com.petshop.petshop_backend.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.petshop.petshop_backend.entity.PushMessages;
import com.petshop.petshop_backend.mapper.PushMessagesMapper;
import com.petshop.petshop_backend.service.PushMessagesService;
import org.springframework.stereotype.Service;

/**
 * 系统推送消息表 服务层实现。
 *
 * @author Xinzhe
 * @since 2025-06-27
 */
@Service
public class PushMessagesServiceImpl extends ServiceImpl<PushMessagesMapper, PushMessages> implements PushMessagesService {

}
