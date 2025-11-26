package com.fullcycle.admin.catalogo.application.genre.retrieve.get;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GetGenreByIdUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultGetGenreByIdUseCase useCase;

    @Mock
    private GenreGateway gateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(gateway);
    }

    @Test
    public void givenAValidId_whenCallsGetGenre_shouldReturnGenre() {
        final var expectedName = "Genero";
        final var expectedActive = true;
        final var expectedCategories = List.of(
                CategoryID.from("123"), CategoryID.from("456")
        );
        final var categorieIds = expectedCategories.stream().map(CategoryID::getValue).toList();

        final var genre = Genre.newGenre(expectedName, expectedActive).addCategory(expectedCategories);

        final var expectedId = genre.getId();

        when(gateway.findById(eq(expectedId))).thenReturn(Optional.of(genre));

        final var actualGenre = useCase.execute(expectedId.getValue());

        Assertions.assertNotNull(actualGenre);
        Assertions.assertEquals(expectedId.getValue(), actualGenre.id());
        Assertions.assertEquals(expectedName, actualGenre.name());
        Assertions.assertEquals(expectedActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories.size(), actualGenre.categories().size());
        Assertions.assertTrue(actualGenre.categories().containsAll(categorieIds));
        Assertions.assertEquals(genre.getCreatedAt(), actualGenre.createdAt());
        Assertions.assertEquals(genre.getUpdatedAt(), actualGenre.updatedAt());
        Assertions.assertEquals(genre.getDeletedAt(), actualGenre.deletedAt());
    }

    @Test
    public void givenAValidId_whenCallsGetGenreWithInactiveGenre_shouldReturnGenre() {
        final var expectedName = "Genero";

        final var genre = Genre.newGenre(expectedName, true);
        genre.deactivate();

        final var expectedId = genre.getId();

        when(gateway.findById(eq(expectedId))).thenReturn(Optional.of(genre));

        final var actualGenre = useCase.execute(expectedId.getValue());

        Assertions.assertNotNull(actualGenre);
        Assertions.assertEquals(expectedId.getValue(), actualGenre.id());
        Assertions.assertEquals(expectedName, actualGenre.name());
        Assertions.assertFalse(actualGenre.isActive());
        Assertions.assertNotNull(actualGenre.deletedAt());
        Assertions.assertEquals(genre.getCreatedAt(), actualGenre.createdAt());
        Assertions.assertEquals(genre.getUpdatedAt(), actualGenre.updatedAt());
    }

    @Test
    public void givenABalidId_whenCallsGetGenre_shouldReturnNotFound() {
        final var expectedErrorMessage = "Genre with ID 123 was not found";
        final var expectedId = GenreID.from("123");

        when(gateway.findById(eq(expectedId)))
                .thenReturn(Optional.empty());

        final var actualException = Assertions.assertThrows(
                NotFoundException.class,
                () -> useCase.execute(expectedId.getValue())
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    public void givenAValidId_whenGatewayThrowsException_shouldReturnException() {
        final var expectedErrorMessage = "Gateway error";
        final var expectedId = GenreID.from("123");

        when(gateway.findById(eq(expectedId)))
                .thenThrow(new IllegalStateException(expectedErrorMessage));

        final var actualException = Assertions.assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(expectedId.getValue())
        );

        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    public void givenANullId_whenCallsGetGenre_shouldThrowNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verify(gateway, never()).findById(any());
    }
}
