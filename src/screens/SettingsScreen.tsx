import React, {useState} from 'react';
import {View, Text, StyleSheet, TextInput, Pressable} from 'react-native';
import type {NativeStackScreenProps} from '@react-navigation/native-stack';

import {RootStackParamList} from '../App';
import {apiClient} from '../api/client';
import {useAuthStore} from '../state/useAuthStore';
import {useAppRuntimeStore} from '../state/useAppRuntimeStore';

export type SettingsScreenProps = NativeStackScreenProps<RootStackParamList, 'Settings'>;

export const SettingsScreen: React.FC<SettingsScreenProps> = () => {
  const userId = useAuthStore(state => state.userId);
  const language = useAppRuntimeStore(state => state.language);
  const setLanguageRuntime = useAppRuntimeStore(state => state.setLanguage);

  const [nickname, setNickname] = useState('');
  const [signature, setSignature] = useState('');
  const [clientLanguage, setClientLanguage] = useState(language ?? 'zh-CN');

  const updateNickname = async () => {
    if (!userId) {
      return;
    }
    await apiClient.put(`/api/user/profile/name?user_id=${userId}`, {name: nickname});
  };

  const updateSignature = async () => {
    if (!userId) {
      return;
    }
    await apiClient.put(`/api/user/profile/signature?user_id=${userId}`, {signature});
  };

  const updateLanguage = async () => {
    if (!userId) {
      return;
    }
    await apiClient.put(`/api/user/profile/client_language?user_id=${userId}`, {client_language: clientLanguage});
    setLanguageRuntime(clientLanguage);
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>设置</Text>
      <Text style={styles.label}>昵称</Text>
      <TextInput style={styles.input} value={nickname} onChangeText={setNickname} placeholder="输入新昵称" />
      <Pressable style={styles.button} onPress={updateNickname}>
        <Text style={styles.buttonText}>保存昵称</Text>
      </Pressable>

      <Text style={styles.label}>个性签名</Text>
      <TextInput style={styles.input} value={signature} onChangeText={setSignature} placeholder="输入个性签名" />
      <Pressable style={styles.button} onPress={updateSignature}>
        <Text style={styles.buttonText}>保存签名</Text>
      </Pressable>

      <Text style={styles.label}>系统语言</Text>
      <TextInput style={styles.input} value={clientLanguage} onChangeText={setClientLanguage} placeholder="如 zh-CN" />
      <Pressable style={styles.button} onPress={updateLanguage}>
        <Text style={styles.buttonText}>保存语言</Text>
      </Pressable>

      <Text style={styles.tip}>头像编辑与微信登录、高德地图 SDK 需在集成真实 SDK 后补充实现。</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {flex: 1, backgroundColor: '#fff', padding: 16},
  title: {fontSize: 20, fontWeight: '600', marginBottom: 16},
  label: {fontSize: 14, marginTop: 12},
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    paddingHorizontal: 8,
    paddingVertical: 6,
    marginTop: 4,
  },
  button: {
    marginTop: 8,
    backgroundColor: '#007AFF',
    borderRadius: 20,
    paddingVertical: 8,
    alignItems: 'center',
  },
  buttonText: {color: '#fff'},
  tip: {marginTop: 24, fontSize: 12, color: '#666'},
});
