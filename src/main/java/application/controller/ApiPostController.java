package application.controller;

import application.api.request.LikeRequest;
import application.api.request.PostRequest;
import application.api.response.PostByIdResponse;
import application.api.response.PostsListResponse;
import application.api.response.ResultResponse;
import application.exception.EntNotFoundException;
import application.mapper.PostMapper;
import application.model.Post;
import application.service.PostCommentServiceImpl;
import application.service.PostServiceImpl;
import application.service.PostVoteServiceImpl;
import application.service.TagServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/post")
@RequiredArgsConstructor
public class ApiPostController {

    private final PostServiceImpl postService;
    private final PostMapper postMapper;
    private final TagServiceImpl tagService;
    private final PostCommentServiceImpl postCommentService;
    private final PostVoteServiceImpl postVoteService;

    @GetMapping(value = "")
    public ResponseEntity<PostsListResponse> allPosts(
            @RequestParam int offset, @RequestParam int limit,
            @RequestParam String mode) {
        return new ResponseEntity<>(postMapper.convertToDto(offset, limit,
                postService.getSortedPosts(mode)), HttpStatus.OK);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<PostsListResponse> postsOnQuery(
            @RequestParam int offset, @RequestParam int limit,
            @RequestParam String query) {
        return new ResponseEntity<>(postMapper.convertToDto(offset, limit,
                postService.getQueriedPosts(query)), HttpStatus.OK);
    }

    @GetMapping(value = "/byTag")
    public ResponseEntity<PostsListResponse> postsByTag(
            @RequestParam int offset, @RequestParam int limit,
            @RequestParam String tag) {
        return new ResponseEntity<>(postMapper.convertToDto(offset, limit,
                postService.getPostsByTag(tag)), HttpStatus.OK);
    }

    @GetMapping(value = "/byDate")
    public ResponseEntity<PostsListResponse> postsByDate(
            @RequestParam int offset, @RequestParam int limit,
            @RequestParam String date) {
        return new ResponseEntity<>(postMapper.convertToDto(offset, limit,
                postService.getPostsByDate(date)), HttpStatus.OK);
    }

    @GetMapping(value = "/my")
    public ResponseEntity<PostsListResponse> myPosts(
            @RequestParam int offset, @RequestParam int limit,
            @RequestParam String status, HttpSession session) {
        return new ResponseEntity<>(postMapper.convertToDto(offset, limit,
                postService.getMyPosts(session, status)), HttpStatus.OK);
    }

    @GetMapping(value = "/moderation")
    public ResponseEntity<PostsListResponse> postsForModeration(
            @RequestParam int offset, @RequestParam int limit,
            @RequestParam String status, HttpSession session) {
        return new ResponseEntity<>(postMapper.convertToDto(offset, limit,
                postService.getPostsForModeration(session, status)), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PostByIdResponse> postById(
            @PathVariable long id, HttpSession session) {
        return new ResponseEntity<>(postMapper.convertToDto(postService.getPostByID(id)
                        .orElseThrow(() -> new EntNotFoundException("post id: " + id)),
                tagService.getTagsToPost(id), postCommentService.getTagsToPost(id),
                session), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ResultResponse> updatePost(
            @PathVariable long id, @Valid @RequestBody PostRequest request, HttpSession session) {
        postService.updatePost(id, request, session);
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    @PostMapping(value = "")
    public ResponseEntity<ResultResponse> newPost(
            @Valid @RequestBody PostRequest request, HttpSession session) {
        postService.savePost(postMapper.convertToEntity(request, session));
        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
    }

    @PostMapping("/like")
    public ResponseEntity<ResultResponse> like(
            @Valid @RequestBody LikeRequest request, HttpSession session) {
        return new ResponseEntity<>(new ResultResponse(postVoteService
                .like(postService.getPostByLikeRequest(request), session, true)),
                HttpStatus.OK);
    }

    @PostMapping("/dislike")
    public ResponseEntity<ResultResponse> dislike(
            @Valid @RequestBody LikeRequest request, HttpSession session) {
        return new ResponseEntity<>(new ResultResponse(postVoteService
                .like(postService.getPostByLikeRequest(request), session, false)),
                HttpStatus.OK);
    }
}
