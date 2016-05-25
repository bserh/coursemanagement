package org.javatraining.service;

import javax.validation.constraints.NotNull;

/**
 * Created by olga on 05.06.15.
 */
public interface AuthService {

    boolean isCredentialValid(@NotNull String idFromRequest, @NotNull String token);

    String getRoleById(@NotNull String idFromRequest);
}
