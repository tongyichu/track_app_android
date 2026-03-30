import React from 'react';
import {View, Text, StyleSheet, Pressable} from 'react-native';
import type {NativeStackScreenProps} from '@react-navigation/native-stack';

import {RootStackParamList} from '../App';

export type UploadSuccessScreenProps = NativeStackScreenProps<RootStackParamList, 'UploadSuccess'>;

export const UploadSuccessScreen: React.FC<UploadSuccessScreenProps> = ({navigation}) => {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>上传成功</Text>
      <Text style={styles.desc}>轨迹已上传至云端，可在他人轨迹详情中被发现和使用。</Text>
      <Pressable style={styles.button} onPress={() => navigation.reset({index: 0, routes: [{name: 'Home'}]})}>
        <Text style={styles.buttonText}>返回首页</Text>
      </Pressable>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {flex: 1, backgroundColor: '#fff', alignItems: 'center', justifyContent: 'center', padding: 24},
  title: {fontSize: 22, fontWeight: '600', marginBottom: 16},
  desc: {fontSize: 14, color: '#666', textAlign: 'center', marginBottom: 24},
  button: {backgroundColor: '#007AFF', borderRadius: 24, paddingHorizontal: 24, paddingVertical: 12},
  buttonText: {color: '#fff', fontSize: 16},
});
