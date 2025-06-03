-- 创建库
create database if not exists store_cop;

-- 切换库
use store_cop;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin',
    editTime     datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    UNIQUE KEY uk_userAccount (userAccount),
    INDEX idx_userName (userName)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 图片表
create table if not exists picture
(
    id           bigint auto_increment comment 'id' primary key,
    url          varchar(512)                       not null comment '图片 url',
    name         varchar(128)                       not null comment '图片名称',
    introduction varchar(512)                       null comment '简介',
    category     varchar(64)                        null comment '分类',
    tags         varchar(512)                       null comment '标签（JSON 数组）',
    picSize      bigint                             null comment '图片体积',
    picWidth     int                                null comment '图片宽度',
    picHeight    int                                null comment '图片高度',
    picScale     double                             null comment '图片宽高比例',
    picFormat    varchar(32)                        null comment '图片格式',
    userId       bigint                             not null comment '创建用户 id',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    editTime     datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除',
    INDEX idx_name (name),                 -- 提升基于图片名称的查询性能
    INDEX idx_introduction (introduction), -- 用于模糊搜索图片简介
    INDEX idx_category (category),         -- 提升基于分类的查询性能
    INDEX idx_tags (tags),                 -- 提升基于标签的查询性能
    INDEX idx_userId (userId)              -- 提升基于用户 ID 的查询性能
) comment '图片' collate = utf8mb4_unicode_ci;

ALTER TABLE picture
    ADD COLUMN viewStatus  tinyint default 0 null comment '当前审核状态',
    ADD COLUMN viewTime    datetime          null comment '审核时间',
    ADD COLUMN viewer      bigint            null comment '审核人 id',
    ADD COLUMN viewMessage varchar(512)      null comment '审核理由（原因）',
    ADD INDEX idx_viewer (viewer),
    ADD INDEX idx_viewMessage (viewMessage);

ALTER TABLE picture
    ADD COLUMN originalUrl varchar(512) not null comment '原始图片url';

-- 空间表
create table if not exists space
(
    id            bigint auto_increment comment 'id' primary key,
    spaceName     varchar(128) default '私人空间'        null comment '空间名称',
    spaceLevel    tinyint      default 0                 null comment '空间等级',
    spaceSizeUsed bigint       default 0                 null comment '空间已使用容量',
    spaceMaxSize  bigint       default 0                 null comment '空间最大容量',
    spaceTotalCount bigint       default 0                 null comment '当前空间下的图片数量',
    spaceMaxCount  bigint       default 0                 null comment '空间最大图片数量',
    userId        bigint                                 not null comment '创建用户 id',
    createTime    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    editTime      datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    updateTime    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint      default 0                 not null comment '是否删除',
    INDEX idx_userId (userId),        -- 提升基于用户的查询性能
    INDEX idx_spaceName (spaceName),  -- 提升基于空间名称的查询效率
    INDEX idx_spaceLevel (spaceLevel) -- 提升基于空间级别的查询性能
) comment '空间' collate = utf8mb4_unicode_ci;

-- 添加新列
ALTER TABLE picture
    ADD COLUMN spaceId bigint null comment '空间 id(为空表示公共空间)';

-- 创建索引
CREATE INDEX idx_spaceId ON picture (spaceId);

-- 为图片表添加picAve字段
ALTER TABLE picture
    ADD COLUMN picAve varchar(32) null comment '图片平均色(主色调)';

# 首先，仍然是5个固定字段：id, createTime, editTime, updateTime, isDelete。然后，需要保存创建任务的相关信息，这些信息来源于API的响应结果，主要有request_id, task_id, task_status, code, message。另外，还需要记录是谁创建了这个任务，所以需要userId.

-- 任务表
create table if not exists task
(
    id            bigint auto_increment comment 'id' primary key,
    taskId        varchar(128)                                null comment '任务id',
    requestId        varchar(128)                                null comment '请求唯一标识。可用于请求明细溯源和问题排查。',
    taskStatus        varchar(128)                                null comment '任务状态。',
    imageUrl  varchar(512) null comment '图片结果url',
    code     varchar(128)         null comment '请求失败的错误码。请求成功时不会返回此参数',
    message     varchar(128)         null comment '请求失败的消息。请求成功时不会返回此参数',
    userId        bigint                                 not null comment '创建用户 id',
    createTime    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    editTime      datetime     default CURRENT_TIMESTAMP not null comment '编辑时间',
    updateTime    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint      default 0                 not null comment '是否删除',
    INDEX idx_userId (userId),        -- 提升基于用户的查询性能
    INDEX idx_taskStatus (taskStatus)  -- 提升基于任务状态的查询效率
) comment '任务' collate = utf8mb4_unicode_ci;

alter table space
    add column spaceType tinyint default 0 null comment '空间类型 0-私有空间;1-团队空间';
-- 空间成员表
create table if not exists space_user
(
    id            bigint auto_increment comment 'id' primary key,
    spaceId        bigint                                 not null comment '空间 id',
    userId        bigint                                 not null comment '创建用户 id',
    spaceRole      varchar(128)   default 'viewer'                             null comment '空间角色 admin-管理员;viewer-浏览者;editor-编辑者',
    createTime    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    UNIQUE key (spaceId, userId),
    index idx_spaceId (spaceId),
    index idx_userId (userId)
) comment '空间成员' collate = utf8mb4_unicode_ci;
