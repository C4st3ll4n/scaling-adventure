package com.fullcycle.admin.catalogo.application.genre.create;

import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateGenreUseCaseTest {

    @InjectMocks
    private DefaultCreateGenreUseCase useCase;

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private GenreGateway genreGateway;


    @Test
    public void givenAValidCommand_whenCallsCreateGenre_shouldReturnGenreId() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        List<String> categoriesIds = expectedCategories.stream().map(CategoryID::getValue).toList();

        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, categoriesIds);

        when(genreGateway.create(any())).thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(aCommand);

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        verify(genreGateway, times(1)).create(argThat(genre -> Objects.equals(genre.getName(), expectedName)));
    }

    @Test
    public void givenAValidCommandWithCategories_whenCallsCreateGenre_shouldReturnGenreId() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.from("123"), CategoryID.from("456"));
        List<String> categoriesIds = expectedCategories.stream().map(CategoryID::getValue).toList();

        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, categoriesIds);

        when(categoryGateway.existsByIds(any())).thenReturn(expectedCategories);

        when(genreGateway.create(any())).thenAnswer(returnsFirstArg());

        final var actualOutput = useCase.execute(aCommand);

        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());

        verify(categoryGateway, times(1)).existsByIds(expectedCategories);

        verify(genreGateway, times(1)).create(argThat(genre -> Objects.equals(genre.getName(), expectedName)));
    }

    @Test
    public void givenAValidEmptyCategoriesGenre_whenCallAddCategoriesEmpty_thenShouldReturnOK() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();

        var aGenre = Genre.newGenre(expectedName, expectedIsActive);

        Assertions.assertEquals(0, aGenre.getCategories().size());

        aGenre = aGenre.addCategory(expectedCategories);

        Assertions.assertEquals(0, aGenre.getCategories().size());
    }

    @Test
    public void givenAInvalidEmptyName_whenCallNewGenreAndValidate_thenShouldReceiveError() {
        final var expectedName = "  ";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";


        final var actualException =
                Assertions.assertThrows(NotificationException.class, () -> Genre.newGenre(expectedName, expectedIsActive));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAValidCommand_whenCallCreateGenreAndSomeCategoriesDoesNotExists_thenShouldReturnNotificationException() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.from("123"), CategoryID.from("456"));
        List<String> categoriesIds = expectedCategories.stream().map(CategoryID::getValue).toList();

        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, categoriesIds);
        
        when(categoryGateway.existsByIds(any())).thenReturn(List.of());

        final var actualException =
                Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(1, actualException.getErrors().size());
        Assertions.assertEquals("Some categories could not be found: 123,456", actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAInvalidNameAndEmptyCategories_whenCallsCreateGenreAndSomeCategoriesDoesNotExistes_thenShouldReturnNotificationException() {
        final var expectedName = "  ";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(CategoryID.from("123"), CategoryID.from("456"));
        List<String> categoriesIds = expectedCategories.stream().map(CategoryID::getValue).toList();

        final var aCommand = CreateGenreCommand.with(expectedName, expectedIsActive, categoriesIds);
        
        final var actualException =
                Assertions.assertThrows(NotificationException.class, () -> useCase.execute(aCommand));

        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(2, actualException.getErrors().size());
        Assertions.assertEquals("'name' should not be empty", actualException.getErrors().get(1).message());
        Assertions.assertEquals("Some categories could not be found: 123,456", actualException.getErrors().get(0).message());
    }
}
