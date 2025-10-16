package com.lyzr.human_in_the_loop.config;

import com.lyzr.human_in_the_loop.client.communication.MailGunClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailGunConfig {

    @Value("${mailgun.api.key}")
    private String apiKey;

    @Value("${mailgun.domain}")
    private String domain;

    @Value("${mailgun.api.base.url}")
    private String baseUrl;

    @Bean
    public MailGunClient mailGunClient() {
        return new MailGunClient(apiKey, domain, baseUrl);
    }
}
