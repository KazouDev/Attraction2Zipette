import axios, { AxiosError, AxiosResponse } from 'axios';

const baseURL: string = import.meta.env.VITE_API_BASE_URL?.replace(/\/$/, '') || 'http://localhost:8080';

export const httpClient = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json'
  },
  timeout: 10000
});

httpClient.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error: AxiosError | Error) => {
    if (axios.isAxiosError(error)) {
      const data = error.response?.data as any;

      let message = error.message;
      if (data) {
        if (typeof data === 'string') {
          message = data;
        } else if (data.violations) {
          if (Array.isArray(data.violations)) {
            message = data.violations.join('; ');
          } else {
            message = String(data.violations);
          }
        } else if (data.message) {
          message = data.message;
        } else if (data.error) {
          message = data.error;
        }
      }

      return Promise.reject(new Error(message));
    }
    return Promise.reject(error);
  }
);
