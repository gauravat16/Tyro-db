package com.gaurav;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello world!");
        Server server = ServerBuilder.forPort(5003)
                .build();

        server.start();

        System.out.println("Server started on - " + server.getPort());
        server.awaitTermination();
    }
}