package com.oz.fixmlconv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.Field;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Group;

import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FieldMapIteratorOrdered extends FieldMapIterator {

    private static final Logger LOG = LoggerFactory.getLogger(FieldMapIteratorOrdered.class);


    public FieldMapIteratorOrdered(FieldMap fieldMap) {
        super(fieldMap);
    }

    @Override
    public void forEach(Consumer<Field> fieldSerializer,
                               BiConsumer<Integer, FieldMap> groupsBeginSerializer,
                               Consumer<Integer> groupItemBegin,
                               Consumer<Integer> groupItemEnd,
                               Consumer<Integer> groupEndSerializer) {
        Iterator fieldIterator = fieldMap.iterator();
        while (fieldIterator.hasNext()) {
            Field field = (Field) fieldIterator.next();
            LOG.trace("Field {}", field);
            if (!fieldMap.hasGroup(1, field.getTag())) {
                fieldSerializer.accept(field);
            } else {
                groupsBeginSerializer.accept(field.getTag(), fieldMap);
                int groupTag = field.getTag();
                for (int i = 1; fieldMap.hasGroup(i, groupTag); ++i) {
                    try {
                        Group group = fieldMap.getGroup(i, groupTag);
                        groupItemBegin.accept(i);
                        new FieldMapIteratorOrdered(group).forEach(
                                fieldSerializer, groupsBeginSerializer, groupItemBegin,
                                groupItemEnd, groupEndSerializer);
                        groupItemEnd.accept(i);
                    } catch (FieldNotFound e) {
                        LOG.error("Exception on group " + groupTag + "(" + i + ").", e);
                    }
                }
                groupEndSerializer.accept(field.getTag());
            }
        }
    }

}
