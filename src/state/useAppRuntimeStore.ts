import {create} from 'zustand';

interface AppRuntimeState {
  language: string | null;
  location: string | null; // "lat,lng" string
  setLanguage: (lang: string) => void;
  setLocation: (location: string) => void;
}

export const useAppRuntimeStore = create<AppRuntimeState>(set => ({
  language: null,
  location: null,
  setLanguage: language => set({language}),
  setLocation: location => set({location}),
}));
