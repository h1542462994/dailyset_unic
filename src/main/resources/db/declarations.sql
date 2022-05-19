# create at 2022/4/20
# author: h1542462994

# dialect: mysql
# database-name: dailyset_unic
# configuration localhost:3306 -u dbuser -p dbpassword

# file: declarations.sql

# create database `dailyset_unic` dsl
CREATE DATABASE IF NOT EXISTS `dailyset_unic`
    DEFAULT CHARACTER SET `utf8mb4`
    DEFAULT COLLATE `utf8mb4_general_ci`;

USE `dailyset_unic`;

# create table `preference` dsl
CREATE TABLE IF NOT EXISTS `preference`
(
    `preference_name` VARCHAR(64) UNIQUE NOT NULL,
    `use_default`     BOOLEAN            NOT NULL DEFAULT TRUE,
    `value`           VARCHAR(256)       NOT NULL,
    PRIMARY KEY (`preference_name`)
);

# create table `unic_ticket` dsl
# ticket is a binder for student account
CREATE TABLE IF NOT EXISTS `ticket`
(
    `ticket_id` VARCHAR(64) UNIQUE NOT NULL,
    `uid`       VARCHAR(64)        NOT NULL,
    `password`  VARCHAR(256)       NOT NULL,
    `status`    INTEGER            NOT NULL DEFAULT 0,
    PRIMARY KEY (`ticket_id`)
);

# 本质上是cloud项目表的一个子集.
# create table `dailyset` dsl
CREATE TABLE IF NOT EXISTS `dailyset`
(
    `uid`            VARCHAR(64) UNIQUE NOT NULL, # 资源的uid
    `type`           INTEGER            NOT NULL, # 资源的类型
    `source_version` INTEGER            NOT NULL, # 资源版本
    `matte_version`  INTEGER            NOT NULL, # 蒙版资源版本
    `meta_version`   INTEGER            NOT NULL, # 元数据版本
    PRIMARY KEY (`uid`)
);

# create table `dailyset_meta_links` dsl
CREATE TABLE IF NOT EXISTS `dailyset_meta_links`
(
    `dailyset_uid`   VARCHAR(64) NOT NULL, # 日程表的uid
    `meta_type`      INTEGER     NOT NULL, # 元数据类型
    `meta_uid`       VARCHAR(64) NOT NULL, # 元数据的uid
    `insert_version` INTEGER     NOT NULL, # 插入版本
    `update_version` INTEGER     NOT NULL, # 更新版本
    `remove_version` INTEGER     NOT NULL, # 移除版本
    `last_tick`      DATETIME DEFAULT '1970-1-1 0:00:00',
    INDEX `dailyset_source_links_index1` (`dailyset_uid`, `meta_type`)
);


CREATE TABLE IF NOT EXISTS `dailyset_source_links`
(
    `dailyset_uid`   VARCHAR(64) NOT NULL, # 日程表的uid
    `source_type`    INTEGER     NOT NULL, # 资源类型
    `source_uid`     VARCHAR(64) NOT NULL, # 资源的uid
    `insert_version` INTEGER     NOT NULL, # 插入版本
    `update_version` INTEGER     NOT NULL, # 更新版本
    `remove_version` INTEGER     NOT NULL, # 删除版本
    `last_tick`      DATETIME DEFAULT '1970-1-1 0:00:00',
    INDEX `dailyset_source_links_index1` (`dailyset_uid`, `source_type`)
);

# create table `dailyset_course` dsl, source_type = 10
CREATE TABLE IF NOT EXISTS `dailyset_course`
(
    `source_uid`    VARCHAR(64) NOT NULL, # 资源的id
    `year`          INTEGER     NOT NULL, # 名称
    `period_code`   INTEGER     NOT NULL, # 周期码
    `name`          VARCHAR(64) NOT NULL, # 名称
    `campus`        VARCHAR(64) NOT NULL, # 校区
    `location`      VARCHAR(64) NOT NULL, # 地点
    `teacher`       VARCHAR(64) NOT NULL, # 教师
    `weeks`         VARCHAR(64) NOT NULL, # 周数
    `week_day`      INTEGER     NOT NULL, # 周几
    `section_start` INTEGER     NOT NULL, # 开始节数
    `section_end`   INTEGER     NOT NULL, # 结束节数
    `digest`        VARCHAR(64) NOT NULL, # 摘要
    PRIMARY KEY (source_uid)
);

# create table `dailyset_duration` dsl, source_type = 4
CREATE TABLE IF NOT EXISTS `dailyset_duration`
(
    `source_uid`          VARCHAR(64) NOT NULL,   # 资源的id
    `type`                INTEGER     NOT NULL,   # 类型
    `start_date`          DATE        NOT NULL,   # 开始日期
    `end_date`            DATE        NOT NULL,   # 结束日期
    `name`                VARCHAR(64) NOT NULL,   # 名称
    `tag`                 VARCHAR(64) DEFAULT '', # 标签
    `binding_year`        INTEGER     DEFAULT -1, # 绑定年份
    `binding_period_code` INTEGER     DEFAULT -1, # 绑定周期码
    PRIMARY KEY (source_uid)
);

#region table of student and course
# create table `unic_student_info` dsl meta_type = 101
CREATE TABLE IF NOT EXISTS `dailyset_student_info_meta`
(
    `meta_uid`        VARCHAR(64)  NOT NULL,
    `department_name` VARCHAR(256) NOT NULL,
    `class_name`      VARCHAR(256) NOT NULL,
    `name`            VARCHAR(64)  NOT NULL,
    `grade`           INTEGER      NOT NULL,
    PRIMARY KEY (`meta_uid`)
);

# create table `unic_student_course` dsl meta_type = 102
CREATE TABLE IF NOT EXISTS `dailyset_school_info_meta`
(
    `meta_uid`   VARCHAR(64) NOT NULL,
    `identifier` VARCHAR(64) NOT NULL,
    `name`       VARCHAR(64) NOT NULL,
    PRIMARY KEY (`meta_uid`)
);

