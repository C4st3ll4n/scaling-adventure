package com.fullcycle.admin.catalogo.application.genre.create;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;

@IntegrationTest
public class CreateGenreUseCaseIT {

    @Autowired
    private GenreRepository genreRepository;

    @SpyBean
    private GenreGateway genreGateway;

    @Autowired
    private DefaultCreateGenreUseCase genreUseCase;

    @Autowired
    private CategoryGateway categoryGateway;

    @Test
    void givenAValidCommandWhenCallsCreateGenreShouldReturnGenreId() {
        final var filmes = Category.newCategory("Filmes", "", true);

        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId().toString());

        final var command = CreateGenreCommand.with(expectedName, expectedIsActive, expectedCategories);

        final var actualOutput = genreUseCase.execute(command);

        Mockito.verify(
                genreGateway, times(1)
        ).create(argThat(genre -> genre.getName().equals(expectedName)));

    }
}
