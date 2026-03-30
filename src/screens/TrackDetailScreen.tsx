import React, {useEffect, useState} from 'react';
import {View, Text, StyleSheet, Pressable} from 'react-native';
import type {NativeStackScreenProps} from '@react-navigation/native-stack';

import {RootStackParamList} from '../App';
import {apiClient} from '../api/client';
import type {TrackDetail, TrackMapResponse} from '../api/types';
import {useAuthStore} from '../state/useAuthStore';
import {useTrackRecordingStore} from '../state/useTrackStore';

export type TrackDetailScreenProps = NativeStackScreenProps<RootStackParamList, 'TrackDetail'>;

export const TrackDetailScreen: React.FC<TrackDetailScreenProps> = ({route, navigation}) => {
  const {trackId, userId} = route.params;
  const [detail, setDetail] = useState<TrackDetail | null>(null);
  const [map, setMap] = useState<TrackMapResponse | null>(null);
  const [collected, setCollected] = useState(false);
  const currentUserId = useAuthStore(state => state.userId);
  const setRecordingTrack = useTrackRecordingStore(state => state.setRecordingTrack);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [detailRes, mapRes, collectRes] = await Promise.all([
          apiClient.get<TrackDetail>(`/api/track/${trackId}/detail`),
          apiClient.get<TrackMapResponse>(`/api/track/${trackId}/map`),
          apiClient.get<{collected: boolean}>(`/api/user/${userId}/collect?track_id=${trackId}`),
        ]);
        setDetail(detailRes.data);
        setMap(mapRes.data);
        setCollected(collectRes.data.collected);
      } catch (e) {
        // eslint-disable-next-line no-console
        console.warn('fetch track detail failed', e);
      }
    };
    fetchData();
  }, [trackId, userId]);

  const toggleCollect = async () => {
    try {
      if (!currentUserId) {
        return;
      }
      if (collected) {
        await apiClient.delete(`/api/track_collect?track_id=${trackId}&user_id=${currentUserId}`);
        setCollected(false);
      } else {
        await apiClient.post(`/api/track_collect?track_id=${trackId}&user_id=${currentUserId}`);
        setCollected(true);
      }
    } catch (e) {
      // eslint-disable-next-line no-console
      console.warn('collect toggle failed', e);
    }
  };

  const handleUseForNavigation = async () => {
    if (!currentUserId) {
      return;
    }
    try {
      const runningRes = await apiClient.get<{running: boolean}>(`/api/track/running?user_id=${currentUserId}`);
      if (runningRes.data.running) {
        // 简化：直接返回，不打断现有轨迹
        return;
      }
      const createRes = await apiClient.post('/api/track/create');
      const newId = (createRes.data as any).id as string;
      setRecordingTrack(newId);
      navigation.navigate('RecordingDetail', {trackId: newId});
    } catch (e) {
      // eslint-disable-next-line no-console
      console.warn('use track navigation failed', e);
    }
  };

  if (!detail) {
    return (
      <View style={styles.container}>
        <Text>加载中...</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <View style={styles.mapPlaceholder}>
        <Text>他人轨迹地图占位（点数：{map?.points.length ?? 0}）</Text>
      </View>
      <View style={styles.infoBox}>
        <Text style={styles.title}>{detail.name}</Text>
        <Text>距离 {(detail.distance_meters / 1000).toFixed(2)} km</Text>
        <Text>用时 {Math.round(detail.duration_sec / 60)} 分钟</Text>
      </View>
      <View style={styles.actions}>
        <Pressable style={styles.primaryButton} onPress={handleUseForNavigation}>
          <Text style={styles.primaryText}>使用轨迹导航</Text>
        </Pressable>
        <Pressable style={styles.secondaryButton} onPress={toggleCollect}>
          <Text style={styles.secondaryText}>{collected ? '已收藏' : '收藏轨迹'}</Text>
        </Pressable>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {flex: 1, backgroundColor: '#fff', padding: 16},
  mapPlaceholder: {
    flex: 1,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#ddd',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 16,
  },
  infoBox: {
    padding: 12,
    borderRadius: 8,
    backgroundColor: '#f5f5f5',
    marginBottom: 16,
  },
  title: {
    fontSize: 18,
    fontWeight: '600',
    marginBottom: 8,
  },
  actions: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  primaryButton: {
    flex: 1,
    backgroundColor: '#007AFF',
    borderRadius: 24,
    paddingVertical: 12,
    alignItems: 'center',
    marginRight: 8,
  },
  primaryText: {color: '#fff', fontSize: 16},
  secondaryButton: {
    flex: 1,
    borderRadius: 24,
    paddingVertical: 12,
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#007AFF',
  },
  secondaryText: {color: '#007AFF', fontSize: 16},
});
