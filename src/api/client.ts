import axios from 'axios';
import {API_BASE_URL, CLIENT_TYPE, CLIENT_VERSION, DEFAULT_LANGUAGE} from '../config/env';
import {useAuthStore} from '../state/useAuthStore';
import {useAppRuntimeStore} from '../state/useAppRuntimeStore';

// createApiClient creates an axios instance with common headers injected.
export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

apiClient.interceptors.request.use(config => {
  const auth = useAuthStore.getState();
  const runtime = useAppRuntimeStore.getState();

  if (!config.headers) {
    config.headers = {};
  }

  config.headers['X-User-ID'] = auth.userId ?? '';
  config.headers['X-Client-Type'] = CLIENT_TYPE;
  config.headers['X-Client-Version'] = CLIENT_VERSION;
  config.headers['X-Client-Language'] = runtime.language ?? DEFAULT_LANGUAGE;
  config.headers['X-User-Location'] = runtime.location ?? '';

  return config;
});
