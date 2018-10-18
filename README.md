# package
mvn clean package -Dmaven.test.skip=true -Plocal/test/product

# 注意
1、数据库和数据表在程序启动时，如果不存在，会自动进行创建

2、tomcat使用的jdk版本必须为1.8以下

3、antx.properties中指定的如下属性，必须在服务器上正常配置

#hadoop 配置文件路径

hadoop.home=/usr/lib/hadoop

hadoop.conf.dir=/etc/hadoop/conf

#hive 配置文件路径

hive.home=/usr/lib/hive

hive.conf.dir=/etc/hive/conf

4、部署的目标服务器上必须安装 dos2unix

yum install dos2unix

# 编译&部署

一、编译

在项目的根目录下，执行如下的命令，-P指定环境

mvn clean package -Dmaven.test.skip=true -Plocal/test/product

生成的war包在 web/target 下面 zeus-web.war

二、部署（需要在所有部署的机器上进行执行）

（1）将zeus-web.war 拷贝到tomcat的webapp目录下

（2）在 catalina.sh  中指定jdk的位置

export JAVA_HOME=/opt/software/jdk1.7.0_80

 （3）安装 dos2unix

yum install dos2unix

三、创建zeus需要的目录（注意目录的权限要正确）

zeus.loggingRoot=/home/yunniao/zeus/zeus_work_dir/log

zeus.localdata.dir=/home/yunniao/zeus/zeus_work_dir/data

四、启动tomcat，此时 zeus会自动的创建数据库和表

五、初始化数据库表数据

（5.1）创建运行作业的host群组信息

注意：

（1）需要创建多个host group群组，来实现不同的任务运行在不同的机器上的目标，从而实现调度资源的隔离

（2）如果只是为了提高调度的并行度，可以创建一个host group群组，并为其指定多个host列表，此时调度任务就会顺序的分别在这两台机器上进行执行

insert into zeus_host_group (description,effective,gmt_create,gmt_modified,name) values('server_01',1,'2018-01-01 18:00:00','2018-01-01 18:00:00','server_01');

insert into zeus_host_relation (host,host_group_id) values ('192.168.202.73',1);
