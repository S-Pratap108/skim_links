(config
    (text-field
        :name "clientId" 
        :label "Client Id"
        :placeholder "Enter Client Id"
        :required true
    )

    (password-field
        :name "clientSecret" 
        :label "Client Secret"
        :placeholder "Enter client secret"
        :required true
    )

    (text-field
        :name "publisherId" 
        :label "Publisher Id"
        :placeholder "Enter Publisher Id"
        :required true
    )

    (text-field
        :name "publisherDomainId" 
        :label "Publisher Domain Id"
        :placeholder "Enter Publisher Domain Id"
        :required true
    )
    

    (oauth2/refresh-token-with-client-credentials
        (token
            (source
                (http/post
                    :url "https://authentication.skimapis.com/access_token"
                    (body-params
                    ;; "response_type" "code"
                    "client_id" "{clientId}"
                    "client_secret" "{clientSecret}"
                    "grant_type" "client_credentials"
                    ;; "scope" "contact_read all_contact_read"
                    )
                )
            )
            (fields
                access_token
                refresh_token
                token_type
                scope
                realm_id :<= "realmId"
                expires_in :<= "expiry_timestamp"
            )
        )
    )
)
(default-source (http/get :base-url "https://merchants.skimapis.com/v4"
    (header-params "Accept" "application/json"))
    (paging/offset  :offset-query-param-initial-value 0
                    :offset-query-param-name "offset"
                    :limit 200
                    :limit-query-param-name "limit")
    (auth/oauth2
        (auth-params
             (query-params 
                 "access_token"  "{access_token}")))
    (error-handler
        (when :status 429 :action rate-limit)
        (when :status 401 :action refresh)
    )
)

(temp-entity MERCHANT 
    (api-docs-url "https://developers.skimlinks.com/merchant.html")
    (source (http/get :url "/publisher/{publisherId}/merchants")
            (query-params "publisher_id" "{publisherId}"
                            "publisher_domain_id" "{publisherDomainId}" )
            (setup-test
                (upon-receiving :code 200 :action (pass)))
            (extract-path "merchants"))    
(fields
    id :id)
)

