package com.example;

import com.example.service.DataInitService;
import com.example.service.PerformanceTestService;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class App {
    public static void main(String[] args) {
        // Create the EntityManagerFactory
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-performance");

        try {
            // Initialize data
            DataInitService dataInitService = new DataInitService(emf);
            dataInitService.initData();

            // Performance test service
            PerformanceTestService performanceTestService = new PerformanceTestService(emf);

            // Test 1: N+1 problem without optimization
            System.out.println("\n\n=== TEST 1: N+1 PROBLEM WITHOUT OPTIMIZATION ===");
            performanceTestService.testN1Problem();

            // Test 2: N+1 problem resolved with JOIN FETCH
            System.out.println("\n\n=== TEST 2: RESOLUTION WITH JOIN FETCH ===");
            performanceTestService.testJoinFetch();

            // Test 3: N+1 problem resolved with Entity Graphs
            System.out.println("\n\n=== TEST 3: RESOLUTION WITH ENTITY GRAPHS ===");
            performanceTestService.testEntityGraph();

            // Test 4: Second-level cache test
            System.out.println("\n\n=== TEST 4: SECOND-LEVEL CACHE ===");
            performanceTestService.testSecondLevelCache();

            // Test 5: Performance comparison with and without cache
            System.out.println("\n\n=== TEST 5: PERFORMANCE COMPARISON ===");
            performanceTestService.testPerformanceComparison();

        } finally {
            // Close the EntityManagerFactory
            emf.close();
        }
    }
}
