import React from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';

import {useAuthStore} from './state/useAuthStore';
import {LoginScreen} from './screens/LoginScreen';
import {HomeScreen} from './screens/HomeScreen';
import {TrackSearchScreen} from './screens/TrackSearchScreen';
import {RecordingDetailScreen} from './screens/RecordingDetailScreen';
import {TrackSummaryScreen} from './screens/TrackSummaryScreen';
import {TrackDetailScreen} from './screens/TrackDetailScreen';
import {UploadSuccessScreen} from './screens/UploadSuccessScreen';
import {ProfileScreen} from './screens/ProfileScreen';
import {SettingsScreen} from './screens/SettingsScreen';

export type RootStackParamList = {
  Login: undefined;
  Home: undefined;
  TrackSearch: undefined;
  RecordingDetail: {trackId: string};
  TrackSummary: {trackId: string};
  TrackDetail: {trackId: string; userId: string};
  UploadSuccess: {trackId: string};
  Profile: undefined;
  Settings: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();

const AppInner: React.FC = () => {
  const loggedIn = useAuthStore(state => state.loggedIn);

  return (
    <NavigationContainer>
      <Stack.Navigator screenOptions={{headerShown: false}}>
        {!loggedIn ? (
          <Stack.Screen name="Login" component={LoginScreen} />
        ) : (
          <>
            <Stack.Screen name="Home" component={HomeScreen} />
            <Stack.Screen name="TrackSearch" component={TrackSearchScreen} />
            <Stack.Screen name="RecordingDetail" component={RecordingDetailScreen} />
            <Stack.Screen name="TrackSummary" component={TrackSummaryScreen} />
            <Stack.Screen name="TrackDetail" component={TrackDetailScreen} />
            <Stack.Screen name="UploadSuccess" component={UploadSuccessScreen} />
            <Stack.Screen name="Profile" component={ProfileScreen} />
            <Stack.Screen name="Settings" component={SettingsScreen} />
          </>
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
};

const App: React.FC = () => {
  return <AppInner />;
};

export default App;
