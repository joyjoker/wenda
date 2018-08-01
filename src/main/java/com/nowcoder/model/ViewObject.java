package com.nowcoder.model;

import java.util.HashMap;
import java.util.Map;

/**
 * ViewObject统一的管理user和question,将二者结合起来
 * 用来传递对象和velocity之间的一个对象
 * 所有的东西都可以往里放通过set进来通过get出去
 */

public class ViewObject {
    private Map<String, Object> objs = new HashMap<>();
    public void set(String key, Object value) {
        objs.put(key, value);
    }

    public Object get(String key) {
        return objs.get(key);
    }
}
