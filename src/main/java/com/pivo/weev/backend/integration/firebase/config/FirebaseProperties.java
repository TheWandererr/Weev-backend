package com.pivo.weev.backend.integration.firebase.config;

import static com.pivo.weev.backend.domain.utils.Constants.EncryptionPatterns.PRIVATE_KEY_MASK_SYMBOLS;
import static org.apache.commons.lang3.StringUtils.replaceChars;

import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "integration.client.firebase")
@Setter
public class FirebaseProperties {

    private String projectId;
    private String privateKeyId;
    private String maskedPrivateKey;
    private String id;
    private String email;
    private String authUri;
    private String tokenUri;
    private String authProviderCertUrl;
    private String certUrl;
    private String storageBucket;
    private Integer connectTimeout;
    private Integer readTimeout;
    private String dataBaseUrl;

    private Map<String, String> credentials;

    @PostConstruct
    public void buildCredentials() {
        String privateKey = replaceChars(maskedPrivateKey, PRIVATE_KEY_MASK_SYMBOLS, "\n");
        this.credentials = Map.of(
                "type", "service_account",
                "project_id", projectId,
                "private_key_id", privateKeyId,
                "private_key", privateKey,
                "client_email", email,
                "client_id", id,
                "auth_uri", authUri,
                "token_uri", tokenUri,
                "auth_provider_x509_cert_url", authProviderCertUrl,
                "client_x509_cert_url", certUrl
        );
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public Map<String, String> getCredentials() {
        return credentials;
    }

    public String getStorageBucket() {
        return storageBucket;
    }

    public String getDataBaseUrl() {
        return dataBaseUrl;
    }

    public String getEmail() {
        return email;
    }
}
