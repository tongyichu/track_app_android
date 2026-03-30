package com.example.outdoortrack.data.api

import com.example.outdoortrack.data.model.TrackDetailResponse
import com.example.outdoortrack.data.model.TrackListItem
import com.example.outdoortrack.data.model.TrackMapResponse
import com.example.outdoortrack.data.model.TrackSummaryResponse
import com.example.outdoortrack.data.model.TrackUploadRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 轨迹相关服务端 API 定义，对应需求文档中的 15 条接口。
 */
interface TrackApiService {

    /** 创建轨迹记录：POST /api/track/create */
    @POST("/api/track/create")
    suspend fun createTrack(): TrackDetailResponse

    /** 轨迹推荐列表：GET /api/track/recommend/list */
    @GET("/api/track/recommend/list")
    suspend fun getRecommendList(): List<TrackListItem>

    /** 轨迹搜索列表：GET /api/track/search/list */
    @GET("/api/track/search/list")
    suspend fun searchTracks(@Query("keyword") keyword: String?): List<TrackListItem>

    /** 轨迹上传云端：POST /api/track/{track_id}/upload_cloud */
    @POST("/api/track/{track_id}/upload_cloud")
    suspend fun uploadTrack(
        @Path("track_id") trackId: String,
        @Body payload: TrackUploadRequest
    )

    /** 是否存在正在进行中的轨迹：GET /api/track/running?user_id=xxx */
    @GET("/api/track/running")
    suspend fun getRunningTrack(@Query("user_id") userId: String): TrackDetailResponse?

    /** 轨迹地图：GET /api/track/{track_id}/map */
    @GET("/api/track/{track_id}/map")
    suspend fun getTrackMap(@Path("track_id") trackId: String): TrackMapResponse

    /** 轨迹扼要信息：GET /api/track/{track_id}/summary */
    @GET("/api/track/{track_id}/summary")
    suspend fun getTrackSummary(@Path("track_id") trackId: String): TrackSummaryResponse

    /** 是否已收藏轨迹：GET /api/user/{user_id}/collect?track_id=xx */
    @GET("/api/user/{user_id}/collect")
    suspend fun isCollected(
        @Path("user_id") userId: String,
        @Query("track_id") trackId: String
    ): Boolean

    /** 收藏轨迹：POST /api/track_collect?track_id=xx&user_id=xxx */
    @POST("/api/track_collect")
    suspend fun collectTrack(
        @Query("track_id") trackId: String,
        @Query("user_id") userId: String
    )

    /** 取消收藏轨迹：DELETE /api/track_collect?track_id=xx&user_id=xxx */
    @DELETE("/api/track_collect")
    suspend fun unCollectTrack(
        @Query("track_id") trackId: String,
        @Query("user_id") userId: String
    )
}
