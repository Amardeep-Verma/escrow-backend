package com.escrow.escrowbackend.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminReportController {

    @GetMapping("/reports")
    public Map<String, Object> getReports() {

        Map<String, Object> data = new HashMap<>();

        data.put("totalVolume", 126544);
        data.put("totalEscrows", 9);
        data.put("activeEscrows", 3);
        data.put("completedEscrows", 6);

        return data;
    }
}