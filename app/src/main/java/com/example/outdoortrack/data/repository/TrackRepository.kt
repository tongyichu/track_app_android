package com.example.outdoortrack.data.repository

import com.example.outdoortrack.data.api.TrackApiService
import com.example.outdoortrack.data.local.TrackPointDao
import com.example.outdoortrack.data.local.TrackPointEntity
import com.example.outdoortrack.data.model.TrackDetailResponse
import com.example.outdoortrack.data.model.TrackListItem
import com.example.outdoortrack.data.model.TrackMapResponse
import com.example.outdoortrack.data.model.TrackSummaryResponse
import com.example.outdoortrack.data.model.TrackUploadRequest
import retrofit2.Retrofit

/**
 * 轨迹相关仓库，封装接口调用与本地采样点存储（Room）。
 */
class TrackRepository(
    retrofit: Retrofit,
    private val trackPointDao: TrackPointDao
) {
    private val api = retrofit.create(TrackApiService::class.java)

    suspend fun createTrack(): TrackDetailResponse = api.createTrack()

    suspend fun getRecommendList(): List<TrackListItem> = api.getRecommendList()

    suspend fun searchTracks(keyword: String?): List<TrackListItem> = api.searchTracks(keyword)

    suspend fun getRunningTrack(userId: String): TrackDetailResponse? = api.getRunningTrack(userId)

    suspend fun getTrackMap(trackId: String): TrackMapResponse = api.getTrackMap(trackId)

    suspend fun getTrackSummary(trackId: String): TrackSummaryResponse = api.getTrackSummary(trackId)

    suspend fun isCollected(userId: String, trackId: String): Boolean = api.isCollected(userId, trackId)

    suspend fun collectTrack(userId: String, trackId: String) = api.collectTrack(trackId, userId)

    suspend fun unCollectTrack(userId: String, trackId: String) = api.unCollectTrack(trackId, userId)

    suspend fun uploadTrack(trackId: String): TrackDetailResponse {
        val points = trackPointDao.getPointsForTrack(trackId).map {
            com.example.outdoortrack.data.model.TrackPointDto(
                latitude = it.latitude,
                longitude = it.longitude,
                altitude = it.altitude,
                timestamp = it.timestamp
            )
        }
        val payload = TrackUploadRequest(
            trackId = trackId,
            points = points,
            distanceMeters = null,
            durationSeconds = null,
            elevationGain = null,
            avgPace = null
        )
        val result = api.uploadTrack(trackId, payload)
        // 上传成功后清理本地采样点
        trackPointDao.deleteByTrack(trackId)
        return result
    }

    suspend fun saveSamplePoint(
        trackId: String,
        latitude: Double,
        longitude: Double,
        altitude: Double?,
        timestamp: Long
    ) {
        trackPointDao.insert(
            TrackPointEntity(
                trackId = trackId,
                latitude = latitude,
                longitude = longitude,
                altitude = altitude,
                timestamp = timestamp
            )
        )
    }
}
