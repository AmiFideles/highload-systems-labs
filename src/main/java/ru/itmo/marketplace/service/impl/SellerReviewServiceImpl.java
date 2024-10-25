package ru.itmo.marketplace.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.marketplace.entity.Role;
import ru.itmo.marketplace.entity.SellerReview;
import ru.itmo.marketplace.entity.User;
import ru.itmo.marketplace.repository.SellerReviewRepository;
import ru.itmo.marketplace.service.ListingService;
import ru.itmo.marketplace.service.SellerReviewService;
import ru.itmo.marketplace.service.UserService;
import ru.itmo.marketplace.service.exceptions.AccessDeniedException;
import ru.itmo.marketplace.service.exceptions.DuplicateException;
import ru.itmo.marketplace.service.exceptions.NotFoundException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellerReviewServiceImpl implements SellerReviewService {
    private final UserService userService;
    private final ListingService listingService;
    private final SellerReviewRepository sellerReviewRepository;


    @Override
    @Transactional(readOnly = true)
    public Page<SellerReview> findByAuthorId(Long authorId, Pageable pageable) {
        return sellerReviewRepository.findByAuthorId(authorId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SellerReview> findAll(Long sellerId, Pageable pageable) {
        if (!userService.existsById(sellerId)) {
            throw new NotFoundException("Seller with id %s not found".formatted(sellerId));
        }
        return sellerReviewRepository.findBySellerId(sellerId, pageable);
    }

    @Override
    @Transactional
    public SellerReview create(SellerReview sellerReview) {
        User seller = userService.findById(sellerReview.getSellerId()).orElseThrow(
                () -> new NotFoundException("Seller not found")
        );
        User author = userService.findById(sellerReview.getAuthorId()).orElseThrow(
                () -> new NotFoundException("Author not found")
        );
        sellerReview.setSeller(seller);
        sellerReview.setAuthor(author);
        if (sellerReviewRepository.existsByAuthorIdAndSellerId(
                sellerReview.getAuthorId(), sellerReview.getSellerId()
        )) {
            throw new DuplicateException(
                    "Review from user %s for seller %s already exists".formatted(
                            sellerReview.getAuthorId(),
                            sellerReview.getSellerId()
                    )
            );
        }
        return sellerReviewRepository.save(sellerReview);
    }

    @Override
    @Transactional
    public Optional<SellerReview> update(SellerReview sellerReview) {
        User author = userService.findById(sellerReview.getAuthorId()).orElseThrow(
                () -> new NotFoundException("User with id %s not found".formatted(sellerReview.getAuthorId()))
        );
        User seller = userService.findById(sellerReview.getSellerId()).orElseThrow(
                () -> new NotFoundException("User with id %s not found".formatted(sellerReview.getAuthorId()))
        );
        if (author.getRole() != Role.BUYER) {
            throw new AccessDeniedException("Only buyer can change review");
        }
        if (!sellerReviewRepository.existsByAuthorIdAndSellerId(
                sellerReview.getAuthorId(), sellerReview.getSellerId()
        )) {
            return Optional.empty();
        }

        sellerReview.setAuthor(author);
        sellerReview.setSeller(seller);
        return Optional.of(sellerReviewRepository.save(sellerReview));
    }

    @Override
    @Transactional
    public boolean deleteById(Long authorId, Long sellerId) {
        int deletedCount =  sellerReviewRepository.deleteByAuthorIdAndSellerId(authorId, sellerId);
        return deletedCount > 0;
    }
}
