# 分布式消息队列

> 消息队列是分布式系统中重要的组件，主要解决应用耦合，异步消息，流量削锋等问题。实现高性能，高可用，可伸缩和最终一致性架构。是大型分布式系统不可缺少的中间件。

<!-- TOC depthFrom:2 depthTo:4 -->

- [1. 消息队列应用场景](#1-消息队列应用场景)
    - [1.1. 异步处理](#11-异步处理)
    - [1.2. 应用解耦](#12-应用解耦)
    - [1.3. 流量削锋](#13-流量削锋)
    - [1.4. 日志处理](#14-日志处理)
    - [1.5. 消息通讯](#15-消息通讯)
- [2. JMS 消息服务](#2-jms-消息服务)
    - [2.1. 消息模型](#21-消息模型)
        - [2.1.1. P2P 模式](#211-p2p-模式)
        - [2.1.2. Pub/sub 模式](#212-pubsub-模式)
    - [2.2. 消息消费](#22-消息消费)
    - [2.3. JMS 编程模型](#23-jms-编程模型)
- [3. 常用 MQ 中间件](#3-常用-mq-中间件)
    - [3.1. ActiveMQ](#31-activemq)
    - [3.2. RabbitMQ](#32-rabbitmq)
    - [3.3. ZeroMQ](#33-zeromq)
    - [3.4. Kafka](#34-kafka)
- [4. MQ 示例](#4-mq-示例)
    - [4.1. 电商系统](#41-电商系统)
    - [4.2. 日志收集系统](#42-日志收集系统)
- [5. 资料](#5-资料)

<!-- /TOC -->

## 1. 消息队列应用场景

### 1.1. 异步处理

场景说明：用户注册后，需要发注册邮件和注册短信。传统的做法有两种 1.串行的方式；2.并行方式。

（1）串行方式：将注册信息写入数据库成功后，发送注册邮件，再发送注册短信。以上三个任务全部完成后，返回给客户端。

<div align="center"><img src="http://upload-images.jianshu.io/upload_images/3101171-c60d8f5be3a7a3f2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"/></div>

（2）并行方式：将注册信息写入数据库成功后，发送注册邮件的同时，发送注册短信。以上三个任务完成后，返回给客户端。与串行的差别是，并行的方式可以提高处理的时间。

<div align="center"><img src="http://upload-images.jianshu.io/upload_images/3101171-1a78d88cfbfebcd2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"/></div>

假设三个业务节点每个使用 50 毫秒钟，不考虑网络等其他开销，则串行方式的时间是 150 毫秒，并行的时间可能是 100 毫秒。

因为 CPU 在单位时间内处理的请求数是一定的，假设 CPU1 秒内吞吐量是 100 次。则串行方式 1 秒内 CPU 可处理的请求量是 7 次（1000/150）。并行方式处理的请求量是 10 次（1000/100）。

小结：如以上案例描述，传统的方式系统的性能（并发量，吞吐量，响应时间）会有瓶颈。如何解决这个问题呢？

引入消息队列，将不是必须的业务逻辑，异步处理。改造后的架构如下：

<div align="center"><img src="http://upload-images.jianshu.io/upload_images/3101171-105299d1fd6f3093.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"/></div>

按照以上约定，用户的响应时间相当于是注册信息写入数据库的时间，也就是 50 毫秒。注册邮件，发送短信写入消息队列后，直接返回，因此写入消息队列的速度很快，基本可以忽略，因此用户的响应时间可能是 50 毫秒。因此架构改变后，系统的吞吐量提高到每秒 20 QPS。比串行提高了 3 倍，比并行提高了两倍。

### 1.2. 应用解耦

场景说明：用户下单后，订单系统需要通知库存系统。传统的做法是，订单系统调用库存系统的接口。如下图：

<div align="center"><img src="http://upload-images.jianshu.io/upload_images/3101171-872f21e480fa4026.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"/></div>

传统模式的缺点：

1）  假如库存系统无法访问，则订单减库存将失败，从而导致订单失败；

2）  订单系统与库存系统耦合；

如何解决以上问题呢？引入应用消息队列后的方案，如下图：

<div align="center"><img src="http://upload-images.jianshu.io/upload_images/3101171-81e965d35df99238.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"/></div>

- 订单系统：用户下单后，订单系统完成持久化处理，将消息写入消息队列，返回用户订单下单成功。
- 库存系统：订阅下单的消息，采用拉/推的方式，获取下单信息，库存系统根据下单信息，进行库存操作。
- 假如：在下单时库存系统不能正常使用。也不影响正常下单，因为下单后，订单系统写入消息队列就不再关心其他的后续操作了。实现订单系统与库存系统的应用解耦。

### 1.3. 流量削锋

流量削锋也是消息队列中的常用场景，一般在秒杀或团抢活动中使用广泛。

应用场景：秒杀活动，一般会因为流量过大，导致流量暴增，应用挂掉。为解决这个问题，一般需要在应用前端加入消息队列。

1.  可以控制活动的人数；
2.  可以缓解短时间内高流量压垮应用；

<div align="center"><img src="http://upload-images.jianshu.io/upload_images/3101171-26394776da461cbf.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"/></div>

1.  用户的请求，服务器接收后，首先写入消息队列。假如消息队列长度超过最大数量，则直接抛弃用户请求或跳转到错误页面；
2.  秒杀业务根据消息队列中的请求信息，再做后续处理。

### 1.4. 日志处理

日志处理是指将消息队列用在日志处理中，比如 Kafka 的应用，解决大量日志传输的问题。架构简化如下：

<div align="center"><img src="http://upload-images.jianshu.io/upload_images/3101171-54d35d48b2fb643b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"/></div>

- 日志采集客户端，负责日志数据采集，定时写入 Kafka 队列；
- Kafka 消息队列，负责日志数据的接收，存储和转发；
- 日志处理应用：订阅并消费 kafka 队列中的日志数据；

以下是新浪 kafka 日志处理应用案例：

转自（http://cloud.51cto.com/art/201507/484338.htm）

<div align="center"><img src="http://upload-images.jianshu.io/upload_images/3101171-46e0f421a0670f4b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"/></div>

- Kafka - 接收用户日志的消息队列。
- Logstash - 负责日志传输和解析，统一成 JSON 输出给 Elasticsearch。
- Elasticsearch - 实时日志分析服务的核心技术，一个 schemaless，实时的数据存储服务，通过 index 组织数据，兼具强大的搜索和统计功能。
- Kibana - 基于 Elasticsearch 的数据可视化组件，超强的数据可视化能力是众多公司选择 ELK stack 的重要原因。

### 1.5. 消息通讯

消息通讯是指，消息队列一般都内置了高效的通信机制，因此也可以用在纯的消息通讯。比如实现点对点消息队列，或者聊天室等。

点对点通讯：

<div align="center"><img src="http://upload-images.jianshu.io/upload_images/3101171-ba662aaae7331dee.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"/></div>

客户端 A 和客户端 B 使用同一队列，进行消息通讯。

聊天室通讯：

<div align="center"><img src="http://upload-images.jianshu.io/upload_images/3101171-07ef81ffec631fd1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"/></div>

客户端 A，客户端 B，客户端 N 订阅同一主题，进行消息发布和接收。实现类似聊天室效果。

以上实际是消息队列的两种消息模式，点对点或发布订阅模式。模型为示意图，供参考。

## 2. JMS 消息服务

讲消息队列就不得不提 JMS 。JMS（JAVA Message Service，java 消息服务）API 是一个消息服务的标准/规范，允许应用程序组件基于 JavaEE 平台创建、发送、接收和读取消息。它使分布式通信耦合度更低，消息服务更加可靠以及异步性。

在 EJB 架构中，有消息 bean 可以无缝的与 JM 消息服务集成。在 J2EE 架构模式中，有消息服务者模式，用于实现消息与应用直接的解耦。

### 2.1. 消息模型

在 JMS 标准中，有两种消息模型：

- P2P(Point to Point)
- Pub/Sub(Publish/Subscribe)

#### 2.1.1. P2P 模式

<div align="center"><img src="http://upload-images.jianshu.io/upload_images/3101171-2adc66e2367cd2c2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"/></div>

P2P 模式包含三个角色：消息队列（Queue），发送者(Sender)，接收者(Receiver)。每个消息都被发送到一个特定的队列，接收者从队列中获取消息。队列保留着消息，直到他们被消费或超时。

P2P 的特点

- 每个消息只有一个消费者（Consumer）(即一旦被消费，消息就不再在消息队列中)
- 发送者和接收者之间在时间上没有依赖性，也就是说当发送者发送了消息之后，不管接收者有没有正在运行，它不会影响到消息被发送到队列
- 接收者在成功接收消息之后需向队列应答成功

如果希望发送的每个消息都会被成功处理的话，那么需要 P2P 模式。

#### 2.1.2. Pub/sub 模式

<div align="center"><img src="http://upload-images.jianshu.io/upload_images/3101171-12afe9581da889ea.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"/></div>

包含三个角色主题（Topic），发布者（Publisher），订阅者（Subscriber） 。多个发布者将消息发送到 Topic,系统将这些消息传递给多个订阅者。

Pub/Sub 的特点

- 每个消息可以有多个消费者
- 发布者和订阅者之间有时间上的依赖性。针对某个主题（Topic）的订阅者，它必须创建一个订阅者之后，才能消费发布者的消息。
- 为了消费消息，订阅者必须保持运行的状态。

为了缓和这样严格的时间相关性，JMS 允许订阅者创建一个可持久化的订阅。这样，即使订阅者没有被激活（运行），它也能接收到发布者的消息。

如果希望发送的消息可以不被做任何处理、或者只被一个消息者处理、或者可以被多个消费者处理的话，那么可以采用 Pub/Sub 模型。

### 2.2. 消息消费

在 JMS 中，消息的产生和消费都是异步的。对于消费来说，JMS 的消息者可以通过两种方式来消费消息。

（1）同步

订阅者或接收者通过 receive 方法来接收消息，receive 方法在接收到消息之前（或超时之前）将一直阻塞；

（2）异步

订阅者或接收者可以注册为一个消息监听器。当消息到达之后，系统自动调用监听器的 onMessage 方法。

JNDI：Java 命名和目录接口,是一种标准的 Java 命名系统接口。可以在网络上查找和访问服务。通过指定一个资源名称，该名称对应于数据库或命名服务中的一个记录，同时返回资源连接建立所必须的信息。

JNDI 在 JMS 中起到查找和访问发送目标或消息来源的作用。

### 2.3. JMS 编程模型

(1) ConnectionFactory

创建 Connection 对象的工厂，针对两种不同的 jms 消息模型，分别有 QueueConnectionFactory 和 TopicConnectionFactory 两种。可以通过 JNDI 来查找 ConnectionFactory 对象。

(2) Destination

Destination 的意思是消息生产者的消息发送目标或者说消息消费者的消息来源。对于消息生产者来说，它的 Destination 是某个队列（Queue）或某个主题（Topic）;对于消息消费者来说，它的 Destination 也是某个队列或主题（即消息来源）。

所以，Destination 实际上就是两种类型的对象：Queue、Topic。可以通过 JNDI 来查找 Destination。

(3) Connection

Connection 表示在客户端和 JMS 系统之间建立的链接（对 TCP/IP socket 的包装）。Connection 可以产生一个或多个 Session。跟 ConnectionFactory 一样，Connection 也有两种类型：QueueConnection 和 TopicConnection。

(4) Session

Session 是操作消息的接口。可以通过 session 创建生产者、消费者、消息等。Session 提供了事务的功能。当需要使用 session 发送/接收多个消息时，可以将这些发送/接收动作放到一个事务中。同样，也分 QueueSession 和 TopicSession。

(5) 消息的生产者

消息生产者由 Session 创建，并用于将消息发送到 Destination。同样，消息生产者分两种类型：QueueSender 和 TopicPublisher。可以调用消息生产者的方法（send 或 publish 方法）发送消息。

(6) 消息消费者

消息消费者由 Session 创建，用于接收被发送到 Destination 的消息。两种类型：QueueReceiver 和 TopicSubscriber。可分别通过 session 的 createReceiver(Queue)或 createSubscriber(Topic)来创建。当然，也可以 session 的 creatDurableSubscriber 方法来创建持久化的订阅者。

(7) MessageListener

消息监听器。如果注册了消息监听器，一旦消息到达，将自动调用监听器的 onMessage 方法。EJB 中的 MDB（Message-Driven Bean）就是一种 MessageListener。

深入学习 JMS 对掌握 JAVA 架构，EJB 架构有很好的帮助，消息中间件也是大型分布式系统必须的组件。本次分享主要做全局性介绍，具体的深入需要大家学习，实践，总结，领会。

## 3. 常用 MQ 中间件

一般商用的容器，比如 WebLogic，JBoss，都支持 JMS 标准，开发上很方便。但免费的比如 Tomcat，Jetty 等则需要使用第三方的消息中间件。本部分内容介绍常用的消息中间件（ActiveMQ、RabbitMQ、RocketMQ、Kafka）以及他们的特点。

### 3.1. ActiveMQ

ActiveMQ 是 Apache 出品，最流行的，能力强劲的开源消息总线。ActiveMQ 是一个完全支持 JMS1.1 和 J2EE 1.4 规范的 JMS Provider 实现，尽管 JMS 规范出台已经是很久的事情了，但是 JMS 在当今的 J2EE 应用中间仍然扮演着特殊的地位。

ActiveMQ 特性如下：

⒈ 多种语言和协议编写客户端。语言: Java,C,C++,C#,Ruby,Perl,Python,PHP。应用协议： OpenWire,Stomp REST,WS Notification,XMPP,AMQP

⒉ 完全支持 JMS1.1 和 J2EE 1.4 规范 （持久化，XA 消息，事务)

⒊ 对 Spring 的支持，ActiveMQ 可以很容易内嵌到使用 Spring 的系统里面去，而且也支持 Spring2.0 的特性

⒋ 通过了常见 J2EE 服务器（如 Geronimo,JBoss 4,GlassFish,WebLogic)的测试，其中通过 JCA 1.5 resource adaptors 的配置，可以让 ActiveMQ 可以自动的部署到任何兼容 J2EE 1.4 商业服务器上

⒌ 支持多种传送协议：in-VM,TCP,SSL,NIO,UDP,JGroups,JXTA

⒍ 支持通过 JDBC 和 journal 提供高速的消息持久化

⒎ 从设计上保证了高性能的集群，客户端-服务器，点对点

⒏ 支持 Ajax

⒐ 支持与 Axis 的整合

⒑ 可以很容易得调用内嵌 JMS provider，进行测试

### 3.2. RabbitMQ

RabbitMQ 是流行的开源消息队列系统，用 erlang 语言开发。RabbitMQ 是 AMQP（高级消息队列协议）的标准实现。支持多种客户端，如：Python、Ruby、.NET、Java、JMS、C、PHP、ActionScript、XMPP、STOMP 等，支持 AJAX，持久化。用于在分布式系统中存储转发消息，在易用性、扩展性、高可用性等方面表现不俗。

结构图如下：

<div align="center"><img src="http://upload-images.jianshu.io/upload_images/3101171-1109f074e8445c6d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"/></div>

几个重要概念：

Broker：简单来说就是消息队列服务器实体。

Exchange：消息交换机，它指定消息按什么规则，路由到哪个队列。

Queue：消息队列载体，每个消息都会被投入到一个或多个队列。

Binding：绑定，它的作用就是把 exchange 和 queue 按照路由规则绑定起来。

Routing Key：路由关键字，exchange 根据这个关键字进行消息投递。

vhost：虚拟主机，一个 broker 里可以开设多个 vhost，用作不同用户的权限分离。

producer：消息生产者，就是投递消息的程序。

consumer：消息消费者，就是接受消息的程序。

channel：消息通道，在客户端的每个连接里，可建立多个 channel，每个 channel 代表一个会话任务。

消息队列的使用过程，如下：

（1）客户端连接到消息队列服务器，打开一个 channel。

（2）客户端声明一个 exchange，并设置相关属性。

（3）客户端声明一个 queue，并设置相关属性。

（4）客户端使用 routing key，在 exchange 和 queue 之间建立好绑定关系。

（5）客户端投递消息到 exchange。

exchange 接收到消息后，就根据消息的 key 和已经设置的 binding，进行消息路由，将消息投递到一个或多个队列里。

### 3.3. ZeroMQ

号称史上最快的消息队列，它实际类似于 Socket 的一系列接口，他跟 Socket 的区别是：普通的 socket 是端到端的（1:1 的关系），而 ZMQ 却是可以 N：M 的关系，人们对 BSD 套接字的了解较多的是点对点的连接，点对点连接需要显式地建立连接、销毁连接、选择协议（TCP/UDP）和处理错误等，而 ZMQ 屏蔽了这些细节，让你的网络编程更为简单。ZMQ 用于 node 与 node 间的通信，node 可以是主机或者是进程。

引用官方的说法： “ZMQ(以下 ZeroMQ 简称 ZMQ)是一个简单好用的传输层，像框架一样的一个 socket library，他使得 Socket 编程更加简单、简洁和性能更高。是一个消息处理队列库，可在多个线程、内核和主机盒之间弹性伸缩。ZMQ 的明确目标是“成为标准网络协议栈的一部分，之后进入 Linux 内核”。现在还未看到它们的成功。但是，它无疑是极具前景的、并且是人们更加需要的“传统”BSD 套接字之上的一 层封装。ZMQ 让编写高性能网络应用程序极为简单和有趣。”

特点是：

- 高性能，非持久化；
- 跨平台：支持 Linux、Windows、OS X 等。
- 多语言支持； C、C++、Java、.NET、Python 等 30 多种开发语言。
- 可单独部署或集成到应用中使用；
- 可作为 Socket 通信库使用。

与 RabbitMQ 相比，ZMQ 并不像是一个传统意义上的消息队列服务器，事实上，它也根本不是一个服务器，更像一个底层的网络通讯库，在 Socket API 之上做了一层封装，将网络通讯、进程通讯和线程通讯抽象为统一的 API 接口。支持“Request-Reply “，”Publisher-Subscriber“，”Parallel Pipeline”三种基本模型和扩展模型。

ZeroMQ 高性能设计要点：

1、无锁的队列模型

对于跨线程间的交互（用户端和 session）之间的数据交换通道 pipe，采用无锁的队列算法 CAS；在 pipe 两端注册有异步事件，在读或者写消息到 pipe 的时，会自动触发读写事件。

2、批量处理的算法

对于传统的消息处理，每个消息在发送和接收的时候，都需要系统的调用，这样对于大量的消息，系统的开销比较大，zeroMQ 对于批量的消息，进行了适应性的优化，可以批量的接收和发送消息。

3、多核下的线程绑定，无须 CPU 切换

区别于传统的多线程并发模式，信号量或者临界区， zeroMQ 充分利用多核的优势，每个核绑定运行一个工作者线程，避免多线程之间的 CPU 切换开销。

### 3.4. Kafka

Kafka 是一种高吞吐量的分布式发布订阅消息系统，它可以处理消费者规模的网站中的所有动作流数据。 这种动作（网页浏览，搜索和其他用户的行动）是在现代网络上的许多社会功能的一个关键因素。 这些数据通常是由于吞吐量的要求而通过处理日志和日志聚合来解决。 对于像 Hadoop 的一样的日志数据和离线分析系统，但又要求实时处理的限制，这是一个可行的解决方案。Kafka 的目的是通过 Hadoop 的并行加载机制来统一线上和离线的消息处理，也是为了通过集群机来提供实时的消费。

Kafka 是一种高吞吐量的分布式发布订阅消息系统，有如下特性：

- 通过 O(1)的磁盘数据结构提供消息的持久化，这种结构对于即使数以 TB 的消息存储也能够保持长时间的稳定性能。（文件追加的方式写入数据，过期的数据定期删除）
- 高吞吐量：即使是非常普通的硬件 Kafka 也可以支持每秒数百万的消息。
- 支持通过 Kafka 服务器和消费机集群来分区消息。
- 支持 Hadoop 并行数据加载。

Kafka 相关概念

- Broker

Kafka 集群包含一个或多个服务器，这种服务器被称为 broker[5]

- Topic

每条发布到 Kafka 集群的消息都有一个类别，这个类别被称为 Topic。（物理上不同 Topic 的消息分开存储，逻辑上一个 Topic 的消息虽然保存于一个或多个 broker 上但用户只需指定消息的 Topic 即可生产或消费数据而不必关心数据存于何处）

- Partition

Parition 是物理上的概念，每个 Topic 包含一个或多个 Partition.

- Producer

负责发布消息到 Kafka broker

- Consumer

消息消费者，向 Kafka broker 读取消息的客户端。

- Consumer Group

每个 Consumer 属于一个特定的 Consumer Group（可为每个 Consumer 指定 group name，若不指定 group name 则属于默认的 group）。

一般应用在大数据日志处理或对实时性（少量延迟），可靠性（少量丢数据）要求稍低的场景使用。

## 4. MQ 示例

### 4.1. 电商系统

<div align="center"><img src="http://upload-images.jianshu.io/upload_images/3101171-1bbed6d1a2274ba1.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"/></div>

消息队列采用高可用，可持久化的消息中间件。比如 Active MQ，Rabbit MQ，Rocket Mq。

（1）应用将主干逻辑处理完成后，写入消息队列。消息发送是否成功可以开启消息的确认模式。（消息队列返回消息接收成功状态后，应用再返回，这样保障消息的完整性）

（2）扩展流程（发短信，配送处理）订阅队列消息。采用推或拉的方式获取消息并处理。

（3）消息将应用解耦的同时，带来了数据一致性问题，可以采用最终一致性方式解决。比如主数据写入数据库，扩展应用根据消息队列，并结合数据库方式实现基于消息队列的后续处理。

### 4.2. 日志收集系统

<div align="center"><img src="http://upload-images.jianshu.io/upload_images/3101171-4275c9f0e9c8c463.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240"/></div>

分为 Zookeeper 注册中心，日志收集客户端，Kafka 集群和 Storm 集群（OtherApp）四部分组成。

- Zookeeper 注册中心，提出负载均衡和地址查找服务；
- 日志收集客户端，用于采集应用系统的日志，并将数据推送到 kafka 队列；
- Kafka 集群：接收，路由，存储，转发等消息处理；

Storm 集群：与 OtherApp 处于同一级别，采用拉的方式消费队列中的数据；

## 5. 资料

- [大型网站架构系列：分布式消息队列（一）](https://www.cnblogs.com/itfly8/p/5155983.html)
- [大型网站架构系列：消息队列（二）](https://www.cnblogs.com/itfly8/p/5156155.html)
- [分布式开放消息系统(RocketMQ)的原理与实践](https://www.jianshu.com/p/453c6e7ff81c)
- [阿里 RocketMQ 优势对比](https://juejin.im/entry/5a0abfb5f265da43062a4a91)
