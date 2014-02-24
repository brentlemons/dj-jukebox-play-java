package controllers;

import models.Account;
import play.cache.Cache;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;

public class CurrentAccount extends Action.Simple {

    public F.Promise<SimpleResult> call(Http.Context ctx) throws Throwable {
        String token = ctx.session().get("token");
        System.out.println("token: " + token);
        if (token != null) {
        	Account account = (Account)Cache.get(token);
            if (account == null) {
            	account = models.Account.findByToken(token);
                Cache.set(token, account);
            }
            ctx.args.put("account", account);
        }
        return delegate.call(ctx);
    }

    public static Account get() {
        return (Account)Http.Context.current().args.get("account");
    }
}