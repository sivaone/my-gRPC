package com.shopping.service;

import com.google.protobuf.util.Timestamps;
import com.shopping.db.Order;
import com.shopping.db.OrderDao;
import com.shopping.stubs.order.OrderRequest;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.order.OrderServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {
    private static final Logger logger = Logger.getLogger(OrderServiceImpl.class.getName());
    private final OrderDao orderDao = new OrderDao();

    @Override
    public void getOrders(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        logger.info("Request received to get orders for user: " + request.getUserId());
        List<Order> orders = orderDao.getOrders(request.getUserId());
        List<com.shopping.stubs.order.Order> ordersForUser = new ArrayList<>(orders.size());
        for(Order order: orders) {
            com.shopping.stubs.order.Order ord = com.shopping.stubs.order.Order.newBuilder()
                    .setUserId(order.getUserId())
                    .setOrderId(order.getOrderId())
                    .setOrderDate(Timestamps.fromMillis(order.getOrderDate().getTime()))
                    .setNoOfItems(order.getNumberOfItems())
                    .setTotalAmount(order.getAmount())
                    .build();
            ordersForUser.add(ord);
        }
        OrderResponse response = OrderResponse.newBuilder().addAllOrder(ordersForUser).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
