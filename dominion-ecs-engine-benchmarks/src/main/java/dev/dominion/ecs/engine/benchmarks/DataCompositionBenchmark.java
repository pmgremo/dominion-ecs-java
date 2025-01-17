/*
 * Copyright (c) 2021 Enrico Stara
 * This code is licensed under the MIT license. See the LICENSE file in the project root for license terms.
 */

package dev.dominion.ecs.engine.benchmarks;

import dev.dominion.ecs.engine.DataComposition;
import dev.dominion.ecs.engine.IntEntity;
import dev.dominion.ecs.engine.collections.ChunkedPool;
import dev.dominion.ecs.engine.system.ClassIndex;
import dev.dominion.ecs.engine.system.Config;
import dev.dominion.ecs.engine.system.Logging;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class DataCompositionBenchmark extends DominionBenchmark {

    private static final ChunkedPool.IdSchema ID_SCHEMA =
            new ChunkedPool.IdSchema(Config.DEFAULT_CHUNK_BIT);

    DataComposition composition2;
    Object[] inputArray2 = new Object[]{
            new C3(0)
            , new C1(0)
    };

    DataComposition composition4;
    Object[] inputArray4 = new Object[]{
            new C7(0)
            , new C6(0)
            , new C2(0)
            , new C4(0)
    };
    DataComposition composition8;
    Object[] inputArray8 = new Object[]{
            new C7(0)
            , new C3(0)
            , new C6(0)
            , new C8(0)
            , new C2(0)
            , new C5(0)
            , new C4(0)
            , new C1(0)
    };

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(
                new String[]{DataCompositionBenchmark.class.getName()}
        );
    }

    @Setup(Level.Iteration)
    public void setup() {
        ClassIndex classIndex = new ClassIndex();
        classIndex.addClass(C1.class);
        classIndex.addClass(C2.class);
        classIndex.addClass(C3.class);
        classIndex.addClass(C4.class);
        classIndex.addClass(C5.class);
        classIndex.addClass(C6.class);
        classIndex.addClass(C7.class);
        classIndex.addClass(C8.class);
        composition2 = new DataComposition(null, null, classIndex, null
                , Logging.Context.TEST, C1.class
                , C3.class
        );
        composition4 = new DataComposition(null, null, classIndex, null
                , Logging.Context.TEST, C2.class
                , C4.class
                , C6.class
                , C7.class
        );
        composition8 = new DataComposition(null, null, classIndex, null
                , Logging.Context.TEST, C1.class
                , C2.class
                , C3.class
                , C4.class
                , C5.class
                , C6.class
                , C7.class
                , C8.class
        );
    }

    @Benchmark
    public Object[] sort2ComponentsInPlaceByIndex() {
        return composition2.sortComponentsInPlaceByIndex(inputArray2);
    }

    @Benchmark
    public Object[] sort4ComponentsInPlaceByIndex() {
        return composition4.sortComponentsInPlaceByIndex(inputArray4);
    }

    @Benchmark
    public Object[] sort8ComponentsInPlaceByIndex() {
        return composition8.sortComponentsInPlaceByIndex(inputArray8);
    }

    public static class CompositionSelect1Comp extends DominionBenchmark {
        ChunkedPool.Tenant<IntEntity> tenant;
        DataComposition composition;
        ClassIndex classIndex = new ClassIndex();
        @Param(value = {"10000000"})
        int size;

        {
            classIndex.addClass(C1.class);
        }

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{CompositionSelect1Comp.class.getName().replace('$', '.')}
            );
        }

        @Setup(Level.Iteration)
        public void setup() {
            composition = new DataComposition(null, new ChunkedPool<>(ID_SCHEMA, Logging.Context.TEST), classIndex, ID_SCHEMA, Logging.Context.TEST, C1.class);
            C1 c1 = new C1(0);
            for (int i = 0; i < size; i++) {
                composition.createEntity(false, c1);
            }
        }

        @Benchmark
        public void iterate1Comp(Blackhole bh) {
            var iterator = composition.select(C1.class, composition.getTenant().noItemIterator(), null);
            while (iterator.hasNext()) {
                bh.consume(iterator.next().comp());
            }
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            tenant.close();
        }
    }

    record C1(int id) {
    }

    record C2(int id) {
    }

    record C3(int id) {
    }

    record C4(int id) {
    }

    record C5(int id) {
    }

    record C6(int id) {
    }

    record C7(int id) {
    }

    record C8(int id) {
    }
}
