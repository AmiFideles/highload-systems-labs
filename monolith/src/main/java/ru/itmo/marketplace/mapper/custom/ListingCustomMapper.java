package ru.itmo.marketplace.mapper.custom;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.itmo.marketplace.dto.ListingPageableResponseDto;
import ru.itmo.marketplace.dto.ListingRequestDto;
import ru.itmo.marketplace.dto.ListingResponseDto;
import ru.itmo.marketplace.dto.ListingStatusDto;
import ru.itmo.marketplace.dto.ModeratedListingResponseDto;
import ru.itmo.marketplace.entity.Category;
import ru.itmo.marketplace.entity.Listing;
import ru.itmo.marketplace.entity.ListingStatus;
import ru.itmo.marketplace.mapper.mapstruct.CategoryMapper;
import ru.itmo.marketplace.mapper.mapstruct.ListingMapper;
import ru.itmo.marketplace.repository.CategoryRepository;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ListingCustomMapper {
    private final ListingMapper listingMapper;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    public Listing fromDto(ListingRequestDto listingRequestDto) {
        Listing listing = listingMapper.fromDto(listingRequestDto);

        Set<Category> categories = listingRequestDto.getCategoryIds().stream()
                .map(categoryId -> categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new NotFoundException("Category not found with id: " + categoryId)))
                .collect(Collectors.toSet());

        listing.setCategories(categories);

        return listing;
    }

    public ListingResponseDto toDto(Listing listing) {
        ListingResponseDto dto = listingMapper.toDto(listing);
        dto.setCreatorId(listing.getCreator().getId());
        dto.setCategories(listing.getCategories().stream().map(categoryMapper::toDto).toList());
        return dto;
    }

    public ListingPageableResponseDto toDto(Page<Listing> page) {
        ListingPageableResponseDto dto = listingMapper.toDto(page);
        dto.setContent(page.getContent().stream().map(this::toDto).toList());
        return dto;
    }

    public ModeratedListingResponseDto toModeratedDto(Listing listing){
        ModeratedListingResponseDto moderatedDto = listingMapper.toModeratedDto(listing);
        moderatedDto.setCreatorId(listing.getCreator().getId());
        return moderatedDto;
    }

    public ListingStatus fromDto(ListingStatusDto listingStatusDto){
        return listingMapper.fromDto(listingStatusDto);
    }
}
