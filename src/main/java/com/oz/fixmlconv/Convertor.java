package com.oz.fixmlconv;

import quickfix.*;
import quickfix.field.ApplVerID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Convertor {

    private final static String CHAR_SOH = String.valueOf('\u0001');

    private static final List<String> SPECIAL_REGEX_CHARS = List.of(
            ".","^","$","*","+","-","?","(",")","[","]","{","}","\\","|","—","/" );
     private static final String REGEX_POSITIVE_LOOKAHEAD_AFTER_DELIMITER = "(?=((\\d*=)|$))";
    private static final String REGEX_REPLACE_VERTICAL_SLASH = "\\|(?=((\\d*=)|$))";

    private DataDictionary dataDictionary;
    private String delimiter;

    public Convertor() throws ConfigError {
        dataDictionary = null;
        delimiter = CHAR_SOH;
    }

    public Message fromString(String messageString) throws ConfigError, InvalidMessage {
        Message message = new Message();
        String sohMessageString = replaceDelimiterToSOH(messageString);
        DataDictionary usedDictionary = dataDictionary;
        if(null == usedDictionary) {
            usedDictionary = DictionaryManager.dictionaryByMessage(sohMessageString);
        }
        message.fromString(sohMessageString, usedDictionary, false);
         return message;
    }

     private String replaceDelimiterToSOH(String messageString) {
        if(CHAR_SOH.equals(delimiter)) {
            return messageString;
        } else {
            String regex = delimiter + REGEX_POSITIVE_LOOKAHEAD_AFTER_DELIMITER;
           return messageString.replaceAll(regex, CHAR_SOH);
        }
    }


    public Convertor withDataDictionary(String dictionaryFileName) throws ConfigError {
        if(null == dictionaryFileName || dictionaryFileName.isEmpty()) {
            this.dataDictionary = null;
        } else {
            this.dataDictionary = new DataDictionary(dictionaryFileName);
        }
        return this;
    }

    public Convertor withDataDictionary(DataDictionary dictionary) throws ConfigError {
        this.dataDictionary = dictionary;
        return this;
    }

    public Convertor withDelimiter(String delimiter) {
        if(SPECIAL_REGEX_CHARS.contains(delimiter)) {
            this.delimiter = "\\"+delimiter;
        } else {
            this.delimiter = delimiter;
        }
        return this;
    }
}
