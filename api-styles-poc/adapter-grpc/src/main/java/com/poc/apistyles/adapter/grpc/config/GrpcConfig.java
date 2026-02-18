package com.poc.apistyles.adapter.grpc.config;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

    @Bean
    public Server grpcServer(ServerBuilder<?> serverBuilder) {
        return serverBuilder.port(9090, io.grpc.NettyServerBuilder.forPort(9090))
                .build();
    }
}
