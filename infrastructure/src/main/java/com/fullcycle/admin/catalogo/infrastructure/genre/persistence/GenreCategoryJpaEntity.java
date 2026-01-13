package com.fullcycle.admin.catalogo.infrastructure.genre.persistence;

import com.fullcycle.admin.catalogo.domain.category.CategoryID;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "genres_categories")
public class GenreCategoryJpaEntity {
    @EmbeddedId
    private GenreCategoryID id;

    @ManyToOne
    @MapsId("genreId")
    private GenreJpaEntity genre;

    public GenreCategoryJpaEntity() {
    }

    private GenreCategoryJpaEntity(final GenreJpaEntity genre, final GenreCategoryID id) {
        this.genre = genre;
        this.id = GenreCategoryID.from(id.getGenreId(), id.getCategoryId());
    }

    public static GenreCategoryJpaEntity from(final GenreJpaEntity genre, final GenreCategoryID id) {
        return new GenreCategoryJpaEntity(genre, id);
    }

    public static GenreCategoryJpaEntity from(GenreJpaEntity genre, CategoryID categoryId) {
        return new GenreCategoryJpaEntity(genre, GenreCategoryID.from(genre.getId(), categoryId.getValue()));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GenreCategoryJpaEntity that = (GenreCategoryJpaEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(genre, that.genre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, genre);
    }

    public GenreCategoryID getId() {
        return id;
    }

    public void setId(GenreCategoryID id) {
        this.id = id;
    }

    public GenreJpaEntity getGenre() {
        return genre;
    }

    public void setGenre(GenreJpaEntity genre) {
        this.genre = genre;
    }
}
