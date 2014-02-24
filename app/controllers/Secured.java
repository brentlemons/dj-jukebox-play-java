package controllers;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {
    
    @Override
    public String getUsername(Context ctx) {
        // todo: need to make sure the account is valid, not just the token
        return ctx.session().get("token");
    }
    
    @Override
    public Result onUnauthorized(Context ctx) {
        // todo: display something
        return unauthorized("Unauthorized");
    }

}