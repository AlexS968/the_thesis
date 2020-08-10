package application.service;

import application.model.Post;
import application.repository.PostRepository;
import application.service.interfaces.PostService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> getPosts() {
        return new ArrayList<>(postRepository.findAll());
    }

    @Override
    public List<Post> getSortedPosts(String mode) {
        List<Post> posts;
        switch (mode) {
            case "recent":
                posts = postRepository
                        .findAllByIsActiveAndModerationStatusAndTimeLessThanEqualAndOrderByTimeDes
                                (true, "ACCEPTED",
                                        Timestamp.valueOf(LocalDateTime.now()));
                break;
            case "popular":
                posts = postRepository
                        .findAllByIsActiveAndModerationStatusAndTimeLessThanEqualAndOrderByCommentsNumberDes
                                (true, "ACCEPTED",
                                        Timestamp.valueOf(LocalDateTime.now()));
                break;
            case "best":
                posts = postRepository
                        .findAllByIsActiveAndModerationStatusAndTimeLessThanEqualAndOrderByLikesNumberDes
                                (true, "ACCEPTED",
                                        Timestamp.valueOf(LocalDateTime.now()));
                break;
            case "early":
                posts = postRepository
                        .findAllByIsActiveAndModerationStatusAndTimeLessThanEqualAndOrderByTimeAsc
                                (true, "ACCEPTED",
                                        Timestamp.valueOf(LocalDateTime.now()));
                break;
            default:
                posts = null;
        }
        return posts;
    }

    @Override
    public List<Post> getQueriedPosts(String query) {
        return postRepository
                .findAllByIsActiveAndModerationStatusAndTimeLessThanEqualAndTitleContainingAndOrderByTimeDes
                        (true, "ACCEPTED",
                                Timestamp.valueOf(LocalDateTime.now()), "%" + query + "%");
    }

    @Override
    public List<Post> getPostsByTag(String tag) {
        return postRepository
                .findAllPostsByTag(true, "ACCEPTED",
                        Timestamp.valueOf(LocalDateTime.now()), tag);
    }

    @Override
    public List<Post> getPostsByDate(String date) {
        return postRepository
                .findAllPostsByDate(true, "ACCEPTED",
                        Timestamp.valueOf(LocalDateTime.now()), date);
    }

    @Override
    public Post getPostByID(long id) {
        return postRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public void deletePost(long id) {
        postRepository.deleteById(id);
    }
}
