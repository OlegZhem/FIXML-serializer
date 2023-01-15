package com.oz.fixmlconv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.junit.jupiter.api.Test;
import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.InvalidMessage;
import quickfix.Message;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NativeConverter {

    private final static String CHAR_SOH = "";

    private static final String REGEX_EXPRESSION_REPLACE_VERTICAL_SLASH = "\\|(?=((\\d+=)|$))";

    @Test
    void serialize() throws ConfigError, InvalidMessage {
        String FIX_MESSAGE = "8=FIX.4.4|9=495|35=8|34=506|49=IFIX-EQ-UAT|52=20200214-11:00:50.252946|56=MU9999900002|128=TEMP|129=CARE|1=ACC1|6=0|11=00022201705ESLO1|14=250|17=89545922|22=4|31=203.32|32=250|37=19052919036|38=3000|39=1|40=1|48=RU0009029557|54=2|55=SBERP|60=20200214-11:00:50|64=20200218|100=RTSX|150=F|151=2750|159=0.0|207=RTSX|236=0.0|278=174186|336=TQBR|526=CARE|625=NORM|851=2|5020=20200218|5155=NCC|5459=Y2|6029=SUR|6636=N|7693=10455|9412=250501|453=2|448=MC9999900000|447=D|452=1|448=FID002|447=D|452=3|10=217|";
        DataDictionary dataDictionary = new DataDictionary("FIX44.xml");
        Message message = new Message();
        message.fromString(FIX_MESSAGE.replaceAll(REGEX_EXPRESSION_REPLACE_VERTICAL_SLASH, CHAR_SOH), dataDictionary, false);
        assertEquals( xmlPrettyPrint, message.toXML(dataDictionary).replace("\r","") );
    }

    private String xmlPrettyPrint = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>\n" +
            "<message>\n" +
            "    <header>\n" +
            "        <field name=\"BeginString\" tag=\"8\"><![CDATA[FIX.4.4]]></field>\n" +
            "        <field name=\"BodyLength\" tag=\"9\"><![CDATA[495]]></field>\n" +
            "        <field name=\"MsgSeqNum\" tag=\"34\"><![CDATA[506]]></field>\n" +
            "        <field enum=\"ExecutionReport\" name=\"MsgType\" tag=\"35\"><![CDATA[8]]></field>\n" +
            "        <field name=\"SenderCompID\" tag=\"49\"><![CDATA[IFIX-EQ-UAT]]></field>\n" +
            "        <field name=\"SendingTime\" tag=\"52\"><![CDATA[20200214-11:00:50.252946]]></field>\n" +
            "        <field name=\"TargetCompID\" tag=\"56\"><![CDATA[MU9999900002]]></field>\n" +
            "        <field name=\"DeliverToCompID\" tag=\"128\"><![CDATA[TEMP]]></field>\n" +
            "        <field name=\"DeliverToSubID\" tag=\"129\"><![CDATA[CARE]]></field>\n" +
            "    </header>\n" +
            "    <body>\n" +
            "        <field name=\"Account\" tag=\"1\"><![CDATA[ACC1]]></field>\n" +
            "        <field name=\"AvgPx\" tag=\"6\"><![CDATA[0]]></field>\n" +
            "        <field name=\"ClOrdID\" tag=\"11\"><![CDATA[00022201705ESLO1]]></field>\n" +
            "        <field name=\"CumQty\" tag=\"14\"><![CDATA[250]]></field>\n" +
            "        <field name=\"ExecID\" tag=\"17\"><![CDATA[89545922]]></field>\n" +
            "        <field enum=\"ISIN_NUMBER\" name=\"SecurityIDSource\" tag=\"22\"><![CDATA[4]]></field>\n" +
            "        <field name=\"LastPx\" tag=\"31\"><![CDATA[203.32]]></field>\n" +
            "        <field name=\"LastQty\" tag=\"32\"><![CDATA[250]]></field>\n" +
            "        <field name=\"OrderID\" tag=\"37\"><![CDATA[19052919036]]></field>\n" +
            "        <field name=\"OrderQty\" tag=\"38\"><![CDATA[3000]]></field>\n" +
            "        <field enum=\"PARTIALLY_FILLED\" name=\"OrdStatus\" tag=\"39\"><![CDATA[1]]></field>\n" +
            "        <field enum=\"MARKET\" name=\"OrdType\" tag=\"40\"><![CDATA[1]]></field>\n" +
            "        <field name=\"SecurityID\" tag=\"48\"><![CDATA[RU0009029557]]></field>\n" +
            "        <field enum=\"SELL\" name=\"Side\" tag=\"54\"><![CDATA[2]]></field>\n" +
            "        <field name=\"Symbol\" tag=\"55\"><![CDATA[SBERP]]></field>\n" +
            "        <field name=\"TransactTime\" tag=\"60\"><![CDATA[20200214-11:00:50]]></field>\n" +
            "        <field name=\"SettlDate\" tag=\"64\"><![CDATA[20200218]]></field>\n" +
            "        <field name=\"ExDestination\" tag=\"100\"><![CDATA[RTSX]]></field>\n" +
            "        <field enum=\"TRADE\" name=\"ExecType\" tag=\"150\"><![CDATA[F]]></field>\n" +
            "        <field name=\"LeavesQty\" tag=\"151\"><![CDATA[2750]]></field>\n" +
            "        <field name=\"AccruedInterestAmt\" tag=\"159\"><![CDATA[0.0]]></field>\n" +
            "        <field name=\"SecurityExchange\" tag=\"207\"><![CDATA[RTSX]]></field>\n" +
            "        <field name=\"Yield\" tag=\"236\"><![CDATA[0.0]]></field>\n" +
            "        <field name=\"MDEntryID\" tag=\"278\"><![CDATA[174186]]></field>\n" +
            "        <field name=\"TradingSessionID\" tag=\"336\"><![CDATA[TQBR]]></field>\n" +
            "        <field name=\"NoPartyIDs\" tag=\"453\"><![CDATA[2]]></field>\n" +
            "        <field name=\"SecondaryClOrdID\" tag=\"526\"><![CDATA[CARE]]></field>\n" +
            "        <field name=\"TradingSessionSubID\" tag=\"625\"><![CDATA[NORM]]></field>\n" +
            "        <field enum=\"REMOVED_LIQUIDITY\" name=\"LastLiquidityInd\" tag=\"851\"><![CDATA[2]]></field>\n" +
            "        <field tag=\"5020\"><![CDATA[20200218]]></field>\n" +
            "        <field tag=\"5155\"><![CDATA[NCC]]></field>\n" +
            "        <field tag=\"5459\"><![CDATA[Y2]]></field>\n" +
            "        <field tag=\"6029\"><![CDATA[SUR]]></field>\n" +
            "        <field tag=\"6636\"><![CDATA[N]]></field>\n" +
            "        <field tag=\"7693\"><![CDATA[10455]]></field>\n" +
            "        <field tag=\"9412\"><![CDATA[250501]]></field>\n" +
            "        <groups name=\"NoPartyIDs\" tag=\"453\">\n" +
            "            <group>\n" +
            "                <field name=\"PartyID\" tag=\"448\"><![CDATA[MC9999900000]]></field>\n" +
            "                <field enum=\"PROPRIETARY_CUSTOM_CODE\" name=\"PartyIDSource\" tag=\"447\"><![CDATA[D]]></field>\n" +
            "                <field enum=\"EXECUTING_FIRM\" name=\"PartyRole\" tag=\"452\"><![CDATA[1]]></field>\n" +
            "            </group>\n" +
            "            <group>\n" +
            "                <field name=\"PartyID\" tag=\"448\"><![CDATA[FID002]]></field>\n" +
            "                <field enum=\"PROPRIETARY_CUSTOM_CODE\" name=\"PartyIDSource\" tag=\"447\"><![CDATA[D]]></field>\n" +
            "                <field enum=\"CLIENT_ID\" name=\"PartyRole\" tag=\"452\"><![CDATA[3]]></field>\n" +
            "            </group>\n" +
            "        </groups>\n" +
            "    </body>\n" +
            "    <trailer>\n" +
            "        <field name=\"CheckSum\" tag=\"10\"><![CDATA[217]]></field>\n" +
            "    </trailer>\n" +
            "</message>\n";

}
