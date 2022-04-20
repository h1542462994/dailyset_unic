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


# create table `ticket` dsl
# ticket is a binder for student account
CREATE TABLE IF NOT EXISTS `unic_ticket`(
    `ticket_id` VARCHAR(64) UNIQUE NOT NULL,
    `uid` VARCHAR(64) NOT NULL,
    `password` VARCHAR(256) NOT NULL,
    `status` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`ticket_id`)
);

#region table of time and daily_set
# create table `term_duration` dsl
CREATE TABLE IF NOT EXISTS `unic_time_duration`(
    `time_duration_id` VARCHAR(64) UNIQUE NOT NULL,
    `start_date` DATE NOT NULL,
    `end_date` DATE NOT NULL,
    `year` INT NOT NULL,
    `period_code` INT NOT NULL,
    PRIMARY KEY (`time_duration_id`)
);

# create table `daily_table` dsl
CREATE TABLE IF NOT EXISTS `unic_daily_table`(
    `uid` VARCHAR(64) NOT NULL,
    `name` VARCHAR(64) NOT NULL,
    PRIMARY KEY (`uid`)
);

# create table `daily_row` dsl
CREATE TABLE IF NOT EXISTS `unic_daily_row`(
    `uid` VARCHAR(64) NOT NULL,
    `current_index` INT NOT NULL,
    `weekdays` VARCHAR(64) NOT NULL,
    `counts` VARCHAR(64) NOT NULL,
    `daily_table_id` VARCHAR(64) NOT NULL,
    PRIMARY KEY (`uid`)
);

# create table `daily_cell` dsl
CREATE TABLE IF NOT EXISTS `unic_daily_cell`(
    `uid` VARCHAR(64) NOT NULL,
    `current_index` INT NOT NULL,
    `start` TIME NOT NULL,
    `end` TIME NOT NULL,
    `normal_type` INT NOT NULL,
    `serial_index` INT NOT NULL
);
#endregion



