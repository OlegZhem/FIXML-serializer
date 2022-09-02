package com.oz.fixmlconv;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import quickfix.ConfigError;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SerializerTest {

    @Test
    void noDict_simple() throws JsonProcessingException {
        Message message = new Message();
        message.getHeader().setString(8, "FIX.4.4");
        message.getHeader().setString(35, "8");
        Group parties = new Group(453, 448, new int[]{448, 447, 452, 802, 0});
        parties.setString(447, "D");
        parties.setString(448, "AF1");
        parties.setString(452, "1");
        message.addGroupRef(parties);
        message.toString();
        String fixml = new Serializer().toFixml(message);
        assertEquals("<fixMessage>\n" +
                "  <header>\n" +
                "    <BeginString fix=\"8\">FIX.4.4</BeginString>\n" +
                "    <BodyLength fix=\"9\">100</BodyLength>\n" +
                "    <MsgType fix=\"35\" description=\"ExecutionReport\">8</MsgType>\n" +
                "  </header>\n" +
                "  <body>\n" +
                "    <NoPartyIDs fix=\"453\" itemCount=\"1\">\n" +
                "      <item>\n" +
                "        <PartyIDSource fix=\"447\" description=\"PROPRIETARY_CUSTOM_CODE\">D</PartyIDSource>\n" +
                "        <PartyID fix=\"448\">AF1</PartyID>\n" +
                "        <PartyRole fix=\"452\" description=\"EXECUTING_FIRM\">1</PartyRole>\n" +
                "      </item>\n" +
                "    </NoPartyIDs>\n" +
                "  </body>\n" +
                "  <trailer>\n" +
                "    <CheckSum fix=\"10\">000</CheckSum>\n" +
                "  </trailer>\n" +
                "</fixMessage>\n", fixml.replace("\r",""));
    }

    @Test
    void noDict_noTag8() throws JsonProcessingException {
        Message message = new Message();
        //message.getHeader().setString(8, "FIX.4.4");
        message.getHeader().setString(35, "8");
        Group parties = new Group(453, 448, new int[]{448, 447, 452, 802, 0});
        parties.setString(447, "D");
        parties.setString(448, "AF1");
        parties.setString(452, "1");
        message.addGroupRef(parties);
        message.toString();
        String fixml = new Serializer().toFixml(message);
        assertEquals("<fixMessage>\n" +
                "  <header>\n" +
                "    <Tag9 fix=\"9\">100</Tag9>\n" +
                "    <Tag35 fix=\"35\">8</Tag35>\n" +
                "  </header>\n" +
                "  <body>\n" +
                "    <Tag453 fix=\"453\" itemCount=\"1\">\n" +
                "      <item>\n" +
                "        <Tag447 fix=\"447\">D</Tag447>\n" +
                "        <Tag448 fix=\"448\">AF1</Tag448>\n" +
                "        <Tag452 fix=\"452\">1</Tag452>\n" +
                "      </item>\n" +
                "    </Tag453>\n" +
                "  </body>\n" +
                "  <trailer>\n" +
                "    <Tag10 fix=\"10\">000</Tag10>\n" +
                "  </trailer>\n" +
                "</fixMessage>\n", fixml.replace("\r",""));
    }

    @Test
    void noDict_twoGropus() throws JsonProcessingException {
        Message message = new Message();
        message.getHeader().setString(8, "FIX.4.2");
        message.getHeader().setString(35, "8");
        message.setString(31, "12.3");
        message.setString(32, "50");
        Group parties = new Group(453, 448, new int[]{448, 447, 452, 802, 0});
        parties.setString(447, "D");
        parties.setString(448, "AF1");
        parties.setString(452, "1");
        message.addGroupRef(parties);
        parties = new Group(453, 448, new int[]{448, 447, 452, 802, 0});
        parties.setString(447, "D");
        parties.setString(448, "AF1");
        parties.setString(452, "3");
        message.addGroupRef(parties);
        message.toString();
        String fixml = new Serializer().toFixml(message);
        assertEquals("<fixMessage>\n" +
                "  <header>\n" +
                "    <BeginString fix=\"8\">FIX.4.2</BeginString>\n" +
                "    <BodyLength fix=\"9\">100</BodyLength>\n" +
                "    <MsgType fix=\"35\" description=\"ExecutionReport\">8</MsgType>\n" +
                "  </header>\n" +
                "  <body>\n" +
                "    <LastPx fix=\"31\">12.3</LastPx>\n" +
                "    <LastShares fix=\"32\">50</LastShares>\n" +
                "    <Tag453 fix=\"453\" itemCount=\"2\">\n" +
                "      <item>\n" +
                "        <Tag447 fix=\"447\">D</Tag447>\n" +
                "        <Tag448 fix=\"448\">AF1</Tag448>\n" +
                "        <Tag452 fix=\"452\">1</Tag452>\n" +
                "      </item>\n" +
                "      <item>\n" +
                "        <Tag447 fix=\"447\">D</Tag447>\n" +
                "        <Tag448 fix=\"448\">AF1</Tag448>\n" +
                "        <Tag452 fix=\"452\">3</Tag452>\n" +
                "      </item>\n" +
                "    </Tag453>\n" +
                "  </body>\n" +
                "  <trailer>\n" +
                "    <CheckSum fix=\"10\">000</CheckSum>\n" +
                "  </trailer>\n" +
                "</fixMessage>\n", fixml.replace("\r",""));
    }

    @Test
    void noDict_typedMessages() throws JsonProcessingException {
        quickfix.fix44.ExecutionReport message = new quickfix.fix44.ExecutionReport(
                new OrderID("order1"), new ExecID("exec1"), new ExecType(ExecType.PARTIAL_FILL),
                new OrdStatus(OrdStatus.PARTIALLY_FILLED), new Side(Side.BUY), new LeavesQty(200),
                new CumQty(1000), new AvgPx(1.23));
        quickfix.fix44.component.Parties parties = new quickfix.fix44.component.Parties();
        quickfix.fix44.component.Parties.NoPartyIDs party = new quickfix.fix44.component.Parties.NoPartyIDs();
        party.set(new PartyID("AF1"));
        party.set(new PartyRole(PartyRole.BUYER_SELLER));
        party.set(new PartyIDSource(PartyIDSource.PROPRIETARY_CUSTOM_CODE));
        parties.addGroupRef(party);
        party = new quickfix.fix44.component.Parties.NoPartyIDs();
        party.set(new PartyID("AF2"));
        party.set(new PartyRole(PartyRole.AGENT));
        party.set(new PartyIDSource(PartyIDSource.PROPRIETARY_CUSTOM_CODE));
        parties.addGroupRef(party);
        message.set(parties);
        String fixml = new Serializer().toFixml(message);
        assertEquals("<fixMessage>\n" +
                "  <header>\n" +
                "    <BeginString fix=\"8\">FIX.4.4</BeginString>\n" +
                "    <MsgType fix=\"35\" description=\"ExecutionReport\">8</MsgType>\n" +
                "  </header>\n" +
                "  <body>\n" +
                "    <AvgPx fix=\"6\">1.23</AvgPx>\n" +
                "    <CumQty fix=\"14\">1000</CumQty>\n" +
                "    <ExecID fix=\"17\">exec1</ExecID>\n" +
                "    <OrderID fix=\"37\">order1</OrderID>\n" +
                "    <OrdStatus fix=\"39\" description=\"PARTIALLY_FILLED\">1</OrdStatus>\n" +
                "    <Side fix=\"54\" description=\"BUY\">1</Side>\n" +
                "    <ExecType fix=\"150\" description=\"PARTIAL_FILL\">1</ExecType>\n" +
                "    <LeavesQty fix=\"151\">200</LeavesQty>\n" +
                "    <NoPartyIDs fix=\"453\" itemCount=\"2\">\n" +
                "      <item>\n" +
                "        <PartyIDSource fix=\"447\" description=\"PROPRIETARY_CUSTOM_CODE\">D</PartyIDSource>\n" +
                "        <PartyID fix=\"448\">AF1</PartyID>\n" +
                "        <PartyRole fix=\"452\" description=\"BUYER_SELLER\">27</PartyRole>\n" +
                "      </item>\n" +
                "      <item>\n" +
                "        <PartyIDSource fix=\"447\" description=\"PROPRIETARY_CUSTOM_CODE\">D</PartyIDSource>\n" +
                "        <PartyID fix=\"448\">AF2</PartyID>\n" +
                "        <PartyRole fix=\"452\" description=\"AGENT\">30</PartyRole>\n" +
                "      </item>\n" +
                "    </NoPartyIDs>\n" +
                "  </body>\n" +
                "  <trailer/>\n" +
                "</fixMessage>\n", fixml.replace("\r",""));
    }

    @Test
    void dict_simple() throws JsonProcessingException, ConfigError {
        Message message = new Message();
        message.getHeader().setString(8, "FIX.4.4");
        message.getHeader().setString(35, "8");
        Group charges = new Group(5000, 5001, new int[]{5001, 5002, 5003, 5004, 0});
        charges.setString(5001, "SEC FEE");
        charges.setString(5002, "MARKET");
        charges.setDecimal(5003, BigDecimal.valueOf(1));
        charges.setDecimal(5004, BigDecimal.valueOf(11.59));
        message.addGroupRef(charges);
        message.toString();
        //String fixml = new Serializer("src/test/resources/dict/customFIX44.xml").toFixml(message);
        String fixml = new Serializer("dict/customFIX44.xml").toFixml(message);
        assertEquals("<fixMessage>\n" +
                "  <header>\n" +
                "    <BeginString fix=\"8\">FIX.4.4</BeginString>\n" +
                "    <BodyLength fix=\"9\">100</BodyLength>\n" +
                "    <MsgType fix=\"35\" description=\"ExecutionReport\">8</MsgType>\n" +
                "  </header>\n" +
                "  <body>\n" +
                "    <NumCharges fix=\"5000\" itemCount=\"1\">\n" +
                "      <item>\n" +
                "        <ChargeName fix=\"5001\">SEC FEE</ChargeName>\n" +
                "        <ChargeType fix=\"5002\">MARKET</ChargeType>\n" +
                "        <ChargeRate fix=\"5003\">1</ChargeRate>\n" +
                "        <ChargeAmount fix=\"5004\">11.59</ChargeAmount>\n" +
                "      </item>\n" +
                "    </NumCharges>\n" +
                "  </body>\n" +
                "  <trailer>\n" +
                "    <CheckSum fix=\"10\">000</CheckSum>\n" +
                "  </trailer>\n" +
                "</fixMessage>\n", fixml.replace("\r",""));
    }

}
