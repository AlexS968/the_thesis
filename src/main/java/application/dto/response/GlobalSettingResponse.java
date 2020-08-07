package application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GlobalSettingResponse {

    private boolean multiuserMode;
    private boolean postPremoderation;
    private boolean statisticsIsPublic;

    public boolean isMultiuserMode() {
        return multiuserMode;
    }

    @JsonProperty("MULTIUSER_MODE")
    public void setMultiuserMode(boolean multiuserMode) {
        this.multiuserMode = multiuserMode;
    }

    public boolean isPostPremoderation() {
        return postPremoderation;
    }

    @JsonProperty("POST_PREMODERATION")
    public void setPostPremoderation(boolean postPremoderation) {
        this.postPremoderation = postPremoderation;
    }

    public boolean isStatisticsIsPublic() {
        return statisticsIsPublic;
    }

    @JsonProperty("STATISTICS_IS_PUBLIC")
    public void setStatisticsIsPublic(boolean statisticsIsPublic) {
        this.statisticsIsPublic = statisticsIsPublic;
    }

}
