import React, {useEffect, useState} from 'react';
import {View, Text, StyleSheet, Pressable} from 'react-native';
import type {NativeStackScreenProps} from '@react-navigation/native-stack';

import {RootStackParamList} from '../App';
import {apiClient} from '../api/client';
import type {UserProfile} from '../api/types';
import {useAuthStore} from '../state/useAuthStore';

export type ProfileScreenProps = NativeStackScreenProps<RootStackParamList, 'Profile'>;

export const ProfileScreen: React.FC<ProfileScreenProps> = ({navigation}) => {
  const userId = useAuthStore(state => state.userId);
  const logout = useAuthStore(state => state.logout);
  const [profile, setProfile] = useState<UserProfile | null>(null);

  useEffect(() => {
    const fetchProfile = async () => {
      if (!userId) {
        return;
      }
      try {
        const res = await apiClient.get<UserProfile>(`/api/user/${userId}/detail`);
        setProfile(res.data);
      } catch (e) {
        // eslint-disable-next-line no-console
        console.warn('fetch profile failed', e);
      }
    };
    fetchProfile();
  }, [userId]);

  const handleLogout = () => {
    logout();
    navigation.reset({index: 0, routes: [{name: 'Login'}]});
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>个人中心</Text>
      <Text style={styles.field}>用户 ID：{profile?.id ?? userId}</Text>
      <Text style={styles.field}>昵称：{profile?.nickname ?? '-'}</Text>
      <Text style={styles.field}>签名：{profile?.signature ?? '-'}</Text>
      <Pressable style={styles.button} onPress={() => navigation.navigate('Settings')}>
        <Text style={styles.buttonText}>设置</Text>
      </Pressable>
      <Pressable style={[styles.button, styles.logout]} onPress={handleLogout}>
        <Text style={styles.buttonText}>退出登录</Text>
      </Pressable>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {flex: 1, backgroundColor: '#fff', padding: 16},
  title: {fontSize: 20, fontWeight: '600', marginBottom: 16},
  field: {fontSize: 14, marginBottom: 8},
  button: {
    marginTop: 16,
    backgroundColor: '#007AFF',
    borderRadius: 24,
    paddingVertical: 10,
    alignItems: 'center',
  },
  logout: {backgroundColor: '#ff3b30'},
  buttonText: {color: '#fff', fontSize: 16},
});
