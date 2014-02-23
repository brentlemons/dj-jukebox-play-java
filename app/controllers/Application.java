package controllers;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import models.Artist;

import com.fasterxml.jackson.databind.JsonNode;
import static play.libs.Json.toJson;

import play.*;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {

    public static Result index() {
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

}
