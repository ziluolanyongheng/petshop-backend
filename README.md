# PetShop Backend（宠物商城后端）

本科课程团队项目的后端服务，Spring Boot + MyBatis-Flex。线上安全演示版（无真实账户/支付）见
**https://zzxonly.icu/petshop/**。

## 功能模块

- 用户注册登录（账号密码 / 邮箱 / QQ 第三方登录）
- 商品与库存管理、购物车、订单、收货地址
- 门店信息、视频内容
- 管理后台（商品/订单/用户管理）
- AI 问答（接入火山方舟 / 百度千帆大模型，流式响应）
- 支付宝沙箱支付
- 文件/图片上传（阿里云 OSS、MinIO 二选一）

## 技术栈

Spring Boot · MyBatis-Flex · MySQL · Spring Security OAuth2（QQ 登录）· 阿里云 OSS / MinIO ·
Knife4j（接口文档）· 支付宝 SDK

## 本地运行

1. 准备 MySQL 数据库，执行建表脚本（团队内部维护，未随本仓库公开）。
2. 复制下方环境变量，替换成你自己的密钥后运行（`application.yaml` 已全部改成
   `${ENV_VAR}` 占位符，不含任何真实密钥）：

```
DB_HOST / DB_PORT / DB_NAME / DB_USERNAME / DB_PASSWORD
QQ_CLIENT_ID / QQ_CLIENT_SECRET
MAIL_USERNAME / MAIL_PASSWORD
OSS_ENDPOINT / OSS_ACCESS_KEY_ID / OSS_ACCESS_KEY_SECRET / OSS_ACCESS_PREFIX / OSS_BUCKET_NAME
MINIO_ENDPOINT / MINIO_ACCESS_KEY / MINIO_SECRET_KEY / MINIO_BUCKET
ARK_API_KEY
BAIDU_API_KEY / BAIDU_SECRET_KEY / BAIDU_APP_ID
ALIPAY_APP_ID / ALIPAY_APP_PRIVATE_KEY / ALIPAY_PUBLIC_KEY / PAY_SERVER_ADDRESS / PAY_SECRET_KEY
GEN_DB_URL / GEN_DB_USERNAME / GEN_DB_PASSWORD（仅 `GeneratorCode` 代码生成工具用到）
```

3. `./gradlew bootRun`

## 说明

- 这是课程团队项目（原仓库在 Gitee 上多人协作），本仓库是整理后的代码快照，用于个人作品展示，
  不包含原始多人提交历史与团队成员邮箱信息。
- 原始配置文件中的数据库密码、OSS/MinIO 密钥、大模型 API Key、支付宝密钥等已全部替换为环境变量
  占位符，不含任何可用的真实密钥。
