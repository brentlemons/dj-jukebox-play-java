package models;

import play.data.validation.Constraints;
import play.db.jpa.JPA;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

@Entity
public class Account {

    @Id
    @GeneratedValue
    public Long id;
    
    private String token;

    @Column(length = 256, unique = true, nullable = false)
    @Constraints.MaxLength(256)
    @Constraints.Required
    @Constraints.Email
    private String emailAddress;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress.toLowerCase();
    }

    @Column(length = 64, nullable = false)
    private byte[] shaPassword;

    @Transient
    @Constraints.Required
    @Constraints.MinLength(6)
    @Constraints.MaxLength(256)
    private String password;
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
        shaPassword = getSha512(password);
    }

    @Column(length = 256, nullable = false)
    @Constraints.Required
    @Constraints.MinLength(2)
    @Constraints.MaxLength(256)
    public String fullName;

    @Column(nullable = false)
    public Date creationDate;

    @Column(nullable = false)
    public boolean isAdmin;
    
    public Account() {
        this.isAdmin = false;
        this.creationDate = new Date();
    }

    public Account(String emailAddress, String password, String fullName) {
        this.isAdmin = false;
        setEmailAddress(emailAddress);
        setPassword(password);
        this.fullName = fullName;
        this.creationDate = new Date();
    }
    
    public String createToken() {
        this.token = UUID.randomUUID().toString();
        this.password = "ignore";
        JPA.em().merge(this);
        return this.token;
    }


    public static byte[] getSha512(String value) {
        try {
            return MessageDigest.getInstance("SHA-512").digest(value.getBytes("UTF-8"));
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Account findByEmailAddressAndPassword(String emailAddress, String password) {
        try  {
	    	CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
	    	CriteriaQuery<Account> query = cb.createQuery(Account.class);
	    	Root<Account> account = query.from(Account.class);
	    	query.select(account)
	    		.where(
	    			cb.and(
	    				(cb.equal(account.get("emailAddress"), emailAddress)),
	    				(cb.equal(account.get("shaPassword"), getSha512(password)))))
	    		.distinct(true);
	        return JPA.em().createQuery(query).getSingleResult();
        } catch (Exception e) {
        	return null;
        }
    }

    public static Account findByToken(String token) {
        if (token == null) {
            return null;
        }
        
        try  {
        	CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        	CriteriaQuery<Account> query = cb.createQuery(Account.class);
        	Root<Account> account = query.from(Account.class);
        	query.select(account).where((cb.equal(account.get("token"), token))).distinct(true);
            return JPA.em().createQuery(query).getSingleResult();
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Account findByEmailAddress(String emailAddress) {
    	try {
	    	CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
	    	CriteriaQuery<Account> query = cb.createQuery(Account.class);
	    	Root<Account> account = query.from(Account.class);
	    	query.select(account).where((cb.equal(account.get("emailAddress"), emailAddress))).distinct(true);
	        return JPA.em().createQuery(query).getSingleResult();
    	} catch (Exception e) {
    		return null;
    	}
    }
}