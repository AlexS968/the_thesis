package application.controller;

import application.api.request.LikeRequest;
import application.api.request.PostRequest;
import application.api.response.PostByIdResponse;
import application.api.response.PostsListResponse;
import application.api.response.ResultResponse;
import application.exception.EntityNotFoundException;
import application.mapper.PostMapper;
import application.model.Post;
import application.model.User;
import application.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/api/post")
public class ApiPostController {

    private final PostServiceImpl postService;
    private final PostMapper postMapper;
    private final TagServiceImpl tagService;
    private final PostCommentServiceImpl postCommentService;
    private final UserServiceImpl userService;
    private final PostVoteServiceImpl postVoteService;

    public ApiPostController(PostServiceImpl postService, PostMapper postMapper, TagServiceImpl tagService, PostCommentServiceImpl postCommentService, UserServiceImpl userService, PostVoteServiceImpl postVoteService) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.tagService = tagService;
        this.postCommentService = postCommentService;
        this.userService = userService;
        this.postVoteService = postVoteService;
    }

    @GetMapping(value = "")
    public ResponseEntity<PostsListResponse> allPosts(
            @RequestParam int offset, @RequestParam int limit, @RequestParam String mode) {
        List<Post> allPosts = postService.getSortedPosts(mode);
        return new ResponseEntity<>(postMapper.convertToDto(offset, limit,
                allPosts, allPosts.size()), HttpStatus.OK);
    }

    @PostMapping(value = "")
    public ResponseEntity<ResultResponse> newPost(
            @Valid @RequestBody PostRequest request, HttpSession session) {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntityNotFoundException::new);

        postService.savePost(postMapper.convertToEntity(request, user));
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<PostsListResponse> postsOnQuery(
            @RequestParam int offset, @RequestParam int limit, @RequestParam String query) {
        List<Post> postsOnQuery = postService.getQueriedPosts(query);
        return new ResponseEntity<>(postMapper.convertToDto(offset, limit,
                postService.getQueriedPosts(query), postsOnQuery.size()), HttpStatus.OK);
    }

    @GetMapping(value = "/byTag")
    public ResponseEntity<PostsListResponse> postsByTag(
            @RequestParam int offset, @RequestParam int limit, @RequestParam String tag) {
        List<Post> postsByTag = postService.getPostsByTag(tag);
        return new ResponseEntity<>(postMapper.convertToDto(offset, limit,
                postsByTag, postsByTag.size()), HttpStatus.OK);
    }

    @GetMapping(value = "/byDate")
    public ResponseEntity<PostsListResponse> postsByDate(
            @RequestParam int offset, @RequestParam int limit, @RequestParam String date) {
        List<Post> postsByDate = postService.getPostsByDate(date);
        return new ResponseEntity<>(postMapper.convertToDto(offset, limit,
                postsByDate, postsByDate.size()), HttpStatus.OK);
    }

    @GetMapping(value = "/my")
    public ResponseEntity<PostsListResponse> myPosts(
            @RequestParam int offset, @RequestParam int limit, @RequestParam String status, HttpSession session) {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntityNotFoundException::new);

        List<Post> myPosts = postService.getMyPosts(user, status);
        return new ResponseEntity<>(postMapper.convertToDto(offset, limit,
                myPosts, myPosts.size()), HttpStatus.OK);
    }

    @GetMapping(value = "/moderation")
    public ResponseEntity<PostsListResponse> postsForModeration(
            @RequestParam int offset, @RequestParam int limit,
            @RequestParam String status, HttpSession session) {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntityNotFoundException::new);

        return new ResponseEntity<>(postMapper.convertToDto(offset, limit,
                postService.getPostsForModeration(user, status),
                postService.getModerationCounter(user)), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PostByIdResponse> PostByID(@PathVariable long id) {
        return new ResponseEntity<>(postMapper.convertToDto(postService.getPostByID(id),
                tagService.getTagsToPost(id), postCommentService.getTagsToPost(id)),
                HttpStatus.OK);
    }

    @PutMapping(value = "/{id}") // дописать
    public ResponseEntity<ResultResponse> updatePost(
            @PathVariable long id, @Valid @RequestBody PostRequest request,
            HttpSession session) {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntityNotFoundException::new);

        postService.updatePost(id, request, user);
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    @PostMapping("/like")
    public ResponseEntity<ResultResponse> like(
            @Valid @RequestBody LikeRequest request, HttpSession session) {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntityNotFoundException::new);

        return new ResponseEntity<>(new ResultResponse(postVoteService
                .like(postService.getPostByID(request.getPostId()), user, true)),
                HttpStatus.OK);
    }

    @PostMapping("/dislike")
    public ResponseEntity<ResultResponse> dislike(
            @Valid @RequestBody LikeRequest request, HttpSession session) {
        User user = userService.findUserById(LoginServiceImpl.getSessionsId()
                .get(session.getId())).orElseThrow(EntityNotFoundException::new);
        return new ResponseEntity<>(new ResultResponse(postVoteService
                .like(postService.getPostByID(request.getPostId()), user, false)),
                HttpStatus.OK);
    }
}
