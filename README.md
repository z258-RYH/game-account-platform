# 游号易 · 游戏账号交易平台（Java 版核心实现）

面向 C2C 游戏账号交易的完整业务闭环：账号发布与审核 → 议价下单 → 资金托管 → 交付验收 → 纠纷仲裁。

## 技术栈

Java 17 · Spring Boot 3.2 · Spring Data JPA · Spring Security + JWT · MySQL · Redis · Docker Compose · Vue3

## 本仓库内容

- `src/main/java/com/platform/Application.java` 启动类（开启缓存支持）
- `src/main/java/com/platform/config/SecurityConfig.java` 无状态鉴权、公开接口白名单、CORS 配置
- `src/main/java/com/platform/model/Order.java` 交易订单领域模型（托管 / 支付 / 交付 / 验收 / 纠纷字段）
- `src/main/java/com/platform/service/OrderService.java` 交易主链路与状态机服务
- `pom.xml` 依赖与构建配置

> 完整工程还包含 Node.js 原型（Express + Sequelize，19 张业务表、140+ 接口端点）与 Vue3 前端，体量较大，未纳入本仓库。

## 关键设计

**资金托管**

买家支付成功后订单进入 `ESCROW` 托管态；卖家交付、买家验收后流转 `ACCEPTED` 并结算；出现纠纷转 `DISPUTED`，管理员仲裁后按结果走放款或退款（`REFUND` → `CANCELLED` + `REFUNDED`）。

**订单状态机**

`PENDING` → `BARGAIN` → `PENDING`（接受/拒绝议价后回到待支付）→ `ESCROW` → `DELIVERED` → `ACCEPTED`，含 `CANCELLED` / `DISPUTED` 旁支。每次变更前校验当前状态与操作人身份，越级流转直接拒绝。

**业务规则收口在服务层**

- 议价最多 3 轮，仅买家可发起
- 未通过审核（非 `APPROVED`）的账号禁止下单，卖家不能购买自己的账号
- 已支付（`PAID`）的订单不能直接取消
- 超过 48 小时未验收的订单单独查询，供后台跟进

**鉴权**

Spring Security + JWT 无状态鉴权，BCrypt 编码密码，注册/登录接口白名单放行，其余请求统一走 Token 过滤器。

## 本地运行

```bash
mvn spring-boot:run
```

默认使用 H2 内存库与 `ddl-auto=update`，启动后即可建表；切换 MySQL 只需替换 `spring.datasource` 配置。

> 仓库内配置的密钥与口令均为占位符，运行前请按自己的环境填写。
