package application.controller;

import application.api.request.LikeRequest;
import application.api.request.PostRequest;
import application.api.response.PostByIdResponse;
import application.api.response.PostsListResponse;
import application.api.response.ResultResponse;
import application.service.interfaces.PostCommentService;
import application.service.interfaces.PostService;
import application.service.interfaces.PostVoteService;
import application.service.interfaces.TagService;
import application.service.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping(value = "/api/post")
@RequiredArgsConstructor
public class ApiPostController {
    private final PostCommentService postCommentService;
    private final PostService postService;
    private final PostVoteService postVoteService;
    private final TagService tagService;
    private final PostMapper postMapper;

    @GetMapping(value = "")
    public ResponseEntity<PostsListResponse> allPosts(
            @RequestParam int offset, @RequestParam int limit, @RequestParam String mode) {
        return ResponseEntity.ok(postMapper.convertToDto(offset, limit,
                postService.getSortedPosts(mode)));
    }

    @GetMapping(value = "/search")
    public ResponseEntity<PostsListResponse> postsOnQuery(
            @RequestParam int offset, @RequestParam int limit, @RequestParam String query) {
        return ResponseEntity.ok(postMapper.convertToDto(offset, limit,
                postService.getQueriedPosts(query)));
    }

    @GetMapping(value = "/byTag")
    public ResponseEntity<PostsListResponse> postsByTag(
            @RequestParam int offset, @RequestParam int limit, @RequestParam String tag) {
        return ResponseEntity.ok(postMapper.convertToDto(offset, limit,
                postService.getPostsByTag(tag)));
    }

    @GetMapping(value = "/byDate")
    public ResponseEntity<PostsListResponse> postsByDate(
            @RequestParam int offset, @RequestParam int limit, @RequestParam String date) {
        return ResponseEntity.ok(postMapper.convertToDto(offset, limit,
                postService.getPostsByDate(date)));
    }

    @GetMapping(value = "/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostsListResponse> myPosts(
            @RequestParam int offset, @RequestParam int limit,
            @RequestParam String status, Principal principal) {
        return ResponseEntity.ok(postMapper.convertToDto(offset, limit,
                postService.getMyPosts(principal, status)));
    }

    @GetMapping(value = "/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<PostsListResponse> postsForModeration(
            @RequestParam int offset, @RequestParam int limit,
            @RequestParam String status, Principal principal) {
        return ResponseEntity.ok(postMapper.convertToDto(offset, limit,
                postService.getPostsForModeration(principal.getName(), status)));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PostByIdResponse> postById(
            @PathVariable long id, Principal principal) {
        postService.increaseViewCounter(id, principal);
        return ResponseEntity.ok(postMapper.convertToDto(postService.getPostByID(id),
                tagService.getTagsToPost(id), postCommentService.getTagsToPost(id)));
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> updatePost(
            @PathVariable long id, @Valid @RequestBody PostRequest request, Principal principal) {
        return ResponseEntity.ok(postService.updatePost(id, request, principal));
    }

    @PostMapping(value = "")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> newPost(
            @Valid @RequestBody PostRequest request, Principal principal) {
        return ResponseEntity.ok(postService.createNewPost(request, principal));
    }

    @PostMapping("/like")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> like(
            @Valid @RequestBody LikeRequest request, Principal principal) {
        return ResponseEntity.ok(new ResultResponse(postVoteService
                .like(postService.getPostByLikeRequest(request), principal, true)));
    }

    @PostMapping("/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> dislike(
            @Valid @RequestBody LikeRequest request, Principal principal) {
        return ResponseEntity.ok(new ResultResponse(postVoteService.like(
                postService.getPostByLikeRequest(request), principal, false)));
    }
}
