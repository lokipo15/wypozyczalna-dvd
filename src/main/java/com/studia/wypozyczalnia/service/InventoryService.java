package com.studia.wypozyczalnia.service;

import java.util.List;

import com.studia.wypozyczalnia.domain.DvdCopy;
import com.studia.wypozyczalnia.domain.Title;
import com.studia.wypozyczalnia.domain.enums.CopyStatus;
import com.studia.wypozyczalnia.service.command.inventory.AddCopyCmd;
import com.studia.wypozyczalnia.service.command.inventory.CreateTitleCmd;
import com.studia.wypozyczalnia.service.command.inventory.CreateTitleFromTvdbCmd;
import com.studia.wypozyczalnia.service.dto.TvdbSearchItem;

public interface InventoryService {

    Title createOrUpdateTitle(CreateTitleCmd cmd);

    void deleteTitle(Long titleId);

    Title getTitle(Long id);

    List<Title> searchLocalTitles(String query);

    DvdCopy addCopy(AddCopyCmd cmd);

    void updateCopyStatus(Long copyId, CopyStatus status);

    DvdCopy getCopy(Long id);

    List<DvdCopy> findCopies(Long titleId, CopyStatus status);

    List<DvdCopy> findAvailableCopiesByTitle(Long titleId);

    List<TvdbSearchItem> searchTvdb(String query);

    Title createTitleFromTvdb(CreateTitleFromTvdbCmd cmd);
}
