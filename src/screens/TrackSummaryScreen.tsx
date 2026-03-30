import React, {useEffect, useState} from 'react';
import {View, Text, StyleSheet, Pressable} from 'react-native';
import type {NativeStackScreenProps} from '@react-navigation/native-stack';

import {RootStackParamList} from '../App';
import {apiClient} from '../api/client';
import type {TrackDetail, TrackMapResponse} from '../api/types';

export type TrackSummaryScreenProps = NativeStackScreenProps<RootStackParamList, 'TrackSummary'>;

export const TrackSummaryScreen: React.FC<TrackSummaryScreenProps> = ({route, navigation}) => {
  const {trackId} = route.params;
  const [detail, setDetail] = useState<TrackDetail | null>(null);
  const [map, setMap] = useState<TrackMapResponse | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [detailRes, mapRes] = await Promise.all([
          apiClient.get<TrackDetail>(`/api/track/${trackId}/detail`),
          apiClient.get<TrackMapResponse>(`/api/track/${trackId}/map`),
        ]);
        setDetail(detailRes.data);
        setMap(mapRes.data);
      } catch (e) {
        // eslint-disable-next-line no-console
        console.warn('fetch track summary failed', e);
      }
    };
    fetchData();
  }, [trackId]);

  const handleUpload = async () => {
    try {
      await apiClient.post(`/api/track/${trackId}/upload_cloud`);
      navigation.replace('UploadSuccess', {trackId});
    } catch (e) {
      // eslint-disable-next-line no-console
      console.warn('upload failed', e);
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
        <Text>结束轨迹地图占位（点数：{map?.points.length ?? 0}）</Text>
      </View>
      <View style={styles.infoBox}>
        <Text style={styles.title}>{detail.name}</Text>
        <Text>距离 {(detail.distance_meters / 1000).toFixed(2)} km</Text>
        <Text>用时 {Math.round(detail.duration_sec / 60)} 分钟</Text>
        <Text>爬升 {Math.round(detail.ascent_meters)} m · 均速 {detail.avg_speed_kmh.toFixed(1)} km/h</Text>
      </View>
      <View style={styles.buttons}>
        <Pressable style={styles.button} onPress={handleUpload}>
          <Text style={styles.buttonText}>上传到云端</Text>
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
  buttons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  button: {
    flex: 1,
    backgroundColor: '#007AFF',
    borderRadius: 24,
    paddingVertical: 12,
    alignItems: 'center',
  },
  buttonText: {color: '#fff', fontSize: 16},
});
