package com.fullcycle.admin.catalogo.infrastructure.genre.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record CreateGenreRequest(
        String name,
        @JsonProperty("categories_id") List<String> categories,
        @JsonProperty("is_active") Boolean isActive
) {

    public List<String> categories() {
        return Objects.isNull(this.categories) ? Collections.emptyList() : this.categories;
    }

    public Boolean isActive() {
        return Objects.isNull(this.isActive) || this.isActive;
    }
}
