package com.oz.fixmlconv;

import quickfix.FieldMap;

public interface FieldMapIteratorFactory {

    FieldMapIterator create(FieldMap fieldMap);
}
