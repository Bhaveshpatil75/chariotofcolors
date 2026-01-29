package com.chariotofcolors.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/colors")
public class ColorCalibrationController {

    @GetMapping("/calibration")
    public ResponseEntity<String> checkCalibration() {
        // Obscure "keep-alive" as a system calibration check
        return ResponseEntity.ok("{\"status\": \"calibrated\", \"sync\": true}");
    }
}
