package com.nowcoder.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.utils.JedisAdapter;
import com.nowcoder.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by nowcoder on 2016/7/30.
 */
@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;
    //放入队列
    public boolean fireEvent(EventModel eventModel) {
        try {
            //将事件转换成文本存储
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEventQueueKey();
            //lpush:将一个或多个值 value 插入到列表 key 的表头
            jedisAdapter.lpush(key, json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
