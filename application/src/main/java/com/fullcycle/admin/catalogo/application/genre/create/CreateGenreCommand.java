package com.fullcycle.admin.catalogo.application.genre.create;

import java.util.List;
import java.util.Objects;

public record CreateGenreCommand(
        String name,
        boolean isActive,
        List<String> categories
) {

    public static CreateGenreCommand with(final String aName, final Boolean isActive, final List<String> categories){
        return new CreateGenreCommand(aName, Objects.isNull(isActive) || isActive, categories);
    }
}
