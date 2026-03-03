package com.example.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Cacheable // Mark entity as cacheable
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(length = 500)
    private String description;

    @ManyToMany(mappedBy = "categories")
    private Set<Livre> livres = new HashSet<>();

    // Constructors
    public Categorie() {
    }

    public Categorie(String nom) {
        this.nom = nom;
    }

    public Categorie(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Livre> getLivres() {
        return livres;
    }

    public void setLivres(Set<Livre> livres) {
        this.livres = livres;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + nom + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
