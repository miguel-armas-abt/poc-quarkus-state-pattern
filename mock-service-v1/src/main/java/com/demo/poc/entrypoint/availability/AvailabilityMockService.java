package com.demo.poc.entrypoint.availability;

import java.util.concurrent.TimeUnit;

import com.demo.poc.commons.custom.config.MockService;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.MatchType;
import org.mockserver.model.Header;
import org.mockserver.model.HttpStatusCode;
import org.mockserver.model.JsonBody;

import org.springframework.stereotype.Component;

import static com.demo.poc.commons.custom.utils.DelayUtil.generateRandomDelay;
import static com.demo.poc.commons.custom.utils.FileReader.readRaw;
import static com.demo.poc.commons.custom.utils.HeadersGenerator.contentType;
import static com.demo.poc.commons.custom.utils.HeadersGenerator.generateTraceId;
import static com.demo.poc.commons.custom.utils.FileReader.readJson;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@Component
public class AvailabilityMockService implements MockService {

  private static final String JSON_REQUEST_BODY = "{\"pendingOrderId\":\"20231030182329822184\",\"ubigeo\":\"150101\",\"courierCode\":\"49\"}";

  @Override
  public void loadMocks(ClientAndServer mockServer) {

    mockServer
        .when(request()
            .withMethod("POST")
            .withPath("/poc/delivery-availability/v1/available-dates")
            .withBody(JsonBody.json(JSON_REQUEST_BODY, MatchType.ONLY_MATCHING_FIELDS)))
        .respond(request -> {

          long randomDelay = generateRandomDelay();
          Header traceIdHeader = generateTraceId();

          return response()
              .withStatusCode(HttpStatusCode.OK_200.code())
              .withHeader(contentType("application/x-ndjson"))
              .withHeader(traceIdHeader)
              .withBody(readRaw("mocks/availability/Availability.200.json"))
              .withDelay(TimeUnit.MILLISECONDS, randomDelay);
        });
  }
}
