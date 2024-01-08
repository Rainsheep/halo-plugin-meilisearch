# halo-plugin-meilisearch

## 介绍
支持 hal 集成 meilisearch，更准确、更强大的搜索功能。

此插件与搜索组件不冲突，需要安装搜索组件。

## 使用方式

**使用 meiliSearch Cloud 或者自己搭建 meiliSearch 服务都可以。**

### 搭建 meilisearch

docker-compose.yml

```yaml
version: "3"

services:
  meilisearch:
    image: getmeili/meilisearch:v1.5
    container_name: meilisearch
    restart: on-failure:3
    ports:
      - 7700:7700
    volumes:
      - ./meili_data:/meili_data
    environment:
      - MEILI_ENV=production
      - MEILI_MASTER_KEY=95d031f029c0f93289791d39f01a7f42a2211973
      - MEILI_NO_ANALYTICS=true
```

* MEILI_MASTER_KEY 可自定义

### 设置插件
进入插件详情 -> 基本设置，填写 meiliseach 的 host、masterKey、单条搜索结果的长度。

### 已知问题

#### 1. 更改设置后不生效
更新设置后，需重启插件方可生效。

#### 2. 搜索不到文章
可能是文章未被索引导致，需进入仪表盘 → 刷新搜索引擎。 安装&配置好 host 和 masterKey 后务必手动刷新一次。


## 开发环境

插件开发的详细文档请查阅：<https://docs.halo.run/developer-guide/plugin/hello-world>

所需环境：

1. Java 17

### 运行方式 1（推荐）

> 此方式需要本地安装 Docker

```bash
# macOS / Linux
./gradlew pnpmInstall

# Windows
./gradlew.bat pnpmInstall
```

```bash
# macOS / Linux
./gradlew haloServer

# Windows
./gradlew.bat haloServer
```

执行此命令后，会自动创建一个 Halo 的 Docker 容器并加载当前的插件，更多文档可查阅：<https://github.com/halo-sigs/halo-gradle-plugin>

### 运行方式 2

> 此方式需要使用源码运行 Halo

编译插件：

```bash
# macOS / Linux
./gradlew build

# Windows
./gradlew.bat build
```

修改 Halo 配置文件：

```yaml
halo:
  plugin:
    runtime-mode: development
    fixedPluginPath:
      - "/path/to/halo-plugin-meilisearch"
```

最后重启 Halo 项目即可。
