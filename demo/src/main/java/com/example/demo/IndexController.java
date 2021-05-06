package com.example.demo;

import com.example.message.QpidHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.qpid.url.URLSyntaxException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class IndexController {
    @RequestMapping("/")
    public String index() {
        int count = 1;
        Map<String, Object> map = new HashMap<>();
        map.put("1024", "zhangsan");
        map.put("1025", "18");
        while (count < 100) {
            try {
                QpidHelper.getInstance().sendString(String.format("hello %s", count++));
                QpidHelper.getInstance().publish("ADDR:server.topic;{create:always, node:{type:topic}}", "123", map);
            }
            catch (URLSyntaxException e) {
                log.error("error.");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return "index...";
    }
}
