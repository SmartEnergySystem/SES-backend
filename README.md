SES后端仓库Readme
更新时间：2025年5月28日
作者：Huangyijun
 


git相关：
开始前在IDEA克隆本仓库（使用URL）

提交和推送：使用IDEA中的提交和推送功能（推送到master分支）

注：挂梯子但IDEA仍然连接不上git服务器，见https://blog.csdn.net/qq_57467091/article/details/132461804


总而言之代码架构来自：https://www.bilibili.com/video/BV1TP411v7v6/
进行了一些修改

本项目后端采用java spring+mybatis架构，主要分为controller、service、mapper层。

1.SES-server模块：
controller：控制层，使用注解接收并解析前端请求，调用service层

service：服务层，软件的核心功能代码
具有接口和对应的实现类

mapper：持久层，使用mybatis进行数据库操作
仅接口，通过注解直接编写简短sql语句，或在对应的mapper.xml文件中编写动态sql语句

cofig：配置类
interceptor：拦截器，检测jwt令牌
此外还有自定义注解AutoFill：自动填充修改时间等信息

2.SES-pojo模块：
包括实体类、和数据传输对象DTO、VO

3.SES-common模块：
包含各种常量与工具类

Result：对返回结果的封装，一般只用在controller层，其他层不必封装。若有需要返回的数据，先放入result中（调用有参构造函数）
PageResult：分页查询的返回值封装



此外，本架构使用swagger进行后端接口调试，运行SESApplication后，在浏览器打开http://localhost:8080/doc.html
即可查看已编写完成的接口，并进行调试
