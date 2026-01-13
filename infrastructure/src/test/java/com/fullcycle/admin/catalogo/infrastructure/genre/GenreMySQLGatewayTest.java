package com.fullcycle.admin.catalogo.infrastructure.genre;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fullcycle.admin.catalogo.MySQLGatewayTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.category.CategoryMySQLGateway;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;

@MySQLGatewayTest
public class GenreMySQLGatewayTest {

    @Autowired
    private CategoryMySQLGateway categoryGateway;

    @Autowired
    private GenreMySQLGateway genreGateway;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    public void testDependenciesInjected() {
        Assertions.assertNotNull(categoryGateway);
        Assertions.assertNotNull(genreGateway);
        Assertions.assertNotNull(genreRepository);
    }

    @Test
    public void givenAValidGenre_whenCallsCreate_shouldReturnANewGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;

        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var series = categoryGateway.create(Category.newCategory("Séries", null, true));

        final var aGenre = Genre.newGenre(expectedName, expectedIsActive);
        aGenre.addCategory(filmes.getId());
        aGenre.addCategory(series.getId());

        Assertions.assertEquals(0, genreRepository.count());

        final var actualGenre = genreGateway.create(aGenre);

        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertEquals(aGenre.getId(), actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        Assertions.assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
        Assertions.assertEquals(2, actualGenre.getCategories().size());
        Assertions.assertTrue(actualGenre.getCategories().contains(filmes.getId()));
        Assertions.assertTrue(actualGenre.getCategories().contains(series.getId()));

        final var actualEntity = genreRepository.findById(aGenre.getId().getValue()).get();

        Assertions.assertEquals(aGenre.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), actualEntity.getUpdatedAt());
        Assertions.assertEquals(aGenre.getDeletedAt(), actualEntity.getDeletedAt());
        Assertions.assertNull(actualEntity.getDeletedAt());
        Assertions.assertEquals(2, actualEntity.getCategories().size());
    }

    @Test
    public void givenAValidGenreWithoutCategories_whenCallsCreate_shouldReturnANewGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;

        final var aGenre = Genre.newGenre(expectedName, expectedIsActive);

        Assertions.assertEquals(0, genreRepository.count());
        Assertions.assertEquals(0, aGenre.getCategories().size());

        final var actualGenre = genreGateway.create(aGenre);

        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertEquals(aGenre.getId(), actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        Assertions.assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
        Assertions.assertEquals(0, actualGenre.getCategories().size());

        final var actualEntity = genreRepository.findById(aGenre.getId().getValue()).get();

        Assertions.assertEquals(aGenre.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), actualEntity.getUpdatedAt());
        Assertions.assertEquals(aGenre.getDeletedAt(), actualEntity.getDeletedAt());
        Assertions.assertNull(actualEntity.getDeletedAt());
        Assertions.assertEquals(0, actualEntity.getCategories().size());
    }

    @Test
    public void givenAValidGenre_whenCallsUpdate_shouldReturnGenreUpdated() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;

        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var series = categoryGateway.create(Category.newCategory("Séries", null, true));
        final var documentarios = categoryGateway.create(Category.newCategory("Documentários", null, true));

        final var aGenre = Genre.newGenre("Action", expectedIsActive);
        aGenre.addCategory(filmes.getId());

        Assertions.assertEquals(0, genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        Assertions.assertEquals(1, genreRepository.count());

        final var actualInvalidEntity = genreRepository.findById(aGenre.getId().getValue()).get();

        Assertions.assertEquals("Action", actualInvalidEntity.getName());
        Assertions.assertEquals(1, actualInvalidEntity.getCategories().size());
        Assertions.assertEquals(expectedIsActive, actualInvalidEntity.isActive());

        final var aUpdatedGenre = Genre.with(aGenre)
                .update(expectedName, expectedIsActive, List.of(series.getId(), documentarios.getId()));

        final var actualGenre = genreGateway.update(aUpdatedGenre);

        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertEquals(aGenre.getId(), actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
        Assertions.assertEquals(2, actualGenre.getCategories().size());
        Assertions.assertTrue(actualGenre.getCategories().contains(series.getId()));
        Assertions.assertTrue(actualGenre.getCategories().contains(documentarios.getId()));

        final var actualEntity = genreRepository.findById(aGenre.getId().getValue()).get();

        Assertions.assertEquals(aGenre.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertEquals(aGenre.getDeletedAt(), actualEntity.getDeletedAt());
        Assertions.assertNull(actualEntity.getDeletedAt());
        Assertions.assertEquals(2, actualEntity.getCategories().size());
    }

    @Test
    public void givenAPrePersistedGenreWithCategories_whenCallsUpdateRemovingAllCategories_shouldReturnGenreWithoutCategories() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;

        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var series = categoryGateway.create(Category.newCategory("Séries", null, true));

        final var aGenre = Genre.newGenre("Action", expectedIsActive);
        aGenre.addCategory(filmes.getId());
        aGenre.addCategory(series.getId());

        Assertions.assertEquals(0, genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        Assertions.assertEquals(1, genreRepository.count());

        final var actualInvalidEntity = genreRepository.findById(aGenre.getId().getValue()).get();

        Assertions.assertEquals("Action", actualInvalidEntity.getName());
        Assertions.assertEquals(2, actualInvalidEntity.getCategories().size());
        Assertions.assertEquals(expectedIsActive, actualInvalidEntity.isActive());

        final var aUpdatedGenre = Genre.with(aGenre)
                .update(expectedName, expectedIsActive, List.of());

        final var actualGenre = genreGateway.update(aUpdatedGenre);

        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertEquals(aGenre.getId(), actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
        Assertions.assertEquals(0, actualGenre.getCategories().size());

        final var actualEntity = genreRepository.findById(aGenre.getId().getValue()).get();

        Assertions.assertEquals(aGenre.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertEquals(aGenre.getDeletedAt(), actualEntity.getDeletedAt());
        Assertions.assertNull(actualEntity.getDeletedAt());
        Assertions.assertEquals(0, actualEntity.getCategories().size());
    }

    @Test
    public void givenAPrePersistedGenreWithoutCategories_whenCallsUpdateAddingCategories_shouldReturnGenreWithCategories() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;

        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var series = categoryGateway.create(Category.newCategory("Séries", null, true));

        final var aGenre = Genre.newGenre("Action", expectedIsActive);

        Assertions.assertEquals(0, genreRepository.count());
        Assertions.assertEquals(0, aGenre.getCategories().size());

        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        Assertions.assertEquals(1, genreRepository.count());

        final var actualInvalidEntity = genreRepository.findById(aGenre.getId().getValue()).get();

        Assertions.assertEquals("Action", actualInvalidEntity.getName());
        Assertions.assertEquals(0, actualInvalidEntity.getCategories().size());
        Assertions.assertEquals(expectedIsActive, actualInvalidEntity.isActive());

        final var aUpdatedGenre = Genre.with(aGenre)
                .update(expectedName, expectedIsActive, List.of(filmes.getId(), series.getId()));

        final var actualGenre = genreGateway.update(aUpdatedGenre);

        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertEquals(aGenre.getId(), actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
        Assertions.assertEquals(2, actualGenre.getCategories().size());
        Assertions.assertTrue(actualGenre.getCategories().contains(filmes.getId()));
        Assertions.assertTrue(actualGenre.getCategories().contains(series.getId()));

        final var actualEntity = genreRepository.findById(aGenre.getId().getValue()).get();

        Assertions.assertEquals(aGenre.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertEquals(aGenre.getDeletedAt(), actualEntity.getDeletedAt());
        Assertions.assertNull(actualEntity.getDeletedAt());
        Assertions.assertEquals(2, actualEntity.getCategories().size());
    }

    @Test
    public void givenAPrePersistedGenreAndValidGenreId_whenTryToDeleteIt_shouldDeleteGenre() {
        final var aGenre = Genre.newGenre("Ação", true);

        Assertions.assertEquals(0, genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        Assertions.assertEquals(1, genreRepository.count());

        genreGateway.deleteById(aGenre.getId());

        Assertions.assertEquals(0, genreRepository.count());
    }

    @Test
    public void givenInvalidGenreId_whenTryToDeleteIt_shouldNotThrowException() {
        Assertions.assertEquals(0, genreRepository.count());

        genreGateway.deleteById(GenreID.from("invalid"));

        Assertions.assertEquals(0, genreRepository.count());
    }

    @Test
    public void givenAPrePersistedGenreAndValidGenreId_whenCallsFindById_shouldReturnGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;

        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var series = categoryGateway.create(Category.newCategory("Séries", null, true));

        final var aGenre = Genre.newGenre(expectedName, expectedIsActive);
        aGenre.addCategory(filmes.getId());
        aGenre.addCategory(series.getId());

        Assertions.assertEquals(0, genreRepository.count());

        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        Assertions.assertEquals(1, genreRepository.count());

        final var actualGenre = genreGateway.findById(aGenre.getId()).get();

        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertEquals(aGenre.getId(), actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        Assertions.assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
        Assertions.assertEquals(2, actualGenre.getCategories().size());
        Assertions.assertTrue(actualGenre.getCategories().contains(filmes.getId()));
        Assertions.assertTrue(actualGenre.getCategories().contains(series.getId()));
    }

    @Test
    public void givenAPrePersistedGenreWithoutCategories_whenCallsFindById_shouldReturnGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;

        final var aGenre = Genre.newGenre(expectedName, expectedIsActive);

        Assertions.assertEquals(0, genreRepository.count());
        Assertions.assertEquals(0, aGenre.getCategories().size());

        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        Assertions.assertEquals(1, genreRepository.count());

        final var actualGenre = genreGateway.findById(aGenre.getId()).get();

        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertEquals(aGenre.getId(), actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        Assertions.assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
        Assertions.assertEquals(0, actualGenre.getCategories().size());

        final var actualEntity = genreRepository.findById(aGenre.getId().getValue()).get();

        Assertions.assertEquals(aGenre.getId().getValue(), actualEntity.getId());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), actualEntity.getUpdatedAt());
        Assertions.assertEquals(aGenre.getDeletedAt(), actualEntity.getDeletedAt());
        Assertions.assertNull(actualEntity.getDeletedAt());
        Assertions.assertEquals(0, actualEntity.getCategories().size());
    }

    @Test
    public void givenValidGenreIdNotStored_whenCallsFindById_shouldReturnEmpty() {
        Assertions.assertEquals(0, genreRepository.count());

        final var actualGenre = genreGateway.findById(GenreID.from("empty"));

        Assertions.assertTrue(actualGenre.isEmpty());
    }

    @Test
    public void givenPrePersistedGenres_whenCallsFindAll_shouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var acao = Genre.newGenre("Ação", true);
        final var comedia = Genre.newGenre("Comédia", true);
        final var drama = Genre.newGenre("Drama", true);

        Assertions.assertEquals(0, genreRepository.count());

        genreRepository.saveAll(List.of(
                GenreJpaEntity.from(acao),
                GenreJpaEntity.from(comedia),
                GenreJpaEntity.from(drama)
        ));

        Assertions.assertEquals(3, genreRepository.count());

        final var query = new SearchQuery(0, 1, "", "name", "asc");
        final var actualResult = genreGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(acao.getId(), actualResult.items().get(0).getId());
    }

    @Test
    public void givenEmptyGenresTable_whenCallsFindAll_shouldReturnEmptyPage() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 0;

        Assertions.assertEquals(0, genreRepository.count());

        final var query = new SearchQuery(0, 1, "", "name", "asc");
        final var actualResult = genreGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(0, actualResult.items().size());
    }

    @Test
    public void givenFollowPagination_whenCallsFindAllWithPage1_shouldReturnPaginated() {
        var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;

        final var acao = Genre.newGenre("Ação", true);
        final var comedia = Genre.newGenre("Comédia", true);
        final var drama = Genre.newGenre("Drama", true);

        Assertions.assertEquals(0, genreRepository.count());

        genreRepository.saveAll(List.of(
                GenreJpaEntity.from(acao),
                GenreJpaEntity.from(comedia),
                GenreJpaEntity.from(drama)
        ));

        Assertions.assertEquals(3, genreRepository.count());

        var query = new SearchQuery(0, 1, "", "name", "asc");
        var actualResult = genreGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(acao.getId(), actualResult.items().get(0).getId());

        // Page 1
        expectedPage = 1;

        query = new SearchQuery(1, 1, "", "name", "asc");
        actualResult = genreGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(comedia.getId(), actualResult.items().get(0).getId());

        // Page 2
        expectedPage = 2;

        query = new SearchQuery(2, 1, "", "name", "asc");
        actualResult = genreGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(drama.getId(), actualResult.items().get(0).getId());
    }

    @Test
    public void givenPrePersistedGenresAndTermAsTerms_whenCallsFindAllAndTermsMatchsGenreName_shouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;

        final var acao = Genre.newGenre("Ação", true);
        final var comedia = Genre.newGenre("Comédia", true);
        final var drama = Genre.newGenre("Drama", true);

        Assertions.assertEquals(0, genreRepository.count());

        genreRepository.saveAll(List.of(
                GenreJpaEntity.from(acao),
                GenreJpaEntity.from(comedia),
                GenreJpaEntity.from(drama)
        ));

        Assertions.assertEquals(3, genreRepository.count());

        final var query = new SearchQuery(0, 1, "com", "name", "asc");
        final var actualResult = genreGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(comedia.getId(), actualResult.items().get(0).getId());
    }

    @Test
    public void givenPrePersistedGenresWithAndWithoutCategories_whenCallsFindAll_shouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 3;
        final var expectedTotal = 3;

        final var filmes = categoryGateway.create(Category.newCategory("Filmes", null, true));
        final var series = categoryGateway.create(Category.newCategory("Séries", null, true));

        final var acao = Genre.newGenre("Ação", true);
        acao.addCategory(filmes.getId());
        acao.addCategory(series.getId());

        final var comedia = Genre.newGenre("Comédia", true);
        // Comédia sem categorias

        final var drama = Genre.newGenre("Drama", true);
        drama.addCategory(filmes.getId());

        Assertions.assertEquals(0, genreRepository.count());

        genreRepository.saveAll(List.of(
                GenreJpaEntity.from(acao),
                GenreJpaEntity.from(comedia),
                GenreJpaEntity.from(drama)
        ));

        Assertions.assertEquals(3, genreRepository.count());

        final var query = new SearchQuery(0, 3, "", "name", "asc");
        final var actualResult = genreGateway.findAll(query);

        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());

        // Validar gênero com categorias (Ação)
        final var acaoResult = actualResult.items().stream()
                .filter(g -> g.getId().equals(acao.getId()))
                .findFirst()
                .get();
        Assertions.assertEquals(2, acaoResult.getCategories().size());
        Assertions.assertTrue(acaoResult.getCategories().contains(filmes.getId()));
        Assertions.assertTrue(acaoResult.getCategories().contains(series.getId()));

        // Validar gênero sem categorias (Comédia)
        final var comediaResult = actualResult.items().stream()
                .filter(g -> g.getId().equals(comedia.getId()))
                .findFirst()
                .get();
        Assertions.assertEquals(0, comediaResult.getCategories().size());

        // Validar gênero com categorias (Drama)
        final var dramaResult = actualResult.items().stream()
                .filter(g -> g.getId().equals(drama.getId()))
                .findFirst()
                .get();
        Assertions.assertEquals(1, dramaResult.getCategories().size());
        Assertions.assertTrue(dramaResult.getCategories().contains(filmes.getId()));
    }
}
