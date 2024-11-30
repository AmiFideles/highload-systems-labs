package ru.itmo.marketplace.service.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellerReviewService {
//    private final UserService userService;
//    private final ListingService listingService;
//    private final SellerReviewRepository sellerReviewRepository;

//    @Transactional(readOnly = true)
//    public Mono<Page<SellerReviewDto>> findByAuthorId(Long authorId, Pageable pageable) {
//        return sellerReviewRepository.findByAuthorId(authorId, pageable);
//    }
//
//    @Transactional(readOnly = true)
//    public Mono<Page<SellerReview>> findAll(Long sellerId, Pageable pageable) {
//        if (!userService.existsById(sellerId)) {
//            throw new NotFoundException("Seller with id %s not found".formatted(sellerId));
//        }
//        return sellerReviewRepository.findBySellerId(sellerId, pageable);
//    }
//
//    @Transactional
//    public Mono<SellerReview> create(SellerReview sellerReview) {
//        User seller = userService.findById(sellerReview.getSellerId()).orElseThrow(
//                () -> new NotFoundException("Seller not found")
//        );
//        User author = userService.findById(sellerReview.getAuthorId()).orElseThrow(
//                () -> new NotFoundException("Author not found")
//        );
//        sellerReview.setSeller(seller);
//        sellerReview.setAuthor(author);
//        if (sellerReviewRepository.existsByAuthorIdAndSellerId(
//                sellerReview.getAuthorId(), sellerReview.getSellerId()
//        )) {
//            throw new DuplicateException(
//                    "Review from user %s for seller %s already exists".formatted(
//                            sellerReview.getAuthorId(),
//                            sellerReview.getSellerId()
//                    )
//            );
//        }
//        return sellerReviewRepository.save(sellerReview);
//    }
//
//    @Transactional
//    public Mono<SellerReview> update(SellerReview sellerReview) {
//        User author = userService.findById(sellerReview.getAuthorId()).orElseThrow(
//                () -> new NotFoundException("User with id %s not found".formatted(sellerReview.getAuthorId()))
//        );
//        User seller = userService.findById(sellerReview.getSellerId()).orElseThrow(
//                () -> new NotFoundException("User with id %s not found".formatted(sellerReview.getAuthorId()))
//        );
//        if (author.getRole() != Role.BUYER) {
//            throw new AccessDeniedException("Only buyer can change review");
//        }
//        if (!sellerReviewRepository.existsByAuthorIdAndSellerId(
//                sellerReview.getAuthorId(), sellerReview.getSellerId()
//        )) {
//            return Optional.empty();
//        }
//
//        sellerReview.setAuthor(author);
//        sellerReview.setSeller(seller);
//        return Optional.of(sellerReviewRepository.save(sellerReview));
//    }
//
//    @Transactional
//    public Mono<Boolean> deleteById(Long authorId, Long sellerId) {
//        int deletedCount =  sellerReviewRepository.deleteByAuthorIdAndSellerId(authorId, sellerId);
//        return deletedCount > 0;
//    }
}
