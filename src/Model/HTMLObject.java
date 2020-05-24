package Model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Arrays;

public class HTMLObject {
    private final String[][] arr;
    private final Boolean header, index;
    private final String table;
    private final String date;

    public HTMLObject(String[][] input, boolean[] flag, String date) {
        this.arr = input;
        this.header = flag[0];
        this.index = flag[1];
        this.date = date;
        this.table = this.toTable();
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
        Gson gson = new Gson();
        String json = gson.toJson(this);
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        JsonElement arr = jsonObject.get("arr");
        boolean header = jsonObject.get("header").getAsBoolean();
        boolean index = jsonObject.get("index").getAsBoolean();
        String table = jsonObject.get("table").getAsString();
        JsonElement date = jsonObject.get("date");
        return new Object[]{arr.toString() + ";" + header + ";" + index, table, date.toString()};
    }

    public String getJsonString() {
        return new Gson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HTMLObject that = (HTMLObject) o;
        return Arrays.equals(arr, that.arr) &&
                header.equals(that.header) &&
                index.equals(that.index);
    }

}
