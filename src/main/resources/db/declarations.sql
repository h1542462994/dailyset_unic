# create at 2022/4/20
# author: h1542462994

# dialect: mysql
# database-name: dailyset_unic
# configuration localhost:3306 -u dbuser -p dbpassword

# file: declarations.sql

# create database `dailyset_unic` dsl
CREATE DATABASE IF NOT EXISTS `dailyset_cloud`
    DEFAULT CHARACTER SET `utf8mb4`
    DEFAULT COLLATE `utf8mb4_general_ci`;

USE `dailyset_cloud`;

# create table `unic_ticket` dsl
# ticket is a binder for student account
CREATE TABLE IF NOT EXISTS `unic_ticket`(
    `ticket_id` VARCHAR(64) UNIQUE NOT NULL,
    `uid` VARCHAR(64) NOT NULL,
    `password` VARCHAR(256) NOT NULL,
    `status` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`ticket_id`)
);

#region table of time and daily_set
# create table `unic_term_duration` dsl
CREATE TABLE IF NOT EXISTS `unic_time_duration`(
    `time_duration_id` VARCHAR(64) UNIQUE NOT NULL,
    `start_date` DATE NOT NULL,
    `end_date` DATE NOT NULL,
    `year` INT NOT NULL,
    `period_code` INT NOT NULL,
    PRIMARY KEY (`time_duration_id`)
);

# create table `unic_daily_table` dsl
CREATE TABLE IF NOT EXISTS `unic_daily_table`(
    `uid` VARCHAR(64) NOT NULL,
    `name` VARCHAR(64) NOT NULL,
    PRIMARY KEY (`uid`)
);

# create table `unic_daily_row` dsl
CREATE TABLE IF NOT EXISTS `unic_daily_row`(
    `uid` VARCHAR(64) NOT NULL,
    `current_index` INT NOT NULL,
    `weekdays` VARCHAR(64) NOT NULL,
    `counts` VARCHAR(64) NOT NULL,
    `daily_table_id` VARCHAR(64) NOT NULL,
    PRIMARY KEY (`uid`)
);

# create table `unic_daily_cell` dsl
CREATE TABLE IF NOT EXISTS `unic_daily_cell`(
    `uid` VARCHAR(64) NOT NULL,
    `current_index` INT NOT NULL,
    `start` TIME NOT NULL,
    `end` TIME NOT NULL,
    `normal_type` INT NOT NULL,
    `serial_index` INT NOT NULL,
    PRIMARY KEY (`uid`)
);


#endregion

#region table of student and course
# create table `unic_student_info` dsl
CREATE TABLE IF NOT EXISTS `unic_student_info`(
      `uid` VARCHAR(64) NOT NULL,
      `department_name` VARCHAR(256) NOT NULL,
      `class_name` VARCHAR(256) NOT NULL,
      `name` VARCHAR(64) NOT NULL,
      `grade` INT NOT NULL,
      PRIMARY KEY (`uid`)
);

# create table `unic_course_info` dsl
CREATE TABLE IF NOT EXISTS `unic_courses`(
    `course_id` VARCHAR(64) NOT NULL,
    `year` INT NOT NULL,
    `period_code` INT NOT NULL,
    `name` VARCHAR(256) NOT NULL,
    `campus` VARCHAR(256) NOT NULL,
    `location` VARCHAR(256) NOT NULL,
    `teacher` VARCHAR(256) NOT NULL,
    `weeks` VARCHAR(256) NOT NULL,
    `week_day` INT NOT NULL,
    `section_start` INT NOT NULL,
    `section_end` INT NOT NULL,
    `digest` VARCHAR(256) NOT NULL,
    PRIMARY KEY (`course_id`),
    INDEX `index_digest` (`digest`)
);

# create table `unic_course_student_bind` dsl
CREATE TABLE IF NOT EXISTS `unic_course_student_bind`(
    `course_id` VARCHAR(64) NOT NULL,
    `uid` VARCHAR(64) NOT NULL,
    PRIMARY KEY (`course_id`, `uid`),
    INDEX `index_course_id` (`course_id`),
    INDEX `index_uid` (`uid`)
);
#endregion


