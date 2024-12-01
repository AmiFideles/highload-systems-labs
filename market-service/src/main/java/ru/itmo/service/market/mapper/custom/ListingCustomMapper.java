package ru.itmo.service.market.mapper.custom;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itmo.common.dto.listing.ListingRequestDto;
import ru.itmo.common.dto.listing.ListingResponseDto;
import ru.itmo.common.dto.listing.ListingStatusDto;
import ru.itmo.common.dto.listing.ModeratedListingResponseDto;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.common.exception.NotFoundException;
import ru.itmo.service.market.entity.Category;
import ru.itmo.service.market.entity.Listing;
import ru.itmo.service.market.entity.ListingStatus;
import ru.itmo.service.market.mapper.mapstruct.CategoryMapper;
import ru.itmo.service.market.mapper.mapstruct.ListingMapper;
import ru.itmo.service.market.repository.CategoryRepository;
import ru.itmo.service.user.client.UserApiClient;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ListingCustomMapper {
    private final ListingMapper listingMapper;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final UserApiClient userApiClient;

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
        UserResponseDto creator = userApiClient.getUserById(listing.getCreatorId());
        dto.setCreator(creator);
        dto.setCategories(listing.getCategories().stream().map(categoryMapper::toDto).toList());
        return dto;
    }

    public ModeratedListingResponseDto toModeratedDto(Listing listing){
        ModeratedListingResponseDto moderatedDto = listingMapper.toModeratedDto(listing);
        moderatedDto.setCreatorId(listing.getCreatorId());
        return moderatedDto;
    }

    public ListingStatus fromDto(ListingStatusDto listingStatusDto){
        return listingMapper.fromDto(listingStatusDto);
    }
}
