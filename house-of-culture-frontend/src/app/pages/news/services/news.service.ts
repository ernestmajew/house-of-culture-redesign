import { Injectable } from '@angular/core';
import {
  PostApiService,
  CreatePostRequestParams,
  GetPostsWithPaginationRequestParams,
  EditPostRequestParams
} from "../../../../../out/api";

@Injectable({
  providedIn: 'root'
})
export class NewsService {

  constructor(
    private service: PostApiService
  ) { }

  createNewNews(newPostParam: CreatePostRequestParams){
    return this.service.createPost(newPostParam);
  }

  getAllNews(paginationParams: GetPostsWithPaginationRequestParams){
    return this.service.getPostsWithPagination(paginationParams);
  }

  getNewsById(postId: number) {
    return this.service.getPostById({id: postId})
  }

  deleteNews(postId: number) {
    return this.service.deletePost({id: postId});
  }

  editNews(params: EditPostRequestParams) {
    return this.service.editPost(params);
  }
}
