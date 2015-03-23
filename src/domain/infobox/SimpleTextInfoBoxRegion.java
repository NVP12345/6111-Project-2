package domain.infobox;

import util.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class SimpleTextInfoBoxRegion extends InfoBoxRegion {

    private final String name;
    private final String value;

    public SimpleTextInfoBoxRegion(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public List<String> getLines(int indent, int length) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append(name);
        sb.append(": ");
        int indentDifference = indent - name.length() - 2 ;
        if (indentDifference > 0) {
            sb.append(StringUtils.multiplyString(" ", indentDifference));
        }
        if (value.length() > length - 1) {
            sb.append(value.substring(0, value.length() - 5));
            sb.append("...");
        } else {
            sb.append(value);
        }
        sb.append(StringUtils.multiplyString(" ", length - value.length() - 1));

        List<String> lines = new LinkedList<String>();
        lines.add(sb.toString());
        return lines;
    }

}
