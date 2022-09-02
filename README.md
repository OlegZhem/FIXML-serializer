# FIXML-serializer
Convert FIX message to FIXML

# Introduction
FIXML-serializer is java 11 library to convert FIX messages to FIXML in [Murex](https://www.murex.com/en) style.

The FIXML Murex style looks like:
```
<fixMessage>
    <header>
        <BeginString fix="8">FIX.4.4</BeginString>
...
    </header>
    <body>
        <AvgPx fix="6">1.37</AvgPx>
...        
    </body>
    <trailer>
...    
    </trailer>
</fixMessage>
```

#Usage
##Quick start
Load data to Message object and generate FIXML using object Serializer.
```
import quickfix.Group;
import quickfix.Message;
import com.oz.fixmlconv.Serializer;
...
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
```
You need to fill tag 8 with the correct value. Otherwise, the names of all tags and groups will look like TagXXX.
You could find more examples in src/test/java/com/oz/fixmlconv/SerializerTest.java

##Custom dictionary
Create a custom data dictionary to add fields and/or groups that do not exist in the FIX standard.
Example of custom dictionary is  src/test/resources/dict/customFIX44.xml.
Use Serializer constructor with path to file of the custom dictionary.
```
import quickfix.Group;
import quickfix.Message;
import com.oz.fixmlconv.Serializer;
...
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
        String fixml = new Serializer("dict/customFIX44.xml").toFixml(message);
```
# Software dependencies
- [QuickFix\J](https://www.quickfixj.org/) version 2.3.1
- [Jackson](https://github.com/FasterXML/jackson) version 2.13.3

# Build and Test
This is a maven project.
Use a maven to build and test the jar library.

