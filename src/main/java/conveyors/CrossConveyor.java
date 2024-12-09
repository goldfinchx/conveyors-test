package conveyors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.NonNull;
import nodes.CrossNode;
import nodes.Node;
import nodes.LinearNode;


public class CrossConveyor {

    private final ConcurrentHashMap<String, Node> conveyors;

    private CrossConveyor(ConcurrentHashMap<String, Node> conveyors) {
        this.conveyors = conveyors;
    }

    public synchronized Integer put(String key, Integer value) {
        if (!this.conveyors.containsKey(key)) {
            throw new IllegalArgumentException("Conveyor with key " + key + " does not exist");
        }

        final Node target = this.conveyors.get(key);
        final Node result = target.push(key, value);
        this.conveyors.put(key, new LinearNode(target).next(key));
        return result.getValue();
    }


    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        this.conveyors.forEach((key, value) -> {
            stringBuilder
                .append(key)
                .append(": ")
                .append(value.stringify(key))
                .append("\n");
        });

        return stringBuilder.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, Integer> conveyors = new HashMap<>();
        private Map<Integer, String[]> connections = new HashMap<>();
        private final Map<Integer, CrossNode> crossNodes = new HashMap<>();

        /**
         * Adds a conveyor with the specified id and size.
         *
         * @param id   the id of the conveyor
         * @param size the size of the conveyor
         */
        public Builder addConveyor(@NonNull String id, int size) {
            if (size < 1) {
                throw new IllegalArgumentException("Size of the conveyor must be greater than 0");
            }

            this.conveyors.put(id, size);
            return this;
        }

        /**
         * Adds multiple conveyors with the specified ids and sizes.
         *
         * @param conveyors the conveyors to add
         */
        public Builder addConveyors(@NonNull Map<String, Integer> conveyors) {
            this.conveyors.putAll(conveyors);
            return this;
        }

        /**
         * Connects the conveyors based on the specified connections.
         *
         * @param connections the connections between nodes
         */
        public Builder connect(@NonNull Connections connections) {
            this.connections = connections.getValues();
            return this;
        }

        /**
         * Builds the conveyors based on the specified configuration.
         *
         * @return the built cross conveyor
         */
        public CrossConveyor build() {
            this.checkNonExistentConnections();
            this.checkOutOfBoundsConnections();

            final ConcurrentHashMap<String, Node> result = new ConcurrentHashMap<>();
            this.conveyors.forEach((key, size) -> {
                final Node node = this.buildNode(key, size);
                result.put(key, node);
            });

            return new CrossConveyor(result);
        }

        /**
         * Builds a node based on the specified key and size.
         *
         * @param key  the key of the node
         * @param size the size of the node
         * @return the built node
         */
        private Node buildNode(String key, int size) {
            Node node = null;

            for (int index = size - 1; index >= 0; index--) {
                if (!this.connections.containsKey(index)) {
                    node = new LinearNode(node);
                    continue;
                }

                final CrossNode crossNode = this.crossNodes.computeIfAbsent(index, $ -> new CrossNode());
                crossNode.connect(key, node);
                node = crossNode;
            }

            return node;
        }

        /**
         * Checks if there are any non-existent connections.
         * If there are, an {@link IllegalStateException} is thrown.
         */
        private void checkNonExistentConnections() {
            final List<String> nonExistentConnections = this.connections.values()
                .stream()
                .flatMap(Arrays::stream)
                .distinct()
                .filter(conveyor -> !this.conveyors.containsKey(conveyor))
                .toList();

            if (nonExistentConnections.isEmpty()) {
                return;
            }

            throw new IllegalStateException("Found non-existent connections: " + nonExistentConnections);
        }


        /**
         * Checks if there are any out-of-bounds connections.
         * If there are, an {@link IllegalStateException} is thrown.
         */
        private void checkOutOfBoundsConnections() {
            final List<String> caughtOutOfBounds = this.connections.entrySet()
                .stream()
                .map(entry -> {
                    final List<String> outOfBounds = new ArrayList<>();
                    for (final String value : entry.getValue()) {
                        if (this.conveyors.get(value) > entry.getKey()) {
                            continue;
                        }

                        outOfBounds.add(value);
                    }

                    return outOfBounds;
                })
                .flatMap(List::stream)
                .toList();

            if (caughtOutOfBounds.isEmpty()) {
                return;
            }

            throw new IllegalStateException("Found out-of-bounds connections: " + caughtOutOfBounds);
        }
    }

    /**
     * Represents the connections between nodes.
     */
    @Getter
    public static class Connections {

        private final Map<Integer, String[]> values;

        private Connections(Map<Integer, String[]> values) {
            this.values = values;
        }

        public Connections() {
            this.values = new HashMap<>();
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            public Map<Integer, String[]> connections = new HashMap<>();

            /**
             * Adds a connection between the specified index and conveyors.
             * @param index the index, where to connect the conveyors
             * @param conveyors the conveyors to connect
             */
            public Builder byIndex(int index, String... conveyors) {
                this.connections.put(index, conveyors);
                return this;
            }

            /**
             * Adds a connection between all conveyors by the specified index.
             * @param index the index, where to connect the conveyors
             */
            public Builder allByIndex(int index) {
                this.connections.put(index, new String[0]);
                return this;
            }

            /**
             * Builds the connections.
             * @return the built connections
             */
            public Connections build() {
                return new Connections(this.connections);
            }
        }

    }
}

