package com.pivo.weev.backend;

import com.pivo.weev.backend.web.utils.Constants.Configs;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WeevBackendLoader {

  public static void main(String[] args) {
    SpringApplication.run(WeevBackendLoader.class, args);
  }

  @PostConstruct
  public void setTimeZone() {
    TimeZone.setDefault(Configs.APPLICATION_TIMEZONE);
  }
}
