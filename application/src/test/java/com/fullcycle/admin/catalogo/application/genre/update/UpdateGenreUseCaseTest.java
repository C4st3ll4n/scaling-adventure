package com.fullcycle.admin.catalogo.application.genre.update;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateGenreUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultUpdateGenreUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private GenreGateway genreGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway, genreGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateGenre_shouldReturnGenreId() {
        final var aGenre = Genre.newGenre("Ação", true);
        final var expectedName = "Filmes";
        final var expectedIsActive = true;
        final var expectedId = aGenre.getId();
        final var expectedCategories = List.<String>of();

        final var aCommand = UpdateGenreCommand.with(expectedId, expectedName, expectedIsActive, expectedCategories);

        when(genreGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(aGenre));

        when(genreGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(aCommand);

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());
        Assertions.assertEquals(expectedName, aGenre.getName());

        verify(genreGateway, times(1)).findById(eq(expectedId));
        verify(genreGateway, times(1)).update(argThat(genre -> genre.getName().equals(expectedName)));
    }

    @Test
    public void givenAValidCommandWithCategories_whenCallsUpdateGenre_shouldUpdateGenreAndReturnId() {
        final var aGenre = Genre.newGenre("Ação", true);
        final var expectedName = "Filmes";
        final var expectedIsActive = false;
        final var expectedId = aGenre.getId();
        final var expectedCategories = List.of(CategoryID.from("123"), CategoryID.from("456"));
        final var expectedCategoriesIds = expectedCategories.stream().map(CategoryID::getValue).toList();

        final var aCommand = UpdateGenreCommand.with(expectedId, expectedName, expectedIsActive, expectedCategoriesIds);

        when(genreGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(aGenre));

        when(categoryGateway.existsByIds(any()))
                .thenReturn(expectedCategories);

        when(genreGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(aCommand);

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        verify(genreGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, times(1)).existsByIds(eq(expectedCategories));
        verify(genreGateway, times(1)).update(argThat(genre ->
                genre.getName().equals(expectedName)
                        && genre.isActive() == expectedIsActive
        ));
    }

    @Test
    public void givenAValidCommandWithEmptyCategories_whenCallsUpdateGenre_shouldNotCallExistsByIds() {
        final var aGenre = Genre.newGenre("Ação", true);
        final var expectedName = "Filmes";
        final var expectedIsActive = true;
        final var expectedId = aGenre.getId();
        final var expectedCategories = List.<String>of();

        final var aCommand = UpdateGenreCommand.with(expectedId, expectedName, expectedIsActive, expectedCategories);

        when(genreGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(aGenre));

        when(genreGateway.update(any()))
                .thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(aCommand);

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        verify(genreGateway, times(1)).findById(eq(expectedId));
        verify(genreGateway, times(1)).update(any());
        verify(categoryGateway, times(0)).existsByIds(any());
    }

    @Test
    public void givenACommandWithNonExistingCategories_whenCallsUpdateGenre_shouldReturnNotificationException() {
        final var aGenre = Genre.newGenre("Ação", true);
        final var expectedName = "Filmes";
        final var expectedIsActive = true;
        final var expectedId = aGenre.getId();
        final var expectedCategories = List.of(CategoryID.from("123"), CategoryID.from("456"));
        final var expectedCategoriesIds = expectedCategories.stream().map(CategoryID::getValue).toList();

        final var aCommand = UpdateGenreCommand.with(expectedId, expectedName, expectedIsActive, expectedCategoriesIds);

        when(genreGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(aGenre));

        // Apenas uma categoria existe
        when(categoryGateway.existsByIds(any()))
                .thenReturn(List.of(CategoryID.from("123")));

        final var actualException =
                Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(1, actualException.getErrors().size());
        Assertions.assertEquals("Some categories could not be found: 456", actualException.getErrors().get(0).message());

        verify(genreGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, times(1)).existsByIds(any());
        verify(genreGateway, never()).update(any());
    }

    @Test
    public void givenACommandWithInvalidName_whenCallsUpdateGenre_shouldReturnNotificationException() {
        final var aGenre = Genre.newGenre("Ação", true);
        final var expectedName = "  ";
        final var expectedIsActive = true;
        final var expectedId = aGenre.getId();
        final var expectedCategories = List.<String>of();

        final var aCommand = UpdateGenreCommand.with(expectedId, expectedName, expectedIsActive, expectedCategories);

        when(genreGateway.findById(eq(expectedId)))
                .thenReturn(Optional.of(aGenre));

        final var actualException =
                Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(1, actualException.getErrors().size());
        Assertions.assertEquals("'name' should not be empty", actualException.getErrors().get(0).message());

        verify(genreGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).update(any());
    }

    @Test
    public void givenAnInvalidId_whenCallsUpdateGenre_shouldReturnNotFoundException() {
        final var aGenre = Genre.newGenre("Ação", true);
        final var expectedName = "Filmes";
        final var expectedIsActive = true;
        final var expectedId = aGenre.getId();
        final var expectedCategories = List.<String>of();

        final var aCommand = UpdateGenreCommand.with(expectedId, expectedName, expectedIsActive, expectedCategories);

        when(genreGateway.findById(eq(expectedId)))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> useCase.execute(aCommand));

        verify(genreGateway, times(1)).findById(eq(expectedId));
        verify(categoryGateway, never()).existsByIds(any());
        verify(genreGateway, never()).update(any());
    }

}
