package com.arpan.controller;

import com.arpan.model.NumberOfIssuesModel;
import com.arpan.services.IssuesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class IssuesController {

    private final IssuesService issuesService;

    @Autowired
    public IssuesController(IssuesService issuesService) {
        this.issuesService = issuesService;
    }

    @GetMapping("/")
    public String showForm() {
        return "/form";
    }

    @GetMapping("open-issues")
    public String getNumberOfOpenIssues(@RequestParam String gitRepoUrl, Model model) {
        Optional<NumberOfIssuesModel> issuesModel = issuesService.getIssuesModel(gitRepoUrl);
        if (!issuesModel.isPresent()) {
            return "/error-page";
        }
        model.addAttribute("gitIssuesModel", issuesModel.get());
        return "/results";
    }
}
