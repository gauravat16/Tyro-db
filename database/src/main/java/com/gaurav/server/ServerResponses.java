package com.gaurav.server;

public enum ServerResponses {
    NAMESPACE_CREATED(1000),
    NAMESPACE_ALREADY_PRESENT(1001),
    NAMESPACE_CREATION_FAILED(1002);
    private int code;

    ServerResponses(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
