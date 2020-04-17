package com.aurum.casesintegrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.data.datastore.repository.config.EnableDatastoreRepositories;

@SpringBootApplication
@EnableDatastoreRepositories
public class CasesIntegratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CasesIntegratorApplication.class, args);
    }

}
