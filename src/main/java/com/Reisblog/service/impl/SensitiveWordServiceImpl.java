package com.Reisblog.service.impl;

import com.Reisblog.entity.SensitiveWord;
import com.Reisblog.mapper.SensitiveWordMapper;
import com.Reisblog.service.SensitiveWordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SensitiveWordServiceImpl extends ServiceImpl<SensitiveWordMapper, SensitiveWord> implements SensitiveWordService {
}
