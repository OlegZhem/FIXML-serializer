package com.oz.fixmlconv;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.ConfigError;
import quickfix.FieldNotFound;
import quickfix.InvalidMessage;
import quickfix.Message;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

public class StringBuilderSerializerTest_JMH {

    private static final Logger LOG = LoggerFactory.getLogger(JacksonCustomSerializerTest_JMH.class);

    @Test
    public void executeJmhRunner() throws RunnerException {
        Options jmhRunnerOptions = new OptionsBuilder()
                // set the class name regex for benchmarks to search for to the current class
                .include("\\." + this.getClass().getSimpleName() + "\\.")
                .mode(Mode.All)
                .timeUnit(TimeUnit.MILLISECONDS)
                .warmupTime(TimeValue.seconds(2))
                .warmupIterations(5)
                .measurementTime(TimeValue.seconds(2))
                .measurementIterations(20)
                .forks(1)
                .threads(1)
                .shouldDoGC(true)
                .shouldFailOnError(true)
                .resultFormat(ResultFormatType.TEXT)
                .result("perf/" + this.getClass().getSimpleName() + ".pr") // set this to a valid filename if you want reports
                .jvmArgs("-server")
                .build();

        new Runner(jmhRunnerOptions).run();
    }

    @State(Scope.Thread)
    public static class BenchmarkState {
        public static  int iteration = 20;
        public static Message message;
        public StringBuilderSerializer stringBuilderSerializer;

        @Param({"Murex style", "Ordered"})
        public String iteratorType;

        @Setup(Level.Trial)
        public void initializeOne() throws ConfigError, InvalidMessage {
            String strMessage = "8=FIX.4.4|9=495|35=8|34=506|49=IFIX-EQ-UAT|52=20200214-11:00:50.252946|" +
                    "56=TST1|128=RENC|129=CARE|6=0|11=0123456789|14=250|17=89545922|22=4|31=203.32|32=250|" +
                    "37=19052919036|38=3000|39=1|40=1|48=US5949181045|54=2|55=MSFT|60=20200214-11:00:50|" +
                    "64=20200218|100=XTKO|150=F|151=2750|159=0.0|207=XTKO|236=0.0|278=174186|526=CARE|625=NORM|" +
                    "851=2|5020=20200218|5155=NCC|5459=Y2|6029=USD|6636=N|7693=10455|9412=250501|453=2|448=AF1|" +
                    "447=D|452=1|448=AF2|447=D|452=3|10=217|";
            message = new FixMsgFactory().withDelimiter("|").parseText(strMessage);
        }

        @Setup(Level.Invocation)
        public void initializeEach() throws ConfigError, FieldNotFound, IOException {
            FieldMapIteratorFactory fieldMapIteratorFactory = iteratorType == "Murex style" ?
                    new FieldMapIteratorFactoryMurexStyle() : new FieldMapIteratorFactoryOrdered();

            stringBuilderSerializer = new StringBuilderSerializer(
                    DictionaryManager.dictionaryByMessage(message), fieldMapIteratorFactory);
        }

/*
        @TearDown(Level.Invocation)
        public void doTearDownEach() throws IOException {
        }
*/

    }

    @Benchmark
    public void benchmarkMethod(BenchmarkState state, Blackhole bh) throws Exception {
        String actualFixml = state.stringBuilderSerializer.serialize(state.message);
        bh.consume(actualFixml);
    }
}
