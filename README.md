# Chronus Project

Chronus是一款轻量级分布式调度引擎，纯java语言，实现简洁，提供高可靠、批量任务调度控制。任务可以通过负载均衡分配到不同JVM服务进程，在不同的线程池快速并发执行。
具有调度策略、任务管理、分组管理、TAG管理、执行日志等功能。
                                                                       
## Architecture

![Architecture](doc/images/architecture.png)

## Features

* 支持Dubbo、http调用，任务可跨平台。
* 外部依赖插件化、支持多种注册方式、多种存储方式。
* Master和Executor高可用、水平扩展、任务自动故障转移。
* 支持在线自定义任务参数，实时生效。
* 任务隔离，保障重要业务不受影响。
* 可视化调度管理。

## Getting started

### Maven dependency

```xml
<dependency>
    <groupId>com.qihoo.finance.chronus</groupId>
    <artifactId>chronus</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Defining spring bean handler

```java
@Service("testBean")
public class TestBean implements JssSdkService<String> {
    private static final Logger logger = LogManager.getLogger(TestBean.class);
    
    @Override
    public List<String> selectTasks(String s, List<TaskItemDefineDomain> list, int i1) throws Exception {
        List<String> result = new ArrayList<>();
        result.add("1");
        return result;
    }

    @Override
    public boolean execute(String domain, String s) throws Exception {
        logger.info("开始处理名单导入初始化，当前时间:{}", LocalDateTime.now());
        System.out.println(domain);
        logger.info("结束处理名单导入初始化，当前时间:{}", LocalDateTime.now());
        return true;
    }
}
```

## Document

## Screenshot
![login_screenshot](doc/images/login_screenshot.png)
![task_list_screenshot](doc/images/task_list_screenshot.png)
![task_edit_screenshot](doc/images/task_edit_screenshot.png)
![log_list_screenshot](doc/images/log_list_screenshot.png)
 

## Downloads

## Contact

* Website: [https://www.360jinrong.net](https://www.360jinrong.net)
* Mail: [dev-service@360jinrong.net](dev-service@360jinrong.net)
* Bugs: [Issues](https://github.com/360jinrong/Chronus/issues/new?template=chronus-issue-report-template.md)

## Who Uses Chronus

## License

[Apache 2.0 license.](/LICENSE) Copyright (C) 360 Finance, Inc.
