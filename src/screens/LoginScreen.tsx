import React from 'react';
import {View, Text, StyleSheet, Pressable} from 'react-native';
import type {NativeStackScreenProps} from '@react-navigation/native-stack';

import {RootStackParamList} from '../App';
import {wechatLogin} from '../services/auth';
import {useAuthStore} from '../state/useAuthStore';

export type LoginScreenProps = NativeStackScreenProps<RootStackParamList, 'Login'>;

export const LoginScreen: React.FC<LoginScreenProps> = ({navigation}) => {
  const setUser = useAuthStore(state => state.setUser);

  const handleWechatLogin = async () => {
    const userId = await wechatLogin();
    setUser(userId);
    navigation.replace('Home');
  };

  return (
    <View style={styles.container}>
      <Text style={styles.title}>户外轨迹 APP</Text>
      <Pressable style={styles.button} onPress={handleWechatLogin}>
        <Text style={styles.buttonText}>使用微信登录（占位）</Text>
      </Pressable>
      <Text style={styles.tip}>登录状态默认保持 15 天，客户端需在实际实现中持久化登录信息。</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: 24,
    backgroundColor: '#ffffff',
  },
  title: {
    fontSize: 24,
    fontWeight: '600',
    marginBottom: 32,
  },
  button: {
    backgroundColor: '#07c160',
    paddingHorizontal: 24,
    paddingVertical: 12,
    borderRadius: 24,
  },
  buttonText: {
    color: '#ffffff',
    fontSize: 16,
  },
  tip: {
    marginTop: 16,
    fontSize: 12,
    color: '#666666',
    textAlign: 'center',
  },
});
