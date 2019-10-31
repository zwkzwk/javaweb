# JavaWeb

> JavaWeb 开发之路经验总结。
>
> - 🔁 项目同步维护：[Github](https://github.com/dunwu/javaweb/) | [Gitee](https://gitee.com/turnon/javaweb/)
> - 📖 电子书阅读：[Github Pages](https://dunwu.github.io/javaweb/) | [Gitee Pages](http://turnon.gitee.io/javaweb/)

|        🔰         |            🎨            |            ✨            |           ⭐️            |          ☕          |            🌏            |
| :---------------: | :----------------------: | :---------------------: | :---------------------: | :-----------------: | :----------------------: |
| [准备](#🔰️-准备) | [架构设计](#🎨-架构设计) | [系统原理](#✨-系统原理) | [主流技术](#⭐-主流技术) | [JavaEE](#☕-JavaEE) | [网络通信](#🌏-网络通信) |

## 🔰️ 准备

作为 Java Web 工程师，你应该多多少少掌握一些的知识：

- [JavaCore](https://dunwu.github.io/javacore/) - Java 核心技术
- [前端技术指南](https://github.com/dunwu/frontend-tutorial) - 即使是后端工程师，也难免会接触到前端技术。前端技术五花八门，如：React、Vue、Angular、Webpack、ES6、Babel、Node.js 等等。不说掌握，至少也应该知道这些技术是什么。

## 🎨 架构设计

> [架构设计](docs/architecture) 整理架构设计方面的一些学习总结和心得。

- [大型网站架构概述](docs/architecture/大型网站架构概述.md)
- [网站的高性能架构](docs/architecture/网站的高性能架构.md)
- [网站的高可用架构](docs/architecture/网站的高可用架构.md)
- [网站的伸缩性架构](docs/architecture/网站的伸缩性架构.md)
- [网站的可扩展架构](docs/architecture/网站的可扩展架构.md)
- [网站的安全架构](docs/architecture/网站的安全架构.md)
- [网站典型故障](docs/architecture/网站典型故障.md)

## ✨ 系统原理

- [分布式基本原理](docs/theory/distributed-base-theory.md)
- [负载均衡基本原理](docs/theory/load-balance-theory.md)
- [缓存基本原理](docs/theory/cache-theory.md)
- [消息队列基本原理](docs/theory/mq-theory.md)
- [分布式锁基本原理](docs/theory/distributed-lock-theory.md)
- [分布式会话基本原理](docs/theory/distributed-session-theory.md)
- [分布式存储基本原理](docs/theory/distributed-storage-theory.md)
- [分布式 ID 基本原理](docs/theory/distributed-id-theory.md)
- [分布式事务基本原理](docs/theory/distributed-transaction-theory.md)
- [分库分表基本原理](docs/theory/sharding-theory.md)
- [系统安全原理](docs/theory/security-theory.md)

## ⭐ 主流技术

> [主流技术](docs/technology) 汇集 JavaWeb 开发常用的各种主流技术。

### Web 框架

- [Spring 教程](https://dunwu.github.io/spring-tutorial/) 📚
- [Spring Boot 教程](https://dunwu.github.io/spring-boot-tutorial/) 📚

### 服务器

- [Tomcat 应用指南](docs/technology/server/Tomcat.md)
- [Jetty 应用指南](docs/technology/server/Jetty.md)
- [Nginx 简易教程](https://github.com/dunwu/nginx-tutorial) 📚

### 消息队列

- [消息队列面经](docs/technology/mq/MqInterview.md)
- [Kafka 基础篇](docs/technology/mq/KafkaBasics.md)
- [Kafka 原理篇](docs/technology/mq/KafkaAdvanced.md)
- [Kafka Cheat Sheet](docs/technology/mq/KafkaCheatSheet.md)
- [RocketMQ 基础篇](docs/technology/mq/RocketmqBasics.md)
- [RocketMQ 进阶篇](docs/technology/mq/RocketmqAdvanced.md)
- [ActiveMQ 实战篇](docs/technology/mq/ActiveMQ.md)

### 缓存

- [缓存面经](docs/technology/cache/CacheInterview.md)
- [Redis](docs/technology/cache/Redis.md)
- [Ehcache](docs/technology/cache/Ehcache.md)
- [Caffeine](docs/technology/cache/Caffeine.md)

### RPC

- [ZooKeeper 应用指南](docs/technology/rpc/ZooKeeper.md)
- [Dubbo 应用指南](docs/technology/rpc/Dubbo.md)
- Spring Cloud

### 安全

- [Shiro 应用指南](docs/technology/security/Shiro.md)
- [Spring Security 应用指南](docs/technology/security/SpringSecurity.md)

### 数据

- [Mybatis 应用指南](docs/technology/data/Mybatis.md)
- [ShardingSphere 应用指南](docs/technology/data/ShardingSphere.md)

### 监控/诊断/测试

- [Arthas 应用指南](docs/technology/monitor/Arthas.md)
- [SkyWalking 应用指南](docs/technology/monitor/SkyWalking.md)
- [Jmeter 应用指南](docs/technology/monitor/Jmeter.md)

## ☕ JavaEE

> [JavaEE](docs/javaee) 技术——Java Web 的基石

- [JavaEE 面经](docs/javaee/javaee-interview.md)
- [JavaEE 之 Servlet 指南](docs/javaee/javaee-servlet.md)
- [JavaEE 之 Jsp 指南](docs/javaee/javaee-jsp.md)
- [JavaEE 之 Filter 和 Listener](docs/javaee/javaee-filter-listener.md)
- [JavaEE 之 Cookie 和 Session](docs/javaee/javaee-cookie-sesion.md)

## 🌏 网络通信

- **面试**
  - [网络通信面经](docs/network/network-interview.md)
- **网络分层**
  - [计算机网络概述](docs/network/network-guide.md)
  - [计算机网络之物理层](docs/network/network-physical.md)
  - [计算机网络之链路层](docs/network/network-data-link.md)
  - [计算机网络之网络层](docs/network/network-network.md)
  - [计算机网络之传输层](docs/network/network-transport.md)
  - [计算机网络之应用层](docs/network/network-application.md)
- **重要技术**
  - [超文本传输协议 HTTP](docs/network/http.md)
  - [域名系统 DNS](docs/network/dns.md)
  - [传输控制协议 TCP](docs/network/tcp.md)
  - [用户数据报协议 UDP](docs/network/udp.md)
  - [网际控制报文协议 ICMP](docs/network/icmp.md)
  - [网络协议之 WebSocket](docs/network/websocket.md)
  - [CDN 详解](docs/network/cdn.md)
