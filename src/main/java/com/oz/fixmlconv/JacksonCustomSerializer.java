package com.oz.fixmlconv;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.Iterator;

public class JacksonCustomSerializer extends StdSerializer<Message> {

    private static final Logger LOG = LoggerFactory.getLogger(JacksonCustomSerializer.class);


    private final DataDictionary customDataDictionary;
    private final FieldMapIteratorFactory fieldMapIteratorFactory;

     public JacksonCustomSerializer(DataDictionary dataDictionary, FieldMapIteratorFactory fieldMapIteratorFactory) {
        super((Class<Message>) null);
        this.customDataDictionary = dataDictionary;
        this.fieldMapIteratorFactory = fieldMapIteratorFactory;
    }

    public JacksonCustomSerializer(Class<Message> t, DataDictionary dataDictionary, FieldMapIteratorFactory fieldMapIteratorFactory) {
        super(t);
        this.customDataDictionary = dataDictionary;
        this.fieldMapIteratorFactory = fieldMapIteratorFactory;
    }

    private final static QName qnameFixMessage = new QName("fixMessage");

    @Override
    public void serialize(Message message, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        DataDictionary dataDictionary = customDataDictionary;
        if(null == dataDictionary) {
            try {
                dataDictionary = DictionaryManager.dictionaryByMessage(message);
            } catch (FieldNotFound e) {
                throw JsonMappingException.from(jsonGenerator, "could not serialize message "+message, e);
            } catch (ConfigError e) {
                throw JsonMappingException.from(jsonGenerator, "could not serialize message "+message, e);
            }
        }

        final ToXmlGenerator xmlGenerator = (ToXmlGenerator) jsonGenerator;
        xmlGenerator.setNextName(qnameFixMessage);
        xmlGenerator.writeStartObject();

        serializeFieldMap(xmlGenerator, "header",
                fieldMapIteratorFactory.create(message.getHeader()), dataDictionary);
        serializeFieldMap(xmlGenerator, "body",
                fieldMapIteratorFactory.create(message), dataDictionary);
        serializeFieldMap(xmlGenerator, "trailer",
                fieldMapIteratorFactory.create(message.getTrailer()), dataDictionary);

        xmlGenerator.writeEndObject();
    }

    private void serializeFieldMap(ToXmlGenerator xmlGenerator,
                                   String mapName,
                                   FieldMap fieldMap,
                                   DataDictionary dataDictionary) throws IOException {
        xmlGenerator.setNextIsAttribute(false);
        xmlGenerator.writeFieldName(mapName);
        xmlGenerator.writeStartObject();
        Iterator fieldIterator = fieldMap.iterator();
        while (fieldIterator.hasNext()) {
            Field field = (Field) fieldIterator.next();
            LOG.trace("Field {}", field);
            if (!fieldMap.hasGroup(1, field.getTag())) {
                fieldToXml(xmlGenerator, field, dataDictionary);
            }
        }
        Iterator groupIterator = fieldMap.groupKeyIterator();
        while (groupIterator.hasNext()) {
            int groupTag = (Integer) groupIterator.next();
            int groupCount = fieldMap.getGroupCount(groupTag);
            String groupName = null == dataDictionary ? null : dataDictionary.getFieldName(groupTag);
            xmlGenerator.writeFieldName(groupName);
            xmlGenerator.writeStartObject();
            xmlGenerator.setNextIsAttribute(true);
            xmlGenerator.writeStringField("fix", String.valueOf(groupTag));
            xmlGenerator.setNextIsAttribute(true);
            xmlGenerator.writeStringField("itemCount", String.valueOf(groupCount));
            for (int i = 1; i <= groupCount; ++i) {
                 try {
                     Group group = new Group(groupTag, 0);
                     fieldMap.getGroup(i, group);
                     //Group group = new Group(fieldMap.getGroup(i, groupTag));
                     //Group group = fieldMap.getGroup(i, groupTag);
                     serializeFieldMap(xmlGenerator, "item", group, dataDictionary);
                } catch (FieldNotFound e) {
                    LOG.error("Discard group " + groupTag + " due to exception.", e);
                }
            }
            xmlGenerator.writeEndObject();
        }
        xmlGenerator.writeEndObject();
    }

    private void serializeFieldMap(ToXmlGenerator xmlGenerator,
                                       String mapName,
                                       FieldMapIterator fieldMapIterator,
                                       DataDictionary dataDictionary) {
        fieldListBegin(xmlGenerator, mapName);
        fieldMapIterator.forEach(
                x -> fieldToXml(xmlGenerator, x, dataDictionary),
                (x, y) -> groupToXmlBegin(xmlGenerator, x, y, dataDictionary),
                x -> fieldListBegin(xmlGenerator, "item"),
                x -> fieldListEnd(xmlGenerator, "item"),
                x -> groupToXmlEnd(xmlGenerator, x));
        fieldListEnd(xmlGenerator, mapName);
    }

    private void fieldListEnd(ToXmlGenerator xmlGenerator, String listName) {
        try {
            xmlGenerator.writeEndObject();
        } catch (IOException e) {
            LOG.error("Exception in "+listName+" end.", e);
        }
    }

    private void fieldListBegin(ToXmlGenerator xmlGenerator, String listName) {
        try {
            xmlGenerator.setNextIsAttribute(false);
            xmlGenerator.writeFieldName(listName);
            xmlGenerator.writeStartObject();
        } catch (IOException e) {
            LOG.error("Exception in "+listName+" begin.", e);
        }
    }

    private boolean groupToXmlBegin(ToXmlGenerator xmlGenerator, Integer groupTag, FieldMap fieldMap, DataDictionary dataDictionary) {
        int groupCount = fieldMap.getGroupCount(groupTag);
        String groupName = getTagName(groupTag, dataDictionary);
        try {
            xmlGenerator.writeFieldName(groupName);
            xmlGenerator.writeStartObject();
            xmlGenerator.setNextIsAttribute(true);
            xmlGenerator.writeStringField("fix", String.valueOf(groupTag));
            xmlGenerator.setNextIsAttribute(true);
            xmlGenerator.writeStringField("itemCount", String.valueOf(groupCount));
        } catch (IOException e) {
            LOG.error("Discard group begin" + groupTag + " due to exception.", e);
            return false;
        }
        return true;
    }

    private void groupToXmlEnd(ToXmlGenerator xmlGenerator, Integer groupTag) {
        try {
            xmlGenerator.writeEndObject();
        } catch (IOException e) {
            LOG.error("Discard group end" + groupTag + " due to exception.", e);
        }
    }

    private void fieldToXml(ToXmlGenerator xmlGenerator, Field field, DataDictionary dataDictionary) {
        int tagNumber = field.getTag();
        String tagName = getTagName(tagNumber, dataDictionary);
        try {
            String value = (String)field.getObject();
            String description = null == dataDictionary ? null : dataDictionary.getValueName(tagNumber, value);
            xmlGenerator.writeFieldName(tagName);
            xmlGenerator.writeStartObject();
            xmlGenerator.setNextIsAttribute(true);
            xmlGenerator.writeStringField("fix", String.valueOf(tagNumber));
            if (description != null) {
                xmlGenerator.writeStringField("description", description);
            }
            xmlGenerator.setNextIsAttribute(false);
            xmlGenerator.writeRaw(value);
            xmlGenerator.writeEndObject();
         } catch (IOException e) {
            LOG.error("Discard field " + tagNumber + "(" + tagName + ") due to exception.", e);
        }
    }

    private String getTagName(int tagNumber, DataDictionary dataDictionary) {
        String tagName = null == dataDictionary ? null : dataDictionary.getFieldName(tagNumber);
        if (tagName == null) {
            tagName = "Tag" + tagNumber;
            LOG.info("Tag {} is absent in the dictionary. Generated tag name is {} ",
                    tagNumber, tagName);
        }
        return tagName;
    }
}