package wang.depp.session.session.config;

import org.apache.commons.lang3.StringUtils;

public class MapField {
    private String name;

    private String type;

    public MapField(String name, String type) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(type)) {
            throw new IllegalArgumentException("session 的 map 转换字段不能为空 ");
        }
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}