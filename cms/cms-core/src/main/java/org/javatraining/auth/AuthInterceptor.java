package org.javatraining.auth;

import org.javatraining.config.Config;
import org.javatraining.integration.google.oauth.TokenVerifierService;
import org.javatraining.integration.google.oauth.exception.AuthException;
import org.javatraining.service.AuthService;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.lang.reflect.Method;

@Auth(roles = {})
@Interceptor
public class AuthInterceptor implements Serializable {
    private static final long serialVersionUID = 8041440307110936046L;
    @Inject
    private transient HttpServletRequest request;
    @Inject
    private transient TokenVerifierService tokenVerifierService;
    @Inject
    private transient AuthService authService;

    @AroundInvoke
    public Object invoke(final InvocationContext context) throws Exception {
        // if the class to which interceptor is injected is not request scoped,
        // throw exception
        if (request == null) {
            throw new AuthException("Unexpected usage of '@Auth' annotation");
        }
        String[] methodAllowedRoles = getRoles(context.getMethod());
        // if the method wasn't annotated or role field in annotation is empty,
        // process request
        if (methodAllowedRoles == null || methodAllowedRoles.length == 0) {
            return context.proceed();
        }
        String token = getToken();
        // if the token wasn't set in request header or if it's not valid,
        // return response with code 401
        if (token == null || !tokenVerifierService.isTokenValid(token)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String id = getId();
        // if the id wasn't set in request header or if clientIDs from database and from the google are not equal,
        // return response with code 401
        if (id == null || !authService.isCredentialValid(id, token)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        String userRole = authService.getRoleById(id);
        // if the user role is undefined or invoked method has another allowed roles,
        // return response with code 401
        if (userRole == null || !isUserRoleAllowedForMethod(userRole, methodAllowedRoles)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        // if all auth steps are passed,
        // process request
        return context.proceed();
    }

    private String[] getRoles(Method method) {
        if (method.isAnnotationPresent(Auth.class)) {
            return method.getAnnotation(Auth.class).roles();
        }

        if (method.getDeclaringClass().isAnnotationPresent(Auth.class)) {
            return method.getDeclaringClass().getAnnotation(Auth.class).roles();
        }
        return null;
    }

    private String getToken() {
        return request.getHeader(Config.REQUEST_HEADER_TOKEN);
    }

    private String getId() {
        return request.getHeader(Config.REQUEST_HEADER_ID);
    }

    private boolean isUserRoleAllowedForMethod(String role, String[] allowedRoles) {
        for (String allowedRole : allowedRoles) {
            if (role.equals(allowedRole)) {
                return true;
            }
        }
        return false;
    }

}
