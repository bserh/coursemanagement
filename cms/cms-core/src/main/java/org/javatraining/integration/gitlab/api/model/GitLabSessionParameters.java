package org.javatraining.integration.gitlab.api.model;

import java.io.Serializable;

/**
 * The project name is cms.
 * Created by sergey on 12.06.15 at 20:40.
 * For more information you should send mail to codedealerb@gmail.com
 */
public class GitLabSessionParameters implements Serializable {
    private String login;
    private String password;
    private String email;

    public GitLabSessionParameters() {

    }

    public GitLabSessionParameters(String login, String password, String email) {
        this.email = email;
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
