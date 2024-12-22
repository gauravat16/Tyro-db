package com.gaurav;

import com.gaurav.server.service.NamespaceServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello world!");
        Server server = ServerBuilder.forPort(5003)
                .addService(new NamespaceServiceImpl())
                .build();

        server.start();

        System.out.println("Server started on - " + server.getPort());
        server.awaitTermination();
    }
}