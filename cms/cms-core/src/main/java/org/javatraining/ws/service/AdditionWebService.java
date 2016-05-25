package org.javatraining.ws.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The project name is cms.
 * Created by sergey on 07.07.15 at 17:19.
 * For more information you should send mail to codedealerb@gmail.com
 */
@Path("mailhash")
public class AdditionWebService {

    @GET
    @Produces("application/json")
    public EmailHash getEmailHash(@QueryParam("email") String email) {
//    public Response getEmailHash(@QueryParam("email") String email) {
        String hash = getMDA5(email);
        EmailHash hash1 = new EmailHash(hash);
        return hash1;
//        return Response.ok(hash1).build();
    }

    private String getMDA5(String email) {
        if (email == null || email.equals("")) {
            throw new IllegalArgumentException("email can't be null");
        }
        try {
            MessageDigest mDigest = MessageDigest.getInstance("MD5");
            byte[] result = mDigest.digest(email.trim().getBytes());
            StringBuilder sb = new StringBuilder();

            for (byte aResult : result) {
                sb.append(Integer.toString((aResult & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    private class EmailHash{
        private String hash;

        public EmailHash(String hash){
            this.hash = hash;
        }


        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }
    }
}
