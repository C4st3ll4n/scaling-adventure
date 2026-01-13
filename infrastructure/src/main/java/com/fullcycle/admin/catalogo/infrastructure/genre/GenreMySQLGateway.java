package com.fullcycle.admin.catalogo.infrastructure.genre;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.genre.persistence.GenreRepository;
import com.fullcycle.admin.catalogo.infrastructure.utils.SpecificationUtils;

@Service
public class GenreMySQLGateway implements GenreGateway {

    private final GenreRepository repository;

    public GenreMySQLGateway(final GenreRepository repository) {
        this.repository = repository;
    }

    @Override
    public Genre create(final Genre aGenre) {
        return save(aGenre);
    }

    @Override
    public void deleteById(final GenreID anId) {
        final String anIdValue = anId.getValue();
        if (this.repository.existsById(anIdValue)) {
            this.repository.deleteById(anIdValue);
        }
    }

    @Override
    public Optional<Genre> findById(final GenreID anId) {
        return this.repository.findById(anId.getValue())
                .map(GenreJpaEntity::toAggregate);
    }

    @Override
    public Genre update(final Genre aGenre) {
        return save(aGenre);
    }

    @Override
    public Pagination<Genre> findAll(final SearchQuery aQuery) {
        // Paginação
        final var page = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Direction.fromString(aQuery.direction()), aQuery.sort())
        );

        // Busca dinamica pelo criterio terms (name)
        final Specification<GenreJpaEntity> specifications = Optional.ofNullable(aQuery.terms())
                .filter(str -> !str.isBlank())
                .map(str -> SpecificationUtils.<GenreJpaEntity>like("name", str))
                .orElse(null);

        final var pageResult =
                this.repository.findAll(Specification.where(specifications), page);

        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(GenreJpaEntity::toAggregate).toList()
        );
    }

    private Genre save(final Genre aGenre) {
        final var entity = this.repository.findById(aGenre.getId().getValue())
                .map(existingEntity -> {
                    existingEntity.setName(aGenre.getName());
                    existingEntity.setActive(aGenre.isActive());
                    existingEntity.setCreatedAt(aGenre.getCreatedAt());
                    existingEntity.setUpdatedAt(aGenre.getUpdatedAt());
                    existingEntity.setDeletedAt(aGenre.getDeletedAt());
                    
                    // Limpar a coleção existente em vez de criar uma nova
                    // para manter a referência gerenciada pelo Hibernate
                    final var categories = existingEntity.getCategories();
                    categories.clear();
                    aGenre.getCategories().forEach(existingEntity::addCategory);
                    return existingEntity;
                })
                .orElseGet(() -> GenreJpaEntity.from(aGenre));
        
        return this.repository.save(entity).toAggregate();
    }
}
