/*
 * Copyright (c) 2021 Enrico Stara
 * This code is licensed under the MIT license. See the LICENSE file in the project root for license terms.
 */

package dev.dominion.ecs.engine.benchmarks;

import dev.dominion.ecs.api.Entity;
import dev.dominion.ecs.engine.EntityRepository;
import dev.dominion.ecs.engine.IntEntity;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;

public class EntityBenchmark extends DominionBenchmark {

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(
                new String[]{fetchBenchmarkName(EntityBenchmark.class)}
        );
    }

    enum State1 {
        ONE,
        TWO
    }

    record C0(int id) {
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

    public static class EntityLayout {
        public static void main(String[] args) {
            System.out.println(VM.current().details());
            System.out.println(ClassLayout.parseClass(IntEntity.class).toPrintable());
            System.out.println(ClassLayout.parseInstance(new IntEntity(0, null, null)).toPrintable());
        }
    }

    public static class EntityMethodBenchmark extends DominionBenchmark {
        EntityRepository entityRepository;
        Entity[] entities;

        @Param(value = {"1000000"})
        int size;

        @Setup(Level.Iteration)
        public void setup() {
            entityRepository = (EntityRepository) new EntityRepository.Factory().create();
            entities = new Entity[size];
            for (int i = 0; i < size; i++) {
                entities[i] = entityRepository.createEntity();
            }
        }

        @Setup(Level.Invocation)
        public void setupInvocation() {
            for (int i = 0; i < size; i++) {
                entityRepository.deleteEntity(entities[i]);
                entities[i] = entityRepository.createEntity(getInput());
            }
        }

        public Object[] getInput() {
            return null;
        }


        @TearDown(Level.Iteration)
        public void tearDown() {
            entityRepository.close();
        }
    }

    // Add

    public static class AddUpTo extends EntityMethodBenchmark {
        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(AddUpTo.class)}
            );
        }
    }

    public static class AddUpTo01 extends AddUpTo {
        Object[] input = new Object[]{};

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(AddUpTo01.class)}
            );
        }

        @Benchmark
        public void add(Blackhole bh) {
            var c0 = new C0(0);
            for (int i = 0; i < size; i++) {
                bh.consume(entities[i].add(c0));
            }
        }

        public Object[] getInput() {
            return input;
        }
    }

    public static class AddUpTo02 extends AddUpTo01 {
        Object[] input = new Object[]{new C1(0)};

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(AddUpTo02.class)}
            );
        }

        public Object[] getInput() {
            return input;
        }
    }

    public static class AddUpTo04 extends AddUpTo01 {
        Object[] input = new Object[]{new C1(0), new C2(0), new C3(0)};

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(AddUpTo04.class)}
            );
        }

        public Object[] getInput() {
            return input;
        }
    }

    public static class AddUpTo08 extends AddUpTo01 {
        Object[] input = new Object[]{
                new C1(0), new C2(0), new C3(0), new C4(0),
                new C5(0), new C6(0), new C7(0)
        };

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(AddUpTo08.class)}
            );
        }

        public Object[] getInput() {
            return input;
        }
    }

    // Remove

    public static class RemoveFrom extends EntityMethodBenchmark {
        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(RemoveFrom.class)}
            );
        }
    }

    public static class RemoveFrom01 extends RemoveFrom {
        Object[] input = new Object[]{new C1(0)};

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(RemoveFrom01.class)}
            );
        }

        @Benchmark
        public void remove(Blackhole bh) {
            for (int i = 0; i < size; i++) {
                bh.consume(entities[i].removeType(C1.class));
            }
        }

        public Object[] getInput() {
            return input;
        }
    }

    public static class RemoveFrom02 extends RemoveFrom01 {
        Object[] input = new Object[]{new C1(0), new C2(0)};

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(RemoveFrom02.class)}
            );
        }

        public Object[] getInput() {
            return input;
        }
    }

    public static class RemoveFrom04 extends RemoveFrom01 {
        Object[] input = new Object[]{new C1(0), new C2(0), new C3(0), new C4(0)};

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(RemoveFrom04.class)}
            );
        }

        public Object[] getInput() {
            return input;
        }
    }

    public static class RemoveFrom08 extends RemoveFrom01 {
        Object[] input = new Object[]{
                new C1(0), new C2(0), new C3(0), new C4(0),
                new C5(0), new C6(0), new C7(0), new C8(0)
        };

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(RemoveFrom08.class)}
            );
        }

        public Object[] getInput() {
            return input;
        }
    }

    public static class SetStateWith extends DominionBenchmark {
        EntityRepository entityRepository;
        Entity[] entities;

        @Param(value = {"1000000"})
        int size;

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(SetStateWith.class)}
            );
        }

        @Setup(Level.Iteration)
        public void setup() {
            entityRepository = (EntityRepository) new EntityRepository.Factory().create();
            entities = new Entity[size];
            for (int i = 0; i < size; i++) {
                entities[i] = entityRepository.createEntity(getInput());
                entities[i].setState(State1.ONE);
            }
        }

        @Setup(Level.Invocation)
        public void setupInvocation() {
            for (int i = 0; i < size; i++) {
                entities[i].setState(State1.ONE);
            }
        }

        public Object[] getInput() {
            return new Object[0];
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            entityRepository.close();
        }
    }

    public static class SetStateWith01 extends SetStateWith {
        Object[] input = new Object[]{new C1(0)};

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(SetStateWith01.class)}
            );
        }

        @Benchmark
        public void setState(Blackhole bh) {
            for (int i = 0; i < size; i++) {
                bh.consume(entities[i].setState(State1.TWO));
            }
        }

        public Object[] getInput() {
            return input;
        }
    }

    public static class SetStateWith02 extends SetStateWith01 {
        Object[] input = new Object[]{new C1(0), new C2(0)};

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(SetStateWith02.class)}
            );
        }

        public Object[] getInput() {
            return input;
        }
    }

    public static class SetStateWith04 extends SetStateWith01 {
        Object[] input = new Object[]{new C1(0), new C2(0), new C3(0), new C4(0)};

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(SetStateWith04.class)}
            );
        }

        public Object[] getInput() {
            return input;
        }
    }

    public static class SetStateWith08 extends SetStateWith01 {
        Object[] input = new Object[]{
                new C1(0), new C2(0), new C3(0), new C4(0),
                new C5(0), new C6(0), new C7(0), new C8(0)
        };

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(SetStateWith08.class)}
            );
        }

        public Object[] getInput() {
            return input;
        }
    }

    // Others

    public static class Has extends DominionBenchmark {
        EntityRepository entityRepository;
        Entity[] entities01;
        Entity[] entities02;
        Entity[] entities04;
        Entity[] entities08;
        Object input01 = new C1(0);
        Object[] input02 = new Object[]{
                new C1(0), new C2(0)
        };
        Object[] input04 = new Object[]{
                new C1(0), new C2(0), new C3(0), new C4(0)
        };
        Object[] input08 = new Object[]{
                new C1(0), new C2(0), new C3(0), new C4(0),
                new C5(0), new C6(0), new C7(0), new C8(0)
        };

        @Param(value = {"1000000"})
        int size;

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(Has.class)}
            );
        }

        @Setup(Level.Iteration)
        public void setup() {
            entityRepository = (EntityRepository) new EntityRepository.Factory().create();
            entities01 = new Entity[size];
            entities02 = new Entity[size];
            entities04 = new Entity[size];
            entities08 = new Entity[size];
            for (int i = 0; i < size; i++) {
                entities01[i] = entityRepository.createEntity(input01);
                entities02[i] = entityRepository.createEntity(input02);
                entities04[i] = entityRepository.createEntity(input04);
                entities08[i] = entityRepository.createEntity(input08);
            }
        }

        @Benchmark
        public void has01(Blackhole bh) {
            for (int i = 0; i < size; i++) {
                bh.consume(entities01[i].has(C1.class));
            }
        }

        @Benchmark
        public void has02(Blackhole bh) {
            for (int i = 0; i < size; i++) {
                bh.consume(entities02[i].has(C1.class));
            }
        }

        @Benchmark
        public void has04(Blackhole bh) {
            for (int i = 0; i < size; i++) {
                bh.consume(entities04[i].has(C1.class));
            }
        }

        @Benchmark
        public void has08(Blackhole bh) {
            for (int i = 0; i < size; i++) {
                bh.consume(entities08[i].has(C1.class));
            }
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            entityRepository.close();
        }
    }

    public static class SetEnabled extends DominionBenchmark {
        EntityRepository entityRepository;
        Entity[] entities;
        Object[] input = new Object[]{
                new C1(0), new C2(0), new C3(0), new C4(0),
                new C5(0), new C6(0), new C7(0), new C8(0),
        };

        @Param(value = {"1000000"})
        int size;

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(SetEnabled.class)}
            );
        }

        @Setup(Level.Iteration)
        public void setup() {
            entityRepository = (EntityRepository) new EntityRepository.Factory().create();
            entities = new Entity[size];
            for (int i = 0; i < size; i++) {
                entities[i] = entityRepository.createEntity(input);
            }
        }

        @Benchmark
        public void setEnabled(Blackhole bh) {
            for (int i = 0; i < size; i++) {
                entities[i].setEnabled(false);
                bh.consume(entities[i].isEnabled());
            }
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            entityRepository.close();
        }
    }


    public static class Contains extends DominionBenchmark {
        EntityRepository entityRepository;
        Entity[] entities01;
        Entity[] entities02;
        Entity[] entities04;
        Entity[] entities08;
        Object input01 = new C1(0);
        Object[] input02 = new Object[]{
                new C1(0), new C2(0)
        };
        Object[] input04 = new Object[]{
                new C1(0), new C2(0), new C3(0), new C4(0)
        };
        Object[] input08 = new Object[]{
                new C1(0), new C2(0), new C3(0), new C4(0),
                new C5(0), new C6(0), new C7(0), new C8(0)
        };

        @Param(value = {"1000000"})
        int size;

        public static void main(String[] args) throws Exception {
            org.openjdk.jmh.Main.main(
                    new String[]{fetchBenchmarkName(Contains.class)}
            );
        }

        @Setup(Level.Iteration)
        public void setup() {
            entityRepository = (EntityRepository) new EntityRepository.Factory().create();
            entities01 = new Entity[size];
            entities02 = new Entity[size];
            entities04 = new Entity[size];
            entities08 = new Entity[size];
            for (int i = 0; i < size; i++) {
                entities01[i] = entityRepository.createEntity(input01);
                entities02[i] = entityRepository.createEntity(input02);
                entities04[i] = entityRepository.createEntity(input04);
                entities08[i] = entityRepository.createEntity(input08);
            }
        }

        @Benchmark
        public void contains01(Blackhole bh) {
            for (int i = 0; i < size; i++) {
                bh.consume(entities01[i].contains(input01));
            }
        }

        @Benchmark
        public void contains02(Blackhole bh) {
            for (int i = 0; i < size; i++) {
                bh.consume(entities02[i].contains(input01));
            }
        }

        @Benchmark
        public void contains04(Blackhole bh) {
            for (int i = 0; i < size; i++) {
                bh.consume(entities04[i].contains(input01));
            }
        }

        @Benchmark
        public void contains08(Blackhole bh) {
            for (int i = 0; i < size; i++) {
                bh.consume(entities08[i].contains(input01));
            }
        }

        @TearDown(Level.Iteration)
        public void tearDown() {
            entityRepository.close();
        }
    }
}
