package com.arpan.services;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IssuesServiceTest {

    private static final String EMPTY_STRING = "";
    private IssuesService issueService = new IssuesService();

    @Test
    public void testGetIssuesWhenUrlIsEmpty() {
        assertThat(issueService.getIssuesModel(EMPTY_STRING)).isEmpty();
    }
}