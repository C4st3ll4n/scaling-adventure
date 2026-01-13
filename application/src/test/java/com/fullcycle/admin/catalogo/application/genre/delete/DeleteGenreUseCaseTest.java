package com.fullcycle.admin.catalogo.application.genre.delete;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteGenreUseCaseTest extends UseCaseTest {

    @Mock
    private GenreGateway gateway;

    @InjectMocks
    private DefaultDeleteGenreUseCase useCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(gateway);
    }

    @Test
    public void givenAValidGenreIdWhenCallsDeleteGenreShouldDeleteGenre() {
        final var genre = Genre.newGenre("Genero", true);

        final var expectedId = genre.getId();

        Mockito.doNothing().when(gateway).deleteById(expectedId);

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        Mockito.verify(gateway, Mockito.times(1)).deleteById(expectedId);
    }

    @Test
    public void givenAInvalidId_whenCallsDeleteGenre_shouldBeOK() {
        final var expectedId = GenreID.from("123");

        doNothing()
                .when(gateway).deleteById(eq(expectedId));

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        Mockito.verify(gateway, times(1)).deleteById(eq(expectedId));
    }

    @Test
    public void givenAValidId_whenGatewayThrowsException_shouldReturnException() {
        final var genre = Genre.newGenre("Genero", true);
        final var expectedId = genre.getId();

        doThrow(new IllegalStateException("Gateway error"))
                .when(gateway).deleteById(eq(expectedId));

        Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(expectedId.getValue()));

        Mockito.verify(gateway, times(1)).deleteById(eq(expectedId));
    }

    @Test
    public void givenANullId_whenCallsDeleteGenre_shouldThrowNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> useCase.execute(null));

        Mockito.verify(gateway, never()).deleteById(any());
    }

    @Test
    public void givenAnEmptyId_whenCallsDeleteGenre_shouldBeOK() {
        final var expectedId = GenreID.from("");

        doNothing()
                .when(gateway).deleteById(eq(expectedId));

        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

        Mockito.verify(gateway, times(1)).deleteById(eq(expectedId));
    }
}
