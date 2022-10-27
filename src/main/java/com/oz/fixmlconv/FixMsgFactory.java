package com.oz.fixmlconv;

import quickfix.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FixMsgFactory {

    private final static String CHAR_SOH = String.valueOf('\u0001');

    private static final List<String> SPECIAL_REGEX_CHARS = List.of(
            ".","^","$","*","+","-","?","(",")","[","]","{","}","\\","|","—","/" );
    private static final String REGEX_POSITIVE_LOOKAHEAD_AFTER_DELIMITER = "(?=((\\d+=)|$))";
    //private static final String REGEX_REPLACE_VERTICAL_SLASH = "\\|(?=((\\d*=)|$))";

    private final static String MSG_BEGIN = "8=FIX";
    private final static int MSG_BEGIN_LENGTH = MSG_BEGIN.length();
    private final static List<String> FIX_VERSIONS = List.of(  "FIX.4.0",  "FIX.4.1",  "FIX.4.2",
            "FIX.4.3", "FIX.4.4", "FIX.5.0", "FIX.5.0SP1", "FIX.5.0SP2", "FIXT.1.1" );
    private static final String REGEX_TAG_BEGIN = "\\d+=";
    private static final Pattern PATTERN_TAG_BEGIN = Pattern.compile(REGEX_TAG_BEGIN);

    private DataDictionary dataDictionary;
    private String delimiter;

    public FixMsgFactory() {
        dataDictionary = null;
        //delimiter = CHAR_SOH;
        delimiter = null;
    }

    public Message parseText(String messageString) throws ConfigError, InvalidMessage {
        Message message = new Message();
        String sohMessageString = replaceDelimiterToSOH(messageString);
        DataDictionary usedDictionary = dataDictionary;
        if(null == usedDictionary) {
            usedDictionary = DictionaryManager.dictionaryByMessage(sohMessageString);
        }
        message.fromString(sohMessageString, usedDictionary, false);
         return message;
    }

    private String findBeginOfMessage(String str) {
        int pos = str.indexOf(MSG_BEGIN);
        return pos < 0 ? str : str.substring(pos);
    }

    public static String detectDelimiter(String str) {
        int posMsgBegin = str.indexOf(MSG_BEGIN);
        if(posMsgBegin >= 0 ) {
            String strMessage = 0 == posMsgBegin ? str : str.substring(posMsgBegin);
            int posVerBegin = posMsgBegin + 2; // 2 - length of "8="
            for(int i = 0; i < FIX_VERSIONS.size(); i++) {
                if(str.startsWith(FIX_VERSIONS.get(i), posVerBegin)) {
                    String strNo8Tag = str.substring(posVerBegin+FIX_VERSIONS.get(i).length());
                    Matcher matcher = PATTERN_TAG_BEGIN.matcher(strNo8Tag);
                    if(matcher.find()) {
                        return strNo8Tag.substring(0, matcher.start());
                    }
                }
            }
        }
        return null;
    }

     private String replaceDelimiterToSOH(String messageString) {
         String currentDelimiter = delimiter;
         if (null == currentDelimiter) {
             currentDelimiter = detectDelimiter(messageString);
             if (SPECIAL_REGEX_CHARS.contains(currentDelimiter)) {
                 currentDelimiter = "\\" + currentDelimiter;
             }
         }
        if(CHAR_SOH.equals(currentDelimiter)) {
            return messageString;
        } else {
            String regex = currentDelimiter + REGEX_POSITIVE_LOOKAHEAD_AFTER_DELIMITER;
           return messageString.replaceAll(regex, CHAR_SOH);
        }
    }


    public FixMsgFactory withDataDictionary(String dictionaryFileName) throws ConfigError {
        if(null == dictionaryFileName || dictionaryFileName.isEmpty()) {
            this.dataDictionary = null;
        } else {
            this.dataDictionary = DictionaryManager.getDictionary(dictionaryFileName);
        }
        return this;
    }

    public FixMsgFactory withDelimiter(String delimiter) {
        if(SPECIAL_REGEX_CHARS.contains(delimiter)) {
            this.delimiter = "\\" + delimiter;
        } else {
            this.delimiter = delimiter;
        }
        return this;
    }
}
