package is.yarr.beerscanner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class BarAdminDTO {

    private Long id;
    private String name;
    private String location;
    private int currentBeerCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastCheckedAt;

    // BarAdmin-specific
    private String aiInstructions;
    private String menuUrl;
    private String menuXPath;
    private String lastMenuHash;

    @JsonProperty("approved")
    private boolean isApproved;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // BarWebpageSettings-specific
    private String menuComponentXPath;
    private String ageVerificationXPath;
    private String cleanupScript;
    private boolean processAsText;

    public BarAdminDTO() {
    }

    public BarAdminDTO(Long id, String name, String location, int currentBeerCount, LocalDateTime lastCheckedAt, String aiInstructions, String menuUrl, String menuXPath, String lastMenuHash, boolean isApproved, LocalDateTime createdAt, LocalDateTime updatedAt, String menuComponentXPath, String ageVerificationXPath, String cleanupScript, boolean processAsText) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.currentBeerCount = currentBeerCount;
        this.lastCheckedAt = lastCheckedAt;
        this.aiInstructions = aiInstructions;
        this.menuUrl = menuUrl;
        this.menuXPath = menuXPath;
        this.lastMenuHash = lastMenuHash;
        this.isApproved = isApproved;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.menuComponentXPath = menuComponentXPath;
        this.ageVerificationXPath = ageVerificationXPath;
        this.cleanupScript = cleanupScript;
        this.processAsText = processAsText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCurrentBeerCount() {
        return currentBeerCount;
    }

    public void setCurrentBeerCount(int currentBeerCount) {
        this.currentBeerCount = currentBeerCount;
    }

    public LocalDateTime getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(LocalDateTime lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }

    public String getAiInstructions() {
        return aiInstructions;
    }

    public void setAiInstructions(String aiInstructions) {
        this.aiInstructions = aiInstructions;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public String getMenuXPath() {
        return menuXPath;
    }

    public void setMenuXPath(String menuXPath) {
        this.menuXPath = menuXPath;
    }

    public String getLastMenuHash() {
        return lastMenuHash;
    }

    public void setLastMenuHash(String lastMenuHash) {
        this.lastMenuHash = lastMenuHash;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getMenuComponentXPath() {
        return menuComponentXPath;
    }

    public void setMenuComponentXPath(String menuComponentXPath) {
        this.menuComponentXPath = menuComponentXPath;
    }

    public String getAgeVerificationXPath() {
        return ageVerificationXPath;
    }

    public void setAgeVerificationXPath(String ageVerificationXPath) {
        this.ageVerificationXPath = ageVerificationXPath;
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

    public static BarAdminDTOBuilder builder() {
        return new BarAdminDTOBuilder();
    }

    @Override
    public String toString() {
        return "BarAdminDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", currentBeerCount=" + currentBeerCount +
                ", lastCheckedAt=" + lastCheckedAt +
                ", aiInstructions='" + aiInstructions + '\'' +
                ", menuUrl='" + menuUrl + '\'' +
                ", menuXPath='" + menuXPath + '\'' +
                ", lastMenuHash='" + lastMenuHash + '\'' +
                ", isApproved=" + isApproved +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", menuComponentXPath='" + menuComponentXPath + '\'' +
                ", ageVerificationXPath='" + ageVerificationXPath + '\'' +
                ", cleanupScript='" + cleanupScript + '\'' +
                ", processAsText=" + processAsText +
                '}';
    }

    public static class BarAdminDTOBuilder {
        private Long id;
        private String name;
        private String location;
        private int currentBeerCount;
        private LocalDateTime lastCheckedAt;
        private String aiInstructions;
        private String menuUrl;
        private String menuXPath;
        private String lastMenuHash;
        private boolean isApproved;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String menuComponentXPath;
        private String ageVerificationXPath;
        private String cleanupScript;
        private boolean processAsText;

        public BarAdminDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BarAdminDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BarAdminDTOBuilder location(String location) {
            this.location = location;
            return this;
        }

        public BarAdminDTOBuilder currentBeerCount(int currentBeerCount) {
            this.currentBeerCount = currentBeerCount;
            return this;
        }

        public BarAdminDTOBuilder lastCheckedAt(LocalDateTime lastCheckedAt) {
            this.lastCheckedAt = lastCheckedAt;
            return this;
        }

        public BarAdminDTOBuilder aiInstructions(String aiInstructions) {
            this.aiInstructions = aiInstructions;
            return this;
        }

        public BarAdminDTOBuilder menuUrl(String menuUrl) {
            this.menuUrl = menuUrl;
            return this;
        }

        public BarAdminDTOBuilder menuXPath(String menuXPath) {
            this.menuXPath = menuXPath;
            return this;
        }

        public BarAdminDTOBuilder lastMenuHash(String lastMenuHash) {
            this.lastMenuHash = lastMenuHash;
            return this;
        }

        public BarAdminDTOBuilder isApproved(boolean approved) {
            this.isApproved = approved;
            return this;
        }

        public BarAdminDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BarAdminDTOBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public BarAdminDTOBuilder menuComponentXPath(String menuComponentXPath) {
            this.menuComponentXPath = menuComponentXPath;
            return this;
        }

        public BarAdminDTOBuilder ageVerificationXPath(String ageVerificationXPath) {
            this.ageVerificationXPath = ageVerificationXPath;
            return this;
        }

        public BarAdminDTOBuilder cleanupScript(String cleanupScript) {
            this.cleanupScript = cleanupScript;
            return this;
        }

        public BarAdminDTOBuilder processAsText(boolean processAsText) {
            this.processAsText = processAsText;
            return this;
        }

        public BarAdminDTO build() {
            return new BarAdminDTO(id, name, location, currentBeerCount, lastCheckedAt, aiInstructions, menuUrl, menuXPath,
                    lastMenuHash, isApproved, createdAt, updatedAt,
                    menuComponentXPath, ageVerificationXPath, cleanupScript, processAsText);
        }
    }
}
