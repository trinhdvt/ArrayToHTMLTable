package Model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class HTMLObject {
    private static final transient Gson gson = new Gson();
    public static int count = 1;
    private final String[][] arr;
    private final Boolean header, index;
    private final String table;
    private final String date;
    private transient int id;

    public HTMLObject(String[][] input, boolean[] flag, String date) {
        this.arr = input;
        this.header = flag[0];
        this.index = flag[1];
        this.date = date;
        this.table = this.toTable();
    }

    public static HTMLObject createObjectFromProperty(int id, String input, String output, String date) {
        String[] tmp = input.split(", ");
        String arr = "\"arr\":" + tmp[0];
        String header = "\"header\":" + tmp[1];
        String index = "\"index\":" + tmp[2];
        String table = "\"table\":" + "\"" + output + "\"";
        date = "\"date\":" + date;
        String[] properties = new String[]{arr, header, index, table, date};
        final String json = "{" + String.join(",", properties) + "}";
        HTMLObject object = gson.fromJson(json, HTMLObject.class);
        object.setId(id);
        return object;
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

    public Object[] getWritableData() {
        String json = gson.toJson(this);
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonElement arr = jsonObject.get("arr");
        boolean header = jsonObject.get("header").getAsBoolean();
        boolean index = jsonObject.get("index").getAsBoolean();
        String table = jsonObject.get("table").getAsString();
        JsonElement date = jsonObject.get("date");
        return new Object[]{arr.toString() + ", " + header + ", " + index, table, date.toString()};
    }

    public String getJsonString() {
        return gson.toJson(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HTMLObject that = (HTMLObject) o;
        return table.equals(that.table);
    }

}
