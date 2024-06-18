

(config
    (text-field
        :name "clientId" 
        :label "Enter clientId"
        :placeholder "Enter Client Id"
        :required true
        (api-config-field)
    )
    (password-field
        :name "clientSecret" 
        :label "Enter clientSecret"
        :placeholder "Enter client secret"
        :required true
        (api-config-field :masked true)
    )
    (text-field
        :name "PublisherId" 
        :label "Publisher Id"
        :placeholder "Enter Publisher Id"
        :required true
    )
    (oauth2/authorization-code-flow-client-credentials
        (token
            (source
                (http/post
                    :url "https://authentication.skimapis.com/access_token"
                    (body-params
                    "response_type" "code"
                    "client_id" "{clientId}"
                    "client_secret" "{clientSecret}"
                    "grant_type" "client_credentials"
                    ;; "scope" "contact_read all_contact_read"
                    )
                )
            )
            (fields
                access_token :<= "access_token"
                refresh_token
                token_type :<= "token_type"
                scope :<= "scope"
                scope 
                realm_id :<= "realmId"
                expires_in :<= "expires_in"
            )
        )
    )
)
(default-source (http/get :base-url "https://merchants.skimapis.com/v4"
    (header-params "Accept" "application/json"))
    (paging/offset  :offset-query-param-initial-value 0
                    offset-query-param-name "offset"
                    limit 200
                    limit-query-param-name "limit")
    (auth/oauth2)
    (error-handler
        (when :status 490 :message "Too Many Requests")
    )
)

(entity MERCHANT 
        "represents a merchant program having an active relationship with the authenticated user in Skimlinks' system"
        (api-docs-url "https://developers.skimlinks.com/merchant.html")
        (source (http/get :url "/publisher/{PublisherId}/merchants")
                (extract-path "merchants")
                (query-params "publisher_id" "{PublisherId}"
                              "access_token" "{access_token}"
                              "publisher_domain_id" )
        )
)