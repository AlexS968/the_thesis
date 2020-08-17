package application.api.response;

public class TagsResponse {

    private TagResponse[] tags;

    public TagsResponse(TagResponse[] tags) {
        this.tags = tags;
    }

    public TagResponse[] getTags() {
        return tags;
    }

    public void setTags(TagResponse[] tags) {
        this.tags = tags;
    }

    public static class TagResponse {

        private String name;
        private double weight;

        public TagResponse(String name, double weight) {
            this.name = name;
            this.weight = weight;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }
    }
}

