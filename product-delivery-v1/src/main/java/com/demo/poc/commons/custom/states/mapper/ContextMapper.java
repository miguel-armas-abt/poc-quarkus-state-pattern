package com.demo.poc.commons.custom.states.mapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.demo.poc.commons.custom.exceptions.ParsingJsonException;
import com.demo.poc.commons.custom.states.context.Context;
import com.demo.poc.commons.custom.states.context.ProductDeliveryContext;
import com.demo.poc.commons.custom.states.dto.response.ContextResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ContextMapper {

  private final ObjectMapper objectMapper;

  public ContextResponse toResponse(Context context) {
    return ContextResponse.builder()
        .nextState(context.getCurrentState().name())
        .ciphered(mapCiphered(context))
        .build();
  }

  private String mapCiphered(Context context) {
    try {
      String contextJson = objectMapper.writeValueAsString(context);
      return new String(Base64.getEncoder().encode(contextJson.getBytes(StandardCharsets.UTF_8)));
    }
    catch (JsonProcessingException exception) {
      throw new ParsingJsonException(exception.getMessage());
    }
  }

  public ProductDeliveryContext toContext(String ciphered) {
    String contextJson = new String(Base64.getDecoder().decode(ciphered));
    try {
      return objectMapper.readValue(contextJson, ProductDeliveryContext.class);
    }
    catch (JsonProcessingException exception) {
      throw new ParsingJsonException(exception.getMessage());
    }
  }
}
