package com.gaurav.server.service;

import com.gaurav.datastore.api.SchemaService;
import com.gaurav.server.ServerResponses;
import com.google.inject.Inject;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class NamespaceServiceImpl extends com.gaurav.server.service.NamespaceServiceGrpc.NamespaceServiceImplBase {
    private static final Logger logger = LogManager.getLogger(NamespaceServiceImpl.class);


    private final SchemaService schemaService;

    @Inject
    public NamespaceServiceImpl(SchemaService schemaService) {
        this.schemaService = schemaService;
    }

    @Override
    public void createNamespace(com.gaurav.server.service.NamespaceCreationRequest request, StreamObserver<com.gaurav.server.service.Response> responseObserver) {
        try {
            boolean isCreated = schemaService.createNamespace(request.getName());
            ServerResponses responseCode = isCreated ? ServerResponses.NAMESPACE_CREATED : ServerResponses.NAMESPACE_ALREADY_PRESENT;

            responseObserver.onNext(com.gaurav.server.service.Response.newBuilder().setStatus(responseCode.getCode()).setVersion(1).build());
        } catch (Exception e) {
            logger.error("Failed to process request", e);
            responseObserver.onNext(com.gaurav.server.service.Response.newBuilder().setStatus(ServerResponses.NAMESPACE_CREATION_FAILED.getCode()).setVersion(1).build());
        }
        responseObserver.onCompleted();


    }
}
