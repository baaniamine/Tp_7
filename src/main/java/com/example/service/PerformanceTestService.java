package com.example.service;

import com.example.model.Auteur;
import com.example.model.Livre;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;

import javax.persistence.*;
import java.util.List;

public class PerformanceTestService {

    private final EntityManagerFactory emf;

    public PerformanceTestService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void resetStatistics() {
        Session session = emf.createEntityManager().unwrap(Session.class);
        Statistics stats = session.getSessionFactory().getStatistics();
        stats.clear();
    }

    public void printStatistics(String testName) {
        Session session = emf.createEntityManager().unwrap(Session.class);
        Statistics stats = session.getSessionFactory().getStatistics();

        System.out.println("\n=== Statistics for " + testName + " ===");
        System.out.println("Queries executed: " + stats.getQueryExecutionCount());
        System.out.println("Max query execution time: " + stats.getQueryExecutionMaxTime() + "ms");
        System.out.println("Entities loaded: " + stats.getEntityLoadCount());
        System.out.println("Second-level cache hits: " + stats.getSecondLevelCacheHitCount());
        System.out.println("Second-level cache misses: " + stats.getSecondLevelCacheMissCount());
        System.out.println("Cache hit ratio: " +
                (stats.getSecondLevelCacheHitCount() + stats.getSecondLevelCacheMissCount() > 0 ?
                        (double) stats.getSecondLevelCacheHitCount() / (stats.getSecondLevelCacheHitCount() + stats.getSecondLevelCacheMissCount()) : 0));
    }

    // Test 1: N+1 problem without optimization
    public void testN1Problem() {
        resetStatistics();
        long startTime = System.currentTimeMillis();

        EntityManager em = emf.createEntityManager();
        try {
            // Fetch all authors
            List<Auteur> auteurs = em.createQuery("SELECT a FROM Auteur a", Auteur.class)
                    .getResultList();

            // For each author, access their books (generates N+1 queries)
            for (Auteur auteur : auteurs) {
                System.out.println("Author: " + auteur.getNom() + " " + auteur.getPrenom());
                System.out.println("Number of books: " + auteur.getLivres().size());

                // Access book details
                for (Livre livre : auteur.getLivres()) {
                    System.out.println("  - " + livre.getTitre() + " (" + livre.getAnneePublication() + ")");
                    // Access categories (generates even more queries)
                    System.out.println("    Categories: " + livre.getCategories().size());
                }
            }

        } finally {
            em.close();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        printStatistics("N+1 problem without optimization");
    }

    // Test 2: N+1 problem resolved with JOIN FETCH
    public void testJoinFetch() {
        resetStatistics();
        long startTime = System.currentTimeMillis();

        EntityManager em = emf.createEntityManager();
        try {
            // Fetch authors with their books in a single query
            List<Auteur> auteurs = em.createQuery(
                            "SELECT DISTINCT a FROM Auteur a LEFT JOIN FETCH a.livres", Auteur.class)
                    .getResultList();

            // For each author, access their books (already loaded)
            for (Auteur auteur : auteurs) {
                System.out.println("Author: " + auteur.getNom() + " " + auteur.getPrenom());
                System.out.println("Number of books: " + auteur.getLivres().size());

                // Access book details
                for (Livre livre : auteur.getLivres()) {
                    System.out.println("  - " + livre.getTitre() + " (" + livre.getAnneePublication() + ")");
                    // Access categories (still generates N+1 queries)
                    System.out.println("    Categories: " + livre.getCategories().size());
                }
            }

        } finally {
            em.close();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        printStatistics("N+1 resolved with JOIN FETCH");
    }

    // Test 3: N+1 problem resolved with Entity Graphs
    public void testEntityGraph() {
        resetStatistics();
        long startTime = System.currentTimeMillis();

        EntityManager em = emf.createEntityManager();
        try {
            // Use a named entity graph
            EntityGraph<?> graph = em.getEntityGraph("graph.Livre.categoriesEtAuteur");

            // Fetch books with their categories and authors in a single query
            List<Livre> livres = em.createQuery("SELECT l FROM Livre l", Livre.class)
                    .setHint("javax.persistence.fetchgraph", graph)
                    .getResultList();

            // Access book details
            for (Livre livre : livres) {
                System.out.println("Book: " + livre.getTitre() + " (" + livre.getAnneePublication() + ")");
                System.out.println("  Author: " + livre.getAuteur().getNom() + " " + livre.getAuteur().getPrenom());
                System.out.println("  Categories: " + livre.getCategories().size());
            }

        } finally {
            em.close();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime) + "ms");
        printStatistics("N+1 resolved with Entity Graphs");
    }

    // Test 4: Second-level cache test
    public void testSecondLevelCache() {
        System.out.println("\n=== Second-level cache test ===");

        // First access (cache miss)
        resetStatistics();
        System.out.println("\nFirst access (cache miss):");
        EntityManager em1 = emf.createEntityManager();
        try {
            Auteur auteur = em1.find(Auteur.class, 1L);
            System.out.println("Author found: " + auteur.getNom() + " " + auteur.getPrenom());
        } finally {
            em1.close();
        }
        printStatistics("First access");

        // Second access (cache hit)
        resetStatistics();
        System.out.println("\nSecond access (cache hit):");
        EntityManager em2 = emf.createEntityManager();
        try {
            Auteur auteur = em2.find(Auteur.class, 1L);
            System.out.println("Author found: " + auteur.getNom() + " " + auteur.getPrenom());
        } finally {
            em2.close();
        }
        printStatistics("Second access");

        // Test with a cached query
        System.out.println("\nTest with a cached query:");

        // First query access
        resetStatistics();
        System.out.println("\nFirst query access:");
        EntityManager em3 = emf.createEntityManager();
        try {
            TypedQuery<Auteur> query = em3.createQuery(
                            "SELECT a FROM Auteur a WHERE a.nom = :nom", Auteur.class)
                    .setParameter("nom", "Hugo")
                    .setHint("org.hibernate.cacheable", "true");

            List<Auteur> auteurs = query.getResultList();
            for (Auteur auteur : auteurs) {
                System.out.println("Author found: " + auteur.getNom() + " " + auteur.getPrenom());
            }
        } finally {
            em3.close();
        }
        printStatistics("First query access");

        // Second query access
        resetStatistics();
        System.out.println("\nSecond query access:");
        EntityManager em4 = emf.createEntityManager();
        try {
            TypedQuery<Auteur> query = em4.createQuery(
                            "SELECT a FROM Auteur a WHERE a.nom = :nom", Auteur.class)
                    .setParameter("nom", "Hugo")
                    .setHint("org.hibernate.cacheable", "true");

            List<Auteur> auteurs = query.getResultList();
            for (Auteur auteur : auteurs) {
                System.out.println("Author found: " + auteur.getNom() + " " + auteur.getPrenom());
            }
        } finally {
            em4.close();
        }
        printStatistics("Second query access");
    }

    // Test 5: Performance comparison with and without cache
    public void testPerformanceComparison() {
        System.out.println("\n=== Performance comparison with and without cache ===");

        // Test without cache (temporarily disabled)
        EntityManager em = emf.createEntityManager();
        try {
            // Disable the cache for this test
            em.unwrap(Session.class).getSessionFactory().getCache().evictAllRegions();

            System.out.println("\nTest without cache:");
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < 100; i++) {
                Auteur auteur = em.find(Auteur.class, (i % 4) + 1L);
                // Force loading of books
                auteur.getLivres().size();
            }

            long endTime = System.currentTimeMillis();
            System.out.println("Execution time without cache: " + (endTime - startTime) + "ms");

        } finally {
            em.close();
        }

        // Test with cache
        System.out.println("\nTest with cache:");
        resetStatistics();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            EntityManager em2 = emf.createEntityManager();
            try {
                Auteur auteur = em2.find(Auteur.class, (i % 4) + 1L);
                // Force loading of books
                auteur.getLivres().size();
            } finally {
                em2.close();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time with cache: " + (endTime - startTime) + "ms");
        printStatistics("Test with cache");
    }
}
