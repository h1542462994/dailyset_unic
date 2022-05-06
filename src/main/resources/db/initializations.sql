# create at 2022/4/20
# author: h1542462994

# dialect: mysql
# database-name: dailyset_unic
# configuration localhost:3306 -u dbuser -p dbpassword

# file: declarations.sql

USE `dailyset_unic`;

# init unic_daily_table
INSERT INTO `dailyset_school_info_meta` (meta_uid, `identifier`, `name`) VALUES ('#school.zjut', 'zjut', '浙江工业大学');

INSERT INTO `dailyset` (uid, type, source_version, matte_version, meta_version)
VALUES ('#school.zjut.unic', 4, 1, 1, 1);

INSERT INTO `dailyset_duration` (source_uid, type, start_date, end_date, name, tag, binding_year, binding_period_code)
VALUES ('#school.zjut.2018-1', 1, '2018-9-1', '2018-12-31', '2018-2019上学期', '', 2018, 1);
INSERT INTO `dailyset_duration` (source_uid, type, start_date, end_date, name, tag, binding_year, binding_period_code)
VALUES ('#school.zjut.2018-2', 1, '2019-3-1', '2019-6-30', '2018-2019下学期', '', 2018, 7);
INSERT INTO `dailyset_duration` (source_uid, type, start_date, end_date, name, tag, binding_year, binding_period_code)
VALUES ('#school.zjut.2019-1', 1, '2019-9-1', '2019-12-31', '2019-2020上学期', '', 2019, 1);
INSERT INTO `dailyset_duration` (source_uid, type, start_date, end_date, name, tag, binding_year, binding_period_code)
VALUES ('#school.zjut.2019-2', 1, '2020-3-1', '2020-6-30', '2019-2020下学期', '', 2019, 7);
INSERT INTO `dailyset_duration` (source_uid, type, start_date, end_date, name, tag, binding_year, binding_period_code)
VALUES ('#school.zjut.2020-1', 1, '2020-9-1', '2020-12-31', '2020-2021上学期', '', 2020, 1);
INSERT INTO `dailyset_duration` (source_uid, type, start_date, end_date, name, tag, binding_year, binding_period_code)
VALUES ('#school.zjut.2020-2', 1, '2021-3-1', '2021-6-30', '2020-2021下学期', '', 2020, 7);
INSERT INTO `dailyset_duration` (source_uid, type, start_date, end_date, name, tag, binding_year, binding_period_code)
VALUES ('#school.zjut.2021-1', 1, '2021-9-1', '2021-12-31', '2021-2022上学期', '', 2021, 1);
INSERT INTO `dailyset_duration` (source_uid, type, start_date, end_date, name, tag, binding_year, binding_period_code)
VALUES ('#school.zjut.2021-2', 1, '2022-3-1', '2022-6-30', '2021-2022下学期', '', 2021, 7);
INSERT INTO `dailyset_duration` (source_uid, type, start_date, end_date, name, tag, binding_year, binding_period_code)
VALUES ('#school.zjut.2022-1', 1, '2022-9-1', '2022-12-21', '2022-2023上学期', '', 2022, 1);
INSERT INTO `dailyset_duration` (source_uid, type, start_date, end_date, name, tag, binding_year, binding_period_code)
VALUES ('#school.zjut.2022-2', 1, '2023-3-1', '2023-6-30', '2022-2023下学期', '', 2022, 7);

INSERT INTO `dailyset_source_links` (dailyset_uid, source_type, source_uid, insert_version, update_version, remove_version)
VALUES ('#school.zjut.unic', 4, '#school.zjut.2018-1', 1, 1, 0);
INSERT INTO `dailyset_source_links` (dailyset_uid, source_type, source_uid, insert_version, update_version, remove_version)
VALUES ('#school.zjut.unic', 4, '#school.zjut.2018-2', 1, 1, 0);
INSERT INTO `dailyset_source_links` (dailyset_uid, source_type, source_uid, insert_version, update_version, remove_version)
VALUES ('#school.zjut.unic', 4, '#school.zjut.2019-1', 1, 1, 0);
INSERT INTO `dailyset_source_links` (dailyset_uid, source_type, source_uid, insert_version, update_version, remove_version)
VALUES ('#school.zjut.unic', 4, '#school.zjut.2019-2', 1, 1, 0);
INSERT INTO `dailyset_source_links` (dailyset_uid, source_type, source_uid, insert_version, update_version, remove_version)
VALUES ('#school.zjut.unic', 4, '#school.zjut.2020-1', 1, 1, 0);
INSERT INTO `dailyset_source_links` (dailyset_uid, source_type, source_uid, insert_version, update_version, remove_version)
VALUES ('#school.zjut.unic', 4, '#school.zjut.2020-2', 1, 1, 0);
INSERT INTO `dailyset_source_links` (dailyset_uid, source_type, source_uid, insert_version, update_version, remove_version)
VALUES ('#school.zjut.unic', 4, '#school.zjut.2021-1', 1, 1, 0);
INSERT INTO `dailyset_source_links` (dailyset_uid, source_type, source_uid, insert_version, update_version, remove_version)
VALUES ('#school.zjut.unic', 4, '#school.zjut.2022-2', 1, 1, 0);
INSERT INTO `dailyset_meta_links` (dailyset_uid, meta_type, meta_uid, insert_version, update_version, remove_version)
VALUES ('#school.zjut.unic', 4, '#school.zjut', 1, 1, 0);
