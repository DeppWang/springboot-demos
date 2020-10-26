package wang.depp.session.session.config;


import wang.depp.session.session.StoreType;

import java.util.ArrayList;
import java.util.List;

/**
 * session-config.xml 一个 <entry></entry> 对应一个 SessionConfigEntry
 */
public class SessionConfigEntry {
    /**
     * the name which is used in the application.
     */
    private String name;
    /**
     * the key means the store key, for example cookie key, this key always
     * is a unmeaningful name
     */
    private String key;

    private StoreType storeType;

    private String domain;

    private String path;

    private int lifeCycle;

    private boolean httpOnly;

    /**
     * 表示此属性是只读的，程序不能修改只读属性，只读属性由 session 框架生成，比如
     * sessionId
     */
    private boolean readOnly;

    private boolean encrypt;

    private String type;


    /**
     * 如果 type 是 map，怎么这个列表表示需要转型的字段
     * 因为 map 类型在序列化为 json 的时候 int 类型会丢掉
     */
    private List<MapField> mapFieldList = new ArrayList<>();


    public void addMapFiled(MapField field) {
        mapFieldList.add(field);
    }

    public List<MapField> getMapFieldList() {
        return mapFieldList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public StoreType getStoreType() {
        return storeType;
    }

    public void setStoreType(StoreType storeType) {
        this.storeType = storeType;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getLifeCycle() {
        return lifeCycle;
    }

    public void setLifeCycle(int lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}