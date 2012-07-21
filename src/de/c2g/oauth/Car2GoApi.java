package de.c2g.oauth;


import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;
import org.scribe.model.Verb;

public class Car2GoApi extends DefaultApi10a {
    private static final String AUTHORIZATION_URL = "https://www.car2go.com/api/authorize?oauth_token=%s";



    @Override
    public String getAccessTokenEndpoint() {
        return "https://www.car2go.com/api/accesstoken";
    }

    @Override
    public String getRequestTokenEndpoint() {
        return "https://www.car2go.com/api/reqtoken";
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.GET;
    }

    @Override
    public Verb getRequestTokenVerb() {
        return Verb.GET;
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {
        return String.format(AUTHORIZATION_URL, requestToken.getToken());
    }
}
