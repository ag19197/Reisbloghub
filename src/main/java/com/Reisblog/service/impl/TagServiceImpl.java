package com.Reisblog.service.impl;

import com.Reisblog.entity.Tag;
import com.Reisblog.mapper.TagMapper;
import com.Reisblog.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
}
