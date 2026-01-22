package com.studia.wypozyczalnia.domain;

import com.studia.wypozyczalnia.domain.base.Entity;
import com.studia.wypozyczalnia.domain.enums.CopyStatus;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Kopia płyty DVD powiązana z tytułem.
 */
@jakarta.persistence.Entity
@Table(name = "dvd_copy")
public class DvdCopy extends Entity {

    /**
     * Identyfikator kopii.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Tytuł, do którego należy kopia.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "title_id", nullable = false)
    private Title title;

    /**
     * Kod inwentarzowy kopii.
     */
    @Column(name = "inventory_code", nullable = false, unique = true)
    private String inventoryCode;

    /**
     * Aktualny status dostępności kopii.
     */
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false, columnDefinition = "copy_status")
    private CopyStatus status = CopyStatus.AVAILABLE;

    /**
     * Wersja rekordu do kontroli współbieżności.
     */
    @Version
    @Column(name = "version")
    private Long version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public String getInventoryCode() {
        return inventoryCode;
    }

    public void setInventoryCode(String inventoryCode) {
        this.inventoryCode = inventoryCode;
    }

    public CopyStatus getStatus() {
        return status;
    }

    public void setStatus(CopyStatus status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
