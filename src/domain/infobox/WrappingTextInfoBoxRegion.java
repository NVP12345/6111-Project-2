package domain.infobox;

import util.StringUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class WrappingTextInfoBoxRegion extends InfoBoxRegion {

    private final String name;
    private final List<String> textAreas;

    public WrappingTextInfoBoxRegion(String name, List<String> textAreas) {
        this.name = name;
        this.textAreas = textAreas;
    }

    @Override
    public boolean isValid() {
        return textAreas != null && ! textAreas.isEmpty();
    }

    @Override
    public List<String> getLines(int indent, int length) {
        List<String> lines = new LinkedList<String>();
        boolean first = true;
        Iterator<String> it = textAreas.iterator();
        while (it.hasNext()) {
            String textArea = it.next();
            int nextIndex = 0;
            boolean newlineFound = false;
            while ( ! newlineFound && nextIndex < textArea.length() ) {
                StringBuilder sb = new StringBuilder();
                if (first) {
                    first = false;
                    sb.append(buildIndentWithName(indent, name));
                } else {
                    sb.append(buildBlankIndent(indent));
                }

                String substringToAppend = textArea.substring(nextIndex, Math.min(nextIndex + length - 1, textArea.length()));
                int positionsToAdvanceIndex = substringToAppend.length();
                int newlineIndex = substringToAppend.indexOf('\n');
                newlineFound = newlineIndex > 0;
                if (newlineFound) {
                    substringToAppend = substringToAppend.substring(0, newlineIndex);
                    positionsToAdvanceIndex++;
                }
                sb.append(substringToAppend);
                nextIndex += positionsToAdvanceIndex;
                sb.append(StringUtils.multiplyString(" ", indent + length - sb.toString().length()));
                lines.add(sb.toString());
            }
            if (it.hasNext()) {
                lines.add("\n\n");
            }
        }
        return lines;
    }

}
