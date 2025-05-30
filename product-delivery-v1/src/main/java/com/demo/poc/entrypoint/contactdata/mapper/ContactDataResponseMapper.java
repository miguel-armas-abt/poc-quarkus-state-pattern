package com.demo.poc.entrypoint.contactdata.mapper;

import java.util.List;

import com.demo.poc.commons.core.config.MappingConfig;
import com.demo.poc.commons.custom.states.context.contactdata.AddressContext;
import com.demo.poc.commons.custom.states.dto.response.ContextResponse;
import com.demo.poc.entrypoint.contactdata.dto.response.ContactDataResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MappingConfig.class)
public interface ContactDataResponseMapper {

  @Mapping(target = "addresses", source = "addresses")
  ContactDataResponseDto toResponse(ContextResponse context, List<AddressContext> addresses);
}
