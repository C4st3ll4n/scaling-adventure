package com.fullcycle.admin.catalogo.domain.genre;

import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.validation.handler.ThrowsValidationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class GenreTest {

    @Test
    public void givenAValidParams_whenCallNewGenre_thenInstantiateAGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = 0;

        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);

        Assertions.assertNotNull(actualGenre);
        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories().size());
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getUpdatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAnInvalidNullName_whenCallNewGenreAndValidate_thenShouldReceiveError() {
        final String expectedName = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedIsActive = true;
        final var expectedCategories = 0;

        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);

        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> actualGenre.validate(new ThrowsValidationHandler()));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidEmptyName_whenCallNewGenreAndValidate_thenShouldReceiveError() {
        final var expectedName = "  ";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";
        final var expectedIsActive = true;
        final var expectedCategories = 0;

        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);

        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> actualGenre.validate(new ThrowsValidationHandler()));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNameLengthLessThan3_whenCallNewGenreAndValidate_thenShouldReceiveError() {
        final var expectedName = "Fi ";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
        final var expectedIsActive = true;
        final var expectedCategories = 0;

        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);

        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> actualGenre.validate(new ThrowsValidationHandler()));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnInvalidNameLengthMoreThan255_whenCallNewGenreAndValidate_thenShouldReceiveError() {
        final var expectedName = """
                Gostaria de enfatizar que o consenso sobre a necessidade de qualificação auxilia a preparação e a
                composição das posturas dos órgãos dirigentes com relação às suas atribuições.
                Do mesmo modo, a estrutura atual da organização apresenta tendências no sentido de aprovar a
                manutenção das novas proposições.
                """;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
        final var expectedIsActive = true;
        final var expectedCategories = 0;

        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);

        final var actualException =
                Assertions.assertThrows(DomainException.class, () -> actualGenre.validate(new ThrowsValidationHandler()));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAActiveGenre_whenCallDeactivate_thenShouldReturnDeactivatedGenre() throws InterruptedException {
        final var aGenre = Genre.newGenre("Ação", true);

        Assertions.assertDoesNotThrow(() -> aGenre.validate(new ThrowsValidationHandler()));

        final var updatedAt = aGenre.getUpdatedAt();
        Thread.sleep(100);

        final var actualGenre = aGenre.deactivate();

        Assertions.assertFalse(actualGenre.isActive());
        Assertions.assertTrue(actualGenre.getUpdatedAt().isAfter(updatedAt));
        Assertions.assertNotNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAInactiveGenre_whenCallActivate_thenShouldReturnActivatedGenre() throws InterruptedException {
        final var aGenre = Genre.newGenre("Ação", false);

        Assertions.assertDoesNotThrow(() -> aGenre.validate(new ThrowsValidationHandler()));

        final var updatedAt = aGenre.getUpdatedAt();
        Thread.sleep(100);
        Assertions.assertFalse(aGenre.isActive());

        final var actualGenre = aGenre.activate();

        Assertions.assertTrue(actualGenre.isActive());
        Assertions.assertTrue(actualGenre.getUpdatedAt().isAfter(updatedAt));
        Assertions.assertNull(aGenre.getDeletedAt());
    }

    @Test
    public void givenAValidGenre_whenCallUpdate_thenShouldReturnUpdatedGenre() throws InterruptedException {
        final var aGenre = Genre.newGenre("Ação", true);
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = 1;

        Assertions.assertDoesNotThrow(() -> aGenre.validate(new ThrowsValidationHandler()));

        final var updatedAt = aGenre.getUpdatedAt();
        final var createdAt = aGenre.getCreatedAt();
        Thread.sleep(100);

        final var actualGenre = aGenre.update(expectedName, expectedIsActive, List.of(CategoryID.unique()));

        Assertions.assertEquals(aGenre.getId(), actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories().size());
        Assertions.assertEquals(createdAt, actualGenre.getCreatedAt());
        Assertions.assertTrue(actualGenre.getUpdatedAt().isAfter(updatedAt));
    }

    @Test
    public void givenAValidGenre_whenCallUpdateWithInvalidParams_thenShouldReturnDomainException() {
        final var aGenre = Genre.newGenre("Ação", true);
        final var expectedName = " ";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";

        Assertions.assertDoesNotThrow(() -> aGenre.validate(new ThrowsValidationHandler()));

        final var actualException =
                Assertions.assertThrows(NotificationException.class, () -> aGenre.update(expectedName, expectedIsActive, List.of(CategoryID.unique())));

        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAValidGenre_whenCallUpdateWithNullCategories_thenShouldReturnOK() {
        final var aGenre = Genre.newGenre("Ação", true);
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final List<CategoryID> expectedCategories = null;

        Assertions.assertDoesNotThrow(() -> aGenre.update(expectedName, expectedIsActive, expectedCategories));
    }

    @Test
    public void givenAValidEmptyCategoriesGenre_whenCallAddCategory_thenShouldReturnOK() {
        var aGenre = Genre.newGenre("Ação", true);
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var seriesID = CategoryID.from("123");
        final var moviesID = CategoryID.from("456");
        final List<CategoryID> expectedCategories = List.of(seriesID, moviesID);

        Assertions.assertEquals(0, aGenre.getCategories().size());

        aGenre = aGenre.addCategory(seriesID);
        aGenre = aGenre.addCategory(moviesID);

        Assertions.assertEquals(expectedName, aGenre.getName());
        Assertions.assertEquals(expectedIsActive, aGenre.isActive());
        Assertions.assertEquals(2, aGenre.getCategories().size());
    }

    @Test
    public void givenAValidGenre_whenCallRemoveCategory_thenShouldReturnOK() {
        var aGenre = Genre.newGenre("Ação", true);
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var seriesID = CategoryID.from("123");
        final var moviesID = CategoryID.from("456");
        final List<CategoryID> expectedCategories = List.of(seriesID, moviesID);
        aGenre = aGenre.update(expectedName, expectedIsActive, expectedCategories);
        Assertions.assertEquals(2, aGenre.getCategories().size());

        aGenre = aGenre.removeCategory(seriesID);

        Assertions.assertEquals(expectedName, aGenre.getName());
        Assertions.assertEquals(expectedIsActive, aGenre.isActive());
        Assertions.assertEquals(1, aGenre.getCategories().size());
    }
}
