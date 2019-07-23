# RocketMQ 实战篇

<!-- TOC depthFrom:2 depthTo:3 -->

- [概述](#概述)
    - [简介](#简介)
    - [核心概念](#核心概念)
- [安装](#安装)
    - [环境要求](#环境要求)
    - [下载解压](#下载解压)
    - [启动 Name Server](#启动-name-server)
    - [启动 Broker](#启动-broker)
    - [收发消息](#收发消息)
    - [关闭服务器](#关闭服务器)
- [API](#api)
    - [Producer](#producer)
    - [Consumer](#consumer)
    - [FAQ](#faq)
- [资料](#资料)

<!-- /TOC -->

## 概述

### 简介

RocketMQ 是一款开源的分布式消息队列，基于高可用分布式集群技术，提供低延时的、高可靠的消息发布与订阅服务。

RocketMQ 被阿里巴巴捐赠给 Apache，成为 Apache 的孵化项目。

### 核心概念

<div align="center">
<img src="https://gitee.com/turnon/javaweb/raw/master/images/distributed/mq/rocketmq/rmq-model.png" />
</div>

RocketMQ 有以下核心概念：

- **Producer** - 将业务应用程序系统生成的消息发送给代理。RocketMQ 提供多种发送范例：同步，异步和单向。
  - **Producer Group** - 具有相同角色的 Producer 组合在一起。如果原始 Producer 在事务之后崩溃，则代理可以联系同一 Producer 组的不同 Producer 实例以提交或回滚事务。**_警告：考虑到提供的 Producer 在发送消息方面足够强大，每个 Producer 组只允许一个实例，以避免不必要的生成器实例初始化。_**
- **Consumer** - Consumer 从 Broker 那里获取消息并将其提供给应用程序。从用户应用的角度来看，提供了两种类型的 Consumer：
  - **PullConsumer** - PullConsumer 积极地从 Broker 那里获取消息。一旦提取了批量消息，用户应用程序就会启动消费过程。
  - **PushConsumer** - PushConsumer 封装消息提取，消费进度并维护其他内部工作，为最终用户留下回调接口，这个借口会在消息到达时被执行。
  - **Consumer Group** - 完全相同角色的 Consumer 被组合在一起并命名为 Consumer Group。Consumer Group 是一个很好的概念，在消息消费方面实现负载平衡和容错目标非常容易。**_警告：Consumer Group 中的 Consumer 实例必须具有完全相同的主题订阅。_**
- **Broker** - Broker 是 RocketMQ 的主要组成部分。它接收从 Producer 发送的消息，存储它们并准备处理来自 Consumer 的消费请求。它还存储与消息相关的元数据，包括 Consumer Group，消耗进度偏移和主题/队列信息。
- Name Server - 充当路由信息提供者。Producer/Consumer 客户查找主题以查找相应的 Broker 列表。
- **Topic** - 是 Producer 传递消息和 Consumer 提取消息的类别。
- **Message** - 是要传递的信息。消息必须有一个主题，可以将其解释为您要发送给的邮件地址。消息还可以具有可选 Tag 和额外的键值对。例如，您可以为消息设置业务密钥，并在代理服务器上查找消息以诊断开发期间的问题。
  - **Message Queue** - 主题被划分为一个或多个子主题“消息队列”。
  - **Tag** - 即子主题，为用户提供了额外的灵活性。对于 Tag，来自同一业务模块的具有不同目的的消息可以具有相同的主题和不同的 Tag。

## 安装

### 环境要求

- 推荐 64 位操作系统：Linux/Unix/Mac
- 64bit JDK 1.8+
- Maven 3.2.x
- Git

### 下载解压

进入官方下载地址：https://rocketmq.apache.org/dowloading/releases/，选择合适版本

建议选择 binary 版本。

解压到本地：

```bash
> unzip rocketmq-all-4.2.0-source-release.zip
> cd rocketmq-all-4.2.0/
```

### 启动 Name Server

```bash
> nohup sh bin/mqnamesrv &
> tail -f ~/logs/rocketmqlogs/namesrv.log
The Name Server boot success...
```

### 启动 Broker

```bash
> nohup sh bin/mqbroker -n localhost:9876 -c conf/broker.conf &
> tail -f ~/logs/rocketmqlogs/broker.log
The broker[%s, 172.30.30.233:10911] boot success...
```

### 收发消息

执行收发消息操作之前，不许告诉客户端命名服务器的位置。在 RocketMQ 中有多种方法来实现这个目的。这里，我们使用最简单的方法——设置环境变量 `NAMESRV_ADDR` ：

```bash
> export NAMESRV_ADDR=localhost:9876
> sh bin/tools.sh org.apache.rocketmq.example.quickstart.Producer
SendResult [sendStatus=SEND_OK, msgId= ...

> sh bin/tools.sh org.apache.rocketmq.example.quickstart.Consumer
ConsumeMessageThread_%d Receive New Messages: [MessageExt...
```

### 关闭服务器

```bash
> sh bin/mqshutdown broker
The mqbroker(36695) is running...
Send shutdown request to mqbroker(36695) OK

> sh bin/mqshutdown namesrv
The mqnamesrv(36664) is running...
Send shutdown request to mqnamesrv(36664) OK
```

## API

首先在项目中引入 maven 依赖：

```xml
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-client</artifactId>
    <version>4.2.0</version>
</dependency>
```

### Producer

Producer 在 RocketMQ 中负责发送消息。

RocketMQ 有三种消息发送方式：

- 可靠的同步发送
- 可靠的异步发送
- 单项发送

#### 可靠的同步发送

可靠的同步传输用于广泛的场景，如重要的通知消息，短信通知，短信营销系统等。

```java
public class SyncProducer {
    public static void main(String[] args) throws Exception {
        //Instantiate with a producer group name.
        DefaultMQProducer producer = new
            DefaultMQProducer("please_rename_unique_group_name");
        //Launch the instance.
        producer.start();
        for (int i = 0; i < 100; i++) {
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("TopicTest" /* Topic */,
                "TagA" /* Tag */,
                ("Hello RocketMQ " +
                    i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            //Call send message to deliver message to one of brokers.
            SendResult sendResult = producer.send(msg);
            System.out.printf("%s%n", sendResult);
        }
        //Shut down once the producer instance is not longer in use.
        producer.shutdown();
    }
}
```

#### 可靠的异步发送

异步传输通常用于响应时间敏感的业务场景。

```java
public class AsyncProducer {
    public static void main(String[] args) throws Exception {
        //Instantiate with a producer group name.
        DefaultMQProducer producer = new DefaultMQProducer("ExampleProducerGroup");
        //Launch the instance.
        producer.start();
        producer.setRetryTimesWhenSendAsyncFailed(0);
        for (int i = 0; i < 100; i++) {
                final int index = i;
                //Create a message instance, specifying topic, tag and message body.
                Message msg = new Message("TopicTest",
                    "TagA",
                    "OrderID188",
                    "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
                producer.send(msg, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        System.out.printf("%-10d OK %s %n", index,
                            sendResult.getMsgId());
                    }
                    @Override
                    public void onException(Throwable e) {
                        System.out.printf("%-10d Exception %s %n", index, e);
                        e.printStackTrace();
                    }
                });
        }
        //Shut down once the producer instance is not longer in use.
        producer.shutdown();
    }
}
```

#### 单向传输

单向传输用于需要中等可靠性的情况，例如日志收集。

```java
public class OnewayProducer {
    public static void main(String[] args) throws Exception{
        //Instantiate with a producer group name.
        DefaultMQProducer producer = new DefaultMQProducer("ExampleProducerGroup");
        //Launch the instance.
        producer.start();
        for (int i = 0; i < 100; i++) {
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("TopicTest" /* Topic */,
                "TagA" /* Tag */,
                ("Hello RocketMQ " +
                    i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            //Call send message to deliver message to one of brokers.
            producer.sendOneway(msg);

        }
        //Shut down once the producer instance is not longer in use.
        producer.shutdown();
    }
}
```

### Consumer

Consumer 在 RocketMQ 中负责接收消息。

```java
public class OrderedConsumer {
    public static void main(String[] args) throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("example_group_name");
        consumer.setNamesrvAddr(RocketConfig.HOST);

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        consumer.subscribe("TopicTest", "TagA || TagC || TagD");

        consumer.registerMessageListener(new MessageListenerOrderly() {

            AtomicLong consumeTimes = new AtomicLong(0);

            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs,
                ConsumeOrderlyContext context) {
                context.setAutoCommit(false);
                System.out.printf(Thread.currentThread().getName() + " Receive New Messages: " + msgs + "%n");
                this.consumeTimes.incrementAndGet();
                if ((this.consumeTimes.get() % 2) == 0) {
                    return ConsumeOrderlyStatus.SUCCESS;
                } else if ((this.consumeTimes.get() % 3) == 0) {
                    return ConsumeOrderlyStatus.ROLLBACK;
                } else if ((this.consumeTimes.get() % 4) == 0) {
                    return ConsumeOrderlyStatus.COMMIT;
                } else if ((this.consumeTimes.get() % 5) == 0) {
                    context.setSuspendCurrentQueueTimeMillis(3000);
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
                return ConsumeOrderlyStatus.SUCCESS;

            }
        });

        consumer.start();

        System.out.printf("Consumer Started.%n");
    }
}
```

### FAQ

#### connect to `<172.17.0.1:10909>` failed

启动后，Producer 客户端连接 RocketMQ 时报错：

```java
org.apache.rocketmq.remoting.exception.RemotingConnectException: connect to <172.17.0.1:10909> failed
    at org.apache.rocketmq.remoting.netty.NettyRemotingClient.invokeSync(NettyRemotingClient.java:357)
    at org.apache.rocketmq.client.impl.MQClientAPIImpl.sendMessageSync(MQClientAPIImpl.java:343)
    at org.apache.rocketmq.client.impl.MQClientAPIImpl.sendMessage(MQClientAPIImpl.java:327)
    at org.apache.rocketmq.client.impl.MQClientAPIImpl.sendMessage(MQClientAPIImpl.java:290)
    at org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl.sendKernelImpl(DefaultMQProducerImpl.java:688)
    at org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl.sendSelectImpl(DefaultMQProducerImpl.java:901)
    at org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl.send(DefaultMQProducerImpl.java:878)
    at org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl.send(DefaultMQProducerImpl.java:873)
    at org.apache.rocketmq.client.producer.DefaultMQProducer.send(DefaultMQProducer.java:369)
    at com.emrubik.uc.mdm.sync.utils.MdmInit.sendMessage(MdmInit.java:62)
    at com.emrubik.uc.mdm.sync.utils.MdmInit.main(MdmInit.java:2149)
```

原因：RocketMQ 部署在虚拟机上，内网 ip 为 10.10.30.63，该虚拟机一个 docker0 网卡，ip 为 172.17.0.1。RocketMQ broker 启动时默认使用了 docker0 网卡，Producer 客户端无法连接 172.17.0.1，造成以上问题。

解决方案

（1）干掉 docker0 网卡或修改网卡名称

（2）停掉 broker，修改 broker 配置文件，重启 broker。

修改 conf/broker.conf，增加两行来指定启动 broker 的 IP：

```
namesrvAddr = 10.10.30.63:9876
brokerIP1 = 10.10.30.63
```

启动时需要指定配置文件

```bash
nohup sh bin/mqbroker -n localhost:9876 -c conf/broker.conf &
```

## 资料

- [RocketMQ 官方文档](http://rocketmq.apache.org/docs/quick-start/)
