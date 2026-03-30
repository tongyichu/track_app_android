import React from 'react';
import {View, Text, StyleSheet, Pressable} from 'react-native';
import type {NativeStackScreenProps} from '@react-navigation/native-stack';

import {RootStackParamList} from '../App';
import {useTrackRecordingStore} from '../state/useTrackStore';

export type RecordingDetailScreenProps = NativeStackScreenProps<RootStackParamList, 'RecordingDetail'>;

export const RecordingDetailScreen: React.FC<RecordingDetailScreenProps> = ({route, navigation}) => {
  const {trackId} = route.params;
  const isPaused = useTrackRecordingStore(state => state.isPaused);
  const pause = useTrackRecordingStore(state => state.pause);
  const resume = useTrackRecordingStore(state => state.resume);
  const finish = useTrackRecordingStore(state => state.finish);

  const togglePause = () => {
    if (isPaused) {
      resume();
    } else {
      pause();
    }
  };

  const handleFinish = () => {
    finish();
    navigation.replace('TrackSummary', {trackId});
  };

  return (
    <View style={styles.container}>
      <View style={styles.mapPlaceholder}>
        <Text>轨迹实时地图占位（集成高德 SDK 时在此渲染）</Text>
      </View>
      <View style={styles.infoBox}>
        <Text>轨迹 {trackId}</Text>
        <Text>预计在此展示实时里程、配速、用时等信息。</Text>
      </View>
      <View style={styles.controls}>
        <Pressable style={styles.controlButton} onPress={togglePause}>
          <Text style={styles.controlText}>{isPaused ? '继续' : '暂停'}</Text>
        </Pressable>
        <Pressable style={[styles.controlButton, styles.finishButton]} onLongPress={handleFinish}>
          <Text style={styles.controlText}>长按结束</Text>
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
  controls: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  controlButton: {
    flex: 1,
    marginHorizontal: 4,
    paddingVertical: 12,
    borderRadius: 24,
    backgroundColor: '#007AFF',
    alignItems: 'center',
  },
  finishButton: {
    backgroundColor: '#ff3b30',
  },
  controlText: {
    color: '#fff',
    fontSize: 16,
  },
});
