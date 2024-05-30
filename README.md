# 更强大的搜索功能

[体验网站](https://blog.rainsheep.cn)

## 功能介绍

**提供更为强大的搜索功能，搜索更加精确**。

我有一篇名为《Java学习笔记-基础篇(1)》的文章。

![官方原生搜索结果](https://rainsheep.oss-cn-beijing.aliyuncs.com/blog/2024/01/1705073153-85c.png)

![插件搜索结果](https://rainsheep.oss-cn-beijing.aliyuncs.com/blog/2024/01/1705073724-3e2.png)

## 插件原理

插件搜索功能基于 [meilisearch](https://www.meilisearch.com)，做了适配工作。

## 使用教程

### 启动 meilisearch（可选）

> 如果已有 meilisearch 服务或者使用 [meilisearch cloud](https://www.meilisearch.com/cloud)，则不需要此步。
> 
> 当然，你也可以通过别的方式搭建 meilisearch

修改 docker-compose.yml

```yaml
version: "3"

services:
  halo:
    image: halohub/halo:2.15.2
    container_name: halo
    restart: on-failure:3
    networks:
      - halo_network
    volumes:
      - ./halo2:/root/.halo2
    ports:
      - 8090:8090
    command:
      # 修改为自己已有的 MySQL 配置
      - --spring.r2dbc.url=r2dbc:pool:mysql://localhost:3306/halo
      - --spring.r2dbc.username=root
      - --spring.r2dbc.password=
      - --spring.sql.init.platform=mysql
      # 外部访问地址，请根据实际需要修改
      - --halo.external-url=http://localhost:8090/

  # 这部分为新增内容，创建一个 meilisearch 容器
  meilisearch:
    image: getmeili/meilisearch:v1.8
    container_name: meilisearch
    restart: on-failure:3
    networks:
      - halo_network
    ports:
        - 7700:7700
    environment:
      - MEILI_ENV=production
      # 可以改成自己的密码，对长度有限制，不建议修改
      - MEILI_MASTER_KEY=95d031f029c0f93289791d39f01a7f42a2211973
      - MEILI_NO_ANALYTICS=true

networks:
    halo_network:
```

* MEILI_MASTER_KEY 可自定义

通过 `docker compose up -d` 启动 halo 和 meilisearch。 

### 设置插件

> 插件需配合 [搜索组件](https://www.halo.run/store/apps/app-DlacW) 一起使用，搜索组件提供了前端界面， Meilisearch 提供了后端搜索服务。

进入插件详情 -> 基本设置，填写 meiliseach 的 host、masterKey、单条搜索结果的长度。

![设置](https://oss.rainsheep.cn/blog/2024/01/1705076571-cc7.png)

**设置完成后，务必进入仪表盘 → 刷新搜索引擎，否则可能搜不到文章**

**每次启动容器(包括删除后的启动)，都需要手动刷新索引**

## 代办事项

- [ ] 插件启动/更新设置后，自动刷新索引。
- [ ] 使用 DefaultExtensionGetter#getEnabledExtensionByDefinition 获取 ExtensionPoint。
- [ ] 对搜索内容进行后置处理、删除图片信息等。
- [ ] 将 meilisearch 集成到插件中，不再需要单独启动。




