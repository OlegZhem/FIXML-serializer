package com.oz.fixmlconv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.ApplVerID;

import java.util.HashMap;
import java.util.Map;

public class DictionaryManager {

    private static final Logger LOG = LoggerFactory.getLogger(DictionaryManager.class);


    private static final Map<String, DataDictionary> cachedDict = new HashMap<>();

    public static DataDictionary getDictionary(String dataDictionaryFileName) throws ConfigError {
        if(!cachedDict.containsKey(dataDictionaryFileName)) {
            cachedDict.put(dataDictionaryFileName, new DataDictionary(dataDictionaryFileName));
        }
        return cachedDict.get(dataDictionaryFileName);
    }

    public static DataDictionary dictionaryByMessage(String messageString) throws ConfigError {
        String beginString = MessageUtils.getStringField(messageString, 8);
        if ("FIXT.1.1".equals(beginString)) {
            ApplVerID applVerID;
            String applVerIdString = MessageUtils.getStringField(messageString, 1128);
            if (applVerIdString != null) {
                applVerID = new ApplVerID(applVerIdString);
                beginString = MessageUtils.toBeginString(applVerID);
            }
        }
        if (null != beginString && !beginString.isEmpty()) {
            String dataDictionaryFileName = beginString.replace(".", "") + ".xml";
            return getDictionary(dataDictionaryFileName);
        }
        return null;
    }


    public static DataDictionary dictionaryByMessage(Message message) throws FieldNotFound, ConfigError {
        if (message.getHeader().isSetField(8)) {
            String beginString = message.getHeader().getString(8);
            if ("FIXT.1.1".equals(beginString)) {
                ApplVerID applVerID;
                String applVerIdString = message.getHeader().getString(1128);
                if (applVerIdString != null) {
                    applVerID = new ApplVerID(applVerIdString);
                    beginString = MessageUtils.toBeginString(applVerID);
                }
            }
            if (null != beginString && !beginString.isEmpty()) {
                String dataDictionaryFileName = beginString.replace(".", "") + ".xml";
                return getDictionary(dataDictionaryFileName);
            }
        }
        return null;
    }
}
