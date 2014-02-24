package controllers;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import models.Account;
import models.Artist;

import com.fasterxml.jackson.databind.JsonNode;
import static play.libs.Json.toJson;

import play.cache.Cache;
import play.data.Form;
import play.data.validation.Constraints;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

import play.mvc.Result;
import play.mvc.Security;
import play.mvc.With;
import play.mvc.Controller;

import views.html.*;

@With(CurrentAccount.class)
public class Application extends Controller {

	@Transactional
    public static Result index() {
		System.out.println("got to index");
        return ok(index.render(Form.form(Artist.class)));
    }

    @Transactional
    public static Result addArtist() {
        Form<Artist> form = Form.form(Artist.class).bindFromRequest();
        Artist bar = form.get();
        JPA.em().persist(bar);
        return redirect(controllers.routes.Application.index());
    }

    @Transactional(readOnly = true)
    public static Result listArtists() {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<Artist> cq = cb.createQuery(Artist.class);
        Root<Artist> root = cq.from(Artist.class);
        CriteriaQuery<Artist> all = cq.select(root);
        TypedQuery<Artist> allQuery = JPA.em().createQuery(all);
        JsonNode jsonNodes = toJson(allQuery.getResultList());
        return ok(jsonNodes);
    }

    public static Result signupForm() {
        return ok(views.html.signupForm.render(Form.form(Account.class)));
    }

    @Transactional
    public static Result signup() {
        Form<Account> signupForm = Form.form(Account.class).bindFromRequest();
        if (signupForm.hasErrors()) {
            return badRequest(views.html.signupForm.render(signupForm));
        }
        else {
            
        	Account account = signupForm.get();
            
            if (Account.findByEmailAddress(account.getEmailAddress()) != null) {
                signupForm.reject("Duplicate Email Address");
                return badRequest(views.html.signupForm.render(signupForm));
            }
            
            JPA.em().persist(account);

            session("token", account.createToken()); // log the user in
            
            return redirect(routes.Application.index());
        }
    }

    public static Result loginForm() {
        return ok(views.html.loginForm.render(Form.form(Login.class)));
    }

    @Transactional
    public static Result login() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
        
        if (loginForm.hasErrors()) {
            return badRequest(views.html.loginForm.render(loginForm));
        }
        
        Login login = loginForm.get();
        
        Account account = Account.findByEmailAddressAndPassword(login.emailAddress, login.password);
        
        if (account == null) {
            loginForm.reject("Invalid Login");
            return badRequest(views.html.loginForm.render(loginForm));
        }
        else {
            // todo: redirect back to the page the user was already on
            session("token", account.createToken());
            return redirect(routes.Application.index());
        }
    }

    @Security.Authenticated(Secured.class)
    public static Result logout() {
        Cache.remove(session().get("token"));
        session().remove("token");
        return redirect(routes.Application.index());
    }
    
    public static class Login {
        
        @Constraints.Required
        @Constraints.Email
        public String emailAddress;

        @Constraints.Required
        public String password;
        
    }
}

//    public static Result index() {
//        List<Region> regions = (List<Region>)Cache.get("regions");
//        if (regions == null) {
//            regions = Region.find.all();
//            Cache.set("regions", regions);
//        }
//        return ok(views.html.index.render(regions));
//    }