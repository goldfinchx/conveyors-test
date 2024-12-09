package nodes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinearNode implements Node {

    private volatile Integer value;
    private Node next;

    public LinearNode(Node next) {
        this.next = next;
    }

    public LinearNode(Integer value, Node next) {
        this.value = value;
        this.next = next;
    }

    @Override
    public Node next(String key) {
        return this.next;
    }
}
