package com.oz.fixmlconv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.Message;

public class Serializer {

    private final XmlMapper xmlMapper;

    public Serializer() {
        this((DataDictionary)null, new FieldMapIteratorFactoryMurexStyle());
    }

    public Serializer(String dictionaryName) throws ConfigError {
        this(DictionaryManager.getDictionary(dictionaryName), new FieldMapIteratorFactoryMurexStyle());
    }

    public Serializer(String dictionaryName, FieldMapIteratorFactory fieldMapIteratorFactory) throws ConfigError {
        this(DictionaryManager.getDictionary(dictionaryName), fieldMapIteratorFactory);
    }

    public Serializer(DataDictionary dictionary, FieldMapIteratorFactory fieldMapIteratorFactory) {
        xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        SimpleModule module = new SimpleModule();
        module.addSerializer(Message.class, new JacksonCustomSerializer(dictionary, fieldMapIteratorFactory));
        xmlMapper.registerModule(module);
    }


    public String toFixml(Message message) throws JsonProcessingException {
         return xmlMapper.writeValueAsString(message);
    }
}
