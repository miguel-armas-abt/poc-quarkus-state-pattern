package com.demo.poc.entrypoint.contactdata.service;

import com.demo.poc.commons.custom.states.StateDispatcher;
import com.demo.poc.commons.custom.exceptions.NoSuchPendingOrderException;
import com.demo.poc.commons.custom.states.dto.response.ContextResponse;
import com.demo.poc.commons.custom.states.mapper.ContextMapper;
import com.demo.poc.entrypoint.contactdata.dto.request.ContactDataRequestDto;
import com.demo.poc.entrypoint.contactdata.dto.response.ContactDataResponseDto;
import com.demo.poc.entrypoint.contactdata.mapper.ContactDataContextMapper;
import com.demo.poc.entrypoint.contactdata.mapper.ContactDataResponseMapper;
import com.demo.poc.entrypoint.contactdata.repository.contactdata.ContactDataRepository;
import com.demo.poc.entrypoint.contactdata.repository.contactdata.wrapper.response.ContactDataResponseWrapper;
import com.demo.poc.entrypoint.contactdata.repository.orders.PendingOrderRepository;
import com.demo.poc.entrypoint.contactdata.repository.orders.wrapper.response.PendingOrderResponseWrapper;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
@RequiredArgsConstructor
public class ContactDataServiceImpl implements ContactDataService {

  @RestClient
  PendingOrderRepository pendingOrderRepository;

  @RestClient
  ContactDataRepository contactDataRepository;

  private final StateDispatcher stateDispatcher;
  private final ContactDataContextMapper contactDataContextMapper;
  private final ContextMapper contextMapper;
  private final ContactDataResponseMapper responseMapper;

  @Override
  public Uni<ContactDataResponseDto> getContactData(ContactDataRequestDto request) {
    String customerId = request.getCustomer().getCustomerId();
    String pendingOrderId = request.getPendingOrderId();

    Uni<PendingOrderResponseWrapper> pendingOrderUni = getPendingOrder(customerId, pendingOrderId);
    Uni<ContactDataResponseWrapper> contactDataUni = contactDataRepository.getContactData(customerId);

    return Uni.combine().all()
        .unis(pendingOrderUni, contactDataUni)
        .with((pendingOrder, contactData) -> contactDataContextMapper.toContext(pendingOrder, contactData, request))
        .flatMap(this.stateDispatcher::next)
        .map(context -> {
          ContextResponse contextResponse = contextMapper.toResponse(context);
          return responseMapper.toResponse(contextResponse, context.getContactData().getAddresses());
        });
  }

  private Uni<PendingOrderResponseWrapper> getPendingOrder(String customerId, String pendingOrderId) {
    return pendingOrderRepository.getPendingOrders(customerId)
        .filter(pendingOrder -> pendingOrder.getId().equals(pendingOrderId))
        .toUni()
        .onItem().ifNull().failWith(new NoSuchPendingOrderException(pendingOrderId));
  }
}
