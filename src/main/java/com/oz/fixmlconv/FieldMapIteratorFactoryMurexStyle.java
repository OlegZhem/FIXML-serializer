package com.oz.fixmlconv;

import quickfix.FieldMap;

public class FieldMapIteratorFactoryMurexStyle implements FieldMapIteratorFactory{

    @Override
    public FieldMapIterator create(FieldMap fieldMap) {
        return new FieldMapIteratorMurexStyle(fieldMap);
    }
}
