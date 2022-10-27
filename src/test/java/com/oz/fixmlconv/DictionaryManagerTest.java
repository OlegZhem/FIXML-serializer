package com.oz.fixmlconv;

import org.junit.jupiter.api.Test;
import quickfix.DataDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class DictionaryManagerTest {

    @Test
    void loadDictionaryOnceOnly() throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<FixMessageConverter> convertors = new ArrayList<>();
        List<Callable<DataDictionary>> tasks = new ArrayList<>();
        for(int i=0; i <10; i++) {
            tasks.add( () -> DictionaryManager.getDictionary("dict/customFIX44.xml"));
        }
        List<Future<DataDictionary>> futures = executorService.invokeAll(tasks);
        List<DataDictionary> dictionaries = new ArrayList<>();
        for(int i=0; i <10; i++) {
            dictionaries.add(futures.get(i).get(100, TimeUnit.MILLISECONDS));
        }
        for(int i=0; i <9; i++) {
            assertEquals(dictionaries.get(i), dictionaries.get(i+1));
        }
    }
}