package application.controller;

import application.dto.response.PostByIdResponse;
import application.dto.response.PostsListResponse;
import application.mapper.PostMapper;
import application.service.impl.PostCommentServiceImpl;
import application.service.impl.PostServiceImpl;
import application.service.impl.TagServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/post")
public class ApiPostController {

    private final PostServiceImpl postService;
    private final PostMapper postMapper;
    private final TagServiceImpl tagService;
    private final PostCommentServiceImpl postCommentService;

    public ApiPostController(PostServiceImpl postService, PostMapper postMapper, TagServiceImpl tagService, PostCommentServiceImpl postCommentService) {
        this.postService = postService;
        this.postMapper = postMapper;
        this.tagService = tagService;
        this.postCommentService = postCommentService;
    }

    @GetMapping(value = "")
    public ResponseEntity<PostsListResponse> allPosts(
            @RequestParam int offset, @RequestParam int limit, @RequestParam String mode) {
        return new ResponseEntity<>(postMapper.convertToDto(offset, limit,
                postService.getSortedPosts(mode)), HttpStatus.OK);
    }

    //****** search only in post titles
    @GetMapping(value = "/search")
    public ResponseEntity<PostsListResponse> postsOnQuery(
            @RequestParam int offset, @RequestParam int limit, @RequestParam String query) {
        return new ResponseEntity<>(postMapper.convertToDto(offset, limit,
                postService.getQueriedPosts(query)), HttpStatus.OK);
    }

    @GetMapping(value = "/byTag")
    public ResponseEntity<PostsListResponse> postsByTag(
            @RequestParam int offset, @RequestParam int limit, @RequestParam String tag) {
        return new ResponseEntity<>(postMapper.convertToDto(offset, limit,
                postService.getPostsByTag(tag)), HttpStatus.OK);
    }

    @GetMapping(value = "/byDate")
    public ResponseEntity<PostsListResponse> postsByDate(
            @RequestParam int offset, @RequestParam int limit, @RequestParam String date) {
        return new ResponseEntity<>(postMapper.convertToDto(offset, limit,
                postService.getPostsByDate(date)), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PostByIdResponse> PostByID(@PathVariable long id){
        System.out.println(id);
        return new ResponseEntity<>(postMapper.convertToDto(postService.getPostByID(id),
                tagService.getTagsToPost(id),postCommentService.getTagsToPost(id)),
                HttpStatus.OK);
    }
}
