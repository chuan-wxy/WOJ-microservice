# WOJ Microservice

WOJ Microservice 是 W Online Judge（WOJ）在线评测系统的后端微服务工程。项目围绕算法题库管理、用户身份治理、代码提交评测、学习内容组织、公告活动发布与 AI 辅助学习等核心场景展开，旨在为在线程序设计教学、竞赛训练与课程实践提供可扩展、可治理、可观测的服务基础。

当前版本：`1.1.0`

## 一、项目定位

在线评测系统本质上是一个面向高并发提交、异步评测与结果反馈的综合性教学支撑平台。WOJ 后端以 Spring Cloud 微服务体系为基础，将业务能力拆分为网关、用户、题库、判题、AI 分析、公共模型与服务客户端等独立模块，从而降低单体系统在功能迭代、服务部署、接口治理和资源隔离方面的复杂度。

本工程主要承担以下职责：

- 提供用户注册、登录、资料维护、权限校验与后台用户管理能力。
- 提供题目、标签、提交记录、评测统计、课程、公告、活动与文件等业务接口。
- 提供 C 语言代码沙箱执行、评测结果聚合与题目统计更新能力。
- 提供统一网关路由、跨域配置、服务发现与接口文档聚合能力。
- 提供 Feign 客户端，支撑服务间同步调用。
- 提供面向 OJ 学习场景的 AI 辅助分析接口。

## 二、技术体系

| 层次 | 技术选型 |
| --- | --- |
| 基础语言 | Java 17 |
| 应用框架 | Spring Boot 3.2.4 |
| 微服务体系 | Spring Cloud 2023.0.1、Spring Cloud Alibaba 2023.0.1.0 |
| 服务注册与发现 | Nacos |
| 网关治理 | Spring Cloud Gateway、Sentinel Gateway |
| 数据访问 | MyBatis-Plus、MySQL |
| 缓存与会话 | Redis、Spring Session Redis |
| 服务调用 | OpenFeign |
| 接口文档 | Knife4j / OpenAPI 3 |
| 工具组件 | Lombok、Hutool、Gson、Apache Commons |

## 三、工程结构

```text
WOJ-microservice
├── woj-common          # 公共配置、响应封装、异常处理、枚举、工具类
├── woj-model           # 领域实体、DTO、VO、代码沙箱请求与响应模型
├── woj-gateway         # API 网关、路由转发、跨域与统一鉴权入口
├── woj-user-service    # 用户、角色、邮箱与后台用户管理服务
├── woj-web-service     # 题库、提交、课程、公告、活动、文件等核心业务服务
├── woj-judge-service   # 判题服务、代码沙箱、评测执行与评测结果生成
├── woj-ai-service      # OJ 学习场景下的 AI 分析与辅助答疑服务
└── woj-service-client  # 服务间调用客户端定义
```

## 四、核心服务说明

### 1. 网关服务 `woj-gateway`

网关作为系统统一流量入口，负责将 `/api/user/**`、`/api/web/**`、`/api/judge/**`、`/api/ai/**` 等请求转发至对应微服务，并承担跨域处理、鉴权过滤、服务发现与 Knife4j 聚合文档入口等职责。

默认端口：`8101`

### 2. 用户服务 `woj-user-service`

用户服务负责账户体系与权限体系的基础能力，包括注册登录、用户资料维护、密码管理、用户角色关联、后台用户查询与管理、邮箱相关能力等。该服务为系统访问控制与用户画像沉淀提供基础数据。

默认上下文路径：`/api/user`

### 3. Web 业务服务 `woj-web-service`

Web 业务服务承载 OJ 平台的主要业务对象，包括题目、标签、题目提交、提交记录、题目统计、课程、公告、活动与文件接口。它既面向前端提供业务 API，也通过内部接口为判题服务、AI 服务等模块提供数据支撑。

默认上下文路径：`/api/web`

### 4. 判题服务 `woj-judge-service`

判题服务负责代码执行与评测逻辑。当前工程包含 C 语言代码沙箱模板、评测服务实现与题目评测结果回写能力。该模块是在线评测系统的计算核心，需要在实际部署中重点关注沙箱隔离、资源限制、执行超时与安全边界。

默认上下文路径：`/api/judge`

### 5. AI 服务 `woj-ai-service`

AI 服务面向 OJ 学习过程提供辅助分析能力，可基于题目信息、用户代码、评测结果与题目标签生成中文反馈。该服务通过 OpenAI Compatible 风格接口调用外部模型，并通过内部工具调用获取题目标签等上下文。

默认上下文路径：`/api/ai`

## 五、运行环境

建议环境如下：

- JDK：`17`
- Maven：`3.8+`
- MySQL：`8.0+`
- Redis：`6.0+`
- Nacos：`2.x`

默认开发配置使用：

- MySQL：`localhost:3306/woj`
- Redis：`localhost:6379`
- Nacos：`127.0.0.1:8848`

实际生产环境请通过配置文件、环境变量或配置中心替换数据库、缓存、邮箱、AI 服务地址等敏感配置。

## 六、本地启动

1. 启动基础设施：MySQL、Redis、Nacos。
2. 初始化数据库结构与基础数据。
3. 在项目根目录编译：

```bash
./mvnw clean package -DskipTests
```

Windows 环境可使用：

```bash
mvnw.cmd clean package -DskipTests
```

4. 按需启动微服务模块。建议顺序如下：

```text
woj-gateway
woj-user-service
woj-web-service
woj-judge-service
woj-ai-service
```

也可在 IDE 中分别运行各模块的 `*Application` 启动类。

## 七、接口与协同

系统采用网关统一暴露 API，前端项目应优先通过网关地址访问后端接口。OpenAPI 文档可通过 Knife4j 聚合能力查看，具体访问地址取决于网关与各服务的运行端口配置。

服务间通信主要通过 `woj-service-client` 中定义的 Feign Client 完成，以减少重复 HTTP 调用代码并强化接口契约。

## 八、工程约定

- 公共响应、异常处理、枚举与工具方法集中维护于 `woj-common`。
- 请求与响应数据模型集中维护于 `woj-model`。
- 微服务间接口优先通过 `woj-service-client` 声明。
- 外部可访问接口与内部服务接口应保持路径与职责边界清晰。
- 判题相关能力应优先考虑资源隔离、输入校验、执行超时与运行安全。

## 九、版本说明

`1.1.0` 版本聚焦于后端微服务工程说明文档的规范化、项目结构的清晰表达，以及与前端工程版本标识的一致化。该版本未改变 Maven 模块组织结构。
