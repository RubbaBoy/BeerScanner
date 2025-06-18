package is.yarr.beerscanner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BarWebpageSettings {

    // The XPath to the menu on the bar's webpage
    @Column(name = "menu_component_xpath")
    private String menuComponentXPath;

    // The XPath to the age verification button on the bar's webpage (if present)
    @Column(name = "age_verification_xpath")
    private String ageVerificationXPath;

    // JS that is run to clean up the webpage before scraping
    @Column(name = "cleanup_script")
    private String cleanupScript;

    @Column(name = "process_as_text", nullable = false)
    private boolean processAsText = true;

    public BarWebpageSettings() {
    }

    public BarWebpageSettings(String menuComponentXPath, String ageVerificationXPath, String cleanupScript, boolean processAsText) {
        this.menuComponentXPath = menuComponentXPath;
        this.ageVerificationXPath = ageVerificationXPath;
        this.cleanupScript = cleanupScript;
        this.processAsText = processAsText;
    }

    public String getMenuComponentXPath() {
        return menuComponentXPath;
    }

    public void setMenuComponentXPath(String menuXPath) {
        this.menuComponentXPath = menuXPath;
    }

    public String getAgeVerificationXPath() {
        return ageVerificationXPath;
    }

    public void setAgeVerificationXPath(String ageVarificationXPath) {
        this.ageVerificationXPath = ageVarificationXPath;
    }

    public String getCleanupScript() {
        return cleanupScript;
    }

    public void setCleanupScript(String cleanupScript) {
        this.cleanupScript = cleanupScript;
    }

    public boolean isProcessAsText() {
        return processAsText;
    }

    public void setProcessAsText(boolean processAsText) {
        this.processAsText = processAsText;
    }

    public static BarWebpageSettingsBuilder builder() {
        return new BarWebpageSettingsBuilder();
    }

    @Override
    public String toString() {
        return "BarWebpageSettings{" +
                "menuComponentXPath='" + menuComponentXPath + '\'' +
                ", ageVerificationXPath='" + ageVerificationXPath + '\'' +
                ", cleanupScript='" + cleanupScript + '\'' +
                '}';
    }

    public static class BarWebpageSettingsBuilder {
        private String menuComponentXPath;
        private String ageVarificationXPath;
        private String cleanupScript;
        private boolean processAsText;

        public BarWebpageSettingsBuilder menuXPath(String menuXPath) {
            this.menuComponentXPath = menuXPath;
            return this;
        }

        public BarWebpageSettingsBuilder ageVerificationXPath(String ageVarificationXPath) {
            this.ageVarificationXPath = ageVarificationXPath;
            return this;
        }

        public BarWebpageSettingsBuilder cleanupScript(String cleanupScript) {
            this.cleanupScript = cleanupScript;
            return this;
        }

        public BarWebpageSettingsBuilder processAsText(boolean processAsText) {
            this.processAsText = processAsText;
            return this;
        }

        public BarWebpageSettings build() {
            return new BarWebpageSettings(menuComponentXPath, ageVarificationXPath, cleanupScript, processAsText);
        }
    }
}
