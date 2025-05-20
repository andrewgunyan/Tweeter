package edu.byu.cs.tweeter.server.helpers;

import java.util.List;

public class AliasFile {
    private List<String> aliases;
    private boolean hasMorePages = false;

    public AliasFile(List<String> aliases, boolean hasMorePages) {
        this.aliases = aliases;
        this.hasMorePages = hasMorePages;
    }

    public AliasFile() {}

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }
}

