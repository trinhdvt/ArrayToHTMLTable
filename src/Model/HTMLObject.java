package Model;

public class HTMLObject {
    private final Object[][] arr;
    private Boolean header = false, index = false;

    public HTMLObject(Object[][] input, Boolean... flag) {
        if (input == null)
            throw new IllegalArgumentException("Cannot passing a null array at input");
        this.arr = input;
        try {
            header = flag[0];
            index = flag[1];
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    public String toTable() {
        StringBuilder table = new StringBuilder("<table>");
        if (header) {
            StringBuilder head = new StringBuilder("<thead><tr>");
            if (index)
                head.append("<th></th>");
            for (Object item : arr[0])
                head.append("<th>").append(item != null ? String.valueOf(item) : "").append("</th>");
            head.append("</tr></thead>");
            table.append(head);
        }
        StringBuilder body = new StringBuilder("<tbody>");
        int base_index = 1;
        int start_index = (header ? 1 : 0);
        for (int i = start_index; i < arr.length; i++) {
            StringBuilder row = new StringBuilder("<tr>");
            if (index)
                row.append("<td>").append(base_index++).append("</td>");
            for (Object item : arr[i])
                row.append("<td>").append(item != null ? String.valueOf(item) : "").append("</td>");
            row.append("</tr>");
            body.append(row);
        }
        body.append("</tbody>");
        table.append(body).append("</table>");

        return table.toString();
    }

}
