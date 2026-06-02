package entity;

/** 系统用户实体 */
public class User {
    private String userId;
    private String username;
    private String password;
    private String role;

    public User() {}
    public User(String userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUserId()   { return userId; }
    public void   setUserId(String v)   { this.userId = v; }
    public String getUsername() { return username; }
    public void   setUsername(String v) { this.username = v; }
    public String getPassword() { return password; }
    public void   setPassword(String v) { this.password = v; }
    public String getRole()     { return role; }
    public void   setRole(String v)     { this.role = v; }
}
