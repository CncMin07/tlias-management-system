-- ============================================================================
--  学生部门管理系统（Tlias 教务管理后台）数据库初始化脚本
--  数据库：MySQL 8.x
--
--  用法（cmd / PowerShell）：
--      mysql -uroot -p < sql/init.sql
--  或在 Navicat / DataGrip 里打开本文件整段执行。
--
--  ⚠️ 警告：本脚本会先 DROP 掉 emp_expr / emp_log / student / clazz / emp / dept 六张表，
--     然后重建并写入示例数据。本地如果已经有一个正在用的 tlias 库，请务必先备份：
--         mysqldump -uroot -p tlias > tlias_backup.sql
--     或者先把脚本里的 DROP TABLE 语句全部注释掉再执行。
--
--  说明：
--    1. 表结构依据项目中的 Mapper 接口与 XML 映射文件反推得出。
--    2. 表之间不建外键约束 —— 业务代码（如删除部门、批量删除员工）并未做级联
--       校验，加外键反而会让正常删除报错，因此只在关联列上建普通索引。
--    3. 唯一约束（emp.username / student.no / dept.name / clazz.name）按业务语义
--       补的，配合全局异常处理器里对 DuplicateKeyException 的友好提示。
-- ============================================================================

CREATE DATABASE IF NOT EXISTS `tlias`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE `tlias`;

DROP TABLE IF EXISTS `emp_expr`;
DROP TABLE IF EXISTS `emp_log`;
DROP TABLE IF EXISTS `student`;
DROP TABLE IF EXISTS `clazz`;
DROP TABLE IF EXISTS `emp`;
DROP TABLE IF EXISTS `dept`;


-- ---------------------------------------------------------------------------
-- 部门表
-- ---------------------------------------------------------------------------
CREATE TABLE `dept`
(
    `id`          INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`        VARCHAR(20)  NOT NULL COMMENT '部门名称',
    `create_time` DATETIME     NOT NULL COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dept_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='部门表';


-- ---------------------------------------------------------------------------
-- 员工表
--   gender: 1 男, 2 女
--   job:    1 班主任, 2 讲师, 3 学工主管, 4 教研主管, 5 咨询师
-- ---------------------------------------------------------------------------
CREATE TABLE `emp`
(
    `id`          INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    VARCHAR(20)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(32)  NOT NULL COMMENT '密码',
    `name`        VARCHAR(10)  NOT NULL COMMENT '姓名',
    `gender`      TINYINT      NOT NULL COMMENT '性别, 1 男, 2 女',
    `phone`       VARCHAR(11)  NOT NULL COMMENT '手机号',
    `job`         TINYINT      NOT NULL COMMENT '职位, 1 班主任, 2 讲师, 3 学工主管, 4 教研主管, 5 咨询师',
    `salary`      INT          NULL COMMENT '薪资',
    `image`       VARCHAR(255) NULL COMMENT '头像地址',
    `entry_date`  DATE         NOT NULL COMMENT '入职日期',
    `dept_id`     INT UNSIGNED NULL COMMENT '部门ID',
    `create_time` DATETIME     NOT NULL COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_emp_username` (`username`),
    KEY `idx_emp_dept_id` (`dept_id`),
    KEY `idx_emp_entry_date` (`entry_date`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='员工表';


-- ---------------------------------------------------------------------------
-- 员工工作经历表（与 emp 一对多）
-- ---------------------------------------------------------------------------
CREATE TABLE `emp_expr`
(
    `id`      INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `emp_id`  INT UNSIGNED NOT NULL COMMENT '员工ID',
    `begin`   DATE         NOT NULL COMMENT '开始时间',
    `end`     DATE         NOT NULL COMMENT '结束时间',
    `company` VARCHAR(50)  NULL COMMENT '公司名称',
    `job`     VARCHAR(50)  NULL COMMENT '职位',
    PRIMARY KEY (`id`),
    KEY `idx_emp_expr_emp_id` (`emp_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='员工工作经历表';


-- ---------------------------------------------------------------------------
-- 员工操作日志表（新增员工时记录，独立事务写入）
-- ---------------------------------------------------------------------------
CREATE TABLE `emp_log`
(
    `id`           INT UNSIGNED   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `operate_time` DATETIME       NOT NULL COMMENT '操作时间',
    `info`         VARCHAR(2000)  NULL COMMENT '日志信息',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='员工操作日志表';


-- ---------------------------------------------------------------------------
-- 班级表
--   subject:  1 Java, 2 前端, 3 大数据, 4 Python, 5 测试
--   master_id 指向 emp.id（班主任）
--   status 字段不入库，由后端按当前日期实时计算（未开班 / 在读中 / 已结课）
-- ---------------------------------------------------------------------------
CREATE TABLE `clazz`
(
    `id`          INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`        VARCHAR(30)  NOT NULL COMMENT '班级名称',
    `room`        VARCHAR(20)  NULL COMMENT '班级教室',
    `begin_date`  DATE         NOT NULL COMMENT '开课时间',
    `end_date`    DATE         NOT NULL COMMENT '结课时间',
    `master_id`   INT UNSIGNED NULL COMMENT '班主任ID',
    `subject`     TINYINT      NULL COMMENT '学科, 1 Java, 2 前端, 3 大数据, 4 Python, 5 测试',
    `create_time` DATETIME     NOT NULL COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_clazz_name` (`name`),
    KEY `idx_clazz_master_id` (`master_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='班级表';


-- ---------------------------------------------------------------------------
-- 学员表
--   gender:     1 男, 2 女
--   is_college: 1 来自院校, 0 否
--   degree:     1 初中, 2 高中, 3 大专, 4 本科, 5 硕士, 6 博士
-- ---------------------------------------------------------------------------
CREATE TABLE `student`
(
    `id`              INT UNSIGNED     NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name`            VARCHAR(10)      NOT NULL COMMENT '姓名',
    `no`              VARCHAR(10)      NOT NULL COMMENT '学号',
    `gender`          TINYINT          NOT NULL COMMENT '性别, 1 男, 2 女',
    `phone`           VARCHAR(11)      NOT NULL COMMENT '手机号',
    `id_card`         VARCHAR(18)      NULL COMMENT '身份证号',
    `is_college`      TINYINT          NULL DEFAULT 0 COMMENT '是否来自院校, 1 是, 0 否',
    `address`         VARCHAR(100)     NULL COMMENT '联系地址',
    `degree`          TINYINT          NULL COMMENT '最高学历, 1 初中, 2 高中, 3 大专, 4 本科, 5 硕士, 6 博士',
    `graduation_date` DATE             NULL COMMENT '毕业时间',
    `clazz_id`        INT UNSIGNED     NULL COMMENT '班级ID',
    `violation_count` SMALLINT UNSIGNED NULL DEFAULT 0 COMMENT '违纪次数',
    `violation_score` SMALLINT UNSIGNED NULL DEFAULT 0 COMMENT '违纪扣分',
    `create_time`     DATETIME         NOT NULL COMMENT '创建时间',
    `update_time`     DATETIME         NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_no` (`no`),
    KEY `idx_student_clazz_id` (`clazz_id`),
    KEY `idx_student_degree` (`degree`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='学员表';


-- ============================================================================
--  示例数据
--  登录账号：admin / 123456 （密码为明文比对，见 EmpMapper.selectByUsernameAndPassword）
-- ============================================================================

INSERT INTO `dept` (`id`, `name`, `create_time`, `update_time`)
VALUES (1, '教研部', '2026-01-05 10:00:00', '2026-01-05 10:00:00'),
       (2, '学工部', '2026-01-05 10:00:00', '2026-01-05 10:00:00'),
       (3, '咨询部', '2026-01-05 10:00:00', '2026-01-05 10:00:00'),
       (4, '行政部', '2026-01-05 10:00:00', '2026-01-05 10:00:00');

INSERT INTO `emp` (`id`, `username`, `password`, `name`, `gender`, `phone`, `job`, `salary`, `image`,
                   `entry_date`, `dept_id`, `create_time`, `update_time`)
VALUES (1, 'admin', '123456', '管理员', 1, '13800000001', 3, 14000, NULL, '2020-01-05', 4, '2026-01-05 10:00:00', '2026-09-01 09:00:00'),
       (2, 'zhangsan', '123456', '张三', 1, '13800000002', 1, 8000, NULL, '2023-03-01', 2, '2026-01-05 10:00:00', '2026-09-02 09:00:00'),
       (3, 'lisi', '123456', '李四', 1, '13800000003', 2, 12000, NULL, '2022-07-15', 1, '2026-01-05 10:00:00', '2026-09-03 09:00:00'),
       (4, 'wangwu', '123456', '王五', 2, '13800000004', 2, 11000, NULL, '2023-01-10', 1, '2026-01-05 10:00:00', '2026-09-04 09:00:00'),
       (5, 'zhaoliu', '123456', '赵六', 2, '13800000005', 3, 13000, NULL, '2021-09-01', 2, '2026-01-05 10:00:00', '2026-09-05 09:00:00'),
       (6, 'sunqi', '123456', '孙七', 1, '13800000006', 4, 15000, NULL, '2020-06-20', 1, '2026-01-05 10:00:00', '2026-09-06 09:00:00'),
       (7, 'zhouba', '123456', '周八', 2, '13800000007', 5, 9000, NULL, '2023-05-08', 3, '2026-01-05 10:00:00', '2026-09-07 09:00:00'),
       (8, 'wujiu', '123456', '吴九', 1, '13800000008', 5, 8500, NULL, '2023-08-01', 3, '2026-01-05 10:00:00', '2026-09-08 09:00:00'),
       (9, 'zhengshi', '123456', '郑十', 2, '13800000009', 1, 8200, NULL, '2024-02-19', 2, '2026-01-05 10:00:00', '2026-09-09 09:00:00'),
       (10, 'chenyi', '123456', '陈一', 2, '13800000010', 1, 7800, NULL, '2024-06-11', 2, '2026-01-05 10:00:00', '2026-09-10 09:00:00'),
       (11, 'qianer', '123456', '钱二', 1, '13800000011', 2, 10500, NULL, '2023-11-02', 1, '2026-01-05 10:00:00', '2026-09-11 09:00:00');

INSERT INTO `emp_expr` (`emp_id`, `begin`, `end`, `company`, `job`)
VALUES (3, '2018-07-01', '2020-06-30', '杭州某某科技有限公司', 'Java 开发工程师'),
       (3, '2020-07-01', '2022-06-30', '上海某某网络科技有限公司', '高级 Java 开发工程师'),
       (4, '2019-03-01', '2023-01-05', '南京某某软件有限公司', '前端开发工程师'),
       (5, '2017-09-01', '2021-08-31', '北京某某教育科技有限公司', '学工主管'),
       (11, '2019-07-15', '2023-10-31', '深圳某某信息技术有限公司', 'Java 讲师');

INSERT INTO `emp_log` (`operate_time`, `info`)
VALUES ('2026-09-01 09:00:00', '新增员工：Emp(id=10, username=chenyi, name=陈一)'),
       ('2026-09-02 09:00:00', '新增员工：Emp(id=11, username=qianer, name=钱二)');

-- 班级状态（未开班 / 在读中 / 已结课）由后端按当前日期计算，
-- 下面日期以 2026-09 为基准构造，方便看到三种状态。
INSERT INTO `clazz` (`id`, `name`, `room`, `begin_date`, `end_date`, `master_id`, `subject`, `create_time`, `update_time`)
VALUES (1, 'Java就业班-125期', '301', '2026-03-01', '2026-11-30', 2, 1, '2026-01-10 10:00:00', '2026-08-01 10:00:00'),
       (2, 'Java就业班-126期', '302', '2026-08-01', '2027-04-30', 9, 1, '2026-07-05 10:00:00', '2026-08-05 10:00:00'),
       (3, '前端就业班-118期', '303', '2026-04-10', '2026-12-20', 10, 2, '2026-02-20 10:00:00', '2026-08-10 10:00:00'),
       (4, '大数据就业班-042期', '401', '2025-09-01', '2026-05-30', 6, 3, '2025-07-15 10:00:00', '2026-06-01 10:00:00'),
       (5, 'Python就业班-020期', '402', '2026-11-01', '2027-07-31', 1, 4, '2026-09-01 10:00:00', '2026-09-01 10:00:00'),
       (6, '测试开发班-008期', '403', '2026-06-15', '2027-02-28', 5, 5, '2026-05-06 10:00:00', '2026-08-20 10:00:00'),
       (7, '软件测试班-009期', '404', '2026-10-08', '2027-06-30', NULL, 5, '2026-09-10 10:00:00', '2026-09-10 10:00:00');

INSERT INTO `student` (`name`, `no`, `gender`, `phone`, `id_card`, `is_college`, `address`, `degree`,
                       `graduation_date`, `clazz_id`, `violation_count`, `violation_score`,
                       `create_time`, `update_time`)
VALUES ('刘一', 'S2026001', 1, '13900000001', '330102200401011234', 1, '浙江省杭州市西湖区', 4, '2024-06-30', 1, 0, 0, '2026-03-01 10:00:00', '2026-09-01 10:00:00'),
       ('陈二', 'S2026002', 2, '13900000002', '330102200402021234', 1, '浙江省宁波市鄞州区', 4, '2024-06-30', 1, 1, 2, '2026-03-01 10:00:00', '2026-09-02 10:00:00'),
       ('张三', 'S2026003', 1, '13900000003', '330102200403031234', 0, '江苏省南京市鼓楼区', 3, '2023-06-30', 1, 0, 0, '2026-03-01 10:00:00', '2026-09-03 10:00:00'),
       ('李四', 'S2026004', 2, '13900000004', '330102200404041234', 1, '安徽省合肥市蜀山区', 5, '2025-06-30', 1, 0, 0, '2026-03-01 10:00:00', '2026-09-04 10:00:00'),
       ('王五', 'S2026005', 1, '13900000005', '330102200405051234', 1, '浙江省温州市鹿城区', 4, '2024-06-30', 2, 0, 0, '2026-08-01 10:00:00', '2026-09-05 10:00:00'),
       ('赵六', 'S2026006', 2, '13900000006', '330102200406061234', 0, '江西省南昌市青山湖区', 3, '2023-06-30', 2, 2, 6, '2026-08-01 10:00:00', '2026-09-06 10:00:00'),
       ('孙七', 'S2026007', 1, '13900000007', '330102200407071234', 1, '浙江省杭州市滨江区', 4, '2024-06-30', 2, 0, 0, '2026-08-01 10:00:00', '2026-09-07 10:00:00'),
       ('周八', 'S2026008', 2, '13900000008', '330102200408081234', 1, '湖北省武汉市洪山区', 2, '2022-06-30', 3, 0, 0, '2026-04-10 10:00:00', '2026-09-08 10:00:00'),
       ('吴九', 'S2026009', 1, '13900000009', '330102200409091234', 0, '湖南省长沙市岳麓区', 3, '2023-06-30', 3, 1, 3, '2026-04-10 10:00:00', '2026-09-09 10:00:00'),
       ('郑十', 'S2026010', 2, '13900000010', '330102200410101234', 1, '浙江省绍兴市越城区', 4, '2024-06-30', 3, 0, 0, '2026-04-10 10:00:00', '2026-09-10 10:00:00'),
       ('陈十一', 'S2026011', 1, '13900000011', '330102200411111234', 1, '福建省福州市鼓楼区', 5, '2025-06-30', 4, 0, 0, '2025-09-01 10:00:00', '2026-05-30 10:00:00'),
       ('钱十二', 'S2026012', 2, '13900000012', '330102200412121234', 0, '河南省郑州市金水区', 3, '2023-06-30', 4, 0, 0, '2025-09-01 10:00:00', '2026-05-30 10:00:00'),
       ('林十三', 'S2026013', 1, '13900000013', '330102200413131234', 1, '浙江省嘉兴市南湖区', 4, '2024-06-30', 6, 0, 0, '2026-06-15 10:00:00', '2026-09-11 10:00:00'),
       ('黄十四', 'S2026014', 2, '13900000014', '330102200414141234', 1, '广东省广州市天河区', 5, '2025-06-30', 6, 0, 0, '2026-06-15 10:00:00', '2026-09-12 10:00:00'),
       ('徐十五', 'S2026015', 1, '13900000015', '330102200415151234', 0, '上海市浦东新区', 3, '2023-06-30', 6, 3, 9, '2026-06-15 10:00:00', '2026-09-13 10:00:00'),
       ('何十六', 'S2026016', 2, '13900000016', '330102200416161234', 1, '浙江省台州市椒江区', 4, '2024-06-30', NULL, 0, 0, '2026-09-15 10:00:00', '2026-09-15 10:00:00');
