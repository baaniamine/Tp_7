package com.example.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "auteurs")
@Cacheable // Mark entity as cacheable
public class Auteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(unique = true)
    private String email;

    @OneToMany(mappedBy = "auteur", cascade = CascadeType.ALL)
    private List<Livre> livres = new ArrayList<>();

    // Constructors
    public Auteur() {
    }

    public Auteur(String nom, String prenom, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }

    // Utility methods
    public void addLivre(Livre livre) {
        livres.add(livre);
        livre.setAuteur(this);
    }

    public void removeLivre(Livre livre) {
        livres.remove(livre);
        livre.setAuteur(null);
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Livre> getLivres() {
        return livres;
    }

    public void setLivres(List<Livre> livres) {
        this.livres = livres;
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", lastName='" + nom + '\'' +
                ", firstName='" + prenom + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
