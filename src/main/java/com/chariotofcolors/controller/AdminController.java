package com.chariotofcolors.controller;

import com.chariotofcolors.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private com.chariotofcolors.repository.ArtPieceRepository artPieceRepository;

    @Autowired
    private com.chariotofcolors.repository.ComplaintRepository complaintRepository;

    @Autowired
    private com.chariotofcolors.repository.ReviewRepository reviewRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/dashboard";
    }

    @GetMapping("/art-pieces")
    public String artPieces(Model model) {
        model.addAttribute("artPieces", artPieceRepository.findAll());
        return "admin/art-pieces";
    }

    @GetMapping("/art-pieces/add")
    public String addArtPieceForm(Model model) {
        model.addAttribute("artPiece", new com.chariotofcolors.model.ArtPiece());
        return "admin/add-art-piece";
    }

    @SuppressWarnings("null")
    @org.springframework.web.bind.annotation.PostMapping("/art-pieces/add")
    public String addArtPiece(
            @org.springframework.web.bind.annotation.ModelAttribute com.chariotofcolors.model.ArtPiece artPiece) {
        artPieceRepository.save(artPiece);
        return "redirect:/admin/art-pieces";
    }

    @SuppressWarnings("null")
    @org.springframework.web.bind.annotation.PostMapping("/art-pieces/delete/{id}")
    public String deleteArtPiece(@org.springframework.web.bind.annotation.PathVariable Long id) {
        artPieceRepository.deleteById(id);
        return "redirect:/admin/art-pieces";
    }

    @GetMapping("/complaints")
    public String complaints(Model model) {
        model.addAttribute("complaints", complaintRepository.findAll());
        return "admin/complaints";
    }

    @GetMapping("/reviews")
    public String reviews(Model model) {
        model.addAttribute("reviews", reviewRepository.findAll());
        return "admin/reviews";
    }

    @org.springframework.web.bind.annotation.PostMapping("/reviews/approve/{id}")
    public String approveReview(@org.springframework.web.bind.annotation.PathVariable Long id) {
        @SuppressWarnings("null")
        com.chariotofcolors.model.Review review = reviewRepository.findById(id).orElse(null);
        if (review != null) {
            review.setApproved(true);
            reviewRepository.save(review);
        }
        return "redirect:/admin/reviews";
    }

    @org.springframework.web.bind.annotation.PostMapping("/reviews/toggle-top10/{id}")
    public String toggleTop10(@org.springframework.web.bind.annotation.PathVariable Long id) {
        @SuppressWarnings("null")
        com.chariotofcolors.model.Review review = reviewRepository.findById(id).orElse(null);
        if (review != null) {
            review.setTop10(!review.isTop10());
            reviewRepository.save(review);
        }
        return "redirect:/admin/reviews";
    }

    @SuppressWarnings("null")
    @org.springframework.web.bind.annotation.PostMapping("/reviews/delete/{id}")
    public String deleteReview(@org.springframework.web.bind.annotation.PathVariable Long id) {
        reviewRepository.deleteById(id);
        return "redirect:/admin/reviews";
    }
}
