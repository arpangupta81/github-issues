package com.arpan.controller;

import com.arpan.model.NumberOfIssuesModel;
import com.arpan.services.IssuesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Optional;

@Controller
public class IssuesController {

    private final IssuesService issuesService;

    @Autowired
    public IssuesController(IssuesService issuesService) {
        this.issuesService = issuesService;
    }

    @GetMapping("open-issues")
    public ResponseEntity<?> getNumberOfOpenIssues(@RequestParam String gitRepoUrl) throws IOException {
        Optional<NumberOfIssuesModel> issuesModel = issuesService.getIssuesModel(gitRepoUrl);
        if (!issuesModel.isPresent()) {
            return ResponseEntity.badRequest().body("The requested Url is invalid");
        }
        return ResponseEntity.ok().body(issuesModel.get());
    }
}
