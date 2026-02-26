# Bibliothèque JPA — Cache & Optimisation des Performances

A Java JPA project demonstrating performance optimization techniques: N+1 problem resolution, Entity Graphs, and Second-Level Cache with Hibernate.

---

## 🎬 Demo Video

[![Watch the demo](https://img.youtube.com/vi/nKeg6PJpMBE/0.jpg)](https://youtu.be/nKeg6PJpMBE)

---

## Project Structure

```
com.example/
├── model/
│   ├── Auteur.java                  # Author entity (@Cacheable)
│   ├── Categorie.java               # Category entity (@Cacheable)
│   └── Livre.java                   # Book entity (@Cacheable, @NamedEntityGraph)
├── service/
│   ├── DataInitService.java         # Data initialization (authors, books, categories)
│   └── PerformanceTestService.java  # Performance tests (N+1, JOIN FETCH, cache)
```

---

## Entities

### `Auteur`
Represents a book author with `nom`, `prenom`, and a unique `email`.
Has a `@OneToMany` relationship with `Livre`. Marked `@Cacheable` for second-level cache.

### `Categorie`
Represents a book category (Roman, Science-Fiction, Fantasy, etc.).
Has a `@ManyToMany` relationship with `Livre`. Marked `@Cacheable`.

### `Livre`
Represents a book with `titre`, `anneePublication`, `isbn`, and `resume`.
- `@ManyToOne` → `Auteur`
- `@ManyToMany` → `Categorie`
- `@NamedEntityGraph` to eagerly load categories and author in one query
- Marked `@Cacheable`

---

## Key Features & Tests

### 1. N+1 Problem (without optimization)
Fetches all authors, then for each author fetches their books separately — generating **N+1 SQL queries**.

### 2. JOIN FETCH
Resolves the author→books N+1 issue by loading everything in **a single query** using `LEFT JOIN FETCH`.

### 3. Entity Graphs
Uses `@NamedEntityGraph` to load books with their authors and categories in **one query**, fully eliminating the N+1 problem.

### 4. Second-Level Cache
Tests Hibernate's second-level cache:
- **First access** → cache miss (hits the DB)
- **Second access** → cache hit (served from cache, no DB query)
- Also tests **cached JPQL queries** with `org.hibernate.cacheable` hint.

### 5. Performance Comparison
Runs 100 iterations with and without cache and compares execution times to show the real benefit of caching.

---

## Requirements

- Java 10+
- JPA 2.x with Hibernate
- A second-level cache provider (e.g., EhCache)
- A configured `persistence.xml` with:
  - Statistics enabled: `hibernate.generate_statistics=true`
  - Second-level cache enabled: `hibernate.cache.use_second_level_cache=true`

---
