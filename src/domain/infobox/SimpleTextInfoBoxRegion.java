package domain.infobox;

import java.util.LinkedList;
import java.util.List;

public class SimpleTextInfoBoxRegion extends InfoBoxRegion {

    private final String name;
    private final List<String> values;

    public SimpleTextInfoBoxRegion(String name, String value) {
        this.name = name;
        values = new LinkedList<String>();
        values.add(value);
    }

    public SimpleTextInfoBoxRegion(String name, List<String> values) {
        this.name = name;
        this.values = values;
    }

    @Override
    public List<String> getLines(int indent, int length) {
        List<String> lines = new LinkedList<String>();
        boolean first = true;
        for (String value : values) {
            StringBuilder sb = new StringBuilder();
            if (first) {
                first = false;
                sb.append(buildIndentWithName(indent, name));
            } else {
                sb.append(buildBlankIndent(indent));
            }
            sb.append(buildTextWithinWidth(value, length));
            lines.add(sb.toString());
        }
        return lines;
    }

}
