package dev.dominion.ecs.engine;

import dev.dominion.ecs.api.Component;
import dev.dominion.ecs.engine.collections.ConcurrentIntMap;
import dev.dominion.ecs.engine.collections.ConcurrentPool;
import dev.dominion.ecs.engine.collections.SparseIntMap;
import dev.dominion.ecs.engine.system.ClassIndex;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class LinkedCompositions implements AutoCloseable {

    private final ClassIndex classIndex = new ClassIndex();
    private final ConcurrentPool<LongEntity> pool = new ConcurrentPool<>();
    private final NodeCache nodeCache = new NodeCache();
    private final Node root;

    public LinkedCompositions() {
        root = new Node(null);
        root.composition = new Composition(pool.newTenant());
    }

    @SafeVarargs
    public final Composition getOrCreate(Class<? extends Component>... componentTypes) {
        if (componentTypes.length == 0) {
            return root.composition;
        }
        traverseNode(root, componentTypes);
        return new Composition(pool.newTenant(), componentTypes);
    }

    private void traverseNode(Node currentNode, Class<? extends Component>[] componentTypes) {
        for (Class<? extends Component> componentType : componentTypes) {
            if (currentNode.hasComponentType(componentType)) continue;
            Node node = currentNode.getOrCreateLink(componentType);
            traverseNode(node, componentTypes);
        }
    }

    @SafeVarargs
    public final Set<Composition> query(Class<? extends Component>... componentTypes) {
        return null;
    }

    public ClassIndex getClassIndex() {
        return classIndex;
    }

    public Node getRoot() {
        return root;
    }


    @Override
    public void close() {
        classIndex.close();
        pool.close();
    }

    public final class NodeCache {
        private final Map<Long, Node> data = new ConcurrentHashMap<>();

        public static long componentTypesHashCode(SparseIntMap<Class<? extends Component>> componentTypes) {
            return componentTypes.keysStream()
                    .sorted()
                    .reduce(0, (sum, hashCode) -> 31 * sum + hashCode);
        }

        public Node getOrCreateNode(SparseIntMap<Class<? extends Component>> componentTypes) {
            long key = componentTypesHashCode(componentTypes);

            return data.computeIfAbsent(key, k -> new Node(componentTypes));
        }
    }

    public final class Node {
        private final SparseIntMap<Class<? extends Component>> componentTypes;
        private final SparseIntMap<Node> links = new ConcurrentIntMap<>();
        private Composition composition;

        public Node(SparseIntMap<Class<? extends Component>> componentTypes) {
            this.componentTypes = componentTypes;
        }

        public Node getOrCreateLink(Class<? extends Component> componentType) {
            int key = classIndex.addClass(componentType);
            return links.computeIfAbsent(key,
                    k -> {
                        var cTypes = componentTypes == null ?
                                new ConcurrentIntMap<Class<? extends Component>>() :
                                componentTypes.clone();
                        cTypes.put(k, componentType);
                        return nodeCache.getOrCreateNode(cTypes);
                    });
        }

        public Node getLink(Class<? extends Component> componentType) {
            int key = classIndex.getIndex(componentType);
            if (key == 0) return null;
            return links.get(key);
        }

        public SparseIntMap<Node> getLinks() {
            return SparseIntMap.UnmodifiableView.wrap(links);
        }

        @Override
        public String toString() {
            return componentTypes == null ?
                    "root" :
                    componentTypes.stream()
                            .map(Class::getSimpleName)
                            .sorted()
                            .collect(Collectors.joining(","));
        }

        public boolean hasComponentType(Class<? extends Component> componentType) {
            return componentTypes != null
                    && componentTypes.contains(classIndex.getIndex(componentType));
        }
    }
}
