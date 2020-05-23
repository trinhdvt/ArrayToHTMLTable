package Model;

public class HTMLObject {
    public static int count = 1;
    private final String[][] arr;
    private final Boolean header, index;
    private final String table;
    private final String date;
    private final int ID;

    public HTMLObject(String[][] input, boolean[] flag, String date) {
        this.arr = input;
        this.header = flag[0];
        this.index = flag[1];
        this.date = date;
        this.table = this.toTable();
        this.ID = count++;
    }

    private String toTable() {
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

    public String getTable() {
        return table;
    }

    public String getDate() {
        return date;
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

    public int getID() {
        return ID;
    }
}
