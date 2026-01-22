package com.studia.wypozyczalnia.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.studia.wypozyczalnia.domain.base.Entity;
import com.studia.wypozyczalnia.domain.converter.StringListConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Encja tytułu dostępnego w wypożyczalni wraz z danymi katalogowymi.
 */
@jakarta.persistence.Entity
@Table(name = "title")
public class Title extends Entity {

    /**
     * Identyfikator tytułu.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nazwa tytułu.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Rok wydania.
     */
    @Column(name = "year")
    private Integer year;

    /**
     * Gatunki przypisane do tytułu.
     */
    @Convert(converter = StringListConverter.class)
    @Column(name = "genre", columnDefinition = "text")
    private List<String> genres = new ArrayList<>();

    /**
     * Opis tytułu.
     */
    @Column(columnDefinition = "text")
    private String description;

    /**
     * Identyfikator TVDB powiązany z tytułem.
     */
    @Column(name = "tvdb_id")
    private String tvdbId;

    /**
     * Ocena tytułu.
     */
    @Column(name = "rating")
    private BigDecimal rating;

    /**
     * Cena za dzień wypożyczenia.
     */
    @Column(name = "price_per_day", nullable = false)
    private BigDecimal pricePerDay;

    /**
     * Adres miniatury grafiki tytułu.
     */
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    /**
     * Adres głównej grafiki tytułu.
     */
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * Dostępne kopie DVD tego tytułu.
     */
    @OneToMany(mappedBy = "title", fetch = FetchType.LAZY)
    private List<DvdCopy> copies = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTvdbId() {
        return tvdbId;
    }

    public void setTvdbId(String tvdbId) {
        this.tvdbId = tvdbId;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<DvdCopy> getCopies() {
        return copies;
    }

    public void setCopies(List<DvdCopy> copies) {
        this.copies = copies;
    }
}
