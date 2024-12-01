package ru.itmo.marketplace.service.review.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.itmo.common.dto.review.seller.SellerReviewCreateRequestDto;
import ru.itmo.common.dto.review.seller.SellerReviewResponseDto;
import ru.itmo.common.dto.review.seller.SellerReviewUpdateRequestDto;
import ru.itmo.common.dto.user.UserResponseDto;
import ru.itmo.marketplace.service.review.entity.SellerReviewEntity;
import ru.itmo.marketplace.service.review.mapper.SellerReviewMapper;
import ru.itmo.marketplace.service.review.service.SellerReviewDataService;
import ru.itmo.marketplace.service.review.service.SellerReviewService;
import ru.itmo.marketplace.service.review.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellerReviewServiceImpl implements SellerReviewService {
    private final SellerReviewDataService sellerReviewDataService;
    private final SellerReviewMapper sellerReviewMapper;
    private final UserService userService;

    @Override
    @Transactional
    public Mono<SellerReviewResponseDto> create(Long authorId, SellerReviewCreateRequestDto createRequestDto) {
        // TODO: handle duplication
        Mono<UserResponseDto> sellerMono = userService.findById(createRequestDto.getSellerId());
        Mono<UserResponseDto> authorMono = userService.findById(authorId);
        SellerReviewEntity entity = sellerReviewMapper.toEntity(createRequestDto).withAuthorId(authorId);
        Mono<SellerReviewEntity> sellerReviewMono = sellerReviewDataService.create(entity);
        return mapToSellerReviewDto(sellerMono, authorMono, sellerReviewMono);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Page<SellerReviewResponseDto>> findByAuthorId(Long id, Pageable pageable) {
        return mapFluxToDto(sellerReviewDataService.findByAuthorId(id, pageable))
                .map(sellerReviews ->
                        new PageImpl<>(sellerReviews, pageable, sellerReviews.size())
                );
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Page<SellerReviewResponseDto>> findAll(Pageable pageable) {
        return mapFluxToDto(sellerReviewDataService.findAll(pageable))
                .map(sellerReviews ->
                        new PageImpl<>(sellerReviews, pageable, sellerReviews.size())
                );
    }

    @Override
    @Transactional
    public Mono<SellerReviewResponseDto> update(
            Long authorId,
            Long sellerId,
            SellerReviewUpdateRequestDto updateRequestDto
    ) {
        // TODO: handle does not exists
        Mono<UserResponseDto> sellerMono = userService.findById(sellerId);
        Mono<UserResponseDto> authorMono = userService.findById(authorId);
        SellerReviewEntity entity = sellerReviewMapper.toEntity(updateRequestDto)
                .withAuthorId(authorId)
                .withSellerId(sellerId);
        Mono<SellerReviewEntity> sellerReviewMono = sellerReviewDataService.update(entity);
        return mapToSellerReviewDto(sellerMono, authorMono, sellerReviewMono);
    }

    @Override
    @Transactional
    public Mono<Boolean> deleteByIds(Long authorId, Long sellerId) {
        return sellerReviewDataService.deleteByIds(authorId, sellerId);
    }

    private Mono<SellerReviewResponseDto> mapToSellerReviewDto(
            Mono<UserResponseDto> sellerMono,
            Mono<UserResponseDto> authorMono,
            Mono<SellerReviewEntity> sellerReviewMono
    ) {
        return Mono.zip(sellerMono, authorMono, sellerReviewMono).map(t -> {
            UserResponseDto seller = t.getT1();
            UserResponseDto author = t.getT2();
            SellerReviewEntity sellerReview = t.getT3();
            return SellerReviewResponseDto.builder()
                    .author(author)
                    .seller(seller)
                    .rating(sellerReview.getRating())
                    .comment(sellerReview.getComment())
                    .createdAt(sellerReview.getCreatedAt())
                    .build();
        });
    }

    private Mono<List<SellerReviewResponseDto>> mapFluxToDto(Flux<SellerReviewEntity> sellerReviewEntityFlux) {
        return sellerReviewEntityFlux.collectList()
                .flatMap(sellerReviews -> {
                    Stream<Long> sellerIdsStream = sellerReviews.stream().map(SellerReviewEntity::getSellerId);
                    Stream<Long> authorIdsStream = sellerReviews.stream().map(SellerReviewEntity::getAuthorId);
                    Set<Long> usersIds = Stream.concat(sellerIdsStream, authorIdsStream).collect(Collectors.toSet());
                    return userService.findByIds(usersIds)
                            .collectMap(UserResponseDto::getId)
                            .map(users ->
                                    sellerReviews.stream()
                                            .map(sellerReview -> SellerReviewResponseDto.builder()
                                                    .seller(users.get(sellerReview.getSellerId()))
                                                    .author(users.get(sellerReview.getAuthorId()))
                                                    .rating(sellerReview.getRating())
                                                    .comment(sellerReview.getComment())
                                                    .createdAt(sellerReview.getCreatedAt())
                                                    .build())
                                            .toList()
                            );
                });
    }
}
