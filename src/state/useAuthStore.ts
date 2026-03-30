import {create} from 'zustand';

interface AuthState {
  userId: string | null;
  loggedIn: boolean;
  setUser: (userId: string) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>(set => ({
  userId: null,
  loggedIn: false,
  setUser: userId => set({userId, loggedIn: true}),
  logout: () => set({userId: null, loggedIn: false}),
}));
