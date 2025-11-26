package com.fullcycle.admin.catalogo.application.genre.retrieve.get;

import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;

import java.util.function.Supplier;

public class DefaultGetGenreByIdUseCase extends GetGenreByIdUseCase {

    private final GenreGateway gateway;

    public DefaultGetGenreByIdUseCase(GenreGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public GenreOutput execute(String anIn) {
        var genre = this.gateway.findById(GenreID.from(anIn))
                .orElseThrow(notFound(GenreID.from(anIn)));

        return GenreOutput.from(genre);
    }

    private Supplier<NotFoundException> notFound(final GenreID anId) {
        return () -> NotFoundException.with(Genre.class, anId);
    }
}
