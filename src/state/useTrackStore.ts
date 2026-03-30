import {create} from 'zustand';

interface TrackRecordingState {
  currentTrackId: string | null;
  isRecording: boolean;
  isPaused: boolean;
  setRecordingTrack: (id: string) => void;
  pause: () => void;
  resume: () => void;
  finish: () => void;
}

export const useTrackRecordingStore = create<TrackRecordingState>(set => ({
  currentTrackId: null,
  isRecording: false,
  isPaused: false,
  setRecordingTrack: id => set({currentTrackId: id, isRecording: true, isPaused: false}),
  pause: () => set({isPaused: true}),
  resume: () => set({isPaused: false}),
  finish: () => set({currentTrackId: null, isRecording: false, isPaused: false}),
}));
