package com.nowcoder.controller;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.Feed;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.FeedService;
import com.nowcoder.service.FollowService;
import com.nowcoder.utils.JedisAdapter;
import com.nowcoder.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedController {
    @Autowired
    FeedService feedService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FollowService followService;
    @Autowired
    JedisAdapter jedisAdapter;

    //拉流
    @RequestMapping(path = {"/pullfeeds"},method = {RequestMethod.GET})
    private String getPullFeeds(Model model){
        int localUserId = hostHolder.getUser() ==null ? 0 : hostHolder.getUser().getId();
        List<Integer> followees = new ArrayList<>();
        if(localUserId==0){
            //获取该用户的关注列表
            followees = followService.getFollowees(EntityType.ENTITY_COMMENT,localUserId,Integer.MAX_VALUE);
        }
        //将关注用户的信息流进行拉流，获取所有关注人的信息
        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE,followees,10);
        model.addAttribute("feeds",feeds);

        return "feeds";
    }

    //推流
    @RequestMapping(path = {"/pushfeeds"},method = {RequestMethod.GET})
    private String getPushFeeds(Model model){
        int localUserId = hostHolder.getUser() ==null ? 0 : hostHolder.getUser().getId();
        //获取推送给我的流。流来自我关注的用户，当我关注的用户使用推流实现，他没做出一件事都会对他的粉丝列表进行推流。
        // 我是粉丝列表之一，所以用我的用户ID可以查到所有我关注事件。推送给我的事件消息。
        List<String> feedIds =jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId),0,10);
        List<Feed> feeds = new ArrayList<>();
        for(String feedId:feedIds){
            Feed feed = feedService.getFeedById(Integer.parseInt(feedId));
            if(feed ==null){
                continue;
            }
            feeds.add(feed);
        }
        return "feeds";
    }

}
