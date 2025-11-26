package com.fullcycle.admin.catalogo.application.genre.delete;

import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;

public class DefaultDeleteGenreUseCase extends DeleteGenreUseCase {

    private final GenreGateway gateway;

    public DefaultDeleteGenreUseCase(GenreGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void execute(String anId) {
        this.gateway.deleteById(GenreID.from(anId));
    }
}
