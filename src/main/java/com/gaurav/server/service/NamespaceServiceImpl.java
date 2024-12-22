package com.gaurav.server.service;

import io.grpc.stub.StreamObserver;

public class NamespaceServiceImpl extends com.gaurav.server.service.NamespaceServiceGrpc.NamespaceServiceImplBase {

    @Override
    public void createNamespace(com.gaurav.server.service.NamespaceCreationRequest request, StreamObserver<com.gaurav.server.service.NamespaceCreationResponse> responseObserver) {
        responseObserver.onNext(com.gaurav.server.service.NamespaceCreationResponse.newBuilder().setIsCreated(true).build());
        responseObserver.onCompleted();
    }
}
