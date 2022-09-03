package com.oz.fixmlconv;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.InvalidMessage;
import quickfix.Message;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JacksonCustomSerializerTest {

    private static final Logger LOG = LoggerFactory.getLogger(JacksonCustomSerializerTest.class);


    private final static String CHAR_SOH = "";

    private static final String REGEX_REPLACE_VERTICAL_SLASH = "\\|(?=((\\d*=)|$))";

    private static Stream<Arguments> provideInput() {
        return Stream.of(
                Arguments.of(Named.of("Murex style", new FieldMapIteratorFactoryMurexStyle()), "fixml/serializerTest_murexStyle.xml"),
                Arguments.of(Named.of("Ordered", new FieldMapIteratorFactoryOrdered()), "fixml/serializerTest_ordered.xml")
        );
    }


    @ParameterizedTest
    @MethodSource("provideInput")
    void serialize(FieldMapIteratorFactory fieldMapIteratorFactory, String expectedXmlFileName)
            throws IOException, ConfigError, InvalidMessage {
        String FIX_MESSAGE = "8=FIX.4.4|9=495|35=8|34=506|49=IFIX-EQ-UAT|52=20200214-11:00:50.252946|" +
                "56=TST1|128=RENC|129=CARE|6=0|11=0123456789|14=250|17=89545922|22=4|31=203.32|32=250|" +
                "37=19052919036|38=3000|39=1|40=1|48=US5949181045|54=2|55=MSFT|60=20200214-11:00:50|" +
                "64=20200218|100=XTKO|150=F|151=2750|159=0.0|207=XTKO|236=0.0|278=174186|526=CARE|625=NORM|" +
                "851=2|5020=20200218|5155=NCC|5459=Y2|6029=USD|6636=N|7693=10455|9412=250501|453=2|448=AF1|" +
                "447=D|452=1|448=AF2|447=D|452=3|10=217|";
        DataDictionary dataDictionary = new DataDictionary("FIX44.xml");

        String actualFixml = process(fieldMapIteratorFactory, FIX_MESSAGE, dataDictionary);

        String expectedFixml = FileHelper.readFile(expectedXmlFileName);
        assertEquals(expectedFixml.replace("\r","").replaceAll("(?<=\\n)[\\t ]+",""), actualFixml.replace("><", ">\n<"));
    }

    @Test
    void specialSymbols() throws ConfigError, InvalidMessage, IOException {
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
                "</fixMessage>";
        assertEquals(expectedFixml, actualFixml.replace("><", ">\n<"));
    }

    private String process(FieldMapIteratorFactory fieldMapIteratorFactory,
                           String FIX_MESSAGE,
                           DataDictionary dataDictionary) throws InvalidMessage, IOException {
        Message message = new Message();
        message.fromString(FIX_MESSAGE.replaceAll(REGEX_REPLACE_VERTICAL_SLASH, CHAR_SOH), dataDictionary, false);
        Writer jsonWriter = new StringWriter();
        ToXmlGenerator toXmlGenerator = new XmlFactory().createGenerator(jsonWriter);
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        SerializerProvider serializerProvider = xmlMapper.getSerializerProvider();
        new JacksonCustomSerializer(dataDictionary, fieldMapIteratorFactory)
                .serialize(message, toXmlGenerator, serializerProvider);
        toXmlGenerator.flush();
        String actualFixml = jsonWriter.toString();
        LOG.info(actualFixml);
        return actualFixml;
    }

}