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
      const message = (error.response?.data as { message?: string; error?: string } | undefined)?.message
        || (error.response?.data as { message?: string; error?: string } | undefined)?.error
        || error.message;
      return Promise.reject(new Error(message));
    }
    return Promise.reject(error);
  }
);
