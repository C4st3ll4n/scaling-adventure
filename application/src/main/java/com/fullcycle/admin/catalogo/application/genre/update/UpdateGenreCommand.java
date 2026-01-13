package com.fullcycle.admin.catalogo.application.genre.update;

import com.fullcycle.admin.catalogo.domain.genre.GenreID;

import java.util.List;
import java.util.Objects;

public record UpdateGenreCommand(
        GenreID id,
        String name,
        boolean isActive,
        List<String> categories
) {

    public static UpdateGenreCommand with(final GenreID id, final String aName,
                                          final Boolean isActive, final List<String> categories) {
        return new UpdateGenreCommand(id, aName, Objects.isNull(isActive) || isActive, categories);
    }
}
