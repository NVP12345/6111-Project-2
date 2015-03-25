package domain.infobox;

import util.StringUtils;

import java.util.List;

public abstract class InfoBoxRegion {

    public abstract List<String> getLines(int indent, int length);

    protected String buildIndentWithName(int indent, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ");
        sb.append(name);
        sb.append(": ");
        int indentDifference = indent - name.length() - 3 ;
        if (indentDifference > 0) {
            sb.append(StringUtils.multiplyString(" ", indentDifference));
        }
        return sb.toString();
    }

    protected String buildBlankIndent(int indent) {
        return StringUtils.multiplyString(" ", indent);
    }

    protected String buildTextWithinWidth(String text, int width) {
        if (text.length() > width - 1) {
            return text.substring(0, width - 5) + "...";
        } else {
            return text + StringUtils.multiplyString(" ", width - text.length());
        }
    }

}
