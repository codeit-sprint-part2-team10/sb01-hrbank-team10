package com.sprint.example.sb01part2hrbankteam10.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import java.io.IOException;
import java.util.Optional;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {


  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
    return builder -> {
      builder.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      builder.modulesToInstall(new Jdk8Module());
      SimpleModule module = new SimpleModule();
      module.addDeserializer(Optional.class, new OptionalDeserializer());
      builder.modulesToInstall(module);
    };
  }

  public static class OptionalDeserializer extends JsonDeserializer<Optional<?>> {
    @Override
    public Optional<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      if (p.getCurrentToken() == JsonToken.VALUE_NULL) {
        return Optional.empty();
      }
      return Optional.ofNullable(p.getCodec().readValue(p, Object.class));
    }
  }
}

