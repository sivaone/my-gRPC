package com.shopping.service;

import com.shopping.client.OrderClient;
import com.shopping.db.User;
import com.shopping.db.UserDao;
import com.shopping.stubs.order.Order;
import com.shopping.stubs.user.UserRequest;
import com.shopping.stubs.user.UserResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserServiceImpl extends com.shopping.stubs.user.UserServiceGrpc.UserServiceImplBase {

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
    private final UserDao dao = new UserDao();

    @Override
    public void getUserDetails(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        logger.log(Level.INFO, "Username is : " + request.getUsername());

        User user = dao.getDetails(request.getUsername());

        UserResponse.Builder builder = UserResponse.newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setName(user.getName())
                .setAge(user.getAge())
                .setGender(com.shopping.stubs.user.Gender.valueOf(user.getGender()));

        logger.info("Retrieving orders for user");
        int orderSize = getOrderSizeForUser(user);
        builder.setNoOfOrders(orderSize);

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
        logger.log(Level.INFO, "User details completed : " + user.getName());
    }

    private int getOrderSizeForUser(User user) {
        // get orders by invoking order client

        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50052")
                .usePlaintext()
                .build();
        OrderClient orderClient = new OrderClient(channel);
        List<Order> orders = orderClient.getOrders(user.getId());

        try {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Channel shutdown issue", e);
        }

        return orders.size();
    }
}
