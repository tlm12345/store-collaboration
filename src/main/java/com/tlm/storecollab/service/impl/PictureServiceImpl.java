package com.tlm.storecollab.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tlm.storecollab.model.entity.Picture;
import com.tlm.storecollab.service.PictureService;
import com.tlm.storecollab.mapper.PictureMapper;
import org.springframework.stereotype.Service;

/**
* @author tlm
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-05-15 11:10:31
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

}




