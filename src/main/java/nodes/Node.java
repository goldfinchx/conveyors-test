package nodes;

public interface Node {

    void setValue(Integer newValue);
    Integer getValue();
    Node next(String key);

    default Node push(Node current, String key, Integer value) {
        final Integer previousValue = current.getValue();
        current.setValue(value);

        if (current.next(key) == null) {
            return new LinearNode(previousValue, current);
        }

        return this.push(current.next(key), key, previousValue);
    }

    default Node push(String key, Integer value) {
        return this.push(this, key, value);
    }

    default String stringify(String key) {
        Node node = this;
        final StringBuilder stringBuilder = new StringBuilder();
        final int width = 2;

        while (node != null) {
            final boolean isCrossNode = node instanceof CrossNode;
            String value = String.valueOf(node.getValue());
            if (value.equals("null")) {
                value = "X";
            }

            stringBuilder
                .append(isCrossNode ? "{" : "")
                .append(String.format("%-" + width + "s", value))
                .append(isCrossNode ? "}" : "")
                .append(" ");
            node = node.next(key);
        }

        return stringBuilder.toString();
    }

}
