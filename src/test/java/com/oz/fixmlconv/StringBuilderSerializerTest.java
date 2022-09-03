package com.oz.fixmlconv;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class StringBuilderSerializerTest {

    private static final Logger LOG = LoggerFactory.getLogger(StringBuilderSerializerTest.class);


    private final static String CHAR_SOH = "";

    private static final String REGEX_REPLACE_VERTICAL_SLASH = "\\|(?=((\\d*=)|$))";

    @Test
    void specialSymbols() throws ConfigError, InvalidMessage, IOException, FieldNotFound {
        FieldMapIteratorFactory fieldMapIteratorFactory = new FieldMapIteratorFactoryMurexStyle();
        String FIX_MESSAGE = "8=FIX.4.4|35=8|52=20200214-11:00:50.252946|100=AA&BB|";
        DataDictionary dataDictionary = new DataDictionary("FIX44.xml");

        String actualFixml = process(fieldMapIteratorFactory, FIX_MESSAGE, dataDictionary);

        String expectedFixml = "<fixMessage>\n" +
                "<header>\n" +
                "<BeginString fix=\"8\">FIX.4.4</BeginString>\n" +
                "<MsgType fix=\"35\" description=\"ExecutionReport\">8</MsgType>\n" +
                "<SendingTime fix=\"52\">20200214-11:00:50.252946</SendingTime>\n" +
                "</header>\n" +
                "<body>\n" +
                "<ExDestination fix=\"100\">AA&amp;BB</ExDestination>\n" +
                "</body>\n" +
                "<trailer/>\n" +
                "</fixMessage>\n";
        assertEquals(expectedFixml, actualFixml);
    }

    private String process(FieldMapIteratorFactory fieldMapIteratorFactory,
                           String fix_message,
                           DataDictionary dataDictionary)
            throws InvalidMessage, ConfigError, IOException, FieldNotFound {
        Message message = new Message();
        message.fromString(fix_message.replaceAll(REGEX_REPLACE_VERTICAL_SLASH, CHAR_SOH), dataDictionary, false);
        StringBuilderSerializer stringBuilderSerializer =
                new StringBuilderSerializer(dataDictionary, fieldMapIteratorFactory);
        String actualFixml = stringBuilderSerializer.serialize(message);
        LOG.info(actualFixml);
        return actualFixml;
    }

}