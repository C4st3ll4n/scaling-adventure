package com.fullcycle.admin.catalogo.domain.genre;

import com.fullcycle.admin.catalogo.domain.AggregateRoot;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Genre extends AggregateRoot<GenreID> {

    private String name;
    private boolean isActive;
    private ArrayList<CategoryID> categories;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    protected Genre(final GenreID genreID,
            final String aName,
            final boolean isActive,
            final Instant aCreatedAt,
            final Instant aUpdatedAt,
            final Instant aDeletedAt,
            final ArrayList<CategoryID> categories) {
        super(genreID);
        this.name = aName;
        this.isActive = isActive;
        this.createdAt = aCreatedAt;
        this.updatedAt = aUpdatedAt;
        this.deletedAt = aDeletedAt;
        this.categories = categories;
        selfValidate();
    }

    public static Genre newGenre(String aName, boolean isActive) {
        return new Genre(GenreID.unique(), aName, isActive, Instant.now(), Instant.now(), null,
                new ArrayList<>());
    }

    public static Genre with(final GenreID genreID,
            final String name,
            final boolean isActive,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt,
            final ArrayList<CategoryID> categories) {
        return new Genre(genreID, name, isActive, createdAt, updatedAt, deletedAt, categories);
    }

    public static Genre with(final Genre aGenre) {
        return with(
                aGenre.getId(),
                aGenre.name,
                aGenre.isActive(),
                aGenre.createdAt,
                aGenre.updatedAt,
                aGenre.deletedAt,
                aGenre.categories);
    }

    @Override
    public void validate(ValidationHandler handler) {
        new GenreValidator(this, handler).validate();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<CategoryID> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public void setCategories(ArrayList<CategoryID> categories) {
        this.categories = categories;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Genre deactivate() {
        if (this.deletedAt == null) {
            this.deletedAt = Instant.now();
        }

        this.isActive = false;
        this.updatedAt = Instant.now();
        return this;
    }

    public Genre activate() {
        this.deletedAt = null;
        this.isActive = true;
        this.updatedAt = Instant.now();
        return this;
    }

    public Genre update(final String name, final boolean isActive, final List<CategoryID> categories) {
        this.name = name;
        this.isActive = isActive;
        this.categories = Objects.nonNull(categories) ? new ArrayList<>(categories) : new ArrayList<>();
        this.updatedAt = Instant.now();
        selfValidate();
        return this;
    }

    private void selfValidate() {
        final var notification = Notification.create();
        validate(notification);
        if (notification.hasError()) {
            throw new NotificationException("Failed to validate aggregate Genre", notification);
        }
    }

    public Genre addCategory(CategoryID categoryID) {
        if (categoryID == null) {
            return this;
        }
        this.categories.add(categoryID);
        this.updatedAt = Instant.now();
        return this;
    }

    public Genre removeCategory(CategoryID categoryID) {
        if (categoryID == null) {
            return this;
        }
        if (!this.categories.contains(categoryID)) {
            return this;
        }
        this.categories.remove(categoryID);
        this.updatedAt = Instant.now();
        return this;
    }

    public Genre addCategory(List<CategoryID> categories) {
        if (categories == null) {
            return this;
        }
        this.categories.addAll(categories);
        this.updatedAt = Instant.now();
        return this;

    }
}
