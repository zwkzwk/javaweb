# JMeter 应用指南

> [Jmeter](https://github.com/apache/jmeter) 是一款使用 Java 开发的功能和性能测试软件。
>
> 🎁 本文编辑时的最新版本为：5.1.1

## 简介

[Jmeter](https://github.com/apache/jmeter) 是一款使用 Java 开发的功能和性能测试软件。

### 特性

Jmeter 能够加载和性能测试许多不同的应用程序/服务器/协议类型：

- 网络 - HTTP，HTTPS(Java，NodeJS，PHP，ASP.NET 等)
- SOAP / REST Web 服务
- FTP 文件
- 通过 JDBC 的数据库
- LDAP
- 通过 JMS 的面向消息的中间件(MOM)
- 邮件-SMTP(S)，POP3(S)和 IMAP(S)
- 本机命令或 Shell 脚本
- TCP 协议
- Java 对象

### 工作流

Jmeter 的工作原理是仿真用户向服务器发送请求，并收集服务器应答信息并计算统计信息。

Jmeter 的工作流如下图所示：

![](http://dunwu.test.upcdn.net/cs/java/javaweb/technology/test/jmeter-workflow.png!zp)

### 主要元素

Jmeter 的主要元素如下：

- **`测试计划(Test Plan)`** - 可以将测试计划视为 JMeter 的测试脚本 。测试计划由测试元素组成，例如线程组，逻辑控制器，样本生成控制器，监听器，定时器，断言和配置元素。
- **`线程组(Thread Group)`** - 线程组的作用是：模拟大量用户负载的运行场景。
  - 设置线程数
  - 设置加速期
  - 设置执行测试的次数
- **`控制器(Controllers)`** - 可以分为两大类：
  - **`采样器（Sampler）`** - 采样器的作用是模拟用户对目标服务器发送请求。 采样器是必须将组件添加到测试计划中的，因为它只能让 JMeter 知道需要将哪种类型的请求发送到服务器。 请求可以是 HTTP，HTTP(s)，FTP，TCP，SMTP，SOAP 等。
  - **`逻辑控制器`** - 逻辑控制器的作用是：控制多个请求发送的循环次数及顺序等。
- **`监听器(Listeners)`** - 监听器的作用是：收集测试结果信息。如查看结果树、汇总报告等。
- **`计时器(Timers)`** - 计时器的作用是：控制多个请求发送的时间频次。
- **`配置元素(Configuration Elements)`** - 配置元素的工作与采样器的工作类似。但是，它不发送请求，而是提供预备的数据等，如 CSV、函数助手。
- **`预处理器元素(Pre-Processor Elements)`** - 预处理器元素在采样器发出请求之前执行，如果预处理器附加到采样器元素，那么它将在该采样器元素运行之前执行。预处理器元素用于在运行之前准备环境及参数。
- **`后处理器元素(Post-Processor Elements)`** - 后处理器元素是在发送采样器请求之后执行的元素，常用于处理响应数据。

![](http://dunwu.test.upcdn.net/cs/java/javaweb/technology/test/jmeter-elements.png!zp)

> 📌 提示：
>
> Jmeter 元素的数量关系大致如下：
>
> 1. 脚本中最多只能有一个测试计划。
> 2. 测试计划中至少要有一个线程组。
> 3. 线程组中至少要有一个取样器。
> 4. 线程组中至少要有一个监听器。

## 安装

### 环境要求

- 必要的。Jmeter 基于 JDK8 开发，所以必须运行在 JDK8 环境。

  - JDK8

- 可选的。有些 jar 包不是 Jmeter 提供的，如果需要相应的功能，需要自行下载并置于 `lib` 目录。
  - JDBC
  - JMS
  - [Bouncy Castle](http://www.bouncycastle.org/test_releases.html)

### 下载

进入 [**Jmeter 官网下载地址**](https://jmeter.apache.org/download_jmeter.cgi) 选择需要版本进行下载。

### 启动

解压 Jmeter 压缩包，进入 bin 目录

Unix 类系统运行 `jmeter` ；Windows 系统运行 `jmeter.bat`

![image-20191024104517721](http://dunwu.test.upcdn.net/snap/jmeter/image-20191024104517721.png!zp)

## 使用 Jmeter

### 创建测试计划

> 🔔 注意：
>
> - 在运行整个测试计划之前，应保存测试计划。
>
> - JMeter 的测试计划以 `.jmx` 扩展文件的形式保存。

#### 创建线程组

- 在“测试计划”上右键 【添加】=>【线程（用户）】=>【线程组】。

- 设置线程数和循环次数

![image-20191024105545736](http://dunwu.test.upcdn.net/snap/jmeter/image-20191024105545736.png!zp)

#### 配置原件

- 在新建的线程组上右键 【添加】=>【配置元件】=>【HTTP 请求默认值】。

- 填写协议、服务器名称或 IP、端口号

![image-20191024110016264](http://dunwu.test.upcdn.net/snap/jmeter/image-20191024110016264.png!zp)

#### 构造 HTTP 请求

- 在“线程组”上右键 【添加-】=>【取样器】=>【HTTP 请求】。

- 填写协议、服务器名称或 IP、端口号（如果配置了 HTTP 请求默认值可以忽略）
- 填写方法、路径
- 填写参数、消息体数据、文件上传

![image-20191024110953063](http://dunwu.test.upcdn.net/snap/jmeter/image-20191024110953063.png!zp)

#### 添加 HTTP 请求头

- 在“线程组”上右键 【添加】=>【配置元件】=>【HTTP 信息头管理器】
- 由于我的测试例中传输的数据为 json 形式，所以设置键值对 `Content-Type`：`application/json`

![image-20191024111825226](http://dunwu.test.upcdn.net/snap/jmeter/image-20191024111825226.png!zp)

#### 添加断言

- 在“线程组”上右键 【添加】=>【断言】=>【 响应断言 】
- 在我的案例中，以 HTTP 应答状态码为 200 来判断请求是否成功

![image-20191024112335130](http://dunwu.test.upcdn.net/snap/jmeter/image-20191024112335130.png!zp)

#### 添加察看结果树

- 在“线程组”上右键 【添加】=>【监听器】=>【察看结果树】
- 直接点击运行，就可以查看测试结果

![image-20191024113849270](http://dunwu.test.upcdn.net/snap/jmeter/image-20191024113849270.png!zp)

#### 添加汇总报告

- 在“线程组”上右键 【添加】=>【监听器】=>【汇总报告】
- 直接点击运行，就可以查看测试结果

![image-20191024114016424](http://dunwu.test.upcdn.net/snap/jmeter/image-20191024114016424.png!zp)

#### 保存测试计划

执行测试计划前，GUI 会提示先保存配置为 `jmx` 文件。

### 执行测试计划

官方建议不要直接使用 GUI 来执行测试计划，这种模式指适用于创建测试计划和 debug。

执行测试计划应该使用命令行模式，语法形式如下：

```bash
jmeter -n -t [jmx file] -l [results file] -e -o [Path to web report folder]
```

执行测试计划后，在 `-e -o` 参数后指定的 web 报告目录下，可以找到测试报告内容。在浏览器中打开 `index.html` 文件，可以看到如下报告：

![image-20191024120233058](http://dunwu.test.upcdn.net/snap/jmeter/image-20191024120233058.png!zp)

## 参考资料

- [Jmeter 官网](https://jmeter.apache.org/)
- [Jmeter Github](https://github.com/apache/jmeter)
- [Jmeter 性能测试入门](https://www.cnblogs.com/TankXiao/p/4045439.html)
- [易百教程 - Jmeter 教程](https://www.yiibai.com/jmeter)
