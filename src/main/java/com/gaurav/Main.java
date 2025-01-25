package com.gaurav;

import com.gaurav.server.config.TyroConfig;
import com.gaurav.server.injection.module.TyroModule;
import com.gaurav.server.service.NamespaceServiceGrpc;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String overridePath = null;
        if (args.length > 0) {
            overridePath = args[0].trim();
        }

        TyroConfig tyroConfig = getTyroConfig(overridePath);

        Injector injector = Guice.createInjector(new TyroModule(tyroConfig));

        Server server = ServerBuilder.forPort(5003)
                .addService(injector.getInstance(NamespaceServiceGrpc.NamespaceServiceImplBase.class))
                .build();

        server.start();

        System.out.println("Server started on - " + server.getPort());
        server.awaitTermination();
    }

    private static TyroConfig getTyroConfig(String path) throws IOException {
        Yaml yaml = new Yaml();
        if (path == null || path.isEmpty()) {
            return yaml.loadAs(Main.class.getClassLoader().getResourceAsStream("config.yml"), TyroConfig.class);
        } else {
            return yaml.loadAs(Files.readString(Path.of(path)), TyroConfig.class);
        }
    }
}