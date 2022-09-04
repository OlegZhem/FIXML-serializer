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
    void serialize() throws IOException, ConfigError, InvalidMessage {
        String FIX_MESSAGE = "8=FIX.4.4|9=495|35=8|34=506|49=IFIX-EQ-UAT|52=20200214-11:00:50.252946|56=MU9999900002|128=TEMP|129=CARE|1=ACC1|6=0|11=00022201705ESLO1|14=250|17=89545922|22=4|31=203.32|32=250|37=19052919036|38=3000|39=1|40=1|48=RU0009029557|54=2|55=SBERP|60=20200214-11:00:50|64=20200218|100=RTSX|150=F|151=2750|159=0.0|207=RTSX|236=0.0|278=174186|336=TQBR|526=CARE|625=NORM|851=2|5020=20200218|5155=NCC|5459=Y2|6029=SUR|6636=N|7693=10455|9412=250501|453=2|448=MC9999900000|447=D|452=1|448=FID002|447=D|452=3|10=217|";
        DataDictionary dataDictionary = new DataDictionary("FIX44.xml");
        Message message = new Message();
        message.fromString(FIX_MESSAGE.replaceAll(REGEX_EXPRESSION_REPLACE_VERTICAL_SLASH, CHAR_SOH), dataDictionary, false);
         assertEquals( xmlPrettyPrint, message.toXML(dataDictionary).replace("\r","") );
    }

    private String xmlPrettyPrint = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>\n" +
            "<message>\n" +
            "    <header>\n" +
            "        <field name=\"BeginString\" tag=\"8\">\n" +
            "            <![CDATA[FIX.4.4]]>\n" +
            "        </field>\n" +
            "        <field name=\"BodyLength\" tag=\"9\">\n" +
            "            <![CDATA[495]]>\n" +
            "        </field>\n" +
            "        <field name=\"MsgSeqNum\" tag=\"34\">\n" +
            "            <![CDATA[506]]>\n" +
            "        </field>\n" +
            "        <field enum=\"ExecutionReport\" name=\"MsgType\" tag=\"35\">\n" +
            "            <![CDATA[8]]>\n" +
            "        </field>\n" +
            "        <field name=\"SenderCompID\" tag=\"49\">\n" +
            "            <![CDATA[IFIX-EQ-UAT]]>\n" +
            "        </field>\n" +
            "        <field name=\"SendingTime\" tag=\"52\">\n" +
            "            <![CDATA[20200214-11:00:50.252946]]>\n" +
            "        </field>\n" +
            "        <field name=\"TargetCompID\" tag=\"56\">\n" +
            "            <![CDATA[MU9999900002]]>\n" +
            "        </field>\n" +
            "        <field name=\"DeliverToCompID\" tag=\"128\">\n" +
            "            <![CDATA[TEMP]]>\n" +
            "        </field>\n" +
            "        <field name=\"DeliverToSubID\" tag=\"129\">\n" +
            "            <![CDATA[CARE]]>\n" +
            "        </field>\n" +
            "    </header>\n" +
            "    <body>\n" +
            "        <field name=\"Account\" tag=\"1\">\n" +
            "            <![CDATA[ACC1]]>\n" +
            "        </field>\n" +
            "        <field name=\"AvgPx\" tag=\"6\">\n" +
            "            <![CDATA[0]]>\n" +
            "        </field>\n" +
            "        <field name=\"ClOrdID\" tag=\"11\">\n" +
            "            <![CDATA[00022201705ESLO1]]>\n" +
            "        </field>\n" +
            "        <field name=\"CumQty\" tag=\"14\">\n" +
            "            <![CDATA[250]]>\n" +
            "        </field>\n" +
            "        <field name=\"ExecID\" tag=\"17\">\n" +
            "            <![CDATA[89545922]]>\n" +
            "        </field>\n" +
            "        <field enum=\"ISIN_NUMBER\" name=\"SecurityIDSource\" tag=\"22\">\n" +
            "            <![CDATA[4]]>\n" +
            "        </field>\n" +
            "        <field name=\"LastPx\" tag=\"31\">\n" +
            "            <![CDATA[203.32]]>\n" +
            "        </field>\n" +
            "        <field name=\"LastQty\" tag=\"32\">\n" +
            "            <![CDATA[250]]>\n" +
            "        </field>\n" +
            "        <field name=\"OrderID\" tag=\"37\">\n" +
            "            <![CDATA[19052919036]]>\n" +
            "        </field>\n" +
            "        <field name=\"OrderQty\" tag=\"38\">\n" +
            "            <![CDATA[3000]]>\n" +
            "        </field>\n" +
            "        <field enum=\"PARTIALLY_FILLED\" name=\"OrdStatus\" tag=\"39\">\n" +
            "            <![CDATA[1]]>\n" +
            "        </field>\n" +
            "        <field enum=\"MARKET\" name=\"OrdType\" tag=\"40\">\n" +
            "            <![CDATA[1]]>\n" +
            "        </field>\n" +
            "        <field name=\"SecurityID\" tag=\"48\">\n" +
            "            <![CDATA[RU0009029557]]>\n" +
            "        </field>\n" +
            "        <field enum=\"SELL\" name=\"Side\" tag=\"54\">\n" +
            "            <![CDATA[2]]>\n" +
            "        </field>\n" +
            "        <field name=\"Symbol\" tag=\"55\">\n" +
            "            <![CDATA[SBERP]]>\n" +
            "        </field>\n" +
            "        <field name=\"TransactTime\" tag=\"60\">\n" +
            "            <![CDATA[20200214-11:00:50]]>\n" +
            "        </field>\n" +
            "        <field name=\"SettlDate\" tag=\"64\">\n" +
            "            <![CDATA[20200218]]>\n" +
            "        </field>\n" +
            "        <field name=\"ExDestination\" tag=\"100\">\n" +
            "            <![CDATA[RTSX]]>\n" +
            "        </field>\n" +
            "        <field enum=\"TRADE\" name=\"ExecType\" tag=\"150\">\n" +
            "            <![CDATA[F]]>\n" +
            "        </field>\n" +
            "        <field name=\"LeavesQty\" tag=\"151\">\n" +
            "            <![CDATA[2750]]>\n" +
            "        </field>\n" +
            "        <field name=\"AccruedInterestAmt\" tag=\"159\">\n" +
            "            <![CDATA[0.0]]>\n" +
            "        </field>\n" +
            "        <field name=\"SecurityExchange\" tag=\"207\">\n" +
            "            <![CDATA[RTSX]]>\n" +
            "        </field>\n" +
            "        <field name=\"Yield\" tag=\"236\">\n" +
            "            <![CDATA[0.0]]>\n" +
            "        </field>\n" +
            "        <field name=\"MDEntryID\" tag=\"278\">\n" +
            "            <![CDATA[174186]]>\n" +
            "        </field>\n" +
            "        <field name=\"TradingSessionID\" tag=\"336\">\n" +
            "            <![CDATA[TQBR]]>\n" +
            "        </field>\n" +
            "        <field name=\"NoPartyIDs\" tag=\"453\">\n" +
            "            <![CDATA[2]]>\n" +
            "        </field>\n" +
            "        <field name=\"SecondaryClOrdID\" tag=\"526\">\n" +
            "            <![CDATA[CARE]]>\n" +
            "        </field>\n" +
            "        <field name=\"TradingSessionSubID\" tag=\"625\">\n" +
            "            <![CDATA[NORM]]>\n" +
            "        </field>\n" +
            "        <field enum=\"REMOVED_LIQUIDITY\" name=\"LastLiquidityInd\" tag=\"851\">\n" +
            "            <![CDATA[2]]>\n" +
            "        </field>\n" +
            "        <field tag=\"5020\">\n" +
            "            <![CDATA[20200218]]>\n" +
            "        </field>\n" +
            "        <field tag=\"5155\">\n" +
            "            <![CDATA[NCC]]>\n" +
            "        </field>\n" +
            "        <field tag=\"5459\">\n" +
            "            <![CDATA[Y2]]>\n" +
            "        </field>\n" +
            "        <field tag=\"6029\">\n" +
            "            <![CDATA[SUR]]>\n" +
            "        </field>\n" +
            "        <field tag=\"6636\">\n" +
            "            <![CDATA[N]]>\n" +
            "        </field>\n" +
            "        <field tag=\"7693\">\n" +
            "            <![CDATA[10455]]>\n" +
            "        </field>\n" +
            "        <field tag=\"9412\">\n" +
            "            <![CDATA[250501]]>\n" +
            "        </field>\n" +
            "        <groups name=\"NoPartyIDs\" tag=\"453\">\n" +
            "            <group>\n" +
            "                <field name=\"PartyID\" tag=\"448\">\n" +
            "                    <![CDATA[MC9999900000]]>\n" +
            "                </field>\n" +
            "                <field enum=\"PROPRIETARY_CUSTOM_CODE\" name=\"PartyIDSource\" tag=\"447\">\n" +
            "                    <![CDATA[D]]>\n" +
            "                </field>\n" +
            "                <field enum=\"EXECUTING_FIRM\" name=\"PartyRole\" tag=\"452\">\n" +
            "                    <![CDATA[1]]>\n" +
            "                </field>\n" +
            "            </group>\n" +
            "            <group>\n" +
            "                <field name=\"PartyID\" tag=\"448\">\n" +
            "                    <![CDATA[FID002]]>\n" +
            "                </field>\n" +
            "                <field enum=\"PROPRIETARY_CUSTOM_CODE\" name=\"PartyIDSource\" tag=\"447\">\n" +
            "                    <![CDATA[D]]>\n" +
            "                </field>\n" +
            "                <field enum=\"CLIENT_ID\" name=\"PartyRole\" tag=\"452\">\n" +
            "                    <![CDATA[3]]>\n" +
            "                </field>\n" +
            "            </group>\n" +
            "        </groups>\n" +
            "    </body>\n" +
            "    <trailer>\n" +
            "        <field name=\"CheckSum\" tag=\"10\">\n" +
            "            <![CDATA[217]]>\n" +
            "        </field>\n" +
            "    </trailer>\n" +
            "</message>\n";

}
