package com.chariotofcolors.controller;

import com.chariotofcolors.model.ArtOrder;
import com.chariotofcolors.repository.OrderRepository;
import com.chariotofcolors.model.ArtPiece;
import com.chariotofcolors.repository.ArtPieceRepository;
import com.chariotofcolors.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ArtPieceRepository artPieceRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {
        request.getSession(true); // Force session creation to avoid response committed error
        // Only show reviews that are approved AND top 10
        model.addAttribute("reviews", reviewRepository.findByApprovedTrueAndTop10True());
        model.addAttribute("artPieces", artPieceRepository.findByType("GALLERY"));

        java.util.List<ArtPiece> aboutUsPieces = artPieceRepository.findByType("ABOUT_US");
        if (!aboutUsPieces.isEmpty()) {
            model.addAttribute("aboutUsImage", aboutUsPieces.get(0));
        } else {
            // Fallback object with default URL if not found (though DataInitializer ensures
            // it)
            ArtPiece fallback = new ArtPiece();
            fallback.setImageUrl("https://picsum.photos/600/400?random=10");
            model.addAttribute("aboutUsImage", fallback);
        }

        return "index";
    }

    @GetMapping("/art/{id}")
    public String artDetails(@PathVariable Long id, Model model) {
        @SuppressWarnings("null")
        ArtPiece art = artPieceRepository.findById(id).orElse(null);
        model.addAttribute("art", art);
        return "art-details";
    }

    @GetMapping("/order")
    public String order(@org.springframework.web.bind.annotation.RequestParam(required = false) String artType,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String refTitle,
            Model model) {
        ArtOrder order = new ArtOrder();
        if (artType != null) {
            order.setArtType(artType);
        }
        if (refTitle != null) {
            order.setDescription(
                    "Reference Art: " + refTitle + "\n\n(Please describe your customization requirements...)");
        }
        model.addAttribute("artOrder", order);
        return "order";
    }

    @GetMapping("/category/{category}")
    public String categoryGallery(@PathVariable String category, Model model) {
        model.addAttribute("category", category);
        model.addAttribute("artPieces", artPieceRepository.findByTypeAndCategory("GALLERY", category));
        return "category-gallery";
    }

    @SuppressWarnings("null")
    @PostMapping("/submit-order")
    public String submitOrder(@ModelAttribute ArtOrder artOrder, Model model) {
        orderRepository.save(artOrder);
        model.addAttribute("success", true);
        model.addAttribute("artOrder", new ArtOrder()); // Reset form
        return "order";
    }

    @Autowired
    private com.chariotofcolors.repository.ComplaintRepository complaintRepository;

    @GetMapping("/write-review")
    public String writeReview() {
        return "review-form";
    }

    @GetMapping("/complaint")
    public String complaint() {
        return "complaint-form";
    }

    @GetMapping("/more-reviews")
    public String moreReviews(Model model) {
        model.addAttribute("reviews", reviewRepository.findByApprovedTrue());
        return "more-reviews";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/submit-review")
    public String submitReview(@ModelAttribute com.chariotofcolors.model.Review review) {
        review.setDeliveryDate(java.time.LocalDate.now());
        // Newly submitted reviews are NOT approved by default.
        review.setApproved(false);
        review.setTop10(false);
        reviewRepository.save(review);
        return "redirect:/?reviewSubmitted=true"; // Redirect back to home
    }

    @SuppressWarnings("null")
    @PostMapping("/submit-complaint")
    public String submitComplaint(@ModelAttribute com.chariotofcolors.model.Complaint complaint) {
        complaintRepository.save(complaint);
        return "redirect:/?complaintSubmitted=true";
    }
}
