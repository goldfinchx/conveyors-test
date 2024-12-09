import conveyors.CrossConveyor;
import conveyors.CrossConveyor.Connections;

public class Main {

    public static void main(String[] args) {
        final CrossConveyor conveyor = CrossConveyor.builder()
            .addConveyor("A", 5)
            .addConveyor("B", 5)
            .addConveyor("C", 20)
            .connect(Connections.builder()
                .byIndex(2, "A", "B")
                .allByIndex(4)
                .build())
            .build();

        for (int i = 0; i < 10; i++) {
            conveyor.put("A", i);
            System.out.println(conveyor);
        }

        for (int i = 10; i < 20; i++) {
            conveyor.put("B", i);
            System.out.println(conveyor);
        }

    }

}
