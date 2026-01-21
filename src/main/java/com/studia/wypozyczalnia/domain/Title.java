package com.studia.wypozyczalnia.domain;

import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

import com.studia.wypozyczalnia.domain.base.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@jakarta.persistence.Entity
@Table(name = "title")
public class Title extends Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "year")
    private Integer year;

    private String genre;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "tvdb_id")
    private String tvdbId;

    @Column(name = "price_per_day", nullable = false)
    private BigDecimal pricePerDay;

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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
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

    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public List<DvdCopy> getCopies() {
        return copies;
    }

    public void setCopies(List<DvdCopy> copies) {
        this.copies = copies;
    }
}
