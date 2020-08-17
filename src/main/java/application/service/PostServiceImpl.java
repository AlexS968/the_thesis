package application.service;

import application.api.request.PostRequest;
import application.exception.ApiValidationException;
import application.exception.BadRequestException;
import application.exception.apierror.ApiValidationError;
import application.model.ModerationStatus;
import application.model.Post;
import application.model.TagToPost;
import application.model.User;
import application.repository.PostRepository;
import application.service.interfaces.PostService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final TagServiceImpl tagService;

    public PostServiceImpl(PostRepository postRepository, TagServiceImpl tagService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
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
        return postRepository.findAllByQuery(true, "ACCEPTED",
                Timestamp.valueOf(LocalDateTime.now()), "%" + query + "%");
    }

    @Override
    public List<Post> getPostsByTag(String tag) {
        return postRepository.findAllPostsByTag(true, "ACCEPTED",
                Timestamp.valueOf(LocalDateTime.now()), tag);
    }

    @Override
    public List<Post> getPostsByDate(String date) {
        return postRepository.findAllPostsByDate(true, "ACCEPTED",
                Timestamp.valueOf(LocalDateTime.now()), date);
    }

    @Override
    public List<Post> getMyPosts(User user, String status) {
        boolean isActive;
        String moderationStatus;
        switch (status) {
            case "inactive":
                isActive = false;
                moderationStatus = "";
                break;
            case "pending":
                isActive = true;
                moderationStatus = "NEW";
                break;
            case "declined":
                isActive = true;
                moderationStatus = "DECLINED";
                break;
            default: //"published"
                isActive = true;
                moderationStatus = "ACCEPTED";
                break;
        }
        return postRepository
                .findAllByIsActiveAndModerationStatusAndUserIdAndOrderByTimeDes(
                        isActive, moderationStatus, user.getId());
    }

    @Override
    public List<Post> getPostsForModeration(User moderator, String status) {
        List<Post> result;
        if (status.equals("new")) {
            result = postRepository.findAllByIsActiveAndModerationStatusNew(true);
        } else {
            String moderationStatus = status.equals("declined") ? "DECLINED" : "ACCEPTED";
            result = postRepository
                    .findAllByIsActiveAndModeratorIdAndModerationStatusAndOrderByTimeDes(
                            true, moderator.getId(), moderationStatus);
        }
        return result;
    }

    public int getModerationCounter(User moderator) {
        int result;
        if (moderator == null) {
            result = postRepository.findAllByIsActiveAndModerationStatusNew(true).size();
        } else {
            result = postRepository
                    .findAllByIsActiveAndModerationStatusNew(true).size()
                    + postRepository
                    .findAllByIsActiveAndModeratorIdAndModerationStatusAndOrderByTimeDes(
                            true, moderator.getId(), "DECLINED").size()
                    + postRepository
                    .findAllByIsActiveAndModeratorIdAndModerationStatusAndOrderByTimeDes(
                            true, moderator.getId(), "ACCEPTED").size();
        }
        return result;
    }

    @Override
    public void moderatePost(long postId, User moderator, String decision) {
        if (!moderator.isModerator()) {
            throw new BadRequestException("User is not moderator!");
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(EntityNotFoundException::new);
        post.setModerator(moderator);
        post.setModerationStatus(decision.equals("accept") ? ModerationStatus.ACCEPTED : ModerationStatus.DECLINED);
        postRepository.save(post);
    }

    @Override
    public Post getPostByID(long id) {
        return postRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public Post updatePost(long postId, PostRequest request, User user) {
        Post post = postRepository.findById(postId).orElseThrow(EntityNotFoundException::new);
        if (!post.getUser().equals(user)) {
            throw new BadRequestException("You don't have the right to change the post");
        }
        if (request.getTitle().length() >= 3) {
            post.setTitle(request.getTitle());
        } else {
            throw new ApiValidationException(new ApiValidationError(
                    "Заголовок не установлен", null), "");
        }
        if (request.getText().length() >= 50) {
            post.setText(request.getText());
        } else {
            throw new ApiValidationException(new ApiValidationError(
                    null, "Текст публикации слишком короткий"), "");
        }
        if (request.getTags() != null) {
            tagService.deleteAllTagsToPost(post);
            Set<TagToPost> tagToPosts = new HashSet<>();
            for (String tag : request.getTags()) {
                tagToPosts.add(tagService.getOrSaveTag(tag, post));
            }
            post.setTagToPosts(tagToPosts);
        }
        post.setActive(request.getActive() == 1);
        //checking post time
        LocalDateTime postTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(request.getTimestamp()), ZoneId.systemDefault());
        postTime = postTime.isBefore(LocalDateTime.now()) ? LocalDateTime.now() : postTime;
        post.setTime(postTime);
        return postRepository.save(post);
    }

    @Override
    public void deletePost(long id) {
        postRepository.deleteById(id);
    }
}
