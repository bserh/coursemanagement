package org.javatraining.integration.gitlab.api.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.javatraining.model.PersonVO;

import java.io.Serializable;

/**
 * The project name is cms.
 * Created by sergey on 02.06.15 at 14:33.
 * For more information you should send mail to codedealerb@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabUser extends PersonVO implements Serializable {
    protected Long id;
    protected String password;//required
    protected String username;//required
    protected String name;//required
    protected String skype;
    protected String linkedin;
    protected String twitter;
    @JsonProperty("website_url")
    protected String websiteUrl;
    @JsonProperty("projects_limit")
    protected int projectsLimit;
    @JsonProperty("extern_uid")
    protected String externUID;
    protected String provider;
    protected String bio;
    @JsonProperty("is_admin")
    protected boolean admin;
    @JsonProperty("can_create_group")
    protected boolean canCreateGroup;
    protected boolean confirm;
    protected String state;
    @JsonProperty("theme_id")
    protected int themeId;
    @JsonProperty("current_sign_in_at")
    protected String currentSignInAt;
    @JsonProperty("can_create_project")
    protected boolean canCreateProject;
    @JsonProperty("color_scheme_id")
    protected int colorSchemeId;
    @JsonProperty("avatar_url")
    protected String avatarUrl;
    @JsonProperty("created_at")
    protected String createdAt;

    public GitLabUser() {

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public int getProjectsLimit() {
        return projectsLimit;
    }

    public void setProjectsLimit(int projectsLimit) {
        this.projectsLimit = projectsLimit;
    }

    public String getExternUID() {
        return externUID;
    }

    public void setExternUID(String externUID) {
        this.externUID = externUID;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isCanCreateGroup() {
        return canCreateGroup;
    }

    public void setCanCreateGroup(boolean canCreateGroup) {
        this.canCreateGroup = canCreateGroup;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }

    public String isState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getThemeId() {
        return themeId;
    }

    public void setThemeId(int themeId) {
        this.themeId = themeId;
    }

    public String getCurrentSignInAt() {
        return currentSignInAt;
    }

    public void setCurrentSignInAt(String currentSignInAt) {
        this.currentSignInAt = currentSignInAt;
    }

    public boolean isCanCreateProject() {
        return canCreateProject;
    }

    public void setCanCreateProject(boolean canCreateProject) {
        this.canCreateProject = canCreateProject;
    }

    public int getColorSchemeId() {
        return colorSchemeId;
    }

    public void setColorSchemeId(int colorSchemeId) {
        this.colorSchemeId = colorSchemeId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
