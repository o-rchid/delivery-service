package org.delivery.storeadmin.domain.userorder.business;

import lombok.RequiredArgsConstructor;
import org.delivery.common.message.model.UserOrderMessage;
import org.delivery.db.usrorder.UserOrderEntity;
import org.delivery.storeadmin.domain.sse.connection.SseConnectionPool;
import org.delivery.storeadmin.domain.storemenu.converter.StoreMenuConverter;
import org.delivery.storeadmin.domain.storemenu.service.StoreMenuService;
import org.delivery.storeadmin.domain.userorder.controller.model.UserOrderDetailResponse;
import org.delivery.storeadmin.domain.userorder.converter.UserOrderConverter;
import org.delivery.storeadmin.domain.userorder.service.UserOrderService;
import org.delivery.storeadmin.domain.userordermenu.service.UserOrderMenuService;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserOrderBusiness {

    private final UserOrderService userOrderService;

    private final UserOrderConverter userOrderConverter;

    private final SseConnectionPool sseConnectionPool;

    private final UserOrderMenuService userOrderMenuService;

    private final StoreMenuService storeMenuService;

    private final StoreMenuConverter  storeMenuConverter;

    public void pushUserOrder(UserOrderMessage userOrderMessage){
        var userOrderEntity = userOrderService.getUserOrder(userOrderMessage.getUserOrderId()).orElseThrow(
                () -> new RuntimeException("사용자 주문 내역 없음"));

        var userOrderMenuList = userOrderMenuService.getUserOrderMenuList(userOrderEntity.getId());

        var storeMenuEntityList = userOrderMenuList.stream()
                .map(userOrderMenuEntity -> {
                    return storeMenuService.getStoreMenuWithThrow(userOrderMenuEntity.getStoreMenuId());
                })
                .map(storeMenuEntity -> {
                    return storeMenuConverter.toResponse(storeMenuEntity);
                })
                .collect(Collectors.toList());

        var userOrderResponse = userOrderConverter.toResponse(userOrderEntity);

        var push = UserOrderDetailResponse.builder()
                .userOrderResponse(userOrderResponse)
                .storeMenuList(storeMenuEntityList)
                .build();

        var userConnection = sseConnectionPool.getSession(userOrderEntity.getStoreId().toString());

        // 사용자에게 push
        userConnection.sendMessage(push);
    }
}
