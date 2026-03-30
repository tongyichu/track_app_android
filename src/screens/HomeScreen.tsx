import React, {useEffect, useState} from 'react';
import {View, Text, StyleSheet, FlatList, Pressable} from 'react-native';
import type {NativeStackScreenProps} from '@react-navigation/native-stack';

import {RootStackParamList} from '../App';
import {apiClient} from '../api/client';
import type {TrackSummary} from '../api/types';
import {useTrackRecordingStore} from '../state/useTrackStore';
import {useAuthStore} from '../state/useAuthStore';

export type HomeScreenProps = NativeStackScreenProps<RootStackParamList, 'Home'>;

export const HomeScreen: React.FC<HomeScreenProps> = ({navigation}) => {
  const [tracks, setTracks] = useState<TrackSummary[]>([]);
  const recording = useTrackRecordingStore(state => state.isRecording);
  const setRecordingTrack = useTrackRecordingStore(state => state.setRecordingTrack);
  const finishRecording = useTrackRecordingStore(state => state.finish);
  const currentTrackId = useTrackRecordingStore(state => state.currentTrackId);
  const userId = useAuthStore(state => state.userId);

  useEffect(() => {
    const fetchRecommend = async () => {
      try {
        const res = await apiClient.get<TrackSummary[]>('/api/track/recommend/list');
        setTracks(res.data);
      } catch (e) {
        // 简化处理：生产环境应统一错误上报
        // eslint-disable-next-line no-console
        console.warn('fetch recommend failed', e);
      }
    };
    fetchRecommend();
  }, []);

  const handleStartOrContinue = async () => {
    if (!recording) {
      const res = await apiClient.post('/api/track/create');
      const newId = (res.data as any).id as string;
      setRecordingTrack(newId);
      navigation.navigate('RecordingDetail', {trackId: newId});
    } else if (currentTrackId) {
      navigation.navigate('RecordingDetail', {trackId: currentTrackId});
    }
  };

  const handleFinishFromHome = () => {
    if (currentTrackId) {
      finishRecording();
      navigation.navigate('TrackSummary', {trackId: currentTrackId});
    }
  };

  const renderTrackItem = ({item}: {item: TrackSummary}) => (
    <Pressable
      style={styles.trackItem}
      onPress={() => navigation.navigate('TrackDetail', {trackId: item.id, userId: item.user_id})}>
      <Text style={styles.trackName}>{item.name}</Text>
      <Text style={styles.trackMeta}>
        距离 {(item.distance_meters / 1000).toFixed(1)} km · 用时 {Math.round(item.duration_sec / 60)} 分钟
      </Text>
    </Pressable>
  );

  return (
    <View style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>推荐轨迹</Text>
        <Pressable onPress={() => navigation.navigate('TrackSearch')}>
          <Text style={styles.link}>搜索</Text>
        </Pressable>
      </View>

      <FlatList
        data={tracks}
        keyExtractor={item => item.id}
        renderItem={renderTrackItem}
        contentContainerStyle={styles.listContent}
      />

      <View style={styles.footer}>
        <Pressable
          style={[styles.mainButton, recording && styles.mainButtonRecording]}
          onPress={handleStartOrContinue}
          onLongPress={recording ? handleFinishFromHome : undefined}>
          <Text style={styles.mainButtonText}>{recording ? '正在记录（长按结束）' : '开始记录'}</Text>
        </Pressable>
        <Pressable
          style={styles.profileEntry}
          onPress={() => navigation.navigate('Profile')}>
          <Text style={styles.profileText}>个人中心 {userId ?? ''}</Text>
        </Pressable>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#ffffff',
  },
  header: {
    paddingHorizontal: 16,
    paddingVertical: 12,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  title: {
    fontSize: 20,
    fontWeight: '600',
  },
  link: {
    color: '#007AFF',
    fontSize: 14,
  },
  listContent: {
    paddingHorizontal: 16,
  },
  trackItem: {
    paddingVertical: 12,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: '#eee',
  },
  trackName: {
    fontSize: 16,
    marginBottom: 4,
  },
  trackMeta: {
    fontSize: 12,
    color: '#666',
  },
  footer: {
    padding: 16,
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: '#eee',
  },
  mainButton: {
    backgroundColor: '#ff9500',
    paddingVertical: 12,
    borderRadius: 24,
    alignItems: 'center',
  },
  mainButtonRecording: {
    backgroundColor: '#ff3b30',
  },
  mainButtonText: {
    color: '#fff',
    fontSize: 16,
  },
  profileEntry: {
    marginTop: 12,
    alignItems: 'center',
  },
  profileText: {
    color: '#007AFF',
    fontSize: 14,
  },
});
