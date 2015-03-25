package domain.infobox;

import util.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class ColumnInfoBoxRegion extends InfoBoxRegion {

    private final String name;
    private final String[] headers;
    private final List<List<String>> rows;

    public ColumnInfoBoxRegion(String name, List<List<String>> rows, String... headers) {
        this.name = name;
        this.rows = rows;
        this.headers = headers;

        if (rows.size() > 0) {
            int numColumns = headers.length;
            for (List<String> column : rows) {
                if (numColumns != column.size()) {
                    throw new RuntimeException("Programmer error: All rows must be the same length");
                }
            }
        }
    }

    @Override
    public boolean isValid() {
        return rows != null && rows.size() > 0 && ! rows.get(0).isEmpty();
    }

    @Override
    public List<String> getLines(int indent, int length) {
        List<String> lines = new LinkedList<String>();
        lines.add(buildIndentWithName(indent - 1, name) + "|" + buildColumns(length, headers));
        lines.add(buildBlankIndent(indent - 1) + "|" + StringUtils.multiplyString("-", length));

        for (List<String> row : rows) {
            lines.add(buildBlankIndent(indent - 1) + "|" + buildColumns(length, row.toArray(new String[row.size()])));
        }
        return lines;
    }

    private String buildColumns(int length, String... columnValues) {
        int N = columnValues.length;
        int sectionWidth = length / N;
        int firstColumnLength = length - (sectionWidth * (N - 1));

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String columnValue : columnValues) {
            if (first) {
                first = false;
                sb.append(buildTextWithinWidth(columnValue, firstColumnLength));
            } else {
                sb.append("| ");
                sb.append(buildTextWithinWidth(columnValue, sectionWidth - 2));
            }

        }

        return sb.toString();
    }

}
