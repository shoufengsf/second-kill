# second-kill
## 秒杀业务流程

### 流程图

![](https://github.com/shoufengsf/second-kill/blob/master/images/秒杀业务流程图.png)

## 主要难点

### 秒杀商品主要逻辑

收到秒杀请求----->>根据商品ID判断该商品是否是秒杀商品（商品表库存大于0，秒杀表库存大于0，当前时间在秒杀时间范围内）----->>根据用户ID和商品ID判断该用户是否抢购成功过该商品（同一个商品一个用户只允许抢购一个）------>>扣减秒杀表库存------>>扣减商品表库存------>>向秒杀成功表插入一条秒杀成功数据

![判断能否秒杀](https://github.com/shoufengsf/second-kill/blob/master/images/判断能否秒杀.png)

### 存在的问题

#### 1、由于秒杀过程并非原子操作，所以在并发情况下可能存在多次秒杀和超卖的状况

##### 超卖情况

![超卖前秒杀表库存](https://github.com/shoufengsf/second-kill/blob/master/images/超卖前秒杀表库存.png)

![超卖前秒杀成功表](https://github.com/shoufengsf/second-kill/blob/master/images/超卖前秒杀成功表.png)



![超卖前商品表库存](https://github.com/shoufengsf/second-kill/blob/master/images/超卖前商品表库存.png)

![超卖秒杀开启线程数](https://github.com/shoufengsf/second-kill/blob/master/images/超卖秒杀开启线程数.png)

![超卖请求](https://github.com/shoufengsf/second-kill/blob/master/images/超卖请求.png)

![超卖后秒杀表](https://github.com/shoufengsf/second-kill/blob/master/images/超卖后秒杀表.png)

![超卖后秒杀成功表](https://github.com/shoufengsf/second-kill/blob/master/images/超卖后秒杀成功表.png)

![超卖后商品表](https://github.com/shoufengsf/second-kill/blob/master/images/超卖后商品表.png)

###### 多次秒杀和超卖解决方案（分布式锁）

1. 利用redis的setnx（用户ID和商品ID组合做key）做分布式锁，用来保证秒杀逻辑代码块在同一个时刻只有一个线程可以操作
2. 利用zookeeper创建临时节点（zk节点唯一，不能重复，多个jvm实例创建临时节点时，只有一个jvm实例能够成功）做分布式锁，用来保证秒杀逻辑代码块在同一个时刻只有一个线程可以操作

##### 多次秒杀情况

![同一用户同个商品多次秒杀](https://github.com/shoufengsf/second-kill/blob/master/images/同一用户同个商品多次秒杀.png)

###### 超卖解决方案（以下方案只能解决超卖，不能解决同个用户同个商品多次秒杀的问题）

1. 在进行秒杀表和商品表库存扣减的时候，在sql中再次加上库存大于0条件（一条sql中的操作是原子操作），如果库存扣减失败，则主动抛出异常，事务回滚。
2. 在秒杀表和商品表中添加version版本号字段（参考jdk包中的AtomicStampedReference中的思想，select操作不增加version，update操作增加version，如果获取的version版本号小于当前数据库表中的version，那么操作失败），用于保证有序执行，如果更新失败，则主动抛出异常，事务回滚。

### @Transactional(rollbackFor = {Exception.class})存在的问题

#### 主要问题

声明式事务@Transactional用于注解方法，在方法执行前开启事务，执行后提交事务。由于秒杀逻辑会对数据库表做更新操作，会对表中的某些数据加锁（命中索引则使用行锁，未命中则使用表锁），如果在@Transactional注解的方法体内有一些需要耗时的操作（比如远程请求第三方，第三方响应很慢等等），方法迟迟不能执行完毕，那么当前事务会长时间持有锁，其他线程访问只能等待。

#### 解决方案

1. 将@Transactional声明式事务改为编程式事务，只对部分需要操作数据库的逻辑加上事务。
2. 使用rabbitmq，将耗时操作解耦（相当于异步操作，未执行完即可返回）。

注意：编程式事务和rabbitmq解耦可以都用

