/*
 * Copyright (c) 2021 Enrico Stara
 * This code is licensed under the MIT license. See the LICENSE file in the project root for license terms.
 */

package dev.dominion.ecs.engine.benchmarks.collections;

import dev.dominion.ecs.engine.IntEntity;
import dev.dominion.ecs.engine.benchmarks.DominionBenchmark;
import dev.dominion.ecs.engine.collections.ChunkedPool;
import dev.dominion.ecs.engine.system.Config;
import dev.dominion.ecs.engine.system.Logging;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static dev.dominion.ecs.engine.collections.ChunkedPool.IdSchema;
import static dev.dominion.ecs.engine.collections.ChunkedPool.Item;

public class ChunkedPoolBenchmark extends DominionBenchmark {
    private static final IdSchema ID_SCHEMA =
            new IdSchema(Config.DEFAULT_CHUNK_BIT);

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(
                new String[]{fetchBenchmarkName(ChunkedPoolBenchmark.class)}
        );
    }


    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public static class TenantNewId extends DominionBenchmark {
        ChunkedPool.Tenant<IntEntity> tenant;

        @Param(value = {"1000000"})
        int size;

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(TenantNewId.class)}
            );
        }

        @SuppressWarnings("resource")
        @Setup(Level.Invocation)
        public void setup() {
            tenant = new ChunkedPool<IntEntity>(ID_SCHEMA, Logging.Context.TEST).newTenant();
        }

        @SuppressWarnings("UnusedReturnValue")
        @Benchmark
        public void nextId(Blackhole bh) {
            for (int i = 0; i < size; i++) {
                bh.consume(tenant.nextId());
            }
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            tenant.close();
        }
    }


    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public static class TenantReuseId extends DominionBenchmark {
        ChunkedPool.Tenant<IntEntity> tenant;

        @Param(value = {"1000000"})
        int size;
        boolean started;

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(TenantReuseId.class)}
            );
        }

        @SuppressWarnings("resource")
        @Setup(Level.Iteration)
        public void setup() {
            tenant = new ChunkedPool<IntEntity>(ID_SCHEMA, Logging.Context.TEST).newTenant();
            started = false;
        }

        @Setup(Level.Invocation)
        public void setupInvocation() {
            if (!started) return;
            for (int i = 0; i < size; i++) {
                tenant.freeId(i);
            }
        }

        @Benchmark
        public void nextId(Blackhole bh) {
            for (int i = 0; i < size; i++) {
                bh.consume(tenant.nextId());
            }
            started = true;
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            tenant.close();
        }
    }


    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public static class TenantFreeId extends DominionBenchmark {
        ChunkedPool.Tenant<IntEntity> tenant;

        @Param(value = {"1000000"})
        int size;

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(TenantFreeId.class)}
            );
        }

        @SuppressWarnings("resource")
        @Setup(Level.Iteration)
        public void setup() {
            tenant = new ChunkedPool<IntEntity>(ID_SCHEMA, Logging.Context.TEST).newTenant();
        }

        @Setup(Level.Invocation)
        public void setupInvocation() {
            for (int i = 0; i < size; i++) {
                tenant.nextId();
            }
        }

        @Benchmark
        public void freeId(Blackhole bh) {
            for (int i = 0; i < size; i++) {
                bh.consume(tenant.freeId(i));
            }
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            tenant.close();
        }
    }


    public static class TenantIterator extends DominionBenchmark {
        ChunkedPool.Tenant<TestItem> tenant;

        @Param(value = {"100000000"})
        int size;

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(TenantIterator.class)}
            );
        }

        @SuppressWarnings("resource")
        @Setup(Level.Invocation)
        public void setupInvocation() {
            tenant = new ChunkedPool<TestItem>(ID_SCHEMA, Logging.Context.TEST).newTenant();
            for (int i = 0; i < size; i++) {
                tenant.register(new TestItem(tenant.nextId(), null, null), null);
            }
        }

        @Benchmark
        public void iterator(Blackhole bh) {
            Iterator<TestItem> iterator = tenant.iterator();
            while (iterator.hasNext()) {
                bh.consume(iterator.next());
            }
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            tenant.close();
        }

        public record TestItem(int id, Item prev, Item next) implements Item {
            @Override
            public int getId() {
                return id;
            }

            @Override
            public void setId(int id) {
            }

            @Override
            public void setStateId(int id) {
            }

            @Override
            public ChunkedPool.LinkedChunk<? extends Item> getChunk() {
                return null;
            }

            @Override
            public void setChunk(ChunkedPool.LinkedChunk<? extends Item> chunk) {
            }

            @Override
            public void setStateChunk(ChunkedPool.LinkedChunk<? extends Item> chunk) {
            }
        }
    }
}
