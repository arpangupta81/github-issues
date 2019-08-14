package com.arpan.util;

import com.google.common.base.Splitter;

import java.util.Optional;

public class IssuesUtil {

    private static final Splitter SPLITTER = Splitter.on("https://github.com/").omitEmptyStrings().trimResults();
    private static final String EMPTY_STRING = "";

    private IssuesUtil() {
    }

    public static String createGitApiUrl(String gitUrl, String type) {
        Optional<String> apiUrls = SPLITTER.splitToList(gitUrl).stream().findFirst();
        if (!apiUrls.isPresent()) {
            return EMPTY_STRING;
        }
        return String.format("/repos/%s%s", apiUrls.get(), type);
    }
}
