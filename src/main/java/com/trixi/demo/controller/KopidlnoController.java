package com.trixi.demo.controller;

import com.trixi.demo.service.KopidlnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/kopidlno")
@RequiredArgsConstructor
public class KopidlnoController {

    private final KopidlnoService service;

    @GetMapping("/saveDataFromXml")
    public ResponseEntity<?> saveDataFromXml() {
        service.saveDataFromXml();
        return ResponseEntity.ok("The data has been successfully saved.");
    }
}
