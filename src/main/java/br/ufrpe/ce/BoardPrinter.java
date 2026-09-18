package br.ufrpe.ce;

import java.util.List;

public final class BoardPrinter {
    private BoardPrinter() {}

    public static String render(List<Integer> rowOfColumn) {
        int n = rowOfColumn.size();
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                sb.append(rowOfColumn.get(col) == row ? " Q" : " .");
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
