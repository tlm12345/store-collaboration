package com.tlm.storecollab.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tlm.storecollab.model.dto.picture.*;
import com.tlm.storecollab.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author tlm
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-05-15 11:10:31
*/
public interface PictureService extends IService<Picture> {

    /**
     * 用户上传图片
     * @param inputSource
     * @param uploadPictureRequest
     * @param loginUser
     * @return
     */
    public PictureVO uploadPicture(Object inputSource,
                                   UploadPictureRequest uploadPictureRequest,
                                   User loginUser);

    /**
     * 用户根据id获取图片信息
     * @param id
     * @return
     */
    public PictureVO getPictureById(Long id);

    /**
     * 用户根据查询条件查询图片列表
     * @param pictureQueryRequest
     * @param loginUser
     * @return
     */
    public List<PictureVO> getPictureList(PictureQueryRequest pictureQueryRequest, User loginUser);

    /**
     * 获取查询条件
     * @param pictureQueryRequest
     * @return
     */
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest, boolean isAdmin);

    /**
     * 将Picture实体类列表转为对应的VO列表
     * @param pictureList
     * @return
     */
    public List<PictureVO> pictureListToVO(List<Picture> pictureList);

    /**
     * 管理员审核图片
     * @param pictureReviewRequest
     * @param loginUser
     * @return
     */
    public boolean reviewPicture(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 管理员批量导入图片
     * @param uploadPictureByBatchRequest
     * @param loginUser
     * @return
     */
    public Integer graspPicturesByBatch(UploadPictureByBatchRequest uploadPictureByBatchRequest, User loginUser);

    /**
     * 从缓存中获取图片列表,未命中，查询数据库
     * @param pictureQueryRequest
     * @param loginUser
     * @return
     */
    public Page<PictureVO> getPictureVOListFromCache(PictureQueryRequest pictureQueryRequest, User loginUser);

    /**
     * 删除图片
     * @param picture
     * @return
     */
    public boolean removePicture(Picture picture);

    /**
     * 清理图片记录的对象存储
     * @param picture
     */
    public void clearPicture(Picture picture);

    /**
     * 根据文件名生成对应的对象存储key(公共空间)
     * @param fileName
     * @param userId
     * @return
     */
    public String generatePublicKeyForCosByFilename(String fileName, Long userId);

    /**
     * 根据文件名生成对应的对象存储key(公共空间)
     * @param fileName
     * @param userId
     * @return
     */
    public String generatePrivateKeyForCosByFilename(String fileName, Long userId);

    /**
     * 删除私有空间图片，并释放空间
     * @param userId
     * @param pictureId
     */
    public void removePrivatePictureAndReleaseSpace(Long userId, Long pictureId);

    /**
     * 根据颜色搜索图片
     */
    public List<PictureVO> searchPictureByColor(SearchPictureByColorRequest searchPictureByColorRequest, User loginUser);

    /**
     * 批量编辑图片
     */
    public boolean editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);

}
