package application.service;

import application.api.request.LikeRequest;
import application.api.request.ModerationRequest;
import application.api.request.PostRequest;
import application.exception.ApiValidationException;
import application.exception.BadRequestException;
import application.exception.EntNotFoundException;
import application.exception.UserUnauthenticatedException;
import application.exception.apierror.ApiValidationError;
import application.model.ModerationStatus;
import application.model.Post;
import application.model.TagToPost;
import application.model.User;
import application.repository.PostRepository;
import application.service.interfaces.PostService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final TagServiceImpl tagService;
    private final UserServiceImpl userService;

    public PostServiceImpl(PostRepository postRepository, TagServiceImpl tagService, UserServiceImpl userService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
        this.userService = userService;
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
    public List<Post> getMyPosts(HttpSession session, String status) {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(UserUnauthenticatedException::new);
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
            case "published":
                isActive = true;
                moderationStatus = "ACCEPTED";
                break;
            default:
                isActive = true;
                moderationStatus = "";
        }
        return postRepository
                .findAllByIsActiveAndModerationStatusAndUserIdAndOrderByTimeDes(
                        isActive, moderationStatus, user.getId());
    }

    @Override
    public List<Post> getPostsForModeration(HttpSession session, String status) {
        User moderator = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(UserUnauthenticatedException::new);
        List<Post> result;
        switch (status) {
            case "new":
                result = postRepository.findAllByIsActiveAndModerationStatusNew(true);
                break;
            case "declined":
                result = postRepository
                        .findAllByIsActiveAndModeratorIdAndModerationStatusAndOrderByTimeDes(
                                true, moderator.getId(), "DECLINED");
                break;
            case "accepted":
                result = postRepository
                        .findAllByIsActiveAndModeratorIdAndModerationStatusAndOrderByTimeDes(
                                true, moderator.getId(), "ACCEPTED");
                break;
            default:
                result = null;
        }
/*        if (status.equals("new")) {
            result = postRepository.findAllByIsActiveAndModerationStatusNew(true);
        } else {
            String moderationStatus = status.equals("declined") ? "DECLINED" : "ACCEPTED";
            result = postRepository
                    .findAllByIsActiveAndModeratorIdAndModerationStatusAndOrderByTimeDes(
                            true, moderator.getId(), moderationStatus);
        }*/
        return result;
    }

    public int getModerationCounter(HttpSession session) {
        User moderator = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(UserUnauthenticatedException::new);
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
    public boolean moderatePost(ModerationRequest request, HttpSession session) {
        boolean result = false;
        Long userId = LoginServiceImpl.getSessionsId().get(session.getId());
        if (userId != null) {
            User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                    .get(session.getId())).orElseThrow(EntNotFoundException::new);
            if (user.isModerator()) {
                Post post = postRepository.findById(request.getPost_id())
                        .orElseThrow(EntNotFoundException::new);
                post.setModerator(user);
                post.setModerationStatus(request.getDecision().equals("accept") ?
                        ModerationStatus.ACCEPTED : ModerationStatus.DECLINED);
                postRepository.save(post);
                result = true;
            }
        }
        return result;
    }

    @Override
    public Optional<Post> getPostByID(long id) {
        return postRepository.findById(id);
    }

    @Override
    public Post getPostByLikeRequest(LikeRequest request) {
        return postRepository.findById(request.getPostId()).orElseThrow(EntNotFoundException::new);
    }

    @Override
    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    public Post updatePost(long postId, PostRequest request, HttpSession session) {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(UserUnauthenticatedException::new);
        Post post = postRepository.findById(postId).orElseThrow(EntNotFoundException::new);
        boolean throwException = false;
        ApiValidationError error = new ApiValidationError();
        if (!post.getUser().equals(user)) {
            throw new BadRequestException("You don't have the right to change the post");
        }
        if (request.getTitle().length() >= 3) {
            post.setTitle(request.getTitle());
        } else {
            error.setTitle("Title is too short");
            throwException = true;
        }
        if (request.getText().length() >= 50) {
            post.setText(request.getText());
        } else {
            error.setText("Text is too short");
            throwException = true;
        }
        if (throwException) {
            throw new ApiValidationException(error, "");
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
