package com.oz.fixmlconv;

import org.junit.jupiter.api.Test;
import quickfix.ConfigError;
import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.Message;

import static org.junit.jupiter.api.Assertions.*;

class ConvertorTest {

    @Test
    void noDictionarySimple() throws ConfigError, FieldNotFound, InvalidMessage {
        Convertor conv = new Convertor().withDelimiter("|");
        Message msg = conv.fromString("8=FIX.4.4|35=8|");
        assertEquals("FIX.4.4", msg.getHeader().getString(8));
        assertEquals("8", msg.getHeader().getString(35));
    }

    @Test
    void noDictionaryGroups() throws ConfigError, FieldNotFound, InvalidMessage {
        Convertor conv = new Convertor().withDelimiter("|");
        Message msg = conv.fromString("8=FIX.4.4|35=8|453=2|448=AF1|447=D|452=1|448=AF2|447=D|452=3|");
        assertEquals(2, msg.getGroupCount(453));
        assertEquals("AF1", msg.getGroup(1,453).getString(448));
        assertEquals("AF2", msg.getGroup(2,453).getString(448));
        msg = conv.fromString("8=FIX.5.0|35=8|453=2|448=AF1|447=D|452=1|448=AF2|447=D|452=3|");
        assertEquals(2, msg.getGroupCount(453));
        assertEquals("AF1", msg.getGroup(1,453).getString(448));
        assertEquals("AF2", msg.getGroup(2,453).getString(448));
    }
}