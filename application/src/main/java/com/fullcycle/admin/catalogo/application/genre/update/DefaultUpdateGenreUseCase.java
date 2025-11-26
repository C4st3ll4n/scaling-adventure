package com.fullcycle.admin.catalogo.application.genre.update;

import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;
import io.vavr.control.Either;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static io.vavr.API.Try;

public class DefaultUpdateGenreUseCase extends UpdateGenreUseCase {

    private final CategoryGateway categoryGateway;
    private final GenreGateway gateway;

    public DefaultUpdateGenreUseCase(CategoryGateway categoryGateway, GenreGateway gateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.gateway = Objects.requireNonNull(gateway);
    }

    @Override
    public UpdateGenreOutput execute(final UpdateGenreCommand anInput) {
        final var aName = anInput.name();
        final var isActive = anInput.isActive();
        final var categories = anInput.categories();
        final var notification = Notification.create();

        final var actualGenre = this.gateway.findById(anInput.id())
                .orElseThrow(notFound(anInput.id()));

        notification.append(validateCategories(toCategoryID(categories)));
        final var aGenre = notification.validate(() -> actualGenre.update(
                aName, isActive, categories.stream().map(CategoryID::from).toList()));

        if (notification.hasError()) {
            System.out.printf(notification.toString());
            throw new NotificationException("Could not update Genre (%s)".formatted(anInput.id()), notification);
        }

        aGenre.addCategory(categories.stream().map(CategoryID::from).toList());

        return update(aGenre).get();

    }

    private Either<Notification, UpdateGenreOutput> update(final Genre aGenre) {
        return Try(() -> this.gateway.update(aGenre))
                .toEither()
                .bimap(Notification::create, UpdateGenreOutput::from);
    }

    private List<CategoryID> toCategoryID(final List<String> categories) {
        return categories.stream()
                .map(CategoryID::from)
                .toList();
    }

    private Supplier<DomainException> notFound(final Identifier id) {
        return () -> NotFoundException.with(Genre.class, id);
    }

    private ValidationHandler validateCategories(final List<CategoryID> categories) {
        final var notification = Notification.create();

        if (Objects.isNull(categories) || categories.isEmpty()) {
            return notification;
        }

        final var retrievedCategoriesIds = categoryGateway.existsByIds(categories);
        if (retrievedCategoriesIds.size() != categories.size()) {
            final var missingIds = new ArrayList<>(categories);
            missingIds.removeAll(retrievedCategoriesIds);
            final var ids = missingIds.stream().map(CategoryID::getValue).collect(Collectors.joining(","));
            notification.append(new Error("Some categories could not be found: %s".formatted(ids)));
        }

        return notification;
    }
}
