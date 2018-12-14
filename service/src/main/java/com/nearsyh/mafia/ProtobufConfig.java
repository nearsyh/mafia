package com.nearsyh.mafia;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;

@Configuration
public class ProtobufConfig {

  @Bean
  @Primary
  ProtobufJsonFormatHttpMessageConverter protobufJsonFormatHttpMessageConverter() {
    return new ProtobufJsonFormatHttpMessageConverter();
  }

}
