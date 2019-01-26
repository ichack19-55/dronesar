package com.droneSAR.crud;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.droneSAR.backend.data.Category;
import com.droneSAR.backend.data.Product;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Grid of products, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
public class ProductGrid extends Grid<Product> {

    public ProductGrid() {
        setSizeFull();

        addColumn(Product::getProductName)
                .setHeader("Campaign name")
                .setFlexGrow(20)
                .setSortable(true);

        // Format and add " €" to price
        final DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(2);

        // To change the text alignment of the column, a template is used.
        final String pointsTemplate = "<div style='text-align: right'>[[item.price]]</div>";
        addColumn(TemplateRenderer.<Product>of(pointsTemplate)
                .withProperty("points", product -> decimalFormat.format(product.getPrice()) + " €"))
                .setHeader("Points")
                .setComparator(Comparator.comparing(Product::getPrice))
                .setFlexGrow(3);

        // Add an traffic light icon in front of availability
        // Three css classes with the same names of three availability values,
        // Available, Coming and Discontinued, are defined in shared-styles.css and are
        // used here in availabilityTemplate.
        final String availabilityTemplate = "<iron-icon icon=\"vaadin:circle\" class-name=\"[[item.availability]]\"></iron-icon> [[item.availability]]";
        addColumn(TemplateRenderer.<Product>of(availabilityTemplate)
                .withProperty("availability", product -> product.getAvailability().toString()))
                .setHeader("Availability")
                .setComparator(Comparator.comparing(Product::getAvailability))
                .setFlexGrow(5);

        final String dateAddedTemplate = "<div style='text-align: right'>[[item.stockCount]]</div>";
        addColumn(TemplateRenderer.<Product>of(dateAddedTemplate)
                .withProperty("dateAdded", product -> product.getStockCount() == 0 ? "-" : Integer.toString(product.getStockCount())))
                .setHeader("Date Added")
                .setComparator(Comparator.comparingInt(Product::getStockCount))
                .setFlexGrow(3);

        // To change the text alignment of the column, a template is used.
        final String activeReviewersTemplate = "<div style='text-align: right'>[[item.stockCount]]</div>";
        addColumn(TemplateRenderer.<Product>of(activeReviewersTemplate)
                .withProperty("activeReviewers", product -> product.getStockCount() == 0 ? "-" : Integer.toString(product.getStockCount())))
                .setHeader("Active Reviewers")
                .setComparator(Comparator.comparingInt(Product::getStockCount))
                .setFlexGrow(3);

        final String viewCountTemplate = "<div style='text-align: right'>[[item.stockCount]]</div>";
        addColumn(TemplateRenderer.<Product>of(viewCountTemplate)
                .withProperty("progress", product -> product.getStockCount() == 0 ? "-" : Integer.toString(product.getStockCount())))
                .setHeader("Progress")
                .setComparator(Comparator.comparingInt(Product::getStockCount))
                .setFlexGrow(3);

        // Show all categories the product is in, separated by commas
        addColumn(this::formatCategories)
                .setHeader("Category")
                .setFlexGrow(12);
    }

    public Product getSelectedRow() {
        return asSingleSelect().getValue();
    }

    public void refresh(Product product) {
        getDataCommunicator().refresh(product);
    }

    private String formatCategories(Product product) {
        if (product.getCategory() == null || product.getCategory().isEmpty()) {
            return "";
        }
        return product.getCategory().stream()
                .sorted(Comparator.comparing(Category::getId))
                .map(Category::getName).collect(Collectors.joining(", "));
    }
}
