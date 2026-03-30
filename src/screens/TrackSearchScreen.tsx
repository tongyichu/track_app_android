import React, {useState} from 'react';
import {View, TextInput, FlatList, Text, StyleSheet, Pressable} from 'react-native';
import type {NativeStackScreenProps} from '@react-navigation/native-stack';

import {RootStackParamList} from '../App';
import {apiClient} from '../api/client';
import type {TrackSummary} from '../api/types';

export type TrackSearchScreenProps = NativeStackScreenProps<RootStackParamList, 'TrackSearch'>;

export const TrackSearchScreen: React.FC<TrackSearchScreenProps> = ({navigation}) => {
  const [keyword, setKeyword] = useState('');
  const [results, setResults] = useState<TrackSummary[]>([]);

  const handleSearch = async () => {
    try {
      const res = await apiClient.get<TrackSummary[]>(`/api/track/search/list?keyword=${encodeURIComponent(keyword)}`);
      setResults(res.data);
    } catch (e) {
      // eslint-disable-next-line no-console
      console.warn('search failed', e);
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.searchBar}>
        <TextInput
          style={styles.input}
          value={keyword}
          onChangeText={setKeyword}
          placeholder="搜索轨迹关键字"/>
        <Pressable style={styles.button} onPress={handleSearch}>
          <Text style={styles.buttonText}>搜索</Text>
        </Pressable>
      </View>
      <FlatList
        data={results}
        keyExtractor={item => item.id}
        renderItem={({item}) => (
          <Pressable
            style={styles.item}
            onPress={() => navigation.navigate('TrackDetail', {trackId: item.id, userId: item.user_id})}>
            <Text style={styles.itemTitle}>{item.name}</Text>
          </Pressable>
        )}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {flex: 1, backgroundColor: '#fff'},
  searchBar: {
    flexDirection: 'row',
    padding: 12,
  },
  input: {
    flex: 1,
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    paddingHorizontal: 8,
    marginRight: 8,
  },
  button: {
    backgroundColor: '#007AFF',
    borderRadius: 8,
    paddingHorizontal: 12,
    justifyContent: 'center',
  },
  buttonText: {
    color: '#fff',
  },
  item: {
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: '#eee',
  },
  itemTitle: {
    fontSize: 16,
  },
});
