import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import conveyors.CrossConveyor;
import conveyors.CrossConveyor.Connections;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class CrossConveyorTest {

    @Test
    void create_outOfBoundsConnection() {
        assertThrows(
            IllegalStateException.class, () -> {
                CrossConveyor.builder()
                    .addConveyor("A", 3)
                    .addConveyor("B", 5)
                    .connect(Connections.builder()
                        .byIndex(3, "A", "B")
                        .build())
                    .build();
            }
        );
    }

    @Test
    void create_nonExistentConnection() {
        assertThrows(
            IllegalStateException.class, () -> {
                CrossConveyor.builder()
                    .addConveyor("A", 3)
                    .addConveyor("B", 5)
                    .connect(Connections.builder()
                        .byIndex(2, "A", "B", "C")
                        .build())
                    .build();
            }
        );
    }

    @Test
    void put_oneConveyor() {
        final CrossConveyor conveyor = CrossConveyor.builder()
            .addConveyor("A", 3)
            .addConveyor("B", 5)
            .connect(Connections.builder()
                .byIndex(2, "A", "B")
                .build())
            .build();

        final List<Integer> expected = new ArrayList<>(4) {{
            this.add(null);
            this.add(null);
            this.add(null);
            this.add(0);
        }};

        final List<Integer> results = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            results.add(conveyor.put("A", i));
        }

        assertEquals(expected, results);
    }

    @Test
    void put_twoConveyors() {
        final CrossConveyor conveyor = CrossConveyor.builder()
            .addConveyor("A", 3)
            .addConveyor("B", 3)
            .connect(Connections.builder()
                .byIndex(2, "A", "B")
                .build())
            .build();

        final List<Integer> expected = new ArrayList<>(8) {{
            this.add(null);
            this.add(null);
            this.add(null);
            this.add(null);
            this.add(null);
            this.add(0);
            this.add(100);
            this.add(1);
        }};

        final List<Integer> results = new ArrayList<>(8);
        for (int i = 0; i < 4; i++) {
            final Integer a = conveyor.put("A", i);
            final Integer b = conveyor.put("B", i + 100);
            results.add(a);
            results.add(b);

        }

        assertEquals(expected, results);
    }

    @Test
    void put_nonExistentConveyor() {
        final CrossConveyor conveyor = CrossConveyor.builder()
            .addConveyor("A", 3)
            .addConveyor("B", 3)
            .connect(Connections.builder()
                .byIndex(2, "A", "B")
                .build())
            .build();

        assertThrows(IllegalArgumentException.class, () -> conveyor.put("C", 0));
    }

}