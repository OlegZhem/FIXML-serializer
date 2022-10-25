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
    void noDictionaryNoDelimiterSimple() throws ConfigError, FieldNotFound, InvalidMessage {
        Convertor conv = new Convertor();
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

    @Test
    void noDictionaryNoDelimiterGroups() throws ConfigError, FieldNotFound, InvalidMessage {
        Convertor conv = new Convertor();
        Message msg = conv.fromString("8=FIX.4.4|35=8|453=2|448=AF1|447=D|452=1|448=AF2|447=D|452=3|");
        assertEquals(2, msg.getGroupCount(453));
        assertEquals("AF1", msg.getGroup(1,453).getString(448));
        assertEquals("AF2", msg.getGroup(2,453).getString(448));
        msg = conv.fromString("8=FIX.5.0|35=8|453=2|448=AF1|447=D|452=1|448=AF2|447=D|452=3|");
        assertEquals(2, msg.getGroupCount(453));
        assertEquals("AF1", msg.getGroup(1,453).getString(448));
        assertEquals("AF2", msg.getGroup(2,453).getString(448));
    }

    @Test
    void dictionaryGroups() throws ConfigError, FieldNotFound, InvalidMessage {
        Convertor conv = new Convertor().withDelimiter("|").withDataDictionary("dict/customFIX44.xml");
        Message msg = conv.fromString("8=FIX.4.4|35=8|5000=1|5001=SEC FEE|5002=MARKET|5003=1|5004=11.59|");
        assertEquals(1, msg.getGroupCount(5000));
        assertEquals("SEC FEE", msg.getGroup(1,5000).getString(5001));
        assertEquals("MARKET", msg.getGroup(1,5000).getString(5002));
        assertEquals("1", msg.getGroup(1,5000).getString(5003));
        assertEquals("11.59", msg.getGroup(1,5000).getString(5004));
     }

     @Test
    void detectDelimiter() {
         assertNull(Convertor.detectDelimiter("TRACE "));
         assertNull(Convertor.detectDelimiter("TRACE 8="));
         assertNull(Convertor.detectDelimiter("TRACE 8=FIX.4.4"));
         assertNull(Convertor.detectDelimiter("TRACE 8=FIX.4.4<SOH>"));
         assertEquals("|", Convertor.detectDelimiter("TRACE 8=FIX.4.4|35=8|"));
         assertEquals("<SOH>", Convertor.detectDelimiter("TRACE 8=FIX.4.4<SOH>35=8<SOH>"));

         assertNull(Convertor.detectDelimiter("8="));
         assertNull(Convertor.detectDelimiter("8=FIX.4.4"));
         assertNull(Convertor.detectDelimiter("8=FIX.4.4<SOH>"));
         assertEquals("|", Convertor.detectDelimiter("8=FIX.4.4|35=8|"));
         assertEquals("<SOH>", Convertor.detectDelimiter("8=FIX.4.4<SOH>35=8<SOH>"));
     }
}