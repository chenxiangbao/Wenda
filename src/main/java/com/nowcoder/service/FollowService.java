package com.nowcoder.service;

import com.nowcoder.utils.JedisAdapter;
import com.nowcoder.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 关注
     * 用户关注了某个实体,可以关注问题,关注用户,关注评论等任何实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean follow(int userId,int entityType,int entityId){
        //以实体类型与实体ID作为key
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        //以用户ID与实体类型作为key
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        Date date = new Date();
        // 实体的粉丝增加当前用户，增加成员ID
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        tx.zadd(followerKey,date.getTime(),String.valueOf(userId));
        // 当前用户对这类实体关注+1
        tx.zadd(followeeKey,date.getTime(),String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx,jedis);
        return ret.size() ==2 && (Long) ret.get(0) >0 && (Long) ret.get(1) >0;
    }

    //取消关注
    public boolean unfollow(int userId,int entityType,int entityId){
        //将关注的事情加入到关注列表
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        //将自己加入到粉丝列表
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        Date date = new Date();
        Jedis jedis = jedisAdapter.getJedis();
        Transaction tx = jedisAdapter.multi(jedis);
        //粉丝里面。加上关注人
        tx.zrem(followerKey,String.valueOf(userId));
        tx.zrem(followeeKey,String.valueOf(entityId));
        List<Object> ret = jedisAdapter.exec(tx,jedis);
        return ret.size() ==2 && (Long) ret.get(0) >0 && (Long) ret.get(1) >0;
    }
    //set集合与list集合转换
    private List<Integer> getIdsFromSet(Set<String> idset){
        List<Integer> ids = new ArrayList<>();
        for(String str: idset){
            ids.add(Integer.parseInt(str));
        }
        return  ids;
    }

    //粉丝列表。取出来是用户ID
    public List<Integer> getFollowers(int entityType,int entityId,int count){
        String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
        return  getIdsFromSet(jedisAdapter.zrevrange(followerKey,0,count));
    }
    //粉丝列表。取出来是用户ID.并进行分页
    public List<Integer> getFollowers(int entityType, int entityId, int offset, int count) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return getIdsFromSet(jedisAdapter.zrevrange(followerKey, offset, offset+count));
    }
    //关注列表。取出来的是实体ID
    public List<Integer> getFollowees(int entityType,int userId,int count){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return  getIdsFromSet(jedisAdapter.zrevrange(followeeKey,0,count));
    }
    //关注列表。取出来的是实体ID。并进行分页
    public List<Integer> getFollowees(int userId, int entityType, int offset, int count) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, offset, offset+count));
    }
    //粉丝数目
    public long getFollowerCount(int entityType,int entityId){
        String followerKey = RedisKeyUtil.getFolloweeKey(entityType,entityId);
        return jedisAdapter.zcard(followerKey);
    }
    //关注的数目
    public long getFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return jedisAdapter.zcard(followeeKey);
    }

    /**
     *  判断用户是否关注了某个实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean isFollower(int userId, int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
    }
}
