package com.baeldung.lsd;

import com.baeldung.lsd.persistence.repository.ProductCartRepository;
import com.baeldung.lsd.worker.CartDeleteWorker;
import com.baeldung.lsd.worker.CartInsertWorker;
import com.baeldung.lsd.worker.CreditCardWorker;
import com.netflix.conductor.client.automator.TaskRunnerConfigurer;
import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.worker.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Collection;

@SpringBootApplication
public class CartSetupApp implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(CartSetupApp.class);
    @Autowired
    private ProductCartRepository productCartRepository;


    public static void main(final String... args) {

        SpringApplication.run(CartSetupApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TaskClient taskClient = new TaskClient();
        taskClient.setRootURI("http://conductor:8080/api/"); // Point this to the server API

        int threadCount = 1; // number of threads used to execute workers.  To avoid starvation, should be


        Worker worker1 = new CartInsertWorker("insert_product_in_the_cart", productCartRepository);
        Worker worker2 = new CreditCardWorker("check_credit_card");
        Worker worker3 = new CartDeleteWorker("cart_delete_product", productCartRepository);

        Collection workerArrayList = new ArrayList<Worker>();
        workerArrayList.add(worker1);
        workerArrayList.add(worker2);
        workerArrayList.add(worker3);

        TaskRunnerConfigurer configurer =
                new TaskRunnerConfigurer.Builder(taskClient, workerArrayList)
                        .withThreadCount(threadCount)
                        .build();
        // Start the polling and execution of tasks
        configurer.init();

        LOG.info("Starting Spring Boot application...");
    }

}
