# 学生部门管理系统 · Tlias 教务管理后台

> 基于 Spring Boot + MyBatis 的教务管理后端服务，覆盖部门、员工、班级、学员、统计报表五大模块，
> 提供统一的 RESTful 接口、分页查询、声明式事务与阿里云 OSS 文件上传能力。

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen)
![MyBatis](https://img.shields.io/badge/MyBatis-4.0.1-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

---

## 一、项目简介

本项目是一个面向教育培训机构的**教务管理后台**后端服务。业务上把「机构组织架构」（部门 → 员工）和
「教学组织」（班级 → 学员）两条主线打通，并在此基础上提供多维度统计报表，供前端图表展示。

实现上严格按照 `Controller → Service → Mapper → DB` 分层组织，接口统一返回 `Result` 包装体，
分页统一返回 `PageResult<T>`，异常统一由全局异常处理器兜底收敛，接口风格遵循 RESTful 语义。

## 二、功能模块

| 模块 | 功能点 |
| --- | --- |
| **部门管理** | 部门列表、新增、修改、删除、按 ID 回显 |
| **员工管理** | 分页条件查询（姓名模糊 + 性别 + 入职日期区间）、新增、修改、批量删除、按 ID 查询详情（含工作经历）、员工下拉列表 |
| **员工工作经历** | 与员工一对多，新增/修改员工时批量写入、批量删除，一次联表查询完成嵌套装配 |
| **员工操作日志** | 新增员工时记录操作日志，用独立事务写入，主业务回滚不影响日志落库 |
| **班级管理** | 分页条件查询（班级名 + 结课时间区间）、新增、修改、删除、按 ID 查询、班级下拉列表；班级状态按当前日期实时计算 |
| **学员管理** | 分页条件查询（姓名模糊 + 学历 + 班级）、新增、修改、批量删除、按 ID 查询、违纪处理（累计违纪次数与扣分） |
| **统计报表** | 员工职位分布、员工性别分布、学员学历分布、各班级学员人数 |
| **登录 & 文件上传** | 员工账号登录；头像/附件上传至阿里云 OSS，按 `yyyy/MM/UUID.ext` 生成对象名 |
| **公共能力** | 统一响应体、统一分页体、全局异常处理、参数自动绑定、SQL 与事务日志 |

## 三、技术栈

| 类别 | 选型 | 版本 |
| --- | --- | --- |
| 语言 / 运行时 | Java | 17 |
| 应用框架 | Spring Boot（`spring-boot-starter-webmvc`、`spring-boot-starter-actuator`） | 4.0.6 |
| 持久层框架 | MyBatis（注解 + XML 双写法）、`mybatis-spring-boot-starter` | 4.0.1 |
| 分页插件 | PageHelper（`pagehelper-spring-boot-starter`） | 1.4.7 |
| 数据库 | MySQL（`mysql-connector-j`） | 8.x |
| 对象存储 | 阿里云 OSS（`aliyun-sdk-oss`） | 3.18.4 |
| 工具 | Lombok、Logback | — |
| 构建 | Maven | 3.9+ |

## 四、系统架构

### 4.1 分层结构

```
                        ┌──────────────────────────────────────┐
   HTTP / JSON          │  Controller 层                        │
  ───────────────────▶  │  DeptController / EmpController /     │
                        │  ClazzController / StudentController /│
                        │  ReportController / LoginController / │
                        │  UploadController                     │
                        └───────────────┬──────────────────────┘
                                        │ 请求参数绑定
                        ┌───────────────▼──────────────────────┐
                        │  Service 层（接口 + impl 实现）        │
                        │  业务编排：分页、事务、时间戳补全、     │
                        │  状态计算、多表数据组装                │
                        └───────────────┬──────────────────────┘
                                        │
                        ┌───────────────▼──────────────────────┐
                        │  Mapper 层（MyBatis 接口）            │
                        │  注解式单表 CRUD + XML 动态 SQL /     │
                        │  resultMap 嵌套映射                   │
                        └───────────────┬──────────────────────┘
                                        │
                        ┌───────────────▼──────────────────────┐
                        │  MySQL 8.x（tlias 库，6 张表）         │
                        └──────────────────────────────────────┘

      横向切面：GlobalExceptionHandler（全局异常兜底）
                Result / PageResult<T>（统一出参）
                AliyunOSSOperator（对象存储）
```

### 4.2 一次分页查询的完整流转（以 `GET /emps` 为例）

1. `EmpQueryParam` 自动绑定查询串中的 `page / pageSize / name / gender / begin / end`，并给出默认值 `page=1, pageSize=10`；
2. `EmpServiceImpl` 调用 `PageHelper.startPage(...)` 开启分页（分页参数写入 ThreadLocal）；
3. `EmpMapper.list()` 执行 XML 中的动态 SQL，`<where>` + `<if>` 按需拼条件，`left join dept` 带出部门名称；
4. PageHelper 拦截本次查询，自动改写为 `limit` 语句并额外查一次 `count(*)`，返回值被包装成 `Page<Emp>`；
5. Service 强转拿到 `total` 与 `result`，封装为 `PageResult<Emp>`；
6. Controller 用 `Result.success(...)` 包一层，最终响应 `{ "code": 1, "msg": "success", "data": { "total": ..., "rows": [...] } }`。

## 五、接口一览

统一返回体：`{ "code": 1|0, "msg": "success|错误信息", "data": ... }`，`code=1` 成功、`code=0` 失败。
统一分页体：`{ "total": 总记录数, "rows": [ ... ] }`。

### 部门 `/depts`

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/depts` | 查询全部部门 |
| GET | `/depts/{id}` | 按 ID 查询部门（修改回显） |
| POST | `/depts` | 新增部门（JSON body） |
| PUT | `/depts` | 修改部门（JSON body） |
| DELETE | `/depts?id=1` | 删除部门 |

### 员工 `/emps`

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/emps?page=1&pageSize=10&name=&gender=&begin=&end=` | 分页条件查询 |
| GET | `/emps/{id}` | 查询员工详情（含工作经历列表） |
| GET | `/emps/list` | 查询全部员工（下拉框用） |
| POST | `/emps` | 新增员工（同时写入工作经历） |
| PUT | `/emps` | 修改员工（工作经历先删后插） |
| DELETE | `/emps?ids=1,2,3` | 批量删除员工 |

### 班级 `/clazzs`

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/clazzs?page=1&pageSize=10&name=&begin=&end=` | 分页条件查询（`begin/end` 作用于结课时间） |
| GET | `/clazzs/{id}` | 按 ID 查询班级 |
| GET | `/clazzs/list` | 查询全部班级 |
| POST | `/clazzs` | 新增班级 |
| PUT | `/clazzs` | 修改班级 |
| DELETE | `/clazzs/{id}` | 删除班级 |

### 学员 `/students`

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/students?page=1&pageSize=10&name=&degree=&clazzId=` | 分页条件查询 |
| GET | `/students/{id}` | 按 ID 查询学员 |
| POST | `/students` | 新增学员 |
| PUT | `/students` | 修改学员 |
| DELETE | `/students/{ids}` | 批量删除（路径内逗号分隔，如 `/students/1,2`） |
| PUT | `/students/violation/{id}/{score}` | 违纪处理：次数 +1、扣分累加 |

### 报表 `/report`

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/report/empJobData` | 员工职位分布 → `{ jobList: [], dataList: [] }` |
| GET | `/report/empGenderData` | 员工性别分布 → `[{ name, value }]` |
| GET | `/report/studentDegreeData` | 学员学历分布 → `[{ name, value }]` |
| GET | `/report/studentCountData` | 各班级学员人数 → `{ clazzList: [], dataList: [] }` |

### 其它

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/login` | 员工登录，body `{ "username": "...", "password": "..." }`，成功返回 `LoginInfo`（`id / username / password / token`，其中 `token` 恒为空串，详见「已知不足」） |
| POST | `/upload` | 文件上传，`multipart/form-data`，字段名 `file`，返回文件可访问 URL |

> 说明：`/c1`、`/c2`、`/s1`、`/s2` 是学习 Cookie 与 HttpSession 时留下的演示接口，非业务接口。

## 六、关键技术实现

### 1. 统一响应体与统一异常兜底

`Result<T>` 把「成功/失败 + 提示信息 + 数据」收敛成一种结构，前端只需判断 `code`。
`GlobalExceptionHandler` 用 `@RestControllerAdvice` 拦截所有异常，避免异常堆栈直接泄漏到响应里；
并对 `DuplicateKeyException`（唯一键冲突）单独处理，从原始报错信息中截取出重复的值，
拼成 `"XXX 已存在"` 这种用户能看懂的提示：

```java
@ExceptionHandler
public Result handleDuplicateKeyException(DuplicateKeyException e) {
    String message = e.getMessage();
    int i = message.indexOf("Duplicate entry");
    String[] arr = message.substring(i).split(" ");   // Duplicate entry 'xxx' for key ...
    return Result.error(arr[2] + " 已存在");            // → 'xxx' 已存在
}
```

### 2. 声明式事务 + `REQUIRES_NEW` 写操作日志

新增员工要同时写 `emp` 和 `emp_expr` 两张表，任一步失败都应整体回滚，因此用
`@Transactional(rollbackFor = Exception.class)`；注意这里显式指定了 `rollbackFor`——
默认只在 `RuntimeException` 时回滚，加上它才能覆盖受检异常。

而「操作日志」的诉求恰好相反：**员工新增失败时，日志也必须留下来**。
所以日志方法单独标注了 `Propagation.REQUIRES_NEW`，挂起外层事务、自己开一个新事务提交：

```java
@Transactional(rollbackFor = Exception.class)
public void save(Emp emp) {
    try {
        empMapper.insert(emp);
        // ... 批量保存工作经历
    } finally {
        // 挂起当前事务，用新事务记录日志，不受主业务回滚影响
        empLogService.insertLog(new EmpLog(null, LocalDateTime.now(), "新增员工：" + emp));
    }
}

// EmpLogServiceImpl
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
public void insertLog(EmpLog empLog) { empLogMapper.insert(empLog); }
```

### 3. MyBatis 动态 SQL：`<where>` / `<set>` / `<foreach>`

- **条件查询**：`<where>` 会自动去掉多余的 `and`，配合 `<if>` 实现「有几个条件就拼几个条件」，
  姓名走 `concat('%', #{name}, '%')` 模糊匹配；
- **局部更新**：`<set>` + `<if>` 实现「只更新传了值的字段」，避免把未传参的字段刷成 `null`；
- **批量操作**：`<foreach>` 拼出 `in (?,?,?)` 与批量 `insert ... values (...),(...)`，
  一次网络往返完成多条写入，比循环单条插入少很多次数据库交互。

### 4. `resultMap` + `<collection>` 一对多嵌套映射

查员工详情时，员工基本信息与他的多段工作经历分属两张表。与其查两次再在 Java 里拼装，
不如用一条 `left join` 拿到全部数据，交给 `<resultMap>` 的 `<collection>` 按 ID 自动折叠成对象树：

```xml
<resultMap id="empResultMap" type="com.fallenleaves.pojo.Emp">
    <id column="id" property="id"/>
    <!-- ... 员工基本字段 ... -->
    <collection property="exprList" ofType="com.fallenleaves.pojo.EmpExpr">
        <id column="ee_id" property="id"/>
        <result column="ee_empid" property="empId"/>
        <result column="ee_company" property="company"/>
        <!-- ... -->
    </collection>
</resultMap>
```

### 5. 文件上传走阿里云 OSS，不落本地磁盘

本地存文件在容器化/多实例部署下会失效（各实例磁盘不共享），因此统一交给 OSS。
`AliyunOSSOperator` 里做了三件关键的事：

- **对象名防冲突**：`yyyy/MM/UUID.扩展名`，按月份分目录，UUID 保证同名文件不会互相覆盖；
- **凭证不落盘**：通过 `EnvironmentVariableCredentialsProvider` 从环境变量读 AccessKey，
  代码与配置文件里都不出现密钥；
- **客户端用后即关**：`OSSClient` 放在 `try ... finally` 中 `shutdown()`，避免连接泄漏。

### 6. 班级状态由后端实时计算，而不是存进数据库

「未开班 / 在读中 / 已结课」是由当前日期与开课、结课时间比较出来的**派生状态**，把它落库就必须靠
定时任务刷新，否则数据必然过期。这里改成查询时实时判断，前端拿到的一定是准确的：

```java
private String judgeStatus(LocalDate now, Clazz clazz) {
    if (now.isAfter(clazz.getEndDate()))  return "已结课";
    if (now.isBefore(clazz.getBeginDate())) return "未开班";
    return "在读中";
}
```

### 7. 统计口径放在 SQL 里，Service 只做拆分

报表数据全部由数据库聚合完成：`CASE WHEN` 把数字字典翻译成中文标签（如 `job=1 → 班主任`），
`GROUP BY` + `COUNT(*)` 出统计结果，Service 层仅用 stream 把 `List<Map>` 拆成「标签数组 + 数值数组」
两个平行数组，直接贴合 ECharts 的数据格式，避免把明细数据捞到内存里再算。

## 七、数据库设计

库名 `tlias`，共 6 张表，建表与示例数据见 [`sql/init.sql`](sql/init.sql)。

| 表名 | 说明 | 关键字段 |
| --- | --- | --- |
| `dept` | 部门 | `name`（唯一） |
| `emp` | 员工 | `username`（唯一，登录用）、`job`、`dept_id`、`entry_date` |
| `emp_expr` | 员工工作经历 | `emp_id`（→ `emp.id`，一对多） |
| `emp_log` | 员工操作日志 | `operate_time`、`info` |
| `clazz` | 班级 | `master_id`（班主任 → `emp.id`）、`begin_date`、`end_date` |
| `student` | 学员 | `no`（学号，唯一）、`clazz_id`、`degree`、`violation_count`、`violation_score` |

关系概览：

```
dept 1 ──< n emp 1 ──< n emp_expr
              │
              └──< n clazz 1 ──< n student
```

**关于外键**：脚本里只建索引、不建外键约束。业务代码在删除部门、批量删除员工时并没有做级联校验，
一旦加上外键，这些正常操作会直接抛约束异常；索引已经足够支撑关联查询的性能。

## 八、快速开始

### 8.1 环境要求

| 依赖 | 版本 |
| --- | --- |
| JDK | 17+ |
| Maven | 3.9+ |
| MySQL | 8.x |

> 提示：多个 JDK 共存时请确认 `mvn -v` 与 IDEA 项目 SDK 都指向 JDK 17，否则可能因
> 字节码版本不一致报 `UnsupportedClassVersionError`。

### 8.2 初始化数据库

```bash
mysql -uroot -p < sql/init.sql
```

脚本会自动创建 `tlias` 库、6 张表，并写入示例数据。示例登录账号：**admin / 123456**。

### 8.3 配置（密钥走环境变量）

`application.yaml` 里所有敏感项都写成 `${ENV_VAR:默认值}`，**不在仓库中保存任何真实密钥**。
可参考 [`docs/application-example.yaml`](docs/application-example.yaml)。

| 环境变量 | 默认值 | 说明 |
| --- | --- | --- |
| `DB_HOST` | `localhost` | 数据库主机 |
| `DB_PORT` | `3306` | 数据库端口 |
| `DB_NAME` | `tlias` | 数据库名 |
| `DB_USERNAME` | `root` | 数据库账号 |
| `DB_PASSWORD` | 空 | 数据库密码 |
| `OSS_ACCESS_KEY_ID` | 无 | 阿里云 AccessKey ID（变量名由 SDK 固定读取，不可改） |
| `OSS_ACCESS_KEY_SECRET` | 无 | 阿里云 AccessKey Secret（同上） |
| `OSS_ENDPOINT` | `https://oss-cn-shanghai.aliyuncs.com` | OSS 访问域名 |
| `OSS_BUCKET_NAME` | 空 | OSS Bucket 名称 |
| `OSS_REGION` | `cn-shanghai` | OSS 地域 |

Windows 下写入用户级环境变量（**新开的终端 / IDEA 才生效**）：

```bat
setx DB_PASSWORD "你的MySQL密码"
setx OSS_ACCESS_KEY_ID "你的AccessKeyId"
setx OSS_ACCESS_KEY_SECRET "你的AccessKeySecret"
setx OSS_BUCKET_NAME "你的Bucket名称"
```

也可以直接在 IDEA 的 `Run/Debug Configurations → Environment variables` 里逐条配置，改完立即生效。

> 只跑数据库相关功能（部门 / 员工 / 班级 / 学员 / 报表）时，OSS 相关变量可以留空，
> 仅「文件上传」接口（`/upload`）会因缺少凭证而失败。

### 8.4 启动

```bash
cd tlias-web-management
mvn spring-boot:run
```

或先打包再运行：

```bash
mvn clean package -DskipTests
java -jar target/tlias-web-management-0.0.1-SNAPSHOT.jar
```

服务默认监听 `http://localhost:8080`。

### 8.5 验证接口

```bash
# 1. 登录
curl -X POST http://localhost:8080/login \
     -H "Content-Type: application/json" \
     -d "{\"username\":\"admin\",\"password\":\"123456\"}"

# 2. 分页查询员工
curl "http://localhost:8080/emps?page=1&pageSize=5&name=张"

# 3. 员工职位分布（报表）
curl http://localhost:8080/report/empJobData
```

也可以直接打开项目内置的测试页 `http://localhost:8080/upload.html` 体验文件上传。

## 九、目录结构

```
web-ai-project02/
├── docs/
│   └── application-example.yaml          # 环境变量配置参考
├── sql/
│   └── init.sql                          # 建库建表 + 示例数据
├── tlias-web-management/
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/fallenleaves/
│       │   │   ├── TliasWebManagementApplication.java   # 启动类
│       │   │   ├── controller/           # 接收请求、参数绑定、返回统一结果
│       │   │   ├── service/              # 业务接口
│       │   │   │   └── impl/             # 业务实现：分页、事务、数据组装
│       │   │   ├── mapper/               # MyBatis Mapper 接口
│       │   │   ├── pojo/                 # 实体、查询参数、统一返回体
│       │   │   ├── exception/            # 全局异常处理器
│       │   │   └── utils/                # 阿里云 OSS 工具与配置绑定
│       │   └── resources/
│       │       ├── application.yaml      # 主配置（敏感项走环境变量）
│       │       ├── logback.xml           # 日志格式
│       │       ├── com/fallenleaves/mapper/*.xml   # XML 动态 SQL 与 resultMap
│       │       └── static/upload.html    # 上传功能测试页
│       └── test/                         # OSS / 日志等试验代码
├── .gitignore
├── LICENSE
└── README.md
```

## 十、已知不足与后续优化

诚恳记录当前实现上的短板，也是后续要补的功课：

- **登录未做鉴权链路**：`/login` 只是明文比对后返回用户信息，返回的 `token` 是空串；
  没有 JWT 生成与校验，也没有拦截器保护业务接口，任何人都能直接调 `/emps` 等接口。
  计划引入 ThreadLocal 保存当前登录用户 + 拦截器统一校验。
- **`LoginInfo` 字段语义错位且回传密码字段**：`LoginInfo` 的字段是
  `id / username / password / token`，而 `EmpServiceImpl.login()` 里构造时传的第三个参数是**姓名**，
  于是姓名被塞进了 `password` 字段返回给前端，接口语义与实际内容不一致。
  同时响应体里出现 `password` 字段本身也不合适，应改为 `id / username / name / token`。
- **密码明文存储与比对**：`emp.password` 是明文，登录 SQL 直接 `where password = #{password}`。
  计划改为 BCrypt 加盐哈希，登录时比对哈希值。
- **数据库唯一约束依赖经验判断**：`sql/init.sql` 中的唯一键是按业务语义补的，
  与线上库可能不完全一致，接入前需核对。
- **日志输出过于啰嗦**：`log-impl: StdOutImpl`、`org.mybatis: debug`、Mapper 包 debug 三处同时开启，
  控制台 SQL 刷屏严重，建议生产环境只保留一处。
- **接口返回体泛型缺失**：`Result` 的 `data` 字段是 `Object`，丢失了编译期类型信息；
  可改为 `Result<T>` 泛型类。
- **单表新增接口未做参数校验**：缺少 `@Valid` + JSR-303 注解，非法入参要靠数据库约束兜底。
- **缺少单元测试与接口文档**：`src/test` 下目前只有学习用的试验代码，
  计划补充 Service 层单测（Mockito）与 SpringDoc / Swagger 接口文档。
- **报表接口未做缓存**：统计 SQL 每次请求都全表聚合，数据量大时可加 Redis 缓存。
- **两处批量删除风格不统一**：员工用 `DELETE /emps?ids=1,2`，学员用 `DELETE /students/1,2`，
  建议统一为查询参数形式。

## 十一、说明

- 项目中的 `/c1`、`/c2`、`/s1`、`/s2` 接口与 `src/test` 下的部分类为学习过程留痕，非业务代码。
- 示例数据（员工姓名、手机号、身份证号等）均为编造的测试数据。
- 本项目为个人学习实践项目，非生产就绪代码。
