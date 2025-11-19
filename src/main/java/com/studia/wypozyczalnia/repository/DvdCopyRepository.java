package com.studia.wypozyczalnia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.studia.wypozyczalnia.domain.DvdCopy;
import com.studia.wypozyczalnia.domain.enums.CopyStatus;

import jakarta.persistence.LockModeType;

public interface DvdCopyRepository extends JpaRepository<DvdCopy, Long> {

    Optional<DvdCopy> findByInventoryCode(String inventoryCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from DvdCopy c where c.id = :id")
    Optional<DvdCopy> findWithLockingById(@Param("id") Long id);

    List<DvdCopy> findByTitleIdAndStatus(Long titleId, CopyStatus status);

    List<DvdCopy> findByTitleId(Long titleId);

    boolean existsByTitleId(Long titleId);
}
