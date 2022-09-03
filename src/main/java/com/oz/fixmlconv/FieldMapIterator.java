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

public abstract class FieldMapIterator {

    protected final FieldMap fieldMap;

    public FieldMapIterator(FieldMap fieldMap) {
        this.fieldMap = fieldMap;
    }

    public abstract void forEach(Consumer<Field> fieldSerializer,
                               BiConsumer<Integer, FieldMap> groupsBeginSerializer,
                               Consumer<Integer> groupItemBegin,
                               Consumer<Integer> groupItemEnd,
                               Consumer<Integer> groupEndSerializer);

    public abstract boolean isEmpty();
}
