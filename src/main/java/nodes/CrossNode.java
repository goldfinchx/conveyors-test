package nodes;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrossNode implements Node {

    private volatile Integer value;
    private final Map<String, Node> connected = new HashMap<>();

    @Override
    public Node next(String key) {
        return this.connected.get(key);
    }

    public void connect(String key, Node node) {
        this.connected.put(key, node);
    }
}
