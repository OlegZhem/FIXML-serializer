package com.oz.fixmlconv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;

import java.io.IOException;

public class StringBuilderSerializer {

    private static final Logger LOG = LoggerFactory.getLogger(StringBuilderSerializer.class);


    private final DataDictionary customDataDictionary;
    private final FieldMapIteratorFactory fieldMapIteratorFactory;

    public StringBuilderSerializer(DataDictionary customDataDictionary,
                                   FieldMapIteratorFactory fieldMapIteratorFactory) {
        this.customDataDictionary = customDataDictionary;
        this.fieldMapIteratorFactory = fieldMapIteratorFactory;
    }

    public String serialize(Message message) throws IOException, ConfigError, FieldNotFound {
        final DataDictionary dataDictionary;
        if(null == customDataDictionary) {
            dataDictionary = DictionaryManager.dictionaryByMessage(message);
        } else {
            dataDictionary = customDataDictionary;
        }

        StringBuilder builder = new StringBuilder();
        writeTag(builder, "fixMessage", () -> {
            serializeFieldMap(builder, "header",
                    fieldMapIteratorFactory.create(message.getHeader()), dataDictionary);
            serializeFieldMap(builder, "body",
                    fieldMapIteratorFactory.create(message), dataDictionary);
            serializeFieldMap(builder, "trailer",
                    fieldMapIteratorFactory.create(message.getTrailer()), dataDictionary);
        });
        return builder.toString();
    }

    private void writeTagBegin(StringBuilder builder, String name) {
        builder.append("<");
        builder.append(name);
        builder.append(">\n");
    }

    private void writeTagEnd(StringBuilder builder, String name) {
        builder.append("</");
        builder.append(name);
        builder.append(">\n");
    }

    private void writeTag( StringBuilder builder, String name) {
        builder.append("<");
        builder.append(name);
        builder.append("/>\n");
    }

    private void writeTag( StringBuilder builder, String name, Runnable tagContent) {
        writeTagBegin(builder, name);
        tagContent.run();
        writeTagEnd(builder, name);
    }

    private void writeTag( StringBuilder builder, String name, Runnable attributes, Runnable tagContent) {
        builder.append("<");
        builder.append(name);
        attributes.run();
        builder.append(">");
        tagContent.run();
        writeTagEnd(builder, name);
    }

    private void writeAttribute(StringBuilder builder, String attrName, String attrValue) {
        builder.append(" ");
        builder.append(attrName);
        builder.append("=\"");
        builder.append(attrValue);
        builder.append("\"");
    }

    private void serializeFieldMap(StringBuilder builder, String tag,
                                   FieldMapIterator fieldMapIterator, DataDictionary dataDictionary) {
        if(fieldMapIterator.isEmpty()) {
            writeTag(builder, tag);
        } else {
            writeTag(builder, tag, () -> {
                fieldMapIterator.forEach(
                        x -> fieldToXml(builder, x, dataDictionary),
                        (x, y) -> groupToXmlBegin(builder, x, y, dataDictionary),
                        x -> writeTagBegin(builder, "item"),
                        x -> writeTagEnd(builder, "item"),
                        x -> groupToXmlEnd(builder, x, dataDictionary));
            });
        }
    }

    private boolean groupToXmlBegin(StringBuilder builder, Integer groupTag,
                                    FieldMap fieldMap, DataDictionary dataDictionary) {
        int groupCount = fieldMap.getGroupCount(groupTag);
        String groupName = getTagName(groupTag, dataDictionary);
        builder.append("<");
        builder.append(groupName);
        writeAttribute(builder, "fix", String.valueOf(groupTag));
        writeAttribute(builder, "itemCount", String.valueOf(groupCount));
        builder.append(">");
        return true;
    }

    private void groupToXmlEnd(StringBuilder builder, Integer groupTag, DataDictionary dataDictionary) {
        String groupName = getTagName(groupTag, dataDictionary);
        builder.append("</");
        builder.append(groupName);
        builder.append(">");
    }

    private void fieldToXml(StringBuilder builder, Field field, DataDictionary dataDictionary) {
        int tagNumber = field.getTag();
        String tagName = getTagName(tagNumber, dataDictionary);
        String value = (String) field.getObject();
        String valueConvertSpecialSymbols = value
                .replace("&", "&amp;")
                .replace(">", "&gt;")
                .replace("<", "&lt;")
                .replace("'", "&apos;")
                .replace("\"", "&quot;");
        String description = null == dataDictionary ? null : dataDictionary.getValueName(tagNumber, value);
        writeTag(builder, tagName,
                () -> {
                    writeAttribute(builder, "fix", String.valueOf(tagNumber));
                    if (description != null) {
                        writeAttribute(builder, "description", description);
                    }
                },
                () -> builder.append(valueConvertSpecialSymbols));
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
