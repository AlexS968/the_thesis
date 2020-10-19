package application.service.impl;

import application.api.request.LikeRequest;
import application.api.request.ModerationRequest;
import application.api.request.PostRequest;
import application.api.response.ResultResponse;
import application.exception.ApiValidationException;
import application.exception.BadRequestException;
import application.exception.apierror.ApiValidationError;
import application.persistence.enums.ModerationStatus;
import application.persistence.model.Post;
import application.persistence.model.TagToPost;
import application.persistence.model.User;
import application.persistence.repository.IPostCount;
import application.persistence.repository.PostRepository;
import application.service.GlobalSettingService;
import application.service.PostService;
import application.service.TagService;
import application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final GlobalSettingService globalSettingService;
    private final UserService userService;
    private final TagService tagService;

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
    public List<Post> getMyPosts(Principal principal, String status) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        boolean isActive;
        String moderationStatus;
        switch (status) {
            case "inactive":
                isActive = false;
                moderationStatus = "NEW";
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
    public List<Post> getPostsForModeration(String email, String status) {
        User moderator = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
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
        return result;
    }

    @Override
    public ResultResponse moderatePost(ModerationRequest request, Principal principal) {
        boolean result = false;
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("principal.getName()"));
        if (user.isModerator()) {
            Post post = postRepository.findById(request.getPostId())
                    .orElseThrow(EntityNotFoundException::new);
            post.setModerator(user);
            post.setModerationStatus(request.getDecision().equals("accept") ?
                    ModerationStatus.ACCEPTED : ModerationStatus.DECLINED);
            postRepository.save(post);
            result = true;
        }
        return new ResultResponse(result);
    }

    @Override
    public Post getPostByID(long id) {
        return postRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public Post getPostByLikeRequest(LikeRequest request) {
        return postRepository.findById(request.getPostId())
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public ResultResponse createNewPost(PostRequest request, Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        //check post title and text
        ApiValidationError apiValidationError = new ApiValidationError();
        boolean throwException = false;
        if (request.getTitle().length() < 3) {
            apiValidationError.setTitle("Title is too short");
            throwException = true;
        }
        if (request.getText().length() < 50) {
            apiValidationError.setText("Text is too short");
            throwException = true;
        }
        if (throwException) {
            throw new ApiValidationException(apiValidationError);
        }
        //if everything is ok, create new post
        Post post = new Post();
        post.setActive(true);
        //if post premoderation is disabled publish the posts immediately
        post.setModerationStatus(globalSettingService.postPreModerationEnabled() ?
                ModerationStatus.NEW : ModerationStatus.ACCEPTED);
        post.setTitle(request.getTitle());
        post.setText(request.getText());
        post.setUser(user);
        //checking post time
        LocalDateTime postTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(request.getTimestamp() * 1000), ZoneId.systemDefault());
        postTime = postTime.isBefore(LocalDateTime.now()) ? LocalDateTime.now() : postTime;
        post.setTime(postTime);
        //setting tags to post
        Set<TagToPost> tagToPosts = new HashSet<>();
        for (String tag : request.getTags()) {
            tagToPosts.add(tagService.getOrSaveTag(tag, post));
        }
        post.setTagToPosts(tagToPosts);
        postRepository.save(post);
        return new ResultResponse(true);
    }

    @Override
    public ResultResponse updatePost(long postId, PostRequest request, Principal principal) {
        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        Post post = postRepository.findById(postId).orElseThrow(EntityNotFoundException::new);
        boolean throwException = false;
        ApiValidationError apiValidationError = new ApiValidationError();
        if (!post.getUser().equals(user)) {
            throw new BadRequestException("You don't have the right to change the post");
        }
        if (request.getTitle().length() >= 3) {
            post.setTitle(request.getTitle());
        } else {
            apiValidationError.setTitle("Title is too short");
            throwException = true;
        }
        if (request.getText().length() >= 50) {
            post.setText(request.getText());
        } else {
            apiValidationError.setText("Text is too short");
            throwException = true;
        }
        if (throwException) {
            throw new ApiValidationException(apiValidationError);
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
        postRepository.save(post);
        return new ResultResponse(true);
    }

    @Override
    public void increaseViewCounter(long id, Principal principal) {
        User user = userService.findByEmail(principal.getName()).orElse(null);
        Post post = getPostByID(id);
        //increase view counter under certain conditions
        if (user == null || (!user.isModerator() & post.getUser().getId() != user.getId())) {
            post.setViewCount(post.getViewCount() + 1);
            postRepository.save(post);
        }
    }

    @Override
    public Optional<Post> findEarliestPost() {
        return postRepository.findEarliestPost();
    }

    @Override
    public List<IPostCount> countPostsByDay(Timestamp from, Timestamp to) {
        return postRepository.countPostsByDay(from, to);
    }

    @Override
    public List<IPostCount> countPostsByTag() {
        return postRepository.countPostsByTag();
    }

    @Override
    public List<Post> findAllOrderByTimeAsc() {
        return postRepository.findAllOrderByTimeAsc();
    }

    @Override
    public List<Post> findAllByUserIdOrderByTimeAsc(long userId) {
        return postRepository.findAllByUserIdOrderByTimeAsc(userId);
    }


}
