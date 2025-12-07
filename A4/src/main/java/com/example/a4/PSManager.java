package com.example.a4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PSManager {
    private final Map<String, List<BTSubscriber>> channels = new HashMap<>();

    public void addSubscriber(String channel, BTSubscriber sub) {
        channels.computeIfAbsent(channel, k -> new ArrayList<>()).add(sub);
    }

    public void notifyChannel(String channel) {
        List<BTSubscriber> list = channels.get(channel);
        if (list == null) return;
        List<BTSubscriber> snapshot = new ArrayList<>(list);
        snapshot.forEach(BTSubscriber::update);
    }
}
