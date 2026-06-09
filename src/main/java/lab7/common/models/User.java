package lab7.common.models;

import java.io.Serializable;

public class User implements Serializable {
    private long id;
    private String login;
    private String passwordHash;

    public User(long id, String login, String passwordHash){
        this.id = id;
        this.login = login;
        this.passwordHash = passwordHash;
    }

    public long getId() {
        return id;
    }
    public String getLogin(){
        return login;
    }
    public String getPasswordHash(){
        return passwordHash;
    }

    public void setId(long id){
        this.id = id;}

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
