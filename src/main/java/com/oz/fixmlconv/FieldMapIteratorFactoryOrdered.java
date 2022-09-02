package com.oz.fixmlconv;

import quickfix.FieldMap;

public class FieldMapIteratorFactoryOrdered implements FieldMapIteratorFactory{
    @Override
    public FieldMapIterator create(FieldMap fieldMap) {
        return new FieldMapIteratorOrdered(fieldMap);
    }
}
