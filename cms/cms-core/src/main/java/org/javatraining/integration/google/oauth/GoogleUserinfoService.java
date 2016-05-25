package org.javatraining.integration.google.oauth;

import org.javatraining.model.PersonVO;

public interface GoogleUserinfoService {
    String getClientIdByToken(String token);

    String getEmailByToken(String token);

    PersonVO getUserInfoByToken(String token);
}
