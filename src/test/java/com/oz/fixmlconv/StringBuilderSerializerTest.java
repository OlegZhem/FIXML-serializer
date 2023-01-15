package com.oz.fixmlconv;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class StringBuilderSerializerTest {

    private static final Logger LOG = LoggerFactory.getLogger(StringBuilderSerializerTest.class);


    @Test
    void specialSymbols() throws ConfigError, InvalidMessage, IOException, FieldNotFound {
        FieldMapIteratorFactory fieldMapIteratorFactory = new FieldMapIteratorFactoryMurexStyle();
        String strMesage = "8=FIX.4.4|35=8|52=20200214-11:00:50.252946|100=AA&BB|";

        String actualFixml = process(fieldMapIteratorFactory, strMesage);

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
                           String strMessage)
            throws InvalidMessage, ConfigError, IOException, FieldNotFound {
        Message message = new FixMsgFactory().withDelimiter("|").parseText(strMessage);
        StringBuilderSerializer stringBuilderSerializer = new StringBuilderSerializer(
                DictionaryManager.dictionaryByMessage(message), fieldMapIteratorFactory);
        String actualFixml = stringBuilderSerializer.serialize(message);
        LOG.info(actualFixml);
        return actualFixml;
    }

}