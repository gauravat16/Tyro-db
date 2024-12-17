package com.gaurav.datastore.schema;

public class TyroSchema implements Validator {
    private final Namespace namespace;


    public TyroSchema(Namespace namespace) {
        this.namespace = namespace;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    @Override
    public boolean validate() {
        return namespace.validate();
    }

    @Override
    public String toString() {
        return "TyroSchema{" +
                "namespace=" + namespace +
                '}';
    }
}
