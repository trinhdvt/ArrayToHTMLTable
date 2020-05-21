package Model;

public class HTMLObject {
    private final String[][] arr;
    private final Boolean header, index;

    public HTMLObject(String[][] input, Boolean... flag) {
//        if (input == null)
//            throw new IllegalArgumentException("Invalid input, it must be a MxN array");
        this.arr = input;
        header = flag[0];
        index = flag[1];
    }

    public String toTable() {
        StringBuilder table = new StringBuilder("<table>\n");
        if (header) {
            StringBuilder head = new StringBuilder("    <thead>\n        <tr>\n");
            if (index)
                head.append("            <th></th>\n");
            for (Object item : arr[0])
                head.append("            <th>").append(item != null ? item : "").append("</th>\n");
            head.append("        </tr>\n    </thead>\n");
            table.append(head);
        }
        StringBuilder body = new StringBuilder("    <tbody>\n");
        int base_index = 1;
        int start_index = (header ? 1 : 0);
        for (int i = start_index; i < arr.length; i++) {
            StringBuilder row = new StringBuilder("        <tr>\n");
            if (index)
                row.append("            <td>").append(base_index++).append("</td>\n");
            for (Object item : arr[i])
                row.append("            <td>").append(item != null ? item : "").append("</td>\n");
            row.append("        </tr>\n");
            body.append(row);
        }
        body.append("    </tbody>\n");
        table.append(body).append("</table>");

        return table.toString();
    }

    public String[][] getArr() {
        return arr;
    }

    public Boolean getHeader() {
        return header;
    }

    public Boolean getIndex() {
        return index;
    }
}
