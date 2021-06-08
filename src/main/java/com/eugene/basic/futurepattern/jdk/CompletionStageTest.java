package com.eugene.basic.futurepattern.jdk;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class CompletionStageTest {

    public static void main(String[] args) {
        String content = "Hello";
        CompletionStage<String> completionStage = CompletableFuture.completedFuture(content);
        completionStage.thenAccept((item) -> item = item + " avengerEug")
                .thenAccept(System.out::print);

    }

}
