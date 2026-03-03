package com.example.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "livres")
@Cacheable // Mark entity as cacheable
@NamedEntityGraph(
        name = "graph.Livre.categoriesEtAuteur",
        attributeNodes = {
                @NamedAttributeNode("categories"),
                @NamedAttributeNode("auteur")
        }
)
public class Livre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(name = "annee_publication")
    private Integer anneePublication;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(length = 2000)
    private String resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id")
    private Auteur auteur;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "livre_categorie",
            joinColumns = @JoinColumn(name = "livre_id"),
            inverseJoinColumns = @JoinColumn(name = "categorie_id")
    )
    private Set<Categorie> categories = new HashSet<>();

    // Constructors
    public Livre() {
    }

    public Livre(String titre) {
        this.titre = titre;
    }

    public Livre(String titre, Integer anneePublication, String isbn) {
        this.titre = titre;
        this.anneePublication = anneePublication;
        this.isbn = isbn;
    }

    // Utility methods
    public void addCategorie(Categorie categorie) {
        categories.add(categorie);
        categorie.getLivres().add(this);
    }

    public void removeCategorie(Categorie categorie) {
        categories.remove(categorie);
        categorie.getLivres().remove(this);
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Integer getAnneePublication() {
        return anneePublication;
    }

    public void setAnneePublication(Integer anneePublication) {
        this.anneePublication = anneePublication;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public Auteur getAuteur() {
        return auteur;
    }

    public void setAuteur(Auteur auteur) {
        this.auteur = auteur;
    }

    public Set<Categorie> getCategories() {
        return categories;
    }

    public void setCategories(Set<Categorie> categories) {
        this.categories = categories;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + titre + '\'' +
                ", publicationYear=" + anneePublication +
                ", isbn='" + isbn + '\'' +
                '}';
    }
}
