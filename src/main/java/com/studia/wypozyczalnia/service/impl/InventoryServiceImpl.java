package com.studia.wypozyczalnia.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.studia.wypozyczalnia.domain.DvdCopy;
import com.studia.wypozyczalnia.domain.Title;
import com.studia.wypozyczalnia.domain.enums.CopyStatus;
import com.studia.wypozyczalnia.exception.ConflictException;
import com.studia.wypozyczalnia.exception.NotFoundException;
import com.studia.wypozyczalnia.integration.tvdb.TvdbClient;
import com.studia.wypozyczalnia.repository.DvdCopyRepository;
import com.studia.wypozyczalnia.repository.TitleRepository;
import com.studia.wypozyczalnia.service.InventoryService;
import com.studia.wypozyczalnia.service.command.inventory.AddCopyCmd;
import com.studia.wypozyczalnia.service.command.inventory.CreateTitleCmd;
import com.studia.wypozyczalnia.service.command.inventory.CreateTitleFromTvdbCmd;
import com.studia.wypozyczalnia.service.dto.TvdbSearchItem;
import com.studia.wypozyczalnia.service.dto.TvdbTitleDetails;
import com.studia.wypozyczalnia.exception.ValidationException;

@Service
@Transactional(readOnly = true)
public class InventoryServiceImpl implements InventoryService {

    private final TitleRepository titleRepository;
    private final DvdCopyRepository dvdCopyRepository;
    private final TvdbClient tvdbClient;

    public InventoryServiceImpl(TitleRepository titleRepository, DvdCopyRepository dvdCopyRepository, TvdbClient tvdbClient) {
        this.titleRepository = titleRepository;
        this.dvdCopyRepository = dvdCopyRepository;
        this.tvdbClient = tvdbClient;
    }

    @Override
    @Transactional
    public Title createOrUpdateTitle(CreateTitleCmd cmd) {
        var name = cmd.name();
        if (!StringUtils.hasText(name)) {
            throw new ValidationException("Title name is required");
        }
        var title = titleRepository.findByNameIgnoreCase(name.trim()).orElseGet(Title::new);
        title.setName(name.trim());
        title.setYear(cmd.year());
        title.setGenre(cmd.genre());
        title.setDescription(cmd.description());
        title.setTvdbId(cmd.tvdbId());
        return titleRepository.save(title);
    }

    @Override
    @Transactional
    public void deleteTitle(Long titleId) {
        var title = getTitle(titleId);
        if (dvdCopyRepository.existsByTitleId(title.getId())) {
            throw new ConflictException("Cannot delete title with existing copies");
        }
        titleRepository.delete(title);
    }

    @Override
    public Title getTitle(Long id) {
        return titleRepository.findById(id).orElseThrow(() -> new NotFoundException("Title not found"));
    }

    @Override
    public List<Title> searchLocalTitles(String query) {
        if (!StringUtils.hasText(query)) {
            return titleRepository.findAll();
        }
        return titleRepository.findByNameContainingIgnoreCase(query.trim());
    }

    @Override
    @Transactional
    public DvdCopy addCopy(AddCopyCmd cmd) {
        var title = titleRepository.findById(cmd.titleId())
            .orElseThrow(() -> new NotFoundException("Title not found"));
        dvdCopyRepository.findByInventoryCode(cmd.inventoryCode())
            .ifPresent(existing -> {
                throw new ConflictException("Inventory code already exists");
            });
        var copy = new DvdCopy();
        copy.setTitle(title);
        copy.setInventoryCode(cmd.inventoryCode());
        copy.setStatus(CopyStatus.AVAILABLE);
        return dvdCopyRepository.save(copy);
    }

    @Override
    @Transactional
    public void updateCopyStatus(Long copyId, CopyStatus status) {
        var copy = getCopy(copyId);
        copy.setStatus(status);
        dvdCopyRepository.save(copy);
    }

    @Override
    public DvdCopy getCopy(Long id) {
        return dvdCopyRepository.findById(id).orElseThrow(() -> new NotFoundException("Copy not found"));
    }

    @Override
    public List<DvdCopy> findCopies(Long titleId, CopyStatus status) {
        getTitle(titleId); // ensure title exists
        if (status != null) {
            return dvdCopyRepository.findByTitleIdAndStatus(titleId, status);
        }
        return dvdCopyRepository.findByTitleId(titleId);
    }

    @Override
    public List<DvdCopy> findAvailableCopiesByTitle(Long titleId) {
        return findCopies(titleId, CopyStatus.AVAILABLE);
    }

    @Override
    public List<TvdbSearchItem> searchTvdb(String query) {
        return tvdbClient.search(query);
    }

    @Override
    @Transactional
    public Title createTitleFromTvdb(CreateTitleFromTvdbCmd cmd) {
        var details = tvdbClient.getDetails(cmd.tvdbId())
            .orElseThrow(() -> new NotFoundException("TVDB title not found"));
        return createOrUpdateTitle(fromDetails(details));
    }

    private CreateTitleCmd fromDetails(TvdbTitleDetails details) {
        return new CreateTitleCmd(details.name(), details.year(), details.genre(), details.overview(), details.tvdbId());
    }
}
