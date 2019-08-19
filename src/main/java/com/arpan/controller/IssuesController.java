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

    /**
     * Method to get to Home Page.
     *
     * @return Returns the path to home page.
     */
    @GetMapping("/")
    public String showForm() {
        return "/form";
    }

    /**
     * Returns the open issues stats.
     *
     * @param gitRepoUrl Git RepositoryUrl.
     * @param model      Model for MVC.
     * @return forwards to the results page.
     */
    @GetMapping("open-issues")
    public String getNumberOfOpenIssues(@RequestParam String gitRepoUrl, Model model) {
        Optional<NumberOfIssuesModel> issuesModel = issuesService.getIssuesModel(gitRepoUrl);
        model.addAttribute("gitUrl", gitRepoUrl);
        if (!issuesModel.isPresent()) {
            return "/error";
        }
        model.addAttribute("gitIssuesModel", issuesModel.get());
        return "/results";
    }
}
