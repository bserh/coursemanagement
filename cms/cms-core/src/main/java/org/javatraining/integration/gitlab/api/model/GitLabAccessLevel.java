package org.javatraining.integration.gitlab.api.model;


/**
 * The project name is cms.
 * Created by sergey on 05.06.15 at 20:12.
 * For more information you should send mail to codedealerb@gmail.com
 */
public enum GitLabAccessLevel {
    Guest(10),
    Reporter(20),
    Developer(30),
    Master(40),
    Owner(50);

    public final int accessValue;

    GitLabAccessLevel(int accessValue) {
        this.accessValue = accessValue;
    }

    public static GitLabAccessLevel fromAccessValue(final int accessValue) throws IllegalArgumentException {
        for (final GitLabAccessLevel gitlabAccessLevel : GitLabAccessLevel.values()) {
            if (gitlabAccessLevel.accessValue == accessValue) {
                return gitlabAccessLevel;
            }
        }
        throw new IllegalArgumentException("No GitLab Access Level enum constant with access value: " + accessValue);
    }
}
