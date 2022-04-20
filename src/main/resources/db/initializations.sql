# create at 2022/4/20
# author: h1542462994

# dialect: mysql
# database-name: dailyset_unic
# configuration localhost:3306 -u dbuser -p dbpassword

# file: declarations.sql

USE `dailyset_cloud`;

# init unic_daily_table
TRUNCATE `unic_daily_table`;
INSERT INTO `unic_daily_table` VALUES ('#default', '浙江工业大学默认时间表');

# init unic_daily_row
TRUNCATE `unic_daily_row`;
INSERT INTO `unic_daily_row` VALUES ('#default', 0, '1,2,3,4,5,6,7', '5,4,3', '#default');

# init unic_daily_cell
TRUNCATE `unic_daily_cell`;
INSERT INTO `unic_daily_cell` VALUES ('#default-0', 0, '8:00:00', '8:45:00', 0, 0);
INSERT INTO `unic_daily_cell` VALUES ('#default-1', 1, '8:55:00', '9:40:00', 0, 1);
INSERT INTO `unic_daily_cell` VALUES ('#default-2', 2, '9:55:00', '10:40:00', 0, 2);
INSERT INTO `unic_daily_cell` VALUES ('#default-3', 3, '10:50:00', '11:35:00', 0, 3);
INSERT INTO `unic_daily_cell` VALUES ('#default-4', 4, '11:45:00', '12:30:00', 0, 4);
INSERT INTO `unic_daily_cell` VALUES ('#default-5', 5, '13:30:00', '14:15:00', 1, 0);
INSERT INTO `unic_daily_cell` VALUES ('#default-6', 6, '14:25:00', '15:10:00', 1, 1);
INSERT INTO `unic_daily_cell` VALUES ('#default-7', 7, '15:25:00', '16:10:00', 1, 2);
INSERT INTO `unic_daily_cell` VALUES ('#default-8', 8, '16:20:00', '17:05:00', 1, 3);
INSERT INTO `unic_daily_cell` VALUES ('#default-9', 9, '18:30:00', '19:15:00', 2, 0);
INSERT INTO `unic_daily_cell` VALUES ('#default-10', 10, '19:25:00', '20:10:00', 2, 1);
INSERT INTO `unic_daily_cell` VALUES ('#default-11', 11, '20:25:00', '21:10:00', 2, 2);

# init unic_time_duration
TRUNCATE `unic_time_duration`;
INSERT INTO `unic_time_duration` VALUES ('#2018-1', '2018-9-1', '2018-12-31', 2018, 1);
INSERT INTO `unic_time_duration` VALUES ('#2018-2', '2019-3-1', '2019-6-30', 2018, 7);
INSERT INTO `unic_time_duration` VALUES ('#2019-1', '2019-9-1', '2019-12-31', 2019, 1);
INSERT INTO `unic_time_duration` VALUES ('#2019-2', '2020-3-1', '2020-6-30', 2019, 7);
INSERT INTO `unic_time_duration` VALUES ('#2020-1', '2020-9-1', '2020-12-31', 2020, 1);
INSERT INTO `unic_time_duration` VALUES ('#2020-2', '2021-3-1', '2021-6-30', 2020, 7);
INSERT INTO `unic_time_duration` VALUES ('#2021-1', '2021-9-1', '2021-12-31', 2021, 1);
INSERT INTO `unic_time_duration` VALUES ('#2021-2', '2022-3-1', '2022-6-30', 2021, 7);
INSERT INTO `unic_time_duration` VALUES ('#2022-1', '2022-9-1', '2022-12-31', 2022, 1);
INSERT INTO `unic_time_duration` VALUES ('#2022-2', '2023-3-1', '2023-6-30', 2022, 7);
