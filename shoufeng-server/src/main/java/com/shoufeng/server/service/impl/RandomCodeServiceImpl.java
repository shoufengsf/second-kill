package com.shoufeng.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shoufeng.model.entity.RandomCodeEntity;
import com.shoufeng.model.mapper.RandomCodeMapper;
import com.shoufeng.server.service.IRandomCodeService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author shoufeng
 * @since 2019-09-21
 */
@Service
public class RandomCodeServiceImpl extends ServiceImpl<RandomCodeMapper, RandomCodeEntity> implements IRandomCodeService {

}
