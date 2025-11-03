本项目目录结构如下：
>* gymnasium-parent-project 为父工程
>  * gymnasium-common子模块 里面存放的是业务实体类，服务接口、工具类
>  * gymnasium-service-course 子模块 服务提供方,描述的课程相关的功能模块
>  * gymnasium-service-goods 子模块 服务提供方,描述的是商品相关的功能模块
>  * gymnasium-service-member 子模块 服务提供方,描述的是会员管理相关的功能模块
>  * gymnasium-service-menu 子模块 服务提供方,描述的是菜单相关的管理功能模块
>  * gymnasium-service-role 子模块 服务提供方,描述的是角色相关的功能模块
>  * gymnasium-service-suggest 子模块 服务提供方,描述的是建议相关的功能模块
>  * gymnasium-service-user 子模块 服务提供方,描述的是用户相关的功能模块
>  * gymnasium-service-web 子模块 服务消费方 远程调用服务提供方里面的功能

项目启动需要注意的细节:
> 确保本机电脑上安装并启动了zookeeper，因为我们的所有服务都注册在zookeeper注册中心里面

服务拆分思路:
> 服务提供方按照业务功能进行拆分；
