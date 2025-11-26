import { useAuthStore } from "@/stores/auth-store";
import axios, { type AxiosInstance, type InternalAxiosRequestConfig, type AxiosResponse, type AxiosError } from 'axios';

const requestInterceptor = async (config: InternalAxiosRequestConfig) => {
    const token = await useAuthStore.getState().getValidToken();
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
};

const requestErrorInterceptor = (error: any) => {
    return Promise.reject(error);
};

const responseInterceptor = (response: AxiosResponse) => response;

const responseErrorInterceptor = async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;

        try {
            await useAuthStore.getState().updateToken(70);
            const freshToken = useAuthStore.getState().token;
            if (freshToken && originalRequest.headers) {
                originalRequest.headers.Authorization = `Bearer ${freshToken}`;
                return axios(originalRequest);
            }
        } catch (refreshError) {
            useAuthStore.getState().login();
            return Promise.reject(refreshError);
        }
    }

    return Promise.reject(error);
};

export const client: AxiosInstance = axios.create();

client.interceptors.request.use(requestInterceptor, requestErrorInterceptor);
client.interceptors.response.use(responseInterceptor, responseErrorInterceptor);

export const createServiceClient = (baseURL: string): AxiosInstance => {
    const client = axios.create({ baseURL });

    client.interceptors.request.use(requestInterceptor, requestErrorInterceptor);
    client.interceptors.response.use(responseInterceptor, responseErrorInterceptor);

    return client;
};