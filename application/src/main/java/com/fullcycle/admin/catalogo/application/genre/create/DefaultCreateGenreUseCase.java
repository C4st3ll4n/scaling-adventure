package com.fullcycle.admin.catalogo.application.genre.create;

import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
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
import java.util.stream.Collectors;

import static io.vavr.API.Try;

public class DefaultCreateGenreUseCase extends CreateGenreUseCase {

    private final CategoryGateway categoryGateway;
    private final GenreGateway gateway;

    public DefaultCreateGenreUseCase(CategoryGateway categoryGateway, GenreGateway gateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.gateway = Objects.requireNonNull(gateway);
    }

    @Override
    public CreateGenreOutput execute(final CreateGenreCommand anInput) {
        final var aName = anInput.name();
        final var isActive = anInput.isActive();
        final var categories = anInput.categories();
        final var notification = Notification.create();

        notification.append(validateCategories(toCategoryID(categories)));
        final var aGenre = notification.validate(() -> Genre.newGenre(aName, isActive));
        System.out.println(aGenre);
        if (notification.hasError()) {
            System.out.printf(notification.toString());
            throw new NotificationException("", notification);
        }

        aGenre.addCategory(categories.stream().map(CategoryID::from).toList());

        return create(aGenre).get();

    }

    private Either<Notification, CreateGenreOutput> create(final Genre aGenre) {
        return Try(() -> this.gateway.create(aGenre))
                .toEither()
                .bimap(Notification::create, CreateGenreOutput::from);
    }

    private List<CategoryID> toCategoryID(final List<String> categories) {
        return categories.stream()
                .map(CategoryID::from)
                .toList();
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
