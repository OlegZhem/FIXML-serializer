package com.oz.fixmlconv;

import quickfix.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FixMessageConverter {

    public static Map<Integer, Object> toMap(Message message) throws FieldNotFound {
        Map<Integer, Object> map = new TreeMap();
        processFieldMap(map, message.getHeader());
        processFieldMap(map, message);
        processFieldMap(map, message.getTrailer());
        return map;
    }

    private static void processFieldMap(Map<Integer, Object> map, FieldMap fieldMap) throws FieldNotFound {
        Iterator fieldIterator = fieldMap.iterator();

        while (fieldIterator.hasNext()) {
            Field field = (Field) fieldIterator.next();
            String value = fieldMap.getString(field.getTag());
            map.put(field.getTag(), value);
        }

        Iterator groupsKeys = fieldMap.groupKeyIterator();

        while (groupsKeys.hasNext()) {
            int groupCountTag = (Integer) groupsKeys.next();
            Group group = new Group(groupCountTag, 0);
            List<Map> groupsList = new ArrayList();

            for (int i = 1; fieldMap.hasGroup(i, groupCountTag); ++i) {
                Map<Integer, Object> groupMap = new HashMap();
                fieldMap.getGroup(i, group);
                processFieldMap(groupMap, group);
                groupsList.add(groupMap);
            }

            map.put(groupCountTag, groupsList);
        }
    }

}
