package com.petshop.petshop_backend.client;

import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;

import java.util.HashMap;
import java.util.Map;

public class CompositeOAuth2AccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private final Map<String, OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>> clients;

    public CompositeOAuth2AccessTokenResponseClient() {
        this.clients = new HashMap<>();
    }

    public Map<String, OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest>> getOAuth2AccessTokenResponseClients() {
        return clients;
    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
        String registrationId = authorizationGrantRequest.getClientRegistration().getRegistrationId();
        OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> client = clients.get(registrationId);

        if (client == null) {
            throw new IllegalArgumentException("No client found for registrationId: " + registrationId);
        }

        return client.getTokenResponse(authorizationGrantRequest);
    }
}