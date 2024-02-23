package com.trixi.demo.controller;

import com.trixi.demo.model.entity.Village;
import com.trixi.demo.service.KopidlnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/kopidlno")
@RequiredArgsConstructor
public class KopidlnoController {

    private final KopidlnoService service;

    @GetMapping("/downloadData")
    public ResponseEntity<?> downloadData() {
        service.downloadData();
        return ResponseEntity.ok("je to v suchu");
    }
    @PostMapping("/save")
    public ResponseEntity save(@RequestBody Village village){
        service.save(village);
        return ResponseEntity.ok("hotovo");
    }
}
