package application.dto.response;

import java.util.Map;

public class CalendarResponse {
    private Integer[] years;
    private Map<String,Integer> posts;

    public CalendarResponse(Integer[] years, Map<String, Integer> posts) {
        this.years = years;
        this.posts = posts;
    }

    public Integer[] getYears() {
        return years;
    }

    public void setYears(Integer[] years) {
        this.years = years;
    }

    public Map<String, Integer> getPosts() {
        return posts;
    }

    public void setPosts(Map<String, Integer> posts) {
        this.posts = posts;
    }

    /*    {
        "years": [2017, 2018, 2019, 2020],
        "posts": {
                "2019-12-17": 56,
                "2019-12-14": 11,
                "2019-06-17": 1,
                "2020-03-12": 6
    }
    }*/
}
