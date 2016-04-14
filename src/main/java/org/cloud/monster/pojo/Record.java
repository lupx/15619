package org.cloud.monster.pojo;

import java.util.*;

public class Record {

    private Map<String, String> map = new HashMap();

    public List<String> getFieldsNames() {
        List<String> filedsNames = new ArrayList<>();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            filedsNames.add((String) pair.getKey());
        }
        return filedsNames;
    }

    public List<String> getFields() {
        List<String> fileds = new ArrayList<>();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            StringBuilder sb = new StringBuilder();
            sb.append("'" + pair.getValue() + "'");
            fileds.add(sb.toString());
        }
        return fileds;
    }

    public void setAttributes(String[] fields, String[] playload) {
        for (int i = 0; i < fields.length; i++) {
            if (playload[i] != null) {
                map.put(fields[i], playload[i]);
            }
        }
    }

    public void setAttribute(String field, String value) {
        map.put(field, value);
    }

    public String getValue(String fieldname) {
        String res = map.get(fieldname);
        if (res == null) {
            return "";
        }
        return res.replaceAll(" ", "+");
    }
}
