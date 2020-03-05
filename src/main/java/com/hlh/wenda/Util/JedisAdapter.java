package com.hlh.wenda.Util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hlh.wenda.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

@Service
public class JedisAdapter implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);


    private  JedisPool pool;

    public static void print(int index, Object obj) {
        System.out.println(String.format("%d,%s", index, obj.toString()));
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis();//什么不写默认连接6379端口
        jedis.auth("123456");//redis密码
        jedis.flushDB();//删掉当前库的内容，flushAll是删掉所以库的

        //get set
        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "newhello");
        print(1, jedis.get("newhello"));
        jedis.setex("hello2", 15, "world");//15秒之后数据超时删除

        //增减value
        jedis.set("pv", "100");
        jedis.incr("pv");//加1
        print(2, jedis.get("pv"));
        jedis.incrBy("pv", 5);//加5（指定特定值）
        print(2, jedis.get("pv"));
        jedis.decr("pv");//减1
        print(2, jedis.get("pv"));
        jedis.incrBy("pv", 5);//减5（可指定特定值）
        print(2, jedis.get("pv"));

        //打印所有存在的key
        print(3, jedis.keys("*"));

        //list(类似于链表的结构）操作队列
        String listName = "list";
        jedis.del(listName);
        for (int i = 0; i < 10; i++) {
            //lpush插进去
            jedis.lpush(listName, "a" + String.valueOf(i));
        }
        print(4, jedis.lrange(listName, 0, 12));//lrange查找并且取出来
        print(4, jedis.lrange(listName, 0, 3));//lrange是两边都闭区间
        print(5, jedis.llen(listName));//输出长度
        print(6, jedis.lpop(listName));//lpop弹出来，弹出来第一个
        print(7, jedis.llen(listName));
        print(8, jedis.lrange(listName, 2, 6));
        print(9, jedis.lindex(listName, 3));//查找特定位置的元素
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "xx"));//在XX之前插入元素
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "bb"));//在XX之后插入元素
        print(11, jedis.lrange(listName, 0, 12));

        //hash（双层kv）哈希（h开头的）（应用于随时可能变更信息的单个用户，比如开会员，是否是管理员等，不用像MySQL一样为了这个单独加一列）
        String userKey = "userxx";
        jedis.hset(userKey, "name", "jim");
        jedis.hset(userKey, "age", "18");
        jedis.hset(userKey, "phone", "17612121212");
        print(12, jedis.hget(userKey, "name"));//单独取出名字列表
        print(13, jedis.hgetAll(userKey));//取出所有
        jedis.hdel(userKey, "phone");//删除字段
        print(14, jedis.hgetAll(userKey));
        print(15,jedis.hexists(userKey,"email"));//检查字段是否存在
        print(15,jedis.hexists(userKey,"age"));
        print(16,jedis.hkeys(userKey));//只取出它的key部分
        print(16,jedis.hvals(userKey));//只取出它的value部分
        jedis.hsetnx(userKey,"school","zju");//如果前者字段（key）不存在，则插入新字段（key = value)
        jedis.hsetnx(userKey,"name","yxy");//如果存在这个key-value，则无效
        print(17,jedis.hgetAll(userKey));

        //集合（set，全部是用s（set）开头的函数）
        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";
        for (int i = 0; i <10 ; i++) {
            jedis.sadd(likeKey1,String.valueOf(i));//将i插入到集合likeKey1中
            jedis.sadd(likeKey2,String.valueOf(i*i));
        }
        print(18,jedis.smembers(likeKey1));//输出集合likeKey1中的成员
        print(18,jedis.smembers(likeKey2));
        print(19,jedis.sunion(likeKey1,likeKey2));//求两个集合的并集
        print(20,jedis.sdiff(likeKey1,likeKey2));//求likeKey1拥有但是likeKey2没有的元素集合
        print(21,jedis.sinter(likeKey1,likeKey2));//求两个集合的交集，两个集合所共有
        print(22,jedis.sismember(likeKey1,"12"));//查询likeKey1中是否存在某个成员
        print(23,jedis.sismember(likeKey2,"16"));
        jedis.srem(likeKey1,"5");//在likeKey1中是删除成员“5”
        print(24,jedis.smembers(likeKey1));
        jedis.smove(likeKey2,likeKey1,"25");//将likeKey2中的“25”移动到likeKey1中
        print(25,jedis.smembers(likeKey1));
        print(25,jedis.smembers(likeKey2));
        print(26,jedis.scard(likeKey1));//查看集合中的成员个数
        print(26,jedis.scard(likeKey2));//查看集合中的成员个数
        //srandmember commentLike1 2 随机从commentLike1（likeKey1）中取2个值（可应用于抽奖）

        //优先队列（类似于数据结构中的堆）(z开头的函数）（zset）通常用来排序
        String rankKey = "rankKey";
        jedis.zadd(rankKey,15,"Jim");
        jedis.zadd(rankKey,60,"Ben");
        jedis.zadd(rankKey,90,"Lee");
        jedis.zadd(rankKey,75,"Lucy");
        jedis.zadd(rankKey,80,"Mei");
        print(27,jedis.zcard(rankKey));//查看成员个数（card）
        print(28,jedis.zcount(rankKey,61,100));//rankKey中61到100分之间的成员个数
        print(29,jedis.zscore(rankKey,"Lucy"));//查询rankKey中某个特定成员的分数（默认浮点型）
        jedis.zincrby(rankKey,6,"Lucy");//给rankKey中特定的成员（Lucy）加特定的分数（5）
        print(30,jedis.zscore(rankKey,"Lucy"));
        print(31,jedis.zincrby(rankKey,2,"lu"));//给一个默认不存在的成员加分，默认从0分开始加
        print(31,jedis.zincrby(rankKey,2,"lu"));//并且可以累加
        //按照排名取
        print(32,jedis.zrange(rankKey,0,100));//对rankKey中排名在【0，100】（闭区间）的成员排序
        print(33,jedis.zrange(rankKey,1,3));//输出从小到大排序的1-3号成员（从0开始排序的）升序，0号最小
        print(34,jedis.zrevrange(rankKey,1,3));//输出从大到小排序的1-3号从成员（从0开始排序）降序，0号最大
        //按照分值取
        for(Tuple tuple : jedis.zrangeByScoreWithScores(rankKey,"60","100")){
            print(35,tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
            //对rankKey里面分数区间在【60，100】的成员按照”member：score”的形式输出
        }
        //rev是逆序即反过来的意思
        print(36,jedis.zrank(rankKey,"Ben"));//从小到大排序
        print(36,jedis.zrevrank(rankKey,"Ben"));//从大到小排序

        //按照字母进行排序
        String setKey = "zset";
        jedis.zadd(setKey,1,"a");
        jedis.zadd(setKey,1,"b");
        jedis.zadd(setKey,1,"c");
        jedis.zadd(setKey,1,"d");
        jedis.zadd(setKey,1,"e");
        print(37,jedis.zlexcount(setKey,"-","+"));//统计个数，-+代表正负无穷
        print(38,jedis.zlexcount(setKey,"[b","[d"));//从b开始到d结束（【b，【d是边界的意思，闭区间）
        print(38,jedis.zlexcount(setKey,"(b","[d"));//从b开始到d结束（（b，（d也是边界的意思，开区间）
        jedis.zrem(setKey,"b");//删除成员b
        print(39,jedis.zrange(setKey,0,10));//排序
        jedis.zremrangeByLex(setKey,"(c","+");//删除（c，+）的所有成员（不包括c）
        print(40,jedis.zrange(setKey,0,2));


        /*
        //连接池（从池子里拿东西，用完以后一定要.close()还回去不然就会独占连接池）
        JedisPool pool = new JedisPool();
        for (int i = 0; i < 100; ++i) {
            Jedis j = pool.getResource();
            j.auth("123456");//设置了秘密的话记得每次定义Jedis的新对象是都要加这行
            print(41,j.get("pv"));
            j.close();
        }
        */

        //redis做缓存
        User user = new User();
        user.setName("xx");
        user.setPassword("ppp");
        user.setHeadUrl("a.png");
        user.setSalt("salt");
        user.setId(1);
        print(42,JSONObject.toJSONString(user));//对象序列化
        jedis.set("user1", JSONObject.toJSONString(user));//将序列化了的对象缓存进redis

        String value = jedis.get("user1");
        User user2 = JSON.parseObject(value,User.class);
        print(43,user2 );

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool();
    }

    //添加数据
    public long sadd(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            jedis.auth("123456");
            return jedis.sadd(key,value);
        }catch (Exception e){
            logger.error("发生异常"+ e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    //删除数据
    public long srem(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            jedis.auth("123456");
            return jedis.srem(key,value);
        }catch (Exception e){
            logger.error("发生异常"+ e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    //统计数量
    public long scard(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            jedis.auth("123456");
            return jedis.scard(key);
        }catch (Exception e){
            logger.error("发生异常"+ e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    //是否是成员
    public boolean sismember(String key,String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            jedis.auth("123456");
            return jedis.sismember(key,value);
        }catch (Exception e){
            logger.error("发生异常"+ e.getMessage());
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return false;
    }
}