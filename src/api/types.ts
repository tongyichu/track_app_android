// Shared API response types for the outdoor track app.

export interface TrackSummary {
  id: string;
  user_id: string;
  name: string;
  distance_meters: number;
  duration_sec: number;
  ascent_meters: number;
  avg_speed_kmh: number;
}

export interface TrackPoint {
  index: number;
  latitude: number;
  longitude: number;
  elevation: number;
  timestamp: string;
}

export interface TrackDetail extends TrackSummary {
  status: 'running' | 'paused' | 'finished';
  points: TrackPoint[];
  started_at: string;
  ended_at?: string | null;
}

export interface TrackMapResponse {
  track_id: string;
  points: TrackPoint[];
}

export interface RunningTrackResponse {
  running: boolean;
  track?: TrackDetail;
}

export interface UserProfile {
  id: string;
  nickname: string;
  avatar_url?: string;
  signature?: string;
  client_language?: string;
}
