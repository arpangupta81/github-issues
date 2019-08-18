package com.arpan.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NumberOfIssuesModel {
    private Long totalNumberOfOpenIssues;
    private Long issuesInLastOneDay;
    private Long issuesInLastSevenDays;
    private Long issuesSevenDaysAgo;
}
