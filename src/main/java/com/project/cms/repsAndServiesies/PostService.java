package com.project.cms.repsAndServiesies;

import com.project.cms.entities.Posts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {
    PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void save(Posts post) {
        postRepository.save(post);
    }

    public List<Posts> findAll() {
        return postRepository.findAll();
    }

    public List<Posts> findPostsByLogin(String login) {
        return postRepository.findPostsByLogin(login);
    }

    public Posts findById(int id) {
        return postRepository.findById(id).orElse(null);
    }

    public void deleteById(int id) {
        postRepository.deleteById(id);
    }

    public List<Posts> findPostsByTags(String tags) {
        List<Posts> posts = new ArrayList<>();
        if (tags.equals(""))
            return postRepository.findAll();
        String[] tagsArray = tags.split(" ");
        for (String tag : tagsArray) {
            for (Posts post : postRepository.findAll()) {
                if (post.getTags().contains(tag)) {
                    if (!posts.contains(post))
                        posts.add(post);
                }
            }
        }
        return posts;
    }
}
