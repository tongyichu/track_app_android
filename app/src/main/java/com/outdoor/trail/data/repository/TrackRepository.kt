package com.outdoor.trail.data.repository

import com.outdoor.trail.data.model.*
import com.outdoor.trail.data.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 轨迹数据仓库，封装所有轨迹相关的网络请求
 * 作为ViewModel和网络层之间的中间层，统一处理错误和数据转换
 */
class TrackRepository {

    private val api = ApiClient.apiService

    /** 创建轨迹记录 */
    suspend fun createTrack(request: CreateTrackRequest): Result<CreateTrackResponse> = safeApiCall {
        api.createTrack(request)
    }

    /** 获取推荐轨迹列表 */
    suspend fun getRecommendList(page: Int, size: Int): Result<TrackListResponse> = safeApiCall {
        api.getRecommendList(page, size)
    }

    /** 搜索轨迹 */
    suspend fun searchTracks(keyword: String, page: Int, size: Int): Result<TrackListResponse> = safeApiCall {
        api.searchTracks(keyword, page, size)
    }

    /** 获取正在进行中的轨迹 */
    suspend fun getRunningTrack(userId: String): Result<RunningTrackResponse> = safeApiCall {
        api.getRunningTrack(userId)
    }

    /** 获取轨迹地图数据 */
    suspend fun getTrackMap(trackId: String): Result<TrackMapResponse> = safeApiCall {
        api.getTrackMap(trackId)
    }

    /** 获取轨迹详情 */
    suspend fun getTrackDetail(trackId: String): Result<TrackDetailResponse> = safeApiCall {
        api.getTrackDetail(trackId)
    }

    /** 获取轨迹摘要 */
    suspend fun getTrackSummary(trackId: String): Result<TrackSummaryResponse> = safeApiCall {
        api.getTrackSummary(trackId)
    }

    /** 上传轨迹到云端 */
    suspend fun uploadToCloud(trackId: String): Result<Any> = safeApiCall {
        api.uploadToCloud(trackId)
    }

    /** 获取收藏状态 */
    suspend fun getCollectStatus(userId: String, trackId: String): Result<CollectStatusResponse> = safeApiCall {
        api.getCollectStatus(userId, trackId)
    }

    /** 收藏轨迹 */
    suspend fun collectTrack(trackId: String, userId: String): Result<Any> = safeApiCall {
        api.collectTrack(trackId, userId)
    }

    /** 取消收藏 */
    suspend fun uncollectTrack(trackId: String, userId: String): Result<Any> = safeApiCall {
        api.uncollectTrack(trackId, userId)
    }

    /**
     * 安全API调用包装，统一处理网络异常和响应解析
     */
    private suspend fun <T> safeApiCall(
        call: suspend () -> retrofit2.Response<ApiResponse<T>>
    ): Result<T> = withContext(Dispatchers.IO) {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.code == 0 && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Unknown error"))
                }
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
