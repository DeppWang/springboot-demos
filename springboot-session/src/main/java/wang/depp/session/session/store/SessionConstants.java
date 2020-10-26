package wang.depp.session.session.store;

public class SessionConstants {
    /**
     * 跟踪 session 对象的 Id
     */
    public static final String SESSION_ID = "sessionId";

    /**
     * 跟踪 ID 生命周期设置足够长，只要用户访问网站即可生成此 cookie
     */
    public static final String TRACK_ID = "trackId";

    /**
     * 上次访问时间
     */
    public static final String LAST_VISIT_TIME = "lastVisitTime";
}