package org.javatraining.integration.gitlab.api.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * The project name is cms.
 * Created by sergey on 05.06.15 at 19:19.
 * For more information you should send mail to codedealerb@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabProject implements Serializable {
    public static final int VISIBILITY_PRIVATE_LEVEL = 0;//Project access must be granted explicitly for each user.
    public static final int VISIBILITY_INTERNAL_LEVEL = 10;//The project can be cloned by any logged in user.
    public static final int VISIBILITY_PUBLIC_LEVEL = 20;//The project can be cloned without any authentication.


    private Integer id;
    private String name;
    private String description;
    private GitLabUser owner;
    private boolean publicProject;
    private String path;

    @JsonProperty("default_branch")
    private String defaultBranch;
    @JsonProperty("visibility_level")
    private Integer visibilityLevel;
    @JsonProperty("issues_enabled")
    private boolean issuesEnabled;
    @JsonProperty("merge_requests_enabled")
    private boolean mergeRequestsEnabled;
    @JsonProperty("snippets_enabled")
    private boolean snippetsEnabled;
    @JsonProperty("wiki_enabled")
    private boolean wikiEnabled;
    @JsonProperty("last_activity_at")
    private Date lastActivityAt;

    public GitLabProject() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultBranch() {
        return defaultBranch;
    }

    public void setDefaultBranch(String defaultBranch) {
        this.defaultBranch = defaultBranch;
    }

    public Integer getVisibilityLevel() {
        return visibilityLevel;
    }

    public void setVisibilityLevel(Integer visibilityLevel) {
        this.visibilityLevel = visibilityLevel;
    }

    public GitLabUser getOwner() {
        return owner;
    }

    public void setOwner(GitLabUser owner) {
        this.owner = owner;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isIssuesEnabled() {
        return issuesEnabled;
    }

    public void setIssuesEnabled(boolean issuesEnabled) {
        this.issuesEnabled = issuesEnabled;
    }

    public boolean isMergeRequestsEnabled() {
        return mergeRequestsEnabled;
    }

    public void setMergeRequestsEnabled(boolean mergeRequestsEnabled) {
        this.mergeRequestsEnabled = mergeRequestsEnabled;
    }

    public boolean isSnippetsEnabled() {
        return snippetsEnabled;
    }

    public void setSnippetsEnabled(boolean snippetsEnabled) {
        this.snippetsEnabled = snippetsEnabled;
    }

    public boolean isWikiEnabled() {
        return wikiEnabled;
    }

    public void setWikiEnabled(boolean wikiEnabled) {
        this.wikiEnabled = wikiEnabled;
    }

    public boolean isPublic() {
        return publicProject;
    }

    public void setPublic(boolean aPublic) {
        publicProject = aPublic;
    }

    public Date getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(Date lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }
}
