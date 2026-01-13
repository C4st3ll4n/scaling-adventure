package com.fullcycle.admin.catalogo.infrastructure.api.controllers;

import com.fullcycle.admin.catalogo.application.genre.create.CreateGenreCommand;
import com.fullcycle.admin.catalogo.application.genre.create.CreateGenreUseCase;
import com.fullcycle.admin.catalogo.application.genre.delete.DeleteGenreUseCase;
import com.fullcycle.admin.catalogo.application.genre.retrieve.get.GetGenreByIdUseCase;
import com.fullcycle.admin.catalogo.application.genre.retrieve.list.ListGenreUseCase;
import com.fullcycle.admin.catalogo.application.genre.update.UpdateGenreCommand;
import com.fullcycle.admin.catalogo.application.genre.update.UpdateGenreUseCase;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.api.GenreAPI;
import com.fullcycle.admin.catalogo.infrastructure.category.presenters.GenreAPIPresenter;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.GenreListResponse;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.GenreResponse;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import org.springframework.http.ResponseEntity;

import java.net.URI;

public class GenreController implements GenreAPI {

    private final CreateGenreUseCase createGenreUseCase;
    private final ListGenreUseCase listGenreUseCase;
    private final GetGenreByIdUseCase getGenreByIdUseCase;
    private final DeleteGenreUseCase deleteGenreUseCase;
    private final UpdateGenreUseCase updateGenreUseCase;

    public GenreController(CreateGenreUseCase createGenreUseCase,
                           ListGenreUseCase listGenreUseCase,
                           GetGenreByIdUseCase getGenreByIdUseCase,
                           DeleteGenreUseCase deleteGenreUseCase, UpdateGenreUseCase updateGenreUseCase) {

        this.createGenreUseCase = createGenreUseCase;
        this.listGenreUseCase = listGenreUseCase;
        this.getGenreByIdUseCase = getGenreByIdUseCase;
        this.deleteGenreUseCase = deleteGenreUseCase;
        this.updateGenreUseCase = updateGenreUseCase;
    }

    @Override
    public ResponseEntity<?> create(final CreateGenreRequest request) {
        final var command = CreateGenreCommand.with(
                request.name(), request.isActive(), request.categories()
        );

        final var output = this.createGenreUseCase.execute(command);
        return ResponseEntity.created(URI.create("/genres/" + output.id())).body(output);
    }

    @Override
    public Pagination<GenreListResponse> list(final String search,
                                              final int page,
                                              final int perPage,
                                              final String sort,
                                              final String direction
    ) {
        return listGenreUseCase.execute(new SearchQuery(
                        page, perPage, search, sort, direction))
                .map(GenreAPIPresenter::present);
    }

    @Override
    public GenreResponse getById(final String id) {
        return GenreAPIPresenter.present(getGenreByIdUseCase.execute(id));
    }

    @Override
    public ResponseEntity<?> updateById(final String id, final UpdateGenreRequest input) {
        final var command = UpdateGenreCommand.with(
                GenreID.from(input.id()), input.name(), input.isActive(), input.categories()
        );

        final var output = this.updateGenreUseCase.execute(command);
        return ResponseEntity.created(URI.create("/genres/" + output.id())).body(output);
    }

    @Override
    public void deleteById(final String id) {
        this.deleteGenreUseCase.execute(id);
    }
}
